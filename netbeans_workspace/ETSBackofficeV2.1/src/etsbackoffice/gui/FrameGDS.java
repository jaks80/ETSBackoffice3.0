package etsbackoffice.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;
import javax.swing.JDialog;

/**
 *
 * @author Yusuf
 */
public class FrameGDS extends JDialog implements ActionListener {

    private boolean save;
    

    /** Creates new form FrameGDS */
    public FrameGDS(java.awt.Frame parent) {
        super(parent, "New agent", true);
        initComponents();
        btnSave.addActionListener(this);
        getRootPane().setDefaultButton(btnClose);
    }

    /*
    public boolean showGDSDialog(List<GDS> gds) {
        amadeus = new GDS();
        galileo = new GDS();
        GDS currentItem = null;

        Iterator<GDS> iterator = gds.iterator();

        while (iterator.hasNext()) {
            currentItem = iterator.next();

            if (currentItem.getGdsName().equals("Amadeus")) {
                amadeus = currentItem;
                enableAmadeusComponents();
            } else if (currentItem.getGdsName().equals("Galileo")) {
                galileo = currentItem;
                enableGalileoComponents();
            } else if (currentItem.getGdsName().equals("Worldspan")) {
                worldspan = currentItem;
            } else if (currentItem.getGdsName().equals("Worldspan")) {
                sabre = currentItem;
            }
        }

        populateAmadeus(amadeus);
        populateGalileo(galileo);

        save = false;
        setLocationRelativeTo(this);
        setVisible(true);

        if (save) {
            String aOID = txtAOID.getText().trim();
            String gOID = txtGOID.getText().trim();

            if(aOID == null ? "" != null : !aOID.equals("")){
            amadeus.setGdsID(1);            
            amadeus.setMAgtOfficeID(aOID);
            amadeus.setGdsName("Amadeus");
            amadeus.setContactPerson(txtAContactPerson.getText().trim());
            amadeus.setTelNo(txtATelNo.getText().trim());
            amadeus.setFax(txtAFax.getText().trim());
            amadeus.setEmail(txtAEmail.getText().trim());
            amadeus.setRemark(txtARemark.getText().trim());
            gds.add(amadeus);
            }

            if(gOID == null ? "" != null : !gOID.equals("")){
            
            galileo.setGdsID(2);            
            galileo.setMAgtOfficeID(gOID);
            galileo.setGdsName("Galileo");
            galileo.setContactPerson(txtGContactPerson.getText().trim());
            galileo.setTelNo(txtGTelNo.getText().trim());
            galileo.setFax(txtGFax.getText().trim());
            galileo.setEmail(txtGEmail.getText().trim());
            galileo.setRemark(txtGRemark.getText().trim());
            gds.add(galileo);
            }
            /*if(aOID == null ? "" != null : !aOID.equals("")){
            gds.add(amadeus);
            }else if(gOID == null ? "" != null : !gOID.equals("")){
            gds.add(galileo);
            }
        }
        return save;
    }*/

    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();
        if (source == btnSave) {
            save = true;
            setVisible(false);
        } else if (source == btnClose) {
            setVisible(false);
        }
    }

    /*
    private void enableAmadeusComponents() {
        txtAContactPerson.setEnabled(true);
        txtATelNo.setEnabled(true);
        txtAFax.setEnabled(true);
        txtAEmail.setEnabled(true);
        txtARemark.setEnabled(true);
    }

    private void disableAmadeusComponents() {
        txtAContactPerson.setEnabled(false);
        txtATelNo.setEnabled(false);
        txtAFax.setEnabled(false);
        txtAEmail.setEnabled(false);
        txtARemark.setEnabled(false);
    }

    private void enableGalileoComponents() {
        txtGContactPerson.setEnabled(true);
        txtGTelNo.setEnabled(true);
        txtGFax.setEnabled(true);
        txtGEmail.setEnabled(true);
        txtGRemark.setEnabled(true);
    }

    private void disableGalileoComponents() {
        txtGContactPerson.setEnabled(false);
        txtGTelNo.setEnabled(false);
        txtGFax.setEnabled(false);
        txtGEmail.setEnabled(false);
        txtGRemark.setEnabled(false);
    }

    private void populateAmadeus(GDS amadeus) {
        txtAOID.setText(amadeus.getGdsName());
        txtAContactPerson.setText(amadeus.getContactPerson());
        txtATelNo.setText(amadeus.getTelNo());
        txtAFax.setText(amadeus.getFax());
        txtAEmail.setText(amadeus.getEmail());
        txtARemark.setText(amadeus.getRemark());
    }

    private void populateGalileo(GDS galileo) {
        txtGOID.setText(galileo.getMAgtOfficeID());
        txtGContactPerson.setText(galileo.getContactPerson());
        txtGTelNo.setText(galileo.getTelNo());
        txtGFax.setText(galileo.getFax());
        txtGEmail.setText(galileo.getEmail());
        txtGRemark.setText(galileo.getRemark());
    }*/

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        lblACManager = new javax.swing.JLabel();
        lblTelNo = new javax.swing.JLabel();
        lblFax = new javax.swing.JLabel();
        lblEmail = new javax.swing.JLabel();
        lblRemark = new javax.swing.JLabel();
        txtAContactPerson = new javax.swing.JTextField();
        txtATelNo = new javax.swing.JTextField();
        txtAFax = new javax.swing.JTextField();
        txtAEmail = new javax.swing.JTextField();
        txtRemark = new javax.swing.JScrollPane();
        txtARemark = new javax.swing.JTextArea();
        lblAOID = new javax.swing.JLabel();
        txtAOID = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        lblGACManager = new javax.swing.JLabel();
        lblGTelNo = new javax.swing.JLabel();
        lblGFax = new javax.swing.JLabel();
        lblGEmail = new javax.swing.JLabel();
        lblGRemark = new javax.swing.JLabel();
        txtGContactPerson = new javax.swing.JTextField();
        txtGTelNo = new javax.swing.JTextField();
        txtGFax = new javax.swing.JTextField();
        txtGEmail = new javax.swing.JTextField();
        txtRemark1 = new javax.swing.JScrollPane();
        txtGRemark = new javax.swing.JTextArea();
        lblGOID = new javax.swing.JLabel();
        txtGOID = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        lblACManager3 = new javax.swing.JLabel();
        lblTelNo3 = new javax.swing.JLabel();
        lblFax3 = new javax.swing.JLabel();
        lblEmail3 = new javax.swing.JLabel();
        lblRemark3 = new javax.swing.JLabel();
        txtContactPerson3 = new javax.swing.JTextField();
        txtTelNo3 = new javax.swing.JTextField();
        txtFax3 = new javax.swing.JTextField();
        txtEmail3 = new javax.swing.JTextField();
        txtRemark3 = new javax.swing.JScrollPane();
        jTextArea4 = new javax.swing.JTextArea();
        lblAOID3 = new javax.swing.JLabel();
        txtAOID3 = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        lblACManager2 = new javax.swing.JLabel();
        lblTelNo2 = new javax.swing.JLabel();
        lblFax2 = new javax.swing.JLabel();
        lblEmail2 = new javax.swing.JLabel();
        lblRemark2 = new javax.swing.JLabel();
        txtContactPerson2 = new javax.swing.JTextField();
        txtTelNo2 = new javax.swing.JTextField();
        txtFax2 = new javax.swing.JTextField();
        txtEmail2 = new javax.swing.JTextField();
        txtRemark2 = new javax.swing.JScrollPane();
        jTextArea3 = new javax.swing.JTextArea();
        lblAOID2 = new javax.swing.JLabel();
        txtAOID2 = new javax.swing.JTextField();
        btnSave = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new java.awt.GridBagLayout());

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getResourceMap(FrameGDS.class);
        lblACManager.setText(resourceMap.getString("lblACManager.text")); // NOI18N
        lblACManager.setName("lblACManager"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 2);
        jPanel1.add(lblACManager, gridBagConstraints);

        lblTelNo.setText(resourceMap.getString("lblTelNo.text")); // NOI18N
        lblTelNo.setName("lblTelNo"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 2);
        jPanel1.add(lblTelNo, gridBagConstraints);

        lblFax.setText(resourceMap.getString("lblFax.text")); // NOI18N
        lblFax.setName("lblFax"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 2);
        jPanel1.add(lblFax, gridBagConstraints);

        lblEmail.setText(resourceMap.getString("lblEmail.text")); // NOI18N
        lblEmail.setName("lblEmail"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 2);
        jPanel1.add(lblEmail, gridBagConstraints);

        lblRemark.setText(resourceMap.getString("lblRemark.text")); // NOI18N
        lblRemark.setName("lblRemark"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 65;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 2);
        jPanel1.add(lblRemark, gridBagConstraints);

        txtAContactPerson.setText(resourceMap.getString("txtAContactPerson.text")); // NOI18N
        txtAContactPerson.setName("txtAContactPerson"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(txtAContactPerson, gridBagConstraints);

        txtATelNo.setText(resourceMap.getString("txtATelNo.text")); // NOI18N
        txtATelNo.setName("txtATelNo"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(txtATelNo, gridBagConstraints);

        txtAFax.setText(resourceMap.getString("txtAFax.text")); // NOI18N
        txtAFax.setName("txtAFax"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(txtAFax, gridBagConstraints);

        txtAEmail.setText(resourceMap.getString("txtAEmail.text")); // NOI18N
        txtAEmail.setName("txtAEmail"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(txtAEmail, gridBagConstraints);

        txtRemark.setName("txtRemark"); // NOI18N

        txtARemark.setColumns(20);
        txtARemark.setLineWrap(true);
        txtARemark.setRows(10);
        txtARemark.setName("txtARemark"); // NOI18N
        txtRemark.setViewportView(txtARemark);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(txtRemark, gridBagConstraints);

        lblAOID.setText(resourceMap.getString("lblAOID.text")); // NOI18N
        lblAOID.setName("lblAOID"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 4, 2, 2);
        jPanel1.add(lblAOID, gridBagConstraints);

        txtAOID.setText(resourceMap.getString("txtAOID.text")); // NOI18N
        txtAOID.setName("txtAOID"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 150;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 2, 2, 2);
        jPanel1.add(txtAOID, gridBagConstraints);

        jTabbedPane1.addTab(resourceMap.getString("jPanel1.TabConstraints.tabTitle"), jPanel1); // NOI18N

        jPanel2.setName("jPanel2"); // NOI18N

        lblGACManager.setText(resourceMap.getString("lblGACManager.text")); // NOI18N
        lblGACManager.setName("lblGACManager"); // NOI18N

        lblGTelNo.setText(resourceMap.getString("lblGTelNo.text")); // NOI18N
        lblGTelNo.setName("lblGTelNo"); // NOI18N

        lblGFax.setText(resourceMap.getString("lblGFax.text")); // NOI18N
        lblGFax.setName("lblGFax"); // NOI18N

        lblGEmail.setText(resourceMap.getString("lblGEmail.text")); // NOI18N
        lblGEmail.setName("lblGEmail"); // NOI18N

        lblGRemark.setText(resourceMap.getString("lblGRemark.text")); // NOI18N
        lblGRemark.setName("lblGRemark"); // NOI18N

        txtGContactPerson.setEnabled(false);
        txtGContactPerson.setName("txtGContactPerson"); // NOI18N

        txtGTelNo.setEnabled(false);
        txtGTelNo.setName("txtGTelNo"); // NOI18N

        txtGFax.setEnabled(false);
        txtGFax.setName("txtGFax"); // NOI18N

        txtGEmail.setEnabled(false);
        txtGEmail.setName("txtGEmail"); // NOI18N

        txtRemark1.setName("txtRemark1"); // NOI18N

        txtGRemark.setColumns(20);
        txtGRemark.setRows(10);
        txtGRemark.setEnabled(false);
        txtGRemark.setName("txtGRemark"); // NOI18N
        txtRemark1.setViewportView(txtGRemark);

        lblGOID.setText(resourceMap.getString("lblGOID.text")); // NOI18N
        lblGOID.setName("lblGOID"); // NOI18N

        txtGOID.setName("txtGOID"); // NOI18N
        txtGOID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtGOIDActionPerformed(evt);
            }
        });
        txtGOID.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtGOIDKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 543, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(lblGOID, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(4, 4, 4)
                            .addComponent(txtGOID, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(lblGACManager, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(4, 4, 4)
                            .addComponent(txtGContactPerson, javax.swing.GroupLayout.PREFERRED_SIZE, 438, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(lblGTelNo, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(4, 4, 4)
                            .addComponent(txtGTelNo, javax.swing.GroupLayout.PREFERRED_SIZE, 438, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(lblGFax, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(4, 4, 4)
                            .addComponent(txtGFax, javax.swing.GroupLayout.PREFERRED_SIZE, 438, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(lblGEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(4, 4, 4)
                            .addComponent(txtGEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 438, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(lblGRemark, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(4, 4, 4)
                            .addComponent(txtRemark1, javax.swing.GroupLayout.PREFERRED_SIZE, 438, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 361, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGap(0, 4, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblGOID)
                        .addComponent(txtGOID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(4, 4, 4)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblGACManager)
                        .addComponent(txtGContactPerson, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(4, 4, 4)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblGTelNo)
                        .addComponent(txtGTelNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(4, 4, 4)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblGFax)
                        .addComponent(txtGFax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(4, 4, 4)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblGEmail)
                        .addComponent(txtGEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(4, 4, 4)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblGRemark)
                        .addComponent(txtRemark1, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(0, 4, Short.MAX_VALUE)))
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel2.TabConstraints.tabTitle"), jPanel2); // NOI18N

        jPanel3.setName("jPanel3"); // NOI18N

        lblACManager3.setText(resourceMap.getString("lblACManager3.text")); // NOI18N
        lblACManager3.setName("lblACManager3"); // NOI18N

        lblTelNo3.setText(resourceMap.getString("lblTelNo3.text")); // NOI18N
        lblTelNo3.setName("lblTelNo3"); // NOI18N

        lblFax3.setText(resourceMap.getString("lblFax3.text")); // NOI18N
        lblFax3.setName("lblFax3"); // NOI18N

        lblEmail3.setText(resourceMap.getString("lblEmail3.text")); // NOI18N
        lblEmail3.setName("lblEmail3"); // NOI18N

        lblRemark3.setText(resourceMap.getString("lblRemark3.text")); // NOI18N
        lblRemark3.setName("lblRemark3"); // NOI18N

        txtContactPerson3.setEnabled(false);
        txtContactPerson3.setName("txtContactPerson3"); // NOI18N

        txtTelNo3.setEnabled(false);
        txtTelNo3.setName("txtTelNo3"); // NOI18N

        txtFax3.setEnabled(false);
        txtFax3.setName("txtFax3"); // NOI18N

        txtEmail3.setEnabled(false);
        txtEmail3.setName("txtEmail3"); // NOI18N

        txtRemark3.setName("txtRemark3"); // NOI18N

        jTextArea4.setColumns(20);
        jTextArea4.setRows(10);
        jTextArea4.setEnabled(false);
        jTextArea4.setName("jTextArea4"); // NOI18N
        txtRemark3.setViewportView(jTextArea4);

        lblAOID3.setText(resourceMap.getString("lblAOID3.text")); // NOI18N
        lblAOID3.setName("lblAOID3"); // NOI18N

        txtAOID3.setName("txtAOID3"); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 543, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addComponent(lblAOID3, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(4, 4, 4)
                            .addComponent(txtAOID3, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addComponent(lblACManager3, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(4, 4, 4)
                            .addComponent(txtContactPerson3, javax.swing.GroupLayout.PREFERRED_SIZE, 438, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addComponent(lblTelNo3, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(4, 4, 4)
                            .addComponent(txtTelNo3, javax.swing.GroupLayout.PREFERRED_SIZE, 438, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addComponent(lblFax3, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(4, 4, 4)
                            .addComponent(txtFax3, javax.swing.GroupLayout.PREFERRED_SIZE, 438, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addComponent(lblEmail3, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(4, 4, 4)
                            .addComponent(txtEmail3, javax.swing.GroupLayout.PREFERRED_SIZE, 438, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addComponent(lblRemark3, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(4, 4, 4)
                            .addComponent(txtRemark3, javax.swing.GroupLayout.PREFERRED_SIZE, 438, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 361, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addGap(0, 4, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblAOID3)
                        .addComponent(txtAOID3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(4, 4, 4)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblACManager3)
                        .addComponent(txtContactPerson3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(4, 4, 4)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblTelNo3)
                        .addComponent(txtTelNo3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(4, 4, 4)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblFax3)
                        .addComponent(txtFax3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(4, 4, 4)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblEmail3)
                        .addComponent(txtEmail3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(4, 4, 4)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblRemark3)
                        .addComponent(txtRemark3, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(0, 4, Short.MAX_VALUE)))
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel3.TabConstraints.tabTitle"), jPanel3); // NOI18N

        jPanel4.setName("jPanel4"); // NOI18N

        lblACManager2.setText(resourceMap.getString("lblACManager2.text")); // NOI18N
        lblACManager2.setName("lblACManager2"); // NOI18N

        lblTelNo2.setText(resourceMap.getString("lblTelNo2.text")); // NOI18N
        lblTelNo2.setName("lblTelNo2"); // NOI18N

        lblFax2.setText(resourceMap.getString("lblFax2.text")); // NOI18N
        lblFax2.setName("lblFax2"); // NOI18N

        lblEmail2.setText(resourceMap.getString("lblEmail2.text")); // NOI18N
        lblEmail2.setName("lblEmail2"); // NOI18N

        lblRemark2.setText(resourceMap.getString("lblRemark2.text")); // NOI18N
        lblRemark2.setName("lblRemark2"); // NOI18N

        txtContactPerson2.setEnabled(false);
        txtContactPerson2.setName("txtContactPerson2"); // NOI18N

        txtTelNo2.setEnabled(false);
        txtTelNo2.setName("txtTelNo2"); // NOI18N

        txtFax2.setEnabled(false);
        txtFax2.setName("txtFax2"); // NOI18N

        txtEmail2.setEnabled(false);
        txtEmail2.setName("txtEmail2"); // NOI18N

        txtRemark2.setName("txtRemark2"); // NOI18N

        jTextArea3.setColumns(20);
        jTextArea3.setRows(10);
        jTextArea3.setEnabled(false);
        jTextArea3.setName("jTextArea3"); // NOI18N
        txtRemark2.setViewportView(jTextArea3);

        lblAOID2.setText(resourceMap.getString("lblAOID2.text")); // NOI18N
        lblAOID2.setName("lblAOID2"); // NOI18N

        txtAOID2.setName("txtAOID2"); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 543, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addComponent(lblAOID2, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(4, 4, 4)
                            .addComponent(txtAOID2, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addComponent(lblACManager2, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(4, 4, 4)
                            .addComponent(txtContactPerson2, javax.swing.GroupLayout.PREFERRED_SIZE, 438, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addComponent(lblTelNo2, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(4, 4, 4)
                            .addComponent(txtTelNo2, javax.swing.GroupLayout.PREFERRED_SIZE, 438, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addComponent(lblFax2, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(4, 4, 4)
                            .addComponent(txtFax2, javax.swing.GroupLayout.PREFERRED_SIZE, 438, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addComponent(lblEmail2, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(4, 4, 4)
                            .addComponent(txtEmail2, javax.swing.GroupLayout.PREFERRED_SIZE, 438, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addComponent(lblRemark2, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(4, 4, 4)
                            .addComponent(txtRemark2, javax.swing.GroupLayout.PREFERRED_SIZE, 438, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 361, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addGap(0, 4, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblAOID2)
                        .addComponent(txtAOID2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(4, 4, 4)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblACManager2)
                        .addComponent(txtContactPerson2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(4, 4, 4)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblTelNo2)
                        .addComponent(txtTelNo2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(4, 4, 4)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblFax2)
                        .addComponent(txtFax2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(4, 4, 4)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblEmail2)
                        .addComponent(txtEmail2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(4, 4, 4)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblRemark2)
                        .addComponent(txtRemark2, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(0, 4, Short.MAX_VALUE)))
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel4.TabConstraints.tabTitle"), jPanel4); // NOI18N

        btnSave.setText(resourceMap.getString("btnSave.text")); // NOI18N
        btnSave.setName("btnSave"); // NOI18N

        btnClose.setText(resourceMap.getString("btnClose.text")); // NOI18N
        btnClose.setName("btnClose"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 389, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnClose)
                    .addComponent(btnSave))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtGOIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtGOIDActionPerformed
        
    }//GEN-LAST:event_txtGOIDActionPerformed

    private void txtGOIDKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtGOIDKeyPressed
        //enableGalileoComponents();
    }//GEN-LAST:event_txtGOIDKeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnSave;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea jTextArea3;
    private javax.swing.JTextArea jTextArea4;
    private javax.swing.JLabel lblACManager;
    private javax.swing.JLabel lblACManager2;
    private javax.swing.JLabel lblACManager3;
    private javax.swing.JLabel lblAOID;
    private javax.swing.JLabel lblAOID2;
    private javax.swing.JLabel lblAOID3;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblEmail2;
    private javax.swing.JLabel lblEmail3;
    private javax.swing.JLabel lblFax;
    private javax.swing.JLabel lblFax2;
    private javax.swing.JLabel lblFax3;
    private javax.swing.JLabel lblGACManager;
    private javax.swing.JLabel lblGEmail;
    private javax.swing.JLabel lblGFax;
    private javax.swing.JLabel lblGOID;
    private javax.swing.JLabel lblGRemark;
    private javax.swing.JLabel lblGTelNo;
    private javax.swing.JLabel lblRemark;
    private javax.swing.JLabel lblRemark2;
    private javax.swing.JLabel lblRemark3;
    private javax.swing.JLabel lblTelNo;
    private javax.swing.JLabel lblTelNo2;
    private javax.swing.JLabel lblTelNo3;
    private javax.swing.JTextField txtAContactPerson;
    private javax.swing.JTextField txtAEmail;
    private javax.swing.JTextField txtAFax;
    private javax.swing.JTextField txtAOID;
    private javax.swing.JTextField txtAOID2;
    private javax.swing.JTextField txtAOID3;
    private javax.swing.JTextArea txtARemark;
    private javax.swing.JTextField txtATelNo;
    private javax.swing.JTextField txtContactPerson2;
    private javax.swing.JTextField txtContactPerson3;
    private javax.swing.JTextField txtEmail2;
    private javax.swing.JTextField txtEmail3;
    private javax.swing.JTextField txtFax2;
    private javax.swing.JTextField txtFax3;
    private javax.swing.JTextField txtGContactPerson;
    private javax.swing.JTextField txtGEmail;
    private javax.swing.JTextField txtGFax;
    private javax.swing.JTextField txtGOID;
    private javax.swing.JTextArea txtGRemark;
    private javax.swing.JTextField txtGTelNo;
    private javax.swing.JScrollPane txtRemark;
    private javax.swing.JScrollPane txtRemark1;
    private javax.swing.JScrollPane txtRemark2;
    private javax.swing.JScrollPane txtRemark3;
    private javax.swing.JTextField txtTelNo2;
    private javax.swing.JTextField txtTelNo3;
    // End of variables declaration//GEN-END:variables
}
