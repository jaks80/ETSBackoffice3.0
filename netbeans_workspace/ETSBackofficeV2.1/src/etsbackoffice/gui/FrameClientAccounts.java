
/*
 * FrameOutstandingInvoice.java
 *
 * Created on 18-Oct-2010, 23:15:52
 */
package etsbackoffice.gui;

import etsbackoffice.ETSBackofficeApp;
import etsbackoffice.businesslogic.*;
import etsbackoffice.domain.*;
import etsbackoffice.report.ClientAcStatementReport;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 *
 * @author Yusuf
 */
public class FrameClientAccounts extends javax.swing.JFrame{

    private DateFormat df = new DateFormat();
    private AccountingDocBo acDocBo = (AccountingDocBo) ETSBackofficeApp.getApplication().ctx.getBean("acDocBo");
    private AccountsBo accountsBo = (AccountsBo) ETSBackofficeApp.getApplication().ctx.getBean("accountsBo");
    private AgentBo agentBo = (AgentBo) ETSBackofficeApp.getApplication().ctx.getBean("agentBo");
    private CustomerBo customerBo = (CustomerBo) ETSBackofficeApp.getApplication().ctx.getBean("customerBo");
    private AccountingDocument acDoc = new AccountingDocument();
    private List<Agent> agents = new ArrayList();
    private List<Customer> customers = new ArrayList();
    private List<Accounts> statementLines = new ArrayList<Accounts>();
    private DefaultTableModel stmtModel;
    DefaultComboBoxModel cmbCustModel = new DefaultComboBoxModel();
    DefaultComboBoxModel cmbAgtModel = new DefaultComboBoxModel();
    private Agent agent;
    private Customer customer;
    private Long contId;
    private Long agtId = null;
    private Long custId = null;
    private int acDocType = 0;
    private int transType = 0;
    private Date from = null;
    private Date to = null;
    private BigDecimal openingBalance = new BigDecimal("0.00");
    private BigDecimal closingBalance = new BigDecimal("0.00");
    BigDecimal totalInvAmount = new BigDecimal("0.00");
    BigDecimal totalCNoteAmount = new BigDecimal("0.00");
    BigDecimal moneyIn = new BigDecimal("0.00");
    BigDecimal moneyOut = new BigDecimal("0.00");      
    /** Creates new form FrameOutstandingInvoice */
    public FrameClientAccounts(java.awt.Frame parent) {
        initComponents();
        busyIcon.setVisible(false);        
    }

