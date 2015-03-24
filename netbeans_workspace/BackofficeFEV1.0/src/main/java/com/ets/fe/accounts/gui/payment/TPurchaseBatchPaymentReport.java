package com.ets.fe.accounts.gui.payment;

import com.ets.fe.acdoc.gui.PurchaseInvoiceDlg;
import com.ets.fe.acdoc.gui.SalesInvoiceDlg;
import com.ets.fe.acdoc.gui.comp.ClientSearchComp;
import com.ets.fe.accounts.model.Payment;
import com.ets.fe.acdoc.model.TicketingPurchaseAcDoc;
import com.ets.fe.accounts.model.Payments;
import com.ets.fe.accounts.task.PaymentTask;
import com.ets.fe.util.DateUtil;
import com.ets.fe.util.Enums;
import java.awt.Frame;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDesktopPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Yusuf
 */
public class TPurchaseBatchPaymentReport extends javax.swing.JInternalFrame implements PropertyChangeListener {

    private List<Payment> payments;
    private PaymentTask task;
    private JDesktopPane desktopPane;

    public TPurchaseBatchPaymentReport(JDesktopPane desktopPane) {        
        this.desktopPane = desktopPane;
        initComponents();
        dtFrom.setDate(DateUtil.getBeginingOfMonth());
        dtTo.setDate(DateUtil.getEndOfMonth());
    }

    private void search() {
        btnSearch.setEnabled(false);
        Long client_id = documentSearchComponent.getClient_id();
        Date from = dtFrom.getDate();
        Date to = dtTo.getDate();

        task = new PaymentTask(Enums.ClientType.AGENT, client_id, from, to, Enums.SaleType.TKTPURCHASE, progressBar);
        task.addPropertyChangeListener(this);
        task.execute();
    }

    private void populateTblPayment() {

        DefaultTableModel tableModel = (DefaultTableModel) tblPayment.getModel();
        tableModel.getDataVector().removeAllElements();

        if (payments.size() > 0) {
            for (int i = 0; i < payments.size(); i++) {
                Payment p = payments.get(i);
                String date = DateUtil.dateToString(p.gettPurchaseAcDocuments().get(0).getDocIssueDate());
                tableModel.insertRow(i, new Object[]{date, p.calculateTotalPurchasePayment().abs(),p.getCreatedByName()});
            }
        } else {
            tableModel.insertRow(0, new Object[]{"", ""});
        }
    }

