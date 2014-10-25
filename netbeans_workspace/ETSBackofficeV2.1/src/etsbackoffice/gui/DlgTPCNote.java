
/*
 * FrameInvoice.java
 *
 * Created on 07-Dec-2010, 11:24:00
 */
package etsbackoffice.gui;

import etsbackoffice.ETSBackofficeApp;
import etsbackoffice.businesslogic.*;
import etsbackoffice.domain.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 *
 * @author Yusuf
 */
public class DlgTPCNote extends javax.swing.JDialog {    
    private DateFormat df = new DateFormat();
    private boolean saveNeeded;
    private boolean createDocument;
    private boolean submitNeeded;
    private DefaultTableModel tktModel;
    private DefaultTableModel transModel;
    private DefaultTableModel tblAdditionalInvLineModel;
    
    private AccountsBo accountsBo = (AccountsBo) ETSBackofficeApp.getApplication().ctx.getBean("accountsBo");
    private PNRBo pnrBo = (PNRBo) ETSBackofficeApp.getApplication().ctx.getBean("pnrBo");
    private AccountingDocBo acDocBo = (AccountingDocBo) ETSBackofficeApp.getApplication().ctx.getBean("acDocBo");
    private AcTransactionBo acTransactionBo = (AcTransactionBo) ETSBackofficeApp.getApplication().ctx.getBean("acTransactionBo");
    private OServiceBo oServiceBo = (OServiceBo) ETSBackofficeApp.getApplication().ctx.getBean("oServiceBo");
    private List<OtherService> additionalServices = new ArrayList();
    private List<Ticket> tickets = new ArrayList<Ticket>();
    DefaultComboBoxModel cmbContactableModel = new DefaultComboBoxModel();
    DefaultComboBoxModel cmbAdditionalSTitleModel;
    private Agent agent;
    
    private PurchaseAccountingDocument acDoc;
    private List<BillingTransaction> transactions;
    private List<PurchaseAccountingDocumentLine> additionalLines = new ArrayList<PurchaseAccountingDocumentLine>();    
    
    private JComboBox cmbAdditionalSTitle;
    

    public DlgTPCNote(java.awt.Frame parent) {
        super(parent, "Purchase Credit Note", true);
        initComponents();       
        initTblTicket();
    }

    private void setSaveNeeded(boolean saveNeeded) {
        if (saveNeeded != this.saveNeeded) {
            this.saveNeeded = saveNeeded;
        }
        if (this.saveNeeded == true) {
            btnSave.setEnabled(true);
        } else {
            btnSave.setEnabled(false);
        }
    }

    private void initTblTicket() {
        tktModel = (DefaultTableModel) tblTicket.getModel();
        TableColumn gFare, disc, atol;
        gFare = tblTicket.getColumnModel().getColumn(4);
        disc = tblTicket.getColumnModel().getColumn(5);
        atol = tblTicket.getColumnModel().getColumn(6);

        JTextField tktLineField = new JTextField();

        CheckInput c = new CheckInput(CheckInput.FLOAT);
        c.setNegativeAccepted(true);

        tktLineField.setDocument(c);
        gFare.setCellEditor(new DefaultCellEditor(tktLineField));
        disc.setCellEditor(new DefaultCellEditor(tktLineField));
        atol.setCellEditor(new DefaultCellEditor(tktLineField));
    }

    private void initTblAdditionalInvLine() {
        TableColumn sTitle, sChg;
        sTitle = tblAdditionalInvLine.getColumnModel().getColumn(0);
        sChg = tblAdditionalInvLine.getColumnModel().getColumn(1);

        JTextField jtf1 = new JTextField();
        CheckInput c = new CheckInput(CheckInput.FLOAT);
        c.setNegativeAccepted(true);
        jtf1.setDocument(c);
        sChg.setCellEditor(new DefaultCellEditor(jtf1));

        cmbAdditionalSTitle = new JComboBox();
        sTitle.setCellEditor(new DefaultCellEditor(cmbAdditionalSTitle));
    }