    private void searchAcDocByCriteria() {

        String[] data;        

        acDocType = 0;
        transType = 0;
        from = null;
        to = null;

        if (cmbContactable.getSelectedIndex() > 0) {
            data = cmbContactable.getSelectedItem().toString().split("-");
            if (rdoAgent.isSelected()) {
                agtId = Long.parseLong(data[2]);
                contId = agtId;
            } else if (rdoCustomer.isSelected()) {
                custId = Long.parseLong(data[2]);
                contId = custId;
            }
        }

        from = dtFrom.getDate();
        to = dtTo.getDate();

        if (rdoInvoice.isSelected()) {
            acDocType = 1;
        } else if (rdoCNote.isSelected()) {
            acDocType = 2;
        } else {
            acDocType = 0;
        }

        if (rdoMoneyIn.isSelected()) {
            transType = 1;
        } else if (rdoMoneyOut.isSelected()) {
            transType = 2;
        }
        if (custId != null || agtId != null) {
            new Thread(new threadStatementLoaderByCriteria()).start();
            //new Thread(new threadCheckAcBalance(contId)).start();
        } else {
            JOptionPane.showMessageDialog(null, "Select client to view statement", "Client Statement", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void searchAcDoc() {
        String acDocRef = txtAcDocRef.getText().trim();
        String gdsPnr = txtGdsPnr.getText().trim();

        /*  if (!acDocRef.isEmpty()) {
        this.invoices = acDocBo.findCompleteAcDocByRefNo(Integer.valueOf(acDocRef));
        } else if (!gdsPnr.isEmpty()) {
        this.invoices = acDocBo.findAcDocByGdsPnr(gdsPnr);
        }
        if (this.invoices.size() > 0) {
        populateTblInvoice(this.invoices);
        }*/
    }

    private void populateTxtAgentDetails(Agent agent) {
        txtContactableDetails.setText(agent.getFullAddressCRSeperated());
    }

    private void populateTxtCustomerDetails(Customer customer) {
        txtContactableDetails.setText(customer.getFullAddressCRSeperated());
    }

    private void populateTblStatement(List<Accounts> stmtLines) {
        int noOfInvoice = 0;
        int noOfCrn = 0;
        totalInvAmount = new BigDecimal("0.00");
        totalCNoteAmount = new BigDecimal("0.00");
        moneyIn = new BigDecimal("0.00");
        moneyOut = new BigDecimal("0.00");  
        
        lblOpeningBalance.setText(this.openingBalance.toString());
        int row = 0;
        stmtModel = (DefaultTableModel) tblStatement.getModel();
        stmtModel.getDataVector().removeAllElements();
        tblStatement.repaint();
       
        BigDecimal balance = this.openingBalance;          
        stmtModel.insertRow(row, new Object[]{null, null, "Opening Balance", null, null, balance});
        row++;

        for (Accounts stmt : this.statementLines) {            

            if (stmt.getcInvAmount() != null) {
                balance = balance.subtract(stmt.getcInvAmount());
                if (stmt.getcInvAmount().signum() == 1) {
                    totalInvAmount = totalInvAmount.add(stmt.getcInvAmount());
                    noOfInvoice++;
                } else {
                    totalCNoteAmount = totalCNoteAmount.add(stmt.getcInvAmount());
                    noOfCrn++;
                }
            } else if (stmt.getcTransAmount() != null) {
                balance = balance.add(stmt.getcTransAmount());
                if (stmt.getcTransAmount().signum() == -1) {
                    moneyOut = moneyOut.add(stmt.getAcTransaction().getTransAmount());
                } else {
                    if (stmt.getcTransAmount().signum() == 1) {
                        moneyIn = moneyIn.add(stmt.getAcTransaction().getTransAmount());
                    }
                }
            }

            stmtModel.insertRow(row, new Object[]{df.dateForGui(stmt.getTransDate()), stmt.getTransType(), stmt.getLineDdesc(), stmt.getcInvAmount(), stmt.getcTransAmount(), balance});
            row++;
        }

        stmtModel.insertRow(row, new Object[]{null, null, "Closing Balance", null, null, balance});

        this.closingBalance = balance;
        lblClosingBalance.setText(closingBalance.toString());
        lblTInvAmount.setText(totalInvAmount.toString()+ " @ "+String.valueOf(noOfInvoice));
        lblTReceive.setText(moneyIn.toString());
        lblTPayment.setText(moneyOut.toString());
        lblTCNoteAmount.setText(totalCNoteAmount.toString());
    }
    private ActionListener radioAgentListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            busyIcon.setVisible(true);
            busyIcon.setBusy(true);
            List cmbElement = new ArrayList();
            agents = agentBo.loadAll();
            for (Agent agent : agents) {
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
            customers = customerBo.loadAll();

            for (Customer customer : customers) {
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
    private ActionListener cmbContactableListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            String[] data;
            long id = 0;
            if (cmbContactable.getSelectedIndex() > 0) {
                data = cmbContactable.getSelectedItem().toString().split("-");
                id = Long.parseLong(data[2]);
            }

            if (rdoAgent.isSelected()) {
                loop:
                for (Agent a : agents) {
                    if (a.getContactableId() == id) {
                        agent = a;
                        customer = null;
                        populateTxtAgentDetails(agent);
                        break loop;
                    }
                }
            } else if (rdoCustomer.isSelected()) {
                loop:
                for (Customer c : customers) {
                    if (c.getContactableId() == id) {
                        customer = c;
                        agent = null;
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
        btnViewReport = new javax.swing.JButton();
        btnEmailReport = new javax.swing.JButton();
        btnPrintReport = new javax.swing.JButton();
        splitPanelMaster = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
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
        busyIcon = new org.jdesktop.swingx.JXBusyLabel();
        rdoInvoice = new javax.swing.JRadioButton();
        rdoCNote = new javax.swing.JRadioButton();
        rdoInCNote = new javax.swing.JRadioButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtAcDocRef = new javax.swing.JTextField();
        txtGdsPnr = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        rdoMoneyIn = new javax.swing.JRadioButton();
        rdoMoneyOut = new javax.swing.JRadioButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtHelpTips = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        JScrollPane = new javax.swing.JScrollPane();
        tblStatement = new JXTable(){public Component prepareRenderer(TableCellRenderer renderer,
            int rowIndex, int vColIndex) {
            Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);  
            String s = "";       

            Object o = tblStatement.getModel().getValueAt(rowIndex, 1);               
            if(o!=null){
                s = o.toString();        
            }
            if(s.equalsIgnoreCase("INVOICE") || s.equalsIgnoreCase("RECEIVED")){
                c.setForeground(Color.green);
            }else if(s.equalsIgnoreCase("CREDIT NOTE") || s.equalsIgnoreCase("PAID")){
                c.setForeground(Color.red);
            }else{
                c.setForeground(Color.WHITE);
            }
            return c;
        }
    };
    summeryPanel = new javax.swing.JPanel();
    lblBalance = new javax.swing.JLabel();
    lblTReceive = new javax.swing.JLabel();
    lblTPayment = new javax.swing.JLabel();
    lblTAmount = new javax.swing.JLabel();
    lblTotalAmount = new javax.swing.JLabel();
    lblTInvAmount = new javax.swing.JLabel();
    jLabel4 = new javax.swing.JLabel();
    lblTCNoteAmount = new javax.swing.JLabel();
    jLabel8 = new javax.swing.JLabel();
    jLabel9 = new javax.swing.JLabel();
    lblOpeningBalance = new javax.swing.JLabel();
    lblClosingBalance = new javax.swing.JLabel();
    jPanel5 = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    txtContactableDetails = new javax.swing.JTextArea();
    jPanel4 = new javax.swing.JPanel();
    progressBar = new javax.swing.JProgressBar();
    statusMessageLabel = new javax.swing.JLabel();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getResourceMap(FrameClientAccounts.class);
    setTitle(resourceMap.getString("Form.title")); // NOI18N
    setName("Form"); // NOI18N
    getContentPane().setLayout(new java.awt.GridBagLayout());

    jToolBar1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    jToolBar1.setRollover(true);
    jToolBar1.setName("jToolBar1"); // NOI18N

    jButton1.setFont(resourceMap.getFont("btnPrintReport.font")); // NOI18N
    jButton1.setIcon(resourceMap.getIcon("jButton1.icon")); // NOI18N
    jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
    jButton1.setFocusable(false);
    jButton1.setName("jButton1"); // NOI18N
    jToolBar1.add(jButton1);

    btnViewInvoice.setFont(resourceMap.getFont("btnPrintReport.font")); // NOI18N
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

    btnViewReport.setFont(resourceMap.getFont("btnViewReport.font")); // NOI18N
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

    btnEmailReport.setFont(resourceMap.getFont("btnPrintReport.font")); // NOI18N
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
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 0.1;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
    getContentPane().add(jToolBar1, gridBagConstraints);

    splitPanelMaster.setBorder(null);
    splitPanelMaster.setDividerLocation(229);
    splitPanelMaster.setDividerSize(4);
    splitPanelMaster.setName("splitPanelMaster"); // NOI18N

    jPanel1.setName("jPanel1"); // NOI18N
    jPanel1.setLayout(new java.awt.GridBagLayout());

    jPanel8.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
    jPanel8.setName("jPanel8"); // NOI18N
    jPanel8.setLayout(new java.awt.GridBagLayout());

    jLabel5.setFont(resourceMap.getFont("rdoInvoice.font")); // NOI18N
    jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
    jLabel5.setName("jLabel5"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
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
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
    jPanel8.add(cmbContactable, gridBagConstraints);

    dtFrom.setFont(resourceMap.getFont("txtAcDocRef.font")); // NOI18N
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

    dtTo.setFont(resourceMap.getFont("txtAcDocRef.font")); // NOI18N
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

    jLabel6.setFont(resourceMap.getFont("rdoInvoice.font")); // NOI18N
    jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
    jLabel6.setName("jLabel6"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
    jPanel8.add(jLabel6, gridBagConstraints);

    jLabel7.setFont(resourceMap.getFont("rdoInvoice.font")); // NOI18N
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
    gridBagConstraints.gridy = 9;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.ipadx = 20;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
    gridBagConstraints.insets = new java.awt.Insets(8, 2, 2, 2);
    jPanel8.add(btnCriteriaSearch, gridBagConstraints);

    buttonGroup1.add(rdoAgent);
    rdoAgent.setFont(resourceMap.getFont("rdoCustomer.font")); // NOI18N
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
    rdoCustomer.setFont(resourceMap.getFont("rdoCustomer.font")); // NOI18N
    rdoCustomer.setText(resourceMap.getString("rdoCustomer.text")); // NOI18N
    rdoCustomer.setName("rdoCustomer"); // NOI18N
    rdoCustomer.addActionListener(radioCustomerListener);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(8, 2, 2, 2);
    jPanel8.add(rdoCustomer, gridBagConstraints);

    busyIcon.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    busyIcon.setText(resourceMap.getString("busyIcon.text")); // NOI18N
    busyIcon.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
    busyIcon.setName("busyIcon"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
    jPanel8.add(busyIcon, gridBagConstraints);

    buttonGroup2.add(rdoInvoice);
    rdoInvoice.setFont(resourceMap.getFont("rdoInvoice.font")); // NOI18N
    rdoInvoice.setText(resourceMap.getString("rdoInvoice.text")); // NOI18N
    rdoInvoice.setName("rdoInvoice"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(8, 2, 2, 2);
    jPanel8.add(rdoInvoice, gridBagConstraints);

    buttonGroup2.add(rdoCNote);
    rdoCNote.setFont(resourceMap.getFont("rdoInvoice.font")); // NOI18N
    rdoCNote.setText(resourceMap.getString("rdoCNote.text")); // NOI18N
    rdoCNote.setName("rdoCNote"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(8, 0, 0, 0);
    jPanel8.add(rdoCNote, gridBagConstraints);

    buttonGroup2.add(rdoInCNote);
    rdoInCNote.setFont(resourceMap.getFont("rdoInvoice.font")); // NOI18N
    rdoInCNote.setSelected(true);
    rdoInCNote.setText(resourceMap.getString("rdoInCNote.text")); // NOI18N
    rdoInCNote.setName("rdoInCNote"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel8.add(rdoInCNote, gridBagConstraints);

    jLabel2.setFont(resourceMap.getFont("jLabel2.font")); // NOI18N
    jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
    jLabel2.setName("jLabel2"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 11;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
    jPanel8.add(jLabel2, gridBagConstraints);

    jLabel3.setFont(resourceMap.getFont("jLabel2.font")); // NOI18N
    jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
    jLabel3.setName("jLabel3"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 14;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
    jPanel8.add(jLabel3, gridBagConstraints);

    txtAcDocRef.setFont(resourceMap.getFont("txtAcDocRef.font")); // NOI18N
    txtAcDocRef.setText(resourceMap.getString("txtAcDocRef.text")); // NOI18N
    txtAcDocRef.setName("txtAcDocRef"); // NOI18N
    txtAcDocRef.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusGained(java.awt.event.FocusEvent evt) {
            txtAcDocRefFocusGained(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 12;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel8.add(txtAcDocRef, gridBagConstraints);

    txtGdsPnr.setFont(resourceMap.getFont("txtAcDocRef.font")); // NOI18N
    txtGdsPnr.setText(resourceMap.getString("txtGdsPnr.text")); // NOI18N
    txtGdsPnr.setName("txtGdsPnr"); // NOI18N
    txtGdsPnr.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusGained(java.awt.event.FocusEvent evt) {
            txtGdsPnrFocusGained(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 15;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel8.add(txtGdsPnr, gridBagConstraints);

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
    gridBagConstraints.gridy = 16;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.ipadx = 20;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
    jPanel8.add(btnSearch, gridBagConstraints);

    jSeparator1.setName("jSeparator1"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 10;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipady = 4;
    gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
    jPanel8.add(jSeparator1, gridBagConstraints);

    rdoMoneyIn.setFont(resourceMap.getFont("rdoMoneyIn.font")); // NOI18N
    rdoMoneyIn.setText(resourceMap.getString("rdoMoneyIn.text")); // NOI18N
    rdoMoneyIn.setName("rdoMoneyIn"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel8.add(rdoMoneyIn, gridBagConstraints);

    rdoMoneyOut.setFont(resourceMap.getFont("rdoMoneyIn.font")); // NOI18N
    rdoMoneyOut.setText(resourceMap.getString("rdoMoneyOut.text")); // NOI18N
    rdoMoneyOut.setName("rdoMoneyOut"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel8.add(rdoMoneyOut, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
    jPanel1.add(jPanel8, gridBagConstraints);

    jScrollPane2.setName("jScrollPane2"); // NOI18N

    txtHelpTips.setColumns(20);
    txtHelpTips.setRows(5);
    txtHelpTips.setName("txtHelpTips"); // NOI18N
    jScrollPane2.setViewportView(txtHelpTips);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
    jPanel1.add(jScrollPane2, gridBagConstraints);

    splitPanelMaster.setLeftComponent(jPanel1);

    jPanel2.setName("jPanel2"); // NOI18N
    jPanel2.setLayout(new java.awt.GridBagLayout());

    JScrollPane.setName("JScrollPane"); // NOI18N

    tblStatement.setBackground(resourceMap.getColor("tblStatement.background")); // NOI18N
    tblStatement.setForeground(resourceMap.getColor("tblStatement.foreground")); // NOI18N
    tblStatement.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {

        },
        new String [] {
            "Date", "Transaction Type", "", "Invoice", "Payment", "Balance"
        }
    ) {
        boolean[] canEdit = new boolean [] {
            false, false, false, false, false, false
        };

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit [columnIndex];
        }
    });
    tblStatement.setFont(resourceMap.getFont("tblStatement.font")); // NOI18N
    tblStatement.setName("tblStatement"); // NOI18N
    tblStatement.setSortable(false);
    tblStatement.getTableHeader().setReorderingAllowed(false);
    JScrollPane.setViewportView(tblStatement);
    tblStatement.getColumnModel().getColumn(0).setMinWidth(80);
    tblStatement.getColumnModel().getColumn(0).setPreferredWidth(80);
    tblStatement.getColumnModel().getColumn(0).setMaxWidth(120);
    tblStatement.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblStatement.columnModel.title0")); // NOI18N
    tblStatement.getColumnModel().getColumn(1).setMinWidth(100);
    tblStatement.getColumnModel().getColumn(1).setPreferredWidth(110);
    tblStatement.getColumnModel().getColumn(1).setMaxWidth(130);
    tblStatement.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblStatement.columnModel.title1")); // NOI18N
    tblStatement.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblStatementLine.columnModel.title5")); // NOI18N
    tblStatement.getColumnModel().getColumn(3).setMinWidth(100);
    tblStatement.getColumnModel().getColumn(3).setPreferredWidth(120);
    tblStatement.getColumnModel().getColumn(3).setMaxWidth(150);
    tblStatement.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblStatementLine.columnModel.title2")); // NOI18N
    tblStatement.getColumnModel().getColumn(4).setMinWidth(100);
    tblStatement.getColumnModel().getColumn(4).setPreferredWidth(120);
    tblStatement.getColumnModel().getColumn(4).setMaxWidth(150);
    tblStatement.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("tblStatementLine.columnModel.title3")); // NOI18N
    tblStatement.getColumnModel().getColumn(5).setMinWidth(100);
    tblStatement.getColumnModel().getColumn(5).setPreferredWidth(120);
    tblStatement.getColumnModel().getColumn(5).setMaxWidth(150);
    tblStatement.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("tblStatementLine.columnModel.title4")); // NOI18N

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel2.add(JScrollPane, gridBagConstraints);

    summeryPanel.setBackground(resourceMap.getColor("summeryPanel.background")); // NOI18N
    summeryPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
    summeryPanel.setForeground(resourceMap.getColor("summeryPanel.foreground")); // NOI18N
    summeryPanel.setName("summeryPanel"); // NOI18N
    summeryPanel.setLayout(new java.awt.GridBagLayout());

    lblBalance.setFont(resourceMap.getFont("lblBalance.font")); // NOI18N
    lblBalance.setForeground(resourceMap.getColor("lblBalance.foreground")); // NOI18N
    lblBalance.setText(resourceMap.getString("lblBalance.text")); // NOI18N
    lblBalance.setName("lblBalance"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    summeryPanel.add(lblBalance, gridBagConstraints);

    lblTReceive.setFont(resourceMap.getFont("lblTReceive.font")); // NOI18N
    lblTReceive.setForeground(resourceMap.getColor("lblTReceive.foreground")); // NOI18N
    lblTReceive.setText(resourceMap.getString("lblTReceive.text")); // NOI18N
    lblTReceive.setMinimumSize(new java.awt.Dimension(100, 14));
    lblTReceive.setName("lblTReceive"); // NOI18N
    lblTReceive.setPreferredSize(new java.awt.Dimension(150, 14));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    summeryPanel.add(lblTReceive, gridBagConstraints);

    lblTPayment.setFont(resourceMap.getFont("lblTPayment.font")); // NOI18N
    lblTPayment.setForeground(resourceMap.getColor("lblTPayment.foreground")); // NOI18N
    lblTPayment.setText(resourceMap.getString("lblTPayment.text")); // NOI18N
    lblTPayment.setMinimumSize(new java.awt.Dimension(100, 14));
    lblTPayment.setName("lblTPayment"); // NOI18N
    lblTPayment.setPreferredSize(new java.awt.Dimension(150, 14));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    summeryPanel.add(lblTPayment, gridBagConstraints);

    lblTAmount.setFont(resourceMap.getFont("lblBalance.font")); // NOI18N
    lblTAmount.setForeground(resourceMap.getColor("lblBalance.foreground")); // NOI18N
    lblTAmount.setText(resourceMap.getString("lblTAmount.text")); // NOI18N
    lblTAmount.setName("lblTAmount"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    summeryPanel.add(lblTAmount, gridBagConstraints);

    lblTotalAmount.setFont(resourceMap.getFont("lblBalance.font")); // NOI18N
    lblTotalAmount.setForeground(resourceMap.getColor("lblBalance.foreground")); // NOI18N
    lblTotalAmount.setText(resourceMap.getString("lblTotalAmount.text")); // NOI18N
    lblTotalAmount.setName("lblTotalAmount"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    summeryPanel.add(lblTotalAmount, gridBagConstraints);

    lblTInvAmount.setFont(resourceMap.getFont("lblTInvAmount.font")); // NOI18N
    lblTInvAmount.setForeground(resourceMap.getColor("lblTInvAmount.foreground")); // NOI18N
    lblTInvAmount.setText(resourceMap.getString("lblTInvAmount.text")); // NOI18N
    lblTInvAmount.setMinimumSize(new java.awt.Dimension(100, 14));
    lblTInvAmount.setName("lblTInvAmount"); // NOI18N
    lblTInvAmount.setPreferredSize(new java.awt.Dimension(150, 14));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    summeryPanel.add(lblTInvAmount, gridBagConstraints);

    jLabel4.setFont(resourceMap.getFont("jLabel4.font")); // NOI18N
    jLabel4.setForeground(resourceMap.getColor("jLabel4.foreground")); // NOI18N
    jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
    jLabel4.setName("jLabel4"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    summeryPanel.add(jLabel4, gridBagConstraints);

    lblTCNoteAmount.setFont(resourceMap.getFont("lblTCNoteAmount.font")); // NOI18N
    lblTCNoteAmount.setForeground(resourceMap.getColor("lblTCNoteAmount.foreground")); // NOI18N
    lblTCNoteAmount.setText(resourceMap.getString("lblTCNoteAmount.text")); // NOI18N
    lblTCNoteAmount.setMinimumSize(new java.awt.Dimension(100, 14));
    lblTCNoteAmount.setName("lblTCNoteAmount"); // NOI18N
    lblTCNoteAmount.setPreferredSize(new java.awt.Dimension(150, 14));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    summeryPanel.add(lblTCNoteAmount, gridBagConstraints);

    jLabel8.setFont(resourceMap.getFont("jLabel9.font")); // NOI18N
    jLabel8.setForeground(resourceMap.getColor("jLabel8.foreground")); // NOI18N
    jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
    jLabel8.setName("jLabel8"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    summeryPanel.add(jLabel8, gridBagConstraints);

    jLabel9.setFont(resourceMap.getFont("jLabel9.font")); // NOI18N
    jLabel9.setForeground(resourceMap.getColor("jLabel9.foreground")); // NOI18N
    jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
    jLabel9.setName("jLabel9"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    summeryPanel.add(jLabel9, gridBagConstraints);

    lblOpeningBalance.setFont(resourceMap.getFont("jLabel9.font")); // NOI18N
    lblOpeningBalance.setForeground(resourceMap.getColor("lblOpeningBalance.foreground")); // NOI18N
    lblOpeningBalance.setText(resourceMap.getString("lblOpeningBalance.text")); // NOI18N
    lblOpeningBalance.setName("lblOpeningBalance"); // NOI18N
    lblOpeningBalance.setPreferredSize(new java.awt.Dimension(150, 14));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    summeryPanel.add(lblOpeningBalance, gridBagConstraints);

    lblClosingBalance.setFont(resourceMap.getFont("jLabel9.font")); // NOI18N
    lblClosingBalance.setForeground(resourceMap.getColor("lblClosingBalance.foreground")); // NOI18N
    lblClosingBalance.setText(resourceMap.getString("lblClosingBalance.text")); // NOI18N
    lblClosingBalance.setName("lblClosingBalance"); // NOI18N
    lblClosingBalance.setPreferredSize(new java.awt.Dimension(150, 14));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    summeryPanel.add(lblClosingBalance, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
    gridBagConstraints.weightx = 0.7;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel2.add(summeryPanel, gridBagConstraints);

    jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
    jPanel5.setName("jPanel5"); // NOI18N
    jPanel5.setLayout(new java.awt.CardLayout(2, 2));

    jScrollPane1.setName("jScrollPane1"); // NOI18N

    txtContactableDetails.setColumns(16);
    txtContactableDetails.setEditable(false);
    txtContactableDetails.setFont(resourceMap.getFont("txtContactableDetails.font")); // NOI18N
    txtContactableDetails.setLineWrap(true);
    txtContactableDetails.setRows(5);
    txtContactableDetails.setName("txtContactableDetails"); // NOI18N
    jScrollPane1.setViewportView(txtContactableDetails);

    jPanel5.add(jScrollPane1, "card2");

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 0.3;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 4);
    jPanel2.add(jPanel5, gridBagConstraints);

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
        searchAcDocByCriteria();
    }//GEN-LAST:event_btnCriteriaSearchActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        String invRef = txtAcDocRef.getText().trim().replaceAll("[^0-9]", "");
        String gdsPnr = txtGdsPnr.getText().trim();
        Integer ref = null;

        if (!invRef.isEmpty() || !gdsPnr.isEmpty()) {
            if (!invRef.isEmpty()) {
                ref = Integer.valueOf(invRef);
            }

            new Thread(new threadStatementLoaderByObjectRef(ref, gdsPnr)).start();
        } else {
            JOptionPane.showMessageDialog(null, "Enter Invoice Refference or PNR ", "Client Statement", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnSearchActionPerformed

    private void txtAcDocRefFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAcDocRefFocusGained
        txtGdsPnr.setText("");
    }//GEN-LAST:event_txtAcDocRefFocusGained

    private void txtGdsPnrFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtGdsPnrFocusGained
        txtAcDocRef.setText("");
    }//GEN-LAST:event_txtGdsPnrFocusGained

    private void btnViewReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewReportActionPerformed
        ClientAcStatementReport cstmtRpt = new ClientAcStatementReport(this.statementLines,this.agent,this.customer);
        cstmtRpt.setOpeningBalance(openingBalance);
        cstmtRpt.setClosingBalance(closingBalance);
        cstmtRpt.setTotalInvAmount(this.totalInvAmount);
        cstmtRpt.setTotalCNoteAmount(this.totalCNoteAmount);
        cstmtRpt.setMoneyIn(moneyIn);
        cstmtRpt.setMoneyOut(moneyOut);
        cstmtRpt.setFrom(from);
        cstmtRpt.setTo(to);
        cstmtRpt.viewClientAcStatementReport();
    }//GEN-LAST:event_btnViewReportActionPerformed

    private void btnEmailReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmailReportActionPerformed
        ClientAcStatementReport cstmtRpt = new ClientAcStatementReport(this.statementLines,this.agent,this.customer);
        cstmtRpt.setOpeningBalance(openingBalance);
        cstmtRpt.setClosingBalance(closingBalance);
        cstmtRpt.setTotalInvAmount(this.totalInvAmount);
        cstmtRpt.setTotalCNoteAmount(this.totalCNoteAmount);
        cstmtRpt.setMoneyIn(moneyIn);
        cstmtRpt.setMoneyOut(moneyOut);
        cstmtRpt.setFrom(from);
        cstmtRpt.setTo(to);
        cstmtRpt.emailClientAcStatementReport();
    }//GEN-LAST:event_btnEmailReportActionPerformed

    private void btnPrintReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintReportActionPerformed
        ClientAcStatementReport cstmtRpt = new ClientAcStatementReport(this.statementLines, this.agent, this.customer);
        cstmtRpt.setOpeningBalance(openingBalance);
        cstmtRpt.setClosingBalance(closingBalance);
        cstmtRpt.setTotalInvAmount(this.totalInvAmount);
        cstmtRpt.setTotalCNoteAmount(this.totalCNoteAmount);
        cstmtRpt.setMoneyIn(moneyIn);
        cstmtRpt.setMoneyOut(moneyOut);
        cstmtRpt.setFrom(from);
        cstmtRpt.setTo(to);
        cstmtRpt.printClientAcStatementReport();
    }//GEN-LAST:event_btnPrintReportActionPerformed

    private void btnViewInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewInvoiceActionPerformed
        if (tblStatement.getSelectedRow() != -1) {
            viewInvoice();
        } else {
            JOptionPane.showMessageDialog(null, "Select statement line to view invoice", "View Invoice", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnViewInvoiceActionPerformed

    /**
     * @param args the command line arguments
     */
  /*  public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new FrameClientAccounts().setVisible(true);
            }
        });
    }*/
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane JScrollPane;
    private javax.swing.JButton btnCriteriaSearch;
    private javax.swing.JButton btnEmailReport;
    private javax.swing.JButton btnPrintReport;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnViewInvoice;
    private javax.swing.JButton btnViewReport;
    private org.jdesktop.swingx.JXBusyLabel busyIcon;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JComboBox cmbContactable;
    private org.jdesktop.swingx.JXDatePicker dtFrom;
    private org.jdesktop.swingx.JXDatePicker dtTo;
    private javax.swing.JButton jButton1;
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
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblBalance;
    private javax.swing.JLabel lblClosingBalance;
    private javax.swing.JLabel lblOpeningBalance;
    private javax.swing.JLabel lblTAmount;
    private javax.swing.JLabel lblTCNoteAmount;
    private javax.swing.JLabel lblTInvAmount;
    private javax.swing.JLabel lblTPayment;
    private javax.swing.JLabel lblTReceive;
    private javax.swing.JLabel lblTotalAmount;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JRadioButton rdoAgent;
    private javax.swing.JRadioButton rdoCNote;
    private javax.swing.JRadioButton rdoCustomer;
    private javax.swing.JRadioButton rdoInCNote;
    private javax.swing.JRadioButton rdoInvoice;
    private javax.swing.JRadioButton rdoMoneyIn;
    private javax.swing.JRadioButton rdoMoneyOut;
    private javax.swing.JSplitPane splitPanelMaster;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel summeryPanel;
    private org.jdesktop.swingx.JXTable tblStatement;
    private javax.swing.JTextField txtAcDocRef;
    private javax.swing.JTextArea txtContactableDetails;
    private javax.swing.JTextField txtGdsPnr;
    private javax.swing.JTextArea txtHelpTips;
    // End of variables declaration//GEN-END:variables

    public void viewInvoice() {
        int row = tblStatement.getSelectedRow();
        if (row != 0) {
            try {
                row = row - 1;//-1 for next row because row 0 is for opening balance
                long acDocId = 0;
                if (this.statementLines.get(row).getAccountingDocument() != null) {
                    acDocId = this.statementLines.get(row).getAccountingDocument().getAcDocId();
                } else if (this.statementLines.get(row).getAcTransaction() != null) {
                    acDocId = this.statementLines.get(row).getAcTransaction().getAccountingDocument().getAcDocId();
                }
                Thread t = new Thread(new threadLoadCompleteInvoice(acDocId));
                t.start();
                t.join();
                {
                    DlgTInvoice frameInvoice = new DlgTInvoice(this);
                    if (frameInvoice.showDialog(acDoc)) {
                        frameInvoice.dispose();
                    }
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(FrameSAcDocHistory.class.getName()).log(Level.SEVERE, null, ex);
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

            //progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            //progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();

            statusMessageLabel.setText("Loading Invoice...");
            acDocBo.findAcDocById(acDocId);
            acDoc = acDocBo.getAccountingDocument();
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            //progressBar.repaint(); //Refresh graphics
        }
    }

    private class threadStatementLoaderByCriteria implements Runnable {

        public threadStatementLoaderByCriteria() {
        }

        public void run() {
            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();

            statusMessageLabel.setText("Caulculating opening balance...");
            openingBalance = accountsBo.finalClientAcBalancetoDate(from, contId);

            statusMessageLabel.setText("Loading statment...");
            statementLines = accountsBo.searchClientAcStatementByCriteria(contId, acDocType, transType, from, to);

            statusMessageLabel.setText("Populating statement...");
            populateTblStatement(statementLines);

            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");

            btnSearch.setEnabled(true);
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            progressBar.setIndeterminate(false);
            progressBar.repaint(); //Refresh graphics
        }
    }

    private class threadStatementLoaderByObjectRef implements Runnable {

        private Integer invRef = null;
        private String pnr = null;

        public threadStatementLoaderByObjectRef(Integer invRef, String pnr) {
            this.invRef = invRef;
            this.pnr = pnr;
        }

        public void run() {
            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();

            openingBalance = new BigDecimal("0.00");

            statusMessageLabel.setText("Loading statment...");

            if (invRef != null) {
                statementLines = accountsBo.searchAcStatementByInvRef(invRef);
            } else if (pnr != null) {
                statementLines = accountsBo.searchAcStatementByGDSPnr(pnr);
            }

            statusMessageLabel.setText("Populating statement...");
            populateTblStatement(statementLines);

            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");

            btnSearch.setEnabled(true);
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            progressBar.setIndeterminate(false);
            progressBar.repaint(); //Refresh graphics
        }
    }

    public void showFrameClientAcStatement() {
        setLocationRelativeTo(this);
        setVisible(true);
        toFront();
    }
}
