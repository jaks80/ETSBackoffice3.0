package com.ets.fe.acdoc.gui;

import com.ets.fe.a_main.PnrPanel;
import com.ets.fe.a_main.TicketingAgentComponent;
import com.ets.fe.acdoc.model.AccountingDocument;
import com.ets.fe.acdoc.model.TicketingPurchaseAcDoc;
import com.ets.fe.acdoc.model.TicketingSalesAcDoc;
import com.ets.fe.acdoc.task.AcDocSummeryTask;
import com.ets.fe.acdoc.task.AccountingDocTask;
import com.ets.fe.util.DateUtil;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Yusuf
 */
public class AccountingDocumentsComponent extends javax.swing.JPanel implements PropertyChangeListener {

    private AcDocSummeryTask summeryTask;
    private AccountingDocTask accountingDocTask;
    private String taskType;
    private final PnrPanel parent;

    private Long pnrId;
    private List<TicketingSalesAcDoc> tSAcDocList = new ArrayList<>();
    private List<TicketingPurchaseAcDoc> tPAcDocList = new ArrayList<>();
    private TicketingSalesAcDoc ticketingSalesAcDoc;
    private TicketingPurchaseAcDoc ticketingPurchaseAcDoc;

    public AccountingDocumentsComponent(PnrPanel parent) {
        this.parent = parent;
        initComponents();
    }

    public void getSalesInvoice() {
        taskType = "COMPLETE";
        if (tablAcDoc.getSelectedIndex() == 0) {
            int index = tblSales.getSelectedRow();
            if (index != -1) {
                Long id = tSAcDocList.get(index).getId();
                accountingDocTask = new AccountingDocTask(id, "SALES", "DETAILS");
                accountingDocTask.addPropertyChangeListener(this);
                accountingDocTask.execute();
            }
        }
    }

    private void _voidDocument() {
        taskType = "VOID";
        if (tablAcDoc.getSelectedIndex() == 0) {
            int index = tblSales.getSelectedRow();
            if (index != -1) {
                Long id = tSAcDocList.get(index).getId();
                accountingDocTask = new AccountingDocTask(id, "SALES", "VOID");
                accountingDocTask.addPropertyChangeListener(this);
                accountingDocTask.execute();
            }
        }
    }

    public void search(Long pnrId) {
        taskType = "SUMMERY";
        this.pnrId = pnrId;
        summeryTask = new AcDocSummeryTask(pnrId);
        summeryTask.addPropertyChangeListener(this);
        summeryTask.execute();
    }

    private void populateTblSales() {
        DefaultTableModel model = (DefaultTableModel) tblSales.getModel();
        model.getDataVector().removeAllElements();
        int row = 0;
        for (TicketingSalesAcDoc s : tSAcDocList) {
            model.insertRow(row, new Object[]{s.getAcDoctype(), s.getAcDocRef(), DateUtil.dateToString(s.getDocIssueDate()), s.getDocumentedAmount(), ""});
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

        tablAcDoc = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblSales = new org.jdesktop.swingx.JXTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jXTable2 = new org.jdesktop.swingx.JXTable();
        jButton2 = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        btnVoid = new javax.swing.JButton();

        tblSales.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Type", "Refference", "Issue Date", "Amount", "Issued By"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblSales.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblSales);

        tablAcDoc.addTab("Sales Documents", jScrollPane1);

        jXTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Type", "Refference", "Issue Date", "Amount"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jXTable2.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(jXTable2);

        tablAcDoc.addTab("Purchase Documents", jScrollPane2);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/details.png"))); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/print24.png"))); // NOI18N

        btnVoid.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/void24.png"))); // NOI18N
        btnVoid.setMaximumSize(new java.awt.Dimension(55, 30));
        btnVoid.setMinimumSize(new java.awt.Dimension(55, 30));
        btnVoid.setPreferredSize(new java.awt.Dimension(55, 30));
        btnVoid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVoidActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnVoid, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tablAcDoc, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPrint)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 76, Short.MAX_VALUE)
                        .addComponent(btnVoid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(tablAcDoc, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        getSalesInvoice();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void btnVoidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVoidActionPerformed
        // TODO add your handling code here
    }//GEN-LAST:event_btnVoidActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnVoid;
    private javax.swing.JButton jButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private org.jdesktop.swingx.JXTable jXTable2;
    private javax.swing.JTabbedPane tablAcDoc;
    private org.jdesktop.swingx.JXTable tblSales;
    // End of variables declaration//GEN-END:variables
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress".equals(evt.getPropertyName())) {
            int progress = (Integer) evt.getNewValue();
            if (progress == 100) {
                try {
                    if ("SUMMERY".equals(taskType)) {
                        tSAcDocList = new ArrayList<>();
                        tPAcDocList = new ArrayList<>();
                        List<AccountingDocument> docs = summeryTask.get();

                        for (AccountingDocument doc : docs) {
                            if (doc instanceof TicketingSalesAcDoc) {
                                tSAcDocList.add((TicketingSalesAcDoc) doc);
                            } else {
                                tPAcDocList.add((TicketingPurchaseAcDoc) doc);
                            }
                        }

                        populateTblSales();
                    } else if ("COMPLETE".equals(taskType)) {
                        AccountingDocument doc = accountingDocTask.get();

                        if (doc instanceof TicketingSalesAcDoc) {
                            ticketingSalesAcDoc = (TicketingSalesAcDoc) doc;
                        } else {
                            ticketingPurchaseAcDoc = (TicketingPurchaseAcDoc) doc;
                        }
                        parent.showTSalesInvoiceDlg(ticketingSalesAcDoc);
                    } else if ("VOID".equals(taskType)) {
                        search(pnrId);
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
