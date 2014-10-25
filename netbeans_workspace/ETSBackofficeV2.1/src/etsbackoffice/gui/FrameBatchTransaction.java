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

public class FrameBatchTransaction extends javax.swing.JFrame{

    private DateFormat df = new DateFormat();
    AccountingDocBo acDocBo = (AccountingDocBo) ETSBackofficeApp.getApplication().ctx.getBean("acDocBo");
    AgentBo agentBo = (AgentBo) ETSBackofficeApp.getApplication().ctx.getBean("agentBo");
    private AccountsBo accountsBo = (AccountsBo) ETSBackofficeApp.getApplication().ctx.getBean("accountsBo");
    
    AcTransactionBo acTransactionBo = (AcTransactionBo) ETSBackofficeApp.getApplication().ctx.getBean("acTransactionBo");
    private AccountingDocument acDoc = new AccountingDocument();
    private Agent agent;
    private List<Agent> outstandingAgents = new ArrayList();             
    private List<AccountingDocument> invoicesForCollection = new ArrayList();
        
    private DefaultTableModel invoiceForCollModel;
    DefaultComboBoxModel cmbCustModel = new DefaultComboBoxModel();
    DefaultComboBoxModel cmbAgtModel = new DefaultComboBoxModel();
    private boolean submitNeeded;

    /** Creates new form FramePaymentCollection1 */
    public FrameBatchTransaction(java.awt.Frame parent) {
        initComponents();
        CheckInput c = new CheckInput(CheckInput.FLOAT);
        c.setNegativeAccepted(true);
        txtTAmount.setDocument(c);
                        
        btnSubmit.setEnabled(false);
        startUpThreads();
    }
    
    private void resetComponent() {                
        lblTotalInvAmount.setText(": 0.00");
        lblTotalCredit.setText(": 0.00");
        lblTotalReceived.setText(": 0.00");
        lblTotalDue.setText(": 0.00");
        //lblAwaitingPayment.setText(": 0.00");
        txtTAmount.setText("");
        chkReverseEntry.setSelected(false);
        btnSubmit.setEnabled(false);
    }
    
    @Action
    public void closeWindow() {
        dispose();
    }

    private void startUpThreads() {
        new Thread(new threadOutstandingAgents()).start();
    }

    private void outstandingAgents() {
        List cmbElement = new ArrayList();
        outstandingAgents = acDocBo.outstandingAgents();
        for (Agent a : outstandingAgents) {
            cmbElement.add(a.getName() + "-" + a.getPostCode()
                    + "-" + a.getContactableId());
        }

        Collections.sort(cmbElement);
        DefaultComboBoxModel cmbContactableModel = new DefaultComboBoxModel(cmbElement.toArray());
        cmbContactableModel.insertElementAt("Select", 0);
        cmbContactable.setModel(cmbContactableModel);
        cmbContactable.setSelectedIndex(0);
    }

    public void searchOutstandingInvoice() {
        int contactableType = 1;//BTransaction only for agents
        long contactableId = 0;
        Date from = null;
        Date to = null;
        String[] data;

        from = dtFrom.getDate();
        to = dtTo.getDate();

        if (cmbContactable.getSelectedIndex() > 0) {
            data = cmbContactable.getSelectedItem().toString().split("-");
            contactableId = Long.parseLong(data[2]);
            if (chkUnBalancedInv.isSelected()) {
                this.invoicesForCollection = acDocBo.outstandingUnBalancedInvForCollection(contactableId, contactableType, from, to);
            } else {
                this.invoicesForCollection = acDocBo.outstandingInvForCollection(contactableId, contactableType, from, to);
            }
            populateTblInvForColl();
        }
        //lblAwaitingPayment.setText(": 0.00");
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
        int noOfRow = tblInvoiceForCol.getRowCount();

        for (int i = 0; i < noOfRow; i++) {
            currentDue = new BigDecimal(String.valueOf(tblInvoiceForCol.getValueAt(i, 7)));
            if(remainingAmount.compareTo(currentDue) >=0){
             tblInvoiceForCol.setValueAt(currentDue, i, 8);
             remainingAmount = remainingAmount.subtract(currentDue);
            }else if(remainingAmount.compareTo(new BigDecimal("0.00")) >0){
             tblInvoiceForCol.setValueAt(remainingAmount, i, 8);
             remainingAmount = remainingAmount.subtract(remainingAmount);
            }
        }
    }

