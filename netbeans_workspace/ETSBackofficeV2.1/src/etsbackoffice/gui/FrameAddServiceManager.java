/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrameOServices.java
 *
 * Created on 25-Mar-2011, 16:14:21
 */
package etsbackoffice.gui;

import etsbackoffice.ETSBackofficeApp;
import etsbackoffice.businesslogic.DocumentSizeFilter;
import etsbackoffice.businesslogic.OServiceBo;
import etsbackoffice.domain.OtherService;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import org.apache.commons.lang.WordUtils;

/**
 *
 * @author Yusuf
 */
public class FrameAddServiceManager extends javax.swing.JFrame{

    private OServiceBo oServiceBo = (OServiceBo) ETSBackofficeApp.getApplication().ctx.getBean("oServiceBo");
    private OtherService oService;
    private List<OtherService> oServices = new ArrayList();
    CheckInput a = new CheckInput();
    CheckInput b = new CheckInput();    

    /** Creates new form FrameOServices */
    public FrameAddServiceManager(java.awt.Frame parent) {
        initComponents();
        AbstractDocument doc;
        doc = (AbstractDocument) txtSTitle.getDocument();
        doc.setDocumentFilter(new DocumentSizeFilter(50));
        doc = (AbstractDocument) txtRemark.getDocument();
        doc.setDocumentFilter(new DocumentSizeFilter(400));

        txtSCharge.setDocument(a);
        txtSCost.setDocument(b);
        loadAllSerivices();
    }

