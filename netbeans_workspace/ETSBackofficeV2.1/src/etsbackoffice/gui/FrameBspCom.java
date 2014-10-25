package etsbackoffice.gui;

import etsbackoffice.ETSBackofficeApp;
import etsbackoffice.businesslogic.CareerBo;
import etsbackoffice.domain.Career;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.jdesktop.application.Action;

public class FrameBspCom extends javax.swing.JDialog {

    private boolean save;
    private boolean submitNeeded;
    private List<Career> careers = new ArrayList();
    CareerBo careerBo = (CareerBo) ETSBackofficeApp.getApplication().ctx.getBean("careerBo");
    DefaultTableModel carModel = new DefaultTableModel();

    public FrameBspCom(java.awt.Frame parent, boolean modal) {
        super(parent, true);
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension dimension = tk.getScreenSize();
        int screenWidth = dimension.width;
        int screenHeight = dimension.height;
        setLocation(screenWidth / 2 - 200, screenHeight / 2 - 200);
        initComponents();

        TableColumn comP, comF;
        comP = tblCareer.getColumnModel().getColumn(2);
        comF = tblCareer.getColumnModel().getColumn(3);
        JTextField jft = new JTextField();
        //jft.setBackground(Color.red);
        jft.setDocument(new CheckInput(CheckInput.FLOAT));
        comP.setCellEditor(new DefaultCellEditor(jft));
        comF.setCellEditor(new DefaultCellEditor(jft));
    }

    public void showBspComSetting() {
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    private void populateBSPTable() {        
        carModel = (DefaultTableModel) tblCareer.getModel();
        carModel.getDataVector().removeAllElements();
        tblCareer.clearSelection();
        tblCareer.repaint();
        
        Iterator it = this.careers.iterator();

        while (it.hasNext()) {
            Career c = (Career) it.next();
            int row = 0;
            carModel.insertRow(row, new Object[]{c.getCode(), c.getName(), c.getBspComPercentage(), c.getBspComFixed()});
            row++;
        }

        carModel.addTableModelListener(new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    int row = e.getFirstRow();
                    int column = e.getColumn();
                    String code = "";
                    String name = "";
                                        
                    if (column == 1) {
                        code = carModel.getValueAt(row, 0).toString();
                        name = carModel.getValueAt(row, column).toString().toUpperCase();
                        for (Career c : careers) {
                            if (c.getCode().equals(code)) {
                                c.setName(name);
                            }
                        }
                    } else if (column == 2) {
                        BigDecimal comP = new BigDecimal("0.00");
                        code = carModel.getValueAt(row, 0).toString();
                        comP = new BigDecimal(carModel.getValueAt(row, column).toString());
                        for (Career c : careers) {
                            if (c.getCode().equals(code)) {
                                c.setBspComPercentage(comP);
                                //c.setBspComFixed(new BigDecimal("0.00"));
                            }
                        }

                    } else if (column == 3) {
                        BigDecimal comF = new BigDecimal("0.00");
                        code = carModel.getValueAt(row, 0).toString();
                        comF = new BigDecimal(carModel.getValueAt(row, column).toString());                        
                        for (Career c : careers) {
                            if (c.getCode().equals(code)) {
                                c.setBspComFixed(comF);
                              //  c.setBspComFixed(new BigDecimal("0.00"));
                            }                            
                        }
                    }
                }
            }
        });
    }

    private void findAllCareer() {
        this.careers = careerBo.findAll();
    }

    private void findCareer(String code) {
        careerBo.find(code);
    }

    @Action
    public void loadCareer() {
        busyIcon.setVisible(true);
        busyIcon.setBusy(true);
        findAllCareer();
        populateBSPTable();
        busyIcon.setBusy(false);
        busyIcon.setVisible(false);
    }

    @Action
    public void searchCareer() {
        String code = txtCareerCode.getText();
        busyIcon.setVisible(true);
        busyIcon.setBusy(true);
        findCareer(code);
        populateBSPTable();
        busyIcon.setBusy(false);
        busyIcon.setVisible(false);
    }

    @Action
    public void save() {
        careerBo.setCareers(this.careers);
        careerBo.storeAll();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblCareer = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btnSave = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        txtCareerCode = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        btnSearchAll = new javax.swing.JButton();
        busyIcon = new org.jdesktop.swingx.JXBusyLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("BSP Commission Setup");

        tblCareer.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Code", "Servicing Career", "Com(%)", "Com (Fixed)"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblCareer.setColumnSelectionAllowed(true);
        tblCareer.getTableHeader().setReorderingAllowed(false);
        tblCareer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblCareerMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblCareer);
        tblCareer.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblCareer.getColumnModel().getColumn(0).setMinWidth(50);
        tblCareer.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblCareer.getColumnModel().getColumn(0).setMaxWidth(100);
        tblCareer.getColumnModel().getColumn(1).setMinWidth(250);
        tblCareer.getColumnModel().getColumn(1).setPreferredWidth(250);
        tblCareer.getColumnModel().getColumn(1).setMaxWidth(500);
        tblCareer.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblCareer.getColumnModel().getColumn(3).setMinWidth(100);
        tblCareer.getColumnModel().getColumn(3).setPreferredWidth(100);
        tblCareer.getColumnModel().getColumn(3).setMaxWidth(200);

        jLabel1.setText("Com(%)      : Vendor is giving Percentage of Sale");

        jLabel2.setText("Com(Fixed) : Vendor is giving Fixed rate of Sale");

        jLabel3.setText("Doubleclick on cell to enter com rate, Press enter when done");

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getActionMap(FrameBspCom.class, this);
        btnSave.setAction(actionMap.get("save")); // NOI18N
        btnSave.setFont(new java.awt.Font("Tahoma", 1, 11));
        btnSave.setFocusable(false);
        btnSave.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnClose.setFont(new java.awt.Font("Tahoma", 1, 11));
        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/etsbackoffice/gui/resources/icons/exit.png"))); // NOI18N
        btnClose.setText("Close");
        btnClose.setFocusable(false);
        btnClose.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnClose.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        jLabel4.setText("Career Code");

        btnSearch.setAction(actionMap.get("searchCareer")); // NOI18N
        btnSearch.setFont(new java.awt.Font("Tahoma", 1, 11));
        btnSearch.setPreferredSize(new java.awt.Dimension(100, 30));

        btnSearchAll.setAction(actionMap.get("loadCareer")); // NOI18N
        btnSearchAll.setFont(new java.awt.Font("Tahoma", 1, 11));
        btnSearchAll.setPreferredSize(new java.awt.Dimension(110, 30));

        busyIcon.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        busyIcon.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSave)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnClose)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addContainerGap(272, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(txtCareerCode, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnSearchAll, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addComponent(busyIcon, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 514, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 512, Short.MAX_VALUE)
                        .addGap(2, 2, 2))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(busyIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearchAll, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCareerCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 6, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSave, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(jLabel3)
                    .addComponent(btnClose, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
    }//GEN-LAST:event_btnSaveActionPerformed

    private void tblCareerMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCareerMouseClicked
    }//GEN-LAST:event_tblCareerMouseClicked

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                FrameBspCom dialog = new FrameBspCom(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnSearchAll;
    private org.jdesktop.swingx.JXBusyLabel busyIcon;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable tblCareer;
    private javax.swing.JTextField txtCareerCode;
    // End of variables declaration//GEN-END:variables
}
