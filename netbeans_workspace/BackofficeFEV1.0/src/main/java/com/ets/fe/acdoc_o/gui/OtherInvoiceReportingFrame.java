package com.ets.fe.acdoc_o.gui;

import com.ets.fe.Application;
import com.ets.fe.acdoc.gui.report.TSalesInvoiceReportingFrame;
import com.ets.fe.acdoc_o.model.*;
import com.ets.fe.acdoc_o.task.AccountingDocTaskOther;
import com.ets.fe.acdoc_o.task.OtherAcDocReportingTask;
import com.ets.fe.report.MyJasperReport;
import com.ets.fe.util.DateUtil;
import com.ets.fe.util.Enums;
import java.awt.*;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Window;
import java.awt.font.TextAttribute;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import org.jdesktop.swingx.JXTable;

/**
 *
 * @author Yusuf
 */
public class OtherInvoiceReportingFrame extends javax.swing.JInternalFrame implements PropertyChangeListener {

    private JDesktopPane desktopPane;
    private OtherAcDocReportingTask task;
    private AccountingDocTaskOther accountingDocTask;
    private String taskType;
    private InvoiceReportOther report;

    private List<OtherInvoiceSummery> invoices;

    private Enums.AcDocType doc_type;
    public static Enums.ClientType client_type;
    private Long client_id;
    private Date from;
    private Date to;

    public OtherInvoiceReportingFrame(JDesktopPane desktopPane) {
        this.desktopPane = desktopPane;
        initComponents();

        dtFrom.setDate(DateUtil.getBeginingOfMonth());
        dtTo.setDate(DateUtil.getEndOfMonth());
    }

    private void search() {

        taskType = "SUMMERY";
        btnSearch.setEnabled(false);

        client_type = documentSearchComponent.getContactableType();
        client_id = documentSearchComponent.getClient_id();
        from = dtFrom.getDate();
        to = dtTo.getDate();

        if (rdoDueInvoice.isSelected()) {
            doc_type = Enums.AcDocType.INVOICE;
            task = new OtherAcDocReportingTask(doc_type, client_type, client_id, from, to, progressBar);
        } else if (rdoDueRefund.isSelected()) {
            doc_type = Enums.AcDocType.REFUND;
            task = new OtherAcDocReportingTask(doc_type, client_type, client_id, from, to, progressBar);
        } else if (rdoInvHistory.isSelected()) {
            doc_type = null;
            task = new OtherAcDocReportingTask(doc_type, client_type, client_id, from, to, progressBar);
        }

        task.addPropertyChangeListener(this);
        task.execute();
    }

    private void populateSummery(InvoiceReportOther r) {
        lblInvAmount.setText(r.getTotalInvAmount());
        lblCMemo.setText(r.getTotalCMAmount());
        lblDMemo.setText(r.getTotalDMAmount());
        lblPayment.setText(r.getTotalPayment());
        lblRefund.setText(r.getTotalRefund());
        lblDue.setText(r.getTotalDue());
    }

    private void populateTable() {
        DefaultTableModel tableModel = (DefaultTableModel) tblReport.getModel();
        tableModel.getDataVector().removeAllElements();
        //tblReport.repaint();
        if (invoices.size() > 0) {
            for (int i = 0; i < invoices.size(); i++) {
                OtherInvoiceSummery s = invoices.get(i);
                String clientName = "";
                if (s.getAgent() != null) {
                    clientName = s.getAgent().getName();
                } else {
                    clientName = s.getCustomer().getFullName();
                }

                tableModel.insertRow(i, new Object[]{i + 1, s.getDocIssueDate(), s.getReference(), clientName,
                    s.getNoOfItems(), s.getCategory(), s.getRemark(),
                    s.getDocumentedAmount(), s.getPayment(), s.getOtherAmount(), s.getDue(), s.getStatus()});
            }
        } else {
            tableModel.insertRow(0, new Object[]{"", "", "", "", "", "", "", "", "", "", "", ""});
        }
        populateSummery(report);
    }

    private void _voidDocument() {
        taskType = "VOID";
        int index = tblReport.getSelectedRow();
        if (index != -1) {
            Long id = this.invoices.get(index).getId();
            OtherSalesAcDoc doc = new OtherSalesAcDoc();
            doc.setId(id);
            doc.recordUpdateBy();
            accountingDocTask = new AccountingDocTaskOther(doc, Enums.SaleType.OTHERSALES, "VOID");
            accountingDocTask.addPropertyChangeListener(this);
            accountingDocTask.execute();
        }
    }

