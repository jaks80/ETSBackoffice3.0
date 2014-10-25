/*
 * FrameCustomer.java
 *
 * Created on 09-Nov-2010, 00:41:34
 */
package etsbackoffice.gui;

import etsbackoffice.businesslogic.AuthenticationBo;
import etsbackoffice.businesslogic.DocumentSizeFilter;
import etsbackoffice.domain.Customer;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.text.AbstractDocument;
import org.jdesktop.application.Action;

/**
 *
 * @author Yusuf
 */
public class FrameCustomer extends javax.swing.JDialog implements ActionListener {

    private boolean save;

    public FrameCustomer(java.awt.Frame parent) {
        super(parent, "New Customer", true);
        initComponents();
        btnSave.addActionListener(this);
        //getRootPane().setDefaultButton(btnClose);
        
        AbstractDocument doc;
        doc = (AbstractDocument) txtSurName.getDocument();
        doc.setDocumentFilter(new DocumentSizeFilter(45));
        doc = (AbstractDocument) txtForeName.getDocument();
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
        doc = (AbstractDocument) txtRemark.getDocument();
        doc.setDocumentFilter(new DocumentSizeFilter(400));

    }

    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();
        if (source == btnSave) {
            if (txtSurName.getText().isEmpty() ||txtForeName.getText().isEmpty()
                    || txtAddLine1.getText().isEmpty() || txtPostCode.getText().isEmpty()
                    || txtTelNo.getText().isEmpty()) {
                lblInfo.setText("Enter mendatory fields...");
            } else {
                save = true;
                setVisible(false);
            }
        } else if (source == btnClose) {
            setVisible(false);
        }
    }

    @Action
    public void closeAboutBox() {
        dispose();
    }

    public boolean showCustomerDialog(Customer customer) {
        if (customer != null) {
            txtSurName.setText(customer.getSurName());
            txtForeName.setText(customer.getForeName());
            txtContactPerson.setText(customer.getContactPerson());
            txtAddLine1.setText(customer.getAddLine1());
            txtAddLine2.setText(customer.getAddLine2());
            txtCity.setText(customer.getCity());
            txtProvince.setText(customer.getProvince());
            txtPostCode.setText(customer.getPostCode());
            txtTelNo.setText(customer.getTelNo());
            txtMobile.setText(customer.getMobile());
            txtEmail.setText(customer.getEmail());
            txtFax.setText(customer.getFax());

            if(customer.getForeName()!=null){
            txtAddLine1.requestFocusInWindow();
            }else{
            txtSurName.requestFocusInWindow();
            }
        }
        save = false;
        setLocationRelativeTo(this);
        setVisible(true);

        if (save) {
            customer.setSurName(txtSurName.getText().trim());
            customer.setForeName(txtForeName.getText().trim());
            customer.setContactPerson(txtContactPerson.getText().trim());
            customer.setAddLine1(txtAddLine1.getText().trim());
            customer.setAddLine2(txtAddLine2.getText().trim());
            customer.setCity(txtCity.getText().trim());
            customer.setProvince(txtProvince.getText().trim());
            customer.setPostCode(txtPostCode.getText().trim());
            customer.setTelNo(txtTelNo.getText().trim());
            customer.setTelNo1(txtTelNo1.getText().trim());
            customer.setFax(txtFax.getText().trim());
            customer.setMobile(txtMobile.getText().trim());
            customer.setEmail(txtEmail.getText().trim());
            customer.setRemark(txtRemark.getText().trim());

            if (customer.getContactableId() == 0) {
                customer.setCreatedOn(new java.util.Date());                
                customer.setCreatedBy(AuthenticationBo.getLoggedOnUser());
            } else {                
                customer.setLastModifiedBy(AuthenticationBo.getLoggedOnUser());
                customer.setLastModified(new java.util.Date());
            }
        }
        return save;
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

        jPanel2 = new javax.swing.JPanel();
        lblName = new javax.swing.JLabel();
        txtSurName = new javax.swing.JTextField();
        lblAddress = new javax.swing.JLabel();
        lblTelNo1 = new javax.swing.JLabel();
        txtTelNo = new javax.swing.JTextField();
        lblTelNo2 = new javax.swing.JLabel();
        txtTelNo1 = new javax.swing.JTextField();
        lblFax = new javax.swing.JLabel();
        lblMobile = new javax.swing.JLabel();
        lblRemark = new javax.swing.JLabel();
        lblEmail = new javax.swing.JLabel();
        txtFax = new javax.swing.JTextField();
        txtMobile = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
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
        jLabel4 = new javax.swing.JLabel();
        txtForeName = new javax.swing.JTextField();
        lblRefPerson = new javax.swing.JLabel();
        txtContactPerson = new javax.swing.JTextField();
        btnSave = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        btnClose = new javax.swing.JButton();
        lblInfo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new java.awt.GridBagLayout());

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getResourceMap(FrameCustomer.class);
        lblName.setText(resourceMap.getString("lblName.text")); // NOI18N
        lblName.setName("lblName"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 0);
        jPanel2.add(lblName, gridBagConstraints);

        txtSurName.setName("txtSurName"); // NOI18N
        txtSurName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSurNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtSurNameFocusLost(evt);
            }
        });
        txtSurName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSurNameKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(txtSurName, gridBagConstraints);

        lblAddress.setText(resourceMap.getString("lblAddress.text")); // NOI18N
        lblAddress.setName("lblAddress"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 0);
        jPanel2.add(lblAddress, gridBagConstraints);

        lblTelNo1.setText(resourceMap.getString("lblTelNo1.text")); // NOI18N
        lblTelNo1.setName("lblTelNo1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 0);
        jPanel2.add(lblTelNo1, gridBagConstraints);

        txtTelNo.setName("txtTelNo"); // NOI18N
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
        gridBagConstraints.ipadx = 150;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(txtTelNo, gridBagConstraints);

        lblTelNo2.setText(resourceMap.getString("lblTelNo2.text")); // NOI18N
        lblTelNo2.setName("lblTelNo2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 0);
        jPanel2.add(lblTelNo2, gridBagConstraints);

        txtTelNo1.setName("txtTelNo1"); // NOI18N
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
        gridBagConstraints.ipadx = 150;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(txtTelNo1, gridBagConstraints);

        lblFax.setText(resourceMap.getString("lblFax.text")); // NOI18N
        lblFax.setName("lblFax"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 0);
        jPanel2.add(lblFax, gridBagConstraints);

        lblMobile.setText(resourceMap.getString("lblMobile.text")); // NOI18N
        lblMobile.setName("lblMobile"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 0);
        jPanel2.add(lblMobile, gridBagConstraints);

        lblRemark.setText(resourceMap.getString("lblRemark.text")); // NOI18N
        lblRemark.setName("lblRemark"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 0);
        jPanel2.add(lblRemark, gridBagConstraints);

        lblEmail.setText(resourceMap.getString("lblEmail.text")); // NOI18N
        lblEmail.setName("lblEmail"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 0);
        jPanel2.add(lblEmail, gridBagConstraints);

        txtFax.setName("txtFax"); // NOI18N
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
        gridBagConstraints.ipadx = 150;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(txtFax, gridBagConstraints);

        txtMobile.setName("txtMobile"); // NOI18N
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
        gridBagConstraints.gridy = 9;
        gridBagConstraints.ipadx = 150;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(txtMobile, gridBagConstraints);

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
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 250;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(txtEmail, gridBagConstraints);

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        txtRemark.setColumns(20);
        txtRemark.setFont(resourceMap.getFont("txtRemark.font")); // NOI18N
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
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 200;
        gridBagConstraints.ipady = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(jScrollPane2, gridBagConstraints);

        lblPostCode.setText(resourceMap.getString("lblPostCode.text")); // NOI18N
        lblPostCode.setName("lblPostCode"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 0);
        jPanel2.add(lblPostCode, gridBagConstraints);

        txtPostCode.setName("txtPostCode"); // NOI18N
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
        gridBagConstraints.ipadx = 150;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(txtPostCode, gridBagConstraints);

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 0);
        jPanel2.add(jLabel1, gridBagConstraints);

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 0);
        jPanel2.add(jLabel2, gridBagConstraints);

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
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(txtAddLine1, gridBagConstraints);

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
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(txtAddLine2, gridBagConstraints);

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 0);
        jPanel2.add(jLabel3, gridBagConstraints);

        txtCity.setName("txtCity"); // NOI18N
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
        gridBagConstraints.ipadx = 150;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(txtCity, gridBagConstraints);

        txtProvince.setName("txtProvince"); // NOI18N
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
        gridBagConstraints.ipadx = 150;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(txtProvince, gridBagConstraints);

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 0);
        jPanel2.add(jLabel4, gridBagConstraints);

        txtForeName.setText(resourceMap.getString("txtForeName.text")); // NOI18N
        txtForeName.setName("txtForeName"); // NOI18N
        txtForeName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtForeNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtForeNameFocusLost(evt);
            }
        });
        txtForeName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtForeNameKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(txtForeName, gridBagConstraints);

        lblRefPerson.setText(resourceMap.getString("lblRefPerson.text")); // NOI18N
        lblRefPerson.setName("lblRefPerson"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 0);
        jPanel2.add(lblRefPerson, gridBagConstraints);

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
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(txtContactPerson, gridBagConstraints);

        btnSave.setIcon(resourceMap.getIcon("btnSave.icon")); // NOI18N
        btnSave.setText(resourceMap.getString("btnSave.text")); // NOI18N
        btnSave.setFocusable(false);
        btnSave.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnSave.setMaximumSize(new java.awt.Dimension(75, 25));
        btnSave.setMinimumSize(new java.awt.Dimension(40, 39));
        btnSave.setName("btnSave"); // NOI18N
        btnSave.setPreferredSize(new java.awt.Dimension(40, 39));

        jLabel5.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        jLabel5.setForeground(resourceMap.getColor("jLabel5.foreground")); // NOI18N
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        btnClose.setIcon(resourceMap.getIcon("btnClose.icon")); // NOI18N
        btnClose.setText(resourceMap.getString("btnClose.text")); // NOI18N
        btnClose.setName("btnClose"); // NOI18N
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        lblInfo.setFont(resourceMap.getFont("lblInfo.font")); // NOI18N
        lblInfo.setForeground(resourceMap.getColor("lblInfo.foreground")); // NOI18N
        lblInfo.setText(resourceMap.getString("lblInfo.text")); // NOI18N
        lblInfo.setName("lblInfo"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(130, 130, 130))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblInfo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnClose))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 456, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblInfo)
                        .addContainerGap(36, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnClose))
                        .addContainerGap())))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtSurNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSurNameFocusGained
        txtSurName.setBackground(Color.YELLOW);
}//GEN-LAST:event_txtSurNameFocusGained

    private void txtSurNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSurNameFocusLost
        txtSurName.setBackground(Color.WHITE);
}//GEN-LAST:event_txtSurNameFocusLost

    private void txtSurNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSurNameKeyPressed
        int key = evt.getKeyCode();        
        if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_DOWN) {
            txtForeName.requestFocus();
        } else if (key == KeyEvent.VK_UP) {
            txtRemark.requestFocus();
        }
}//GEN-LAST:event_txtSurNameKeyPressed

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
            txtMobile.requestFocus();
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
            txtEmail.requestFocus();
        } else if (key == KeyEvent.VK_UP) {
            txtMobile.requestFocus();
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
            txtFax.requestFocus();
        } else if (key == KeyEvent.VK_UP) {
            txtTelNo1.requestFocus();
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
            txtContactPerson.requestFocus();
        } else if (key == KeyEvent.VK_UP) {
            txtFax.requestFocus();
        }
}//GEN-LAST:event_txtEmailKeyPressed

    private void txtRemarkFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRemarkFocusGained
        txtRemark.setBackground(Color.YELLOW);
}//GEN-LAST:event_txtRemarkFocusGained

    private void txtRemarkFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRemarkFocusLost
        txtRemark.setBackground(Color.WHITE);
}//GEN-LAST:event_txtRemarkFocusLost

    private void txtRemarkKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtRemarkKeyPressed
        int key = evt.getKeyCode();
        if (key == KeyEvent.VK_DOWN) {
            txtSurName.requestFocus();
        } else if (key == KeyEvent.VK_UP) {
            txtContactPerson.requestFocus();
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

    private void txtForeNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtForeNameFocusGained
        txtForeName.setBackground(Color.YELLOW);
    }//GEN-LAST:event_txtForeNameFocusGained

    private void txtForeNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtForeNameFocusLost
       txtForeName.setBackground(Color.WHITE);
    }//GEN-LAST:event_txtForeNameFocusLost

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

    private void txtContactPersonFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtContactPersonFocusGained
        txtContactPerson.setBackground(Color.YELLOW);
    }//GEN-LAST:event_txtContactPersonFocusGained

    private void txtContactPersonFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtContactPersonFocusLost
        txtContactPerson.setBackground(Color.WHITE);
    }//GEN-LAST:event_txtContactPersonFocusLost

    private void txtForeNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtForeNameKeyPressed
        int key = evt.getKeyCode();
        if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_DOWN) {
            txtAddLine1.requestFocus();
        } else if (key == KeyEvent.VK_UP) {
            txtSurName.requestFocus();
        }
    }//GEN-LAST:event_txtForeNameKeyPressed

    private void txtAddLine1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAddLine1KeyPressed
       int key = evt.getKeyCode();
        if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_DOWN) {
            txtAddLine2.requestFocus();
        } else if (key == KeyEvent.VK_UP) {
            txtForeName.requestFocus();
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

    private void txtContactPersonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtContactPersonKeyPressed
        int key = evt.getKeyCode();
        if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_DOWN) {
            txtRemark.requestFocus();
        } else if (key == KeyEvent.VK_UP) {
            txtEmail.requestFocus();
        }
    }//GEN-LAST:event_txtContactPersonKeyPressed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
       dispose();
    }//GEN-LAST:event_btnCloseActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblAddress;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblFax;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JLabel lblMobile;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblPostCode;
    private javax.swing.JLabel lblRefPerson;
    private javax.swing.JLabel lblRemark;
    private javax.swing.JLabel lblTelNo1;
    private javax.swing.JLabel lblTelNo2;
    private javax.swing.JTextField txtAddLine1;
    private javax.swing.JTextField txtAddLine2;
    private javax.swing.JTextField txtCity;
    private javax.swing.JTextField txtContactPerson;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtFax;
    private javax.swing.JTextField txtForeName;
    private javax.swing.JTextField txtMobile;
    private javax.swing.JTextField txtPostCode;
    private javax.swing.JTextField txtProvince;
    private javax.swing.JTextArea txtRemark;
    private javax.swing.JTextField txtSurName;
    private javax.swing.JTextField txtTelNo;
    private javax.swing.JTextField txtTelNo1;
    // End of variables declaration//GEN-END:variables
}