    private void populatePaymentDocuments(Payment payment){
     DefaultTableModel tableModel = (DefaultTableModel) tblPaymentDocs.getModel();
        tableModel.getDataVector().removeAllElements();
        
        List<TicketingPurchaseAcDoc> docs = payment.gettPurchaseAcDocuments();
        BigDecimal total = new BigDecimal("0.00");
        int i = 0;
        if (docs.size() > 0) {
            for (; i < docs.size(); i++) {
                TicketingPurchaseAcDoc doc = docs.get(i);
                total = total.add(doc.getDocumentedAmount());
                String date = DateUtil.dateToString(doc.getDocIssueDate());
                tableModel.insertRow(i, new Object[]{i+1,doc.getReference(),date,doc.getPnr().getGdsPnr(),doc.getPnr().getAirLineCode(),doc.getDocumentedAmount().abs()});
            }
            tableModel.insertRow(i, new Object[]{"", "","Total:","","",total});
        } else {
            tableModel.insertRow(0, new Object[]{"", "","","","",""});
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

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel4 = new javax.swing.JPanel();
        documentSearchComponent = new com.ets.fe.acdoc.gui.comp.ClientSearchComp(false, false, false,Enums.AgentType.TICKETING_AGT);
        jLabel6 = new javax.swing.JLabel();
        dtFrom = new org.jdesktop.swingx.JXDatePicker();
        jLabel8 = new javax.swing.JLabel();
        dtTo = new org.jdesktop.swingx.JXDatePicker();
        jLabel7 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPayment = new org.jdesktop.swingx.JXTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblPaymentDocs = new org.jdesktop.swingx.JXTable();
        jPanel1 = new javax.swing.JPanel();
        btnViewReport = new javax.swing.JButton();
        btnViewInvoice = new javax.swing.JButton();
        btnEmail = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        btnSearch = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("History: Purchase Payment");

        jSplitPane1.setDividerLocation(200);
        jSplitPane1.setDividerSize(4);

        jLabel6.setText("Date From");

        dtFrom.setPreferredSize(new java.awt.Dimension(110, 20));

        jLabel8.setText("Date To");

        dtTo.setPreferredSize(new java.awt.Dimension(110, 20));

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 0, 0));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Payment History");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(dtFrom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(dtTo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel8))
                        .addContainerGap())
                    .addComponent(documentSearchComponent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39)
                .addComponent(jLabel6)
                .addGap(2, 2, 2)
                .addComponent(dtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addGap(2, 2, 2)
                .addComponent(dtTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(documentSearchComponent, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(82, Short.MAX_VALUE))
        );

        jSplitPane1.setLeftComponent(jPanel4);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        tblPayment.setBackground(new java.awt.Color(255, 255, 204));
        tblPayment.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Date", "Amount", "User"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblPayment.setSortable(false);
        tblPayment.getSelectionModel().addListSelectionListener(tblPaymentListener);
        jScrollPane1.setViewportView(tblPayment);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.weighty = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(jScrollPane1, gridBagConstraints);

        tblPaymentDocs.setBackground(new java.awt.Color(204, 255, 204));
        tblPaymentDocs.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
        tblPaymentDocs.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "Inv Reference", "Date", "PNR", "Airline", "Paid"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblPaymentDocs.setSortable(false);
        tblPaymentDocs.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tblPaymentDocs);
        if (tblPaymentDocs.getColumnModel().getColumnCount() > 0) {
            tblPaymentDocs.getColumnModel().getColumn(0).setMinWidth(40);
            tblPaymentDocs.getColumnModel().getColumn(0).setPreferredWidth(40);
            tblPaymentDocs.getColumnModel().getColumn(0).setMaxWidth(60);
            tblPaymentDocs.getColumnModel().getColumn(4).setMinWidth(60);
            tblPaymentDocs.getColumnModel().getColumn(4).setPreferredWidth(60);
            tblPaymentDocs.getColumnModel().getColumn(4).setMaxWidth(60);
        }

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.8;
        gridBagConstraints.weighty = 0.8;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(jScrollPane2, gridBagConstraints);

        jSplitPane1.setRightComponent(jPanel2);

        jPanel1.setBackground(new java.awt.Color(102, 102, 102));
        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        btnViewReport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/details.png"))); // NOI18N

        btnViewInvoice.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Invoice24.png"))); // NOI18N
        btnViewInvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewInvoiceActionPerformed(evt);
            }
        });

        btnEmail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/email24.png"))); // NOI18N

        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/print24.png"))); // NOI18N

        btnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/search24.png"))); // NOI18N
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(btnViewInvoice)
                .addGap(2, 2, 2)
                .addComponent(btnViewReport)
                .addGap(2, 2, 2)
                .addComponent(btnEmail)
                .addGap(2, 2, 2)
                .addComponent(btnPrint)
                .addGap(2, 2, 2)
                .addComponent(btnSearch)
                .addGap(651, 651, 651))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnViewInvoice, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnViewReport, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnSearch, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanel3.add(jSeparator1, gridBagConstraints);

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(jSplitPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jSplitPane1)
                .addGap(0, 0, 0)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        search();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnViewInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewInvoiceActionPerformed
        int index_pay = tblPayment.getSelectedRow();
        int index_doc = tblPaymentDocs.getSelectedRow();

        if (index_pay != -1 && index_doc != -1) {
            Long id = payments.get(index_pay).gettPurchaseAcDocuments().get(index_doc).getParent().getId();

            Window w = SwingUtilities.getWindowAncestor(this);
            Frame owner = w instanceof Frame ? (Frame) w : null;
            PurchaseInvoiceDlg dlg = new PurchaseInvoiceDlg(owner);            
            dlg.showDialog(id);            
        }
    }//GEN-LAST:event_btnViewInvoiceActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEmail;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnViewInvoice;
    private javax.swing.JButton btnViewReport;
    private com.ets.fe.acdoc.gui.comp.ClientSearchComp documentSearchComponent;
    private org.jdesktop.swingx.JXDatePicker dtFrom;
    private org.jdesktop.swingx.JXDatePicker dtTo;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JProgressBar progressBar;
    private org.jdesktop.swingx.JXTable tblPayment;
    private org.jdesktop.swingx.JXTable tblPaymentDocs;
    // End of variables declaration//GEN-END:variables
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress".equals(evt.getPropertyName())) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
            if (progress == 100) {
                try {
                    payments = new ArrayList<>();
                    Payments pays = task.get();
                    payments = pays.getList();
                    populateTblPayment();
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(TPurchaseBatchPaymentReport.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    btnSearch.setEnabled(true);
                }
            }
        }
    }

    private ListSelectionListener tblPaymentListener = new ListSelectionListener() {

        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int selectedRow = tblPayment.getSelectedRow();
            if (selectedRow != -1) {
                Payment payment = payments.get(selectedRow);
                populatePaymentDocuments(payment);
            }
        }
    };
}
