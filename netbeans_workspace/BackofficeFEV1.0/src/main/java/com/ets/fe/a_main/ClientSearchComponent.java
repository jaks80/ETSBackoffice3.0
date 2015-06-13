package com.ets.fe.a_main;

import com.ets.fe.client.task.CustomerSearchTask;
import com.ets.fe.client.task.CustomerTask;
import com.ets.fe.client.task.AgentSearchTask;
import com.ets.fe.client.task.ContactableSearchTask;
import com.ets.fe.client.collection.Agents;
import com.ets.fe.client.collection.Customers;
import com.ets.fe.client.gui.*;
import com.ets.fe.client.model.*;
import com.ets.fe.util.Enums;
import com.ets.fe.util.PnrUtil;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 *
 * @author Yusuf
 */
public class ClientSearchComponent extends JPanel implements PropertyChangeListener {

    private boolean saveNeeded = false;

    private Agent agent;
    private Customer customer;
    private ContactableSearchTask task;
    private List<Agent> agentlist;
    private List<Customer> customerlist;
    private String clientName;
    private String officeId;
    private String taskType;

    public ClientSearchComponent() {
        initComponents();
    }

    public void search() {

        String keyword = txtContactableSearch.getText();
        if (keyword != null && !keyword.isEmpty()) {
            busyLabel.setBusy(true);
            if (rdoAgent.isSelected()) {
                task = new AgentSearchTask(keyword);
                task.addPropertyChangeListener(this);
                task.execute();
            } else {
                task = new CustomerSearchTask(keyword, null, null);
                task.addPropertyChangeListener(this);
                task.execute();
            }
        }
    }

    public void populateComponent() {
        if (rdoAgent.isSelected()) {
            if (agentlist.size() == 1) {
                cmbSearchResult.setEnabled(false);
                this.customer = null;
                this.agent = this.agentlist.get(0);
                setTxtAgentDetails(this.getAgent());
            } else {
                cmbSearchResult.setEnabled(true);
                List<String> list = new ArrayList<>();
                for (Agent a : agentlist) {
                    list.add(a.calculateFullName() + "-" + a.getId());
                }
                this.agent = null;
                setTxtAgentDetails(this.getAgent());
                setCmbSearchResult(list);
            }
        } else {
            if (customerlist.size() == 1) {
                this.agent = null;
                this.customer = this.customerlist.get(0);
                setTxtCustomerDetails(this.getCustomer());

                //If this is not the customer, allow to add one
                cmbSearchResult.setEnabled(true);
                List<String> list = new ArrayList<>();
                setCmbSearchResult(list);
            } else {
                cmbSearchResult.setEnabled(true);
                List<String> list = new ArrayList<>();
                for (Customer a : customerlist) {
                    list.add(PnrUtil.calculatePartialName(a.calculateFullName(),20) + "-" + a.getPostCode());
                }
                this.customer = null;
                setTxtCustomerDetails(this.getCustomer());
                setCmbSearchResult(list);
            }
        }
        setSaveNeeded(true);
    }

    private void addNewCustomer() {
        Window w = SwingUtilities.getWindowAncestor(this);
        Frame owner = w instanceof Frame ? (Frame) w : null;

        CustomerDlg customerDlg = new CustomerDlg(owner);
        customerDlg.setTitle("New Customer");
        Customer newCustomer = new Customer();
        String tempCustomer = txtContactableSearch.getText();
        if (tempCustomer.length() > 0 && tempCustomer.contains("/")) {
            String[] data = tempCustomer.split("/");
            newCustomer.setSurName(data[0].trim());
            newCustomer.setForeName(data[1].trim());
        } else if (tempCustomer.length() > 0 && !tempCustomer.contains("/")) {
            newCustomer.setSurName(tempCustomer);
        }

        if (customerDlg.showDialog(newCustomer)) {
            taskType = "NEWCUSTOMER";
            CustomerTask task = new CustomerTask(newCustomer);
            task.addPropertyChangeListener(this);
            task.execute();
        }
    }

