package etsbackoffice.gui;

import etsbackoffice.ETSBackofficeApp;
import etsbackoffice.businesslogic.*;
import etsbackoffice.domain.*;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextAttribute;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import org.jdesktop.application.Action;
import org.jdesktop.swingx.JXTable;

/**
 *
 * @author Yusuf
 */
public class DlgTransaction extends javax.swing.JDialog {

    private boolean save;
    private boolean submitNeeded;
    private AccountingDocument invoice;
    private List<AccountingDocument> acDocs = new ArrayList();
    private List<AcTransaction> transactions = new ArrayList<AcTransaction>();
    private AccountingDocBo acDocBo = (AccountingDocBo) ETSBackofficeApp.getApplication().ctx.getBean("acDocBo");
    private AcTransactionBo acTransactionBo = (AcTransactionBo) ETSBackofficeApp.getApplication().ctx.getBean("acTransactionBo");
    private AccountsBo accountsBo = (AccountsBo) ETSBackofficeApp.getApplication().ctx.getBean("accountsBo");
    private DateFormat df = new DateFormat();
    private BigDecimal acBalance = new BigDecimal("0.00");
    DefaultTableModel transModel;
    private boolean saveNeeded;
    /** Creates new form FrameTransaction */
    public DlgTransaction(java.awt.Frame parent) {
        super(parent, "Trasaction Wizard", true);
        initComponents();
        populateCmbTType();
        txtTAmount.setDocument(new CheckInput(CheckInput.FLOAT));
        initTblTransaction();
    }

        private void actionRole() {        
        if (AuthenticationBo.getLoggedOnUser().getUserRole() != null && AuthenticationBo.getLoggedOnUser().getUserRole().isAdministrator()) {
            btnDeleteTrans.setEnabled(true);
            btnVoidTrans.setEnabled(true);            
        } else {
            btnDeleteTrans.setEnabled(false);
            btnVoidTrans.setEnabled(false);
        }
    }
        
    private void initTblTransaction() {
        
        TableColumn tType, amount;
        tType = tblTransaction.getColumnModel().getColumn(1);        

        JTextField jtf = new JTextField();
        jtf.setDocument(new CheckInput(CheckInput.FLOAT));        

        JComboBox cmbTTypeInTable = new JComboBox();  
        DefaultComboBoxModel tTypeModel1 = new DefaultComboBoxModel(Enums.TransType.values());
        cmbTTypeInTable.setModel(tTypeModel1);
        tType.setCellEditor(new DefaultCellEditor(cmbTTypeInTable));
    }
    
    private void populateCmbTType() {
        DefaultComboBoxModel tTypeModel = new DefaultComboBoxModel(Enums.TransType.values());
        cmbTType.setModel(tTypeModel);
        cmbTType.insertItemAt("Select", 0);
        cmbTType.setSelectedIndex(0);
    }

