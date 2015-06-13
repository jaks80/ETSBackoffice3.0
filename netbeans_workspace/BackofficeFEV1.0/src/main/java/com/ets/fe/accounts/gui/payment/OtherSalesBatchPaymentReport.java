package com.ets.fe.accounts.gui.payment;

import com.ets.fe.Application;
import com.ets.fe.accounts.model.Payment;
import com.ets.fe.accounts.model.TransactionReceipt;
import com.ets.fe.accounts.model.TransactionReceipts;
import com.ets.fe.accounts.task.PaymentTask;
import com.ets.fe.accounts.task.ReceiptTask;
import com.ets.fe.acdoc.model.report.TktingInvoiceSummery;
import com.ets.fe.acdoc_o.gui.OtherInvoiceDlg;
import com.ets.fe.acdoc_o.model.report.OtherInvoiceSummery;
import com.ets.fe.report.BeanJasperReport;
import com.ets.fe.util.DateUtil;
import com.ets.fe.util.Enums;
import com.ets.fe.util.PnrUtil;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Window;
import java.awt.font.TextAttribute;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import net.sf.jasperreports.swing.JRViewer;
import org.jdesktop.swingx.JXTable;

/**
 *
 * @author Yusuf
 */
public class OtherSalesBatchPaymentReport extends javax.swing.JInternalFrame implements PropertyChangeListener {

    private List<TransactionReceipt> payments;
    private ReceiptTask task;
    private PaymentTask paymentTask;
    private JDesktopPane desktopPane;
    private int selectedRow = 0;
    private Enums.TaskType taskType;

    public OtherSalesBatchPaymentReport(JDesktopPane desktopPane) {
        this.desktopPane = desktopPane;
        initComponents();
        dtFrom.setDate(DateUtil.getBeginingOfMonth());
        dtTo.setDate(DateUtil.getEndOfMonth());
        initButton();
    }

    private void initButton() {        
        if (Application.getLoggedOnUser().getUserType().getId() >= 1) {
            btnVoid.setVisible(true);          
        } else {
            btnVoid.setVisible(false);
        }
        
        if (Application.getLoggedOnUser().getUserType().getId() >= 2) {
            btnDelete.setVisible(true);
        } else {
            btnDelete.setVisible(false);
        }
    }

    private void search() {
        btnSearch.setEnabled(false);
        taskType = Enums.TaskType.READ;
        Enums.ClientType clientType = documentSearchComponent.getClient_type();
        Long client_id = documentSearchComponent.getClient_id();
        Date from = dtFrom.getDate();
        Date to = dtTo.getDate();
        task = new ReceiptTask(clientType, client_id, from, to, Enums.SaleType.OTHERSALES, progressBar);
        task.addPropertyChangeListener(this);
        task.execute();
    }

    private void populateTblPayment() {

        DefaultTableModel tableModel = (DefaultTableModel) tblPayment.getModel();
        tableModel.getDataVector().removeAllElements();

        if (payments.size() > 0) {
            for (int i = 0; i < payments.size(); i++) {
                TransactionReceipt p = payments.get(i);
                tableModel.insertRow(i, new Object[]{p.getPaymentDate(), p.getPaymentType(),
                    p.getTotalAmount(), p.getRemark(), PnrUtil.calculatePartialName(p.getCashier())});
            }
        } else {
            tableModel.insertRow(0, new Object[]{"", ""});
        }

        tblPayment.setRowSelectionInterval(selectedRow, selectedRow);
    }

