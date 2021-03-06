
/*
 * FrameOutstandingInvoice.java
 *
 * Created on 18-Oct-2010, 23:15:52
 */
package etsbackoffice.gui;

import etsbackoffice.ETSBackofficeApp;
import etsbackoffice.businesslogic.*;
import etsbackoffice.domain.*;
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
import javax.swing.JProgressBar;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 *
 * @author Yusuf
 */
public class FramePAcDocHistory extends javax.swing.JFrame{

    AccountingDocBo acDocBo = (AccountingDocBo) ETSBackofficeApp.getApplication().ctx.getBean("acDocBo");
    AgentBo agentBo = (AgentBo) ETSBackofficeApp.getApplication().ctx.getBean("agentBo");
    CustomerBo customerBo = (CustomerBo) ETSBackofficeApp.getApplication().ctx.getBean("customerBo");
    private PurchaseAccountingDocument pAcDoc;
    private List<Agent> vendors = new ArrayList();

    private List<PurchaseAccountingDocument> acDocs = new ArrayList();

    private DefaultTableModel invoiceModel;
    private DateFormat df = new DateFormat();
    //DefaultComboBoxModel cmbCustModel = new DefaultComboBoxModel();
    //DefaultComboBoxModel cmbAgtModel = new DefaultComboBoxModel();

    /** Creates new form FrameOutstandingInvoice */
    public FramePAcDocHistory(java.awt.Frame parent) {
        initComponents();
        busyIcon.setVisible(false);        
        txtAcDocRef.setDocument(new CheckInput(CheckInput.FLOAT));        
        AbstractDocument doc = (AbstractDocument) txtGdsPnr.getDocument();
        doc.setDocumentFilter(new DocumentSizeFilter(8));
        initialThread();
    }    
        private void initialThread() {
        new Thread(new threadLoadVendors()).start();
    }

    private void populateCmbContactable() {
        List cmbElement = new ArrayList();
        for (Agent agent : vendors) {
            cmbElement.add(agent.getName() + "-" + agent.getPostCode()
                    + "-" + agent.getContactableId());
        }

        Collections.sort(cmbElement);
        DefaultComboBoxModel cmbContactableModel = new DefaultComboBoxModel(cmbElement.toArray());
        cmbContactableModel.insertElementAt("Select", 0);
        cmbContactable.setModel(cmbContactableModel);
        cmbContactable.setSelectedIndex(0);
    }
    
    private void searchAcDocByCriteria() {
        int contactableType = 0;
        Long contactableId = null;
        Integer acDocType = null;
        Date from = null;
        Date to = null;
        String[] data;

        if (cmbContactable.getSelectedIndex() > 0) {
            data = cmbContactable.getSelectedItem().toString().split("-");
            contactableId = Long.parseLong(data[2]);
        }

        from = dtFrom.getDate();
        to = dtTo.getDate();

        if (rdoInvoice.isSelected()) {
            acDocType = 1;
        } else if (rdoCNote.isSelected()) {
            acDocType = 2;
        } else if(rdoInCNote.isSelected()) {
            acDocType = null;
        }        
        
        this.acDocs = acDocBo.pAcDocHistoryByCriteria(contactableId, acDocType, from, to);
        populateTblInvoice(this.acDocs);
    }