    private void totalPaid() {
        int noOfRow = tblInvoiceForCol.getRowCount();
        BigDecimal totalPaid = new BigDecimal("0.00");

        for (int i = 0; i < noOfRow; i++) {
            Object rowVal = tblInvoiceForCol.getValueAt(i, 8);
            if (!rowVal.equals("")) {
                BigDecimal newPayment = new BigDecimal(rowVal.toString());
                totalPaid = totalPaid.add(newPayment);
            }
        }
        txtTAmount.setText(totalPaid.toString());
        //lblAwaitingPayment.setText(" : "+totalPaid.toString());
    }    

     public List<AcTransaction> allocateRefund(AccountingDocument invoice,BigDecimal totalAmount) {
        List<AcTransaction> acts = new ArrayList();
        BigDecimal remainingAmount = totalAmount;                                                         

        for (AccountingDocument currentCNote : invoice.getRelatedDocuments()) {
            BigDecimal outstandingAmount = currentCNote.getOutstandingAmount();
            AcTransaction t = new AcTransaction();
            
            if (remainingAmount.compareTo(outstandingAmount) < 0) {
                t.setTransDate(new java.util.Date());
                t.setTransAmount(outstandingAmount);
                t.setAccountingDocument(currentCNote);
                t.setInvoice(invoice);
                t.setTransType(7);//Ac Adjustment
                t.setTransRef("Part of Batch Transaction: " + ", Amount: " + txtTAmount.getText());
                acts.add(t);
                remainingAmount = remainingAmount.subtract(outstandingAmount);                
            } else if (remainingAmount.compareTo(outstandingAmount) >= 0) {
                t.setTransDate(new java.util.Date());
                t.setTransAmount(remainingAmount);
                t.setAccountingDocument(currentCNote);
                t.setInvoice(invoice);
                acts.add(t);
                remainingAmount = remainingAmount.subtract(remainingAmount);                
            }            
        }
        return acts;
    }
           

