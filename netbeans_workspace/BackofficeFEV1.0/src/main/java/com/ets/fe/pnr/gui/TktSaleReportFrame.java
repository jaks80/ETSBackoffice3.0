package com.ets.fe.pnr.gui;

import com.ets.fe.Main;
import com.ets.fe.pnr.model.Ticket;
import com.ets.fe.report.model.TicketSaleReport;
import com.ets.fe.util.DateUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JInternalFrame;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import org.jdesktop.swingx.JXTable;

/**
 *
 * @author Yusuf
 */
public class TktSaleReportFrame extends JInternalFrame implements PropertyChangeListener {

    List<Ticket> list = new ArrayList<>();
    TktSalesReportTask task;

    public TktSaleReportFrame() {
        initComponents();
        dtFrom.setDate(DateUtil.getBeginingOfMonth());
        dtTo.setDate(DateUtil.getEndOfMonth());

        int inset = 0;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset, screenSize.width / 2, screenSize.height / 2);

    }

    private void buttonSearchActionPerformed(ActionEvent event) {

        String issueType = (String) cmbIssueType.getSelectedItem();
        String ticketStatus = (String) cmbTicketStatus.getSelectedItem();
        String airLineCode = txtCareerCode.getText();
        String ticketingAgtOid = txtOfficeId.getText();

        Date from = dtFrom.getDate();
        Date to = dtTo.getDate();

        if ("All".equals(ticketStatus)) {
            ticketStatus = null;
        }

        if (ticketingAgtOid == null || ticketingAgtOid.isEmpty()) {
            ticketingAgtOid = null;
        }

        if (airLineCode == null || airLineCode.isEmpty()) {
            airLineCode = null;
        }

        progressBar.setValue(0);

        task = new TktSalesReportTask(ticketStatus, airLineCode, from, to, ticketingAgtOid);
        task.addPropertyChangeListener(this);
        task.execute();
    }

    private void populateTblTicket() {
        TicketSaleReport report = task.getReport();
        list = report.getList();
        DefaultTableModel tableModel = (DefaultTableModel) tblTicket.getModel();
        tableModel.getDataVector().removeAllElements();
        tblTicket.repaint();
        int row = 0;

        if (list.size() > 0) {
            for (Ticket t : list) {
                tableModel.insertRow(row, new Object[]{t.getTktDateString(), t.getTktStatus(),
                    t.getPnr().getGdsPnr(), t.getPnr().getBookingAgtOid(), t.getPnr().getTicketingAgtOid(),
                    t.getPnr().getAirLineCode(), t.getFullPaxNameWithPaxNo(), t.getFullTicketNo(),
                    t.getBaseFare(), t.getTax(), t.getCommission(), t.getFee(), t.getNetPurchaseFare()});
                row++;
            }
        } else {
            tableModel.insertRow(0, new Object[]{});
        }

        HTMLEditorKit kit = new HTMLEditorKit();
        StyleSheet s = kit.getStyleSheet();

        s.addRule("body {font-family: Tahoma; font-size: 10 px;font-weight: bold;color : white;}");

        txtSaleSummery.setEditorKit(kit);
        txtSaleSummery.setText(report.getSummery());
        btnSearch.setEnabled(true);
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
        btnSearch = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtSaleSummery = new javax.swing.JTextPane();
        jPanel3 = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
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

    setClosable(true);
    setIconifiable(true);
    setMaximizable(true);
    setResizable(true);
    setTitle("Sale Report");
    getContentPane().setLayout(new java.awt.GridBagLayout());

    jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Search", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
    jPanel1.setLayout(new java.awt.GridBagLayout());

    jLabel3.setText("Issue Type");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
    gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
    jPanel1.add(jLabel3, gridBagConstraints);

    cmbIssueType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All", "Self Issue", "3rd Party Issue" }));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 0.3;
    gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
    jPanel1.add(cmbIssueType, gridBagConstraints);

    jLabel1.setText("Ticket Status");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
    jPanel1.add(jLabel1, gridBagConstraints);

    cmbTicketStatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All", "ISSUE", "REISSUE", "REFUND", "VOID" }));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 0.3;
    gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
    jPanel1.add(cmbTicketStatus, gridBagConstraints);

    jLabel4.setText("Date From");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
    jPanel1.add(jLabel4, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 0.3;
    gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
    jPanel1.add(dtFrom, gridBagConstraints);

    jLabel5.setText("Date To");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
    jPanel1.add(jLabel5, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 0.3;
    gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
    jPanel1.add(dtTo, gridBagConstraints);

    txtOfficeId.setToolTipText("Ticketing agent office Id, Separated by comma, Example: ABC123AB,CDE123CD");
    txtOfficeId.setMinimumSize(new java.awt.Dimension(110, 22));
    txtOfficeId.setPreferredSize(new java.awt.Dimension(110, 22));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 5;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 0.3;
    gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
    jPanel1.add(txtOfficeId, gridBagConstraints);

    jLabel6.setText("Career Code");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
    jPanel1.add(jLabel6, gridBagConstraints);

    jLabel7.setText("Ticketed OfficeID");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 4;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
    jPanel1.add(jLabel7, gridBagConstraints);

    txtCareerCode.setToolTipText("Separated by comma, Example: SV,EK,BA");
    txtCareerCode.setMinimumSize(new java.awt.Dimension(110, 22));
    txtCareerCode.setPreferredSize(new java.awt.Dimension(110, 22));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 0.3;
    gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
    jPanel1.add(txtCareerCode, gridBagConstraints);

    btnSearch.setText("Search");
    btnSearch.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnSearchActionPerformed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 6;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.weightx = 0.3;
    jPanel1.add(btnSearch, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
    gridBagConstraints.weightx = 0.3;
    gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
    getContentPane().add(jPanel1, gridBagConstraints);

    jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Summery", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

    txtSaleSummery.setEditable(false);
    txtSaleSummery.setBackground(new java.awt.Color(0, 0, 0));
    jScrollPane1.setViewportView(txtSaleSummery);

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
    );
    jPanel2Layout.setVerticalGroup(
        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel2Layout.createSequentialGroup()
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(0, 0, Short.MAX_VALUE))
    );

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 0.7;
    gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
    getContentPane().add(jPanel2, gridBagConstraints);

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

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
    getContentPane().add(jPanel3, gridBagConstraints);

    tblTicket.setBackground(new java.awt.Color(0, 0, 0));
    tblTicket.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {

        },
        new String [] {
            "Date", "Status", "Pnr", "B.Agent", "T.Agent", "Car", "Pax Name", "TktNo", "Base Fare", "Tax", "Com", "Fee", "Total Fare", "NetFare"
        }
    ) {
        boolean[] canEdit = new boolean [] {
            false, false, false, false, false, false, false, false, false, false, false, false, false, false
        };

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit [columnIndex];
        }
    });
    tblTicket.getTableHeader().setReorderingAllowed(false);
    jScrollPane3.setViewportView(tblTicket);
    if (tblTicket.getColumnModel().getColumnCount() > 0) {
        tblTicket.getColumnModel().getColumn(5).setMaxWidth(40);
        tblTicket.getColumnModel().getColumn(6).setMinWidth(150);
        tblTicket.getColumnModel().getColumn(7).setMinWidth(100);
    }

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
    getContentPane().add(jScrollPane3, gridBagConstraints);

    pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        buttonSearchActionPerformed(evt);
        btnSearch.setEnabled(false);
    }//GEN-LAST:event_btnSearchActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSearch;
    private javax.swing.JComboBox cmbIssueType;
    private javax.swing.JComboBox cmbTicketStatus;
    private org.jdesktop.swingx.JXDatePicker dtFrom;
    private org.jdesktop.swingx.JXDatePicker dtTo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JProgressBar progressBar;
    private org.jdesktop.swingx.JXTable tblTicket;
    private javax.swing.JTextField txtCareerCode;
    private javax.swing.JTextField txtOfficeId;
    private javax.swing.JTextPane txtSaleSummery;
    // End of variables declaration//GEN-END:variables
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
            if (progress == 100) {
                populateTblTicket();
            }
        }
    }
}