    private void searchAcDoc() {
        String acDocVRef = txtAcDocRef.getText().trim();
        String gdsPnr = txtGdsPnr.getText().trim();

        if (!acDocVRef.isEmpty()) {
          this.acDocs = acDocBo.findCompletePAcDocVByRefNo(acDocVRef);
        } else if (!gdsPnr.isEmpty()) {
          this.acDocs = acDocBo.findPAcDocByGdsPnr(gdsPnr);
        }
        if(this.acDocs.size()>0){
         populateTblInvoice(this.acDocs);
        }
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

    private void populateTblInvoice(List<PurchaseAccountingDocument> pAcDocs) {

        int row = 0;
        invoiceModel = (DefaultTableModel) tblInvoice.getModel();
        invoiceModel.getDataVector().removeAllElements();
        JScrollPane.repaint();
        BigDecimal totalInvAmount = new BigDecimal("0.00");        
        BigDecimal totalReceived = new BigDecimal("0.00");
        BigDecimal finalBalance = new BigDecimal("0.00");
        

        for (PurchaseAccountingDocument a : pAcDocs) {                       
            
            BigDecimal balance = a.getTotalPaid().subtract(a.getTotalDocumentedAmount());
            
            invoiceModel.insertRow(row, new Object[]{a.getVendorRef(),
                        df.dateForGui(a.getIssueDate()), a.getTerms(),a.getPnr().getGdsPNR(),
                        a.getPnr().getServicingCareer().getCode(),a.getLeadPaxFromTickets(),
                        a.getTotalDocumentedAmount(),a.getTotalPaid(),
                        balance});
            row++;
            totalInvAmount = totalInvAmount.add(a.getTotalDocumentedAmount());
            totalReceived = totalReceived.add(a.getTotalPaid());
        }
         lblTotalDoc.setText(String.valueOf(this.acDocs.size()));
         lblTotalInvAmount.setText(totalInvAmount.toString());
         lblTotalPaid.setText(totalReceived.toString());         
         lblTotalOutstanding.setText(totalReceived.subtract(totalInvAmount).toString());
    }

    private ActionListener cmbContactableListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            String[] data;
            long id = 0;
            if(cmbContactable.getSelectedIndex()>0){
            data = cmbContactable.getSelectedItem().toString().split("-");
            id = Long.parseLong(data[2]);
            }
            
                loop:
                for (Agent agent : vendors) {
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
        buttonGroup2 = new javax.swing.ButtonGroup();
        jToolBar1 = new javax.swing.JToolBar();
        jButton1 = new javax.swing.JButton();
        btnViewInvoice = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
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
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getResourceMap(FramePAcDocHistory.class);
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

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getActionMap(FramePAcDocHistory.class, this);
        btnViewInvoice.setAction(actionMap.get("viewInvoice")); // NOI18N
        btnViewInvoice.setFont(resourceMap.getFont("jButton8.font")); // NOI18N
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

        jButton4.setFont(resourceMap.getFont("jButton8.font")); // NOI18N
        jButton4.setIcon(resourceMap.getIcon("jButton4.icon")); // NOI18N
        jButton4.setText(resourceMap.getString("jButton4.text")); // NOI18N
        jButton4.setFocusable(false);
        jButton4.setName("jButton4"); // NOI18N
        jToolBar1.add(jButton4);

        jButton7.setFont(resourceMap.getFont("jButton8.font")); // NOI18N
        jButton7.setIcon(resourceMap.getIcon("jButton7.icon")); // NOI18N
        jButton7.setText(resourceMap.getString("jButton7.text")); // NOI18N
        jButton7.setFocusable(false);
        jButton7.setName("jButton7"); // NOI18N
        jToolBar1.add(jButton7);

        jButton8.setFont(resourceMap.getFont("jButton8.font")); // NOI18N
        jButton8.setText(resourceMap.getString("jButton8.text")); // NOI18N
        jButton8.setFocusable(false);
        jButton8.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton8.setName("jButton8"); // NOI18N
        jButton8.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton8);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        getContentPane().add(jToolBar1, gridBagConstraints);

        splitPanelMaster.setBorder(null);
        splitPanelMaster.setDividerLocation(210);
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

        jLabel5.setFont(resourceMap.getFont("rdoInCNote.font")); // NOI18N
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        cmbContactable.setFont(resourceMap.getFont("rdoInCNote.font")); // NOI18N
        cmbContactable.setName("cmbContactable"); // NOI18N
        cmbContactable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbContactableActionPerformed(evt);
            }
        });

        dtFrom.setFont(resourceMap.getFont("rdoInCNote.font")); // NOI18N
        dtFrom.setName("dtFrom"); // NOI18N

        dtTo.setFont(resourceMap.getFont("rdoInCNote.font")); // NOI18N
        dtTo.setName("dtTo"); // NOI18N

        jLabel6.setFont(resourceMap.getFont("rdoInCNote.font")); // NOI18N
        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        jLabel7.setFont(resourceMap.getFont("rdoInCNote.font")); // NOI18N
        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        btnCriteriaSearch.setFont(resourceMap.getFont("btnCriteriaSearch.font")); // NOI18N
        btnCriteriaSearch.setIcon(resourceMap.getIcon("btnCriteriaSearch.icon")); // NOI18N
        btnCriteriaSearch.setText(resourceMap.getString("btnCriteriaSearch.text")); // NOI18N
        btnCriteriaSearch.setName("btnCriteriaSearch"); // NOI18N
        btnCriteriaSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCriteriaSearchActionPerformed(evt);
            }
        });

        busyIcon.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        busyIcon.setText(resourceMap.getString("busyIcon.text")); // NOI18N
        busyIcon.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        busyIcon.setName("busyIcon"); // NOI18N

        buttonGroup2.add(rdoInvoice);
        rdoInvoice.setFont(resourceMap.getFont("rdoInCNote.font")); // NOI18N
        rdoInvoice.setText(resourceMap.getString("rdoInvoice.text")); // NOI18N
        rdoInvoice.setName("rdoInvoice"); // NOI18N

        buttonGroup2.add(rdoCNote);
        rdoCNote.setFont(resourceMap.getFont("rdoInCNote.font")); // NOI18N
        rdoCNote.setText(resourceMap.getString("rdoCNote.text")); // NOI18N
        rdoCNote.setName("rdoCNote"); // NOI18N

        buttonGroup2.add(rdoInCNote);
        rdoInCNote.setFont(resourceMap.getFont("rdoInCNote.font")); // NOI18N
        rdoInCNote.setSelected(true);
        rdoInCNote.setText(resourceMap.getString("rdoInCNote.text")); // NOI18N
        rdoInCNote.setName("rdoInCNote"); // NOI18N

        jLabel2.setFont(resourceMap.getFont("rdoInCNote.font")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel3.setFont(resourceMap.getFont("rdoInCNote.font")); // NOI18N
        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        txtAcDocRef.setFont(resourceMap.getFont("rdoInCNote.font")); // NOI18N
        txtAcDocRef.setText(resourceMap.getString("txtAcDocRef.text")); // NOI18N
        txtAcDocRef.setName("txtAcDocRef"); // NOI18N
        txtAcDocRef.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtAcDocRefFocusGained(evt);
            }
        });

        txtGdsPnr.setFont(resourceMap.getFont("rdoInCNote.font")); // NOI18N
        txtGdsPnr.setText(resourceMap.getString("txtGdsPnr.text")); // NOI18N
        txtGdsPnr.setName("txtGdsPnr"); // NOI18N
        txtGdsPnr.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtGdsPnrFocusGained(evt);
            }
        });

        btnSearch.setFont(resourceMap.getFont("btnSearch.font")); // NOI18N
        btnSearch.setIcon(resourceMap.getIcon("btnSearch.icon")); // NOI18N
        btnSearch.setText(resourceMap.getString("btnSearch.text")); // NOI18N
        btnSearch.setName("btnSearch"); // NOI18N
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        jSeparator1.setName("jSeparator1"); // NOI18N

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jLabel5)
                        .addGap(22, 22, 22)
                        .addComponent(busyIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(cmbContactable, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(rdoInvoice)
                        .addGap(2, 2, 2)
                        .addComponent(rdoCNote)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rdoInCNote))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jLabel6))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(dtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jLabel7))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(dtTo, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jLabel2))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(txtAcDocRef, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jLabel3))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(txtGdsPnr, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(91, 91, 91)
                        .addComponent(btnCriteriaSearch))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(91, 91, 91)
                        .addComponent(btnSearch)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel5))
                    .addComponent(busyIcon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addComponent(cmbContactable, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rdoCNote)
                            .addComponent(rdoInvoice))
                        .addGap(6, 6, 6)
                        .addComponent(jLabel6))
                    .addComponent(rdoInCNote))
                .addGap(6, 6, 6)
                .addComponent(dtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(jLabel7)
                .addGap(6, 6, 6)
                .addComponent(dtTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btnCriteriaSearch)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 5, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(jLabel2)
                .addGap(4, 4, 4)
                .addComponent(txtAcDocRef, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(jLabel3)
                .addGap(4, 4, 4)
                .addComponent(txtGdsPnr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSearch)
                .addContainerGap())
        );

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
                "V.InvRef", "LeadPax", "Terms", "GDSPNR", "Career", "LeadPax", "InvAmount", "TransAmount", "Balance"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
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
        tblInvoice.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title11")); // NOI18N
        tblInvoice.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title1")); // NOI18N
        tblInvoice.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title10")); // NOI18N
        tblInvoice.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title2")); // NOI18N
        tblInvoice.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title11")); // NOI18N
        tblInvoice.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title6")); // NOI18N
        tblInvoice.getColumnModel().getColumn(7).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title7")); // NOI18N
        tblInvoice.getColumnModel().getColumn(8).setHeaderValue(resourceMap.getString("tblInvoice.columnModel.title8")); // NOI18N

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

        lblBalance.setFont(resourceMap.getFont("lblBalance.font")); // NOI18N
        lblBalance.setText(resourceMap.getString("lblBalance.text")); // NOI18N
        lblBalance.setName("lblBalance"); // NOI18N

        lblTotalOutstanding.setFont(resourceMap.getFont("lblBalance.font")); // NOI18N
        lblTotalOutstanding.setForeground(resourceMap.getColor("lblTotalOutstanding.foreground")); // NOI18N
        lblTotalOutstanding.setText(resourceMap.getString("lblTotalOutstanding.text")); // NOI18N
        lblTotalOutstanding.setName("lblTotalOutstanding"); // NOI18N

        lblTotalPaid.setFont(resourceMap.getFont("lblBalance.font")); // NOI18N
        lblTotalPaid.setForeground(resourceMap.getColor("lblTotalPaid.foreground")); // NOI18N
        lblTotalPaid.setText(resourceMap.getString("lblTotalPaid.text")); // NOI18N
        lblTotalPaid.setName("lblTotalPaid"); // NOI18N

        lblTAmount.setFont(resourceMap.getFont("lblBalance.font")); // NOI18N
        lblTAmount.setText(resourceMap.getString("lblTAmount.text")); // NOI18N
        lblTAmount.setName("lblTAmount"); // NOI18N

        lblTotalAmount.setFont(resourceMap.getFont("lblBalance.font")); // NOI18N
        lblTotalAmount.setText(resourceMap.getString("lblTotalAmount.text")); // NOI18N
        lblTotalAmount.setName("lblTotalAmount"); // NOI18N

        lblTotalInvAmount.setFont(resourceMap.getFont("lblBalance.font")); // NOI18N
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

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        new Thread(new threadSearchInvoice()).start();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void txtAcDocRefFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAcDocRefFocusGained
        txtGdsPnr.setText("");
    }//GEN-LAST:event_txtAcDocRefFocusGained

    private void txtGdsPnrFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtGdsPnrFocusGained
       txtAcDocRef.setText("");
    }//GEN-LAST:event_txtGdsPnrFocusGained

    private void btnViewInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewInvoiceActionPerformed
       viewTPAcDocDialogue();
    }//GEN-LAST:event_btnViewInvoiceActionPerformed

    /**
     * @param args the command line arguments
     */
  /*  public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new FramePAcDocHistory().setVisible(true);
            }
        });
    }*/
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane JScrollPane;
    private javax.swing.JButton btnCriteriaSearch;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnViewInvoice;
    private org.jdesktop.swingx.JXBusyLabel busyIcon;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JComboBox cmbContactable;
    private org.jdesktop.swingx.JXDatePicker dtFrom;
    private org.jdesktop.swingx.JXDatePicker dtTo;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
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
    private javax.swing.JRadioButton rdoCNote;
    private javax.swing.JRadioButton rdoInCNote;
    private javax.swing.JRadioButton rdoInvoice;
    private javax.swing.JSplitPane splitPanelMaster;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel summeryPanel;
    private org.jdesktop.swingx.JXTable tblInvoice;
    private javax.swing.JTextField txtAcDocRef;
    private javax.swing.JTextArea txtContactableDetails;
    private javax.swing.JTextField txtGdsPnr;
    // End of variables declaration//GEN-END:variables

    public void viewTPAcDocDialogue() {
        int row = tblInvoice.getSelectedRow();
        if (this.acDocs.get(row).getAcDoctype() == 1) {
            DlgTPInvoice dlgTPInvoice = new DlgTPInvoice(this);
            if (dlgTPInvoice.showDialog(this.acDocs.get(row))) {
                dlgTPInvoice.dispose();
            } else {
                //new Thread(new threadPurchaseAcDoc(pnr.getPnrId())).start();
            }
        } else if (this.acDocs.get(row).getAcDoctype() == 2) {
            DlgTPCNote dlgTPCNote = new DlgTPCNote(this);
            if (dlgTPCNote.showDialog(this.acDocs.get(row))) {
                dlgTPCNote.dispose();
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
            pAcDoc = acDocBo.getPurchaseAccountingDocument();
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            progressBar.repaint(); //Refresh graphics
        }
    }

    private class threadOutstandingInvoiceReport implements Runnable {

        private int noOfRecord;

        public threadOutstandingInvoiceReport() {

        }
        public void run() {
            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            progressBar.repaint(); //Refresh graphics
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
            progressBar.repaint(); //Refresh graphics
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
            noOfRecord = acDocs.size();
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

 private class threadLoadVendors implements Runnable {

        public threadLoadVendors() {
        }

        public void run() {
            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();

            statusMessageLabel.setText("Loading Agents...");
            btnSearch.setEnabled(false);

            vendors = agentBo.vendorList();

            populateCmbContactable();
            btnSearch.setEnabled(true);
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            progressBar.repaint(); //Refresh graphics
        }
    }
}
