package com.ets.fe.accounts.gui.report;

import com.ets.fe.Application;
import com.ets.fe.acdoc.gui.PurchaseInvoiceDlg;
import com.ets.fe.accounts.model.AccountsReport;
import com.ets.fe.accounts.model.AccountsReport.AccountsLine;
import com.ets.fe.accounts.task.AccountsHistoryTask;
import com.ets.fe.acdoc.gui.PurchaseAcDocumentDlg;
import com.ets.fe.acdoc.gui.report.TPurchaseInvoiceReportingFrame;
import com.ets.fe.report.BeanJasperReport;
import com.ets.fe.util.DateUtil;
import com.ets.fe.util.Enums;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import org.jdesktop.swingx.JXTable;

/**
 *
 * @author Yusuf
 */
public class TPurchaseAccountsFrame extends javax.swing.JInternalFrame implements PropertyChangeListener {

    private JDesktopPane desktopPane;
    private AccountsHistoryTask task;
    private AccountsReport report;
    private List<AccountsReport.AccountsLine> lines = new ArrayList<>();

    public TPurchaseAccountsFrame(JDesktopPane desktopPane) {
        this.desktopPane = desktopPane;
        initComponents();
        dtFrom.setDate(DateUtil.getBeginingOfMonth());
        dtTo.setDate(DateUtil.getEndOfMonth());
    }

    private void search() {

        btnSearch.setEnabled(false);
        Enums.ClientType client_type = documentSearchComponent.getContactableType();
        Long client_id = documentSearchComponent.getClient_id();
        Date from = dtFrom.getDate();
        Date to = dtTo.getDate();

        task = new AccountsHistoryTask(client_type, client_id, from, to, progressBar, Enums.SaleType.TKTPURCHASE);
        task.addPropertyChangeListener(this);
        task.execute();
    }

    private void populateSummery(AccountsReport r) {
        lblInvAmount.setText(r.getTotalInvAmount());
        lblCMemo.setText(r.getTotalCMAmount());
        lblDMemo.setText(r.getTotalDMAmount());
        lblPayment.setText(r.getTotalPayment());
        lblRefund.setText(r.getTotalRefund());
    }

    private void populateTable() {

        DefaultTableModel tableModel = (DefaultTableModel) tblAccounts.getModel();
        tableModel.getDataVector().removeAllElements();

        if (lines.size() > 0) {
            int i = 0;
            for (; i < lines.size(); i++) {
                AccountsLine l = lines.get(i);
                tableModel.insertRow(i, new Object[]{l.getDate(), l.getDocType(), l.getLine_desc(), l.getDebit_amount(), l.getCredit_amount(), l.getLine_balance()});
            }
        } else {
            tableModel.insertRow(0, new Object[]{"", "", "", "", "", ""});
        }
        populateSummery(report);
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
        btnViewReport = new javax.swing.JButton();
        btnViewInvoice = new javax.swing.JButton();
        btnEmail = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        btnSearch = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel4 = new javax.swing.JPanel();
        documentSearchComponent = new com.ets.fe.acdoc.gui.comp.ClientSearchComp(false,false,false,Enums.AgentType.TICKETING_AGT);
        jLabel6 = new javax.swing.JLabel();
        dtFrom = new org.jdesktop.swingx.JXDatePicker();
        jLabel8 = new javax.swing.JLabel();
        dtTo = new org.jdesktop.swingx.JXDatePicker();
        jLabel7 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jPanel2 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblInvAmount = new javax.swing.JLabel();
        lblCMemo = new javax.swing.JLabel();
        lblDMemo = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        lblPayment = new javax.swing.JLabel();
        lblRefund = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblAccounts = new JXTable(){public Component prepareRenderer(TableCellRenderer renderer,
            int rowIndex, int vColIndex) {
            Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);  
            String s = "";       

            Object o = tblAccounts.getModel().getValueAt(rowIndex, 1);               
            if(o!=null){
                s = o.toString();        
            }

            if(s.equalsIgnoreCase("INVOICE") || s.equalsIgnoreCase("DEBITMEMO")|| s.equalsIgnoreCase("REFUND")){
                c.setForeground(Color.GREEN);
            }else if(s.equalsIgnoreCase("PAYMENT") || s.equalsIgnoreCase("CREDITMEMO")){
                c.setForeground(Color.RED);
            }else{
                c.setForeground(Color.WHITE);
            }
            return c;
        }
    };

    setClosable(true);
    setMaximizable(true);
    setResizable(true);
    setTitle("Vendor accounts history");

    jPanel1.setBackground(new java.awt.Color(102, 102, 102));
    jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