    private void addNewService() {

        oService = new OtherService();
        String title = txtSTitle.getText().trim();
        String sCost = txtSCost.getText().trim();
        String sCharge = txtSCharge.getText().trim();        
        String remark = txtRemark.getText();
        if (!title.isEmpty() && !sCost.isEmpty() && !sCharge.isEmpty()) {
            oService.setServiceTitle(WordUtils.capitalizeFully(title));
            oService.setServiceCost(new BigDecimal(sCost));
            oService.setServiceCharge(new BigDecimal(sCharge));            
            oService.setRemark(WordUtils.capitalizeFully(remark));
            oService.setVatRate(new BigDecimal("0.00"));
            oService.setServiceType(2);
            if (rdoFixed.isSelected()) {
                oService.setCalculationtype(1);
            } else if (rdoPercentage.isSelected()) {
                oService.setCalculationtype(2);
            }
            
            Thread t = new Thread(new threadSaveService());
            t.start();
            try {
                t.join();
                new Thread(new threadLoadAllServices()).start();
            } catch (InterruptedException ex) {
                Logger.getLogger(FrameAddServiceManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Enter mendatory fields !!!", "New Service", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateService() {
        String title = txtSTitle.getText().trim();
        String sCost = txtSCost.getText().trim();
        String sCharge = txtSCharge.getText().trim();        
        String remark = txtRemark.getText();
        if (!title.isEmpty() && !sCost.isEmpty() && !sCharge.isEmpty()) {
            oService.setServiceTitle(WordUtils.capitalizeFully(title));
            oService.setServiceCost(new BigDecimal(sCost));
            oService.setServiceCharge(new BigDecimal(sCharge));           
            oService.setRemark(WordUtils.capitalizeFully(remark));
            oService.setVatRate(new BigDecimal("0.00"));
            if (rdoFixed.isSelected()) {
                oService.setCalculationtype(1);
            } else if (rdoPercentage.isSelected()) {
                oService.setCalculationtype(2);
            }

            Thread t = new Thread(new threadSaveService());
            t.start();
            try {
                t.join();
                new Thread(new threadLoadAllServices()).start();
            } catch (InterruptedException ex) {
                Logger.getLogger(FrameAddServiceManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Enter mendatory fields !!!", "New Service", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void saveSerivce() {
        if (oService == null) {
            addNewService();
        } else {
            updateService();
        }
    }

    private void resetComponent() {
        this.oService = null;
        txtSTitle.setText("");
        txtSCost.setText("");
        txtSCharge.setText("");     
        txtRemark.setText("");
        rdoFixed.isSelected();
    }

    private void loadAllSerivices() {
        new Thread(new threadLoadAllServices()).start();
    }

    private void populateTblOServices() {

        DefaultTableModel servicesModel;
        servicesModel = (DefaultTableModel) tblOServices.getModel();
        servicesModel.getDataVector().removeAllElements();
        tblOServices.repaint();

        int row = 0;
        for (OtherService o : this.oServices) {
            servicesModel.insertRow(row, new Object[]{o.getServiceTitle(), o.getServiceCharge(),
                        o.getServiceCost(), o.getVatRate()});
            row++;
        }
    }

    private void populateServiceDetails() {
        txtSTitle.setText(this.oService.getServiceTitle());
        txtSCost.setText(this.oService.getServiceCost().toString());
        txtSCharge.setText(this.oService.getServiceCharge().toString());       

        if(this.oService.getCalculationtype()==1){
        rdoFixed.setSelected(true);
        }else{
        rdoPercentage.setSelected(true);
        }
        
        if (this.oService.getRemark() != null) {
            txtRemark.setText(this.oService.getRemark().trim());
        }
    }
    private ListSelectionListener tblOServicesListener = new ListSelectionListener() {

        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int selectedRow = tblOServices.getSelectedRow();
            if (selectedRow != -1) {
                oService = oServices.get(selectedRow);
                populateServiceDetails();
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
        jScrollPane1 = new javax.swing.JScrollPane();
        tblOServices = new org.jdesktop.swingx.JXTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtSTitle = new javax.swing.JTextField();
        txtSCost = new javax.swing.JTextField();
        txtSCharge = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtRemark = new javax.swing.JTextArea();
        btnNewService = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        rdoFixed = new javax.swing.JRadioButton();
        rdoPercentage = new javax.swing.JRadioButton();
        jLabel6 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        jSeparator2 = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getResourceMap(FrameAddServiceManager.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setMinimumSize(new java.awt.Dimension(750, 530));
        setName("Form"); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tblOServices.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title", "Service Cost", "Service Charge", "VAT(%)"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblOServices.setFont(resourceMap.getFont("tblOServices.font")); // NOI18N
        tblOServices.setName("tblOServices"); // NOI18N
        tblOServices.getTableHeader().setReorderingAllowed(false);
        tblOServices.getSelectionModel().addListSelectionListener(tblOServicesListener);
        jScrollPane1.setViewportView(tblOServices);
        tblOServices.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblOServices.columnModel.title0")); // NOI18N
        tblOServices.getColumnModel().getColumn(1).setMinWidth(100);
        tblOServices.getColumnModel().getColumn(1).setPreferredWidth(100);
        tblOServices.getColumnModel().getColumn(1).setMaxWidth(100);
        tblOServices.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblOServices.columnModel.title1")); // NOI18N
        tblOServices.getColumnModel().getColumn(2).setMinWidth(100);
        tblOServices.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblOServices.getColumnModel().getColumn(2).setMaxWidth(100);
        tblOServices.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblOServices.columnModel.title2")); // NOI18N
        tblOServices.getColumnModel().getColumn(3).setMinWidth(80);
        tblOServices.getColumnModel().getColumn(3).setPreferredWidth(80);
        tblOServices.getColumnModel().getColumn(3).setMaxWidth(80);
        tblOServices.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblOServices.columnModel.title3")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        getContentPane().add(jScrollPane1, gridBagConstraints);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
        jPanel1.add(jLabel1, gridBagConstraints);

        jLabel2.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
        jPanel1.add(jLabel2, gridBagConstraints);

        jLabel3.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
        jPanel1.add(jLabel3, gridBagConstraints);

        txtSTitle.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        txtSTitle.setText(resourceMap.getString("txtSTitle.text")); // NOI18N
        txtSTitle.setName("txtSTitle"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
        jPanel1.add(txtSTitle, gridBagConstraints);

        txtSCost.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        txtSCost.setText(resourceMap.getString("txtSCost.text")); // NOI18N
        txtSCost.setMinimumSize(new java.awt.Dimension(150, 20));
        txtSCost.setName("txtSCost"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 80;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
        jPanel1.add(txtSCost, gridBagConstraints);

        txtSCharge.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        txtSCharge.setText(resourceMap.getString("txtSCharge.text")); // NOI18N
        txtSCharge.setMinimumSize(new java.awt.Dimension(150, 20));
        txtSCharge.setName("txtSCharge"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 80;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
        jPanel1.add(txtSCharge, gridBagConstraints);

        jLabel5.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
        jPanel1.add(jLabel5, gridBagConstraints);

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        txtRemark.setColumns(20);
        txtRemark.setFont(resourceMap.getFont("txtRemark.font")); // NOI18N
        txtRemark.setRows(5);
        txtRemark.setName("txtRemark"); // NOI18N
        jScrollPane2.setViewportView(txtRemark);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
        jPanel1.add(jScrollPane2, gridBagConstraints);

        btnNewService.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        btnNewService.setIcon(resourceMap.getIcon("btnNewService.icon")); // NOI18N
        btnNewService.setText(resourceMap.getString("btnNewService.text")); // NOI18N
        btnNewService.setName("btnNewService"); // NOI18N
        btnNewService.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewServiceActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(btnNewService, gridBagConstraints);

        btnSave.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        btnSave.setIcon(resourceMap.getIcon("btnSave.icon")); // NOI18N
        btnSave.setText(resourceMap.getString("btnSave.text")); // NOI18N
        btnSave.setName("btnSave"); // NOI18N
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(btnSave, gridBagConstraints);

        btnClose.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        btnClose.setIcon(resourceMap.getIcon("btnClose.icon")); // NOI18N
        btnClose.setText(resourceMap.getString("btnClose.text")); // NOI18N
        btnClose.setName("btnClose"); // NOI18N
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(btnClose, gridBagConstraints);

        buttonGroup1.add(rdoFixed);
        rdoFixed.setFont(resourceMap.getFont("rdoFixed.font")); // NOI18N
        rdoFixed.setSelected(true);
        rdoFixed.setText(resourceMap.getString("rdoFixed.text")); // NOI18N
        rdoFixed.setName("rdoFixed"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(rdoFixed, gridBagConstraints);

        buttonGroup1.add(rdoPercentage);
        rdoPercentage.setFont(resourceMap.getFont("rdoPercentage.font")); // NOI18N
        rdoPercentage.setText(resourceMap.getString("rdoPercentage.text")); // NOI18N
        rdoPercentage.setName("rdoPercentage"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(rdoPercentage, gridBagConstraints);

        jLabel6.setFont(resourceMap.getFont("jLabel6.font")); // NOI18N
        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
        jPanel1.add(jLabel6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        getContentPane().add(jPanel1, gridBagConstraints);

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

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(jPanel4, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed

        saveSerivce();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnNewServiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewServiceActionPerformed
        resetComponent();
    }//GEN-LAST:event_btnNewServiceActionPerformed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
       dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    /**
     * @param args the command line arguments
     */
  /*  public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new FrameAddServiceManager().setVisible(true);
            }
        });
    }*/
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnNewService;
    private javax.swing.JButton btnSave;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JRadioButton rdoFixed;
    private javax.swing.JRadioButton rdoPercentage;
    private javax.swing.JLabel statusMessageLabel;
    private org.jdesktop.swingx.JXTable tblOServices;
    private javax.swing.JTextArea txtRemark;
    private javax.swing.JTextField txtSCharge;
    private javax.swing.JTextField txtSCost;
    private javax.swing.JTextField txtSTitle;
    // End of variables declaration//GEN-END:variables
    //Thread1 to load complete pnr;
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

            oServices = oServiceBo.loadAdditionalServices();
            resetComponent();
            populateTblOServices();
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            //progressBar.repaint(); //Refresh graphics
        }
    }

    private class threadSaveService implements Runnable {

        public threadSaveService() {
        }

        public void run() {

            //progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            //progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();
            statusMessageLabel.setText("Saving Service");

            oServiceBo.saveOrUpdateOService(oService);

            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            //progressBar.repaint(); //Refresh graphics
        }
    } 
}
