
/*
 * FrameOutstandingInvoice.java
 *
 * Created on 18-Oct-2010, 23:15:52
 */
package etsbackoffice.gui;

import etsbackoffice.ETSBackofficeApp;
import etsbackoffice.businesslogic.AccountingDocBo;
import etsbackoffice.businesslogic.AgentBo;
import etsbackoffice.businesslogic.AuthenticationBo;
import etsbackoffice.businesslogic.CustomerBo;
import etsbackoffice.businesslogic.DateFormat;
import etsbackoffice.businesslogic.DocumentSizeFilter;
import etsbackoffice.domain.AccountingDocument;
import etsbackoffice.domain.Agent;
import etsbackoffice.domain.Customer;
import etsbackoffice.report.BackofficeReporting;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextAttribute;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.AbstractDocument;
import org.jdesktop.application.Action;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 *
 * @author Yusuf
 */
public class FrameSAcDocHistory extends javax.swing.JFrame {
    
    private DateFormat df = new DateFormat();
    AccountingDocBo acDocBo = (AccountingDocBo) ETSBackofficeApp.getApplication().ctx.getBean("acDocBo");
    AgentBo agentBo = (AgentBo) ETSBackofficeApp.getApplication().ctx.getBean("agentBo");
    CustomerBo customerBo = (CustomerBo) ETSBackofficeApp.getApplication().ctx.getBean("customerBo");
    private AccountingDocument acDoc = new AccountingDocument();
    private List<Agent> agents = new ArrayList();
    private List<Customer> customers = new ArrayList();
    private List<AccountingDocument> invoices = new ArrayList();
    private DefaultTableModel invoiceModel;
    DefaultComboBoxModel cmbCustModel = new DefaultComboBoxModel();
    DefaultComboBoxModel cmbAgtModel = new DefaultComboBoxModel();
    private Agent agent;
    private Customer customer;
    private int contactableType = 0;
    private Long contactableId = null;
    private Integer acDocTypeFrom = null;
    private Integer acDocTypeTo = null;
    private Date from = null;
    private Date to = null;
    private Long tktingAgtFrom = null;
    private Long tktingAgtTo = null;

    /** Creates new form FrameOutstandingInvoice */
    public FrameSAcDocHistory(java.awt.Frame parent) {        
        
        initComponents();        
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        busyIcon.setVisible(false);        
        txtAcDocRef.setDocument(new CheckInput(CheckInput.FLOAT));
        
        AbstractDocument doc = (AbstractDocument) txtGdsPnr.getDocument();
        doc.setDocumentFilter(new DocumentSizeFilter(8));        
    }
    
    private void searchAcDocByCriteria() {
        
        String[] data;
        
        if (cmbContactable.getSelectedIndex() > 0) {
            data = cmbContactable.getSelectedItem().toString().split("-");
            contactableId = Long.parseLong(data[2]);
        } else {
            contactableId = null;
        }
        
        from = dtFrom.getDate();
        to = dtTo.getDate();
        
        if (rdoSelf.isSelected()) {
            tktingAgtFrom = Long.valueOf(1);
            tktingAgtTo = Long.valueOf(1);
        } else if (rdoTParty.isSelected()) {
            tktingAgtFrom = Long.valueOf(2);
            tktingAgtTo = null;
        } else if (rdoSelfTParty.isSelected()) {
            tktingAgtFrom = null;
            tktingAgtTo = null;
        }
        
        if (rdoAgent.isSelected()) {
            contactableType = 1;
        } else if (rdoCustomer.isSelected()) {
            contactableType = 2;
        } else if (rdoAll.isSelected()) {
            contactableType = 0;
            contactableId = null;
        }
        
        if (rdoInvoice.isSelected()) {
            acDocTypeFrom = 1;
            acDocTypeTo = 1;
        } else if (rdoCNote.isSelected()) {
            acDocTypeFrom = 2;
            acDocTypeTo = 3;
        } else if (rdoInCNote.isSelected()) {
            acDocTypeFrom = null;
            acDocTypeTo = null;
        }
        
        this.invoices = acDocBo.sAcDocHistoryByCriteria(contactableId, contactableType, acDocTypeFrom, acDocTypeTo, from, to, tktingAgtFrom, tktingAgtTo);
        populateTblInvoice(this.invoices);
    }
    
    private void searchAcDoc() {
        String acDocRef = txtAcDocRef.getText().trim();
        String gdsPnr = txtGdsPnr.getText().trim();
        
        if (!acDocRef.isEmpty()) {
            this.invoices = acDocBo.findCompleteAcDocByRefNo(Integer.valueOf(acDocRef));
        } else if (!gdsPnr.isEmpty()) {
            this.invoices = acDocBo.findAcDocByGdsPnr(gdsPnr);
        }
        if (this.invoices.size() > 0) {
            populateTblInvoice(this.invoices);
        }
    }
    
    private void populateTxtAgentDetails(Agent agent) {
        txtContactableDetails.setText(agent.getFullAddressCRSeperated());
    }
    
    private void populateTxtCustomerDetails(Customer customer) {
        txtContactableDetails.setText(customer.getFullAddressCRSeperated());
    }
    
