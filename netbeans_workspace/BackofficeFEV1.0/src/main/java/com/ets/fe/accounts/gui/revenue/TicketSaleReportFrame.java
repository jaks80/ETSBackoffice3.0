package com.ets.fe.accounts.gui.revenue;

import com.ets.fe.pnr.model.TicketSaleReport;
import com.ets.fe.pnr.model.TicketSaleReport.SaleReportLine;
import com.ets.fe.pnr.task.TicketSaleReportTask;
import com.ets.fe.settings.model.User;
import com.ets.fe.settings.model.Users;
import com.ets.fe.settings.task.UserTask;
import com.ets.fe.util.DateUtil;
import com.ets.fe.util.Enums;
import java.awt.Color;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import org.jdesktop.swingx.JXTable;

/**
 *
 * @author Yusuf
 */
public class TicketSaleReportFrame extends JInternalFrame implements PropertyChangeListener {

    private JDesktopPane desktopPane;
    private TicketSaleReportTask task;
    private UserTask userTask;
    private TicketSaleReport report;
    private List<User> users;
    private String taskType = "";

    private Long userid;
    private Enums.TicketingType ticketingType;
    private Enums.TicketStatus ticketStatus = null;
    private String airLineCode = null;
    private String ticketingAgtOid = null;
    private Date from = null;
    private Date to = null;

    public TicketSaleReportFrame(JDesktopPane desktopPane) {
        initComponents();
        this.desktopPane = desktopPane;
        dtFrom.setDate(DateUtil.getBeginingOfMonth());
        dtTo.setDate(DateUtil.getEndOfMonth());
        setIssueType();
        setTicketStatus();
        //int inset = 0;
        //Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        //setBounds(inset, inset, screenSize.width / 2, screenSize.height / 2);
        loadUsers();
    }

    private void search() {
        btnSearch.setEnabled(false);
        ticketingType = (Enums.TicketingType) cmbIssueType.getSelectedItem();

        if (cmbTicketStatus.getSelectedIndex() > 0) {
            ticketStatus = (Enums.TicketStatus) cmbTicketStatus.getSelectedItem();
        }

        airLineCode = txtCareerCode.getText();
        ticketingAgtOid = txtOfficeId.getText();

        from = dtFrom.getDate();
        to = dtTo.getDate();

        if ("All".equals(ticketStatus)) {
            ticketStatus = null;
        }

        if (ticketingAgtOid == null || ticketingAgtOid.isEmpty()) {
            ticketingAgtOid = null;
        }

        if (airLineCode == null || airLineCode.isEmpty()) {
            airLineCode = null;
        }

        if (cmbCashier.getSelectedIndex() > 0) {
            User user = users.get(cmbCashier.getSelectedIndex() - 1);
            if (user != null) {
                userid = user.getId();
            }
        } else {
            userid = null;
        }
        progressBar.setValue(0);

        task = new TicketSaleReportTask(userid,ticketingType, ticketStatus, airLineCode, from, to, ticketingAgtOid);
        task.addPropertyChangeListener(this);
        task.execute();
    }

    private void loadUsers() {
        taskType = "USER";
        userTask = new UserTask();
        userTask.addPropertyChangeListener(this);
        userTask.execute();
    }

    private void populateSummery(){
     lblTktQty.setText(String.valueOf(report.getTktQty()));
     lblTotalPurchase.setText(report.getTotalNetPurchaseFare());
     lblTotalSelling.setText(report.getTotalNetSellingFare());
     lblTotalRevenue.setText(report.getTotalRevenue());
     
    }
    