    private void setSubmitNeeded(boolean submitNeeded) {
        if (submitNeeded != this.submitNeeded) {
            this.submitNeeded = submitNeeded;
        }
        if (this.submitNeeded == true) {
            btnSubmit.setEnabled(true);
        } else {
            btnSubmit.setEnabled(false);
        }
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
    
    private void enableTransControll() {
        lblTType.setEnabled(true);
        lblTAmount.setEnabled(true);
        lblTRef.setEnabled(true);
        cmbTType.setEnabled(true);
        //btnSubmit.setEnabled(true);
    }

    private void disableTransControll() {
        lblTType.setEnabled(false);
        lblTAmount.setEnabled(false);
        lblTRef.setEnabled(false);
        txtTAmount.setEditable(false);
        txtTRef.setEditable(false);
        cmbTType.setEnabled(false);
        btnSubmit.setEnabled(false);
    }

    private void resetTransControll() {
        txtTAmount.setText("");
        txtTRef.setText("");
        cmbTType.setSelectedIndex(0);
    }

    @Action
    public void closeTransaction() {
        dispose();
    }

    public boolean showTransactionDialog(List<AccountingDocument> acDocs) {
        this.acDocs = acDocs;

        populateCmbInvoice();

        save = false;
        setLocationRelativeTo(this);
        setVisible(true);

        if (save) {
        }
        return save;
    }

    private void populateTblAcDoc() {
        DefaultTableModel acDocModel = (DefaultTableModel) tblAcDoc.getModel();
        acDocModel.getDataVector().removeAllElements();
        tblAcDoc.repaint();

        Iterator it = this.acDocs.listIterator();
        int row = 0;
        while (it.hasNext()) {
            AccountingDocument acDoc = (AccountingDocument) it.next();
            if (acDoc.getAcDoctype() == 1) {
                this.invoice = acDoc;
            }
            acDocModel.insertRow(row, new Object[]{acDoc.getAcDocTypeString(), acDoc.getAcDocRef(),
                        acDoc.getIssueDate(),
                        acDoc.getTerms(), acDoc.getTotalDocumentedAmount(), acDoc.getTotalTransactionAmount()});
            row++;
        }
    }

    private void populateCmbInvoice() {
        DefaultComboBoxModel cmbInvoiceModel = new DefaultComboBoxModel();
        for (AccountingDocument acDoc : this.acDocs) {
            if (acDoc.getAcDoctype() == 1) {
                cmbInvoiceModel.addElement(acDoc.getAcDocRef());
            }
        }
        cmbInvoice.setModel(cmbInvoiceModel);
        cmbInvoice.setSelectedIndex(0);
    }

    private void populateTblTransaction() {
        transModel = (DefaultTableModel) tblTransaction.getModel();
        transModel.getDataVector().removeAllElements();
        tblTransaction.repaint();

        for (AccountingDocument currentAcDoc : this.acDocs) {
            transactions.addAll(currentAcDoc.getAcTransactions());
        }

        Iterator it = this.transactions.listIterator();
        int row = 0;
        while (it.hasNext()) {
            AcTransaction t = (AcTransaction) it.next();
            transModel.insertRow(row, new Object[]{df.dateForGui(t.getTransDate()), Enums.TransType.valueOf(t.getTransType()),
                        t.getTransAmount(), t.getTransRef(), (t.isActive() == true) ? "LIVE" : "VOID"});
            row++;
        }
        
        transModel.addTableModelListener(new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    int row = e.getLastRow();
                    int column = e.getColumn();

                    if (column == 0) {
                    }
                    if (column == 1) {                      
                      transactions.get(row).
                      setTransType(Enums.TransType.valueOf(tblTransaction.getValueAt(row, column).toString()).getId());
                      setSaveNeeded(true);
                    }
                    if (column == 2) {
                        BigDecimal newAmount = new BigDecimal("0.00");
                        String val = tblTransaction.getValueAt(row, column).toString().replaceAll("[^.0-9]", "");
                        if (!val.isEmpty()) {
                            newAmount = new BigDecimal(val);
                            transactions.get(row).setTransAmount(newAmount);
                            setSaveNeeded(true);
                        }
                    }
                    if (column == 3) {
                        String val = tblTransaction.getValueAt(row, column).toString().trim();
                        if(!val.isEmpty()){
                        transactions.get(row).setTransRef(tblTransaction.getValueAt(row, column).toString());
                        setSaveNeeded(true);
                        }
                    }
                }
            }
        });
    }

    private void populateMessage() {
        if (transactionTypeNeeded() == 1) {
            enableTransControll();
            txtMessage.setText(netOutstandingToThisInvoice() + "\n" + "payment collection needed.");
        } else if (transactionTypeNeeded() == 2) {
            enableTransControll();
            txtMessage.setText(netOutstandingToThisInvoice() + "\n" + "refund needed");
        } else if (transactionTypeNeeded() == 0) {
            disableTransControll();
            txtMessage.setText("No transaction required to this invoice ");
        }
    }

    public int transactionTypeNeeded() {
        int type = 0;//0:NoTransaction,1-Payment,2-Refund
        if (netOutstandingToThisInvoice().compareTo(new BigDecimal("0.00")) == 1) {
            type = 1;
        } else if (netOutstandingToThisInvoice().compareTo(new BigDecimal("0.00")) == -1) {
            type = 2;
        } else if (netOutstandingToThisInvoice().compareTo(new BigDecimal("0.00")) == 0) {
            type = 0;
        }
        return type;
    }

    private BigDecimal netInvAmount() {
        BigDecimal acDocTotal = new BigDecimal("0.00");

        for (AccountingDocument currentAcDoc : this.acDocs) {
            acDocTotal = acDocTotal.add(currentAcDoc.getTotalDocumentedAmount());
        }
        return acDocTotal;
    }

    private BigDecimal netTransBallance() {
        BigDecimal transTotal = new BigDecimal("0.00");
        for (AccountingDocument currentAcDoc : this.acDocs) {
            transTotal = transTotal.add(currentAcDoc.getTotalTransactionAmount());

        }
        return transTotal;
    }

    private BigDecimal netOutstandingToThisInvoice() {
        return netInvAmount().subtract(netTransBallance());
    }

    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();
        if (source == btnVoidTrans) {
            save = true;
            setVisible(false);

        } else {
            dispose();
        }
    }

    public List<AcTransaction> allocateRefund(BigDecimal totalAmount) {
        List<AcTransaction> acts = new ArrayList();
        BigDecimal remainingAmount = totalAmount;


        for (AccountingDocument currentAcDoc : this.acDocs) {
            if (remainingAmount.compareTo(new BigDecimal("0.00")) == -1) {
                //if (currentAcDoc.getAcDoctype() == 2 || currentAcDoc.getAcDoctype() == 3 || (currentAcDoc.getAcDoctype() == 1 && currentAcDoc.isActive() == false)) {
                    if (currentAcDoc.getTotalDocumentedAmount().compareTo(currentAcDoc.getTotalTransactionAmount()) == -1) {
                        BigDecimal outstandingAmount = currentAcDoc.getTotalDocumentedAmount().add(currentAcDoc.getTotalTransactionAmount());
                        AcTransaction act = new AcTransaction();

                        if (outstandingAmount.compareTo(totalAmount) <= 0) {
                            act.setTransAmount(remainingAmount);
                            act.setAccountingDocument(currentAcDoc);
                            act.setInvoice(this.invoice);
                            acts.add(act);
                            remainingAmount = totalAmount.subtract(outstandingAmount);
                        } else if (outstandingAmount.compareTo(totalAmount) >= 0) {
                            act.setTransAmount(totalAmount);
                            act.setAccountingDocument(currentAcDoc);
                            act.setInvoice(this.invoice);
                            acts.add(act);
                            remainingAmount = totalAmount.add(outstandingAmount);
                        }
                    }
                //}
            }
        }
        return acts;
    }

    @Action
    public void submitTransaction() {
        if (this.submitNeeded = true) {
            //int type = cmbTType.getSelectedIndex();
            BigDecimal amount = new BigDecimal(txtTAmount.getText());
            String tRef = txtTRef.getText();
            if (transactionTypeNeeded() == 1) {
                if (amount.compareTo(netOutstandingToThisInvoice()) <= 0) {
                    AcTransaction t = new AcTransaction();
                    t.setTransType(Enums.TransType.valueOf(cmbTType.getSelectedItem().toString()).getId());
                    t.setTransAmount(amount);
                    t.setTransDate(new java.util.Date());
                    t.setTransRef(tRef);
                    t.setInvoice(this.invoice);
                    t.setUser(AuthenticationBo.getLoggedOnUser());
                    t.setAccountingDocument(this.invoice);
                    t.setPnr(this.invoice.getPnr());
                    t.setActive(true);
                    t.addAcStatement(accountsBo.newAccountsTransactionFromSAcTransaction(t));                    
                    acTransactionBo.setAcTrans(t);
                    acTransactionBo.saveACTransaction();
                    setSubmitNeeded(false);
                    resetTransControll();
                    int invRef = Integer.parseInt(cmbInvoice.getSelectedItem().toString());
                    new Thread(new threadLoadFrameObjects(invRef)).start();
                } else {
                    JOptionPane.showMessageDialog(null, "Payment can not be more then invoice amount", "Transaction", JOptionPane.WARNING_MESSAGE);
                }
            } else if (transactionTypeNeeded() == 2) {
                amount = amount.negate();

                if (amount.compareTo(netOutstandingToThisInvoice()) >= 0) {
                    List<AcTransaction> acts = allocateRefund(amount);
                    for (AcTransaction t : acts) {
                        t.setTransType(Enums.TransType.valueOf(cmbTType.getSelectedItem().toString()).getId());
                        t.setTransDate(new java.util.Date());

                        if (t.getTransType() == 11) {
                            tRef = tRef.concat(" Credited client account.");
                        }
                        t.setTransRef(tRef);
                        t.setUser(AuthenticationBo.getLoggedOnUser());
                        t.setPnr(this.invoice.getPnr());
                        t.setActive(true);
                        t.addAcStatement(accountsBo.newAccountsTransactionFromSAcTransaction(t));
                        acTransactionBo.setAcTrans(t);
                        acTransactionBo.saveACTransaction();
                        setSubmitNeeded(false);
                        resetTransControll();
                        int invRef = Integer.parseInt(cmbInvoice.getSelectedItem().toString());
                        new Thread(new threadLoadFrameObjects(invRef)).start();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Payment can not be more then Credit Amount", "Transaction", JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }

    private void deleteTransaction() {
        int row = tblTransaction.getSelectedRow();
        if (row != -1) {
            busyIcon.setText("Please wait while deleting transaction...");
            busyIcon.setBusy(true);
            acTransactionBo.deleteAcTransaction(this.transactions.get(row));
            busyIcon.setText("Transaction deleted...Refreshing data...");
            int invRef = Integer.parseInt(cmbInvoice.getSelectedItem().toString());
            new Thread(new threadLoadFrameObjects(invRef)).start();
            busyIcon.setText("");
            busyIcon.setBusy(false);
        } else {
            JOptionPane.showMessageDialog(null, "Select Transaction", "Delete Transaction", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void voidTransaction() {
        int row = tblTransaction.getSelectedRow();
        if (row != -1) {
            busyIcon.setText("Please wait while voiding transaction...");
            busyIcon.setBusy(true);

            acTransactionBo.voidAcTransaction(this.transactions.get(row));
            busyIcon.setText("Transaction Void...Refreshing data...");
            int invRef = Integer.parseInt(cmbInvoice.getSelectedItem().toString());
            new Thread(new threadLoadFrameObjects(invRef)).start();
            busyIcon.setText("");
            busyIcon.setBusy(false);
        } else {
            JOptionPane.showMessageDialog(null, "Select Transaction", "Delete Transaction", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void populateFrameObjects() {
        transactions.clear();
        populateTblAcDoc();
        populateTblTransaction();
        populateMessage();
         setSaveNeeded(false);
        lblNetInvAmount.setText(netInvAmount().toString());
        lblTotalReceived.setText(netTransBallance().toString());
        lblOutstanding.setText(netOutstandingToThisInvoice().toString());
        actionRole();
    }
    private ActionListener cmbInvoiceListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            int invRef = Integer.parseInt(cmbInvoice.getSelectedItem().toString());
            new Thread(new threadLoadFrameObjects(invRef)).start();
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

        jPanel1 = new javax.swing.JPanel();
        lblTType = new javax.swing.JLabel();
        lblTAmount = new javax.swing.JLabel();
        lblTRef = new javax.swing.JLabel();
        cmbTType = new javax.swing.JComboBox();
        txtTAmount = new javax.swing.JTextField();
        txtTRef = new javax.swing.JTextField();
        jSeparator5 = new javax.swing.JSeparator();
        busyIcon = new org.jdesktop.swingx.JXBusyLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTransaction = new JXTable(){public Component prepareRenderer(TableCellRenderer renderer,
            int rowIndex, int vColIndex) {
            Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);                     
            String s = this.getModel().getValueAt(rowIndex, 4).toString();       

            if(s.equalsIgnoreCase("VOID")){
                //c.setForeground(Color.ORANGE);
                Map  attributes = c.getFont().getAttributes();
                attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
                Font newFont = new Font(attributes);
                c.setFont(newFont);
            }
            return c;
        }
    };
    btnSubmit = new javax.swing.JButton();
    jScrollPane2 = new javax.swing.JScrollPane();
    tblAcDoc = new org.jdesktop.swingx.JXTable();
    jToolBar1 = new javax.swing.JToolBar();
    btnVoidTrans = new javax.swing.JButton();
    btnDeleteTrans = new javax.swing.JButton();
    btnSave = new javax.swing.JButton();
    jButton2 = new javax.swing.JButton();
    jLabel1 = new javax.swing.JLabel();
    cmbInvoice = new javax.swing.JComboBox();
    cmbInvoice.addActionListener(cmbInvoiceListener);
    busyIcon1 = new org.jdesktop.swingx.JXBusyLabel();
    jLabel2 = new javax.swing.JLabel();
    lblNetInvAmount = new javax.swing.JLabel();
    jLabel4 = new javax.swing.JLabel();
    jLabel5 = new javax.swing.JLabel();
    lblTotalReceived = new javax.swing.JLabel();
    lblOutstanding = new javax.swing.JLabel();
    jSeparator1 = new javax.swing.JSeparator();
    jScrollPane3 = new javax.swing.JScrollPane();
    txtMessage = new javax.swing.JTextArea();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setName("Form"); // NOI18N
    setResizable(false);

    org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getResourceMap(DlgTransaction.class);
    jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, resourceMap.getString("jPanel1.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, resourceMap.getFont("jPanel1.border.titleFont"))); // NOI18N
    jPanel1.setName("jPanel1"); // NOI18N
    jPanel1.setLayout(new java.awt.GridBagLayout());

    lblTType.setFont(resourceMap.getFont("lblTAmount.font")); // NOI18N
    lblTType.setText(resourceMap.getString("lblTType.text")); // NOI18N
    lblTType.setName("lblTType"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel1.add(lblTType, gridBagConstraints);

    lblTAmount.setFont(resourceMap.getFont("lblTAmount.font")); // NOI18N
    lblTAmount.setText(resourceMap.getString("lblTAmount.text")); // NOI18N
    lblTAmount.setName("lblTAmount"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel1.add(lblTAmount, gridBagConstraints);

    lblTRef.setFont(resourceMap.getFont("lblTAmount.font")); // NOI18N
    lblTRef.setText(resourceMap.getString("lblTRef.text")); // NOI18N
    lblTRef.setName("lblTRef"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel1.add(lblTRef, gridBagConstraints);

    cmbTType.setFont(resourceMap.getFont("lblTAmount.font")); // NOI18N
    cmbTType.setName("cmbTType"); // NOI18N
    cmbTType.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            cmbTTypeActionPerformed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.ipadx = 100;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel1.add(cmbTType, gridBagConstraints);

    txtTAmount.setEditable(false);
    txtTAmount.setFont(resourceMap.getFont("lblTAmount.font")); // NOI18N
    txtTAmount.setName("txtTAmount"); // NOI18N
    txtTAmount.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyReleased(java.awt.event.KeyEvent evt) {
            txtTAmountKeyReleased(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.ipadx = 100;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel1.add(txtTAmount, gridBagConstraints);

    txtTRef.setEditable(false);
    txtTRef.setFont(resourceMap.getFont("lblTAmount.font")); // NOI18N
    txtTRef.setName("txtTRef"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipadx = 300;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel1.add(txtTRef, gridBagConstraints);

    jSeparator5.setName("jSeparator5"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridwidth = 5;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipady = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel1.add(jSeparator5, gridBagConstraints);

    busyIcon.setFont(resourceMap.getFont("busyIcon.font")); // NOI18N
    busyIcon.setName("busyIcon"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
    jPanel1.add(busyIcon, gridBagConstraints);

    jScrollPane1.setName("jScrollPane1"); // NOI18N

    tblTransaction.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {

        },
        new String [] {
            "T.Date", "T.Type", "T.Amount", "T.Ref", "Status"
        }
    ) {
        boolean[] canEdit = new boolean [] {
            false, true, true, true, false
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
    tblTransaction.getColumnModel().getColumn(0).setPreferredWidth(100);
    tblTransaction.getColumnModel().getColumn(0).setMaxWidth(100);
    tblTransaction.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblTransaction.columnModel.title0")); // NOI18N
    tblTransaction.getColumnModel().getColumn(1).setMinWidth(60);
    tblTransaction.getColumnModel().getColumn(1).setPreferredWidth(100);
    tblTransaction.getColumnModel().getColumn(1).setMaxWidth(100);
    tblTransaction.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblTransaction.columnModel.title1")); // NOI18N
    tblTransaction.getColumnModel().getColumn(2).setMinWidth(60);
    tblTransaction.getColumnModel().getColumn(2).setPreferredWidth(100);
    tblTransaction.getColumnModel().getColumn(2).setMaxWidth(100);
    tblTransaction.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblTransaction.columnModel.title2")); // NOI18N
    tblTransaction.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblTransaction.columnModel.title3")); // NOI18N
    tblTransaction.getColumnModel().getColumn(4).setMinWidth(60);
    tblTransaction.getColumnModel().getColumn(4).setPreferredWidth(60);
    tblTransaction.getColumnModel().getColumn(4).setMaxWidth(60);
    tblTransaction.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("tblTransaction.columnModel.title4")); // NOI18N

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.gridwidth = 5;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel1.add(jScrollPane1, gridBagConstraints);

    javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getActionMap(DlgTransaction.class, this);
    btnSubmit.setAction(actionMap.get("submitTransaction")); // NOI18N
    btnSubmit.setFont(resourceMap.getFont("btnSubmit.font")); // NOI18N
    btnSubmit.setIcon(resourceMap.getIcon("btnSubmit.icon")); // NOI18N
    btnSubmit.setText(resourceMap.getString("btnSubmit.text")); // NOI18N
    btnSubmit.setName("btnSubmit"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 4;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    jPanel1.add(btnSubmit, gridBagConstraints);

    jScrollPane2.setName("jScrollPane2"); // NOI18N

    tblAcDoc.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {

        },
        new String [] {
            "Type", "RefNo", "IssueDate", "Terms", "DocAmount", "Received/Paid"
        }
    ) {
        boolean[] canEdit = new boolean [] {
            false, false, false, false, false, false
        };

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit [columnIndex];
        }
    });
    tblAcDoc.setFont(resourceMap.getFont("tblAcDoc.font")); // NOI18N
    tblAcDoc.setName("tblAcDoc"); // NOI18N
    tblAcDoc.setSortable(false);
    tblAcDoc.getTableHeader().setReorderingAllowed(false);
    jScrollPane2.setViewportView(tblAcDoc);
    tblAcDoc.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblAcDoc.columnModel.title0")); // NOI18N
    tblAcDoc.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblAcDoc.columnModel.title1")); // NOI18N
    tblAcDoc.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblAcDoc.columnModel.title2")); // NOI18N
    tblAcDoc.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblAcDoc.columnModel.title3")); // NOI18N
    tblAcDoc.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("tblAcDoc.columnModel.title4")); // NOI18N
    tblAcDoc.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("tblAcDoc.columnModel.title5")); // NOI18N

    jToolBar1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    jToolBar1.setRollover(true);
    jToolBar1.setName("jToolBar1"); // NOI18N

    btnVoidTrans.setFont(resourceMap.getFont("btnVoidTrans.font")); // NOI18N
    btnVoidTrans.setIcon(resourceMap.getIcon("btnVoidTrans.icon")); // NOI18N
    btnVoidTrans.setText(resourceMap.getString("btnVoidTrans.text")); // NOI18N
    btnVoidTrans.setFocusable(false);
    btnVoidTrans.setName("btnVoidTrans"); // NOI18N
    btnVoidTrans.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnVoidTransActionPerformed(evt);
        }
    });
    jToolBar1.add(btnVoidTrans);

    btnDeleteTrans.setFont(resourceMap.getFont("btnDeleteTrans.font")); // NOI18N
    btnDeleteTrans.setIcon(resourceMap.getIcon("btnDeleteTrans.icon")); // NOI18N
    btnDeleteTrans.setText(resourceMap.getString("btnDeleteTrans.text")); // NOI18N
    btnDeleteTrans.setFocusable(false);
    btnDeleteTrans.setName("btnDeleteTrans"); // NOI18N
    btnDeleteTrans.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnDeleteTransActionPerformed(evt);
        }
    });
    jToolBar1.add(btnDeleteTrans);

    btnSave.setFont(resourceMap.getFont("btnSave.font")); // NOI18N
    btnSave.setIcon(resourceMap.getIcon("btnSave.icon")); // NOI18N
    btnSave.setText(resourceMap.getString("btnSave.text")); // NOI18N
    btnSave.setEnabled(false);
    btnSave.setFocusable(false);
    btnSave.setName("btnSave"); // NOI18N
    btnSave.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnSaveActionPerformed(evt);
        }
    });
    jToolBar1.add(btnSave);

    jButton2.setFont(resourceMap.getFont("jButton2.font")); // NOI18N
    jButton2.setIcon(resourceMap.getIcon("jButton2.icon")); // NOI18N
    jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
    jButton2.setFocusable(false);
    jButton2.setName("jButton2"); // NOI18N
    jButton2.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton2ActionPerformed(evt);
        }
    });
    jToolBar1.add(jButton2);

    jLabel1.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
    jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
    jLabel1.setName("jLabel1"); // NOI18N

    cmbInvoice.setName("cmbInvoice"); // NOI18N

    busyIcon1.setText(resourceMap.getString("busyIcon1.text")); // NOI18N
    busyIcon1.setFont(resourceMap.getFont("busyIcon1.font")); // NOI18N
    busyIcon1.setName("busyIcon1"); // NOI18N

    jLabel2.setFont(resourceMap.getFont("lblOutstanding.font")); // NOI18N
    jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
    jLabel2.setName("jLabel2"); // NOI18N

    lblNetInvAmount.setFont(resourceMap.getFont("lblOutstanding.font")); // NOI18N
    lblNetInvAmount.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    lblNetInvAmount.setText(resourceMap.getString("lblNetInvAmount.text")); // NOI18N
    lblNetInvAmount.setName("lblNetInvAmount"); // NOI18N

    jLabel4.setFont(resourceMap.getFont("lblOutstanding.font")); // NOI18N
    jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
    jLabel4.setName("jLabel4"); // NOI18N

    jLabel5.setFont(resourceMap.getFont("lblOutstanding.font")); // NOI18N
    jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
    jLabel5.setName("jLabel5"); // NOI18N

    lblTotalReceived.setFont(resourceMap.getFont("lblOutstanding.font")); // NOI18N
    lblTotalReceived.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    lblTotalReceived.setText(resourceMap.getString("lblTotalReceived.text")); // NOI18N
    lblTotalReceived.setName("lblTotalReceived"); // NOI18N

    lblOutstanding.setFont(resourceMap.getFont("lblOutstanding.font")); // NOI18N
    lblOutstanding.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    lblOutstanding.setText(resourceMap.getString("lblOutstanding.text")); // NOI18N
    lblOutstanding.setName("lblOutstanding"); // NOI18N

    jSeparator1.setName("jSeparator1"); // NOI18N

    jScrollPane3.setName("jScrollPane3"); // NOI18N

    txtMessage.setColumns(20);
    txtMessage.setEditable(false);
    txtMessage.setFont(resourceMap.getFont("txtMessage.font")); // NOI18N
    txtMessage.setLineWrap(true);
    txtMessage.setRows(5);
    txtMessage.setName("txtMessage"); // NOI18N
    jScrollPane3.setViewportView(txtMessage);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addGap(4, 4, 4)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 747, Short.MAX_VALUE)
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                        .addComponent(cmbInvoice, javax.swing.GroupLayout.Alignment.TRAILING, 0, 255, Short.MAX_VALUE)
                        .addComponent(busyIcon1, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(273, 273, 273)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel5)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(lblOutstanding, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel2)
                                    .addGap(4, 4, 4)
                                    .addComponent(lblNetInvAmount, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE))
                                .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel4)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(lblTotalReceived, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE))))
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 488, javax.swing.GroupLayout.PREFERRED_SIZE))))
            .addContainerGap())
        .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 761, Short.MAX_VALUE)
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(6, 6, 6)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(4, 4, 4)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblNetInvAmount)
                        .addComponent(jLabel2))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblTotalReceived)
                        .addComponent(jLabel4))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblOutstanding)
                        .addComponent(jLabel5)))
                .addGroup(layout.createSequentialGroup()
                    .addGap(4, 4, 4)
                    .addComponent(jLabel1)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(cmbInvoice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(busyIcon1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGap(4, 4, 4)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
    );

    pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmbTTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbTTypeActionPerformed
        if (cmbTType.getSelectedIndex() > 0) {
            txtTAmount.setEditable(true);
            txtTAmount.requestFocus();
            txtTRef.setEditable(true);
            //btnSubmit.setEnabled(true);
        } else {
            txtTAmount.setEditable(false);
            txtTAmount.requestFocus();
            txtTRef.setEditable(false);
            //btnSubmit.setEnabled(false);
        }
    }//GEN-LAST:event_cmbTTypeActionPerformed

    private void btnVoidTransActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVoidTransActionPerformed
        voidTransaction();
    }//GEN-LAST:event_btnVoidTransActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void txtTAmountKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTAmountKeyReleased
        String val = txtTAmount.getText();
        if (!val.isEmpty()) {
            btnSubmit.setEnabled(true);
        } else {
            btnSubmit.setEnabled(false);
        }
    }//GEN-LAST:event_txtTAmountKeyReleased

    private void btnDeleteTransActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteTransActionPerformed
        deleteTransaction();
    }//GEN-LAST:event_btnDeleteTransActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        Thread t = new Thread(new threadSaveTransaction());
        t.start();
        try {
            t.join();
            int invRef = Integer.parseInt(cmbInvoice.getSelectedItem().toString());
            new Thread(new threadLoadFrameObjects(invRef)).start();
        } catch (InterruptedException ex) {
            Logger.getLogger(DlgTransaction.class.getName()).log(Level.SEVERE, null, ex);
        }             
    }//GEN-LAST:event_btnSaveActionPerformed
    /**
     * @param args the command line arguments
     */
    /* public static void main(String args[]) {
    java.awt.EventQueue.invokeLater(new Runnable() {
    public void run() {
    FrameTransaction dialog = new FrameTransaction(new javax.swing.JFrame(), true);
    dialog.addWindowListener(new java.awt.event.WindowAdapter() {
    public void windowClosing(java.awt.event.WindowEvent e) {
    System.exit(0);
    }
    });
    dialog.setVisible(true);
    }
    });
    }*/
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDeleteTrans;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSubmit;
    private javax.swing.JButton btnVoidTrans;
    private org.jdesktop.swingx.JXBusyLabel busyIcon;
    private org.jdesktop.swingx.JXBusyLabel busyIcon1;
    private javax.swing.JComboBox cmbInvoice;
    private javax.swing.JComboBox cmbTType;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblNetInvAmount;
    private javax.swing.JLabel lblOutstanding;
    private javax.swing.JLabel lblTAmount;
    private javax.swing.JLabel lblTRef;
    private javax.swing.JLabel lblTType;
    private javax.swing.JLabel lblTotalReceived;
    private org.jdesktop.swingx.JXTable tblAcDoc;
    private org.jdesktop.swingx.JXTable tblTransaction;
    private javax.swing.JTextArea txtMessage;
    private javax.swing.JTextField txtTAmount;
    private javax.swing.JTextField txtTRef;
    // End of variables declaration//GEN-END:variables

    private class threadLoadFrameObjects implements Runnable {

        private int invRef;

        public threadLoadFrameObjects(int invRef) {
            this.invRef = invRef;
        }

        public void run() {

            busyIcon.setBusy(true);

            acDocs = acDocBo.findAcDocByRefNo(invRef);
            populateFrameObjects();

            //acBalance = accountsBo.finalClientAcBalance(invoice.getPnr().getContactableId());
            //lblAcBalance.setText("Accounts Balance : " + acBalance);
            busyIcon.setBusy(false);
        }
    }

    private class threadSubmitTransaction implements Runnable {

        private AcTransaction transaction;

        public threadSubmitTransaction(AcTransaction transaction) {
            this.transaction = transaction;
        }

        public void run() {
            busyIcon.setBusy(true);
            busyIcon.setText("Please wait...");

            acTransactionBo.setAcTrans(transaction);
            acTransactionBo.saveACTransaction();
            busyIcon.setBusy(false);
            busyIcon.setText("");
            cmbTType.setSelectedIndex(0);
            txtTRef.setText("");
            txtTAmount.setText("");
        }
    }
    
    public class threadSaveTransaction implements Runnable {

        public threadSaveTransaction() {
        }

        public void run() {
            busyIcon.setBusy(true);
            busyIcon.setText("Please wait...");
            
            acTransactionBo.saveBulkACTransaction(transactions);

            busyIcon.setBusy(false);
            busyIcon.setText("Transaction updated");
        }
    }
}
