package com.ets.fe.a_main;

import com.ets.fe.Application;
import com.ets.fe.acdoc.gui.comp.AccountingDocumentsComponent;
import com.ets.fe.a_maintask.CompletePnrTask;
import com.ets.fe.acdoc.bo.AcDocUtil;
import com.ets.fe.acdoc.gui.*;
import com.ets.fe.acdoc.model.TicketingPurchaseAcDoc;
import com.ets.fe.acdoc.task.NewTSalesDocumentTask;
import com.ets.fe.acdoc.model.TicketingSalesAcDoc;
import com.ets.fe.client.gui.AgentFrame;
import com.ets.fe.client.model.*;
import com.ets.fe.pnr.gui.DlgItinerary;
import com.ets.fe.pnr.task.SavePnrTask;
import com.ets.fe.pnr.model.*;
import com.ets.fe.util.*;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;

/**
 *
 * @author Yusuf
 */
public class PnrPanel extends JPanel implements PropertyChangeListener, ComponentListener {

    final DashBoardFrame parent;

    private boolean editable = true;
    private boolean saveNeeded = false;

    private Pnr pnr;
    private final Long pnrId;
    private List<Itinerary> segments;
    private String taskType = "";//PNR,NEW_INVOICE,

    private NewTSalesDocumentTask newTSalesDocumentTask;
    private CompletePnrTask completePnrTask;
    private SavePnrTask savePnrTask;

    public PnrPanel(Long pnrId, DashBoardFrame parent) {
        this.parent = parent;
        this.pnrId = pnrId;
        initComponents();
        AbstractDocument doc;
        doc = (AbstractDocument) txtBookingOID.getDocument();
        doc.setDocumentFilter(new DocumentSizeFilter(9));
        doc = (AbstractDocument) txtTicketingOID.getDocument();
        doc.setDocumentFilter(new DocumentSizeFilter(9));
        doc = (AbstractDocument) txtAirline.getDocument();
        doc.setDocumentFilter(new DocumentSizeFilter(3));

        mainSplitPane.addComponentListener(this);
        loadCompletePnr();
        ticketComponent.setPnrPanel(this);
    }

    public void setSaveNeeded(boolean value) {
        saveNeeded = value;
    }

    private void callAccountingDocs() {
        accountingDocumentsComponent.search(pnrId);
    }

    private void populatePnr() {
        txtPnr.setText(pnr.getGdsPnr());
        txtBookingOID.setText(pnr.getBookingAgtOid());
        txtTicketingOID.setText(pnr.getTicketingAgtOid());
        txtBAgentSine.setText(pnr.getPnrCreatorAgentSine());
        txtTAgentSine.setText(pnr.getTicketingAgentSine());
        txtVendorPNR.setText(pnr.getVendorPNR());
        txtAirline.setText(pnr.getAirLineCode());
        txtTotalPsgr.setText(pnr.getNoOfPax().toString());
        dtBookingDate.setDate(pnr.getPnrCreationDate());
        dtCanceldate.setDate(pnr.getPnrCancellationDate());
        if (pnr.getTicketingAgtOid() != null && !pnr.getTicketingAgtOid().isEmpty()) {
            dtCanceldate.setEnabled(true);
        } else {
            dtCanceldate.setEnabled(false);
        }
    }

    private void populateTblItinerary() {
        DefaultTableModel itineraryModel = (DefaultTableModel) tblItinerrary.getModel();
        itineraryModel.getDataVector().removeAllElements();

        int row = 0;
        for (Itinerary s : segments) {

            itineraryModel.insertRow(row, new Object[]{s.getSegmentNo(), s.getDeptFrom(), s.getDeptTo(), DateUtil.dateTOddmm(s.getDeptDate()),
                s.getDeptTime(), DateUtil.dateTOddmm(s.getArvDate()), s.getArvTime(), s.getAirLineCode(),
                s.getFlightNo(), s.getTicketClass(), s.getTktStatus(), s.getBaggage(), s.getCheckInTerminal(), s.getCheckInTime(), s.getMealCode(),
                s.getFlightDuration(), s.getMileage()});
            row++;
        }
    }

