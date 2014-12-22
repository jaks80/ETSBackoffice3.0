package com.ets.fe.components;

import com.ets.fe.pnr.model.Ticket;
import com.ets.fe.util.CheckInput;
import com.ets.fe.util.Enums;
import java.awt.Color;
import java.math.BigDecimal;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 *
 * @author Yusuf
 */
public class TicketComponent extends javax.swing.JPanel {

    private boolean editable;
    private Ticket ticket;

    public TicketComponent() {
        initComponents();

        CheckInput a = new CheckInput();
        CheckInput b = new CheckInput();
        CheckInput c = new CheckInput();
        CheckInput d = new CheckInput();
        CheckInput e = new CheckInput();
        CheckInput f = new CheckInput();
        CheckInput g = new CheckInput();

        a.setNegativeAccepted(true);
        b.setNegativeAccepted(true);
        c.setNegativeAccepted(true);
        d.setNegativeAccepted(true);
        e.setNegativeAccepted(true);
        f.setNegativeAccepted(true);
        g.setNegativeAccepted(true);

        txtBaseFare.setDocument(a);
        txtTax.setDocument(b);
        txtBspCom.setDocument(c);
        txtGrossFare.setDocument(d);
        txtDisc.setDocument(e);
        txtAtol.setDocument(f);
        txtAirlineCode.setDocument(g);
        cmbTicketStatus();
    }

    private void cmbTicketStatus() {
        DefaultComboBoxModel model = new DefaultComboBoxModel(Enums.TicketStatus.values());
        cmbStatus.setModel(model);
        //cmbStatus.setSelectedItem(ticket.getTktStatus());
    }

