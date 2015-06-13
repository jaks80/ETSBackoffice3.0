package com.ets.fe.acdoc.gui;

import com.ets.fe.Application;
import com.ets.fe.accounts.gui.logic.PaymentLogic;
import com.ets.fe.accounts.gui.payment.DlgTktSalesCreditTransfer;
import com.ets.fe.acdoc.gui.comp.AcDocHeaderComponent;
import com.ets.fe.acdoc_o.model.AdditionalChargeLine;
import com.ets.fe.accounts.model.Payment;
import com.ets.fe.acdoc.task.NewTSalesDocumentTask;
import com.ets.fe.acdoc.model.TicketingSalesAcDoc;
import com.ets.fe.acdoc.task.AccountingDocTask;
import com.ets.fe.accounts.task.PaymentTask;
import com.ets.fe.acdoc.model.report.InvoiceModel;
import com.ets.fe.client.model.Contactable;
import com.ets.fe.os.model.AdditionalCharge;
import com.ets.fe.pnr.logic.AtolLogic;
import com.ets.fe.pnr.model.Pnr;
import com.ets.fe.pnr.model.Ticket;
import com.ets.fe.report.BeanJasperReport;
import com.ets.fe.util.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Window;
import java.awt.font.TextAttribute;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import org.jdesktop.swingx.JXTable;

/**
 *
 * @author Yusuf
 */
public class SalesInvoiceDlg extends JDialog implements PropertyChangeListener {

    private NewTSalesDocumentTask newInvoiceTask;
    private AccountingDocTask accountingDocTask;
    private PaymentTask paymentTask;

    private Contactable client;
    private String taskType;
    private Pnr pnr;
    private List<Ticket> tickets;
    private TicketingSalesAcDoc tInvoice;
    private boolean allowPayment = true;

    private List<AdditionalChargeLine> chargeLines = new ArrayList<>();
    private List<AdditionalCharge> charges = Application.getAdditionalCharges();

    public SalesInvoiceDlg(Frame parent) {
        super(parent, true);
        initComponents();
        CheckInput a = new CheckInput();
        CheckInput b = new CheckInput();
        CheckInput c = new CheckInput();
        CheckInput d = new CheckInput();

        a.setNegativeAccepted(true);

        txtAmount.setDocument(a);
        txtCHFee.setDocument(b);
        txtPostage.setDocument(c);
        txtOther.setDocument(d);

        setPaymentType();
    }

    public boolean showDialog(TicketingSalesAcDoc tInvoice) {

        displayInvoice(tInvoice);
        setLocationRelativeTo(this);
        setVisible(true);
        return true;
    }

    public void showDialog(Long id) {
        loadInvoice(id);
        setLocationRelativeTo(this);
        setVisible(true);
    }

    private void displayInvoice(TicketingSalesAcDoc tInvoice) {

        this.tInvoice = tInvoice;
        this.pnr = tInvoice.getPnr();
        this.tickets = tInvoice.getTickets();
        this.chargeLines = tInvoice.getAdditionalChargeLines();

        acDocHeaderComponent.display(tInvoice);
        populateTblTicket();
        displayBalance(tInvoice);
        displayOtherCharge();
        populateTblPayment(tInvoice);
        controllComponent(tInvoice);

        if (pnr.getAgent() != null) {
            client = pnr.getAgent();
            txtAcDocFor.setText(pnr.getAgent().calculateFullName() + pnr.getAgent().getFullAddressCRSeperated());
        } else {
            client = pnr.getCustomer();
            txtAcDocFor.setText(pnr.getCustomer().calculateFullName() + pnr.getCustomer().getFullAddressCRSeperated());
        }
    }

    private void displayBalance(TicketingSalesAcDoc invoice) {

        lblSubTotal.setText(invoice.calculateTicketedSubTotal().toString());
        lblAddCharge.setText(invoice.calculateAddChargesSubTotal().toString());
        lblInvAmount.setText(invoice.calculateDocumentedAmount().toString());
        lblTotalPayment.setText(invoice.calculateTotalPayment().abs().toString());
        lblOther.setText(invoice.calculateRelatedDocBalance().toString());
        lblDue.setText(invoice.calculateDueAmount().toString());
    }