    btnViewReport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/details18.png"))); // NOI18N
    btnViewReport.setMaximumSize(new java.awt.Dimension(35, 22));
    btnViewReport.setMinimumSize(new java.awt.Dimension(35, 22));
    btnViewReport.setPreferredSize(new java.awt.Dimension(35, 22));
    btnViewReport.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnViewReportActionPerformed(evt);
        }
    });

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
    btnEmail.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnEmailActionPerformed(evt);
        }
    });

    btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/print18.png"))); // NOI18N
    btnPrint.setMaximumSize(new java.awt.Dimension(35, 22));
    btnPrint.setMinimumSize(new java.awt.Dimension(35, 22));
    btnPrint.setPreferredSize(new java.awt.Dimension(35, 22));
    btnPrint.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnPrintActionPerformed(evt);
        }
    });

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

    jSplitPane1.setDividerLocation(200);
    jSplitPane1.setDividerSize(4);

    jLabel6.setText("Date From");

    dtFrom.setPreferredSize(new java.awt.Dimension(110, 20));

    jLabel8.setText("Date To");

    dtTo.setPreferredSize(new java.awt.Dimension(110, 20));

    jLabel7.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
    jLabel7.setForeground(new java.awt.Color(255, 0, 0));
    jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    jLabel7.setText("Accounts History");

    jLabel10.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
    jLabel10.setForeground(new java.awt.Color(255, 0, 0));
    jLabel10.setText("Vendor");

    javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
    jPanel4.setLayout(jPanel4Layout);
    jPanel4Layout.setHorizontalGroup(
        jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel4Layout.createSequentialGroup()
            .addGap(71, 71, 71)
            .addComponent(jLabel10)
            .addGap(0, 0, Short.MAX_VALUE))
        .addGroup(jPanel4Layout.createSequentialGroup()
            .addGap(2, 2, 2)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jSeparator2)
                .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(dtFrom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(dtTo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel6)
                        .addComponent(jLabel8))
                    .addContainerGap())
                .addComponent(documentSearchComponent, javax.swing.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)))
    );
    jPanel4Layout.setVerticalGroup(
        jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel4Layout.createSequentialGroup()
            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jLabel10)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jLabel6)
            .addGap(2, 2, 2)
            .addComponent(dtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jLabel8)
            .addGap(2, 2, 2)
            .addComponent(dtTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(documentSearchComponent, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(15, Short.MAX_VALUE))
    );

    jSplitPane1.setLeftComponent(jPanel4);

    jPanel5.setBackground(new java.awt.Color(255, 255, 255));
    jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
    jPanel5.setLayout(new java.awt.GridBagLayout());

    jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
    jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel1.setText("Invoice Amount:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
    gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
    jPanel5.add(jLabel1, gridBagConstraints);

    jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
    jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel3.setText("Credit Memo:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel5.add(jLabel3, gridBagConstraints);

    jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
    jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel4.setText("Refund:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel5.add(jLabel4, gridBagConstraints);

    lblInvAmount.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
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
    jPanel5.add(lblInvAmount, gridBagConstraints);

    lblCMemo.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
    lblCMemo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    lblCMemo.setText("0.00");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel5.add(lblCMemo, gridBagConstraints);

    lblDMemo.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
    lblDMemo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    lblDMemo.setText("0.00");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel5.add(lblDMemo, gridBagConstraints);

    jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
    jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel9.setText("Payment:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
    gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
    jPanel5.add(jLabel9, gridBagConstraints);

    lblPayment.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
    lblPayment.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    lblPayment.setText("0.00");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 4);
    jPanel5.add(lblPayment, gridBagConstraints);

    lblRefund.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
    lblRefund.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    lblRefund.setText("0.00");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel5.add(lblRefund, gridBagConstraints);

    jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
    jLabel5.setText("Debit Memo:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel5.add(jLabel5, gridBagConstraints);

    tblAccounts.setBackground(new java.awt.Color(51, 51, 51));
    tblAccounts.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {

        },
        new String [] {
            "Date", "Type", "", "Amount (Debit)", "Amount (Credit)", "Outstanding"
        }
    ) {
        boolean[] canEdit = new boolean [] {
            false, false, false, false, false, false
        };

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit [columnIndex];
        }
    });
    tblAccounts.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
    tblAccounts.setSortable(false);
    tblAccounts.getTableHeader().setReorderingAllowed(false);
    jScrollPane1.setViewportView(tblAccounts);
    if (tblAccounts.getColumnModel().getColumnCount() > 0) {
        tblAccounts.getColumnModel().getColumn(0).setMinWidth(100);
        tblAccounts.getColumnModel().getColumn(0).setPreferredWidth(100);
        tblAccounts.getColumnModel().getColumn(0).setMaxWidth(100);
        tblAccounts.getColumnModel().getColumn(1).setMinWidth(90);
        tblAccounts.getColumnModel().getColumn(1).setPreferredWidth(90);
        tblAccounts.getColumnModel().getColumn(1).setMaxWidth(90);
        tblAccounts.getColumnModel().getColumn(3).setMinWidth(100);
        tblAccounts.getColumnModel().getColumn(3).setPreferredWidth(100);
        tblAccounts.getColumnModel().getColumn(3).setMaxWidth(100);
        tblAccounts.getColumnModel().getColumn(4).setMinWidth(100);
        tblAccounts.getColumnModel().getColumn(4).setPreferredWidth(100);
        tblAccounts.getColumnModel().getColumn(4).setMaxWidth(100);
        tblAccounts.getColumnModel().getColumn(5).setMinWidth(100);
        tblAccounts.getColumnModel().getColumn(5).setPreferredWidth(100);
        tblAccounts.getColumnModel().getColumn(5).setMaxWidth(100);
    }

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel2Layout.createSequentialGroup()
            .addContainerGap(385, Short.MAX_VALUE)
            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
    );
    jPanel2Layout.setVerticalGroup(
        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel2Layout.createSequentialGroup()
            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE))
    );

    jSplitPane1.setRightComponent(jPanel2);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        .addComponent(jSplitPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
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

    private void btnViewReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewReportActionPerformed
        report("VIEW");
    }//GEN-LAST:event_btnViewReportActionPerformed

    private void btnViewInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewInvoiceActionPerformed
        int index = tblAccounts.getSelectedRow();
        if (index != -1) {
            viewDocument(lines.get(index));
        }
    }//GEN-LAST:event_btnViewInvoiceActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        search();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        report("PRINT");
    }//GEN-LAST:event_btnPrintActionPerformed

    private void btnEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmailActionPerformed
        if (report == null) {
            return;
        }

        String receipent = report.getEmail();
        String subject = report.getReportTitle().concat(" From").concat(Application.getMainAgent().getName());
        String body = report.getReportTitle().concat(" From").concat(Application.getMainAgent().getName());
        String refference = "report";
        if (receipent != null) {
            BeanJasperReport jasperreport = new BeanJasperReport(receipent, subject, body, refference);
            List<AccountsReport> list = new ArrayList<>();
            list.add(report);
            jasperreport.accountStatement(list, "EMAIL");
        } else {
            JOptionPane.showMessageDialog(null, "No Email address", "Email", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnEmailActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEmail;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnViewInvoice;
    private javax.swing.JButton btnViewReport;
    private com.ets.fe.acdoc.gui.comp.ClientSearchComp documentSearchComponent;
    private org.jdesktop.swingx.JXDatePicker dtFrom;
    private org.jdesktop.swingx.JXDatePicker dtTo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JLabel lblCMemo;
    private javax.swing.JLabel lblDMemo;
    private javax.swing.JLabel lblInvAmount;
    private javax.swing.JLabel lblPayment;
    private javax.swing.JLabel lblRefund;
    private javax.swing.JProgressBar progressBar;
    private org.jdesktop.swingx.JXTable tblAccounts;
    // End of variables declaration//GEN-END:variables
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress".equals(evt.getPropertyName())) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
            if (progress == 100) {
                try {
                    report = task.get();
                    report.addFirstLine();
                    report.addLastLine();
                    lines = report.getLines();
                    populateTable();
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(TPurchaseInvoiceReportingFrame.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    btnSearch.setEnabled(true);
                }
            }
        }
    }

    public void viewDocument(AccountsLine line) {

        Window w = SwingUtilities.getWindowAncestor(this);
        Frame owner = w instanceof Frame ? (Frame) w : null;

        if (line.getDocType().equals(Enums.AcDocType.INVOICE.toString())) {

            PurchaseInvoiceDlg dlg = new PurchaseInvoiceDlg(owner);
            dlg.showDialog(line.getId());
        } else if (line.getDocType().equals(Enums.AcDocType.DEBITMEMO.toString())
                || line.getDocType().equals(Enums.AcDocType.CREDITMEMO.toString())) {

            PurchaseAcDocumentDlg dlg = new PurchaseAcDocumentDlg(owner);
            dlg.showDialog(line.getId());
        }
    }

    private void report(String action) {
        BeanJasperReport jasperreport = new BeanJasperReport();
        List<AccountsReport> list = new ArrayList<>();
        list.add(report);
        jasperreport.accountStatement(list, action);
    }
}
