package etsbackoffice.gui;

/**
 *
 * @author Yusuf
 */
import etsbackoffice.ETSBackofficeApp;
import etsbackoffice.businesslogic.*;
import etsbackoffice.domain.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.application.Action;

public class FrameBPBsp extends javax.swing.JFrame{

    private DateFormat df = new DateFormat();
    private AccountsBo accountsBo = (AccountsBo) ETSBackofficeApp.getApplication().ctx.getBean("accountsBo");
    private AccountingDocBo acDocBo = (AccountingDocBo) ETSBackofficeApp.getApplication().ctx.getBean("acDocBo");
    private AgentBo agentBo = (AgentBo) ETSBackofficeApp.getApplication().ctx.getBean("agentBo");
    private AcTransactionBo acTransactionBo = (AcTransactionBo) ETSBackofficeApp.getApplication().ctx.getBean("acTransactionBo");        
    private List<PurchaseAccountingDocument> outstandingInvoices = new ArrayList();
    private List<PurchaseAccountingDocument> invoicesToPay = new ArrayList();
    private DefaultTableModel invoiceModel;
    private boolean submitNeeded;

    /** Creates new form FramePaymentCollection1 */
    public FrameBPBsp(java.awt.Frame parent) {
        initComponents();
        busyIcon.setVisible(false);
        busyIcon1.setVisible(false);        
        btnSubmit.setEnabled(false);
        populateCmbTType();
    }

        private void populateCmbTType() {
        DefaultComboBoxModel tTypeModel = new DefaultComboBoxModel(Enums.TransType.values());
        cmbTType.setModel(tTypeModel);
        cmbTType.insertItemAt("Select", 0);
        cmbTType.setSelectedIndex(0);
    }
        
    @Action
    public void closeWindow() {
        dispose();
    }

    public void showFrameBPBsp() {
        setLocationRelativeTo(this);
        setVisible(true);
        toFront();
    }

    public void searchOutstandingPInvoice() {
        Date from = null;
        Date to = null;

        from = dtFrom.getDate();
        to = dtTo.getDate();
        this.outstandingInvoices = acDocBo.outstandingPInvForBspBilling(from, to);
        populateTblInvoice();
    }

    private void populateTblInvoice() {

        int row = 0;
        invoiceModel = (DefaultTableModel) tblInvoice.getModel();
        invoiceModel.getDataVector().removeAllElements();
        tblInvoice.clearSelection();
        tblInvoice.repaint();

        Set<Ticket> tickets = new LinkedHashSet<Ticket>();
        BigDecimal totalCredit = new BigDecimal("0.00");
        BigDecimal totalInvoiceAmount = new BigDecimal("0.00");

        for (PurchaseAccountingDocument a : outstandingInvoices) {
            tickets.addAll(a.getTickets());
            totalInvoiceAmount = totalInvoiceAmount.add(a.getTotalDocumentedAmount());
        }

        BigDecimal totalBF = new BigDecimal("0.00");
        BigDecimal totalRF = new BigDecimal("0.00");
        BigDecimal netTIssue = new BigDecimal("0.00");
        BigDecimal netTRefund = new BigDecimal("0.00");
        BigDecimal totalBspCom = new BigDecimal("0.00");
        BigDecimal totalBspComDeduct = new BigDecimal("0.00");
        BigDecimal comBalance = new BigDecimal("0.00");

        long totalNoOfIssue = 0;
        long totalNoOfRefund = 0;

        for (Ticket t : tickets) {
            if (t.getTktStatus() == 2 || t.getTktStatus() == 3) {
                totalBF = totalBF.add(t.getTotalFare());
                netTIssue = netTIssue.add(t.getNetFare());
                totalBspCom = totalBspCom.add(t.getBspCom());
                ++totalNoOfIssue;
            } else if (t.getTktStatus() == 4) {
                netTRefund = netTRefund.add(netTRefund);
                netTRefund = netTRefund.add(t.getTotalFare());
                totalBspComDeduct = totalBspComDeduct.add(t.getBspCom());
                ++totalNoOfRefund;
            }

            invoiceModel.insertRow(row, new Object[]{row + 1, t.getPnr().getGdsPNR(), t.getPnr().getServicingCareer().getCode(), t.getFullTicketNo(),
                        df.dateForGui(t.getDocIssuedate()), t.getTktStatusString(), t.getBaseFare(), t.getTax(), t.getTotalFare(),
                        t.getBspCom(), t.getNetFare()});
            row++;
        }
        
        comBalance = totalBspCom.add(totalBspComDeduct);
        lblTotalBF.setText(totalBF.toString());
        lblTotalRF.setText(netTRefund.toString());
        
        lblTotalIssue.setText(" : " + netTIssue.toString() + "  @  " + totalNoOfIssue);
        lblTotalRefund.setText(" : " + netTRefund.toString() + "  @  " + totalNoOfRefund);
        lblBspCom.setText(" : " + totalBspCom.toString());
        lblComDeduct.setText(totalBspComDeduct.toString());
        lblBalanceToBePaid.setText(" : " + netTIssue.add(netTRefund));
        txtTAmount.setText(totalInvoiceAmount.add(totalCredit).toString());
    }

