package com.ets.fe.tools.gui;

import com.ets.fe.acdoc.gui.comp.ClientSearchComp;
import com.ets.fe.client.collection.Agents;
import com.ets.fe.client.collection.Customers;
import com.ets.fe.client.model.Agent;
import com.ets.fe.client.model.Customer;
import com.ets.fe.client.task.AgentSearchTask;
import com.ets.fe.client.task.CustomerSearchTask;
import com.ets.fe.util.Enums;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
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
public class EmailFrame extends javax.swing.JInternalFrame implements PropertyChangeListener {

    private AgentSearchTask agentTask;
    private CustomerSearchTask customerTask;

    private List<Customer> customerList = new ArrayList<>();
    private List<Agent> agentList = new ArrayList<>();

    private Set<String> emailids = new LinkedHashSet<>();

    public EmailFrame() {
        initComponents();
        //tblClient.getModel().addTableModelListener(tableModelListener);
    }

    private void displayAgent() {
        DefaultTableModel tableModel = (DefaultTableModel) tblClient.getModel();
        tableModel.getDataVector().removeAllElements();
        int i = 0;
        for (Agent agent : agentList) {
            if (agent.getEmail() != null && !agent.getEmail().isEmpty()) {
                tableModel.insertRow(i, new Object[]{agent.getName(), agent.getEmail(), false});
                i++;
            }
        }
    }

    private void displayEmailIds() {
        txtRecepient.setText("");
        for (String s : emailids) {
            txtRecepient.append(s);
            txtRecepient.append(",");
        }
    }

    private void populateTable() {

        DefaultTableModel tableModel = (DefaultTableModel) tblClient.getModel();
        tableModel.getDataVector().removeAllElements();
        int i = 0;
        for (Agent agent : agentList) {
            if (agent.getEmail() != null && !agent.getEmail().isEmpty()) {
                tableModel.insertRow(i, new Object[]{agent.getName(), agent.getEmail(), false});
                i++;
            }
        }
    }

    private void selectAll() {
        if (btnSelect.isSelected()) {
            for (int i = 0; i < tblClient.getRowCount(); i++) {
                tblClient.getModel().setValueAt(true, i, 2);
                String email = (String) tblClient.getValueAt(i, 1);
                if (!email.isEmpty() && email.contains("@")) {
                    emailids.add(email);
                }
            }
        } else {
            for (int i = 0; i < tblClient.getRowCount(); i++) {
                tblClient.getModel().setValueAt(false, i, 2);
                String email = (String) tblClient.getValueAt(i, 1);
                if (!email.isEmpty()) {
                    emailids.remove(email);
                }
            }
        }
        displayEmailIds();
    }