    private void displayOtherCharge() {
        if (!chargeLines.isEmpty()) {
            for (AdditionalChargeLine line : chargeLines) {
                if (line.getAdditionalCharge().getTitle().equals("Other")) {
                    txtOther.setText(line.getAmount().toString());
                } else if (line.getAdditionalCharge().getTitle().equals("Card Handling")) {
                    txtCHFee.setText(line.getAmount().toString());
                } else if (line.getAdditionalCharge().getTitle().equals("Postage")) {
                    txtPostage.setText(line.getAmount().toString());
                }
            }
        }
    }

    private void createOtherChargeLine() {
        String chFee = txtCHFee.getText();
        String postage = txtPostage.getText();
        String other = txtOther.getText();

        if (chFee != null && !chFee.isEmpty()) {
            AdditionalChargeLine line = new AdditionalChargeLine();
            line.setAmount(new BigDecimal(chFee));
            line.setAdditionalCharge(Application.getCardHandlingFee());
            chargeLines.add(line);
        }

        if (postage != null && !postage.isEmpty()) {
            AdditionalChargeLine line = new AdditionalChargeLine();
            line.setAmount(new BigDecimal(postage));
            line.setAdditionalCharge(Application.getPostage());
            chargeLines.add(line);
        }

        if (other != null && !other.isEmpty()) {
            AdditionalChargeLine line = new AdditionalChargeLine();
            line.setAmount(new BigDecimal(other));
            line.setAdditionalCharge(Application.getOther());
            chargeLines.add(line);
        }
    }

