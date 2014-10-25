
/*
 * FrameOutstandingInvoice.java
 *
 * Created on 18-Oct-2010, 23:15:52
 */
package etsbackoffice.gui;

import etsbackoffice.ETSBackofficeApp;
import etsbackoffice.businesslogic.*;
import etsbackoffice.domain.*;
import etsbackoffice.report.BatchTransactionReport;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JProgressBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.application.Action;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 *
 * @author Yusuf
 */
public class FrameBPThirdPartyRpt extends javax.swing.JFrame {

    
    private DateFormat df = new DateFormat();
    AccountingDocBo acDocBo = (AccountingDocBo) ETSBackofficeApp.getApplication().ctx.getBean("acDocBo");
    AgentBo agentBo = (AgentBo) ETSBackofficeApp.getApplication().ctx.getBean("agentBo");
    AcTransactionBo acTransactionBo = (AcTransactionBo) ETSBackofficeApp.getApplication().ctx.getBean("acTransactionBo");

    private Agent vendor;
    private AccountingDocument acDoc;
    private PurchaseAccountingDocument pAcDoc;
    private BatchBillingTransaction batch;
    private List<BatchBillingTransaction> batchs = new ArrayList();
    private List<BillingTransaction> transactions = new ArrayList();
    private List<Agent> vendors = new ArrayList();
    
    private List<PurchaseAccountingDocument> pacDocs = new ArrayList();
    

    private DefaultTableModel invoiceModel;
    private DefaultTableModel invoiceToPayModel;

    DefaultComboBoxModel cmbAgtModel = new DefaultComboBoxModel();
    private boolean submitNeeded;  

    /** Creates new form FrameOutstandingInvoice */
    public FrameBPThirdPartyRpt(java.awt.Frame parent) {
        initComponents();
        busyIcon.setVisible(false);        
        initialThread();        
    }    

    public void showFrameBPThirdPartyRpt() {
        //this.setPreferredSize(new Dimension(100, 100));
        setLocationRelativeTo(this);
        setVisible(true);
        toFront();
    }

    private void initialThread() {
        new Thread(new threadLoadVendors()).start();
    }
    
    public void searchBactch() {
        Long contactableId = null;       
        Date from = null;
        Date to = null;
        String[] data;

        if (cmbContactable.getSelectedIndex() > 0) {
            data = cmbContactable.getSelectedItem().toString().split("-");
            contactableId = Long.parseLong(data[2]);
        }else{
        contactableId = null;
        }

        from = dtFrom.getDate();
        to = dtTo.getDate();     
        
        this.batchs = acTransactionBo.findBatchBPThirdParty(contactableId, from, to);
        populateTblBatch();
    }

    private void populateBatchSummery(BatchBillingTransaction batch) {
        populateTxtAgentDetails(batch.getVendor());
        lblRef.setText(batch.getBatchBillingRef());
        lblDate.setText(df.dateForGui(batch.getBatchBillingDate()));
        lblTotalTrans.setText(String.valueOf(batch.getNoOfTransactions()));
        lblUser.setText(batch.getBillingBy().getSurName() + " " + batch.getBillingBy().getForeName());
        lblAmount.setText(batch.getBatchAmount().toString());
    }

    private void populateCmbContactable() {
        List cmbElement = new ArrayList();
       
        for (Agent a : this.vendors) {
                cmbElement.add(a.getName() + "-" + a.getPostCode()
                        + "-" + a.getContactableId());
            }

            Collections.sort(cmbElement);
            DefaultComboBoxModel cmbContactableModel = new DefaultComboBoxModel(cmbElement.toArray());
            cmbContactableModel.insertElementAt("All", 0);
            cmbContactable.setModel(cmbContactableModel);
            cmbContactable.setSelectedIndex(0);
    }

    private void populateTxtAgentDetails(Agent agent) {
         txtContactableDetails.setText(agent.getFullAddressCRSeperated());
    }    

