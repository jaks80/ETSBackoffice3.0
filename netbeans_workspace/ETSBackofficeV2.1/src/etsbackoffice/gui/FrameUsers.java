package etsbackoffice.gui;

import etsbackoffice.domain.User;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;
import java.util.ListIterator;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Yusuf
 */
public class FrameUsers extends javax.swing.JFrame{

    public Object frameState = null;
    private DefaultTableModel userModel;
    private ListIterator<User> iterator;

    //List<User> userList;
    User user;
    /** Creates new form FrameUsers */
    public FrameUsers(java.awt.Frame parent) {
        initComponents();       
        //this.userList = users;
        //populateTblUsers(users);
    }

   public void populateTblUsers(List<User> users) {
        int i = 0;
        userModel = (DefaultTableModel) tblUsers.getModel();
        userModel.getDataVector().removeAllElements();
        tblUsers.repaint();
        
        iterator = users.listIterator();

        while (iterator.hasNext()) {
            user = new User();
            user = iterator.next();            
            userModel.insertRow(i, new Object[]{user.getSurName(), user.getForeName(),user.getAddLine1(), user.getPostCode(),user.getTelNo(), user.getMobile(), user.getEmail()});
            i++;
        }
    }
 /*  public void showFrameUsers(List<User> users) {
        setLocationRelativeTo(this);
        setVisible(true);
        populateTblUsers(users);
    }*/
   public void showFrameUsers() {
        setLocationRelativeTo(this);
        setVisible(true);
        toFront();
    }
   public void refreshFrame(List<User> users){
   int i = 0;
        userModel = (DefaultTableModel) tblUsers.getModel();
        userModel.getDataVector().removeAllElements();
        tblUsers.repaint();

        iterator = users.listIterator();

        while (iterator.hasNext()) {
            user = new User();
            user = iterator.next();
            userModel.insertRow(i, new Object[]{user.getSurName(), user.getForeName(),user.getAddLine1(), user.getPostCode(),user.getTelNo(), user.getMobile(), user.getEmail()});
            i++;
        }
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

        toolBar = new javax.swing.JToolBar();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        splitPaneMain = new javax.swing.JSplitPane();
        panelSearch = new javax.swing.JPanel();
        lblLoginID = new javax.swing.JLabel();
        lblName = new javax.swing.JLabel();
        lblTelNo = new javax.swing.JLabel();
        txtLoginID = new javax.swing.JTextField();
        cmbTelNo = new javax.swing.JComboBox();
        cmbName = new javax.swing.JComboBox();
        btnSearch = new javax.swing.JButton();
        splitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblUsers = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        lblPassword = new javax.swing.JLabel();
        lblLoginID1 = new javax.swing.JLabel();
        lblCreatedOn = new javax.swing.JLabel();
        lblCreatedBy = new javax.swing.JLabel();
        txtPassword = new javax.swing.JTextField();
        txtCreatedOn = new javax.swing.JTextField();
        txtCreatedBy = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtRemark = new javax.swing.JTextArea();
        txtLoginID1 = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtAddress = new javax.swing.JTextArea();
        lblAddress = new javax.swing.JLabel();
        lblRemark = new javax.swing.JLabel();
        lblLastModified = new javax.swing.JLabel();
        lblModifiedBy = new javax.swing.JLabel();
        txtLastModified = new javax.swing.JTextField();
        txtLastModifiedBy = new javax.swing.JTextField();
        statusBar = new javax.swing.JToolBar();
        jProgressBar1 = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getResourceMap(FrameUsers.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setMinimumSize(new java.awt.Dimension(800, 550));
        setName("Form"); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());

        toolBar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        toolBar.setRollover(true);
        toolBar.setName("toolBar"); // NOI18N

        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setName("jButton1"); // NOI18N
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(jButton1);

        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setName("jButton2"); // NOI18N
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(jButton2);

        jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setName("jButton3"); // NOI18N
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(jButton3);

        jButton4.setText(resourceMap.getString("jButton4.text")); // NOI18N
        jButton4.setFocusable(false);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.setName("jButton4"); // NOI18N
        jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(jButton4);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        getContentPane().add(toolBar, gridBagConstraints);

        splitPaneMain.setDividerLocation(230);
        splitPaneMain.setDividerSize(4);
        splitPaneMain.setName("splitPaneMain"); // NOI18N

        panelSearch.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("panelSearch.border.title"))); // NOI18N
        panelSearch.setName("panelSearch"); // NOI18N
        panelSearch.setLayout(new java.awt.GridBagLayout());

        lblLoginID.setText(resourceMap.getString("lblLoginID.text")); // NOI18N
        lblLoginID.setName("lblLoginID"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        panelSearch.add(lblLoginID, gridBagConstraints);

        lblName.setText(resourceMap.getString("lblName.text")); // NOI18N
        lblName.setName("lblName"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        panelSearch.add(lblName, gridBagConstraints);

        lblTelNo.setText(resourceMap.getString("lblTelNo.text")); // NOI18N
        lblTelNo.setName("lblTelNo"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        panelSearch.add(lblTelNo, gridBagConstraints);

        txtLoginID.setText(resourceMap.getString("txtLoginID.text")); // NOI18N
        txtLoginID.setName("txtLoginID"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        panelSearch.add(txtLoginID, gridBagConstraints);

        cmbTelNo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbTelNo.setName("cmbTelNo"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        panelSearch.add(cmbTelNo, gridBagConstraints);

        cmbName.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbName.setName("cmbName"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        panelSearch.add(cmbName, gridBagConstraints);

        btnSearch.setText(resourceMap.getString("btnSearch.text")); // NOI18N
        btnSearch.setName("btnSearch"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        panelSearch.add(btnSearch, gridBagConstraints);

        splitPaneMain.setLeftComponent(panelSearch);

        splitPane1.setDividerSize(4);
        splitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        splitPane1.setResizeWeight(1.0);
        splitPane1.setName("splitPane1"); // NOI18N

        jScrollPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 1, 1));
        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tblUsers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "SurName", "ForeName", "Address", "PostCode", "TelNo", "Mobile", "Email"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblUsers.setName("tblUsers"); // NOI18N
        jScrollPane1.setViewportView(tblUsers);
        tblUsers.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblUsers.columnModel.title0")); // NOI18N
        tblUsers.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblUsers.columnModel.title5")); // NOI18N
        tblUsers.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblUsers.columnModel.title1")); // NOI18N
        tblUsers.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblUsers.columnModel.title4")); // NOI18N
        tblUsers.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("tblUsers.columnModel.title2")); // NOI18N
        tblUsers.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("tblUsers.columnModel.title6")); // NOI18N
        tblUsers.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("tblUsers.columnModel.title3")); // NOI18N

        splitPane1.setTopComponent(jScrollPane1);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel1.border.title"))); // NOI18N
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new java.awt.GridBagLayout());

        lblPassword.setText(resourceMap.getString("lblPassword.text")); // NOI18N
        lblPassword.setName("lblPassword"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        jPanel1.add(lblPassword, gridBagConstraints);

        lblLoginID1.setText(resourceMap.getString("lblLoginID1.text")); // NOI18N
        lblLoginID1.setName("lblLoginID1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.3;
        jPanel1.add(lblLoginID1, gridBagConstraints);

        lblCreatedOn.setText(resourceMap.getString("lblCreatedOn.text")); // NOI18N
        lblCreatedOn.setName("lblCreatedOn"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        jPanel1.add(lblCreatedOn, gridBagConstraints);

        lblCreatedBy.setText(resourceMap.getString("lblCreatedBy.text")); // NOI18N
        lblCreatedBy.setName("lblCreatedBy"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        jPanel1.add(lblCreatedBy, gridBagConstraints);

        txtPassword.setName("txtPassword"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel1.add(txtPassword, gridBagConstraints);

        txtCreatedOn.setName("txtCreatedOn"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel1.add(txtCreatedOn, gridBagConstraints);

        txtCreatedBy.setName("txtCreatedBy"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        jPanel1.add(txtCreatedBy, gridBagConstraints);

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        txtRemark.setColumns(20);
        txtRemark.setRows(5);
        txtRemark.setName("txtRemark"); // NOI18N
        jScrollPane2.setViewportView(txtRemark);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        jPanel1.add(jScrollPane2, gridBagConstraints);

        txtLoginID1.setName("txtLoginID1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel1.add(txtLoginID1, gridBagConstraints);

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        txtAddress.setColumns(20);
        txtAddress.setRows(5);
        txtAddress.setName("txtAddress"); // NOI18N
        jScrollPane3.setViewportView(txtAddress);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        jPanel1.add(jScrollPane3, gridBagConstraints);

        lblAddress.setText(resourceMap.getString("lblAddress.text")); // NOI18N
        lblAddress.setName("lblAddress"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        jPanel1.add(lblAddress, gridBagConstraints);

        lblRemark.setText(resourceMap.getString("lblRemark.text")); // NOI18N
        lblRemark.setName("lblRemark"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        jPanel1.add(lblRemark, gridBagConstraints);

        lblLastModified.setText(resourceMap.getString("lblLastModified.text")); // NOI18N
        lblLastModified.setName("lblLastModified"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 10, 0, 0);
        jPanel1.add(lblLastModified, gridBagConstraints);

        lblModifiedBy.setText(resourceMap.getString("lblModifiedBy.text")); // NOI18N
        lblModifiedBy.setName("lblModifiedBy"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 10, 0, 0);
        jPanel1.add(lblModifiedBy, gridBagConstraints);

        txtLastModified.setText(resourceMap.getString("txtLastModified.text")); // NOI18N
        txtLastModified.setName("txtLastModified"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        jPanel1.add(txtLastModified, gridBagConstraints);

        txtLastModifiedBy.setText(resourceMap.getString("txtLastModifiedBy.text")); // NOI18N
        txtLastModifiedBy.setName("txtLastModifiedBy"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        jPanel1.add(txtLastModifiedBy, gridBagConstraints);

        splitPane1.setRightComponent(jPanel1);

        splitPaneMain.setRightComponent(splitPane1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(splitPaneMain, gridBagConstraints);

        statusBar.setRollover(true);
        statusBar.setName("statusBar"); // NOI18N

        jProgressBar1.setName("jProgressBar1"); // NOI18N
        statusBar.add(jProgressBar1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        getContentPane().add(statusBar, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
    * @param args the command line arguments
    */
   /* public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                //new FrameUsers().setVisible(true);
            }
        });
    }*/

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSearch;
    private javax.swing.JComboBox cmbName;
    private javax.swing.JComboBox cmbTelNo;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblAddress;
    private javax.swing.JLabel lblCreatedBy;
    private javax.swing.JLabel lblCreatedOn;
    private javax.swing.JLabel lblLastModified;
    private javax.swing.JLabel lblLoginID;
    private javax.swing.JLabel lblLoginID1;
    private javax.swing.JLabel lblModifiedBy;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblRemark;
    private javax.swing.JLabel lblTelNo;
    private javax.swing.JPanel panelSearch;
    private javax.swing.JSplitPane splitPane1;
    private javax.swing.JSplitPane splitPaneMain;
    private javax.swing.JToolBar statusBar;
    private javax.swing.JTable tblUsers;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JTextArea txtAddress;
    private javax.swing.JTextField txtCreatedBy;
    private javax.swing.JTextField txtCreatedOn;
    private javax.swing.JTextField txtLastModified;
    private javax.swing.JTextField txtLastModifiedBy;
    private javax.swing.JTextField txtLoginID;
    private javax.swing.JTextField txtLoginID1;
    private javax.swing.JTextField txtPassword;
    private javax.swing.JTextArea txtRemark;
    // End of variables declaration//GEN-END:variables

}