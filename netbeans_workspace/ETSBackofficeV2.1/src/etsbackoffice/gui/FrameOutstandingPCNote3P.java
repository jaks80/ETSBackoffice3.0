
/*
 * FrameOutstandingInvoice.java
 *
 * Created on 18-Oct-2010, 23:15:52
 */
package etsbackoffice.gui;

import etsbackoffice.ETSBackofficeApp;
import etsbackoffice.businesslogic.AccountingDocBo;
import etsbackoffice.businesslogic.AgentBo;
import etsbackoffice.businesslogic.DateFormat;
import etsbackoffice.domain.Agent;
import etsbackoffice.domain.PurchaseAccountingDocument;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JProgressBar;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.application.Action;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 *
 * @author Yusuf
 */
public class FrameOutstandingPCNote3P extends javax.swing.JFrame{

    private DateFormat df = new DateFormat();

    AccountingDocBo acDocBo = (AccountingDocBo) ETSBackofficeApp.getApplication().ctx.getBean("acDocBo");
    AgentBo agentBo = (AgentBo) ETSBackofficeApp.getApplication().ctx.getBean("agentBo");
    
    private PurchaseAccountingDocument acDoc = new PurchaseAccountingDocument();
    private List<Agent> outstandingAgents = new ArrayList();    

    private List<PurchaseAccountingDocument> outstandingInvoices = new ArrayList();

    private DefaultTableModel invoiceModel;    
    DefaultComboBoxModel cmbAgtModel = new DefaultComboBoxModel();

    /** Creates new form FrameOutstandingInvoice */
    public FrameOutstandingPCNote3P(java.awt.Frame parent) {
        initComponents();
        busyIcon.setVisible(false);        
    }

    private void loadOutstandingInvoice() {
        this.outstandingInvoices = acDocBo.outstandingPurchaseInvoice();
        populateTblInvoice(this.outstandingInvoices);
    }

    public void showFrameOutBilling() {
        setLocationRelativeTo(this);
        setVisible(true);
        toFront();
        startUpThreads();
    }

    private void startUpThreads(){
    new Thread(new threadOutstandingVendors()).start();
    }

    public void searchOutstandingPInvoice() {
        
        Long contactableId = null;
        Date from = null;
        Date to = null;
        String[] data;
        
        if(cmbContactable.getSelectedIndex() > 0){
        data = cmbContactable.getSelectedItem().toString().split("-");
        contactableId = Long.parseLong(data[2]);
        }
        from = dtFrom.getDate();
        to = dtTo.getDate();
                
        this.outstandingInvoices = acDocBo.outstandingPCNoteByCriteria(contactableId, from, to);
        
        populateTblInvoice(this.outstandingInvoices);
    }

    private void populateTblInvoice(List<PurchaseAccountingDocument> outstandingInvoices) {

        int row = 0;
        invoiceModel = (DefaultTableModel) tblInvoice.getModel();
        invoiceModel.getDataVector().removeAllElements();
        JScrollPane.repaint();
        BigDecimal totalInvAmount = new BigDecimal("0.00");
        BigDecimal grandTotalCredit = new BigDecimal("0.00");
        BigDecimal totalReceived = new BigDecimal("0.00");
        BigDecimal totalDue = new BigDecimal("0.00");
        BigDecimal balance = new BigDecimal("0.00");

        for (PurchaseAccountingDocument a : outstandingInvoices) {

            String clientName = "";            

            if(a.getPnr().getTicketingAgt()!=null){
            clientName = a.getPnr().getTicketingAgt().getName();
            }
            BigDecimal totalCredit = new BigDecimal("0.00");            

            for (PurchaseAccountingDocument creditNote : a.getRelatedDocuments()) {
                totalCredit = totalCredit.add(creditNote.getTotalDocumentedAmount());
            }
            BigDecimal due = a.getTotalDocumentedAmount().subtract(a.getTotalPaid()).add(totalCredit);
            invoiceModel.insertRow(row, new Object[]{a.getPnr().getGdsPNR(), a.getPnr().getServicingCareer().getCode(),
                        a.getLeadPaxFromTickets(), a.getRecipientRef(),
                        df.dateForGui(a.getIssueDate()), a.getTerms(), clientName,
                         a.getTotalDocumentedAmount(),totalCredit,
                        a.getTotalPaid(),
                        due});
            row++;
            totalInvAmount = totalInvAmount.add(a.getTotalDocumentedAmount());
            totalReceived = totalReceived.add(a.getTotalPaid());
            grandTotalCredit = grandTotalCredit.add(totalCredit);
            totalDue = totalDue.add(due);
        }
         lblTotalInvAmount.setText(totalInvAmount.toString());
         lblTotalPaid.setText(totalReceived.toString());
         lblTotalCredit.setText(grandTotalCredit.toString());
         lblTotalOutstanding.setText(totalDue.toString());
         lblTotalInvoce.setText(String.valueOf(outstandingInvoices.size()));
    }

