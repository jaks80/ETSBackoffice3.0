package com.ets.fe.accounts.gui.payment;

import com.ets.fe.Application;
import com.ets.fe.acdoc.model.TicketingPurchaseAcDoc;
import com.ets.fe.accounts.task.BSPPaymentTask;
import com.ets.fe.acdoc.model.BSPReport;
import com.ets.fe.acdoc.task.DueBSPReportTask;
import com.ets.fe.pnr.model.GDSSaleReport;
import com.ets.fe.pnr.model.Ticket;
import com.ets.fe.util.*;
import java.awt.Color;
import java.awt.Component;
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
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import org.jdesktop.swingx.JXTable;

/**
 *
 * @author Yusuf
 */
public class BSPBillPayment extends javax.swing.JInternalFrame implements PropertyChangeListener {

    private JDesktopPane desktopPane;
    private DueBSPReportTask task;
    private BSPPaymentTask paymentTask;

    private String taskType = "";
    private Date from;
    private Date to;

    private List<TicketingPurchaseAcDoc> dueInvoices = new ArrayList<>();
    private List<TicketingPurchaseAcDoc> adm_acm = new ArrayList<>();
    private List<Ticket> tickets = new ArrayList<>();
    private GDSSaleReport gdsSaleReport;

    public BSPBillPayment(JDesktopPane desktopPane) {
        this.desktopPane = desktopPane;
        initComponents();
        dtFrom.setDate(DateUtil.getBeginingOfMonth());
        dtTo.setDate(DateUtil.getEndOfMonth());

        CheckInput c = new CheckInput(CheckInput.FLOAT);
        c.setNegativeAccepted(true);
        txtAmount.setDocument(c);
    }

    private void search() {
        taskType = "SEARCH";
        btnSearch.setEnabled(false);
        Long agent_id = Application.getMainAgent().getId();
        from = dtFrom.getDate();
        to = dtTo.getDate();
        task = new DueBSPReportTask(agent_id, from, to, progressBar);
        task.addPropertyChangeListener(this);
        task.execute();
    }

    public void processPayment() {
        busyLabel.setText("");
        busyLabel.setBusy(true);
        btnSubmitPayment.setEnabled(false);
        String amountString = txtAmount.getText();
        if (!amountString.isEmpty()) {
            taskType = "PAYMENT";
            paymentTask = new BSPPaymentTask(Application.getMainAgent().getId(), from, to);
            paymentTask.addPropertyChangeListener(this);
            paymentTask.execute();

        } else {
            busyLabel.setText("Warning! No amount!");
            btnSubmitPayment.setEnabled(true);
        }
    }

    private void populateTblDocument() {
        DefaultTableModel tableModel = (DefaultTableModel) tblAcDoc.getModel();
        tableModel.getDataVector().removeAllElements();
        tblTicket.repaint();
        int row = 0;
        BigDecimal total = new BigDecimal("0.00");

        if (adm_acm.size() > 0) {
            for (TicketingPurchaseAcDoc doc : adm_acm) {
                total = total.add(doc.getDocumentedAmount());
                tableModel.insertRow(row, new Object[]{DateUtil.dateToString(doc.getDocIssueDate()),
                    doc.getDocTypeString(), doc.getPnr().getGdsPnr(), doc.getPnr().getAirLineCode(),
                    doc.getDocumentedAmount()});
                row++;
            }
            tableModel.insertRow(row, new Object[]{"", "", "Total:", "", total});
        } else {
            tableModel.insertRow(0, new Object[]{});
        }
    }

