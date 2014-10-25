/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrameOtherInvMain.java
 *
 * Created on 23-Mar-2011, 18:31:48
 */
package etsbackoffice.gui;

import etsbackoffice.ETSBackofficeApp;
import etsbackoffice.businesslogic.*;
import etsbackoffice.domain.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.jdesktop.application.Action;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 *
 * @author Yusuf
 */
public class FrameOInvMain extends javax.swing.JFrame{

    /** Creates new form FrameOtherInvMain */
    DefaultComboBoxModel cmbSTitleModel;
    JComboBox cmbSTitle;
    JSpinner spinner;
    private DefaultTableModel tblInvLineModel;
    private ControllFrame cf = new ControllFrame();
    
    private OServiceBo oServiceBo = (OServiceBo) ETSBackofficeApp.getApplication().ctx.getBean("oServiceBo");
    private OAccountingDocBo oAcDocBo = (OAccountingDocBo) ETSBackofficeApp.getApplication().ctx.getBean("oAcDocBo");
    
    private DateFormat df = new DateFormat();
    private List<OtherService> oServices = new ArrayList();
    private OAccountingDocument invoice;
    private List<OAccountingDocument> acDocs = new ArrayList();
    private List<OAccountingDocumentLine> lines = new ArrayList();

    public FrameOInvMain(java.awt.Frame parent) {
        initComponents();
        //setExtendedState(JFrame.MAXIMIZED_BOTH);               
    }