    public void displayTicket(Ticket ticket) {
        this.ticket = ticket;
        txtName.setText(this.ticket.getPaxSurName() + "/" + this.ticket.getPaxForeName());
        txtAirlineCode.setText(this.ticket.getNumericAirLineCode());
        txtTktNo.setText(this.ticket.getTicketNo());
        cmbStatus.setSelectedItem(this.ticket.getTktStatus());
        dtIssueDate.setDate(this.ticket.getDocIssuedate());

        txtBaseFare.setText(this.ticket.getBaseFare().toString());
        txtTax.setText(this.ticket.getTax().toString());
        txtBspCom.setText(this.ticket.getCommission().toString());
        txtNetPurchaseFare.setText(this.ticket.getBaseFare().add(this.ticket.getTax()).toString());

        txtGrossFare.setText(this.ticket.getGrossFare().toString());
        txtDisc.setText(this.ticket.getDiscount().toString());
        txtAtol.setText(this.ticket.getAtolChg().toString());
        txtNetSellingFare.setText(this.ticket.getNetSellingFare().toString());

        lblRevenue.setText(this.ticket.calculateRevenue().toString());
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
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        lblRevenue = new javax.swing.JLabel();
        txtAirlineCode = new javax.swing.JTextField();
        txtBaseFare = new javax.swing.JTextField();
        txtTax = new javax.swing.JTextField();
        txtBspCom = new javax.swing.JTextField();
        txtNetPurchaseFare = new javax.swing.JTextField();
        txtGrossFare = new javax.swing.JTextField();
        txtDisc = new javax.swing.JTextField();
        txtAtol = new javax.swing.JTextField();
        txtNetSellingFare = new javax.swing.JTextField();
        cmbStatus = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtName = new javax.swing.JTextArea();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        txtTktNo = new javax.swing.JTextField();
        dtIssueDate = new org.jdesktop.swingx.JXDatePicker();

        setPreferredSize(new java.awt.Dimension(245, 365));
        setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Ticket");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(jLabel1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 4, 0);
        add(jSeparator1, gridBagConstraints);

        jLabel2.setText("Name");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(jLabel2, gridBagConstraints);

        jLabel3.setText("TktNo");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(jLabel3, gridBagConstraints);

        jLabel4.setText("Status");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(jLabel4, gridBagConstraints);

        jLabel5.setText("B.Fare");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(jLabel5, gridBagConstraints);

        jLabel6.setText("Tax");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(jLabel6, gridBagConstraints);

        jLabel7.setText("Com");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(jLabel7, gridBagConstraints);

        jLabel8.setText("NetFare");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(jLabel8, gridBagConstraints);

        jLabel9.setText("GrsFare");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(jLabel9, gridBagConstraints);

        jLabel10.setText("Disc");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(jLabel10, gridBagConstraints);

        jLabel11.setText("ATOL");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(jLabel11, gridBagConstraints);

        jLabel12.setText("NetSelling");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(jLabel12, gridBagConstraints);

        lblRevenue.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblRevenue.setText("Revenue: 0.00");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(lblRevenue, gridBagConstraints);

        txtAirlineCode.setToolTipText("Airline Code");
        txtAirlineCode.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtAirlineCodeFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(txtAirlineCode, gridBagConstraints);

        txtBaseFare.setMinimumSize(new java.awt.Dimension(70, 20));
        txtBaseFare.setPreferredSize(new java.awt.Dimension(70, 20));
        txtBaseFare.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtBaseFareFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(txtBaseFare, gridBagConstraints);

        txtTax.setMinimumSize(new java.awt.Dimension(70, 20));
        txtTax.setPreferredSize(new java.awt.Dimension(70, 20));
        txtTax.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtTaxFocusLost(evt);
            }
        });
        txtTax.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTaxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(txtTax, gridBagConstraints);

        txtBspCom.setMinimumSize(new java.awt.Dimension(70, 20));
        txtBspCom.setPreferredSize(new java.awt.Dimension(70, 20));
        txtBspCom.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtBspComFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(txtBspCom, gridBagConstraints);

        txtNetPurchaseFare.setEditable(false);
        txtNetPurchaseFare.setBackground(new java.awt.Color(0, 0, 0));
        txtNetPurchaseFare.setMinimumSize(new java.awt.Dimension(70, 20));
        txtNetPurchaseFare.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(txtNetPurchaseFare, gridBagConstraints);

        txtGrossFare.setMinimumSize(new java.awt.Dimension(70, 20));
        txtGrossFare.setPreferredSize(new java.awt.Dimension(70, 20));
        txtGrossFare.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtGrossFareFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(txtGrossFare, gridBagConstraints);

        txtDisc.setMinimumSize(new java.awt.Dimension(70, 20));
        txtDisc.setPreferredSize(new java.awt.Dimension(70, 20));
        txtDisc.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDiscFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(txtDisc, gridBagConstraints);

        txtAtol.setMinimumSize(new java.awt.Dimension(70, 20));
        txtAtol.setPreferredSize(new java.awt.Dimension(70, 20));
        txtAtol.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtAtolFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(txtAtol, gridBagConstraints);

        txtNetSellingFare.setEditable(false);
        txtNetSellingFare.setBackground(new java.awt.Color(0, 0, 0));
        txtNetSellingFare.setMinimumSize(new java.awt.Dimension(70, 20));
        txtNetSellingFare.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(txtNetSellingFare, gridBagConstraints);

        cmbStatus.setToolTipText("");
        cmbStatus.setMaximumSize(new java.awt.Dimension(32767, 19));
        cmbStatus.setMinimumSize(new java.awt.Dimension(28, 19));
        cmbStatus.setPreferredSize(new java.awt.Dimension(28, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(cmbStatus, gridBagConstraints);

        txtName.setEditable(false);
        txtName.setColumns(12);
        txtName.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        txtName.setLineWrap(true);
        txtName.setRows(2);
        txtName.setToolTipText("Surname / Forename(s)");
        jScrollPane1.setViewportView(txtName);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(jScrollPane1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        add(jSeparator2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        add(jSeparator3, gridBagConstraints);

        txtTktNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtTktNoFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(txtTktNo, gridBagConstraints);

        dtIssueDate.setToolTipText("Date: Booking / Issue / Refund");
        dtIssueDate.setMaximumSize(new java.awt.Dimension(108, 20));
        dtIssueDate.setMinimumSize(new java.awt.Dimension(108, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(dtIssueDate, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void txtTaxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTaxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTaxActionPerformed

    private void txtAirlineCodeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAirlineCodeFocusLost
        this.ticket.setNumericAirLineCode(checkValue(txtAirlineCode));
    }//GEN-LAST:event_txtAirlineCodeFocusLost

    private void txtBaseFareFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBaseFareFocusLost
        this.ticket.setBaseFare(new BigDecimal(checkValue(txtBaseFare)));
    }//GEN-LAST:event_txtBaseFareFocusLost

    private void txtTaxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTaxFocusLost
        this.ticket.setTax(new BigDecimal(checkValue(txtTax)));
    }//GEN-LAST:event_txtTaxFocusLost

    private void txtBspComFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBspComFocusLost
        this.ticket.setTax(new BigDecimal(checkValue(txtTax)));
    }//GEN-LAST:event_txtBspComFocusLost

    private void txtGrossFareFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtGrossFareFocusLost
        this.ticket.setGrossFare(new BigDecimal(checkValue(txtGrossFare)));
    }//GEN-LAST:event_txtGrossFareFocusLost

    private void txtDiscFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDiscFocusLost
        this.ticket.setDiscount(new BigDecimal(checkValue(txtDisc)));
    }//GEN-LAST:event_txtDiscFocusLost

    private void txtAtolFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAtolFocusLost
        this.ticket.setAtolChg(new BigDecimal(checkValue(txtAtol)));
    }//GEN-LAST:event_txtAtolFocusLost

    private void txtTktNoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTktNoFocusLost

    }//GEN-LAST:event_txtTktNoFocusLost


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cmbStatus;
    private org.jdesktop.swingx.JXDatePicker dtIssueDate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JLabel lblRevenue;
    private javax.swing.JTextField txtAirlineCode;
    private javax.swing.JTextField txtAtol;
    private javax.swing.JTextField txtBaseFare;
    private javax.swing.JTextField txtBspCom;
    private javax.swing.JTextField txtDisc;
    private javax.swing.JTextField txtGrossFare;
    private javax.swing.JTextArea txtName;
    private javax.swing.JTextField txtNetPurchaseFare;
    private javax.swing.JTextField txtNetSellingFare;
    private javax.swing.JTextField txtTax;
    private javax.swing.JTextField txtTktNo;
    // End of variables declaration//GEN-END:variables

    private String checkValue(JTextField jTextField) {
        String val = txtAirlineCode.getText();
        if (val != null && !val.isEmpty()) {
            val = val.trim();
            jTextField.setBackground(Color.WHITE);
        } else {
            jTextField.setBackground(Color.YELLOW);
            jTextField.requestFocus();
        }

        return val;
    }
}