package com.ets.fe.a_main;

import com.ets.fe.a_maintask.GlobalSearchTask;
import com.ets.fe.pnr.gui.task.DeletePnrTask;
import com.ets.fe.pnr.model.Pnr;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JInternalFrame;
import javax.swing.JRootPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicInternalFrameUI;

/**
 *
 * @author Yusuf
 */
public class DashBoardFrame extends JInternalFrame {

    //private final JDesktopPane desktopPane;
    private GlobalSearchTask globalSearchTask;
    private final List<PnrPanel> pnrTabs = new ArrayList<>();

    public DashBoardFrame() {      
        initComponents();
        remove_title_bar();
    }
    
    private void showPnrPanel() {
        int index = tblUninvoicedPnr.getSelectedRow();
        if (index != -1) {
            Pnr pnr = globalSearchTask.getPnrs().get(index);

            PnrPanel panel = getPanel(pnr.getGdsPnr());
            if (panel == null) {
                PnrPanel p = new PnrPanel(pnr.getId(),this);
                mainTabPane.addTab(pnr.getGdsPnr(), p);
                pnrTabs.add(p);
                mainTabPane.setSelectedComponent(p);
            } else {
                mainTabPane.setSelectedComponent(panel);
            }
        }
    }
   
    private void deletePnr(){
    int index = tblUninvoicedPnr.getSelectedRow();
        if (index != -1) {
            Pnr pnr = globalSearchTask.getPnrs().get(index);
            DeletePnrTask task = new DeletePnrTask(pnr, pnrBusyLabel, tblUninvoicedPnr);
            task.execute();            
        }
    }