    public void createInvoice() {
        if (this.tickets.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Error creating invoice!!!"
                    + "\nPlease close this window,refresh PNR and create invoice again.", "New Invoice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        btnCreateDocument.setEnabled(false);
        createOtherChargeLine();
        tInvoice.setAdditionalChargeLines(chargeLines);

        String terms = (String) AcDocHeaderComponent.cmbTerms.getSelectedItem();

        if (!"Select".equals(terms)) {
            tInvoice.setTerms(terms);
            taskType = "CREATE";
            newInvoiceTask = new NewTSalesDocumentTask(tInvoice, progressBar);
            newInvoiceTask.addPropertyChangeListener(this);
            newInvoiceTask.execute();
        } else {
            btnCreateDocument.setEnabled(true);
        }
    }

    public void loadInvoice(Long id) {
        taskType = "COMPLETE";
        accountingDocTask = new AccountingDocTask(id, Enums.SaleType.TKTSALES, "DETAILS");
        accountingDocTask.addPropertyChangeListener(this);
        accountingDocTask.execute();
    }

    public void populateTblTicket() {
        tblTicket.clearSelection();
        DefaultTableModel model = (DefaultTableModel) tblTicket.getModel();
        model.getDataVector().removeAllElements();

        int row = 0;
        BigDecimal totalGF = new BigDecimal("0.00");
        BigDecimal totalDisc = new BigDecimal("0.00");
        BigDecimal totalAtol = new BigDecimal("0.00");
        BigDecimal totalNetPayable = new BigDecimal("0.00");

        for (Ticket t : this.tickets) {
            totalGF = totalGF.add(t.getGrossFare());
            totalDisc = totalDisc.add(t.getDiscount());
            totalAtol = totalAtol.add(t.getAtolChg());
            totalNetPayable = totalNetPayable.add(t.calculateNetSellingFare());

            model.insertRow(row, new Object[]{t.getFullPaxNameWithPaxNo(),
                t.getTktStatus(), t.getGrossFare(), t.getDiscount(), t.getAtolChg(), t.calculateNetSellingFare()});

            row++;
        }

        model.addRow(new Object[]{"Totals", "", totalGF, totalDisc, totalAtol, totalNetPayable});
    }

    private void populateTblPayment(TicketingSalesAcDoc invoice) {
        tblPayment.clearSelection();
        DefaultTableModel model = (DefaultTableModel) tblPayment.getModel();
        model.getDataVector().removeAllElements();

        int row = 0;
        for (TicketingSalesAcDoc doc : invoice.getRelatedDocuments()) {
            String remark = doc.getRemark();
            if (doc.getType().equals(Enums.AcDocType.PAYMENT) || doc.getType().equals(Enums.AcDocType.REFUND)) {
                Payment payment = doc.getPayment();
                if (payment != null) {
                    remark = doc.getPayment().getPaymentType().toString();
                    remark = remark + " / " + doc.getPayment().getRemark();
                }
            }
            model.insertRow(row, new Object[]{doc.getType(), remark, doc.getDocumentedAmount().abs(), DateUtil.dateToString(doc.getDocIssueDate())});
            row++;
        }
    }

    public void processPayment(TicketingSalesAcDoc invoice) {
        busyLabel.setText("");
        taskType = "PAYMENT";
        busyLabel.setBusy(true);
        btnSubmitPayment.setEnabled(false);
        String amountString = txtAmount.getText();
        String remark = txtRef.getText();

        if (!amountString.isEmpty() && !remark.isEmpty() && cmbTType.getSelectedIndex() > 0) {
            Enums.PaymentType type = (Enums.PaymentType) cmbTType.getSelectedItem();
            BigDecimal amount = new BigDecimal(amountString.trim());
            PaymentLogic logic = new PaymentLogic();

            if (amount.compareTo(invoice.calculateDueAmount().abs()) <= 0) {
                Payment payment = logic.processSingleTSalesPayment(amount, invoice, remark, type);
                paymentTask = new PaymentTask(payment,Enums.TaskType.CREATE);
                paymentTask.addPropertyChangeListener(this);
                paymentTask.execute();
            } else {
                busyLabel.setText("Warning! Excess payment");
                btnSubmitPayment.setEnabled(true);
            }
        } else {
            busyLabel.setText("Warning! Mandatory fields!");
            btnSubmitPayment.setEnabled(true);
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

        jPanel1 = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        acDocHeaderComponent = new com.ets.fe.acdoc.gui.comp.AcDocHeaderComponent();
        jPanel3 = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTicket = new JXTable(){
            public Component prepareRenderer(TableCellRenderer renderer,int rowIndex, int vColIndex) 
            {

                Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
                Object o = this.getModel().getValueAt(rowIndex, 1);
                String s = "";
                if(o!=null){
                    s = o.toString();
                }

                if (s.equalsIgnoreCase("BOOK") ) 
                {c.setForeground(Color.yellow);
                } else if(s.equalsIgnoreCase("ISSUE")){
                    c.setForeground(Color.green);
                }else if(s.equalsIgnoreCase("REISSUE")){
                    c.setForeground(Color.cyan);}
                else if(s.equalsIgnoreCase("REFUND")){
                    c.setForeground(Color.red);
                }else if(s.equalsIgnoreCase("VOID")){c.setForeground(Color.ORANGE);
                    Map  attributes = c.getFont().getAttributes();
                    attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
                    Font newFont = new Font(attributes);
                    c.setFont(newFont);}
                else{
                    c.setForeground(Color.WHITE);
                }
                return c;} 
        };
        jPanel9 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        txtCHFee = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtPostage = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        txtOther = new javax.swing.JTextField();
        tabPayment = new javax.swing.JTabbedPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblPayment = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        cmbTType = new javax.swing.JComboBox();
        txtAmount = new javax.swing.JTextField();
        txtRef = new javax.swing.JTextField();
        btnSubmitPayment = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        lblTDueRefund = new javax.swing.JLabel();
        btnAppyCredit = new javax.swing.JButton();
        busyLabel = new org.jdesktop.swingx.JXBusyLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        lblSubTotal = new javax.swing.JLabel();
        lblAddCharge = new javax.swing.JLabel();
        lblInvAmount = new javax.swing.JLabel();
        lblTotalPayment = new javax.swing.JLabel();
        lblDue = new javax.swing.JLabel();
        lblOther = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtAcDocFor = new javax.swing.JTextArea();
        jPanel7 = new javax.swing.JPanel();
        btnCreateDocument = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        btnEmail = new javax.swing.JButton();
        btnOfficeCopy = new javax.swing.JButton();
        btnATOLFront = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sales Invoice");
        setResizable(false);

        lblTitle.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblTitle.setText("Invoice");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(lblTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(acDocHeaderComponent, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(lblTitle)
                .addGap(2, 2, 2)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 6, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(acDocHeaderComponent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

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

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanel3.add(jSeparator2, gridBagConstraints);

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

        jTabbedPane1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        jScrollPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        tblTicket.setBackground(new java.awt.Color(0, 0, 0));
        tblTicket.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        tblTicket.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Passenger Name", "Status", "Gross Fare", "Disc", "ATOL", "NetPayable"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblTicket.setSelectionBackground(new java.awt.Color(204, 204, 255));
        tblTicket.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblTicket);
        if (tblTicket.getColumnModel().getColumnCount() > 0) {
            tblTicket.getColumnModel().getColumn(0).setMinWidth(230);
            tblTicket.getColumnModel().getColumn(3).setPreferredWidth(60);
            tblTicket.getColumnModel().getColumn(4).setPreferredWidth(40);
        }

        jTabbedPane1.addTab("Tickets", jScrollPane1);

        jLabel12.setText("Card Handling Fee:");

        txtCHFee.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        txtCHFee.setText("0.00");
        txtCHFee.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCHFeeFocusGained(evt);
            }
        });

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel13.setText("Postage:");

        txtPostage.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        txtPostage.setText("0.00");
        txtPostage.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPostageFocusGained(evt);
            }
        });

        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel14.setText("Other:");

        txtOther.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        txtOther.setText("0.00");
        txtOther.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtOtherFocusGained(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtCHFee, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                    .addComponent(txtPostage)
                    .addComponent(txtOther))
                .addContainerGap(451, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(txtCHFee, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13)
                    .addComponent(txtPostage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(txtOther, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(112, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Other Charges", jPanel9);

        tabPayment.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        //tabPayment.addChangeListener(tabPaymentListener);

        tblPayment.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        tblPayment.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Type", "Remark", "Amount", "Date"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblPayment.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(tblPayment);
        if (tblPayment.getColumnModel().getColumnCount() > 0) {
            tblPayment.getColumnModel().getColumn(0).setMinWidth(75);
            tblPayment.getColumnModel().getColumn(0).setMaxWidth(75);
            tblPayment.getColumnModel().getColumn(2).setMinWidth(80);
            tblPayment.getColumnModel().getColumn(2).setMaxWidth(80);
            tblPayment.getColumnModel().getColumn(3).setMinWidth(80);
            tblPayment.getColumnModel().getColumn(3).setMaxWidth(80);
        }

        tabPayment.addTab("Payments", jScrollPane3);

        jPanel8.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("T.Type");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        jPanel8.add(jLabel1, gridBagConstraints);

        jLabel9.setText("Amount");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        jPanel8.add(jLabel9, gridBagConstraints);

        jLabel10.setText("Remark");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        jPanel8.add(jLabel10, gridBagConstraints);

        cmbTType.setMinimumSize(new java.awt.Dimension(28, 19));
        cmbTType.setPreferredSize(new java.awt.Dimension(28, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 0, 2);
        jPanel8.add(cmbTType, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 2);
        jPanel8.add(txtAmount, gridBagConstraints);

        txtRef.setPreferredSize(new java.awt.Dimension(50, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 4, 2);
        jPanel8.add(txtRef, gridBagConstraints);

        btnSubmitPayment.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnSubmitPayment.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/payment18.png"))); // NOI18N
        btnSubmitPayment.setText("Submit");
        btnSubmitPayment.setPreferredSize(new java.awt.Dimension(135, 30));
        btnSubmitPayment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSubmitPaymentActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel8.add(btnSubmitPayment, gridBagConstraints);

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel11.setText("Available Credit:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel8.add(jLabel11, gridBagConstraints);

        lblTDueRefund.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblTDueRefund.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTDueRefund.setText("0.00");
        lblTDueRefund.setPreferredSize(new java.awt.Dimension(100, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel8.add(lblTDueRefund, gridBagConstraints);

        btnAppyCredit.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnAppyCredit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/credit18.png"))); // NOI18N
        btnAppyCredit.setText("Apply Credit");
        btnAppyCredit.setPreferredSize(new java.awt.Dimension(135, 30));
        btnAppyCredit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAppyCreditActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 2, 2);
        jPanel8.add(btnAppyCredit, gridBagConstraints);

        busyLabel.setDirection(null);
        busyLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel8.add(busyLabel, gridBagConstraints);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, 514, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabPayment.addTab("New Payment", jPanel2);

        jPanel4.setBackground(new java.awt.Color(0, 0, 0));
        jPanel4.setLayout(new java.awt.GridBagLayout());

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("SubTotal:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 0);
        jPanel4.add(jLabel3, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Add.Charge:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 0);
        jPanel4.add(jLabel4, gridBagConstraints);

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Inv.Amount:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 0);
        jPanel4.add(jLabel5, gridBagConstraints);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Payment:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 0);
        jPanel4.add(jLabel6, gridBagConstraints);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Outstanding:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 0);
        jPanel4.add(jLabel7, gridBagConstraints);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Other (+/-):");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 0);
        jPanel4.add(jLabel8, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel4.add(jSeparator4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel4.add(jSeparator3, gridBagConstraints);

        lblSubTotal.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblSubTotal.setForeground(new java.awt.Color(255, 255, 0));
        lblSubTotal.setText("0.00");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
        jPanel4.add(lblSubTotal, gridBagConstraints);

        lblAddCharge.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblAddCharge.setForeground(new java.awt.Color(255, 255, 0));
        lblAddCharge.setText("0.00");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
        jPanel4.add(lblAddCharge, gridBagConstraints);

        lblInvAmount.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblInvAmount.setForeground(new java.awt.Color(255, 255, 0));
        lblInvAmount.setText("0.00");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
        jPanel4.add(lblInvAmount, gridBagConstraints);

        lblTotalPayment.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblTotalPayment.setForeground(new java.awt.Color(51, 255, 0));
        lblTotalPayment.setText("0.00");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
        jPanel4.add(lblTotalPayment, gridBagConstraints);

        lblDue.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDue.setForeground(new java.awt.Color(255, 0, 0));
        lblDue.setText("0.00");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
        jPanel4.add(lblDue, gridBagConstraints);

        lblOther.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblOther.setForeground(new java.awt.Color(255, 255, 255));
        lblOther.setText("0.00");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
        jPanel4.add(lblOther, gridBagConstraints);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Invoice For", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        txtAcDocFor.setEditable(false);
        txtAcDocFor.setColumns(20);
        txtAcDocFor.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        txtAcDocFor.setLineWrap(true);
        txtAcDocFor.setRows(5);
        jScrollPane5.setViewportView(txtAcDocFor);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        jPanel7.setBackground(new java.awt.Color(102, 102, 102));
        jPanel7.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        btnCreateDocument.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/createInvoice.png"))); // NOI18N
        btnCreateDocument.setToolTipText("Create New Invoice");
        btnCreateDocument.setFocusable(false);
        btnCreateDocument.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCreateDocument.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCreateDocument.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateDocumentActionPerformed(evt);
            }
        });

        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/print18.png"))); // NOI18N
        btnPrint.setToolTipText("Print");
        btnPrint.setFocusable(false);
        btnPrint.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPrint.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });

        btnEmail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/email18.png"))); // NOI18N
        btnEmail.setToolTipText("Email Invoice");
        btnEmail.setFocusable(false);
        btnEmail.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEmail.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEmailActionPerformed(evt);
            }
        });

        btnOfficeCopy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/print18.png"))); // NOI18N
        btnOfficeCopy.setToolTipText("Print Office Copy");
        btnOfficeCopy.setFocusable(false);
        btnOfficeCopy.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnOfficeCopy.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnOfficeCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOfficeCopyActionPerformed(evt);
            }
        });

        btnATOLFront.setText("ATOL");
        btnATOLFront.setToolTipText("Print ATOL Certificate");
        btnATOLFront.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnATOLFrontActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnATOLFront)
                .addGap(2, 2, 2)
                .addComponent(btnCreateDocument)
                .addGap(2, 2, 2)
                .addComponent(btnPrint)
                .addGap(2, 2, 2)
                .addComponent(btnEmail)
                .addGap(2, 2, 2)
                .addComponent(btnOfficeCopy))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnCreateDocument, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnOfficeCopy, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnATOLFront, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(tabPayment)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 701, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(54, 54, 54)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
                    .addComponent(tabPayment, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCreateDocumentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateDocumentActionPerformed
        createInvoice();
    }//GEN-LAST:event_btnCreateDocumentActionPerformed

    private void btnSubmitPaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubmitPaymentActionPerformed
        processPayment(tInvoice);
    }//GEN-LAST:event_btnSubmitPaymentActionPerformed

    private void txtPostageFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPostageFocusGained
        txtPostage.selectAll();
    }//GEN-LAST:event_txtPostageFocusGained

    private void txtCHFeeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCHFeeFocusGained
        txtCHFee.selectAll();
    }//GEN-LAST:event_txtCHFeeFocusGained

    private void txtOtherFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtOtherFocusGained
        txtOther.selectAll();
    }//GEN-LAST:event_txtOtherFocusGained

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        report("PRINT");
    }//GEN-LAST:event_btnPrintActionPerformed

    private void btnEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmailActionPerformed
        String receipent = client.getEmail();
        String subject = "Invoice".concat(" From ").concat(Application.getMainAgent().getName());
        String body = "Invoice".concat(" From ").concat(Application.getMainAgent().getName());
        String refference = this.tInvoice.getReference().toString();
        if (receipent != null) {
            BeanJasperReport jasperreport = new BeanJasperReport(receipent, subject, body, refference);
            List<InvoiceModel> list = new ArrayList<>();
            InvoiceModel model = InvoiceModel.createModel(tInvoice);
            list.add(model);
            jasperreport.invoice(list, Enums.SaleType.TKTSALES, "EMAIL");
        } else {
            JOptionPane.showMessageDialog(null, "No Email address", "Email", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnEmailActionPerformed

    private void btnAppyCreditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAppyCreditActionPerformed
        Window w = SwingUtilities.getWindowAncestor(this);
        Frame owner = w instanceof Frame ? (Frame) w : null;
        DlgTktSalesCreditTransfer dlg = new DlgTktSalesCreditTransfer(owner);
        if (dlg.showDialog(tInvoice)) {
            loadInvoice(tInvoice.getId());
            resetPaymentComponent();
        }
    }//GEN-LAST:event_btnAppyCreditActionPerformed

    private void btnOfficeCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOfficeCopyActionPerformed
        report("VIEW");
    }//GEN-LAST:event_btnOfficeCopyActionPerformed

    private void btnATOLFrontActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnATOLFrontActionPerformed
        btnATOLFront.setEnabled(false);
        AtolLogic logic = new AtolLogic();
        logic.printCertificate(this.tInvoice.getPnr().getId());
    }//GEN-LAST:event_btnATOLFrontActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.ets.fe.acdoc.gui.comp.AcDocHeaderComponent acDocHeaderComponent;
    private javax.swing.JButton btnATOLFront;
    private javax.swing.JButton btnAppyCredit;
    private javax.swing.JButton btnCreateDocument;
    private javax.swing.JButton btnEmail;
    private javax.swing.JButton btnOfficeCopy;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnSubmitPayment;
    private org.jdesktop.swingx.JXBusyLabel busyLabel;
    private javax.swing.JComboBox cmbTType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblAddCharge;
    private javax.swing.JLabel lblDue;
    private javax.swing.JLabel lblInvAmount;
    private javax.swing.JLabel lblOther;
    private javax.swing.JLabel lblSubTotal;
    private javax.swing.JLabel lblTDueRefund;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTotalPayment;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JTabbedPane tabPayment;
    private javax.swing.JTable tblPayment;
    private javax.swing.JTable tblTicket;
    private javax.swing.JTextArea txtAcDocFor;
    private javax.swing.JTextField txtAmount;
    private javax.swing.JTextField txtCHFee;
    private javax.swing.JTextField txtOther;
    private javax.swing.JTextField txtPostage;
    private javax.swing.JTextField txtRef;
    // End of variables declaration//GEN-END:variables

    private void setPaymentType() {
        DefaultComboBoxModel model = new DefaultComboBoxModel(Enums.PaymentType.values());
        model.insertElementAt("Select", 0);
        cmbTType.setModel(model);
        cmbTType.setSelectedIndex(0);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress".equals(evt.getPropertyName())) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
            if (progress == 100) {
                try {
                    if ("CREATE".equals(taskType)) {
                        tInvoice = newInvoiceTask.get();
                        displayInvoice(tInvoice);
                    } else if ("PAYMENT".equals(taskType)) {
                        busyLabel.setBusy(false);
                        btnSubmitPayment.setEnabled(true);
                        loadInvoice(tInvoice.getId());
                        resetPaymentComponent();
                    } else if ("COMPLETE".equals(taskType)) {
                        tInvoice = (TicketingSalesAcDoc) accountingDocTask.get();
                        displayInvoice(tInvoice);
                        taskType = "";
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(SalesInvoiceDlg.class.getName()).log(Level.SEVERE, null, ex);
                } finally {

                }
            }
        }
    }

    private void controllComponent(TicketingSalesAcDoc tInvoice) {
        if (tInvoice.getId() == null || tInvoice.getStatus().equals(Enums.AcDocStatus.VOID)) {
            btnCreateDocument.setEnabled(true);
            btnEmail.setEnabled(false);
            btnPrint.setEnabled(false);
            btnOfficeCopy.setEnabled(false);
            btnATOLFront.setVisible(false);
            allowPayment = false;
            tabPayment.setEnabledAt(1, allowPayment);
            txtCHFee.setEditable(true);
            txtPostage.setEditable(true);
            txtOther.setEditable(true);
        } else {
            btnCreateDocument.setEnabled(false);
            btnEmail.setEnabled(true);
            btnPrint.setEnabled(true);
            btnOfficeCopy.setEnabled(true);
            txtCHFee.setEditable(false);
            txtPostage.setEditable(false);
            txtOther.setEditable(false);

            if (tInvoice.calculateDueAmount().compareTo(new BigDecimal("0.00")) == 0) {
                allowPayment = false;
                tabPayment.setEnabledAt(1, allowPayment);
                tabPayment.setSelectedIndex(0);
            } else {
                allowPayment = true;
                tabPayment.setEnabledAt(1, allowPayment);
                tabPayment.setSelectedIndex(0);
            }

            if (AtolLogic.validForATOL(tickets)) {
                btnATOLFront.setVisible(true);
            } else {
                btnATOLFront.setVisible(false);
            }
        }
    }

    private void resetPaymentComponent() {
        tabPayment.setSelectedIndex(0);
        setPaymentType();
        txtAmount.setText("");
        txtRef.setText("");
    }

    private void report(String action) {
        BeanJasperReport jasperreport = new BeanJasperReport();
        List<InvoiceModel> list = new ArrayList<>();
        InvoiceModel model = InvoiceModel.createModel(tInvoice);
        list.add(model);
        jasperreport.invoice(list, Enums.SaleType.TKTSALES, action);
    }
}