    public void setAllocatetClient(final Contactable cont, String clientName, String officeId, final boolean editable) {

        this.clientName = clientName;
        this.officeId = officeId;

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (cont instanceof Agent) {
                    agent = (Agent) cont;
                    rdoAgent.setSelected(true);
                    if (agent != null) {
                        setTxtAgentDetails(agent);
                    }
                } else {
                    customer = (Customer) cont;
                    rdoCustomer.setSelected(true);
                    if (customer != null) {
                        setTxtCustomerDetails(customer);
                    }
                }
                txtContactableSearch.setText("");
                controllComponent(editable);
            }
        });

    }

    private void controllComponent(boolean editable) {
        if (!editable) {
            rdoAgent.setEnabled(false);
            rdoCustomer.setEnabled(false);
            txtContactableSearch.setEditable(false);
            cmbSearchResult.setEnabled(false);
            btnContactableSearch.setEnabled(false);
        } else {
            rdoAgent.setEnabled(true);
            rdoCustomer.setEnabled(true);
            txtContactableSearch.setEditable(true);
            cmbSearchResult.setEnabled(true);
            btnContactableSearch.setEnabled(true);
        }
    }

    public void suggestAllocatedClient(final Enums.ClientType clientType, String clientName, String officeId) {
        this.clientName = clientName;
        this.officeId = officeId;

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                if (clientType.equals(Enums.ClientType.CUSTOMER)) {
                    rdoCustomer.doClick();
                } else {
                    rdoAgent.doClick();
                }
            }
        });
    }

    private ActionListener cmbSearchResultListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (cmbSearchResult.getItemCount() > 0) {
                String selectedItem = (String) cmbSearchResult.getSelectedItem();
                int index = cmbSearchResult.getSelectedIndex();
                if ("Add New".equals(selectedItem)) {
                    addNewCustomer();
                } else {
                    if (rdoAgent.isSelected()) {
                        Agent a = agentlist.get(index);
                        agent = a;
                        customer = null;
                        setTxtAgentDetails(a);
                    } else if (rdoCustomer.isSelected()) {
                        Customer c = customerlist.get(index);
                        agent = null;
                        customer = c;
                        setTxtCustomerDetails(c);
                    }
                }
            }
        }
    };

    private ActionListener radioAgent = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            txtContactableSearch.setText(officeId);
            btnContactableSearch.setEnabled(true);
            if (cmbSearchResult.getItemCount() > 0) {
                cmbSearchResult.removeAllItems();
            }
            txtContactableDetails.setText("");
        }
    };
    private ActionListener radioCustomer = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if(clientName!=null){
             clientName = clientName.replaceAll("[^a-zA-Z0-9/]", " ");
            }
            txtContactableSearch.setText(clientName);

            if (cmbSearchResult.getItemCount() > 0) {
                cmbSearchResult.removeAllItems();
            }
            txtContactableDetails.setText("");

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
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        rdoCustomer = new javax.swing.JRadioButton();
        rdoAgent = new javax.swing.JRadioButton();
        txtContactableSearch = new javax.swing.JTextField();
        btnContactableSearch = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtContactableDetails = new javax.swing.JTextArea();
        cmbSearchResult = new javax.swing.JComboBox();
        cmbSearchResult.addActionListener(cmbSearchResultListener);  AutoCompleteDecorator.decorate(cmbSearchResult);
        busyLabel = new org.jdesktop.swingx.JXBusyLabel();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Invoice For", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(180, 235));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        buttonGroup1.add(rdoCustomer);
        rdoCustomer.setText("Customer");
        rdoCustomer.setToolTipText("Click to Invoice a Customer");
        rdoCustomer.addActionListener(radioCustomer);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        jPanel1.add(rdoCustomer, gridBagConstraints);

        buttonGroup1.add(rdoAgent);
        rdoAgent.setText("Agent");
        rdoAgent.setToolTipText("Click to Invoice an Agent");
        rdoAgent.addActionListener(radioAgent);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        jPanel1.add(rdoAgent, gridBagConstraints);

        txtContactableSearch.setToolTipText("<html>\n<b>Search Customer:</b> Surname/ Forename -> Click on Search or Hit Enter Key<br>\n<b>Search Agent:</b> OFFICEID or Name -> Click on Search or Hit Enter Key\n</html>");
        txtContactableSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtContactableSearchKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        jPanel1.add(txtContactableSearch, gridBagConstraints);

        btnContactableSearch.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnContactableSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/search18.png"))); // NOI18N
        btnContactableSearch.setText("Search");
        btnContactableSearch.setPreferredSize(new java.awt.Dimension(110, 25));
        btnContactableSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnContactableSearchActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        jPanel1.add(btnContactableSearch, gridBagConstraints);

        txtContactableDetails.setEditable(false);
        txtContactableDetails.setColumns(16);
        txtContactableDetails.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        txtContactableDetails.setLineWrap(true);
        txtContactableDetails.setRows(5);
        txtContactableDetails.setToolTipText("Invoice For");
        jScrollPane1.setViewportView(txtContactableDetails);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        jPanel1.add(jScrollPane1, gridBagConstraints);

        cmbSearchResult.setEditable(true);
        cmbSearchResult.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        cmbSearchResult.setToolTipText("Select customer from List or Add new.");
        cmbSearchResult.setEnabled(false);
        cmbSearchResult.setPreferredSize(new java.awt.Dimension(56, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        jPanel1.add(cmbSearchResult, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel1.add(busyLabel, gridBagConstraints);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnContactableSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnContactableSearchActionPerformed
        search();
    }//GEN-LAST:event_btnContactableSearchActionPerformed

    private void txtContactableSearchKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtContactableSearchKeyPressed
        int key = evt.getKeyCode();
        if (key == KeyEvent.VK_ENTER) {
            search();
        }
    }//GEN-LAST:event_txtContactableSearchKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnContactableSearch;
    private org.jdesktop.swingx.JXBusyLabel busyLabel;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox cmbSearchResult;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JRadioButton rdoAgent;
    private javax.swing.JRadioButton rdoCustomer;
    private javax.swing.JTextArea txtContactableDetails;
    private javax.swing.JTextField txtContactableSearch;
    // End of variables declaration//GEN-END:variables

    private void setCmbSearchResult(List<String> list) {
        if(rdoCustomer.isSelected()){
         list.add("Add New");
        }
        DefaultComboBoxModel cmbSearchResultModel = new DefaultComboBoxModel(list.toArray());
        cmbSearchResult.setModel(cmbSearchResultModel);
    }

    private void setTxtAgentDetails(Agent agent) {
        if (agent != null) {
            txtContactableDetails.setText(agent.calculateFullName() + "\n");
            txtContactableDetails.append(agent.getFullAddressCRSeperated());
        } else {
            txtContactableDetails.setText("");
        }
    }

    private void setTxtCustomerDetails(Customer customer) {
        if (customer != null) {
            txtContactableDetails.setText(customer.calculateFullName() + "\n");
            txtContactableDetails.append(customer.getFullAddressCRSeperated());
        } else {
            txtContactableDetails.setText("");
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress".equals(evt.getPropertyName())) {
            int progress = (Integer) evt.getNewValue();
            if (progress == 100) {
                try {
                    if ("NEWCUSTOMER".equals(taskType)) {
                        search();
                        taskType = "";
                    } else {

                        if (task instanceof AgentSearchTask) {
                            Agents agts = (Agents) task.get();
                            agentlist = agts.getList();
                            customerlist = new ArrayList<>();
                        }

                        if (task instanceof CustomerSearchTask) {
                            Customers customers = (Customers) task.get();
                            customerlist = customers.getList();
                            agentlist = new ArrayList<>();
                        }
                    }
                    busyLabel.setBusy(false);
                    populateComponent();
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(ClientSearchComponent.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public Agent getAgent() {
        return agent;
    }

    public Customer getCustomer() {
        return customer;
    }

    public boolean isSaveNeeded() {
        return saveNeeded;
    }

    public void setSaveNeeded(boolean saveNeeded) {
        this.saveNeeded = saveNeeded;
    }
}
