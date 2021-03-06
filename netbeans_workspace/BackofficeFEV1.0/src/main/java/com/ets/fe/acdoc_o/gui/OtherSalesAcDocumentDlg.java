package com.ets.fe.acdoc_o.gui;

import com.ets.fe.acdoc.gui.SalesInvoiceDlg;
import com.ets.fe.acdoc.gui.comp.AcDocHeaderComponent;
import com.ets.fe.acdoc.model.TicketingSalesAcDoc;
import com.ets.fe.acdoc_o.model.OtherSalesAcDoc;
import com.ets.fe.acdoc_o.task.AccountingDocTaskOther;
import com.ets.fe.acdoc_o.task.NewOSalesDocumentTask;
import com.ets.fe.util.CheckInput;
import com.ets.fe.util.Enums;
import com.ets.fe.util.Enums.AcDocType;
import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Yusuf
 */
public class OtherSalesAcDocumentDlg extends javax.swing.JDialog implements PropertyChangeListener {

    private NewOSalesDocumentTask newOSalesDocumentTask;
    private AccountingDocTaskOther accountingDocTask;
    private String taskType;
    private OtherSalesAcDoc document;

    public OtherSalesAcDocumentDlg(Frame parent) {
        super(parent, true);
        initComponents();
        CheckInput a = new CheckInput();
        a.setNegativeAccepted(true);
        txtAmount.setDocument(a);
    }

    /**
     * This method creates new document. 
     * @param invoice If ID is not null it loeads document. If ID is null then
     * its creates a draft DM/CM using invoice provided. Invoice can not be null
     * in case of creating new document.
     * @return 
     */

    public boolean showDialog(OtherSalesAcDoc invoice) {
        document = createDraftDocument(invoice);
        displayDocument();
        setLocationRelativeTo(this);
        setVisible(true);
        return true;
    }

    /**
     * This method loads existing document
     * @param id 
     */
    public void showDialog(Long id) {
        loadDocument(id);
        setLocationRelativeTo(this);
        setVisible(true);
    }

    private void displayDocument() {
        AcDocType docType = document.getType();
        if (docType != null) {
            if (document.getType().equals(Enums.AcDocType.DEBITMEMO)) {
                lblTitle.setText("Debit Memo");
            } else if (document.getType().equals(AcDocType.CREDITMEMO)) {
                lblTitle.setText("Credit Memo");
            }
        }
        acDocHeaderComponent.display(document);

        controllComponent();
        if (document.getAgent() != null) {
            txtAcDocFor.setText(document.getAgent().calculateFullName() + document.getAgent().getFullAddressCRSeperated());
        } else {
            txtAcDocFor.setText(document.getCustomer().calculateFullName() + document.getCustomer().getFullAddressCRSeperated());
        }
        if (document.getDocumentedAmount() != null) {
            txtAmount.setText(document.getDocumentedAmount().toString());
        }
        txtRemark.setText(document.getRemark());
    }

    public OtherSalesAcDoc createDraftDocument(OtherSalesAcDoc invoice) {
        OtherSalesAcDoc draft_doc = new OtherSalesAcDoc();

        draft_doc.setReference(invoice.getReference());
        draft_doc.setParent(invoice);
        draft_doc.setAgent(invoice.getAgent());
        draft_doc.setCustomer(invoice.getCustomer());
        draft_doc.setDocIssueDate(new java.util.Date());

        return draft_doc;
    }

    public void createDocument() {
        taskType = "CREATE";
        BigDecimal amount = new BigDecimal(txtAmount.getText());
        String remark = txtRemark.getText();
        int cmbIndex = cmbAdditionalCharge.getSelectedIndex();

        String terms = (String) AcDocHeaderComponent.cmbTerms.getSelectedItem();
        if (!"Select".equals(terms)) {
            if (amount.compareTo(new BigDecimal("0.00")) == 1 && cmbIndex > 0) {
                document.setRemark(remark);
                document.setTerms(terms);
                String docType = cmbAdditionalCharge.getSelectedItem().toString();
                if (docType.equals("Debit Memo")) {
                    document.setType(AcDocType.DEBITMEMO);
                    document.setDocumentedAmount(amount);
                } else if (docType.equals("Credit Memo")) {
                    document.setType(AcDocType.CREDITMEMO);
                    document.setDocumentedAmount(amount.negate());
                }

                document.setStatus(Enums.AcDocStatus.ACTIVE);
                newOSalesDocumentTask = new NewOSalesDocumentTask(document, progressBar);
                newOSalesDocumentTask.addPropertyChangeListener(this);
                newOSalesDocumentTask.execute();
            } else {

            }
        }
    }

