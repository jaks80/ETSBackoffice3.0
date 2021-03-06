package com.ets.fe.acdoc.gui.comp;

import com.ets.fe.Application;
import com.ets.fe.acdoc_o.model.AdditionalChargeLine;
import com.ets.fe.acdoc.model.TicketingSalesAcDoc;
import com.ets.fe.os.model.AdditionalCharge;
import com.ets.fe.util.CheckInput;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 *
 * @author Yusuf
 */
public class AdditionalChargeComp extends javax.swing.JPanel {
    
    private static List<AdditionalCharge> charges = new ArrayList<>();
    private static List<AdditionalChargeLine> chargLine = new ArrayList<>();
    private static DefaultTableModel table_model = null;
    private static TicketingSalesAcDoc tInvoice = null;
    private static JComboBox cmbCharge = null;
    
    public AdditionalChargeComp() {
        initComponents();
    }
    
    public static void display(TicketingSalesAcDoc tInvoice) {
        AdditionalChargeComp.tInvoice = tInvoice;
        if (tInvoice.getId() == null) {
            initTblOtherCharge();
            populatetblAddCharge();
        } else {
            populatetblAddCharge();
        }
    }
    
    public static void populatetblAddCharge() {
        
        table_model = (DefaultTableModel) tblCharge.getModel();
        table_model.getDataVector().removeAllElements();
        tblCharge.repaint();
        
        int row = 0;
        for (AdditionalChargeLine c : chargLine) {
            table_model.insertRow(row, new Object[]{c.getAdditionalCharge().getTitle(), c.getAmount()});
            row++;
        }
        table_model.addRow(new Object[]{"", ""});
        
        table_model.addTableModelListener(new TableModelListener() {
            
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    int row = e.getLastRow();
                    int column = e.getColumn();
                    
                    if (column == 1) {
                        String chg = tblCharge.getValueAt(row, column).toString();
                        if (!chg.isEmpty() && charges.size() > row) {
                            //aServicesInAcDoc.get(row).setServiceCharge(new BigDecimal(sCharge));
                        }
                    }
                    
                    populatetblAddCharge();
                }
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblCharge = new org.jdesktop.swingx.JXTable();
        btnRemoveRow = new javax.swing.JButton();

        tblCharge.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title", "Amount"
            }
        ));
        tblCharge.setSortable(false);
        tblCharge.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblCharge);
        if (tblCharge.getColumnModel().getColumnCount() > 0) {
            tblCharge.getColumnModel().getColumn(1).setMinWidth(80);
            tblCharge.getColumnModel().getColumn(1).setPreferredWidth(80);
            tblCharge.getColumnModel().getColumn(1).setMaxWidth(80);
        }

        btnRemoveRow.setText("X");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRemoveRow)
                .addContainerGap(100, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRemoveRow))
                .addGap(0, 0, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnRemoveRow;
    private javax.swing.JScrollPane jScrollPane1;
    private static org.jdesktop.swingx.JXTable tblCharge;
    // End of variables declaration//GEN-END:variables

    private static ActionListener cmbChargeListener = new ActionListener() {
        
        public void actionPerformed(ActionEvent e) {            
            int row = tblCharge.getSelectedRow();
            int index = cmbCharge.getSelectedIndex();
            
            if (index != -1) {
                AdditionalCharge charge = charges.get(index);
                AdditionalChargeLine line = new AdditionalChargeLine();
                line.setAdditionalCharge(charge);
                line.setTicketingSalesAcDoc(tInvoice);
                line.setAmount(charge.calculateAmount(tInvoice.calculateDocumentedAmount()));
                chargLine.add(line);
                populatetblAddCharge();
            }
        }
    };
    
    private static void initTblOtherCharge() {
        charges = Application.getAdditionalCharges();
        
        table_model = (DefaultTableModel) tblCharge.getModel();
        TableColumn sTitle, sChg;
        sTitle = tblCharge.getColumnModel().getColumn(0);
        sChg = tblCharge.getColumnModel().getColumn(1);
        
        JTextField jtf = new JTextField();
        jtf.setDocument(new CheckInput(CheckInput.FLOAT));
        sChg.setCellEditor(new DefaultCellEditor(jtf));
        
        List cmbElement = new ArrayList();
        
        for (AdditionalCharge c : charges) {
            cmbElement.add(c.getTitle());
        }
        
        cmbCharge = new JComboBox();
        DefaultComboBoxModel cmbOtherSTitleModel = new DefaultComboBoxModel(cmbElement.toArray());
        cmbCharge.setModel(cmbOtherSTitleModel);
        cmbCharge.addActionListener(cmbChargeListener);
        
        sTitle.setCellEditor(new DefaultCellEditor(cmbCharge));
    }
}
