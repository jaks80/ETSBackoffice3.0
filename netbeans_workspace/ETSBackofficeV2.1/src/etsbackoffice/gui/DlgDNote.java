package etsbackoffice.gui;

import etsbackoffice.ETSBackofficeApp;
import etsbackoffice.businesslogic.AccountsBo;
import etsbackoffice.businesslogic.AuthenticationBo;
import etsbackoffice.domain.AccountingDocument;
import etsbackoffice.domain.AccountingDocumentLine;
import etsbackoffice.domain.Agent;
import etsbackoffice.domain.Customer;
import etsbackoffice.domain.PurchaseAccountingDocument;
import etsbackoffice.domain.PurchaseAccountingDocumentLine;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

/**
 *
 * @author Yusuf
 */
public class DlgDNote extends javax.swing.JDialog implements ActionListener {

    private boolean close;
    private boolean createNote;
    private Agent agent;
    private Customer customer;
    private AccountingDocument acDoc;
    private PurchaseAccountingDocument pAcDoc;
    
    private AccountsBo accountsBo = (AccountsBo) ETSBackofficeApp.getApplication().ctx.getBean("accountsBo");
    
    /** Creates new form FrameCNote */
    public DlgDNote(java.awt.Frame parent) {
        super(parent, "Debit Note", true);
        initComponents();
        btnCreateNote.addActionListener(this);
    }

    private void populateTxtAgentDetails() {
        txtContactableDetails.append(agent.getFullAddressCRSeperated());
    }

    private void populateTxtCustomerDetails() {
        txtContactableDetails.setText(customer.getFullAddressCRSeperated());
    }

    public boolean showDebitNoteDialog(AccountingDocument acDoc) {
        this.acDoc = acDoc;
        lblContactable.setText("Debit Note For");
        txtVRef.setVisible(false);
        lblVRef.setVisible(false);
        if (this.acDoc.getPnr().getAgent() != null) {
            this.agent = acDoc.getPnr().getAgent();
            populateTxtAgentDetails();
        } else if (this.acDoc.getPnr().getCustomer() != null) {
            this.customer = acDoc.getPnr().getCustomer();
            populateTxtCustomerDetails();
        }

        txtPnr.setText(this.acDoc.getPnr().getGdsPNR());

        if (acDoc.getIssueDate() != null) {            
            if(!this.acDoc.getAccountingDocumentLines().isEmpty()){
             txtAmount.setText(this.acDoc.getTotalDocumentedAmount().toString());
             txtRemark.setText(this.acDoc.getAccountingDocumentLines().iterator().next().getRemark());
            }
            txtInvRef.setText(this.acDoc.getAcDocRefString());
            txtUser.setText(this.acDoc.getAcDocIssuedBy().getSurName() + " " + this.acDoc.getAcDocIssuedBy().getForeName());
            disableComponent();
        }
        close = false;
        createNote = false;

        setLocationRelativeTo(this);
        setVisible(true);

        if (close) {
            dispose();
        } else if (createNote) {
            String remark = txtRemark.getText();            
            BigDecimal amount = new BigDecimal(txtAmount.getText());

            this.acDoc.setActive(true);
            this.acDoc.setIssueDate(new java.util.Date());
            this.acDoc.setAcDocIssuedBy(AuthenticationBo.getLoggedOnUser());
            this.acDoc.addAcStatement(accountsBo.newAccountsTransactionFromSAcDoc(this.acDoc));
            AccountingDocumentLine acDocLine = new AccountingDocumentLine();
            acDocLine.setAmount(amount);
            acDocLine.setRemark(remark);
            acDocLine.setType(7);
            acDocLine.setAccountingDocument(acDoc);            
            this.acDoc.getAccountingDocumentLines().add(acDocLine);
        }
        return createNote;
    }