    private void populateTblTicket() {
        DefaultTableModel tableModel = (DefaultTableModel) tblTicket.getModel();
        tableModel.getDataVector().removeAllElements();
        tblTicket.repaint();
        int row = 0;

        BigDecimal totalBaseFare = new BigDecimal("0.00");
        BigDecimal totalTax = new BigDecimal("0.00");
        BigDecimal totalFee = new BigDecimal("0.00");
        BigDecimal totalCommission = new BigDecimal("0.00");
        BigDecimal totalNetPurchaseFare = new BigDecimal("0.00");

        if (tickets.size() > 0) {
            for (Ticket t : tickets) {

                totalBaseFare = totalBaseFare.add(t.getBaseFare());
                totalTax = totalTax.add(t.getTax());
                totalCommission = totalCommission.add(t.getCommission());
                totalFee = totalFee.add(t.getFee());
                totalNetPurchaseFare = totalNetPurchaseFare.add(t.calculateNetPurchaseFare());

                tableModel.insertRow(row, new Object[]{t.getTktDateString(), t.getTktStatus(),
                    t.getTicketNo(), t.getPnr().getGdsPnr(),
                    t.getBaseFare(), t.getTax(), t.getFee(), t.getCommission(), t.calculateNetPurchaseFare()});
                row++;
            }
        } else {
            tableModel.insertRow(0, new Object[]{});
        }
        tableModel.insertRow(row, new Object[]{"", "",
            "", "Totals:",
            totalBaseFare, totalTax, totalFee, totalCommission, totalNetPurchaseFare});

        BigDecimal totalDue = new BigDecimal("0.00");

        for (TicketingPurchaseAcDoc invoice : dueInvoices) {
            totalDue = totalDue.add(invoice.calculateDueAmount());
        }
        txtAmount.setText(totalDue.toString());
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        btnViewReport = new javax.swing.JButton();
        btnViewInvoice = new javax.swing.JButton();
        btnEmail = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        btnSearch = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lblInvAmount = new javax.swing.JLabel();
        lblOther = new javax.swing.JLabel();
        lblDMemo = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        dtFrom = new org.jdesktop.swingx.JXDatePicker();
        jLabel8 = new javax.swing.JLabel();
        dtTo = new org.jdesktop.swingx.JXDatePicker();
        jPanel8 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        txtAmount = new javax.swing.JTextField();
        btnSubmitPayment = new javax.swing.JButton();
        busyLabel = new org.jdesktop.swingx.JXBusyLabel();
        jLabel7 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTicket = new JXTable(){public Component prepareRenderer(TableCellRenderer renderer,
            int rowIndex, int vColIndex) {
            Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);  
            String s = "";
            Object o = tblTicket.getModel().getValueAt(rowIndex, 1);       
            if(o!=null){
                s = o.toString();
            }
            if (s.equalsIgnoreCase("BOOK") ) {                
                c.setForeground(Color.yellow);
            } else if(s.equalsIgnoreCase("ISSUE")){
                c.setForeground(Color.green);
            }else if(s.equalsIgnoreCase("REISSUE")){
                c.setForeground(Color.cyan);
            }else if(s.equalsIgnoreCase("REFUND")){
                c.setForeground(Color.red);
            }else if(s.equalsIgnoreCase("VOID")){
                c.setForeground(Color.ORANGE);
            }else{
                c.setForeground(Color.WHITE);
            }
            return c;
        }
    };
    jPanel5 = new javax.swing.JPanel();
    jScrollPane2 = new javax.swing.JScrollPane();
    tblAcDoc = new JXTable(){public Component prepareRenderer(TableCellRenderer renderer,                                       int rowIndex, int vColIndex) {          Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);            String s = "";          Object o = tblAcDoc.getModel().getValueAt(rowIndex, 1);                 if(o!=null){          s = o.toString();          }              if(s.equalsIgnoreCase("ACM")){                  c.setForeground(Color.green);              }else if(s.equalsIgnoreCase("ADM")){                  c.setForeground(Color.red);              }else{               c.setForeground(Color.WHITE);              }  return c;      }  };
    progressBar = new javax.swing.JProgressBar();
    jLabel2 = new javax.swing.JLabel();

    setClosable(true);
    setMaximizable(true);
    setResizable(true);
    setTitle("Purchase: Bill Payment");
    setMinimumSize(new java.awt.Dimension(1000, 500));
    setPreferredSize(new java.awt.Dimension(1000, 500));

    jPanel1.setBackground(new java.awt.Color(102, 102, 102));
    jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

    btnViewReport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/details18.png"))); // NOI18N
    btnViewReport.setMaximumSize(new java.awt.Dimension(35, 22));
    btnViewReport.setMinimumSize(new java.awt.Dimension(35, 22));
    btnViewReport.setPreferredSize(new java.awt.Dimension(35, 22));

    btnViewInvoice.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/acdoc18.png"))); // NOI18N
    btnViewInvoice.setMaximumSize(new java.awt.Dimension(35, 22));
    btnViewInvoice.setMinimumSize(new java.awt.Dimension(35, 22));
    btnViewInvoice.setPreferredSize(new java.awt.Dimension(35, 22));
    btnViewInvoice.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnViewInvoiceActionPerformed(evt);
        }
    });

    btnEmail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/email18.png"))); // NOI18N
    btnEmail.setMaximumSize(new java.awt.Dimension(35, 22));
    btnEmail.setMinimumSize(new java.awt.Dimension(35, 22));
    btnEmail.setPreferredSize(new java.awt.Dimension(35, 22));

    btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/print18.png"))); // NOI18N
    btnPrint.setMaximumSize(new java.awt.Dimension(35, 22));
    btnPrint.setMinimumSize(new java.awt.Dimension(35, 22));
    btnPrint.setPreferredSize(new java.awt.Dimension(35, 22));

    btnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/search18.png"))); // NOI18N
    btnSearch.setMaximumSize(new java.awt.Dimension(35, 22));
    btnSearch.setMinimumSize(new java.awt.Dimension(35, 22));
    btnSearch.setPreferredSize(new java.awt.Dimension(35, 22));
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
            .addComponent(btnViewInvoice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(2, 2, 2)
            .addComponent(btnViewReport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(2, 2, 2)
            .addComponent(btnEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(2, 2, 2)
            .addComponent(btnPrint, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(2, 2, 2)
            .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    jPanel1Layout.setVerticalGroup(
        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(btnViewInvoice, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addComponent(btnEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addComponent(btnPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addComponent(btnViewReport, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addComponent(btnSearch, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
    );

    jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

    jPanel2.setBackground(new java.awt.Color(255, 255, 255));
    jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
    jPanel2.setLayout(new java.awt.GridBagLayout());

    jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
    jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel1.setText("Nett Tktd Amount:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
    jPanel2.add(jLabel1, gridBagConstraints);

    jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
    jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel3.setText("ADM:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
    jPanel2.add(jLabel3, gridBagConstraints);

    lblInvAmount.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    lblInvAmount.setText("0.00");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
    jPanel2.add(lblInvAmount, gridBagConstraints);

    lblOther.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    lblOther.setText("0.00");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
    jPanel2.add(lblOther, gridBagConstraints);

    lblDMemo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    lblDMemo.setText("0.00");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
    jPanel2.add(lblDMemo, gridBagConstraints);

    jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
    jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel9.setText("ACM:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
    jPanel2.add(jLabel9, gridBagConstraints);

    jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
    jLabel6.setText("Date From");

    dtFrom.setPreferredSize(new java.awt.Dimension(110, 20));

    jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
    jLabel8.setText("Date To");

    dtTo.setPreferredSize(new java.awt.Dimension(110, 20));

    javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
    jPanel4.setLayout(jPanel4Layout);
    jPanel4Layout.setHorizontalGroup(
        jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel4Layout.createSequentialGroup()
            .addGap(2, 2, 2)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(dtFrom, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
                .addComponent(dtTo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel6)
                        .addComponent(jLabel8))
                    .addContainerGap())))
    );
    jPanel4Layout.setVerticalGroup(
        jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel4Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jLabel6)
            .addGap(2, 2, 2)
            .addComponent(dtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jLabel8)
            .addGap(2, 2, 2)
            .addComponent(dtTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Submit Payment", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
    jPanel8.setLayout(new java.awt.GridBagLayout());

    jLabel11.setText("Amount");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
    jPanel8.add(jLabel11, gridBagConstraints);

    txtAmount.setEditable(false);
    txtAmount.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel8.add(txtAmount, gridBagConstraints);

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
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel8.add(btnSubmitPayment, gridBagConstraints);

    busyLabel.setDirection(null);
    busyLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel8.add(busyLabel, gridBagConstraints);

    jLabel7.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
    jLabel7.setForeground(new java.awt.Color(255, 0, 0));
    jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    jLabel7.setText("BSP Bill Payment");

    jTabbedPane1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

    tblTicket.setBackground(new java.awt.Color(0, 0, 0));
    tblTicket.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {

        },
        new String [] {
            "Date", "Status", "TicketNo", "PNR", "BaseFare", "Tax", "Fee", "Com", "Nett"
        }
    ) {
        boolean[] canEdit = new boolean [] {
            false, false, false, true, false, false, false, false, false
        };

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit [columnIndex];
        }
    });
    tblTicket.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
    tblTicket.getTableHeader().setReorderingAllowed(false);
    jScrollPane1.setViewportView(tblTicket);

    jTabbedPane1.addTab("Tickets", jScrollPane1);

    tblAcDoc.setBackground(new java.awt.Color(51, 51, 51));
    tblAcDoc.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {

        },
        new String [] {
            "Date", "Type", "PNR", "Airline", "Amount"
        }
    ) {
        boolean[] canEdit = new boolean [] {
            false, false, false, false, false
        };

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit [columnIndex];
        }
    });
    tblAcDoc.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
    tblAcDoc.getTableHeader().setReorderingAllowed(false);
    jScrollPane2.setViewportView(tblAcDoc);
    if (tblAcDoc.getColumnModel().getColumnCount() > 0) {
        tblAcDoc.getColumnModel().getColumn(3).setMaxWidth(45);
    }

    javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
    jPanel5.setLayout(jPanel5Layout);
    jPanel5Layout.setHorizontalGroup(
        jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel5Layout.createSequentialGroup()
            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 590, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(0, 196, Short.MAX_VALUE))
    );
    jPanel5Layout.setVerticalGroup(
        jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE)
    );

    jTabbedPane1.addTab("ADM/ACM", jPanel5);

    progressBar.setMaximumSize(new java.awt.Dimension(32767, 18));
    progressBar.setMinimumSize(new java.awt.Dimension(10, 18));
    progressBar.setPreferredSize(new java.awt.Dimension(146, 18));
    progressBar.setStringPainted(true);

    jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel2.setText("Message:");

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGap(4, 4, 4)
            .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(2, 2, 2)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 455, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(2, 2, 2)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jSeparator2)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(16, 16, 16)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGap(2, 2, 2))
    );

    pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        search();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnSubmitPaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubmitPaymentActionPerformed
        processPayment();
    }//GEN-LAST:event_btnSubmitPaymentActionPerformed

    private void btnViewInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewInvoiceActionPerformed
//        int index = tblInvoices.getSelectedRow();
//        if (index != -1) {
//            Long id = invoices.get(index).getId();
//
//            Window w = SwingUtilities.getWindowAncestor(this);
//            Frame owner = w instanceof Frame ? (Frame) w : null;
//            PurchaseInvoiceDlg dlg = new PurchaseInvoiceDlg(owner);
//            dlg.showDialog(id);
//        }
    }//GEN-LAST:event_btnViewInvoiceActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEmail;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnSubmitPayment;
    private javax.swing.JButton btnViewInvoice;
    private javax.swing.JButton btnViewReport;
    private org.jdesktop.swingx.JXBusyLabel busyLabel;
    private javax.swing.ButtonGroup buttonGroup1;
    private org.jdesktop.swingx.JXDatePicker dtFrom;
    private org.jdesktop.swingx.JXDatePicker dtTo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblDMemo;
    private javax.swing.JLabel lblInvAmount;
    private javax.swing.JLabel lblOther;
    private javax.swing.JProgressBar progressBar;
    private org.jdesktop.swingx.JXTable tblAcDoc;
    private org.jdesktop.swingx.JXTable tblTicket;
    private javax.swing.JTextField txtAmount;
    // End of variables declaration//GEN-END:variables

    private void resetPaymentComponent() {
        btnSubmitPayment.setEnabled(true);
        txtAmount.setText("");
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress".equals(evt.getPropertyName())) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
            if (progress == 100) {
                try {
                    if (null != taskType) {
                        switch (taskType) {
                            case "SEARCH":
                                BSPReport report = task.get();
                                dueInvoices = report.getDueInvoices();
                                adm_acm = report.getAdm_acm();
                                gdsSaleReport = report.getSale_report();
                                tickets = gdsSaleReport.getList();

                                populateTblTicket();
                                populateTblDocument();
                                taskType = "";
                                break;
                            case "PAYMENT":
                                resetPaymentComponent();
                                search();
                                break;
                        }
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(TPurchaseBatchPayment.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    btnSearch.setEnabled(true);
                }
            }
        }
    }
}
