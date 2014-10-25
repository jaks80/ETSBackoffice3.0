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
import etsbackoffice.businesslogic.VendorBo;
import etsbackoffice.domain.OtherService;
import etsbackoffice.domain.Vendor;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import org.apache.commons.lang.WordUtils;
import org.jdesktop.application.Action;

/**
 *
 * @author Yusuf
 */
public class FrameOServiceManager extends javax.swing.JFrame {

    private OServiceBo oServiceBo = (OServiceBo) ETSBackofficeApp.getApplication().ctx.getBean("oServiceBo");
    private VendorBo vendorBo = (VendorBo) ETSBackofficeApp.getApplication().ctx.getBean("vendorBo");
    private OtherService oService;
    private List<OtherService> oServices = new ArrayList();
    private List<Vendor> vendors = new ArrayList();
    CheckInput a = new CheckInput();
    CheckInput b = new CheckInput();
    CheckInput c = new CheckInput();

    /** Creates new form FrameOServices */
    public FrameOServiceManager(java.awt.Frame parent) {
        initComponents();
        AbstractDocument doc;
        doc = (AbstractDocument) txtSTitle.getDocument();
        doc.setDocumentFilter(new DocumentSizeFilter(50));
        doc = (AbstractDocument) txtRemark.getDocument();
        doc.setDocumentFilter(new DocumentSizeFilter(400));

        txtSCharge.setDocument(a);
        txtSCost.setDocument(b);
        txtVat.setDocument(c);
        PopulateTblOServices s = new PopulateTblOServices();
        s.execute();
    }

    @Action
    public void newVendor() {
        
        DlgVendor dlgVendor = new DlgVendor(this);
        Vendor vendor = new Vendor();
        if (dlgVendor.showAgentDialog(vendor)) {
            SaveNewVendor s = new SaveNewVendor(vendor);
            s.execute();
        }
    }
        
