package etsbackoffice.gui;

import etsbackoffice.ETSBackofficeApp;
import etsbackoffice.businesslogic.AccountingDocBo;
import etsbackoffice.businesslogic.AgentBo;
import etsbackoffice.businesslogic.AuthenticationBo;
import etsbackoffice.businesslogic.CustomerBo;
import etsbackoffice.domain.AccountingDocument;
import etsbackoffice.domain.Agent;
import etsbackoffice.domain.Customer;
import etsbackoffice.report.BackofficeReporting;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 *
 * @author Yusuf
 */
public class FrameOutstandingSCNote extends javax.swing.JFrame{

    AccountingDocBo acDocBo = (AccountingDocBo) ETSBackofficeApp.getApplication().ctx.getBean("acDocBo");
    AgentBo agentBo = (AgentBo) ETSBackofficeApp.getApplication().ctx.getBean("agentBo");
    CustomerBo customerBo = (CustomerBo) ETSBackofficeApp.getApplication().ctx.getBean("customerBo");
    private AccountingDocument acDoc = new AccountingDocument();
    private List<Agent> outstandingAgents = new ArrayList();
    private List<Customer> outstandingCustomers = new ArrayList();
    private List<AccountingDocument> outstandingCNotes = new ArrayList();
    private DefaultTableModel invoiceModel;
    DefaultComboBoxModel cmbCustModel = new DefaultComboBoxModel();
    DefaultComboBoxModel cmbAgtModel = new DefaultComboBoxModel();
    private Agent agent;
    private Customer customer;
    
    /** Creates new form FrameOutstandingRefund */
    public FrameOutstandingSCNote(java.awt.Frame parent) {
        initComponents();        
        loadOutstandingRefund();
    }

    public void showFrameOutRefund() {
        setLocationRelativeTo(this);
        setVisible(true);
        toFront();
    }

    private void loadOutstandingRefund() {
        //this.outstandingInvoices = acDocBo.outstandingRefund();
        //populateTblInvoice(this.outstandingInvoices);
         new Thread(new threadOutstandingRefundReport()).start();
    }

    private void populateTxtAgentDetails(Agent agent) {
        txtContactableDetails.setText(agent.getFullAddressCRSeperated());
    }

    private void populateTxtCustomerDetails(Customer customer) {
        txtContactableDetails.setText(customer.getFullAddressCRSeperated());
    }

    public void searchOutstandingRefund() {
        int contactableType = 0;
        Long contactableId = null;
        Date from = null;
        Date to = null;
        String[] data;

        if (cmbContactable.getSelectedIndex()>0) {
            data = cmbContactable.getSelectedItem().toString().split("-");
            contactableId = Long.parseLong(data[2]);
        }
        from = dtFrom.getDate();
        to = dtTo.getDate();

        if (rdoAgent.isSelected()) {
            contactableType = 1;            
        } else if (rdoCustomer.isSelected()) {
            contactableType = 2;            
        } else if (rdoAll.isSelected()) {
            contactableType = 0;            
        }
        
        this.outstandingCNotes = acDocBo.outstandingCNote(contactableId, contactableType, from, to);
        populateTblInvoice(this.outstandingCNotes);
    }

