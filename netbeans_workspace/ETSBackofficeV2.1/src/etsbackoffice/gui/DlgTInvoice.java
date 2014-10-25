package etsbackoffice.gui;

import etsbackoffice.ETSBackofficeApp;
import etsbackoffice.businesslogic.*;
import etsbackoffice.domain.*;
import etsbackoffice.report.BackofficeReporting;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import org.jdesktop.application.Action;

/**
 *
 * @author Yusuf
 */
public class DlgTInvoice extends javax.swing.JDialog {

    private boolean close;
    private boolean createDocument;
    private boolean submitNeeded;
    private boolean isEditable;
    private DefaultTableModel tktModel;
    private DefaultTableModel transModel;
    private DefaultTableModel tblAdditionalInvLineModel;
    private DefaultTableModel tblOtherInvLineModel;
    private AccountsBo accountsBo = (AccountsBo) ETSBackofficeApp.getApplication().ctx.getBean("accountsBo");
    private PNRBo pnrBo = (PNRBo) ETSBackofficeApp.getApplication().ctx.getBean("pnrBo");
    private AccountingDocBo acDocBo = (AccountingDocBo) ETSBackofficeApp.getApplication().ctx.getBean("acDocBo");
    private AcTransactionBo acTransactionBo = (AcTransactionBo) ETSBackofficeApp.getApplication().ctx.getBean("acTransactionBo");
    private OServiceBo oServiceBo = (OServiceBo) ETSBackofficeApp.getApplication().ctx.getBean("oServiceBo");
    private List<OtherService> allServices = new ArrayList();
    private List<OtherService> additionalServices = new ArrayList();
    private List<OtherService> otherServices = new ArrayList();
    private List<Ticket> tickets = new ArrayList<Ticket>();
    DefaultComboBoxModel cmbContactableModel = new DefaultComboBoxModel();
    //DefaultComboBoxModel cmbAdditionalSTitleModel;
    private Agent agent;
    private Customer customer;
    private AccountingDocument acDoc;
    private List<AcTransaction> transactions = new ArrayList<AcTransaction>();
    //private List<AccountingDocumentLine> additionalLines = new ArrayList<AccountingDocumentLine>();
    private List<AccountingDocumentLine> otherLines = new ArrayList<AccountingDocumentLine>();
    private List<Services> aServicesInAcDoc = new ArrayList();
    private List<Services> oServicesInAcDoc = new ArrayList();
    private JComboBox cmbAdditionalSTitle = new JComboBox();
    private JComboBox cmbOtherSTitle = new JComboBox();
    //private ProgressListener listenerAcDoc;
    
    private boolean atolProtected = false;
    
    public DlgTInvoice(java.awt.Frame parent) {
        super(parent, "Invoice", true);
        initComponents();
        populateCmbTType();
        initTblTicket();
        initTblAdditionalInvLine();
        initTblOtherInvLine();
    }

            private void actionRole() {        
        if (AuthenticationBo.getLoggedOnUser().getUserRole() != null && AuthenticationBo.getLoggedOnUser().getUserRole().isAdministrator()) {
            btnEdit.setEnabled(true);            
        } else {
            btnEdit.setEnabled(false);
        }
    }
    private void populateCmbTType() {
        DefaultComboBoxModel tTypeModel = new DefaultComboBoxModel(Enums.TransType.values());
        cmbTType.setModel(tTypeModel);
        cmbTType.insertItemAt("Select", 0);
        cmbTType.setSelectedIndex(0);
    }

    @Action
    public void Close() {
        close = true;
        dispose();
    }

    private void initTblTicket() {
        tktModel = (DefaultTableModel) tblTicket.getModel();
        TableColumn gFare, disc, atol;
        gFare = tblTicket.getColumnModel().getColumn(4);
        disc = tblTicket.getColumnModel().getColumn(5);
        atol = tblTicket.getColumnModel().getColumn(6);

        JTextField tktLineField = new JTextField();

        CheckInput c = new CheckInput(CheckInput.FLOAT);
        c.setNegativeAccepted(true);

        tktLineField.setDocument(c);
        gFare.setCellEditor(new DefaultCellEditor(tktLineField));
        disc.setCellEditor(new DefaultCellEditor(tktLineField));
        atol.setCellEditor(new DefaultCellEditor(tktLineField));
    }

    private void initTblAdditionalInvLine() {
        TableColumn sTitle, sChg;
        sTitle = tblAdditionalInvLine.getColumnModel().getColumn(0);
        sChg = tblAdditionalInvLine.getColumnModel().getColumn(1);

        JTextField jtf1 = new JTextField();
        CheckInput c = new CheckInput(CheckInput.FLOAT);
        c.setNegativeAccepted(true);
        jtf1.setDocument(c);
        sChg.setCellEditor(new DefaultCellEditor(jtf1));

        sTitle.setCellEditor(new DefaultCellEditor(cmbAdditionalSTitle));
    }

    private void initTblOtherInvLine() {

        tblOtherInvLineModel = (DefaultTableModel) tblOtherInvLine.getModel();
        TableColumn sTitle, sChg, disc, vat, quantity;
        sTitle = tblOtherInvLine.getColumnModel().getColumn(1);
        sChg = tblOtherInvLine.getColumnModel().getColumn(2);
        disc = tblOtherInvLine.getColumnModel().getColumn(3);
        quantity = tblOtherInvLine.getColumnModel().getColumn(4);

        JTextField jtf = new JTextField();
        jtf.setDocument(new CheckInput(CheckInput.FLOAT));
        sChg.setCellEditor(new DefaultCellEditor(jtf));
        disc.setCellEditor(new DefaultCellEditor(jtf));
        quantity.setCellEditor(new DefaultCellEditor(jtf));

        sTitle.setCellEditor(new DefaultCellEditor(cmbOtherSTitle));
    }

    private void setSubmitNeeded(boolean submitNeeded) {
        if (submitNeeded != this.submitNeeded) {
            this.submitNeeded = submitNeeded;
        }
        if (this.submitNeeded == true) {
            btnSubmit.setEnabled(true);
        } else {
            btnSubmit.setEnabled(false);
        }
    }

    private void populateFGuide(Ticket t) {
        txtFGuide.setText("");
        SimpleAttributeSet GREEN = new SimpleAttributeSet();
        SimpleAttributeSet WHITE = new SimpleAttributeSet();
        SimpleAttributeSet RED = new SimpleAttributeSet();
        StyleConstants.setForeground(GREEN, Color.GREEN);
        StyleConstants.setForeground(RED, Color.RED);
        StyleConstants.setForeground(WHITE, Color.WHITE);


        Document fGuideDoc = txtFGuide.getDocument();

        try {
            fGuideDoc.insertString(fGuideDoc.getLength(), "Base Fare : " + t.getBaseFare() + "          " + "Selling Fare : " + t.getGrossFare(), GREEN);
            fGuideDoc.insertString(fGuideDoc.getLength(), "\nTax         : " + t.getTax(), GREEN);
            fGuideDoc.insertString(fGuideDoc.getLength(), "\nCom(BSP): " + t.getBspCom() + "             " + "Com/Disc     : " + t.getDiscount(), GREEN);
            fGuideDoc.insertString(fGuideDoc.getLength(), "\nATOL      : " + t.getAtolVendor() + "             " + "ATOL          : " + t.getAtolVendor(), GREEN);
            fGuideDoc.insertString(fGuideDoc.getLength(), "\n-------------------------------------------------------------", WHITE);
            fGuideDoc.insertString(fGuideDoc.getLength(), "\nPurchase Balance  : " + t.getNetBillable() + "          " + "Selling Balance  : " + t.getNetPayble(), RED);
            fGuideDoc.insertString(fGuideDoc.getLength(), "\nRevenue current ticket: " + t.getNetPayble().subtract(t.getNetBillable()), GREEN);
        } catch (BadLocationException ex) {
            Logger.getLogger(DlgTInvoice.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
  
    private void populateTxtAgentDetails() {
        txtContactableDetails.setText(agent.getFullAddressCRSeperated());
    }

    private void populateTxtCustomerDetails() {
        txtContactableDetails.setText(customer.getFullAddressCRSeperated());
    }

    public void loadObjects(AccountingDocument acDoc) {
        this.transactions = new ArrayList(acDoc.getAcTransactions());        
        this.otherLines = new ArrayList(acDoc.getOtherServiceLine());           
        this.tickets = new ArrayList(this.acDoc.getTickets());        
    }

    public void populateComponents() {
        initControlls();
        initTransControl();

        if (this.acDoc.getPnr().getAgent() != null) {
            this.agent = acDoc.getPnr().getAgent();
            populateTxtAgentDetails();
        } else if (this.acDoc.getPnr().getCustomer() != null) {
            this.customer = acDoc.getPnr().getCustomer();
            populateTxtCustomerDetails();
        }

        populateTblTicket();
        populateInvoice();
        populateTblTransaction(this.transactions);
        populatetblAdditionalInvLine();
        populatetblOtherInvLine();

        tabsAcDocLine.setSelectedIndex(0);
        tabsAcDocLine.repaint();

        txtFGuide.setText("Select a ticket to view fare details");
        actionRole();
    }

    public boolean showDialog(AccountingDocument acDoc) {
        transactions.clear();
        otherLines.clear();
        tickets.clear();
        aServicesInAcDoc.clear();
        oServicesInAcDoc.clear();
        loadAcDoc l = new loadAcDoc(acDoc);
        l.execute();
        ETSBackofficeApp.getApplication().show(this);
        close = false;
        createDocument = false;

        if (createDocument) {
            acDoc = this.acDoc;
        }
        return createDocument;
    }

    private void populateTblTicket() {
        tabsAcDocLine.setTitleAt(0, "Ticketing Items (" + this.tickets.size() + ")");
        tktModel = (DefaultTableModel) tblTicket.getModel();
        tblTicket.clearSelection();
        tktModel.getDataVector().removeAllElements();
        int row = 0;
        atolProtected = false;
        for (Ticket ticket : this.tickets) {
            
            if(ticket.getAtolChg().compareTo(new BigDecimal("0.00"))!=0){
                this.atolProtected = true;
            }
            
            tktModel.insertRow(row, new Object[]{ticket.getFullPaxNameWithPaxNo(),
                        ticket.getTicketNo(), ticket.getTktStatusString(),
                        ticket.getDocIssuedate(), ticket.getGrossFare(),
                        ticket.getDiscount(), ticket.getAtolChg(), ticket.getNetPayble()});
            row++;
        }
        tktModel.addTableModelListener(new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    int row = e.getLastRow();
                    int column = e.getColumn();

                    if (column == 4) {
                        String newGFare = tblTicket.getValueAt(row, 4).toString().replaceAll("[^.0-9]", "");

                        if (!newGFare.isEmpty()) {
                            tickets.get(row).setGrossFare(new BigDecimal(newGFare));
                            tktModel.setValueAt(tickets.get(row).getNetPayble(), row, 7);
                            recalculateDocument();
                            populateFGuide(tickets.get(row));
                        }
                    }

                    if (column == 5) {
                        String newDisc = tblTicket.getValueAt(row, 5).toString().replaceAll("[^.0-9]", "");
                        if (!newDisc.isEmpty()) {
                            tickets.get(row).setDiscount(new BigDecimal(newDisc).negate());
                            tktModel.setValueAt(tickets.get(row).getNetPayble(), row, 7);
                            recalculateDocument();
                            populateFGuide(tickets.get(row));
                        }
                    }

                    if (column == 6) {
                        String newAtol = tblTicket.getValueAt(row, 6).toString().replaceAll("[^.0-9]", "");
                        if (!newAtol.isEmpty()) {
                            tickets.get(row).setAtolChg(new BigDecimal(newAtol));
                            tktModel.setValueAt(tickets.get(row).getNetPayble(), row, 7);
                            recalculateDocument();
                            populateFGuide(tickets.get(row));
                        }
                    }
                }
            }
        });
    }