    private void setPnrOwner() {
        if (pnr.getCustomer() == null && pnr.getAgent() == null) {
            MainAgent mainAgent = Application.getMainAgent();
            if (mainAgent.getOfficeID().contains(pnr.getBookingAgtOid())) {
                clientComponent.suggestAllocatedClient(Enums.ClientType.CUSTOMER, PnrUtil.calculateLeadPaxName(pnr.getTickets()), pnr.getBookingAgtOid());
            } else {
                clientComponent.suggestAllocatedClient(Enums.ClientType.AGENT, PnrUtil.calculateLeadPaxName(pnr.getTickets()), pnr.getBookingAgtOid());
            }

        } else {
            Contactable cont = null;
            if (pnr.getAgent() != null) {
                cont = pnr.getAgent();
            } else {
                cont = pnr.getCustomer();
            }
            clientComponent.setAllocatetClient(cont, PnrUtil.calculateLeadPaxName(pnr.getTickets()), pnr.getBookingAgtOid(), editable);
        }
    }

    public void editingLogic() {
        int unInvoicedTicket = 0;
        boolean invoiceExist = false;
        
        for (Ticket t : pnr.getTickets()) {
            if (t.getTicketingSalesAcDoc() != null) {
                invoiceExist = true;
                editable = false;//At least one access here make editable false;
            } else {
                unInvoicedTicket++;
            }
        }
        
        if(invoiceExist){
         btnNewDoc.setEnabled(true);
        }else{
         btnNewDoc.setEnabled(false);
        }
        
        if (unInvoicedTicket == pnr.getTickets().size()) {
            editable = true;
        }

        if (unInvoicedTicket == 0) {
            btnCreateInvoice.setEnabled(false);
            btnSave.setEnabled(false);
        } else {
            btnCreateInvoice.setEnabled(true);
            btnSave.setEnabled(true);
        }
    }

    private void SetTicketingAgent() {
        ticketingAgentComponent.setTicketingAgent(pnr);
    }

    private void savePnr() {
        btnSave.setEnabled(false);
        pnr.setAgent(clientComponent.getAgent());
        pnr.setCustomer(clientComponent.getCustomer());
        pnr.setPnrCreationDate(dtBookingDate.getDate());
        pnr.setBookingAgtOid(txtBookingOID.getText().trim());
        pnr.setTicketingAgtOid(txtTicketingOID.getText().trim());
        pnr.setAirLineCode(txtAirline.getText().trim());

        pnr.setPnrCancellationDate(dtCanceldate.getDate());
        if (pnr.getBookingAgtOid() != null || !pnr.getBookingAgtOid().isEmpty()) {
            this.pnr.setTickets(ticketComponent.getTickets());
            this.taskType = "SAVE";
            savePnrTask = new SavePnrTask(pnr, progressBar);
            savePnrTask.addPropertyChangeListener(this);
            savePnrTask.execute();
        }
    }

    private void updateSavingStatus(boolean status) {
        ticketingAgentComponent.setSaveNeeded(status);
        clientComponent.setSaveNeeded(status);
        ticketComponent.setSaveNeeded(status);
        setSaveNeeded(status);

    }

    /**
     * Save it before creating invoice. Just incase user forget to call save.
     * Saving is a different task. A loop is used to check of its done.
     */
    public void newTSalesAcDocumentTask() {
        btnCreateInvoice.setEnabled(false);

        if (ticketingAgentComponent.isSaveNeeded()
                || clientComponent.isSaveNeeded()
                || ticketComponent.isSaveNeeded()
                || saveNeeded) {
            savePnr();
            while (savePnrTask.isDone() == false) {
                try {
                    Thread.sleep(300);
                    //wait untill save worker is done.
                } catch (InterruptedException ex) {
                }
            }
            savePnrTask.removePropertyChangeListener(this);
        }

        if (AcDocUtil.validateSellingFare(pnr.getTickets())&&
                //AcDocUtil.validatePurchaseFare(pnr.getTickets()) && 
                AcDocUtil.validateContactable(pnr)) {
            this.taskType = "AC_DOCUMENT";
            newTSalesDocumentTask = new NewTSalesDocumentTask(pnrId, progressBar);
            newTSalesDocumentTask.addPropertyChangeListener(this);
            newTSalesDocumentTask.execute();
        } else {
            btnCreateInvoice.setEnabled(true);
            btnSave.setEnabled(true);
        }
    }

