package com.ets.fe.acdoc_o.gui;

import com.ets.fe.acdoc_o.model.*;
import com.ets.fe.accounts.model.Payment;
import com.ets.fe.acdoc_o.task.NewOSalesDocumentTask;
import com.ets.fe.Application;
import com.ets.fe.acdoc.gui.SalesInvoiceDlg;
import com.ets.fe.acdoc.gui.comp.AcDocHeaderComponent;
import com.ets.fe.accounts.task.PaymentTask;
import com.ets.fe.accounts.gui.logic.PaymentLogicOther;
import com.ets.fe.accounts.gui.payment.DlgOtherSalesCreditTransfer;
import com.ets.fe.acdoc_o.task.AccountingDocTaskOther;
import com.ets.fe.client.model.Contactable;
import com.ets.fe.os.model.*;
import com.ets.fe.report.BeanJasperReport;
import com.ets.fe.util.*;
import java.awt.Frame;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Yusuf
 */
public class OtherInvoiceDlg extends javax.swing.JDialog implements PropertyChangeListener {

    private boolean editable;
    private PaymentTask paymentTask;
    private AccountingDocTaskOther accountingDocTask;
    private NewOSalesDocumentTask newInvoiceTask;
    private boolean allowPayment = true;
    private String taskType;
    private Contactable client;

    private OtherSalesAcDoc invoice;
    private List<AdditionalCharge> charges = Application.getAdditionalCharges();

    public OtherInvoiceDlg(java.awt.Frame parent) {
        super(parent, true);
        initComponents();
        CheckInput a = new CheckInput();
        CheckInput b = new CheckInput();
        CheckInput c = new CheckInput();
        CheckInput d = new CheckInput();

        a.setNegativeAccepted(true);

        txtAmount.setDocument(a);
        txtCHFee.setDocument(b);
        txtPostage.setDocument(c);
        txtOther.setDocument(d);

        setPaymentType();
    }

    public void showDialog(Long id) {
        otherServiceComp.setParent(this);
        if (id != null) {
            loadInvoice(id);
            this.editable = false;

        } else {
            this.invoice = new OtherSalesAcDoc();
            List<AccountingDocumentLine> lines = new ArrayList<>();
            this.invoice.setAccountingDocumentLines(lines);
            this.editable = true;
            displayInvoice(invoice);
        }

        setLocationRelativeTo(this);
        setVisible(true);
    }

    private void displayInvoice(OtherSalesAcDoc invoice) {

        otherServiceComp.display(invoice.getAccountingDocumentLines(), editable);
        acDocHeaderComponent.display(invoice);
        displayOtherCharge(invoice);
        displayBalance(invoice);
        populateTblPayment(invoice);
        txtRemark.setText(invoice.getRemark());
        controllComponent(invoice);

        if (invoice.getAgent() != null) {
            client = invoice.getAgent();
            clientComponent.setAllocatetClient(invoice.getAgent(), null, null, editable);
        } else {
            client = invoice.getCustomer();
            clientComponent.setAllocatetClient(invoice.getCustomer(), null, null, editable);
        }
    }

    private void displayOtherCharge(OtherSalesAcDoc invoice) {
        if (!invoice.getAdditionalChargeLines().isEmpty()) {
            for (AdditionalChargeLine line : invoice.getAdditionalChargeLines()) {
                if (line.getAdditionalCharge().getTitle().equals("Other")) {
                    txtOther.setText(line.getAmount().toString());
                } else if (line.getAdditionalCharge().getTitle().equals("Card Handling")) {
                    txtCHFee.setText(line.getAmount().toString());
                } else if (line.getAdditionalCharge().getTitle().equals("Postage")) {
                    txtPostage.setText(line.getAmount().toString());
                }
            }
        }
    }

    public void displayBalance(OtherSalesAcDoc invoice) {
        lblSubTotal.setText(invoice.calculateOtherServiceSubTotal().toString());
        lblAddCharge.setText(invoice.calculateAddChargesSubTotal().toString());
        lblInvAmount.setText(invoice.calculateDocumentedAmount().toString());
        lblTotalPayment.setText(invoice.calculateTotalPayment().abs().toString());
        lblOther.setText(invoice.calculateRelatedDocBalance().toString());
        lblDue.setText(invoice.calculateDueAmount().toString());
    }

