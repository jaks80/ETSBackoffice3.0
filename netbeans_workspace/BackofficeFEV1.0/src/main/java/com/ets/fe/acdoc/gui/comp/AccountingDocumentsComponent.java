package com.ets.fe.acdoc.gui.comp;

import com.ets.fe.a_main.PnrPanel;
import com.ets.fe.a_main.TicketingAgentComponent;
import com.ets.fe.acdoc.model.*;
import com.ets.fe.acdoc.task.AcDocSummeryTask;
import com.ets.fe.acdoc.task.AccountingDocTask;
import com.ets.fe.util.DateUtil;
import com.ets.fe.util.Enums;
import com.ets.fe.util.Enums.AcDocType;
import com.ets.fe.util.Enums.SaleType;
import java.awt.Component;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import org.jdesktop.swingx.JXTable;

/**
 *
 * @author Yusuf
 */
public class AccountingDocumentsComponent extends javax.swing.JPanel implements PropertyChangeListener {

    private AcDocSummeryTask summeryTask;
    private AccountingDocTask accountingDocTask;
    private String taskType;
    private String saleType = "SALES";
    private PnrPanel parent = null;

    private Long pnrId;
    private List<TicketingSalesAcDoc> tSAcDocList = new ArrayList<>();
    private List<TicketingPurchaseAcDoc> tPAcDocList = new ArrayList<>();

    private TicketingSalesAcDoc ticketingSalesAcDoc;
    private TicketingSalesAcDoc salesSummeryInvoice;

    private TicketingPurchaseAcDoc ticketingPurchaseAcDoc;
    private TicketingPurchaseAcDoc purchaseSummeryInvoice;

    public AccountingDocumentsComponent() {
        initComponents();
    }

    public AccountingDocumentsComponent(PnrPanel parent) {
        this.parent = parent;
        initComponents();
    }

    public void getDocument() {
        taskType = "COMPLETE";
        int index = 0;
        Long id = null;

        if (null != saleType) {
            switch (saleType) {
                case "SALES":
                    index = tblSales.getSelectedRow();
                    id = tSAcDocList.get(index).getId();
                    if (index != -1) {
                        if (!tSAcDocList.get(index).getType().equals(AcDocType.PAYMENT)) {
                            accountingDocTask = new AccountingDocTask(id, SaleType.TKTSALES, "DETAILS");
                            accountingDocTask.addPropertyChangeListener(this);
                            accountingDocTask.execute();
                        }
                    }
                    break;
                case "PURCHASE":
                    index = tblPurchase.getSelectedRow();
                    id = tPAcDocList.get(index).getId();
                    if (index != -1) {
                        if (!tPAcDocList.get(index).getType().equals(AcDocType.PAYMENT)) {
                            accountingDocTask = new AccountingDocTask(id, SaleType.TKTPURCHASE, "DETAILS");
                            accountingDocTask.addPropertyChangeListener(this);
                            accountingDocTask.execute();
                        }
                    }
                    break;
            }
        }
    }