    private void populateTblInvoice(List<AccountingDocument> outstandingInvoices) {

        int row = 0;
        invoiceModel = (DefaultTableModel) tblInvoice.getModel();
        invoiceModel.getDataVector().removeAllElements();
        tblInvoice.repaint();
        
        BigDecimal totalInvAmount = new BigDecimal("0.00");
        BigDecimal grandTotalCredit = new BigDecimal("0.00");
        BigDecimal totalReceived = new BigDecimal("0.00");
        BigDecimal totalDue = new BigDecimal("0.00");
        BigDecimal balance = new BigDecimal("0.00");
        
        for (AccountingDocument a : outstandingInvoices) {

            String clientName = "";
            if (a.getPnr().getAgent() != null) {
                clientName = a.getPnr().getAgent().getName();
            } else if (a.getPnr().getCustomer() != null) {
                clientName = a.getPnr().getCustomer().getSurName() + "/" + a.getPnr().getCustomer().getForeName();
            }
            BigDecimal totalCredit = new BigDecimal("0.00");

            for (AccountingDocument creditNote : a.getRelatedDocuments()) {
                totalCredit = totalCredit.add(creditNote.getTotalDocumentedAmount());
            }
            BigDecimal due = a.getTotalDocumentedAmount().subtract(a.getTotalTransactionAmountWithRelatedDoc()).add(totalCredit);
            
            invoiceModel.insertRow(row, new Object[]{a.getPnr().getGdsPNR(), a.getPnr().getServicingCareer().getCode(),
                        a.getLeadPaxFromTickets(), a.getAcDocRef(),
                        a.getIssueDate(), a.getTerms(), clientName,
                        a.getAcDocIssuedBy().getSurName(), a.getTotalDocumentedAmount(), totalCredit,
                        a.getTotalTransactionAmountWithRelatedDoc(),
                        due});
            row++;
            totalInvAmount = totalInvAmount.add(a.getTotalDocumentedAmount());
            totalReceived = totalReceived.add(a.getTotalTransactionAmount());
            grandTotalCredit = grandTotalCredit.add(totalCredit);
            totalDue = totalDue.add(due);
        }
        lblTotalInvAmount.setText(totalInvAmount.toString());
         lblTotalPaid.setText(totalReceived.toString());
         lblTotalCredit.setText(grandTotalCredit.toString());
         lblTotalOutstanding.setText(totalDue.toString());
    }