    private void populateTxtAgentDetails(Agent agent) {

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

    private void outstandingVendors(){
    List cmbElement = new ArrayList();
            outstandingAgents = acDocBo.outstandingCNoteVendors();
            for (Agent agent : outstandingAgents) {
                cmbElement.add(agent.getName() + "-" + agent.getPostCode()
                        + "-" + agent.getContactableId());
            }

            Collections.sort(cmbElement);
        DefaultComboBoxModel cmbContactableModel = new DefaultComboBoxModel(cmbElement.toArray());
        cmbContactableModel.insertElementAt("Select", 0);
        cmbContactable.setModel(cmbContactableModel);
        cmbContactable.setSelectedIndex(0);
    }
    
    private ActionListener cmbContactableListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            String[] data;
            long id = 0;
            if (cmbContactable.getSelectedIndex() >0) {
                data = cmbContactable.getSelectedItem().toString().split("-");
                id = Long.parseLong(data[2]);
            }
            loop:
            for (Agent agent : outstandingAgents) {
                if (agent.getContactableId() == id) {
                    populateTxtAgentDetails(agent);
                    break loop;
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
        jToolBar1 = new javax.swing.JToolBar();
        jButton1 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
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
        btnSearch = new javax.swing.JButton();
        busyIcon = new org.jdesktop.swingx.JXBusyLabel();
        jPanel2 = new javax.swing.JPanel();
        pagePanel = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        lbl1 = new javax.swing.JLabel();
        lblTotalInvoce = new javax.swing.JLabel();
        JScrollPane = new javax.swing.JScrollPane();
        tblInvoice = new org.jdesktop.swingx.JXTable();
        summeryPanel = new javax.swing.JPanel();
        lblTotalCredit = new javax.swing.JLabel();
        lbl4 = new javax.swing.JLabel();
        lblTotalOutstanding = new javax.swing.JLabel();
        lblTotalPaid = new javax.swing.JLabel();
        lbl3 = new javax.swing.JLabel();
        lbl2 = new javax.swing.JLabel();
        lblTotalInvAmount = new javax.swing.JLabel();
        lbl5 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        statusMessageLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getResourceMap(FrameOutstandingPCNote3P.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jToolBar1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        jButton1.setFont(resourceMap.getFont("jButton8.font")); // NOI18N
        jButton1.setIcon(resourceMap.getIcon("jButton1.icon")); // NOI18N
        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setFocusable(false);
        jButton1.setName("jButton1"); // NOI18N
        jToolBar1.add(jButton1);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getActionMap(FrameOutstandingPCNote3P.class, this);
        jButton5.setAction(actionMap.get("viewInvoice")); // NOI18N
        jButton5.setFont(resourceMap.getFont("jButton8.font")); // NOI18N
        jButton5.setIcon(resourceMap.getIcon("jButton5.icon")); // NOI18N
        jButton5.setText(resourceMap.getString("jButton5.text")); // NOI18N
        jButton5.setFocusable(false);
        jButton5.setName("jButton5"); // NOI18N
        jToolBar1.add(jButton5);

        jButton8.setFont(resourceMap.getFont("jButton8.font")); // NOI18N
        jButton8.setIcon(resourceMap.getIcon("jButton8.icon")); // NOI18N
        jButton8.setText(resourceMap.getString("jButton8.text")); // NOI18N
        jButton8.setFocusable(false);
        jButton8.setName("jButton8"); // NOI18N
        jToolBar1.add(jButton8);

        jButton7.setFont(resourceMap.getFont("jButton8.font")); // NOI18N
        jButton7.setIcon(resourceMap.getIcon("jButton7.icon")); // NOI18N
        jButton7.setText(resourceMap.getString("jButton7.text")); // NOI18N
        jButton7.setFocusable(false);
        jButton7.setName("jButton7"); // NOI18N
        jToolBar1.add(jButton7);

        jButton4.setFont(resourceMap.getFont("jButton8.font")); // NOI18N
        jButton4.setIcon(resourceMap.getIcon("jButton4.icon")); // NOI18N
        jButton4.setText(resourceMap.getString("jButton4.text")); // NOI18N
        jButton4.setFocusable(false);
        jButton4.setName("jButton4"); // NOI18N
        jToolBar1.add(jButton4);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        getContentPane().add(jToolBar1, gridBagConstraints);

        splitPanelMaster.setBorder(null);
        splitPanelMaster.setDividerLocation(200);
        splitPanelMaster.setDividerSize(4);
        splitPanelMaster.setName("splitPanelMaster"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jPanel5.setName("jPanel5"); // NOI18N
        jPanel5.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(resourceMap.getFont("jLabel7.font")); // NOI18N
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
        txtContactableDetails.setRows(5);
        txtContactableDetails.setName("txtContactableDetails"); // NOI18N
        jScrollPane1.setViewportView(txtContactableDetails);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel5.add(jScrollPane1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 0, 2);
        jPanel1.add(jPanel5, gridBagConstraints);

        jPanel8.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jPanel8.setName("jPanel8"); // NOI18N
        jPanel8.setLayout(new java.awt.GridBagLayout());

        jLabel5.setFont(resourceMap.getFont("jLabel7.font")); // NOI18N
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 2, 0, 2);
        jPanel8.add(jLabel5, gridBagConstraints);

        cmbContactable.setFont(resourceMap.getFont("dtFrom.font")); // NOI18N
        cmbContactable.setModel(cmbAgtModel);
        cmbContactable.setName("cmbContactable"); // NOI18N
        cmbContactable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbContactableActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 2);
        jPanel8.add(cmbContactable, gridBagConstraints);

        dtFrom.setFont(resourceMap.getFont("dtFrom.font")); // NOI18N
        dtFrom.setName("dtFrom"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 2);
        jPanel8.add(dtFrom, gridBagConstraints);

        dtTo.setFont(resourceMap.getFont("dtFrom.font")); // NOI18N
        dtTo.setName("dtTo"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 2);
        jPanel8.add(dtTo, gridBagConstraints);

        jLabel6.setFont(resourceMap.getFont("jLabel7.font")); // NOI18N
        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 2, 0, 2);
        jPanel8.add(jLabel6, gridBagConstraints);

        jLabel7.setFont(resourceMap.getFont("jLabel7.font")); // NOI18N
        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 2, 0, 2);
        jPanel8.add(jLabel7, gridBagConstraints);

        btnSearch.setFont(resourceMap.getFont("btnSearch.font")); // NOI18N
        btnSearch.setIcon(resourceMap.getIcon("btnSearch.icon")); // NOI18N
        btnSearch.setText(resourceMap.getString("btnSearch.text")); // NOI18N
        btnSearch.setName("btnSearch"); // NOI18N
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 2, 0, 2);
        jPanel8.add(btnSearch, gridBagConstraints);