    private void populateFGuide(Ticket t) {
        txtFGuide.setText("");
        SimpleAttributeSet GREEN = new SimpleAttributeSet();
        SimpleAttributeSet WHITE = new SimpleAttributeSet();
        SimpleAttributeSet RED = new SimpleAttributeSet();
        StyleConstants.setForeground(GREEN, Color.GREEN);
        StyleConstants.setForeground(RED, Color.RED);
        StyleConstants.setForeground(WHITE, Color.WHITE);


        Document fGuideDoc = txtFGuide.getDocument();

        try {
            fGuideDoc.insertString(fGuideDoc.getLength(), "Base Fare : " + t.getBaseFare() + "          " + "Selling Fare : " + t.getGrossFare(), GREEN);
            fGuideDoc.insertString(fGuideDoc.getLength(), "\nTax         : " + t.getTax(), GREEN);
            fGuideDoc.insertString(fGuideDoc.getLength(), "\nCom(BSP): " + t.getBspCom() + "             " + "Com/Disc     : " + t.getDiscount(), GREEN);
            fGuideDoc.insertString(fGuideDoc.getLength(), "\nATOL      : " + t.getAtolVendor() + "             " + "ATOL          : " + t.getAtolVendor(), GREEN);
            fGuideDoc.insertString(fGuideDoc.getLength(), "\n-------------------------------------------------------------", WHITE);
            fGuideDoc.insertString(fGuideDoc.getLength(), "\nPurchase Balance  : " + t.getNetBillable() + "          " + "Selling Balance  : " + t.getNetPayble(), RED);
            fGuideDoc.insertString(fGuideDoc.getLength(), "\nRevenue current ticket: " + t.getNetPayble().subtract(t.getNetBillable()), GREEN);
        } catch (BadLocationException ex) {
            Logger.getLogger(DlgTPCNote.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void populateCmbSTitle() {
        Thread t = new Thread(new threadLoadAdditionalServices());
        t.start();
        try {
            t.join();
            List cmbAdditionalElement = new ArrayList();

            for (OtherService o : this.additionalServices) {
                cmbAdditionalElement.add(o.getServiceTitle());
            }

            cmbAdditionalSTitleModel = new DefaultComboBoxModel(cmbAdditionalElement.toArray());
            cmbAdditionalSTitle.setModel(cmbAdditionalSTitleModel);
            cmbAdditionalSTitle.addActionListener(cmbAdditionalSTitleListener);
        } catch (InterruptedException ex) {
            Logger.getLogger(DlgTPCNote.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void populateTxtAgentDetails() {

        txtContactableDetails.setText(agent.getName() + "\n");

        if (!this.agent.getAddLine1().isEmpty()) {
            txtContactableDetails.append(this.agent.getAddLine1() + "\n");
        }

        if (!this.agent.getAddLine2().isEmpty()) {
            txtContactableDetails.append(this.agent.getAddLine2() + "\n");
        }
        if (!this.agent.getProvince().isEmpty()) {
            txtContactableDetails.append(this.agent.getProvince() + "\n");
        }
        if (!this.agent.getPostCode().isEmpty()) {
            txtContactableDetails.append(this.agent.getPostCode() + "\n");
        }
        if (!this.agent.getTelNo().isEmpty()) {
            txtContactableDetails.append("TelNo: " + this.agent.getTelNo() + "\n");
        }
    }

   public void loadObjects(PurchaseAccountingDocument acDoc){
        this.transactions = new ArrayList(this.acDoc.getBillingTransactions());
        this.additionalLines = new ArrayList(this.acDoc.getAdditionalServiceLine());
        this.tickets = new ArrayList(this.acDoc.getTickets());

        if (this.acDoc.getPnr().getAgent() != null) {
            this.agent = acDoc.getPnr().getAgent();
            populateTxtAgentDetails();
        }        
        populateTblTicket();
        populateInvoice();
        populateTblTransaction(this.transactions);
        populatetblAdditionalInvLine();
        txtFGuide.setText("Select a ticket to view fare details");
        initControlls();
        //initTransControl();
   }
   
   public boolean showDialog(PurchaseAccountingDocument acDoc) {

       if (acDoc.getPurchaseAcDocId() != 0) {
           new Thread(new threadLoadInvoice(acDoc.getPurchaseAcDocId())).start();
       } else {
           this.acDoc = acDoc;
           loadObjects(this.acDoc);
       }  
        
        setLocationRelativeTo(this);
        setVisible(true);
        
        createDocument = false;

        if (createDocument) {
            acDoc = this.acDoc;
        }
        return createDocument;
    }

    private void populateTblTicket() {
        tktModel = (DefaultTableModel) tblTicket.getModel();
        tblTicket.clearSelection();
        tktModel.getDataVector().removeAllElements();
        int row = 0;

        for (Ticket t : this.tickets) {

            tktModel.insertRow(row, new Object[]{t.getFullPaxNameWithPaxNo(),
                        t.getTicketNo(), t.getTktStatusString(),
                        t.getDocIssuedate(), t.getBaseFare(),t.getTax(),t.getTotalFare(),
                        t.getBspCom(), t.getAtolVendor(), t.getNetBillable()});
            row++;
        }
        tktModel.addTableModelListener(new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    int row = e.getLastRow();
                    int column = e.getColumn();

                    if (column == 4) {
                        String newGFare = tblTicket.getValueAt(row, 4).toString().replaceAll("[^.0-9]", "");

                        if (!newGFare.isEmpty()) {
                            if (tickets.get(row).getTktStatus() == 4) {
                                tickets.get(row).setGrossFare(new BigDecimal(newGFare).negate());
                            } else {
                                tickets.get(row).setGrossFare(new BigDecimal(newGFare));
                            }
                            tktModel.setValueAt(tickets.get(row).getNetPayble(), row, 7);
                            recalculateDocument();
                            populateFGuide(tickets.get(row));
                        }
                    }

                    if (column == 5) {
                        String newDisc = tblTicket.getValueAt(row, 5).toString().replaceAll("[^.0-9]", "");
                        if (!newDisc.isEmpty()) {
                            if (tickets.get(row).getTktStatus() == 4) {
                                tickets.get(row).setDiscount(new BigDecimal(newDisc));
                            } else {
                                tickets.get(row).setDiscount(new BigDecimal(newDisc));
                            }

                            tktModel.setValueAt(tickets.get(row).getNetPayble(), row, 7);                           
                            recalculateDocument();
                            populateFGuide(tickets.get(row));
                        }
                    }

                    if (column == 6) {
                        String newAtol = tblTicket.getValueAt(row, 6).toString().replaceAll("[^.0-9]", "");
                        if (!newAtol.isEmpty()) {
                            if (tickets.get(row).getTktStatus() == 4) {
                                tickets.get(row).setAtolChg(new BigDecimal(newAtol).negate());
                            } else {
                                tickets.get(row).setAtolChg(new BigDecimal(newAtol));
                            }
                            tktModel.setValueAt(tickets.get(row).getNetPayble(), row, 7);                            
                            recalculateDocument();
                            populateFGuide(tickets.get(row));
                        }
                    }
                }
            }
        });
    }

    private void populatetblAdditionalInvLine() {
        tblAdditionalInvLineModel = (DefaultTableModel) tblAdditionalInvLine.getModel();
        tblAdditionalInvLineModel.getDataVector().removeAllElements();
        tblAdditionalInvLine.repaint();
        Iterator it = this.additionalLines.iterator();
        int row = 0;
        while (it.hasNext()) {
            PurchaseAccountingDocumentLine line = (PurchaseAccountingDocumentLine) it.next();

            tblAdditionalInvLineModel.insertRow(row, new Object[]{line.getRemark(),
                        line.getAmount()});
            row++;

        }
        if (this.acDoc.getIssueDate() == null) {
            tblAdditionalInvLineModel.addRow(new Object[]{"", ""});
        }

        tblAdditionalInvLineModel.addTableModelListener(new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    int row = e.getLastRow();
                    int column = e.getColumn();

                    if (column == 1) {
                        String sCharge = tblAdditionalInvLine.getValueAt(row, column).toString();
                        if (!sCharge.isEmpty() && additionalLines.size() > row) {
                            if (additionalLines.get(row).getPurchaseAccountingDocument() == null) {
                                additionalLines.get(row).setAmount(new BigDecimal(sCharge));
                            }
                        }
                    }
                    populatetblAdditionalInvLine();
                    recalculateDocument();
                }
            }
        });
    }

