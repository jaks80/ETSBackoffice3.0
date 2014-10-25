
/*
 * FrameOutstandingInvoice.java
 *
 * Created on 18-Oct-2010, 23:15:52
 */
package etsbackoffice.gui;

import etsbackoffice.ETSBackofficeApp;
import etsbackoffice.businesslogic.*;
import etsbackoffice.domain.*;
import etsbackoffice.report.BackofficeReporting;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 *
 * @author Yusuf
 */
public class FrameSOAcDocOutstanding extends javax.swing.JFrame{

    private ControllFrame cf = new ControllFrame();
    
    OAccountingDocBo oAcDocBo = (OAccountingDocBo) ETSBackofficeApp.getApplication().ctx.getBean("oAcDocBo");
    private OServiceBo oServiceBo = (OServiceBo) ETSBackofficeApp.getApplication().ctx.getBean("oServiceBo");
    private OAccountingDocument acDoc = new OAccountingDocument();
    private List<Agent> outstandingAgents = new ArrayList();
    private List<Customer> outstandingCustomers = new ArrayList();
    private List<OAccountingDocument> acDocs = new ArrayList();
    private List<OtherService> oServices = new ArrayList<>();
    private DefaultTableModel acDocModel;
    DefaultComboBoxModel cmbCustModel = new DefaultComboBoxModel();
    DefaultComboBoxModel cmbAgtModel = new DefaultComboBoxModel();
    private Date from = null;
    private Date to = null;
    private Agent agent;
    private Customer customer;
    /** Creates new form FrameOutstandingInvoice */
    public FrameSOAcDocOutstanding(java.awt.Frame parent) {
        initComponents();
        busyIcon.setVisible(false);    
        PopulateCmbService otherService = new PopulateCmbService();
         otherService.execute();
    }    
    
    private void populateCmbService(){
                   
            List cmbElement = new ArrayList();
            
            for(OtherService o: oServices){
              cmbElement.add(o.getServiceTitle());
            }
            

            DefaultComboBoxModel cmbServiceModel = new DefaultComboBoxModel(cmbElement.toArray());
            cmbServiceModel.insertElementAt("All", 0);
            cmbService.setModel(cmbServiceModel);
            cmbService.setSelectedIndex(0);                
    }
    
    private void searchAcDocByCriteria() {        
        int contType = 0;
        Long contactableId = null;        
        from = null;
        to = null;
        String[] data;

        if (cmbContactable.getSelectedIndex() > 0) {
            data = cmbContactable.getSelectedItem().toString().split("-");
            contactableId = Long.parseLong(data[2]);
        }

        from = dtFrom.getDate();
        to = dtTo.getDate();

        if (rdoAgent.isSelected()) {
           contType = 1;
        } else if (rdoCustomer.isSelected()) {
           contType = 2;
        } else if (rdoAll.isSelected()) {
            contType = 0;
        }      
        
        this.acDocs = oAcDocBo.sOOutstandingInvByCriteria(contType,contactableId,from, to);
        populateTblInvoice(this.acDocs);
    }

    private void populateTxtAgentDetails(Agent agent) {
        this.agent = agent;
        this.customer = null;
        txtContactableDetails.setText(agent.getName() + "\n");

        if (!agent.getAddLine1().isEmpty()) {
            txtContactableDetails.append(agent.getAddLine1() + "\n");
        }

        if (!agent.getAddLine2().isEmpty()) {
            txtContactableDetails.append(agent.getAddLine2() + "\n");
        }
        if (!agent.getProvince().isEmpty()) {
            txtContactableDetails.append(agent.getProvince() + "\n");
        }
        if (!agent.getPostCode().isEmpty()) {
            txtContactableDetails.append(agent.getPostCode() + "\n");
        }
        if (!agent.getTelNo().isEmpty()) {
            txtContactableDetails.append("TelNo: " + agent.getTelNo() + "\n");
        }
    }