        busyIcon.setText(resourceMap.getString("busyIcon.text")); // NOI18N
        busyIcon.setName("busyIcon"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 2, 0, 2);
        jPanel8.add(busyIcon, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
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

        lbl1.setFont(resourceMap.getFont("lbl1.font")); // NOI18N
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

        lblTotalInvoce.setFont(resourceMap.getFont("lblTotalInvoce.font")); // NOI18N
        lblTotalInvoce.setForeground(resourceMap.getColor("lblTotalInvoce.foreground")); // NOI18N
        lblTotalInvoce.setText(resourceMap.getString("lblTotalInvoce.text")); // NOI18N
        lblTotalInvoce.setName("lblTotalInvoce"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pagePanel.add(lblTotalInvoce, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 4, 2);
        jPanel2.add(pagePanel, gridBagConstraints);

        JScrollPane.setName("JScrollPane"); // NOI18N

        tblInvoice.setBackground(resourceMap.getColor("tblInvoice.background")); // NOI18N
        tblInvoice.setForeground(resourceMap.getColor("tblInvoice.foreground")); // NOI18N
        tblInvoice.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "GDSPNR", "Career", "LeadPax", "InvRef", "InvDate", "Terms", "InvFrom", "InvAmount", "CNote", "Paid", "Outstanding"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblInvoice.setFont(resourceMap.getFont("tblInvoice.font")); // NOI18N
        tblInvoice.setSortable(false);
        tblInvoice.getTableHeader().setReorderingAllowed(false);
        JScrollPane.setViewportView(tblInvoice);
        tblInvoice.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title0")); // NOI18N
        tblInvoice.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title1")); // NOI18N
        tblInvoice.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title2")); // NOI18N
        tblInvoice.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title3")); // NOI18N
        tblInvoice.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title4")); // NOI18N
        tblInvoice.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title5")); // NOI18N
        tblInvoice.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title6")); // NOI18N
        tblInvoice.getColumnModel().getColumn(7).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title7")); // NOI18N
        tblInvoice.getColumnModel().getColumn(8).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title8")); // NOI18N
        tblInvoice.getColumnModel().getColumn(9).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title9")); // NOI18N
        tblInvoice.getColumnModel().getColumn(10).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title10")); // NOI18N

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
        summeryPanel.setLayout(new java.awt.GridBagLayout());

        lblTotalCredit.setFont(resourceMap.getFont("lbl3.font")); // NOI18N
        lblTotalCredit.setForeground(resourceMap.getColor("lblTotalOutstanding.foreground")); // NOI18N
        lblTotalCredit.setText(resourceMap.getString("lblTotalCredit.text")); // NOI18N
        lblTotalCredit.setName("lblTotalCredit"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        summeryPanel.add(lblTotalCredit, gridBagConstraints);

        lbl4.setFont(resourceMap.getFont("lbl3.font")); // NOI18N
        lbl4.setForeground(resourceMap.getColor("lblTotalOutstanding.foreground")); // NOI18N
        lbl4.setText(resourceMap.getString("lbl4.text")); // NOI18N
        lbl4.setName("lbl4"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 45;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        summeryPanel.add(lbl4, gridBagConstraints);

        lblTotalOutstanding.setFont(resourceMap.getFont("lbl3.font")); // NOI18N
        lblTotalOutstanding.setForeground(resourceMap.getColor("lblTotalOutstanding.foreground")); // NOI18N
        lblTotalOutstanding.setText(resourceMap.getString("lblTotalOutstanding.text")); // NOI18N
        lblTotalOutstanding.setName("lblTotalOutstanding"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        summeryPanel.add(lblTotalOutstanding, gridBagConstraints);

        lblTotalPaid.setFont(resourceMap.getFont("lbl3.font")); // NOI18N
        lblTotalPaid.setForeground(resourceMap.getColor("lblTotalOutstanding.foreground")); // NOI18N
        lblTotalPaid.setText(resourceMap.getString("lblTotalPaid.text")); // NOI18N
        lblTotalPaid.setName("lblTotalPaid"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        summeryPanel.add(lblTotalPaid, gridBagConstraints);

        lbl3.setFont(resourceMap.getFont("lbl3.font")); // NOI18N
        lbl3.setForeground(resourceMap.getColor("lblTotalOutstanding.foreground")); // NOI18N
        lbl3.setText(resourceMap.getString("lbl3.text")); // NOI18N
        lbl3.setName("lbl3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        summeryPanel.add(lbl3, gridBagConstraints);

        lbl2.setFont(resourceMap.getFont("lbl3.font")); // NOI18N
        lbl2.setForeground(resourceMap.getColor("lblTotalOutstanding.foreground")); // NOI18N
        lbl2.setText(resourceMap.getString("lbl2.text")); // NOI18N
        lbl2.setName("lbl2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        summeryPanel.add(lbl2, gridBagConstraints);

        lblTotalInvAmount.setFont(resourceMap.getFont("lbl3.font")); // NOI18N
        lblTotalInvAmount.setForeground(resourceMap.getColor("lblTotalOutstanding.foreground")); // NOI18N
        lblTotalInvAmount.setText(resourceMap.getString("lblTotalInvAmount.text")); // NOI18N
        lblTotalInvAmount.setName("lblTotalInvAmount"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        summeryPanel.add(lblTotalInvAmount, gridBagConstraints);

        lbl5.setFont(resourceMap.getFont("lbl3.font")); // NOI18N
        lbl5.setForeground(resourceMap.getColor("lblTotalOutstanding.foreground")); // NOI18N
        lbl5.setText(resourceMap.getString("Balance.text")); // NOI18N
        lbl5.setName("Balance"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        summeryPanel.add(lbl5, gridBagConstraints);

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

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        new Thread(new threadOutstandingPInvoices()).start();
    }//GEN-LAST:event_btnSearchActionPerformed

    /**
     * @param args the command line arguments
     */
/*    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new FrameOutstandingPCNote3P().setVisible(true);
            }
        });
    }*/
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane JScrollPane;
    private javax.swing.JButton btnSearch;
    private org.jdesktop.swingx.JXBusyLabel busyIcon;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox cmbContactable;
    private org.jdesktop.swingx.JXDatePicker dtFrom;
    private org.jdesktop.swingx.JXDatePicker dtTo;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lbl1;
    private javax.swing.JLabel lbl2;
    private javax.swing.JLabel lbl3;
    private javax.swing.JLabel lbl4;
    private javax.swing.JLabel lbl5;
    private javax.swing.JLabel lblTotalCredit;
    private javax.swing.JLabel lblTotalInvAmount;
    private javax.swing.JLabel lblTotalInvoce;
    private javax.swing.JLabel lblTotalOutstanding;
    private javax.swing.JLabel lblTotalPaid;
    private javax.swing.JPanel pagePanel;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JSplitPane splitPanelMaster;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel summeryPanel;
    private org.jdesktop.swingx.JXTable tblInvoice;
    private javax.swing.JTextArea txtContactableDetails;
    // End of variables declaration//GEN-END:variables
   @Action
    public void viewInvoice() {
        int row = tblInvoice.getSelectedRow();
        if (row != -1) {
            try {
                long acDocId = this.outstandingInvoices.get(row).getPurchaseAcDocId();
                Thread t = new Thread(new threadLoadCompleteInvoice(acDocId));
                t.start();
                t.join();
                {
                 DlgTPCNote dlgTPAcDoc = new DlgTPCNote(this);  
                 dlgTPAcDoc.showDialog(this.acDoc);
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(FrameOutstandingPCNote3P.class.getName()).log(Level.SEVERE, null, ex);
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
    private class threadLoadCompleteInvoice implements Runnable {

        private long acDocId;

        public threadLoadCompleteInvoice(long acDocId) {
            this.acDocId = acDocId;
        }

        public void run() {

            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();

            statusMessageLabel.setText("Loading Invoice...");
            acDocBo.findPurchaseAcDocById(acDocId);
            acDoc = acDocBo.getPurchaseAccountingDocument();

            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            progressBar.repaint(); //Refresh graphics
        }
    }

    private class threadOutstandingPInvoices implements Runnable {

        public threadOutstandingPInvoices() {

        }
        public void run() {
            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();

            statusMessageLabel.setText("Loading Invoice...");
            btnSearch.setEnabled(false);
            searchOutstandingPInvoice();
            btnSearch.setEnabled(true);
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            progressBar.repaint(); //Refresh graphics
        }
    }

    private class threadOutstandingVendors implements Runnable {

        public threadOutstandingVendors() {

        }
        public void run() {
            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();

            statusMessageLabel.setText("Loading Invoice...");
            btnSearch.setEnabled(false);
            outstandingVendors();
            btnSearch.setEnabled(true);
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            progressBar.repaint(); //Refresh graphics
        }
    }
}
