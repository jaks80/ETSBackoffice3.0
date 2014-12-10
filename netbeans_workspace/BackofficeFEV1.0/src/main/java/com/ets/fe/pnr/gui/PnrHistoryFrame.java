package com.ets.fe.pnr.gui;

import com.ets.fe.pnr.model.Pnr;
import com.ets.fe.util.DateUtil;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JInternalFrame;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Yusuf
 */
public class PnrHistoryFrame extends JInternalFrame implements PropertyChangeListener {

    public PnrHistoryFrame() {
        initComponents();

        dtFrom.setDate(DateUtil.getBeginingOfMonth());
        dtTo.setDate(DateUtil.getEndOfMonth());
    }

    private List<Pnr> list = new ArrayList<>();
    private PnrHistoryTask task;

    private void buttonSearchActionPerformed(ActionEvent event) {

        String bookingAgt = txtBookingAgt.getText();
        String ticketingAgt = txtTktingAgt.getText();
        Date from = dtFrom.getDate();
        Date to = dtTo.getDate();

        if (bookingAgt.isEmpty()) {
            bookingAgt = null;
        }

        if (ticketingAgt.isEmpty()) {
            ticketingAgt = null;
        }

        progressBar.setValue(0);

        task = new PnrHistoryTask(bookingAgt, ticketingAgt, from, to);
        task.addPropertyChangeListener(this);
        task.execute();
    }

    private void populateTblPnr() {
        DefaultTableModel tableModel = (DefaultTableModel) tblPnr.getModel();
        tableModel.getDataVector().removeAllElements();
        tblPnr.repaint();
        this.list = task.getList();
        if (list.size() > 0) {
            int i = 0;
            if (list.size() > 0) {
                for (Pnr p : this.list) {
                    tableModel.insertRow(i, new Object[]{p.getGdsPnr(), p.getBookingAgtOid(), p.getTicketingAgtOid(),
                        p.getPnrCreatorAgentSine(), p.getTicketingAgentSine(),
                        DateUtil.dateToString(p.getPnrCreationDate()), DateUtil.dateToString(p.getAirCreationDate()), p.getAirLineCode(), p.getNoOfPax()});
                    i++;
                }
            } else {
                tableModel.insertRow(0, new Object[]{});
            }
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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtBookingAgt = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtTktingAgt = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        dtFrom = new org.jdesktop.swingx.JXDatePicker();
        jLabel4 = new javax.swing.JLabel();
        dtTo = new org.jdesktop.swingx.JXDatePicker();
        btnSearch = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPnr = new org.jdesktop.swingx.JXTable();
        jPanel3 = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        jLabel5 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("PNR History");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Search", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        jLabel1.setText("Booking Agent OID");

        txtBookingAgt.setToolTipText("Booking Agent office Id, Separated by comma, Example: ABC123AB,CDE123CD");

        jLabel2.setText("Issue Agent OID");

        txtTktingAgt.setToolTipText("Ticketing agent office Id, Separated by comma, Example: ABC123AB,CDE123CD");
        txtTktingAgt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTktingAgtActionPerformed(evt);
            }
        });

        jLabel3.setText("Issue Date From");

        jLabel4.setText("Issue Date To");

        btnSearch.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnSearch.setText("Search");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtBookingAgt, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTktingAgt, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dtTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(121, 121, 121))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBookingAgt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(txtTktingAgt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(dtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(dtTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearch))
                .addGap(1, 1, 1))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Search Result", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        tblPnr.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "PNR", "OID Booking", "OID Ticketing", "A.Sine Booking", "A.Sine Ticketing", "Creation Date", "Issue Date", "Career", "Pax"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblPnr.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblPnr);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 331, Short.MAX_VALUE)
        );

        progressBar.setStringPainted(true);

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel5.setText("Pnr Search: Result");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 860, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 115, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        buttonSearchActionPerformed(evt);
    }//GEN-LAST:event_btnSearchActionPerformed

    private void txtTktingAgtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTktingAgtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTktingAgtActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSearch;
    private org.jdesktop.swingx.JXDatePicker dtFrom;
    private org.jdesktop.swingx.JXDatePicker dtTo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JProgressBar progressBar;
    private org.jdesktop.swingx.JXTable tblPnr;
    private javax.swing.JTextField txtBookingAgt;
    private javax.swing.JTextField txtTktingAgt;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
            if (progress == 100) {
                populateTblPnr();
            }
        }
    }
}
