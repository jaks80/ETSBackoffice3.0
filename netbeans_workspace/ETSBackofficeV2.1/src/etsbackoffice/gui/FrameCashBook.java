package etsbackoffice.gui;

import etsbackoffice.ETSBackofficeApp;
import etsbackoffice.businesslogic.AcTransactionBo;
import etsbackoffice.businesslogic.AgentBo;
import etsbackoffice.businesslogic.CustomerBo;
import etsbackoffice.businesslogic.DateFormat;
import etsbackoffice.businesslogic.Enums;
import etsbackoffice.businesslogic.UserBo;
import etsbackoffice.domain.AcTransaction;
import etsbackoffice.domain.Agent;
import etsbackoffice.domain.Customer;
import etsbackoffice.domain.User;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.font.TextAttribute;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JProgressBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 *
 * @author Yusuf
 */
public class FrameCashBook extends javax.swing.JFrame{

    AcTransactionBo acTransactionBo = (AcTransactionBo) ETSBackofficeApp.getApplication().ctx.getBean("acTransactionBo");
    AgentBo agentBo = (AgentBo) ETSBackofficeApp.getApplication().ctx.getBean("agentBo");
    CustomerBo customerBo = (CustomerBo) ETSBackofficeApp.getApplication().ctx.getBean("customerBo");
    UserBo userBo = (UserBo) ETSBackofficeApp.getApplication().ctx.getBean("userBo");
    List<Agent> agents = new ArrayList();
    List<Customer> customers = new ArrayList();
    List<User> users = new ArrayList();
    List<AcTransaction> transactions = new ArrayList<AcTransaction>();
    List<AcTransaction> cashTransactions = new ArrayList<AcTransaction>();
    List<AcTransaction> chequeTransactions = new ArrayList<AcTransaction>();
    List<AcTransaction> dCardTransactions = new ArrayList<AcTransaction>();
    List<AcTransaction> cCardTransactions = new ArrayList<AcTransaction>();
    List<AcTransaction> bDepositTransactions = new ArrayList<AcTransaction>();
    List<AcTransaction> oTransferTransactions = new ArrayList<AcTransaction>();
    List<AcTransaction> aAdjustmentTransactions = new ArrayList<AcTransaction>();
    BigDecimal totalCash = new BigDecimal("0.00");
    BigDecimal totalCheque = new BigDecimal("0.00");
    BigDecimal totalDC = new BigDecimal("0.00");
    BigDecimal totalCC = new BigDecimal("0.00");
    BigDecimal totalBD = new BigDecimal("0.00");
    BigDecimal totalOT = new BigDecimal("0.00");
    BigDecimal totalAA = new BigDecimal("0.00");
    private DateFormat df = new DateFormat();
    private Date from = null, to = null;
    private Integer tType = null;
    private Long userId = null;
    private Long contactableId = null;
    private Integer acDocId = null;
    private Integer oAcDocId = -1;
    private int contType = 0;

    /** Creates new form FrameCashBook */
    public FrameCashBook(java.awt.Frame parent) {
        initComponents();       
        populateCmbCashier();
        populateCmbTType();
    }

    private void populateCmbTType() {
        DefaultComboBoxModel tTypeModel = new DefaultComboBoxModel(Enums.TransType.values());
        cmbTType.setModel(tTypeModel);
        cmbTType.insertItemAt("All", 0);
        cmbTType.setSelectedIndex(0);
    }

    private void populateCmbCashier() {
        this.users = userBo.loadAll();
        List userName = new ArrayList();
        for (int i = 0; i < this.users.size(); i++) {
            userName.add(this.users.get(i).getSurName() + " " + this.users.get(i).getForeName()
                    + "-" + this.users.get(i).getUserId());
        }

        DefaultComboBoxModel cmbCashierModel = new DefaultComboBoxModel(userName.toArray());
        cmbCashierModel.insertElementAt("All", 0);
        cmbCashier.setModel(cmbCashierModel);

        cmbCashier.setSelectedIndex(0);
    }

    private void populateTransaction() {
        tabsTrans.setSelectedIndex(0);
        //transactions.clear();
        cashTransactions.clear();
        chequeTransactions.clear();
        dCardTransactions.clear();
        cCardTransactions.clear();
        bDepositTransactions.clear();
        oTransferTransactions.clear();
        aAdjustmentTransactions.clear();

        populateTblTransaction(this.transactions);
        for (AcTransaction t : transactions) {
            if (t.getTransType() == 1 && t.isActive()) {
                totalCash = totalCash.add(t.getTransAmount());
                cashTransactions.add(t);
            } else if (t.getTransType() == 2 && t.isActive()) {
                totalCheque = totalCheque.add(t.getTransAmount());
                chequeTransactions.add(t);
            } else if (t.getTransType() == 3 && t.isActive()) {
                totalCC = totalCC.add(t.getTransAmount());
                cCardTransactions.add(t);                
            } else if (t.getTransType() == 4 && t.isActive()) {
                totalDC = totalDC.add(t.getTransAmount());
                dCardTransactions.add(t);
            } else if (t.getTransType() == 5 && t.isActive()) {
                totalBD = totalBD.add(t.getTransAmount());
                bDepositTransactions.add(t);
            } else if (t.getTransType() == 6 && t.isActive()) {
                totalOT = totalOT.add(t.getTransAmount());
                oTransferTransactions.add(t);
            } else if (t.getTransType() == 7 && t.isActive()) {
                totalAA = totalAA.add(t.getTransAmount());
                aAdjustmentTransactions.add(t);
            }
        }

        lblNoCashT.setText(": " + String.valueOf(cashTransactions.size()));
        lblNoChequeT.setText(": " + String.valueOf(chequeTransactions.size()));
        lblNoDCT.setText(": " + String.valueOf(dCardTransactions.size()));
        lblNoCCT.setText(": " + String.valueOf(cCardTransactions.size()));
        lblNoBDT.setText(": " + String.valueOf(bDepositTransactions.size()));
        lblNoOT.setText(": " + String.valueOf(oTransferTransactions.size()));
        lblNoAAT.setText(": " + String.valueOf(aAdjustmentTransactions.size()));

        lblTotalCashT.setText(": " + String.valueOf(totalCash));
        lblTotalChequeT.setText(": " + String.valueOf(totalCheque));
        lblTotalDCT.setText(": " + String.valueOf(totalDC));
        lblTotalCCT.setText(": " + String.valueOf(totalCC));
        lblTotalBDT.setText(": " + String.valueOf(totalBD));
        lblTotalOT.setText(": " + String.valueOf(totalOT));
        lblTotalAAT.setText(": " + String.valueOf(totalAA));

        lblGTotalT.setText(": " + String.valueOf(this.transactions.size()));
        lblGTotalValue.setText(": " + String.valueOf(totalCash.add(totalCheque).
                add(totalDC).add(totalCC).add(totalBD).add(totalOT).add(totalAA)));
    }

