package com.ets.fe.accounts.gui.payment;

import com.ets.fe.Application;
import com.ets.fe.accounts.model.CreditTransfer;
import com.ets.fe.accounts.task.NewCreditTransferTask;
import com.ets.fe.acdoc.model.TicketingSalesAcDoc;
import com.ets.fe.acdoc.model.report.InvoiceReport;
import com.ets.fe.acdoc.model.report.TktingInvoiceSummery;
import com.ets.fe.acdoc.task.SalesAcDocReportingTask;
import com.ets.fe.util.DateUtil;
import com.ets.fe.util.Enums;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Yusuf
 */
public class DlgTktSalesCreditTransfer extends javax.swing.JDialog implements PropertyChangeListener {

    private TicketingSalesAcDoc invoice;

    private String taskType = "";
    private SalesAcDocReportingTask refundSearchTask;
    private NewCreditTransferTask creditTransferTask;

    private BigDecimal balDueInThisInvoice = new BigDecimal("0.00");
    private List<TktingInvoiceSummery> dueRefunds = new ArrayList<>();
    private String reportType = "OUTSTANDING";

    public DlgTktSalesCreditTransfer(java.awt.Frame parent) {
        super(parent, "Credit Transfer", true);
        initComponents();
    }

    public boolean showDialog(TicketingSalesAcDoc invoice) {
        this.invoice = invoice;
        populateInvoice();
        searchDueRefund();
        setLocationRelativeTo(this);
        setVisible(true);
        return true;
    }

    private void populateInvoice() {

        this.balDueInThisInvoice = this.invoice.calculateDueAmount();
        lblAcDocRef.setText(this.invoice.getReference().toString());
        lblIssueDate.setText(DateUtil.dateToString(this.invoice.getDocIssueDate()));
        lblAcDocFor.setText(this.invoice.calculateClientName());
        lblAcDocAmount.setText(this.invoice.getDocumentedAmount().toString());
        lblDueAmount.setText(this.invoice.calculateDueAmount().toString());
        lblRfdApplied.setText("0.00");
        lblDueToThisInv.setText(this.invoice.calculateDueAmount().toString());
    }

    private void searchDueRefund() {

        taskType = "DUEREFUND";
        Enums.ClientType client_type = null;
        Long client_id = null;

        if (this.invoice.getPnr().getAgent() != null) {
            client_type = Enums.ClientType.AGENT;
            client_id = this.invoice.getPnr().getAgent().getId();
        } else {
            client_type = Enums.ClientType.CUSTOMER;
            client_id = this.invoice.getPnr().getCustomer().getId();
        }
        refundSearchTask = new SalesAcDocReportingTask(reportType,Enums.AcDocType.REFUND, client_type, client_id, null, null, progressBar);
        refundSearchTask.addPropertyChangeListener(this);
        refundSearchTask.execute();
    }

    private void calculateCurrentRow(int row, int col) {

        BigDecimal avaiableRfd = new BigDecimal("0.00");
        BigDecimal rfdTaken = new BigDecimal("0.00");
        BigDecimal remainingRfd = new BigDecimal("0.00");
        BigDecimal tRfdTaken = new BigDecimal("0.00");

        avaiableRfd = dueRefunds.get(row).getDue().negate();
        Object val = tblInvoice.getValueAt(row, 6);

        if (val.equals(true)) {
            if (balDueInThisInvoice.compareTo(avaiableRfd) >= 0) {
                rfdTaken = avaiableRfd;
                remainingRfd = avaiableRfd.subtract(rfdTaken);
            } else {
                rfdTaken = balDueInThisInvoice;
                remainingRfd = avaiableRfd.subtract(rfdTaken);
            }

            tblInvoice.getModel().setValueAt(rfdTaken, row, 4);
            tblInvoice.getModel().setValueAt(remainingRfd, row, 5);
        }
        for (int i = 0; i < tblInvoice.getRowCount(); i++) {
            Object val1 = tblInvoice.getValueAt(i, 6);
            if (val1.equals(true)) {
                tRfdTaken = tRfdTaken.add((BigDecimal) tblInvoice.getValueAt(i, 4));
            }
        }

        balDueInThisInvoice = this.invoice.calculateDueAmount().subtract(tRfdTaken);
        lblRfdApplied.setText(tRfdTaken.toString());
        lblDueToThisInv.setText(balDueInThisInvoice.toString());
    }