    private void populateTblTransaction(List<BillingTransaction> transactions) {
        transModel = (DefaultTableModel) tblTransaction.getModel();
        transModel.getDataVector().removeAllElements();

        for (BillingTransaction t : transactions) {
            int row = 0;
            transModel.insertRow(row, new Object[]{t.getTransDate(), t.getTransTypeString(), t.getTransAmount(), t.getTransRef()});
            row++;
        }
    }

    private void populateInvoice() {

        if (this.acDoc.getIssueDate() != null) {
            datePicker.setDate(this.acDoc.getIssueDate());
        }
        if (this.acDoc.getRecipientRef() != null) {
            txtRecipientRef.setText(String.valueOf(this.acDoc.getRecipientRef()));
        }
        if (this.acDoc.getVendorRef() != null) {
            txtVendorRef.setText(String.valueOf(this.acDoc.getVendorRef()));
        }
        if (this.acDoc.getTerms() != null) {
            cmbTerms.setSelectedItem(this.acDoc.getTerms());
        } else {
            cmbTerms.setSelectedIndex(-1);
        }

        lblTSubTotal.setText(this.acDoc.getTktdSubTotal().toString());
        lblOSubTotal.setText(this.acDoc.getAdditionalServiceSubTotal().toString());
        lblCNoteAmount.setText(this.acDoc.getTotalDocumentedAmount().toString());
        lblReceived.setText(this.acDoc.getTotalPaid().toString());
        lblOutstanding.setText(this.acDoc.getOutstandingAmount().toString());
        
        if (this.acDoc.getPurchaseAcDocId() != 0) {
            lblInvAmount.setText(this.acDoc.getPurchaseAccountingDocument().getTotalDocumentedAmount().
                    add(this.acDoc.getPurchaseAccountingDocument().getTotalAdm()).toString());
            lblPaid.setText(this.acDoc.getPurchaseAccountingDocument().getTotalPaid().toString());
            lblFinalBalance.setText(this.acDoc.getOutstandingAmount().add(this.acDoc.getPurchaseAccountingDocument().getDueAmountWithRelatedDoc()).toString());
        }
    }
        
    private void disableComponent() {
        cmbTerms.setEnabled(false);
    }

    private void recalculateDocument() {
        BigDecimal tktSubTotal = new BigDecimal("0.00");
        BigDecimal oSubTotal = new BigDecimal("0.00");
        BigDecimal transBalance = new BigDecimal("0.00");

        tktSubTotal = this.acDoc.getTktdSubTotal();
        oSubTotal = this.acDoc.getAdditionalServiceSubTotal();
        transBalance = this.acDoc.getTotalPaid();
        if (!this.acDoc.getRelatedDocuments().isEmpty()) {
            for (PurchaseAccountingDocument a : this.acDoc.getRelatedDocuments()) {
                tktSubTotal.add(a.getTktdSubTotal());
                oSubTotal.add(a.getAdditionalServiceSubTotal());
                transBalance.add(a.getTotalPaid());
            }
        }
        for (PurchaseAccountingDocumentLine l : this.additionalLines) {
            oSubTotal = oSubTotal.add(l.getAmount());
        }
        lblTSubTotal.setText(this.acDoc.getTktdSubTotal().toString());
        lblOSubTotal.setText(this.acDoc.getAdditionalServiceSubTotal().toString());
        lblCNoteAmount.setText(this.acDoc.getTotalDocumentedAmount().toString());
        lblReceived.setText(this.acDoc.getTotalPaid().toString());
        lblOutstanding.setText(this.acDoc.getOutstandingAmount().toString());
        
    }

    /*private void initTransControl() {
        tabsTransaction.setSelectedIndex(0);
        if (this.acDoc.getPurchaseAcDocId() != 0) {
            if (this.acDoc.getOutstandingAmount().add(this.acDoc.getPurchaseAccountingDocument().getDueAmountWithRelatedDoc()).compareTo(new BigDecimal("0.00")) >= 0
                    || this.acDoc.getIssueDate() == null) {
                tabsTransaction.setEnabledAt(1, false);
            } else if (this.acDoc.getOutstandingAmount().add(this.acDoc.getPurchaseAccountingDocument().getDueAmountWithRelatedDoc()).compareTo(new BigDecimal("0.00")) <= 0
                    || this.acDoc.getIssueDate() != null) {
                tabsTransaction.setEnabledAt(1, true);
            }
        } else {
            tabsTransaction.setEnabledAt(1, false);
        }
    }*/

    private void initControlls() {
        if (this.acDoc.getIssueDate() != null) {
            disableComponent();
            tblAdditionalInvLine.setEditable(false);
            tblTicket.setEditable(false);
            btnPrint.setEnabled(true);           
            btnPreview.setEnabled(true);
            btnEmail.setEnabled(true);
            btnSave.setEnabled(false);
        } else {
            tblAdditionalInvLine.setEditable(true);
            tblTicket.setEditable(true);
            initTblAdditionalInvLine();
            btnPrint.setEnabled(false);            
            btnPreview.setEnabled(false);
            btnEmail.setEnabled(false);
            btnSave.setEnabled(true);
            populateCmbSTitle();
        }
    }