    private void populateTblTransaction(List<AcTransaction> transactions) {
        DefaultTableModel transModel = (DefaultTableModel) tblTransaction.getModel();

        transModel.getDataVector().removeAllElements();
        tblTransaction.repaint();

        for (AcTransaction t : transactions) {
            int row = 0;
            String acDocFor = "";
            String acDocRef = "";
            String pnr = "";
            String tStatus = "";
            
            if (t.getAccountingDocument() != null) {
                acDocFor = t.getAccountingDocument().getAcDocFor();
                acDocRef = t.getAccountingDocument().getAcDocRefString();
                pnr = t.getAccountingDocument().getPnr().getGdsPNR();
            } else {
                acDocFor = t.getoAccountingDocument().getAcDocFor();
                acDocRef = t.getoAccountingDocument().getAcDocRefString();
            }

            if (t.isActive()) {
                tStatus = "LIVE";
            } else if (!t.isActive()) {
                tStatus = "VOID";
            }
                
            transModel.insertRow(row, new Object[]{df.dateForGui(t.getTransDate()), t.getTransTypeString(), t.getTransAmount(),
                        t.getUser().getForeName(), acDocFor, acDocRef,pnr, t.getTransRef(),tStatus});
            row++;
        }
    }

    private void populateTblCashTransaction(List<AcTransaction> cashTransactions) {
        DefaultTableModel cashModel = (DefaultTableModel) tblCash.getModel();

        cashModel.getDataVector().removeAllElements();
        tblCash.repaint();

        for (AcTransaction t : cashTransactions) {
            int row = 0;
            String acDocFor = "";
            String acDocRef = "";
            String pnr = "";
            String tStatus = "";
            
            if (t.getAccountingDocument() != null) {
                acDocFor = t.getAccountingDocument().getAcDocFor();
                acDocRef = t.getAccountingDocument().getAcDocRefString();
                pnr = t.getAccountingDocument().getPnr().getGdsPNR();
            } else {
                acDocFor = t.getoAccountingDocument().getAcDocFor();
                acDocRef = t.getoAccountingDocument().getAcDocRefString();
            }
            cashModel.insertRow(row, new Object[]{df.dateForGui(t.getTransDate()), t.getTransTypeString(), t.getTransAmount(),
                        t.getUser().getForeName(), acDocFor, acDocRef,pnr, t.getTransRef()});
            row++;
        }
    }

    private void populateTblChequeTransaction(List<AcTransaction> chequeTransactions) {
        DefaultTableModel chequeModel = (DefaultTableModel) tblCheque.getModel();
        chequeModel.getDataVector().removeAllElements();
        tblCheque.repaint();

        for (AcTransaction t : chequeTransactions) {
            int row = 0;
            chequeModel.insertRow(row, new Object[]{df.dateForGui(t.getTransDate()), t.getTransTypeString(), t.getTransAmount(),
                        t.getUser().getForeName(), t.getAccountingDocument().getAcDocFor(), t.getAccountingDocument().getAcDocRefString(),
                        t.getPnr().getGdsPNR(), t.getTransRef()});
            row++;
        }
    }

    private void populateTblDCardTransaction(List<AcTransaction> dCardTransactions) {
        DefaultTableModel dCardModel = (DefaultTableModel) tblCCard.getModel();
        dCardModel.getDataVector().removeAllElements();
        tblCCard.repaint();

        for (AcTransaction t : dCardTransactions) {
            int row = 0;
            dCardModel.insertRow(row, new Object[]{df.dateForGui(t.getTransDate()), t.getTransTypeString(), t.getTransAmount(),
                        t.getUser().getForeName(), t.getAccountingDocument().getAcDocFor(), t.getAccountingDocument().getAcDocRefString(),
                        t.getPnr().getGdsPNR(), t.getTransRef()});
            row++;
        }
    }

