package com.ets.fe.a_main;

import com.ets.fe.Application;
import com.ets.fe.client.collection.Agents;
import com.ets.fe.client.collection.Customers;
import com.ets.fe.client.gui.*;
import com.ets.fe.client.model.*;
import com.ets.fe.pnr.model.Pnr;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
public class ClientComponent extends JPanel implements PropertyChangeListener {

    private Pnr pnr;
    private ContactableSearchTask task;
    private List<Agent> agentlist;
    private List<Customer> customerlist;

    public ClientComponent() {
        initComponents();
    }

    public void search() {

        String keyword = txtContactableSearch.getText();
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

    public void populateComponent() {
        if (rdoAgent.isSelected()) {
            if (agentlist.size() == 1) {
                cmbSearchResult.setEnabled(false);
                pnr.setCustomer(null);
                pnr.setAgent(this.agentlist.get(0));                
                setTxtAgentDetails(pnr.getAgent());
            } else {
                cmbSearchResult.setEnabled(true);
                List<String> list = new ArrayList<>();
                for (Agent a : agentlist) {
                    list.add(a.getName() + "-" + a.getId());
                }
                setCmbSearchResult(list);
            }
        } else {
            if (customerlist.size() == 1) {
                cmbSearchResult.setEnabled(false);
                pnr.setAgent(null);
                pnr.setCustomer(this.customerlist.get(0));
                setTxtCustomerDetails(pnr.getCustomer());

            } else {
                cmbSearchResult.setEnabled(true);
                List<String> list = new ArrayList<>();
                for (Customer a : customerlist) {
                    list.add(a.getFullCustomerName() + "-" + a.getPostCode() + "-" + a.getId());
                }
                setCmbSearchResult(list);
            }
        }
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

            if (customerDlg.showCustomerDialog(newCustomer)) {
                CustomerTask task = new CustomerTask(newCustomer);
                task.execute();
            }
        }
    }

    public void setPnrAllocatedTo(Pnr pnr) {
        this.pnr = pnr;
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                if (pnr.getAgent() != null) {
                    rdoAgent.setSelected(true);                    
                    setTxtAgentDetails(pnr.getAgent());
                } else {
                    rdoCustomer.setSelected(true);
                    setTxtCustomerDetails(pnr.getCustomer());
                }
                txtContactableSearch.setText("");
            }
        });
    }

    public void suggestPnrAllocatedTo(Pnr pnr) {
        this.pnr = pnr;
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                MainAgent mainAgent = Application.getMainAgent();

                if (mainAgent.getOfficeID().contains(pnr.getBookingAgtOid())) {
                    rdoCustomer.doClick();
                } else {
                    rdoAgent.doClick();
                }
            }
        });
    }

    private ActionListener cmbSearchResultListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            if (cmbSearchResult.getItemCount() > 0) {
                String selectedItem = (String) cmbSearchResult.getSelectedItem();
                int index = cmbSearchResult.getSelectedIndex();
                if ("Add New".equals(selectedItem)) {
                    addNewCustomer();
                } else {
                    if (rdoAgent.isSelected()) {
                        Agent a = agentlist.get(index);
                        pnr.setAgent(a);
                        pnr.setCustomer(null);
                        setTxtAgentDetails(a);
                    } else if (rdoCustomer.isSelected()) {
                        Customer c = customerlist.get(index);
                        pnr.setAgent(null);
                        pnr.setCustomer(c);
                        setTxtCustomerDetails(c);
                    }
                }
            }
        }
    };

    private ActionListener radioAgent = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            txtContactableSearch.setText(pnr.getBookingAgtOid());
            btnContactableSearch.setEnabled(true);
            if (cmbSearchResult.getItemCount() > 0) {
                cmbSearchResult.removeAllItems();
            }
            txtContactableDetails.setText("");
        }
    };
    private ActionListener radioCustomer = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            txtContactableSearch.setText(pnr.calculateLeadPaxName());

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
        rdoCustomer.addActionListener(radioCustomer);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        jPanel1.add(rdoCustomer, gridBagConstraints);

        buttonGroup1.add(rdoAgent);
        rdoAgent.setText("Agent");
        rdoAgent.addActionListener(radioAgent);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        jPanel1.add(rdoAgent, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        jPanel1.add(txtContactableSearch, gridBagConstraints);

        btnContactableSearch.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnContactableSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/search24.png"))); // NOI18N
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
        txtContactableDetails.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        txtContactableDetails.setLineWrap(true);
        txtContactableDetails.setRows(5);
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

        cmbSearchResult.setEnabled(false);
        cmbSearchResult.setPreferredSize(new java.awt.Dimension(56, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
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
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnContactableSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnContactableSearchActionPerformed
        search();
    }//GEN-LAST:event_btnContactableSearchActionPerformed


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
        list.add("Add New");
        DefaultComboBoxModel cmbSearchResultModel = new DefaultComboBoxModel(list.toArray());
        cmbSearchResult.setModel(cmbSearchResultModel);
    }

    private void setTxtAgentDetails(Agent agent) {
        txtContactableDetails.setText(agent.getName());
        txtContactableDetails.append(agent.getFullAddressCRSeperated());
    }

    private void setTxtCustomerDetails(Customer customer) {
        txtContactableDetails.setText(customer.getFullCustomerName());
        txtContactableDetails.append(customer.getFullAddressCRSeperated());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress".equals(evt.getPropertyName())) {
            int progress = (Integer) evt.getNewValue();
            if (progress == 100) {
                try {
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
                    busyLabel.setBusy(false);
                    populateComponent();
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(ClientComponent.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