    private void populateTblTicket() {
        List<SaleReportLine> lines = report.getSaleReportLines();
        DefaultTableModel tableModel = (DefaultTableModel) tblTicket.getModel();
        tableModel.getDataVector().removeAllElements();
        tblTicket.repaint();
        int row = 0;

        if (lines.size() > 0) {
            for (SaleReportLine t : lines) {
                tableModel.insertRow(row, new Object[]{row+1,t.getDocIssuedate(), t.getTktStatus(),
                    t.getGdsPnr(), t.getClient(), t.getTicketingAgent(),
                    t.getAirLineCode(), t.getTicketNo(), t.getSellingRefference(),
                    t.getNetPurchaseFare(), t.getNetSellingFare(), t.getRevenue(), t.getAtolChg()});
                row++;
            }
            tableModel.insertRow(row, new Object[]{"", "", "", "", "", "", "", "", report.getTotalNetPurchaseFare(),
                report.getTotalNetSellingFare(), report.getTotalRevenue(), report.getTotalAtolChg()});
        } else {
            tableModel.insertRow(0, new Object[]{});
        }
        
        populateSummery();
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
        jPanel5 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblTicket = new JXTable(){public Component prepareRenderer(TableCellRenderer renderer,
            int rowIndex, int vColIndex) {
            Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);  
            String s = "";
            Object o = tblTicket.getModel().getValueAt(rowIndex, 2);       
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
    jPanel2 = new javax.swing.JPanel();
    jLabel9 = new javax.swing.JLabel();
    jLabel10 = new javax.swing.JLabel();
    jLabel11 = new javax.swing.JLabel();
    jLabel12 = new javax.swing.JLabel();
    lblTktQty = new javax.swing.JLabel();
    lblTotalPurchase = new javax.swing.JLabel();
    lblTotalSelling = new javax.swing.JLabel();
    lblTotalRevenue = new javax.swing.JLabel();
    jPanel1 = new javax.swing.JPanel();
    jLabel3 = new javax.swing.JLabel();
    cmbIssueType = new javax.swing.JComboBox();
    jLabel1 = new javax.swing.JLabel();
    cmbTicketStatus = new javax.swing.JComboBox();
    jLabel4 = new javax.swing.JLabel();
    dtFrom = new org.jdesktop.swingx.JXDatePicker();
    jLabel5 = new javax.swing.JLabel();
    dtTo = new org.jdesktop.swingx.JXDatePicker();
    txtOfficeId = new javax.swing.JTextField();
    jLabel6 = new javax.swing.JLabel();
    jLabel7 = new javax.swing.JLabel();
    txtCareerCode = new javax.swing.JTextField();
    progressBar = new javax.swing.JProgressBar();
    jLabel2 = new javax.swing.JLabel();
    cmbCashier = new javax.swing.JComboBox();
    jLabel8 = new javax.swing.JLabel();
    jPanel4 = new javax.swing.JPanel();
    btnViewReport = new javax.swing.JButton();
    btnViewInvoice = new javax.swing.JButton();
    btnEmail = new javax.swing.JButton();
    btnPrint = new javax.swing.JButton();
    btnSearch = new javax.swing.JButton();

    setClosable(true);
    setIconifiable(true);
    setMaximizable(true);
    setResizable(true);
    setTitle("Sale Report");
    setMinimumSize(new java.awt.Dimension(1000, 500));
    setPreferredSize(new java.awt.Dimension(1000, 500));

    jSplitPane1.setDividerLocation(180);
    jSplitPane1.setDividerSize(4);

    tblTicket.setBackground(new java.awt.Color(0, 0, 0));
    tblTicket.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {

        },
        new String [] {
            "", "Date", "Status", "Pnr", "B.Agent", "T.Agent", "AirLine", "TktNo", "SalesInv", "NetPurchase", "NetSelling", "Revenue", "ATOL"
        }
    ) {
        boolean[] canEdit = new boolean [] {
            false, false, false, false, false, false, false, false, false, false, false, false, false
        };

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit [columnIndex];
        }
    });
    tblTicket.getTableHeader().setReorderingAllowed(false);
    jScrollPane3.setViewportView(tblTicket);
    if (tblTicket.getColumnModel().getColumnCount() > 0) {
        tblTicket.getColumnModel().getColumn(0).setMaxWidth(30);
        tblTicket.getColumnModel().getColumn(6).setMaxWidth(55);
        tblTicket.getColumnModel().getColumn(7).setMinWidth(100);
    }

    jPanel2.setBackground(new java.awt.Color(255, 255, 255));
    jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

    jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
    jLabel9.setText("No Of Tickets:");

    jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
    jLabel10.setText("Total Net PurchaseFare:");

    jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
    jLabel11.setText("Total Net Selling:");

    jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
    jLabel12.setText("Total Revenue:");

    lblTktQty.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
    lblTktQty.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    lblTktQty.setText("0.00");

    lblTotalPurchase.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
    lblTotalPurchase.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    lblTotalPurchase.setText("0.00");

    lblTotalSelling.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
    lblTotalSelling.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    lblTotalSelling.setText("0.00");

    lblTotalRevenue.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
    lblTotalRevenue.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    lblTotalRevenue.setText("0.00");

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel2Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addComponent(lblTktQty, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)
                .addComponent(lblTotalPurchase, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblTotalSelling, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblTotalRevenue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addContainerGap(20, Short.MAX_VALUE))
    );
    jPanel2Layout.setVerticalGroup(
        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel2Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel9)
                .addComponent(lblTktQty))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel10)
                .addComponent(lblTotalPurchase))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel11)
                .addComponent(lblTotalSelling))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel12)
                .addComponent(lblTotalRevenue))
            .addContainerGap(28, Short.MAX_VALUE))
    );

    javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
    jPanel5.setLayout(jPanel5Layout);
    jPanel5Layout.setHorizontalGroup(
        jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 799, Short.MAX_VALUE)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
            .addGap(0, 0, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
    );
    jPanel5Layout.setVerticalGroup(
        jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE))
    );

    jSplitPane1.setRightComponent(jPanel5);

    jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Search", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
    jPanel1.setLayout(new java.awt.GridBagLayout());

    jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
    jLabel3.setText("Issue Type");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
    gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
    jPanel1.add(jLabel3, gridBagConstraints);

    cmbIssueType.setPreferredSize(new java.awt.Dimension(99, 18));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
    gridBagConstraints.weightx = 0.3;
    gridBagConstraints.insets = new java.awt.Insets(0, 4, 4, 4);
    jPanel1.add(cmbIssueType, gridBagConstraints);

    jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
    jLabel1.setText("Ticket Status");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
    gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
    jPanel1.add(jLabel1, gridBagConstraints);

    cmbTicketStatus.setPreferredSize(new java.awt.Dimension(67, 18));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
    gridBagConstraints.weightx = 0.3;
    gridBagConstraints.insets = new java.awt.Insets(0, 4, 4, 4);
    jPanel1.add(cmbTicketStatus, gridBagConstraints);

    jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
    jLabel4.setText("Date From");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
    gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
    jPanel1.add(jLabel4, gridBagConstraints);

    dtFrom.setMinimumSize(new java.awt.Dimension(110, 20));
    dtFrom.setPreferredSize(new java.awt.Dimension(110, 20));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
    gridBagConstraints.weightx = 0.3;
    gridBagConstraints.insets = new java.awt.Insets(0, 4, 4, 4);
    jPanel1.add(dtFrom, gridBagConstraints);

    jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
    jLabel5.setText("Date To");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
    gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
    jPanel1.add(jLabel5, gridBagConstraints);

    dtTo.setPreferredSize(new java.awt.Dimension(110, 20));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 7;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
    gridBagConstraints.weightx = 0.3;
    gridBagConstraints.insets = new java.awt.Insets(0, 4, 4, 4);
    jPanel1.add(dtTo, gridBagConstraints);

    txtOfficeId.setToolTipText("Ticketing agent office Id, Separated by comma, Example: ABC123AB,CDE123CD");
    txtOfficeId.setMinimumSize(new java.awt.Dimension(110, 22));
    txtOfficeId.setPreferredSize(new java.awt.Dimension(110, 22));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 11;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
    gridBagConstraints.weightx = 0.3;
    gridBagConstraints.insets = new java.awt.Insets(0, 4, 4, 4);
    jPanel1.add(txtOfficeId, gridBagConstraints);

    jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
    jLabel6.setText("Career Code");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 8;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
    gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
    jPanel1.add(jLabel6, gridBagConstraints);

    jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
    jLabel7.setText("Ticketed OfficeID");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 10;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
    jPanel1.add(jLabel7, gridBagConstraints);

    txtCareerCode.setToolTipText("Separated by comma, Example: SV,EK,BA");
    txtCareerCode.setMinimumSize(new java.awt.Dimension(110, 22));
    txtCareerCode.setPreferredSize(new java.awt.Dimension(110, 22));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 9;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
    gridBagConstraints.weightx = 0.3;
    gridBagConstraints.insets = new java.awt.Insets(0, 4, 4, 4);
    jPanel1.add(txtCareerCode, gridBagConstraints);

    progressBar.setMaximumSize(new java.awt.Dimension(146, 16));
    progressBar.setMinimumSize(new java.awt.Dimension(146, 16));
    progressBar.setPreferredSize(new java.awt.Dimension(146, 16));
    progressBar.setStringPainted(true);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 15;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_END;
    jPanel1.add(progressBar, gridBagConstraints);

    jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel2.setText("Message:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 14;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_END;
    jPanel1.add(jLabel2, gridBagConstraints);

    cmbCashier.setPreferredSize(new java.awt.Dimension(28, 19));
    cmbCashier.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            cmbCashierActionPerformed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 13;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 4, 4, 4);
    jPanel1.add(cmbCashier, gridBagConstraints);

    jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
    jLabel8.setText("Cashier");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 12;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
    gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
    jPanel1.add(jLabel8, gridBagConstraints);

    jSplitPane1.setLeftComponent(jPanel1);

    jPanel4.setBackground(new java.awt.Color(102, 102, 102));
    jPanel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

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

    javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
    jPanel4.setLayout(jPanel4Layout);
    jPanel4Layout.setHorizontalGroup(
        jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
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
    jPanel4Layout.setVerticalGroup(
        jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(btnViewInvoice, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addComponent(btnEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addComponent(btnPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addComponent(btnViewReport, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addComponent(btnSearch, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
    );

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(jSplitPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(0, 0, 0)
            .addComponent(jSplitPane1))
    );

    pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnViewReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewReportActionPerformed

    }//GEN-LAST:event_btnViewReportActionPerformed

    private void btnViewInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewInvoiceActionPerformed

    }//GEN-LAST:event_btnViewInvoiceActionPerformed

    private void btnEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmailActionPerformed

    }//GEN-LAST:event_btnEmailActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed

    }//GEN-LAST:event_btnPrintActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        search();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void cmbCashierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbCashierActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbCashierActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEmail;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnViewInvoice;
    private javax.swing.JButton btnViewReport;
    private javax.swing.JComboBox cmbCashier;
    private javax.swing.JComboBox cmbIssueType;
    private javax.swing.JComboBox cmbTicketStatus;
    private org.jdesktop.swingx.JXDatePicker dtFrom;
    private org.jdesktop.swingx.JXDatePicker dtTo;
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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JLabel lblTktQty;
    private javax.swing.JLabel lblTotalPurchase;
    private javax.swing.JLabel lblTotalRevenue;
    private javax.swing.JLabel lblTotalSelling;
    private javax.swing.JProgressBar progressBar;
    private org.jdesktop.swingx.JXTable tblTicket;
    private javax.swing.JTextField txtCareerCode;
    private javax.swing.JTextField txtOfficeId;
    // End of variables declaration//GEN-END:variables

    private void setTicketStatus() {
        DefaultComboBoxModel model = new DefaultComboBoxModel(Enums.TicketStatus.values());
        model.insertElementAt("All", 0);
        cmbTicketStatus.setModel(model);
        cmbTicketStatus.setSelectedIndex(0);
    }

    private void setIssueType() {
        DefaultComboBoxModel model = new DefaultComboBoxModel(Enums.TicketingType.values());
        cmbIssueType.setModel(model);
        cmbIssueType.setSelectedIndex(0);
    }

    private void populateUsers() {
        List cmbElement = new ArrayList();

        for (User user : users) {
            cmbElement.add(user.calculateFullName() + "-" + user.getId());
        }

        DefaultComboBoxModel cmbContactableModel = new DefaultComboBoxModel(cmbElement.toArray());
        cmbContactableModel.insertElementAt("All", 0);
        cmbCashier.setModel(cmbContactableModel);
        cmbCashier.setSelectedIndex(0);
        cmbCashier.setEnabled(true);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress".equals(evt.getPropertyName())) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
            if (progress == 100) {
                try {
                    if ("USER".equals(taskType)) {
                        Users user_ = userTask.get();
                        users = user_.getList();
                        populateUsers();
                        taskType = "";
                    } else {

                        report = task.get();
                        populateTblTicket();
                        btnSearch.setEnabled(true);
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(TicketSaleReportFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