    private void populateTxtCustomerDetails(Customer customer) {
        this.customer = customer;
        this.agent = null;
        txtContactableDetails.setText(customer.getSurName() + " " + customer.getForeName() + "\n");

        if (!customer.getContactPerson().isEmpty()) {
        txtContactableDetails.append("C/P: "+customer.getContactPerson() + "\n");
        }
        if (!customer.getAddLine1().isEmpty()) {
            txtContactableDetails.append(customer.getAddLine1() + "\n");
        }
        if (!customer.getAddLine2().isEmpty()) {
            txtContactableDetails.append(customer.getAddLine2() + "\n");
        }
        if (!customer.getProvince().isEmpty()) {
            txtContactableDetails.append(customer.getProvince() + "\n");
        }
        if (!customer.getPostCode().isEmpty()) {
            txtContactableDetails.append(customer.getPostCode() + "\n");
        }
        if (!customer.getTelNo().isEmpty()) {
            txtContactableDetails.append("TelNo: " + customer.getTelNo() + "\n");
        }
    }

    private void populateTblInvoice(List<OAccountingDocument> acDocs) {

        int row = 0;
        acDocModel = (DefaultTableModel) tblInvoice.getModel();
        acDocModel.getDataVector().removeAllElements();
        JScrollPane.repaint();
        BigDecimal totalInvAmount = new BigDecimal("0.00");        
        BigDecimal totalReceived = new BigDecimal("0.00");
        BigDecimal finalBalance = new BigDecimal("0.00");
        
        ///int totalDoc = 0;
        List<OAccountingDocument> acDocsToBeFiltered = new ArrayList<>();
        
        for (OAccountingDocument a : acDocs) {

            String title = (String) cmbService.getSelectedItem();

            if (!"All".equals(title)) {

                boolean exist = false;
                for (OAccountingDocumentLine l : a.getoAccountingDocumentLines()) {
                    if (l.getServiceTitle().equals(title)) {
                        exist = true;
                        break;
                    }
                }

                if (!exist) {       
                    acDocsToBeFiltered.add(a);
                    continue;
                }
                //if(cmbService.getSelectedItem() != a.get )
            }

            String clientName = "";
            if(a.getAgent()!=null){
            clientName = a.getAgent().getName();
            }else if(a.getCustomer()!=null){
            clientName = a.getCustomer().getFullCustomerName();
            }
            this.oAcDocBo.setClient(clientName);
            BigDecimal balance = a.getOutstandingAmount();
            
            acDocModel.insertRow(row, new Object[]{a.getAcDocRef(),
                        a.getIssueDate(), a.getTerms(),
                        clientName,
                        a.getTotalDocumentedAmount(),a.getTotalTransactionAmount(),
                        balance,a.getAcDocIssuedBy().getSurName()});
            row++;
            totalInvAmount = totalInvAmount.add(a.getTotalDocumentedAmount());
            totalReceived = totalReceived.add(a.getTotalTransactionAmount());
        }
        this.acDocs.removeAll(acDocsToBeFiltered);
         lblTotalDoc.setText(String.valueOf(this.acDocs.size()));   
         this.oAcDocBo.setNoOfInvoice(this.acDocs.size());
         lblTotalInvAmount.setText(totalInvAmount.toString());
         this.oAcDocBo.setTotalInvAmount(totalInvAmount);
         lblTotalPaid.setText(totalReceived.toString());  
         this.oAcDocBo.setTotalReceived(totalReceived);
         lblTotalOutstanding.setText(totalReceived.subtract(totalInvAmount).toString());
         this.oAcDocBo.setTotalOutstanding(totalReceived.subtract(totalInvAmount));
    }

    private ActionListener radioAgentListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            busyIcon.setVisible(true);
            busyIcon.setBusy(true);
            List cmbElement = new ArrayList();
            outstandingAgents = oAcDocBo.outstandingAgents();
            for (Agent agent : outstandingAgents) {
                cmbElement.add(agent.getName() + "-" + agent.getPostCode()
                        + "-" + agent.getContactableId());
            }