    private ActionListener radioAgentListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            busyIcon.setVisible(true);
            busyIcon.setBusy(true);
            List cmbElement = new ArrayList();
            outstandingAgents = acDocBo.outstandingRefundAgents();
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
            outstandingCustomers = acDocBo.outstandingRefundCustomers();

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
            //busyIcon.setVisible(true);
            //busyIcon.setBusy(true);
            cmbContactable.setSelectedIndex(-1);
            dtFrom.setDate(null);
            dtTo.setDate(null);
            //busyIcon.setBusy(false);
            //busyIcon.setVisible(false);
        }
    };
    private ActionListener cmbContactableListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            String[] data;
            long id = 0;
            if (cmbContactable.getSelectedIndex() >0) {
                data = cmbContactable.getSelectedItem().toString().split("-");
                id = Long.parseLong(data[2]);
            }

            if (rdoAgent.isSelected()) {
                loop:
                for (Agent a : outstandingAgents) {
                    if (a.getContactableId() == id) {
                        agent = a;
                        populateTxtAgentDetails(a);
                        break loop;
                    }
                }
            } else if (rdoCustomer.isSelected()) {
                loop:
                for (Customer c : outstandingCustomers) {
                    if (c.getContactableId() == id) {
                        customer = c;
                        populateTxtCustomerDetails(c);
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
        jToolBar1 = new javax.swing.JToolBar();
        jButton1 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        btnViewReport = new javax.swing.JButton();
        btnEmailReport = new javax.swing.JButton();
        btnPrintReport = new javax.swing.JButton();
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
        rdoAgent = new javax.swing.JRadioButton();
        rdoCustomer = new javax.swing.JRadioButton();
        rdoAll = new javax.swing.JRadioButton();
        busyIcon = new org.jdesktop.swingx.JXBusyLabel();
        jPanel2 = new javax.swing.JPanel();
        JScrollPane = new javax.swing.JScrollPane();
        tblInvoice = new org.jdesktop.swingx.JXTable();
        pagePanel = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        lbl1 = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
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
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getResourceMap(FrameOutstandingSCNote.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jToolBar1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        jButton1.setFont(resourceMap.getFont("btnEmailReport.font")); // NOI18N
        jButton1.setIcon(resourceMap.getIcon("jButton1.icon")); // NOI18N
        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setFocusable(false);
        jButton1.setName("jButton1"); // NOI18N
        jToolBar1.add(jButton1);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getActionMap(FrameOutstandingSCNote.class, this);
        jButton5.setAction(actionMap.get("viewInvoice")); // NOI18N
        jButton5.setFont(resourceMap.getFont("btnEmailReport.font")); // NOI18N
        jButton5.setIcon(resourceMap.getIcon("jButton5.icon")); // NOI18N
        jButton5.setText(resourceMap.getString("jButton5.text")); // NOI18N
        jButton5.setFocusable(false);
        jButton5.setName("jButton5"); // NOI18N
        jToolBar1.add(jButton5);

        btnViewReport.setFont(resourceMap.getFont("btnEmailReport.font")); // NOI18N
        btnViewReport.setIcon(resourceMap.getIcon("btnViewReport.icon")); // NOI18N
        btnViewReport.setText(resourceMap.getString("btnViewReport.text")); // NOI18N
        btnViewReport.setFocusable(false);
        btnViewReport.setName("btnViewReport"); // NOI18N
        btnViewReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewReportActionPerformed(evt);
            }
        });
        jToolBar1.add(btnViewReport);

        btnEmailReport.setFont(resourceMap.getFont("btnEmailReport.font")); // NOI18N
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

        btnPrintReport.setFont(resourceMap.getFont("btnPrintReport.font")); // NOI18N
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

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
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

        jLabel1.setFont(resourceMap.getFont("txtContactableDetails.font")); // NOI18N
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

        txtContactableDetails.setColumns(15);
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
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 0, 2);
        jPanel1.add(jPanel5, gridBagConstraints);

        jPanel8.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jPanel8.setName("jPanel8"); // NOI18N

        jLabel5.setFont(resourceMap.getFont("btnSearch.font")); // NOI18N
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        cmbContactable.setFont(resourceMap.getFont("btnSearch.font")); // NOI18N
        cmbContactable.setModel(cmbAgtModel);
        cmbContactable.setName("cmbContactable"); // NOI18N
        cmbContactable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbContactableActionPerformed(evt);
            }
        });

        dtFrom.setFont(resourceMap.getFont("btnSearch.font")); // NOI18N
        dtFrom.setName("dtFrom"); // NOI18N

        dtTo.setFont(resourceMap.getFont("btnSearch.font")); // NOI18N
        dtTo.setName("dtTo"); // NOI18N

        jLabel6.setFont(resourceMap.getFont("btnSearch.font")); // NOI18N
        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        jLabel7.setFont(resourceMap.getFont("btnSearch.font")); // NOI18N
        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        btnSearch.setFont(resourceMap.getFont("btnSearch.font")); // NOI18N
        btnSearch.setIcon(resourceMap.getIcon("btnSearch.icon")); // NOI18N
        btnSearch.setText(resourceMap.getString("btnSearch.text")); // NOI18N
        btnSearch.setName("btnSearch"); // NOI18N
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        buttonGroup1.add(rdoAgent);
        rdoAgent.setFont(resourceMap.getFont("btnSearch.font")); // NOI18N
        rdoAgent.setText(resourceMap.getString("rdoAgent.text")); // NOI18N
        rdoAgent.setName("rdoAgent"); // NOI18N
        rdoAgent.addActionListener(radioAgentListener);

        buttonGroup1.add(rdoCustomer);
        rdoCustomer.setFont(resourceMap.getFont("btnSearch.font")); // NOI18N
        rdoCustomer.setText(resourceMap.getString("rdoCustomer.text")); // NOI18N
        rdoCustomer.setName("rdoCustomer"); // NOI18N
        rdoCustomer.addActionListener(radioCustomerListener);

        buttonGroup1.add(rdoAll);
        rdoAll.setFont(resourceMap.getFont("btnSearch.font")); // NOI18N
        rdoAll.setSelected(true);
        rdoAll.setText(resourceMap.getString("rdoAll.text")); // NOI18N
        rdoAll.setName("rdoAll"); // NOI18N
        rdoAll.addActionListener(radioAllListener);

        busyIcon.setText(resourceMap.getString("busyIcon.text")); // NOI18N
        busyIcon.setName("busyIcon"); // NOI18N

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(rdoAgent)
                .addGap(2, 2, 2)
                .addComponent(rdoCustomer)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rdoAll)
                .addContainerGap(9, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 78, Short.MAX_VALUE)
                .addComponent(busyIcon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnSearch))
            .addComponent(dtTo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jLabel6)
                .addContainerGap())
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jLabel7)
                .addContainerGap())
            .addComponent(dtFrom, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
            .addComponent(cmbContactable, 0, 192, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdoAgent)
                    .addComponent(rdoCustomer)
                    .addComponent(rdoAll))
                .addGap(9, 9, 9)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(busyIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbContactable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(jLabel6)
                .addGap(4, 4, 4)
                .addComponent(dtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel7)
                .addGap(3, 3, 3)
                .addComponent(dtTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSearch)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanel1.add(jPanel8, gridBagConstraints);

        splitPanelMaster.setLeftComponent(jPanel1);

        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new java.awt.GridBagLayout());

        JScrollPane.setName("JScrollPane"); // NOI18N

        tblInvoice.setBackground(resourceMap.getColor("tblInvoice.background")); // NOI18N
        tblInvoice.setForeground(resourceMap.getColor("tblInvoice.foreground")); // NOI18N
        tblInvoice.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "GDSPNR", "Career", "LeadPax", "InvRef", "InvDate", "Terms", "InvFor", "InvBy", "InvAmount", "CrNote", "Received", "Outstanding"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, true, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblInvoice.setFont(resourceMap.getFont("tblInvoice.font")); // NOI18N
        tblInvoice.setName("tblInvoice"); // NOI18N
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
        tblInvoice.getColumnModel().getColumn(11).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title11")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.8;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(JScrollPane, gridBagConstraints);

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

        lblTotal.setForeground(resourceMap.getColor("lblTotal.foreground")); // NOI18N
        lblTotal.setText(resourceMap.getString("lblTotal.text")); // NOI18N
        lblTotal.setName("lblTotal"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pagePanel.add(lblTotal, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 4, 2);
        jPanel2.add(pagePanel, gridBagConstraints);

        summeryPanel.setBackground(resourceMap.getColor("summeryPanel.background")); // NOI18N
        summeryPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        summeryPanel.setForeground(resourceMap.getColor("summeryPanel.foreground")); // NOI18N
        summeryPanel.setName("summeryPanel"); // NOI18N
        summeryPanel.setLayout(new java.awt.GridBagLayout());

        lblTotalCredit.setFont(resourceMap.getFont("lblTotalCredit.font")); // NOI18N
        lblTotalCredit.setForeground(resourceMap.getColor("lblTotalCredit.foreground")); // NOI18N
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
        lbl4.setForeground(resourceMap.getColor("lbl3.foreground")); // NOI18N
        lbl4.setText(resourceMap.getString("lbl4.text")); // NOI18N
        lbl4.setName("lbl4"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 45;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        summeryPanel.add(lbl4, gridBagConstraints);

        lblTotalOutstanding.setFont(resourceMap.getFont("lblTotalCredit.font")); // NOI18N
        lblTotalOutstanding.setForeground(resourceMap.getColor("lblTotalCredit.foreground")); // NOI18N
        lblTotalOutstanding.setText(resourceMap.getString("lblTotalOutstanding.text")); // NOI18N
        lblTotalOutstanding.setName("lblTotalOutstanding"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        summeryPanel.add(lblTotalOutstanding, gridBagConstraints);

        lblTotalPaid.setFont(resourceMap.getFont("lblTotalCredit.font")); // NOI18N
        lblTotalPaid.setForeground(resourceMap.getColor("lblTotalCredit.foreground")); // NOI18N
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
        lbl3.setForeground(resourceMap.getColor("lbl3.foreground")); // NOI18N
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
        lbl2.setForeground(resourceMap.getColor("lbl3.foreground")); // NOI18N
        lbl2.setText(resourceMap.getString("lbl2.text")); // NOI18N
        lbl2.setName("lbl2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        summeryPanel.add(lbl2, gridBagConstraints);

        lblTotalInvAmount.setFont(resourceMap.getFont("lblTotalCredit.font")); // NOI18N
        lblTotalInvAmount.setForeground(resourceMap.getColor("lblTotalCredit.foreground")); // NOI18N
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
        lbl5.setForeground(resourceMap.getColor("lbl3.foreground")); // NOI18N
        lbl5.setText(resourceMap.getString("lbl5.text")); // NOI18N
        lbl5.setName("lbl5"); // NOI18N
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
        gridBagConstraints.gridy = 1;
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
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        getContentPane().add(jPanel4, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmbContactableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbContactableActionPerformed
}//GEN-LAST:event_cmbContactableActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        new Thread(new threadOutstandingRefundReport()).start();
}//GEN-LAST:event_btnSearchActionPerformed

    private void btnViewReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewReportActionPerformed
       acDocBo.setAcDocType("Outstanding Refund Report");        
        BackofficeReporting rptAcDoc = new BackofficeReporting();
        List<Object> invObject = new ArrayList();
        acDocBo.setmAgent(AuthenticationBo.getmAgent());
        acDocBo.setAcDocsRpt(this.outstandingCNotes);
        acDocBo.setAgent(this.agent);
        acDocBo.setCustomer(this.customer);
        invObject.add(acDocBo);
        acDocBo.reportSummery();
        rptAcDoc.viewAcDocReport(invObject);
    }//GEN-LAST:event_btnViewReportActionPerformed

    private void btnEmailReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmailReportActionPerformed
        acDocBo.setAcDocType("Outstanding Refund Report");             
        acDocBo.setmAgent(AuthenticationBo.getmAgent());
        acDocBo.setAcDocsRpt(this.outstandingCNotes);
        acDocBo.setAgent(this.agent);
        acDocBo.setCustomer(this.customer);        
        acDocBo.reportSummery();
        BackofficeReporting rptAcDoc = new BackofficeReporting();
        List<Object> invObject = new ArrayList();
        invObject.add(acDocBo);
        
        String emailAddress = this.agent.getEmail();
        if(!emailAddress.isEmpty()){
        String subject = "Outstanding Invoice Report from : " + AuthenticationBo.getmAgent().getName();
        String body = "Outstanding Invoices from " + AuthenticationBo.getmAgent().getName();
        rptAcDoc.emailAcDocReport(emailAddress,subject, body,invObject);
        }else{
         JOptionPane.showMessageDialog(null, "No email address found \n Please add email address in clients profile.", "Email Report", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnEmailReportActionPerformed

    private void btnPrintReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintReportActionPerformed
         acDocBo.setAcDocType("Outstanding Refund Report");     
        BackofficeReporting rptAcDoc = new BackofficeReporting();
        List<Object> invObject = new ArrayList();
        acDocBo.setmAgent(AuthenticationBo.getmAgent());
        acDocBo.setAcDocsRpt(this.outstandingCNotes);
        acDocBo.setAgent(this.agent);
        acDocBo.setCustomer(this.customer);
        invObject.add(acDocBo);
        acDocBo.reportSummery();
        rptAcDoc.printAcDocReport(invObject);
    }//GEN-LAST:event_btnPrintReportActionPerformed

    /**
     * @param args the command line arguments
     */
   /* public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new FrameOutstandingSCNote().setVisible(true);
            }
        });
    }*/   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane JScrollPane;
    private javax.swing.JButton btnEmailReport;
    private javax.swing.JButton btnPrintReport;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnViewReport;
    private org.jdesktop.swingx.JXBusyLabel busyIcon;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox cmbContactable;
    private org.jdesktop.swingx.JXDatePicker dtFrom;
    private org.jdesktop.swingx.JXDatePicker dtTo;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
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
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblTotalCredit;
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
            acDocBo.findAcDocById(acDocId);
            acDoc = acDocBo.getAccountingDocument();

            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            progressBar.repaint(); //Refresh graphics
        }
    }

    private class threadOutstandingRefundReport implements Runnable {

        public threadOutstandingRefundReport() {
        }

        public void run() {
            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();

            statusMessageLabel.setText("Loading Invoice...");
            btnSearch.setEnabled(false);
            searchOutstandingRefund();
            btnSearch.setEnabled(true);
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            progressBar.repaint(); //Refresh graphics
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
            btnSearch.setEnabled(false);
            searchOutstandingRefund();
            btnSearch.setEnabled(true);
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            progressBar.repaint(); //Refresh graphics
        }
    }
}
