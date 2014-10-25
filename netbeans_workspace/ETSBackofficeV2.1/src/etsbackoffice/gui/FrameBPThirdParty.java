package etsbackoffice.gui;

/**
 *
 * @author Yusuf
 */
import etsbackoffice.ETSBackofficeApp;
import etsbackoffice.businesslogic.*;
import etsbackoffice.domain.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.application.Action;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

public class FrameBPThirdParty extends javax.swing.JFrame{

    private DateFormat df = new DateFormat();
    private AccountingDocBo acDocBo = (AccountingDocBo) ETSBackofficeApp.getApplication().ctx.getBean("acDocBo");
    private AgentBo agentBo = (AgentBo) ETSBackofficeApp.getApplication().ctx.getBean("agentBo");
    private AcTransactionBo acTransactionBo = (AcTransactionBo) ETSBackofficeApp.getApplication().ctx.getBean("acTransactionBo");
    private AccountsBo accountsBo = (AccountsBo) ETSBackofficeApp.getApplication().ctx.getBean("accountsBo");
    private Agent vendor;
    private PurchaseAccountingDocument acDoc = new PurchaseAccountingDocument();
    private List<Agent> outstandingVendors = new ArrayList();
    //private List<PurchaseAccountingDocument> outstandingInvoices = new ArrayList();
    private List<PurchaseAccountingDocument> invoicesToPay = new ArrayList();
    private DefaultTableModel invoiceModel;
    private DefaultTableModel invoiceToPayModel;
    DefaultComboBoxModel cmbAgtModel = new DefaultComboBoxModel();
    private boolean submitNeeded;