            Collections.sort(cmbElement);
            DefaultComboBoxModel cmbContactableModel = new DefaultComboBoxModel(cmbElement.toArray());
            cmbContactableModel.insertElementAt("All", 0);
            cmbContactable.setModel(cmbContactableModel);
            cmbContactable.setSelectedIndex(0);
            busyIcon.setBusy(false);
            busyIcon.setVisible(false);
        }
    };

    private ActionListener radioCustomerListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            busyIcon.setVisible(true);
            busyIcon.setBusy(true);
            List cmbElement = new ArrayList();
            outstandingCustomers = oAcDocBo.outstandingCustomers();

            for (Customer customer : outstandingCustomers) {
                cmbElement.add(customer.getSurName() + "/"
                        + customer.getForeName() + "-" + customer.getPostCode()
                        + "-" + customer.getContactableId());
            }

            Collections.sort(cmbElement);
            DefaultComboBoxModel cmbContactableModel = new DefaultComboBoxModel(cmbElement.toArray());
            cmbContactableModel.insertElementAt("All", 0);
            cmbContactable.setModel(cmbContactableModel);
            cmbContactable.setSelectedIndex(0);
            busyIcon.setBusy(false);
            busyIcon.setVisible(false);
        }
    };

    private ActionListener radioAllListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            busyIcon.setVisible(true);
            busyIcon.setBusy(true);
            cmbContactable.setSelectedIndex(-1);
            dtFrom.setDate(null);
            dtTo.setDate(null);
            busyIcon.setBusy(false);
            busyIcon.setVisible(false);
        }
    };

    private ActionListener cmbContactableListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {            
            String[] data;
            long id = 0;
            if(cmbContactable.getSelectedIndex()>0){
            data = cmbContactable.getSelectedItem().toString().split("-");
            id = Long.parseLong(data[2]);
            }

            if (rdoAgent.isSelected()) {
                loop:
                for (Agent agent : outstandingAgents) {
                    if (agent.getContactableId() == id) {
                        populateTxtAgentDetails(agent);
                        break loop;
                    }
                }
            } else if (rdoCustomer.isSelected()) {
                loop:
                for (Customer customer : outstandingCustomers) {
                    if (customer.getContactableId() == id) {
                        populateTxtCustomerDetails(customer);
                        break loop;
                    }
                }
            }
        }
    };
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jToolBar1 = new javax.swing.JToolBar();
        jButton1 = new javax.swing.JButton();
        btnViewInvoice = new javax.swing.JButton();
        btnPrintReport = new javax.swing.JButton();
        btnEmailReport = new javax.swing.JButton();
        btnViewReport = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        splitPanelMaster = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtContactableDetails = new javax.swing.JTextArea();
        jPanel8 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        cmbContactable = new javax.swing.JComboBox();
        cmbContactable.addActionListener(cmbContactableListener);
        AutoCompleteDecorator.decorate(cmbContactable);
        dtFrom = new org.jdesktop.swingx.JXDatePicker();
        dtTo = new org.jdesktop.swingx.JXDatePicker();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        btnCriteriaSearch = new javax.swing.JButton();
        rdoAgent = new javax.swing.JRadioButton();
        rdoCustomer = new javax.swing.JRadioButton();
        rdoAll = new javax.swing.JRadioButton();
        busyIcon = new org.jdesktop.swingx.JXBusyLabel();
        jSeparator1 = new javax.swing.JSeparator();
        cmbService = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        pagePanel = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        lbl1 = new javax.swing.JLabel();
        lblTotalDoc = new javax.swing.JLabel();
        JScrollPane = new javax.swing.JScrollPane();
        tblInvoice = new org.jdesktop.swingx.JXTable();
        summeryPanel = new javax.swing.JPanel();
        lblBalance = new javax.swing.JLabel();
        lblTotalOutstanding = new javax.swing.JLabel();
        lblTotalPaid = new javax.swing.JLabel();
        lblTAmount = new javax.swing.JLabel();
        lblTotalAmount = new javax.swing.JLabel();
        lblTotalInvAmount = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        statusMessageLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getResourceMap(FrameSOAcDocOutstanding.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jToolBar1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        jButton1.setFont(resourceMap.getFont("jButton1.font")); // NOI18N
        jButton1.setIcon(resourceMap.getIcon("jButton1.icon")); // NOI18N
        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setFocusable(false);
        jButton1.setName("jButton1"); // NOI18N
        jToolBar1.add(jButton1);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getActionMap(FrameSOAcDocOutstanding.class, this);
        btnViewInvoice.setAction(actionMap.get("viewInvoice")); // NOI18N
        btnViewInvoice.setFont(resourceMap.getFont("btnViewInvoice.font")); // NOI18N
        btnViewInvoice.setIcon(resourceMap.getIcon("btnViewInvoice.icon")); // NOI18N
        btnViewInvoice.setText(resourceMap.getString("btnViewInvoice.text")); // NOI18N
        btnViewInvoice.setFocusable(false);
        btnViewInvoice.setName("btnViewInvoice"); // NOI18N
        btnViewInvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewInvoiceActionPerformed(evt);
            }
        });
        jToolBar1.add(btnViewInvoice);

        btnPrintReport.setFont(resourceMap.getFont("jButton1.font")); // NOI18N
        btnPrintReport.setIcon(resourceMap.getIcon("btnPrintReport.icon")); // NOI18N
        btnPrintReport.setText(resourceMap.getString("btnPrintReport.text")); // NOI18N
        btnPrintReport.setFocusable(false);
        btnPrintReport.setName("btnPrintReport"); // NOI18N
        btnPrintReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintReportActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPrintReport);

        btnEmailReport.setFont(resourceMap.getFont("jButton9.font")); // NOI18N
        btnEmailReport.setIcon(resourceMap.getIcon("btnEmailReport.icon")); // NOI18N
        btnEmailReport.setText(resourceMap.getString("btnEmailReport.text")); // NOI18N
        btnEmailReport.setFocusable(false);
        btnEmailReport.setName("btnEmailReport"); // NOI18N
        btnEmailReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEmailReportActionPerformed(evt);
            }
        });
        jToolBar1.add(btnEmailReport);

        btnViewReport.setFont(resourceMap.getFont("btnViewReport.font")); // NOI18N
        btnViewReport.setIcon(resourceMap.getIcon("btnViewReport.icon")); // NOI18N
        btnViewReport.setText(resourceMap.getString("btnViewReport.text")); // NOI18N
        btnViewReport.setFocusable(false);
        btnViewReport.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        btnViewReport.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnViewReport.setName("btnViewReport"); // NOI18N
        btnViewReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewReportActionPerformed(evt);
            }
        });
        jToolBar1.add(btnViewReport);

        jButton9.setFont(resourceMap.getFont("jButton9.font")); // NOI18N
        jButton9.setIcon(resourceMap.getIcon("jButton9.icon")); // NOI18N
        jButton9.setText(resourceMap.getString("jButton9.text")); // NOI18N
        jButton9.setFocusable(false);
        jButton9.setName("jButton9"); // NOI18N
        jToolBar1.add(jButton9);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        getContentPane().add(jToolBar1, gridBagConstraints);

        splitPanelMaster.setBorder(null);
        splitPanelMaster.setDividerLocation(220);
        splitPanelMaster.setDividerSize(4);
        splitPanelMaster.setName("splitPanelMaster"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jPanel5.setName("jPanel5"); // NOI18N
        jPanel5.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel5.add(jLabel1, gridBagConstraints);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        txtContactableDetails.setColumns(16);
        txtContactableDetails.setEditable(false);
        txtContactableDetails.setFont(resourceMap.getFont("txtContactableDetails.font")); // NOI18N
        txtContactableDetails.setLineWrap(true);
        txtContactableDetails.setRows(5);
        txtContactableDetails.setName("txtContactableDetails"); // NOI18N
        jScrollPane1.setViewportView(txtContactableDetails);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 80;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel5.add(jScrollPane1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 0, 2);
        jPanel1.add(jPanel5, gridBagConstraints);

        jPanel8.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jPanel8.setName("jPanel8"); // NOI18N
        jPanel8.setLayout(new java.awt.GridBagLayout());

        jLabel5.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
        jPanel8.add(jLabel5, gridBagConstraints);

        cmbContactable.setModel(cmbAgtModel);
        cmbContactable.setName("cmbContactable"); // NOI18N
        cmbContactable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbContactableActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
        jPanel8.add(cmbContactable, gridBagConstraints);

        dtFrom.setName("dtFrom"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
        jPanel8.add(dtFrom, gridBagConstraints);

        dtTo.setName("dtTo"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
        jPanel8.add(dtTo, gridBagConstraints);

        jLabel6.setFont(resourceMap.getFont("jLabel6.font")); // NOI18N
        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
        jPanel8.add(jLabel6, gridBagConstraints);

        jLabel7.setFont(resourceMap.getFont("jLabel6.font")); // NOI18N
        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
        jPanel8.add(jLabel7, gridBagConstraints);

        btnCriteriaSearch.setFont(resourceMap.getFont("btnCriteriaSearch.font")); // NOI18N
        btnCriteriaSearch.setIcon(resourceMap.getIcon("btnCriteriaSearch.icon")); // NOI18N
        btnCriteriaSearch.setText(resourceMap.getString("btnCriteriaSearch.text")); // NOI18N
        btnCriteriaSearch.setName("btnCriteriaSearch"); // NOI18N
        btnCriteriaSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCriteriaSearchActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(8, 2, 2, 2);
        jPanel8.add(btnCriteriaSearch, gridBagConstraints);

        buttonGroup1.add(rdoAgent);
        rdoAgent.setFont(resourceMap.getFont("rdoAgent.font")); // NOI18N
        rdoAgent.setText(resourceMap.getString("rdoAgent.text")); // NOI18N
        rdoAgent.setName("rdoAgent"); // NOI18N
        rdoAgent.addActionListener(radioAgentListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 2, 2, 2);
        jPanel8.add(rdoAgent, gridBagConstraints);

        buttonGroup1.add(rdoCustomer);
        rdoCustomer.setFont(resourceMap.getFont("rdoAgent.font")); // NOI18N
        rdoCustomer.setText(resourceMap.getString("rdoCustomer.text")); // NOI18N
        rdoCustomer.setName("rdoCustomer"); // NOI18N
        rdoCustomer.addActionListener(radioCustomerListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 2, 2, 2);
        jPanel8.add(rdoCustomer, gridBagConstraints);

        buttonGroup1.add(rdoAll);
        rdoAll.setFont(resourceMap.getFont("rdoAgent.font")); // NOI18N
        rdoAll.setSelected(true);
        rdoAll.setText(resourceMap.getString("rdoAll.text")); // NOI18N
        rdoAll.setName("rdoAll"); // NOI18N
        rdoAll.addActionListener(radioAllListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 2, 2, 2);
        jPanel8.add(rdoAll, gridBagConstraints);

        busyIcon.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        busyIcon.setText(resourceMap.getString("busyIcon.text")); // NOI18N
        busyIcon.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        busyIcon.setName("busyIcon"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
        jPanel8.add(busyIcon, gridBagConstraints);

        jSeparator1.setName("jSeparator1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        jPanel8.add(jSeparator1, gridBagConstraints);

        cmbService.setFont(resourceMap.getFont("cmbService.font")); // NOI18N
        cmbService.setName("cmbService"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
        jPanel8.add(cmbService, gridBagConstraints);

        jLabel2.setFont(resourceMap.getFont("jLabel2.font")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
        jPanel8.add(jLabel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanel1.add(jPanel8, gridBagConstraints);

        splitPanelMaster.setLeftComponent(jPanel1);

        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new java.awt.GridBagLayout());

        pagePanel.setBackground(resourceMap.getColor("pagePanel.background")); // NOI18N
        pagePanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pagePanel.setName("pagePanel"); // NOI18N
        pagePanel.setLayout(new java.awt.GridBagLayout());

        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        jButton2.setName("jButton2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 50;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pagePanel.add(jButton2, gridBagConstraints);

        jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
        jButton3.setName("jButton3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 50;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pagePanel.add(jButton3, gridBagConstraints);

        lbl1.setText(resourceMap.getString("lbl1.text")); // NOI18N
        lbl1.setName("lbl1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pagePanel.add(lbl1, gridBagConstraints);

        lblTotalDoc.setName("lblTotalDoc"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pagePanel.add(lblTotalDoc, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 4, 2);
        jPanel2.add(pagePanel, gridBagConstraints);

        JScrollPane.setName("JScrollPane"); // NOI18N

        tblInvoice.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Ref", "Date", "Terms", "DocumentFor", "DocAmount", "TransAmount", "Balance", "DocumentBy"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblInvoice.setFont(resourceMap.getFont("tblInvoice.font")); // NOI18N
        tblInvoice.setName("tblInvoice"); // NOI18N
        tblInvoice.setSortable(false);
        tblInvoice.getTableHeader().setReorderingAllowed(false);
        JScrollPane.setViewportView(tblInvoice);
        tblInvoice.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title0")); // NOI18N
        tblInvoice.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title1")); // NOI18N
        tblInvoice.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title10")); // NOI18N
        tblInvoice.getColumnModel().getColumn(3).setMinWidth(140);
        tblInvoice.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title5")); // NOI18N
        tblInvoice.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title6")); // NOI18N
        tblInvoice.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title7")); // NOI18N
        tblInvoice.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title8")); // NOI18N
        tblInvoice.getColumnModel().getColumn(7).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title9")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.8;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(JScrollPane, gridBagConstraints);

        summeryPanel.setBackground(resourceMap.getColor("summeryPanel.background")); // NOI18N
        summeryPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        summeryPanel.setForeground(resourceMap.getColor("summeryPanel.foreground")); // NOI18N
        summeryPanel.setName("summeryPanel"); // NOI18N

        lblBalance.setText(resourceMap.getString("lblBalance.text")); // NOI18N
        lblBalance.setName("lblBalance"); // NOI18N

        lblTotalOutstanding.setForeground(resourceMap.getColor("lblTotalOutstanding.foreground")); // NOI18N
        lblTotalOutstanding.setText(resourceMap.getString("lblTotalOutstanding.text")); // NOI18N
        lblTotalOutstanding.setName("lblTotalOutstanding"); // NOI18N

        lblTotalPaid.setForeground(resourceMap.getColor("lblTotalPaid.foreground")); // NOI18N
        lblTotalPaid.setText(resourceMap.getString("lblTotalPaid.text")); // NOI18N
        lblTotalPaid.setName("lblTotalPaid"); // NOI18N

        lblTAmount.setText(resourceMap.getString("lblTAmount.text")); // NOI18N
        lblTAmount.setName("lblTAmount"); // NOI18N

        lblTotalAmount.setText(resourceMap.getString("lblTotalAmount.text")); // NOI18N
        lblTotalAmount.setName("lblTotalAmount"); // NOI18N

        lblTotalInvAmount.setForeground(resourceMap.getColor("lblTotalInvAmount.foreground")); // NOI18N
        lblTotalInvAmount.setText(resourceMap.getString("lblTotalInvAmount.text")); // NOI18N
        lblTotalInvAmount.setName("lblTotalInvAmount"); // NOI18N

        javax.swing.GroupLayout summeryPanelLayout = new javax.swing.GroupLayout(summeryPanel);
        summeryPanel.setLayout(summeryPanelLayout);
        summeryPanelLayout.setHorizontalGroup(
            summeryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(summeryPanelLayout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addGroup(summeryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(summeryPanelLayout.createSequentialGroup()
                        .addComponent(lblTotalAmount)
                        .addGap(18, 18, 18)
                        .addComponent(lblTotalInvAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(summeryPanelLayout.createSequentialGroup()
                        .addComponent(lblTAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(lblTotalPaid, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(summeryPanelLayout.createSequentialGroup()
                        .addComponent(lblBalance, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(64, 64, 64)
                        .addComponent(lblTotalOutstanding, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        summeryPanelLayout.setVerticalGroup(
            summeryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(summeryPanelLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(summeryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotalAmount)
                    .addComponent(lblTotalInvAmount))
                .addGap(4, 4, 4)
                .addGroup(summeryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTAmount)
                    .addComponent(lblTotalPaid))
                .addGap(4, 4, 4)
                .addGroup(summeryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblBalance)
                    .addComponent(lblTotalOutstanding)))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 4, 2);
        jPanel2.add(summeryPanel, gridBagConstraints);

        splitPanelMaster.setRightComponent(jPanel2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(splitPanelMaster, gridBagConstraints);

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel4.setName("jPanel4"); // NOI18N
        jPanel4.setLayout(new java.awt.GridBagLayout());

        progressBar.setName("progressBar"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel4.add(progressBar, gridBagConstraints);

        statusMessageLabel.setFont(resourceMap.getFont("statusMessageLabel.font")); // NOI18N
        statusMessageLabel.setText(resourceMap.getString("statusMessageLabel.text")); // NOI18N
        statusMessageLabel.setName("statusMessageLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 0);
        jPanel4.add(statusMessageLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        getContentPane().add(jPanel4, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmbContactableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbContactableActionPerformed
}//GEN-LAST:event_cmbContactableActionPerformed

    private void btnCriteriaSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCriteriaSearchActionPerformed
        new Thread(new threadOutstandingInvoiceReport()).start();
    }//GEN-LAST:event_btnCriteriaSearchActionPerformed

    private void btnViewInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewInvoiceActionPerformed
        viewInvoice();
    }//GEN-LAST:event_btnViewInvoiceActionPerformed

    private void btnViewReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewReportActionPerformed
        oAcDocBo.setAcDocType("Outstanding Invoice Report");        
        BackofficeReporting rptAcDoc = new BackofficeReporting();
        List<Object> invObject = new ArrayList();
        oAcDocBo.setFrom(from);
        oAcDocBo.setTo(to);        
        oAcDocBo.setAgent(this.agent);
        oAcDocBo.setCustomer(this.customer);
        oAcDocBo.setmAgent(AuthenticationBo.getmAgent());      
        oAcDocBo.setOfficeCopy(false);
        oAcDocBo.setAcDocsRpt(this.acDocs);
        
        invObject.add(oAcDocBo);
        rptAcDoc.viewOAcDocHistoryReport(invObject);                
    }//GEN-LAST:event_btnViewReportActionPerformed

    private void btnEmailReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmailReportActionPerformed
        oAcDocBo.setAcDocType("Outstanding Invoice Report");        
        BackofficeReporting rptAcDoc = new BackofficeReporting();
        List<Object> invObject = new ArrayList();
        oAcDocBo.setFrom(from);
        oAcDocBo.setTo(to);        
        oAcDocBo.setAgent(this.agent);
        oAcDocBo.setCustomer(this.customer);
        oAcDocBo.setmAgent(AuthenticationBo.getmAgent());      
        oAcDocBo.setOfficeCopy(false);
        oAcDocBo.setAcDocsRpt(this.acDocs);
        
        invObject.add(oAcDocBo);
        if (this.agent != null) {
            String emailAddress = this.agent.getEmail();
            if (!emailAddress.isEmpty()) {
                String subject = "Outstanding Invoice Report from : " + AuthenticationBo.getmAgent().getName();
                String body = "Outstanding Invoices from " + AuthenticationBo.getmAgent().getName();                
                rptAcDoc.emailOAcDocHistoryReport(emailAddress, subject, body, invObject);
            } else {
                JOptionPane.showMessageDialog(null, "No email address found \n Please add email address in clients profile.", "Email Report", JOptionPane.WARNING_MESSAGE);
            }
        }else{
        JOptionPane.showMessageDialog(null, "No Agent", "Email Report", JOptionPane.WARNING_MESSAGE);
        }        
    }//GEN-LAST:event_btnEmailReportActionPerformed

    private void btnPrintReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintReportActionPerformed
         oAcDocBo.setAcDocType("Outstanding Invoice Report");        
        BackofficeReporting rptAcDoc = new BackofficeReporting();
        List<Object> invObject = new ArrayList();
        oAcDocBo.setFrom(from);
        oAcDocBo.setTo(to);        
        oAcDocBo.setAgent(this.agent);
        oAcDocBo.setCustomer(this.customer);
        oAcDocBo.setmAgent(AuthenticationBo.getmAgent());      
        oAcDocBo.setOfficeCopy(false);
        oAcDocBo.setAcDocsRpt(this.acDocs);
        
        invObject.add(oAcDocBo);
        rptAcDoc.printOAcDocHistoryReport(invObject);
    }//GEN-LAST:event_btnPrintReportActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane JScrollPane;
    private javax.swing.JButton btnCriteriaSearch;
    private javax.swing.JButton btnEmailReport;
    private javax.swing.JButton btnPrintReport;
    private javax.swing.JButton btnViewInvoice;
    private javax.swing.JButton btnViewReport;
    private org.jdesktop.swingx.JXBusyLabel busyIcon;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JComboBox cmbContactable;
    private javax.swing.JComboBox cmbService;
    private org.jdesktop.swingx.JXDatePicker dtFrom;
    private org.jdesktop.swingx.JXDatePicker dtTo;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lbl1;
    private javax.swing.JLabel lblBalance;
    private javax.swing.JLabel lblTAmount;
    private javax.swing.JLabel lblTotalAmount;
    private javax.swing.JLabel lblTotalDoc;
    private javax.swing.JLabel lblTotalInvAmount;
    private javax.swing.JLabel lblTotalOutstanding;
    private javax.swing.JLabel lblTotalPaid;
    private javax.swing.JPanel pagePanel;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JRadioButton rdoAgent;
    private javax.swing.JRadioButton rdoAll;
    private javax.swing.JRadioButton rdoCustomer;
    private javax.swing.JSplitPane splitPanelMaster;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel summeryPanel;
    private org.jdesktop.swingx.JXTable tblInvoice;
    private javax.swing.JTextArea txtContactableDetails;
    // End of variables declaration//GEN-END:variables

    
    public void viewInvoice() {
        int row = tblInvoice.getSelectedRow();
        if (row != -1) {
            try {
                long acDocId = this.acDocs.get(row).getoAcDocId();
                int docType = this.acDocs.get(row).getAcDoctype();
                Thread t = new Thread(new threadLoadCompleteAcDoc(acDocId));
                t.start();
                t.join();
                {
                  cf.frameOAcDoc(this.acDoc);
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(FrameSOAcDocOutstanding.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    //************************threads***************************
    private long startTime = 0;
    private long stopTime = 0;
    private float elapsedTime = 0;

    /**
     * @param pnrDao the pnrDao to set
     */
    private class threadLoadCompleteAcDoc implements Runnable {

        private long acDocId;

        public threadLoadCompleteAcDoc(long acDocId) {
            this.acDocId = acDocId;
        }

        public void run() {

            //progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            //progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();
            statusMessageLabel.setText("Searching...");

            acDoc = oAcDocBo.findCompleteAcDocById(acDocId);

            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Search Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            //progressBar.repaint(); //Refresh graphics
        }
    }

    private class threadOutstandingInvoiceReport implements Runnable {

        private int noOfRecord;

        public threadOutstandingInvoiceReport() {

        }
        public void run() {
            //progressBar = new javax.swing.JProgressBar();
            //progressBar.repaint(); //Refresh graphics
            progressBar.setIndeterminate(true); //Set value
            
            startTime = System.currentTimeMillis();

            statusMessageLabel.setText("Loading Invoice...");
            btnCriteriaSearch.setEnabled(false);
            searchAcDocByCriteria();
            noOfRecord = acDocs.size();

            btnCriteriaSearch.setEnabled(true);
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);

            if (noOfRecord > 0) {
                statusMessageLabel.setText(noOfRecord + " Records fetched in: " + elapsedTime / 1000 + " seconds");
            } else {
                statusMessageLabel.setText("No record found. Search Copleted in " + elapsedTime / 1000 + " seconds");
            }
            
            progressBar.setIndeterminate(false);
            //progressBar.repaint(); //Refresh graphics
        }
    }

    private class threadOutstandingAgents implements Runnable {

        public threadOutstandingAgents() {

        }
        public void run() {
            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();

            statusMessageLabel.setText("Loading Invoice...");
            btnCriteriaSearch.setEnabled(false);
            searchAcDocByCriteria();
            btnCriteriaSearch.setEnabled(true);
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            progressBar.repaint(); //Refresh graphics
        }
    }
    
        
     private class PopulateCmbService extends SwingWorker<List<OtherService>, Void> {

        public PopulateCmbService() {
        }

        @Override
        protected List<OtherService> doInBackground() throws Exception {
            List<OtherService> services = oServiceBo.loadOtherServices();
            return services;
        }

        @Override
        protected void done() {
            try {
                oServices = get();
                populateCmbService();
            } catch (InterruptedException ex) {
                Logger.getLogger(FrameSAcDocHistory.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(FrameSAcDocHistory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