    private PnrPanel getPanel(String pnr) {
        for (int i = 0; i < pnrTabs.size(); i++) {
            String title = mainTabPane.getTitleAt(i + 1);//0 is already dashboard
            if (title.equals(pnr)) {
                return pnrTabs.get(i);
            }
        }

        return null;
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

        mainTabPane = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        pnlTask = new javax.swing.JPanel();
        tabPnr = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblUninvoicedPnr = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        btnRefreshPnr = new javax.swing.JButton();
        btnPnrDetails = new javax.swing.JButton();
        btnDeletePnr = new javax.swing.JButton();
        pnrBusyLabel = new org.jdesktop.swingx.JXBusyLabel();
        pnrSearch = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        txtPnr = new javax.swing.JTextField();
        txtInvRef = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        pnlInvoice = new javax.swing.JPanel();
        jTabbedPane3 = new javax.swing.JTabbedPane();
        jScrollPane9 = new javax.swing.JScrollPane();
        jXTable9 = new org.jdesktop.swingx.JXTable();
        jScrollPane10 = new javax.swing.JScrollPane();
        jXTable10 = new org.jdesktop.swingx.JXTable();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        pnlFlight = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();

        setBorder(null);
        setPreferredSize(new java.awt.Dimension(861, 400));

        mainTabPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                System.out.println("Tab=" + mainTabPane.getSelectedIndex()+" "+e);
            }
        });

        jPanel1.setLayout(new java.awt.GridBagLayout());

        pnlTask.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        pnlTask.setLayout(new java.awt.GridBagLayout());

        tabPnr.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        tabPnr.setPreferredSize(new java.awt.Dimension(457, 250));

        jScrollPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        tblUninvoicedPnr.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "PNR", "Passenger Name", "Tkt.AgentSine", "B.OfficeID", "T.OfficeID", "Airline"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblUninvoicedPnr.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblUninvoicedPnr);
        if (tblUninvoicedPnr.getColumnModel().getColumnCount() > 0) {
            tblUninvoicedPnr.getColumnModel().getColumn(1).setMinWidth(120);
            tblUninvoicedPnr.getColumnModel().getColumn(5).setPreferredWidth(40);
        }

        tabPnr.addTab("UnInvoicedPNR", jScrollPane1);

        jScrollPane2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        tabPnr.addTab("PNRToday", jScrollPane2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlTask.add(tabPnr, gridBagConstraints);

        btnRefreshPnr.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/refresh24.png"))); // NOI18N
        btnRefreshPnr.setBorderPainted(false);
        btnRefreshPnr.setMaximumSize(new java.awt.Dimension(45, 30));
        btnRefreshPnr.setMinimumSize(new java.awt.Dimension(45, 30));
        btnRefreshPnr.setPreferredSize(new java.awt.Dimension(45, 30));
        btnRefreshPnr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshPnrActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        pnlTask.add(btnRefreshPnr, gridBagConstraints);

        btnPnrDetails.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/details.png"))); // NOI18N
        btnPnrDetails.setBorderPainted(false);
        btnPnrDetails.setMaximumSize(new java.awt.Dimension(45, 30));
        btnPnrDetails.setMinimumSize(new java.awt.Dimension(45, 30));
        btnPnrDetails.setPreferredSize(new java.awt.Dimension(45, 30));
        btnPnrDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPnrDetailsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        pnlTask.add(btnPnrDetails, gridBagConstraints);

        btnDeletePnr.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/delete.png"))); // NOI18N
        btnDeletePnr.setBorderPainted(false);
        btnDeletePnr.setMaximumSize(new java.awt.Dimension(45, 30));
        btnDeletePnr.setMinimumSize(new java.awt.Dimension(45, 30));
        btnDeletePnr.setPreferredSize(new java.awt.Dimension(45, 30));
        btnDeletePnr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeletePnrActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        pnlTask.add(btnDeletePnr, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        pnlTask.add(pnrBusyLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel1.add(pnlTask, gridBagConstraints);

        pnrSearch.setBorder(javax.swing.BorderFactory.createTitledBorder("Search"));
        pnrSearch.setPreferredSize(new java.awt.Dimension(457, 250));

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel6.setText("Gds PNR");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(jLabel6, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(txtPnr, gridBagConstraints);

        txtInvRef.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtInvRefActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(txtInvRef, gridBagConstraints);

        jLabel5.setText("Inv Refference");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(jLabel5, gridBagConstraints);

        jLabel4.setText("Pax Name");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(jLabel4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(txtName, gridBagConstraints);

        btnSearch.setText("Search");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(btnSearch, gridBagConstraints);

        javax.swing.GroupLayout pnrSearchLayout = new javax.swing.GroupLayout(pnrSearch);
        pnrSearch.setLayout(pnrSearchLayout);
        pnrSearchLayout.setHorizontalGroup(
            pnrSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnrSearchLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(205, Short.MAX_VALUE))
        );
        pnrSearchLayout.setVerticalGroup(
            pnrSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnrSearchLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel1.add(pnrSearch, gridBagConstraints);

        pnlInvoice.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        pnlInvoice.setPreferredSize(new java.awt.Dimension(366, 250));
        pnlInvoice.setLayout(new java.awt.GridBagLayout());

        jTabbedPane3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        jScrollPane9.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        jXTable9.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Type", "Ref", "Amount", "Issue By"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jXTable9.getTableHeader().setReorderingAllowed(false);
        jScrollPane9.setViewportView(jXTable9);

        jTabbedPane3.addTab("Invoice Today", jScrollPane9);

        jScrollPane10.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        jXTable10.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane10.setViewportView(jXTable10);

        jTabbedPane3.addTab("Refund Today", jScrollPane10);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlInvoice.add(jTabbedPane3, gridBagConstraints);

        jButton10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/refresh24.png"))); // NOI18N
        jButton10.setBorderPainted(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlInvoice.add(jButton10, gridBagConstraints);

        jButton11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/details.png"))); // NOI18N
        jButton11.setBorderPainted(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlInvoice.add(jButton11, gridBagConstraints);

        jButton12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/delete.png"))); // NOI18N
        jButton12.setBorderPainted(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlInvoice.add(jButton12, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel1.add(pnlInvoice, gridBagConstraints);

        pnlFlight.setBorder(javax.swing.BorderFactory.createTitledBorder("Flight Today"));
        pnlFlight.setPreferredSize(new java.awt.Dimension(366, 250));

        javax.swing.GroupLayout pnlFlightLayout = new javax.swing.GroupLayout(pnlFlight);
        pnlFlight.setLayout(pnlFlightLayout);
        pnlFlightLayout.setHorizontalGroup(
            pnlFlightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 473, Short.MAX_VALUE)
        );
        pnlFlightLayout.setVerticalGroup(
            pnlFlightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 95, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel1.add(pnlFlight, gridBagConstraints);

        mainTabPane.addTab("Dashboard", jPanel1);

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

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanel3.add(jSeparator2, gridBagConstraints);

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
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(mainTabPane, javax.swing.GroupLayout.DEFAULT_SIZE, 857, Short.MAX_VALUE)
                .addGap(2, 2, 2))
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addGap(2, 2, 2))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addComponent(mainTabPane, javax.swing.GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE)
                .addGap(1, 1, 1)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnRefreshPnrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshPnrActionPerformed
        globalSearchTask = new GlobalSearchTask("UNINVOICED_PNR",pnrBusyLabel, tblUninvoicedPnr);
        globalSearchTask.execute();
    }//GEN-LAST:event_btnRefreshPnrActionPerformed

    private void btnPnrDetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPnrDetailsActionPerformed
        if (tabPnr.getSelectedIndex() == 0) {
            showPnrPanel();
        }
    }//GEN-LAST:event_btnPnrDetailsActionPerformed

    private void btnDeletePnrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeletePnrActionPerformed
        deletePnr();
    }//GEN-LAST:event_btnDeletePnrActionPerformed

    private void txtInvRefActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtInvRefActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtInvRefActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        String name = txtName.getText();
        String pnr = txtPnr.getText();
        String invRef = txtInvRef.getText();
        globalSearchTask = new GlobalSearchTask("QUERY_SEARCH",pnrBusyLabel, tblUninvoicedPnr,pnr,invRef,name);
        globalSearchTask.execute();
    }//GEN-LAST:event_btnSearchActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDeletePnr;
    private javax.swing.JButton btnPnrDetails;
    private javax.swing.JButton btnRefreshPnr;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JTable jTable2;
    private org.jdesktop.swingx.JXTable jXTable10;
    private org.jdesktop.swingx.JXTable jXTable9;
    private javax.swing.JTabbedPane mainTabPane;
    private javax.swing.JPanel pnlFlight;
    private javax.swing.JPanel pnlInvoice;
    private javax.swing.JPanel pnlTask;
    private org.jdesktop.swingx.JXBusyLabel pnrBusyLabel;
    private javax.swing.JPanel pnrSearch;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JTabbedPane tabPnr;
    private javax.swing.JTable tblUninvoicedPnr;
    private javax.swing.JTextField txtInvRef;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtPnr;
    // End of variables declaration//GEN-END:variables

    private void remove_title_bar() {
        putClientProperty("JInternalFrame.isPalette", Boolean.TRUE);
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        ((BasicInternalFrameUI) this.getUI()).setNorthPane(null);
        this.setBorder(null);
    }
    public void removeTabRefference(String pnr) {        
        PnrPanel panel = getPanel(pnr);
        pnrTabs.remove(panel);
        mainTabPane.remove(panel);        
    }    
}