    @Action
    public void submitTransaction() {
        busyIcon1.setVisible(true);
        busyIcon1.setBusy(true);

        if (this.submitNeeded = true) {           
            String tRef = txtTRef.getText();
            List<BillingTransaction> newTransactions = new ArrayList();

            BatchBillingTransaction bspBilling = new BatchBillingTransaction();
            bspBilling.setBatchBillingDate(new java.util.Date());
            bspBilling.setBillingBy(AuthenticationBo.getLoggedOnUser());           
            bspBilling.setBillingType(1);
            bspBilling.setBatchBillingRef(acTransactionBo.generateBatchBillingRefBsp());

            for (PurchaseAccountingDocument pAcDoc : this.outstandingInvoices) {
                
                if (pAcDoc.getOutstandingAmount().compareTo(new BigDecimal("0.00")) != 0) {
                    BigDecimal newPayment = pAcDoc.getOutstandingAmount();                    
                    BillingTransaction bt = new BillingTransaction();
                    bt.setPurchaseAccountingDocument(pAcDoc);
                    //bt.setInvoice(pAcDoc);
                    bt.setPnr(pAcDoc.getPnr());
                    bt.setTransDate(new java.util.Date());
                    bt.setTransAmount(newPayment);
                    bt.setTransType(Enums.TransType.valueOf(cmbTType.getSelectedItem().toString()).getId());
                    bt.setTransRef("BSP Payment: " + tRef);
                    bt.setUser(AuthenticationBo.getLoggedOnUser());
                    bt.setBatchBillingTransaction(bspBilling);
                    bt.addAcStatement(accountsBo.newAccountsTransactionFromPAcTransaction(bt)); 
                    newTransactions.add(bt);
                }
            }
            if (newTransactions.size() > 0) {
                bspBilling.setBillingTransactions(new LinkedHashSet(newTransactions));
                acTransactionBo.saveBBillingTransaction(bspBilling);
                setSubmitNeeded(false);
                this.invoicesToPay.clear();

                searchOutstandingPInvoice();
            } else {
                JOptionPane.showMessageDialog(null, "No Transaction to submit", "Transaction", JOptionPane.WARNING_MESSAGE);
                setSubmitNeeded(false);
            }
        }
        busyIcon1.setBusy(false);
        busyIcon1.setVisible(false);
    }