    private Long contactableId = null;
    private Date from = null;
    private Date to = null;
    /** Creates new form FramePaymentCollection1 */
    public FrameBPThirdParty(java.awt.Frame parent) {
        initComponents();
        busyIcon.setVisible(false);
        busyIcon1.setVisible(false);        
        btnSubmit.setEnabled(false);
        startUpThreads();
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

    public void showFrameBPThirdParty() {
        setLocationRelativeTo(this);
        setVisible(true);
        toFront();
        startUpThreads();
    }

    private void startUpThreads() {
        new Thread(new threadOutstandingVendors()).start();
    }

    public void searchOutstandingPInvoice() {        
        String[] data;

        from = dtFrom.getDate();
        to = dtTo.getDate();

        if (cmbContactable.getSelectedIndex() > 0) {
            data = cmbContactable.getSelectedItem().toString().split("-");
            contactableId = Long.parseLong(data[2]);

            this.invoicesToPay = acDocBo.outstandingPInvByCriteria(contactableId, from, to);
            populateTblInvoiceToPay();
        }
    }

    private void populateTxtAgentDetails(Agent agent) {
        txtContactableDetails.setText(agent.getFullAddressCRSeperated());
    }

    private void reverseEntry() {
        BigDecimal totalAmountEntry = new BigDecimal("0.00");
        BigDecimal remainingAmount = new BigDecimal("0.00");
        BigDecimal currentDue = new BigDecimal("0.00");

        if (!txtTAmount.getText().isEmpty()) {
            totalAmountEntry = new BigDecimal(txtTAmount.getText());
            remainingAmount = totalAmountEntry;
        }
        int noOfRow = tblInvoiceToPay.getRowCount();

        for (int i = 0; i < noOfRow; i++) {
            currentDue = new BigDecimal(String.valueOf(tblInvoiceToPay.getValueAt(i, 7)));
            if (remainingAmount.compareTo(currentDue) >= 0) {
                tblInvoiceToPay.setValueAt(currentDue, i, 8);
                remainingAmount = remainingAmount.subtract(currentDue);
            } else if (remainingAmount.compareTo(new BigDecimal("0.00")) > 0) {
                tblInvoiceToPay.setValueAt(remainingAmount, i, 8);
                remainingAmount = remainingAmount.subtract(remainingAmount);
            }
        }
    }

    private void outstandingVendors() {
        List cmbElement = new ArrayList();
        outstandingVendors = acDocBo.outstandingVendors();
        for (Agent agent : outstandingVendors) {
            cmbElement.add(agent.getName() + "-" + agent.getPostCode()
                    + "-" + agent.getContactableId());
        }

        Collections.sort(cmbElement);
        DefaultComboBoxModel cmbContactableModel = new DefaultComboBoxModel(cmbElement.toArray());
        cmbContactableModel.insertElementAt("Select", 0);
        cmbContactable.setModel(cmbContactableModel);
        cmbContactable.setSelectedIndex(0);
    }

    private void populateTblInvoiceToPay() {

        int row = 0;        
        BigDecimal tInvAmount = new BigDecimal("0.00");
        BigDecimal tInvAmountToPay = new BigDecimal("0.00");
        //BigDecimal tInvAmountToPay = new BigDecimal("0.00");
        BigDecimal tCredit = new BigDecimal("0.00");
        BigDecimal tPaid = new BigDecimal("0.00");
        
        invoiceToPayModel = (DefaultTableModel) tblInvoiceToPay.getModel();
        invoiceToPayModel.getDataVector().removeAllElements();

        tblInvoiceToPay.clearSelection();
        tblInvoiceToPay.repaint();

        for (PurchaseAccountingDocument a : invoicesToPay) {            
            
            tPaid = tPaid.add(a.getTotalTransactionAmountWithRelatedDoc());
            tInvAmount = tInvAmount.add(a.getTotalDocumentedAmount());
            
            BigDecimal due = a.getDueAmountWithRelatedDoc();
            tInvAmountToPay = tInvAmountToPay.add(due);
            
            for (PurchaseAccountingDocument creditNote : a.getRelatedDocuments()) {
                tCredit = tCredit.add(creditNote.getTotalDocumentedAmount());
            }

            invoiceToPayModel.insertRow(row, new Object[]{a.getVendorRef(), a.getPnr().getGdsPNR(), a.getLeadPaxFromTickets(),
                        df.dateForGui(a.getIssueDate()), a.getTotalDocumentedAmount(),
                        a.getTotalAdm(), a.getTotalTransactionAmountWithRelatedDoc(), a.getDueAmountWithRelatedDoc(), "", false});
            row++;
        }
        
        lblNoOfInvoice.setText(String.valueOf(invoicesToPay.size()));
        lblTotalInvAmount.setText(tInvAmount.toString());
        lblTotalCredit.setText(tCredit.toString());
        lblTotalPaid.setText(tPaid.toString());
        lblTotalDue.setText(tInvAmountToPay.toString());
        
        invoiceToPayModel.addTableModelListener(new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    int row = e.getFirstRow();
                    int column = e.getColumn();

                    BigDecimal due = new BigDecimal(tblInvoiceToPay.getValueAt(row, 7).toString());
                    BigDecimal newPayment = new BigDecimal("0.00");

                    if (column == 8) {
                        Object val = tblInvoiceToPay.getValueAt(row, 8);
                        if (!val.equals("")) {
                            newPayment = new BigDecimal(tblInvoiceToPay.getValueAt(row, 8).toString());
                            if (newPayment.compareTo(due) <= 0) {
                                if (newPayment.compareTo(due) == 0) {
                                    if (!tblInvoiceToPay.getValueAt(row, 9).equals(true)) {
                                        tblInvoiceToPay.setValueAt(true, row, 9);
                                    }
                                } else {
                                    if (!tblInvoiceToPay.getValueAt(row, 9).equals(false)) {
                                        tblInvoiceToPay.setValueAt(false, row, 9);
                                    }
                                }
                                
                            } else {
                                JOptionPane.showMessageDialog(null, "Payment can not be more then invoice amount", "Transaction", JOptionPane.WARNING_MESSAGE);
                                tblInvoiceToPay.setValueAt("", row, 8);
                                //totalPaid();
                            }
                        }
                        totalPaid();
                    } else if (column == 9) {
                        if (tblInvoiceToPay.getValueAt(row, 9).equals(true)) {
                            tblInvoiceToPay.setValueAt(due, row, 8);
                            totalPaid();
                        } else if (tblInvoiceToPay.getValueAt(row, 9).equals(false)) {
                            tblInvoiceToPay.setValueAt("", row, 8);
                            totalPaid();
                        }
                    }
                }
            }
        });
    }

    private void totalPaid() {
        int noOfRow = tblInvoiceToPay.getRowCount();
        BigDecimal totalPaid = new BigDecimal("0.00");

        for (int i = 0; i < noOfRow; i++) {
            Object rowVal = tblInvoiceToPay.getValueAt(i, 8);

            if (!rowVal.equals("")) {
                BigDecimal newPayment = new BigDecimal(rowVal.toString());
                totalPaid = totalPaid.add(newPayment);
            }
        }
        txtTAmount.setText(totalPaid.toString());        
    }

    @Action
    public void submitTransaction() {
        busyIcon1.setVisible(true);
        busyIcon1.setBusy(true);

        if (this.submitNeeded = true) {            
            String tRef = txtTRef.getText();
            int noOfRow = tblInvoiceToPay.getRowCount();
            List<BillingTransaction> newTransactions = new ArrayList();

            BatchBillingTransaction consignment = new BatchBillingTransaction();
            consignment.setBatchBillingDate(new java.util.Date());
            consignment.setBillingBy(AuthenticationBo.getLoggedOnUser());
            consignment.setVendor(this.vendor);
            consignment.setBatchBillingRef(acTransactionBo.generateBatchBillingRef(this.vendor.getName()));

            for (int i = 0; i < noOfRow; i++) {
                if (!tblInvoiceToPay.getValueAt(i, 8).equals("")) {
                    BigDecimal newPayment = new BigDecimal(tblInvoiceToPay.getValueAt(i, 8).toString());
                    BillingTransaction bt = new BillingTransaction();
                    bt.setPurchaseAccountingDocument(invoicesToPay.get(i));
                    bt.setInvoice(invoicesToPay.get(i));
                    bt.setPnr(invoicesToPay.get(i).getPnr());
                    bt.setTransDate(new java.util.Date());
                    bt.setTransAmount(newPayment);
                    bt.setTransType(Enums.TransType.valueOf(cmbTType.getSelectedItem().toString()).getId());
                    bt.setTransRef("Part of Bulk Payment: " + tRef);
                    bt.setUser(AuthenticationBo.getLoggedOnUser());
                    bt.setBatchBillingTransaction(consignment);
                    bt.addAcStatement(accountsBo.newAccountsTransactionFromPAcTransaction(bt));                    
                    newTransactions.add(bt);
                }
            }
            if (newTransactions.size() > 0) {
                consignment.setBillingTransactions(new LinkedHashSet(newTransactions));
                acTransactionBo.saveBBillingTransaction(consignment);
                setSubmitNeeded(false);
                this.invoicesToPay.clear();
                populateTblInvoiceToPay();
                searchOutstandingPInvoice();
            } else {
                JOptionPane.showMessageDialog(null, "No Transaction to submit", "Transaction", JOptionPane.WARNING_MESSAGE);
                setSubmitNeeded(false);
            }
        }
        busyIcon1.setBusy(false);
        busyIcon1.setVisible(false);
    }
    private ActionListener cmbContactableListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            String[] data;
            long id = 0;
            if (cmbContactable.getSelectedIndex() > 0) {
                data = cmbContactable.getSelectedItem().toString().split("-");
                id = Long.parseLong(data[2]);
            }
            loop:
            for (Agent currentVendor : outstandingVendors) {
                if (currentVendor.getContactableId() == id) {
                    vendor = currentVendor;
                    invoicesToPay.clear();
                    populateTblInvoiceToPay();
                    populateTxtAgentDetails(vendor);
                    break loop;
                }
            }
        }
    };
    private ActionListener chkReverseEntryListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            if (chkReverseEntry.isSelected() == true) {
                reverseEntry();
            } else {
                populateTblInvoiceToPay();
            }
        }
    };

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
        btnViewInvoice = new javax.swing.JButton();
        btnViewCNote = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        splitPanelMaster = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtContactableDetails = new javax.swing.JTextArea();
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
        chkUnBalancedInv = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblInvoiceToPay = new org.jdesktop.swingx.JXTable();
        jPanel10 = new javax.swing.JPanel();
        lblTotalRecord1 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        lblNoOfInvoice = new javax.swing.JLabel();
        lblTotalInvAmount = new javax.swing.JLabel();
        lblTotalCredit = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        lblTotalDue = new javax.swing.JLabel();
        lblTotalPaid = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel6 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        lblTOutRefund = new javax.swing.JLabel();
        btnApplyCredit = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        cmbTType = new javax.swing.JComboBox();
        jLabel10 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txtTRef = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        txtTAmount = new javax.swing.JTextField();
        busyIcon1 = new org.jdesktop.swingx.JXBusyLabel();
        btnSubmit = new javax.swing.JButton();
        chkReverseEntry = new javax.swing.JCheckBox();
        btnClear = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        statusMessageLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getResourceMap(FrameBPThirdParty.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jToolBar1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        jButton1.setFont(resourceMap.getFont("jButton4.font")); // NOI18N
        jButton1.setIcon(resourceMap.getIcon("jButton1.icon")); // NOI18N
        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setFocusable(false);
        jButton1.setName("jButton1"); // NOI18N
        jToolBar1.add(jButton1);

        btnViewInvoice.setFont(resourceMap.getFont("jButton4.font")); // NOI18N
        btnViewInvoice.setIcon(resourceMap.getIcon("btnViewInvoice.icon")); // NOI18N
        btnViewInvoice.setText(resourceMap.getString("btnViewInvoice.text")); // NOI18N
        btnViewInvoice.setFocusable(false);
        btnViewInvoice.setName("btnViewInvoice"); // NOI18N
        btnViewInvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewInvoiceActionPerformed(evt);
            }
        });
        jToolBar1.add(btnViewInvoice);

        btnViewCNote.setFont(resourceMap.getFont("btnViewCNote.font")); // NOI18N
        btnViewCNote.setText(resourceMap.getString("btnViewCNote.text")); // NOI18N
        btnViewCNote.setFocusable(false);
        btnViewCNote.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnViewCNote.setName("btnViewCNote"); // NOI18N
        btnViewCNote.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnViewCNote.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewCNoteActionPerformed(evt);
            }
        });
        jToolBar1.add(btnViewCNote);

        jButton4.setFont(resourceMap.getFont("jButton4.font")); // NOI18N
        jButton4.setIcon(resourceMap.getIcon("jButton4.icon")); // NOI18N
        jButton4.setText(resourceMap.getString("jButton4.text")); // NOI18N
        jButton4.setFocusable(false);
        jButton4.setName("jButton4"); // NOI18N
        jToolBar1.add(jButton4);

        jButton6.setFont(resourceMap.getFont("jButton4.font")); // NOI18N
        jButton6.setText(resourceMap.getString("jButton6.text")); // NOI18N
        jButton6.setFocusable(false);
        jButton6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton6.setName("jButton6"); // NOI18N
        jButton6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton6);

        jButton2.setFont(resourceMap.getFont("jButton4.font")); // NOI18N
        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setName("jButton2"); // NOI18N
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton2);

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

        txtContactableDetails.setColumns(16);
        txtContactableDetails.setEditable(false);
        txtContactableDetails.setFont(resourceMap.getFont("txtContactableDetails.font")); // NOI18N
        txtContactableDetails.setRows(5);
        txtContactableDetails.setName("txtContactableDetails"); // NOI18N
        jScrollPane1.setViewportView(txtContactableDetails);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel5.add(jScrollPane1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 0, 2);
        jPanel1.add(jPanel5, gridBagConstraints);

        jPanel8.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jPanel8.setName("jPanel8"); // NOI18N
        jPanel8.setLayout(new java.awt.GridBagLayout());

        jLabel5.setFont(resourceMap.getFont("jLabel7.font")); // NOI18N
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel8.add(jLabel5, gridBagConstraints);

        cmbContactable.setModel(cmbAgtModel);
        cmbContactable.setName("cmbContactable"); // NOI18N
        cmbContactable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbContactableActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel8.add(cmbContactable, gridBagConstraints);

        dtFrom.setName("dtFrom"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel8.add(dtFrom, gridBagConstraints);

        dtTo.setName("dtTo"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel8.add(dtTo, gridBagConstraints);

        jLabel6.setFont(resourceMap.getFont("jLabel7.font")); // NOI18N
        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel8.add(jLabel6, gridBagConstraints);

        jLabel7.setFont(resourceMap.getFont("jLabel7.font")); // NOI18N
        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel8.add(jLabel7, gridBagConstraints);

        btnSearch.setFont(resourceMap.getFont("btnSearch.font")); // NOI18N
        btnSearch.setIcon(resourceMap.getIcon("btnSearch.icon")); // NOI18N
        btnSearch.setText(resourceMap.getString("btnSearch.text")); // NOI18N
        btnSearch.setName("btnSearch"); // NOI18N
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel8.add(btnSearch, gridBagConstraints);

        busyIcon.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        busyIcon.setText(resourceMap.getString("busyIcon.text")); // NOI18N
        busyIcon.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        busyIcon.setName("busyIcon"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel8.add(busyIcon, gridBagConstraints);

        chkUnBalancedInv.setFont(resourceMap.getFont("chkUnBalancedInv.font")); // NOI18N
        chkUnBalancedInv.setText(resourceMap.getString("chkUnBalancedInv.text")); // NOI18N
        chkUnBalancedInv.setName("chkUnBalancedInv"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel8.add(chkUnBalancedInv, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanel1.add(jPanel8, gridBagConstraints);

        splitPanelMaster.setLeftComponent(jPanel1);

        jPanel2.setName("jPanel2"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        tblInvoiceToPay.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "VInvRef", "Pnr", "LeadPax", "InvDate", "InvAmount", "CRN", "Paid", "Due", "NewPayment", "FullPayment?"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblInvoiceToPay.setFont(resourceMap.getFont("tblInvoiceToPay.font")); // NOI18N
        tblInvoiceToPay.setName("tblInvoiceToPay"); // NOI18N
        tblInvoiceToPay.setSortable(false);
        tblInvoiceToPay.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tblInvoiceToPay);
        tblInvoiceToPay.getColumnModel().getColumn(0).setMinWidth(80);
        tblInvoiceToPay.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblInvoiceToPay.getColumnModel().getColumn(0).setMaxWidth(80);
        tblInvoiceToPay.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblInvoiceToPay.columnModel.title0")); // NOI18N
        tblInvoiceToPay.getColumnModel().getColumn(1).setMinWidth(80);
        tblInvoiceToPay.getColumnModel().getColumn(1).setPreferredWidth(80);
        tblInvoiceToPay.getColumnModel().getColumn(1).setMaxWidth(80);
        tblInvoiceToPay.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblInvoiceToPay.columnModel.title8")); // NOI18N
        tblInvoiceToPay.getColumnModel().getColumn(2).setMinWidth(200);
        tblInvoiceToPay.getColumnModel().getColumn(2).setPreferredWidth(220);
        tblInvoiceToPay.getColumnModel().getColumn(2).setMaxWidth(300);
        tblInvoiceToPay.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblInvoiceToPay.columnModel.title9")); // NOI18N
        tblInvoiceToPay.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblInvoiceToPay.columnModel.title1")); // NOI18N
        tblInvoiceToPay.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("tblInvoiceToPay.columnModel.title2")); // NOI18N
        tblInvoiceToPay.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("tblInvoiceToPay.columnModel.title6")); // NOI18N
        tblInvoiceToPay.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("tblInvoiceToPay.columnModel.title7")); // NOI18N
        tblInvoiceToPay.getColumnModel().getColumn(7).setHeaderValue(resourceMap.getString("tblInvoiceToPay.columnModel.title3")); // NOI18N
        tblInvoiceToPay.getColumnModel().getColumn(8).setHeaderValue(resourceMap.getString("tblInvoiceToPay.columnModel.title4")); // NOI18N
        tblInvoiceToPay.getColumnModel().getColumn(9).setHeaderValue(resourceMap.getString("tblInvoiceToPay.columnModel.title5")); // NOI18N

        jPanel10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel10.setName("jPanel10"); // NOI18N
        jPanel10.setLayout(new java.awt.GridBagLayout());

        lblTotalRecord1.setFont(resourceMap.getFont("jLabel9.font")); // NOI18N
        lblTotalRecord1.setText(resourceMap.getString("lblTotalRecord1.text")); // NOI18N
        lblTotalRecord1.setName("lblTotalRecord1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 4, 2);
        jPanel10.add(lblTotalRecord1, gridBagConstraints);

        jLabel9.setFont(resourceMap.getFont("jLabel9.font")); // NOI18N
        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 4, 2);
        jPanel10.add(jLabel9, gridBagConstraints);

        jLabel11.setFont(resourceMap.getFont("jLabel9.font")); // NOI18N
        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 4, 2);
        jPanel10.add(jLabel11, gridBagConstraints);

        lblNoOfInvoice.setFont(resourceMap.getFont("jLabel9.font")); // NOI18N
        lblNoOfInvoice.setText(resourceMap.getString("lblNoOfInvoice.text")); // NOI18N
        lblNoOfInvoice.setName("lblNoOfInvoice"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        jPanel10.add(lblNoOfInvoice, gridBagConstraints);

        lblTotalInvAmount.setFont(resourceMap.getFont("jLabel9.font")); // NOI18N
        lblTotalInvAmount.setText(resourceMap.getString("lblTotalInvAmount.text")); // NOI18N
        lblTotalInvAmount.setName("lblTotalInvAmount"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.5;
        jPanel10.add(lblTotalInvAmount, gridBagConstraints);

        lblTotalCredit.setFont(resourceMap.getFont("jLabel9.font")); // NOI18N
        lblTotalCredit.setText(resourceMap.getString("lblTotalCredit.text")); // NOI18N
        lblTotalCredit.setName("lblTotalCredit"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.5;
        jPanel10.add(lblTotalCredit, gridBagConstraints);

        jLabel15.setFont(resourceMap.getFont("jLabel9.font")); // NOI18N
        jLabel15.setText(resourceMap.getString("jLabel15.text")); // NOI18N
        jLabel15.setName("jLabel15"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 2);
        jPanel10.add(jLabel15, gridBagConstraints);

        jLabel18.setFont(resourceMap.getFont("jLabel9.font")); // NOI18N
        jLabel18.setText(resourceMap.getString("jLabel18.text")); // NOI18N
        jLabel18.setName("jLabel18"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 2);
        jPanel10.add(jLabel18, gridBagConstraints);

        lblTotalDue.setFont(resourceMap.getFont("jLabel9.font")); // NOI18N
        lblTotalDue.setText(resourceMap.getString("lblTotalDue.text")); // NOI18N
        lblTotalDue.setName("lblTotalDue"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 4, 4);
        jPanel10.add(lblTotalDue, gridBagConstraints);

        lblTotalPaid.setFont(resourceMap.getFont("jLabel9.font")); // NOI18N
        lblTotalPaid.setText(resourceMap.getString("lblTotalPaid.text")); // NOI18N
        lblTotalPaid.setName("lblTotalPaid"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 4, 4);
        jPanel10.add(lblTotalPaid, gridBagConstraints);

        jSeparator1.setName("jSeparator1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel10.add(jSeparator1, gridBagConstraints);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, resourceMap.getString("jPanel6.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, resourceMap.getFont("jPanel6.border.titleFont"))); // NOI18N
        jPanel6.setName("jPanel6"); // NOI18N

        jLabel3.setFont(resourceMap.getFont("jLabel3.font")); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        lblTOutRefund.setFont(resourceMap.getFont("jLabel3.font")); // NOI18N
        lblTOutRefund.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTOutRefund.setText(resourceMap.getString("lblTOutRefund.text")); // NOI18N
        lblTOutRefund.setName("lblTOutRefund"); // NOI18N

        btnApplyCredit.setFont(resourceMap.getFont("jLabel3.font")); // NOI18N
        btnApplyCredit.setIcon(resourceMap.getIcon("btnApplyCredit.icon")); // NOI18N
        btnApplyCredit.setText(resourceMap.getString("btnApplyCredit.text")); // NOI18N
        btnApplyCredit.setMaximumSize(new java.awt.Dimension(129, 33));
        btnApplyCredit.setName("btnApplyCredit"); // NOI18N
        btnApplyCredit.setOpaque(false);
        btnApplyCredit.setPreferredSize(new java.awt.Dimension(129, 33));
        btnApplyCredit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApplyCreditActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblTOutRefund, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(275, 275, 275)
                .addComponent(btnApplyCredit, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTOutRefund)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                .addComponent(btnApplyCredit, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, resourceMap.getString("jPanel3.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, resourceMap.getFont("jPanel3.border.titleFont"))); // NOI18N
        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setLayout(new java.awt.GridBagLayout());

        cmbTType.setFont(resourceMap.getFont("cmbTType.font")); // NOI18N
        cmbTType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Select", "CASH", "CHEQUE", "CREDITCARD", "DEBITCARD", "BANKDEPOSIT", "ONLINETRANSFER", "ACT ADJUSTMENT" }));
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

        jLabel10.setFont(resourceMap.getFont("cmbTType.font")); // NOI18N
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

        jLabel17.setFont(resourceMap.getFont("cmbTType.font")); // NOI18N
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
        txtTRef.setFont(resourceMap.getFont("cmbTType.font")); // NOI18N
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

        jLabel16.setFont(resourceMap.getFont("cmbTType.font")); // NOI18N
        jLabel16.setText(resourceMap.getString("jLabel16.text")); // NOI18N
        jLabel16.setName("jLabel16"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(jLabel16, gridBagConstraints);

        txtTAmount.setFont(resourceMap.getFont("cmbTType.font")); // NOI18N
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

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getActionMap(FrameBPThirdParty.class, this);
        btnSubmit.setAction(actionMap.get("submitTransaction")); // NOI18N
        btnSubmit.setFont(resourceMap.getFont("cmbTType.font")); // NOI18N
        btnSubmit.setIcon(resourceMap.getIcon("btnSubmit.icon")); // NOI18N
        btnSubmit.setText(resourceMap.getString("btnSubmit.text")); // NOI18N
        btnSubmit.setMinimumSize(new java.awt.Dimension(75, 30));
        btnSubmit.setName("btnSubmit"); // NOI18N
        btnSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSubmitActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 55;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(btnSubmit, gridBagConstraints);

        chkReverseEntry.setFont(resourceMap.getFont("cmbTType.font")); // NOI18N
        chkReverseEntry.setText(resourceMap.getString("chkReverseEntry.text")); // NOI18N
        chkReverseEntry.setName("chkReverseEntry"); // NOI18N
        chkReverseEntry.addActionListener(chkReverseEntryListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(chkReverseEntry, gridBagConstraints);

        btnClear.setFont(resourceMap.getFont("btnClear.font")); // NOI18N
        btnClear.setIcon(resourceMap.getIcon("btnClear.icon")); // NOI18N
        btnClear.setText(resourceMap.getString("btnClear.text")); // NOI18N
        btnClear.setName("btnClear"); // NOI18N
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 509, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 937, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

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

    private void cmbContactableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbContactableActionPerformed
}//GEN-LAST:event_cmbContactableActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        setSubmitNeeded(false);
        new Thread(new threadOutstandingPInvoices()).start();
}//GEN-LAST:event_btnSearchActionPerformed

    private void btnApplyCreditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApplyCreditActionPerformed
        int row = tblInvoiceToPay.getSelectedRow();
        if (row != -1) {
            DlgApplyPCredit dlgApplyPC = new DlgApplyPCredit(this);
            if (dlgApplyPC.showDlgApplyCredit(this.invoicesToPay.get(row))) {
                new Thread(new threadOutstandingPInvoices()).start();
            } else {
                System.out.println("Note");
            }
        }
}//GEN-LAST:event_btnApplyCreditActionPerformed

    private void btnViewInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewInvoiceActionPerformed
        viewTPInvoice();
    }//GEN-LAST:event_btnViewInvoiceActionPerformed

    private void btnViewCNoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewCNoteActionPerformed
        viewTPCNote();
    }//GEN-LAST:event_btnViewCNoteActionPerformed

    private void cmbTTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbTTypeActionPerformed
        if (cmbTType.getSelectedIndex() > 0) {
            setSubmitNeeded(true);
        }
}//GEN-LAST:event_cmbTTypeActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        populateTblInvoiceToPay();
}//GEN-LAST:event_btnClearActionPerformed

    private void btnSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubmitActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSubmitActionPerformed

    /**
     * @param args the command line arguments
     */
  /*  public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new FrameBPThirdParty().setVisible(true);
            }
        });
    }*/
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnApplyCredit;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnSubmit;
    private javax.swing.JButton btnViewCNote;
    private javax.swing.JButton btnViewInvoice;
    private org.jdesktop.swingx.JXBusyLabel busyIcon;
    private org.jdesktop.swingx.JXBusyLabel busyIcon1;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox chkReverseEntry;
    private javax.swing.JCheckBox chkUnBalancedInv;
    private javax.swing.JComboBox cmbContactable;
    private javax.swing.JComboBox cmbTType;
    private org.jdesktop.swingx.JXDatePicker dtFrom;
    private org.jdesktop.swingx.JXDatePicker dtTo;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblNoOfInvoice;
    private javax.swing.JLabel lblTOutRefund;
    private javax.swing.JLabel lblTotalCredit;
    private javax.swing.JLabel lblTotalDue;
    private javax.swing.JLabel lblTotalInvAmount;
    private javax.swing.JLabel lblTotalPaid;
    private javax.swing.JLabel lblTotalRecord1;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JSplitPane splitPanelMaster;
    private javax.swing.JLabel statusMessageLabel;
    private org.jdesktop.swingx.JXTable tblInvoiceToPay;
    private javax.swing.JTextArea txtContactableDetails;
    private javax.swing.JTextField txtTAmount;
    private javax.swing.JTextField txtTRef;
    // End of variables declaration//GEN-END:variables

    @Action
    public void viewInvoice() {
        int row = tblInvoiceToPay.getSelectedRow();
        if (row != -1) {
            try {
                long acDocId = this.invoicesToPay.get(row).getPurchaseAcDocId();
                Thread t = new Thread(new threadLoadCompleteInvoice(acDocId));
                t.start();
                t.join();
                {
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(FrameBPThirdParty.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void viewTPInvoice() {
        int row = tblInvoiceToPay.getSelectedRow();

        DlgTPInvoice dlgTPInvoice = new DlgTPInvoice(this);
        if (dlgTPInvoice.showDialog(this.invoicesToPay.get(row))) {
            dlgTPInvoice.dispose();
        } else {
            //new Thread(new threadPurchaseAcDoc(pnr.getPnrId())).start();
        }
    }

    private void viewTPCNote() {
        int row = tblInvoiceToPay.getSelectedRow();
        if (!this.invoicesToPay.get(row).getRelatedDocuments().isEmpty()) {
            DlgTPInvoice dlgTPInvoice = new DlgTPInvoice(this);
            if (dlgTPInvoice.showDialog(this.invoicesToPay.get(row).getRelatedDocuments().iterator().next())) {
                dlgTPInvoice.dispose();
            } else {
                //new Thread(new threadPurchaseAcDoc(pnr.getPnrId())).start();
            }
        } else {
            JOptionPane.showMessageDialog(null, "No Credit Note", "View Credit Note", JOptionPane.WARNING_MESSAGE);
        }
    }
       
    //************************threads***************************
    private long startTime = 0;
    private long stopTime = 0;
    private float elapsedTime = 0;

    /**
     * @param pnrDao the pnrDao to set
     */
    private class threadLoadCompleteInvoice implements Runnable {

        private long acDocId;

        public threadLoadCompleteInvoice(long acDocId) {
            this.acDocId = acDocId;
        }

        public void run() {

            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();

            statusMessageLabel.setText("Loading Invoice...");
            acDocBo.findCompletePAcDocWithRelatedDocsById(acDocId);
            acDoc = acDocBo.getPurchaseAccountingDocument();

            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            progressBar.repaint(); //Refresh graphics
        }
    }

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

    private class threadOutstandingVendors implements Runnable {

        public threadOutstandingVendors() {
        }

        public void run() {
            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();

            statusMessageLabel.setText("Loading Invoice...");
            btnSearch.setEnabled(false);
            outstandingVendors();
            btnSearch.setEnabled(true);
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            progressBar.repaint(); //Refresh graphics
        }
    }
}