    private void populatetblAdditionalInvLine() {
        if (!this.otherLines.isEmpty()) {
            aServicesInAcDoc = this.otherLines.iterator().next().getAdditionalServices();
        }
        
        tabsAcDocLine.setTitleAt(2, "Additional Charge (" + aServicesInAcDoc.size() + ")");

        tblAdditionalInvLineModel = (DefaultTableModel) tblAdditionalInvLine.getModel();
        tblAdditionalInvLineModel.getDataVector().removeAllElements();
        tblAdditionalInvLine.repaint();
        Iterator it = this.aServicesInAcDoc.iterator();
        int row = 0;
        while (it.hasNext()) {            
            Services s = (Services) it.next();
            tblAdditionalInvLineModel.insertRow(row, new Object[]{s.getServiceTitle(),
                        s.getServiceCharge(), s.getServiceCost()});
            row++;

        }
        tblAdditionalInvLineModel.addRow(new Object[]{"", ""});

        tblAdditionalInvLineModel.addTableModelListener(new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    int row = e.getLastRow();
                    int column = e.getColumn();

                    if (column == 1) {
                        String sCharge = tblAdditionalInvLine.getValueAt(row, column).toString();
                        if (!sCharge.isEmpty() && aServicesInAcDoc.size() > row) {
                            aServicesInAcDoc.get(row).setServiceCharge(new BigDecimal(sCharge));
                        }
                    }

                    if (column == 2) {
                        String sCost = tblAdditionalInvLine.getValueAt(row, column).toString();
                        if (!sCost.isEmpty() && aServicesInAcDoc.size() > row) {
                            aServicesInAcDoc.get(row).setServiceCost(new BigDecimal(sCost));
                        }
                    }

                    populatetblAdditionalInvLine();
                    recalculateDocument();
                }
            }
        });
    }

    private void populatetblOtherInvLine() {
        if (!this.otherLines.isEmpty()) {
            oServicesInAcDoc = this.otherLines.iterator().next().getOtherServices();
        }       
        
        tabsAcDocLine.setTitleAt(1, "Other Items (" + this.oServicesInAcDoc.size() + ")");

        tblOtherInvLineModel = (DefaultTableModel) tblOtherInvLine.getModel();
        tblOtherInvLineModel.getDataVector().removeAllElements();
        tblOtherInvLine.repaint();
        
        Iterator it = oServicesInAcDoc.iterator();
        int row = 0;
        while (it.hasNext()) {
            //AccountingDocumentLine line = (AccountingDocumentLine) it.next();
            Services s = (Services) it.next();
            tblOtherInvLineModel.insertRow(row, new Object[]{row + 1, s.getServiceTitle(),
                        s.getServiceCharge(), s.getDiscount(), s.getUnit(), s.getNetPayable()});
            row++;

        }
        tblOtherInvLineModel.addRow(new Object[]{"", "", "", "", "", ""});

        tblOtherInvLineModel.addTableModelListener(new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    int row = e.getLastRow();
                    int column = e.getColumn();

                    if (column == 2) {
                        String sCharge = tblOtherInvLine.getValueAt(row, column).toString();
                        if (!sCharge.isEmpty() && oServicesInAcDoc.size() > row) {
                            oServicesInAcDoc.get(row).setServiceCharge(new BigDecimal(sCharge));
                        }
                    }
                    if (column == 3) {
                        String disc = tblOtherInvLine.getValueAt(row, column).toString().replaceAll("[^.0-9]", "");
                        if (!disc.isEmpty() && oServicesInAcDoc.size() > row) {
                            oServicesInAcDoc.get(row).setDiscount(new BigDecimal(disc).negate());
                        }
                    }
                    if (column == 4) {
                        String unit = tblOtherInvLine.getValueAt(row, column).toString();
                        if (!unit.isEmpty() && oServicesInAcDoc.size() > row) {
                            oServicesInAcDoc.get(row).setUnit(Integer.valueOf(unit));
                        }
                    }
                    recalculateDocument();
                    populatetblOtherInvLine();
                    //populateDocument();
                }
            }
        });
    }

    private void populateTblTransaction(List<AcTransaction> transactions) {
        transModel = (DefaultTableModel) tblTransaction.getModel();
        transModel.getDataVector().removeAllElements();

        for (AcTransaction t : transactions) {
            int row = 0;
            transModel.insertRow(row, new Object[]{t.getTransDate(), t.getTransTypeString(), t.getTransAmount(), t.getTransRef()});
            row++;
        }
    }

    private void populateInvoice() {

        if (this.acDoc.getIssueDate() != null) {
            datePicker.setDate(this.acDoc.getIssueDate());
        }
        if (this.acDoc.getAcDocRef() != null) {
            txtInvRef.setText(String.valueOf(this.acDoc.getAcDocRefString()));
        }
        if (this.acDoc.getAcDocIssuedBy().getSurName() != null && acDoc.getAcDocIssuedBy().getForeName() != null) {
            txtUser.setText(this.acDoc.getAcDocIssuedBy().getSurName() + this.acDoc.getAcDocIssuedBy().getForeName());
        }
        if (this.acDoc.getTerms() != null) {
            cmbTerms.setSelectedItem(this.acDoc.getTerms());
        } else {
            cmbTerms.setSelectedIndex(-1);
        }

        lblSubTotal.setText(this.acDoc.getTktdSubTotal().add(this.acDoc.getOtherServiceSubTotal()).toString());
        lblASubTotal.setText(this.acDoc.getAdditionalServiceSubTotal().toString());
        lblInvAmount.setText(this.acDoc.getTotalDocumentedAmount().toString());
        lblReceived.setText(this.acDoc.getTotalTransactionAmount().toString());
        lblOutstanding.setText(this.acDoc.getOutstandingAmount().toString());
        lblCreditNoteAmount.setText(this.acDoc.getTotalAdm().toString());
        lblPaid.setText(this.acDoc.getTotalPaidFromCNote().toString());
        lblFinalBalance.setText(this.acDoc.getOutstandingAmount().add(this.acDoc.getTotalAdm().subtract(this.acDoc.getTotalPaidFromCNote())).toString());
    }

    private void disableComponent() {
        cmbTerms.setEnabled(false);
    }

    private void recalculateDocument() {
        BigDecimal tktSubTotal = new BigDecimal("0.00");
        BigDecimal oSubTotal = new BigDecimal("0.00");
        BigDecimal aSubTotal = new BigDecimal("0.00");
        BigDecimal transBalance = new BigDecimal("0.00");

        tktSubTotal = this.acDoc.getTktdSubTotal();        
        transBalance = this.acDoc.getTotalTransactionAmount();

        for (AccountingDocumentLine l : this.otherLines) {
            oSubTotal = oSubTotal.add(l.getOtherNetPayable());            
            aSubTotal = aSubTotal.add(l.getAdditionalNetPayable());            
        }

        lblSubTotal.setText(tktSubTotal.add(oSubTotal).toString());
        lblASubTotal.setText(aSubTotal.toString());
        lblInvAmount.setText(tktSubTotal.add(oSubTotal).add(aSubTotal).toString());
        lblCreditNoteAmount.setText(this.acDoc.getTotalAdm().toString());
        lblReceived.setText(this.acDoc.getTotalTransactionAmount().toString());
        lblOutstanding.setText(tktSubTotal.add(oSubTotal).add(aSubTotal).subtract(transBalance).toString());
    }

    private void initTransControl() {
        tabsTransaction.setSelectedIndex(0);
        if (this.acDoc.getOutstandingAmount().compareTo(new BigDecimal("0.00")) <= 0
                || this.acDoc.getIssueDate() == null) {
            tabsTransaction.setEnabledAt(1, false);
        } else if (this.acDoc.getOutstandingAmount().compareTo(new BigDecimal("0.00")) >= 0
                || this.acDoc.getIssueDate() != null) {
            tabsTransaction.setEnabledAt(1, true);
        }
    }

    private void initControlls() {
        if (this.acDoc.getIssueDate() != null) {
            disableComponent();
            isEditable = false;
            tblAdditionalInvLine.setEditable(false);
            tblOtherInvLine.setEditable(false);
            tblTicket.setEditable(false);
            btnPrint.setEnabled(true);
            btnOfficeCopy.setEnabled(true);
            btnPreview.setEnabled(true);
            btnEmail.setEnabled(true);
            btnIssueInvoice.setEnabled(false);
            btnRemoveAdChg.setEnabled(false);
            btnRemoveOService.setEnabled(false);
            btnRemoveTktLine.setEnabled(false);
        } else {
            isEditable = true;
            tblAdditionalInvLine.setEditable(true);
            tblOtherInvLine.setEditable(true);
            tblTicket.setEditable(true);
            initTblAdditionalInvLine();
            initTblOtherInvLine();
            btnPrint.setEnabled(false);
            btnOfficeCopy.setEnabled(false);
            btnPreview.setEnabled(false);
            btnEmail.setEnabled(false);
            btnIssueInvoice.setEnabled(true);
            btnRemoveAdChg.setEnabled(true);
            btnRemoveOService.setEnabled(true);
            btnRemoveTktLine.setEnabled(true);
        }
    }

    private void initAcDocInLine(AccountingDocument o, List<AccountingDocumentLine> ls) {
        for (AccountingDocumentLine l : ls) {
            l.setAccountingDocument(o);
        }
    }

    @Action
    public void createNewInvoice() {

        this.acDoc.setAcDoctype(1);
        this.acDoc.setAcDocRef(acDocBo.generateAcDocRef());
        this.acDoc.setIssueDate(new java.util.Date());

        if (!this.otherLines.isEmpty()) {
            initAcDocInLine(this.acDoc, this.otherLines);
            this.acDoc.getAccountingDocumentLines().addAll(this.otherLines);
            this.acDoc.getPnr().setServices(new LinkedHashSet(this.otherLines.iterator().next().getAllServices()));
        }

        this.acDoc.setActive(true);

        this.acDoc.addAcStatement(accountsBo.newAccountsTransactionFromSAcDoc(this.acDoc));

        if (cmbTerms.getSelectedIndex() > 0) {
            this.acDoc.setTerms(cmbTerms.getSelectedItem().toString());
        }
        if (this.acDoc.getTerms() == null) {
            JOptionPane.showMessageDialog(null, "Select Terms", "New Invoice", JOptionPane.WARNING_MESSAGE);
        } else {
            btnIssueInvoice.setEnabled(false);
            this.acDoc.setAcDocIssuedBy(AuthenticationBo.getLoggedOnUser());
            AccountingDocument tempDoc = acDocBo.findAcDocByTkt(this.acDoc.getTickets().iterator().next().getTicketId());
            if (tempDoc == null) {
                new Thread(new threadCreateInvoice()).start();
            } else {
                JOptionPane.showMessageDialog(null, "Invoice Already Exist !!!", "New Invoice", JOptionPane.WARNING_MESSAGE);
                new Thread(new threadLoadInvoice(tempDoc.getAcDocId())).start();
            }
        }
    }

    private void submitTransaction() {
        if (this.submitNeeded = true) {
            BigDecimal amount = new BigDecimal(txtTAmount.getText());
            BigDecimal totalDocAmount = this.acDoc.getTotalDocumentedAmount();
            BigDecimal totalreceived = this.acDoc.getTotalTransactionAmount();
            String tRef = txtTRef.getText();

            if (amount.compareTo(totalDocAmount.subtract(totalreceived)) != 1) {
                AcTransaction t = new AcTransaction();
                t.setTransType(Enums.TransType.valueOf(cmbTType.getSelectedItem().toString()).getId());
                t.setTransAmount(amount);
                t.setTransDate(new java.util.Date());
                t.setTransRef(tRef);
                t.setUser(AuthenticationBo.getLoggedOnUser());
                t.setActive(true);
                t.setAccountingDocument(this.acDoc);
                t.setInvoice(this.acDoc);
                t.setPnr(this.acDoc.getPnr());
                t.addAcStatement(accountsBo.newAccountsTransactionFromSAcTransaction(t));
                new Thread(new threadSubmitTransaction(t)).start();
                setSubmitNeeded(false);
            } else {
                JOptionPane.showMessageDialog(null, "Payment can not be more then invoice amount", "Transaction", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void save() {
        //initAcDocInLine(this.acDoc, this.additionalLines);
        initAcDocInLine(this.acDoc, this.otherLines);
        this.acDoc.setAcDocModifiedBy(AuthenticationBo.getLoggedOnUser());
        this.acDoc.setLastModifiedDate(new java.util.Date());

        for (AccountingDocumentLine a : this.otherLines) {            
            if (a.getAcDocLineId() == 0) {
                this.acDoc.addLine(a);
            }
        }

        PNR pnr = new PNR();
        Set<AccountingDocument> acDocs = new LinkedHashSet();
        pnr = acDoc.getPnr();
        acDocs.add(acDoc);
        pnr.setAccountingDocuments(acDocs);
        pnr.setTickets(new LinkedHashSet(this.tickets));
        pnr.setServices(new LinkedHashSet(this.otherLines.iterator().next().getServices()));
        pnrBo.setPnr(pnr);
        pnrBo.savePnr();

        //new Thread(new threadLoadInvoice(this.acDoc.getAcDocId())).start();
        loadAcDoc l = new loadAcDoc(acDoc);
        l.execute();
    }
    
    private ActionListener cmbAdditionalSTitleListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            JComboBox cb = (JComboBox) e.getSource();
            int row = tblAdditionalInvLine.getSelectedRow();
            int cmbSTitleIndex = cb.getSelectedIndex();

            if (row != -1) {
                Services newOService = new Services();
                newOService.setServiceTitle(additionalServices.get(cmbSTitleIndex).getServiceTitle());
                newOService.setServiceCharge(additionalServices.get(cmbSTitleIndex).getFinalServiceCharge(acDoc.getTotalDocumentedAmount()));
                newOService.setServiceType(2);
                newOService.setUnit(1);
                newOService.setPnr(acDoc.getPnr());
                newOService.setOtherService(additionalServices.get(cmbSTitleIndex));

                if (!otherLines.isEmpty()) {
                    otherLines.iterator().next().addService(newOService);                      
                } else {
                    AccountingDocumentLine l = new AccountingDocumentLine();
                    l.setAccountingDocument(acDoc);
                    l.setType(2);
                    l.addService(newOService);
                    otherLines.add(l);                    
                    //otherLines.iterator().next().addService(newOService);
                }
            }
            recalculateDocument();
            populatetblAdditionalInvLine();
        }
    };
    
    private ActionListener cmbOtherSTitleListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            JComboBox cb = (JComboBox) e.getSource();
            int row = tblOtherInvLine.getSelectedRow();
            int cmbSTitleIndex = cb.getSelectedIndex();

            if (row != -1) {
                Services newOService = new Services();
                newOService.setServiceTitle(otherServices.get(cmbSTitleIndex).getServiceTitle());
                newOService.setServiceCharge(otherServices.get(cmbSTitleIndex).getServiceCharge());
                newOService.setServiceCost(otherServices.get(cmbSTitleIndex).getServiceCost());
                newOService.setOtherService(otherServices.get(cmbSTitleIndex));
                newOService.setUnit(1);
                newOService.setServiceType(1);
                newOService.setPnr(acDoc.getPnr());
                //oServicesInAcDoc.iterator().next()(newOService);
                if (!otherLines.isEmpty()) {
                    otherLines.iterator().next().addService(newOService);
                } else {
                    AccountingDocumentLine l = new AccountingDocumentLine();
                    l.setAccountingDocument(acDoc);
                    l.setType(2);
                    otherLines.add(l);
                    otherLines.iterator().next().addService(newOService);
                }
            }

            populatetblOtherInvLine();
            recalculateDocument();
        }
    };
    private ListSelectionListener tblTicketListener = new ListSelectionListener() {

        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int selectedRow = tblTicket.getSelectedRow();
            if (selectedRow != -1) {
                populateFGuide(tickets.get(selectedRow));
            }
        }
    };
    private ChangeListener tabsAcDocLineListener = new ChangeListener() {

        public void stateChanged(ChangeEvent e) {

            if (tabsAcDocLine.getSelectedIndex() == 0) {
            } else if (tabsAcDocLine.getSelectedIndex() == 1 && allServices.isEmpty() && isEditable == true) {
                PopulateCmbServiceTitle p = new PopulateCmbServiceTitle();
                p.execute();
            } else if (tabsAcDocLine.getSelectedIndex() == 2 && allServices.isEmpty() && isEditable == true) {
                PopulateCmbServiceTitle p = new PopulateCmbServiceTitle();
                p.execute();
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
        jLabel11 = new javax.swing.JLabel();
        jToolBar1 = new javax.swing.JToolBar();
        btnIssueInvoice = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        btnPreview = new javax.swing.JButton();
        btnOfficeCopy = new javax.swing.JButton();
        btnEmail = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        pnlStatus = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        //listenerAcDoc = new ProgressListener(progressBar);
        jSeparator2 = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        pnlMain = new javax.swing.JPanel();
        pnlHeader = new javax.swing.JPanel();
        pnlInvoiceFor = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtContactableDetails = new javax.swing.JTextArea();
        lblDocFor = new javax.swing.JLabel();
        pnInvlHeader = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtInvRef = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtUser = new javax.swing.JTextField();
        cmbTerms = new javax.swing.JComboBox();
        datePicker = new org.jdesktop.swingx.JXDatePicker();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtFGuide = new javax.swing.JTextPane();
        pnlInvLine = new javax.swing.JPanel();
        tabsAcDocLine = new javax.swing.JTabbedPane();
        pnlTktLine = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblTicket = new org.jdesktop.swingx.JXTable();
        tblTicket.getSelectionModel().addListSelectionListener(tblTicketListener);
        jToolBar2 = new javax.swing.JToolBar();
        btnRemoveTktLine = new javax.swing.JButton();
        pnlOServiceLine = new javax.swing.JPanel();
        jToolBar4 = new javax.swing.JToolBar();
        btnRemoveOService = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        tblOtherInvLine = new org.jdesktop.swingx.JXTable();
        pnlAServiceLine = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblAdditionalInvLine = new org.jdesktop.swingx.JXTable();
        jToolBar3 = new javax.swing.JToolBar();
        btnRemoveAdChg = new javax.swing.JButton();
        pnlTransaction = new javax.swing.JPanel();
        tabsTransaction = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTransaction = new org.jdesktop.swingx.JXTable();
        jPanel4 = new javax.swing.JPanel();
        lblTType = new javax.swing.JLabel();
        cmbTType = new javax.swing.JComboBox();
        txtTAmount = new javax.swing.JTextField();
        lblTAmount = new javax.swing.JLabel();
        lblTRef = new javax.swing.JLabel();
        txtTRef = new javax.swing.JTextField();
        busyIcon = new org.jdesktop.swingx.JXBusyLabel();
        btnSubmit = new javax.swing.JButton();
        lblTOutRefund = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btnApplyCredit = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        lblReceived1 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblSubTotal = new javax.swing.JLabel();
        lblASubTotal = new javax.swing.JLabel();
        lblInvAmount = new javax.swing.JLabel();
        lblReceived = new javax.swing.JLabel();
        lblOutstanding = new javax.swing.JLabel();
        lblCreditNoteAmount = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        lblPaid = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lblFinalBalance = new javax.swing.JLabel();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getResourceMap(DlgTInvoice.class);
        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(700, 440));
        setName("Form"); // NOI18N
        setResizable(false);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jToolBar1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getActionMap(DlgTInvoice.class, this);
        btnIssueInvoice.setAction(actionMap.get("createNewInvoice")); // NOI18N
        btnIssueInvoice.setFont(resourceMap.getFont("btnEmail.font")); // NOI18N
        btnIssueInvoice.setIcon(resourceMap.getIcon("btnIssueInvoice.icon")); // NOI18N
        btnIssueInvoice.setText(resourceMap.getString("btnIssueInvoice.text")); // NOI18N
        btnIssueInvoice.setToolTipText(resourceMap.getString("btnIssueInvoice.toolTipText")); // NOI18N
        btnIssueInvoice.setFocusable(false);
        btnIssueInvoice.setName("btnIssueInvoice"); // NOI18N
        btnIssueInvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIssueInvoiceActionPerformed(evt);
            }
        });
        jToolBar1.add(btnIssueInvoice);

        btnPrint.setAction(actionMap.get("printAcDoc")); // NOI18N
        btnPrint.setFont(resourceMap.getFont("btnEmail.font")); // NOI18N
        btnPrint.setIcon(resourceMap.getIcon("btnPrint.icon")); // NOI18N
        btnPrint.setText(resourceMap.getString("btnPrint.text")); // NOI18N
        btnPrint.setToolTipText(resourceMap.getString("btnPrint.toolTipText")); // NOI18N
        btnPrint.setFocusable(false);
        btnPrint.setName("btnPrint"); // NOI18N
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPrint);

        btnPreview.setFont(resourceMap.getFont("btnEmail.font")); // NOI18N
        btnPreview.setIcon(resourceMap.getIcon("btnPreview.icon")); // NOI18N
        btnPreview.setText(resourceMap.getString("btnPreview.text")); // NOI18N
        btnPreview.setToolTipText(resourceMap.getString("btnPreview.toolTipText")); // NOI18N
        btnPreview.setFocusable(false);
        btnPreview.setName("btnPreview"); // NOI18N
        btnPreview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviewActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPreview);

        btnOfficeCopy.setAction(actionMap.get("printAcDocOfficeCopy")); // NOI18N
        btnOfficeCopy.setFont(resourceMap.getFont("btnEmail.font")); // NOI18N
        btnOfficeCopy.setIcon(resourceMap.getIcon("btnOfficeCopy.icon")); // NOI18N
        btnOfficeCopy.setText(resourceMap.getString("btnOfficeCopy.text")); // NOI18N
        btnOfficeCopy.setFocusable(false);
        btnOfficeCopy.setName("btnOfficeCopy"); // NOI18N
        btnOfficeCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOfficeCopyActionPerformed(evt);
            }
        });
        jToolBar1.add(btnOfficeCopy);

        btnEmail.setFont(resourceMap.getFont("btnEmail.font")); // NOI18N
        btnEmail.setIcon(resourceMap.getIcon("btnEmail.icon")); // NOI18N
        btnEmail.setText(resourceMap.getString("btnEmail.text")); // NOI18N
        btnEmail.setToolTipText(resourceMap.getString("btnEmail.toolTipText")); // NOI18N
        btnEmail.setFocusable(false);
        btnEmail.setName("btnEmail"); // NOI18N
        btnEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEmailActionPerformed(evt);
            }
        });
        jToolBar1.add(btnEmail);

        btnEdit.setFont(resourceMap.getFont("btnEdit.font")); // NOI18N
        btnEdit.setIcon(resourceMap.getIcon("btnEdit.icon")); // NOI18N
        btnEdit.setText(resourceMap.getString("btnEdit.text")); // NOI18N
        btnEdit.setFocusable(false);
        btnEdit.setName("btnEdit"); // NOI18N
        btnEdit.setPreferredSize(new java.awt.Dimension(63, 31));
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });
        jToolBar1.add(btnEdit);

        btnUpdate.setFont(resourceMap.getFont("btnUpdate.font")); // NOI18N
        btnUpdate.setIcon(resourceMap.getIcon("btnUpdate.icon")); // NOI18N
        btnUpdate.setText(resourceMap.getString("btnUpdate.text")); // NOI18N
        btnUpdate.setEnabled(false);
        btnUpdate.setFocusable(false);
        btnUpdate.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnUpdate.setName("btnUpdate"); // NOI18N
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });
        jToolBar1.add(btnUpdate);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        getContentPane().add(jToolBar1, gridBagConstraints);

        pnlStatus.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlStatus.setName("pnlStatus"); // NOI18N
        pnlStatus.setLayout(new java.awt.GridBagLayout());

        progressBar.setName("progressBar"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlStatus.add(progressBar, gridBagConstraints);

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
        pnlStatus.add(jSeparator2, gridBagConstraints);

        statusMessageLabel.setFont(resourceMap.getFont("statusMessageLabel.font")); // NOI18N
        statusMessageLabel.setText(resourceMap.getString("statusMessageLabel.text")); // NOI18N
        statusMessageLabel.setName("statusMessageLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 0);
        pnlStatus.add(statusMessageLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        getContentPane().add(pnlStatus, gridBagConstraints);

        pnlMain.setBackground(resourceMap.getColor("pnlMain.background")); // NOI18N
        pnlMain.setName("pnlMain"); // NOI18N
        pnlMain.setLayout(new java.awt.GridBagLayout());

        pnlHeader.setName("pnlHeader"); // NOI18N
        pnlHeader.setOpaque(false);

        pnlInvoiceFor.setName("pnlInvoiceFor"); // NOI18N
        pnlInvoiceFor.setOpaque(false);
        pnlInvoiceFor.setLayout(new java.awt.GridBagLayout());

        jScrollPane4.setName("jScrollPane4"); // NOI18N
        jScrollPane4.setPreferredSize(new java.awt.Dimension(220, 76));

        txtContactableDetails.setColumns(16);
        txtContactableDetails.setEditable(false);
        txtContactableDetails.setFont(resourceMap.getFont("txtContactableDetails.font")); // NOI18N
        txtContactableDetails.setLineWrap(true);
        txtContactableDetails.setRows(5);
        txtContactableDetails.setName("txtContactableDetails"); // NOI18N
        jScrollPane4.setViewportView(txtContactableDetails);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlInvoiceFor.add(jScrollPane4, gridBagConstraints);

        lblDocFor.setFont(resourceMap.getFont("lblDocFor.font")); // NOI18N
        lblDocFor.setText(resourceMap.getString("lblDocFor.text")); // NOI18N
        lblDocFor.setName("lblDocFor"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        pnlInvoiceFor.add(lblDocFor, gridBagConstraints);

        pnInvlHeader.setBackground(resourceMap.getColor("pnInvlHeader.background")); // NOI18N
        pnInvlHeader.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnInvlHeader.setName("pnInvlHeader"); // NOI18N

        jPanel2.setBackground(resourceMap.getColor("jPanel2.background")); // NOI18N
        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new java.awt.GridBagLayout());

        lblTitle.setFont(resourceMap.getFont("lblTitle.font")); // NOI18N
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblTitle.setText(resourceMap.getString("lblTitle.text")); // NOI18N
        lblTitle.setForeground(resourceMap.getColor("lblTitle.foreground")); // NOI18N
        lblTitle.setName("lblTitle"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(lblTitle, gridBagConstraints);

        jLabel1.setFont(resourceMap.getFont("txtUser.font")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(jLabel1, gridBagConstraints);

        jLabel2.setFont(resourceMap.getFont("txtUser.font")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(jLabel2, gridBagConstraints);

        jLabel4.setFont(resourceMap.getFont("txtUser.font")); // NOI18N
        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(jLabel4, gridBagConstraints);

        txtInvRef.setBackground(resourceMap.getColor("txtUser.background")); // NOI18N
        txtInvRef.setEditable(false);
        txtInvRef.setFont(resourceMap.getFont("txtUser.font")); // NOI18N
        txtInvRef.setText(resourceMap.getString("txtInvRef.text")); // NOI18N
        txtInvRef.setName("txtInvRef"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(txtInvRef, gridBagConstraints);

        jLabel6.setFont(resourceMap.getFont("txtUser.font")); // NOI18N
        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(jLabel6, gridBagConstraints);

        txtUser.setBackground(resourceMap.getColor("txtUser.background")); // NOI18N
        txtUser.setEditable(false);
        txtUser.setFont(resourceMap.getFont("txtUser.font")); // NOI18N
        txtUser.setText(resourceMap.getString("txtUser.text")); // NOI18N
        txtUser.setName("txtUser"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(txtUser, gridBagConstraints);

        cmbTerms.setFont(resourceMap.getFont("txtUser.font")); // NOI18N
        cmbTerms.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Select", "CIA", "COD", "CWO", "Net monthly account", "Net 7", "Net 10", "Net 30", "Net 60", "Net 90" }));
        cmbTerms.setName("cmbTerms"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(cmbTerms, gridBagConstraints);

        datePicker.setFont(resourceMap.getFont("txtUser.font")); // NOI18N
        datePicker.setEditable(false);
        datePicker.setName("datePicker"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(datePicker, gridBagConstraints);

        javax.swing.GroupLayout pnInvlHeaderLayout = new javax.swing.GroupLayout(pnInvlHeader);
        pnInvlHeader.setLayout(pnInvlHeaderLayout);
        pnInvlHeaderLayout.setHorizontalGroup(
            pnInvlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        pnInvlHeaderLayout.setVerticalGroup(
            pnInvlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        txtFGuide.setBackground(resourceMap.getColor("txtFGuide.background")); // NOI18N
        txtFGuide.setFont(resourceMap.getFont("txtFGuide.font")); // NOI18N
        txtFGuide.setForeground(resourceMap.getColor("txtFGuide.foreground")); // NOI18N
        txtFGuide.setName("txtFGuide"); // NOI18N
        jScrollPane2.setViewportView(txtFGuide);

        javax.swing.GroupLayout pnlHeaderLayout = new javax.swing.GroupLayout(pnlHeader);
        pnlHeader.setLayout(pnlHeaderLayout);
        pnlHeaderLayout.setHorizontalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlHeaderLayout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(pnlInvoiceFor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(pnInvlHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pnlHeaderLayout.setVerticalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlInvoiceFor, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
            .addComponent(pnInvlHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlMain.add(pnlHeader, gridBagConstraints);

        pnlInvLine.setName("pnlInvLine"); // NOI18N
        pnlInvLine.setOpaque(false);

        tabsAcDocLine.addChangeListener(tabsAcDocLineListener);
        tabsAcDocLine.setFont(resourceMap.getFont("tabsAcDocLine.font")); // NOI18N
        tabsAcDocLine.setName("tabsAcDocLine"); // NOI18N

        pnlTktLine.setName("pnlTktLine"); // NOI18N

        jScrollPane5.setName("jScrollPane5"); // NOI18N
        jScrollPane5.setPreferredSize(new java.awt.Dimension(650, 150));

        tblTicket.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "PaxName", "TktNo", "Status", "Date", "SellingFare", "Disc/Com", "ATOL", "NetPayable"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true, true, true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblTicket.setFont(resourceMap.getFont("tblTicket.font")); // NOI18N
        tblTicket.setName("tblTicket"); // NOI18N
        tblTicket.setSortable(false);
        tblTicket.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(tblTicket);
        tblTicket.getColumnModel().getColumn(0).setMinWidth(240);
        tblTicket.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblTicket.columnModel.title0")); // NOI18N
        tblTicket.getColumnModel().getColumn(1).setMinWidth(100);
        tblTicket.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblTicket.columnModel.title1")); // NOI18N
        tblTicket.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblTicket.columnModel.title2")); // NOI18N
        tblTicket.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblTicket.columnModel.title3")); // NOI18N
        tblTicket.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("tblTicket.columnModel.title4")); // NOI18N
        tblTicket.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("tblTicket.columnModel.title5")); // NOI18N
        tblTicket.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("tblTicket.columnModel.title6")); // NOI18N
        tblTicket.getColumnModel().getColumn(7).setHeaderValue(resourceMap.getString("tblTicket.columnModel.title7")); // NOI18N

        jToolBar2.setFloatable(false);
        jToolBar2.setOrientation(1);
        jToolBar2.setRollover(true);
        jToolBar2.setName("jToolBar2"); // NOI18N

        btnRemoveTktLine.setIcon(resourceMap.getIcon("btnRemoveTktLine.icon")); // NOI18N
        btnRemoveTktLine.setText(resourceMap.getString("btnRemoveTktLine.text")); // NOI18N
        btnRemoveTktLine.setToolTipText(resourceMap.getString("btnRemoveTktLine.toolTipText")); // NOI18N
        btnRemoveTktLine.setFocusable(false);
        btnRemoveTktLine.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRemoveTktLine.setName("btnRemoveTktLine"); // NOI18N
        btnRemoveTktLine.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRemoveTktLine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveTktLineActionPerformed(evt);
            }
        });
        jToolBar2.add(btnRemoveTktLine);

        javax.swing.GroupLayout pnlTktLineLayout = new javax.swing.GroupLayout(pnlTktLine);
        pnlTktLine.setLayout(pnlTktLineLayout);
        pnlTktLineLayout.setHorizontalGroup(
            pnlTktLineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTktLineLayout.createSequentialGroup()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 830, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlTktLineLayout.setVerticalGroup(
            pnlTktLineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTktLineLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(pnlTktLineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabsAcDocLine.addTab(resourceMap.getString("pnlTktLine.TabConstraints.tabTitle"), pnlTktLine); // NOI18N

        pnlOServiceLine.setToolTipText(resourceMap.getString("pnlOServiceLine.toolTipText")); // NOI18N
        pnlOServiceLine.setName("pnlOServiceLine"); // NOI18N

        jToolBar4.setFloatable(false);
        jToolBar4.setOrientation(1);
        jToolBar4.setRollover(true);
        jToolBar4.setBorder(null);
        jToolBar4.setName("jToolBar4"); // NOI18N
        jToolBar4.setToolTipText(resourceMap.getString("jToolBar4.toolTipText")); // NOI18N

        btnRemoveOService.setFont(resourceMap.getFont("btnRemoveOService.font")); // NOI18N
        btnRemoveOService.setIcon(resourceMap.getIcon("btnRemoveOService.icon")); // NOI18N
        btnRemoveOService.setToolTipText(resourceMap.getString("btnRemoveOService.toolTipText")); // NOI18N
        btnRemoveOService.setFocusable(false);
        btnRemoveOService.setName("btnRemoveOService"); // NOI18N
        btnRemoveOService.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveOServiceActionPerformed(evt);
            }
        });
        jToolBar4.add(btnRemoveOService);

        jScrollPane6.setBorder(null);
        jScrollPane6.setName("jScrollPane6"); // NOI18N
        jScrollPane6.setPreferredSize(new java.awt.Dimension(515, 25));

        tblOtherInvLine.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "", "Service", "GrossCharge", "Disc", "Quantity", "Net Payable"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true, true, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblOtherInvLine.setEditable(false);
        tblOtherInvLine.setFont(resourceMap.getFont("tblOtherInvLine.font")); // NOI18N
        tblOtherInvLine.setMinimumSize(new java.awt.Dimension(310, 54));
        tblOtherInvLine.setName("tblOtherInvLine"); // NOI18N
        tblOtherInvLine.setPreferredSize(new java.awt.Dimension(525, 18));
        tblOtherInvLine.setSortable(false);
        tblOtherInvLine.getTableHeader().setReorderingAllowed(false);
        jScrollPane6.setViewportView(tblOtherInvLine);
        tblOtherInvLine.getColumnModel().getColumn(0).setPreferredWidth(40);
        tblOtherInvLine.getColumnModel().getColumn(0).setMaxWidth(40);
        tblOtherInvLine.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblOtherInvLine.columnModel.title0")); // NOI18N
        tblOtherInvLine.getColumnModel().getColumn(1).setPreferredWidth(250);
        tblOtherInvLine.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblOtherInvLine.columnModel.title1")); // NOI18N
        tblOtherInvLine.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblOtherInvLine.columnModel.title2")); // NOI18N
        tblOtherInvLine.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblOtherInvLine.columnModel.title3")); // NOI18N
        tblOtherInvLine.getColumnModel().getColumn(4).setPreferredWidth(70);
        tblOtherInvLine.getColumnModel().getColumn(4).setMaxWidth(70);
        tblOtherInvLine.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("tblOtherInvLine.columnModel.title4")); // NOI18N
        tblOtherInvLine.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("tblOtherInvLine.columnModel.title5")); // NOI18N

        javax.swing.GroupLayout pnlOServiceLineLayout = new javax.swing.GroupLayout(pnlOServiceLine);
        pnlOServiceLine.setLayout(pnlOServiceLineLayout);
        pnlOServiceLineLayout.setHorizontalGroup(
            pnlOServiceLineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlOServiceLineLayout.createSequentialGroup()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 833, Short.MAX_VALUE)
                .addGap(4, 4, 4)
                .addComponent(jToolBar4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pnlOServiceLineLayout.setVerticalGroup(
            pnlOServiceLineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOServiceLineLayout.createSequentialGroup()
                .addGroup(pnlOServiceLineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jToolBar4, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE))
                .addGap(11, 11, 11))
        );

        tabsAcDocLine.addTab(resourceMap.getString("pnlOServiceLine.TabConstraints.tabTitle"), pnlOServiceLine); // NOI18N

        pnlAServiceLine.setName("pnlAServiceLine"); // NOI18N

        jScrollPane3.setMinimumSize(new java.awt.Dimension(19, 70));
        jScrollPane3.setName("jScrollPane3"); // NOI18N
        jScrollPane3.setPreferredSize(new java.awt.Dimension(157, 150));

        tblAdditionalInvLine.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null}
            },
            new String [] {
                "Service Title", "Charge", "Cost"
            }
        ));
        tblAdditionalInvLine.setToolTipText(resourceMap.getString("tblAdditionalInvLine.toolTipText")); // NOI18N
        tblAdditionalInvLine.setEditable(false);
        tblAdditionalInvLine.setFont(resourceMap.getFont("tblAdditionalInvLine.font")); // NOI18N
        tblAdditionalInvLine.setName("tblAdditionalInvLine"); // NOI18N
        tblAdditionalInvLine.setSortable(false);
        tblAdditionalInvLine.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(tblAdditionalInvLine);
        tblAdditionalInvLine.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblAdditionalInvLine.columnModel.title0")); // NOI18N
        tblAdditionalInvLine.getColumnModel().getColumn(1).setMinWidth(60);
        tblAdditionalInvLine.getColumnModel().getColumn(1).setPreferredWidth(60);
        tblAdditionalInvLine.getColumnModel().getColumn(1).setMaxWidth(60);
        tblAdditionalInvLine.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblAdditionalInvLine.columnModel.title1")); // NOI18N
        tblAdditionalInvLine.getColumnModel().getColumn(2).setMinWidth(60);
        tblAdditionalInvLine.getColumnModel().getColumn(2).setPreferredWidth(60);
        tblAdditionalInvLine.getColumnModel().getColumn(2).setMaxWidth(60);
        tblAdditionalInvLine.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblAdditionalInvLine.columnModel.title2")); // NOI18N

        jToolBar3.setFloatable(false);
        jToolBar3.setOrientation(1);
        jToolBar3.setRollover(true);
        jToolBar3.setBorder(null);
        jToolBar3.setName("jToolBar3"); // NOI18N
        jToolBar3.setToolTipText(resourceMap.getString("jToolBar3.toolTipText")); // NOI18N

        btnRemoveAdChg.setFont(resourceMap.getFont("btnRemoveAdChg.font")); // NOI18N
        btnRemoveAdChg.setIcon(resourceMap.getIcon("btnRemoveAdChg.icon")); // NOI18N
        btnRemoveAdChg.setToolTipText(resourceMap.getString("btnRemoveAdChg.toolTipText")); // NOI18N
        btnRemoveAdChg.setFocusable(false);
        btnRemoveAdChg.setName("btnRemoveAdChg"); // NOI18N
        btnRemoveAdChg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveAdChgActionPerformed(evt);
            }
        });
        jToolBar3.add(btnRemoveAdChg);

        javax.swing.GroupLayout pnlAServiceLineLayout = new javax.swing.GroupLayout(pnlAServiceLine);
        pnlAServiceLine.setLayout(pnlAServiceLineLayout);
        pnlAServiceLineLayout.setHorizontalGroup(
            pnlAServiceLineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAServiceLineLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(479, 479, 479))
        );
        pnlAServiceLineLayout.setVerticalGroup(
            pnlAServiceLineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
            .addComponent(jToolBar3, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
        );

        tabsAcDocLine.addTab(resourceMap.getString("pnlAServiceLine.TabConstraints.tabTitle"), pnlAServiceLine); // NOI18N

        javax.swing.GroupLayout pnlInvLineLayout = new javax.swing.GroupLayout(pnlInvLine);
        pnlInvLine.setLayout(pnlInvLineLayout);
        pnlInvLineLayout.setHorizontalGroup(
            pnlInvLineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabsAcDocLine, javax.swing.GroupLayout.PREFERRED_SIZE, 873, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        pnlInvLineLayout.setVerticalGroup(
            pnlInvLineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabsAcDocLine, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.6;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlMain.add(pnlInvLine, gridBagConstraints);

        pnlTransaction.setName("pnlTransaction"); // NOI18N
        pnlTransaction.setOpaque(false);

        tabsTransaction.setFont(resourceMap.getFont("tabsTransaction.font")); // NOI18N
        tabsTransaction.setName("tabsTransaction"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new java.awt.CardLayout(2, 2));

        jScrollPane1.setName("jScrollPane1"); // NOI18N
        jScrollPane1.setPreferredSize(new java.awt.Dimension(300, 100));

        tblTransaction.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Date", "Type", "Amount", "Remarks"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblTransaction.setFont(resourceMap.getFont("tblTransaction.font")); // NOI18N
        tblTransaction.setName("tblTransaction"); // NOI18N
        tblTransaction.setSortable(false);
        tblTransaction.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblTransaction);
        tblTransaction.getColumnModel().getColumn(0).setMinWidth(60);
        tblTransaction.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblTransaction.getColumnModel().getColumn(0).setMaxWidth(100);
        tblTransaction.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblTransaction.columnModel.title0")); // NOI18N
        tblTransaction.getColumnModel().getColumn(1).setMinWidth(60);
        tblTransaction.getColumnModel().getColumn(1).setPreferredWidth(80);
        tblTransaction.getColumnModel().getColumn(1).setMaxWidth(100);
        tblTransaction.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblTransaction.columnModel.title1")); // NOI18N
        tblTransaction.getColumnModel().getColumn(2).setMinWidth(60);
        tblTransaction.getColumnModel().getColumn(2).setPreferredWidth(80);
        tblTransaction.getColumnModel().getColumn(2).setMaxWidth(100);
        tblTransaction.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblTransaction.columnModel.title2")); // NOI18N
        tblTransaction.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblTransaction.columnModel.title3")); // NOI18N

        jPanel1.add(jScrollPane1, "card2");

        tabsTransaction.addTab(resourceMap.getString("jPanel1.TabConstraints.tabTitle"), jPanel1); // NOI18N

        jPanel4.setName("jPanel4"); // NOI18N

        lblTType.setFont(resourceMap.getFont("lblTType.font")); // NOI18N
        lblTType.setText(resourceMap.getString("lblTType.text")); // NOI18N
        lblTType.setName("lblTType"); // NOI18N

        cmbTType.setFont(resourceMap.getFont("cmbTType.font")); // NOI18N
        cmbTType.setName("cmbTType"); // NOI18N
        cmbTType.setPreferredSize(new java.awt.Dimension(70, 20));
        cmbTType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbTTypeActionPerformed(evt);
            }
        });

        txtTAmount.setEditable(false);
        txtTAmount.setFont(resourceMap.getFont("txtTAmount.font")); // NOI18N
        txtTAmount.setName("txtTAmount"); // NOI18N
        txtTAmount.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTAmountKeyReleased(evt);
            }
        });

        lblTAmount.setFont(resourceMap.getFont("lblTAmount.font")); // NOI18N
        lblTAmount.setText(resourceMap.getString("lblTAmount.text")); // NOI18N
        lblTAmount.setName("lblTAmount"); // NOI18N

        lblTRef.setFont(resourceMap.getFont("lblTRef.font")); // NOI18N
        lblTRef.setText(resourceMap.getString("lblTRef.text")); // NOI18N
        lblTRef.setName("lblTRef"); // NOI18N

        txtTRef.setEditable(false);
        txtTRef.setFont(resourceMap.getFont("txtTRef.font")); // NOI18N
        txtTRef.setName("txtTRef"); // NOI18N

        busyIcon.setName("busyIcon"); // NOI18N

        btnSubmit.setFont(resourceMap.getFont("btnSubmit.font")); // NOI18N
        btnSubmit.setIcon(resourceMap.getIcon("btnSubmit.icon")); // NOI18N
        btnSubmit.setText(resourceMap.getString("btnSubmit.text")); // NOI18N
        btnSubmit.setName("btnSubmit"); // NOI18N
        btnSubmit.setOpaque(false);
        btnSubmit.setPreferredSize(new java.awt.Dimension(100, 33));
        btnSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSubmitActionPerformed(evt);
            }
        });

        lblTOutRefund.setFont(resourceMap.getFont("jLabel3.font")); // NOI18N
        lblTOutRefund.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblTOutRefund.setText(resourceMap.getString("lblTOutRefund.text")); // NOI18N
        lblTOutRefund.setName("lblTOutRefund"); // NOI18N

        jLabel3.setFont(resourceMap.getFont("jLabel3.font")); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        btnApplyCredit.setFont(resourceMap.getFont("btnApplyCredit.font")); // NOI18N
        btnApplyCredit.setIcon(resourceMap.getIcon("btnApplyCredit.icon")); // NOI18N
        btnApplyCredit.setText(resourceMap.getString("btnApplyCredit.text")); // NOI18N
        btnApplyCredit.setMaximumSize(new java.awt.Dimension(129, 33));
        btnApplyCredit.setName("btnApplyCredit"); // NOI18N
        btnApplyCredit.setOpaque(false);
        btnApplyCredit.setPreferredSize(new java.awt.Dimension(129, 33));
        btnApplyCredit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApplyCreditActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(lblTRef)
                        .addGap(44, 44, 44)
                        .addComponent(txtTRef, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(busyIcon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 270, Short.MAX_VALUE)
                        .addComponent(btnSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                                .addComponent(lblTAmount)
                                .addGap(55, 55, 55)
                                .addComponent(txtTAmount))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                                .addComponent(lblTType)
                                .addGap(62, 62, 62)
                                .addComponent(cmbTType, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblTOutRefund, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnApplyCredit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(84, 84, 84))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTType)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cmbTType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)
                        .addComponent(lblTOutRefund)))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTAmount)
                            .addComponent(txtTAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(4, 4, 4)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTRef)
                            .addComponent(txtTRef, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(busyIcon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnApplyCredit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabsTransaction.addTab(resourceMap.getString("jPanel4.TabConstraints.tabTitle"), jPanel4); // NOI18N

        jPanel6.setBackground(resourceMap.getColor("jPanel6.background")); // NOI18N
        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel6.setName("jPanel6"); // NOI18N
        jPanel6.setLayout(new java.awt.GridBagLayout());

        jLabel7.setFont(resourceMap.getFont("jLabel14.font")); // NOI18N
        jLabel7.setForeground(resourceMap.getColor("jLabel12.foreground")); // NOI18N
        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(6, 2, 2, 2);
        jPanel6.add(jLabel7, gridBagConstraints);

        lblReceived1.setFont(resourceMap.getFont("jLabel14.font")); // NOI18N
        lblReceived1.setForeground(resourceMap.getColor("jLabel12.foreground")); // NOI18N
        lblReceived1.setText(resourceMap.getString("lblReceived1.text")); // NOI18N
        lblReceived1.setName("lblReceived1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(lblReceived1, gridBagConstraints);

        jLabel12.setFont(resourceMap.getFont("jLabel14.font")); // NOI18N
        jLabel12.setForeground(resourceMap.getColor("jLabel12.foreground")); // NOI18N
        jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(jLabel12, gridBagConstraints);

        jSeparator3.setName("jSeparator3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 2);
        jPanel6.add(jSeparator3, gridBagConstraints);

        jSeparator4.setName("jSeparator4"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(jSeparator4, gridBagConstraints);

        jLabel13.setFont(resourceMap.getFont("jLabel14.font")); // NOI18N
        jLabel13.setForeground(resourceMap.getColor("jLabel12.foreground")); // NOI18N
        jLabel13.setText(resourceMap.getString("jLabel13.text")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(jLabel13, gridBagConstraints);

        jLabel14.setFont(resourceMap.getFont("jLabel14.font")); // NOI18N
        jLabel14.setForeground(resourceMap.getColor("jLabel12.foreground")); // NOI18N
        jLabel14.setText(resourceMap.getString("jLabel14.text")); // NOI18N
        jLabel14.setName("jLabel14"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(jLabel14, gridBagConstraints);

        jLabel5.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        jLabel5.setForeground(resourceMap.getColor("jLabel5.foreground")); // NOI18N
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(jLabel5, gridBagConstraints);

        lblSubTotal.setFont(resourceMap.getFont("lblInvAmount.font")); // NOI18N
        lblSubTotal.setForeground(resourceMap.getColor("lblSubTotal.foreground")); // NOI18N
        lblSubTotal.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblSubTotal.setText(resourceMap.getString("lblSubTotal.text")); // NOI18N
        lblSubTotal.setName("lblSubTotal"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(lblSubTotal, gridBagConstraints);

        lblASubTotal.setFont(resourceMap.getFont("lblASubTotal.font")); // NOI18N
        lblASubTotal.setForeground(resourceMap.getColor("lblASubTotal.foreground")); // NOI18N
        lblASubTotal.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblASubTotal.setText(resourceMap.getString("lblASubTotal.text")); // NOI18N
        lblASubTotal.setName("lblASubTotal"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(lblASubTotal, gridBagConstraints);

        lblInvAmount.setFont(resourceMap.getFont("lblInvAmount.font")); // NOI18N
        lblInvAmount.setForeground(resourceMap.getColor("lblInvAmount.foreground")); // NOI18N
        lblInvAmount.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblInvAmount.setText(resourceMap.getString("lblInvAmount.text")); // NOI18N
        lblInvAmount.setName("lblInvAmount"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(lblInvAmount, gridBagConstraints);

        lblReceived.setFont(resourceMap.getFont("lblInvAmount.font")); // NOI18N
        lblReceived.setForeground(resourceMap.getColor("lblReceived.foreground")); // NOI18N
        lblReceived.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblReceived.setText(resourceMap.getString("lblReceived.text")); // NOI18N
        lblReceived.setName("lblReceived"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(lblReceived, gridBagConstraints);

        lblOutstanding.setFont(resourceMap.getFont("lblInvAmount.font")); // NOI18N
        lblOutstanding.setForeground(resourceMap.getColor("lblOutstanding.foreground")); // NOI18N
        lblOutstanding.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblOutstanding.setText(resourceMap.getString("lblOutstanding.text")); // NOI18N
        lblOutstanding.setName("lblOutstanding"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(lblOutstanding, gridBagConstraints);

        lblCreditNoteAmount.setFont(resourceMap.getFont("lblCreditNoteAmount.font")); // NOI18N
        lblCreditNoteAmount.setForeground(resourceMap.getColor("lblCreditNoteAmount.foreground")); // NOI18N
        lblCreditNoteAmount.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblCreditNoteAmount.setText(resourceMap.getString("lblCreditNoteAmount.text")); // NOI18N
        lblCreditNoteAmount.setName("lblCreditNoteAmount"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(lblCreditNoteAmount, gridBagConstraints);

        jLabel8.setFont(resourceMap.getFont("jLabel8.font")); // NOI18N
        jLabel8.setForeground(resourceMap.getColor("jLabel8.foreground")); // NOI18N
        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(jLabel8, gridBagConstraints);

        lblPaid.setFont(resourceMap.getFont("lblPaid.font")); // NOI18N
        lblPaid.setForeground(resourceMap.getColor("lblPaid.foreground")); // NOI18N
        lblPaid.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblPaid.setText(resourceMap.getString("lblPaid.text")); // NOI18N
        lblPaid.setName("lblPaid"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(lblPaid, gridBagConstraints);

        jLabel10.setFont(resourceMap.getFont("jLabel8.font")); // NOI18N
        jLabel10.setForeground(resourceMap.getColor("jLabel10.foreground")); // NOI18N
        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(jLabel10, gridBagConstraints);

        lblFinalBalance.setFont(resourceMap.getFont("jLabel8.font")); // NOI18N
        lblFinalBalance.setForeground(resourceMap.getColor("lblFinalBalance.foreground")); // NOI18N
        lblFinalBalance.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblFinalBalance.setText(resourceMap.getString("lblFinalBalance.text")); // NOI18N
        lblFinalBalance.setName("lblFinalBalance"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(lblFinalBalance, gridBagConstraints);

        javax.swing.GroupLayout pnlTransactionLayout = new javax.swing.GroupLayout(pnlTransaction);
        pnlTransaction.setLayout(pnlTransactionLayout);
        pnlTransactionLayout.setHorizontalGroup(
            pnlTransactionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTransactionLayout.createSequentialGroup()
                .addComponent(tabsTransaction, javax.swing.GroupLayout.PREFERRED_SIZE, 667, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pnlTransactionLayout.setVerticalGroup(
            pnlTransactionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabsTransaction, javax.swing.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlMain.add(pnlTransaction, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(pnlMain, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        acDocBo.setmAgent(AuthenticationBo.getmAgent());
        acDocBo.setAcDoc(this.acDoc);
        this.acDoc.setInvoiceTerms(AuthenticationBo.getAppSettings().gettInvTAndC());        
        if(this.atolProtected){
        this.acDoc.setInvoiceFooter(AuthenticationBo.getAppSettings().gettInvFooter());        
        }else{
        this.acDoc.setInvoiceFooter("");        
        }

        acDocBo.setOfficeCopy(false);
        BackofficeReporting rptAcDoc = new BackofficeReporting();
        List<Object> invObject = new ArrayList();
        invObject.add(acDocBo);
        rptAcDoc.printAcDoc(invObject);
    }//GEN-LAST:event_btnPrintActionPerformed

    private void btnPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviewActionPerformed
        acDocBo.setmAgent(AuthenticationBo.getmAgent());
        acDocBo.setAcDoc(this.acDoc);
        this.acDoc.setInvoiceTerms(AuthenticationBo.getAppSettings().gettInvTAndC());     
        if(this.atolProtected){
        this.acDoc.setInvoiceFooter(AuthenticationBo.getAppSettings().gettInvFooter());        
        }else{
        this.acDoc.setInvoiceFooter("");        
        }
        acDocBo.setOfficeCopy(false);
        BackofficeReporting rptAcDoc = new BackofficeReporting();
        List<Object> invObject = new ArrayList();
        invObject.add(acDocBo);
        rptAcDoc.viewAcDoc(invObject);
    }//GEN-LAST:event_btnPreviewActionPerformed

    private void btnOfficeCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOfficeCopyActionPerformed
        acDocBo.setmAgent(AuthenticationBo.getmAgent());
        acDocBo.setAcDoc(this.acDoc);
        acDocBo.setOfficeCopy(true);
        this.acDoc.setInvoiceTerms(AuthenticationBo.getAppSettings().gettInvTAndC());        
        if(this.atolProtected){
        this.acDoc.setInvoiceFooter(AuthenticationBo.getAppSettings().gettInvFooter());        
        }else{
        this.acDoc.setInvoiceFooter("");        
        }

        BackofficeReporting rptAcDoc = new BackofficeReporting();
        List<Object> invObject = new ArrayList();
        invObject.add(acDocBo);
        rptAcDoc.viewAcDoc(invObject);
    }//GEN-LAST:event_btnOfficeCopyActionPerformed

    private void btnEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmailActionPerformed
        acDocBo.setmAgent(AuthenticationBo.getmAgent());
        acDocBo.setAcDoc(this.acDoc);
        acDocBo.setOfficeCopy(false);
        this.acDoc.setInvoiceTerms(AuthenticationBo.getAppSettings().gettInvTAndC());        
        if(this.atolProtected){
        this.acDoc.setInvoiceFooter(AuthenticationBo.getAppSettings().gettInvFooter());        
        }else{
        this.acDoc.setInvoiceFooter("");        
        }

        BackofficeReporting rptAcDoc = new BackofficeReporting();
        List<Object> invObject = new ArrayList();
        invObject.add(acDocBo);
        String emailAddress = "";
        if (this.acDoc.getPnr().getAgent() != null) {
            emailAddress = this.acDoc.getPnr().getAgent().getEmail();
        } else if (this.acDoc.getPnr().getCustomer() != null) {
            emailAddress = this.acDoc.getPnr().getCustomer().getEmail();
        }
        if (!emailAddress.isEmpty()) {
            rptAcDoc.emailAcDoc(emailAddress, invObject);
        } else {
            JOptionPane.showMessageDialog(null, "No email address found !!!", "Email Invoice", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnEmailActionPerformed

    private void btnRemoveAdChgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveAdChgActionPerformed
        int row = tblAdditionalInvLine.getSelectedRow();

        if (row != -1) {
            Services s = this.aServicesInAcDoc.get(row);           
                if (s.getServiceId() == 0) {
                    for (AccountingDocumentLine l : this.otherLines) {
                        l.removeService(s);
                    }
                    populatetblOtherInvLine();
                } else {
                    this.acDocBo.deleteService(s);
                    loadAcDoc l = new loadAcDoc(acDoc);
                    l.execute();
                }            
        } else {
            JOptionPane.showMessageDialog(null, "Select Item to Remove", "New Invoice", JOptionPane.WARNING_MESSAGE);
        }
}//GEN-LAST:event_btnRemoveAdChgActionPerformed

    private void cmbTTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbTTypeActionPerformed
        if (cmbTType.getSelectedIndex() > 0) {
            txtTAmount.setEditable(true);
            txtTAmount.requestFocus();
            txtTRef.setEditable(true);
            btnSubmit.setEnabled(true);
        } else {
            txtTAmount.setEditable(false);
            txtTAmount.requestFocus();
            txtTRef.setEditable(false);
            btnSubmit.setEnabled(false);
        }
}//GEN-LAST:event_cmbTTypeActionPerformed

    private void txtTAmountKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTAmountKeyReleased
        String text = txtTAmount.getText();
        if (!text.isEmpty()) {
            BigDecimal amount = new BigDecimal(text);

            if (amount.compareTo(this.acDoc.getTotalDocumentedAmount()) == 1) {
                statusMessageLabel.setText("Warning: Payment more then invoice amount...");
            } else {
                statusMessageLabel.setText("");
            }
        }
}//GEN-LAST:event_txtTAmountKeyReleased

    private void btnSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubmitActionPerformed
        submitTransaction();
}//GEN-LAST:event_btnSubmitActionPerformed

    private void btnApplyCreditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApplyCreditActionPerformed
        DlgApplyCredit dlgApplyC = new DlgApplyCredit(this);
        if (dlgApplyC.showDlgApplyCredit(acDoc)) {
            new Thread(new threadLoadInvoice(this.acDoc.getAcDocId())).start();
        } else {
            System.out.println("Note");
        }
    }//GEN-LAST:event_btnApplyCreditActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        if (isEditable == false) {
            tabsAcDocLine.setSelectedIndex(0);//Swing is being funny populating visible table if 0 index is not sellected
            isEditable = true;

            btnUpdate.setEnabled(true);
            btnRemoveAdChg.setEnabled(true);
            btnRemoveOService.setEnabled(true);
            //btnRemoveTktLine.setEnabled(true);
            lblTitle.setText("Invoice: Editable Mode");
            tblTicket.setEditable(true);

            tblAdditionalInvLine.setEditable(true);
            tblOtherInvLine.setEditable(true);
            //populatetblOtherInvLine();
            //populatetblAdditionalInvLine();
        }
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        isEditable = false;
        btnUpdate.setEnabled(false);
        lblTitle.setText("Invoice");
        new Thread(new threadSaveAcDoc()).start();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnRemoveTktLineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveTktLineActionPerformed
        if (this.tickets.size() > 0) {
            int row = tblTicket.getSelectedRow();
            if (row != -1) {
                Ticket t = this.tickets.get(row);
                this.acDoc.removeTicketLine(t);
                loadObjects(this.acDoc);
            } else {
                JOptionPane.showMessageDialog(null, "Select Ticket to Remove", "New Invoice", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnRemoveTktLineActionPerformed

    private void btnRemoveOServiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveOServiceActionPerformed
        int row = tblOtherInvLine.getSelectedRow();

        if (row != -1) {
            Services s = this.oServicesInAcDoc.get(row);           
                if (s.getServiceId() == 0) {
                    for (AccountingDocumentLine l : this.otherLines) {
                        l.removeService(s);
                    }
                    populatetblOtherInvLine();
                } else {
                    this.acDocBo.deleteService(s);
                    loadAcDoc l = new loadAcDoc(acDoc);
                    l.execute();
                }            
        } else {
            JOptionPane.showMessageDialog(null, "Select Item to Remove", "New Invoice", JOptionPane.WARNING_MESSAGE);
        }
}//GEN-LAST:event_btnRemoveOServiceActionPerformed

    private void btnIssueInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIssueInvoiceActionPerformed
       //createNewInvoice();
    }//GEN-LAST:event_btnIssueInvoiceActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnApplyCredit;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnEmail;
    private javax.swing.JButton btnIssueInvoice;
    private javax.swing.JButton btnOfficeCopy;
    private javax.swing.JButton btnPreview;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnRemoveAdChg;
    private javax.swing.JButton btnRemoveOService;
    private javax.swing.JButton btnRemoveTktLine;
    private javax.swing.JButton btnSubmit;
    private javax.swing.JButton btnUpdate;
    private org.jdesktop.swingx.JXBusyLabel busyIcon;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox cmbTType;
    private javax.swing.JComboBox cmbTerms;
    private org.jdesktop.swingx.JXDatePicker datePicker;
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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JToolBar jToolBar4;
    private javax.swing.JLabel lblASubTotal;
    private javax.swing.JLabel lblCreditNoteAmount;
    private javax.swing.JLabel lblDocFor;
    private javax.swing.JLabel lblFinalBalance;
    private javax.swing.JLabel lblInvAmount;
    private javax.swing.JLabel lblOutstanding;
    private javax.swing.JLabel lblPaid;
    private javax.swing.JLabel lblReceived;
    private javax.swing.JLabel lblReceived1;
    private javax.swing.JLabel lblSubTotal;
    private javax.swing.JLabel lblTAmount;
    private javax.swing.JLabel lblTOutRefund;
    private javax.swing.JLabel lblTRef;
    private javax.swing.JLabel lblTType;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel pnInvlHeader;
    private javax.swing.JPanel pnlAServiceLine;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlInvLine;
    private javax.swing.JPanel pnlInvoiceFor;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlOServiceLine;
    private javax.swing.JPanel pnlStatus;
    private javax.swing.JPanel pnlTktLine;
    private javax.swing.JPanel pnlTransaction;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JTabbedPane tabsAcDocLine;
    private javax.swing.JTabbedPane tabsTransaction;
    private org.jdesktop.swingx.JXTable tblAdditionalInvLine;
    private org.jdesktop.swingx.JXTable tblOtherInvLine;
    private org.jdesktop.swingx.JXTable tblTicket;
    private org.jdesktop.swingx.JXTable tblTransaction;
    private javax.swing.JTextArea txtContactableDetails;
    private javax.swing.JTextPane txtFGuide;
    private javax.swing.JTextField txtInvRef;
    private javax.swing.JTextField txtTAmount;
    private javax.swing.JTextField txtTRef;
    private javax.swing.JTextField txtUser;
    // End of variables declaration//GEN-END:variables
    private long startTime = 0;
    private long stopTime = 0;
    private float elapsedTime = 0;

    private class threadCreateInvoice implements Runnable {

        public threadCreateInvoice() {
        }

        public void run() {

            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            //progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();
            statusMessageLabel.setText("Generating new document...");
            acDocBo.setAcDoc(acDoc);

            Set<AccountingDocument> acDocs = new LinkedHashSet();
            acDocs.add(acDoc);
            PNR pnr = acDoc.getPnr();
            pnr.setTickets(acDoc.getTickets());
            
            pnr.setAccountingDocuments(acDocs);
            pnrBo.setPnr(pnr);
            pnrBo.savePnr();

            statusMessageLabel.setText("Document created, Loading new document...");

            acDocBo.findCompleteAcDocWithRelatedDocsById(acDocBo.getAccountingDocument().getAcDocId());

            acDoc = acDocBo.getAccountingDocument();
            statusMessageLabel.setText("Populating new document...");
            showDialog(acDoc);
            //acBalance = accountsBo.finalClientAcBalance(acDoc.getPnr().getContactableId());
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            progressBar.setIndeterminate(false);

            statusMessageLabel.setText("Document Successfully Created. Time: " + elapsedTime / 1000 + " seconds");
            //progressBar.repaint(); //Refresh graphics

        }
    }

    private class threadLoadInvoice implements Runnable {

        private long acDocId;

        public threadLoadInvoice(long acDocId) {
            this.acDocId = acDocId;
        }

        public void run() {

            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            //progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();

            statusMessageLabel.setText("Loading invoice...");
            acDocBo.findCompleteAcDocWithRelatedDocsById(acDocId);
            acDoc = acDocBo.getAccountingDocument();

            statusMessageLabel.setText("Populating new document...");
            loadObjects(acDoc);
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            progressBar.setIndeterminate(false);
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            //progressBar.repaint(); //Refresh graphics
        }
    }

    private class loadAcDoc extends SwingWorker<AccountingDocument, Void> {

        private AccountingDocument a;

        public loadAcDoc(AccountingDocument a) {
            this.a = a;
        }

        @Override
        protected AccountingDocument doInBackground() throws Exception {
            AccountingDocument fullAcDoc = null;            
            if (this.a.getAcDocId() != 0) {
                acDocBo.findCompleteAcDocWithRelatedDocsById(a.getAcDocId());
                fullAcDoc = acDocBo.getAccountingDocument();
            } else {
                fullAcDoc = this.a;
            }
            return fullAcDoc;
        }

        @Override
        protected void done() {
            try {
                acDoc = get();                
            } catch (InterruptedException ex) {
                Logger.getLogger(FrameSAcDocHistory.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(FrameSAcDocHistory.class.getName()).log(Level.SEVERE, null, ex);
            }
            loadObjects(acDoc);
            populateComponents();
            populateInvoice();
        }
    }

    private class PopulateCmbServiceTitle extends SwingWorker<List<OtherService>, Void> {

        public PopulateCmbServiceTitle() {
            allServices.clear();
            additionalServices.clear();
            otherServices.clear();
        }

        @Override
        protected List<OtherService> doInBackground() throws Exception {
            List<OtherService> services = oServiceBo.loadAllServices();
            return services;
        }

        @Override
        protected void done() {
            try {
                allServices = get();
                for (OtherService s : allServices) {
                    if (s.getServiceType() == 1) {
                        otherServices.add(s);
                    } else if (s.getServiceType() == 2) {
                        additionalServices.add(s);
                    }
                }

                List cmbElement = new ArrayList();
                List cmbAdditionalElement = new ArrayList();

                for (OtherService o : otherServices) {
                    cmbElement.add(o.getServiceTitle());
                }
                for (OtherService o : additionalServices) {
                    cmbAdditionalElement.add(o.getServiceTitle());
                }

                DefaultComboBoxModel cmbAdditionalSTitleModel = new DefaultComboBoxModel(cmbAdditionalElement.toArray());
                cmbAdditionalSTitle.setModel(cmbAdditionalSTitleModel);
                cmbAdditionalSTitle.addActionListener(cmbAdditionalSTitleListener);

                DefaultComboBoxModel cmbOtherSTitleModel = new DefaultComboBoxModel(cmbElement.toArray());
                cmbOtherSTitle.setModel(cmbOtherSTitleModel);
                cmbOtherSTitle.addActionListener(cmbOtherSTitleListener);

            } catch (InterruptedException ex) {
                Logger.getLogger(FrameSAcDocHistory.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(FrameSAcDocHistory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private class threadSubmitTransaction implements Runnable {

        private AcTransaction transaction;

        public threadSubmitTransaction(AcTransaction transaction) {
            this.transaction = transaction;
        }

        public void run() {

            progressBar = new JProgressBar();
            busyIcon.setBusy(true);
            busyIcon.setText("Please wait...");
            progressBar.setIndeterminate(true); //Set value
            //progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();

            statusMessageLabel.setText("Saving transaction...");
            acTransactionBo.setAcTrans(transaction);
            acTransactionBo.saveACTransaction();

            statusMessageLabel.setText("Loading invoice...");
            acDocBo.findCompleteAcDocWithRelatedDocsById(acDoc.getAcDocId());
            acDoc = acDocBo.getAccountingDocument();

            showDialog(acDoc);
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            progressBar.setIndeterminate(false);

            busyIcon.setBusy(false);
            busyIcon.setText("");

            cmbTType.setSelectedIndex(0);
            txtTRef.setText("");
            txtTAmount.setText("");
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            //progressBar.repaint(); //Refresh graphics
        }
    }
    
    private class threadSaveAcDoc implements Runnable {

        AccountingDocument acDoc;

        public threadSaveAcDoc() {
        }

        public void run() {

            //progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            //progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();
            statusMessageLabel.setText("Saving invoice...");

            save();
        }
    }
}
