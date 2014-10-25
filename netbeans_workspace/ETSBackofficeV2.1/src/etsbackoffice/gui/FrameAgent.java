/*
 * FrameAgent.java
 */
package etsbackoffice.gui;

import etsbackoffice.businesslogic.AuthenticationBo;
import etsbackoffice.domain.Agent;
import etsbackoffice.businesslogic.DocumentSizeFilter;
import etsbackoffice.businesslogic.DocumentSizeFilterLowerCase;
import etsbackoffice.domain.GDS;
import etsbackoffice.domain.OfficeID;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import org.jdesktop.application.Action;

public class FrameAgent extends JDialog implements ActionListener {

    // Local Fields
    private boolean save;
    

    public FrameAgent(java.awt.Frame parent) {
        super(parent, "New agent", true);
        initComponents();
        btnSave.addActionListener(this);
        //getRootPane().setDefaultButton(btnClose);
        //setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
         txtCLimit.setDocument(new CheckInput(CheckInput.FLOAT));
        
        AbstractDocument doc;
        doc = (AbstractDocument) txtName.getDocument();
        doc.setDocumentFilter(new DocumentSizeFilter(45));
        doc = (AbstractDocument) txtContactPerson.getDocument();
        doc.setDocumentFilter(new DocumentSizeFilter(45));
        doc = (AbstractDocument) txtAddLine1.getDocument();
        doc.setDocumentFilter(new DocumentSizeFilter(45));
        doc = (AbstractDocument) txtAddLine2.getDocument();
        doc.setDocumentFilter(new DocumentSizeFilter(45));
        doc = (AbstractDocument) txtCity.getDocument();
        doc.setDocumentFilter(new DocumentSizeFilter(45));
        doc = (AbstractDocument) txtProvince.getDocument();
        doc.setDocumentFilter(new DocumentSizeFilter(45));
        doc = (AbstractDocument) txtPostCode.getDocument();
        doc.setDocumentFilter(new DocumentSizeFilter(15));
        doc = (AbstractDocument) txtTelNo.getDocument();
        doc.setDocumentFilter(new DocumentSizeFilter(15));
        doc = (AbstractDocument) txtFax.getDocument();
        doc.setDocumentFilter(new DocumentSizeFilter(15));
        doc = (AbstractDocument) txtMobile.getDocument();
        doc.setDocumentFilter(new DocumentSizeFilter(15));
        doc = (AbstractDocument) txtEmail.getDocument();
        doc.setDocumentFilter(new DocumentSizeFilterLowerCase(45));
        doc = (AbstractDocument) txtWeb.getDocument();
        doc.setDocumentFilter(new DocumentSizeFilterLowerCase(45));
        doc = (AbstractDocument) txtIATANo.getDocument();
        doc.setDocumentFilter(new DocumentSizeFilter(10));
        doc = (AbstractDocument) txtATOLNo.getDocument();
        doc.setDocumentFilter(new DocumentSizeFilter(10));
        doc = (AbstractDocument) txtRemark.getDocument();
        doc.setDocumentFilter(new DocumentSizeFilter(400));

        doc = (AbstractDocument) txtOID.getDocument();
        doc.setDocumentFilter(new DocumentSizeFilter(12));
        txtName.requestFocusInWindow();
    }
       

    @Action
    public void closeAboutBox() {
        dispose();
    }

    private void populateTblOID(List<OfficeID> officeIDs) {
        DefaultTableModel oidModel = (DefaultTableModel) tblOID.getModel();
           
        List<OfficeID> aOfficeID = new ArrayList();
        List<OfficeID> gOfficeID = new ArrayList();
        List<OfficeID> sOfficeID = new ArrayList();
        List<OfficeID> wOfficeID = new ArrayList();

        for (int i = 0; i < officeIDs.size(); i++) {
            if (officeIDs.get(i).getGds().getGdsId() == 1) {
                aOfficeID.add(officeIDs.get(i));
            } else if (officeIDs.get(i).getGds().getGdsId() == 2) {
                gOfficeID.add(officeIDs.get(i));
            } else if (officeIDs.get(i).getGds().getGdsId() == 3) {
                sOfficeID.add(officeIDs.get(i));
            } else if (officeIDs.get(i).getGds().getGdsId() == 4) {
                wOfficeID.add(officeIDs.get(i));
            }
        }
        for (int col = 0; col < 4; col++) {
            if (col == 0) {
                for (int row = 0; row < aOfficeID.size(); row++) {
                    oidModel.setValueAt(aOfficeID.get(row).getOfficeID(), row, col);
                }
            } else if (col == 1) {
                for (int row = 0; row < gOfficeID.size(); row++) {
                    oidModel.setValueAt(gOfficeID.get(row).getOfficeID(), row, col);
                }
            } else if (col == 2) {
                for (int row = 0; row < sOfficeID.size(); row++) {
                    oidModel.setValueAt(sOfficeID.get(row).getOfficeID(), row, col);
                }
            } else if (col == 3) {
                for (int row = 0; row < wOfficeID.size(); row++) {
                    oidModel.setValueAt(wOfficeID.get(row).getOfficeID(), row, col);
                }
            }
        }
    }

