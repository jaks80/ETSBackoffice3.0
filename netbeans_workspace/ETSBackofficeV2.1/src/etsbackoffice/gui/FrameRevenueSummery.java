package etsbackoffice.gui;

import etsbackoffice.ETSBackofficeApp;
import etsbackoffice.report.RevenueReport;
import etsbackoffice.businesslogic.*;
import etsbackoffice.domain.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import org.jdesktop.application.Action;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 *
 * @author Yusuf
 */
public class FrameRevenueSummery extends javax.swing.JFrame{

    ReportBo reportBo = (ReportBo) ETSBackofficeApp.getApplication().ctx.getBean("reportBo");
    AccountingDocBo acDocBo = (AccountingDocBo) ETSBackofficeApp.getApplication().ctx.getBean("acDocBo");
    AgentBo agentBo = (AgentBo) ETSBackofficeApp.getApplication().ctx.getBean("agentBo");
    CustomerBo customerBo = (CustomerBo) ETSBackofficeApp.getApplication().ctx.getBean("customerBo");
    CareerBo careerBo = (CareerBo) ETSBackofficeApp.getApplication().ctx.getBean("careerBo");
    //private List<PurchaseAccountingDocument> acDocs = new ArrayList();
    private List<AccountingDocument> acDocs = new ArrayList();    
    private List<Career> careers = new ArrayList();
    private AccountingDocument acDoc;
    private PurchaseAccountingDocument pAcDoc;
    /** Creates new form FrameAccounts */
    public FrameRevenueSummery(java.awt.Frame parent) {
        initComponents();        
        initialThreads();
    }

    private void initialThreads(){
    new Thread(new threadFindCareers()).start();
    }

    private void accountsReportByCriteria() {

        Date from = null;
        Date to = null;        
        
        String careerId = null;
        int contactableType = 0;
        Long contactableId = null;

        from = dtFrom.getDate();
        to = dtTo.getDate();        

        if (cmbCareer.getSelectedIndex() > 0) {
        }
        String[] data;

        if (cmbCareer.getSelectedIndex() > 0) {            
            careerId = (String) cmbCareer.getSelectedItem();
        } else {
            careerId = null;
        }

        if (cmbAgent.getSelectedIndex() > 0) {
            data = cmbAgent.getSelectedItem().toString().split("-");
            contactableId = Long.parseLong(data[2]);
        } else {
            contactableId = null;
        }

        if (rdoAgent.isSelected()) {
            contactableType = 1;
        } else if (rdoCustomer.isSelected()) {
            contactableType = 2;
        } else if (rdoAgtCust.isSelected()) {
            contactableType = 0;
            contactableId = null;
        }

        this.acDocs = reportBo.revenueSummeryReport(from, to, careerId, contactableType, contactableId);
        populateTblAccounts();
    }

    private void populateCmbCareer(){        

        DefaultComboBoxModel cmbCareerModel = new DefaultComboBoxModel(careerBo.findAllCodes().toArray());
            cmbCareerModel.insertElementAt("All", 0);
            cmbCareer.setModel(cmbCareerModel);
            cmbCareer.setSelectedIndex(0);
    }