    private void searchTransaction() {

        String[] data;

        if (cmbContactable.getSelectedIndex() > 0) {
            data = cmbContactable.getSelectedItem().toString().split("-");
            contactableId = Long.parseLong(data[2]);
        } else {
            contactableId = null;
        }

        from = dtFrom.getDate();
        to = dtTo.getDate();

        if (rdoAgent.isSelected()) {
            contType = 1;
        } else if (rdoCustomer.isSelected()) {
            contType = 2;
        } else if (rdoAgtCust.isSelected()) {
            contType = 0;
        }

        if (rdoTicketing.isSelected()) {
            acDocId = -1;
            oAcDocId = null;
        } else if (rdoOther.isSelected()) {
            acDocId = null;
            oAcDocId = -1;
        } else if (rdoAllClass.isSelected()) {
            acDocId = null;
            oAcDocId = null;
        }

        if (cmbCashier.getSelectedIndex() > 0) {
            data = cmbCashier.getSelectedItem().toString().split("-");
            userId = Long.parseLong(data[1]);
        } else {
            userId = null;
        }
        if (cmbTType.getSelectedIndex() > 0) {
            tType = Enums.TransType.valueOf(cmbTType.getSelectedItem().toString()).getId();
        } else {
            tType = null;
        }
        //tType = acTransactionBo.getTransTypeInteger(cmbTType.getSelectedItem().toString());

        this.transactions = acTransactionBo.loadTransactionsByCriteria(
                from, to, tType, userId, contactableId, contType, acDocId, oAcDocId);
    }

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
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jSeparator2 = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        pnlSearch = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        dtFrom = new org.jdesktop.swingx.JXDatePicker();
        dtTo = new org.jdesktop.swingx.JXDatePicker();
        cmbContactable = new javax.swing.JComboBox();
        btnSearch = new javax.swing.JButton();
        rdoAgent = new javax.swing.JRadioButton();
        rdoCustomer = new javax.swing.JRadioButton();
        rdoAgtCust = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel6 = new javax.swing.JLabel();
        jSeparator5 = new javax.swing.JSeparator();
        jLabel7 = new javax.swing.JLabel();
        jSeparator6 = new javax.swing.JSeparator();
        cmbCashier = new javax.swing.JComboBox();
        busyIcon = new org.jdesktop.swingx.JXBusyLabel();
        busyIcon1 = new org.jdesktop.swingx.JXBusyLabel();
        cmbTType = new javax.swing.JComboBox();
        rdoTicketing = new javax.swing.JRadioButton();
        rdoOther = new javax.swing.JRadioButton();
        jSeparator8 = new javax.swing.JSeparator();
        jLabel13 = new javax.swing.JLabel();
        rdoAllClass = new javax.swing.JRadioButton();
        jPanel9 = new javax.swing.JPanel();
        tabsTrans = new javax.swing.JTabbedPane();
        tabsTrans.addChangeListener(tabsTransListener);
        jPanel11 = new javax.swing.JPanel();
        jScrollPane9 = new javax.swing.JScrollPane();
        tblTransaction = new JXTable(){public Component prepareRenderer(TableCellRenderer renderer,
            int rowIndex, int vColIndex) {
            Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);  
            String s = "";       

            Object o = tblTransaction.getModel().getValueAt(rowIndex, 8);       

            if(o!=null){
                s = o.toString();
            }

            if(s.equalsIgnoreCase("VOID")){           
                Map  attributes = c.getFont().getAttributes();
                attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
                Font newFont = new Font(attributes);
                c.setFont(newFont);
                //c.setForeground(Color.WHITE);
            }
            return c;
        }
    };
    jPanel1 = new javax.swing.JPanel();
    jScrollPane2 = new javax.swing.JScrollPane();
    tblCash = new org.jdesktop.swingx.JXTable();
    jPanel2 = new javax.swing.JPanel();
    jScrollPane3 = new javax.swing.JScrollPane();
    tblCheque = new org.jdesktop.swingx.JXTable();
    jPanel3 = new javax.swing.JPanel();
    jScrollPane4 = new javax.swing.JScrollPane();
    tblDCard = new org.jdesktop.swingx.JXTable();
    jPanel5 = new javax.swing.JPanel();
    jScrollPane5 = new javax.swing.JScrollPane();
    tblCCard = new org.jdesktop.swingx.JXTable();
    jPanel6 = new javax.swing.JPanel();
    jScrollPane6 = new javax.swing.JScrollPane();
    tblBDeposit = new org.jdesktop.swingx.JXTable();
    jPanel7 = new javax.swing.JPanel();
    jScrollPane7 = new javax.swing.JScrollPane();
    tblOTransfer = new org.jdesktop.swingx.JXTable();
    jPanel8 = new javax.swing.JPanel();
    jScrollPane8 = new javax.swing.JScrollPane();
    tblAAdjustment = new org.jdesktop.swingx.JXTable();
    jPanel10 = new javax.swing.JPanel();
    Chash = new javax.swing.JLabel();
    jLabel9 = new javax.swing.JLabel();
    jLabel10 = new javax.swing.JLabel();
    jLabel11 = new javax.swing.JLabel();
    lblNoCashT = new javax.swing.JLabel();
    lblNoChequeT = new javax.swing.JLabel();
    lblNoDCT = new javax.swing.JLabel();
    jLabel15 = new javax.swing.JLabel();
    lblTotalCashT = new javax.swing.JLabel();
    lblTotalChequeT = new javax.swing.JLabel();
    lblTotalDCT = new javax.swing.JLabel();
    lblNoBDT = new javax.swing.JLabel();
    jLabel20 = new javax.swing.JLabel();
    lblGTotalT = new javax.swing.JLabel();
    lblNoAAT1 = new javax.swing.JLabel();
    lblNoAAT = new javax.swing.JLabel();
    lblTotalBDT = new javax.swing.JLabel();
    lblNoOT = new javax.swing.JLabel();
    lblTotalOT = new javax.swing.JLabel();
    lblTotalAAT = new javax.swing.JLabel();
    jLabel8 = new javax.swing.JLabel();
    lblNoCCT = new javax.swing.JLabel();
    lblTotalCCT = new javax.swing.JLabel();
    jSeparator7 = new javax.swing.JSeparator();
    jLabel12 = new javax.swing.JLabel();
    lblGTotalValue = new javax.swing.JLabel();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getResourceMap(FrameCashBook.class);
    setTitle(resourceMap.getString("Form.title")); // NOI18N
    setName("Form"); // NOI18N

    jToolBar1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    jToolBar1.setRollover(true);
    jToolBar1.setName("jToolBar1"); // NOI18N

    jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
    jButton1.setFocusable(false);
    jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jButton1.setName("jButton1"); // NOI18N
    jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar1.add(jButton1);

    jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
    jButton2.setFocusable(false);
    jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jButton2.setName("jButton2"); // NOI18N
    jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar1.add(jButton2);

    jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
    jButton3.setFocusable(false);
    jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jButton3.setName("jButton3"); // NOI18N
    jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar1.add(jButton3);

    jButton4.setText(resourceMap.getString("jButton4.text")); // NOI18N
    jButton4.setFocusable(false);
    jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jButton4.setName("jButton4"); // NOI18N
    jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar1.add(jButton4);

    jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    jPanel4.setName("jPanel4"); // NOI18N
    jPanel4.setLayout(new java.awt.GridBagLayout());

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

    statusMessageLabel.setFont(resourceMap.getFont("statusMessageLabel.font")); // NOI18N
    statusMessageLabel.setText(resourceMap.getString("statusMessageLabel.text")); // NOI18N
    statusMessageLabel.setName("statusMessageLabel"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 0);
    jPanel4.add(statusMessageLabel, gridBagConstraints);

    progressBar.setName("progressBar"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 0;
    jPanel4.add(progressBar, gridBagConstraints);

    pnlSearch.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    pnlSearch.setName("pnlSearch"); // NOI18N
    pnlSearch.setLayout(new java.awt.GridBagLayout());

    jLabel5.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
    jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
    jLabel5.setName("jLabel5"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 16;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 2, 2, 2);
    pnlSearch.add(jLabel5, gridBagConstraints);

    jLabel4.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
    jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
    jLabel4.setName("jLabel4"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 18;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
    pnlSearch.add(jLabel4, gridBagConstraints);

    jLabel3.setFont(resourceMap.getFont("jLabel3.font")); // NOI18N
    jLabel3.setForeground(resourceMap.getColor("jLabel3.foreground")); // NOI18N
    jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
    jLabel3.setName("jLabel3"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
    pnlSearch.add(jLabel3, gridBagConstraints);

    dtFrom.setName("dtFrom"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 17;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(4, 2, 4, 2);
    pnlSearch.add(dtFrom, gridBagConstraints);

    dtTo.setName("dtTo"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 19;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(4, 2, 4, 2);
    pnlSearch.add(dtTo, gridBagConstraints);

    cmbContactable.setName("cmbContactable"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 4, 2);
    pnlSearch.add(cmbContactable, gridBagConstraints);

    javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getActionMap(FrameCashBook.class, this);
    btnSearch.setAction(actionMap.get("search")); // NOI18N
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
    gridBagConstraints.gridy = 20;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
    gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 2);
    pnlSearch.add(btnSearch, gridBagConstraints);

    buttonGroup1.add(rdoAgent);
    rdoAgent.setFont(resourceMap.getFont("rdoTicketing.font")); // NOI18N
    rdoAgent.setText(resourceMap.getString("rdoAgent.text")); // NOI18N
    rdoAgent.setName("rdoAgent"); // NOI18N
    rdoAgent.addActionListener(rdoAgentListener);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
    pnlSearch.add(rdoAgent, gridBagConstraints);

    buttonGroup1.add(rdoCustomer);
    rdoCustomer.setFont(resourceMap.getFont("rdoTicketing.font")); // NOI18N
    rdoCustomer.setText(resourceMap.getString("rdoCustomer.text")); // NOI18N
    rdoCustomer.setName("rdoCustomer"); // NOI18N
    rdoCustomer.addActionListener(rdoCustomerListener);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
    pnlSearch.add(rdoCustomer, gridBagConstraints);

    buttonGroup1.add(rdoAgtCust);
    rdoAgtCust.setFont(resourceMap.getFont("rdoTicketing.font")); // NOI18N
    rdoAgtCust.setSelected(true);
    rdoAgtCust.setText(resourceMap.getString("rdoAgtCust.text")); // NOI18N
    rdoAgtCust.setName("rdoAgtCust"); // NOI18N
    rdoAgtCust.addActionListener(rdoContactableListener);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
    pnlSearch.add(rdoAgtCust, gridBagConstraints);

    jLabel1.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
    jLabel1.setForeground(resourceMap.getColor("jLabel1.foreground")); // NOI18N
    jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
    jLabel1.setName("jLabel1"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
    pnlSearch.add(jLabel1, gridBagConstraints);

    jSeparator1.setName("jSeparator1"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipady = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
    pnlSearch.add(jSeparator1, gridBagConstraints);

    jLabel2.setFont(resourceMap.getFont("jLabel2.font")); // NOI18N
    jLabel2.setForeground(resourceMap.getColor("jLabel2.foreground")); // NOI18N
    jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
    jLabel2.setName("jLabel2"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
    pnlSearch.add(jLabel2, gridBagConstraints);

    jSeparator3.setName("jSeparator3"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipady = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
    pnlSearch.add(jSeparator3, gridBagConstraints);

    jSeparator4.setName("jSeparator4"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 21;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
    pnlSearch.add(jSeparator4, gridBagConstraints);

    jLabel6.setFont(resourceMap.getFont("jLabel6.font")); // NOI18N
    jLabel6.setForeground(resourceMap.getColor("jLabel6.foreground")); // NOI18N
    jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
    jLabel6.setName("jLabel6"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 14;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
    pnlSearch.add(jLabel6, gridBagConstraints);

    jSeparator5.setName("jSeparator5"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 15;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipady = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
    pnlSearch.add(jSeparator5, gridBagConstraints);

    jLabel7.setFont(resourceMap.getFont("jLabel7.font")); // NOI18N
    jLabel7.setForeground(resourceMap.getColor("jLabel7.foreground")); // NOI18N
    jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
    jLabel7.setName("jLabel7"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 11;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(6, 2, 2, 2);
    pnlSearch.add(jLabel7, gridBagConstraints);

    jSeparator6.setName("jSeparator6"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 12;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipady = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
    pnlSearch.add(jSeparator6, gridBagConstraints);

    cmbCashier.setName("cmbCashier"); // NOI18N
    AutoCompleteDecorator.decorate(cmbCashier);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 13;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 4, 2);
    pnlSearch.add(cmbCashier, gridBagConstraints);

    busyIcon.setText(resourceMap.getString("busyIcon.text")); // NOI18N
    busyIcon.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
    busyIcon.setName("busyIcon"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
    pnlSearch.add(busyIcon, gridBagConstraints);

    busyIcon1.setText(resourceMap.getString("busyIcon1.text")); // NOI18N
    busyIcon1.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
    busyIcon1.setName("busyIcon1"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 11;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
    pnlSearch.add(busyIcon1, gridBagConstraints);

    cmbTType.setFont(resourceMap.getFont("cmbTType.font")); // NOI18N
    cmbTType.setName("cmbTType"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 7;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
    pnlSearch.add(cmbTType, gridBagConstraints);

    buttonGroup2.add(rdoTicketing);
    rdoTicketing.setFont(resourceMap.getFont("rdoTicketing.font")); // NOI18N
    rdoTicketing.setText(resourceMap.getString("rdoTicketing.text")); // NOI18N
    rdoTicketing.setName("rdoTicketing"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 10;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    pnlSearch.add(rdoTicketing, gridBagConstraints);

    buttonGroup2.add(rdoOther);
    rdoOther.setFont(resourceMap.getFont("rdoOther.font")); // NOI18N
    rdoOther.setText(resourceMap.getString("rdoOther.text")); // NOI18N
    rdoOther.setName("rdoOther"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 10;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
    pnlSearch.add(rdoOther, gridBagConstraints);

    jSeparator8.setName("jSeparator8"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 9;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
    pnlSearch.add(jSeparator8, gridBagConstraints);

    jLabel13.setFont(resourceMap.getFont("jLabel13.font")); // NOI18N
    jLabel13.setForeground(resourceMap.getColor("jLabel13.foreground")); // NOI18N
    jLabel13.setText(resourceMap.getString("jLabel13.text")); // NOI18N
    jLabel13.setName("jLabel13"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 8;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(6, 2, 2, 2);
    pnlSearch.add(jLabel13, gridBagConstraints);

    buttonGroup2.add(rdoAllClass);
    rdoAllClass.setFont(resourceMap.getFont("rdoAllClass.font")); // NOI18N
    rdoAllClass.setSelected(true);
    rdoAllClass.setText(resourceMap.getString("rdoAllClass.text")); // NOI18N
    rdoAllClass.setName("rdoAllClass"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 10;
    pnlSearch.add(rdoAllClass, gridBagConstraints);

    jPanel9.setName("jPanel9"); // NOI18N
    jPanel9.setLayout(new java.awt.GridBagLayout());

    tabsTrans.setName("tabsTrans"); // NOI18N

    jPanel11.setName("jPanel11"); // NOI18N
    jPanel11.setLayout(new java.awt.GridBagLayout());

    jScrollPane9.setName("jScrollPane9"); // NOI18N

    tblTransaction.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {

        },
        new String [] {
            "Trans.Date", "Trans.Type", "Trans.Amount", "Cashier", "Client", "InvRef", "PNR", "Remarks", "T.Status"
        }
    ) {
        boolean[] canEdit = new boolean [] {
            false, false, false, false, false, false, false, false, false
        };

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit [columnIndex];
        }
    });
    tblTransaction.setFont(resourceMap.getFont("tblTransaction.font")); // NOI18N
    tblTransaction.setName("tblTransaction"); // NOI18N
    tblTransaction.setShowHorizontalLines(false);
    tblTransaction.setShowVerticalLines(false);
    tblTransaction.setSortable(false);
    tblTransaction.getTableHeader().setReorderingAllowed(false);
    jScrollPane9.setViewportView(tblTransaction);
    tblTransaction.getColumnModel().getColumn(0).setMinWidth(80);
    tblTransaction.getColumnModel().getColumn(0).setPreferredWidth(80);
    tblTransaction.getColumnModel().getColumn(0).setMaxWidth(130);
    tblTransaction.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblCash.columnModel.title0")); // NOI18N
    tblTransaction.getColumnModel().getColumn(1).setMinWidth(80);
    tblTransaction.getColumnModel().getColumn(1).setPreferredWidth(80);
    tblTransaction.getColumnModel().getColumn(1).setMaxWidth(130);
    tblTransaction.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblCash.columnModel.title1")); // NOI18N
    tblTransaction.getColumnModel().getColumn(2).setMinWidth(100);
    tblTransaction.getColumnModel().getColumn(2).setPreferredWidth(100);
    tblTransaction.getColumnModel().getColumn(2).setMaxWidth(130);
    tblTransaction.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblCash.columnModel.title2")); // NOI18N
    tblTransaction.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblCash.columnModel.title5")); // NOI18N
    tblTransaction.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("tblCash.columnModel.title6")); // NOI18N
    tblTransaction.getColumnModel().getColumn(5).setMinWidth(100);
    tblTransaction.getColumnModel().getColumn(5).setPreferredWidth(100);
    tblTransaction.getColumnModel().getColumn(5).setMaxWidth(150);
    tblTransaction.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("tblCash.columnModel.title7")); // NOI18N
    tblTransaction.getColumnModel().getColumn(6).setMinWidth(70);
    tblTransaction.getColumnModel().getColumn(6).setPreferredWidth(70);
    tblTransaction.getColumnModel().getColumn(6).setMaxWidth(130);
    tblTransaction.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("tblCash.columnModel.title3")); // NOI18N
    tblTransaction.getColumnModel().getColumn(7).setHeaderValue(resourceMap.getString("tblCash.columnModel.title8")); // NOI18N
    tblTransaction.getColumnModel().getColumn(8).setMinWidth(60);
    tblTransaction.getColumnModel().getColumn(8).setPreferredWidth(60);
    tblTransaction.getColumnModel().getColumn(8).setHeaderValue(resourceMap.getString("tblTransaction.columnModel.title8")); // NOI18N

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel11.add(jScrollPane9, gridBagConstraints);

    tabsTrans.addTab(resourceMap.getString("jPanel11.TabConstraints.tabTitle"), jPanel11); // NOI18N

    jPanel1.setName("jPanel1"); // NOI18N
    jPanel1.setLayout(new java.awt.GridBagLayout());

    jScrollPane2.setName("jScrollPane2"); // NOI18N

    tblCash.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null}
        },
        new String [] {
            "Trans.Date", "Trans.Type", "Trans.Amount", "Cashier", "Client", "InvRef", "PNR", "Trans.Ref"
        }
    ) {
        boolean[] canEdit = new boolean [] {
            false, false, false, false, false, false, false, false
        };

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit [columnIndex];
        }
    });
    tblCash.setFont(resourceMap.getFont("tblCash.font")); // NOI18N
    tblCash.setName("tblCash"); // NOI18N
    jScrollPane2.setViewportView(tblCash);
    tblCash.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblCash.columnModel.title0")); // NOI18N
    tblCash.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblCash.columnModel.title1")); // NOI18N
    tblCash.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblCash.columnModel.title2")); // NOI18N
    tblCash.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblCash.columnModel.title5")); // NOI18N
    tblCash.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("tblCash.columnModel.title6")); // NOI18N
    tblCash.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("tblCash.columnModel.title7")); // NOI18N
    tblCash.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("tblCash.columnModel.title3")); // NOI18N
    tblCash.getColumnModel().getColumn(7).setHeaderValue(resourceMap.getString("tblCash.columnModel.title8")); // NOI18N

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel1.add(jScrollPane2, gridBagConstraints);

    tabsTrans.addTab(resourceMap.getString("jPanel1.TabConstraints.tabTitle"), jPanel1); // NOI18N

    jPanel2.setName("jPanel2"); // NOI18N
    jPanel2.setLayout(new java.awt.GridBagLayout());

    jScrollPane3.setName("jScrollPane3"); // NOI18N

    tblCheque.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null}
        },
        new String [] {
            "Trans.Date", "Trans.Type", "Trans.Amount", "Cashier", "Client", "InvRef", "PNR", "Trans.Ref"
        }
    ) {
        boolean[] canEdit = new boolean [] {
            false, false, false, false, false, false, false, false
        };

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit [columnIndex];
        }
    });
    tblCheque.setFont(resourceMap.getFont("tblCheque.font")); // NOI18N
    tblCheque.setName("tblCheque"); // NOI18N
    jScrollPane3.setViewportView(tblCheque);
    tblCheque.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblCash.columnModel.title0")); // NOI18N
    tblCheque.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblCash.columnModel.title1")); // NOI18N
    tblCheque.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblCash.columnModel.title2")); // NOI18N
    tblCheque.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblCash.columnModel.title5")); // NOI18N
    tblCheque.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("tblCash.columnModel.title6")); // NOI18N
    tblCheque.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("tblCash.columnModel.title7")); // NOI18N
    tblCheque.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("tblCash.columnModel.title3")); // NOI18N
    tblCheque.getColumnModel().getColumn(7).setHeaderValue(resourceMap.getString("tblCash.columnModel.title8")); // NOI18N

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel2.add(jScrollPane3, gridBagConstraints);

    tabsTrans.addTab(resourceMap.getString("jPanel2.TabConstraints.tabTitle"), jPanel2); // NOI18N

    jPanel3.setName("jPanel3"); // NOI18N
    jPanel3.setLayout(new java.awt.GridBagLayout());

    jScrollPane4.setName("jScrollPane4"); // NOI18N

    tblDCard.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null}
        },
        new String [] {
            "Trans.Date", "Trans.Type", "Trans.Amount", "Cashier", "Client", "InvRef", "PNR", "Trans.Ref"
        }
    ) {
        boolean[] canEdit = new boolean [] {
            false, false, false, false, false, false, false, false
        };

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit [columnIndex];
        }
    });
    tblDCard.setFont(resourceMap.getFont("tblDCard.font")); // NOI18N
    tblDCard.setName("tblDCard"); // NOI18N
    jScrollPane4.setViewportView(tblDCard);
    tblDCard.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblCash.columnModel.title0")); // NOI18N
    tblDCard.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblCash.columnModel.title1")); // NOI18N
    tblDCard.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblCash.columnModel.title2")); // NOI18N
    tblDCard.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblCash.columnModel.title5")); // NOI18N
    tblDCard.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("tblCash.columnModel.title6")); // NOI18N
    tblDCard.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("tblCash.columnModel.title7")); // NOI18N
    tblDCard.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("tblCash.columnModel.title3")); // NOI18N
    tblDCard.getColumnModel().getColumn(7).setHeaderValue(resourceMap.getString("tblCash.columnModel.title8")); // NOI18N

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel3.add(jScrollPane4, gridBagConstraints);

    tabsTrans.addTab(resourceMap.getString("jPanel3.TabConstraints.tabTitle"), jPanel3); // NOI18N

    jPanel5.setName("jPanel5"); // NOI18N
    jPanel5.setLayout(new java.awt.GridBagLayout());

    jScrollPane5.setName("jScrollPane5"); // NOI18N

    tblCCard.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null}
        },
        new String [] {
            "Trans.Date", "Trans.Type", "Trans.Amount", "Cashier", "Client", "InvRef", "PNR", "Trans.Ref"
        }
    ) {
        boolean[] canEdit = new boolean [] {
            false, false, false, false, false, false, false, false
        };

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit [columnIndex];
        }
    });
    tblCCard.setFont(resourceMap.getFont("tblCCard.font")); // NOI18N
    tblCCard.setName("tblCCard"); // NOI18N
    jScrollPane5.setViewportView(tblCCard);
    tblCCard.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblCash.columnModel.title0")); // NOI18N
    tblCCard.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblCash.columnModel.title1")); // NOI18N
    tblCCard.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblCash.columnModel.title2")); // NOI18N
    tblCCard.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblCash.columnModel.title5")); // NOI18N
    tblCCard.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("tblCash.columnModel.title6")); // NOI18N
    tblCCard.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("tblCash.columnModel.title7")); // NOI18N
    tblCCard.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("tblCash.columnModel.title3")); // NOI18N
    tblCCard.getColumnModel().getColumn(7).setHeaderValue(resourceMap.getString("tblCash.columnModel.title8")); // NOI18N

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel5.add(jScrollPane5, gridBagConstraints);

    tabsTrans.addTab(resourceMap.getString("jPanel5.TabConstraints.tabTitle"), jPanel5); // NOI18N

    jPanel6.setName("jPanel6"); // NOI18N
    jPanel6.setLayout(new java.awt.GridBagLayout());

    jScrollPane6.setName("jScrollPane6"); // NOI18N

    tblBDeposit.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null}
        },
        new String [] {
            "Trans.Date", "Trans.Type", "Trans.Amount", "Cashier", "Client", "InvRef", "PNR", "Trans.Ref"
        }
    ) {
        boolean[] canEdit = new boolean [] {
            false, false, false, false, false, false, false, false
        };

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit [columnIndex];
        }
    });
    tblBDeposit.setFont(resourceMap.getFont("tblBDeposit.font")); // NOI18N
    tblBDeposit.setName("tblBDeposit"); // NOI18N
    jScrollPane6.setViewportView(tblBDeposit);
    tblBDeposit.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblCash.columnModel.title0")); // NOI18N
    tblBDeposit.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblCash.columnModel.title1")); // NOI18N
    tblBDeposit.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblCash.columnModel.title2")); // NOI18N
    tblBDeposit.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblCash.columnModel.title5")); // NOI18N
    tblBDeposit.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("tblCash.columnModel.title6")); // NOI18N
    tblBDeposit.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("tblCash.columnModel.title7")); // NOI18N
    tblBDeposit.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("tblCash.columnModel.title3")); // NOI18N
    tblBDeposit.getColumnModel().getColumn(7).setHeaderValue(resourceMap.getString("tblCash.columnModel.title8")); // NOI18N

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel6.add(jScrollPane6, gridBagConstraints);

    tabsTrans.addTab(resourceMap.getString("jPanel6.TabConstraints.tabTitle"), jPanel6); // NOI18N

    jPanel7.setName("jPanel7"); // NOI18N
    jPanel7.setLayout(new java.awt.GridBagLayout());

    jScrollPane7.setName("jScrollPane7"); // NOI18N

    tblOTransfer.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null}
        },
        new String [] {
            "Trans.Date", "Trans.Type", "Trans.Amount", "Cashier", "Client", "InvRef", "PNR", "Trans.Ref"
        }
    ) {
        boolean[] canEdit = new boolean [] {
            false, false, false, false, false, false, false, false
        };

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit [columnIndex];
        }
    });
    tblOTransfer.setFont(resourceMap.getFont("tblOTransfer.font")); // NOI18N
    tblOTransfer.setName("tblOTransfer"); // NOI18N
    jScrollPane7.setViewportView(tblOTransfer);
    tblOTransfer.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblCash.columnModel.title0")); // NOI18N
    tblOTransfer.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblCash.columnModel.title1")); // NOI18N
    tblOTransfer.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblCash.columnModel.title2")); // NOI18N
    tblOTransfer.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblCash.columnModel.title5")); // NOI18N
    tblOTransfer.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("tblCash.columnModel.title6")); // NOI18N
    tblOTransfer.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("tblCash.columnModel.title7")); // NOI18N
    tblOTransfer.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("tblCash.columnModel.title3")); // NOI18N
    tblOTransfer.getColumnModel().getColumn(7).setHeaderValue(resourceMap.getString("tblCash.columnModel.title8")); // NOI18N

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel7.add(jScrollPane7, gridBagConstraints);

    tabsTrans.addTab(resourceMap.getString("jPanel7.TabConstraints.tabTitle"), jPanel7); // NOI18N

    jPanel8.setName("jPanel8"); // NOI18N
    jPanel8.setLayout(new java.awt.GridBagLayout());

    jScrollPane8.setName("jScrollPane8"); // NOI18N

    tblAAdjustment.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null}
        },
        new String [] {
            "Trans.Date", "Trans.Type", "Trans.Amount", "Cashier", "Client", "InvRef", "PNR", "Trans.Ref"
        }
    ) {
        boolean[] canEdit = new boolean [] {
            false, false, false, false, false, false, false, false
        };

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit [columnIndex];
        }
    });
    tblAAdjustment.setFont(resourceMap.getFont("tblAAdjustment.font")); // NOI18N
    tblAAdjustment.setName("tblAAdjustment"); // NOI18N
    jScrollPane8.setViewportView(tblAAdjustment);
    tblAAdjustment.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblCash.columnModel.title0")); // NOI18N
    tblAAdjustment.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblCash.columnModel.title1")); // NOI18N
    tblAAdjustment.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblCash.columnModel.title2")); // NOI18N
    tblAAdjustment.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblCash.columnModel.title5")); // NOI18N
    tblAAdjustment.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("tblCash.columnModel.title6")); // NOI18N
    tblAAdjustment.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("tblCash.columnModel.title7")); // NOI18N
    tblAAdjustment.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("tblCash.columnModel.title3")); // NOI18N
    tblAAdjustment.getColumnModel().getColumn(7).setHeaderValue(resourceMap.getString("tblCash.columnModel.title8")); // NOI18N

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel8.add(jScrollPane8, gridBagConstraints);

    tabsTrans.addTab(resourceMap.getString("jPanel8.TabConstraints.tabTitle"), jPanel8); // NOI18N

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    jPanel9.add(tabsTrans, gridBagConstraints);

    jPanel10.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    jPanel10.setName("jPanel10"); // NOI18N
    jPanel10.setLayout(new java.awt.GridBagLayout());

    Chash.setFont(resourceMap.getFont("jLabel15.font")); // NOI18N
    Chash.setText(resourceMap.getString("Chash.text")); // NOI18N
    Chash.setName("Chash"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(4, 2, 0, 0);
    jPanel10.add(Chash, gridBagConstraints);

    jLabel9.setFont(resourceMap.getFont("jLabel15.font")); // NOI18N
    jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
    jLabel9.setName("jLabel9"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel10.add(jLabel9, gridBagConstraints);

    jLabel10.setFont(resourceMap.getFont("jLabel15.font")); // NOI18N
    jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
    jLabel10.setName("jLabel10"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel10.add(jLabel10, gridBagConstraints);

    jLabel11.setFont(resourceMap.getFont("lblTotalAAT.font")); // NOI18N
    jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
    jLabel11.setName("jLabel11"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 4;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel10.add(jLabel11, gridBagConstraints);

    lblNoCashT.setFont(resourceMap.getFont("lblTotalChequeT.font")); // NOI18N
    lblNoCashT.setText(resourceMap.getString("lblNoCashT.text")); // NOI18N
    lblNoCashT.setName("lblNoCashT"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.ipadx = 20;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(4, 4, 2, 4);
    jPanel10.add(lblNoCashT, gridBagConstraints);

    lblNoChequeT.setFont(resourceMap.getFont("lblTotalChequeT.font")); // NOI18N
    lblNoChequeT.setText(resourceMap.getString("lblNoChequeT.text")); // NOI18N
    lblNoChequeT.setName("lblNoChequeT"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.ipadx = 20;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(4, 4, 2, 4);
    jPanel10.add(lblNoChequeT, gridBagConstraints);

    lblNoDCT.setFont(resourceMap.getFont("lblTotalChequeT.font")); // NOI18N
    lblNoDCT.setText(resourceMap.getString("lblNoDCT.text")); // NOI18N
    lblNoDCT.setName("lblNoDCT"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.ipadx = 20;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(4, 4, 2, 4);
    jPanel10.add(lblNoDCT, gridBagConstraints);

    jLabel15.setFont(resourceMap.getFont("jLabel15.font")); // NOI18N
    jLabel15.setText(resourceMap.getString("jLabel15.text")); // NOI18N
    jLabel15.setName("jLabel15"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel10.add(jLabel15, gridBagConstraints);

    lblTotalCashT.setFont(resourceMap.getFont("lblTotalChequeT.font")); // NOI18N
    lblTotalCashT.setText(resourceMap.getString("lblTotalCashT.text")); // NOI18N
    lblTotalCashT.setName("lblTotalCashT"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.ipadx = 80;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
    jPanel10.add(lblTotalCashT, gridBagConstraints);

    lblTotalChequeT.setFont(resourceMap.getFont("lblTotalChequeT.font")); // NOI18N
    lblTotalChequeT.setText(resourceMap.getString("lblTotalChequeT.text")); // NOI18N
    lblTotalChequeT.setName("lblTotalChequeT"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.ipadx = 80;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel10.add(lblTotalChequeT, gridBagConstraints);

    lblTotalDCT.setFont(resourceMap.getFont("lblTotalChequeT.font")); // NOI18N
    lblTotalDCT.setText(resourceMap.getString("lblTotalDCT.text")); // NOI18N
    lblTotalDCT.setName("lblTotalDCT"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.ipadx = 80;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel10.add(lblTotalDCT, gridBagConstraints);

    lblNoBDT.setFont(resourceMap.getFont("lblTotalAAT.font")); // NOI18N
    lblNoBDT.setText(resourceMap.getString("lblNoBDT.text")); // NOI18N
    lblNoBDT.setName("lblNoBDT"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 5;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.ipadx = 20;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
    jPanel10.add(lblNoBDT, gridBagConstraints);

    jLabel20.setFont(resourceMap.getFont("jLabel20.font")); // NOI18N
    jLabel20.setText(resourceMap.getString("jLabel20.text")); // NOI18N
    jLabel20.setName("jLabel20"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 8;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(4, 4, 2, 2);
    jPanel10.add(jLabel20, gridBagConstraints);

    lblGTotalT.setFont(resourceMap.getFont("lblGTotalT.font")); // NOI18N
    lblGTotalT.setText(resourceMap.getString("lblGTotalT.text")); // NOI18N
    lblGTotalT.setName("lblGTotalT"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 9;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(4, 4, 2, 2);
    jPanel10.add(lblGTotalT, gridBagConstraints);

    lblNoAAT1.setFont(resourceMap.getFont("lblTotalAAT.font")); // NOI18N
    lblNoAAT1.setText(resourceMap.getString("lblNoAAT1.text")); // NOI18N
    lblNoAAT1.setName("lblNoAAT1"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 4;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel10.add(lblNoAAT1, gridBagConstraints);

    lblNoAAT.setFont(resourceMap.getFont("lblTotalAAT.font")); // NOI18N
    lblNoAAT.setText(resourceMap.getString("lblNoAAT.text")); // NOI18N
    lblNoAAT.setName("lblNoAAT"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 5;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.ipadx = 20;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel10.add(lblNoAAT, gridBagConstraints);

    lblTotalBDT.setFont(resourceMap.getFont("lblTotalAAT.font")); // NOI18N
    lblTotalBDT.setText(resourceMap.getString("lblTotalBDT.text")); // NOI18N
    lblTotalBDT.setName("lblTotalBDT"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 6;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipadx = 80;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
    jPanel10.add(lblTotalBDT, gridBagConstraints);

    lblNoOT.setFont(resourceMap.getFont("lblTotalAAT.font")); // NOI18N
    lblNoOT.setText(resourceMap.getString("lblNoOT.text")); // NOI18N
    lblNoOT.setName("lblNoOT"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 5;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.ipadx = 20;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel10.add(lblNoOT, gridBagConstraints);

    lblTotalOT.setFont(resourceMap.getFont("lblTotalAAT.font")); // NOI18N
    lblTotalOT.setText(resourceMap.getString("lblTotalOT.text")); // NOI18N
    lblTotalOT.setName("lblTotalOT"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 6;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipadx = 80;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel10.add(lblTotalOT, gridBagConstraints);

    lblTotalAAT.setFont(resourceMap.getFont("lblTotalAAT.font")); // NOI18N
    lblTotalAAT.setText(resourceMap.getString("lblTotalAAT.text")); // NOI18N
    lblTotalAAT.setName("lblTotalAAT"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 6;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipadx = 80;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel10.add(lblTotalAAT, gridBagConstraints);

    jLabel8.setFont(resourceMap.getFont("lblTotalAAT.font")); // NOI18N
    jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
    jLabel8.setName("jLabel8"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 4;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
    jPanel10.add(jLabel8, gridBagConstraints);

    lblNoCCT.setFont(resourceMap.getFont("lblTotalChequeT.font")); // NOI18N
    lblNoCCT.setText(resourceMap.getString("lblNoCCT.text")); // NOI18N
    lblNoCCT.setName("lblNoCCT"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.ipadx = 20;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(4, 4, 2, 4);
    jPanel10.add(lblNoCCT, gridBagConstraints);

    lblTotalCCT.setFont(resourceMap.getFont("lblTotalChequeT.font")); // NOI18N
    lblTotalCCT.setText(resourceMap.getString("lblTotalCCT.text")); // NOI18N
    lblTotalCCT.setName("lblTotalCCT"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.ipadx = 80;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel10.add(lblTotalCCT, gridBagConstraints);

    jSeparator7.setOrientation(javax.swing.SwingConstants.VERTICAL);
    jSeparator7.setName("jSeparator7"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 7;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridheight = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weighty = 1.0;
    jPanel10.add(jSeparator7, gridBagConstraints);

    jLabel12.setFont(resourceMap.getFont("jLabel20.font")); // NOI18N
    jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
    jLabel12.setName("jLabel12"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 8;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(4, 4, 2, 2);
    jPanel10.add(jLabel12, gridBagConstraints);

    lblGTotalValue.setFont(resourceMap.getFont("jLabel20.font")); // NOI18N
    lblGTotalValue.setText(resourceMap.getString("lblGTotalValue.text")); // NOI18N
    lblGTotalValue.setName("lblGTotalValue"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 9;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(4, 4, 2, 2);
    jPanel10.add(lblGTotalValue, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    jPanel9.add(jPanel10, gridBagConstraints);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 930, Short.MAX_VALUE)
        .addGroup(layout.createSequentialGroup()
            .addComponent(pnlSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, 715, Short.MAX_VALUE))
        .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 930, Short.MAX_VALUE)
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(2, 2, 2)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(pnlSearch, javax.swing.GroupLayout.DEFAULT_SIZE, 446, Short.MAX_VALUE)
                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, 446, Short.MAX_VALUE))
            .addGap(2, 2, 2)
            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
    );

    pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        transactions = new ArrayList<AcTransaction>();
    cashTransactions = new ArrayList<AcTransaction>();
    chequeTransactions = new ArrayList<AcTransaction>();
    dCardTransactions = new ArrayList<AcTransaction>();
    cCardTransactions = new ArrayList<AcTransaction>();
    bDepositTransactions = new ArrayList<AcTransaction>();
    oTransferTransactions = new ArrayList<AcTransaction>();
    aAdjustmentTransactions = new ArrayList<AcTransaction>();
    totalCash = new BigDecimal("0.00");
    totalCheque = new BigDecimal("0.00");
    totalDC = new BigDecimal("0.00");
    totalCC = new BigDecimal("0.00");
    totalBD = new BigDecimal("0.00");
    totalOT = new BigDecimal("0.00");
    totalAA = new BigDecimal("0.00");
    
        new Thread(new threadTransaction()).start();
}//GEN-LAST:event_btnSearchActionPerformed
    private ActionListener rdoAgentListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            busyIcon.setVisible(true);
            busyIcon.setBusy(true);
            cmbContactable.setEnabled(true);
            List agents = agentBo.agentNameList();

            DefaultComboBoxModel cmbContactableModel = new DefaultComboBoxModel(agents.toArray());
            cmbContactableModel.insertElementAt("All", 0);
            cmbContactable.setModel(cmbContactableModel);
            cmbContactable.setSelectedIndex(0);
            busyIcon.setBusy(false);
            busyIcon.setVisible(false);
        }
    };
    private ActionListener rdoCustomerListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            busyIcon.setVisible(true);
            busyIcon.setBusy(true);
            cmbContactable.setSelectedIndex(-1);
            cmbContactable.setEnabled(false);
            //customers = customerBo.loadAll();

            //DefaultComboBoxModel cmbContactableModel = new DefaultComboBoxModel(customers.toArray());
            //cmbContactable.setModel(cmbContactableModel);
            busyIcon.setBusy(false);
            busyIcon.setVisible(false);
        }
    };
    private ActionListener rdoContactableListener = new ActionListener() {

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
            if (cmbContactable.getSelectedIndex() != -1) {
                data = cmbContactable.getSelectedItem().toString().split("-");
                id = Long.parseLong(data[2]);
            }
        }
    };
    private ChangeListener tabsTransListener = new ChangeListener() {

        public void stateChanged(ChangeEvent e) {

            if (tabsTrans.getSelectedIndex() == 1) {
                populateTblCashTransaction(cashTransactions);
            } else if (tabsTrans.getSelectedIndex() == 2) {
            }
        }
    };

    /**
     * @param args the command line arguments
     */
   /* public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new FrameCashBook().setVisible(true);
            }
        });
    }*/
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Chash;
    private javax.swing.JButton btnSearch;
    private org.jdesktop.swingx.JXBusyLabel busyIcon;
    private org.jdesktop.swingx.JXBusyLabel busyIcon1;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JComboBox cmbCashier;
    private javax.swing.JComboBox cmbContactable;
    private javax.swing.JComboBox cmbTType;
    private org.jdesktop.swingx.JXDatePicker dtFrom;
    private org.jdesktop.swingx.JXDatePicker dtTo;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblGTotalT;
    private javax.swing.JLabel lblGTotalValue;
    private javax.swing.JLabel lblNoAAT;
    private javax.swing.JLabel lblNoAAT1;
    private javax.swing.JLabel lblNoBDT;
    private javax.swing.JLabel lblNoCCT;
    private javax.swing.JLabel lblNoCashT;
    private javax.swing.JLabel lblNoChequeT;
    private javax.swing.JLabel lblNoDCT;
    private javax.swing.JLabel lblNoOT;
    private javax.swing.JLabel lblTotalAAT;
    private javax.swing.JLabel lblTotalBDT;
    private javax.swing.JLabel lblTotalCCT;
    private javax.swing.JLabel lblTotalCashT;
    private javax.swing.JLabel lblTotalChequeT;
    private javax.swing.JLabel lblTotalDCT;
    private javax.swing.JLabel lblTotalOT;
    private javax.swing.JPanel pnlSearch;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JRadioButton rdoAgent;
    private javax.swing.JRadioButton rdoAgtCust;
    private javax.swing.JRadioButton rdoAllClass;
    private javax.swing.JRadioButton rdoCustomer;
    private javax.swing.JRadioButton rdoOther;
    private javax.swing.JRadioButton rdoTicketing;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JTabbedPane tabsTrans;
    private org.jdesktop.swingx.JXTable tblAAdjustment;
    private org.jdesktop.swingx.JXTable tblBDeposit;
    private org.jdesktop.swingx.JXTable tblCCard;
    private org.jdesktop.swingx.JXTable tblCash;
    private org.jdesktop.swingx.JXTable tblCheque;
    private org.jdesktop.swingx.JXTable tblDCard;
    private org.jdesktop.swingx.JXTable tblOTransfer;
    private org.jdesktop.swingx.JXTable tblTransaction;
    // End of variables declaration//GEN-END:variables
    private long startTime = 0;
    private long stopTime = 0;
    private float elapsedTime = 0;

    private class threadTransaction implements Runnable {

        public threadTransaction() {
        }

        public void run() {
            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();

            statusMessageLabel.setText("Loading Invoice...");
            btnSearch.setEnabled(false);

            searchTransaction();
            populateTransaction();
            btnSearch.setEnabled(true);
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            progressBar.repaint(); //Refresh graphics
            btnSearch.setEnabled(true);
        }
    }   
}