    private void populateTblInvForColl() {
        resetComponent();
        
        int noOfOutInv1 = 0;
        int noOfOutRefund1 = 0;
        BigDecimal tInvAmount = new BigDecimal("0.00");
        BigDecimal tInvAmountToReceive = new BigDecimal("0.00");        
        BigDecimal tCredit = new BigDecimal("0.00");
        BigDecimal tReceived = new BigDecimal("0.00");
        //BigDecimal tCredit = new BigDecimal("0.00");
        
        int row = 0;
        invoiceForCollModel = (DefaultTableModel) tblInvoiceForCol.getModel();
        invoiceForCollModel.getDataVector().removeAllElements();

        tblInvoiceForCol.clearSelection();
        tblInvoiceForCol.repaint();

        for (AccountingDocument a : invoicesForCollection) {            
            
            tReceived = tReceived.add(a.getTotalTransactionAmountWithRelatedDoc());
            tInvAmount = tInvAmount.add(a.getTotalDocumentedAmount());
            for (AccountingDocument creditNote : a.getRelatedDocuments()) {
                tCredit = tCredit.add(creditNote.getTotalDocumentedAmount());
            }
            
            BigDecimal due = a.getDueAmountWithRelatedDoc();
            tInvAmountToReceive = tInvAmountToReceive.add(due);
            if (due.signum() == 1) {
                noOfOutInv1++;
                //tInvAmountToReceive = tInvAmountToReceive.add(due);
            } else {
                noOfOutRefund1++;
                //tInvAmountToPay = tInvAmountToPay.add(due);
            }
            
            invoiceForCollModel.insertRow(row, new Object[]{a.getAcDocRef(),a.getPnr().getGdsPNR(),a.getLeadPaxFromTickets(), 
                df.dateForGui(a.getIssueDate()), a.getTotalDocumentedAmount(),a.getTotalAdm(),
                a.getTotalTransactionAmountWithRelatedDoc(), due, "", false});
            row++;
        }   
        
        lblNoOfInvoice.setText(String.valueOf(invoicesForCollection.size()));
        lblTotalInvAmount.setText(tInvAmount.toString());
        lblTotalCredit.setText(tCredit.toString());
        lblTotalReceived.setText(tReceived.toString());
        lblTotalDue.setText(tInvAmountToReceive.toString());
        
        invoiceForCollModel.addTableModelListener(new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    int row = e.getFirstRow();
                    int column = e.getColumn();

                    BigDecimal due = new BigDecimal(tblInvoiceForCol.getValueAt(row, 7).toString());
                    BigDecimal newPayment = new BigDecimal("0.00");

                    if (column == 8) {
                        Object val = tblInvoiceForCol.getValueAt(row, 8);
                        
                        if (!val.equals("")) {
                            newPayment = new BigDecimal(val.toString());
                            if (newPayment.compareTo(due) <= 0) {
                                if (newPayment.compareTo(due) == 0) {
                                    if (!tblInvoiceForCol.getValueAt(row, 9).equals(true)) {
                                        tblInvoiceForCol.setValueAt(true, row, 9);
                                    }
                                } else {
                                    if (!tblInvoiceForCol.getValueAt(row, 9).equals(false)) {
                                        tblInvoiceForCol.setValueAt(false, row, 9);
                                    }
                                }
                                //totalPaid();
                            } else {
                                JOptionPane.showMessageDialog(null, "Payment can not be more then invoice amount", "Transaction", JOptionPane.WARNING_MESSAGE);
                                tblInvoiceForCol.setValueAt("", row, 8);
                                //totalPaid();
                            }
                        }
                        totalPaid();
                    } else if (column == 9) {
                        if (tblInvoiceForCol.getValueAt(row, 9).equals(true)) {
                            tblInvoiceForCol.setValueAt(due, row, 8);
                            totalPaid();
                        } else {
                            tblInvoiceForCol.setValueAt("", row, 8);
                            totalPaid();
                        }
                    }
                }
            }
        });
    }

    @Action
    public void submitTransaction() {

        busyIcon1.setVisible(true);
        busyIcon1.setBusy(true);
        if (this.submitNeeded = true) {
            int type = cmbTType.getSelectedIndex();
            String tRef = txtTRef.getText();
            int noOfRow = tblInvoiceForCol.getRowCount();
            List<AcTransaction> newTransactions = new ArrayList();

            BatchTransaction consignment = new BatchTransaction();
            consignment.setCollectionDate(new java.util.Date());
            consignment.setCollectionBy(AuthenticationBo.getLoggedOnUser());
            consignment.setConsignee(this.agent);
            consignment.setCollectionRef(acTransactionBo.generateBatchTransRef(this.agent.getName()));

            for (int i = 0; i < noOfRow; i++) {
                Object val = tblInvoiceForCol.getValueAt(i, 8);
                
                if (!val.equals("")) {                    
                    BigDecimal newPayment = new BigDecimal(val.toString());
                    if (newPayment.compareTo(new BigDecimal("0.00")) > 0) {
                        AcTransaction t = new AcTransaction();
                        t.setAccountingDocument(invoicesForCollection.get(i));
                        t.setInvoice(invoicesForCollection.get(i));
                        t.setPnr(invoicesForCollection.get(i).getPnr());
                        t.setTransDate(new java.util.Date());
                        t.setTransAmount(newPayment);
                        t.setTransType(type);
                        t.setTransRef("Part of Batch Transaction: " + tRef + ", Amount: " + txtTAmount.getText());
                        t.setUser(AuthenticationBo.getLoggedOnUser());
                        t.setBatchTransaction(consignment);
                        t.setActive(true);
                        newTransactions.add(t);
                    }else if(newPayment.compareTo(new BigDecimal("0.00")) < 0){
                       List<AcTransaction> rfundsInCurrentAcDoc =  allocateRefund(invoicesForCollection.get(i),newPayment);
                       
                      for(AcTransaction t: rfundsInCurrentAcDoc){
                       t.setBatchTransaction(consignment);
                      }  
                      newTransactions.addAll(rfundsInCurrentAcDoc);
                    }
                }
            }
            for(AcTransaction t : newTransactions){
             t.addAcStatement(accountsBo.newAccountsTransactionFromSAcTransaction(t));
            }
            
            if (newTransactions.size() > 0) {
                consignment.setAcTransactions(new LinkedHashSet(newTransactions));
                //acTransactionBo.saveBulkACTransaction(newTransactions);
                acTransactionBo.saveBTransaction(consignment);
                System.out.println("Consignee>>>"+consignment.getConsignee().getName());
                setSubmitNeeded(false);
                this.invoicesForCollection.clear();
                populateTblInvForColl();
                searchOutstandingInvoice();
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
            for (Agent currentAgent : outstandingAgents) {
                if (currentAgent.getContactableId() == id) {
                    agent = currentAgent;
                    invoicesForCollection.clear();
                    populateTblInvForColl();
                    populateTxtAgentDetails(agent);
                    break loop;
                }
            }
        }
    };

    private ActionListener chkReverseEntryListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            if (!txtTAmount.getText().isEmpty()) {
                if (chkReverseEntry.isSelected() == true) {
                    reverseEntry();
                } else {
                    populateTblInvForColl();
                }
            } else {
                JOptionPane.showMessageDialog(null, "No amount to allocate", "Batch Transaction", JOptionPane.WARNING_MESSAGE);
                chkReverseEntry.setSelected(false);
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
        jButton5 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
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
        chkUnBalancedInv = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        JScrollPane = new javax.swing.JScrollPane();
        tblInvoiceForCol = new org.jdesktop.swingx.JXTable();
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
        lblTotalReceived = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel6 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        lblTOutRefund = new javax.swing.JLabel();
        btnApplyCredit = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        statusMessageLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getResourceMap(FrameBatchTransaction.class);
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

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getActionMap(FrameBatchTransaction.class, this);
        jButton5.setAction(actionMap.get("viewInvoice")); // NOI18N
        jButton5.setFont(resourceMap.getFont("jButton1.font")); // NOI18N
        jButton5.setIcon(resourceMap.getIcon("jButton5.icon")); // NOI18N
        jButton5.setText(resourceMap.getString("jButton5.text")); // NOI18N
        jButton5.setFocusable(false);
        jButton5.setName("jButton5"); // NOI18N
        jToolBar1.add(jButton5);

        jButton4.setFont(resourceMap.getFont("jButton1.font")); // NOI18N
        jButton4.setIcon(resourceMap.getIcon("jButton4.icon")); // NOI18N
        jButton4.setText(resourceMap.getString("jButton4.text")); // NOI18N
        jButton4.setFocusable(false);
        jButton4.setName("jButton4"); // NOI18N
        jToolBar1.add(jButton4);

        jButton2.setFont(resourceMap.getFont("jButton2.font")); // NOI18N
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

        jLabel1.setFont(resourceMap.getFont("txtContactableDetails.font")); // NOI18N
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

        txtContactableDetails.setColumns(15);
        txtContactableDetails.setEditable(false);
        txtContactableDetails.setFont(resourceMap.getFont("txtContactableDetails.font")); // NOI18N
        txtContactableDetails.setLineWrap(true);
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

        jLabel5.setFont(resourceMap.getFont("cmbContactable.font")); // NOI18N
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        cmbContactable.setFont(resourceMap.getFont("cmbContactable.font")); // NOI18N
        cmbContactable.setModel(cmbAgtModel);
        cmbContactable.setName("cmbContactable"); // NOI18N
        cmbContactable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbContactableActionPerformed(evt);
            }
        });

        dtFrom.setFont(resourceMap.getFont("cmbContactable.font")); // NOI18N
        dtFrom.setName("dtFrom"); // NOI18N

        dtTo.setFont(resourceMap.getFont("cmbContactable.font")); // NOI18N
        dtTo.setName("dtTo"); // NOI18N

        jLabel6.setFont(resourceMap.getFont("cmbContactable.font")); // NOI18N
        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        jLabel7.setFont(resourceMap.getFont("cmbContactable.font")); // NOI18N
        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        btnSearch.setFont(resourceMap.getFont("btnSearch.font")); // NOI18N
        btnSearch.setIcon(resourceMap.getIcon("btnSearch.icon")); // NOI18N
        btnSearch.setText(resourceMap.getString("btnSearch.text")); // NOI18N
        btnSearch.setName("btnSearch"); // NOI18N
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        chkUnBalancedInv.setFont(resourceMap.getFont("chkUnBalancedInv.font")); // NOI18N
        chkUnBalancedInv.setText(resourceMap.getString("chkUnBalancedInv.text")); // NOI18N
        chkUnBalancedInv.setName("chkUnBalancedInv"); // NOI18N

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                        .addContainerGap(83, Short.MAX_VALUE)
                        .addComponent(btnSearch))
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addContainerGap())
            .addComponent(cmbContactable, 0, 192, Short.MAX_VALUE)
            .addComponent(dtFrom, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
            .addComponent(dtTo, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addContainerGap(120, Short.MAX_VALUE))
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(chkUnBalancedInv)
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel5)
                .addGap(7, 7, 7)
                .addComponent(cmbContactable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(jLabel6)
                .addGap(5, 5, 5)
                .addComponent(dtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(jLabel7)
                .addGap(4, 4, 4)
                .addComponent(dtTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(chkUnBalancedInv)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSearch)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

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

        JScrollPane.setAutoscrolls(true);
        JScrollPane.setName("JScrollPane"); // NOI18N

        tblInvoiceForCol.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "InvRef", "PNR", "LeadPax", "InvDate", "InvAmount", "CRN", "Paid", "Due", "NewPayment", "FullPayment?"
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
        tblInvoiceForCol.setFont(resourceMap.getFont("tblInvoiceForCol.font")); // NOI18N
        tblInvoiceForCol.setName("tblInvoiceForCol"); // NOI18N
        tblInvoiceForCol.setSortable(false);
        tblInvoiceForCol.getTableHeader().setReorderingAllowed(false);
        JScrollPane.setViewportView(tblInvoiceForCol);
        tblInvoiceForCol.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblInvoiceToPay.columnModel.title3")); // NOI18N
        tblInvoiceForCol.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblInvoiceForCol.columnModel.title6")); // NOI18N
        tblInvoiceForCol.getColumnModel().getColumn(2).setMinWidth(200);
        tblInvoiceForCol.getColumnModel().getColumn(2).setPreferredWidth(220);
        tblInvoiceForCol.getColumnModel().getColumn(2).setMaxWidth(300);
        tblInvoiceForCol.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblInvoiceForCol.columnModel.title7")); // NOI18N
        tblInvoiceForCol.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblInvoiceToPay.columnModel.title4")); // NOI18N
        tblInvoiceForCol.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("tblInvoiceToPay.columnModel.title6")); // NOI18N
        tblInvoiceForCol.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("tblInvoiceForCol.columnModel.title8")); // NOI18N
        tblInvoiceForCol.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("tblInvoiceForCol.columnModel.title9")); // NOI18N
        tblInvoiceForCol.getColumnModel().getColumn(7).setHeaderValue(resourceMap.getString("tblInvoiceToPay.columnModel.title9")); // NOI18N
        tblInvoiceForCol.getColumnModel().getColumn(8).setHeaderValue(resourceMap.getString("tblInvoiceToPay.columnModel.title10")); // NOI18N
        tblInvoiceForCol.getColumnModel().getColumn(9).setHeaderValue(resourceMap.getString("tblInvoiceToPay.columnModel.title11")); // NOI18N

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

        btnSubmit.setAction(actionMap.get("submitTransaction")); // NOI18N
        btnSubmit.setFont(resourceMap.getFont("btnSubmit.font")); // NOI18N
        btnSubmit.setIcon(resourceMap.getIcon("btnSubmit.icon")); // NOI18N
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

        chkReverseEntry.setFont(resourceMap.getFont("chkReverseEntry.font")); // NOI18N
        chkReverseEntry.setText(resourceMap.getString("chkReverseEntry.text")); // NOI18N
        chkReverseEntry.setName("chkReverseEntry"); // NOI18N
        chkReverseEntry.addActionListener(chkReverseEntryListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(chkReverseEntry, gridBagConstraints);

        jPanel10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel10.setName("jPanel10"); // NOI18N
        jPanel10.setLayout(new java.awt.GridBagLayout());

        lblTotalRecord1.setFont(resourceMap.getFont("lblTotalRecord1.font")); // NOI18N
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
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 4, 2);
        jPanel10.add(jLabel9, gridBagConstraints);

        jLabel11.setFont(resourceMap.getFont("jLabel11.font")); // NOI18N
        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 4, 2);
        jPanel10.add(jLabel11, gridBagConstraints);

        lblNoOfInvoice.setFont(resourceMap.getFont("lblNoOfInvoice.font")); // NOI18N
        lblNoOfInvoice.setText(resourceMap.getString("lblNoOfInvoice.text")); // NOI18N
        lblNoOfInvoice.setName("lblNoOfInvoice"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        jPanel10.add(lblNoOfInvoice, gridBagConstraints);

        lblTotalInvAmount.setFont(resourceMap.getFont("lblTotalInvAmount.font")); // NOI18N
        lblTotalInvAmount.setText(resourceMap.getString("lblTotalInvAmount.text")); // NOI18N
        lblTotalInvAmount.setName("lblTotalInvAmount"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        jPanel10.add(lblTotalInvAmount, gridBagConstraints);

        lblTotalCredit.setFont(resourceMap.getFont("lblTotalCredit.font")); // NOI18N
        lblTotalCredit.setText(resourceMap.getString("lblTotalCredit.text")); // NOI18N
        lblTotalCredit.setName("lblTotalCredit"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        jPanel10.add(lblTotalCredit, gridBagConstraints);

        jLabel15.setFont(resourceMap.getFont("jLabel15.font")); // NOI18N
        jLabel15.setText(resourceMap.getString("jLabel15.text")); // NOI18N
        jLabel15.setName("jLabel15"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 2);
        jPanel10.add(jLabel15, gridBagConstraints);

        jLabel18.setFont(resourceMap.getFont("jLabel18.font")); // NOI18N
        jLabel18.setText(resourceMap.getString("jLabel18.text")); // NOI18N
        jLabel18.setName("jLabel18"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 2);
        jPanel10.add(jLabel18, gridBagConstraints);

        lblTotalDue.setFont(resourceMap.getFont("lblTotalDue.font")); // NOI18N
        lblTotalDue.setText(resourceMap.getString("lblTotalDue.text")); // NOI18N
        lblTotalDue.setName("lblTotalDue"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 4, 4);
        jPanel10.add(lblTotalDue, gridBagConstraints);

        lblTotalReceived.setFont(resourceMap.getFont("lblTotalReceived.font")); // NOI18N
        lblTotalReceived.setText(resourceMap.getString("lblTotalReceived.text")); // NOI18N
        lblTotalReceived.setName("lblTotalReceived"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 4, 4);
        jPanel10.add(lblTotalReceived, gridBagConstraints);

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

        lblTOutRefund.setFont(resourceMap.getFont("lblTOutRefund.font")); // NOI18N
        lblTOutRefund.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTOutRefund.setText(resourceMap.getString("lblTOutRefund.text")); // NOI18N
        lblTOutRefund.setName("lblTOutRefund"); // NOI18N

        btnApplyCredit.setFont(resourceMap.getFont("btnApplyCredit.font")); // NOI18N
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
                .addContainerGap(142, Short.MAX_VALUE))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap(247, Short.MAX_VALUE)
                .addComponent(btnApplyCredit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTOutRefund)
                    .addComponent(jLabel3))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(23, Short.MAX_VALUE)
                .addComponent(btnApplyCredit, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, 392, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(2, 2, 2)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 516, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(JScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 912, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(JScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(247, Short.MAX_VALUE)
                .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(149, 149, 149))
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
        new Thread(new threadOutstandingInvoiceReport()).start();
}//GEN-LAST:event_btnSearchActionPerformed

    private void btnApplyCreditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApplyCreditActionPerformed
        int row = tblInvoiceForCol.getSelectedRow();
        if (row != -1) {
            DlgApplyCredit dlgApplyC = new DlgApplyCredit(this);
            if (dlgApplyC.showDlgApplyCredit(this.invoicesForCollection.get(row))) {
                new Thread(new threadOutstandingInvoiceReport()).start();
            } else {
                System.out.println("Note");
            }
        }
}//GEN-LAST:event_btnApplyCreditActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        populateTblInvForColl();
    }//GEN-LAST:event_btnClearActionPerformed

    private void cmbTTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbTTypeActionPerformed
        if (cmbTType.getSelectedIndex() > 0) {
            setSubmitNeeded(true);
        }
}//GEN-LAST:event_cmbTTypeActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane JScrollPane;
    private javax.swing.JButton btnApplyCredit;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnSubmit;
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
    private javax.swing.JButton jButton5;
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
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblNoOfInvoice;
    private javax.swing.JLabel lblTOutRefund;
    private javax.swing.JLabel lblTotalCredit;
    private javax.swing.JLabel lblTotalDue;
    private javax.swing.JLabel lblTotalInvAmount;
    private javax.swing.JLabel lblTotalReceived;
    private javax.swing.JLabel lblTotalRecord1;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JSplitPane splitPanelMaster;
    private javax.swing.JLabel statusMessageLabel;
    private org.jdesktop.swingx.JXTable tblInvoiceForCol;
    private javax.swing.JTextArea txtContactableDetails;
    private javax.swing.JTextField txtTAmount;
    private javax.swing.JTextField txtTRef;
    // End of variables declaration//GEN-END:variables

    @Action
    public void viewInvoice() {
        int row = tblInvoiceForCol.getSelectedRow();        
        if (row != -1) {
            try {
                long acDocId = this.invoicesForCollection.get(row).getAcDocId();
                Thread t = new Thread(new threadLoadCompleteInvoice(acDocId));
                t.start();
                t.join();
                {
                    DlgTInvoice frameInvoice = new DlgTInvoice(this);
                    if (frameInvoice.showDialog(acDoc)) {
                        frameInvoice.dispose();
                    }
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(FrameBatchTransaction.class.getName()).log(Level.SEVERE, null, ex);
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
            acDocBo.findAcDocById(acDocId);
            acDoc = acDocBo.getAccountingDocument();

            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            progressBar.repaint(); //Refresh graphics
        }
    }

    private class threadOutstandingInvoiceReport implements Runnable {

        public threadOutstandingInvoiceReport() {
        }

        public void run() {
            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();

            statusMessageLabel.setText("Loading Invoice...");
            btnSearch.setEnabled(false);
            searchOutstandingInvoice();
            btnSearch.setEnabled(true);
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            progressBar.repaint(); //Refresh graphics
        }
    }

    private class threadOutstandingAgents implements Runnable {

        public threadOutstandingAgents() {
        }

        public void run() {
            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();

            statusMessageLabel.setText("Loading Agents...");
            
            outstandingAgents();
            
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            progressBar.repaint(); //Refresh graphics
        }
    }
}