    public List<OfficeID> addNewOfficeID(Agent agent) {
        List<OfficeID> officeIDs = new ArrayList();
        OfficeID officeID = new OfficeID();
        GDS gds = new GDS();
        String newOfficeID = txtOID.getText();
        if (cmbGDS.getSelectedIndex() > 0 && !newOfficeID.equals("")) {
            gds.setGdsId(cmbGDS.getSelectedIndex());
            officeID.setOfficeID(newOfficeID.trim().toUpperCase());
            officeID.setAgent(agent);
            officeID.setGds(gds);
            if (rdoMain.isSelected()) {
                officeID.setType(1);
            } else if (rdoSubAgent.isSelected()) {
                officeID.setType(2);
            }
            officeIDs.add(officeID);
        }
        return officeIDs;
    }

    public boolean showAgentDialog(Agent agent) {
        if (agent != null) {
            txtName.setText(agent.getName());
            txtAddLine1.setText(agent.getAddLine1());
            txtAddLine2.setText(agent.getAddLine2());
            txtCity.setText(agent.getCity());
            txtProvince.setText(agent.getProvince());
            txtPostCode.setText(agent.getPostCode());
            txtTelNo.setText(agent.getTelNo());
            txtMobile.setText(agent.getMobile());
            txtEmail.setText(agent.getEmail());
            txtFax.setText(agent.getFax());
            txtWeb.setText(agent.getWeb());
            txtIATANo.setText(agent.getIata());
            txtATOLNo.setText(agent.getAtol());
            if (agent.getCreditLimit() != null) {
                txtCLimit.setText(agent.getCreditLimit().toString());
            }
            if (agent.iscLimitOverInvoicing() == true) {
                chkAllowInvoicing.setSelected(true);
            } else {
                chkAllowInvoicing.setSelected(false);
            }

            if (agent.getOfficeIDs().size() > 0) {
                populateTblOID(agent.getOfficeIDs());
            }
        }
        save = false;
        setLocationRelativeTo(this);
        setVisible(true);

        if (save) {

            agent.setName(txtName.getText().trim());
            agent.setWeb(txtWeb.getText().trim());
            agent.setAddLine1(txtAddLine1.getText().trim());
            agent.setAddLine2(txtAddLine2.getText().trim());
            agent.setCity(txtCity.getText().trim());
            agent.setProvince(txtProvince.getText().trim());
            agent.setPostCode(txtPostCode.getText().trim());
            agent.setTelNo(txtTelNo.getText().trim());
            agent.setFax(txtFax.getText().trim());
            agent.setMobile(txtMobile.getText().trim());
            agent.setEmail(txtEmail.getText().trim());
            agent.setRemark(txtRemark.getText().trim());
            if (agent.getCreditLimit() != null) {
                agent.setCreditLimit(new BigDecimal(txtCLimit.getText().trim()));
            }
            if (chkAllowInvoicing.isSelected()) {
                agent.setcLimitOverInvoicing(true);
            } else {
                agent.setcLimitOverInvoicing(false);
            }

            if (agent.getContactableId() == 0) {
                agent.setCreatedOn(new java.util.Date());
                agent.setCreatedBy(AuthenticationBo.getLoggedOnUser());
                agent.setLastModifiedBy(null);
                agent.setLastModified(null);
            } else {
                agent.setLastModifiedBy(AuthenticationBo.getLoggedOnUser());
                agent.setLastModified(new java.util.Date());
            }
            agent.setIata(txtIATANo.getText().trim());
            agent.setAtol(txtATOLNo.getText().trim());
            agent.setOfficeIDs(addNewOfficeID(agent));
        }
        return save;
    }

    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();
        if (source == btnSave) {
            if (txtName.getText().isEmpty() || txtAddLine1.getText().isEmpty() || txtPostCode.getText().isEmpty()) {
                lblInfo.setText("Enter mendatory fields...");
            } else {
                save = true;
                setVisible(false);
            }
        } else if (source == btnClose) {
            setVisible(false);
        }
    }

    private ActionListener cmbGDSListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            if (cmbGDS.getSelectedIndex() > 0) {
                txtOID.setEditable(true);
                txtOID.requestFocus();
                rdoMain.setEnabled(true);
                rdoSubAgent.setEnabled(true);
            } else {
                txtOID.setEditable(false);
                rdoMain.setEnabled(false);
                rdoSubAgent.setEnabled(false);
            }
        }
    };
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel2 = new javax.swing.JPanel();
        lblName = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        lblAddress = new javax.swing.JLabel();
        lblTelNo1 = new javax.swing.JLabel();
        txtTelNo = new javax.swing.JTextField();
        lblTelNo2 = new javax.swing.JLabel();
        txtTelNo1 = new javax.swing.JTextField();
        lblFax = new javax.swing.JLabel();
        lblMobile = new javax.swing.JLabel();
        lblWeb = new javax.swing.JLabel();
        lblIATA = new javax.swing.JLabel();
        lblATOL = new javax.swing.JLabel();
        lblRemark = new javax.swing.JLabel();
        lblEmail = new javax.swing.JLabel();
        txtFax = new javax.swing.JTextField();
        txtMobile = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        txtWeb = new javax.swing.JTextField();
        txtIATANo = new javax.swing.JTextField();
        txtATOLNo = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtRemark = new javax.swing.JTextArea();
        lblPostCode = new javax.swing.JLabel();
        txtPostCode = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtAddLine1 = new javax.swing.JTextField();
        txtAddLine2 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtCity = new javax.swing.JTextField();
        txtProvince = new javax.swing.JTextField();
        lblContactPerson = new javax.swing.JLabel();
        txtContactPerson = new javax.swing.JTextField();
        btnSave = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        lblOID = new javax.swing.JLabel();
        txtOID = new javax.swing.JTextField();
        lblGDS = new javax.swing.JLabel();
        cmbGDS = new javax.swing.JComboBox();
        cmbGDS.addActionListener(cmbGDSListener);
        lblMessage = new javax.swing.JLabel();
        rdoMain = new javax.swing.JRadioButton();
        rdoSubAgent = new javax.swing.JRadioButton();
        jLabel5 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblOID = new javax.swing.JTable();
        jToolBar1 = new javax.swing.JToolBar();
        jButton1 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        lblInfo = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        txtCLimit = new javax.swing.JTextField();
        chkAllowInvoicing = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getResourceMap(FrameAgent.class);
        setTitle(resourceMap.getString("title")); // NOI18N
        setName("aboutBox"); // NOI18N
        setResizable(false);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel2.border.title"))); // NOI18N
        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new java.awt.GridBagLayout());

        lblName.setFont(resourceMap.getFont("lblName.font")); // NOI18N
        lblName.setText(resourceMap.getString("lblName.text")); // NOI18N
        lblName.setName("lblName"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel2.add(lblName, gridBagConstraints);

        txtName.setFont(resourceMap.getFont("txtTelNo.font")); // NOI18N
        txtName.setName("txtName"); // NOI18N
        txtName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNameActionPerformed(evt);
            }
        });
        txtName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtNameFocusLost(evt);
            }
        });
        txtName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtNameKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(txtName, gridBagConstraints);

        lblAddress.setFont(resourceMap.getFont("lblName.font")); // NOI18N
        lblAddress.setText(resourceMap.getString("lblAddress.text")); // NOI18N
        lblAddress.setName("lblAddress"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel2.add(lblAddress, gridBagConstraints);

        lblTelNo1.setFont(resourceMap.getFont("lblName.font")); // NOI18N
        lblTelNo1.setText(resourceMap.getString("lblTelNo1.text")); // NOI18N
        lblTelNo1.setName("lblTelNo1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel2.add(lblTelNo1, gridBagConstraints);

        txtTelNo.setFont(resourceMap.getFont("txtTelNo.font")); // NOI18N
        txtTelNo.setName("txtTelNo"); // NOI18N
        txtTelNo.setPreferredSize(new java.awt.Dimension(150, 20));
        txtTelNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtTelNoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtTelNoFocusLost(evt);
            }
        });
        txtTelNo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtTelNoKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(txtTelNo, gridBagConstraints);

        lblTelNo2.setFont(resourceMap.getFont("lblName.font")); // NOI18N
        lblTelNo2.setText(resourceMap.getString("lblTelNo2.text")); // NOI18N
        lblTelNo2.setName("lblTelNo2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel2.add(lblTelNo2, gridBagConstraints);

        txtTelNo1.setFont(resourceMap.getFont("txtTelNo.font")); // NOI18N
        txtTelNo1.setName("txtTelNo1"); // NOI18N
        txtTelNo1.setPreferredSize(new java.awt.Dimension(150, 20));
        txtTelNo1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtTelNo1FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtTelNo1FocusLost(evt);
            }
        });
        txtTelNo1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtTelNo1KeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(txtTelNo1, gridBagConstraints);

        lblFax.setFont(resourceMap.getFont("lblName.font")); // NOI18N
        lblFax.setText(resourceMap.getString("lblFax.text")); // NOI18N
        lblFax.setName("lblFax"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel2.add(lblFax, gridBagConstraints);

        lblMobile.setFont(resourceMap.getFont("lblName.font")); // NOI18N
        lblMobile.setText(resourceMap.getString("lblMobile.text")); // NOI18N
        lblMobile.setName("lblMobile"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel2.add(lblMobile, gridBagConstraints);

        lblWeb.setFont(resourceMap.getFont("lblName.font")); // NOI18N
        lblWeb.setText(resourceMap.getString("lblWeb.text")); // NOI18N
        lblWeb.setName("lblWeb"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel2.add(lblWeb, gridBagConstraints);

        lblIATA.setFont(resourceMap.getFont("lblName.font")); // NOI18N
        lblIATA.setText(resourceMap.getString("lblIATA.text")); // NOI18N
        lblIATA.setName("lblIATA"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel2.add(lblIATA, gridBagConstraints);

        lblATOL.setFont(resourceMap.getFont("lblName.font")); // NOI18N
        lblATOL.setText(resourceMap.getString("lblATOL.text")); // NOI18N
        lblATOL.setName("lblATOL"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel2.add(lblATOL, gridBagConstraints);

        lblRemark.setFont(resourceMap.getFont("lblName.font")); // NOI18N
        lblRemark.setText(resourceMap.getString("lblRemark.text")); // NOI18N
        lblRemark.setName("lblRemark"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel2.add(lblRemark, gridBagConstraints);

        lblEmail.setFont(resourceMap.getFont("lblName.font")); // NOI18N
        lblEmail.setText(resourceMap.getString("lblEmail.text")); // NOI18N
        lblEmail.setName("lblEmail"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel2.add(lblEmail, gridBagConstraints);

        txtFax.setFont(resourceMap.getFont("txtTelNo.font")); // NOI18N
        txtFax.setName("txtFax"); // NOI18N
        txtFax.setPreferredSize(new java.awt.Dimension(150, 20));
        txtFax.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtFaxFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtFaxFocusLost(evt);
            }
        });
        txtFax.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtFaxKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(txtFax, gridBagConstraints);

        txtMobile.setFont(resourceMap.getFont("txtTelNo.font")); // NOI18N
        txtMobile.setName("txtMobile"); // NOI18N
        txtMobile.setPreferredSize(new java.awt.Dimension(150, 20));
        txtMobile.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtMobileFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtMobileFocusLost(evt);
            }
        });
        txtMobile.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtMobileKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(txtMobile, gridBagConstraints);

        txtEmail.setFont(resourceMap.getFont("txtTelNo.font")); // NOI18N
        txtEmail.setName("txtEmail"); // NOI18N
        txtEmail.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtEmailFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtEmailFocusLost(evt);
            }
        });
        txtEmail.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtEmailKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(txtEmail, gridBagConstraints);

        txtWeb.setFont(resourceMap.getFont("txtTelNo.font")); // NOI18N
        txtWeb.setName("txtWeb"); // NOI18N
        txtWeb.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtWebFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtWebFocusLost(evt);
            }
        });
        txtWeb.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtWebKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(txtWeb, gridBagConstraints);

        txtIATANo.setFont(resourceMap.getFont("txtTelNo.font")); // NOI18N
        txtIATANo.setName("txtIATANo"); // NOI18N
        txtIATANo.setPreferredSize(new java.awt.Dimension(150, 20));
        txtIATANo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtIATANoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtIATANoFocusLost(evt);
            }
        });
        txtIATANo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtIATANoKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(txtIATANo, gridBagConstraints);

        txtATOLNo.setFont(resourceMap.getFont("txtTelNo.font")); // NOI18N
        txtATOLNo.setName("txtATOLNo"); // NOI18N
        txtATOLNo.setPreferredSize(new java.awt.Dimension(150, 20));
        txtATOLNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtATOLNoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtATOLNoFocusLost(evt);
            }
        });
        txtATOLNo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtATOLNoKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(txtATOLNo, gridBagConstraints);

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        txtRemark.setColumns(20);
        txtRemark.setFont(resourceMap.getFont("txtTelNo.font")); // NOI18N
        txtRemark.setLineWrap(true);
        txtRemark.setRows(5);
        txtRemark.setTabSize(4);
        txtRemark.setWrapStyleWord(true);
        txtRemark.setName("txtRemark"); // NOI18N
        txtRemark.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtRemarkFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtRemarkFocusLost(evt);
            }
        });
        txtRemark.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtRemarkKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(txtRemark);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(jScrollPane2, gridBagConstraints);

        lblPostCode.setFont(resourceMap.getFont("lblName.font")); // NOI18N
        lblPostCode.setText(resourceMap.getString("lblPostCode.text")); // NOI18N
        lblPostCode.setName("lblPostCode"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel2.add(lblPostCode, gridBagConstraints);

        txtPostCode.setFont(resourceMap.getFont("txtTelNo.font")); // NOI18N
        txtPostCode.setName("txtPostCode"); // NOI18N
        txtPostCode.setPreferredSize(new java.awt.Dimension(150, 20));
        txtPostCode.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPostCodeFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPostCodeFocusLost(evt);
            }
        });
        txtPostCode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPostCodeKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(txtPostCode, gridBagConstraints);

        jLabel1.setFont(resourceMap.getFont("lblName.font")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel2.add(jLabel1, gridBagConstraints);

        jLabel2.setFont(resourceMap.getFont("lblName.font")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel2.add(jLabel2, gridBagConstraints);

        txtAddLine1.setFont(resourceMap.getFont("txtTelNo.font")); // NOI18N
        txtAddLine1.setText(resourceMap.getString("txtAddLine1.text")); // NOI18N
        txtAddLine1.setCaretColor(resourceMap.getColor("txtAddLine1.caretColor")); // NOI18N
        txtAddLine1.setName("txtAddLine1"); // NOI18N
        txtAddLine1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtAddLine1FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtAddLine1FocusLost(evt);
            }
        });
        txtAddLine1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtAddLine1KeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(txtAddLine1, gridBagConstraints);

        txtAddLine2.setFont(resourceMap.getFont("txtTelNo.font")); // NOI18N
        txtAddLine2.setText(resourceMap.getString("txtAddLine2.text")); // NOI18N
        txtAddLine2.setName("txtAddLine2"); // NOI18N
        txtAddLine2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtAddLine2FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtAddLine2FocusLost(evt);
            }
        });
        txtAddLine2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtAddLine2KeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(txtAddLine2, gridBagConstraints);

        jLabel3.setFont(resourceMap.getFont("lblName.font")); // NOI18N
        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel2.add(jLabel3, gridBagConstraints);

        txtCity.setFont(resourceMap.getFont("txtTelNo.font")); // NOI18N
        txtCity.setText(resourceMap.getString("txtCity.text")); // NOI18N
        txtCity.setName("txtCity"); // NOI18N
        txtCity.setPreferredSize(new java.awt.Dimension(150, 20));
        txtCity.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCityFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtCityFocusLost(evt);
            }
        });
        txtCity.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCityKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(txtCity, gridBagConstraints);

        txtProvince.setFont(resourceMap.getFont("txtTelNo.font")); // NOI18N
        txtProvince.setText(resourceMap.getString("txtProvince.text")); // NOI18N
        txtProvince.setName("txtProvince"); // NOI18N
        txtProvince.setPreferredSize(new java.awt.Dimension(150, 20));
        txtProvince.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtProvinceFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtProvinceFocusLost(evt);
            }
        });
        txtProvince.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtProvinceKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(txtProvince, gridBagConstraints);

        lblContactPerson.setFont(resourceMap.getFont("lblName.font")); // NOI18N
        lblContactPerson.setText(resourceMap.getString("lblContactPerson.text")); // NOI18N
        lblContactPerson.setName("lblContactPerson"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel2.add(lblContactPerson, gridBagConstraints);

        txtContactPerson.setFont(resourceMap.getFont("txtTelNo.font")); // NOI18N
        txtContactPerson.setText(resourceMap.getString("txtContactPerson.text")); // NOI18N
        txtContactPerson.setName("txtContactPerson"); // NOI18N
        txtContactPerson.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtContactPersonFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtContactPersonFocusLost(evt);
            }
        });
        txtContactPerson.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtContactPersonKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(txtContactPerson, gridBagConstraints);

        btnSave.setFont(resourceMap.getFont("btnSave.font")); // NOI18N
        btnSave.setIcon(resourceMap.getIcon("btnSave.icon")); // NOI18N
        btnSave.setText(resourceMap.getString("btnSave.text")); // NOI18N
        btnSave.setFocusable(false);
        btnSave.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnSave.setMinimumSize(new java.awt.Dimension(40, 39));
        btnSave.setName("btnSave"); // NOI18N
        btnSave.setPreferredSize(new java.awt.Dimension(40, 39));

        btnClose.setFont(resourceMap.getFont("btnSave.font")); // NOI18N
        btnClose.setIcon(resourceMap.getIcon("btnClose.icon")); // NOI18N
        btnClose.setText(resourceMap.getString("btnClose.text")); // NOI18N
        btnClose.setFocusable(false);
        btnClose.setName("btnClose"); // NOI18N
        btnClose.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, resourceMap.getString("jPanel4.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, resourceMap.getFont("jPanel4.border.titleFont"))); // NOI18N
        jPanel4.setName("jPanel4"); // NOI18N

        lblOID.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        lblOID.setText(resourceMap.getString("lblOID.text")); // NOI18N
        lblOID.setName("lblOID"); // NOI18N

        txtOID.setEditable(false);
        txtOID.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        txtOID.setName("txtOID"); // NOI18N

        lblGDS.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        lblGDS.setText(resourceMap.getString("lblGDS.text")); // NOI18N
        lblGDS.setName("lblGDS"); // NOI18N

        cmbGDS.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        cmbGDS.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Select", "Amadeus", "Galileo", "Worldspan", "Sabre" }));
        cmbGDS.setName("cmbGDS"); // NOI18N

        lblMessage.setForeground(resourceMap.getColor("lblMessage.foreground")); // NOI18N
        lblMessage.setName("lblMessage"); // NOI18N

        rdoMain.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        rdoMain.setText(resourceMap.getString("rdoMain.text")); // NOI18N
        rdoMain.setEnabled(false);
        rdoMain.setName("rdoMain"); // NOI18N

        rdoSubAgent.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        rdoSubAgent.setText(resourceMap.getString("rdoSubAgent.text")); // NOI18N
        rdoSubAgent.setEnabled(false);
        rdoSubAgent.setName("rdoSubAgent"); // NOI18N

        jLabel5.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblMessage)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(lblGDS)
                        .addGap(6, 6, 6)
                        .addComponent(cmbGDS, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblOID)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtOID, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addGap(35, 35, 35)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(rdoMain)
                    .addGap(4, 4, 4)
                    .addComponent(rdoSubAgent)
                    .addContainerGap(100, Short.MAX_VALUE)))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblMessage)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblOID)
                        .addComponent(txtOID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblGDS)
                        .addComponent(cmbGDS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(39, Short.MAX_VALUE))
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addGap(29, 29, 29)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(rdoMain)
                        .addComponent(rdoSubAgent))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, resourceMap.getString("jPanel1.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, resourceMap.getFont("jPanel1.border.titleFont"))); // NOI18N
        jPanel1.setName("jPanel1"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tblOID.setFont(resourceMap.getFont("tblOID.font")); // NOI18N
        tblOID.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Amadeus", "Galileo", "Worldspan", "Sabre"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblOID.setName("tblOID"); // NOI18N
        jScrollPane1.setViewportView(tblOID);

        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N
        jToolBar1.add(jButton1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 453, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 453, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jLabel4.setFont(resourceMap.getFont("jLabel4.font")); // NOI18N
        jLabel4.setForeground(resourceMap.getColor("jLabel4.foreground")); // NOI18N
        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        lblInfo.setForeground(resourceMap.getColor("lblInfo.foreground")); // NOI18N
        lblInfo.setText(resourceMap.getString("lblInfo.text")); // NOI18N
        lblInfo.setName("lblInfo"); // NOI18N

        jSeparator1.setName("jSeparator1"); // NOI18N

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, resourceMap.getString("jPanel3.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, resourceMap.getFont("jPanel3.border.titleFont"))); // NOI18N
        jPanel3.setName("jPanel3"); // NOI18N

        jLabel6.setFont(resourceMap.getFont("jLabel6.font")); // NOI18N
        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        txtCLimit.setFont(resourceMap.getFont("txtCLimit.font")); // NOI18N
        txtCLimit.setText(resourceMap.getString("txtCLimit.text")); // NOI18N
        txtCLimit.setName("txtCLimit"); // NOI18N

        chkAllowInvoicing.setFont(resourceMap.getFont("chkAllowInvoicing.font")); // NOI18N
        chkAllowInvoicing.setText(resourceMap.getString("chkAllowInvoicing.text")); // NOI18N
        chkAllowInvoicing.setName("chkAllowInvoicing"); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtCLimit, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkAllowInvoicing))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtCLimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkAllowInvoicing))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 368, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(2, 2, 2)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(btnClose))
                                .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGap(2, 2, 2))
                        .addComponent(jSeparator1))
                    .addComponent(lblInfo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 473, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        dispose();
}//GEN-LAST:event_btnCloseActionPerformed

    private void txtNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNameActionPerformed
}//GEN-LAST:event_txtNameActionPerformed

    private void txtNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNameFocusGained
        txtName.setBackground(Color.YELLOW);
}//GEN-LAST:event_txtNameFocusGained

    private void txtNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNameFocusLost
        txtName.setBackground(Color.WHITE);
}//GEN-LAST:event_txtNameFocusLost

    private void txtNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNameKeyPressed
        int key = evt.getKeyCode();
        if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_DOWN) {
            txtContactPerson.requestFocus();
        } else if (key == KeyEvent.VK_UP) {
            txtRemark.requestFocus();
        }
}//GEN-LAST:event_txtNameKeyPressed

    private void txtTelNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTelNoFocusGained
        txtTelNo.setBackground(Color.YELLOW);
}//GEN-LAST:event_txtTelNoFocusGained

    private void txtTelNoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTelNoFocusLost
        txtTelNo.setBackground(Color.WHITE);
}//GEN-LAST:event_txtTelNoFocusLost

    private void txtTelNoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTelNoKeyPressed
        int key = evt.getKeyCode();
        if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_DOWN) {
            txtTelNo1.requestFocus();
        } else if (key == KeyEvent.VK_UP) {
            txtPostCode.requestFocus();
        }
}//GEN-LAST:event_txtTelNoKeyPressed

    private void txtTelNo1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTelNo1FocusGained
        txtTelNo1.setBackground(Color.YELLOW);
}//GEN-LAST:event_txtTelNo1FocusGained

    private void txtTelNo1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTelNo1FocusLost
        txtTelNo1.setBackground(Color.WHITE);
}//GEN-LAST:event_txtTelNo1FocusLost

    private void txtTelNo1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTelNo1KeyPressed
        int key = evt.getKeyCode();
        if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_DOWN) {
            txtFax.requestFocus();
        } else if (key == KeyEvent.VK_UP) {
            txtTelNo.requestFocus();
        }
}//GEN-LAST:event_txtTelNo1KeyPressed

    private void txtFaxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFaxFocusGained
        txtFax.setBackground(Color.YELLOW);
}//GEN-LAST:event_txtFaxFocusGained

    private void txtFaxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFaxFocusLost
        txtFax.setBackground(Color.WHITE);
}//GEN-LAST:event_txtFaxFocusLost

    private void txtFaxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFaxKeyPressed
        int key = evt.getKeyCode();
        if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_DOWN) {
            txtMobile.requestFocus();
        } else if (key == KeyEvent.VK_UP) {
            txtTelNo1.requestFocus();
        }
}//GEN-LAST:event_txtFaxKeyPressed

    private void txtMobileFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMobileFocusGained
        txtMobile.setBackground(Color.YELLOW);
}//GEN-LAST:event_txtMobileFocusGained

    private void txtMobileFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMobileFocusLost
        txtMobile.setBackground(Color.WHITE);
}//GEN-LAST:event_txtMobileFocusLost

    private void txtMobileKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtMobileKeyPressed
        int key = evt.getKeyCode();
        if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_DOWN) {
            txtEmail.requestFocus();
        } else if (key == KeyEvent.VK_UP) {
            txtFax.requestFocus();
        }
}//GEN-LAST:event_txtMobileKeyPressed

    private void txtEmailFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtEmailFocusGained
        txtEmail.setBackground(Color.YELLOW);
}//GEN-LAST:event_txtEmailFocusGained

    private void txtEmailFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtEmailFocusLost
        txtEmail.setBackground(Color.WHITE);
}//GEN-LAST:event_txtEmailFocusLost

    private void txtEmailKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtEmailKeyPressed
        int key = evt.getKeyCode();
        if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_DOWN) {
            txtWeb.requestFocus();
        } else if (key == KeyEvent.VK_UP) {
            txtMobile.requestFocus();
        }
}//GEN-LAST:event_txtEmailKeyPressed

    private void txtWebFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtWebFocusGained
        txtWeb.setBackground(Color.YELLOW);
}//GEN-LAST:event_txtWebFocusGained

    private void txtWebFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtWebFocusLost
        txtWeb.setBackground(Color.WHITE);
}//GEN-LAST:event_txtWebFocusLost

    private void txtWebKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtWebKeyPressed
        int key = evt.getKeyCode();
        if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_DOWN) {
            txtIATANo.requestFocus();
        } else if (key == KeyEvent.VK_UP) {
            txtEmail.requestFocus();
        }
}//GEN-LAST:event_txtWebKeyPressed

    private void txtIATANoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtIATANoFocusGained
        txtIATANo.setBackground(Color.YELLOW);
}//GEN-LAST:event_txtIATANoFocusGained

    private void txtIATANoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtIATANoFocusLost
        txtIATANo.setBackground(Color.WHITE);
}//GEN-LAST:event_txtIATANoFocusLost

    private void txtIATANoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtIATANoKeyPressed
        int key = evt.getKeyCode();
        if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_DOWN) {
            txtATOLNo.requestFocus();
        } else if (key == KeyEvent.VK_UP) {
            txtWeb.requestFocus();
        }
}//GEN-LAST:event_txtIATANoKeyPressed

    private void txtATOLNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtATOLNoFocusGained
        txtATOLNo.setBackground(Color.YELLOW);
}//GEN-LAST:event_txtATOLNoFocusGained

    private void txtATOLNoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtATOLNoFocusLost
        txtATOLNo.setBackground(Color.WHITE);
}//GEN-LAST:event_txtATOLNoFocusLost

    private void txtATOLNoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtATOLNoKeyPressed
        int key = evt.getKeyCode();
        if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_DOWN) {
            txtRemark.requestFocus();
        } else if (key == KeyEvent.VK_UP) {
            txtIATANo.requestFocus();
        }
}//GEN-LAST:event_txtATOLNoKeyPressed

    private void txtRemarkFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRemarkFocusGained
        txtRemark.setBackground(Color.YELLOW);
}//GEN-LAST:event_txtRemarkFocusGained

    private void txtRemarkFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRemarkFocusLost
        txtRemark.setBackground(Color.WHITE);
}//GEN-LAST:event_txtRemarkFocusLost

    private void txtRemarkKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtRemarkKeyPressed
        int key = evt.getKeyCode();
        if (key == KeyEvent.VK_DOWN) {
            txtName.requestFocus();
        } else if (key == KeyEvent.VK_UP) {
            txtATOLNo.requestFocus();
        }
}//GEN-LAST:event_txtRemarkKeyPressed

    private void txtPostCodeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPostCodeFocusGained
        txtPostCode.setBackground(Color.YELLOW);
}//GEN-LAST:event_txtPostCodeFocusGained

    private void txtPostCodeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPostCodeFocusLost
        txtPostCode.setBackground(Color.WHITE);
}//GEN-LAST:event_txtPostCodeFocusLost

    private void txtPostCodeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPostCodeKeyPressed
         int key = evt.getKeyCode();
        if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_DOWN) {
            txtTelNo.requestFocus();
        } else if (key == KeyEvent.VK_UP) {
            txtProvince.requestFocus();
        }
}//GEN-LAST:event_txtPostCodeKeyPressed

    private void txtContactPersonFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtContactPersonFocusGained
        txtContactPerson.setBackground(Color.YELLOW);
    }//GEN-LAST:event_txtContactPersonFocusGained

    private void txtContactPersonFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtContactPersonFocusLost
        txtContactPerson.setBackground(Color.WHITE);
    }//GEN-LAST:event_txtContactPersonFocusLost

    private void txtAddLine1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAddLine1FocusGained
        txtAddLine1.setBackground(Color.YELLOW);
    }//GEN-LAST:event_txtAddLine1FocusGained

    private void txtAddLine1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAddLine1FocusLost
          txtAddLine1.setBackground(Color.WHITE);
    }//GEN-LAST:event_txtAddLine1FocusLost

    private void txtAddLine2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAddLine2FocusGained
        txtAddLine2.setBackground(Color.YELLOW);
    }//GEN-LAST:event_txtAddLine2FocusGained

    private void txtAddLine2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAddLine2FocusLost
         txtAddLine2.setBackground(Color.WHITE);
    }//GEN-LAST:event_txtAddLine2FocusLost

    private void txtCityFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCityFocusGained
        txtCity.setBackground(Color.YELLOW);
    }//GEN-LAST:event_txtCityFocusGained

    private void txtCityFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCityFocusLost
        txtCity.setBackground(Color.WHITE);
    }//GEN-LAST:event_txtCityFocusLost

    private void txtProvinceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtProvinceFocusGained
        txtProvince.setBackground(Color.YELLOW);
    }//GEN-LAST:event_txtProvinceFocusGained

    private void txtProvinceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtProvinceFocusLost
       txtProvince.setBackground(Color.WHITE);
    }//GEN-LAST:event_txtProvinceFocusLost

    private void txtContactPersonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtContactPersonKeyPressed
       int key = evt.getKeyCode();
        if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_DOWN) {
            txtAddLine1.requestFocus();
        } else if (key == KeyEvent.VK_UP) {
            txtName.requestFocus();
        }
    }//GEN-LAST:event_txtContactPersonKeyPressed

    private void txtAddLine1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAddLine1KeyPressed
        int key = evt.getKeyCode();
        if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_DOWN) {
            txtAddLine2.requestFocus();
        } else if (key == KeyEvent.VK_UP) {
            txtContactPerson.requestFocus();
        }
    }//GEN-LAST:event_txtAddLine1KeyPressed

    private void txtAddLine2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAddLine2KeyPressed
        int key = evt.getKeyCode();
        if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_DOWN) {
            txtCity.requestFocus();
        } else if (key == KeyEvent.VK_UP) {
            txtAddLine1.requestFocus();
        }
    }//GEN-LAST:event_txtAddLine2KeyPressed

    private void txtCityKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCityKeyPressed
        int key = evt.getKeyCode();
        if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_DOWN) {
            txtProvince.requestFocus();
        } else if (key == KeyEvent.VK_UP) {
            txtAddLine2.requestFocus();
        }
    }//GEN-LAST:event_txtCityKeyPressed

    private void txtProvinceKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtProvinceKeyPressed
       int key = evt.getKeyCode();
        if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_DOWN) {
            txtPostCode.requestFocus();
        } else if (key == KeyEvent.VK_UP) {
            txtCity.requestFocus();
        }
    }//GEN-LAST:event_txtProvinceKeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox chkAllowInvoicing;
    private javax.swing.JComboBox cmbGDS;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblATOL;
    private javax.swing.JLabel lblAddress;
    private javax.swing.JLabel lblContactPerson;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblFax;
    private javax.swing.JLabel lblGDS;
    private javax.swing.JLabel lblIATA;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JLabel lblMessage;
    private javax.swing.JLabel lblMobile;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblOID;
    private javax.swing.JLabel lblPostCode;
    private javax.swing.JLabel lblRemark;
    private javax.swing.JLabel lblTelNo1;
    private javax.swing.JLabel lblTelNo2;
    private javax.swing.JLabel lblWeb;
    private javax.swing.JRadioButton rdoMain;
    private javax.swing.JRadioButton rdoSubAgent;
    private javax.swing.JTable tblOID;
    private javax.swing.JTextField txtATOLNo;
    private javax.swing.JTextField txtAddLine1;
    private javax.swing.JTextField txtAddLine2;
    private javax.swing.JTextField txtCLimit;
    private javax.swing.JTextField txtCity;
    private javax.swing.JTextField txtContactPerson;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtFax;
    private javax.swing.JTextField txtIATANo;
    private javax.swing.JTextField txtMobile;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtOID;
    private javax.swing.JTextField txtPostCode;
    private javax.swing.JTextField txtProvince;
    private javax.swing.JTextArea txtRemark;
    private javax.swing.JTextField txtTelNo;
    private javax.swing.JTextField txtTelNo1;
    private javax.swing.JTextField txtWeb;
    // End of variables declaration//GEN-END:variables
}