    private void addNewService() {

        oService = new OtherService();
        String title = txtSTitle.getText().trim();
        String sCost = txtSCost.getText().trim();
        String sCharge = txtSCharge.getText().trim();
        String vat = txtVat.getText().trim();
        String remark = txtRemark.getText();
        if (!title.isEmpty() && !sCost.isEmpty() && !sCharge.isEmpty() && !vat.isEmpty()) {
            oService.setServiceTitle(WordUtils.capitalizeFully(title));
            oService.setServiceCost(new BigDecimal(sCost));
            oService.setServiceCharge(new BigDecimal(sCharge));
            oService.setVatRate(new BigDecimal(vat));
            oService.setRemark(WordUtils.capitalizeFully(remark));            
            oService.setServiceType(1);
            oService.setCalculationtype(1);//Fixed            
            oService.setVendor(vendors.get(cmbVendor.getSelectedIndex()-1));
            Thread t = new Thread(new threadSaveService());
            t.start();
            try {
                t.join();
                PopulateTblOServices s = new PopulateTblOServices();
                s.execute();
                resetComponent();
            } catch (InterruptedException ex) {
                Logger.getLogger(FrameOServiceManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Enter mendatory fields !!!", "New Service", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateService() {
        String title = txtSTitle.getText().trim();
        String sCost = txtSCost.getText().trim();
        String sCharge = txtSCharge.getText().trim();
        String vat = txtVat.getText().trim();
        String remark = txtRemark.getText();
        if (!title.isEmpty() && !sCost.isEmpty() && !sCharge.isEmpty() && !vat.isEmpty()) {
            oService.setServiceTitle(WordUtils.capitalizeFully(title));
            oService.setServiceCost(new BigDecimal(sCost));
            oService.setServiceCharge(new BigDecimal(sCharge));
            oService.setVatRate(new BigDecimal(vat));
            oService.setRemark(remark);                    
            //oService.setVendor(vendors.get(cmbVendor.getSelectedIndex()-1));
            Thread t = new Thread(new threadSaveService());
            t.start();
            try {
                t.join();
                PopulateTblOServices s = new PopulateTblOServices();
                resetComponent();
        s.execute();
            } catch (InterruptedException ex) {
                Logger.getLogger(FrameOServiceManager.class.getName()).log(Level.SEVERE, null, ex);
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
        txtVat.setText("");
        txtRemark.setText("");
        cmbVendor.setSelectedIndex(-1);
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
        txtVat.setText(this.oService.getVatRate().toString());
        if (this.oService.getVendor() != null) {
            cmbVendor.setSelectedItem(this.oService.getVendor().getName());
        } else {
            cmbVendor.setSelectedIndex(-1);
        }
        if (this.oService.getRemark() != null) {
            txtRemark.setText(this.oService.getRemark());
        } else {
            txtRemark.setText("");
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
        jLabel4 = new javax.swing.JLabel();
        txtSTitle = new javax.swing.JTextField();
        txtSCost = new javax.swing.JTextField();
        txtSCharge = new javax.swing.JTextField();
        txtVat = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtRemark = new javax.swing.JTextArea();
        btnNewService = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        cmbVendor = new javax.swing.JComboBox();
        btnNewVendor = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        jSeparator2 = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getResourceMap(FrameOServiceManager.class);
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
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
        jPanel1.add(jLabel1, gridBagConstraints);

        jLabel2.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
        jPanel1.add(jLabel2, gridBagConstraints);

        jLabel3.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
        jPanel1.add(jLabel3, gridBagConstraints);

        jLabel4.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
        jPanel1.add(jLabel4, gridBagConstraints);

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

        txtVat.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        txtVat.setText(resourceMap.getString("txtVat.text")); // NOI18N
        txtVat.setMinimumSize(new java.awt.Dimension(150, 20));
        txtVat.setName("txtVat"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 80;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
        jPanel1.add(txtVat, gridBagConstraints);

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
        gridBagConstraints.gridwidth = 4;
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
        btnNewService.setToolTipText(resourceMap.getString("btnNewService.toolTipText")); // NOI18N
        btnNewService.setName("btnNewService"); // NOI18N
        btnNewService.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewServiceActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(btnNewService, gridBagConstraints);

        btnSave.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        btnSave.setIcon(resourceMap.getIcon("btnSave.icon")); // NOI18N
        btnSave.setText(resourceMap.getString("btnSave.text")); // NOI18N
        btnSave.setToolTipText(resourceMap.getString("btnSave.toolTipText")); // NOI18N
        btnSave.setName("btnSave"); // NOI18N
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(btnSave, gridBagConstraints);

        btnClose.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        btnClose.setIcon(resourceMap.getIcon("btnClose.icon")); // NOI18N
        btnClose.setText(resourceMap.getString("btnClose.text")); // NOI18N
        btnClose.setToolTipText(resourceMap.getString("btnClose.toolTipText")); // NOI18N
        btnClose.setName("btnClose"); // NOI18N
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(btnClose, gridBagConstraints);

        jLabel6.setFont(resourceMap.getFont("jLabel6.font")); // NOI18N
        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(jLabel6, gridBagConstraints);

        cmbVendor.setEditable(true);
        cmbVendor.setFont(resourceMap.getFont("cmbVendor.font")); // NOI18N
        cmbVendor.setName("cmbVendor"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel1.add(cmbVendor, gridBagConstraints);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getActionMap(FrameOServiceManager.class, this);
        btnNewVendor.setAction(actionMap.get("newVendor")); // NOI18N
        btnNewVendor.setFont(resourceMap.getFont("btnNewVendor.font")); // NOI18N
        btnNewVendor.setIcon(resourceMap.getIcon("btnNewVendor.icon")); // NOI18N
        btnNewVendor.setText(resourceMap.getString("btnNewVendor.text")); // NOI18N
        btnNewVendor.setToolTipText(resourceMap.getString("btnNewVendor.toolTipText")); // NOI18N
        btnNewVendor.setName("btnNewVendor"); // NOI18N
        btnNewVendor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewVendorActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(btnNewVendor, gridBagConstraints);

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
        Loadvendors lv = new Loadvendors();
        lv.execute();
    }//GEN-LAST:event_btnNewServiceActionPerformed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnNewVendorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewVendorActionPerformed
       
    }//GEN-LAST:event_btnNewVendorActionPerformed

    /**
     * @param args the command line arguments
     */
 /*   public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new FrameOServiceManager().setVisible(true);
            }
        });
    }*/
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnNewService;
    private javax.swing.JButton btnNewVendor;
    private javax.swing.JButton btnSave;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox cmbVendor;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel statusMessageLabel;
    private org.jdesktop.swingx.JXTable tblOServices;
    private javax.swing.JTextArea txtRemark;
    private javax.swing.JTextField txtSCharge;
    private javax.swing.JTextField txtSCost;
    private javax.swing.JTextField txtSTitle;
    private javax.swing.JTextField txtVat;
    // End of variables declaration//GEN-END:variables
    //Thread1 to load complete pnr;
    private long startTime = 0;
    private long stopTime = 0;
    private float elapsedTime = 0;

    /**
     * @param pnrDao the pnrDao to set
     */ 

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
    
   private class SaveNewVendor extends SwingWorker<Vendor, Void> {

        private Vendor newV;

        public SaveNewVendor(Vendor newV) {
            this.newV = newV;
        };
        
        @Override
        protected Vendor doInBackground() throws Exception {
            vendorBo.save(this.newV);
            return this.newV;
        }
        
        @Override
        protected void done() {
            try {
                newV = get();
                Loadvendors lv = new Loadvendors();
                lv.execute();
            } catch (InterruptedException ex) {
                Logger.getLogger(FrameSAcDocHistory.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(FrameSAcDocHistory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }  
   
     private class Loadvendors extends SwingWorker<List<Vendor>, Void> {        

        public Loadvendors() {            
        };
        
        @Override
        protected List<Vendor> doInBackground() throws Exception {           
            List<Vendor> vendors = vendorBo.getVendors();
            return vendors;
        }
        
        @Override
        protected void done() {
            try {
                vendors = get();
                
                List cmbElement = new ArrayList();
                
                for (Vendor v : vendors) {
                    cmbElement.add(v.getName());
                }
                
                DefaultComboBoxModel cmbVendorsModel = new DefaultComboBoxModel(cmbElement.toArray());
                cmbVendor.setModel(cmbVendorsModel);      
                cmbVendorsModel.insertElementAt("Select", 0);
                cmbVendor.setSelectedIndex(0);
            } catch (InterruptedException ex) {
                Logger.getLogger(FrameSAcDocHistory.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(FrameSAcDocHistory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    } 
     
    private class PopulateTblOServices extends SwingWorker<List<OtherService>, Void> {

        public PopulateTblOServices() {
        }

        @Override
        protected List<OtherService> doInBackground() throws Exception {
            List<OtherService> services = oServiceBo.loadOtherServices();
            return services;
        }

        @Override
        protected void done() {
            try {
                oServices = get();
                populateTblOServices();
            } catch (InterruptedException ex) {
                Logger.getLogger(FrameSAcDocHistory.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(FrameSAcDocHistory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