    private void setSubmitNeeded(boolean submitNeeded) {
        if (submitNeeded != this.submitNeeded) {
            this.submitNeeded = submitNeeded;
        }
        if (this.submitNeeded == true) {
            btnSubmit.setEnabled(true);
            txtTRef.setEditable(true);
        } else {
            btnSubmit.setEnabled(false);
            txtTRef.setText(null);
            txtTRef.setEditable(false);
            txtTAmount.setText(null);
            cmbTType.setSelectedIndex(0);
        }
    }

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
        jButton1 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        splitPanelMaster = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        dtFrom = new org.jdesktop.swingx.JXDatePicker();
        dtTo = new org.jdesktop.swingx.JXDatePicker();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        btnSearch = new javax.swing.JButton();
        busyIcon = new org.jdesktop.swingx.JXBusyLabel();
        jPanel2 = new javax.swing.JPanel();
        JScrollPane = new javax.swing.JScrollPane();
        tblInvoice = new org.jdesktop.swingx.JXTable();
        jPanel10 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        lblTotalIssue = new javax.swing.JLabel();
        lblTotalRefund = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        lblBalanceToBePaid = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblBspCom = new javax.swing.JLabel();
        lblComDeduct = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblTotalBF = new javax.swing.JLabel();
        lblTotalRF = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        cmbTType = new javax.swing.JComboBox();
        jLabel10 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txtTRef = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        txtTAmount = new javax.swing.JTextField();
        busyIcon1 = new org.jdesktop.swingx.JXBusyLabel();
        btnSubmit = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        statusMessageLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getResourceMap(FrameBPBsp.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jToolBar1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        jButton1.setFont(resourceMap.getFont("jButton1.font")); // NOI18N
        jButton1.setIcon(resourceMap.getIcon("jButton1.icon")); // NOI18N
        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setFocusable(false);
        jButton1.setName("jButton1"); // NOI18N
        jToolBar1.add(jButton1);

        jButton4.setFont(resourceMap.getFont("jButton1.font")); // NOI18N
        jButton4.setIcon(resourceMap.getIcon("jButton4.icon")); // NOI18N
        jButton4.setText(resourceMap.getString("jButton4.text")); // NOI18N
        jButton4.setFocusable(false);
        jButton4.setName("jButton4"); // NOI18N
        jToolBar1.add(jButton4);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        getContentPane().add(jToolBar1, gridBagConstraints);

        splitPanelMaster.setBorder(null);
        splitPanelMaster.setDividerLocation(200);
        splitPanelMaster.setDividerSize(4);
        splitPanelMaster.setName("splitPanelMaster"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel8.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jPanel8.setName("jPanel8"); // NOI18N

        dtFrom.setFont(resourceMap.getFont("jLabel7.font")); // NOI18N
        dtFrom.setName("dtFrom"); // NOI18N

        dtTo.setFont(resourceMap.getFont("jLabel7.font")); // NOI18N
        dtTo.setName("dtTo"); // NOI18N

        jLabel6.setFont(resourceMap.getFont("jLabel7.font")); // NOI18N
        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        jLabel7.setFont(resourceMap.getFont("jLabel7.font")); // NOI18N
        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        btnSearch.setFont(resourceMap.getFont("jLabel7.font")); // NOI18N
        btnSearch.setIcon(resourceMap.getIcon("btnSearch.icon")); // NOI18N
        btnSearch.setText(resourceMap.getString("btnSearch.text")); // NOI18N
        btnSearch.setName("btnSearch"); // NOI18N
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        busyIcon.setText(resourceMap.getString("busyIcon.text")); // NOI18N
        busyIcon.setName("busyIcon"); // NOI18N

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(busyIcon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSearch))
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addContainerGap())
            .addComponent(dtFrom, javax.swing.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)
            .addComponent(dtTo, javax.swing.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel6)
                .addGap(5, 5, 5)
                .addComponent(dtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(jLabel7)
                .addGap(4, 4, 4)
                .addComponent(dtTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSearch)
                    .addComponent(busyIcon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanel1.add(jPanel8, gridBagConstraints);

        splitPanelMaster.setLeftComponent(jPanel1);

        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new java.awt.GridBagLayout());

        JScrollPane.setName("JScrollPane"); // NOI18N

        tblInvoice.setBackground(resourceMap.getColor("tblInvoice.background")); // NOI18N
        tblInvoice.setForeground(resourceMap.getColor("tblInvoice.foreground")); // NOI18N
        tblInvoice.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "BaseFare", "GDSPNR", "Career", "TktNo", "Status", "Status", "BaseFare", "Tax", "TotalFare", "BSPCom", "NetFare"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblInvoice.setFont(resourceMap.getFont("tblInvoice.font")); // NOI18N
        tblInvoice.setName("tblInvoice"); // NOI18N
        tblInvoice.setSortable(false);
        tblInvoice.getTableHeader().setReorderingAllowed(false);
        JScrollPane.setViewportView(tblInvoice);
        tblInvoice.getColumnModel().getColumn(0).setMinWidth(40);
        tblInvoice.getColumnModel().getColumn(0).setPreferredWidth(40);
        tblInvoice.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title8")); // NOI18N
        tblInvoice.getColumnModel().getColumn(1).setMinWidth(60);
        tblInvoice.getColumnModel().getColumn(1).setPreferredWidth(60);
        tblInvoice.getColumnModel().getColumn(1).setMaxWidth(60);
        tblInvoice.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title0")); // NOI18N
        tblInvoice.getColumnModel().getColumn(2).setMinWidth(70);
        tblInvoice.getColumnModel().getColumn(2).setPreferredWidth(70);
        tblInvoice.getColumnModel().getColumn(2).setMaxWidth(70);
        tblInvoice.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title3")); // NOI18N
        tblInvoice.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title4")); // NOI18N
        tblInvoice.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title6")); // NOI18N
        tblInvoice.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title7")); // NOI18N
        tblInvoice.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title8")); // NOI18N
        tblInvoice.getColumnModel().getColumn(7).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title9")); // NOI18N
        tblInvoice.getColumnModel().getColumn(8).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title7")); // NOI18N
        tblInvoice.getColumnModel().getColumn(9).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title9")); // NOI18N
        tblInvoice.getColumnModel().getColumn(10).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title10")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 0);
        jPanel2.add(JScrollPane, gridBagConstraints);

        jPanel10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel10.setName("jPanel10"); // NOI18N
        jPanel10.setLayout(new java.awt.GridBagLayout());

        jLabel9.setFont(resourceMap.getFont("jLabel9.font")); // NOI18N
        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 4, 8);
        jPanel10.add(jLabel9, gridBagConstraints);