    private void populateTblBatch() {
        DefaultTableModel batchModel = (DefaultTableModel) tblBatch.getModel();
        batchModel.getDataVector().removeAllElements();
        tblBatch.clearSelection();
        tblBatch.repaint();

        int row = 0;
        for (BatchBillingTransaction batch : this.batchs) {
            batchModel.insertRow(row, new Object[]{batch.getBatchBillingDate(), batch.getBatchBillingRef()});
            row++;
        }
    }
    
    private void populateTblTransaction(List<BillingTransaction> transactions) {
        DefaultTableModel transModel = (DefaultTableModel) tblTransaction.getModel();
        transModel.getDataVector().removeAllElements();
        tblTransaction.clearSelection();
        tblTransaction.repaint();

        for (BillingTransaction bt : transactions) {
            int row = 0;
            transModel.insertRow(row, new Object[]{bt.getPurchaseAccountingDocument().getVendorRef(),
            bt.getPurchaseAccountingDocument().getRecipientRef(),
            df.dateForGui(bt.getPurchaseAccountingDocument().getIssueDate()),
            bt.getPurchaseAccountingDocument().getPnr().getGdsPNR(),
            bt.getPurchaseAccountingDocument().getPnr().getServicingCareer().getCode(),
            bt.getPurchaseAccountingDocument().getPnr().getLeadPaxFromTickets(),
            bt.getTransAmount(),Enums.TransType.valueOf(bt.getTransType())});
            row++;
        }
    }