    private void deleteDocument() {
        taskType = "DELETE";
        AccountingDocument doc = null;
        int index = -1;
        SaleType saleType = null;
        if (tabAcDoc.getSelectedIndex() == 0) {
            index = tblSales.getSelectedRow();
            saleType = SaleType.TKTSALES;
            if (index != -1) {
                doc = tSAcDocList.get(index);
                if (doc.getType().equals(Enums.AcDocType.PAYMENT)
                        || doc.getType().equals(Enums.AcDocType.REFUND)) {
                    JOptionPane.showMessageDialog(null, "Delete Payment in Payment History", "Delete Payment", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                doc.recordUpdateBy();
            }
        } else if (tabAcDoc.getSelectedIndex() == 1) {
            index = tblPurchase.getSelectedRow();
            saleType = SaleType.TKTPURCHASE;
            if (index != -1) {
                doc = tPAcDocList.get(index);
                if (doc.getType().equals(Enums.AcDocType.PAYMENT) || doc.getType().equals(Enums.AcDocType.REFUND)) {
                    JOptionPane.showMessageDialog(null, "Delete Payment in Payment History", "Delete Payment", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                doc.recordUpdateBy();
            }
        }

        if (doc != null && doc.getStatus().equals(Enums.AcDocStatus.VOID)) {
            accountingDocTask = new AccountingDocTask(doc, saleType, "DELETE");
            accountingDocTask.addPropertyChangeListener(this);
            accountingDocTask.execute();
        }
    }

    private void _voidDocument() {
        taskType = "VOID";
        AccountingDocument doc = null;
        int index = -1;
        SaleType saleType = null;
        if (tabAcDoc.getSelectedIndex() == 0) {
            index = tblSales.getSelectedRow();
            saleType = SaleType.TKTSALES;
            if (index != -1) {
                doc = new TicketingSalesAcDoc();
                Long id = tSAcDocList.get(index).getId();
                doc.setId(id);
                doc.recordUpdateBy();
            }
        } else if (tabAcDoc.getSelectedIndex() == 1) {
            index = tblPurchase.getSelectedRow();
            saleType = SaleType.TKTPURCHASE;
            if (index != -1) {
                doc = new TicketingPurchaseAcDoc();
                Long id = tPAcDocList.get(index).getId();
                doc.setId(id);
                doc.recordUpdateBy();
            }
        }

        if (doc != null) {
            accountingDocTask = new AccountingDocTask(doc, saleType, "VOID");
            accountingDocTask.addPropertyChangeListener(this);
            accountingDocTask.execute();
        }
    }

    public void search(Long pnrId) {
        taskType = "SUMMERY";
        this.pnrId = pnrId;
        summeryTask = new AcDocSummeryTask(pnrId, saleType);
        summeryTask.addPropertyChangeListener(this);
        summeryTask.execute();
    }

    private void populateTblSales() {
        DefaultTableModel model = (DefaultTableModel) tblSales.getModel();
        model.getDataVector().removeAllElements();
        int row = 0;
        if (!tSAcDocList.isEmpty()) {
            for (TicketingSalesAcDoc s : tSAcDocList) {
                BigDecimal amount = s.getDocumentedAmount();
                if (s.getType().equals(AcDocType.PAYMENT)) {
                    amount = amount.abs();
                }
                model.insertRow(row, new Object[]{s.getType(), s.getReference(),
                    DateUtil.dateToString(s.getDocIssueDate()), amount, s.getCreatedBy().calculateFullName(), s.getStatus()});
                row++;
            }
            tblSales.setRowSelectionInterval(0, 0);
        } else {
            model.insertRow(row, new Object[]{"", "", "", "", "", ""});
            //tblSales.setRowSelectionInterval(-1, -1);
        }        
    }

    private void populateTblPurchase() {
        DefaultTableModel model = (DefaultTableModel) tblPurchase.getModel();
        model.getDataVector().removeAllElements();
        int row = 0;
        if (!tPAcDocList.isEmpty()) {
            for (TicketingPurchaseAcDoc s : tPAcDocList) {
                BigDecimal amount = s.getDocumentedAmount();
                if (s.getType().equals(AcDocType.PAYMENT)) {
                    amount = amount.abs();
                }
                model.insertRow(row, new Object[]{s.getType(), s.getReference(), s.getVendorRef(),
                    DateUtil.dateToString(s.getDocIssueDate()), amount, s.getStatus()});
                row++;
            }
            tblPurchase.setRowSelectionInterval(0, 0);
        } else {
            model.insertRow(row, new Object[]{"", "", "", "", "", ""});
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

        tabAcDoc = new javax.swing.JTabbedPane();
        tabAcDoc.addChangeListener(tabListener);
        jScrollPane1 = new javax.swing.JScrollPane();
        tblSales = new JXTable(){
            public Component prepareRenderer(TableCellRenderer renderer,int rowIndex, int vColIndex) 
            {
                Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
                String s = this.getModel().getValueAt(rowIndex, 5).toString();
                if(s.equalsIgnoreCase("VOID")){
                    Map  attributes = c.getFont().getAttributes();
                    attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
                    Font newFont = new Font(attributes);
                    c.setFont(newFont);}
                return c;} 
        };
        jScrollPane2 = new javax.swing.JScrollPane();
        tblPurchase = new JXTable(){
            public Component prepareRenderer(TableCellRenderer renderer,int rowIndex, int vColIndex) 
            {
                Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
                String s = this.getModel().getValueAt(rowIndex, 5).toString();
                if(s.equalsIgnoreCase("VOID")){
                    Map  attributes = c.getFont().getAttributes();
                    attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
                    Font newFont = new Font(attributes);
                    c.setFont(newFont);}
                return c;} 
        };
        btnViewDocument = new javax.swing.JButton();
        btnVoid = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();

        tblSales.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Type", "Refference", "Issue Date", "Amount", "Issued By", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblSales.setSortable(false);
        tblSales.getTableHeader().setReorderingAllowed(false);
        tblSales.getSelectionModel().addListSelectionListener(tblSalesListener);
        tblSales.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblSalesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblSales);

        tabAcDoc.addTab("Sales Documents", jScrollPane1);

        tblPurchase.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Type", "Refference", "Vendor Ref", "Issue Date", "Amount", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblPurchase.setSortable(false);
        tblPurchase.getTableHeader().setReorderingAllowed(false);
        tblPurchase.getSelectionModel().addListSelectionListener(tblPurchaseListener);
        tblPurchase.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPurchaseMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblPurchase);

        tabAcDoc.addTab("Purchase Documents", jScrollPane2);

        btnViewDocument.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/details18.png"))); // NOI18N
        btnViewDocument.setMaximumSize(new java.awt.Dimension(35, 25));
        btnViewDocument.setMinimumSize(new java.awt.Dimension(35, 25));
        btnViewDocument.setPreferredSize(new java.awt.Dimension(35, 25));
        btnViewDocument.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewDocumentActionPerformed(evt);
            }
        });

        btnVoid.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/void18.png"))); // NOI18N
        btnVoid.setToolTipText("<html>\n<p>Void Invoice: VOID related documents before, if there is any.\n<br>For example, void payment/debit memo/credit memo before voiding invoice<p>\n</html>");
        btnVoid.setMaximumSize(new java.awt.Dimension(35, 25));
        btnVoid.setMinimumSize(new java.awt.Dimension(35, 25));
        btnVoid.setPreferredSize(new java.awt.Dimension(35, 25));
        btnVoid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVoidActionPerformed(evt);
            }
        });

        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/delete18.png"))); // NOI18N
        btnDelete.setToolTipText("<html>\n<p>Only VOID document can be deleted.<br> Delete related documents before deleting invoice<p>\n</html>");
        btnDelete.setMaximumSize(new java.awt.Dimension(35, 25));
        btnDelete.setMinimumSize(new java.awt.Dimension(35, 25));
        btnDelete.setPreferredSize(new java.awt.Dimension(35, 25));
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnVoid, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnViewDocument, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addComponent(tabAcDoc, javax.swing.GroupLayout.DEFAULT_SIZE, 509, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnViewDocument, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 84, Short.MAX_VALUE)
                        .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(btnVoid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(tabAcDoc, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnViewDocumentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewDocumentActionPerformed
        getDocument();
    }//GEN-LAST:event_btnViewDocumentActionPerformed

    private void btnVoidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVoidActionPerformed
        _voidDocument();
    }//GEN-LAST:event_btnVoidActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        deleteDocument();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void tblSalesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSalesMouseClicked
        if (evt.getClickCount() == 2) {
            getDocument();
        }
    }//GEN-LAST:event_tblSalesMouseClicked

    private void tblPurchaseMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPurchaseMouseClicked
        if (evt.getClickCount() == 2) {
            getDocument();
        }
    }//GEN-LAST:event_tblPurchaseMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnViewDocument;
    private javax.swing.JButton btnVoid;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane tabAcDoc;
    private org.jdesktop.swingx.JXTable tblPurchase;
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
                                TicketingSalesAcDoc temp_doc = (TicketingSalesAcDoc) doc;

                                tSAcDocList.add(temp_doc);