    public void createInvoice() {
        createOtherChargeLine();
        invoice.setAdditionalChargeLines(createOtherChargeLine());
        //invoice.setAccountingDocumentLines(otherServiceComp.getLines()); Lines are added automatically
        invoice.setAgent(clientComponent.getAgent());
        invoice.setCustomer(clientComponent.getCustomer());
        invoice.setRemark(txtRemark.getText());
        invoice.setType(Enums.AcDocType.INVOICE);
        invoice.setDocIssueDate(new java.util.Date());

        String terms = (String) AcDocHeaderComponent.cmbTerms.getSelectedItem();

        if (!"Select".equals(terms)) {
            invoice.setTerms(terms);
            taskType = "CREATE";
            newInvoiceTask = new NewOSalesDocumentTask(invoice, progressBar);
            newInvoiceTask.addPropertyChangeListener(this);
            newInvoiceTask.execute();
        }
    }

    public void loadInvoice(Long id) {
        taskType = "COMPLETE";
        accountingDocTask = new AccountingDocTaskOther(id, Enums.SaleType.OTHERSALES, "DETAILS");
        accountingDocTask.addPropertyChangeListener(this);
        accountingDocTask.execute();
    }

    private List<AdditionalChargeLine> createOtherChargeLine() {

        List<AdditionalChargeLine> chargeLines = new ArrayList<>();

        String chFee = txtCHFee.getText();
        String postage = txtPostage.getText();
        String other = txtOther.getText();

        if (chFee != null && !chFee.isEmpty()) {
            AdditionalChargeLine line = new AdditionalChargeLine();
            line.setAmount(new BigDecimal(chFee));
            line.setAdditionalCharge(Application.getCardHandlingFee());
            chargeLines.add(line);
        }

        if (postage != null && !postage.isEmpty()) {
            AdditionalChargeLine line = new AdditionalChargeLine();
            line.setAmount(new BigDecimal(postage));
            line.setAdditionalCharge(Application.getPostage());
            chargeLines.add(line);
        }

        if (other != null && !other.isEmpty()) {
            AdditionalChargeLine line = new AdditionalChargeLine();
            line.setAmount(new BigDecimal(other));
            line.setAdditionalCharge(Application.getOther());
            chargeLines.add(line);
        }
        return chargeLines;
    }

    public void processPayment(OtherSalesAcDoc invoice) {
        busyLabel.setText("");
        taskType = "PAYMENT";
        busyLabel.setBusy(true);
        btnSubmitPayment.setEnabled(false);
        String amountString = txtAmount.getText();
        String remark = txtRef.getText();

        if (!amountString.isEmpty() && !remark.isEmpty() && cmbTType.getSelectedIndex() > 0) {
            Enums.PaymentType type = (Enums.PaymentType) cmbTType.getSelectedItem();
            BigDecimal amount = new BigDecimal(amountString.trim());
            PaymentLogicOther logic = new PaymentLogicOther();

            if (amount.compareTo(invoice.calculateDueAmount().abs()) <= 0) {
                Payment payment = logic.processSingleOSalesPayment(amount, invoice, remark, type);
                paymentTask = new PaymentTask(payment, Enums.TaskType.CREATE);
                paymentTask.addPropertyChangeListener(this);
                paymentTask.execute();
            } else {
                busyLabel.setText("Warning! Excess payment");
                btnSubmitPayment.setEnabled(true);
            }
        } else {
            busyLabel.setText("Warning! Mandatory fields!");
            btnSubmitPayment.setEnabled(true);
        }
    }