     TableModelListener tableModelListener = new TableModelListener() {

        @Override
        public void tableChanged(TableModelEvent e) {

            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getLastRow();
                int column = e.getColumn();

                if (column == 2) {
                    if ((boolean) tblClient.getValueAt(row, 2)) {
                        String email = (String) tblClient.getValueAt(row, 1);
                        if (!email.isEmpty() && email.contains("@")) {
                            emailids.add(email);
                        }
                    } else {
                        String email = (String) tblClient.getValueAt(row, 1);
                        if (!email.isEmpty()) {
                            emailids.remove(email);
                        }
                    }
                    displayEmailIds();
                }
            }
        }
    };

    private void displayCustomer() {
        DefaultTableModel tableModel = (DefaultTableModel) tblClient.getModel();
        tableModel.getDataVector().removeAllElements();
        int i = 0;
        for (Customer cust : customerList) {
            if (cust.getEmail() != null && !cust.getEmail().isEmpty()) {
                tableModel.insertRow(i, new Object[]{cust.calculateFullName(), cust.getEmail(), false});
                i++;
            }
        }
    }

    public void agentTask() {
        busyLabel.setBusy(true);
        agentTask = new AgentSearchTask(busyLabel, Enums.AgentType.ALL);
        agentTask.addPropertyChangeListener(this);
        agentTask.execute();
    }

    private void customerTask() {
        busyLabel.setBusy(true);
        customerTask = new CustomerSearchTask(null, null, null);
        customerTask.addPropertyChangeListener(this);
        customerTask.execute();
    }

    private ActionListener radioAgentListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            agentTask();
        }
    };

    private ActionListener radioCustomerListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            customerTask();
        }
    };

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane5 = new javax.swing.JScrollPane();
        jXTable1 = new org.jdesktop.swingx.JXTable();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        rdoAgent = new javax.swing.JRadioButton();
        rdoCustomer = new javax.swing.JRadioButton();
        btnSelect = new javax.swing.JToggleButton();
        busyLabel = new org.jdesktop.swingx.JXBusyLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tblClient = new org.jdesktop.swingx.JXTable();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        mainPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtRecepient = new javax.swing.JTextArea();
        txtSubject = new javax.swing.JTextField();
        btnBrowse = new javax.swing.JButton();
        lblPath = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtMessage = new javax.swing.JTextPane();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        progressBar = new javax.swing.JProgressBar();
        lblStatus = new javax.swing.JLabel();

        jXTable1.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane5.setViewportView(jXTable1);

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Email Sender");

        jSplitPane1.setDividerLocation(400);
        jSplitPane1.setResizeWeight(1.0);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Client", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        buttonGroup1.add(rdoAgent);
        rdoAgent.setText("Agent");
        rdoAgent.addActionListener(radioAgentListener);

        buttonGroup1.add(rdoCustomer);
        rdoCustomer.setText("Customer");
        rdoCustomer.addActionListener(radioCustomerListener);

        btnSelect.setText("All");
        btnSelect.setToolTipText("Select/Unselect All");
        btnSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectActionPerformed(evt);
            }
        });

        tblClient.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Name", "Email", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblClient.getTableHeader().setReorderingAllowed(false);
        jScrollPane6.setViewportView(tblClient);
        if (tblClient.getColumnModel().getColumnCount() > 0) {
            tblClient.getColumnModel().getColumn(2).setMaxWidth(20);
        }

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(rdoAgent)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rdoCustomer)
                .addGap(43, 43, 43)
                .addComponent(busyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 154, Short.MAX_VALUE)
                .addComponent(btnSelect))
            .addComponent(jScrollPane6)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(rdoAgent)
                        .addComponent(rdoCustomer)
                        .addComponent(busyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnSelect, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 433, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSplitPane1.setRightComponent(jPanel1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jSplitPane1.setLeftComponent(jPanel2);

        jScrollPane2.setViewportView(jTextPane1);

        jSplitPane1.setLeftComponent(jScrollPane2);

        jLabel1.setText("Mail To");

        jLabel2.setText("Subject");

        txtRecepient.setColumns(20);
        txtRecepient.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtRecepient.setLineWrap(true);
        txtRecepient.setRows(5);
        jScrollPane1.setViewportView(txtRecepient);

        btnBrowse.setText("Attach File");

        lblPath.setText("No Files Attached");

        jLabel4.setText("Message");

        jScrollPane3.setViewportView(txtMessage);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/email18.png"))); // NOI18N
        jButton2.setText("Send");

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/clear18.png"))); // NOI18N
        jButton3.setText("Clear");

        progressBar.setMaximumSize(new java.awt.Dimension(145, 17));
        progressBar.setMinimumSize(new java.awt.Dimension(145, 17));
        progressBar.setPreferredSize(new java.awt.Dimension(145, 17));
        progressBar.setStringPainted(true);

        lblStatus.setText("Status");

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(btnBrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblPath, javax.swing.GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE)
                                .addGap(34, 34, 34))
                            .addComponent(jScrollPane1)
                            .addComponent(txtSubject)))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                                .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(jButton3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton2))
                            .addComponent(jScrollPane3))))
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(txtSubject, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBrowse)
                    .addComponent(lblPath))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2)
                    .addComponent(jButton3))
                .addGap(12, 12, 12)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblStatus))
                .addGap(7, 7, 7))
        );

        jSplitPane1.setLeftComponent(mainPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectActionPerformed
        selectAll();
    }//GEN-LAST:event_btnSelectActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrowse;
    private javax.swing.JToggleButton btnSelect;
    private org.jdesktop.swingx.JXBusyLabel busyLabel;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTextPane jTextPane1;
    private org.jdesktop.swingx.JXTable jXTable1;
    private javax.swing.JLabel lblPath;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JRadioButton rdoAgent;
    private javax.swing.JRadioButton rdoCustomer;
    private org.jdesktop.swingx.JXTable tblClient;
    private javax.swing.JTextPane txtMessage;
    private javax.swing.JTextArea txtRecepient;
    private javax.swing.JTextField txtSubject;
    // End of variables declaration//GEN-END:variables
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            if (progress == 100) {
                try {
                    if (rdoAgent.isSelected()) {
                        Agents agents = (Agents) agentTask.get();
                        agentList = agents.getList();
                        displayAgent();
                    } else if (rdoCustomer.isSelected()) {
                        Customers customers = (Customers) customerTask.get();
                        customerList = customers.getList();
                        displayCustomer();
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(ClientSearchComp.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    busyLabel.setBusy(false);
                }
            }
        }
    }
}