    private void populateTblAccounts() {

        int row = 0;
        BigDecimal tPFare = new BigDecimal("0.00");
        BigDecimal tPOtherCost = new BigDecimal("0.00");
        BigDecimal gTotalPurchase = new BigDecimal("0.00");
        BigDecimal tPRefund = new BigDecimal("0.00");
        BigDecimal tPOtherRefund = new BigDecimal("0.00");
        BigDecimal gTotalPRefund = new BigDecimal("0.00");
        
        BigDecimal tSFare = new BigDecimal("0.00");
        BigDecimal tSOService = new BigDecimal("0.00");
        BigDecimal tSOtherChg = new BigDecimal("0.00");
        
        BigDecimal gTotalSales = new BigDecimal("0.00");
        BigDecimal tSRefund = new BigDecimal("0.00");
        BigDecimal tSOServiceRefund = new BigDecimal("0.00");
        
        BigDecimal tSOtherRefund = new BigDecimal("0.00");
        BigDecimal gTotalSRefund = new BigDecimal("0.00");       

        BigDecimal tPBalance,tSBalance = new BigDecimal("0.00");
        BigDecimal netRevenue = new BigDecimal("0.00");


        DefaultTableModel accountsModel = (DefaultTableModel) tblAccounts.getModel();
        accountsModel.getDataVector().removeAllElements();
        tblAccounts.repaint();
               
        for (AccountingDocument a : this.acDocs) {
            
            RevenueReport r = new RevenueReport(a);
            if(a.getAcDocRef()== 11000572){
            System.out.append("Inv: "+ a.getAcDocRef());
            }
            BigDecimal sBalance = new BigDecimal("0.00");
            BigDecimal pBalance = new BigDecimal("0.00");
            
            if (a.getAcDoctype() == 1) {
                tPFare = tPFare.add(r.tPFare());
                tPOtherCost = tPOtherCost.add(r.tPOCost());
                gTotalPurchase = gTotalPurchase.add(r.pInvAmount());
                tSFare = tSFare.add(r.tSFare());
                tSOService = tSOService.add(r.tSOtherService());
                tSOtherChg = tSOtherChg.add(r.tSOCharge());
                gTotalSales = gTotalSales.add(r.sInvAmount());
                
            } else if (a.getAcDoctype() == 2|| a.getAcDoctype()==3) {
                tPRefund = tPRefund.add(r.tPFare());
                tPOtherRefund = tPOtherRefund.add(r.tPOCost());
                gTotalPRefund = gTotalPRefund.add(r.pInvAmount());
                tSRefund = tSRefund.add(r.tSFare());
                tSOServiceRefund = tSOServiceRefund.add(r.tSOtherService());
                tSOtherRefund = tSOtherRefund.add(r.tSOCharge());
                gTotalSRefund = gTotalSRefund.add(r.sInvAmount());
                
            }
            
            accountsModel.insertRow(row, new Object[]{r.pnr(),r.career(),r.noOfTicket(),r.sAcDocRef(),r.sAcDocDate(),
            r.tSFare(),r.tSOtherService(),r.tSOCharge(),r.sInvAmount(),r.paymentReceived(),r.outstandingSBalance(),r.pInvAmount(),
            r.billPaid(),r.outstandingPBalance(),r.tktdRevenue(),r.oSRevenue(),r.oChgRevenue(),
            r.revenueAdm(),r.etstimatedTotalRevenue(),r.etstimatedRevenueEffectedByTrans()});
            
            row++;            
        }
        tPBalance = gTotalPurchase.add(gTotalPRefund);
        tSBalance = gTotalSales.add(gTotalSRefund);
        netRevenue = tSBalance.subtract(tPBalance); 
        //netRevenue = sBalance.subtract(pBalance);//.add(com).add(comRefund);
        lblTPFare.setText(tPFare.toString());
        lblTPOtherCost.setText(tPOtherCost.toString());
        lblGTPurchase.setText(gTotalPurchase.toString());
        lblTPRefund.setText(tPRefund.toString());
        lblTPOtherRefund.setText(tPOtherRefund.toString());
        lblGTPRefund.setText(gTotalPRefund.toString());
        lblTSFare.setText(tSFare.toString());
        lblTSOtherChg.setText(tSOtherChg.toString());
        lblGTSales.setText(gTotalSales.toString());
        lblTSRefund.setText(tSRefund.toString());
        lblTSOtherRfd.setText(tSOtherRefund.toString());
        lblGTSRefund.setText(gTotalSRefund.toString());

        //lblCom.setText(com.toString());
        //lblComRefund.setText(comRefund.toString());

        lblPBalance.setText(tPBalance.toString());
        lblSBalance.setText(tSBalance.toString());
        lblNetRev.setText(netRevenue.toString());   
    }