    public boolean showDebitNoteDialog(PurchaseAccountingDocument pAcDoc) {
        this.pAcDoc = pAcDoc;
        lblContactable.setText("Vendor/Debit Note From");
        if (this.pAcDoc.getPnr().getTicketingAgt() != null) {
            this.agent = pAcDoc.getPnr().getTicketingAgt();
            populateTxtAgentDetails();
        } 

        txtPnr.setText(this.pAcDoc.getPnr().getGdsPNR());
        txtUser.setText(this.pAcDoc.getAcDocIssuedBy().getFullName());
        if (pAcDoc.getIssueDate() != null) {
            txtAmount.setText(this.pAcDoc.getTotalDocumentedAmount().toString());
            txtRemark.setText(this.pAcDoc.getPurchaseAcDocLines().iterator().next().getRemark());
            txtInvRef.setText(this.pAcDoc.getRecipientRef());
            txtVRef.setText(this.pAcDoc.getVendorRef());
            
            //txtUser.setText(this.pAcDoc.getAcDocIssuedBy().getSurName() + " " + this.acDoc.getAcDocIssuedBy().getForeName());
            disableComponent();
        }
        close = false;
        createNote = false;

        setLocationRelativeTo(this);
        setVisible(true);

        if (close) {
            dispose();
        } else if (createNote) {
            String remark = txtRemark.getText();            
            BigDecimal amount = new BigDecimal(txtAmount.getText());

            this.pAcDoc.setActive(true);
            this.pAcDoc.setIssueDate(new java.util.Date());            
            this.pAcDoc.addAcStatement(accountsBo.newAccountsTransactionFromPAcDoc(this.pAcDoc));
            this.pAcDoc.setVendorRef(txtVRef.getText().trim());
            PurchaseAccountingDocumentLine acDocLine = new PurchaseAccountingDocumentLine();
            acDocLine.setAmount(amount);
            acDocLine.setRemark(remark);
            acDocLine.setType(7);
            acDocLine.setPurchaseAccountingDocument(pAcDoc);
            //acDocLine.setUnit(1);
            this.pAcDoc.getPurchaseAcDocLines().add(acDocLine);
        }
        return createNote;
    }
    
    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();
        if (source == btnCreateNote) {
            if (txtAmount.getText().isEmpty() || txtRemark.getText().isEmpty()) {
                lblInfo.setText("Enter mendatory fields...");
            } else {
                createNote = true;
                lblInfo.setText("Creating Credit Note");
                dispose();
            }
        } else if (source == btnClose) {
            setVisible(false);
        }
    }

    private void disableComponent() {
        btnCreateNote.setVisible(false);
        txtAmount.setEditable(false);
        txtRemark.setEditable(false);       
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

        jPanel3 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        txtInvRef = new javax.swing.JTextField();
        txtUser = new javax.swing.JTextField();
        datePicker = new org.jdesktop.swingx.JXDatePicker();
        jLabel9 = new javax.swing.JLabel();
        txtPnr = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtVRef = new javax.swing.JTextField();
        lblVRef = new javax.swing.JLabel();
        pnlCNoteFor = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtContactableDetails = new javax.swing.JTextArea();
        lblContactable = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        txtAmount = new javax.swing.JTextField();
        txtRemark = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        btnCreateNote = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        lblInfo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getResourceMap(DlgDNote.class);
        jPanel3.setBackground(resourceMap.getColor("jPanel3.background")); // NOI18N
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel3.setName("jPanel3"); // NOI18N

        jPanel2.setBackground(resourceMap.getColor("jPanel2.background")); // NOI18N
        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new java.awt.GridBagLayout());

        txtInvRef.setBackground(resourceMap.getColor("txtPnr.background")); // NOI18N
        txtInvRef.setEditable(false);
        txtInvRef.setFont(resourceMap.getFont("datePicker.font")); // NOI18N
        txtInvRef.setName("txtInvRef"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(txtInvRef, gridBagConstraints);

        txtUser.setBackground(resourceMap.getColor("txtPnr.background")); // NOI18N
        txtUser.setEditable(false);
        txtUser.setFont(resourceMap.getFont("datePicker.font")); // NOI18N
        txtUser.setName("txtUser"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(txtUser, gridBagConstraints);

        datePicker.setDate(new java.util.Date());
        datePicker.setFont(resourceMap.getFont("datePicker.font")); // NOI18N
        datePicker.setBackground(resourceMap.getColor("datePicker.background")); // NOI18N
        datePicker.setName("datePicker"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(datePicker, gridBagConstraints);

        jLabel9.setFont(resourceMap.getFont("datePicker.font")); // NOI18N
        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(jLabel9, gridBagConstraints);

        txtPnr.setBackground(resourceMap.getColor("txtPnr.background")); // NOI18N
        txtPnr.setEditable(false);
        txtPnr.setFont(resourceMap.getFont("datePicker.font")); // NOI18N
        txtPnr.setText(resourceMap.getString("txtPnr.text")); // NOI18N
        txtPnr.setName("txtPnr"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(txtPnr, gridBagConstraints);

        jLabel3.setFont(resourceMap.getFont("jLabel3.font")); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setForeground(resourceMap.getColor("jLabel3.foreground")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        jPanel2.add(jLabel3, gridBagConstraints);

        jLabel1.setFont(resourceMap.getFont("datePicker.font")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(jLabel1, gridBagConstraints);

        jLabel2.setFont(resourceMap.getFont("datePicker.font")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(jLabel2, gridBagConstraints);

        jLabel6.setFont(resourceMap.getFont("datePicker.font")); // NOI18N
        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(jLabel6, gridBagConstraints);

        txtVRef.setFont(resourceMap.getFont("txtVRef.font")); // NOI18N
        txtVRef.setText(resourceMap.getString("txtVRef.text")); // NOI18N
        txtVRef.setName("txtVRef"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(txtVRef, gridBagConstraints);

        lblVRef.setFont(resourceMap.getFont("lblVRef.font")); // NOI18N
        lblVRef.setText(resourceMap.getString("lblVRef.text")); // NOI18N
        lblVRef.setName("lblVRef"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(lblVRef, gridBagConstraints);

        pnlCNoteFor.setBackground(resourceMap.getColor("pnlCNoteFor.background")); // NOI18N
        pnlCNoteFor.setName("pnlCNoteFor"); // NOI18N
        pnlCNoteFor.setPreferredSize(new java.awt.Dimension(259, 140));

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        txtContactableDetails.setColumns(15);
        txtContactableDetails.setEditable(false);
        txtContactableDetails.setFont(resourceMap.getFont("txtContactableDetails.font")); // NOI18N
        txtContactableDetails.setRows(5);
        txtContactableDetails.setName("txtContactableDetails"); // NOI18N
        jScrollPane4.setViewportView(txtContactableDetails);

        lblContactable.setFont(resourceMap.getFont("lblContactable.font")); // NOI18N
        lblContactable.setText(resourceMap.getString("lblContactable.text")); // NOI18N
        lblContactable.setName("lblContactable"); // NOI18N

        javax.swing.GroupLayout pnlCNoteForLayout = new javax.swing.GroupLayout(pnlCNoteFor);
        pnlCNoteFor.setLayout(pnlCNoteForLayout);
        pnlCNoteForLayout.setHorizontalGroup(
            pnlCNoteForLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCNoteForLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlCNoteForLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblContactable, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        pnlCNoteForLayout.setVerticalGroup(
            pnlCNoteForLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCNoteForLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblContactable, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(pnlCNoteFor, javax.swing.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlCNoteFor, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setName("jPanel1"); // NOI18N

        jLabel7.setFont(resourceMap.getFont("jLabel7.font")); // NOI18N
        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        txtAmount.setFont(resourceMap.getFont("jLabel7.font")); // NOI18N
        txtAmount.setText(resourceMap.getString("txtAmount.text")); // NOI18N
        txtAmount.setName("txtAmount"); // NOI18N

        txtRemark.setFont(resourceMap.getFont("jLabel7.font")); // NOI18N
        txtRemark.setText(resourceMap.getString("txtRemark.text")); // NOI18N
        txtRemark.setName("txtRemark"); // NOI18N

        jLabel4.setFont(resourceMap.getFont("jLabel7.font")); // NOI18N
        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtAmount, javax.swing.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
                        .addGap(202, 202, 202))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtRemark, javax.swing.GroupLayout.DEFAULT_SIZE, 358, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(4, 4, 4)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(49, 49, 49))
        );

        btnCreateNote.setFont(resourceMap.getFont("btnClose.font")); // NOI18N
        btnCreateNote.setIcon(resourceMap.getIcon("btnCreateNote.icon")); // NOI18N
        btnCreateNote.setText(resourceMap.getString("btnCreateNote.text")); // NOI18N
        btnCreateNote.setName("btnCreateNote"); // NOI18N

        btnClose.setFont(resourceMap.getFont("btnClose.font")); // NOI18N
        btnClose.setIcon(resourceMap.getIcon("btnClose.icon")); // NOI18N
        btnClose.setText(resourceMap.getString("btnClose.text")); // NOI18N
        btnClose.setName("btnClose"); // NOI18N
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        lblInfo.setFont(resourceMap.getFont("lblInfo.font")); // NOI18N
        lblInfo.setText(resourceMap.getString("lblInfo.text")); // NOI18N
        lblInfo.setName("lblInfo"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(lblInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCreateNote)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnClose)
                .addContainerGap())
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnClose)
                            .addComponent(btnCreateNote)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(lblInfo)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        dispose();
    }//GEN-LAST:event_btnCloseActionPerformed
    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnCreateNote;
    private org.jdesktop.swingx.JXDatePicker datePicker;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lblContactable;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JLabel lblVRef;
    private javax.swing.JPanel pnlCNoteFor;
    private javax.swing.JTextField txtAmount;
    private javax.swing.JTextArea txtContactableDetails;
    private javax.swing.JTextField txtInvRef;
    private javax.swing.JTextField txtPnr;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JTextField txtUser;
    private javax.swing.JTextField txtVRef;
    // End of variables declaration//GEN-END:variables
}