    private void controllComponent() {
        if (document.getId() == null) {
            btnCreateDocument.setEnabled(true);
            btnEmail.setEnabled(false);
            btnPrint.setEnabled(false);
            btnOfficeCopy.setEnabled(false);
        } else {
            btnCreateDocument.setEnabled(false);
            btnEmail.setEnabled(true);
            btnPrint.setEnabled(true);
            btnOfficeCopy.setEnabled(true);
        }
    }

    public void loadDocument(Long id) {
        taskType = "COMPLETE";
        accountingDocTask = new AccountingDocTaskOther(id, Enums.SaleType.OTHERSALES, "DETAILS");
        accountingDocTask.addPropertyChangeListener(this);
        accountingDocTask.execute();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtAcDocFor = new javax.swing.JTextArea();
        acDocHeaderComponent = new com.ets.fe.acdoc.gui.comp.AcDocHeaderComponent();
        jLabel1 = new javax.swing.JLabel();
        txtAmount = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtRemark = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        cmbAdditionalCharge = new javax.swing.JComboBox();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel2 = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        lblTitle = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        btnEmail = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        btnOfficeCopy = new javax.swing.JButton();
        btnCreateDocument = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Agent/Customer", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        txtAcDocFor.setEditable(false);
        txtAcDocFor.setColumns(20);
        txtAcDocFor.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        txtAcDocFor.setLineWrap(true);
        txtAcDocFor.setRows(5);
        jScrollPane5.setViewportView(txtAcDocFor);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5)
        );

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Amount");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Remark");

        txtRemark.setColumns(15);
        txtRemark.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        txtRemark.setRows(3);
        jScrollPane1.setViewportView(txtRemark);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Document Type");

        cmbAdditionalCharge.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Select", "Debit Memo", "Credit Memo" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtAmount)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbAdditionalCharge, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(acDocHeaderComponent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(acDocHeaderComponent, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbAdditionalCharge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        progressBar.setMaximumSize(new java.awt.Dimension(32767, 18));
        progressBar.setMinimumSize(new java.awt.Dimension(10, 18));
        progressBar.setPreferredSize(new java.awt.Dimension(146, 18));
        progressBar.setStringPainted(true);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        lblTitle.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblTitle.setText("Debit / Credit Memo");

        jPanel3.setBackground(new java.awt.Color(102, 102, 102));
        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        btnEmail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/email18.png"))); // NOI18N
        btnEmail.setToolTipText("Email Invoice");
        btnEmail.setFocusable(false);
        btnEmail.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEmail.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/print18.png"))); // NOI18N
        btnPrint.setToolTipText("Print");
        btnPrint.setFocusable(false);
        btnPrint.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPrint.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        btnOfficeCopy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/print18.png"))); // NOI18N
        btnOfficeCopy.setToolTipText("Print Office Copy");
        btnOfficeCopy.setFocusable(false);
        btnOfficeCopy.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnOfficeCopy.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        btnCreateDocument.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnCreateDocument.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/createInvoice.png"))); // NOI18N
        btnCreateDocument.setPreferredSize(new java.awt.Dimension(57, 25));
        btnCreateDocument.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateDocumentActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(238, Short.MAX_VALUE)
                .addComponent(btnCreateDocument, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(btnPrint)
                .addGap(2, 2, 2)
                .addComponent(btnEmail)
                .addGap(2, 2, 2)
                .addComponent(btnOfficeCopy))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnCreateDocument, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(btnPrint, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnEmail, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnOfficeCopy, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addGap(1, 1, 1))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(270, 270, 270)
                        .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTitle)
                        .addGap(4, 4, 4)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCreateDocumentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateDocumentActionPerformed
        createDocument();
    }//GEN-LAST:event_btnCreateDocumentActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.ets.fe.acdoc.gui.comp.AcDocHeaderComponent acDocHeaderComponent;
    private javax.swing.JButton btnCreateDocument;
    private javax.swing.JButton btnEmail;
    private javax.swing.JButton btnOfficeCopy;
    private javax.swing.JButton btnPrint;
    private javax.swing.JComboBox cmbAdditionalCharge;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JTextArea txtAcDocFor;
    private javax.swing.JTextField txtAmount;
    private javax.swing.JTextArea txtRemark;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress".equals(evt.getPropertyName())) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
            if (progress == 100) {
                try {
                    if ("CREATE".equals(taskType)) {
                        document = newOSalesDocumentTask.get();
                        displayDocument();
                    } else if ("COMPLETE".equals(taskType)) {
                        document = (OtherSalesAcDoc) accountingDocTask.get();
                        displayDocument();
                        taskType = "";
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(SalesInvoiceDlg.class.getName()).log(Level.SEVERE, null, ex);
                } finally {

                }
            }
        }
    }
}