        jLabel11.setFont(resourceMap.getFont("jLabel9.font")); // NOI18N
        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 4, 8);
        jPanel10.add(jLabel11, gridBagConstraints);

        lblTotalIssue.setFont(resourceMap.getFont("jLabel9.font")); // NOI18N
        lblTotalIssue.setText(resourceMap.getString("lblTotalIssue.text")); // NOI18N
        lblTotalIssue.setName("lblTotalIssue"); // NOI18N
        lblTotalIssue.setPreferredSize(new java.awt.Dimension(100, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        jPanel10.add(lblTotalIssue, gridBagConstraints);

        lblTotalRefund.setFont(resourceMap.getFont("jLabel9.font")); // NOI18N
        lblTotalRefund.setText(resourceMap.getString("lblTotalRefund.text")); // NOI18N
        lblTotalRefund.setName("lblTotalRefund"); // NOI18N
        lblTotalRefund.setPreferredSize(new java.awt.Dimension(100, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        jPanel10.add(lblTotalRefund, gridBagConstraints);

        jLabel18.setFont(resourceMap.getFont("jLabel9.font")); // NOI18N
        jLabel18.setForeground(resourceMap.getColor("jLabel18.foreground")); // NOI18N
        jLabel18.setText(resourceMap.getString("jLabel18.text")); // NOI18N
        jLabel18.setName("jLabel18"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 2);
        jPanel10.add(jLabel18, gridBagConstraints);

        lblBalanceToBePaid.setFont(resourceMap.getFont("jLabel9.font")); // NOI18N
        lblBalanceToBePaid.setForeground(resourceMap.getColor("jLabel18.foreground")); // NOI18N
        lblBalanceToBePaid.setText(resourceMap.getString("lblBalanceToBePaid.text")); // NOI18N
        lblBalanceToBePaid.setName("lblBalanceToBePaid"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 4, 4);
        jPanel10.add(lblBalanceToBePaid, gridBagConstraints);

        jSeparator1.setName("jSeparator1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.weightx = 1.0;
        jPanel10.add(jSeparator1, gridBagConstraints);

        jLabel1.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 4, 8);
        jPanel10.add(jLabel1, gridBagConstraints);

        jLabel5.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 4, 8);
        jPanel10.add(jLabel5, gridBagConstraints);

        lblBspCom.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        lblBspCom.setText(resourceMap.getString("lblBspCom.text")); // NOI18N
        lblBspCom.setName("lblBspCom"); // NOI18N
        lblBspCom.setPreferredSize(new java.awt.Dimension(60, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 4, 2);
        jPanel10.add(lblBspCom, gridBagConstraints);

        lblComDeduct.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        lblComDeduct.setText(resourceMap.getString("lblComDeduct.text")); // NOI18N
        lblComDeduct.setName("lblComDeduct"); // NOI18N
        lblComDeduct.setPreferredSize(new java.awt.Dimension(60, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 4, 2);
        jPanel10.add(lblComDeduct, gridBagConstraints);

        jLabel2.setFont(resourceMap.getFont("jLabel4.font")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 4, 2);
        jPanel10.add(jLabel2, gridBagConstraints);

        lblTotalBF.setFont(resourceMap.getFont("jLabel4.font")); // NOI18N
        lblTotalBF.setText(resourceMap.getString("lblTotalBF.text")); // NOI18N
        lblTotalBF.setName("lblTotalBF"); // NOI18N
        lblTotalBF.setPreferredSize(new java.awt.Dimension(100, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 4, 2);
        jPanel10.add(lblTotalBF, gridBagConstraints);

        lblTotalRF.setFont(resourceMap.getFont("lblTotalRF.font")); // NOI18N
        lblTotalRF.setText(resourceMap.getString("lblTotalRF.text")); // NOI18N
        lblTotalRF.setName("lblTotalRF"); // NOI18N
        lblTotalRF.setPreferredSize(new java.awt.Dimension(100, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 4, 2);
        jPanel10.add(lblTotalRF, gridBagConstraints);

        jLabel8.setFont(resourceMap.getFont("jLabel4.font")); // NOI18N
        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 4, 2);
        jPanel10.add(jLabel8, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(jPanel10, gridBagConstraints);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Submit Payment"));
        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setLayout(new java.awt.GridBagLayout());

        cmbTType.setName("cmbTType"); // NOI18N
        cmbTType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbTTypeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(cmbTType, gridBagConstraints);

        jLabel10.setFont(resourceMap.getFont("jLabel10.font")); // NOI18N
        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 21;
        gridBagConstraints.ipady = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(jLabel10, gridBagConstraints);

        jLabel17.setFont(resourceMap.getFont("jLabel10.font")); // NOI18N
        jLabel17.setText(resourceMap.getString("jLabel17.text")); // NOI18N
        jLabel17.setName("jLabel17"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(jLabel17, gridBagConstraints);

        txtTRef.setEditable(false);
        txtTRef.setName("txtTRef"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(txtTRef, gridBagConstraints);

        jLabel16.setFont(resourceMap.getFont("jLabel10.font")); // NOI18N
        jLabel16.setText(resourceMap.getString("jLabel16.text")); // NOI18N
        jLabel16.setName("jLabel16"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(jLabel16, gridBagConstraints);

        txtTAmount.setBackground(resourceMap.getColor("txtTAmount.background")); // NOI18N
        txtTAmount.setEditable(false);
        txtTAmount.setFont(resourceMap.getFont("txtTAmount.font")); // NOI18N
        txtTAmount.setName("txtTAmount"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(txtTAmount, gridBagConstraints);

        busyIcon1.setDirection(org.jdesktop.swingx.JXBusyLabel.Direction.RIGHT);
        busyIcon1.setName("busyIcon1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(busyIcon1, gridBagConstraints);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getActionMap(FrameBPBsp.class, this);
        btnSubmit.setAction(actionMap.get("submitTransaction")); // NOI18N
        btnSubmit.setFont(resourceMap.getFont("btnSubmit.font")); // NOI18N
        btnSubmit.setText(resourceMap.getString("btnSubmit.text")); // NOI18N
        btnSubmit.setMinimumSize(new java.awt.Dimension(65, 30));
        btnSubmit.setName("btnSubmit"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 25;
        gridBagConstraints.ipady = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(btnSubmit, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(jPanel3, gridBagConstraints);

        splitPanelMaster.setRightComponent(jPanel2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(splitPanelMaster, gridBagConstraints);

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

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        getContentPane().add(jPanel4, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        setSubmitNeeded(false);
        new Thread(new threadOutstandingPInvoices()).start();
}//GEN-LAST:event_btnSearchActionPerformed

    private void cmbTTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbTTypeActionPerformed
        if (cmbTType.getSelectedIndex() > 0) {
            setSubmitNeeded(true);
        }
}//GEN-LAST:event_cmbTTypeActionPerformed

    /**
     * @param args the command line arguments
     */
    /*public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new FrameBPBsp().setVisible(true);
            }
        });
    }*/
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane JScrollPane;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnSubmit;
    private org.jdesktop.swingx.JXBusyLabel busyIcon;
    private org.jdesktop.swingx.JXBusyLabel busyIcon1;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox cmbTType;
    private org.jdesktop.swingx.JXDatePicker dtFrom;
    private org.jdesktop.swingx.JXDatePicker dtTo;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblBalanceToBePaid;
    private javax.swing.JLabel lblBspCom;
    private javax.swing.JLabel lblComDeduct;
    private javax.swing.JLabel lblTotalBF;
    private javax.swing.JLabel lblTotalIssue;
    private javax.swing.JLabel lblTotalRF;
    private javax.swing.JLabel lblTotalRefund;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JSplitPane splitPanelMaster;
    private javax.swing.JLabel statusMessageLabel;
    private org.jdesktop.swingx.JXTable tblInvoice;
    private javax.swing.JTextField txtTAmount;
    private javax.swing.JTextField txtTRef;
    // End of variables declaration//GEN-END:variables
      
    //************************threads***************************
    private long startTime = 0;
    private long stopTime = 0;
    private float elapsedTime = 0;

    /**
     * @param pnrDao the pnrDao to set
     */

    private class threadOutstandingPInvoices implements Runnable {

        public threadOutstandingPInvoices() {
        }

        public void run() {
            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();

            statusMessageLabel.setText("Loading Invoice...");
            btnSearch.setEnabled(false);
            searchOutstandingPInvoice();
            btnSearch.setEnabled(true);
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            progressBar.repaint(); //Refresh graphics
        }
    }
}