    public void newSalesAcDocDlg(OtherSalesAcDoc acdoc) {
        Window w = SwingUtilities.getWindowAncestor(this);
        Frame owner = w instanceof Frame ? (Frame) w : null;

        int index = tblReport.getSelectedRow();
        if (index != -1) {

            OtherInvoiceSummery invSummery = report.getInvoices().get(index);

            //Convert summery to invoice
            OtherSalesAcDoc invoice = new OtherSalesAcDoc();
            invoice.setReference(invSummery.getReference());
            invoice.setId(invSummery.getId());
            invoice.setAgent(invSummery.getAgent());
            invoice.setCustomer(invSummery.getCustomer());

            acdoc.setReference(invoice.getReference());
            acdoc.setParent(invoice);
            acdoc.setDocIssueDate(new java.util.Date());

            OtherSalesAcDocumentDlg dlg = new OtherSalesAcDocumentDlg(owner);
            dlg.setLocationRelativeTo(this);
            dlg.showDialog(null, invoice);
        }
    }

    public void viewDocument(OtherInvoiceSummery invSummery) {

        Window w = SwingUtilities.getWindowAncestor(this);
        Frame owner = w instanceof Frame ? (Frame) w : null;

        if (invSummery.getType().equals(Enums.AcDocType.INVOICE)) {

            OtherInvoiceDlg dlg = new OtherInvoiceDlg(owner);
            dlg.showDialog(invSummery.getId());
        } else if (invSummery.getType().equals(Enums.AcDocType.DEBITMEMO)
                || invSummery.getType().equals(Enums.AcDocType.CREDITMEMO)) {

            OtherSalesAcDocumentDlg dlg = new OtherSalesAcDocumentDlg(owner);
            dlg.showDialog(invSummery.getId(), null);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel3 = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        btnViewReport = new javax.swing.JButton();
        btnViewInvoice = new javax.swing.JButton();
        btnEmail = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        btnSearch = new javax.swing.JButton();
        btnVoid = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        btnNewInvoice = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        documentSearchComponent = new com.ets.fe.acdoc.gui.comp.ClientSearchComp(true,true,true,Enums.AgentType.ALL);
        jSeparator2 = new javax.swing.JSeparator();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblInvAmount = new javax.swing.JLabel();
        lblCMemo = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblDMemo = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lblPayment = new javax.swing.JLabel();
        lblRefund = new javax.swing.JLabel();
        lblDue = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblReport =         new JXTable() {
            public Component prepareRenderer(TableCellRenderer renderer, int rowIndex, int vColIndex) {
                Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
                String s = this.getModel().getValueAt(rowIndex, 10).toString();
                if (s.startsWith("-")) {
                    c.setForeground(Color.red);
                } else {
                    c.setForeground(Color.GREEN);
                }
                String x = this.getModel().getValueAt(rowIndex, 11).toString();
                if(x.equalsIgnoreCase("VOID")){
                    Map  attributes = c.getFont().getAttributes();
                    attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
                    Font newFont = new Font(attributes);
                    c.setFont(newFont);
                    c.setForeground(Color.WHITE);
                }
                return c;
            }
        };
        jPanel4 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        dtFrom = new org.jdesktop.swingx.JXDatePicker();
        jLabel8 = new javax.swing.JLabel();
        dtTo = new org.jdesktop.swingx.JXDatePicker();
        rdoDueInvoice = new javax.swing.JRadioButton();
        rdoDueRefund = new javax.swing.JRadioButton();
        rdoInvHistory = new javax.swing.JRadioButton();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Other Sales Invoice: Report");

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel3.setMaximumSize(new java.awt.Dimension(32767, 24));
        jPanel3.setMinimumSize(new java.awt.Dimension(71, 24));
        jPanel3.setPreferredSize(new java.awt.Dimension(980, 24));
        jPanel3.setLayout(new java.awt.GridBagLayout());

        progressBar.setMaximumSize(new java.awt.Dimension(32767, 18));
        progressBar.setMinimumSize(new java.awt.Dimension(10, 18));
        progressBar.setPreferredSize(new java.awt.Dimension(146, 18));
        progressBar.setStringPainted(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 1, 0, 1);
        jPanel3.add(progressBar, gridBagConstraints);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanel3.add(jSeparator1, gridBagConstraints);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Message:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 2, 1, 2);
        jPanel3.add(jLabel2, gridBagConstraints);

        jPanel1.setBackground(new java.awt.Color(102, 102, 102));
        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        btnViewReport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/details18.png"))); // NOI18N
        btnViewReport.setMaximumSize(new java.awt.Dimension(40, 22));
        btnViewReport.setMinimumSize(new java.awt.Dimension(40, 22));
        btnViewReport.setPreferredSize(new java.awt.Dimension(40, 22));
        btnViewReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewReportActionPerformed(evt);
            }
        });

        btnViewInvoice.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/acdoc18.png"))); // NOI18N
        btnViewInvoice.setMaximumSize(new java.awt.Dimension(40, 22));
        btnViewInvoice.setMinimumSize(new java.awt.Dimension(40, 22));
        btnViewInvoice.setPreferredSize(new java.awt.Dimension(40, 22));
        btnViewInvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewInvoiceActionPerformed(evt);
            }
        });

        btnEmail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/email18.png"))); // NOI18N
        btnEmail.setMaximumSize(new java.awt.Dimension(40, 22));
        btnEmail.setMinimumSize(new java.awt.Dimension(40, 22));
        btnEmail.setPreferredSize(new java.awt.Dimension(40, 22));
        btnEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEmailActionPerformed(evt);
            }
        });

        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/print18.png"))); // NOI18N
        btnPrint.setMaximumSize(new java.awt.Dimension(40, 22));
        btnPrint.setMinimumSize(new java.awt.Dimension(40, 22));
        btnPrint.setPreferredSize(new java.awt.Dimension(40, 22));
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });

        btnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/search18.png"))); // NOI18N
        btnSearch.setMaximumSize(new java.awt.Dimension(40, 22));
        btnSearch.setMinimumSize(new java.awt.Dimension(40, 22));
        btnSearch.setPreferredSize(new java.awt.Dimension(40, 22));
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        btnVoid.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/void18-1.png"))); // NOI18N
        btnVoid.setMaximumSize(new java.awt.Dimension(40, 22));
        btnVoid.setMinimumSize(new java.awt.Dimension(40, 22));
        btnVoid.setPreferredSize(new java.awt.Dimension(40, 22));
        btnVoid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVoidActionPerformed(evt);
            }
        });

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/newdoc18.png"))); // NOI18N
        jButton2.setMaximumSize(new java.awt.Dimension(40, 22));
        jButton2.setMinimumSize(new java.awt.Dimension(40, 22));
        jButton2.setPreferredSize(new java.awt.Dimension(40, 22));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        btnNewInvoice.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/onewinv18.png"))); // NOI18N
        btnNewInvoice.setMaximumSize(new java.awt.Dimension(40, 22));
        btnNewInvoice.setMinimumSize(new java.awt.Dimension(40, 22));
        btnNewInvoice.setPreferredSize(new java.awt.Dimension(40, 22));
        btnNewInvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewInvoiceActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(btnViewInvoice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(btnViewReport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(btnEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(btnPrint, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(564, 564, 564)
                .addComponent(filler1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnNewInvoice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(btnVoid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSearch, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnVoid, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(filler1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnViewInvoice, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                    .addComponent(btnEmail, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                    .addComponent(btnPrint, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                    .addComponent(btnViewReport, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                    .addComponent(btnNewInvoice, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Invoice Amount:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
        jPanel2.add(jLabel1, gridBagConstraints);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Credit Memo:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(jLabel3, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Due:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        jPanel2.add(jLabel4, gridBagConstraints);

        lblInvAmount.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblInvAmount.setText("0.00");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
        jPanel2.add(lblInvAmount, gridBagConstraints);

        lblCMemo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCMemo.setText("0.00");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        jPanel2.add(lblCMemo, gridBagConstraints);

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Debit Memo:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(jLabel7, gridBagConstraints);

        lblDMemo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblDMemo.setText("0.00");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        jPanel2.add(lblDMemo, gridBagConstraints);

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Payment:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        jPanel2.add(jLabel9, gridBagConstraints);

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Refund:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        jPanel2.add(jLabel10, gridBagConstraints);

        lblPayment.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblPayment.setText("0.00");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 4);
        jPanel2.add(lblPayment, gridBagConstraints);

        lblRefund.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblRefund.setText("0.00");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        jPanel2.add(lblRefund, gridBagConstraints);

        lblDue.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblDue.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblDue.setText("0.00");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        jPanel2.add(lblDue, gridBagConstraints);

        tblReport.setBackground(new java.awt.Color(51, 51, 51));
        tblReport.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "Date", "Reference", "Invoicee", "ItemQty", "Category", "Remark", "Inv Amount", "Payment", "Other (+/-)", "Due", ""
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblReport.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        tblReport.setSelectionBackground(new java.awt.Color(255, 255, 153));
        tblReport.setSortable(false);
        tblReport.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblReport);
        if (tblReport.getColumnModel().getColumnCount() > 0) {
            tblReport.getColumnModel().getColumn(0).setMinWidth(40);
            tblReport.getColumnModel().getColumn(0).setPreferredWidth(40);
            tblReport.getColumnModel().getColumn(0).setMaxWidth(60);
            tblReport.getColumnModel().getColumn(1).setMinWidth(80);
            tblReport.getColumnModel().getColumn(1).setPreferredWidth(80);
            tblReport.getColumnModel().getColumn(1).setMaxWidth(80);
            tblReport.getColumnModel().getColumn(4).setMinWidth(65);
            tblReport.getColumnModel().getColumn(4).setPreferredWidth(65);
            tblReport.getColumnModel().getColumn(4).setMaxWidth(65);
            tblReport.getColumnModel().getColumn(6).setMinWidth(130);
            tblReport.getColumnModel().getColumn(6).setPreferredWidth(130);
            tblReport.getColumnModel().getColumn(6).setMaxWidth(160);
        }

        jLabel6.setText("Date From");

        dtFrom.setPreferredSize(new java.awt.Dimension(110, 20));

        jLabel8.setText("Date To");

        dtTo.setPreferredSize(new java.awt.Dimension(110, 20));

        buttonGroup1.add(rdoDueInvoice);
        rdoDueInvoice.setSelected(true);
        rdoDueInvoice.setText("Outstanding Invoice");

        buttonGroup1.add(rdoDueRefund);
        rdoDueRefund.setText("Outstanding Refund");

        buttonGroup1.add(rdoInvHistory);
        rdoInvHistory.setText("Invoice Hisory");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dtFrom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(dtTo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(rdoDueRefund, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel8))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(rdoDueInvoice, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(rdoInvHistory, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addGap(2, 2, 2)
                .addComponent(dtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addGap(2, 2, 2)
                .addComponent(dtTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(rdoDueInvoice)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rdoDueRefund)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rdoInvHistory)
                .addContainerGap(9, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(documentSearchComponent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(2, 2, 2))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator2)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(documentSearchComponent, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(2, 2, 2)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        search();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnViewReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewReportActionPerformed
        MyJasperReport report = new MyJasperReport();
        report.otherInvoiceReport(doc_type, client_type, client_id, from, to, "VIEW");
    }//GEN-LAST:event_btnViewReportActionPerformed

    private void btnViewInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewInvoiceActionPerformed
        int index = tblReport.getSelectedRow();
        if (index != -1) {
            viewDocument(report.getInvoices().get(index));
        }
    }//GEN-LAST:event_btnViewInvoiceActionPerformed

    private void btnVoidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVoidActionPerformed
        _voidDocument();
    }//GEN-LAST:event_btnVoidActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        newSalesAcDocDlg(new OtherSalesAcDoc());
    }//GEN-LAST:event_jButton2ActionPerformed

    private void btnEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmailActionPerformed
        String receipent = report.getEmail();
        String subject = report.getTitle().concat(" From").concat(Application.getMainAgent().getName());
        String body = report.getTitle().concat(" From").concat(Application.getMainAgent().getName());
        String refference = "report";
        if (receipent != null) {
            MyJasperReport report = new MyJasperReport(receipent, subject, body, refference);
            report.otherInvoiceReport(doc_type, client_type, client_id, from, to, "EMAIL");
        } else {
            JOptionPane.showMessageDialog(null, "No Email address", "Email", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnEmailActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        MyJasperReport report = new MyJasperReport();
        report.otherInvoiceReport(doc_type, client_type, client_id, from, to, "PRINT");
    }//GEN-LAST:event_btnPrintActionPerformed

    private void btnNewInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewInvoiceActionPerformed
        Window w = SwingUtilities.getWindowAncestor(this);
        Frame owner = w instanceof Frame ? (Frame) w : null;
        OtherInvoiceDlg dlg = new OtherInvoiceDlg(owner);
        dlg.showDialog(null);
    }//GEN-LAST:event_btnNewInvoiceActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEmail;
    private javax.swing.JButton btnNewInvoice;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnViewInvoice;
    private javax.swing.JButton btnViewReport;
    private javax.swing.JButton btnVoid;
    private javax.swing.ButtonGroup buttonGroup1;
    private com.ets.fe.acdoc.gui.comp.ClientSearchComp documentSearchComponent;
    private org.jdesktop.swingx.JXDatePicker dtFrom;
    private org.jdesktop.swingx.JXDatePicker dtTo;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblCMemo;
    private javax.swing.JLabel lblDMemo;
    private javax.swing.JLabel lblDue;
    private javax.swing.JLabel lblInvAmount;
    private javax.swing.JLabel lblPayment;
    private javax.swing.JLabel lblRefund;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JRadioButton rdoDueInvoice;
    private javax.swing.JRadioButton rdoDueRefund;
    private javax.swing.JRadioButton rdoInvHistory;
    private org.jdesktop.swingx.JXTable tblReport;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress".equals(evt.getPropertyName())) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
            if (progress == 100) {
                try {
                    report = task.get();
                    invoices = new ArrayList<>();
                    invoices = report.getInvoices();
                    populateTable();
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(TSalesInvoiceReportingFrame.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    btnSearch.setEnabled(true);
                }
            }
        }
    }
}