    private void populateTblPayment(OtherSalesAcDoc invoice) {
        tblPayment.clearSelection();
        DefaultTableModel model = (DefaultTableModel) tblPayment.getModel();
        model.getDataVector().removeAllElements();

        int row = 0;
        for (OtherSalesAcDoc doc : invoice.getRelatedDocuments()) {
            String remark = doc.getRemark();
            if (doc.getType().equals(Enums.AcDocType.PAYMENT) || doc.getType().equals(Enums.AcDocType.REFUND)) {
                Payment payment = doc.getPayment();
                if (payment != null) {
                    remark = doc.getPayment().getPaymentType().toString();
                    remark = remark + " / " + doc.getPayment().getRemark();
                }
            }
            model.insertRow(row, new Object[]{doc.getType(), remark, doc.getDocumentedAmount().abs(), DateUtil.dateToString(doc.getDocIssueDate())});
            row++;
        }
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

        jPanel7 = new javax.swing.JPanel();
        btnCreateDocument = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        btnEmail = new javax.swing.JButton();
        btnOfficeCopy = new javax.swing.JButton();
        btnVoid = new javax.swing.JButton();
        btnNewDoc = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel4 = new javax.swing.JPanel();
        clientComponent = new com.ets.fe.a_main.ClientSearchComponent();
        acDocHeaderComponent = new com.ets.fe.acdoc.gui.comp.AcDocHeaderComponent();
        jPanel5 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        otherServiceComp = new com.ets.fe.acdoc_o.gui.comp.OtherServiceComp();
        jPanel9 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        txtCHFee = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtPostage = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        txtOther = new javax.swing.JTextField();
        tabPayment = new javax.swing.JTabbedPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblPayment = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        cmbTType = new javax.swing.JComboBox();
        txtAmount = new javax.swing.JTextField();
        txtRef = new javax.swing.JTextField();
        btnSubmitPayment = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        lblTDueRefund = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        busyLabel = new org.jdesktop.swingx.JXBusyLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtRemark = new javax.swing.JTextArea();
        jPanel6 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        lblSubTotal = new javax.swing.JLabel();
        lblAddCharge = new javax.swing.JLabel();
        lblInvAmount = new javax.swing.JLabel();
        lblTotalPayment = new javax.swing.JLabel();
        lblDue = new javax.swing.JLabel();
        lblOther = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Other Invoice");
        setResizable(false);

        jPanel7.setBackground(new java.awt.Color(102, 102, 102));
        jPanel7.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        btnCreateDocument.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/createInvoice.png"))); // NOI18N
        btnCreateDocument.setToolTipText("Create New Invoice");
        btnCreateDocument.setEnabled(false);
        btnCreateDocument.setFocusable(false);
        btnCreateDocument.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCreateDocument.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCreateDocument.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateDocumentActionPerformed(evt);
            }
        });

        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/print18.png"))); // NOI18N
        btnPrint.setToolTipText("Print Invoice");
        btnPrint.setEnabled(false);
        btnPrint.setFocusable(false);
        btnPrint.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPrint.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });

        btnEmail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/email18.png"))); // NOI18N
        btnEmail.setToolTipText("Email Invoice");
        btnEmail.setEnabled(false);
        btnEmail.setFocusable(false);
        btnEmail.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEmail.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEmailActionPerformed(evt);
            }
        });

        btnOfficeCopy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/print18.png"))); // NOI18N
        btnOfficeCopy.setToolTipText("Print Office Copy");
        btnOfficeCopy.setEnabled(false);
        btnOfficeCopy.setFocusable(false);
        btnOfficeCopy.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnOfficeCopy.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnOfficeCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOfficeCopyActionPerformed(evt);
            }
        });

        btnVoid.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/void18-1.png"))); // NOI18N
        btnVoid.setToolTipText("<html>\nVOID Payment, Debit/ Credit Memo.<br>\nVOID Invoice from Invoice History.\n</html>");
        btnVoid.setEnabled(false);
        btnVoid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVoidActionPerformed(evt);
            }
        });

        btnNewDoc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/newdoc18.png"))); // NOI18N
        btnNewDoc.setToolTipText("New Debit/ Credit Memo");
        btnNewDoc.setEnabled(false);
        btnNewDoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewDocActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(btnVoid)
                .addGap(2, 2, 2)
                .addComponent(btnNewDoc)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCreateDocument)
                .addGap(0, 0, 0)
                .addComponent(btnPrint)
                .addGap(0, 0, 0)
                .addComponent(btnEmail)
                .addGap(0, 0, 0)
                .addComponent(btnOfficeCopy))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnCreateDocument, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnOfficeCopy, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(btnVoid, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btnNewDoc, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel3.setMaximumSize(new java.awt.Dimension(32767, 24));
        jPanel3.setMinimumSize(new java.awt.Dimension(71, 24));
        jPanel3.setPreferredSize(new java.awt.Dimension(980, 24));
        jPanel3.setLayout(new java.awt.GridBagLayout());

        progressBar.setMaximumSize(new java.awt.Dimension(32767, 18));
        progressBar.setMinimumSize(new java.awt.Dimension(10, 18));
        progressBar.setPreferredSize(new java.awt.Dimension(146, 18));
        progressBar.setStringPainted(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 1, 0, 1);
        jPanel3.add(progressBar, gridBagConstraints);

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanel3.add(jSeparator2, gridBagConstraints);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Message:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 2, 1, 2);
        jPanel3.add(jLabel2, gridBagConstraints);

        jSplitPane1.setDividerLocation(695);
        jSplitPane1.setDividerSize(4);

        clientComponent.setMinimumSize(new java.awt.Dimension(95, 162));
        clientComponent.setPreferredSize(new java.awt.Dimension(178, 286));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(clientComponent, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
            .addComponent(acDocHeaderComponent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(clientComponent, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(acDocHeaderComponent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(62, Short.MAX_VALUE))
        );

        jSplitPane1.setRightComponent(jPanel4);

        jTabbedPane1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(otherServiceComp, javax.swing.GroupLayout.DEFAULT_SIZE, 689, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(otherServiceComp, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Services", jPanel1);

        jLabel12.setText("Card Handling Fee:");

        txtCHFee.setText("0.00");
        txtCHFee.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCHFeeFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtCHFeeFocusLost(evt);
            }
        });

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel13.setText("Postage:");

        txtPostage.setText("0.00");
        txtPostage.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPostageFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPostageFocusLost(evt);
            }
        });

        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel14.setText("Other:");

        txtOther.setText("0.00");
        txtOther.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtOtherFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtOtherFocusLost(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtCHFee, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                    .addComponent(txtPostage)
                    .addComponent(txtOther))
                .addContainerGap(444, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(txtCHFee, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13)
                    .addComponent(txtPostage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(txtOther, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Other Charges", jPanel9);

        tabPayment.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        //tabPayment.addChangeListener(tabPaymentListener);

        tblPayment.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        tblPayment.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Type", "Remark", "Amount", "Date"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblPayment.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(tblPayment);
        if (tblPayment.getColumnModel().getColumnCount() > 0) {
            tblPayment.getColumnModel().getColumn(0).setMaxWidth(80);
            tblPayment.getColumnModel().getColumn(2).setMaxWidth(150);
            tblPayment.getColumnModel().getColumn(3).setMaxWidth(100);
        }

        tabPayment.addTab("Payments", jScrollPane3);

        jPanel8.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("T.Type");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        jPanel8.add(jLabel1, gridBagConstraints);

        jLabel9.setText("Amount");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        jPanel8.add(jLabel9, gridBagConstraints);

        jLabel10.setText("Remark");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        jPanel8.add(jLabel10, gridBagConstraints);

        cmbTType.setMinimumSize(new java.awt.Dimension(28, 19));
        cmbTType.setPreferredSize(new java.awt.Dimension(28, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 0, 2);
        jPanel8.add(cmbTType, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 2);
        jPanel8.add(txtAmount, gridBagConstraints);

        txtRef.setPreferredSize(new java.awt.Dimension(50, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 4, 2);
        jPanel8.add(txtRef, gridBagConstraints);

        btnSubmitPayment.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnSubmitPayment.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/payment18.png"))); // NOI18N
        btnSubmitPayment.setText("Submit");
        btnSubmitPayment.setPreferredSize(new java.awt.Dimension(135, 30));
        btnSubmitPayment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSubmitPaymentActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel8.add(btnSubmitPayment, gridBagConstraints);

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel11.setText("Available Credit:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel8.add(jLabel11, gridBagConstraints);

        lblTDueRefund.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblTDueRefund.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTDueRefund.setText("0.00");
        lblTDueRefund.setPreferredSize(new java.awt.Dimension(100, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel8.add(lblTDueRefund, gridBagConstraints);

        jButton6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/credit18.png"))); // NOI18N
        jButton6.setText("Apply Credit");
        jButton6.setPreferredSize(new java.awt.Dimension(135, 30));
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 2, 2);
        jPanel8.add(jButton6, gridBagConstraints);

        busyLabel.setDirection(null);
        busyLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel8.add(busyLabel, gridBagConstraints);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, 498, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabPayment.addTab("New Payment", jPanel2);

        txtRemark.setColumns(20);
        txtRemark.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        txtRemark.setLineWrap(true);
        txtRemark.setRows(3);
        txtRemark.setToolTipText("Additional information can be added. <br>This will be shown in client invoice.");
        jScrollPane1.setViewportView(txtRemark);

        jPanel6.setBackground(new java.awt.Color(0, 0, 0));
        jPanel6.setLayout(new java.awt.GridBagLayout());

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("SubTotal:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 0);
        jPanel6.add(jLabel3, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Add.Charge:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 0);
        jPanel6.add(jLabel4, gridBagConstraints);

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Inv.Amount:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 0);
        jPanel6.add(jLabel5, gridBagConstraints);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Payment:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 0);
        jPanel6.add(jLabel6, gridBagConstraints);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Outstanding:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 0);
        jPanel6.add(jLabel7, gridBagConstraints);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Other (+/-):");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 0);
        jPanel6.add(jLabel8, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel6.add(jSeparator4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel6.add(jSeparator3, gridBagConstraints);

        lblSubTotal.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblSubTotal.setForeground(new java.awt.Color(255, 255, 0));
        lblSubTotal.setText("0.00");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
        jPanel6.add(lblSubTotal, gridBagConstraints);

        lblAddCharge.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblAddCharge.setForeground(new java.awt.Color(255, 255, 0));
        lblAddCharge.setText("0.00");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
        jPanel6.add(lblAddCharge, gridBagConstraints);

        lblInvAmount.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblInvAmount.setForeground(new java.awt.Color(255, 255, 0));
        lblInvAmount.setText("0.00");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
        jPanel6.add(lblInvAmount, gridBagConstraints);

        lblTotalPayment.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblTotalPayment.setForeground(new java.awt.Color(51, 255, 0));
        lblTotalPayment.setText("0.00");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
        jPanel6.add(lblTotalPayment, gridBagConstraints);

        lblDue.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDue.setForeground(new java.awt.Color(255, 0, 0));
        lblDue.setText("0.00");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
        jPanel6.add(lblDue, gridBagConstraints);

        lblOther.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblOther.setForeground(new java.awt.Color(255, 255, 255));
        lblOther.setText("0.00");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
        jPanel6.add(lblOther, gridBagConstraints);

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel15.setText("Remark");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(tabPayment)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 413, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tabPayment, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );

        jSplitPane1.setLeftComponent(jPanel5);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(jSplitPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jSplitPane1)
                .addGap(0, 0, 0)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCreateDocumentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateDocumentActionPerformed
        createInvoice();
    }//GEN-LAST:event_btnCreateDocumentActionPerformed

    private void txtCHFeeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCHFeeFocusGained
        txtCHFee.selectAll();
    }//GEN-LAST:event_txtCHFeeFocusGained

    private void txtPostageFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPostageFocusGained
        txtPostage.selectAll();
    }//GEN-LAST:event_txtPostageFocusGained

    private void txtOtherFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtOtherFocusGained
        txtOther.selectAll();
    }//GEN-LAST:event_txtOtherFocusGained

    private void btnSubmitPaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubmitPaymentActionPerformed
        processPayment(invoice);
    }//GEN-LAST:event_btnSubmitPaymentActionPerformed

    private void txtCHFeeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCHFeeFocusLost
        String _chFee = txtCHFee.getText();
        if (_chFee != null && !_chFee.isEmpty()) {
            invoice.setAdditionalChargeLines(createOtherChargeLine());
            displayBalance(invoice);
        }
    }//GEN-LAST:event_txtCHFeeFocusLost

    private void txtPostageFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPostageFocusLost
        String _postage = txtPostage.getText();
        if (_postage != null && !_postage.isEmpty()) {
            invoice.setAdditionalChargeLines(createOtherChargeLine());
            displayBalance(invoice);
        }
    }//GEN-LAST:event_txtPostageFocusLost

    private void txtOtherFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtOtherFocusLost
        String _other = txtOther.getText();
        if (_other != null && !_other.isEmpty()) {
            invoice.setAdditionalChargeLines(createOtherChargeLine());
            displayBalance(invoice);
        }
    }//GEN-LAST:event_txtOtherFocusLost

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        report("PRINT");
    }//GEN-LAST:event_btnPrintActionPerformed

    private void btnEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmailActionPerformed
        String receipent = client.getEmail();
        String subject = "Invoice".concat(" From ").concat(Application.getMainAgent().getName());
        String body = "Invoice".concat(" From ").concat(Application.getMainAgent().getName());
        String refference = this.invoice.getReference().toString();
        if (receipent != null) {

            BeanJasperReport jasperreport = new BeanJasperReport(receipent, subject, body, refference);
            List<OtherInvoiceModel> list = new ArrayList<>();
            OtherInvoiceModel model = OtherInvoiceModel.createModel(invoice);
            list.add(model);
            jasperreport.invoice(list, Enums.SaleType.OTHERSALES, "EMAIL");
        } else {
            JOptionPane.showMessageDialog(null, "No Email address", "Email", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnEmailActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        Window w = SwingUtilities.getWindowAncestor(this);
        Frame owner = w instanceof Frame ? (Frame) w : null;
        DlgOtherSalesCreditTransfer dlg = new DlgOtherSalesCreditTransfer(owner);
        if (dlg.showDialog(invoice)) {
            loadInvoice(invoice.getId());
            resetPaymentComponent();
        }
    }//GEN-LAST:event_jButton6ActionPerformed

    private void btnOfficeCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOfficeCopyActionPerformed
        report("VIEW");
    }//GEN-LAST:event_btnOfficeCopyActionPerformed

    private void btnNewDocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewDocActionPerformed
        newSalesAcDocDlg(this.invoice);
    }//GEN-LAST:event_btnNewDocActionPerformed

    private void btnVoidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVoidActionPerformed
       _voidDocument();
    }//GEN-LAST:event_btnVoidActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.ets.fe.acdoc.gui.comp.AcDocHeaderComponent acDocHeaderComponent;
    private javax.swing.JButton btnCreateDocument;
    private javax.swing.JButton btnEmail;
    private javax.swing.JButton btnNewDoc;
    private javax.swing.JButton btnOfficeCopy;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnSubmitPayment;
    private javax.swing.JButton btnVoid;
    private org.jdesktop.swingx.JXBusyLabel busyLabel;
    private com.ets.fe.a_main.ClientSearchComponent clientComponent;
    private javax.swing.JComboBox cmbTType;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
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
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblAddCharge;
    private javax.swing.JLabel lblDue;
    private javax.swing.JLabel lblInvAmount;
    private javax.swing.JLabel lblOther;
    private javax.swing.JLabel lblSubTotal;
    private javax.swing.JLabel lblTDueRefund;
    private javax.swing.JLabel lblTotalPayment;
    private com.ets.fe.acdoc_o.gui.comp.OtherServiceComp otherServiceComp;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JTabbedPane tabPayment;
    private javax.swing.JTable tblPayment;
    private javax.swing.JTextField txtAmount;
    private javax.swing.JTextField txtCHFee;
    private javax.swing.JTextField txtOther;
    private javax.swing.JTextField txtPostage;
    private javax.swing.JTextField txtRef;
    private javax.swing.JTextArea txtRemark;
    // End of variables declaration//GEN-END:variables

    private void setPaymentType() {
        DefaultComboBoxModel model = new DefaultComboBoxModel(Enums.PaymentType.values());
        model.insertElementAt("Select", 0);
        cmbTType.setModel(model);
        cmbTType.setSelectedIndex(0);
    }

    private void _voidDocument() {
        taskType = "VOID";
        int index = tblPayment.getSelectedRow();
        if (index != -1) {
            Long id = this.invoice.getRelatedDocuments().get(index).getId();
            OtherSalesAcDoc doc = new OtherSalesAcDoc();
            doc.setId(id);
            doc.recordUpdateBy();
            accountingDocTask = new AccountingDocTaskOther(doc, Enums.SaleType.OTHERSALES, "VOID");
            accountingDocTask.addPropertyChangeListener(this);
            accountingDocTask.execute();
        }else{
         JOptionPane.showMessageDialog (null, "Select a document to VOID.", "VOID Document", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void newSalesAcDocDlg(OtherSalesAcDoc invoice) {
        Window w = SwingUtilities.getWindowAncestor(this);
        Frame owner = w instanceof Frame ? (Frame) w : null;

        OtherSalesAcDocumentDlg dlg = new OtherSalesAcDocumentDlg(owner);
        dlg.setLocationRelativeTo(this);
        if (dlg.showDialog(invoice)) {
            loadInvoice(invoice.getId());
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress".equals(evt.getPropertyName())) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
            if (progress == 100) {
                try {
                    if ("CREATE".equals(taskType)) {
                        invoice = newInvoiceTask.get();
                        showDialog(invoice.getId());
                    } else if ("PAYMENT".equals(taskType)) {
                        busyLabel.setBusy(false);
                        btnSubmitPayment.setEnabled(true);
                        loadInvoice(invoice.getId());
                        resetPaymentComponent();
                    } else if ("COMPLETE".equals(taskType)) {
                        invoice = (OtherSalesAcDoc) accountingDocTask.get();
                        displayInvoice(invoice);
                        taskType = "";
                    }else if ("VOID".equals(taskType)) {
                        busyLabel.setBusy(false);                        
                        loadInvoice(invoice.getId());                        
                    } 
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(SalesInvoiceDlg.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                }
            }
        }
    }

    private void controllComponent(OtherSalesAcDoc invoice) {
        if (invoice.getId() == null) {
            btnCreateDocument.setEnabled(true);
            btnEmail.setEnabled(false);
            btnPrint.setEnabled(false);
            btnOfficeCopy.setEnabled(false);
            btnVoid.setEnabled(false);
            btnNewDoc.setEnabled(false);

            allowPayment = false;
            tabPayment.setEnabledAt(1, allowPayment);
            txtCHFee.setEditable(true);
            txtPostage.setEditable(true);
            txtOther.setEditable(true);
            txtRemark.setEditable(true);
        } else {
            btnCreateDocument.setEnabled(false);
            btnEmail.setEnabled(true);
            btnPrint.setEnabled(true);
            btnOfficeCopy.setEnabled(true);
            btnVoid.setEnabled(true);
            btnNewDoc.setEnabled(true);

            txtCHFee.setEditable(false);
            txtPostage.setEditable(false);
            txtOther.setEditable(false);
            txtRemark.setEditable(false);

            if (invoice.calculateDueAmount().compareTo(new BigDecimal("0.00")) == 0) {
                allowPayment = false;
                tabPayment.setEnabledAt(1, allowPayment);
                tabPayment.setSelectedIndex(0);
            } else {
                allowPayment = true;
                tabPayment.setEnabledAt(1, allowPayment);
                tabPayment.setSelectedIndex(0);
            }
        }
    }

    private void resetPaymentComponent() {
        tabPayment.setSelectedIndex(0);
        setPaymentType();
        txtAmount.setText("");
        txtRef.setText("");
    }

    public OtherSalesAcDoc getInvoice() {
        return this.invoice;
    }

    private void report(String action) {
        BeanJasperReport jasperreport = new BeanJasperReport();
        List<OtherInvoiceModel> list = new ArrayList<>();
        OtherInvoiceModel model = OtherInvoiceModel.createModel(invoice);
        list.add(model);
        jasperreport.invoice(list, Enums.SaleType.OTHERSALES, action);
    }
}