    private void initAcDocInLine(PurchaseAccountingDocument o, List<PurchaseAccountingDocumentLine> ls) {
        for (PurchaseAccountingDocumentLine l : ls) {
            l.setPurchaseAccountingDocument(o);
        }
    }            
    
    private ActionListener cmbAdditionalSTitleListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            JComboBox cb = (JComboBox) e.getSource();
            int row = tblAdditionalInvLine.getSelectedRow();
            int cmbSTitleIndex = cb.getSelectedIndex();

            if (row != -1) {
                PurchaseAccountingDocumentLine line = new PurchaseAccountingDocumentLine();
                line.setRemark(additionalServices.get(cmbSTitleIndex).getServiceTitle());
                if (acDoc.getAcDoctype() == 1) {
                    line.setAmount(additionalServices.get(cmbSTitleIndex).getFinalServiceCharge(new BigDecimal(lblCNoteAmount.getText())));
                } else if (acDoc.getAcDoctype() == 2) {
                    line.setAmount(additionalServices.get(cmbSTitleIndex).getFinalServiceCharge(new BigDecimal(lblCNoteAmount.getText())).negate());
                }
                line.setType(2);
                line.setOtherService(additionalServices.get(cmbSTitleIndex));

                additionalLines.add(line);
                populatetblAdditionalInvLine();
                recalculateDocument();
            }
        }
    };
    private ListSelectionListener tblTicketListener = new ListSelectionListener() {

        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int selectedRow = tblTicket.getSelectedRow();
            if (selectedRow != -1) {
                populateFGuide(tickets.get(selectedRow));
            }
        }
    }; 
        
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        jToolBar1 = new javax.swing.JToolBar();
        btnSave = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        btnPreview = new javax.swing.JButton();
        btnEmail = new javax.swing.JButton();
        pnlStatus = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        jSeparator2 = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        pnlMain = new javax.swing.JPanel();
        pnlHeader = new javax.swing.JPanel();
        pnlInvoiceFor = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtContactableDetails = new javax.swing.JTextArea();
        lblDocFor = new javax.swing.JLabel();
        pnInvlHeader = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtRecipientRef = new javax.swing.JTextField();
        cmbTerms = new javax.swing.JComboBox();
        datePicker = new org.jdesktop.swingx.JXDatePicker();
        jLabel9 = new javax.swing.JLabel();
        txtVendorRef = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtFGuide = new javax.swing.JTextPane();
        pnlInvLine = new javax.swing.JPanel();
        jToolBar3 = new javax.swing.JToolBar();
        btnRemoveLine1 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblAdditionalInvLine = new org.jdesktop.swingx.JXTable();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblTicket = new org.jdesktop.swingx.JXTable();
        tblTicket.getSelectionModel().addListSelectionListener(tblTicketListener);
        lblAdditionalServices = new javax.swing.JLabel();
        pnlTransaction = new javax.swing.JPanel();
        tabsTransaction = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTransaction = new org.jdesktop.swingx.JXTable();
        jPanel6 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        lblPaid1 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblTSubTotal = new javax.swing.JLabel();
        lblOSubTotal = new javax.swing.JLabel();
        lblCNoteAmount = new javax.swing.JLabel();
        lblReceived = new javax.swing.JLabel();
        lblOutstanding = new javax.swing.JLabel();
        lblInvAmount = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        lblPaid = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lblFinalBalance = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(700, 440));
        setName("Form"); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jToolBar1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getResourceMap(DlgTPCNote.class);
        btnSave.setFont(resourceMap.getFont("btnSave.font")); // NOI18N
        btnSave.setIcon(resourceMap.getIcon("btnSave.icon")); // NOI18N
        btnSave.setText(resourceMap.getString("btnSave.text")); // NOI18N
        btnSave.setFocusable(false);
        btnSave.setName("btnSave"); // NOI18N
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSave);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getActionMap(DlgTPCNote.class, this);
        btnPrint.setAction(actionMap.get("printAcDoc")); // NOI18N
        btnPrint.setFont(resourceMap.getFont("btnEmail.font")); // NOI18N
        btnPrint.setIcon(resourceMap.getIcon("btnPrint.icon")); // NOI18N
        btnPrint.setText(resourceMap.getString("btnPrint.text")); // NOI18N
        btnPrint.setToolTipText(resourceMap.getString("btnPrint.toolTipText")); // NOI18N
        btnPrint.setFocusable(false);
        btnPrint.setName("btnPrint"); // NOI18N
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPrint);

        btnPreview.setFont(resourceMap.getFont("btnEmail.font")); // NOI18N
        btnPreview.setIcon(resourceMap.getIcon("btnPreview.icon")); // NOI18N
        btnPreview.setText(resourceMap.getString("btnPreview.text")); // NOI18N
        btnPreview.setToolTipText(resourceMap.getString("btnPreview.toolTipText")); // NOI18N
        btnPreview.setFocusable(false);
        btnPreview.setName("btnPreview"); // NOI18N
        btnPreview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviewActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPreview);

        btnEmail.setFont(resourceMap.getFont("btnEmail.font")); // NOI18N
        btnEmail.setIcon(resourceMap.getIcon("btnEmail.icon")); // NOI18N
        btnEmail.setText(resourceMap.getString("btnEmail.text")); // NOI18N
        btnEmail.setToolTipText(resourceMap.getString("btnEmail.toolTipText")); // NOI18N
        btnEmail.setFocusable(false);
        btnEmail.setName("btnEmail"); // NOI18N
        btnEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEmailActionPerformed(evt);
            }
        });
        jToolBar1.add(btnEmail);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        getContentPane().add(jToolBar1, gridBagConstraints);

        pnlStatus.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlStatus.setName("pnlStatus"); // NOI18N
        pnlStatus.setLayout(new java.awt.GridBagLayout());

        progressBar.setName("progressBar"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlStatus.add(progressBar, gridBagConstraints);

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator2.setName("jSeparator2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.weighty = 1.0;
        pnlStatus.add(jSeparator2, gridBagConstraints);

        statusMessageLabel.setFont(resourceMap.getFont("statusMessageLabel.font")); // NOI18N
        statusMessageLabel.setText(resourceMap.getString("statusMessageLabel.text")); // NOI18N
        statusMessageLabel.setName("statusMessageLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 0);
        pnlStatus.add(statusMessageLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        getContentPane().add(pnlStatus, gridBagConstraints);

        pnlMain.setBackground(resourceMap.getColor("pnlMain.background")); // NOI18N
        pnlMain.setName("pnlMain"); // NOI18N
        pnlMain.setLayout(new java.awt.GridBagLayout());

        pnlHeader.setName("pnlHeader"); // NOI18N
        pnlHeader.setOpaque(false);

        pnlInvoiceFor.setName("pnlInvoiceFor"); // NOI18N
        pnlInvoiceFor.setOpaque(false);
        pnlInvoiceFor.setLayout(new java.awt.GridBagLayout());

        jScrollPane4.setName("jScrollPane4"); // NOI18N
        jScrollPane4.setPreferredSize(new java.awt.Dimension(220, 76));

        txtContactableDetails.setColumns(16);
        txtContactableDetails.setEditable(false);
        txtContactableDetails.setFont(resourceMap.getFont("txtContactableDetails.font")); // NOI18N
        txtContactableDetails.setLineWrap(true);
        txtContactableDetails.setRows(5);
        txtContactableDetails.setName("txtContactableDetails"); // NOI18N
        jScrollPane4.setViewportView(txtContactableDetails);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlInvoiceFor.add(jScrollPane4, gridBagConstraints);

        lblDocFor.setFont(resourceMap.getFont("lblDocFor.font")); // NOI18N
        lblDocFor.setText(resourceMap.getString("lblDocFor.text")); // NOI18N
        lblDocFor.setName("lblDocFor"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        pnlInvoiceFor.add(lblDocFor, gridBagConstraints);

        pnInvlHeader.setBackground(resourceMap.getColor("pnInvlHeader.background")); // NOI18N
        pnInvlHeader.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnInvlHeader.setName("pnInvlHeader"); // NOI18N

        jPanel2.setBackground(resourceMap.getColor("jPanel2.background")); // NOI18N
        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new java.awt.GridBagLayout());

        lblTitle.setFont(resourceMap.getFont("lblTitle.font")); // NOI18N
        lblTitle.setForeground(resourceMap.getColor("lblTitle.foreground")); // NOI18N
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblTitle.setText(resourceMap.getString("lblTitle.text")); // NOI18N
        lblTitle.setName("lblTitle"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(lblTitle, gridBagConstraints);

        jLabel1.setFont(resourceMap.getFont("txtVendorRef.font")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(jLabel1, gridBagConstraints);

        jLabel2.setFont(resourceMap.getFont("txtVendorRef.font")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(jLabel2, gridBagConstraints);

        jLabel4.setFont(resourceMap.getFont("txtVendorRef.font")); // NOI18N
        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(jLabel4, gridBagConstraints);

        txtRecipientRef.setEditable(false);
        txtRecipientRef.setFont(resourceMap.getFont("txtVendorRef.font")); // NOI18N
        txtRecipientRef.setText(resourceMap.getString("txtRecipientRef.text")); // NOI18N
        txtRecipientRef.setName("txtRecipientRef"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(txtRecipientRef, gridBagConstraints);

        cmbTerms.setFont(resourceMap.getFont("txtVendorRef.font")); // NOI18N
        cmbTerms.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Select", "CIA", "COD", "CWO", "Net monthly account", "Net 7", "Net 10", "Net 30", "Net 60", "Net 90" }));
        cmbTerms.setName("cmbTerms"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(cmbTerms, gridBagConstraints);

        datePicker.setFont(resourceMap.getFont("txtVendorRef.font")); // NOI18N
        datePicker.setEditable(false);
        datePicker.setName("datePicker"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(datePicker, gridBagConstraints);

        jLabel9.setFont(resourceMap.getFont("txtVendorRef.font")); // NOI18N
        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(jLabel9, gridBagConstraints);

        txtVendorRef.setFont(resourceMap.getFont("txtVendorRef.font")); // NOI18N
        txtVendorRef.setName("txtVendorRef"); // NOI18N
        txtVendorRef.setPreferredSize(new java.awt.Dimension(150, 22));
        txtVendorRef.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtVendorRefKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(txtVendorRef, gridBagConstraints);

        javax.swing.GroupLayout pnInvlHeaderLayout = new javax.swing.GroupLayout(pnInvlHeader);
        pnInvlHeader.setLayout(pnInvlHeaderLayout);
        pnInvlHeaderLayout.setHorizontalGroup(
            pnInvlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
        );
        pnInvlHeaderLayout.setVerticalGroup(
            pnInvlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
        );

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        txtFGuide.setBackground(resourceMap.getColor("txtFGuide.background")); // NOI18N
        txtFGuide.setFont(resourceMap.getFont("txtFGuide.font")); // NOI18N
        txtFGuide.setForeground(resourceMap.getColor("txtFGuide.foreground")); // NOI18N
        txtFGuide.setName("txtFGuide"); // NOI18N
        jScrollPane2.setViewportView(txtFGuide);

        javax.swing.GroupLayout pnlHeaderLayout = new javax.swing.GroupLayout(pnlHeader);
        pnlHeader.setLayout(pnlHeaderLayout);
        pnlHeaderLayout.setHorizontalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlHeaderLayout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlInvoiceFor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnInvlHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pnlHeaderLayout.setVerticalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
            .addComponent(pnlInvoiceFor, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
            .addComponent(pnInvlHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlMain.add(pnlHeader, gridBagConstraints);

        pnlInvLine.setName("pnlInvLine"); // NOI18N
        pnlInvLine.setOpaque(false);

        jToolBar3.setFloatable(false);
        jToolBar3.setOrientation(1);
        jToolBar3.setRollover(true);
        jToolBar3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jToolBar3.setName("jToolBar3"); // NOI18N
        jToolBar3.setToolTipText(resourceMap.getString("jToolBar3.toolTipText")); // NOI18N

        btnRemoveLine1.setFont(resourceMap.getFont("btnRemoveLine1.font")); // NOI18N
        btnRemoveLine1.setIcon(resourceMap.getIcon("btnRemoveLine1.icon")); // NOI18N
        btnRemoveLine1.setToolTipText(resourceMap.getString("btnRemoveLine1.toolTipText")); // NOI18N
        btnRemoveLine1.setFocusable(false);
        btnRemoveLine1.setName("btnRemoveLine1"); // NOI18N
        btnRemoveLine1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveLine1ActionPerformed(evt);
            }
        });
        jToolBar3.add(btnRemoveLine1);

        jScrollPane3.setMinimumSize(new java.awt.Dimension(19, 70));
        jScrollPane3.setName("jScrollPane3"); // NOI18N
        jScrollPane3.setPreferredSize(new java.awt.Dimension(157, 150));

        tblAdditionalInvLine.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null}
            },
            new String [] {
                "Service Title", "Charge"
            }
        ));
        tblAdditionalInvLine.setToolTipText(resourceMap.getString("tblAdditionalInvLine.toolTipText")); // NOI18N
        tblAdditionalInvLine.setEditable(false);
        tblAdditionalInvLine.setFont(resourceMap.getFont("tblAdditionalInvLine.font")); // NOI18N
        tblAdditionalInvLine.setName("tblAdditionalInvLine"); // NOI18N
        tblAdditionalInvLine.setSortable(false);
        tblAdditionalInvLine.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(tblAdditionalInvLine);
        tblAdditionalInvLine.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblAdditionalInvLine.columnModel.title0")); // NOI18N
        tblAdditionalInvLine.getColumnModel().getColumn(1).setMinWidth(80);
        tblAdditionalInvLine.getColumnModel().getColumn(1).setPreferredWidth(80);
        tblAdditionalInvLine.getColumnModel().getColumn(1).setMaxWidth(80);
        tblAdditionalInvLine.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblAdditionalInvLine.columnModel.title1")); // NOI18N

        jScrollPane5.setName("jScrollPane5"); // NOI18N
        jScrollPane5.setPreferredSize(new java.awt.Dimension(650, 150));

        tblTicket.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "PaxName", "TktNo", "Status", "Date", "BaseFare", "Tax", "TotalFare", "Com/Disc", "Atol", "NetPayable"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true, true, false, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblTicket.setColumnSelectionAllowed(true);
        tblTicket.setFont(resourceMap.getFont("tblTicket.font")); // NOI18N
        tblTicket.setName("tblTicket"); // NOI18N
        tblTicket.setSortable(false);
        tblTicket.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(tblTicket);
        tblTicket.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblTicket.getColumnModel().getColumn(0).setMinWidth(150);
        tblTicket.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblTicket.columnModel.title0")); // NOI18N
        tblTicket.getColumnModel().getColumn(1).setMinWidth(100);
        tblTicket.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblTicket.columnModel.title1")); // NOI18N
        tblTicket.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblTicket.columnModel.title2")); // NOI18N
        tblTicket.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblTicket.columnModel.title3")); // NOI18N
        tblTicket.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("tblTicket.columnModel.title4")); // NOI18N
        tblTicket.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("tblTicket.columnModel.title5")); // NOI18N
        tblTicket.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("tblTicket.columnModel.title6")); // NOI18N
        tblTicket.getColumnModel().getColumn(7).setHeaderValue(resourceMap.getString("tblTicket.columnModel.title7")); // NOI18N
        tblTicket.getColumnModel().getColumn(8).setHeaderValue(resourceMap.getString("tblTicket.columnModel.title9")); // NOI18N
        tblTicket.getColumnModel().getColumn(9).setHeaderValue(resourceMap.getString("tblTicket.columnModel.title10")); // NOI18N

        lblAdditionalServices.setFont(resourceMap.getFont("lblAdditionalServices.font")); // NOI18N
        lblAdditionalServices.setText(resourceMap.getString("lblAdditionalServices.text")); // NOI18N
        lblAdditionalServices.setName("lblAdditionalServices"); // NOI18N

        javax.swing.GroupLayout pnlInvLineLayout = new javax.swing.GroupLayout(pnlInvLine);
        pnlInvLine.setLayout(pnlInvLineLayout);
        pnlInvLineLayout.setHorizontalGroup(
            pnlInvLineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlInvLineLayout.createSequentialGroup()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 655, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlInvLineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlInvLineLayout.createSequentialGroup()
                        .addComponent(lblAdditionalServices, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(124, 124, 124))
                    .addGroup(pnlInvLineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jToolBar3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE))))
        );
        pnlInvLineLayout.setVerticalGroup(
            pnlInvLineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlInvLineLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(lblAdditionalServices)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
                .addGap(2, 2, 2)
                .addComponent(jToolBar3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.6;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlMain.add(pnlInvLine, gridBagConstraints);

        pnlTransaction.setName("pnlTransaction"); // NOI18N
        pnlTransaction.setOpaque(false);

        tabsTransaction.setFont(resourceMap.getFont("tabsTransaction.font")); // NOI18N
        tabsTransaction.setName("tabsTransaction"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new java.awt.CardLayout(2, 2));

        jScrollPane1.setName("jScrollPane1"); // NOI18N
        jScrollPane1.setPreferredSize(new java.awt.Dimension(300, 100));

        tblTransaction.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Date", "Type", "Amount", "Remarks"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblTransaction.setFont(resourceMap.getFont("tblTransaction.font")); // NOI18N
        tblTransaction.setName("tblTransaction"); // NOI18N
        tblTransaction.setSortable(false);
        tblTransaction.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblTransaction);
        tblTransaction.getColumnModel().getColumn(0).setMinWidth(60);
        tblTransaction.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblTransaction.getColumnModel().getColumn(0).setMaxWidth(100);
        tblTransaction.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblTransaction.columnModel.title0")); // NOI18N
        tblTransaction.getColumnModel().getColumn(1).setMinWidth(60);
        tblTransaction.getColumnModel().getColumn(1).setPreferredWidth(80);
        tblTransaction.getColumnModel().getColumn(1).setMaxWidth(100);
        tblTransaction.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblTransaction.columnModel.title1")); // NOI18N
        tblTransaction.getColumnModel().getColumn(2).setMinWidth(60);
        tblTransaction.getColumnModel().getColumn(2).setPreferredWidth(80);
        tblTransaction.getColumnModel().getColumn(2).setMaxWidth(100);
        tblTransaction.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblTransaction.columnModel.title2")); // NOI18N
        tblTransaction.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblTransaction.columnModel.title3")); // NOI18N

        jPanel1.add(jScrollPane1, "card2");

        tabsTransaction.addTab(resourceMap.getString("jPanel1.TabConstraints.tabTitle"), jPanel1); // NOI18N

        jPanel6.setBackground(resourceMap.getColor("jPanel6.background")); // NOI18N
        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel6.setName("jPanel6"); // NOI18N
        jPanel6.setLayout(new java.awt.GridBagLayout());

        jLabel7.setFont(resourceMap.getFont("jLabel14.font")); // NOI18N
        jLabel7.setForeground(resourceMap.getColor("jLabel12.foreground")); // NOI18N
        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(6, 2, 2, 2);
        jPanel6.add(jLabel7, gridBagConstraints);

        lblPaid1.setFont(resourceMap.getFont("jLabel14.font")); // NOI18N
        lblPaid1.setForeground(resourceMap.getColor("jLabel12.foreground")); // NOI18N
        lblPaid1.setText(resourceMap.getString("lblPaid1.text")); // NOI18N
        lblPaid1.setName("lblPaid1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(lblPaid1, gridBagConstraints);

        jLabel12.setFont(resourceMap.getFont("jLabel14.font")); // NOI18N
        jLabel12.setForeground(resourceMap.getColor("jLabel12.foreground")); // NOI18N
        jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(jLabel12, gridBagConstraints);

        jSeparator3.setName("jSeparator3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 2);
        jPanel6.add(jSeparator3, gridBagConstraints);

        jSeparator4.setName("jSeparator4"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(jSeparator4, gridBagConstraints);

        jLabel13.setFont(resourceMap.getFont("jLabel14.font")); // NOI18N
        jLabel13.setForeground(resourceMap.getColor("jLabel12.foreground")); // NOI18N
        jLabel13.setText(resourceMap.getString("jLabel13.text")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(jLabel13, gridBagConstraints);

        jLabel14.setFont(resourceMap.getFont("jLabel14.font")); // NOI18N
        jLabel14.setForeground(resourceMap.getColor("jLabel12.foreground")); // NOI18N
        jLabel14.setText(resourceMap.getString("jLabel14.text")); // NOI18N
        jLabel14.setName("jLabel14"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(jLabel14, gridBagConstraints);

        jLabel5.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        jLabel5.setForeground(resourceMap.getColor("jLabel5.foreground")); // NOI18N
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(jLabel5, gridBagConstraints);

        lblTSubTotal.setFont(resourceMap.getFont("lblOSubTotal.font")); // NOI18N
        lblTSubTotal.setForeground(resourceMap.getColor("lblOSubTotal.foreground")); // NOI18N
        lblTSubTotal.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblTSubTotal.setText(resourceMap.getString("lblTSubTotal.text")); // NOI18N
        lblTSubTotal.setName("lblTSubTotal"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(lblTSubTotal, gridBagConstraints);

        lblOSubTotal.setFont(resourceMap.getFont("lblOSubTotal.font")); // NOI18N
        lblOSubTotal.setForeground(resourceMap.getColor("lblOSubTotal.foreground")); // NOI18N
        lblOSubTotal.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblOSubTotal.setText(resourceMap.getString("lblOSubTotal.text")); // NOI18N
        lblOSubTotal.setName("lblOSubTotal"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(lblOSubTotal, gridBagConstraints);

        lblCNoteAmount.setFont(resourceMap.getFont("lblOSubTotal.font")); // NOI18N
        lblCNoteAmount.setForeground(resourceMap.getColor("lblOSubTotal.foreground")); // NOI18N
        lblCNoteAmount.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblCNoteAmount.setText(resourceMap.getString("lblCNoteAmount.text")); // NOI18N
        lblCNoteAmount.setName("lblCNoteAmount"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(lblCNoteAmount, gridBagConstraints);

        lblReceived.setFont(resourceMap.getFont("lblOSubTotal.font")); // NOI18N
        lblReceived.setForeground(resourceMap.getColor("lblReceived.foreground")); // NOI18N
        lblReceived.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblReceived.setText(resourceMap.getString("lblReceived.text")); // NOI18N
        lblReceived.setName("lblReceived"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(lblReceived, gridBagConstraints);

        lblOutstanding.setFont(resourceMap.getFont("lblOSubTotal.font")); // NOI18N
        lblOutstanding.setForeground(resourceMap.getColor("lblFinalBalance.foreground")); // NOI18N
        lblOutstanding.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblOutstanding.setText(resourceMap.getString("lblOutstanding.text")); // NOI18N
        lblOutstanding.setName("lblOutstanding"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(lblOutstanding, gridBagConstraints);

        lblInvAmount.setFont(resourceMap.getFont("lblInvAmount.font")); // NOI18N
        lblInvAmount.setForeground(resourceMap.getColor("lblInvAmount.foreground")); // NOI18N
        lblInvAmount.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblInvAmount.setText(resourceMap.getString("lblInvAmount.text")); // NOI18N
        lblInvAmount.setName("lblInvAmount"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(lblInvAmount, gridBagConstraints);

        jLabel8.setFont(resourceMap.getFont("jLabel8.font")); // NOI18N
        jLabel8.setForeground(resourceMap.getColor("jLabel8.foreground")); // NOI18N
        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(jLabel8, gridBagConstraints);

        lblPaid.setFont(resourceMap.getFont("lblPaid.font")); // NOI18N
        lblPaid.setForeground(resourceMap.getColor("lblPaid.foreground")); // NOI18N
        lblPaid.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblPaid.setText(resourceMap.getString("lblPaid.text")); // NOI18N
        lblPaid.setName("lblPaid"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(lblPaid, gridBagConstraints);

        jLabel10.setFont(resourceMap.getFont("jLabel8.font")); // NOI18N
        jLabel10.setForeground(resourceMap.getColor("jLabel10.foreground")); // NOI18N
        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(jLabel10, gridBagConstraints);

        lblFinalBalance.setFont(resourceMap.getFont("jLabel8.font")); // NOI18N
        lblFinalBalance.setForeground(resourceMap.getColor("lblFinalBalance.foreground")); // NOI18N
        lblFinalBalance.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblFinalBalance.setText(resourceMap.getString("lblFinalBalance.text")); // NOI18N
        lblFinalBalance.setName("lblFinalBalance"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(lblFinalBalance, gridBagConstraints);

        javax.swing.GroupLayout pnlTransactionLayout = new javax.swing.GroupLayout(pnlTransaction);
        pnlTransaction.setLayout(pnlTransactionLayout);
        pnlTransactionLayout.setHorizontalGroup(
            pnlTransactionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTransactionLayout.createSequentialGroup()
                .addComponent(tabsTransaction, javax.swing.GroupLayout.PREFERRED_SIZE, 764, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE))
        );
        pnlTransactionLayout.setVerticalGroup(
            pnlTransactionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabsTransaction, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlMain.add(pnlTransaction, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(pnlMain, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        
    }//GEN-LAST:event_btnPrintActionPerformed

    private void btnPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviewActionPerformed
        
    }//GEN-LAST:event_btnPreviewActionPerformed

    private void btnEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmailActionPerformed
        
    }//GEN-LAST:event_btnEmailActionPerformed

    private void btnRemoveLine1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveLine1ActionPerformed
        int row = tblAdditionalInvLine.getSelectedRow();
        if (row != -1) {
            if (this.additionalLines.get(row).getPurchaseAccountingDocument() == null) {
                this.additionalLines.remove(row);
                populatetblAdditionalInvLine();
                recalculateDocument();
            } else {
                JOptionPane.showMessageDialog(null, "Invoiced line can not be removed !!!", "Remove Line", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Select Item to Remove", "New Invoice", JOptionPane.WARNING_MESSAGE);
        }
}//GEN-LAST:event_btnRemoveLine1ActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        
    }//GEN-LAST:event_btnSaveActionPerformed

    private void txtVendorRefKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtVendorRefKeyReleased
        this.acDoc.setVendorRef(txtVendorRef.getText());
        setSaveNeeded(true);
}//GEN-LAST:event_txtVendorRefKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEmail;
    private javax.swing.JButton btnPreview;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnRemoveLine1;
    private javax.swing.JButton btnSave;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox cmbTerms;
    private org.jdesktop.swingx.JXDatePicker datePicker;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JLabel lblAdditionalServices;
    private javax.swing.JLabel lblCNoteAmount;
    private javax.swing.JLabel lblDocFor;
    private javax.swing.JLabel lblFinalBalance;
    private javax.swing.JLabel lblInvAmount;
    private javax.swing.JLabel lblOSubTotal;
    private javax.swing.JLabel lblOutstanding;
    private javax.swing.JLabel lblPaid;
    private javax.swing.JLabel lblPaid1;
    private javax.swing.JLabel lblReceived;
    private javax.swing.JLabel lblTSubTotal;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel pnInvlHeader;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlInvLine;
    private javax.swing.JPanel pnlInvoiceFor;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlStatus;
    private javax.swing.JPanel pnlTransaction;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JTabbedPane tabsTransaction;
    private org.jdesktop.swingx.JXTable tblAdditionalInvLine;
    private org.jdesktop.swingx.JXTable tblTicket;
    private org.jdesktop.swingx.JXTable tblTransaction;
    private javax.swing.JTextArea txtContactableDetails;
    private javax.swing.JTextPane txtFGuide;
    private javax.swing.JTextField txtRecipientRef;
    private javax.swing.JTextField txtVendorRef;
    // End of variables declaration//GEN-END:variables
    private long startTime = 0;
    private long stopTime = 0;
    private float elapsedTime = 0;    

    private class threadLoadInvoice implements Runnable {

        private long acDocId;

        public threadLoadInvoice(long acDocId) {
            this.acDocId = acDocId;
        }

        public void run() {

            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            //progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();

            statusMessageLabel.setText("Loading invoice...");
            acDocBo.findPCNoteByIdWithInvoice(acDocId);
            acDoc = acDocBo.getPurchaseAccountingDocument();

            statusMessageLabel.setText("Populating new document...");
            loadObjects(acDoc);
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            progressBar.setIndeterminate(false);
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            //progressBar.repaint(); //Refresh graphics
        }
    }
   
    private class threadLoadAdditionalServices implements Runnable {

        public threadLoadAdditionalServices() {
        }

        public void run() {

            //progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            //progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();
            statusMessageLabel.setText("Loading Services...");

            additionalServices = oServiceBo.loadAdditionalServices();

            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            //progressBar.repaint(); //Refresh graphics
        }
    }
    
    
}
