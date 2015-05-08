package com.ets.fe.acdoc_o.gui.comp;

import com.ets.fe.acdoc.bo.AcDocUtil;
import com.ets.fe.acdoc_o.gui.OtherInvoiceDlg;
import com.ets.fe.acdoc.gui.SalesInvoiceDlg;
import com.ets.fe.acdoc_o.model.AccountingDocumentLine;
import com.ets.fe.os.task.OtherServiceTask;
import com.ets.fe.os.model.OtherService;
import com.ets.fe.os.model.OtherServices;
import com.ets.fe.util.CheckInput;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 *
 * @author Yusuf
 */
public class OtherServiceComp extends javax.swing.JPanel implements PropertyChangeListener {

    private List<AccountingDocumentLine> lines = new ArrayList<>();
    private List<OtherService> services = new ArrayList<>();
    private OtherServiceTask task;
    private JComboBox cmbServices;
    private boolean editable;
    private JDialog parent;

    public OtherServiceComp() {
        initComponents();
    }

    public void setParent(JDialog parent) {
        this.parent = parent;
    }

    public void loadOtherService() {
        task = new OtherServiceTask(null,null);
        task.addPropertyChangeListener(this);
        task.execute();
    }

    public void display(List<AccountingDocumentLine> lines, boolean editable) {
        this.editable = editable;

        if (lines.isEmpty()) {
            loadOtherService();
        } else {
            this.lines = lines;
            tblServices.setEnabled(editable);
            populatetblInvLine();
        }
    }

    private void initTblServices() {
        TableColumn sTitle, gross, disc, quantity;
        sTitle = tblServices.getColumnModel().getColumn(1);
        gross = tblServices.getColumnModel().getColumn(3);
        disc = tblServices.getColumnModel().getColumn(4);
        quantity = tblServices.getColumnModel().getColumn(5);

        JTextField jtf = new JTextField();
        jtf.setDocument(new CheckInput(CheckInput.FLOAT));
        gross.setCellEditor(new DefaultCellEditor(jtf));
        disc.setCellEditor(new DefaultCellEditor(jtf));
        quantity.setCellEditor(new DefaultCellEditor(jtf));

        cmbServices = new JComboBox();

        List cmbElement = new ArrayList();

        for (OtherService s : services) {
            cmbElement.add(s.getTitle());
        }

        DefaultComboBoxModel cmbServicesModel = new DefaultComboBoxModel(cmbElement.toArray());
        cmbServices.setModel(cmbServicesModel);
        cmbServices.addActionListener(cmbServicesListener);
        sTitle.setCellEditor(new DefaultCellEditor(cmbServices));

        tblServices.getModel().addTableModelListener(tableModelListener);
    }

    private void populatetblInvLine() {
        DefaultTableModel servicesModel = (DefaultTableModel) tblServices.getModel();
        servicesModel.getDataVector().removeAllElements();
        tblServices.repaint();

        for (int i = 0; i < getLines().size(); i++) {
            AccountingDocumentLine line = getLines().get(i);
            OtherService s = line.getOtherService();

            servicesModel.insertRow(i, new Object[]{i + 1, s.getTitle(), line.getRemark(),
                line.getAmount(), line.getDiscount(), line.getQty(), line.calculateOServiceLineTotal()});
        }
        servicesModel.addRow(new Object[]{"", "", "", "", "", "", ""});
        tblServices.getSelectionModel().clearSelection();//Never ever remove it.Add where ever use table model listener             

        if (parent instanceof OtherInvoiceDlg) {
            OtherInvoiceDlg dlg = (OtherInvoiceDlg) parent;
            dlg.getInvoice().setAccountingDocumentLines(lines);
            dlg.displayBalance(dlg.getInvoice());
        }
    }