//                                if (temp_doc.getType().equals(AcDocType.INVOICE)) {
//                                    salesSummeryInvoice = temp_doc;
//                                }
                            } else {
                                TicketingPurchaseAcDoc temp_doc = (TicketingPurchaseAcDoc) doc;
                                tPAcDocList.add(temp_doc);
//                                if (temp_doc.getType().equals(AcDocType.INVOICE)) {
//                                    purchaseSummeryInvoice = temp_doc;
//                                }
                            }
                        }

                        if (saleType.equals("SALES")) {
                            populateTblSales();
                        } else if (saleType.equals("PURCHASE")) {
                            populateTblPurchase();
                        }

                    } else if ("COMPLETE".equals(taskType)) {
                        AccountingDocument doc = accountingDocTask.get();
                        taskType = "";
                        if (doc instanceof TicketingSalesAcDoc) {
                            ticketingSalesAcDoc = (TicketingSalesAcDoc) doc;
                            if (ticketingSalesAcDoc.getType().equals(AcDocType.INVOICE)) {
                                parent.showTSalesInvoiceDlg(ticketingSalesAcDoc);
                            } else if (ticketingSalesAcDoc.getType().equals(AcDocType.DEBITMEMO)
                                    || ticketingSalesAcDoc.getType().equals(AcDocType.CREDITMEMO)) {

                                if (!ticketingSalesAcDoc.getTickets().isEmpty()) {
                                    parent.showTSalesAcDocDlg(ticketingSalesAcDoc);
                                } else {
                                    parent.showSalesAcDocDlg(ticketingSalesAcDoc);
                                }
                            }
                        } else {
                            ticketingPurchaseAcDoc = (TicketingPurchaseAcDoc) doc;

                            if (ticketingPurchaseAcDoc.getType().equals(AcDocType.INVOICE)) {
                                parent.showTPurchaseInvoiceDlg(ticketingPurchaseAcDoc);
                            } else if (ticketingPurchaseAcDoc.getType().equals(AcDocType.DEBITMEMO)
                                    || ticketingPurchaseAcDoc.getType().equals(AcDocType.CREDITMEMO)) {

                                if (!ticketingPurchaseAcDoc.getTickets().isEmpty()) {
                                    parent.showTPurchaseAcDocDlg(ticketingPurchaseAcDoc);
                                } else {
                                    parent.showPurchaseAcDocDlg(ticketingPurchaseAcDoc);
                                }
                            }
                        }

                    } else if ("VOID".equals(taskType) || "DELETE".equals(taskType)) {
                        parent.loadCompletePnr();
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(TicketingAgentComponent.class.getName()).log(Level.SEVERE, null, ex);
                } finally {

                }
            }
        }
    }

    public TicketingSalesAcDoc getSalesSummeryInvoice() {
        return salesSummeryInvoice;
    }

    private ChangeListener tabListener = new ChangeListener() {

        public void stateChanged(ChangeEvent e) {

            if (pnrId != null) {
                if (tabAcDoc.getSelectedIndex() == 0) {
                    saleType = "SALES";
                    search(pnrId);
                } else if (tabAcDoc.getSelectedIndex() == 1) {
                    saleType = "PURCHASE";
                    search(pnrId);
                } else {
                    saleType = "";
                }
            }
        }
    };

    private ListSelectionListener tblSalesListener = new ListSelectionListener() {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int row = tblSales.getSelectedRow();
            if (row != -1) {
                TicketingSalesAcDoc doc = tSAcDocList.get(row);
                if (doc.getType().equals(Enums.AcDocType.INVOICE)) {
                    salesSummeryInvoice = doc;
                }
            }
        }
    };

    private ListSelectionListener tblPurchaseListener = new ListSelectionListener() {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int row = tblPurchase.getSelectedRow();
            if (row != -1) {
                TicketingPurchaseAcDoc doc = tPAcDocList.get(row);
                if (doc.getType().equals(Enums.AcDocType.INVOICE)) {
                    purchaseSummeryInvoice = doc;
                }
            }
        }
    };

    public TicketingPurchaseAcDoc getPurchaseSummeryInvoice() {
        return purchaseSummeryInvoice;
    }

    public int getSelectedTab() {
        return tabAcDoc.getSelectedIndex();
    }
}