    private void initTblInvLine() {
        tblInvLineModel = (DefaultTableModel) tblInvLine.getModel();
        TableColumn sTitle, sChg, disc, vat, quantity;
        sTitle = tblInvLine.getColumnModel().getColumn(1);
        sChg = tblInvLine.getColumnModel().getColumn(2);
        disc = tblInvLine.getColumnModel().getColumn(3);
        vat = tblInvLine.getColumnModel().getColumn(4);
        quantity = tblInvLine.getColumnModel().getColumn(5);

        JTextField jtf = new JTextField();
        jtf.setDocument(new CheckInput(CheckInput.FLOAT));
        sChg.setCellEditor(new DefaultCellEditor(jtf));
        disc.setCellEditor(new DefaultCellEditor(jtf));
        vat.setCellEditor(new DefaultCellEditor(jtf));
        quantity.setCellEditor(new DefaultCellEditor(jtf));

        cmbSTitle = new JComboBox();
        cmbSTitle.setEditable(true);
        AutoCompleteDecorator.decorate(cmbSTitle);
        sTitle.setCellEditor(new DefaultCellEditor(cmbSTitle));
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

    private void populateTxtCustomerDetails(Customer customer) {

        txtContactableDetails.setText(customer.getSurName() + " " + customer.getForeName() + "\n");

        if (!customer.getContactPerson().isEmpty()) {
            txtContactableDetails.append("C/P: " + customer.getContactPerson() + "\n");
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

    private void populatetblInvLine() {
        tblInvLineModel = (DefaultTableModel) tblInvLine.getModel();
        tblInvLineModel.getDataVector().removeAllElements();
        tblInvLine.repaint();
        Iterator it = this.lines.iterator();
        int row = 0;
        while (it.hasNext()) {
            OAccountingDocumentLine line = (OAccountingDocumentLine) it.next();
            tblInvLineModel.insertRow(row, new Object[]{row, line.getServiceTitle(),
                        line.getServiceCharge(), line.getDiscount(), line.getVat(), line.getUnit(), line.getNetPayable()});
            row++;
        }
    }

    private void populateDocument() {
        BigDecimal subTotal = new BigDecimal("0.00");
        BigDecimal vat = new BigDecimal("0.00");
        String oChgRmk = "";

        for (OAccountingDocumentLine l : this.lines) {
            subTotal = subTotal.add(l.getNetPayable());
            vat = vat.add(l.getVat());
        }
        txtSubTotal.setText(subTotal.toString());
        txtVat.setText(vat.toString());
        txtInvAmount.setText(subTotal.add(vat).toString());
        txtBalance.setText(subTotal.add(vat).subtract(this.invoice.getTotalTransactionAmount()).toString());
    }

    private void populateDocumentHeader() {
        if (this.invoice.getCustomer() != null) {
            populateTxtCustomerDetails(this.invoice.getCustomer());
        } else if (this.invoice.getAgent() != null) {
            populateTxtAgentDetails(this.invoice.getAgent());
        }

        datePicker.setDate(this.invoice.getIssueDate());
        txtInvRef.setText(this.invoice.getAcDocRefString());
        txtUser.setText(this.invoice.getAcDocIssuedBy().getSurName() + "/" + this.invoice.getAcDocIssuedBy().getForeName());
        cmbTerms.setSelectedItem(this.invoice.getTerms());
    }

    private void searchAcDoc() {
        String invRef = txtAcDocRef.getText();
        String name = txtName.getText();

        if (!invRef.isEmpty()) {
            this.acDocs = oAcDocBo.findAcDocByRef(Integer.parseInt(invRef));
        } else if (!name.isEmpty()) {
            if (name.contains("/")) {
                String data[] = name.split("/");
                this.acDocs = oAcDocBo.findAcDocByCustomerName(data[0], data[1]);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Enter Invoice Ref or Name to Perform a Search", "Search Invoice", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void populateAcDocs() {
        DefaultTableModel tblAcDocsModel = (DefaultTableModel) tblAcDocs.getModel();
        tblAcDocsModel.getDataVector().removeAllElements();
        tblAcDocs.repaint();
        Iterator it = this.acDocs.iterator();
        int row = 0;
        while (it.hasNext()) {
            String cName = "";
            OAccountingDocument o = (OAccountingDocument) it.next();

            if (o.getCustomer() != null) {
                cName = o.getCustomer().getSurName() + "/" + o.getCustomer().getForeName();
            } else if (o.getAgent() != null) {
                cName = o.getAgent().getName();
            }

            tblAcDocsModel.insertRow(row, new Object[]{o.getAcDocRefString(), o.getAcDocTypeString(),
                        df.dateForGui(o.getIssueDate()), cName, null});
            row++;
        }
    }

    private void issueCNote(OAccountingDocument oAcDoc) {

        OAccountingDocument cNote = new OAccountingDocument();
        List<OAccountingDocumentLine> ls = new ArrayList();
        for (OAccountingDocumentLine l : oAcDoc.getoAccountingDocumentLines()) {
            l.setServiceCharge(l.getNetPayable().negate());
            l.setServiceCost(l.getServiceCost().negate());
            ls.add(l);
        }
        oAcDoc.setoAccountingDocumentLines(new LinkedHashSet(ls));        
        oAcDoc.setIssueDate(null);
        //cf.frameOItemCNote(oAcDoc);
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
        jToolBar1 = new javax.swing.JToolBar();
        btnNewInvoice = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        btnOServiceManager = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        statusMessageLabel = new javax.swing.JLabel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtContactableDetails = new javax.swing.JTextArea();
        jPanel9 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        txtSubTotal = new javax.swing.JTextField();
        txtVat = new javax.swing.JTextField();
        txtInvAmount = new javax.swing.JTextField();
        lblReceived = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtReceived = new javax.swing.JTextField();
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel13 = new javax.swing.JLabel();
        txtBalance = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblAcDocs = new org.jdesktop.swingx.JXTable();
        jPanel12 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtAcDocRef = new javax.swing.JTextField();
        txtName = new javax.swing.JTextField();
        btnSearchAcDoc = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblInvLine = new org.jdesktop.swingx.JXTable();
        jPanel8 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtInvRef = new javax.swing.JTextField();
        txtUser = new javax.swing.JTextField();
        datePicker = new org.jdesktop.swingx.JXDatePicker();
        cmbTerms = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getResourceMap(FrameOInvMain.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jToolBar1.setBorder(null);
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        btnNewInvoice.setText(resourceMap.getString("btnNewInvoice.text")); // NOI18N
        btnNewInvoice.setFocusable(false);
        btnNewInvoice.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNewInvoice.setName("btnNewInvoice"); // NOI18N
        btnNewInvoice.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNewInvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewInvoiceActionPerformed(evt);
            }
        });
        jToolBar1.add(btnNewInvoice);

        jButton4.setText(resourceMap.getString("jButton4.text")); // NOI18N
        jButton4.setFocusable(false);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.setName("jButton4"); // NOI18N
        jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton4);

        jButton5.setText(resourceMap.getString("jButton5.text")); // NOI18N
        jButton5.setFocusable(false);
        jButton5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton5.setName("jButton5"); // NOI18N
        jButton5.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton5);

        btnSave.setText(resourceMap.getString("btnSave.text")); // NOI18N
        btnSave.setFocusable(false);
        btnSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSave.setName("btnSave"); // NOI18N
        btnSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnSave);

        btnDelete.setText(resourceMap.getString("btnDelete.text")); // NOI18N
        btnDelete.setFocusable(false);
        btnDelete.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDelete.setName("btnDelete"); // NOI18N
        btnDelete.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnDelete);

        jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setName("jButton3"); // NOI18N
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton3);

        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setName("jButton1"); // NOI18N
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton1);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getActionMap(FrameOInvMain.class, this);
        btnOServiceManager.setAction(actionMap.get("frameOServiceManager")); // NOI18N
        btnOServiceManager.setText(resourceMap.getString("btnOServiceManager.text")); // NOI18N
        btnOServiceManager.setFocusable(false);
        btnOServiceManager.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnOServiceManager.setName("btnOServiceManager"); // NOI18N
        btnOServiceManager.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnOServiceManager);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        getContentPane().add(jToolBar1, gridBagConstraints);

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
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        getContentPane().add(jPanel4, gridBagConstraints);

        jSplitPane1.setDividerLocation(600);
        jSplitPane1.setDividerSize(4);
        jSplitPane1.setResizeWeight(1.0);
        jSplitPane1.setMinimumSize(new java.awt.Dimension(600, 200));
        jSplitPane1.setName("jSplitPane1"); // NOI18N
        jSplitPane1.setPreferredSize(new java.awt.Dimension(600, 200));

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        txtContactableDetails.setColumns(16);
        txtContactableDetails.setEditable(false);
        txtContactableDetails.setLineWrap(true);
        txtContactableDetails.setRows(5);
        txtContactableDetails.setMinimumSize(new java.awt.Dimension(104, 30));
        txtContactableDetails.setName("txtContactableDetails"); // NOI18N
        jScrollPane4.setViewportView(txtContactableDetails);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(jScrollPane4, gridBagConstraints);

        jPanel9.setBackground(resourceMap.getColor("jPanel9.background")); // NOI18N
        jPanel9.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel9.setName("jPanel9"); // NOI18N
        jPanel9.setLayout(new java.awt.GridBagLayout());

        jLabel8.setFont(resourceMap.getFont("jLabel8.font")); // NOI18N
        jLabel8.setForeground(resourceMap.getColor("jLabel8.foreground")); // NOI18N
        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(6, 2, 2, 2);
        jPanel9.add(jLabel8, gridBagConstraints);

        txtSubTotal.setEditable(false);
        txtSubTotal.setFont(resourceMap.getFont("txtSubTotal.font")); // NOI18N
        txtSubTotal.setText(resourceMap.getString("txtSubTotal.text")); // NOI18N
        txtSubTotal.setName("txtSubTotal"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 2, 2, 2);
        jPanel9.add(txtSubTotal, gridBagConstraints);

        txtVat.setBackground(resourceMap.getColor("txtVat.background")); // NOI18N
        txtVat.setEditable(false);
        txtVat.setFont(resourceMap.getFont("txtVat.font")); // NOI18N
        txtVat.setText(resourceMap.getString("txtVat.text")); // NOI18N
        txtVat.setName("txtVat"); // NOI18N
        txtVat.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtVatFocusGained(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel9.add(txtVat, gridBagConstraints);

        txtInvAmount.setBackground(resourceMap.getColor("txtInvAmount.background")); // NOI18N
        txtInvAmount.setEditable(false);
        txtInvAmount.setFont(resourceMap.getFont("txtInvAmount.font")); // NOI18N
        txtInvAmount.setForeground(resourceMap.getColor("txtInvAmount.foreground")); // NOI18N
        txtInvAmount.setText(resourceMap.getString("txtInvAmount.text")); // NOI18N
        txtInvAmount.setName("txtInvAmount"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel9.add(txtInvAmount, gridBagConstraints);

        lblReceived.setFont(resourceMap.getFont("lblReceived.font")); // NOI18N
        lblReceived.setForeground(resourceMap.getColor("lblReceived.foreground")); // NOI18N
        lblReceived.setText(resourceMap.getString("lblReceived.text")); // NOI18N
        lblReceived.setName("lblReceived"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel9.add(lblReceived, gridBagConstraints);

        jLabel12.setFont(resourceMap.getFont("jLabel12.font")); // NOI18N
        jLabel12.setForeground(resourceMap.getColor("jLabel12.foreground")); // NOI18N
        jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel9.add(jLabel12, gridBagConstraints);

        txtReceived.setBackground(resourceMap.getColor("txtReceived.background")); // NOI18N
        txtReceived.setEditable(false);
        txtReceived.setFont(resourceMap.getFont("txtReceived.font")); // NOI18N
        txtReceived.setForeground(resourceMap.getColor("txtReceived.foreground")); // NOI18N
        txtReceived.setText(resourceMap.getString("txtReceived.text")); // NOI18N
        txtReceived.setName("txtReceived"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel9.add(txtReceived, gridBagConstraints);

        jSeparator3.setName("jSeparator3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 0, 2);
        jPanel9.add(jSeparator3, gridBagConstraints);

        jSeparator4.setName("jSeparator4"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 4, 2);
        jPanel9.add(jSeparator4, gridBagConstraints);

        jLabel13.setFont(resourceMap.getFont("jLabel13.font")); // NOI18N
        jLabel13.setForeground(resourceMap.getColor("jLabel13.foreground")); // NOI18N
        jLabel13.setText(resourceMap.getString("jLabel13.text")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel9.add(jLabel13, gridBagConstraints);

        txtBalance.setBackground(resourceMap.getColor("txtBalance.background")); // NOI18N
        txtBalance.setEditable(false);
        txtBalance.setFont(resourceMap.getFont("txtBalance.font")); // NOI18N
        txtBalance.setText(resourceMap.getString("txtBalance.text")); // NOI18N
        txtBalance.setName("txtBalance"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 6, 2);
        jPanel9.add(txtBalance, gridBagConstraints);

        jLabel11.setFont(resourceMap.getFont("jLabel11.font")); // NOI18N
        jLabel11.setForeground(resourceMap.getColor("jLabel11.foreground")); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel9.add(jLabel11, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
        jPanel1.add(jPanel9, gridBagConstraints);

        jSplitPane1.setRightComponent(jPanel1);

        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new java.awt.GridBagLayout());

        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setLayout(new java.awt.CardLayout(2, 2));

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tblAcDocs.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "AcDocRef", "Type", "IssueDate", "Client", "Amount"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblAcDocs.setEditable(false);
        tblAcDocs.setFont(resourceMap.getFont("tblAcDocs.font")); // NOI18N
        tblAcDocs.setName("tblAcDocs"); // NOI18N
        tblAcDocs.setSortable(false);
        tblAcDocs.getTableHeader().setReorderingAllowed(false);
        tblAcDocs.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblAcDocsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblAcDocs);
        tblAcDocs.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblAcDocs.columnModel.title0")); // NOI18N
        tblAcDocs.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblAcDocs.columnModel.title1")); // NOI18N
        tblAcDocs.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblAcDocs.columnModel.title2")); // NOI18N
        tblAcDocs.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblAcDocs.columnModel.title3")); // NOI18N
        tblAcDocs.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("tblAcDocs.columnModel.title4")); // NOI18N

        jPanel3.add(jScrollPane1, "card2");

        jTabbedPane1.addTab(resourceMap.getString("jPanel3.TabConstraints.tabTitle"), jPanel3); // NOI18N

        jPanel12.setName("jPanel12"); // NOI18N

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 351, Short.MAX_VALUE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 117, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel12.TabConstraints.tabTitle"), jPanel12); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.6;
        jPanel2.add(jTabbedPane1, gridBagConstraints);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, resourceMap.getString("jPanel5.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, resourceMap.getFont("jPanel5.border.titleFont"))); // NOI18N
        jPanel5.setName("jPanel5"); // NOI18N
        jPanel5.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 4, 4, 4);
        jPanel5.add(jLabel1, gridBagConstraints);

        jLabel2.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel5.add(jLabel2, gridBagConstraints);

        txtAcDocRef.setFont(resourceMap.getFont("txtName.font")); // NOI18N
        txtAcDocRef.setText(resourceMap.getString("txtAcDocRef.text")); // NOI18N
        txtAcDocRef.setMinimumSize(new java.awt.Dimension(100, 20));
        txtAcDocRef.setName("txtAcDocRef"); // NOI18N
        txtAcDocRef.setPreferredSize(new java.awt.Dimension(150, 20));
        txtAcDocRef.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAcDocRefActionPerformed(evt);
            }
        });
        txtAcDocRef.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtAcDocRefFocusGained(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 4, 4, 4);
        jPanel5.add(txtAcDocRef, gridBagConstraints);

        txtName.setFont(resourceMap.getFont("txtName.font")); // NOI18N
        txtName.setText(resourceMap.getString("txtName.text")); // NOI18N
        txtName.setMinimumSize(new java.awt.Dimension(100, 20));
        txtName.setName("txtName"); // NOI18N
        txtName.setPreferredSize(new java.awt.Dimension(150, 20));
        txtName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNameActionPerformed(evt);
            }
        });
        txtName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtNameFocusGained(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel5.add(txtName, gridBagConstraints);

        btnSearchAcDoc.setFont(resourceMap.getFont("btnSearchAcDoc.font")); // NOI18N
        btnSearchAcDoc.setIcon(resourceMap.getIcon("btnSearchAcDoc.icon")); // NOI18N
        btnSearchAcDoc.setText(resourceMap.getString("btnSearchAcDoc.text")); // NOI18N
        btnSearchAcDoc.setName("btnSearchAcDoc"); // NOI18N
        btnSearchAcDoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchAcDocActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 4, 4, 4);
        jPanel5.add(btnSearchAcDoc, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 0.1;
        jPanel2.add(jPanel5, gridBagConstraints);

        jPanel6.setBackground(resourceMap.getColor("jPanel6.background")); // NOI18N
        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel6.setName("jPanel6"); // NOI18N
        jPanel6.setLayout(new java.awt.GridBagLayout());

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        tblInvLine.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "", "Service", "GrossCharge", "Disc", "VAT", "Quantity", "Net"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true, true, false, true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblInvLine.setEditable(false);
        tblInvLine.setName("tblInvLine"); // NOI18N
        tblInvLine.setSortable(false);
        tblInvLine.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tblInvLine);
        tblInvLine.getColumnModel().getColumn(0).setMinWidth(40);
        tblInvLine.getColumnModel().getColumn(0).setPreferredWidth(40);
        tblInvLine.getColumnModel().getColumn(0).setMaxWidth(50);
        tblInvLine.getColumnModel().getColumn(2).setMinWidth(80);
        tblInvLine.getColumnModel().getColumn(2).setPreferredWidth(80);
        tblInvLine.getColumnModel().getColumn(2).setMaxWidth(100);
        tblInvLine.getColumnModel().getColumn(3).setMinWidth(80);
        tblInvLine.getColumnModel().getColumn(3).setPreferredWidth(80);
        tblInvLine.getColumnModel().getColumn(3).setMaxWidth(100);
        tblInvLine.getColumnModel().getColumn(4).setMinWidth(80);
        tblInvLine.getColumnModel().getColumn(4).setPreferredWidth(80);
        tblInvLine.getColumnModel().getColumn(4).setMaxWidth(100);
        tblInvLine.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("tblInvLine.columnModel.title5")); // NOI18N
        tblInvLine.getColumnModel().getColumn(5).setMinWidth(80);
        tblInvLine.getColumnModel().getColumn(5).setPreferredWidth(80);
        tblInvLine.getColumnModel().getColumn(5).setMaxWidth(80);
        tblInvLine.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("tblInvLine.columnModel.title6")); // NOI18N
        tblInvLine.getColumnModel().getColumn(6).setMinWidth(80);
        tblInvLine.getColumnModel().getColumn(6).setPreferredWidth(80);
        tblInvLine.getColumnModel().getColumn(6).setMaxWidth(100);
        tblInvLine.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("tblInvLine.columnModel.title4")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(jScrollPane2, gridBagConstraints);

        jPanel8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel8.setName("jPanel8"); // NOI18N

        jLabel4.setFont(resourceMap.getFont("jLabel4.font")); // NOI18N
        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        jLabel5.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        jLabel6.setFont(resourceMap.getFont("jLabel6.font")); // NOI18N
        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        jLabel7.setFont(resourceMap.getFont("jLabel7.font")); // NOI18N
        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        txtInvRef.setEditable(false);
        txtInvRef.setFont(resourceMap.getFont("txtInvRef.font")); // NOI18N
        txtInvRef.setName("txtInvRef"); // NOI18N

        txtUser.setEditable(false);
        txtUser.setFont(resourceMap.getFont("txtUser.font")); // NOI18N
        txtUser.setName("txtUser"); // NOI18N

        datePicker.setDate(new java.util.Date());
        datePicker.setFont(resourceMap.getFont("datePicker.font")); // NOI18N
        datePicker.setName("datePicker"); // NOI18N

        cmbTerms.setFont(resourceMap.getFont("cmbTerms.font")); // NOI18N
        cmbTerms.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Select", "CIA", "COD", "CWO", "Net monthly account", "Net 7", "Net 10", "Net 30", "Net 60", "Net 90" }));
        cmbTerms.setName("cmbTerms"); // NOI18N

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(cmbTerms, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtUser, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtInvRef, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(datePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(datePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtInvRef, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(cmbTerms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(jPanel8, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(jPanel6, gridBagConstraints);

        jSplitPane1.setLeftComponent(jPanel2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(jSplitPane1, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnNewInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewInvoiceActionPerformed
    }//GEN-LAST:event_btnNewInvoiceActionPerformed

    private void btnSearchAcDocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchAcDocActionPerformed
        new Thread(new threadFindAcDoc()).start();
    }//GEN-LAST:event_btnSearchAcDocActionPerformed

    private void tblAcDocsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblAcDocsMouseClicked
        if (evt.getClickCount() == 2) {
            int row = tblAcDocs.getSelectedRow();
            if (row != -1) {
                new Thread(new threadLoadCompleteAcDoc(this.acDocs.get(row).getoAcDocId())).start();
            }
        }
    }//GEN-LAST:event_tblAcDocsMouseClicked

    private void txtAcDocRefActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAcDocRefActionPerformed
        new Thread(new threadFindAcDoc()).start();
    }//GEN-LAST:event_txtAcDocRefActionPerformed

    private void txtNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNameActionPerformed
        new Thread(new threadFindAcDoc()).start();
    }//GEN-LAST:event_txtNameActionPerformed

    private void txtAcDocRefFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAcDocRefFocusGained
        txtName.setText("");
    }//GEN-LAST:event_txtAcDocRefFocusGained

    private void txtNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNameFocusGained
        txtAcDocRef.setText("");
    }//GEN-LAST:event_txtNameFocusGained

    private void txtVatFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtVatFocusGained
        txtVat.selectAll();
}//GEN-LAST:event_txtVatFocusGained

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        cf.frameOAcDoc(this.invoice);
    }//GEN-LAST:event_jButton3ActionPerformed

    /**
     * @param args the command line arguments
     */
 /*   public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new FrameOInvMain().setVisible(true);
            }
        });
    }*/
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnNewInvoice;
    private javax.swing.JButton btnOServiceManager;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSearchAcDoc;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox cmbTerms;
    private org.jdesktop.swingx.JXDatePicker datePicker;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblReceived;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel statusMessageLabel;
    private org.jdesktop.swingx.JXTable tblAcDocs;
    private org.jdesktop.swingx.JXTable tblInvLine;
    private javax.swing.JTextField txtAcDocRef;
    private javax.swing.JTextField txtBalance;
    private javax.swing.JTextArea txtContactableDetails;
    private javax.swing.JTextField txtInvAmount;
    private javax.swing.JTextField txtInvRef;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtReceived;
    private javax.swing.JTextField txtSubTotal;
    private javax.swing.JTextField txtUser;
    private javax.swing.JTextField txtVat;
    // End of variables declaration//GEN-END:variables
    private long startTime = 0;
    private long stopTime = 0;
    private float elapsedTime = 0;

    /**
     * @param pnrDao the pnrDao to set
     */
    private class threadLoadAllServices implements Runnable {

        public threadLoadAllServices() {
        }

        public void run() {

            //progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            //progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();
            statusMessageLabel.setText("Loading Services...");

            oServices = oServiceBo.loadAllServices();

            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            //progressBar.repaint(); //Refresh graphics
        }
    }

    private class threadFindAcDoc implements Runnable {

        public threadFindAcDoc() {
        }

        public void run() {

            //progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            //progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();
            statusMessageLabel.setText("Searching...");

            searchAcDoc();

            populateAcDocs();
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Search Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            //progressBar.repaint(); //Refresh graphics
        }
    }

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

            invoice = oAcDocBo.findCompleteAcDocById(acDocId);

            lines = new ArrayList(invoice.getoAccountingDocumentLines());
            if (invoice.getRelatedDocuments().size() > 0) {
                for (OAccountingDocument o : invoice.getRelatedDocuments()) {
                    lines.addAll(o.getoAccountingDocumentLines());
                }
            }
            populateDocumentHeader();
            populatetblInvLine();
            populateDocument();
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Search Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            //progressBar.repaint(); //Refresh graphics
        }
    } 
}