    TableModelListener tableModelListener = new TableModelListener() {

        @Override
        public void tableChanged(TableModelEvent e) {

            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getLastRow();
                int column = e.getColumn();

                if (column == 2) {
                    String remark = tblServices.getValueAt(row, column).toString();
                    getLines().get(row).setRemark(remark);
                    //populatetblInvLine();
                }

                if (column == 3) {
                    String gross = tblServices.getValueAt(row, column).toString();
                    if (!gross.isEmpty() && getLines().size() > row) {
                        BigDecimal oldVal = getLines().get(row).getAmount();
                        getLines().get(row).setAmount(new BigDecimal(gross));
                        if (!AcDocUtil.validateOtherSellingPrice(getLines().get(row))) {
                            getLines().get(row).setAmount(oldVal);
                        }
                        populatetblInvLine();
                    }
                }

                if (column == 4) {
                    String disc = tblServices.getValueAt(row, column).toString();
                    if (!disc.isEmpty() && getLines().size() > row) {
                        getLines().get(row).setDiscount(new BigDecimal(disc).negate());
                        populatetblInvLine();
                    }
                }
                if (column == 5) {
                    String unit = tblServices.getValueAt(row, column).toString();
                    if (!unit.isEmpty() && getLines().size() > row) {
                        getLines().get(row).setQty(Integer.valueOf(unit));
                        populatetblInvLine();
                    }
                }
            }
        }
    };

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblServices = new javax.swing.JTable();
        btnRemoveItem = new javax.swing.JButton();

        jScrollPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        tblServices.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        tblServices.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "Title", "Remark", "Gross", "Disc", "Qty", "NetPayable"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true, true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblServices.setSelectionBackground(new java.awt.Color(204, 204, 255));
        tblServices.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblServices);
        if (tblServices.getColumnModel().getColumnCount() > 0) {
            tblServices.getColumnModel().getColumn(0).setMinWidth(35);
            tblServices.getColumnModel().getColumn(0).setPreferredWidth(35);
            tblServices.getColumnModel().getColumn(0).setMaxWidth(35);
            tblServices.getColumnModel().getColumn(3).setMinWidth(80);
            tblServices.getColumnModel().getColumn(3).setPreferredWidth(80);
            tblServices.getColumnModel().getColumn(3).setMaxWidth(80);
            tblServices.getColumnModel().getColumn(4).setMinWidth(80);
            tblServices.getColumnModel().getColumn(4).setPreferredWidth(80);
            tblServices.getColumnModel().getColumn(4).setMaxWidth(80);
            tblServices.getColumnModel().getColumn(5).setMinWidth(35);
            tblServices.getColumnModel().getColumn(5).setPreferredWidth(35);
            tblServices.getColumnModel().getColumn(5).setMaxWidth(35);
            tblServices.getColumnModel().getColumn(6).setMinWidth(80);
            tblServices.getColumnModel().getColumn(6).setPreferredWidth(80);
            tblServices.getColumnModel().getColumn(6).setMaxWidth(80);
        }

        btnRemoveItem.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnRemoveItem.setForeground(new java.awt.Color(255, 0, 0));
        btnRemoveItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/delete18.png"))); // NOI18N
        btnRemoveItem.setToolTipText("Remove Item");
        btnRemoveItem.setPreferredSize(new java.awt.Dimension(25, 25));
        btnRemoveItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveItemActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 650, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(btnRemoveItem, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)
            .addComponent(btnRemoveItem, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnRemoveItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveItemActionPerformed
        if (editable) {
            removeItem();
        }
    }//GEN-LAST:event_btnRemoveItemActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnRemoveItem;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblServices;
    // End of variables declaration//GEN-END:variables

    private void removeItem() {
        int index = tblServices.getSelectedRow();
        if (index != -1) {
            this.lines.remove(index);
            populatetblInvLine();
        }
    }

    private ActionListener cmbServicesListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int row = tblServices.getSelectedRow();
            int cmbSTitleIndex = cmbServices.getSelectedIndex();

            if (row != -1) {
                AccountingDocumentLine line = new AccountingDocumentLine();
                line.setPurchaseAmount(services.get(cmbSTitleIndex).getPurchaseCost());
                line.setAmount(services.get(cmbSTitleIndex).getSellingPrice());
                line.setQty(1);
                line.setOtherService(services.get(cmbSTitleIndex));
                getLines().add(line);
                populatetblInvLine();
                tblServices.getSelectionModel().clearSelection();
            }
        }
    };

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress".equals(evt.getPropertyName())) {
            int progress = (Integer) evt.getNewValue();
            if (progress == 100) {
                try {
                    OtherServices otherservices = task.get();
                    services = otherservices.getList();
                    initTblServices();
                    populatetblInvLine();
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(SalesInvoiceDlg.class.getName()).log(Level.SEVERE, null, ex);
                } finally {

                }
            }
        }
    }

    public List<AccountingDocumentLine> getLines() {
        return lines;
    }
}
