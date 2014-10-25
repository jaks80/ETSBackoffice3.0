/*
 * FrameAgent.java
 */
package etsbackoffice.gui;

import etsbackoffice.businesslogic.AuthenticationBo;
import etsbackoffice.domain.Agent;
import etsbackoffice.businesslogic.DocumentSizeFilter;
import etsbackoffice.businesslogic.DocumentSizeFilterLowerCase;
import etsbackoffice.domain.Vendor;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JDialog;
import javax.swing.text.AbstractDocument;
import org.jdesktop.application.Action;

public class DlgVendor extends JDialog implements ActionListener {

    private boolean save;

    public DlgVendor(java.awt.Frame parent) {
        super(parent, "New vendor", true);
        initComponents();
        btnSave.addActionListener(this);

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
        doc = (AbstractDocument) txtRegNo.getDocument();
        doc.setDocumentFilter(new DocumentSizeFilter(10));
        doc = (AbstractDocument) txtRemark.getDocument();
        doc.setDocumentFilter(new DocumentSizeFilter(400));

        txtName.requestFocusInWindow();
    }

    @Action
    public void closeAboutBox() {
        dispose();
    }

    public boolean showAgentDialog(Vendor vendor) {
        if (vendor != null) {
            txtName.setText(vendor.getName());
            txtAddLine1.setText(vendor.getAddLine1());
            txtAddLine2.setText(vendor.getAddLine2());
            txtCity.setText(vendor.getCity());
            txtProvince.setText(vendor.getProvince());
            txtPostCode.setText(vendor.getPostCode());
            cmbCountry.setSelectedItem(vendor.getCountry());
            txtTelNo.setText(vendor.getTelNo());
            txtMobile.setText(vendor.getMobile());
            txtEmail.setText(vendor.getEmail());
            txtFax.setText(vendor.getFax());
            txtWeb.setText(vendor.getWeb());
            txtRegNo.setText(vendor.getCompanyRegNo());
        }
        save = false;
        setLocationRelativeTo(this);
        setVisible(true);

        if (save) {
            vendor.setName(txtName.getText().trim());
            vendor.setWeb(txtWeb.getText().trim());
            vendor.setAddLine1(txtAddLine1.getText().trim());
            vendor.setAddLine2(txtAddLine2.getText().trim());
            vendor.setCity(txtCity.getText().trim());
            vendor.setProvince(txtProvince.getText().trim());
            vendor.setPostCode(txtPostCode.getText().trim());
            vendor.setCountry(cmbCountry.getSelectedItem().toString());
            vendor.setTelNo(txtTelNo.getText().trim());
            vendor.setFax(txtFax.getText().trim());
            vendor.setMobile(txtMobile.getText().trim());
            vendor.setEmail(txtEmail.getText().trim());
            vendor.setCompanyRegNo(txtRegNo.getText().trim());
            vendor.setRemark(txtRemark.getText().trim());
            vendor.setCreatedBy(AuthenticationBo.getLoggedOnUser());
            vendor.setCreatedOn(new java.util.Date());
        }
        return save;
    }

    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();
        if (source == btnSave) {
            if (txtName.getText().isEmpty() || txtAddLine1.getText().isEmpty() || txtPostCode.getText().isEmpty() || cmbCountry.getSelectedIndex()==0) {
                lblInfo.setText("Enter mendatory fields...");
            } else {
                save = true;
                setVisible(false);
            }
        } else if (source == btnClose) {
            setVisible(false);
        }
    }

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
        txtRegNo = new javax.swing.JTextField();
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
        cmbCountry = new javax.swing.JComboBox();
        btnSave = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        lblInfo = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getResourceMap(DlgVendor.class);
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
        gridBagConstraints.gridy = 8;
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
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(txtTelNo, gridBagConstraints);

        lblTelNo2.setFont(resourceMap.getFont("lblName.font")); // NOI18N
        lblTelNo2.setText(resourceMap.getString("lblTelNo2.text")); // NOI18N
        lblTelNo2.setName("lblTelNo2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
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
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(txtTelNo1, gridBagConstraints);

        lblFax.setFont(resourceMap.getFont("lblName.font")); // NOI18N
        lblFax.setText(resourceMap.getString("lblFax.text")); // NOI18N
        lblFax.setName("lblFax"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel2.add(lblFax, gridBagConstraints);

        lblMobile.setFont(resourceMap.getFont("lblName.font")); // NOI18N
        lblMobile.setText(resourceMap.getString("lblMobile.text")); // NOI18N
        lblMobile.setName("lblMobile"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel2.add(lblMobile, gridBagConstraints);

        lblWeb.setFont(resourceMap.getFont("lblName.font")); // NOI18N
        lblWeb.setText(resourceMap.getString("lblWeb.text")); // NOI18N
        lblWeb.setName("lblWeb"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel2.add(lblWeb, gridBagConstraints);

        lblIATA.setFont(resourceMap.getFont("lblName.font")); // NOI18N
        lblIATA.setText(resourceMap.getString("lblIATA.text")); // NOI18N
        lblIATA.setName("lblIATA"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel2.add(lblIATA, gridBagConstraints);

        lblATOL.setFont(resourceMap.getFont("lblName.font")); // NOI18N
        lblATOL.setText(resourceMap.getString("lblATOL.text")); // NOI18N
        lblATOL.setName("lblATOL"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
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
        gridBagConstraints.gridy = 12;
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
        gridBagConstraints.gridy = 10;
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
        gridBagConstraints.gridy = 11;
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
        gridBagConstraints.gridy = 12;
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
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(txtWeb, gridBagConstraints);

        txtRegNo.setFont(resourceMap.getFont("txtTelNo.font")); // NOI18N
        txtRegNo.setName("txtRegNo"); // NOI18N
        txtRegNo.setPreferredSize(new java.awt.Dimension(150, 20));
        txtRegNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtRegNoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtRegNoFocusLost(evt);
            }
        });
        txtRegNo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtRegNoKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(txtRegNo, gridBagConstraints);

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

        cmbCountry.setFont(resourceMap.getFont("cmbCountry.font")); // NOI18N
        cmbCountry.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Select", "Abkhazia", "Afghanistan", "Akrotiri and Dhekelia", "Aland", "Albania", "Algeria", "American Samoa", "Andorra", "Angola", "Anguilla", "Antigua and Barbuda", "Argentina", "Armenia", "Aruba", "Ascension Island", "Australia", "Austria", "Azerbaijan", "Bahamas, The", "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia", "Bosnia and Herzegovina", "Botswana", "Brazil", "Brunei", "Bulgaria", "Burkina Faso", "Burundi", "Cambodia", "Cameroon", "Canada", "Cape Verde", "Cayman Islands", "Central Africa Republic", "Chad", "Chile", "China", "Christmas Island", "Cocos (Keeling) Islands", "Colombia", "Comoros", "Congo", "Cook Islands", "Costa Rica", "Cote d'lvoire", "Croatia", "Cuba", "Cyprus", "Czech Republic", "Denmark", "Djibouti", "Dominica", "Dominican Republic", "East Timor Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia", "Ethiopia", "Falkland Islands", "Faroe Islands", "Fiji", "Finland", "France", "French Polynesia", "Gabon", "Cambia, The", "Georgia", "Germany", "Ghana", "Gibraltar", "Greece", "Greenland", "Grenada", "Guam", "Guatemala", "Guemsey", "Guinea", "Guinea-Bissau", "Guyana", "Haiti", "Honduras", "Hong Kong", "Hungary", "Iceland", "India", "Indonesia", "Iran", "Iraq", "Ireland", "Isle of Man", "Israel", "Italy", "Jamaica", "Japan", "Jersey", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Korea, N", "Korea, S", "Kosovo", "Kuwait", "Kyrgyzstan", "Laos", "Latvia", "Lebanon", "Lesotho", "Liberia", "Libya", "Liechtenstein", "Lithuania", "Luxembourg", "Macao", "Macedonia", "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands", "Mauritania", "Mauritius", "Mayotte", "Mexico", "Micronesia", "Moldova", "Monaco", "Mongolia", "Montenegro", "Montserrat", "Morocco", "Mozambique", "Myanmar", "Nagorno-Karabakh", "Namibia", "Nauru", "Nepal", "Netherlands", "Netherlands Antilles", "New Caledonia", "New Zealand", "Nicaragua", "Niger", "Nigeria", "Niue", "Norfolk Island", "Northern Cyprus", "Northern Mariana Islands", "Norway", "Oman", "Pakistan", "Palau", "Palestine", "Panama", "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Pitcaim Islands", "Poland", "Portugal", "Puerto Rico", "Qatar", "Romania", "Russia", "Rwanda", "Sahrawi Arab Democratic Republic", "Saint-Barthelemy", "Saint Helena", "Saint Kitts and Nevis", "Saint Lucia", "Saint Martin", "Saint Pierre and Miquelon", "Saint Vincent and Grenadines", "Samos", "San Marino", "Sao Tome and Principe", "Saudi Arabia", "Senegal", "Serbia", "Seychelles", "Sierra Leone", "Singapore", "Slovakia", "Slovenia", "Solomon Islands", "Somalia", "Somaliland", "South Africa", "South Ossetia", "Spain", "Sri Lanka", "Sudan", "Suriname", "Svalbard", "Swaziland", "Sweden", "Switzerland", "Syria", "Tajikistan", "Tanzania", "Thailand", "Togo", "Tokelau", "Tonga", "Transnistria", "Trinidad and Tobago", "Tristan da Cunha", "Tunisia", "Turkey", "Turkmenistan", "Turks and Caicos Islands", "Tuvalu", "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom", "United States", "Uruguay", "Uzbekistan", "Vanuatu", "Vatican City", "Venezuela", "Vietnam", "Virgin Islands, British", "Virgin Islands, U.S.", "Wallis and Futuna", "Yemen", "Zambia", "Zimbabwe" }));
        cmbCountry.setName("cmbCountry"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(cmbCountry, gridBagConstraints);

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

        jLabel4.setFont(resourceMap.getFont("jLabel4.font")); // NOI18N
        jLabel4.setForeground(resourceMap.getColor("jLabel4.foreground")); // NOI18N
        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        lblInfo.setForeground(resourceMap.getColor("lblInfo.foreground")); // NOI18N
        lblInfo.setText(resourceMap.getString("lblInfo.text")); // NOI18N
        lblInfo.setName("lblInfo"); // NOI18N

        jSeparator1.setName("jSeparator1"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 368, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnClose))
                    .addComponent(lblInfo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSeparator1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(lblInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(86, 86, 86)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
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
            txtRegNo.requestFocus();
        } else if (key == KeyEvent.VK_UP) {
            txtEmail.requestFocus();
        }
}//GEN-LAST:event_txtWebKeyPressed

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
            txtRegNo.requestFocus();
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

    private void txtRegNoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtRegNoKeyPressed
        int key = evt.getKeyCode();
        if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_DOWN) {
            txtRemark.requestFocus();
        } else if (key == KeyEvent.VK_UP) {
            txtWeb.requestFocus();
        }
}//GEN-LAST:event_txtRegNoKeyPressed

    private void txtRegNoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRegNoFocusLost
        txtRegNo.setBackground(Color.WHITE);
}//GEN-LAST:event_txtRegNoFocusLost

    private void txtRegNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRegNoFocusGained
        txtRegNo.setBackground(Color.YELLOW);
}//GEN-LAST:event_txtRegNoFocusGained
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox cmbCountry;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblATOL;
    private javax.swing.JLabel lblAddress;
    private javax.swing.JLabel lblContactPerson;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblFax;
    private javax.swing.JLabel lblIATA;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JLabel lblMobile;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblPostCode;
    private javax.swing.JLabel lblRemark;
    private javax.swing.JLabel lblTelNo1;
    private javax.swing.JLabel lblTelNo2;
    private javax.swing.JLabel lblWeb;
    private javax.swing.JTextField txtAddLine1;
    private javax.swing.JTextField txtAddLine2;
    private javax.swing.JTextField txtCity;
    private javax.swing.JTextField txtContactPerson;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtFax;
    private javax.swing.JTextField txtMobile;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtPostCode;
    private javax.swing.JTextField txtProvince;
    private javax.swing.JTextField txtRegNo;
    private javax.swing.JTextArea txtRemark;
    private javax.swing.JTextField txtTelNo;
    private javax.swing.JTextField txtTelNo1;
    private javax.swing.JTextField txtWeb;
    // End of variables declaration//GEN-END:variables
}