    private void populateTblInvoice(List<AccountingDocument> outstandingInvoices) {
        
        int row = 0;
        
        invoiceModel = (DefaultTableModel) tblInvoice.getModel();
        invoiceModel.getDataVector().removeAllElements();
        JScrollPane.repaint();
        BigDecimal totalInvAmount = new BigDecimal("0.00");
        BigDecimal totalCRNAmount = new BigDecimal("0.00");
        BigDecimal totalReceived = new BigDecimal("0.00");
        BigDecimal finalBalance = new BigDecimal("0.00");
        int noOfInvoice = 0;
        int noOfOutstandingInvoice = 0;
        int noOfCrn = 0;
        int noOfPax = 0;
        int noOfRfdPax = 0;
        
        for (AccountingDocument a : outstandingInvoices) {
            noOfPax += a.getTotalPaxInAcDoc();
            String clientName = "";
            if (a.getPnr().getAgent() != null) {
                clientName = a.getPnr().getAgent().getName();
            } else if (a.getPnr().getCustomer() != null) {
                clientName = a.getPnr().getCustomer().getSurName() + "/" + a.getPnr().getCustomer().getForeName();
            }
            String status = "";
            if (a.isActive()) {
                status = "LIVE";
            } else {
                status = "VOID";
            }            
            if(a.getOutstandingAmount().compareTo(new BigDecimal("0.00")) > 0){                
             noOfOutstandingInvoice++;
             System.out.println("Ref: "+a.getAcDocRef());
            }
            
            invoiceModel.insertRow(row, new Object[]{a.getAcDocRef(), a.getAcDocTypeString(),
                        df.dateForGui(a.getIssueDate()), a.getTerms(), a.getPnr().getGdsPNR(),
                        a.getPnr().getServicingCareer().getCode(),a.getTotalPaxInAcDoc(),
                        a.getLeadPaxFromTickets(), clientName,a.getTktdSubTotal(),a.getOtherServiceSubTotal(),a.getAdditionalServiceSubTotal(),
                        a.getTotalDocumentedAmount(), a.getTotalTransactionAmount(),
                        a.getOutstandingAmount(), a.getAcDocIssuedBy().getSurName(), status});
            row++;
            if (a.getAcDoctype() == 1) {
                totalInvAmount = totalInvAmount.add(a.getTotalDocumentedAmount());
                noOfInvoice++;
            } else {
                totalCRNAmount = totalCRNAmount.add(a.getTotalDocumentedAmount());
                noOfCrn++;
            }
            totalReceived = totalReceived.add(a.getTotalTransactionAmount());
        }        
        lblNoInvoice.setText(String.valueOf(noOfInvoice));
        lblNoCrn.setText(String.valueOf(noOfCrn));
        lblTInvAmount.setText(totalInvAmount.toString());
        lblTCNoteAmount.setText(totalCRNAmount.toString());
        lblTotalReceived.setText(totalReceived.toString());
        lblTotalOutstanding.setText(totalInvAmount.add(totalCRNAmount).subtract(totalReceived).toString());
        lblTPax.setText(String.valueOf(noOfPax));
    }
    private ActionListener radioAgentListener = new ActionListener() {
        
        public void actionPerformed(ActionEvent e) {
            busyIcon.setVisible(true);
            busyIcon.setBusy(true);
            cmbContactable.setSelectedIndex(-1);
            cmbContactable.setEnabled(true);
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
            cmbContactable.setSelectedIndex(-1);
            cmbContactable.setEnabled(true);
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
    private ActionListener radioAllListener = new ActionListener() {
        
        public void actionPerformed(ActionEvent e) {
            busyIcon.setVisible(true);
            busyIcon.setBusy(true);
            cmbContactable.setSelectedIndex(-1);
            cmbContactable.setEnabled(false);
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
            txtContactableDetails.setText("");
            if (cmbContactable.getSelectedIndex() > 0) {
                data = cmbContactable.getSelectedItem().toString().split("-");
                id = Long.parseLong(data[2]);
                
                if (rdoAgent.isSelected()) {                    
                    loop:                    
                    for (Agent a : agents) {                        
                        if (a.getContactableId() == id) {                            
                            agent = a;
                            populateTxtAgentDetails(a);
                            break loop;
                        }
                    }
                } else if (rdoCustomer.isSelected()) {
                    loop:
                    for (Customer c : customers) {
                        if (c.getContactableId() == id) {
                            customer = c;
                            populateTxtCustomerDetails(c);
                            break loop;
                        }
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
        buttonGroup3 = new javax.swing.ButtonGroup();
        jToolBar1 = new javax.swing.JToolBar();
        jButton1 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        btnViewReport = new javax.swing.JButton();
        btnEmailReport = new javax.swing.JButton();
        btnPrintReport = new javax.swing.JButton();
        splitPanelMaster = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        rdoTParty = new javax.swing.JRadioButton();
        rdoSelf = new javax.swing.JRadioButton();
        rdoSelfTParty = new javax.swing.JRadioButton();
        jPanel7 = new javax.swing.JPanel();
        cmbContactable = new javax.swing.JComboBox();
        cmbContactable.addActionListener(cmbContactableListener);
        AutoCompleteDecorator.decorate(cmbContactable);
        jLabel5 = new javax.swing.JLabel();
        busyIcon = new org.jdesktop.swingx.JXBusyLabel();
        rdoAgent = new javax.swing.JRadioButton();
        rdoCustomer = new javax.swing.JRadioButton();
        rdoAll = new javax.swing.JRadioButton();
        jPanel6 = new javax.swing.JPanel();
        rdoInvoice = new javax.swing.JRadioButton();
        rdoCNote = new javax.swing.JRadioButton();
        rdoInCNote = new javax.swing.JRadioButton();
        jPanel9 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        dtFrom = new org.jdesktop.swingx.JXDatePicker();
        jLabel7 = new javax.swing.JLabel();
        dtTo = new org.jdesktop.swingx.JXDatePicker();
        jPanel10 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtAcDocRef = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtGdsPnr = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        btnCriteriaSearch = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        JScrollPane = new javax.swing.JScrollPane();
        tblInvoice = new JXTable(){public Component prepareRenderer(TableCellRenderer renderer,
            int rowIndex, int vColIndex) {
            Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);  
            String s = "";
            String s1 = "";

            Object o = tblInvoice.getModel().getValueAt(rowIndex, 1);       
            Object o1 = tblInvoice.getModel().getValueAt(rowIndex, 15);       
            if(o!=null){
                s = o.toString();
                s1 = o1.toString();
            }
            if(s.equalsIgnoreCase("INV")){
                c.setForeground(Color.green);
            }else if(s.equalsIgnoreCase("CRN")){
                c.setForeground(Color.red);
            }else{
                c.setForeground(Color.WHITE);
            }

            if(s1.equalsIgnoreCase("VOID")){           
                Map  attributes = c.getFont().getAttributes();
                attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
                Font newFont = new Font(attributes);
                c.setFont(newFont);
                c.setForeground(Color.WHITE);
            }
            return c;
        }
    };
    summeryPanel = new javax.swing.JPanel();
    lblBalance = new javax.swing.JLabel();
    lblTotalOutstanding = new javax.swing.JLabel();
    lblTotalReceived = new javax.swing.JLabel();
    lblTAmount = new javax.swing.JLabel();
    lblTotalAmount = new javax.swing.JLabel();
    lblTInvAmount = new javax.swing.JLabel();
    jLabel4 = new javax.swing.JLabel();
    jLabel8 = new javax.swing.JLabel();
    lblTCNoteAmount = new javax.swing.JLabel();
    lblPaid = new javax.swing.JLabel();
    jLabel11 = new javax.swing.JLabel();
    jLabel12 = new javax.swing.JLabel();
    lblNoInvoice = new javax.swing.JLabel();
    lblNoCrn = new javax.swing.JLabel();
    jLabel15 = new javax.swing.JLabel();
    jLabel1 = new javax.swing.JLabel();
    lblTPax = new javax.swing.JLabel();
    jScrollPane1 = new javax.swing.JScrollPane();
    txtContactableDetails = new javax.swing.JTextArea();
    jPanel4 = new javax.swing.JPanel();
    progressBar = new javax.swing.JProgressBar();
    statusMessageLabel = new javax.swing.JLabel();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getResourceMap(FrameSAcDocHistory.class);
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

    javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getActionMap(FrameSAcDocHistory.class, this);
    jButton5.setAction(actionMap.get("viewInvoice")); // NOI18N
    jButton5.setFont(resourceMap.getFont("btnPrintReport.font")); // NOI18N
    jButton5.setIcon(resourceMap.getIcon("jButton5.icon")); // NOI18N
    jButton5.setText(resourceMap.getString("jButton5.text")); // NOI18N
    jButton5.setFocusable(false);
    jButton5.setName("jButton5"); // NOI18N
    jToolBar1.add(jButton5);

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
    splitPanelMaster.setDividerLocation(235);
    splitPanelMaster.setDividerSize(4);
    splitPanelMaster.setName("splitPanelMaster"); // NOI18N

    jPanel1.setName("jPanel1"); // NOI18N
    jPanel1.setLayout(new java.awt.GridBagLayout());

    jPanel8.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
    jPanel8.setName("jPanel8"); // NOI18N

    jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, resourceMap.getString("jPanel3.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, resourceMap.getFont("jPanel3.border.titleFont"))); // NOI18N
    jPanel3.setName("jPanel3"); // NOI18N
    jPanel3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

    buttonGroup3.add(rdoTParty);
    rdoTParty.setFont(resourceMap.getFont("rdoTParty.font")); // NOI18N
    rdoTParty.setText(resourceMap.getString("rdoTParty.text")); // NOI18N
    rdoTParty.setName("rdoTParty"); // NOI18N
    rdoTParty.setPreferredSize(new java.awt.Dimension(80, 24));
    jPanel3.add(rdoTParty);

    buttonGroup3.add(rdoSelf);
    rdoSelf.setFont(resourceMap.getFont("rdoSelf.font")); // NOI18N
    rdoSelf.setText(resourceMap.getString("rdoSelf.text")); // NOI18N
    rdoSelf.setName("rdoSelf"); // NOI18N
    rdoSelf.setPreferredSize(new java.awt.Dimension(60, 24));
    jPanel3.add(rdoSelf);

    buttonGroup3.add(rdoSelfTParty);
    rdoSelfTParty.setFont(resourceMap.getFont("rdoSelfTParty.font")); // NOI18N
    rdoSelfTParty.setSelected(true);
    rdoSelfTParty.setText(resourceMap.getString("rdoSelfTParty.text")); // NOI18N
    rdoSelfTParty.setName("rdoSelfTParty"); // NOI18N
    rdoSelfTParty.setPreferredSize(new java.awt.Dimension(40, 24));
    jPanel3.add(rdoSelfTParty);

    jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(null, resourceMap.getString("jPanel7.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, resourceMap.getFont("jPanel7.border.titleFont"))); // NOI18N
    jPanel7.setName("jPanel7"); // NOI18N

    cmbContactable.setModel(cmbAgtModel);
    cmbContactable.setEnabled(false);
    cmbContactable.setName("cmbContactable"); // NOI18N
    cmbContactable.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            cmbContactableActionPerformed(evt);
        }
    });

    jLabel5.setFont(resourceMap.getFont("rdoInvoice.font")); // NOI18N
    jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
    jLabel5.setName("jLabel5"); // NOI18N

    busyIcon.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    busyIcon.setText(resourceMap.getString("busyIcon.text")); // NOI18N
    busyIcon.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
    busyIcon.setName("busyIcon"); // NOI18N

    buttonGroup1.add(rdoAgent);
    rdoAgent.setFont(resourceMap.getFont("rdoCustomer.font")); // NOI18N
    rdoAgent.setText(resourceMap.getString("rdoAgent.text")); // NOI18N
    rdoAgent.setName("rdoAgent"); // NOI18N
    rdoAgent.setPreferredSize(new java.awt.Dimension(60, 24));
    rdoAgent.addActionListener(radioAgentListener);

    buttonGroup1.add(rdoCustomer);
    rdoCustomer.setFont(resourceMap.getFont("rdoCustomer.font")); // NOI18N
    rdoCustomer.setText(resourceMap.getString("rdoCustomer.text")); // NOI18N
    rdoCustomer.setName("rdoCustomer"); // NOI18N
    rdoCustomer.setPreferredSize(new java.awt.Dimension(80, 24));
    rdoCustomer.addActionListener(radioCustomerListener);

    buttonGroup1.add(rdoAll);
    rdoAll.setFont(resourceMap.getFont("rdoCustomer.font")); // NOI18N
    rdoAll.setSelected(true);
    rdoAll.setText(resourceMap.getString("rdoAll.text")); // NOI18N
    rdoAll.setName("rdoAll"); // NOI18N
    rdoAll.setPreferredSize(new java.awt.Dimension(40, 24));
    rdoAll.addActionListener(radioAllListener);

    javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
    jPanel7.setLayout(jPanel7Layout);
    jPanel7Layout.setHorizontalGroup(
        jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel7Layout.createSequentialGroup()
            .addGap(4, 4, 4)
            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(97, 97, 97)
            .addComponent(busyIcon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGroup(jPanel7Layout.createSequentialGroup()
            .addGap(4, 4, 4)
            .addComponent(cmbContactable, 0, 202, Short.MAX_VALUE)
            .addContainerGap())
        .addGroup(jPanel7Layout.createSequentialGroup()
            .addComponent(rdoAgent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(rdoCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(rdoAll, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(34, Short.MAX_VALUE))
    );
    jPanel7Layout.setVerticalGroup(
        jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel7Layout.createSequentialGroup()
            .addGap(0, 0, 0)
            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(rdoAgent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(rdoCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(rdoAll, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(0, 0, 0)
            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel7Layout.createSequentialGroup()
                    .addGap(4, 4, 4)
                    .addComponent(jLabel5))
                .addComponent(busyIcon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(4, 4, 4)
            .addComponent(cmbContactable, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap())
    );

    jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, resourceMap.getString("jPanel6.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, resourceMap.getFont("jPanel6.border.titleFont"))); // NOI18N
    jPanel6.setName("jPanel6"); // NOI18N
    jPanel6.setLayout(new java.awt.GridBagLayout());

    buttonGroup2.add(rdoInvoice);
    rdoInvoice.setFont(resourceMap.getFont("rdoInvoice.font")); // NOI18N
    rdoInvoice.setText(resourceMap.getString("rdoInvoice.text")); // NOI18N
    rdoInvoice.setName("rdoInvoice"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel6.add(rdoInvoice, gridBagConstraints);

    buttonGroup2.add(rdoCNote);
    rdoCNote.setFont(resourceMap.getFont("rdoInvoice.font")); // NOI18N
    rdoCNote.setText(resourceMap.getString("rdoCNote.text")); // NOI18N
    rdoCNote.setName("rdoCNote"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel6.add(rdoCNote, gridBagConstraints);

    buttonGroup2.add(rdoInCNote);
    rdoInCNote.setFont(resourceMap.getFont("rdoInvoice.font")); // NOI18N
    rdoInCNote.setSelected(true);
    rdoInCNote.setText(resourceMap.getString("rdoInCNote.text")); // NOI18N
    rdoInCNote.setName("rdoInCNote"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel6.add(rdoInCNote, gridBagConstraints);

    jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(null, resourceMap.getString("jPanel9.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, resourceMap.getFont("jPanel9.border.titleFont"))); // NOI18N
    jPanel9.setName("jPanel9"); // NOI18N
    jPanel9.setLayout(new java.awt.GridBagLayout());

    jLabel6.setFont(resourceMap.getFont("rdoInvoice.font")); // NOI18N
    jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
    jLabel6.setName("jLabel6"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel9.add(jLabel6, gridBagConstraints);

    dtFrom.setFont(resourceMap.getFont("txtAcDocRef.font")); // NOI18N
    dtFrom.setName("dtFrom"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel9.add(dtFrom, gridBagConstraints);

    jLabel7.setFont(resourceMap.getFont("rdoInvoice.font")); // NOI18N
    jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
    jLabel7.setName("jLabel7"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel9.add(jLabel7, gridBagConstraints);

    dtTo.setFont(resourceMap.getFont("txtAcDocRef.font")); // NOI18N
    dtTo.setName("dtTo"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel9.add(dtTo, gridBagConstraints);

    jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder(null, resourceMap.getString("jPanel10.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, resourceMap.getFont("jPanel10.border.titleFont"))); // NOI18N
    jPanel10.setName("jPanel10"); // NOI18N
    jPanel10.setLayout(new java.awt.GridBagLayout());

    jLabel2.setFont(resourceMap.getFont("jLabel2.font")); // NOI18N
    jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
    jLabel2.setName("jLabel2"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel10.add(jLabel2, gridBagConstraints);

    txtAcDocRef.setFont(resourceMap.getFont("txtAcDocRef.font")); // NOI18N
    txtAcDocRef.setText(resourceMap.getString("txtAcDocRef.text")); // NOI18N
    txtAcDocRef.setName("txtAcDocRef"); // NOI18N
    txtAcDocRef.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusGained(java.awt.event.FocusEvent evt) {
            txtAcDocRefFocusGained(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel10.add(txtAcDocRef, gridBagConstraints);

    jLabel3.setFont(resourceMap.getFont("jLabel2.font")); // NOI18N
    jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
    jLabel3.setName("jLabel3"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel10.add(jLabel3, gridBagConstraints);

    txtGdsPnr.setFont(resourceMap.getFont("txtAcDocRef.font")); // NOI18N
    txtGdsPnr.setText(resourceMap.getString("txtGdsPnr.text")); // NOI18N
    txtGdsPnr.setName("txtGdsPnr"); // NOI18N
    txtGdsPnr.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusGained(java.awt.event.FocusEvent evt) {
            txtGdsPnrFocusGained(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel10.add(txtGdsPnr, gridBagConstraints);

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
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel10.add(btnSearch, gridBagConstraints);

    btnCriteriaSearch.setFont(resourceMap.getFont("btnCriteriaSearch.font")); // NOI18N
    btnCriteriaSearch.setIcon(resourceMap.getIcon("btnCriteriaSearch.icon")); // NOI18N
    btnCriteriaSearch.setText(resourceMap.getString("btnCriteriaSearch.text")); // NOI18N
    btnCriteriaSearch.setName("btnCriteriaSearch"); // NOI18N
    btnCriteriaSearch.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnCriteriaSearchActionPerformed(evt);
        }
    });

    javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
    jPanel8.setLayout(jPanel8Layout);
    jPanel8Layout.setHorizontalGroup(
        jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(btnCriteriaSearch, javax.swing.GroupLayout.Alignment.TRAILING)
        .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
        .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
        .addComponent(jPanel7, 0, 231, Short.MAX_VALUE)
        .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
    );
    jPanel8Layout.setVerticalGroup(
        jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel8Layout.createSequentialGroup()
            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(0, 0, 0)
            .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(0, 0, 0)
            .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(2, 2, 2)
            .addComponent(btnCriteriaSearch)
            .addGap(0, 0, 0)
            .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(0, 0, 0))
    );

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    jPanel1.add(jPanel8, gridBagConstraints);

    splitPanelMaster.setLeftComponent(jPanel1);

    jPanel2.setName("jPanel2"); // NOI18N
    jPanel2.setLayout(new java.awt.GridBagLayout());

    JScrollPane.setName("JScrollPane"); // NOI18N

    tblInvoice.setBackground(resourceMap.getColor("tblInvoice.background")); // NOI18N
    tblInvoice.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {

        },
        new String [] {
            "Ref", "Type", "Date", "Terms", "PNR", "Career", "T.Pax", "LeadPax", "DocumentFor", "TktSubTotal", "OSSubTotal", "OChg", "D.Amount", "T.Amount", "Due", "IssuedBy", "Stat"
        }
    ) {
        boolean[] canEdit = new boolean [] {
            false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
        };

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit [columnIndex];
        }
    });
    tblInvoice.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
    tblInvoice.setFont(resourceMap.getFont("tblInvoice.font")); // NOI18N
    tblInvoice.setName("tblInvoice"); // NOI18N
    tblInvoice.setSortable(false);
    tblInvoice.getTableHeader().setReorderingAllowed(false);
    JScrollPane.setViewportView(tblInvoice);
    tblInvoice.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title0")); // NOI18N
    tblInvoice.getColumnModel().getColumn(1).setPreferredWidth(40);
    tblInvoice.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title1")); // NOI18N
    tblInvoice.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title10")); // NOI18N
    tblInvoice.getColumnModel().getColumn(4).setPreferredWidth(50);
    tblInvoice.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title2")); // NOI18N
    tblInvoice.getColumnModel().getColumn(5).setPreferredWidth(50);
    tblInvoice.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title11")); // NOI18N
    tblInvoice.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title16")); // NOI18N
    tblInvoice.getColumnModel().getColumn(7).setMinWidth(140);
    tblInvoice.getColumnModel().getColumn(7).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title3")); // NOI18N
    tblInvoice.getColumnModel().getColumn(8).setMinWidth(140);
    tblInvoice.getColumnModel().getColumn(8).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title5")); // NOI18N
    tblInvoice.getColumnModel().getColumn(9).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title13")); // NOI18N
    tblInvoice.getColumnModel().getColumn(11).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title15")); // NOI18N
    tblInvoice.getColumnModel().getColumn(12).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title6")); // NOI18N
    tblInvoice.getColumnModel().getColumn(13).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title7")); // NOI18N
    tblInvoice.getColumnModel().getColumn(14).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title8")); // NOI18N
    tblInvoice.getColumnModel().getColumn(15).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title9")); // NOI18N
    tblInvoice.getColumnModel().getColumn(16).setMinWidth(40);
    tblInvoice.getColumnModel().getColumn(16).setPreferredWidth(40);
    tblInvoice.getColumnModel().getColumn(16).setMaxWidth(40);
    tblInvoice.getColumnModel().getColumn(16).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title12")); // NOI18N

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
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

    lblBalance.setFont(resourceMap.getFont("lblBalance.font")); // NOI18N
    lblBalance.setForeground(resourceMap.getColor("lblBalance.foreground")); // NOI18N
    lblBalance.setText(resourceMap.getString("lblBalance.text")); // NOI18N
    lblBalance.setName("lblBalance"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(4, 10, 4, 2);
    summeryPanel.add(lblBalance, gridBagConstraints);

    lblTotalOutstanding.setFont(resourceMap.getFont("lblTotalOutstanding.font")); // NOI18N
    lblTotalOutstanding.setForeground(resourceMap.getColor("lblTotalOutstanding.foreground")); // NOI18N
    lblTotalOutstanding.setText(resourceMap.getString("lblTotalOutstanding.text")); // NOI18N
    lblTotalOutstanding.setMinimumSize(new java.awt.Dimension(100, 14));
    lblTotalOutstanding.setName("lblTotalOutstanding"); // NOI18N
    lblTotalOutstanding.setPreferredSize(new java.awt.Dimension(150, 14));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(4, 10, 4, 2);
    summeryPanel.add(lblTotalOutstanding, gridBagConstraints);

    lblTotalReceived.setFont(resourceMap.getFont("lblTotalReceived.font")); // NOI18N
    lblTotalReceived.setForeground(resourceMap.getColor("lblTotalReceived.foreground")); // NOI18N
    lblTotalReceived.setText(resourceMap.getString("lblTotalReceived.text")); // NOI18N
    lblTotalReceived.setMinimumSize(new java.awt.Dimension(100, 14));
    lblTotalReceived.setName("lblTotalReceived"); // NOI18N
    lblTotalReceived.setPreferredSize(new java.awt.Dimension(150, 14));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(4, 10, 4, 2);
    summeryPanel.add(lblTotalReceived, gridBagConstraints);

    lblTAmount.setFont(resourceMap.getFont("lblBalance.font")); // NOI18N
    lblTAmount.setForeground(resourceMap.getColor("lblBalance.foreground")); // NOI18N
    lblTAmount.setText(resourceMap.getString("lblTAmount.text")); // NOI18N
    lblTAmount.setName("lblTAmount"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(4, 10, 4, 2);
    summeryPanel.add(lblTAmount, gridBagConstraints);

    lblTotalAmount.setFont(resourceMap.getFont("lblBalance.font")); // NOI18N
    lblTotalAmount.setForeground(resourceMap.getColor("lblBalance.foreground")); // NOI18N
    lblTotalAmount.setText(resourceMap.getString("lblTotalAmount.text")); // NOI18N
    lblTotalAmount.setName("lblTotalAmount"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(4, 10, 4, 2);
    summeryPanel.add(lblTotalAmount, gridBagConstraints);

    lblTInvAmount.setFont(resourceMap.getFont("lblTInvAmount.font")); // NOI18N
    lblTInvAmount.setForeground(resourceMap.getColor("lblTInvAmount.foreground")); // NOI18N
    lblTInvAmount.setText(resourceMap.getString("lblTInvAmount.text")); // NOI18N
    lblTInvAmount.setMinimumSize(new java.awt.Dimension(100, 14));
    lblTInvAmount.setName("lblTInvAmount"); // NOI18N
    lblTInvAmount.setPreferredSize(new java.awt.Dimension(150, 14));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(4, 10, 4, 2);
    summeryPanel.add(lblTInvAmount, gridBagConstraints);

    jLabel4.setFont(resourceMap.getFont("jLabel8.font")); // NOI18N
    jLabel4.setForeground(resourceMap.getColor("jLabel4.foreground")); // NOI18N
    jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
    jLabel4.setName("jLabel4"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 5;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
    gridBagConstraints.insets = new java.awt.Insets(4, 10, 4, 2);
    summeryPanel.add(jLabel4, gridBagConstraints);

    jLabel8.setFont(resourceMap.getFont("jLabel8.font")); // NOI18N
    jLabel8.setForeground(resourceMap.getColor("jLabel8.foreground")); // NOI18N
    jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
    jLabel8.setName("jLabel8"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 5;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
    gridBagConstraints.insets = new java.awt.Insets(4, 10, 4, 2);
    summeryPanel.add(jLabel8, gridBagConstraints);

    lblTCNoteAmount.setFont(resourceMap.getFont("lblTCNoteAmount.font")); // NOI18N
    lblTCNoteAmount.setForeground(resourceMap.getColor("lblTCNoteAmount.foreground")); // NOI18N
    lblTCNoteAmount.setText(resourceMap.getString("lblTCNoteAmount.text")); // NOI18N
    lblTCNoteAmount.setMinimumSize(new java.awt.Dimension(100, 14));
    lblTCNoteAmount.setName("lblTCNoteAmount"); // NOI18N
    lblTCNoteAmount.setPreferredSize(new java.awt.Dimension(150, 14));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 6;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(4, 10, 4, 2);
    summeryPanel.add(lblTCNoteAmount, gridBagConstraints);

    lblPaid.setFont(resourceMap.getFont("lblPaid.font")); // NOI18N
    lblPaid.setForeground(resourceMap.getColor("lblPaid.foreground")); // NOI18N
    lblPaid.setText(resourceMap.getString("lblPaid.text")); // NOI18N
    lblPaid.setMinimumSize(new java.awt.Dimension(100, 14));
    lblPaid.setName("lblPaid"); // NOI18N
    lblPaid.setPreferredSize(new java.awt.Dimension(150, 14));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 6;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(4, 10, 4, 2);
    summeryPanel.add(lblPaid, gridBagConstraints);

    jLabel11.setFont(resourceMap.getFont("jLabel11.font")); // NOI18N
    jLabel11.setForeground(resourceMap.getColor("jLabel11.foreground")); // NOI18N
    jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
    jLabel11.setName("jLabel11"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(4, 10, 4, 2);
    summeryPanel.add(jLabel11, gridBagConstraints);

    jLabel12.setFont(resourceMap.getFont("jLabel11.font")); // NOI18N
    jLabel12.setForeground(resourceMap.getColor("jLabel12.foreground")); // NOI18N
    jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
    jLabel12.setName("jLabel12"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 5;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(4, 10, 4, 2);
    summeryPanel.add(jLabel12, gridBagConstraints);

    lblNoInvoice.setFont(resourceMap.getFont("lblNoInvoice.font")); // NOI18N
    lblNoInvoice.setForeground(resourceMap.getColor("lblNoInvoice.foreground")); // NOI18N
    lblNoInvoice.setText(resourceMap.getString("lblNoInvoice.text")); // NOI18N
    lblNoInvoice.setName("lblNoInvoice"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(4, 10, 4, 2);
    summeryPanel.add(lblNoInvoice, gridBagConstraints);

    lblNoCrn.setFont(resourceMap.getFont("lblNoCrn.font")); // NOI18N
    lblNoCrn.setForeground(resourceMap.getColor("lblNoCrn.foreground")); // NOI18N
    lblNoCrn.setText(resourceMap.getString("lblNoCrn.text")); // NOI18N
    lblNoCrn.setName("lblNoCrn"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 6;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(4, 10, 4, 2);
    summeryPanel.add(lblNoCrn, gridBagConstraints);

    jLabel15.setFont(resourceMap.getFont("jLabel15.font")); // NOI18N
    jLabel15.setForeground(resourceMap.getColor("jLabel15.foreground")); // NOI18N
    jLabel15.setText(resourceMap.getString("jLabel15.text")); // NOI18N
    jLabel15.setName("jLabel15"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 6;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
    gridBagConstraints.insets = new java.awt.Insets(4, 10, 4, 2);
    summeryPanel.add(jLabel15, gridBagConstraints);

    jLabel1.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
    jLabel1.setForeground(resourceMap.getColor("jLabel1.foreground")); // NOI18N
    jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
    jLabel1.setName("jLabel1"); // NOI18N
    summeryPanel.add(jLabel1, new java.awt.GridBagConstraints());

    lblTPax.setFont(resourceMap.getFont("lblTPax.font")); // NOI18N
    lblTPax.setForeground(resourceMap.getColor("lblTPax.foreground")); // NOI18N
    lblTPax.setText(resourceMap.getString("lblTPax.text")); // NOI18N
    lblTPax.setName("lblTPax"); // NOI18N
    lblTPax.setPreferredSize(new java.awt.Dimension(80, 14));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
    summeryPanel.add(lblTPax, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 0.5;
    gridBagConstraints.insets = new java.awt.Insets(4, 2, 4, 2);
    jPanel2.add(summeryPanel, gridBagConstraints);

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
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 0.5;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel2.add(jScrollPane1, gridBagConstraints);

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
    
    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        //new Thread(new threadSearchInvoice()).start();
        String acDocRef = txtAcDocRef.getText().trim();
        String gdsPnr = txtGdsPnr.getText().trim();
        searchAcDoc s = new searchAcDoc(tblInvoice, gdsPnr, acDocRef);
        s.execute();
    }//GEN-LAST:event_btnSearchActionPerformed
    
    private void txtAcDocRefFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAcDocRefFocusGained
        txtGdsPnr.setText("");
    }//GEN-LAST:event_txtAcDocRefFocusGained
    
    private void txtGdsPnrFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtGdsPnrFocusGained
        txtAcDocRef.setText("");
    }//GEN-LAST:event_txtGdsPnrFocusGained
    
    private void btnViewReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewReportActionPerformed
        acDocBo.setFrom(from);
        acDocBo.setTo(to);
        acDocBo.setAcDocType("Outstanding Invoice Report");
        acDocBo.setmAgent(AuthenticationBo.getmAgent());
        acDocBo.setAcDocsRpt(this.invoices);
        acDocBo.setAgent(this.agent);
        acDocBo.setCustomer(this.customer);
        acDocBo.reportSummeryAcDocHistory();
        BackofficeReporting rptAcDoc = new BackofficeReporting();
        List<Object> invObject = new ArrayList();
        invObject.add(acDocBo);
        rptAcDoc.viewAcDocHistoryReport(invObject);
    }//GEN-LAST:event_btnViewReportActionPerformed
    
    private void btnEmailReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmailReportActionPerformed
        acDocBo.setFrom(from);
        acDocBo.setTo(to);
        acDocBo.setAcDocType("Outstanding Invoice Report");
        acDocBo.setmAgent(AuthenticationBo.getmAgent());
        acDocBo.setAcDocsRpt(this.invoices);
        acDocBo.setAgent(this.agent);
        acDocBo.setCustomer(this.customer);
        acDocBo.reportSummeryAcDocHistory();
        BackofficeReporting rptAcDoc = new BackofficeReporting();
        List<Object> invObject = new ArrayList();
        invObject.add(acDocBo);
        
        String emailAddress = this.agent.getEmail();
        if (!emailAddress.isEmpty()) {
            String subject = "Invoice Report from : " + AuthenticationBo.getmAgent().getName();
            String body = "Invoices from " + AuthenticationBo.getmAgent().getName();
            rptAcDoc.emailAcDocHistoryReport(emailAddress, subject, body, invObject);
        } else {
            JOptionPane.showMessageDialog(null, "No email address found \n Please add email address in clients profile.", "Email Report", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnEmailReportActionPerformed
    
    private void btnPrintReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintReportActionPerformed
        acDocBo.setFrom(from);
        acDocBo.setTo(to);
        acDocBo.setAcDocType("Outstanding Invoice Report");
        acDocBo.setmAgent(AuthenticationBo.getmAgent());
        acDocBo.setAcDocsRpt(this.invoices);
        acDocBo.setAgent(this.agent);
        acDocBo.setCustomer(this.customer);
        acDocBo.reportSummeryAcDocHistory();
        BackofficeReporting rptAcDoc = new BackofficeReporting();
        List<Object> invObject = new ArrayList();
        invObject.add(acDocBo);
        rptAcDoc.printAcDocHistoryReport(invObject);
    }//GEN-LAST:event_btnPrintReportActionPerformed
    /**
     * @param args the command line arguments
     */
    /*public static void main(String args[]) {
    java.awt.EventQueue.invokeLater(new Runnable() {
    
    public void run() {
    new FrameSAcDocHistory().setVisible(true);
    }
    });
    }*/
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane JScrollPane;
    private javax.swing.JButton btnCriteriaSearch;
    private javax.swing.JButton btnEmailReport;
    private javax.swing.JButton btnPrintReport;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnViewReport;
    private org.jdesktop.swingx.JXBusyLabel busyIcon;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.JComboBox cmbContactable;
    private org.jdesktop.swingx.JXDatePicker dtFrom;
    private org.jdesktop.swingx.JXDatePicker dtTo;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblBalance;
    private javax.swing.JLabel lblNoCrn;
    private javax.swing.JLabel lblNoInvoice;
    private javax.swing.JLabel lblPaid;
    private javax.swing.JLabel lblTAmount;
    private javax.swing.JLabel lblTCNoteAmount;
    private javax.swing.JLabel lblTInvAmount;
    private javax.swing.JLabel lblTPax;
    private javax.swing.JLabel lblTotalAmount;
    private javax.swing.JLabel lblTotalOutstanding;
    private javax.swing.JLabel lblTotalReceived;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JRadioButton rdoAgent;
    private javax.swing.JRadioButton rdoAll;
    private javax.swing.JRadioButton rdoCNote;
    private javax.swing.JRadioButton rdoCustomer;
    private javax.swing.JRadioButton rdoInCNote;
    private javax.swing.JRadioButton rdoInvoice;
    private javax.swing.JRadioButton rdoSelf;
    private javax.swing.JRadioButton rdoSelfTParty;
    private javax.swing.JRadioButton rdoTParty;
    private javax.swing.JSplitPane splitPanelMaster;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel summeryPanel;
    private org.jdesktop.swingx.JXTable tblInvoice;
    private javax.swing.JTextField txtAcDocRef;
    private javax.swing.JTextArea txtContactableDetails;
    private javax.swing.JTextField txtGdsPnr;
    // End of variables declaration//GEN-END:variables

    @Action
    public void viewInvoice() {
        int row = tblInvoice.getSelectedRow();
        if (row != -1) {
            try {
                long acDocId = this.invoices.get(row).getAcDocId();
                int docType = this.invoices.get(row).getAcDoctype();
                Thread t = new Thread(new threadLoadCompleteInvoice(acDocId));
                t.start();
                t.join();
                {
                    if (docType == 1 || docType == 2) {
                        DlgTInvoice frameInvoice = new DlgTInvoice(this);
                        if (frameInvoice.showDialog(acDoc)) {
                            frameInvoice.dispose();
                        }
                    } else if (docType == 3) {
                        DlgCNote frameCNote = new DlgCNote(this);
                        if (frameCNote.showCreditNoteDialog(acDoc)) {
                            frameCNote.dispose();
                        }
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
            noOfRecord = invoices.size();
            
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
    
    private class threadSearchInvoice implements Runnable {
        
        private int noOfRecord;
        
        public threadSearchInvoice() {
        }
        
        public void run() {
            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();
            
            statusMessageLabel.setText("Loading Invoice...");
            btnSearch.setEnabled(false);
            searchAcDoc();
            noOfRecord = invoices.size();
            btnSearch.setEnabled(true);
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            if (noOfRecord > 0) {
                statusMessageLabel.setText(noOfRecord + " Records fetched in: " + elapsedTime / 1000 + " seconds");
            } else {
                statusMessageLabel.setText("No record found. Search Copleted in " + elapsedTime / 1000 + " seconds");
            }
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
    
    private class searchAcDoc extends SwingWorker<List<AccountingDocument>, Void> {
        
        String pnr;
        String acDocRef;
        JXTable table;
        
        public searchAcDoc(JXTable table, String pnr, String acDocRef) {
            this.pnr = pnr;
            this.acDocRef = acDocRef;
            this.table = table;
        }
        
        @Override
        protected List<AccountingDocument> doInBackground() throws Exception {
            List<AccountingDocument> acDocs = null;
            
            if (!acDocRef.isEmpty()) {
                acDocs = acDocBo.findCompleteAcDocByRefNo(Integer.valueOf(acDocRef));
            } else if (!pnr.isEmpty()) {
                acDocs = acDocBo.findAcDocByGdsPnr(pnr);
            }
            
            return acDocs;
        }
        
        @Override
        protected void done() {
            try {
                invoices = get();
            } catch (InterruptedException ex) {
                Logger.getLogger(FrameSAcDocHistory.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(FrameSAcDocHistory.class.getName()).log(Level.SEVERE, null, ex);
            }
            populateTblInvoice(invoices);
        }        
    }
}