        private ListSelectionListener tblBatchListener = new ListSelectionListener() {

        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int selectedRow = tblBatch.getSelectedRow();
            if (selectedRow != -1) {
                batch = batchs.get(selectedRow);
                populateBatchSummery(batch);
            }
        }
    };
        
    private ActionListener cmbContactableListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            String[] data;
            long id = 0;
            if (cmbContactable.getSelectedIndex() > 0) {
                data = cmbContactable.getSelectedItem().toString().split("-");
                id = Long.parseLong(data[2]);
            }

            loop:
            for (Agent a : vendors) {
                if (a.getContactableId() == id) {
                    vendor = a;
                    populateTxtAgentDetails(a);
                    break loop;
                }
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
        buttonGroup2 = new javax.swing.ButtonGroup();
        jToolBar1 = new javax.swing.JToolBar();
        jButton1 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        btnPrintReport = new javax.swing.JButton();
        btnEmailReport = new javax.swing.JButton();
        btnViewReport = new javax.swing.JButton();
        splitPanelMaster = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblBatch = new org.jdesktop.swingx.JXTable();
        jPanel8 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        cmbContactable = new javax.swing.JComboBox();
        cmbContactable.addActionListener(cmbContactableListener);
        AutoCompleteDecorator.decorate(cmbContactable);
        dtFrom = new org.jdesktop.swingx.JXDatePicker();
        dtTo = new org.jdesktop.swingx.JXDatePicker();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        btnSearch = new javax.swing.JButton();
        busyIcon = new org.jdesktop.swingx.JXBusyLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel2 = new javax.swing.JPanel();
        JScrollPane = new javax.swing.JScrollPane();
        tblTransaction = new org.jdesktop.swingx.JXTable();
        summeryPanel = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtContactableDetails = new javax.swing.JTextArea();
        jPanel3 = new javax.swing.JPanel();
        lblTotalTrans = new javax.swing.JLabel();
        lblDate = new javax.swing.JLabel();
        lblPaymentDate = new javax.swing.JLabel();
        lblRefference = new javax.swing.JLabel();
        lblRef = new javax.swing.JLabel();
        lblPaidBy = new javax.swing.JLabel();
        lblTAmount = new javax.swing.JLabel();
        lblUser = new javax.swing.JLabel();
        lblNoOfTransction = new javax.swing.JLabel();
        lblAmount = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        statusMessageLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getResourceMap(FrameBPThirdPartyRpt.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setMinimumSize(new java.awt.Dimension(620, 175));
        setName("Form"); // NOI18N

        jToolBar1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        jButton1.setFont(resourceMap.getFont("btnPrintReport.font")); // NOI18N
        jButton1.setIcon(resourceMap.getIcon("jButton1.icon")); // NOI18N
        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setFocusable(false);
        jButton1.setName("jButton1"); // NOI18N
        jToolBar1.add(jButton1);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getActionMap(FrameBPThirdPartyRpt.class, this);
        jButton5.setAction(actionMap.get("viewInvoice")); // NOI18N
        jButton5.setFont(resourceMap.getFont("btnPrintReport.font")); // NOI18N
        jButton5.setIcon(resourceMap.getIcon("jButton5.icon")); // NOI18N
        jButton5.setText(resourceMap.getString("jButton5.text")); // NOI18N
        jButton5.setFocusable(false);
        jButton5.setName("jButton5"); // NOI18N
        jToolBar1.add(jButton5);

        btnPrintReport.setFont(resourceMap.getFont("btnPrintReport.font")); // NOI18N
        btnPrintReport.setIcon(resourceMap.getIcon("btnPrintReport.icon")); // NOI18N
        btnPrintReport.setText(resourceMap.getString("btnPrintReport.text")); // NOI18N
        btnPrintReport.setFocusable(false);
        btnPrintReport.setName("btnPrintReport"); // NOI18N
        btnPrintReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintReportActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPrintReport);

        btnEmailReport.setFont(resourceMap.getFont("btnPrintReport.font")); // NOI18N
        btnEmailReport.setIcon(resourceMap.getIcon("btnEmailReport.icon")); // NOI18N
        btnEmailReport.setText(resourceMap.getString("btnEmailReport.text")); // NOI18N
        btnEmailReport.setFocusable(false);
        btnEmailReport.setName("btnEmailReport"); // NOI18N
        btnEmailReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEmailReportActionPerformed(evt);
            }
        });
        jToolBar1.add(btnEmailReport);

        btnViewReport.setFont(resourceMap.getFont("btnPrintReport.font")); // NOI18N
        btnViewReport.setIcon(resourceMap.getIcon("btnViewReport.icon")); // NOI18N
        btnViewReport.setText(resourceMap.getString("btnViewReport.text")); // NOI18N
        btnViewReport.setFocusable(false);
        btnViewReport.setName("btnViewReport"); // NOI18N
        btnViewReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewReportActionPerformed(evt);
            }
        });
        jToolBar1.add(btnViewReport);

        splitPanelMaster.setBorder(null);
        splitPanelMaster.setDividerLocation(250);
        splitPanelMaster.setDividerSize(4);
        splitPanelMaster.setName("splitPanelMaster"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jPanel5.setName("jPanel5"); // NOI18N
        jPanel5.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel5.add(jLabel1, gridBagConstraints);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tblBatch.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Date", "Refference"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblBatch.setFont(resourceMap.getFont("tblBatch.font")); // NOI18N
        tblBatch.setName("tblBatch"); // NOI18N
        tblBatch.setShowVerticalLines(false);
        tblBatch.getTableHeader().setReorderingAllowed(false);
        tblBatch.getSelectionModel().addListSelectionListener(tblBatchListener);
        tblBatch.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblBatchMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblBatch);
        tblBatch.getColumnModel().getColumn(0).setMinWidth(80);
        tblBatch.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblBatch.getColumnModel().getColumn(0).setMaxWidth(80);
        tblBatch.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblBatch.columnModel.title0")); // NOI18N
        tblBatch.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblBatch.columnModel.title1")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel5.add(jScrollPane1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.8;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 0, 2);
        jPanel1.add(jPanel5, gridBagConstraints);

        jPanel8.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jPanel8.setName("jPanel8"); // NOI18N
        jPanel8.setLayout(new java.awt.GridBagLayout());

        jLabel5.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
        jPanel8.add(jLabel5, gridBagConstraints);

        cmbContactable.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        cmbContactable.setName("cmbContactable"); // NOI18N
        cmbContactable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbContactableActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
        jPanel8.add(cmbContactable, gridBagConstraints);

        dtFrom.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        dtFrom.setName("dtFrom"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
        jPanel8.add(dtFrom, gridBagConstraints);

        dtTo.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        dtTo.setName("dtTo"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
        jPanel8.add(dtTo, gridBagConstraints);

        jLabel6.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
        jPanel8.add(jLabel6, gridBagConstraints);

        jLabel7.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
        jPanel8.add(jLabel7, gridBagConstraints);

        btnSearch.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        btnSearch.setIcon(resourceMap.getIcon("btnSearch.icon")); // NOI18N
        btnSearch.setText(resourceMap.getString("btnSearch.text")); // NOI18N
        btnSearch.setName("btnSearch"); // NOI18N
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(8, 2, 2, 2);
        jPanel8.add(btnSearch, gridBagConstraints);

        busyIcon.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        busyIcon.setText(resourceMap.getString("busyIcon.text")); // NOI18N
        busyIcon.setFont(resourceMap.getFont("busyIcon.font")); // NOI18N
        busyIcon.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        busyIcon.setName("busyIcon"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
        jPanel8.add(busyIcon, gridBagConstraints);

        jLabel2.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
        jPanel8.add(jLabel2, gridBagConstraints);

        jTextField1.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        jTextField1.setText(resourceMap.getString("jTextField1.text")); // NOI18N
        jTextField1.setName("jTextField1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
        jPanel8.add(jTextField1, gridBagConstraints);

        jSeparator1.setName("jSeparator1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        jPanel8.add(jSeparator1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanel1.add(jPanel8, gridBagConstraints);

        splitPanelMaster.setLeftComponent(jPanel1);

        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new java.awt.GridBagLayout());

        JScrollPane.setName("JScrollPane"); // NOI18N

        tblTransaction.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "V.InvRef", "RecepientRef", "InvDate", "GDSPNR", "Career", "LeadPax", "TransAmount", "T.Type"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblTransaction.setEditable(false);
        tblTransaction.setFont(resourceMap.getFont("tblTransaction.font")); // NOI18N
        tblTransaction.setName("tblTransaction"); // NOI18N
        tblTransaction.setShowHorizontalLines(false);
        tblTransaction.setShowVerticalLines(false);
        tblTransaction.setSortable(false);
        tblTransaction.getTableHeader().setReorderingAllowed(false);
        JScrollPane.setViewportView(tblTransaction);
        tblTransaction.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblTransaction.columnModel.title0")); // NOI18N
        tblTransaction.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblTransaction.columnModel.title7")); // NOI18N
        tblTransaction.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblTransaction.columnModel.title1")); // NOI18N
        tblTransaction.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title10")); // NOI18N
        tblTransaction.getColumnModel().getColumn(4).setMaxWidth(80);
        tblTransaction.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title11")); // NOI18N
        tblTransaction.getColumnModel().getColumn(5).setMinWidth(140);
        tblTransaction.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title3")); // NOI18N
        tblTransaction.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title7")); // NOI18N
        tblTransaction.getColumnModel().getColumn(7).setHeaderValue(resourceMap.getString("tblTransaction.columnModel.title6")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(JScrollPane, gridBagConstraints);

        summeryPanel.setBackground(resourceMap.getColor("summeryPanel.background")); // NOI18N
        summeryPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        summeryPanel.setForeground(resourceMap.getColor("summeryPanel.foreground")); // NOI18N
        summeryPanel.setName("summeryPanel"); // NOI18N
        summeryPanel.setLayout(new java.awt.GridBagLayout());

        jLabel10.setFont(resourceMap.getFont("jLabel10.font")); // NOI18N
        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 10);
        summeryPanel.add(jLabel10, gridBagConstraints);

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        txtContactableDetails.setColumns(20);
        txtContactableDetails.setFont(resourceMap.getFont("txtContactableDetails.font")); // NOI18N
        txtContactableDetails.setRows(5);
        txtContactableDetails.setName("txtContactableDetails"); // NOI18N
        jScrollPane2.setViewportView(txtContactableDetails);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 10);
        summeryPanel.add(jScrollPane2, gridBagConstraints);

        jPanel3.setBackground(resourceMap.getColor("jPanel3.background")); // NOI18N
        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setLayout(new java.awt.GridBagLayout());

        lblTotalTrans.setFont(resourceMap.getFont("lblTotalTrans.font")); // NOI18N
        lblTotalTrans.setText(resourceMap.getString("lblTotalTrans.text")); // NOI18N
        lblTotalTrans.setName("lblTotalTrans"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        jPanel3.add(lblTotalTrans, gridBagConstraints);

        lblDate.setFont(resourceMap.getFont("lblDate.font")); // NOI18N
        lblDate.setForeground(resourceMap.getColor("lblDate.foreground")); // NOI18N
        lblDate.setText(resourceMap.getString("lblDate.text")); // NOI18N
        lblDate.setName("lblDate"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        jPanel3.add(lblDate, gridBagConstraints);

        lblPaymentDate.setFont(resourceMap.getFont("lblBalance.font")); // NOI18N
        lblPaymentDate.setText(resourceMap.getString("lblPaymentDate.text")); // NOI18N
        lblPaymentDate.setName("lblPaymentDate"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 6, 2);
        jPanel3.add(lblPaymentDate, gridBagConstraints);

        lblRefference.setFont(resourceMap.getFont("lblBalance.font")); // NOI18N
        lblRefference.setText(resourceMap.getString("lblRefference.text")); // NOI18N
        lblRefference.setName("lblRefference"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 6, 2);
        jPanel3.add(lblRefference, gridBagConstraints);

        lblRef.setFont(resourceMap.getFont("lblRef.font")); // NOI18N
        lblRef.setText(resourceMap.getString("lblRef.text")); // NOI18N
        lblRef.setName("lblRef"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        jPanel3.add(lblRef, gridBagConstraints);

        lblPaidBy.setFont(resourceMap.getFont("lblPaidBy.font")); // NOI18N
        lblPaidBy.setText(resourceMap.getString("lblPaidBy.text")); // NOI18N
        lblPaidBy.setName("lblPaidBy"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 6, 2);
        jPanel3.add(lblPaidBy, gridBagConstraints);

        lblTAmount.setFont(resourceMap.getFont("lblTAmount.font")); // NOI18N
        lblTAmount.setText(resourceMap.getString("lblTAmount.text")); // NOI18N
        lblTAmount.setName("lblTAmount"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 6, 2);
        jPanel3.add(lblTAmount, gridBagConstraints);

        lblUser.setFont(resourceMap.getFont("lblUser.font")); // NOI18N
        lblUser.setText(resourceMap.getString("lblUser.text")); // NOI18N
        lblUser.setName("lblUser"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        jPanel3.add(lblUser, gridBagConstraints);

        lblNoOfTransction.setFont(resourceMap.getFont("lblNoOfTransction.font")); // NOI18N
        lblNoOfTransction.setText(resourceMap.getString("lblNoOfTransction.text")); // NOI18N
        lblNoOfTransction.setName("lblNoOfTransction"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 6, 2);
        jPanel3.add(lblNoOfTransction, gridBagConstraints);

        lblAmount.setFont(resourceMap.getFont("lblAmount.font")); // NOI18N
        lblAmount.setText(resourceMap.getString("lblAmount.text")); // NOI18N
        lblAmount.setName("lblAmount"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        jPanel3.add(lblAmount, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        summeryPanel.add(jPanel3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 4, 2);
        jPanel2.add(summeryPanel, gridBagConstraints);

        splitPanelMaster.setRightComponent(jPanel2);

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel4.setName("jPanel4"); // NOI18N
        jPanel4.setLayout(new java.awt.GridBagLayout());

        progressBar.setName("progressBar"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel4.add(progressBar, gridBagConstraints);

        statusMessageLabel.setFont(resourceMap.getFont("statusMessageLabel.font")); // NOI18N
        statusMessageLabel.setText(resourceMap.getString("statusMessageLabel.text")); // NOI18N
        statusMessageLabel.setName("statusMessageLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 0);
        jPanel4.add(statusMessageLabel, gridBagConstraints);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 1029, Short.MAX_VALUE)
            .addComponent(splitPanelMaster, javax.swing.GroupLayout.DEFAULT_SIZE, 1029, Short.MAX_VALUE)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 1029, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(splitPanelMaster, javax.swing.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmbContactableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbContactableActionPerformed
}//GEN-LAST:event_cmbContactableActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        new Thread(new threadBatchFinder()).start();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void tblBatchMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBatchMouseClicked
        int row = tblBatch.getSelectedRow();
        if (evt.getClickCount() == 2) {
            this.transactions = new ArrayList(this.batchs.get(row).getBillingTransactions());
            populateTblTransaction(this.transactions);
        }
    }//GEN-LAST:event_tblBatchMouseClicked

    private void btnPrintReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintReportActionPerformed
        BatchTransactionReport bRpt = new BatchTransactionReport(this.batch, this.vendor);
        bRpt.printBillingReport();
    }//GEN-LAST:event_btnPrintReportActionPerformed

    private void btnViewReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewReportActionPerformed
        BatchTransactionReport bRpt = new BatchTransactionReport(this.batch, this.vendor);
        bRpt.viewBillingReport();
    }//GEN-LAST:event_btnViewReportActionPerformed

    private void btnEmailReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmailReportActionPerformed
        BatchTransactionReport bRpt = new BatchTransactionReport(this.batch, this.vendor);
        bRpt.emailBillingReport();
    }//GEN-LAST:event_btnEmailReportActionPerformed

    /**
     * @param args the command line arguments
     */
 /*   public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                //new FrameCollectionReport().setVisible(true);
            }
        });
    }*/
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane JScrollPane;
    private javax.swing.JButton btnEmailReport;
    private javax.swing.JButton btnPrintReport;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnViewReport;
    private org.jdesktop.swingx.JXBusyLabel busyIcon;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JComboBox cmbContactable;
    private org.jdesktop.swingx.JXDatePicker dtFrom;
    private org.jdesktop.swingx.JXDatePicker dtTo;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblAmount;
    private javax.swing.JLabel lblDate;
    private javax.swing.JLabel lblNoOfTransction;
    private javax.swing.JLabel lblPaidBy;
    private javax.swing.JLabel lblPaymentDate;
    private javax.swing.JLabel lblRef;
    private javax.swing.JLabel lblRefference;
    private javax.swing.JLabel lblTAmount;
    private javax.swing.JLabel lblTotalTrans;
    private javax.swing.JLabel lblUser;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JSplitPane splitPanelMaster;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel summeryPanel;
    private org.jdesktop.swingx.JXTable tblBatch;
    private org.jdesktop.swingx.JXTable tblTransaction;
    private javax.swing.JTextArea txtContactableDetails;
    // End of variables declaration//GEN-END:variables

        @Action
    public void viewInvoice() {
        int row = tblBatch.getSelectedRow();
        if (row != -1) {
            try {
                long pAcDocId = this.transactions.get(row).getPurchaseAccountingDocument().getPurchaseAcDocId();
                Thread t = new Thread(new threadLoadCompletePAcDoc(pAcDocId));
                t.start();
                t.join();
                {
                    
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(FrameBPThirdParty.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }  

    //************************threads***************************
    private long startTime = 0;
    private long stopTime = 0;
    private float elapsedTime = 0;

    /**
     * @param pnrDao the pnrDao to set
     */
    private class threadLoadCompletePAcDoc implements Runnable {

        private long pAcDocId;

        public threadLoadCompletePAcDoc(long pAcDocId) {
            this.pAcDocId = pAcDocId;
        }

        public void run() {

            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();

            statusMessageLabel.setText("Loading Document...");
            acDocBo.findPurchaseAcDocById(pAcDocId);
            pAcDoc = acDocBo.getPurchaseAccountingDocument();

            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Document Loaded in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            progressBar.repaint(); //Refresh graphics
        }
    }

        private class threadBatchFinder implements Runnable {

        public threadBatchFinder() {
        }

        public void run() {
            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();

            statusMessageLabel.setText("Loading Invoice...");
            btnSearch.setEnabled(false);
            searchBactch();
            btnSearch.setEnabled(true);
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            progressBar.repaint(); //Refresh graphics
        }
    }

    private class threadLoadVendors implements Runnable {

        public threadLoadVendors() {
        }

        public void run() {
            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();

            statusMessageLabel.setText("Loading Vendors...");
            btnSearch.setEnabled(false);

            vendors = agentBo.vendorList();

            populateCmbContactable();
            btnSearch.setEnabled(true);
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            progressBar.repaint(); //Refresh graphics
        }
    }
}
