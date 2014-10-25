/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DlgUnBalancedAcDoc.java
 *
 * Created on 18-May-2011, 11:36:34
 */
package etsbackoffice.gui;

import etsbackoffice.ETSBackofficeApp;
import etsbackoffice.businesslogic.*;
import etsbackoffice.domain.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Yusuf
 */
public class DlgApplyPCredit extends javax.swing.JDialog implements ActionListener{

    private boolean save;
    private DefaultTableModel invoiceModel;
    private DateFormat df = new DateFormat();
    
    private AccountsBo accountsBo = (AccountsBo) ETSBackofficeApp.getApplication().ctx.getBean("accountsBo");
    private AcTransactionBo acTransactionBo = (AcTransactionBo) ETSBackofficeApp.getApplication().ctx.getBean("acTransactionBo");
    private AccountingDocBo acDocBo = (AccountingDocBo) ETSBackofficeApp.getApplication().ctx.getBean("acDocBo");
    private List<PurchaseAccountingDocument> outstandingRefunds = new ArrayList<PurchaseAccountingDocument>();
    private PurchaseAccountingDocument acDoc;
    private BigDecimal tOutRefund = new BigDecimal("0.00");
    private BigDecimal balDueInThisInvoice = new BigDecimal("0.00");

    /** Creates new form DlgUnBalancedAcDoc */
    public DlgApplyPCredit(java.awt.Frame parent)  {
        super(parent, "Apply Credit", true);
        initComponents();
        btnDone.addActionListener(this);
    }

    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();
        if (source == btnDone) {
            save = true;
            setVisible(false);
        }
    }

    private void populateInvoice() {
        this.balDueInThisInvoice = this.acDoc.getDueAmountWithRelatedDoc();
        lblAcDocRef.setText(this.acDoc.getVendorRef());
        lblIssueDate.setText(df.dateForGui(this.acDoc.getIssueDate()));
        lblVendor.setText(this.acDoc.getAcDocFrom());

        lblAcDocAmount.setText(this.acDoc.getTotalDocumentedAmount().toString());
        lblDueAmount.setText(this.acDoc.getDueAmountWithRelatedDoc().toString());
        lblRfdApplied.setText("0.00");
        lblBalDueToThisInv.setText(this.acDoc.getDueAmountWithRelatedDoc().toString());
    }

    private void populateTblInvoice() {

        int row = 0;
        invoiceModel = (DefaultTableModel) tblInvoice.getModel();
        invoiceModel.getDataVector().removeAllElements();
        tblInvoice.repaint();

        for (PurchaseAccountingDocument a : outstandingRefunds) {

            invoiceModel.insertRow(row, new Object[]{a.getVendorRef(), a.getIssueDate(), a.getPnr().getServicingCareer().getCode(),
                        a.getDueAmountWithRelatedDoc(), null, null, false});
            row++;
        }

        invoiceModel.addTableModelListener(new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    int row = e.getLastRow();
                    int column = e.getColumn();

                    if (column == 6) {
                        if (tblInvoice.getValueAt(row, 6).equals(true)) {
                            calculateCurrentRow(row, column);
                        } else {
                            tblInvoice.getModel().setValueAt(null, row, 4);
                            tblInvoice.getModel().setValueAt(null, row, 5);
                            calculateCurrentRow(row, column);
                        }
                    }
                }
            }
        });
    }

    private void calculateCurrentRow(int row, int col) {

        BigDecimal avaiableRfd = new BigDecimal("0.00");
        BigDecimal rfdTaken = new BigDecimal("0.00");
        BigDecimal remainingRfd = new BigDecimal("0.00");
        BigDecimal tRfdTaken = new BigDecimal("0.00");

        avaiableRfd = outstandingRefunds.get(row).getDueAmountWithRelatedDoc().negate();
        Object val = tblInvoice.getValueAt(row, 6);

        if (val.equals(true)) {
            if (balDueInThisInvoice.compareTo(avaiableRfd) >= 0) {
                rfdTaken = avaiableRfd;
                remainingRfd = avaiableRfd.subtract(rfdTaken);
            } else {
                rfdTaken = balDueInThisInvoice;
                remainingRfd = avaiableRfd.subtract(rfdTaken);
            }

            tblInvoice.getModel().setValueAt(rfdTaken, row, 4);
            tblInvoice.getModel().setValueAt(remainingRfd, row, 5);
        }
        for (int i = 0; i < tblInvoice.getRowCount(); i++) {
            Object val1 = tblInvoice.getValueAt(i, 6);
            if (val1.equals(true)) {
                tRfdTaken = tRfdTaken.add((BigDecimal) tblInvoice.getValueAt(i, 4));
            }
        }

        balDueInThisInvoice = this.acDoc.getDueAmountWithRelatedDoc().subtract(tRfdTaken);
        lblRfdApplied.setText(tRfdTaken.toString());
        lblBalDueToThisInv.setText(balDueInThisInvoice.toString());
    }

    public boolean showDlgApplyCredit(PurchaseAccountingDocument invoice) {
        this.acDoc = invoice;
        populateInvoice();
        Thread t = new Thread(new threadOutstandingRefundCompact());
        t.start();

        save = false;
        setLocationRelativeTo(this);
        setVisible(true);

        if (save) {
            submitTransactoin();
            dispose();
        }
        return save;
    }
    
        public List<BillingTransaction> allocateRefund(PurchaseAccountingDocument invoice, BigDecimal totalAmount) {
        List<BillingTransaction> acts = new ArrayList();
        BigDecimal remainingAmount = totalAmount;

        for (PurchaseAccountingDocument currentCNote : invoice.getRelatedDocuments()) {
            BigDecimal outstandingAmount = currentCNote.getOutstandingAmount();
            BillingTransaction t = new BillingTransaction();

            if (remainingAmount.compareTo(outstandingAmount) < 0) {
                t.setTransDate(new java.util.Date());
                t.setTransAmount(outstandingAmount.negate());
                t.setPurchaseAccountingDocument(currentCNote);
                t.setInvoice(invoice);
                t.setTransType(7);//Ac Adjustment
                t.setPnr(currentCNote.getPnr());
                t.setUser(AuthenticationBo.getLoggedOnUser());
                t.setTransRef("Credit transfer from : " + invoice.getVendorRef() + " to " + this.acDoc.getVendorRef());
                t.addAcStatement(accountsBo.newAccountsTransactionFromPAcTransaction(t));
                acts.add(t);
                currentCNote.applyTransToThisAcDoc(t);
                remainingAmount = remainingAmount.subtract(outstandingAmount);
            } else if (remainingAmount.compareTo(outstandingAmount) >= 0) {
                t.setTransDate(new java.util.Date());
                t.setTransAmount(remainingAmount.negate());
                t.setTransType(7);//Ac Adjustment
                t.setPnr(currentCNote.getPnr());
                t.setUser(AuthenticationBo.getLoggedOnUser());
                t.setTransRef("Credit transfer from : " + invoice.getVendorRef() + " to " + this.acDoc.getVendorRef());
                t.addAcStatement(accountsBo.newAccountsTransactionFromPAcTransaction(t));
                t.setPurchaseAccountingDocument(currentCNote);
                t.setInvoice(invoice);
                acts.add(t);
                currentCNote.applyTransToThisAcDoc(t);
                remainingAmount = remainingAmount.subtract(remainingAmount);
            }
        }
        return acts;
    }
    public List<BillingTransaction> applyCredit(List<BillingTransaction> acts) {
        List<BillingTransaction> newTrans = new ArrayList();

        for (BillingTransaction t : acts) {
            BillingTransaction newT = new BillingTransaction();
            newT.setTransDate(t.getTransDate());
            newT.setPnr(this.acDoc.getPnr());            
            newT.setUser(AuthenticationBo.getLoggedOnUser());
            newT.setPurchaseAccountingDocument(this.acDoc);
            newT.setInvoice(this.acDoc);
            newT.setTransType(8);//Ac Adjustment
            newT.setTransRef(t.getTransRef());
            newT.addAcStatement(accountsBo.newAccountsTransactionFromPAcTransaction(newT));
            if (t.getTransAmount().signum() < 0) {
                newT.setTransAmount(t.getTransAmount().negate());
            } else {
                newT.setTransAmount(t.getTransAmount());
            }           
            newTrans.add(newT);
        }
        return newTrans;
    }
    
    public void submitTransactoin() {
        List<PurchaseAccountingDocument> acDocs = new ArrayList();
        List<BillingTransaction> trans = new ArrayList();
        
        for (int row = 0; row < tblInvoice.getRowCount(); row++) {
            Object val = tblInvoice.getValueAt(row, 6);
            if (val.equals(true)) {
                trans.addAll(allocateRefund(this.outstandingRefunds.get(row), new BigDecimal(tblInvoice.getValueAt(row, 4).toString())));                                               
            }
        }
        trans.addAll(applyCredit(trans));        
        acTransactionBo.saveBulkBTransaction(trans);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblAcDocRef = new javax.swing.JLabel();
        lblIssueDate = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        lblAcDocAmount = new javax.swing.JLabel();
        lblDueAmount = new javax.swing.JLabel();
        lblRfdApplied = new javax.swing.JLabel();
        lblVendor = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lblBalDueToThisInv = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        btnClear = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        tblInvoice = new org.jdesktop.swingx.JXTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        btnDone = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        jPanel1.setName("jPanel1"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getResourceMap(DlgApplyPCredit.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setBackground(resourceMap.getColor("Form.background")); // NOI18N
        setName("Form"); // NOI18N

        jPanel2.setBackground(resourceMap.getColor("jPanel2.background")); // NOI18N
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Invoice", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, resourceMap.getFont("jPanel2.border.titleFont"))); // NOI18N
        jPanel2.setName("jPanel2"); // NOI18N

        jLabel1.setFont(resourceMap.getFont("jLabel6.font")); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setFont(resourceMap.getFont("jLabel6.font")); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        lblAcDocRef.setFont(resourceMap.getFont("jLabel6.font")); // NOI18N
        lblAcDocRef.setText(resourceMap.getString("lblAcDocRef.text")); // NOI18N
        lblAcDocRef.setName("lblAcDocRef"); // NOI18N

        lblIssueDate.setFont(resourceMap.getFont("jLabel6.font")); // NOI18N
        lblIssueDate.setText(resourceMap.getString("lblIssueDate.text")); // NOI18N
        lblIssueDate.setName("lblIssueDate"); // NOI18N

        jLabel5.setFont(resourceMap.getFont("jLabel6.font")); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        jLabel6.setFont(resourceMap.getFont("jLabel6.font")); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        jLabel7.setFont(resourceMap.getFont("jLabel6.font")); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        jLabel8.setFont(resourceMap.getFont("jLabel6.font")); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N

        lblAcDocAmount.setFont(resourceMap.getFont("jLabel6.font")); // NOI18N
        lblAcDocAmount.setText(resourceMap.getString("lblAcDocAmount.text")); // NOI18N
        lblAcDocAmount.setName("lblAcDocAmount"); // NOI18N

        lblDueAmount.setFont(resourceMap.getFont("jLabel6.font")); // NOI18N
        lblDueAmount.setText(resourceMap.getString("lblDueAmount.text")); // NOI18N
        lblDueAmount.setName("lblDueAmount"); // NOI18N

        lblRfdApplied.setFont(resourceMap.getFont("jLabel6.font")); // NOI18N
        lblRfdApplied.setText(resourceMap.getString("lblRfdApplied.text")); // NOI18N
        lblRfdApplied.setName("lblRfdApplied"); // NOI18N

        lblVendor.setFont(resourceMap.getFont("jLabel6.font")); // NOI18N
        lblVendor.setText(resourceMap.getString("lblVendor.text")); // NOI18N
        lblVendor.setName("lblVendor"); // NOI18N

        jLabel3.setFont(resourceMap.getFont("jLabel3.font")); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        lblBalDueToThisInv.setFont(resourceMap.getFont("lblBalDueToThisInv.font")); // NOI18N
        lblBalDueToThisInv.setText(resourceMap.getString("lblBalDueToThisInv.text")); // NOI18N
        lblBalDueToThisInv.setName("lblBalDueToThisInv"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblAcDocRef, javax.swing.GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE)
                    .addComponent(lblIssueDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblVendor, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 42, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(lblBalDueToThisInv, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblDueAmount, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblRfdApplied, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblAcDocAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblAcDocRef)
                    .addComponent(jLabel6)
                    .addComponent(jLabel1)
                    .addComponent(lblAcDocAmount))
                .addGap(4, 4, 4)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(lblIssueDate))
                        .addGap(4, 4, 4)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(lblVendor)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(lblDueAmount))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(lblRfdApplied))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblBalDueToThisInv)
                    .addComponent(jLabel3))
                .addContainerGap(32, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, resourceMap.getString("jPanel3.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, resourceMap.getFont("jPanel2.border.titleFont"))); // NOI18N
        jPanel3.setName("jPanel3"); // NOI18N

        btnClear.setFont(resourceMap.getFont("btnClear.font")); // NOI18N
        btnClear.setIcon(resourceMap.getIcon("btnClear.icon")); // NOI18N
        btnClear.setText(resourceMap.getString("btnClear.text")); // NOI18N
        btnClear.setName("btnClear"); // NOI18N
        btnClear.setPreferredSize(new java.awt.Dimension(100, 33));
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        jScrollPane6.setName("jScrollPane6"); // NOI18N

        tblInvoice.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "InvRef", "IssueDate", "Career", "AvailableRfd", "RfdTaken", "RemainingRfd", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, true
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
        jScrollPane6.setViewportView(tblInvoice);
        tblInvoice.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title0")); // NOI18N
        tblInvoice.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title1")); // NOI18N
        tblInvoice.getColumnModel().getColumn(2).setPreferredWidth(50);
        tblInvoice.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title2")); // NOI18N
        tblInvoice.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title3")); // NOI18N
        tblInvoice.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title4")); // NOI18N
        tblInvoice.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title5")); // NOI18N
        tblInvoice.getColumnModel().getColumn(6).setPreferredWidth(40);
        tblInvoice.getColumnModel().getColumn(6).setMaxWidth(60);
        tblInvoice.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title6")); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTextArea1.setBackground(resourceMap.getColor("jTextArea1.background")); // NOI18N
        jTextArea1.setColumns(20);
        jTextArea1.setEditable(false);
        jTextArea1.setFont(resourceMap.getFont("jTextArea1.font")); // NOI18N
        jTextArea1.setRows(5);
        jTextArea1.setText(resourceMap.getString("jTextArea1.text")); // NOI18N
        jTextArea1.setDisabledTextColor(resourceMap.getColor("jTextArea1.disabledTextColor")); // NOI18N
        jTextArea1.setEnabled(false);
        jTextArea1.setName("jTextArea1"); // NOI18N
        jScrollPane1.setViewportView(jTextArea1);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 448, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 561, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        btnDone.setFont(resourceMap.getFont("btnDone.font")); // NOI18N
        btnDone.setIcon(resourceMap.getIcon("btnDone.icon")); // NOI18N
        btnDone.setText(resourceMap.getString("btnDone.text")); // NOI18N
        btnDone.setActionCommand(resourceMap.getString("btnDone.actionCommand")); // NOI18N
        btnDone.setName("btnDone"); // NOI18N
        btnDone.setPreferredSize(new java.awt.Dimension(100, 33));
        btnDone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDoneActionPerformed(evt);
            }
        });

        btnCancel.setFont(resourceMap.getFont("btnCancel.font")); // NOI18N
        btnCancel.setIcon(resourceMap.getIcon("btnCancel.icon")); // NOI18N
        btnCancel.setText(resourceMap.getString("btnCancel.text")); // NOI18N
        btnCancel.setName("btnCancel"); // NOI18N
        btnCancel.setPreferredSize(new java.awt.Dimension(100, 33));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(352, 352, 352)
                .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addComponent(btnDone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDone, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        populateTblInvoice();
        populateInvoice();
    }//GEN-LAST:event_btnClearActionPerformed

    private void btnDoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDoneActionPerformed
      
    }//GEN-LAST:event_btnDoneActionPerformed
    /**
     * @param args the command line arguments
     */
    /*   public static void main(String args[]) {
    java.awt.EventQueue.invokeLater(new Runnable() {
    
    public void run() {
    DlgApplyCredit dialog = new DlgApplyCredit(, true);
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
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnDone;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel lblAcDocAmount;
    private javax.swing.JLabel lblAcDocRef;
    private javax.swing.JLabel lblBalDueToThisInv;
    private javax.swing.JLabel lblDueAmount;
    private javax.swing.JLabel lblIssueDate;
    private javax.swing.JLabel lblRfdApplied;
    private javax.swing.JLabel lblVendor;
    private org.jdesktop.swingx.JXTable tblInvoice;
    // End of variables declaration//GEN-END:variables
    private class threadOutstandingRefundCompact implements Runnable {

        public threadOutstandingRefundCompact() {
        }

        public void run() {
            outstandingRefunds = acDocBo.outstandingPCNoteByCriteria(acDoc.getPnr().getContactableIdTktingAgt(), null, null);
            populateTblInvoice();
        }
    }
}