        @Action
    public void viewSAcDoc() {
        int row = tblAccounts.getSelectedRow();
        if (row != -1) {
            try {
                long acDocId = this.acDocs.get(row).getAcDocId();
                //int docType = this.acDocs.get(row).getAcDoctype();
                Thread t = new Thread(new threadLoadCompleteInvoice(acDocId));
                t.start();
                t.join();
                {
                    if (acDoc.getAcDoctype() == 1) {
                        DlgTInvoice frameInvoice = new DlgTInvoice(this);
                        if (frameInvoice.showDialog(acDoc)) {
                            frameInvoice.dispose();
                        }
                    }else if (acDoc.getAcDoctype() == 3) {
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

    @Action
    public void viewPAcDoc() {
        int row = tblAccounts.getSelectedRow();
        if (row != -1) {
            try {
                if (!this.acDocs.get(row).getPurchaseAccountingDocuments().isEmpty()) {
                    long acDocId = this.acDocs.get(row).getPurchaseAccountingDocuments().iterator().next().getPurchaseAcDocId();
                    //int docType = this.acDocs.get(row).getPurchaseAccountingDocuments().iterator().next().getAcDoctype();
                    Thread t = new Thread(new threadLoadCompletePInvoice(acDocId));
                    t.start();
                    t.join();
                    {
                        
                    }
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(FrameSAcDocHistory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    private ActionListener rdoAgentListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            busyIcon.setBusy(true);
            List cmbElement = new ArrayList();
            cmbElement = agentBo.agentNameList();

            DefaultComboBoxModel cmbContactableModel = new DefaultComboBoxModel(cmbElement.toArray());
            cmbContactableModel.insertElementAt("All", 0);
            cmbAgent.setModel(cmbContactableModel);
            cmbAgent.setSelectedIndex(0);
            if (!cmbAgent.isEnabled()) {
                cmbAgent.setEnabled(true);
            }
            busyIcon.setBusy(false);
        }
    };

    private ActionListener rdoCustomerListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            cmbAgent.setSelectedIndex(-1);
            cmbAgent.setEnabled(false);
        }
    };

    private ActionListener rdoAgtCustListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            cmbAgent.setSelectedIndex(-1);
            cmbAgent.setEnabled(false);
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
        btnSAcDoc = new javax.swing.JButton();
        btnPAcDoc = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        cmbAgent = new javax.swing.JComboBox();
        AutoCompleteDecorator.decorate(cmbAgent);
        jLabel6 = new javax.swing.JLabel();
        lblVedor = new javax.swing.JLabel();
        cmbCareer = new javax.swing.JComboBox();
        AutoCompleteDecorator.decorate(cmbCareer);
        btnSearch = new javax.swing.JButton();
        dtFrom = new org.jdesktop.swingx.JXDatePicker();
        dtTo = new org.jdesktop.swingx.JXDatePicker();
        rdoAgent = new javax.swing.JRadioButton();
        rdoCustomer = new javax.swing.JRadioButton();
        rdoAgtCust = new javax.swing.JRadioButton();
        busyIcon = new org.jdesktop.swingx.JXBusyLabel();
        jPanel4 = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        jSeparator2 = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblAccounts = new JXTable(){public Component prepareRenderer(TableCellRenderer renderer,
            int rowIndex, int vColIndex) {
            Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
            if (rowIndex % 2 == 0 && !isCellSelected(rowIndex, vColIndex)) {
                c.setBackground(Color.LIGHT_GRAY);
                c.setForeground(Color.BLACK);
            } 
            return c;
        }
    };
    jPanel3 = new javax.swing.JPanel();
    jLabel7 = new javax.swing.JLabel();
    lblTPFare = new javax.swing.JLabel();
    jLabel10 = new javax.swing.JLabel();
    lblTSFare = new javax.swing.JLabel();
    jLabel12 = new javax.swing.JLabel();
    lblTPOtherCost = new javax.swing.JLabel();
    jLabel14 = new javax.swing.JLabel();
    lblTSOtherChg = new javax.swing.JLabel();
    jLabel16 = new javax.swing.JLabel();
    lblGTPurchase = new javax.swing.JLabel();
    jLabel18 = new javax.swing.JLabel();
    lblGTSales = new javax.swing.JLabel();
    jSeparator1 = new javax.swing.JSeparator();
    lblPurchaseBalance1 = new javax.swing.JLabel();
    jSeparator3 = new javax.swing.JSeparator();
    jLabel9 = new javax.swing.JLabel();
    lblPBalance = new javax.swing.JLabel();
    lblSBalance = new javax.swing.JLabel();
    lblNetRev = new javax.swing.JLabel();
    jLabel3 = new javax.swing.JLabel();
    jLabel4 = new javax.swing.JLabel();
    jLabel15 = new javax.swing.JLabel();
    lblGTPRefund = new javax.swing.JLabel();
    lblTPRefund = new javax.swing.JLabel();
    lblTPOtherRefund = new javax.swing.JLabel();
    jLabel24 = new javax.swing.JLabel();
    jLabel25 = new javax.swing.JLabel();
    jLabel27 = new javax.swing.JLabel();
    lblTSRefund = new javax.swing.JLabel();
    lblTSOtherRfd = new javax.swing.JLabel();
    lblGTSRefund = new javax.swing.JLabel();
    jLabel20 = new javax.swing.JLabel();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getResourceMap(FrameRevenueSummery.class);
    setTitle(resourceMap.getString("Form.title")); // NOI18N
    setName("Form"); // NOI18N

    jToolBar1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    jToolBar1.setFloatable(false);
    jToolBar1.setRollover(true);
    jToolBar1.setName("jToolBar1"); // NOI18N

    javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getActionMap(FrameRevenueSummery.class, this);
    btnSAcDoc.setAction(actionMap.get("viewSAcDoc")); // NOI18N
    btnSAcDoc.setFont(resourceMap.getFont("jButton4.font")); // NOI18N
    btnSAcDoc.setText(resourceMap.getString("btnSAcDoc.text")); // NOI18N
    btnSAcDoc.setFocusable(false);
    btnSAcDoc.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    btnSAcDoc.setName("btnSAcDoc"); // NOI18N
    btnSAcDoc.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar1.add(btnSAcDoc);

    btnPAcDoc.setAction(actionMap.get("viewPAcDoc")); // NOI18N
    btnPAcDoc.setFont(resourceMap.getFont("jButton4.font")); // NOI18N
    btnPAcDoc.setText(resourceMap.getString("btnPAcDoc.text")); // NOI18N
    btnPAcDoc.setFocusable(false);
    btnPAcDoc.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    btnPAcDoc.setName("btnPAcDoc"); // NOI18N
    btnPAcDoc.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar1.add(btnPAcDoc);

    jButton3.setFont(resourceMap.getFont("jButton4.font")); // NOI18N
    jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
    jButton3.setFocusable(false);
    jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jButton3.setName("jButton3"); // NOI18N
    jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar1.add(jButton3);

    jButton4.setFont(resourceMap.getFont("jButton4.font")); // NOI18N
    jButton4.setText(resourceMap.getString("jButton4.text")); // NOI18N
    jButton4.setFocusable(false);
    jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jButton4.setName("jButton4"); // NOI18N
    jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar1.add(jButton4);

    jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    jPanel1.setName("jPanel1"); // NOI18N
    jPanel1.setLayout(new java.awt.GridBagLayout());

    jLabel1.setFont(resourceMap.getFont("lblVedor.font")); // NOI18N
    jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
    jLabel1.setName("jLabel1"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
    jPanel1.add(jLabel1, gridBagConstraints);

    jLabel2.setFont(resourceMap.getFont("lblVedor.font")); // NOI18N
    jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
    jLabel2.setName("jLabel2"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipadx = 18;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
    gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
    jPanel1.add(jLabel2, gridBagConstraints);

    cmbAgent.setFont(resourceMap.getFont("rdoCustomer.font")); // NOI18N
    cmbAgent.setEnabled(false);
    cmbAgent.setName("cmbAgent"); // NOI18N
    cmbAgent.setPreferredSize(new java.awt.Dimension(80, 20));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 4;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 0.4;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel1.add(cmbAgent, gridBagConstraints);

    jLabel6.setFont(resourceMap.getFont("rdoCustomer.font")); // NOI18N
    jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
    jLabel6.setName("jLabel6"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel1.add(jLabel6, gridBagConstraints);

    lblVedor.setFont(resourceMap.getFont("lblVedor.font")); // NOI18N
    lblVedor.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    lblVedor.setText(resourceMap.getString("lblVedor.text")); // NOI18N
    lblVedor.setName("lblVedor"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel1.add(lblVedor, gridBagConstraints);

    cmbCareer.setFont(resourceMap.getFont("lblVedor.font")); // NOI18N
    cmbCareer.setName("cmbCareer"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 0.4;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel1.add(cmbCareer, gridBagConstraints);

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
    gridBagConstraints.gridx = 6;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel1.add(btnSearch, gridBagConstraints);

    dtFrom.setFont(resourceMap.getFont("lblVedor.font")); // NOI18N
    dtFrom.setName("dtFrom"); // NOI18N
    dtFrom.setPreferredSize(new java.awt.Dimension(80, 20));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 0.3;
    gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
    jPanel1.add(dtFrom, gridBagConstraints);

    dtTo.setFont(resourceMap.getFont("lblVedor.font")); // NOI18N
    dtTo.setName("dtTo"); // NOI18N
    dtTo.setPreferredSize(new java.awt.Dimension(80, 20));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 0.3;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel1.add(dtTo, gridBagConstraints);

    buttonGroup1.add(rdoAgent);
    rdoAgent.setFont(resourceMap.getFont("rdoCustomer.font")); // NOI18N
    rdoAgent.setText(resourceMap.getString("rdoAgent.text")); // NOI18N
    rdoAgent.setName("rdoAgent"); // NOI18N
    rdoAgent.addActionListener(rdoAgentListener);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 2);
    jPanel1.add(rdoAgent, gridBagConstraints);

    buttonGroup1.add(rdoCustomer);
    rdoCustomer.setFont(resourceMap.getFont("rdoCustomer.font")); // NOI18N
    rdoCustomer.setText(resourceMap.getString("rdoCustomer.text")); // NOI18N
    rdoCustomer.setName("rdoCustomer"); // NOI18N
    rdoCustomer.addActionListener(rdoCustomerListener);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 2);
    jPanel1.add(rdoCustomer, gridBagConstraints);

    buttonGroup1.add(rdoAgtCust);
    rdoAgtCust.setFont(resourceMap.getFont("rdoCustomer.font")); // NOI18N
    rdoAgtCust.setSelected(true);
    rdoAgtCust.setText(resourceMap.getString("rdoAgtCust.text")); // NOI18N
    rdoAgtCust.setName("rdoAgtCust"); // NOI18N
    rdoAgtCust.addActionListener(rdoAgtCustListener);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 2);
    jPanel1.add(rdoAgtCust, gridBagConstraints);

    busyIcon.setText(resourceMap.getString("busyIcon.text")); // NOI18N
    busyIcon.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
    busyIcon.setName("busyIcon"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 6;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel1.add(busyIcon, gridBagConstraints);

    jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    jPanel4.setName("jPanel4"); // NOI18N
    jPanel4.setLayout(new java.awt.GridBagLayout());

    progressBar.setName("progressBar"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel4.add(progressBar, gridBagConstraints);

    jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
    jSeparator2.setName("jSeparator2"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
    gridBagConstraints.ipadx = 4;
    gridBagConstraints.ipady = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
    gridBagConstraints.weighty = 1.0;
    jPanel4.add(jSeparator2, gridBagConstraints);

    statusMessageLabel.setText(resourceMap.getString("statusMessageLabel.text")); // NOI18N
    statusMessageLabel.setName("statusMessageLabel"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 0);
    jPanel4.add(statusMessageLabel, gridBagConstraints);

    jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    jPanel2.setName("jPanel2"); // NOI18N
    jPanel2.setLayout(new java.awt.GridBagLayout());

    jScrollPane1.setName("jScrollPane1"); // NOI18N

    tblAccounts.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {

        },
        new String [] {
            "PNR", "AirLine", "NoOfPax", "SInvoiceRef", "SInvDate", "SellingFare", "SOther", "SOtherChg(+/-)", "SellingTotal", "PReceived", "SalesDue", "NetPurchase", "BillPaid", "PDue", "TktRevenue", "OSRevenue", "OChgRevenue", "RevAdm", "NetRevenue", "CurrentRevenue"
        }
    ) {
        boolean[] canEdit = new boolean [] {
            false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
        };

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit [columnIndex];
        }
    });
    tblAccounts.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
    tblAccounts.setFont(resourceMap.getFont("tblAccounts.font")); // NOI18N
    tblAccounts.setName("tblAccounts"); // NOI18N
    tblAccounts.setSortable(false);
    tblAccounts.getTableHeader().setReorderingAllowed(false);
    jScrollPane1.setViewportView(tblAccounts);
    tblAccounts.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblAccounts.columnModel.title0")); // NOI18N
    tblAccounts.getColumnModel().getColumn(1).setMinWidth(50);
    tblAccounts.getColumnModel().getColumn(1).setPreferredWidth(50);
    tblAccounts.getColumnModel().getColumn(1).setMaxWidth(50);
    tblAccounts.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblAccounts.columnModel.title1")); // NOI18N
    tblAccounts.getColumnModel().getColumn(2).setMinWidth(60);
    tblAccounts.getColumnModel().getColumn(2).setPreferredWidth(60);
    tblAccounts.getColumnModel().getColumn(2).setMaxWidth(60);
    tblAccounts.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblAccounts.columnModel.title2")); // NOI18N
    tblAccounts.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("jXTable1.columnModel.title7")); // NOI18N
    tblAccounts.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("tblAccounts.columnModel.title5")); // NOI18N
    tblAccounts.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("jXTable1.columnModel.title8")); // NOI18N
    tblAccounts.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("tblAccounts.columnModel.title17")); // NOI18N
    tblAccounts.getColumnModel().getColumn(7).setHeaderValue(resourceMap.getString("jXTable1.columnModel.title9")); // NOI18N
    tblAccounts.getColumnModel().getColumn(8).setHeaderValue(resourceMap.getString("jXTable1.columnModel.title10")); // NOI18N
    tblAccounts.getColumnModel().getColumn(9).setHeaderValue(resourceMap.getString("tblAccounts.columnModel.title12")); // NOI18N
    tblAccounts.getColumnModel().getColumn(10).setHeaderValue(resourceMap.getString("tblAccounts.columnModel.title13")); // NOI18N
    tblAccounts.getColumnModel().getColumn(11).setHeaderValue(resourceMap.getString("jXTable1.columnModel.title6")); // NOI18N
    tblAccounts.getColumnModel().getColumn(12).setHeaderValue(resourceMap.getString("tblAccounts.columnModel.title14")); // NOI18N
    tblAccounts.getColumnModel().getColumn(13).setHeaderValue(resourceMap.getString("tblAccounts.columnModel.title15")); // NOI18N
    tblAccounts.getColumnModel().getColumn(14).setHeaderValue(resourceMap.getString("jXTable1.columnModel.title12")); // NOI18N
    tblAccounts.getColumnModel().getColumn(15).setHeaderValue(resourceMap.getString("tblAccounts.columnModel.title18")); // NOI18N
    tblAccounts.getColumnModel().getColumn(16).setHeaderValue(resourceMap.getString("tblAccounts.columnModel.title17")); // NOI18N
    tblAccounts.getColumnModel().getColumn(17).setHeaderValue(resourceMap.getString("tblAccounts.columnModel.title20")); // NOI18N
    tblAccounts.getColumnModel().getColumn(18).setHeaderValue(resourceMap.getString("tblAccounts.columnModel.title19")); // NOI18N
    tblAccounts.getColumnModel().getColumn(19).setHeaderValue(resourceMap.getString("tblAccounts.columnModel.title16")); // NOI18N

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel2.add(jScrollPane1, gridBagConstraints);

    jPanel3.setBackground(resourceMap.getColor("jPanel3.background")); // NOI18N
    jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
    jPanel3.setName("jPanel3"); // NOI18N
    jPanel3.setLayout(new java.awt.GridBagLayout());

    jLabel7.setFont(resourceMap.getFont("lblTPRefund.font")); // NOI18N
    jLabel7.setForeground(resourceMap.getColor("jLabel16.foreground")); // NOI18N
    jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
    jLabel7.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
    jLabel7.setName("jLabel7"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.ipadx = 28;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel3.add(jLabel7, gridBagConstraints);

    lblTPFare.setFont(resourceMap.getFont("lblTPRefund.font")); // NOI18N
    lblTPFare.setText(resourceMap.getString("lblTPFare.text")); // NOI18N
    lblTPFare.setName("lblTPFare"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 0.5;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel3.add(lblTPFare, gridBagConstraints);

    jLabel10.setFont(resourceMap.getFont("lblTSRefund.font")); // NOI18N
    jLabel10.setForeground(resourceMap.getColor("jLabel18.foreground")); // NOI18N
    jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
    jLabel10.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
    jLabel10.setName("jLabel10"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel3.add(jLabel10, gridBagConstraints);

    lblTSFare.setFont(resourceMap.getFont("lblTSRefund.font")); // NOI18N
    lblTSFare.setForeground(resourceMap.getColor("lblTSFare.foreground")); // NOI18N
    lblTSFare.setText(resourceMap.getString("lblTSFare.text")); // NOI18N
    lblTSFare.setName("lblTSFare"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 0.5;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel3.add(lblTSFare, gridBagConstraints);

    jLabel12.setFont(resourceMap.getFont("lblTPRefund.font")); // NOI18N
    jLabel12.setForeground(resourceMap.getColor("jLabel16.foreground")); // NOI18N
    jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
    jLabel12.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
    jLabel12.setName("jLabel12"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel3.add(jLabel12, gridBagConstraints);

    lblTPOtherCost.setFont(resourceMap.getFont("lblTPRefund.font")); // NOI18N
    lblTPOtherCost.setForeground(resourceMap.getColor("lblTPOtherCost.foreground")); // NOI18N
    lblTPOtherCost.setText(resourceMap.getString("lblTPOtherCost.text")); // NOI18N
    lblTPOtherCost.setName("lblTPOtherCost"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 0.5;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel3.add(lblTPOtherCost, gridBagConstraints);

    jLabel14.setFont(resourceMap.getFont("lblTSRefund.font")); // NOI18N
    jLabel14.setForeground(resourceMap.getColor("jLabel18.foreground")); // NOI18N
    jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    jLabel14.setText(resourceMap.getString("jLabel14.text")); // NOI18N
    jLabel14.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
    jLabel14.setName("jLabel14"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel3.add(jLabel14, gridBagConstraints);

    lblTSOtherChg.setFont(resourceMap.getFont("lblTSRefund.font")); // NOI18N
    lblTSOtherChg.setText(resourceMap.getString("lblTSOtherChg.text")); // NOI18N
    lblTSOtherChg.setName("lblTSOtherChg"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 0.5;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel3.add(lblTSOtherChg, gridBagConstraints);

    jLabel16.setFont(resourceMap.getFont("lblTPRefund.font")); // NOI18N
    jLabel16.setForeground(resourceMap.getColor("jLabel16.foreground")); // NOI18N
    jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    jLabel16.setText(resourceMap.getString("jLabel16.text")); // NOI18N
    jLabel16.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
    jLabel16.setName("jLabel16"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.ipadx = 5;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel3.add(jLabel16, gridBagConstraints);

    lblGTPurchase.setFont(resourceMap.getFont("lblTPRefund.font")); // NOI18N
    lblGTPurchase.setText(resourceMap.getString("lblGTPurchase.text")); // NOI18N
    lblGTPurchase.setName("lblGTPurchase"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 0.5;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel3.add(lblGTPurchase, gridBagConstraints);

    jLabel18.setFont(resourceMap.getFont("lblTSRefund.font")); // NOI18N
    jLabel18.setForeground(resourceMap.getColor("jLabel18.foreground")); // NOI18N
    jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    jLabel18.setText(resourceMap.getString("jLabel18.text")); // NOI18N
    jLabel18.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
    jLabel18.setName("jLabel18"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel3.add(jLabel18, gridBagConstraints);

    lblGTSales.setFont(resourceMap.getFont("lblTSRefund.font")); // NOI18N
    lblGTSales.setText(resourceMap.getString("lblGTSales.text")); // NOI18N
    lblGTSales.setName("lblGTSales"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 0.5;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel3.add(lblGTSales, gridBagConstraints);

    jSeparator1.setName("jSeparator1"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.gridwidth = 6;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipady = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
    jPanel3.add(jSeparator1, gridBagConstraints);

    lblPurchaseBalance1.setFont(resourceMap.getFont("lblTPRefund.font")); // NOI18N
    lblPurchaseBalance1.setForeground(resourceMap.getColor("lblPurchaseBalance1.foreground")); // NOI18N
    lblPurchaseBalance1.setText(resourceMap.getString("lblPurchaseBalance1.text")); // NOI18N
    lblPurchaseBalance1.setName("lblPurchaseBalance1"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 7;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel3.add(lblPurchaseBalance1, gridBagConstraints);

    jSeparator3.setName("jSeparator3"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 8;
    gridBagConstraints.gridwidth = 6;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipady = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
    jPanel3.add(jSeparator3, gridBagConstraints);

    jLabel9.setFont(resourceMap.getFont("lblTPRefund.font")); // NOI18N
    jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
    jLabel9.setName("jLabel9"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 10;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel3.add(jLabel9, gridBagConstraints);

    lblPBalance.setFont(resourceMap.getFont("lblTPRefund.font")); // NOI18N
    lblPBalance.setForeground(resourceMap.getColor("lblPBalance.foreground")); // NOI18N
    lblPBalance.setText(resourceMap.getString("lblPBalance.text")); // NOI18N
    lblPBalance.setName("lblPBalance"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 7;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 0.5;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel3.add(lblPBalance, gridBagConstraints);

    lblSBalance.setFont(resourceMap.getFont("lblTSRefund.font")); // NOI18N
    lblSBalance.setForeground(resourceMap.getColor("lblSBalance.foreground")); // NOI18N
    lblSBalance.setText(resourceMap.getString("lblSBalance.text")); // NOI18N
    lblSBalance.setName("lblSBalance"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 7;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 0.5;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel3.add(lblSBalance, gridBagConstraints);

    lblNetRev.setFont(resourceMap.getFont("lblTPRefund.font")); // NOI18N
    lblNetRev.setText(resourceMap.getString("lblNetRev.text")); // NOI18N
    lblNetRev.setName("lblNetRev"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 10;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel3.add(lblNetRev, gridBagConstraints);

    jLabel3.setFont(resourceMap.getFont("lblTPRefund.font")); // NOI18N
    jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
    jLabel3.setName("jLabel3"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel3.add(jLabel3, gridBagConstraints);

    jLabel4.setFont(resourceMap.getFont("lblTPRefund.font")); // NOI18N
    jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
    jLabel4.setName("jLabel4"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel3.add(jLabel4, gridBagConstraints);

    jLabel15.setFont(resourceMap.getFont("lblTPRefund.font")); // NOI18N
    jLabel15.setText(resourceMap.getString("jLabel15.text")); // NOI18N
    jLabel15.setName("jLabel15"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel3.add(jLabel15, gridBagConstraints);

    lblGTPRefund.setFont(resourceMap.getFont("lblTPRefund.font")); // NOI18N
    lblGTPRefund.setText(resourceMap.getString("lblGTPRefund.text")); // NOI18N
    lblGTPRefund.setName("lblGTPRefund"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 0.5;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel3.add(lblGTPRefund, gridBagConstraints);

    lblTPRefund.setFont(resourceMap.getFont("lblTPRefund.font")); // NOI18N
    lblTPRefund.setText(resourceMap.getString("lblTPRefund.text")); // NOI18N
    lblTPRefund.setName("lblTPRefund"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 0.5;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel3.add(lblTPRefund, gridBagConstraints);

    lblTPOtherRefund.setFont(resourceMap.getFont("lblTPRefund.font")); // NOI18N
    lblTPOtherRefund.setText(resourceMap.getString("lblTPOtherRefund.text")); // NOI18N
    lblTPOtherRefund.setName("lblTPOtherRefund"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 0.5;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel3.add(lblTPOtherRefund, gridBagConstraints);

    jLabel24.setFont(resourceMap.getFont("lblTSRefund.font")); // NOI18N
    jLabel24.setText(resourceMap.getString("jLabel24.text")); // NOI18N
    jLabel24.setName("jLabel24"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    jPanel3.add(jLabel24, gridBagConstraints);

    jLabel25.setFont(resourceMap.getFont("lblTSRefund.font")); // NOI18N
    jLabel25.setText(resourceMap.getString("jLabel25.text")); // NOI18N
    jLabel25.setName("jLabel25"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    jPanel3.add(jLabel25, gridBagConstraints);

    jLabel27.setFont(resourceMap.getFont("lblTSRefund.font")); // NOI18N
    jLabel27.setText(resourceMap.getString("jLabel27.text")); // NOI18N
    jLabel27.setName("jLabel27"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    jPanel3.add(jLabel27, gridBagConstraints);

    lblTSRefund.setFont(resourceMap.getFont("lblTSRefund.font")); // NOI18N
    lblTSRefund.setText(resourceMap.getString("lblTSRefund.text")); // NOI18N
    lblTSRefund.setName("lblTSRefund"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 0.5;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel3.add(lblTSRefund, gridBagConstraints);

    lblTSOtherRfd.setFont(resourceMap.getFont("lblTSRefund.font")); // NOI18N
    lblTSOtherRfd.setText(resourceMap.getString("lblTSOtherRfd.text")); // NOI18N
    lblTSOtherRfd.setName("lblTSOtherRfd"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 0.5;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel3.add(lblTSOtherRfd, gridBagConstraints);

    lblGTSRefund.setFont(resourceMap.getFont("lblTSRefund.font")); // NOI18N
    lblGTSRefund.setText(resourceMap.getString("lblGTSRefund.text")); // NOI18N
    lblGTSRefund.setName("lblGTSRefund"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 0.5;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel3.add(lblGTSRefund, gridBagConstraints);

    jLabel20.setFont(resourceMap.getFont("lblTSRefund.font")); // NOI18N
    jLabel20.setForeground(resourceMap.getColor("jLabel20.foreground")); // NOI18N
    jLabel20.setText(resourceMap.getString("jLabel20.text")); // NOI18N
    jLabel20.setName("jLabel20"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 7;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    jPanel3.add(jLabel20, gridBagConstraints);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 1111, Short.MAX_VALUE)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 1111, Short.MAX_VALUE)
                .addGroup(layout.createSequentialGroup()
                    .addGap(2, 2, 2)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 527, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 154, Short.MAX_VALUE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 428, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 1109, Short.MAX_VALUE))))
            .addGap(0, 0, 0))
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(4, 4, 4)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createSequentialGroup()
                    .addGap(2, 2, 2)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)
            .addGap(4, 4, 4)
            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(0, 0, 0))
    );

    pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        new Thread (new threadAccountsReport()).start();
    }//GEN-LAST:event_btnSearchActionPerformed

    /**
    * @param args the command line arguments
    */
  /*  public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrameRevenueSummery().setVisible(true);
            }
        });
    }*/

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnPAcDoc;
    private javax.swing.JButton btnSAcDoc;
    private javax.swing.JButton btnSearch;
    private org.jdesktop.swingx.JXBusyLabel busyIcon;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JComboBox cmbAgent;
    private javax.swing.JComboBox cmbCareer;
    private org.jdesktop.swingx.JXDatePicker dtFrom;
    private org.jdesktop.swingx.JXDatePicker dtTo;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblGTPRefund;
    private javax.swing.JLabel lblGTPurchase;
    private javax.swing.JLabel lblGTSRefund;
    private javax.swing.JLabel lblGTSales;
    private javax.swing.JLabel lblNetRev;
    private javax.swing.JLabel lblPBalance;
    private javax.swing.JLabel lblPurchaseBalance1;
    private javax.swing.JLabel lblSBalance;
    private javax.swing.JLabel lblTPFare;
    private javax.swing.JLabel lblTPOtherCost;
    private javax.swing.JLabel lblTPOtherRefund;
    private javax.swing.JLabel lblTPRefund;
    private javax.swing.JLabel lblTSFare;
    private javax.swing.JLabel lblTSOtherChg;
    private javax.swing.JLabel lblTSOtherRfd;
    private javax.swing.JLabel lblTSRefund;
    private javax.swing.JLabel lblVedor;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JRadioButton rdoAgent;
    private javax.swing.JRadioButton rdoAgtCust;
    private javax.swing.JRadioButton rdoCustomer;
    private javax.swing.JLabel statusMessageLabel;
    private org.jdesktop.swingx.JXTable tblAccounts;
    // End of variables declaration//GEN-END:variables

    private long startTime = 0;
    private long stopTime = 0;
    private float elapsedTime = 0;

    private class threadAccountsReport implements Runnable {

        private int noOfRecord;

        public threadAccountsReport() {
        }

        public void run() {
            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();

            statusMessageLabel.setText("Loading Invoice...");
            btnSearch.setEnabled(false);
            accountsReportByCriteria();
            //noOfRecord = invoices.size();
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

    private class threadLoadCompletePInvoice implements Runnable {

        private long pAcDocId;

        public threadLoadCompletePInvoice(long pAcDocId) {
            this.pAcDocId = pAcDocId;
        }

        public void run() {
            progressBar = new JProgressBar();
            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();

            statusMessageLabel.setText("Loading Invoice...");
            acDocBo.findPurchaseAcDocById(pAcDocId);
            pAcDoc = acDocBo.getPurchaseAccountingDocument();
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            progressBar.repaint(); //Refresh graphics
        }
    }

    private class threadFindCareers implements Runnable {

        public threadFindCareers() {
        }

        public void run() {        
        busyIcon.setBusy(true);
        populateCmbCareer();
        busyIcon.setBusy(false);        
        }
    } 
    
    /**
* Applied background and foreground color to single column of a JTable
* in order to distinguish it apart from other columns.
*/ 
    class ColorColumnRenderer extends DefaultTableCellRenderer {

        Color bkgndColor, fgndColor;

        public ColorColumnRenderer(Color bkgnd, Color foregnd) {
            super();
            bkgndColor = bkgnd;
            fgndColor = foregnd;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            cell.setBackground(bkgndColor);
            cell.setForeground(fgndColor);

            return cell;
        }
    }
}