    public void loadCompletePnr() {
        completePnrTask = new CompletePnrTask(pnrId, progressBar);
        completePnrTask.addPropertyChangeListener(this);
        completePnrTask.execute();
        callAccountingDocs();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        CommandPanel = new javax.swing.JPanel();
        btnNewDoc = new javax.swing.JButton();
        btnCreateInvoice = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        mainSplitPane = new javax.swing.JSplitPane();
        innerSplitPane = new javax.swing.JSplitPane();
        TopPanel = new javax.swing.JPanel();
        PnrPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtBookingOID = new javax.swing.JTextField();
        txtBAgentSine = new javax.swing.JTextField();
        txtVendorPNR = new javax.swing.JTextField();
        txtTicketingOID = new javax.swing.JTextField();
        txtAirline = new javax.swing.JTextField();
        txtTAgentSine = new javax.swing.JTextField();
        txtTotalPsgr = new javax.swing.JTextField();
        dtBookingDate = new org.jdesktop.swingx.JXDatePicker();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtPnr = new javax.swing.JTextArea();
        jLabel8 = new javax.swing.JLabel();
        dtCanceldate = new org.jdesktop.swingx.JXDatePicker();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 5), new java.awt.Dimension(20, 5), new java.awt.Dimension(0, 32767));
        accountingDocumentsComponent = new AccountingDocumentsComponent(this);
        TicketPanel = new javax.swing.JPanel();
        tabsTicket = new javax.swing.JTabbedPane();
        tabsTicket.addChangeListener(tabsTicketListener);
        jPanel4 = new javax.swing.JPanel();
        ticketComponent = new com.ets.fe.a_main.TicketComponent();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblItinerrary = new org.jdesktop.swingx.JXTable();
        jPanel5 = new javax.swing.JPanel();
        remarkComponent = new com.ets.fe.a_main.RemarkComponent();
        jPanel2 = new javax.swing.JPanel();
        clientComponent = new com.ets.fe.a_main.ClientSearchComponent();
        ticketingAgentComponent = new com.ets.fe.a_main.TicketingAgentComponent();
        progressBar = new javax.swing.JProgressBar();

        CommandPanel.setBackground(new java.awt.Color(102, 102, 102));
        CommandPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        CommandPanel.setPreferredSize(new java.awt.Dimension(441, 35));
        java.awt.FlowLayout flowLayout1 = new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 3, 1);
        flowLayout1.setAlignOnBaseline(true);
        CommandPanel.setLayout(flowLayout1);

        btnNewDoc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/newdoc22.png"))); // NOI18N
        btnNewDoc.setToolTipText("New Debit/Credit Memo");
        btnNewDoc.setMaximumSize(new java.awt.Dimension(45, 22));
        btnNewDoc.setMinimumSize(new java.awt.Dimension(45, 22));
        btnNewDoc.setPreferredSize(new java.awt.Dimension(45, 24));
        btnNewDoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewDocActionPerformed(evt);
            }
        });
        CommandPanel.add(btnNewDoc);

        btnCreateInvoice.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/tinv22.png"))); // NOI18N
        btnCreateInvoice.setToolTipText("New Invoice");
        btnCreateInvoice.setMaximumSize(new java.awt.Dimension(45, 22));
        btnCreateInvoice.setMinimumSize(new java.awt.Dimension(45, 22));
        btnCreateInvoice.setPreferredSize(new java.awt.Dimension(45, 24));
        btnCreateInvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateInvoiceActionPerformed(evt);
            }
        });
        CommandPanel.add(btnCreateInvoice);

        btnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/refresh22.png"))); // NOI18N
        btnRefresh.setToolTipText("Refresh");
        btnRefresh.setMaximumSize(new java.awt.Dimension(45, 22));
        btnRefresh.setMinimumSize(new java.awt.Dimension(45, 22));
        btnRefresh.setPreferredSize(new java.awt.Dimension(45, 24));
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });
        CommandPanel.add(btnRefresh);

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/save22.png"))); // NOI18N
        btnSave.setToolTipText("Save");
        btnSave.setMaximumSize(new java.awt.Dimension(45, 22));
        btnSave.setMinimumSize(new java.awt.Dimension(45, 22));
        btnSave.setPreferredSize(new java.awt.Dimension(45, 24));
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        CommandPanel.add(btnSave);

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/exit22.png"))); // NOI18N
        btnClose.setToolTipText("Close PNR");
        btnClose.setMaximumSize(new java.awt.Dimension(45, 22));
        btnClose.setMinimumSize(new java.awt.Dimension(45, 22));
        btnClose.setPreferredSize(new java.awt.Dimension(45, 24));
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        CommandPanel.add(btnClose);

        mainSplitPane.setDividerLocation(1000);
        mainSplitPane.setDividerSize(4);
        mainSplitPane.setResizeWeight(0.8);
        mainSplitPane.setAutoscrolls(true);

        innerSplitPane.setDividerLocation(205);
        innerSplitPane.setDividerSize(4);
        innerSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        innerSplitPane.setResizeWeight(0.2);

        PnrPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "PNR", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        PnrPanel.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("BookingOID");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        PnrPanel.add(jLabel1, gridBagConstraints);

        jLabel2.setText("B.Agt Sine");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        PnrPanel.add(jLabel2, gridBagConstraints);

        jLabel3.setText("Vendor PNR");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        PnrPanel.add(jLabel3, gridBagConstraints);

        jLabel4.setText("TicketingOID");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        PnrPanel.add(jLabel4, gridBagConstraints);

        jLabel5.setText("T.Agt Sine");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        PnrPanel.add(jLabel5, gridBagConstraints);

        jLabel6.setText("Airline");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        PnrPanel.add(jLabel6, gridBagConstraints);

        jLabel7.setText("Total Psgr");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        PnrPanel.add(jLabel7, gridBagConstraints);

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Booking Date");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        PnrPanel.add(jLabel9, gridBagConstraints);

        txtBookingOID.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtBookingOIDFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        PnrPanel.add(txtBookingOID, gridBagConstraints);

        txtBAgentSine.setEditable(false);
        txtBAgentSine.setToolTipText("PNR Creator Agent Sine ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        PnrPanel.add(txtBAgentSine, gridBagConstraints);

        txtVendorPNR.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        PnrPanel.add(txtVendorPNR, gridBagConstraints);

        txtTicketingOID.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtTicketingOIDFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        PnrPanel.add(txtTicketingOID, gridBagConstraints);

        txtAirline.setToolTipText("Airline code: BA,EK etc");
        txtAirline.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtAirlineFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        PnrPanel.add(txtAirline, gridBagConstraints);

        txtTAgentSine.setEditable(false);
        txtTAgentSine.setToolTipText("Ticketing Agent Sine");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        PnrPanel.add(txtTAgentSine, gridBagConstraints);

        txtTotalPsgr.setEditable(false);
        txtTotalPsgr.setToolTipText("Total number of passengers in PNR ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        PnrPanel.add(txtTotalPsgr, gridBagConstraints);

        dtBookingDate.setPreferredSize(new java.awt.Dimension(108, 21));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        PnrPanel.add(dtBookingDate, gridBagConstraints);

        txtPnr.setEditable(false);
        txtPnr.setBackground(new java.awt.Color(0, 0, 0));
        txtPnr.setColumns(20);
        txtPnr.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        txtPnr.setForeground(new java.awt.Color(255, 0, 0));
        txtPnr.setRows(1);
        txtPnr.setText("PNR:");
        txtPnr.setAutoscrolls(false);
        jScrollPane1.setViewportView(txtPnr);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        PnrPanel.add(jScrollPane1, gridBagConstraints);

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Cancel Date");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        PnrPanel.add(jLabel8, gridBagConstraints);

        dtCanceldate.setPreferredSize(new java.awt.Dimension(108, 21));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 2, 2);
        PnrPanel.add(dtCanceldate, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        PnrPanel.add(filler2, gridBagConstraints);

        javax.swing.GroupLayout TopPanelLayout = new javax.swing.GroupLayout(TopPanel);
        TopPanel.setLayout(TopPanelLayout);
        TopPanelLayout.setHorizontalGroup(
            TopPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TopPanelLayout.createSequentialGroup()
                .addComponent(PnrPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(accountingDocumentsComponent, javax.swing.GroupLayout.DEFAULT_SIZE, 633, Short.MAX_VALUE))
        );
        TopPanelLayout.setVerticalGroup(
            TopPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PnrPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE)
            .addComponent(accountingDocumentsComponent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        innerSplitPane.setTopComponent(TopPanel);

        tabsTicket.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(ticketComponent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(ticketComponent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        tabsTicket.addTab("Tickets", jPanel4);

        tblItinerrary.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "", "D.From", "D.To", "D.Date", "D.Time", "A.Date", "A.Time", "AirLine", "FlightNo", "Cls", "Status", "Baggage", "Terminal", "ChkInTime", "MealCode", "FlDuration", "Milage"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblItinerrary.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        tblItinerrary.setSortable(false);
        tblItinerrary.getTableHeader().setReorderingAllowed(false);
        tblItinerrary.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblItinerraryMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblItinerrary);
        if (tblItinerrary.getColumnModel().getColumnCount() > 0) {
            tblItinerrary.getColumnModel().getColumn(0).setMinWidth(30);
            tblItinerrary.getColumnModel().getColumn(0).setPreferredWidth(30);
            tblItinerrary.getColumnModel().getColumn(0).setMaxWidth(30);
            tblItinerrary.getColumnModel().getColumn(9).setMinWidth(30);
            tblItinerrary.getColumnModel().getColumn(9).setPreferredWidth(30);
            tblItinerrary.getColumnModel().getColumn(9).setMaxWidth(30);
        }

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 972, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(90, Short.MAX_VALUE))
        );

        tabsTicket.addTab("Segments", jPanel1);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(remarkComponent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(348, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(remarkComponent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(50, Short.MAX_VALUE))
        );

        tabsTicket.addTab("Remarks", jPanel5);

        javax.swing.GroupLayout TicketPanelLayout = new javax.swing.GroupLayout(TicketPanel);
        TicketPanel.setLayout(TicketPanelLayout);
        TicketPanelLayout.setHorizontalGroup(
            TicketPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabsTicket, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        TicketPanelLayout.setVerticalGroup(
            TicketPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TicketPanelLayout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addComponent(tabsTicket))
        );

        innerSplitPane.setRightComponent(TicketPanel);

        mainSplitPane.setLeftComponent(innerSplitPane);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        clientComponent.setMinimumSize(new java.awt.Dimension(95, 162));
        clientComponent.setPreferredSize(new java.awt.Dimension(178, 286));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        jPanel2.add(clientComponent, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        jPanel2.add(ticketingAgentComponent, gridBagConstraints);

        progressBar.setMinimumSize(new java.awt.Dimension(150, 17));
        progressBar.setPreferredSize(new java.awt.Dimension(146, 17));
        progressBar.setStringPainted(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 2, 2);
        jPanel2.add(progressBar, gridBagConstraints);

        mainSplitPane.setRightComponent(jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 1223, Short.MAX_VALUE)
            .addComponent(CommandPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(CommandPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(mainSplitPane)
                .addGap(0, 0, 0))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        loadCompletePnr();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnCreateInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateInvoiceActionPerformed
        newTSalesAcDocumentTask();
    }//GEN-LAST:event_btnCreateInvoiceActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        savePnr();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void txtBookingOIDFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBookingOIDFocusLost
        //this.pnr.setBookingAgtOid(checkValue(txtBookingOID, pnr.getBookingAgtOid()));
    }//GEN-LAST:event_txtBookingOIDFocusLost

    private void txtTicketingOIDFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTicketingOIDFocusLost
        //this.pnr.setTicketingAgtOid(checkValue(txtTicketingOID, pnr.getTicketingAgtOid()));
    }//GEN-LAST:event_txtTicketingOIDFocusLost

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        parent.removeTabRefference(pnr.getGdsPnr());
    }//GEN-LAST:event_btnCloseActionPerformed

    private void txtAirlineFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAirlineFocusLost
        this.pnr.setAirLineCode(checkValue(txtAirline, pnr.getAirLineCode()));
    }//GEN-LAST:event_txtAirlineFocusLost

    private void btnNewDocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewDocActionPerformed
        if (accountingDocumentsComponent.getSelectedTab() == 0) {
            showSalesAcDocDlg(new TicketingSalesAcDoc());
        } else {
            showPurchaseAcDocDlg(new TicketingPurchaseAcDoc());
        }
    }//GEN-LAST:event_btnNewDocActionPerformed

    private void tblItinerraryMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblItinerraryMouseClicked
        if (evt.getClickCount() == 2) {
            int index = tblItinerrary.getSelectedRow();
            if (index != -1) {
                Itinerary segment = this.segments.get(index);
                Pnr p = new Pnr();
                p.setId(pnr.getId());
                segment.setPnr(p);

                Window w = SwingUtilities.getWindowAncestor(this);
                Frame owner = w instanceof Frame ? (Frame) w : null;
                DlgItinerary dlg = new DlgItinerary(owner);
                if(dlg.showDialog(segment)){
                 populateTblItinerary();
                }
            }
        }
    }//GEN-LAST:event_tblItinerraryMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel CommandPanel;
    private javax.swing.JPanel PnrPanel;
    private javax.swing.JPanel TicketPanel;
    private javax.swing.JPanel TopPanel;
    private com.ets.fe.acdoc.gui.comp.AccountingDocumentsComponent accountingDocumentsComponent;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnCreateInvoice;
    private javax.swing.JButton btnNewDoc;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSave;
    private com.ets.fe.a_main.ClientSearchComponent clientComponent;
    private org.jdesktop.swingx.JXDatePicker dtBookingDate;
    private org.jdesktop.swingx.JXDatePicker dtCanceldate;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JSplitPane innerSplitPane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane mainSplitPane;
    private javax.swing.JProgressBar progressBar;
    private com.ets.fe.a_main.RemarkComponent remarkComponent;
    private javax.swing.JTabbedPane tabsTicket;
    private org.jdesktop.swingx.JXTable tblItinerrary;
    private com.ets.fe.a_main.TicketComponent ticketComponent;
    private com.ets.fe.a_main.TicketingAgentComponent ticketingAgentComponent;
    private javax.swing.JTextField txtAirline;
    private javax.swing.JTextField txtBAgentSine;
    private javax.swing.JTextField txtBookingOID;
    private javax.swing.JTextArea txtPnr;
    private javax.swing.JTextField txtTAgentSine;
    private javax.swing.JTextField txtTicketingOID;
    private javax.swing.JTextField txtTotalPsgr;
    private javax.swing.JTextField txtVendorPNR;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress".equals(evt.getPropertyName())) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
            if (progress == 100) {
                try {
                    if ("AC_DOCUMENT".equals(taskType)) {
                        TicketingSalesAcDoc draftDocument = newTSalesDocumentTask.get();

                        if (draftDocument != null) {
                            if (draftDocument.getType().equals(Enums.AcDocType.INVOICE)) {
                                showTSalesInvoiceDlg(draftDocument);
                            } else if (draftDocument.getType().equals(Enums.AcDocType.DEBITMEMO)
                                    || draftDocument.getType().equals(Enums.AcDocType.CREDITMEMO)) {
                                showTSalesAcDocDlg(draftDocument);
                            }
                        }
                    } else {
                        pnr = completePnrTask.get();
                        if (pnr != null) {
                            editingLogic();
                            segments = pnr.getSegments();
                            populatePnr();
                            
                            if(tabsTicket.getSelectedIndex()==0){
                             ticketComponent.setTickets(pnr.getTickets());
                             ticketComponent.populateTblTicket(pnr.getTickets());
                            }else if(tabsTicket.getSelectedIndex()==1){
                             populateTblItinerary();
                            }
                            
                            setPnrOwner();
                            SetTicketingAgent();                            
                        }
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(AgentFrame.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    updateSavingStatus(false);
                    this.taskType = "";
                }
            }
        }
    }

    private String checkValue(JTextField jTextField, String oldValue) {
        String val = jTextField.getText();
        if (val != null && !val.isEmpty()) {
            val = val.trim();
            jTextField.setBackground(Color.WHITE);
        } else {
            jTextField.setText(oldValue);
        }

        return val;
    }

    private ChangeListener tabsTicketListener = new ChangeListener() {

        @Override
        public void stateChanged(ChangeEvent e) {

            if (pnr != null) {
                if (tabsTicket.getSelectedIndex() == 1) {
                    populateTblItinerary();
                } else if (tabsTicket.getSelectedIndex() == 2) {
                    remarkComponent.setPnr(pnr);
                    remarkComponent.load();
                }
            }
        }
    };

    public void showTSalesInvoiceDlg(TicketingSalesAcDoc acdoc) {
        Window w = SwingUtilities.getWindowAncestor(this);
        Frame owner = w instanceof Frame ? (Frame) w : null;

        SalesInvoiceDlg dlg = new SalesInvoiceDlg(owner);
        dlg.setLocationRelativeTo(this);
        if (dlg.showDialog(acdoc)) {
            loadCompletePnr();
        }
    }

    public void showTSalesAcDocDlg(TicketingSalesAcDoc acdoc) {
        Window w = SwingUtilities.getWindowAncestor(this);
        Frame owner = w instanceof Frame ? (Frame) w : null;

        SalesTktdMemoDlg dlg = new SalesTktdMemoDlg(owner);
        dlg.setLocationRelativeTo(this);
        if (dlg.showDialog(acdoc)) {
            loadCompletePnr();
        }
    }

    public void showSalesAcDocDlg(TicketingSalesAcDoc acdoc) {
        Window w = SwingUtilities.getWindowAncestor(this);
        Frame owner = w instanceof Frame ? (Frame) w : null;

        if (acdoc.getId() == null) {
            TicketingSalesAcDoc invoice = accountingDocumentsComponent.getSalesSummeryInvoice();
            acdoc.setPnr(pnr);
            acdoc.setReference(invoice.getReference());
            acdoc.setParent(invoice);
            acdoc.setDocIssueDate(new java.util.Date());
        }
        SalesAcDocumentDlg dlg = new SalesAcDocumentDlg(owner);
        dlg.setLocationRelativeTo(this);
        if (dlg.showDialog(acdoc)) {
            loadCompletePnr();
        }
    }

    public void showTPurchaseInvoiceDlg(TicketingPurchaseAcDoc acdoc) {
        Window w = SwingUtilities.getWindowAncestor(this);
        Frame owner = w instanceof Frame ? (Frame) w : null;

        PurchaseInvoiceDlg dlg = new PurchaseInvoiceDlg(owner);
        dlg.setLocationRelativeTo(this);
        if (dlg.showDialog(acdoc)) {
            loadCompletePnr();
        }
    }

    public void showTPurchaseAcDocDlg(TicketingPurchaseAcDoc acdoc) {
        Window w = SwingUtilities.getWindowAncestor(this);
        Frame owner = w instanceof Frame ? (Frame) w : null;

        PurchaseTktdMemoDlg dlg = new PurchaseTktdMemoDlg(owner);
        dlg.setLocationRelativeTo(this);
        if (dlg.showDialog(acdoc)) {
            loadCompletePnr();
        }
    }

    public void showPurchaseAcDocDlg(TicketingPurchaseAcDoc acdoc) {
        Window w = SwingUtilities.getWindowAncestor(this);
        Frame owner = w instanceof Frame ? (Frame) w : null;

        if (acdoc.getId() == null) {
            TicketingPurchaseAcDoc invoice = accountingDocumentsComponent.getPurchaseSummeryInvoice();
            acdoc.setPnr(pnr);
            acdoc.setReference(invoice.getReference());
            acdoc.setParent(invoice);
            acdoc.setDocIssueDate(new java.util.Date());
        }
        PurchaseAcDocumentDlg dlg = new PurchaseAcDocumentDlg(owner);
        dlg.setLocationRelativeTo(this);
        if (dlg.showDialog(acdoc)) {
            //callAccountingDocs();
            loadCompletePnr();
        }
    }

    @Override
    public void componentResized(ComponentEvent e) {
        int width = getWidth();
        int rightPanelRatio = 83;
        mainSplitPane.setDividerLocation(width * rightPanelRatio / 100);
    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }
}