    private void populatePaymentDocuments(TransactionReceipt payment) {
        DefaultTableModel tableModel = (DefaultTableModel) tblPaymentDocs.getModel();
        tableModel.getDataVector().removeAllElements();

        List<OtherInvoiceSummery> docs = payment.getOlines();
        BigDecimal total = new BigDecimal("0.00");

        if (docs.size() > 0) {
            int i = 0;
            for (; i < docs.size(); i++) {
                OtherInvoiceSummery doc = docs.get(i);
                total = total.add(doc.getDocumentedAmount());
                tableModel.insertRow(i, new Object[]{i + 1, doc.getReference(), doc.getRemark(),
                    doc.getDocumentedAmount(), doc.getStatus()});
            }
            tableModel.insertRow(i, new Object[]{"", "", "Total:", "", "", total.abs(), ""});
        } else {
            tableModel.insertRow(0, new Object[]{"", "", "", "", "", ""});
        }
        tblPaymentDocs.getColumnModel().getColumn(4).setMinWidth(0);
        tblPaymentDocs.getColumnModel().getColumn(4).setMaxWidth(0);
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
        documentSearchComponent = new com.ets.fe.acdoc.gui.comp.ClientSearchComp(true, true, true,Enums.AgentType.ALL);
        jLabel6 = new javax.swing.JLabel();
        dtFrom = new org.jdesktop.swingx.JXDatePicker();
        jLabel8 = new javax.swing.JLabel();
        dtTo = new org.jdesktop.swingx.JXDatePicker();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        tabResult = new javax.swing.JTabbedPane();
        tabResult.addChangeListener(tabListener);
        searchPanel = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPayment = new org.jdesktop.swingx.JXTable();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblPaymentDocs = new JXTable(){
            public Component prepareRenderer(TableCellRenderer renderer,int rowIndex, int vColIndex) 
            {
                Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
                String s = this.getModel().getValueAt(rowIndex, 4).toString();
                if(s.equalsIgnoreCase("VOID")){
                    Map  attributes = c.getFont().getAttributes();
                    attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
                    Font newFont = new Font(attributes);
                    c.setFont(newFont);}
                return c;} 
        };
        toolBarPanel = new javax.swing.JPanel();
        btnViewInvoice = new javax.swing.JButton();
        btnEmail = new javax.swing.JButton();
        btnSearch = new javax.swing.JButton();
        btnVoid = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        reportPane = new javax.swing.JScrollPane();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("History: Sales Payment for Other Invoices");
        setMinimumSize(new java.awt.Dimension(1000, 500));
        setPreferredSize(new java.awt.Dimension(1000, 500));

        jSplitPane1.setDividerLocation(210);
        jSplitPane1.setDividerSize(10);
        jSplitPane1.setOneTouchExpandable(true);

        jLabel6.setText("Date From");

        dtFrom.setPreferredSize(new java.awt.Dimension(110, 20));

        jLabel8.setText("Date To");

        dtTo.setPreferredSize(new java.awt.Dimension(110, 20));

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 0, 0));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Other Sales");

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 0, 0));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Payment History");

        progressBar.setMaximumSize(new java.awt.Dimension(32767, 18));
        progressBar.setMinimumSize(new java.awt.Dimension(10, 18));
        progressBar.setPreferredSize(new java.awt.Dimension(146, 18));
        progressBar.setStringPainted(true);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Message:");

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
            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6)
                .addGap(2, 2, 2)
                .addComponent(dtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addGap(2, 2, 2)
                .addComponent(dtTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(documentSearchComponent, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 86, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2))
        );

        jSplitPane1.setLeftComponent(jPanel4);

        tabResult.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        searchPanel.setLayout(new java.awt.GridBagLayout());

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Payment", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jPanel5.setLayout(new java.awt.GridLayout(1, 0));

        tblPayment.setBackground(new java.awt.Color(204, 255, 255));
        tblPayment.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Date", "Type", "Amount", "Remark", "Cashier"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblPayment.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        tblPayment.setSortable(false);
        tblPayment.getTableHeader().setReorderingAllowed(false);
        tblPayment.getSelectionModel().addListSelectionListener(tblPaymentListener);
        jScrollPane1.setViewportView(tblPayment);
        if (tblPayment.getColumnModel().getColumnCount() > 0) {
            tblPayment.getColumnModel().getColumn(0).setMaxWidth(85);
            tblPayment.getColumnModel().getColumn(1).setMaxWidth(85);
            tblPayment.getColumnModel().getColumn(2).setPreferredWidth(100);
            tblPayment.getColumnModel().getColumn(2).setMaxWidth(150);
            tblPayment.getColumnModel().getColumn(4).setPreferredWidth(80);
            tblPayment.getColumnModel().getColumn(4).setMaxWidth(80);
        }

        jPanel5.add(jScrollPane1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        searchPanel.add(jPanel5, gridBagConstraints);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Invoices", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jPanel6.setLayout(new java.awt.GridLayout(1, 0));

        tblPaymentDocs.setBackground(new java.awt.Color(255, 255, 204));
        tblPaymentDocs.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
        tblPaymentDocs.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "Invoice No", "Remark", "Amount", ""
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblPaymentDocs.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        tblPaymentDocs.setSortable(false);
        tblPaymentDocs.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tblPaymentDocs);
        if (tblPaymentDocs.getColumnModel().getColumnCount() > 0) {
            tblPaymentDocs.getColumnModel().getColumn(0).setMinWidth(40);
            tblPaymentDocs.getColumnModel().getColumn(0).setPreferredWidth(40);
            tblPaymentDocs.getColumnModel().getColumn(0).setMaxWidth(60);
            tblPaymentDocs.getColumnModel().getColumn(4).setMinWidth(40);
            tblPaymentDocs.getColumnModel().getColumn(4).setPreferredWidth(40);
            tblPaymentDocs.getColumnModel().getColumn(4).setMaxWidth(40);
        }

        jPanel6.add(jScrollPane2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        searchPanel.add(jPanel6, gridBagConstraints);

        toolBarPanel.setBackground(new java.awt.Color(102, 102, 102));
        toolBarPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        btnViewInvoice.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/acdoc18.png"))); // NOI18N
        btnViewInvoice.setMaximumSize(new java.awt.Dimension(40, 22));
        btnViewInvoice.setMinimumSize(new java.awt.Dimension(40, 22));
        btnViewInvoice.setPreferredSize(new java.awt.Dimension(40, 22));
        btnViewInvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewInvoiceActionPerformed(evt);
            }
        });

        btnEmail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/email18.png"))); // NOI18N
        btnEmail.setMaximumSize(new java.awt.Dimension(40, 22));
        btnEmail.setMinimumSize(new java.awt.Dimension(40, 22));
        btnEmail.setPreferredSize(new java.awt.Dimension(40, 22));
        btnEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEmailActionPerformed(evt);
            }
        });

        btnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/search18.png"))); // NOI18N
        btnSearch.setMaximumSize(new java.awt.Dimension(40, 22));
        btnSearch.setMinimumSize(new java.awt.Dimension(40, 22));
        btnSearch.setPreferredSize(new java.awt.Dimension(40, 22));
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        btnVoid.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/void18.png"))); // NOI18N
        btnVoid.setToolTipText("VOID Payment");
        btnVoid.setMinimumSize(new java.awt.Dimension(35, 22));
        btnVoid.setPreferredSize(new java.awt.Dimension(35, 22));
        btnVoid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVoidActionPerformed(evt);
            }
        });

        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/delete18.png"))); // NOI18N
        btnDelete.setPreferredSize(new java.awt.Dimension(35, 22));
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout toolBarPanelLayout = new javax.swing.GroupLayout(toolBarPanel);
        toolBarPanel.setLayout(toolBarPanelLayout);
        toolBarPanelLayout.setHorizontalGroup(
            toolBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, toolBarPanelLayout.createSequentialGroup()
                .addComponent(btnViewInvoice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(btnEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 555, Short.MAX_VALUE)
                .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnVoid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        toolBarPanelLayout.setVerticalGroup(
            toolBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnViewInvoice, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnSearch, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnVoid, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        searchPanel.add(toolBarPanel, gridBagConstraints);

        tabResult.addTab("Search", searchPanel);
        tabResult.addTab("Print View", reportPane);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabResult, javax.swing.GroupLayout.DEFAULT_SIZE, 770, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabResult, javax.swing.GroupLayout.DEFAULT_SIZE, 494, Short.MAX_VALUE)
        );

        jSplitPane1.setRightComponent(jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE)
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
            OtherInvoiceSummery doc = payments.get(index_pay).getOlines().get(index_doc);
            if (doc.getStatus() == Enums.AcDocStatus.VOID) {
                return;
            }
            Long id = doc.getParentId();

            Window w = SwingUtilities.getWindowAncestor(this);
            Frame owner = w instanceof Frame ? (Frame) w : null;
            OtherInvoiceDlg dlg = new OtherInvoiceDlg(owner);
            dlg.showDialog(id);
        }
    }//GEN-LAST:event_btnViewInvoiceActionPerformed

    private void btnEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmailActionPerformed
        int index = tblPayment.getSelectedRow();
        if (index != -1) {
            TransactionReceipt receipt = payments.get(index);
            String receipent = receipt.getEmail();
            String subject = receipt.getReportTitle().concat(" From").concat(Application.getMainAgent().getName());
            String body = receipt.getReportTitle().concat(" From").concat(Application.getMainAgent().getName());
            String refference = "report";

            if (receipent != null) {
                BeanJasperReport jasperreport = new BeanJasperReport(receipent, subject, body, refference);
                List<TransactionReceipt> list = new ArrayList<>();
                list.add(receipt);
                jasperreport.transactionReceipt(list, Enums.SaleType.OTHERSALES, "EMAIL");
            } else {
                JOptionPane.showMessageDialog(null, "No Email address", "Email", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnEmailActionPerformed

    private void btnVoidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVoidActionPerformed
        int index = tblPayment.getSelectedRow();
        int choice = JOptionPane.showConfirmDialog(null, "VOID Payment!!!Are you sure?", "VOID Payment", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.NO_OPTION) {
            return;
        }

        if (index != -1) {
            selectedRow = index;
            Long id = payments.get(selectedRow).getId();
            Payment payment = new Payment();
            payment.setId(id);
            taskType = Enums.TaskType.VOID;
            paymentTask = new PaymentTask(payment, taskType);
            paymentTask.addPropertyChangeListener(this);
            paymentTask.execute();
        }
    }//GEN-LAST:event_btnVoidActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        int index = tblPayment.getSelectedRow();
        int choice = JOptionPane.showConfirmDialog(null, "Delete Payment!!!Are you sure?", "Delete Payment", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.NO_OPTION) {
            return;
        }

        if (index != -1) {
            selectedRow = index;
            Long id = payments.get(selectedRow).getId();
            Payment payment = new Payment();
            payment.setId(id);
            taskType = Enums.TaskType.DELETE;
            paymentTask = new PaymentTask(payment, taskType);
            paymentTask.addPropertyChangeListener(this);
            paymentTask.execute();
        }
    }//GEN-LAST:event_btnDeleteActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEmail;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnViewInvoice;
    private javax.swing.JButton btnVoid;
    private com.ets.fe.acdoc.gui.comp.ClientSearchComp documentSearchComponent;
    private org.jdesktop.swingx.JXDatePicker dtFrom;
    private org.jdesktop.swingx.JXDatePicker dtTo;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JScrollPane reportPane;
    private javax.swing.JPanel searchPanel;
    private javax.swing.JTabbedPane tabResult;
    private org.jdesktop.swingx.JXTable tblPayment;
    private org.jdesktop.swingx.JXTable tblPaymentDocs;
    private javax.swing.JPanel toolBarPanel;
    // End of variables declaration//GEN-END:variables
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress".equals(evt.getPropertyName())) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
            if (progress == 100) {
                try {
                    if (taskType.equals(Enums.TaskType.READ)) {
                        payments = new ArrayList<>();
                        TransactionReceipts pays = task.get();
                        payments = pays.getList();
                        populateTblPayment();
                    } else if (taskType.equals(Enums.TaskType.VOID)) {
                        search();
                    } else if (taskType.equals(Enums.TaskType.DELETE)) {
                        search();
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(OtherSalesBatchPaymentReport.class.getName()).log(Level.SEVERE, null, ex);
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
            if (selectedRow != -1 && payments.size() > 0) {
                TransactionReceipt payment = payments.get(selectedRow);
                populatePaymentDocuments(payment);
            }
        }
    };

    private void report(String action) {
        int index = tblPayment.getSelectedRow();
        if (index != -1) {
            BeanJasperReport jasperreport = new BeanJasperReport();
            List<TransactionReceipt> list = new ArrayList<>();
            list.add(payments.get(index));
            JRViewer viewer = jasperreport.transactionReceipt(list, Enums.SaleType.OTHERSALES, action);
            if (viewer != null) {
                reportPane.setViewportView(viewer);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Select a Payment", "Transaction Report", JOptionPane.INFORMATION_MESSAGE);
            tabResult.setSelectedIndex(0);
        }
    }

    private ChangeListener tabListener = new ChangeListener() {

        @Override
        public void stateChanged(ChangeEvent e) {
            if (tabResult.getSelectedIndex() == 1) {
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        report("VIEW");
                    }
                });
            }
        }
    };
}