    private void populateTblRefund() {

        int row = 0;
        DefaultTableModel invoiceModel = (DefaultTableModel) tblInvoice.getModel();
        invoiceModel.getDataVector().removeAllElements();

        if (dueRefunds.size() > 0) {
            for (TktingInvoiceSummery a : dueRefunds) {
                invoiceModel.insertRow(row, new Object[]{a.getReference(), a.getDocIssueDate(),
                    a.getAirLine(), a.getDue(), null, null, false});
                row++;
            }
        } else {
            invoiceModel.insertRow(row, new Object[]{"", "", "", "", "", "", false});
        }

        invoiceModel.addTableModelListener(new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    int row = e.getLastRow();
                    int column = e.getColumn();

                    if (column == 6) {
                        if (tblInvoice.getValueAt(row, 6).equals(false)) {
                            tblInvoice.getModel().setValueAt(null, row, 4);
                            tblInvoice.getModel().setValueAt(null, row, 5);
                        }
                        calculateCurrentRow(row, column);
                    }
                }
            }
        });
    }

    private void submit() {
        LinkedHashMap<Long, String> refundMap = new LinkedHashMap<>();

        int noOfRow = tblInvoice.getRowCount();
        for (int i = 0; i < noOfRow; i++) {
            if (tblInvoice.getValueAt(i, 6).equals(true)) {
                refundMap.put(dueRefunds.get(i).getId(), tblInvoice.getModel().getValueAt(i, 4).toString());
            }
        }
        CreditTransfer creditTransfer = new CreditTransfer();
        creditTransfer.setRefundMap(refundMap);
        creditTransfer.setInvoiceId(invoice.getId());
        creditTransfer.setSaleType(Enums.SaleType.TKTSALES);
        creditTransfer.setUser(Application.getLoggedOnUser());
        taskType = "SUBMIT";
        creditTransferTask = new NewCreditTransferTask(creditTransfer, lblMessage);
        creditTransferTask.addPropertyChangeListener(this);
        creditTransferTask.execute();
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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lblAcDocRef = new javax.swing.JLabel();
        lblIssueDate = new javax.swing.JLabel();
        lblAcDocFor = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lblAcDocAmount = new javax.swing.JLabel();
        lblDueAmount = new javax.swing.JLabel();
        lblRfdApplied = new javax.swing.JLabel();
        lblDueToThisInv = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblInvoice = new org.jdesktop.swingx.JXTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        btnClear = new javax.swing.JButton();
        btnApply = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        progressBar = new javax.swing.JProgressBar();
        lblMessage = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Invoice", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Invoice Ref:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel1.add(jLabel1, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Invoice Date:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel1.add(jLabel2, gridBagConstraints);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Invoice For:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel1.add(jLabel3, gridBagConstraints);

        lblAcDocRef.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblAcDocRef.setText("0.00");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel1.add(lblAcDocRef, gridBagConstraints);

        lblIssueDate.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblIssueDate.setText("0.00");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel1.add(lblIssueDate, gridBagConstraints);

        lblAcDocFor.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblAcDocFor.setText("0.00");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel1.add(lblAcDocFor, gridBagConstraints);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("Invoice Amount:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel1.add(jLabel7, gridBagConstraints);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("Amount Due:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel1.add(jLabel8, gridBagConstraints);

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("Refund Applied:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel1.add(jLabel9, gridBagConstraints);

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setText("Balance Due:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel1.add(jLabel10, gridBagConstraints);

        lblAcDocAmount.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblAcDocAmount.setText("0.00");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel1.add(lblAcDocAmount, gridBagConstraints);

        lblDueAmount.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDueAmount.setText("0.00");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel1.add(lblDueAmount, gridBagConstraints);

        lblRfdApplied.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblRfdApplied.setText("0.00");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel1.add(lblRfdApplied, gridBagConstraints);

        lblDueToThisInv.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDueToThisInv.setText("0.00");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel1.add(lblDueToThisInv, gridBagConstraints);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Available Refund", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        tblInvoice.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "InvRef", "IssueDate", "AirLine", "AvailableRefund", "RefundTaken", "RemainingRefund", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblInvoice.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        tblInvoice.setSortable(false);
        tblInvoice.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblInvoice);
        if (tblInvoice.getColumnModel().getColumnCount() > 0) {
            tblInvoice.getColumnModel().getColumn(2).setMaxWidth(55);
            tblInvoice.getColumnModel().getColumn(6).setMaxWidth(35);
        }

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 615, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
        );

        jTextArea1.setEditable(false);
        jTextArea1.setBackground(new java.awt.Color(255, 233, 236));
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTextArea1.setRows(5);
        jTextArea1.setText("This window applies refunds to current invoice as payment. \nIt will issue a refund transaction to selected invoice on the \ntable and payment transaction on current invoice. \n\nTick on last column of table and follow calculation above.\n");
        jTextArea1.setMargin(new java.awt.Insets(4, 4, 4, 4));
        jScrollPane2.setViewportView(jTextArea1);

        btnClear.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnClear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/clear18.png"))); // NOI18N
        btnClear.setText("Clear");
        btnClear.setMaximumSize(new java.awt.Dimension(85, 27));
        btnClear.setMinimumSize(new java.awt.Dimension(85, 27));
        btnClear.setPreferredSize(new java.awt.Dimension(85, 27));
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        btnApply.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnApply.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ok18.png"))); // NOI18N
        btnApply.setText("Apply");
        btnApply.setMaximumSize(new java.awt.Dimension(90, 27));
        btnApply.setMinimumSize(new java.awt.Dimension(90, 27));
        btnApply.setPreferredSize(new java.awt.Dimension(90, 27));
        btnApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApplyActionPerformed(evt);
            }
        });

        btnCancel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/cancel18.png"))); // NOI18N
        btnCancel.setText("Close");
        btnCancel.setMaximumSize(new java.awt.Dimension(90, 27));
        btnCancel.setMinimumSize(new java.awt.Dimension(90, 27));
        btnCancel.setPreferredSize(new java.awt.Dimension(90, 27));
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        progressBar.setMaximumSize(new java.awt.Dimension(32767, 18));
        progressBar.setMinimumSize(new java.awt.Dimension(10, 18));
        progressBar.setPreferredSize(new java.awt.Dimension(146, 18));
        progressBar.setStringPainted(true);

        lblMessage.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblMessage.setForeground(new java.awt.Color(255, 0, 0));
        lblMessage.setText("Message:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 379, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 147, Short.MAX_VALUE)
                                .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lblMessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnApply, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(4, 4, 4))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(19, 19, 19)
                        .addComponent(lblMessage))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnApply, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApplyActionPerformed
        submit();
    }//GEN-LAST:event_btnApplyActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        populateTblRefund();
        populateInvoice();
    }//GEN-LAST:event_btnClearActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnApply;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnClear;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel lblAcDocAmount;
    private javax.swing.JLabel lblAcDocFor;
    private javax.swing.JLabel lblAcDocRef;
    private javax.swing.JLabel lblDueAmount;
    private javax.swing.JLabel lblDueToThisInv;
    private javax.swing.JLabel lblIssueDate;
    private javax.swing.JLabel lblMessage;
    private javax.swing.JLabel lblRfdApplied;
    private javax.swing.JProgressBar progressBar;
    private org.jdesktop.swingx.JXTable tblInvoice;
    // End of variables declaration//GEN-END:variables
@Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress".equals(evt.getPropertyName())) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
            if (progress == 100) {
                if ("DUEREFUND".equals(taskType)) {
                    try {
                        InvoiceReport report = refundSearchTask.get();
                        dueRefunds = report.getInvoices();
                        populateTblRefund();
                        taskType = "";
                    } catch (InterruptedException | ExecutionException ex) {
                        Logger.getLogger(DlgTktSalesCreditTransfer.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } else if ("SUBMIT".equals(taskType)) {
                    searchDueRefund();
                }
            }
        }
    }
}
