package com.ets.fe.acdoc.gui;

import com.ets.fe.a_main.PnrPanel;
import com.ets.fe.a_main.TicketingAgentComponent;
import com.ets.fe.acdoc.bo.PaymentLogic;
import com.ets.fe.acdoc.model.AccountingDocument;
import com.ets.fe.acdoc.model.TicketingSalesAcDoc;
import com.ets.fe.acdoc.model.TicketingSalesPayment;
import com.ets.fe.acdoc.task.AccountingDocTask;
import com.ets.fe.util.CheckInput;
import com.ets.fe.util.Enums;
import com.ets.fe.util.Enums.PaymentType;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author Yusuf
 */
public class PaymentComponent extends javax.swing.JPanel implements PropertyChangeListener {

    private TicketingSalesAcDoc doc;
    private AccountingDocTask task;
    private String taskType = "PAYMENT";
    private PnrPanel parent;
    private List<TicketingSalesAcDoc> invoices;

    public PaymentComponent(List<TicketingSalesAcDoc> invoices) {
        initComponents();
        setPaymentType();
        this.invoices = invoices;
        CheckInput a = new CheckInput();
        a.setNegativeAccepted(true);
        txtAmount.setDocument(a);
    }

    public void processPayment() {
        String amountString = txtAmount.getText();
        String remark = txtRef.getText();
        PaymentType type = (PaymentType) cmbTType.getSelectedItem();

        if (!amountString.isEmpty() && !remark.isEmpty() && cmbTType.getSelectedIndex() <= 0) {
            List<TicketingSalesAcDoc> invoices = new ArrayList<>();
            invoices.add(doc);
            BigDecimal amount = new BigDecimal(amountString.trim());
            PaymentLogic logic = new PaymentLogic();

            if (amount.compareTo(logic.calculateTotalInvoiceAmount(invoices)) > 0) {
                TicketingSalesPayment docs = logic.processPayment(amount, invoices, remark, type);
            } else {
                busyLabel.setText("Warning! Excess payment");
            }
        }
    }

    private void setPaymentType() {
        DefaultComboBoxModel model = new DefaultComboBoxModel(Enums.PaymentType.values());
        cmbTType.setModel(model);
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

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        cmbTType = new javax.swing.JComboBox();
        txtAmount = new javax.swing.JTextField();
        txtRef = new javax.swing.JTextField();
        btnSubmit = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        lblTDueRefund = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        busyLabel = new org.jdesktop.swingx.JXBusyLabel();

        setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("T.Type");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        add(jLabel1, gridBagConstraints);

        jLabel2.setText("Amount");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        add(jLabel2, gridBagConstraints);

        jLabel3.setText("Remark");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        add(jLabel3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 0, 2);
        add(cmbTType, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 2);
        add(txtAmount, gridBagConstraints);

        txtRef.setPreferredSize(new java.awt.Dimension(50, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 4, 2);
        add(txtRef, gridBagConstraints);

        btnSubmit.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnSubmit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/payment24.png"))); // NOI18N
        btnSubmit.setText("Submit");
        btnSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSubmitActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(btnSubmit, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("Available Credit:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(jLabel4, gridBagConstraints);

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
        add(lblTDueRefund, gridBagConstraints);

        jButton2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/credit24.png"))); // NOI18N
        jButton2.setText("Apply Credit");
        jButton2.setPreferredSize(new java.awt.Dimension(130, 31));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 2, 2);
        add(jButton2, gridBagConstraints);

        busyLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        busyLabel.setDirection(null);
        busyLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(busyLabel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void btnSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubmitActionPerformed
      processPayment();
    }//GEN-LAST:event_btnSubmitActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSubmit;
    private org.jdesktop.swingx.JXBusyLabel busyLabel;
    private javax.swing.JComboBox cmbTType;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel lblTDueRefund;
    private javax.swing.JTextField txtAmount;
    private javax.swing.JTextField txtRef;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress".equals(evt.getPropertyName())) {
            int progress = (Integer) evt.getNewValue();
            if (progress == 100) {
                try {
                    if ("SUMMERY".equals(taskType)) {

                        AccountingDocument doc = task.get();

                        if (doc instanceof TicketingSalesAcDoc) {
                            doc = (TicketingSalesAcDoc) doc;
                        } else {

                        }

                        //populateTblSales();
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(TicketingAgentComponent.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    taskType = "";
                }
            }
        }
    }
}
