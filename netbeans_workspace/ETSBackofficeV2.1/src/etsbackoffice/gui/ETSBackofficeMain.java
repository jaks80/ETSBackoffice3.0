package etsbackoffice.gui;

import etsbackoffice.ETSBackofficeApp;
import etsbackoffice.GDSFileSender;
import etsbackoffice.businesslogic.*;
import etsbackoffice.datalogic.*;
import etsbackoffice.domain.*;
import etsbackoffice.thirdparty.TextTransfer;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.font.TextAttribute;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.Action;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 * The application's main frame.
 */
public class ETSBackofficeMain extends FrameView {

    MAgentBo mAgentBo = (MAgentBo) ETSBackofficeApp.getApplication().ctx.getBean("mAgentBo");
    AgentBo agentBo = (AgentBo) ETSBackofficeApp.getApplication().ctx.getBean("agentBo");
    CustomerBo customerBo = (CustomerBo) ETSBackofficeApp.getApplication().ctx.getBean("customerBo");
    PNRBo pnrBo = (PNRBo) ETSBackofficeApp.getApplication().ctx.getBean("pnrBo");
    TicketBo ticketBo = (TicketBo) ETSBackofficeApp.getApplication().ctx.getBean("ticketBo");
    ItineraryBo itineraryBo = (ItineraryBo) ETSBackofficeApp.getApplication().ctx.getBean("itineraryBo");
    AccountingDocBo acDocBo = (AccountingDocBo) ETSBackofficeApp.getApplication().ctx.getBean("acDocBo");
    OAccountingDocBo oAcDocBo = (OAccountingDocBo) ETSBackofficeApp.getApplication().ctx.getBean("oAcDocBo");
    AcTransactionBo acTransactionBo = (AcTransactionBo) ETSBackofficeApp.getApplication().ctx.getBean("acTransactionBo");
    UserDao userDao = (UserDao) ETSBackofficeApp.getApplication().ctx.getBean("userDao");
    MAgentDao mAgentDao = (MAgentDao) ETSBackofficeApp.getApplication().ctx.getBean("mAgentDao");
    OfficeIDDao officeIDDao = (OfficeIDDao) ETSBackofficeApp.getApplication().ctx.getBean("officeIDDao");
    User user = (User) ETSBackofficeApp.getApplication().ctx.getBean("user");
    ReportBo reportBo = (ReportBo) ETSBackofficeApp.getApplication().ctx.getBean("reportBo");
    private OServiceBo oServiceBo = (OServiceBo) ETSBackofficeApp.getApplication().ctx.getBean("oServiceBo");
    private AccountsBo accountsBo = (AccountsBo) ETSBackofficeApp.getApplication().ctx.getBean("accountsBo");
    private DateFormat df = new DateFormat();
    private PNR pnr;// = pnrBo.getPnr();
    private Ticket ticket;

    private Agent agent;
    private MasterAgent mAgent;
    private Customer customer;
    private AccountingDocument invoice;
    private DefaultTableModel pnrTodayModel, bookedPnrModel, tktModel, fareLineModel,
            itineraryModel, salesAcDocModel, purchaseAcDocModel;
    private DefaultTableModel tblOtherServiceModel;
    private List<OtherService> services = new ArrayList();
    private List<OtherService> oServices = new ArrayList();
    private List<OtherService> additionalServices = new ArrayList();
    
    private List<PNR> pnrs = new ArrayList();
    private List<PNR> pnrsToday = new ArrayList();
    private List<PNR> bookedPnrs = new ArrayList();
    private List<Ticket> tickets = new ArrayList();
    private List<Ticket> manuallyRfdtickets = new ArrayList();
    private List<Itinerary> segments = new ArrayList();
    private List<AccountingDocument> acDocs = new ArrayList();
    private List<PurchaseAccountingDocument> pAcDocs = new ArrayList();
    private List<PurchaseAccountingDocument> newPurchaseAcDocs = new ArrayList();
    private List<Services> otherServicesInPnr = new ArrayList<Services>();
    private List<Services> additionalServiceInPnr = new ArrayList<Services>();
    private List<Customer> searchedCustomer = new ArrayList();
    private List<Agent> searchedAgent = new ArrayList();
    private List<Agent> vendors;
    private DefaultComboBoxModel cmbContactableModel = new DefaultComboBoxModel();
    private DefaultComboBoxModel cmbOtherSTitleModel;
    private DefaultComboBoxModel cmbAdditionalSTitleModel;
    private JComboBox cmbOtherSTitle = new JComboBox();
    private JComboBox cmbAdditionalSTitle = new JComboBox();
    private JTextField tktLineField;

    private BigDecimal clientAcBalance;

    public ETSBackofficeMain(SingleFrameApplication app) {
        super(app);
        //getFrame().setExtendedState(JFrame.MAXIMIZED_BOTH);

        getFrame().setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        getFrame().addWindowListener(new CloseListener());        
        initComponents();
        initTblOtherService();
        initTicketComponent();
        refreshMainFrame();

        txtSByInvRef.setDocument(new CheckInput(CheckInput.FLOAT));

        doc = (AbstractDocument) txtContactableSearch.getDocument();
        doc.setDocumentFilter(new DocumentSizeFilter(45));

        doc = (AbstractDocument) txtSByPaxName.getDocument();
        doc.setDocumentFilter(new DocumentSizeFilter(45));

        doc = (AbstractDocument) txtSByGDSPNR.getDocument();
        doc.setDocumentFilter(new DocumentSizeFilter(6));

        lblLogonTime.setText(" Logon Time: " + df.getCurrentTime() + " ");
        lblLoggedOnUser.setText(" User : " + AuthenticationBo.getLoggedOnUser().getSurName()
                + " " + AuthenticationBo.getLoggedOnUser().getForeName() + " ");

        mAgent = AuthenticationBo.getmAgent();
        if (mAgent != null) {
            this.getFrame().setTitle("ETSBackoffice V 2.0001 : " + mAgent.getName());
        }
        actionRole();
    }

        private void actionRole() {
        
        if (AuthenticationBo.getLoggedOnUser().getUserRole() != null && AuthenticationBo.getLoggedOnUser().getUserRole().isAdministrator()) {
            btnDeletePnr.setEnabled(true);
            btnEditAcDoc.setEnabled(true);
            btnDelAcDoc.setEnabled(true);
            btnVoidAcDoc.setEnabled(true);
            btnDeleteTicket.setEnabled(true);
            btnVoidTicket.setEnabled(true);
        } else {
            btnDeletePnr.setEnabled(false);
            btnEditAcDoc.setEnabled(false);
            btnDelAcDoc.setEnabled(false);
            btnVoidAcDoc.setEnabled(false);
            btnDeleteTicket.setEnabled(false);
            btnVoidTicket.setEnabled(false);
        }
    }
        
    private class CloseListener extends WindowAdapter {

        @Override
        public void windowClosing(WindowEvent event) {
            int choice = JOptionPane.showConfirmDialog(null, "Do you wish to close?", "Exit application", JOptionPane.YES_NO_OPTION);
            if (choice == 0) {

                ETSBackofficeApp.getApplication().close();
            } else if (choice == 1) {
            }
        }
    }

    private void initTicketComponent() {
        CheckInput a = new CheckInput(CheckInput.FLOAT);
        CheckInput b = new CheckInput(CheckInput.FLOAT);
        CheckInput c = new CheckInput(CheckInput.FLOAT);
        CheckInput d = new CheckInput(CheckInput.FLOAT);
        CheckInput e = new CheckInput(CheckInput.FLOAT);
        CheckInput f = new CheckInput(CheckInput.FLOAT);
        CheckInput g = new CheckInput(CheckInput.FLOAT);
        CheckInput h = new CheckInput(CheckInput.FLOAT);
        a.setNegativeAccepted(true);
        b.setNegativeAccepted(true);
        c.setNegativeAccepted(true);
        d.setNegativeAccepted(true);
        e.setNegativeAccepted(true);
        f.setNegativeAccepted(true);
        g.setNegativeAccepted(true);
        h.setNegativeAccepted(true);

        txtBaseFare.setDocument(a);
        txtTax.setDocument(b);
        txtBspCom.setDocument(c);
        txtGrossFare.setDocument(d);
        txtDisc.setDocument(e);
        txtAtol.setDocument(f);
        txtNumAirCode.setDocument(g);
        txtTktNo.setDocument(h);
    }

    private void initTblOtherService() {

        tblOtherServiceModel = (DefaultTableModel) tblOtherService.getModel();
        TableColumn sTitle, sChg, disc, vat, quantity;
        sTitle = tblOtherService.getColumnModel().getColumn(1);
        sChg = tblOtherService.getColumnModel().getColumn(2);
        disc = tblOtherService.getColumnModel().getColumn(3);
        quantity = tblOtherService.getColumnModel().getColumn(4);

        JTextField jtf1 = new JTextField();
        JTextField jtf2 = new JTextField();
        JTextField jtf3 = new JTextField();
        
        CheckInput a = new CheckInput(CheckInput.FLOAT);
        CheckInput b = new CheckInput(CheckInput.FLOAT);
        CheckInput c = new CheckInput(CheckInput.FLOAT);
        a.setNegativeAccepted(true);
        b.setNegativeAccepted(true);        
        
        jtf1.setDocument(a);
        jtf2.setDocument(b);
        jtf3.setDocument(c);
        
        sChg.setCellEditor(new DefaultCellEditor(jtf1));
        disc.setCellEditor(new DefaultCellEditor(jtf2));
        quantity.setCellEditor(new DefaultCellEditor(jtf3));

        sTitle.setCellEditor(new DefaultCellEditor(cmbOtherSTitle));
    }
    
    private void refreshMainFrame() {
        resetComponents();
        new Thread(new threadUninvoicedPnr()).start();
    }

    private void comtrollComponents() {
        int activeAcDocSize = 0;
        for (AccountingDocument a : this.acDocs) {
            if (a.isActive()) {
                activeAcDocSize++;
            }
        }
        if (activeAcDocSize > 0) {
            rdoAgent.setEnabled(false);
            rdoCustomer.setEnabled(false);
            lblSearchClient.setEnabled(false);
            txtContactableSearch.setText("");
            txtContactableSearch.setEnabled(false);
            btnContactableSearch.setEnabled(false);
        } else {
            rdoAgent.setEnabled(true);
            rdoCustomer.setEnabled(true);
            lblSearchClient.setEnabled(true);
            txtContactableSearch.setText("");
            txtContactableSearch.setEnabled(true);
            btnContactableSearch.setEnabled(true);
        }
    }

    private void setSaveNeeded(boolean saveNeeded) {
        if (saveNeeded != this.saveNeeded) {
            this.saveNeeded = saveNeeded;
        }
        if (this.saveNeeded == true) {
            btnSave.setEnabled(true);
        } else {
            btnSave.setEnabled(false);
        }
    }
    //*****************Searching Contactable Object****************

    private void searchContactable() {
        String searchElement = txtContactableSearch.getText().trim();

        if (!searchElement.isEmpty()) {
            if (rdoAgent.isSelected()) {
                searchedAgent = new ArrayList();
                searchedAgent = agentBo.searchAgents(searchElement);

                if (searchedAgent.size() == 1) {
                    this.pnr.setAgent(searchedAgent.get(0));
                    this.pnr.setCustomer(null);
                    setSaveNeeded(true);
                    populateTxtAgentDetails(this.pnr.getAgent());
                    busyLabel.setText("");
                } else if (searchedAgent.size() > 1) {
                    busyLabel.setText("Multiple Client Found");
                    cmbSearchResult.setEnabled(true);
                    List cmbSearchResultElement = new ArrayList();
                    for (int i = 0; i < searchedAgent.size(); i++) {
                        cmbSearchResultElement.add(searchedAgent.get(i).getName() + "-" + searchedAgent.get(i).getPostCode()
                                + "-" + searchedAgent.get(i).getContactableId());
                    }

                    Collections.sort(cmbSearchResultElement);
                    DefaultComboBoxModel cmbSearchResultModel = new DefaultComboBoxModel(cmbSearchResultElement.toArray());
                    cmbSearchResult.setModel(cmbSearchResultModel);
                } else {//No Agent found using officeID now perform a search by name like
                    searchedAgent = agentBo.searchByAgtNameLike(searchElement);
                    if (searchedAgent.isEmpty()) {
                        busyLabel.setText("No agent found...");
                        //newAgent();
                    } else if (searchedAgent.size() > 1) {
                        busyLabel.setText("Multiple Client Found");
                        cmbSearchResult.setEnabled(true);
                        List cmbSearchResultElement = new ArrayList();
                        for (int i = 0; i < searchedAgent.size(); i++) {
                            cmbSearchResultElement.add(searchedAgent.get(i).getName() + "-" + searchedAgent.get(i).getPostCode()
                                    + "-" + searchedAgent.get(i).getContactableId());
                        }

                        Collections.sort(cmbSearchResultElement);
                        DefaultComboBoxModel cmbSearchResultModel = new DefaultComboBoxModel(cmbSearchResultElement.toArray());
                        cmbSearchResult.setModel(cmbSearchResultModel);
                    } else if (searchedAgent.size() == 1) {
                        if (this.pnr != null) {
                            this.pnr.setAgent(searchedAgent.get(0));
                            this.pnr.setCustomer(null);
                        }
                        setSaveNeeded(true);
                        populateTxtAgentDetails(this.pnr.getAgent());
                        busyLabel.setText("");
                    }
                }
            } else if (rdoCustomer.isSelected()) {
                searchedCustomer = new ArrayList();
                searchedCustomer = customerBo.searchCustomerForeNameLike(searchElement);

                if (searchedCustomer.size() == 1) {
                    this.pnr.setCustomer(searchedCustomer.get(0));
                    this.pnr.setAgent(null);
                    setSaveNeeded(true);
                    populateTxtCustomerDetails(this.pnr.getCustomer());
                    busyLabel.setText("");

                } else if (searchedCustomer.size() > 1) {
                    busyLabel.setText("Multiple Client Found");
                    cmbSearchResult.setEnabled(true);
                    List cmbSearchResultElement = new ArrayList();

                    for (int i = 0; i < searchedCustomer.size(); i++) {
                        cmbSearchResultElement.add(searchedCustomer.get(i).getSurName() + "/"
                                + searchedCustomer.get(i).getForeName() + "-" + searchedCustomer.get(i).getPostCode()
                                + "-" + searchedCustomer.get(i).getContactableId());
                    }

                    Collections.sort(cmbSearchResultElement);
                    DefaultComboBoxModel cmbSearchResultModel = new DefaultComboBoxModel(cmbSearchResultElement.toArray());
                    cmbSearchResult.setModel(cmbSearchResultModel);
                } else {
                    busyLabel.setText("No result, add new client...");
                    //newCustomer();
                }
            }
        } else {
            busyLabel.setText("No keyword to search");
        }
    }

    private void loadVendors() {
        List thirdpartyVendors = new ArrayList();
        for (int i = 0; i < this.vendors.size(); i++) {
            thirdpartyVendors.add(this.vendors.get(i).getName() + "-" + this.vendors.get(i).getOfficeIDs().iterator().next().getOfficeID()
                    + "-" + this.vendors.get(i).getContactableId());
        }

        DefaultComboBoxModel cmbVendorModel = new DefaultComboBoxModel(thirdpartyVendors.toArray());
        cmbVendorModel.insertElementAt("Select", 0);
        cmbVendor.setModel(cmbVendorModel);
        cmbVendor.setSelectedIndex(0);
    }

    private void populateTxtAgentDetails(Agent agent) {
        new Thread(new threadCheckAcBalance(agent.getContactableId())).start();
        txtContactableDetails.setText(agent.getFullAddressCRSeperated());
    }

    private void populateTxtVendorDetails(Agent vendor) {
        txtVendorDetails.setText(vendor.getFullAddressCRSeperated());
    }

    private void populateTxtCustomerDetails(Customer customer) {
        new Thread(new threadCheckAcBalance(customer.getContactableId())).start();
        txtContactableDetails.setText(customer.getFullAddressCRSeperated());
    }

    //*****************End of searching contactable object
    //**************Search Methods**************
    private void searchByGDSPnr(String gdsPnr) {
        this.pnrs = pnrBo.searchByGDSPnr(gdsPnr);
        populateTblPnr();
    }

    private void searchByAcDocRef(int acDocRef) {
        if (String.valueOf(acDocRef).length() < 8) {
            String invRefStarting = df.getCurrentYear().concat("000000");
            if (acDocRef < Integer.valueOf(invRefStarting)) {
                acDocRef = acDocRef + Integer.valueOf(invRefStarting);
            }

        } else {
            this.pnrs = acDocBo.findPnrByAcDocRef(acDocRef);
            populateTblPnr();
        }
    }

    private void searchByPaxName(String name) {
        this.pnrs = pnrBo.searchByPaxName(name);
        populateTblPnr();
    }

    private void searchByTktNo(String tktNo) {
        this.pnrs = pnrBo.searchByTktNo(tktNo);
        populateTblPnr();
    }
    //********************End of search methods

    private void populateAccounts(List<AccountingDocument> acDocs, List<PurchaseAccountingDocument> pAcDocs) {
        txtPPane.setText("");
        txtSPane.setText("");
        txtBPane.setText("");

        SimpleAttributeSet GREEN = new SimpleAttributeSet();
        SimpleAttributeSet WHITE = new SimpleAttributeSet();
        SimpleAttributeSet RED = new SimpleAttributeSet();

        StyleConstants.setForeground(GREEN, Color.GREEN);
        StyleConstants.setForeground(RED, Color.RED);
        StyleConstants.setForeground(WHITE, Color.WHITE);

        Document pPaneDoc = txtPPane.getDocument();
        Document sPaneDoc = txtSPane.getDocument();
        Document bPaneDoc = txtBPane.getDocument();
        
        BigDecimal pInvAmount = new BigDecimal("0.00");
        BigDecimal pOtherAdjustment = new BigDecimal("0.00");
        BigDecimal bPaid = new BigDecimal("0.00");
        BigDecimal pBalance = new BigDecimal("0.00");
                
        BigDecimal sInvAmount = new BigDecimal("0.00");
        BigDecimal sOtherAdjustment = new BigDecimal("0.00");
        BigDecimal pReceived = new BigDecimal("0.00");      
        BigDecimal sBalance = new BigDecimal("0.00"); 
        
        
        BigDecimal tRevenue = new BigDecimal("0.00");//tktd Rev
        BigDecimal oRevenue = new BigDecimal("0.00");//Other service rev
        BigDecimal aRevenue = new BigDecimal("0.00");// Add chg rev
        BigDecimal revAdm = new BigDecimal("0.00");   
        
        for (AccountingDocument a : this.acDocs) {
            if (a.getAcDoctype() == 1) {                
                sInvAmount = sInvAmount.add(a.getTotalDocumentedAmount());
            }
                sOtherAdjustment = sOtherAdjustment.add(a.getTotalAdm());
                pReceived = pReceived.add(a.getTotalTransactionAmount());
                tRevenue = tRevenue.add(a.getTktdRevenue());
                oRevenue = oRevenue.add(a.getRevenueFromOService());
                aRevenue = aRevenue.add(a.getRevenueFormAService());                
                revAdm = revAdm.add(a.getRevenueAdm());                           
        }

        for (PurchaseAccountingDocument pa : this.pAcDocs) {
            //if (pa.getAcDoctype() == 1) {
                pInvAmount = pInvAmount.add(pa.getTotalDocumentedAmount());
                pOtherAdjustment = pOtherAdjustment.add(pa.getTotalAdm());
                bPaid = bPaid.add(pa.getTotalPaid());
                revAdm = revAdm.add(pa.getRevenueAdm());
            //}
        }
        pBalance = pInvAmount.add(pOtherAdjustment).subtract(bPaid);
        sBalance = sInvAmount.add(sOtherAdjustment).subtract(pReceived);
        //finalBalance = sTotal.sufinalBalancebtract(pTotal);
        try {
            pPaneDoc.insertString(pPaneDoc.getLength(), "Tktd Billing Information", WHITE);
            pPaneDoc.insertString(pPaneDoc.getLength(), "\n----------------------", WHITE);            

            pPaneDoc.insertString(pPaneDoc.getLength(), "\nBillingAmount : ", WHITE);
            pPaneDoc.insertString(pPaneDoc.getLength(), pInvAmount.toString(), GREEN);            
            pPaneDoc.insertString(pPaneDoc.getLength(), "\nPurchase Adjustment : ", WHITE);
            pPaneDoc.insertString(pPaneDoc.getLength(), pOtherAdjustment.toString(), GREEN);
            
            pPaneDoc.insertString(pPaneDoc.getLength(), "\nBill Paid : ", WHITE);
            pPaneDoc.insertString(pPaneDoc.getLength(), bPaid.toString(), GREEN);
            pPaneDoc.insertString(pPaneDoc.getLength(), " Outstanding : ", WHITE);
            pPaneDoc.insertString(pPaneDoc.getLength(), pBalance.toString(), GREEN);

            sPaneDoc.insertString(sPaneDoc.getLength(), "Sales Information", WHITE);
            sPaneDoc.insertString(sPaneDoc.getLength(), "\n----------------------", WHITE);
            sPaneDoc.insertString(sPaneDoc.getLength(), "\nInvoice Amount : ", WHITE);
            sPaneDoc.insertString(sPaneDoc.getLength(), sInvAmount.toString(), GREEN);
            sPaneDoc.insertString(sPaneDoc.getLength(), "\nSales Adjustment : ", WHITE);
            sPaneDoc.insertString(sPaneDoc.getLength(), sOtherAdjustment.toString(), GREEN);
            

            sPaneDoc.insertString(sPaneDoc.getLength(), "\nReceived : ", WHITE);
            sPaneDoc.insertString(sPaneDoc.getLength(), pReceived.toString(), GREEN);
            sPaneDoc.insertString(sPaneDoc.getLength(), "  Outstanding : ", WHITE);
            sPaneDoc.insertString(sPaneDoc.getLength(), sBalance.toString(), GREEN);

            bPaneDoc.insertString(bPaneDoc.getLength(), "Revenue Information", WHITE);
            bPaneDoc.insertString(bPaneDoc.getLength(), "\n---------------", WHITE);
            bPaneDoc.insertString(bPaneDoc.getLength(), "\nTktd Net Rev : " + tRevenue, GREEN);
            bPaneDoc.insertString(bPaneDoc.getLength(), "\nO.Service Net Rev : " + oRevenue, GREEN);
            bPaneDoc.insertString(bPaneDoc.getLength(), "\nAdd.Chg Net Rev : " + aRevenue, GREEN);
            bPaneDoc.insertString(bPaneDoc.getLength(), "\nRev adjustment: " + revAdm, GREEN);
        } catch (BadLocationException ex) {
            Logger.getLogger(ETSBackofficeMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void populateTblPnr() {
        DefaultTableModel pnrModel = new DefaultTableModel();
        int row = 0;
        pnrModel = (DefaultTableModel) tblPnr.getModel();
        pnrModel.getDataVector().removeAllElements();
        tblPnr.repaint();
        tblPnr.clearSelection();
        if (this.pnrs.size() > 0) {
            for (PNR pnrForTable : this.pnrs) {

                pnrModel.insertRow(row, new Object[]{row+1, pnrForTable.getGdsPNR(),
                            pnrForTable.getLeadPax().getPaxSurName() + "/" + pnrForTable.getLeadPax().getPaxForeName(), pnrForTable.getNoOfPassenger(),
                            df.dateForGui(pnrForTable.getPnrCreationDate()), pnrForTable.getBookingAgtOID(), pnrForTable.getTicketingAgtOID(), pnrForTable.getServicingCareer().getCode()});
                row++;
            }
        }
    }

    private void populateTblPnrToday() {
        int row = 0;
        pnrTodayModel = (DefaultTableModel) tblPnrToday.getModel();
        pnrTodayModel.getDataVector().removeAllElements();
        tblPnrToday.clearSelection();
        if (this.pnrsToday.size() > 0) {
            for (PNR pnrForTable : this.pnrsToday) {

                pnrTodayModel.insertRow(row, new Object[]{row + 1, pnrForTable.getGdsPNR(),
                            pnrForTable.getLeadPax().getPaxSurName() + "/" + pnrForTable.getLeadPax().getPaxForeName(),
                            pnrForTable.getNoOfPassenger(), pnrForTable.getServicingCareer().getCode(),
                            pnrForTable.getBookingAgtOID(), pnrForTable.getTicketingAgtOID(),pnrForTable.hasUninvoicedTicket()});
                row++;
            }
        }                
    }

    private void populateTblReservation() {
        int row = 0;
        bookedPnrModel = (DefaultTableModel) tblReservation.getModel();
        bookedPnrModel.getDataVector().removeAllElements();
        tblPnrToday.clearSelection();
        if (this.bookedPnrs.size() > 0) {
            for (PNR pnrForTable : this.bookedPnrs) {

                bookedPnrModel.insertRow(row, new Object[]{row + 1, pnrForTable.getGdsPNR(),
                            pnrForTable.getLeadPax().getPaxSurName() + "/" + pnrForTable.getLeadPax().getPaxForeName(),
                            pnrForTable.getNoOfPassenger(),
                            pnrForTable.getServicingCareer().getCode(),
                            pnrForTable.getBookingAgtOID(),
                            df.dateForGui(pnrForTable.getPnrCreationDate())});
                row++;
            }
        }
    }

    private void searchSales() {

        Date from, to;
        from = dpToday.getDate();
        to = dpToday.getDate();

        int noOfPaxSelfIssue = 0, noOfPaxTPIssue = 0, noOfPaxSelfRfd = 0, noOfPaxTPRfd = 0;
        BigDecimal selfSale = new BigDecimal("0.00");
        BigDecimal selfRefund = new BigDecimal("0.00");

        BigDecimal thirdPartySale = new BigDecimal("0.00");
        BigDecimal thirdPartyRefund = new BigDecimal("0.00");

        List<Ticket> soldTickets = ticketBo.findSoldTickets(0, null, null,null,null, from, to);

        for (Ticket t : soldTickets) {
            if (t.getPnr().getTicketingAgt().getContactableId() == mAgent.getContactableId()) {
                if (t.getTktStatus() == 2 || t.getTktStatus() == 3 || t.getTktStatus() == 5) {
                    selfSale = selfSale.add(t.getNetFare());
                    noOfPaxSelfIssue++;
                } else if (t.getTktStatus() == 4) {
                    selfRefund = selfRefund.add(t.getNetFare());
                    noOfPaxSelfRfd++;
                }
            } else {
                if (t.getTktStatus() == 2 || t.getTktStatus() == 3 || t.getTktStatus() == 5) {
                    thirdPartySale = thirdPartySale.add(t.getNetFare());
                    noOfPaxTPIssue++;
                } else if (t.getTktStatus() == 4) {
                    thirdPartyRefund = thirdPartyRefund.add(t.getNetFare());
                    noOfPaxTPRfd++;
                }
            }
        }

        lblTotalIssueSelf.setText(" " + selfSale.toString());
        lblTotalRefundSelf.setText(" " + selfRefund.toString());
        lblTotalIssueTP.setText(" " + thirdPartySale.toString());
        lblTotalRefundTP.setText(" " + thirdPartyRefund.toString());

        lblBalanceSelf.setText(" " + selfSale.add(selfRefund).toString());
        lblBalanceTP.setText(" " + thirdPartySale.add(thirdPartyRefund).toString());

        lblNoOfPaxSelfIssue.setText(" " + String.valueOf(noOfPaxSelfIssue));
        lblNoOfPaxTPIssue.setText(" " + String.valueOf(noOfPaxTPIssue));
    }

    private void populatePnrDetails() {
        txtRetrievedPnr.setText("PNR Showing : " + this.pnr.getGdsPNR());
        txtGdsPnr.setText(this.pnr.getGdsPNR());
        txtBookingAgtOid.setText(this.pnr.getBookingAgtOID());
        txtTicketingAgtOid.setText(this.pnr.getTicketingAgtOID());
        dpPnrCreationDate.setDate(this.pnr.getPnrCreationDate());
        txtGDS.setText(this.pnr.getGds().getName());
        //txtSegments.setText(this.pnr);
        txtCareer.setText(this.pnr.getServicingCareer().getName());
        txtVendorPnr.setText(this.pnr.getVendorPNR());
    }

    private void populateClientDetails() {
        if (this.pnr.getAgent() != null) {
            this.agent = this.pnr.getAgent();
            populateTxtAgentDetails(this.pnr.getAgent());
        } else if (this.pnr.getCustomer() != null) {
            this.customer = this.pnr.getCustomer();
            populateTxtCustomerDetails(this.pnr.getCustomer());
        } else {
        }
    }

    private void populateTblTicket() {

        tblTicket.setEditable(false);
        txtRestrictions.setText("");
        tblTicket.clearSelection();
        tktModel = (DefaultTableModel) tblTicket.getModel();
        tktModel.getDataVector().removeAllElements();

        int row = 0;
        BigDecimal tNetFare = new BigDecimal("0.00");
        BigDecimal tGFare = new BigDecimal("0.00");
        BigDecimal tCom = new BigDecimal("0.00");
        BigDecimal tNetPayable = new BigDecimal("0.00");
        BigDecimal tPL = new BigDecimal("0.00");

        for (Ticket t : this.tickets) {
            boolean invoiced = true;
            if (t.getAccountingDocumentLine().isEmpty()) {
                invoiced = false;
            } else {
                invoiced = true;
            }
            tNetFare = tNetFare.add(t.getNetFare());
            tGFare = tGFare.add(t.getGrossFare());
            tCom = tCom.add(t.getDiscount());
            tNetPayable = tNetPayable.add(t.getNetPayble());
            tPL = tPL.add(t.getTktdRevenue());

            txtRestrictions.append(t.getRestrictions() + "\n");// Note: This should be removed,
                                                               //because everytime updating ticket cozing reprinting restrictions

            tktModel.insertRow(row, new Object[]{t.getPassengerNo()+". "+t.getPaxForeName(),
                        t.getTktStatusString(), df.dateForGui(t.getDocIssuedate()),
                        t.getNetFare(), t.getGrossFare(), t.getDiscount(),
                        t.getNetPayble(), t.getTktdRevenue(), invoiced});

            row++;
        }

        tktModel.addRow(new Object[]{"", "","Totals",tNetFare,tGFare,tCom,tNetPayable,tPL});

        tblTicket.setRowSelectionInterval(this.tickets.indexOf(this.ticket), this.tickets.indexOf(this.ticket));
    }

    private void populateTblOtherService() {        

        tblOtherServiceModel = (DefaultTableModel) tblOtherService.getModel();
        tblOtherServiceModel.getDataVector().removeAllElements();
        tblOtherService.repaint();
        Iterator it = this.otherServicesInPnr.iterator();
        int row = 0;
        while (it.hasNext()) {
            Services s = (Services) it.next();

            tblOtherServiceModel.insertRow(row, new Object[]{row + 1, s.getServiceTitle(),s.getServiceCost(),
                        s.getServiceCharge(), s.getDiscount(), s.getUnit(), s.getNetPayable(),s.getRevenue()});
            row++;

        }
        tblOtherServiceModel.addRow(new Object[]{"", "", "", "", "", ""});

        tblOtherServiceModel.addTableModelListener(new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    int row = e.getLastRow();
                    int column = e.getColumn();

                    if(column ==1 ){
                    
                    }
                    
                    if(column == 2){
                    String sCost = tblOtherService.getValueAt(row, column).toString();
                        if (!sCost.isEmpty() && otherServicesInPnr.size() > row) {
                            otherServicesInPnr.get(row).setServiceCost(new BigDecimal(sCost));
                        }
                    }
                    
                    if (column == 3) {
                        String sCharge = tblOtherService.getValueAt(row, column).toString();
                        if (!sCharge.isEmpty() && otherServicesInPnr.size() > row) {
                            otherServicesInPnr.get(row).setServiceCharge(new BigDecimal(sCharge));
                        }
                    }
                    if (column == 4) {
                        String disc = tblOtherService.getValueAt(row, column).toString().replaceAll("[^.0-9]", "");
                        if (!disc.isEmpty() && otherServicesInPnr.size() > row) {
                            otherServicesInPnr.get(row).setDiscount(new BigDecimal(disc).negate());
                        }
                    }
                    if (column == 5) {
                        String unit = tblOtherService.getValueAt(row, column).toString();
                        if (!unit.isEmpty() && otherServicesInPnr.size() > row) {
                            otherServicesInPnr.get(row).setUnit(Integer.valueOf(unit));                            
                        }
                    }
                    populateTblOtherService();
                }
            }
        });
    }
    
    public void populateTicketDetails() {

        txtName.setText(this.ticket.getPaxSurName() + "/" + this.ticket.getPaxForeName());
        txtNumAirCode.setText(this.ticket.getNumericAirLineCode());
        txtTktNo.setText(this.ticket.getTicketNo());
        txtStatus.setText(this.ticket.getTktStatusString());
        datePicker.setDate(this.ticket.getDocIssuedate());
        txtBaseFare.setText(this.ticket.getBaseFare().toString());
        txtTax.setText(this.ticket.getTax().toString());
        txtBspCom.setText(this.ticket.getBspCom().toString());
        txtTotalFare.setText(this.ticket.getNetFare().toString());

        txtGrossFare.setText(this.ticket.getGrossFare().toString());
        txtDisc.setText(this.ticket.getDiscount().toString());
        txtAtol.setText(this.ticket.getAtolChg().toString());
        txtNetToPay.setText(this.ticket.getNetPayble().toString());
        lblProfitLoss.setText(this.ticket.getTktdRevenue().toString());

        if (!this.ticket.getAccountingDocumentLine().isEmpty()) {
            txtBaseFare.setEditable(false);
            txtTax.setEditable(false);
            txtGrossFare.setEditable(false);
            txtDisc.setEditable(false);
            txtAtol.setEditable(false);
        } else {
            txtBaseFare.setEditable(true);
            txtTax.setEditable(true);
            txtGrossFare.setEditable(true);
            txtDisc.setEditable(true);
            txtAtol.setEditable(true);
            //txtGrossFare.requestFocus();//do not use cause focus lost prb
        }
    }

    private void populateTblFareLine() {
        //tblFareLine.setDefaultRenderer(Object.class, new ColorColumnRenderer());

        tblFareLine.clearSelection();
        fareLineModel = (DefaultTableModel) tblFareLine.getModel();
        fareLineModel.getDataVector().removeAllElements();

        Iterator it = this.tickets.iterator();
        int row = 0;
        for (Ticket t : this.tickets) {

            fareLineModel.insertRow(row, new Object[]{t.getFullPaxNameWithPaxNo(), t.getTktStatusString(),
                        t.getBaseFare(), t.getTax(), t.getBspCom(), t.getNetFare(),
                        t.getBaseFareVendor(), t.getTaxVendor(),
                        t.getBaseFareVendor().add(t.getTaxVendor())});
            row++;
        }
    }

    private void populateTblItinerary() {
        itineraryModel = (DefaultTableModel) tblItinerary.getModel();
        itineraryModel.getDataVector().removeAllElements();
        tblItinerary.clearSelection();
        int row = 0;
        Iterator it = this.segments.iterator();

        while (it.hasNext()) {
            Itinerary segment = (Itinerary) it.next();

            itineraryModel.insertRow(row, new Object[]{segment.getDeptFrom(),
                        segment.getDeptTo(), segment.getDeptDate(),
                        segment.getDeptTime(), segment.getArvDate(),
                        segment.getArvTime(), segment.getAirLineID(),
                        segment.getFlightNo(), segment.getTicketClass(),
                        segment.getTktStatus()});
            row++;
        }
    }

    private void populateTblSalesAcDoc() {
        salesAcDocModel = (DefaultTableModel) tblSalesAcDoc.getModel();
        salesAcDocModel.getDataVector().removeAllElements();
        tblSalesAcDoc.clearSelection();
        tblSalesAcDoc.repaint();
        Iterator it = this.acDocs.iterator();
        if (!it.hasNext()) {
            lblAcDocWarning.setText("Warning: No Sales Invoice");
        } else {
            lblAcDocWarning.setText("");
        }
        int row = 0;
        while (it.hasNext()) {
            AccountingDocument acDoc = (AccountingDocument) it.next();
           
            String status = "";
            if (acDoc.isActive()) {
                status = "LIVE";
            } else {
                status = "VOID";
            }
            salesAcDocModel.insertRow(row, new Object[]{Enums.AccountingDocumentType.valueOf(acDoc.getAcDoctype()),
                        df.dateForGui(acDoc.getIssueDate()), acDoc.getAcDocRef(),
                        acDoc.getTerms(), acDoc.getTotalDocumentedAmount(), status});
            row++;
        }
    }

    private void populateTblPurchaseAcDoc() {
        purchaseAcDocModel = (DefaultTableModel) tblPurchaseAcDoc.getModel();
        purchaseAcDocModel.getDataVector().removeAllElements();
        tblPurchaseAcDoc.clearSelection();
        tblPurchaseAcDoc.repaint();

        Iterator it = this.pAcDocs.iterator();
        int row = 0;
        while (it.hasNext()) {
            PurchaseAccountingDocument acDoc = (PurchaseAccountingDocument) it.next();
            String vendor = "";
            if (this.pnr.getTicketingAgt() != null) {
                vendor = this.pnr.getTicketingAgt().getName();
            } else {
                vendor = "IATA";
            }

            String acDocType = "";
            if (acDoc.getAcDoctype() == 1) {
                acDocType = "INVOICE";
            } else if (acDoc.getAcDoctype() == 2) {
                acDocType = "RFDCNOTE";
            } else if (acDoc.getAcDoctype() == 3) {
                acDocType = "CNOTE";
            }

            purchaseAcDocModel.insertRow(row, new Object[]{Enums.AccountingDocumentType.valueOf(acDoc.getAcDoctype()),
                        df.dateForGui(acDoc.getIssueDate()), acDoc.getRecipientRef(), acDoc.getVendorRef(),
                        acDoc.getTerms(), vendor, acDoc.getTotalDocumentedAmount()});
            row++;
        }

        purchaseAcDocModel.addTableModelListener(new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    int row = e.getLastRow();
                    int column = e.getColumn();

                    if (column == 3) {
                        String newVRef = tblPurchaseAcDoc.getValueAt(row, 3).toString().trim();
                        if (!newVRef.isEmpty()) {
                            pAcDocs.get(row).setVendorRef(newVRef);

                            pnr.setPurchaseAccountingDocuments(new LinkedHashSet(pAcDocs));
                            for (PurchaseAccountingDocument pAcDoc : pAcDocs) {
                                pAcDoc.setPurchaseAcDocLines(null);
                            }
                            setSaveNeeded(true);
                        }
                    }
                }
            }
        });
    }

    private void populateTblTransaction() {
        DefaultTableModel transModel = (DefaultTableModel) tblTransaction.getModel();
        transModel.getDataVector().removeAllElements();
        tblTransaction.repaint();
        List<AcTransaction> acTransactions = acTransactionBo.loadTransactions(this.pnr.getPnrId());
        for (AcTransaction t : acTransactions) {
            int row = 0;
            transModel.insertRow(row, new Object[]{df.dateForGui(t.getTransDate()),
                        t.getTransTypeString(), t.getTransAmount(), t.getTransRef()});
            row++;
        }
    }

    private Ticket manualRefundTicket(Ticket ticket) {
        boolean alreadyRefunded = false;
        for (Ticket tmpTicket : this.tickets) {
            //if (tmpTicket.getPassengerNo() == ticket.getPassengerNo() && tmpTicket.getTktStatus() == 4) {
            if (tmpTicket.getTicketNo().equals(ticket.getTicketNo()) && tmpTicket.getTktStatus() == 4) {
                alreadyRefunded = true;
            }
        }
        if (ticket.getTktStatus() != 4 && alreadyRefunded == false) {
            List<Object> rfdElement = pnrBo.loadPNR(ticket.getTicketNo(), ticket.getPaxSurName());

            Ticket oldTicket = null;
            PNR pnrInDB = new PNR();
            Set<Itinerary> oldSegments = new LinkedHashSet<Itinerary>();
            for (int j = 0; j < rfdElement.size(); j++) {
                Object[] objects = (Object[]) rfdElement.get(j);
                oldTicket = (Ticket) objects[0];
                pnrInDB = (PNR) objects[1];
                Itinerary seg = (Itinerary) objects[2];
                oldSegments.add(seg);
                newPurchaseAcDocs.clear();
                newPurchaseAcDocs.add((PurchaseAccountingDocument) objects[3]);
                this.pnr.setPurchaseAccountingDocuments(new LinkedHashSet(newPurchaseAcDocs));
                //pnrInDB.setPurchaseAccountingDocuments(new LinkedHashSet(newPurchaseAcDocs));
            }

            Ticket newTicket = new Ticket();
            newTicket.setBaseFare(oldTicket.getBaseFare().negate());
            newTicket.setTax(oldTicket.getTax().negate());
            newTicket.setTktStatus(4);
            newTicket.setPaxSurName(oldTicket.getPaxSurName());
            newTicket.setPaxForeName(oldTicket.getPaxForeName());
            newTicket.setPassengerNo(oldTicket.getPassengerNo());
            newTicket.setNumericAirLineCode(oldTicket.getNumericAirLineCode());
            newTicket.setTicketNo(oldTicket.getTicketNo());
            newTicket.setDocIssuedate(new java.util.Date());

            newTicket.setPnr(pnrInDB);
            newTicket.setSegments(oldSegments);

            TicketRefundDetails rfdDetails = new TicketRefundDetails();
            rfdDetails.setFarePaid(oldTicket.getBaseFare());
            rfdDetails.setFareUsed(BigDecimal.ZERO);
            rfdDetails.setCancellationFee(BigDecimal.ZERO);
            //rfdDetails.setTicket(newTicket);
            newTicket.setTicketRefundDetails(rfdDetails);
            return newTicket;
        } else {
            JOptionPane.showMessageDialog(null, "Ticket is already REFUND!!!", "REFUND Ticket", JOptionPane.YES_NO_OPTION);
            return null;
        }
    }

    private void manualVoidTicket(Ticket ticket) {

        if (ticket.getTktStatus() != 5) {
            ticket.setBaseFare(new BigDecimal("0.00"));
            ticket.setTax(new BigDecimal("0.00"));
            ticket.setBspCom(new BigDecimal("0.00"));
            ticket.setGrossFare(new BigDecimal("0.00"));
            ticket.setDiscount(new BigDecimal("0.00"));
            ticket.setAtolChg(new BigDecimal("0.00"));
            //ticket.setTotalFare(ticket.getTotalFare());
            ticket.setTktStatus(5);
        } else {
            JOptionPane.showMessageDialog(null, "Ticket is already VOID!!!", "VOID Ticket", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void decideAcDocAllocatedTo() {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                boolean allocateCustomer = false;
                boolean allocateAgent = false;
                loop:
                for (int i = 0; i < mAgent.getOfficeIDs().size(); i++) {

                    if (mAgent.getOfficeIDs().get(i).getOfficeID().equals(pnr.getBookingAgtOID())) {
                        allocateCustomer = true;
                        allocateAgent = false;
                        break loop;
                    }
                }
                if (allocateCustomer == false) {
                    allocateAgent = true;
                    rdoAgent.doClick();
                } else {
                    rdoCustomer.doClick();
                }
            }
        });
    }

    //@Action
    public void savePnr() {

        setSaveNeeded(false);

        this.pnr.setTickets(new LinkedHashSet(tickets));
        this.pnr.setSegments(new LinkedHashSet(segments));
        if (!this.manuallyRfdtickets.isEmpty()) {
            //this.pnr.addAllTickets(this.manuallyRfdtickets);
            PurchaseAccountingDocument pAcDoc = acDocBo.newPurchaseAcDoc(this.pnr);
            if (pAcDoc != null) {
                this.pnr.addPurchaseAcDoc(pAcDoc);
            }
        }
        if(!this.otherServicesInPnr.isEmpty()){
         this.pnr.setServices(new LinkedHashSet(this.otherServicesInPnr));
        }
        pnrBo.setPnr(pnr);
        pnrBo.savePnr();

        this.pnr = pnrBo.getPnr();
        this.tickets = pnrBo.getTickets();
        this.segments = pnrBo.getSegments();
        //this.acDocs = pnrBo.getAccountingDocuments();
        //this.pAcDocs = pnrBo.getPAccountingDocuments();
        populateClientDetails();
        populatePnrDetails();
        populateTblTicket();
        populateTblFareLine();
        populateTblItinerary();
        //populateTblSalesAcDoc();
        //populateTblPurchaseAcDoc();
        Thread t1 = new Thread(new threadLoadAcDocs(pnr.getPnrId()));
        t1.start();
        try {
            t1.join();
             populateTblSalesAcDoc();
        } catch (InterruptedException ex) {
            Logger.getLogger(ETSBackofficeMain.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (pnr.getAgent() == null && pnr.getCustomer() == null) {
            decideAcDocAllocatedTo();
        }
    }

    private void saveRefundCreditNote() {
        //this.pnrToRefundManually.setTickets(new LinkedHashSet(this.tickets));
        PurchaseAccountingDocument pAcDoc = acDocBo.newPurchaseAcDoc(this.pnr);
        if (pAcDoc != null) {
            this.pnr.addPurchaseAcDoc(pAcDoc);
            pnrBo.setPnr(this.pnr);
            pnrBo.savePnr();
        }
    }
    //Component Listeners----------------------------------------
    private ActionListener radioAgent = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            if (pnr != null) {
                txtContactableSearch.setText(pnr.getBookingAgtOID());
                btnContactableSearch.setEnabled(true);
                if (cmbSearchResult.getItemCount() > 0) {
                    cmbSearchResult.removeAllItems();
                }
                txtContactableDetails.setText(null);
            }
        }
    };
    private ActionListener radioCustomer = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            if (cmbSearchResult.getItemCount() > 0) {
                cmbSearchResult.removeAllItems();
            }
            txtContactableDetails.setText(null);
            for (Ticket ticket : tickets) {
                if (ticket.getPassengerNo() == 1) {
                    int indexOfBrace = ticket.getPaxForeName().indexOf("(");
                    if (indexOfBrace != -1) {
                        String foreName = ticket.getPaxForeName().substring(0, indexOfBrace);
                        txtContactableSearch.setText(ticket.getPaxSurName() + "/" + foreName);
                    } else {
                        txtContactableSearch.setText(ticket.getPaxSurName() + "/" + ticket.getPaxForeName());
                    }
                }
            }
        }
    };
    private ActionListener cmbSearchResultListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            if (cmbSearchResult.getItemCount() > 0) {
                String[] data = cmbSearchResult.getSelectedItem().toString().split("-");
                long id = Long.parseLong(data[2]);

                if (rdoAgent.isSelected()) {
                    for (int i = 0; i < searchedAgent.size(); i++) {
                        if (searchedAgent.get(i).getContactableId() == id) {
                            //agent = searchedAgent.get(i);
                            pnr.setAgent(searchedAgent.get(i));
                            pnr.setCustomer(null);
                            populateTxtAgentDetails(pnr.getAgent());
                            setSaveNeeded(true);
                        }
                    }
                } else if (rdoCustomer.isSelected()) {
                    for (int i = 0; i < searchedCustomer.size(); i++) {
                        if (searchedCustomer.get(i).getContactableId() == id) {
                            //customer = searchedCustomer.get(i);
                            pnr.setCustomer(searchedCustomer.get(i));
                            pnr.setAgent(null);
                            populateTxtCustomerDetails(pnr.getCustomer());
                            setSaveNeeded(true);
                        }
                    }
                }
            }
        }
    };
    private ActionListener cmbVendorListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            String[] data;
            long id = 0;
            if (cmbVendor.getSelectedIndex() > 0) {
                data = cmbVendor.getSelectedItem().toString().split("-");
                id = Long.parseLong(data[2]);
            }

            if (!vendors.isEmpty()) {
                loop:
                for (Agent vendor : vendors) {
                    if (vendor.getContactableId() == id) {
                        populateTxtVendorDetails(vendor);
                        if (pnr != null) {
                            pnr.setTicketingAgt(vendor);
                            if(vendor.getOfficeIDs().size()==1){
                            String oid = vendor.getOfficeIDs().iterator().next().getOfficeID();
                            txtTicketingAgtOid.setText(oid);
                            pnr.setTicketingAgtOID(oid);
                            }else if(vendor.getOfficeIDs().size()>1){
                            JOptionPane.showMessageDialog(null, "Please set ticketing agent office ID in PNR Details", "Warning! Setting ticketing agent", JOptionPane.WARNING_MESSAGE);
                            }
                            setSaveNeeded(true);
                        }
                        break loop;
                    }
                }
            }
        }
    };
    private ChangeListener tabsTicketListener = new ChangeListener() {

        public void stateChanged(ChangeEvent e) {

            if (pnr != null) {
                if (tabsTicket.getSelectedIndex() == 4) {
                    busyLabelRemark.setText("Loading...");
                    busyLabelRemark.setBusy(true);
                    txtPnrRemark.setText(null);
                    populatePNRDiary(pnr);
                    busyLabelRemark.setText("");
                    busyLabelRemark.setBusy(false);
                } else if (tabsTicket.getSelectedIndex() == 3) {
                    if (pnr != null) {
                        new Thread(new threadItinerary(pnr.getPnrId())).start();
                    }
                } else if (tabsTicket.getSelectedIndex() == 5) {
                    busyIcon.setVisible(true);
                    busyIcon.setBusy(true);
                    populateTblTransaction();
                    busyIcon.setBusy(false);
                    busyIcon.setVisible(false);
                } else if (tabsTicket.getSelectedIndex() == 6) {
                    
                    PopulateCmbServiceTitle p = new PopulateCmbServiceTitle();
                    //PopulateTblOtherService p1 = new PopulateTblOtherService();
                    //p1.execute();
                    p.execute();                    
                    
                }
            }
        }
    };
    private ChangeListener tabsAcDocListener = new ChangeListener() {

        public void stateChanged(ChangeEvent e) {

            if (pnr != null) {
                if (tabsAcDoc.getSelectedIndex() == 1) {
                    Thread t = new Thread(new threadPurchaseAcDoc(pnr.getPnrId()));
                    t.start();
                    try {
                        t.join();
                        populateTblPurchaseAcDoc();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ETSBackofficeMain.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } else if (tabsAcDoc.getSelectedIndex() == 2) {
                    Thread t = new Thread(new threadPurchaseAcDoc(pnr.getPnrId()));
                    Thread t1 = new Thread(new threadLoadAcDocs(pnr.getPnrId()));
                    t.start();

                    try {
                        t.join();
                        t1.start();
                        t1.join();
                        populateAccounts(acDocs, pAcDocs);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ETSBackofficeMain.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    };
    private ChangeListener tabsPnrListener = new ChangeListener() {

        public void stateChanged(ChangeEvent e) {

            if (tabsPnr.getSelectedIndex() == 0) {
                new Thread(new threadUninvoicedPnr()).start();
            } else if (tabsPnr.getSelectedIndex() == 1) {
                new Thread(new threadPnrsToday()).start();
            } else if (tabsPnr.getSelectedIndex() == 2) {
                new Thread(new threadSales()).start();
                //searchSales();
            } else if (tabsPnr.getSelectedIndex() == 3) {
                new Thread(new threadBookedPnrs()).start();
            }
        }
    };
    
    private ListSelectionListener tblTicketListener = new ListSelectionListener() {

        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int selectedRow = tblTicket.getSelectedRow();
            if (selectedRow != -1 && selectedRow != tblTicket.getRowCount()-1) {
                ticket = tickets.get(selectedRow);
                populateTicketDetails();
            }
        }
    };
        
    private ActionListener cmbOtherSTitleListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            JComboBox cb = (JComboBox) e.getSource();
            int row = tblOtherService.getSelectedRow();
            int cmbSTitleIndex = cb.getSelectedIndex();

            if (row != -1) {
                Services newOService = new Services();
                newOService.setServiceTitle(oServices.get(cmbSTitleIndex).getServiceTitle());
                newOService.setServiceCharge(oServices.get(cmbSTitleIndex).getServiceCharge());
                newOService.setServiceCost(oServices.get(cmbSTitleIndex).getServiceCost());
                newOService.setOtherService(oServices.get(cmbSTitleIndex));
                newOService.setUnit(1);
                newOService.setServiceType(1);
                newOService.setPnr(pnr);
                //line.setOtherService(otherServices.get(cmbSTitleIndex));
                otherServicesInPnr.add(newOService);
            }
            setSaveNeeded(true);
            populateTblOtherService();
        }
    };
    
    //------------End of Component Listeners
    private void resetComponents() {
        this.pnr = null;
        this.agent = null;
        this.customer = null;
        this.acDocs.clear();
        this.pAcDocs.clear();
        this.segments.clear();
        this.clientAcBalance = new BigDecimal("0.00");
        rdoAgent.setSelected(false);
        rdoCustomer.setSelected(false);
        txtContactableSearch.setText("");
        txtContactableDetails.setText("");
        lblAcDocWarning.setText("");
        lblCAcFinalBalance.setText("");
        cmbSearchResult.removeAllItems();
        cmbSearchResult.setEnabled(false);
        cmbVendor.removeAllItems();
        setSaveNeeded(false);

        tktModel = (DefaultTableModel) tblTicket.getModel();
        tktModel.getDataVector().removeAllElements();
        tblTicket.repaint();

        itineraryModel = (DefaultTableModel) tblItinerary.getModel();
        itineraryModel.getDataVector().removeAllElements();
        tblItinerary.repaint();

        salesAcDocModel = (DefaultTableModel) tblSalesAcDoc.getModel();
        salesAcDocModel.getDataVector().removeAllElements();
        tblSalesAcDoc.repaint();

        //tabsPnr.setSelectedIndex(0);
        tabsAcDoc.setSelectedIndex(0);
        tabsTicket.setSelectedIndex(0);

        txtRestrictions.setText("");
        txtGdsPnr.setText("");
        txtRetrievedPnr.setText("Retrieved PNR: ");
        txtGDS.setText("");
        txtBookingAgtOid.setText("");
        txtVendorPnr.setText("");
        txtTicketingAgtOid.setText("");
        txtCareer.setText("");
        txtSegments.setText("");
        dpPnrCreationDate.setDate(null);

        txtName.setText("");
        txtNumAirCode.setText("");
        txtTktNo.setText("");
        txtStatus.setText("");
        datePicker.setDate(null);
        txtBaseFare.setText("");
        txtTax.setText("");
        txtBspCom.setText("");
        txtTotalFare.setText("");

        txtGrossFare.setText("");
        txtDisc.setText("");
        txtAtol.setText("");
        txtNetToPay.setText("");
        lblProfitLoss.setText("");
    }

    private void populatePNRDiary(PNR pnr) {

        SimpleAttributeSet BLUE = new SimpleAttributeSet();
        SimpleAttributeSet BLACK = new SimpleAttributeSet();
        StyleConstants.setForeground(BLUE, Color.BLUE);
        StyleConstants.setForeground(BLACK, Color.BLACK);

        Document rmkDoc = txtPnrRemark.getDocument();

        List<PNRRemark> prmks = pnrBo.loadRemarks(pnr);

        for (int i = prmks.size() - 1; i >= 0; i--) {
            PNRRemark prmk = prmks.get(i);
            try {
                if (prmk.getUser() == null) {
                    rmkDoc.insertString(rmkDoc.getLength(), "" + "" + "" + "", BLUE);
                } else {
                    rmkDoc.insertString(rmkDoc.getLength(), prmk.getDateTime() + " : " + prmk.getUser().getSurName() + " : ", BLUE);
                }
                rmkDoc.insertString(rmkDoc.getLength(), prmk.getText() + '\n', BLACK);
            } catch (BadLocationException e) {
                System.out.println("Exception in pnr remark: " + e);
            }
        }
    }

    @Action
    private void deletePnr() {
        int row = tblPnr.getSelectedRow();
        if (row != -1) {
            int choice = JOptionPane.showConfirmDialog(null, "Delete pnr permanently?", "Delete PNR", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                this.pnr = pnrs.get(row);
                Thread t = new Thread(new threadDeletePnr(pnr));
                t.start();
                try {
                    t.join();
                    new Thread(new threadUninvoicedPnr()).start();
                    resetComponents();
                } catch (InterruptedException ex) {
                    Logger.getLogger(ETSBackofficeMain.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (choice == JOptionPane.NO_OPTION) {
            }
        } else {
            JOptionPane.showMessageDialog(null, "Select PNR", "Delete PNR", JOptionPane.WARNING_MESSAGE);
        }
    }

    @Action
    public void viewPnrDetails() {
        resetComponents();
        long pnrId = 0;
        if (tabsPnr.getSelectedIndex() == 0) {
            pnrId = pnrs.get(tblPnr.getSelectedRow()).getPnrId();
        } else if (tabsPnr.getSelectedIndex() == 1) {
            pnrId = pnrsToday.get(tblPnrToday.getSelectedRow()).getPnrId();
        } else if (tabsPnr.getSelectedIndex() == 3) {
            pnrId = this.bookedPnrs.get(tblReservation.getSelectedRow()).getPnrId();
        }
        Thread t = new Thread(new threadLoadCompletePnr(pnrId));
        t.start();
        try {
            t.join();
            busyLabelAcDoc.setText("Loading invoice. Please wait...");
            busyLabelAcDoc.setBusy(true);
            Thread t1 = new Thread(new threadLoadAcDocs(pnr.getPnrId()));
            t1.start();
            t1.join();
            populateTblSalesAcDoc();
            busyLabelAcDoc.setText("");
            busyLabelAcDoc.setBusy(false);
        } catch (InterruptedException ex) {
            Logger.getLogger(ETSBackofficeMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void reloadPNR() {
        long pnrId = this.pnr.getPnrId();
        Thread t = new Thread(new threadLoadCompletePnr(pnrId));
        t.start();
        try {
            t.join();
            Thread t1 = new Thread(new threadLoadAcDocs(pnr.getPnrId()));
            t1.start();
            t1.join();
            populateTblSalesAcDoc();
            //new Thread(new threadUninvoicedPnr()).start();
        } catch (InterruptedException ex) {
            Logger.getLogger(ETSBackofficeMain.class.getName()).log(Level.SEVERE, null, ex);
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

        menuBar = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        menuNewAgent = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem2 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenuItem12 = new javax.swing.JMenuItem();
        menuSettings = new javax.swing.JMenu();
        menuMAgent = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        menuBspCom = new javax.swing.JMenuItem();
        menuAddChg = new javax.swing.JMenuItem();
        menuInvTAndC = new javax.swing.JMenuItem();
        menuClient = new javax.swing.JMenu();
        menuAgentSummery = new javax.swing.JMenuItem();
        menuCustSummery = new javax.swing.JMenuItem();
        menuTicketing = new javax.swing.JMenu();
        menuSale = new javax.swing.JMenuItem();
        menuItinerary = new javax.swing.JMenuItem();
        menuSaleRev = new javax.swing.JMenuItem();
        menuPInvoice = new javax.swing.JMenu();
        menuOutThrdParty = new javax.swing.JMenuItem();
        menuPAcDocHistory = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        menuSInvoice = new javax.swing.JMenu();
        menuNTktingInv = new javax.swing.JMenuItem();
        jSeparator14 = new javax.swing.JPopupMenu.Separator();
        menuOutInv = new javax.swing.JMenuItem();
        menuOutRfd = new javax.swing.JMenuItem();
        menuInvHistory = new javax.swing.JMenuItem();
        jSeparator15 = new javax.swing.JPopupMenu.Separator();
        mnuSDebitNote = new javax.swing.JMenuItem();
        mnuSCreditNote = new javax.swing.JMenuItem();
        menuBilling = new javax.swing.JMenu();
        menuBPBsp = new javax.swing.JMenuItem();
        menuBPThirdParty = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jMenuItem9 = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        menuBillingRptThirdParty = new javax.swing.JMenuItem();
        menuTransaction = new javax.swing.JMenu();
        menuBTRpt = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        menuPCollection = new javax.swing.JMenuItem();
        jSeparator10 = new javax.swing.JPopupMenu.Separator();
        menuCashBook = new javax.swing.JMenuItem();
        menuSOther = new javax.swing.JMenu();
        menuONewInv = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        menuOInvOutstanding = new javax.swing.JMenuItem();
        menuOInvHistory = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JPopupMenu.Separator();
        jMenuItem11 = new javax.swing.JMenuItem();
        menuOInvMain = new javax.swing.JMenuItem();
        menuAccounts = new javax.swing.JMenu();
        menuAccountSummery = new javax.swing.JMenuItem();
        menuCAccounts = new javax.swing.JMenuItem();
        menuVAccounts = new javax.swing.JMenuItem();
        menuTools = new javax.swing.JMenu();
        jMenuItem10 = new javax.swing.JMenuItem();
        menuHelp = new javax.swing.JMenu();
        menuSendContenttoVendor = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        statusMessageLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        lblLoggedOnUser = new javax.swing.JLabel();
        lblLogonTime = new javax.swing.JLabel();
        dpToday = new org.jdesktop.swingx.JXDatePicker();
        mainPanel = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jSplitPane2 = new javax.swing.JSplitPane();
        inerPanel1 = new javax.swing.JPanel();
        tabsPnr = new javax.swing.JTabbedPane();
        pnlTask = new javax.swing.JPanel();
        jToolBar3 = new javax.swing.JToolBar();
        btnDeletePnr = new javax.swing.JButton();
        btnPnrDetails = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        busyLblTask = new org.jdesktop.swingx.JXBusyLabel();
        pnlPnr = new javax.swing.JScrollPane();
        tblPnr = new org.jdesktop.swingx.JXTable();
        pnlPnrToday = new javax.swing.JPanel();
        jScrollPane9 = new javax.swing.JScrollPane();
        tblPnrToday = new JXTable(){public Component prepareRenderer(TableCellRenderer renderer,
            int rowIndex, int vColIndex) {
            Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);                           
            String hasUninvoicedTicket = this.getModel().getValueAt(rowIndex, 7).toString();

            if (hasUninvoicedTicket.equalsIgnoreCase("false") ) {                
                c.setForeground(Color.green);
            } else{
                c.setForeground(Color.red);
            }
            return c;
        }
    };
    jToolBar5 = new javax.swing.JToolBar();
    btnDeletePnr1 = new javax.swing.JButton();
    jButton7 = new javax.swing.JButton();
    btnRefreshPnrToday = new javax.swing.JButton();
    busyLblPnrToDay = new org.jdesktop.swingx.JXBusyLabel();
    pnlSalesSummery = new javax.swing.JPanel();
    busyLblSalesToday = new org.jdesktop.swingx.JXBusyLabel();
    jToolBar6 = new javax.swing.JToolBar();
    btnDeletePnr2 = new javax.swing.JButton();
    jButton8 = new javax.swing.JButton();
    btnRefreshSalesSummery = new javax.swing.JButton();
    jPanel1 = new javax.swing.JPanel();
    jLabel2 = new javax.swing.JLabel();
    jLabel3 = new javax.swing.JLabel();
    lblTotalRefundSelf = new javax.swing.JLabel();
    jLabel9 = new javax.swing.JLabel();
    lblTotalIssueSelf = new javax.swing.JLabel();
    lblTotalIssueTP = new javax.swing.JLabel();
    jLabel12 = new javax.swing.JLabel();
    lblTotalRefundTP = new javax.swing.JLabel();
    jSeparator4 = new javax.swing.JSeparator();
    jLabel14 = new javax.swing.JLabel();
    jSeparator5 = new javax.swing.JSeparator();
    jLabel15 = new javax.swing.JLabel();
    jLabel18 = new javax.swing.JLabel();
    lblBalanceTP = new javax.swing.JLabel();
    jLabel16 = new javax.swing.JLabel();
    lblBalanceSelf = new javax.swing.JLabel();
    jLabel20 = new javax.swing.JLabel();
    lblNoOfPaxSelfIssue = new javax.swing.JLabel();
    jLabel22 = new javax.swing.JLabel();
    lblNoOfPaxTPIssue = new javax.swing.JLabel();
    pnlReservation = new javax.swing.JPanel();
    jToolBar7 = new javax.swing.JToolBar();
    btnDeletePnr3 = new javax.swing.JButton();
    jButton15 = new javax.swing.JButton();
    jButton16 = new javax.swing.JButton();
    busyLblPnrReservation = new org.jdesktop.swingx.JXBusyLabel();
    jScrollPane10 = new javax.swing.JScrollPane();
    tblReservation = new org.jdesktop.swingx.JXTable();
    searchPnrPanel = new javax.swing.JPanel();
    jLabel4 = new javax.swing.JLabel();
    txtSByGDSPNR = new javax.swing.JTextField();
    jLabel59 = new javax.swing.JLabel();
    txtSByInvRef = new javax.swing.JTextField();
    btnRTPNR = new javax.swing.JButton();
    jLabel1 = new javax.swing.JLabel();
    txtSByPaxName = new javax.swing.JTextField();
    jLabel5 = new javax.swing.JLabel();
    txtSByTktNo = new javax.swing.JTextField();
    innerPanel2 = new javax.swing.JPanel();
    tabsTicket = new javax.swing.JTabbedPane();
    tabsTicket.addChangeListener(tabsTicketListener);
    jPanel5 = new javax.swing.JPanel();
    jToolBar4 = new javax.swing.JToolBar();
    jButton10 = new javax.swing.JButton();
    btnDeleteTicket = new javax.swing.JButton();
    btnRefundTicket = new javax.swing.JButton();
    btnVoidTicket = new javax.swing.JButton();
    jScrollPane14 = new javax.swing.JScrollPane();
    tblTicket = new JXTable(){public Component prepareRenderer(TableCellRenderer renderer,
        int rowIndex, int vColIndex) {
        Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);                     
        String s = this.getModel().getValueAt(rowIndex, 1).toString();       

        if (s.equalsIgnoreCase("BOOK") ) {                
            c.setForeground(Color.yellow);
        } else if(s.equalsIgnoreCase("ISSUE")){
            c.setForeground(Color.green);
        }else if(s.equalsIgnoreCase("REISSUE")){
            c.setForeground(Color.cyan);
        }else if(s.equalsIgnoreCase("REFUND")){
            c.setForeground(Color.red);
        }else if(s.equalsIgnoreCase("VOID")){
            c.setForeground(Color.ORANGE);
            Map  attributes = c.getFont().getAttributes();
            attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
            Font newFont = new Font(attributes);
            c.setFont(newFont);
        }else{
            c.setForeground(Color.WHITE);
        }
        return c;
    }
    };
    jPanel3 = new javax.swing.JPanel();
    lblBookingAgtName = new javax.swing.JLabel();
    txtGdsPnr = new javax.swing.JTextField();
    txtBookingAgtOid = new javax.swing.JTextField();
    lblBookingAgtIATA = new javax.swing.JLabel();
    lblTicketingAgtName = new javax.swing.JLabel();
    txtTicketingAgtOid = new javax.swing.JTextField();
    txtSegments = new javax.swing.JTextField();
    lblSegment = new javax.swing.JLabel();
    dpPnrCreationDate = new org.jdesktop.swingx.JXDatePicker();
    txtCareer = new javax.swing.JTextField();
    txtVendorPnr = new javax.swing.JTextField();
    txtGDS = new javax.swing.JTextField();
    lblGDS = new javax.swing.JLabel();
    lblVendorPNR = new javax.swing.JLabel();
    lblCareer = new javax.swing.JLabel();
    lblTicketingAgtIATA = new javax.swing.JLabel();
    jLabel7 = new javax.swing.JLabel();
    jScrollPane8 = new javax.swing.JScrollPane();
    txtRestrictions = new javax.swing.JTextArea();
    jPanel13 = new javax.swing.JPanel();
    jScrollPane12 = new javax.swing.JScrollPane();
    tblFareLine = new JXTable(){public Component prepareRenderer(TableCellRenderer renderer,
        int rowIndex, int vColIndex) {
        Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);                     
        String s = this.getModel().getValueAt(rowIndex, 1).toString();       

        if (s.equalsIgnoreCase("BOOK") ) {                
            c.setForeground(Color.yellow);
        } else if(s.equalsIgnoreCase("ISSUE")){
            c.setForeground(Color.green);
        }else if(s.equalsIgnoreCase("REISSUE")){
            c.setForeground(Color.green);
        }else if(s.equalsIgnoreCase("REFUND")){
            c.setForeground(Color.red);
        }else if(s.equalsIgnoreCase("VOID")){
            c.setForeground(Color.ORANGE);
        }else{
            c.setForeground(Color.WHITE);
        }
        return c;
    }
    };
    jPanel6 = new javax.swing.JPanel();
    jScrollPane3 = new javax.swing.JScrollPane();
    tblItinerary = new JXTable(){public Component prepareRenderer(TableCellRenderer renderer,
        int rowIndex, int vColIndex) {
        Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
        if (rowIndex % 2 == 0 && !isCellSelected(rowIndex, vColIndex)) {
            c.setBackground(Color.LIGHT_GRAY);
            c.setForeground(Color.BLACK);
        } else {
            // If not shaded, match the table's background
            // c.setBackground(getBackground());
            // c.setForeground(Color.BLACK);

        }
        return c;
    }
    };
    jToolBar8 = new javax.swing.JToolBar();
    btnRefreshItinerary = new javax.swing.JButton();
    btnDeleteSegment = new javax.swing.JButton();
    btnViewSegment = new javax.swing.JButton();
    jPanel9 = new javax.swing.JPanel();
    txtTextField = new javax.swing.JTextField();
    jScrollPane7 = new javax.swing.JScrollPane();
    txtPnrRemark = new javax.swing.JTextPane();
    jLabel6 = new javax.swing.JLabel();
    jLabel35 = new javax.swing.JLabel();
    busyLabelRemark = new org.jdesktop.swingx.JXBusyLabel();
    jPanel15 = new javax.swing.JPanel();
    jScrollPane6 = new javax.swing.JScrollPane();
    tblTransaction = new org.jdesktop.swingx.JXTable();
    btnLoadTransaction = new javax.swing.JButton();
    busyIcon = new org.jdesktop.swingx.JXBusyLabel();
    jPanel4 = new javax.swing.JPanel();
    jScrollPane5 = new javax.swing.JScrollPane();
    tblOtherService = new org.jdesktop.swingx.JXTable();
    jToolBar9 = new javax.swing.JToolBar();
    btnRemoveLine1 = new javax.swing.JButton();
    btnReturnItem = new javax.swing.JButton();
    pnlAcDocs = new javax.swing.JPanel();
    lblCAcFinalBalance = new javax.swing.JLabel();
    busyLabelAcDoc = new org.jdesktop.swingx.JXBusyLabel();
    jToolBar1 = new javax.swing.JToolBar();
    btnEditAcDoc = new javax.swing.JButton();
    btnVewAcDoc = new javax.swing.JButton();
    btnPrintAcDoc = new javax.swing.JButton();
    btnDelAcDoc = new javax.swing.JButton();
    btnVoidAcDoc = new javax.swing.JButton();
    lblAcDocWarning = new javax.swing.JLabel();
    tabsAcDoc = new javax.swing.JTabbedPane();
    tabsAcDoc.addChangeListener(tabsAcDocListener);
    jPanel11 = new javax.swing.JPanel();
    jScrollPane18 = new javax.swing.JScrollPane();
    tblSalesAcDoc = new JXTable(){public Component prepareRenderer(TableCellRenderer renderer,
        int rowIndex, int vColIndex) {
        Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);                     
        String s = this.getModel().getValueAt(rowIndex, 5).toString();       

        if(s.equalsIgnoreCase("VOID")){
            //c.setForeground(Color.ORANGE);
            Map  attributes = c.getFont().getAttributes();
            attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
            Font newFont = new Font(attributes);
            c.setFont(newFont);
        }
        return c;
    }
    };
    jPanel12 = new javax.swing.JPanel();
    jScrollPane11 = new javax.swing.JScrollPane();
    tblPurchaseAcDoc = new javax.swing.JTable();
    jPanel2 = new javax.swing.JPanel();
    jScrollPane15 = new javax.swing.JScrollPane();
    txtPPane = new javax.swing.JTextPane();
    jScrollPane16 = new javax.swing.JScrollPane();
    txtSPane = new javax.swing.JTextPane();
    jScrollPane17 = new javax.swing.JScrollPane();
    txtBPane = new javax.swing.JTextPane();
    pnlTktDetails = new javax.swing.JPanel();
    jLabel8 = new javax.swing.JLabel();
    jLabel11 = new javax.swing.JLabel();
    jLabel17 = new javax.swing.JLabel();
    lblBaseFare = new javax.swing.JLabel();
    lblTax = new javax.swing.JLabel();
    lblTotalFare = new javax.swing.JLabel();
    lblBspCom = new javax.swing.JLabel();
    lblGrossFare = new javax.swing.JLabel();
    lblDisc = new javax.swing.JLabel();
    lblAtol = new javax.swing.JLabel();
    lblNetToPay = new javax.swing.JLabel();
    jLabel24 = new javax.swing.JLabel();
    txtTktNo = new javax.swing.JTextField();
    txtStatus = new javax.swing.JTextField();
    txtBaseFare = new javax.swing.JTextField();
    txtTax = new javax.swing.JTextField();
    txtTotalFare = new javax.swing.JTextField();
    txtBspCom = new javax.swing.JTextField();
    txtGrossFare = new javax.swing.JTextField();
    txtDisc = new javax.swing.JTextField();
    txtAtol = new javax.swing.JTextField();
    txtNetToPay = new javax.swing.JTextField();
    lblProfitLoss = new javax.swing.JLabel();
    datePicker = new org.jdesktop.swingx.JXDatePicker();
    jSeparator12 = new javax.swing.JSeparator();
    lblGFMessage = new javax.swing.JLabel();
    txtNumAirCode = new javax.swing.JTextField();
    jScrollPane2 = new javax.swing.JScrollPane();
    txtName = new javax.swing.JTextArea();
    btnPTicket = new javax.swing.JButton();
    btnNTicket = new javax.swing.JButton();
    jSeparator6 = new javax.swing.JSeparator();
    jLabel10 = new javax.swing.JLabel();
    jSeparator13 = new javax.swing.JSeparator();
    txtRetrievedPnr = new javax.swing.JTextField();
    innerPanel3 = new javax.swing.JPanel();
    jButton2 = new javax.swing.JButton();
    rightPanel = new javax.swing.JPanel();
    jPanel7 = new javax.swing.JPanel();
    cmbSearchResult = new javax.swing.JComboBox();
    cmbSearchResult.addActionListener(cmbSearchResultListener);
    AutoCompleteDecorator.decorate(cmbSearchResult);
    jScrollPane4 = new javax.swing.JScrollPane();
    txtContactableDetails = new javax.swing.JTextArea();
    lblContactibleDetails = new javax.swing.JLabel();
    txtContactableSearch = new javax.swing.JTextField();
    btnContactableSearch = new javax.swing.JButton();
    rdoAgent = new javax.swing.JRadioButton();
    rdoCustomer = new javax.swing.JRadioButton();
    lblSearchClient = new javax.swing.JLabel();
    busyLabel = new org.jdesktop.swingx.JXBusyLabel();
    jPanel14 = new javax.swing.JPanel();
    cmbVendor = new javax.swing.JComboBox();
    cmbVendor.addActionListener(cmbVendorListener);
    AutoCompleteDecorator.decorate(cmbVendor);
    busyLabelVendor = new org.jdesktop.swingx.JXBusyLabel();
    btnLoadVendors = new javax.swing.JButton();
    jScrollPane13 = new javax.swing.JScrollPane();
    txtVendorDetails = new javax.swing.JTextArea();
    toolBar = new javax.swing.JToolBar();
    btnNewCustomer = new javax.swing.JButton();
    btnSave = new javax.swing.JButton();
    btnRefresh = new javax.swing.JButton();
    jButton1 = new javax.swing.JButton();
    btnOutstandingInvoice = new javax.swing.JButton();
    btnTransaction = new javax.swing.JButton();
    jSeparator11 = new javax.swing.JToolBar.Separator();
    btnInvoice = new javax.swing.JButton();
    btnTktRfdCreditNote = new javax.swing.JButton();
    btnSCreditNote = new javax.swing.JButton();
    btnSDebitNote = new javax.swing.JButton();
    btnPCNote = new javax.swing.JButton();
    jButton12 = new javax.swing.JButton();
    btnONewInv = new javax.swing.JButton();
    jToolBar2 = new javax.swing.JToolBar();
    btnQuickSearch = new javax.swing.JButton();
    txtQuickSearch = new javax.swing.JTextField();
    busyLabelSQ = new org.jdesktop.swingx.JXBusyLabel();
    jButton11 = new javax.swing.JButton();
    buttonGroup1 = new javax.swing.ButtonGroup();
    jScrollPane1 = new javax.swing.JScrollPane();
    jXTable1 = new org.jdesktop.swingx.JXTable();
    jTextField1 = new javax.swing.JTextField();

    menuBar.setName("menuBar"); // NOI18N

    org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getResourceMap(ETSBackofficeMain.class);
    menuFile.setText(resourceMap.getString("menuFile.text")); // NOI18N
    menuFile.setFont(resourceMap.getFont("jMenu3.font")); // NOI18N
    menuFile.setName("menuFile"); // NOI18N

    javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getActionMap(ETSBackofficeMain.class, this);
    menuNewAgent.setAction(actionMap.get("newAgent")); // NOI18N
    menuNewAgent.setFont(resourceMap.getFont("jMenuItem2.font")); // NOI18N
    menuNewAgent.setText(resourceMap.getString("menuNewAgent.text")); // NOI18N
    menuNewAgent.setName("menuNewAgent"); // NOI18N
    menuFile.add(menuNewAgent);

    jMenuItem6.setAction(actionMap.get("newCustomer")); // NOI18N
    jMenuItem6.setFont(resourceMap.getFont("jMenuItem2.font")); // NOI18N
    jMenuItem6.setText(resourceMap.getString("jMenuItem6.text")); // NOI18N
    jMenuItem6.setName("jMenuItem6"); // NOI18N
    menuFile.add(jMenuItem6);

    jSeparator1.setName("jSeparator1"); // NOI18N
    menuFile.add(jSeparator1);

    jMenuItem2.setFont(resourceMap.getFont("jMenuItem2.font")); // NOI18N
    jMenuItem2.setText(resourceMap.getString("jMenuItem2.text")); // NOI18N
    jMenuItem2.setName("jMenuItem2"); // NOI18N
    menuFile.add(jMenuItem2);
    jMenuItem2.getAccessibleContext().setAccessibleName(resourceMap.getString("jMenuItem2.AccessibleContext.accessibleName")); // NOI18N

    jSeparator2.setName("jSeparator2"); // NOI18N
    menuFile.add(jSeparator2);

    jMenuItem7.setFont(resourceMap.getFont("jMenuItem2.font")); // NOI18N
    jMenuItem7.setText(resourceMap.getString("jMenuItem7.text")); // NOI18N
    jMenuItem7.setName("jMenuItem7"); // NOI18N
    jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuItem7ActionPerformed(evt);
        }
    });
    menuFile.add(jMenuItem7);

    jMenuItem12.setAction(actionMap.get("console")); // NOI18N
    jMenuItem12.setFont(resourceMap.getFont("jMenuItem12.font")); // NOI18N
    jMenuItem12.setText(resourceMap.getString("jMenuItem12.text")); // NOI18N
    jMenuItem12.setName("jMenuItem12"); // NOI18N
    menuFile.add(jMenuItem12);

    menuBar.add(menuFile);

    menuSettings.setText(resourceMap.getString("menuSettings.text")); // NOI18N
    menuSettings.setFont(resourceMap.getFont("jMenu3.font")); // NOI18N
    menuSettings.setName("jMenu3"); // NOI18N

    menuMAgent.setAction(actionMap.get("mAgent")); // NOI18N
    menuMAgent.setFont(resourceMap.getFont("menuMAgent.font")); // NOI18N
    menuMAgent.setText(resourceMap.getString("menuMAgent.text")); // NOI18N
    menuMAgent.setName("menuMAgent"); // NOI18N
    menuSettings.add(menuMAgent);

    jMenuItem8.setAction(actionMap.get("gds")); // NOI18N
    jMenuItem8.setFont(resourceMap.getFont("menuMAgent.font")); // NOI18N
    jMenuItem8.setText(resourceMap.getString("jMenuItem8.text")); // NOI18N
    jMenuItem8.setName("jMenuItem8"); // NOI18N
    menuSettings.add(jMenuItem8);

    jMenuItem5.setAction(actionMap.get("newUser")); // NOI18N
    jMenuItem5.setFont(resourceMap.getFont("menuMAgent.font")); // NOI18N
    jMenuItem5.setText(resourceMap.getString("jMenuItem5.text")); // NOI18N
    jMenuItem5.setName("jMenuItem5"); // NOI18N
    menuSettings.add(jMenuItem5);

    jMenuItem4.setAction(actionMap.get("userSummery")); // NOI18N
    jMenuItem4.setFont(resourceMap.getFont("menuMAgent.font")); // NOI18N
    jMenuItem4.setText(resourceMap.getString("jMenuItem4.text")); // NOI18N
    jMenuItem4.setName("jMenuItem4"); // NOI18N
    menuSettings.add(jMenuItem4);

    menuBspCom.setAction(actionMap.get("frameBspCom")); // NOI18N
    menuBspCom.setFont(resourceMap.getFont("menuMAgent.font")); // NOI18N
    menuBspCom.setText(resourceMap.getString("menuBspCom.text")); // NOI18N
    menuBspCom.setName("menuBspCom"); // NOI18N
    menuSettings.add(menuBspCom);

    menuAddChg.setAction(actionMap.get("frameAddServiceManager")); // NOI18N
    menuAddChg.setFont(resourceMap.getFont("menuAddChg.font")); // NOI18N
    menuAddChg.setText(resourceMap.getString("menuAddChg.text")); // NOI18N
    menuAddChg.setName("menuAddChg"); // NOI18N
    menuSettings.add(menuAddChg);

    menuInvTAndC.setAction(actionMap.get("dlgInvTAndC")); // NOI18N
    menuInvTAndC.setFont(resourceMap.getFont("menuInvTAndC.font")); // NOI18N
    menuInvTAndC.setText(resourceMap.getString("menuInvTAndC.text")); // NOI18N
    menuInvTAndC.setName("menuInvTAndC"); // NOI18N
    menuSettings.add(menuInvTAndC);

    menuBar.add(menuSettings);

    menuClient.setAction(actionMap.get("agentSummery")); // NOI18N
    menuClient.setText(resourceMap.getString("menuClient.text")); // NOI18N
    menuClient.setFont(resourceMap.getFont("jMenu3.font")); // NOI18N
    menuClient.setName("menuClient"); // NOI18N

    menuAgentSummery.setAction(actionMap.get("agentSummery")); // NOI18N
    menuAgentSummery.setFont(resourceMap.getFont("menuCustSummery.font")); // NOI18N
    menuAgentSummery.setText(resourceMap.getString("menuAgentSummery.text")); // NOI18N
    menuAgentSummery.setName("menuAgentSummery"); // NOI18N
    menuClient.add(menuAgentSummery);

    menuCustSummery.setAction(actionMap.get("customerSummery")); // NOI18N
    menuCustSummery.setFont(resourceMap.getFont("menuCustSummery.font")); // NOI18N
    menuCustSummery.setText(resourceMap.getString("menuCustSummery.text")); // NOI18N
    menuCustSummery.setName("menuCustSummery"); // NOI18N
    menuClient.add(menuCustSummery);

    menuBar.add(menuClient);

    menuTicketing.setText(resourceMap.getString("menuTicketing.text")); // NOI18N
    menuTicketing.setFont(resourceMap.getFont("jMenu3.font")); // NOI18N
    menuTicketing.setName("menuTicketing"); // NOI18N

    menuSale.setAction(actionMap.get("frameSale")); // NOI18N
    menuSale.setFont(resourceMap.getFont("menuItinerary.font")); // NOI18N
    menuSale.setText(resourceMap.getString("menuSale.text")); // NOI18N
    menuSale.setName("menuSale"); // NOI18N
    menuTicketing.add(menuSale);

    menuItinerary.setAction(actionMap.get("frameItinerary")); // NOI18N
    menuItinerary.setFont(resourceMap.getFont("menuItinerary.font")); // NOI18N
    menuItinerary.setText(resourceMap.getString("menuItinerary.text")); // NOI18N
    menuItinerary.setName("menuItinerary"); // NOI18N
    menuTicketing.add(menuItinerary);

    menuSaleRev.setAction(actionMap.get("frameSaleWithRev")); // NOI18N
    menuSaleRev.setFont(resourceMap.getFont("menuSaleRev.font")); // NOI18N
    menuSaleRev.setText(resourceMap.getString("menuSaleRev.text")); // NOI18N
    menuSaleRev.setName("menuSaleRev"); // NOI18N
    menuTicketing.add(menuSaleRev);

    menuBar.add(menuTicketing);

    menuPInvoice.setText(resourceMap.getString("menuPInvoice.text")); // NOI18N
    menuPInvoice.setFont(resourceMap.getFont("menuPInvoice.font")); // NOI18N
    menuPInvoice.setName("menuPInvoice"); // NOI18N

    menuOutThrdParty.setAction(actionMap.get("frameOutPInv")); // NOI18N
    menuOutThrdParty.setFont(resourceMap.getFont("jMenuItem9.font")); // NOI18N
    menuOutThrdParty.setText(resourceMap.getString("menuOutThrdParty.text")); // NOI18N
    menuOutThrdParty.setName("menuOutThrdParty"); // NOI18N
    menuPInvoice.add(menuOutThrdParty);

    menuPAcDocHistory.setAction(actionMap.get("framePAcDocHistory")); // NOI18N
    menuPAcDocHistory.setFont(resourceMap.getFont("menuPAcDocHistory.font")); // NOI18N
    menuPAcDocHistory.setText(resourceMap.getString("menuPAcDocHistory.text")); // NOI18N
    menuPAcDocHistory.setName("menuPAcDocHistory"); // NOI18N
    menuPInvoice.add(menuPAcDocHistory);

    jMenuItem3.setAction(actionMap.get("frameOutPCNote")); // NOI18N
    jMenuItem3.setFont(resourceMap.getFont("jMenuItem3.font")); // NOI18N
    jMenuItem3.setText(resourceMap.getString("jMenuItem3.text")); // NOI18N
    jMenuItem3.setName("jMenuItem3"); // NOI18N
    menuPInvoice.add(jMenuItem3);

    menuBar.add(menuPInvoice);

    menuSInvoice.setText(resourceMap.getString("menuSInvoice.text")); // NOI18N
    menuSInvoice.setFont(resourceMap.getFont("menuSInvoice.font")); // NOI18N
    menuSInvoice.setName("menuSInvoice"); // NOI18N

    menuNTktingInv.setFont(resourceMap.getFont("menuNTktingInv.font")); // NOI18N
    menuNTktingInv.setText(resourceMap.getString("menuNTktingInv.text")); // NOI18N
    menuNTktingInv.setName("menuNTktingInv"); // NOI18N
    menuNTktingInv.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            menuNTktingInvActionPerformed(evt);
        }
    });
    menuSInvoice.add(menuNTktingInv);

    jSeparator14.setName("jSeparator14"); // NOI18N
    menuSInvoice.add(jSeparator14);

    menuOutInv.setAction(actionMap.get("outInvoiceSummery")); // NOI18N
    menuOutInv.setFont(resourceMap.getFont("menuOutRfd.font")); // NOI18N
    menuOutInv.setText(resourceMap.getString("menuOutInv.text")); // NOI18N
    menuOutInv.setName("menuOutInv"); // NOI18N
    menuSInvoice.add(menuOutInv);

    menuOutRfd.setAction(actionMap.get("outRefundSummery")); // NOI18N
    menuOutRfd.setFont(resourceMap.getFont("menuOutRfd.font")); // NOI18N
    menuOutRfd.setText(resourceMap.getString("menuOutRfd.text")); // NOI18N
    menuOutRfd.setName("menuOutRfd"); // NOI18N
    menuSInvoice.add(menuOutRfd);

    menuInvHistory.setAction(actionMap.get("frameSAcDocHistory")); // NOI18N
    menuInvHistory.setFont(resourceMap.getFont("menuOutRfd.font")); // NOI18N
    menuInvHistory.setText(resourceMap.getString("menuInvHistory.text")); // NOI18N
    menuInvHistory.setName("menuInvHistory"); // NOI18N
    menuSInvoice.add(menuInvHistory);

    jSeparator15.setName("jSeparator15"); // NOI18N
    menuSInvoice.add(jSeparator15);

    mnuSDebitNote.setFont(resourceMap.getFont("mnuSDebitNote.font")); // NOI18N
    mnuSDebitNote.setText(resourceMap.getString("mnuSDebitNote.text")); // NOI18N
    mnuSDebitNote.setName("mnuSDebitNote"); // NOI18N
    mnuSDebitNote.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            mnuSDebitNoteActionPerformed(evt);
        }
    });
    menuSInvoice.add(mnuSDebitNote);

    mnuSCreditNote.setText(resourceMap.getString("mnuSCreditNote.text")); // NOI18N
    mnuSCreditNote.setName("mnuSCreditNote"); // NOI18N
    mnuSCreditNote.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            mnuSCreditNoteActionPerformed(evt);
        }
    });
    menuSInvoice.add(mnuSCreditNote);

    menuBar.add(menuSInvoice);

    menuBilling.setText(resourceMap.getString("menuBilling.text")); // NOI18N
    menuBilling.setFont(resourceMap.getFont("jMenu3.font")); // NOI18N
    menuBilling.setName("menuBilling"); // NOI18N

    menuBPBsp.setAction(actionMap.get("frameBPBsp")); // NOI18N
    menuBPBsp.setFont(resourceMap.getFont("menuBPBsp.font")); // NOI18N
    menuBPBsp.setText(resourceMap.getString("menuBPBsp.text")); // NOI18N
    menuBPBsp.setName("menuBPBsp"); // NOI18N
    menuBilling.add(menuBPBsp);

    menuBPThirdParty.setAction(actionMap.get("frameBPThirdParty")); // NOI18N
    menuBPThirdParty.setFont(resourceMap.getFont("jMenuItem9.font")); // NOI18N
    menuBPThirdParty.setText(resourceMap.getString("menuBPThirdParty.text")); // NOI18N
    menuBPThirdParty.setName("menuBPThirdParty"); // NOI18N
    menuBilling.add(menuBPThirdParty);

    jSeparator3.setName("jSeparator3"); // NOI18N
    menuBilling.add(jSeparator3);

    jMenuItem9.setFont(resourceMap.getFont("jMenuItem9.font")); // NOI18N
    jMenuItem9.setText(resourceMap.getString("jMenuItem9.text")); // NOI18N
    jMenuItem9.setName("jMenuItem9"); // NOI18N
    menuBilling.add(jMenuItem9);

    jSeparator7.setName("jSeparator7"); // NOI18N
    menuBilling.add(jSeparator7);

    menuBillingRptThirdParty.setAction(actionMap.get("frameBPThirdPartyRpt")); // NOI18N
    menuBillingRptThirdParty.setFont(resourceMap.getFont("menuBillingRptThirdParty.font")); // NOI18N
    menuBillingRptThirdParty.setText(resourceMap.getString("menuBillingRptThirdParty.text")); // NOI18N
    menuBillingRptThirdParty.setName("menuBillingRptThirdParty"); // NOI18N
    menuBilling.add(menuBillingRptThirdParty);

    menuBar.add(menuBilling);

    menuTransaction.setText(resourceMap.getString("menuTransaction.text")); // NOI18N
    menuTransaction.setFont(resourceMap.getFont("jMenu3.font")); // NOI18N
    menuTransaction.setName("jMenu8"); // NOI18N

    menuBTRpt.setAction(actionMap.get("framePCollectionRpt")); // NOI18N
    menuBTRpt.setFont(resourceMap.getFont("menuCashBook.font")); // NOI18N
    menuBTRpt.setText(resourceMap.getString("menuBTRpt.text")); // NOI18N
    menuBTRpt.setName("menuBTRpt"); // NOI18N
    menuTransaction.add(menuBTRpt);

    jMenuItem1.setAction(actionMap.get("Transaction")); // NOI18N
    jMenuItem1.setFont(resourceMap.getFont("menuCashBook.font")); // NOI18N
    jMenuItem1.setText(resourceMap.getString("jMenuItem1.text")); // NOI18N
    jMenuItem1.setName("jMenuItem1"); // NOI18N
    menuTransaction.add(jMenuItem1);

    menuPCollection.setAction(actionMap.get("framePCollection")); // NOI18N
    menuPCollection.setFont(resourceMap.getFont("menuCashBook.font")); // NOI18N
    menuPCollection.setText(resourceMap.getString("menuPCollection.text")); // NOI18N
    menuPCollection.setName("menuPCollection"); // NOI18N
    menuTransaction.add(menuPCollection);

    jSeparator10.setName("jSeparator10"); // NOI18N
    menuTransaction.add(jSeparator10);

    menuCashBook.setAction(actionMap.get("frameCashBook")); // NOI18N
    menuCashBook.setFont(resourceMap.getFont("menuCashBook.font")); // NOI18N
    menuCashBook.setText(resourceMap.getString("menuCashBook.text")); // NOI18N
    menuCashBook.setName("menuCashBook"); // NOI18N
    menuTransaction.add(menuCashBook);

    menuBar.add(menuTransaction);

    menuSOther.setText(resourceMap.getString("menuSOther.text")); // NOI18N
    menuSOther.setFont(resourceMap.getFont("menuSOther.font")); // NOI18N
    menuSOther.setName("menuSOther"); // NOI18N

    menuONewInv.setAction(actionMap.get("frameOAcDoc")); // NOI18N
    menuONewInv.setFont(resourceMap.getFont("menuONewInv.font")); // NOI18N
    menuONewInv.setText(resourceMap.getString("menuONewInv.text")); // NOI18N
    menuONewInv.setName("menuONewInv"); // NOI18N
    menuSOther.add(menuONewInv);

    jSeparator8.setName("jSeparator8"); // NOI18N
    menuSOther.add(jSeparator8);

    menuOInvOutstanding.setAction(actionMap.get("frameSOAcDocOutstanding")); // NOI18N
    menuOInvOutstanding.setFont(resourceMap.getFont("menuOInvOutstanding.font")); // NOI18N
    menuOInvOutstanding.setText(resourceMap.getString("menuOInvOutstanding.text")); // NOI18N
    menuOInvOutstanding.setName("menuOInvOutstanding"); // NOI18N
    menuSOther.add(menuOInvOutstanding);

    menuOInvHistory.setAction(actionMap.get("frameSOAcDocHistory")); // NOI18N
    menuOInvHistory.setFont(resourceMap.getFont("menuOInvHistory.font")); // NOI18N
    menuOInvHistory.setText(resourceMap.getString("menuOInvHistory.text")); // NOI18N
    menuOInvHistory.setName("menuOInvHistory"); // NOI18N
    menuSOther.add(menuOInvHistory);

    jSeparator9.setName("jSeparator9"); // NOI18N
    menuSOther.add(jSeparator9);

    jMenuItem11.setAction(actionMap.get("frameOServiceManager")); // NOI18N
    jMenuItem11.setFont(resourceMap.getFont("jMenuItem11.font")); // NOI18N
    jMenuItem11.setText(resourceMap.getString("jMenuItem11.text")); // NOI18N
    menuSOther.add(jMenuItem11);

    menuOInvMain.setAction(actionMap.get("frameOInvoiceMain")); // NOI18N
    menuOInvMain.setFont(resourceMap.getFont("menuOInvMain.font")); // NOI18N
    menuOInvMain.setText(resourceMap.getString("menuOInvMain.text")); // NOI18N
    menuOInvMain.setName("menuOInvMain"); // NOI18N
    menuSOther.add(menuOInvMain);

    menuBar.add(menuSOther);

    menuAccounts.setText(resourceMap.getString("menuAccounts.text")); // NOI18N
    menuAccounts.setFont(resourceMap.getFont("menuAccounts.font")); // NOI18N

    menuAccountSummery.setAction(actionMap.get("frameAccounts")); // NOI18N
    menuAccountSummery.setFont(resourceMap.getFont("menuAccountSummery.font")); // NOI18N
    menuAccountSummery.setText(resourceMap.getString("menuAccountSummery.text")); // NOI18N
    menuAccountSummery.setName("menuAccountSummery"); // NOI18N
    menuAccounts.add(menuAccountSummery);

    menuCAccounts.setAction(actionMap.get("frameCAccounts")); // NOI18N
    menuCAccounts.setFont(resourceMap.getFont("menuCAccounts.font")); // NOI18N
    menuCAccounts.setText(resourceMap.getString("menuCAccounts.text")); // NOI18N
    menuCAccounts.setName("menuCAccounts"); // NOI18N
    menuAccounts.add(menuCAccounts);

    menuVAccounts.setAction(actionMap.get("frameVAccounts")); // NOI18N
    menuVAccounts.setFont(resourceMap.getFont("menuVAccounts.font")); // NOI18N
    menuVAccounts.setText(resourceMap.getString("menuVAccounts.text")); // NOI18N
    menuVAccounts.setName("menuVAccounts"); // NOI18N
    menuAccounts.add(menuVAccounts);

    menuBar.add(menuAccounts);

    menuTools.setText(resourceMap.getString("menuTools.text")); // NOI18N
    menuTools.setFont(resourceMap.getFont("menuTools.font")); // NOI18N

    jMenuItem10.setFont(resourceMap.getFont("jMenuItem10.font")); // NOI18N
    jMenuItem10.setText(resourceMap.getString("jMenuItem10.text")); // NOI18N
    jMenuItem10.setName("jMenuItem10"); // NOI18N
    jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jMenuItem10ActionPerformed(evt);
        }
    });
    menuTools.add(jMenuItem10);

    menuBar.add(menuTools);

    menuHelp.setText(resourceMap.getString("menuHelp.text")); // NOI18N
    menuHelp.setFont(resourceMap.getFont("menuHelp.font")); // NOI18N
    menuHelp.setName("menuHelp"); // NOI18N

    menuSendContenttoVendor.setFont(resourceMap.getFont("menuSendContenttoVendor.font")); // NOI18N
    menuSendContenttoVendor.setText(resourceMap.getString("menuSendContenttoVendor.text")); // NOI18N
    menuSendContenttoVendor.setName("menuSendContenttoVendor"); // NOI18N
    menuSendContenttoVendor.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            menuSendContenttoVendorActionPerformed(evt);
        }
    });
    menuHelp.add(menuSendContenttoVendor);

    menuBar.add(menuHelp);

    statusPanel.setBackground(resourceMap.getColor("statusPanel.background")); // NOI18N
    statusPanel.setName("statusPanel"); // NOI18N
    statusPanel.setPreferredSize(new java.awt.Dimension(784, 26));
    statusPanel.setLayout(new java.awt.GridBagLayout());

    statusMessageLabel.setFont(resourceMap.getFont("statusMessageLabel.font")); // NOI18N
    statusMessageLabel.setForeground(resourceMap.getColor("statusMessageLabel.foreground")); // NOI18N
    statusMessageLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    statusMessageLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    statusMessageLabel.setName("statusMessageLabel"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 4);
    statusPanel.add(statusMessageLabel, gridBagConstraints);

    progressBar.setName("progressBar"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 4;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 2);
    statusPanel.add(progressBar, gridBagConstraints);

    lblLoggedOnUser.setFont(resourceMap.getFont("lblLoggedOnUser.font")); // NOI18N
    lblLoggedOnUser.setText(resourceMap.getString("lblLoggedOnUser.text")); // NOI18N
    lblLoggedOnUser.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    lblLoggedOnUser.setName("lblLoggedOnUser"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    statusPanel.add(lblLoggedOnUser, gridBagConstraints);

    lblLogonTime.setFont(resourceMap.getFont("lblLogonTime.font")); // NOI18N
    lblLogonTime.setText(resourceMap.getString("lblLogonTime.text")); // NOI18N
    lblLogonTime.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    lblLogonTime.setName("lblLogonTime"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    statusPanel.add(lblLogonTime, gridBagConstraints);

    dpToday.setDate(new java.util.Date());
    dpToday.setFont(resourceMap.getFont("dpToday.font")); // NOI18N
    dpToday.setEditable(false);
    dpToday.setName("dpToday"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.ipadx = 10;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 0);
    statusPanel.add(dpToday, gridBagConstraints);

    mainPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    mainPanel.setName("mainPanel"); // NOI18N
    mainPanel.setLayout(new java.awt.GridBagLayout());

    jSplitPane1.setDividerLocation(950);
    jSplitPane1.setDividerSize(4);
    jSplitPane1.setResizeWeight(0.8);
    jSplitPane1.setBorder(null);
    jSplitPane1.setName("jSplitPane1"); // NOI18N

    jSplitPane2.setDividerLocation(200);
    jSplitPane2.setDividerSize(4);
    jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
    jSplitPane2.setResizeWeight(0.2);
    jSplitPane2.setBorder(null);
    jSplitPane2.setName("jSplitPane2"); // NOI18N

    inerPanel1.setName("inerPanel1"); // NOI18N
    inerPanel1.setLayout(new java.awt.GridBagLayout());

    tabsPnr.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
    tabsPnr.setFont(resourceMap.getFont("tabsPnr.font")); // NOI18N
    tabsPnr.setName("tabsPnr"); // NOI18N
    tabsPnr.addChangeListener(tabsPnrListener);

    pnlTask.setName("pnlTask"); // NOI18N
    pnlTask.setLayout(new java.awt.GridBagLayout());

    jToolBar3.setFloatable(false);
    jToolBar3.setOrientation(1);
    jToolBar3.setRollover(true);
    jToolBar3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    jToolBar3.setName("jToolBar3"); // NOI18N

    btnDeletePnr.setIcon(new javax.swing.ImageIcon(getClass().getResource("/etsbackoffice/gui/resources/icons/delete.png"))); // NOI18N
    btnDeletePnr.setToolTipText(resourceMap.getString("btnDeletePnr.toolTipText")); // NOI18N
    btnDeletePnr.setFocusable(false);
    btnDeletePnr.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    btnDeletePnr.setName("btnDeletePnr"); // NOI18N
    btnDeletePnr.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    btnDeletePnr.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnDeletePnrActionPerformed(evt);
        }
    });
    jToolBar3.add(btnDeletePnr);

    btnPnrDetails.setAction(actionMap.get("viewPnrDetails")); // NOI18N
    btnPnrDetails.setIcon(resourceMap.getIcon("btnPnrDetails.icon")); // NOI18N
    btnPnrDetails.setText(resourceMap.getString("btnPnrDetails.text")); // NOI18N
    btnPnrDetails.setToolTipText(resourceMap.getString("btnPnrDetails.toolTipText")); // NOI18N
    btnPnrDetails.setFocusable(false);
    btnPnrDetails.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    btnPnrDetails.setName("btnPnrDetails"); // NOI18N
    btnPnrDetails.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar3.add(btnPnrDetails);

    jButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/etsbackoffice/gui/resources/icons/refresh24.png"))); // NOI18N
    jButton9.setToolTipText(resourceMap.getString("jButton9.toolTipText")); // NOI18N
    jButton9.setFocusable(false);
    jButton9.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jButton9.setName("jButton9"); // NOI18N
    jButton9.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jButton9.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton9ActionPerformed(evt);
        }
    });
    jToolBar3.add(jButton9);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    pnlTask.add(jToolBar3, gridBagConstraints);

    busyLblTask.setText(resourceMap.getString("busyLblTask.text")); // NOI18N
    busyLblTask.setName("busyLblTask"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    pnlTask.add(busyLblTask, gridBagConstraints);

    pnlPnr.setName("pnlPnr"); // NOI18N

    tblPnr.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {

        },
        new String [] {
            "", "GDSPNR", "PaxName", "TotalPax", "BookingDate", "BookedBy", "IssuedBy", "Career"
        }
    ) {
        boolean[] canEdit = new boolean [] {
            false, false, false, false, false, false, false, false
        };

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit [columnIndex];
        }
    });
    tblPnr.setFont(resourceMap.getFont("tblPnr.font")); // NOI18N
    tblPnr.setName("tblPnr"); // NOI18N
    tblPnr.setSortable(false);
    tblPnr.getTableHeader().setReorderingAllowed(false);
    tblPnr.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            tblPnrMouseClicked(evt);
        }
    });
    pnlPnr.setViewportView(tblPnr);
    tblPnr.getColumnModel().getColumn(0).setMinWidth(30);
    tblPnr.getColumnModel().getColumn(0).setPreferredWidth(30);
    tblPnr.getColumnModel().getColumn(0).setMaxWidth(50);
    tblPnr.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblPnr.columnModel.title0")); // NOI18N
    tblPnr.getColumnModel().getColumn(1).setMinWidth(80);
    tblPnr.getColumnModel().getColumn(1).setPreferredWidth(80);
    tblPnr.getColumnModel().getColumn(1).setMaxWidth(80);
    tblPnr.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblPnr.columnModel.title1")); // NOI18N
    tblPnr.getColumnModel().getColumn(2).setMinWidth(60);
    tblPnr.getColumnModel().getColumn(2).setPreferredWidth(150);
    tblPnr.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblPnr.columnModel.title2")); // NOI18N
    tblPnr.getColumnModel().getColumn(3).setMinWidth(40);
    tblPnr.getColumnModel().getColumn(3).setPreferredWidth(60);
    tblPnr.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblPnr.columnModel.title3")); // NOI18N
    tblPnr.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("tblPnr.columnModel.title4")); // NOI18N
    tblPnr.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("tblPnr.columnModel.title5")); // NOI18N
    tblPnr.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("tblPnr.columnModel.title6")); // NOI18N
    tblPnr.getColumnModel().getColumn(7).setMinWidth(60);
    tblPnr.getColumnModel().getColumn(7).setPreferredWidth(60);
    tblPnr.getColumnModel().getColumn(7).setMaxWidth(60);
    tblPnr.getColumnModel().getColumn(7).setHeaderValue(resourceMap.getString("tblPnr.columnModel.title7")); // NOI18N

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridheight = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 0);
    pnlTask.add(pnlPnr, gridBagConstraints);

    tabsPnr.addTab(resourceMap.getString("pnlTask.TabConstraints.tabTitle"), pnlTask); // NOI18N

    pnlPnrToday.setName("pnlPnrToday"); // NOI18N
    pnlPnrToday.setLayout(new java.awt.GridBagLayout());

    jScrollPane9.setName("jScrollPane9"); // NOI18N

    tblPnrToday.setBackground(resourceMap.getColor("tblPnrToday.background")); // NOI18N
    tblPnrToday.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {

        },
        new String [] {
            "", "PNR", "LeadPax", "TPax", "Career", "BookedBy", "IssuedBy", "NoInv"
        }
    ) {
        boolean[] canEdit = new boolean [] {
            false, false, false, false, false, false, false, false
        };

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit [columnIndex];
        }
    });
    tblPnrToday.setFont(resourceMap.getFont("tblPnrToday.font")); // NOI18N
    tblPnrToday.setName("tblPnrToday"); // NOI18N
    tblPnrToday.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            tblPnrTodayMouseClicked(evt);
        }
    });
    jScrollPane9.setViewportView(tblPnrToday);
    tblPnrToday.getColumnModel().getColumn(0).setMinWidth(40);
    tblPnrToday.getColumnModel().getColumn(0).setPreferredWidth(40);
    tblPnrToday.getColumnModel().getColumn(0).setMaxWidth(40);
    tblPnrToday.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblPnrToday.columnModel.title0")); // NOI18N
    tblPnrToday.getColumnModel().getColumn(1).setMinWidth(80);
    tblPnrToday.getColumnModel().getColumn(1).setPreferredWidth(80);
    tblPnrToday.getColumnModel().getColumn(1).setMaxWidth(80);
    tblPnrToday.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblPnrToday.columnModel.title1")); // NOI18N
    tblPnrToday.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblPnrToday.columnModel.title2")); // NOI18N
    tblPnrToday.getColumnModel().getColumn(3).setMinWidth(40);
    tblPnrToday.getColumnModel().getColumn(3).setPreferredWidth(40);
    tblPnrToday.getColumnModel().getColumn(3).setMaxWidth(40);
    tblPnrToday.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblPnrToday.columnModel.title6")); // NOI18N
    tblPnrToday.getColumnModel().getColumn(4).setMinWidth(60);
    tblPnrToday.getColumnModel().getColumn(4).setPreferredWidth(60);
    tblPnrToday.getColumnModel().getColumn(4).setMaxWidth(60);
    tblPnrToday.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("tblPnrToday.columnModel.title3")); // NOI18N
    tblPnrToday.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("tblPnrToday.columnModel.title4")); // NOI18N
    tblPnrToday.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("tblPnrToday.columnModel.title5")); // NOI18N
    tblPnrToday.getColumnModel().getColumn(7).setMinWidth(0);
    tblPnrToday.getColumnModel().getColumn(7).setPreferredWidth(0);
    tblPnrToday.getColumnModel().getColumn(7).setMaxWidth(0);
    tblPnrToday.getColumnModel().getColumn(7).setHeaderValue(resourceMap.getString("tblPnrToday.columnModel.title7")); // NOI18N

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridheight = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    pnlPnrToday.add(jScrollPane9, gridBagConstraints);

    jToolBar5.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    jToolBar5.setFloatable(false);
    jToolBar5.setOrientation(1);
    jToolBar5.setRollover(true);
    jToolBar5.setName("jToolBar5"); // NOI18N

    btnDeletePnr1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/etsbackoffice/gui/resources/icons/delete.png"))); // NOI18N
    btnDeletePnr1.setToolTipText(resourceMap.getString("btnDeletePnr1.toolTipText")); // NOI18N
    btnDeletePnr1.setFocusable(false);
    btnDeletePnr1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    btnDeletePnr1.setName("btnDeletePnr1"); // NOI18N
    btnDeletePnr1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    btnDeletePnr1.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnDeletePnr1ActionPerformed(evt);
        }
    });
    jToolBar5.add(btnDeletePnr1);

    jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/etsbackoffice/gui/resources/icons/details.png"))); // NOI18N
    jButton7.setToolTipText(resourceMap.getString("jButton7.toolTipText")); // NOI18N
    jButton7.setFocusable(false);
    jButton7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jButton7.setName("jButton7"); // NOI18N
    jButton7.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar5.add(jButton7);

    btnRefreshPnrToday.setIcon(new javax.swing.ImageIcon(getClass().getResource("/etsbackoffice/gui/resources/icons/refresh24.png"))); // NOI18N
    btnRefreshPnrToday.setToolTipText(resourceMap.getString("btnRefreshPnrToday.toolTipText")); // NOI18N
    btnRefreshPnrToday.setFocusable(false);
    btnRefreshPnrToday.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    btnRefreshPnrToday.setName("btnRefreshPnrToday"); // NOI18N
    btnRefreshPnrToday.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    btnRefreshPnrToday.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            btnRefreshPnrTodayMouseClicked(evt);
        }
    });
    jToolBar5.add(btnRefreshPnrToday);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    pnlPnrToday.add(jToolBar5, gridBagConstraints);

    busyLblPnrToDay.setText(resourceMap.getString("busyLblPnrToDay.text")); // NOI18N
    busyLblPnrToDay.setName("busyLblPnrToDay"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    pnlPnrToday.add(busyLblPnrToDay, gridBagConstraints);

    tabsPnr.addTab(resourceMap.getString("pnlPnrToday.TabConstraints.tabTitle"), pnlPnrToday); // NOI18N

    pnlSalesSummery.setName("pnlSalesSummery"); // NOI18N
    pnlSalesSummery.setLayout(new java.awt.GridBagLayout());

    busyLblSalesToday.setText(resourceMap.getString("busyLblSalesToday.text")); // NOI18N
    busyLblSalesToday.setName("busyLblSalesToday"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    pnlSalesSummery.add(busyLblSalesToday, gridBagConstraints);

    jToolBar6.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    jToolBar6.setFloatable(false);
    jToolBar6.setOrientation(1);
    jToolBar6.setRollover(true);
    jToolBar6.setName("jToolBar6"); // NOI18N

    btnDeletePnr2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/etsbackoffice/gui/resources/icons/delete.png"))); // NOI18N
    btnDeletePnr2.setToolTipText(resourceMap.getString("btnDeletePnr2.toolTipText")); // NOI18N
    btnDeletePnr2.setFocusable(false);
    btnDeletePnr2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    btnDeletePnr2.setName("btnDeletePnr2"); // NOI18N
    btnDeletePnr2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    btnDeletePnr2.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnDeletePnr2ActionPerformed(evt);
        }
    });
    jToolBar6.add(btnDeletePnr2);

    jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/etsbackoffice/gui/resources/icons/details.png"))); // NOI18N
    jButton8.setToolTipText(resourceMap.getString("jButton8.toolTipText")); // NOI18N
    jButton8.setFocusable(false);
    jButton8.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jButton8.setName("jButton8"); // NOI18N
    jButton8.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar6.add(jButton8);

    btnRefreshSalesSummery.setIcon(new javax.swing.ImageIcon(getClass().getResource("/etsbackoffice/gui/resources/icons/refresh24.png"))); // NOI18N
    btnRefreshSalesSummery.setToolTipText(resourceMap.getString("btnRefreshSalesSummery.toolTipText")); // NOI18N
    btnRefreshSalesSummery.setFocusable(false);
    btnRefreshSalesSummery.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    btnRefreshSalesSummery.setName("btnRefreshSalesSummery"); // NOI18N
    btnRefreshSalesSummery.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    btnRefreshSalesSummery.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            btnRefreshSalesSummeryMouseClicked(evt);
        }
    });
    btnRefreshSalesSummery.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnRefreshSalesSummeryActionPerformed(evt);
        }
    });
    jToolBar6.add(btnRefreshSalesSummery);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    pnlSalesSummery.add(jToolBar6, gridBagConstraints);

    jPanel1.setName("jPanel1"); // NOI18N
    jPanel1.setLayout(new java.awt.GridBagLayout());

    jLabel2.setFont(resourceMap.getFont("jLabel9.font")); // NOI18N
    jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
    jLabel2.setName("jLabel2"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel1.add(jLabel2, gridBagConstraints);

    jLabel3.setFont(resourceMap.getFont("jLabel9.font")); // NOI18N
    jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
    jLabel3.setName("jLabel3"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel1.add(jLabel3, gridBagConstraints);

    lblTotalRefundSelf.setBackground(resourceMap.getColor("lblBalanceTP.background")); // NOI18N
    lblTotalRefundSelf.setText(resourceMap.getString("lblTotalRefundSelf.text")); // NOI18N
    lblTotalRefundSelf.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
    lblTotalRefundSelf.setName("lblTotalRefundSelf"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipady = 5;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 0.33;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel1.add(lblTotalRefundSelf, gridBagConstraints);
    lblTotalRefundSelf.getAccessibleContext().setAccessibleName(resourceMap.getString("jLabel8.AccessibleContext.accessibleName")); // NOI18N

    jLabel9.setFont(resourceMap.getFont("jLabel9.font")); // NOI18N
    jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
    jLabel9.setName("jLabel9"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel1.add(jLabel9, gridBagConstraints);

    lblTotalIssueSelf.setBackground(resourceMap.getColor("lblBalanceTP.background")); // NOI18N
    lblTotalIssueSelf.setText(resourceMap.getString("lblTotalIssueSelf.text")); // NOI18N
    lblTotalIssueSelf.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
    lblTotalIssueSelf.setName("lblTotalIssueSelf"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipady = 5;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 0.33;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel1.add(lblTotalIssueSelf, gridBagConstraints);
    lblTotalIssueSelf.getAccessibleContext().setAccessibleName(resourceMap.getString("jLabel10.AccessibleContext.accessibleName")); // NOI18N

    lblTotalIssueTP.setBackground(resourceMap.getColor("lblBalanceTP.background")); // NOI18N
    lblTotalIssueTP.setText(resourceMap.getString("lblTotalIssueTP.text")); // NOI18N
    lblTotalIssueTP.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
    lblTotalIssueTP.setName("lblTotalIssueTP"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipady = 5;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 0.33;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel1.add(lblTotalIssueTP, gridBagConstraints);
    lblTotalIssueTP.getAccessibleContext().setAccessibleName(resourceMap.getString("jLabel11.AccessibleContext.accessibleName")); // NOI18N

    jLabel12.setFont(resourceMap.getFont("jLabel9.font")); // NOI18N
    jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
    jLabel12.setName("jLabel12"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 7;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel1.add(jLabel12, gridBagConstraints);

    lblTotalRefundTP.setBackground(resourceMap.getColor("lblBalanceTP.background")); // NOI18N
    lblTotalRefundTP.setText(resourceMap.getString("lblTotalRefundTP.text")); // NOI18N
    lblTotalRefundTP.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
    lblTotalRefundTP.setName("lblTotalRefundTP"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 7;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipady = 5;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 0.33;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel1.add(lblTotalRefundTP, gridBagConstraints);
    lblTotalRefundTP.getAccessibleContext().setAccessibleName(resourceMap.getString("jLabel13.AccessibleContext.accessibleName")); // NOI18N

    jSeparator4.setName("jSeparator4"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 8;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipady = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    jPanel1.add(jSeparator4, gridBagConstraints);

    jLabel14.setFont(resourceMap.getFont("jLabel14.font")); // NOI18N
    jLabel14.setText(resourceMap.getString("jLabel14.text")); // NOI18N
    jLabel14.setName("jLabel14"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel1.add(jLabel14, gridBagConstraints);

    jSeparator5.setName("jSeparator5"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.gridwidth = 8;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipady = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
    jPanel1.add(jSeparator5, gridBagConstraints);

    jLabel15.setFont(resourceMap.getFont("jLabel15.font")); // NOI18N
    jLabel15.setText(resourceMap.getString("jLabel15.text")); // NOI18N
    jLabel15.setName("jLabel15"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel1.add(jLabel15, gridBagConstraints);

    jLabel18.setFont(resourceMap.getFont("jLabel18.font")); // NOI18N
    jLabel18.setForeground(resourceMap.getColor("jLabel18.foreground")); // NOI18N
    jLabel18.setText(resourceMap.getString("jLabel18.text")); // NOI18N
    jLabel18.setName("jLabel18"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 7;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel1.add(jLabel18, gridBagConstraints);

    lblBalanceTP.setBackground(resourceMap.getColor("lblBalanceTP.background")); // NOI18N
    lblBalanceTP.setFont(resourceMap.getFont("lblBalanceTP.font")); // NOI18N
    lblBalanceTP.setForeground(resourceMap.getColor("jLabel18.foreground")); // NOI18N
    lblBalanceTP.setText(resourceMap.getString("lblBalanceTP.text")); // NOI18N
    lblBalanceTP.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
    lblBalanceTP.setName("lblBalanceTP"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 7;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipady = 5;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 0.33;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel1.add(lblBalanceTP, gridBagConstraints);

    jLabel16.setFont(resourceMap.getFont("jLabel16.font")); // NOI18N
    jLabel16.setForeground(resourceMap.getColor("jLabel18.foreground")); // NOI18N
    jLabel16.setText(resourceMap.getString("jLabel16.text")); // NOI18N
    jLabel16.setName("jLabel16"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel1.add(jLabel16, gridBagConstraints);

    lblBalanceSelf.setBackground(resourceMap.getColor("lblBalanceTP.background")); // NOI18N
    lblBalanceSelf.setFont(resourceMap.getFont("lblBalanceTP.font")); // NOI18N
    lblBalanceSelf.setForeground(resourceMap.getColor("jLabel18.foreground")); // NOI18N
    lblBalanceSelf.setText(resourceMap.getString("lblBalanceSelf.text")); // NOI18N
    lblBalanceSelf.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
    lblBalanceSelf.setName("lblBalanceSelf"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipady = 5;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 0.33;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel1.add(lblBalanceSelf, gridBagConstraints);

    jLabel20.setFont(resourceMap.getFont("jLabel20.font")); // NOI18N
    jLabel20.setText(resourceMap.getString("jLabel20.text")); // NOI18N
    jLabel20.setName("jLabel20"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 4;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel1.add(jLabel20, gridBagConstraints);

    lblNoOfPaxSelfIssue.setBackground(resourceMap.getColor("lblBalanceTP.background")); // NOI18N
    lblNoOfPaxSelfIssue.setText(resourceMap.getString("lblNoOfPaxSelfIssue.text")); // NOI18N
    lblNoOfPaxSelfIssue.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
    lblNoOfPaxSelfIssue.setName("lblNoOfPaxSelfIssue"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 5;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipady = 5;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 0.33;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel1.add(lblNoOfPaxSelfIssue, gridBagConstraints);

    jLabel22.setFont(resourceMap.getFont("jLabel20.font")); // NOI18N
    jLabel22.setText(resourceMap.getString("jLabel22.text")); // NOI18N
    jLabel22.setName("jLabel22"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 4;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel1.add(jLabel22, gridBagConstraints);

    lblNoOfPaxTPIssue.setBackground(resourceMap.getColor("lblBalanceTP.background")); // NOI18N
    lblNoOfPaxTPIssue.setFont(resourceMap.getFont("lblNoOfPaxTPIssue.font")); // NOI18N
    lblNoOfPaxTPIssue.setText(resourceMap.getString("lblNoOfPaxTPIssue.text")); // NOI18N
    lblNoOfPaxTPIssue.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
    lblNoOfPaxTPIssue.setName("lblNoOfPaxTPIssue"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 5;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipady = 5;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 0.33;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel1.add(lblNoOfPaxTPIssue, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridheight = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    pnlSalesSummery.add(jPanel1, gridBagConstraints);

    tabsPnr.addTab(resourceMap.getString("pnlSalesSummery.TabConstraints.tabTitle"), pnlSalesSummery); // NOI18N

    pnlReservation.setName("pnlReservation"); // NOI18N
    pnlReservation.setLayout(new java.awt.GridBagLayout());

    jToolBar7.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    jToolBar7.setFloatable(false);
    jToolBar7.setOrientation(1);
    jToolBar7.setRollover(true);
    jToolBar7.setName("jToolBar7"); // NOI18N

    btnDeletePnr3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/etsbackoffice/gui/resources/icons/delete.png"))); // NOI18N
    btnDeletePnr3.setToolTipText(resourceMap.getString("btnDeletePnr3.toolTipText")); // NOI18N
    btnDeletePnr3.setFocusable(false);
    btnDeletePnr3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    btnDeletePnr3.setName("btnDeletePnr3"); // NOI18N
    btnDeletePnr3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    btnDeletePnr3.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnDeletePnr3ActionPerformed(evt);
        }
    });
    jToolBar7.add(btnDeletePnr3);

    jButton15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/etsbackoffice/gui/resources/icons/details.png"))); // NOI18N
    jButton15.setToolTipText(resourceMap.getString("jButton15.toolTipText")); // NOI18N
    jButton15.setFocusable(false);
    jButton15.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jButton15.setName("jButton15"); // NOI18N
    jButton15.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar7.add(jButton15);

    jButton16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/etsbackoffice/gui/resources/icons/refresh24.png"))); // NOI18N
    jButton16.setToolTipText(resourceMap.getString("jButton16.toolTipText")); // NOI18N
    jButton16.setFocusable(false);
    jButton16.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jButton16.setName("jButton16"); // NOI18N
    jButton16.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jButton16.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            jButton16MouseClicked(evt);
        }
    });
    jToolBar7.add(jButton16);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    pnlReservation.add(jToolBar7, gridBagConstraints);

    busyLblPnrReservation.setText(resourceMap.getString("busyLblPnrReservation.text")); // NOI18N
    busyLblPnrReservation.setName("busyLblPnrReservation"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
    pnlReservation.add(busyLblPnrReservation, gridBagConstraints);

    jScrollPane10.setName("jScrollPane10"); // NOI18N

    tblReservation.setBackground(resourceMap.getColor("tblReservation.background")); // NOI18N
    tblReservation.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {

        },
        new String [] {
            "", "PNR", "LeadPax", "NoOfPax", "Career", "BookedBy", "BookingDate"
        }
    ) {
        boolean[] canEdit = new boolean [] {
            false, false, false, false, false, false, false
        };

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit [columnIndex];
        }
    });
    tblReservation.setFont(resourceMap.getFont("tblReservation.font")); // NOI18N
    tblReservation.setName("tblReservation"); // NOI18N
    tblReservation.getTableHeader().setReorderingAllowed(false);
    tblReservation.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            tblReservationMouseClicked(evt);
        }
    });
    jScrollPane10.setViewportView(tblReservation);
    tblReservation.getColumnModel().getColumn(0).setMinWidth(40);
    tblReservation.getColumnModel().getColumn(0).setPreferredWidth(40);
    tblReservation.getColumnModel().getColumn(0).setMaxWidth(40);
    tblReservation.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblReservation.columnModel.title0")); // NOI18N
    tblReservation.getColumnModel().getColumn(1).setMinWidth(80);
    tblReservation.getColumnModel().getColumn(1).setPreferredWidth(80);
    tblReservation.getColumnModel().getColumn(1).setMaxWidth(80);
    tblReservation.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblReservation.columnModel.title1")); // NOI18N
    tblReservation.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblReservation.columnModel.title2")); // NOI18N
    tblReservation.getColumnModel().getColumn(3).setMinWidth(60);
    tblReservation.getColumnModel().getColumn(3).setPreferredWidth(60);
    tblReservation.getColumnModel().getColumn(3).setMaxWidth(60);
    tblReservation.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblReservation.columnModel.title3")); // NOI18N
    tblReservation.getColumnModel().getColumn(4).setMinWidth(60);
    tblReservation.getColumnModel().getColumn(4).setPreferredWidth(60);
    tblReservation.getColumnModel().getColumn(4).setMaxWidth(60);
    tblReservation.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("tblReservation.columnModel.title4")); // NOI18N
    tblReservation.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("tblReservation.columnModel.title5")); // NOI18N
    tblReservation.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("tblReservation.columnModel.title6")); // NOI18N

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridheight = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    pnlReservation.add(jScrollPane10, gridBagConstraints);

    tabsPnr.addTab(resourceMap.getString("pnlReservation.TabConstraints.tabTitle"), pnlReservation); // NOI18N

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 0.8;
    gridBagConstraints.weighty = 0.5;
    inerPanel1.add(tabsPnr, gridBagConstraints);

    searchPnrPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, resourceMap.getString("searchPnrPanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, resourceMap.getFont("searchPnrPanel.border.titleFont"))); // NOI18N
    searchPnrPanel.setName("searchPnrPanel"); // NOI18N
    searchPnrPanel.setLayout(new java.awt.GridBagLayout());

    jLabel4.setFont(resourceMap.getFont("jLabel4.font")); // NOI18N
    jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
    jLabel4.setName("jLabel4"); // NOI18N
    jLabel4.setPreferredSize(new java.awt.Dimension(65, 14));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
    searchPnrPanel.add(jLabel4, gridBagConstraints);

    txtSByGDSPNR.setFont(resourceMap.getFont("txtSByGDSPNR.font")); // NOI18N
    txtSByGDSPNR.setToolTipText(resourceMap.getString("txtSByGDSPNR.toolTipText")); // NOI18N
    txtSByGDSPNR.setName("txtSByGDSPNR"); // NOI18N
    txtSByGDSPNR.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            txtSByGDSPNRActionPerformed(evt);
        }
    });
    txtSByGDSPNR.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusGained(java.awt.event.FocusEvent evt) {
            txtSByGDSPNRFocusGained(evt);
        }
        public void focusLost(java.awt.event.FocusEvent evt) {
            txtSByGDSPNRFocusLost(evt);
        }
    });
    txtSByGDSPNR.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyPressed(java.awt.event.KeyEvent evt) {
            txtSByGDSPNRKeyPressed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
    searchPnrPanel.add(txtSByGDSPNR, gridBagConstraints);

    jLabel59.setFont(resourceMap.getFont("jLabel4.font")); // NOI18N
    jLabel59.setText(resourceMap.getString("jLabel59.text")); // NOI18N
    jLabel59.setName("jLabel59"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 2);
    searchPnrPanel.add(jLabel59, gridBagConstraints);

    txtSByInvRef.setFont(resourceMap.getFont("txtSByGDSPNR.font")); // NOI18N
    txtSByInvRef.setToolTipText(resourceMap.getString("txtSByInvRef.toolTipText")); // NOI18N
    txtSByInvRef.setName("txtSByInvRef"); // NOI18N
    txtSByInvRef.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            txtSByInvRefActionPerformed(evt);
        }
    });
    txtSByInvRef.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusGained(java.awt.event.FocusEvent evt) {
            txtSByInvRefFocusGained(evt);
        }
        public void focusLost(java.awt.event.FocusEvent evt) {
            txtSByInvRefFocusLost(evt);
        }
    });
    txtSByInvRef.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyPressed(java.awt.event.KeyEvent evt) {
            txtSByInvRefKeyPressed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
    searchPnrPanel.add(txtSByInvRef, gridBagConstraints);

    btnRTPNR.setFont(resourceMap.getFont("btnRTPNR.font")); // NOI18N
    btnRTPNR.setIcon(resourceMap.getIcon("btnRTPNR.icon")); // NOI18N
    btnRTPNR.setText(resourceMap.getString("btnRTPNR.text")); // NOI18N
    btnRTPNR.setName("btnRTPNR"); // NOI18N
    btnRTPNR.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnRTPNRActionPerformed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    searchPnrPanel.add(btnRTPNR, gridBagConstraints);

    jLabel1.setFont(resourceMap.getFont("jLabel4.font")); // NOI18N
    jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
    jLabel1.setName("jLabel1"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
    searchPnrPanel.add(jLabel1, gridBagConstraints);

    txtSByPaxName.setFont(resourceMap.getFont("txtSByGDSPNR.font")); // NOI18N
    txtSByPaxName.setToolTipText(resourceMap.getString("txtSByPaxName.toolTipText")); // NOI18N
    txtSByPaxName.setName("txtSByPaxName"); // NOI18N
    txtSByPaxName.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            txtSByPaxNameActionPerformed(evt);
        }
    });
    txtSByPaxName.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusGained(java.awt.event.FocusEvent evt) {
            txtSByPaxNameFocusGained(evt);
        }
        public void focusLost(java.awt.event.FocusEvent evt) {
            txtSByPaxNameFocusLost(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
    searchPnrPanel.add(txtSByPaxName, gridBagConstraints);

    jLabel5.setFont(resourceMap.getFont("jLabel4.font")); // NOI18N
    jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
    jLabel5.setName("jLabel5"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
    searchPnrPanel.add(jLabel5, gridBagConstraints);

    txtSByTktNo.setFont(resourceMap.getFont("txtSByGDSPNR.font")); // NOI18N
    txtSByTktNo.setText(resourceMap.getString("txtSByTktNo.text")); // NOI18N
    txtSByTktNo.setName("txtSByTktNo"); // NOI18N
    txtSByTktNo.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            txtSByTktNoActionPerformed(evt);
        }
    });
    txtSByTktNo.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusGained(java.awt.event.FocusEvent evt) {
            txtSByTktNoFocusGained(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
    searchPnrPanel.add(txtSByTktNo, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 0.2;
    gridBagConstraints.weighty = 0.5;
    inerPanel1.add(searchPnrPanel, gridBagConstraints);

    jSplitPane2.setTopComponent(inerPanel1);

    innerPanel2.setName("innerPanel2"); // NOI18N
    innerPanel2.setLayout(new java.awt.GridBagLayout());

    tabsTicket.setFont(resourceMap.getFont("tabsTicket.font")); // NOI18N
    tabsTicket.setName("tabsTicket"); // NOI18N
    tabsTicket.setPreferredSize(new java.awt.Dimension(100, 35));

    jPanel5.setAutoscrolls(true);
    jPanel5.setName("jPanel5"); // NOI18N
    jPanel5.setLayout(new java.awt.GridBagLayout());

    jToolBar4.setFloatable(false);
    jToolBar4.setRollover(true);
    jToolBar4.setName("jToolBar4"); // NOI18N

    jButton10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/etsbackoffice/gui/resources/icons/details.png"))); // NOI18N
    jButton10.setToolTipText(resourceMap.getString("jButton10.toolTipText")); // NOI18N
    jButton10.setFocusable(false);
    jButton10.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jButton10.setName("jButton10"); // NOI18N
    jButton10.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jButton10.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton10ActionPerformed(evt);
        }
    });
    jToolBar4.add(jButton10);

    btnDeleteTicket.setIcon(new javax.swing.ImageIcon(getClass().getResource("/etsbackoffice/gui/resources/icons/delete.png"))); // NOI18N
    btnDeleteTicket.setToolTipText(resourceMap.getString("btnDeleteTicket.toolTipText")); // NOI18N
    btnDeleteTicket.setFocusable(false);
    btnDeleteTicket.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    btnDeleteTicket.setName("btnDeleteTicket"); // NOI18N
    btnDeleteTicket.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    btnDeleteTicket.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnDeleteTicketActionPerformed(evt);
        }
    });
    jToolBar4.add(btnDeleteTicket);

    btnRefundTicket.setIcon(new javax.swing.ImageIcon(getClass().getResource("/etsbackoffice/gui/resources/icons/refund.png"))); // NOI18N
    btnRefundTicket.setToolTipText(resourceMap.getString("btnRefundTicket.toolTipText")); // NOI18N
    btnRefundTicket.setFocusable(false);
    btnRefundTicket.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    btnRefundTicket.setName("btnRefundTicket"); // NOI18N
    btnRefundTicket.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    btnRefundTicket.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnRefundTicketActionPerformed(evt);
        }
    });
    jToolBar4.add(btnRefundTicket);

    btnVoidTicket.setIcon(new javax.swing.ImageIcon(getClass().getResource("/etsbackoffice/gui/resources/icons/void22.png"))); // NOI18N
    btnVoidTicket.setToolTipText(resourceMap.getString("btnVoidTicket.toolTipText")); // NOI18N
    btnVoidTicket.setFocusable(false);
    btnVoidTicket.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    btnVoidTicket.setName("btnVoidTicket"); // NOI18N
    btnVoidTicket.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    btnVoidTicket.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            btnVoidTicketMouseClicked(evt);
        }
    });
    jToolBar4.add(btnVoidTicket);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    jPanel5.add(jToolBar4, gridBagConstraints);

    jScrollPane14.setName("jScrollPane14"); // NOI18N

    tblTicket.setBackground(resourceMap.getColor("tblTicket.background")); // NOI18N
    tblTicket.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {

        },
        new String [] {
            "P.Name", "Status", "Date", "NetFare", "G.Fare", "Com/Disc", "NetPayable", "P/L", "Inv"
        }
    ) {
        Class[] types = new Class [] {
            java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class
        };
        boolean[] canEdit = new boolean [] {
            false, false, false, false, false, false, false, false, false
        };

        public Class getColumnClass(int columnIndex) {
            return types [columnIndex];
        }

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit [columnIndex];
        }
    });
    tblTicket.setEditable(false);
    tblTicket.setFont(resourceMap.getFont("tblTicket.font")); // NOI18N
    tblTicket.setName("tblTicket"); // NOI18N
    tblTicket.setSortable(false);
    tblTicket.getTableHeader().setReorderingAllowed(false);
    tblTicket.getSelectionModel().addListSelectionListener(tblTicketListener);
    tblTicket.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            tblTicketMouseClicked(evt);
        }
    });
    jScrollPane14.setViewportView(tblTicket);
    tblTicket.getColumnModel().getColumn(0).setPreferredWidth(150);
    tblTicket.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblTicket.columnModel.title0")); // NOI18N
    tblTicket.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblTicket.columnModel.title2")); // NOI18N
    tblTicket.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblTicket.columnModel.title3")); // NOI18N
    tblTicket.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblTicket.columnModel.title4")); // NOI18N
    tblTicket.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("tblTicket.columnModel.title6")); // NOI18N
    tblTicket.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("tblTicket.columnModel.title7")); // NOI18N
    tblTicket.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("tblTicket.columnModel.title9")); // NOI18N
    tblTicket.getColumnModel().getColumn(7).setHeaderValue(resourceMap.getString("tblTicket.columnModel.title10")); // NOI18N
    tblTicket.getColumnModel().getColumn(8).setMinWidth(30);
    tblTicket.getColumnModel().getColumn(8).setPreferredWidth(25);
    tblTicket.getColumnModel().getColumn(8).setMaxWidth(30);
    tblTicket.getColumnModel().getColumn(8).setHeaderValue(resourceMap.getString("tblTicket.columnModel.title11")); // NOI18N

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel5.add(jScrollPane14, gridBagConstraints);

    tabsTicket.addTab(resourceMap.getString("jPanel5.TabConstraints.tabTitle"), jPanel5); // NOI18N

    jPanel3.setName("jPanel3"); // NOI18N
    jPanel3.setLayout(new java.awt.GridBagLayout());

    lblBookingAgtName.setFont(resourceMap.getFont("txtGDS.font")); // NOI18N
    lblBookingAgtName.setText(resourceMap.getString("lblBookingAgtName.text")); // NOI18N
    lblBookingAgtName.setName("lblBookingAgtName"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(6, 2, 2, 2);
    jPanel3.add(lblBookingAgtName, gridBagConstraints);

    txtGdsPnr.setBackground(resourceMap.getColor("txtCareer.background")); // NOI18N
    txtGdsPnr.setFont(resourceMap.getFont("txtGDS.font")); // NOI18N
    txtGdsPnr.setToolTipText(resourceMap.getString("txtGdsPnr.toolTipText")); // NOI18N
    txtGdsPnr.setMinimumSize(new java.awt.Dimension(80, 16));
    txtGdsPnr.setName("txtGdsPnr"); // NOI18N
    txtGdsPnr.setPreferredSize(new java.awt.Dimension(120, 20));
    txtGdsPnr.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyReleased(java.awt.event.KeyEvent evt) {
            txtGdsPnrKeyReleased(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(6, 2, 2, 2);
    jPanel3.add(txtGdsPnr, gridBagConstraints);

    txtBookingAgtOid.setFont(resourceMap.getFont("txtGDS.font")); // NOI18N
    txtBookingAgtOid.setToolTipText(resourceMap.getString("txtBookingAgtOid.toolTipText")); // NOI18N
    txtBookingAgtOid.setMinimumSize(new java.awt.Dimension(80, 16));
    txtBookingAgtOid.setName("txtBookingAgtOid"); // NOI18N
    txtBookingAgtOid.setPreferredSize(new java.awt.Dimension(120, 20));
    txtBookingAgtOid.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyReleased(java.awt.event.KeyEvent evt) {
            txtBookingAgtOidKeyReleased(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel3.add(txtBookingAgtOid, gridBagConstraints);

    lblBookingAgtIATA.setFont(resourceMap.getFont("txtGDS.font")); // NOI18N
    lblBookingAgtIATA.setText(resourceMap.getString("lblBookingAgtIATA.text")); // NOI18N
    lblBookingAgtIATA.setName("lblBookingAgtIATA"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel3.add(lblBookingAgtIATA, gridBagConstraints);

    lblTicketingAgtName.setFont(resourceMap.getFont("txtGDS.font")); // NOI18N
    lblTicketingAgtName.setText(resourceMap.getString("lblTicketingAgtName.text")); // NOI18N
    lblTicketingAgtName.setName("lblTicketingAgtName"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel3.add(lblTicketingAgtName, gridBagConstraints);

    txtTicketingAgtOid.setFont(resourceMap.getFont("txtGDS.font")); // NOI18N
    txtTicketingAgtOid.setToolTipText(resourceMap.getString("txtTicketingAgtOid.toolTipText")); // NOI18N
    txtTicketingAgtOid.setMinimumSize(new java.awt.Dimension(80, 16));
    txtTicketingAgtOid.setName("txtTicketingAgtOid"); // NOI18N
    txtTicketingAgtOid.setPreferredSize(new java.awt.Dimension(120, 20));
    txtTicketingAgtOid.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyReleased(java.awt.event.KeyEvent evt) {
            txtTicketingAgtOidKeyReleased(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel3.add(txtTicketingAgtOid, gridBagConstraints);

    txtSegments.setBackground(resourceMap.getColor("txtCareer.background")); // NOI18N
    txtSegments.setEditable(false);
    txtSegments.setFont(resourceMap.getFont("txtGDS.font")); // NOI18N
    txtSegments.setMinimumSize(new java.awt.Dimension(80, 16));
    txtSegments.setName("txtSegments"); // NOI18N
    txtSegments.setPreferredSize(new java.awt.Dimension(120, 20));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel3.add(txtSegments, gridBagConstraints);

    lblSegment.setFont(resourceMap.getFont("txtGDS.font")); // NOI18N
    lblSegment.setText(resourceMap.getString("lblSegment.text")); // NOI18N
    lblSegment.setName("lblSegment"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel3.add(lblSegment, gridBagConstraints);

    dpPnrCreationDate.setFont(resourceMap.getFont("txtGDS.font")); // NOI18N
    dpPnrCreationDate.setMinimumSize(new java.awt.Dimension(112, 18));
    dpPnrCreationDate.setName("dpPnrCreationDate"); // NOI18N
    dpPnrCreationDate.setPreferredSize(new java.awt.Dimension(200, 20));
    dpPnrCreationDate.setToolTipText(resourceMap.getString("dpPnrCreationDate.toolTipText")); // NOI18N
    dpPnrCreationDate.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseReleased(java.awt.event.MouseEvent evt) {
            dpPnrCreationDateMouseReleased(evt);
        }
    });
    dpPnrCreationDate.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            dpPnrCreationDateActionPerformed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel3.add(dpPnrCreationDate, gridBagConstraints);

    txtCareer.setBackground(resourceMap.getColor("txtCareer.background")); // NOI18N
    txtCareer.setEditable(false);
    txtCareer.setFont(resourceMap.getFont("txtGDS.font")); // NOI18N
    txtCareer.setToolTipText(resourceMap.getString("txtCareer.toolTipText")); // NOI18N
    txtCareer.setMinimumSize(new java.awt.Dimension(80, 16));
    txtCareer.setName("txtCareer"); // NOI18N
    txtCareer.setPreferredSize(new java.awt.Dimension(200, 20));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel3.add(txtCareer, gridBagConstraints);

    txtVendorPnr.setBackground(resourceMap.getColor("txtCareer.background")); // NOI18N
    txtVendorPnr.setEditable(false);
    txtVendorPnr.setFont(resourceMap.getFont("txtGDS.font")); // NOI18N
    txtVendorPnr.setToolTipText(resourceMap.getString("txtVendorPnr.toolTipText")); // NOI18N
    txtVendorPnr.setMinimumSize(new java.awt.Dimension(80, 16));
    txtVendorPnr.setName("txtVendorPnr"); // NOI18N
    txtVendorPnr.setPreferredSize(new java.awt.Dimension(200, 20));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel3.add(txtVendorPnr, gridBagConstraints);

    txtGDS.setBackground(resourceMap.getColor("txtCareer.background")); // NOI18N
    txtGDS.setEditable(false);
    txtGDS.setFont(resourceMap.getFont("txtGDS.font")); // NOI18N
    txtGDS.setToolTipText(resourceMap.getString("txtGDS.toolTipText")); // NOI18N
    txtGDS.setMinimumSize(new java.awt.Dimension(80, 16));
    txtGDS.setName("txtGDS"); // NOI18N
    txtGDS.setPreferredSize(new java.awt.Dimension(200, 20));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(6, 2, 2, 2);
    jPanel3.add(txtGDS, gridBagConstraints);

    lblGDS.setFont(resourceMap.getFont("txtGDS.font")); // NOI18N
    lblGDS.setText(resourceMap.getString("lblGDS.text")); // NOI18N
    lblGDS.setName("lblGDS"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(6, 2, 2, 2);
    jPanel3.add(lblGDS, gridBagConstraints);

    lblVendorPNR.setFont(resourceMap.getFont("txtGDS.font")); // NOI18N
    lblVendorPNR.setText(resourceMap.getString("lblVendorPNR.text")); // NOI18N
    lblVendorPNR.setName("lblVendorPNR"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel3.add(lblVendorPNR, gridBagConstraints);

    lblCareer.setFont(resourceMap.getFont("txtGDS.font")); // NOI18N
    lblCareer.setText(resourceMap.getString("lblCareer.text")); // NOI18N
    lblCareer.setName("lblCareer"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel3.add(lblCareer, gridBagConstraints);

    lblTicketingAgtIATA.setFont(resourceMap.getFont("txtGDS.font")); // NOI18N
    lblTicketingAgtIATA.setText(resourceMap.getString("lblTicketingAgtIATA.text")); // NOI18N
    lblTicketingAgtIATA.setName("lblTicketingAgtIATA"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel3.add(lblTicketingAgtIATA, gridBagConstraints);

    jLabel7.setFont(resourceMap.getFont("jLabel7.font")); // NOI18N
    jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
    jLabel7.setName("jLabel7"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 4;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.insets = new java.awt.Insets(6, 2, 2, 2);
    jPanel3.add(jLabel7, gridBagConstraints);

    jScrollPane8.setName("jScrollPane8"); // NOI18N

    txtRestrictions.setColumns(16);
    txtRestrictions.setEditable(false);
    txtRestrictions.setFont(resourceMap.getFont("txtRestrictions.font")); // NOI18N
    txtRestrictions.setLineWrap(true);
    txtRestrictions.setRows(5);
    txtRestrictions.setToolTipText(resourceMap.getString("txtRestrictions.toolTipText")); // NOI18N
    txtRestrictions.setName("txtRestrictions"); // NOI18N
    jScrollPane8.setViewportView(txtRestrictions);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 4;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.gridheight = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 0.33;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel3.add(jScrollPane8, gridBagConstraints);

    tabsTicket.addTab(resourceMap.getString("jPanel3.TabConstraints.tabTitle"), jPanel3); // NOI18N

    jPanel13.setName("jPanel13"); // NOI18N
    jPanel13.setLayout(new java.awt.CardLayout(2, 2));

    jScrollPane12.setName("jScrollPane12"); // NOI18N

    tblFareLine.setBackground(resourceMap.getColor("tblFareLine.background")); // NOI18N
    tblFareLine.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {

        },
        new String [] {
            "PaxName", "Status", "BaseFare", "Tax", "BspCom", "NetFare", "BF3P", "Tax3p", "Total3p"
        }
    ) {
        boolean[] canEdit = new boolean [] {
            false, false, false, false, false, false, false, false, false
        };

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit [columnIndex];
        }
    });
    tblFareLine.setFont(resourceMap.getFont("tblFareLine.font")); // NOI18N
    tblFareLine.setName("tblFareLine"); // NOI18N
    tblFareLine.setSortable(false);
    tblFareLine.getTableHeader().setReorderingAllowed(false);
    jScrollPane12.setViewportView(tblFareLine);
    tblFareLine.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblFareLine.columnModel.title0")); // NOI18N
    tblFareLine.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblFareLine.columnModel.title1")); // NOI18N
    tblFareLine.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblFareLine.columnModel.title2")); // NOI18N
    tblFareLine.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblFareLine.columnModel.title3")); // NOI18N
    tblFareLine.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("tblFareLine.columnModel.title8")); // NOI18N
    tblFareLine.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("tblFareLine.columnModel.title4")); // NOI18N
    tblFareLine.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("tblFareLine.columnModel.title5")); // NOI18N
    tblFareLine.getColumnModel().getColumn(7).setHeaderValue(resourceMap.getString("tblFareLine.columnModel.title6")); // NOI18N
    tblFareLine.getColumnModel().getColumn(8).setHeaderValue(resourceMap.getString("tblFareLine.columnModel.title7")); // NOI18N

    jPanel13.add(jScrollPane12, "card2");

    tabsTicket.addTab(resourceMap.getString("jPanel13.TabConstraints.tabTitle"), jPanel13); // NOI18N

    jPanel6.setName("jPanel6"); // NOI18N
    jPanel6.setLayout(new java.awt.GridBagLayout());

    jScrollPane3.setName("jScrollPane3"); // NOI18N

    tblItinerary.setFont(resourceMap.getFont("tblItinerary.font")); // NOI18N
    tblItinerary.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {

        },
        new String [] {
            "D.From", "D.To", "D.Date", "D.Time", "A.Date", "A.Time", "AirLine", "FlNo", "Class", "Status"
        }
    ));
    tblItinerary.setName("tblItinerary"); // NOI18N
    tblItinerary.getTableHeader().setReorderingAllowed(false);
    tblItinerary.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            tblItineraryMouseClicked(evt);
        }
    });
    jScrollPane3.setViewportView(tblItinerary);
    tblItinerary.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblItinerary.columnModel.title0")); // NOI18N
    tblItinerary.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblItinerary.columnModel.title1")); // NOI18N
    tblItinerary.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblItinerary.columnModel.title2")); // NOI18N
    tblItinerary.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblItinerary.columnModel.title3")); // NOI18N
    tblItinerary.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("tblItinerary.columnModel.title4")); // NOI18N
    tblItinerary.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("tblItinerary.columnModel.title5")); // NOI18N
    tblItinerary.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("tblItinerary.columnModel.title6")); // NOI18N
    tblItinerary.getColumnModel().getColumn(7).setHeaderValue(resourceMap.getString("tblItinerary.columnModel.title7")); // NOI18N
    tblItinerary.getColumnModel().getColumn(8).setHeaderValue(resourceMap.getString("tblItinerary.columnModel.title8")); // NOI18N
    tblItinerary.getColumnModel().getColumn(9).setHeaderValue(resourceMap.getString("tblItinerary.columnModel.title9")); // NOI18N

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel6.add(jScrollPane3, gridBagConstraints);

    jToolBar8.setFloatable(false);
    jToolBar8.setRollover(true);
    jToolBar8.setName("jToolBar8"); // NOI18N

    btnRefreshItinerary.setIcon(new javax.swing.ImageIcon(getClass().getResource("/etsbackoffice/gui/resources/icons/refresh24.png"))); // NOI18N
    btnRefreshItinerary.setToolTipText(resourceMap.getString("btnRefreshItinerary.toolTipText")); // NOI18N
    btnRefreshItinerary.setFocusable(false);
    btnRefreshItinerary.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    btnRefreshItinerary.setName("btnRefreshItinerary"); // NOI18N
    btnRefreshItinerary.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    btnRefreshItinerary.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnRefreshItineraryActionPerformed(evt);
        }
    });
    jToolBar8.add(btnRefreshItinerary);

    btnDeleteSegment.setIcon(new javax.swing.ImageIcon(getClass().getResource("/etsbackoffice/gui/resources/icons/delete.png"))); // NOI18N
    btnDeleteSegment.setToolTipText(resourceMap.getString("btnDeleteSegment.toolTipText")); // NOI18N
    btnDeleteSegment.setFocusable(false);
    btnDeleteSegment.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    btnDeleteSegment.setName("btnDeleteSegment"); // NOI18N
    btnDeleteSegment.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    btnDeleteSegment.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnDeleteSegmentActionPerformed(evt);
        }
    });
    jToolBar8.add(btnDeleteSegment);

    btnViewSegment.setIcon(resourceMap.getIcon("btnViewSegment.icon")); // NOI18N
    btnViewSegment.setText(resourceMap.getString("btnViewSegment.text")); // NOI18N
    btnViewSegment.setFocusable(false);
    btnViewSegment.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    btnViewSegment.setName("btnViewSegment"); // NOI18N
    btnViewSegment.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    btnViewSegment.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnViewSegmentActionPerformed(evt);
        }
    });
    jToolBar8.add(btnViewSegment);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    jPanel6.add(jToolBar8, gridBagConstraints);

    tabsTicket.addTab(resourceMap.getString("jPanel6.TabConstraints.tabTitle"), jPanel6); // NOI18N

    jPanel9.setName("jPanel9"); // NOI18N
    jPanel9.setLayout(new java.awt.GridBagLayout());

    txtTextField.setName("txtTextField"); // NOI18N
    txtTextField.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            txtTextFieldActionPerformed(evt);
        }
    });
    txtTextField.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyPressed(java.awt.event.KeyEvent evt) {
            txtTextFieldKeyPressed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
    jPanel9.add(txtTextField, gridBagConstraints);

    jScrollPane7.setName("jScrollPane7"); // NOI18N

    txtPnrRemark.setEditable(false);
    txtPnrRemark.setName("txtPnrRemark"); // NOI18N
    jScrollPane7.setViewportView(txtPnrRemark);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridheight = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel9.add(jScrollPane7, gridBagConstraints);

    jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
    jLabel6.setName("jLabel6"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
    jPanel9.add(jLabel6, gridBagConstraints);

    jLabel35.setText(resourceMap.getString("jLabel35.text")); // NOI18N
    jLabel35.setName("jLabel35"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel9.add(jLabel35, gridBagConstraints);

    busyLabelRemark.setName("busyLabelRemark"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    jPanel9.add(busyLabelRemark, gridBagConstraints);

    tabsTicket.addTab(resourceMap.getString("jPanel9.TabConstraints.tabTitle"), jPanel9); // NOI18N

    jPanel15.setName("jPanel15"); // NOI18N
    jPanel15.setLayout(new java.awt.GridBagLayout());

    jScrollPane6.setName("jScrollPane6"); // NOI18N

    tblTransaction.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {

        },
        new String [] {
            "Date", "T.Type", "T.Amount", "T.Ref"
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
    jScrollPane6.setViewportView(tblTransaction);
    tblTransaction.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblTransaction.columnModel.title0")); // NOI18N
    tblTransaction.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblTransaction.columnModel.title1")); // NOI18N
    tblTransaction.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblTransaction.columnModel.title2")); // NOI18N
    tblTransaction.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblTransaction.columnModel.title3")); // NOI18N

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridheight = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel15.add(jScrollPane6, gridBagConstraints);

    btnLoadTransaction.setIcon(new javax.swing.ImageIcon(getClass().getResource("/etsbackoffice/gui/resources/icons/refresh24.png"))); // NOI18N
    btnLoadTransaction.setFocusable(false);
    btnLoadTransaction.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    btnLoadTransaction.setName("btnLoadTransaction"); // NOI18N
    btnLoadTransaction.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    btnLoadTransaction.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            btnLoadTransactionMouseClicked(evt);
        }
    });
    jPanel15.add(btnLoadTransaction, new java.awt.GridBagConstraints());

    busyIcon.setText(resourceMap.getString("busyIcon.text")); // NOI18N
    busyIcon.setName("busyIcon"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(6, 2, 2, 2);
    jPanel15.add(busyIcon, gridBagConstraints);

    tabsTicket.addTab(resourceMap.getString("jPanel15.TabConstraints.tabTitle"), jPanel15); // NOI18N

    jPanel4.setName("jPanel4"); // NOI18N
    jPanel4.setLayout(new java.awt.GridBagLayout());

    jScrollPane5.setName("jScrollPane5"); // NOI18N

    tblOtherService.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {
            {null, null, null, null, null, null, null, null}
        },
        new String [] {
            "", "ServiceTitle", "ServiceCost", "GrossCharge", "Discount", "Quantity", "NetPayable", "Rev"
        }
    ) {
        boolean[] canEdit = new boolean [] {
            false, true, false, true, true, false, true, false
        };

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit [columnIndex];
        }
    });
    tblOtherService.setFont(resourceMap.getFont("tblOtherService.font")); // NOI18N
    tblOtherService.setName("tblOtherService"); // NOI18N
    tblOtherService.setSortable(false);
    tblOtherService.getTableHeader().setReorderingAllowed(false);
    jScrollPane5.setViewportView(tblOtherService);
    tblOtherService.getColumnModel().getColumn(0).setPreferredWidth(40);
    tblOtherService.getColumnModel().getColumn(0).setMaxWidth(40);
    tblOtherService.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblOtherService.columnModel.title0")); // NOI18N
    tblOtherService.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblOtherService.columnModel.title1")); // NOI18N
    tblOtherService.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblOtherService.columnModel.title2")); // NOI18N
    tblOtherService.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblOtherService.columnModel.title3")); // NOI18N
    tblOtherService.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("tblOtherService.columnModel.title4")); // NOI18N
    tblOtherService.getColumnModel().getColumn(5).setPreferredWidth(60);
    tblOtherService.getColumnModel().getColumn(5).setMaxWidth(60);
    tblOtherService.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("tblOtherService.columnModel.title5")); // NOI18N
    tblOtherService.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("tblOtherService.columnModel.title6")); // NOI18N
    tblOtherService.getColumnModel().getColumn(7).setHeaderValue(resourceMap.getString("tblOtherService.columnModel.title7")); // NOI18N

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 0);
    jPanel4.add(jScrollPane5, gridBagConstraints);

    jToolBar9.setFloatable(false);
    jToolBar9.setOrientation(1);
    jToolBar9.setRollover(true);
    jToolBar9.setBorder(null);
    jToolBar9.setName("jToolBar9"); // NOI18N
    jToolBar9.setToolTipText(resourceMap.getString("jToolBar9.toolTipText")); // NOI18N

    btnRemoveLine1.setIcon(resourceMap.getIcon("btnRemoveLine1.icon")); // NOI18N
    btnRemoveLine1.setToolTipText(resourceMap.getString("btnRemoveLine1.toolTipText")); // NOI18N
    btnRemoveLine1.setFocusable(false);
    btnRemoveLine1.setName("btnRemoveLine1"); // NOI18N
    btnRemoveLine1.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnRemoveLine1ActionPerformed(evt);
        }
    });
    jToolBar9.add(btnRemoveLine1);

    btnReturnItem.setIcon(resourceMap.getIcon("btnReturnItem.icon")); // NOI18N
    btnReturnItem.setToolTipText(resourceMap.getString("btnReturnItem.toolTipText")); // NOI18N
    btnReturnItem.setFocusable(false);
    btnReturnItem.setName("btnReturnItem"); // NOI18N
    btnReturnItem.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnReturnItemActionPerformed(evt);
        }
    });
    jToolBar9.add(btnReturnItem);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
    gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
    jPanel4.add(jToolBar9, gridBagConstraints);

    tabsTicket.addTab(resourceMap.getString("jPanel4.TabConstraints.tabTitle"), jPanel4); // NOI18N

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 0.7;
    innerPanel2.add(tabsTicket, gridBagConstraints);

    pnlAcDocs.setBorder(javax.swing.BorderFactory.createTitledBorder(null, resourceMap.getString("pnlAcDocs.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, resourceMap.getFont("pnlAcDocs.border.titleFont"))); // NOI18N
    pnlAcDocs.setName("pnlAcDocs"); // NOI18N
    pnlAcDocs.setLayout(new java.awt.GridBagLayout());

    lblCAcFinalBalance.setFont(resourceMap.getFont("lblCAcFinalBalance.font")); // NOI18N
    lblCAcFinalBalance.setForeground(resourceMap.getColor("lblCAcFinalBalance.foreground")); // NOI18N
    lblCAcFinalBalance.setText(resourceMap.getString("lblCAcFinalBalance.text")); // NOI18N
    lblCAcFinalBalance.setName("lblCAcFinalBalance"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    pnlAcDocs.add(lblCAcFinalBalance, gridBagConstraints);

    busyLabelAcDoc.setText(resourceMap.getString("busyLabelAcDoc.text")); // NOI18N
    busyLabelAcDoc.setName("busyLabelAcDoc"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 1;
    pnlAcDocs.add(busyLabelAcDoc, gridBagConstraints);

    jToolBar1.setFloatable(false);
    jToolBar1.setRollover(true);
    jToolBar1.setName("jToolBar1"); // NOI18N

    btnEditAcDoc.setIcon(resourceMap.getIcon("btnEditAcDoc.icon")); // NOI18N
    btnEditAcDoc.setText(resourceMap.getString("btnEditAcDoc.text")); // NOI18N
    btnEditAcDoc.setToolTipText(resourceMap.getString("btnEditAcDoc.toolTipText")); // NOI18N
    btnEditAcDoc.setFocusable(false);
    btnEditAcDoc.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    btnEditAcDoc.setName("btnEditAcDoc"); // NOI18N
    btnEditAcDoc.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    btnEditAcDoc.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnEditAcDocActionPerformed(evt);
        }
    });
    jToolBar1.add(btnEditAcDoc);

    btnVewAcDoc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/etsbackoffice/gui/resources/icons/details.png"))); // NOI18N
    btnVewAcDoc.setToolTipText(resourceMap.getString("btnVewAcDoc.toolTipText")); // NOI18N
    btnVewAcDoc.setFocusable(false);
    btnVewAcDoc.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    btnVewAcDoc.setName("btnVewAcDoc"); // NOI18N
    btnVewAcDoc.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    btnVewAcDoc.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnVewAcDocActionPerformed(evt);
        }
    });
    jToolBar1.add(btnVewAcDoc);

    btnPrintAcDoc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/etsbackoffice/gui/resources/icons/print24.png"))); // NOI18N
    btnPrintAcDoc.setToolTipText(resourceMap.getString("btnPrintAcDoc.toolTipText")); // NOI18N
    btnPrintAcDoc.setFocusable(false);
    btnPrintAcDoc.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    btnPrintAcDoc.setName("btnPrintAcDoc"); // NOI18N
    btnPrintAcDoc.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar1.add(btnPrintAcDoc);

    btnDelAcDoc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/etsbackoffice/gui/resources/icons/delete.png"))); // NOI18N
    btnDelAcDoc.setToolTipText(resourceMap.getString("btnDelAcDoc.toolTipText")); // NOI18N
    btnDelAcDoc.setFocusable(false);
    btnDelAcDoc.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    btnDelAcDoc.setName("btnDelAcDoc"); // NOI18N
    btnDelAcDoc.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    btnDelAcDoc.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnDelAcDocActionPerformed(evt);
        }
    });
    jToolBar1.add(btnDelAcDoc);
    btnDelAcDoc.getAccessibleContext().setAccessibleName(resourceMap.getString("btnDelAcDoc.AccessibleContext.accessibleName")); // NOI18N

    btnVoidAcDoc.setIcon(resourceMap.getIcon("btnVoidAcDoc.icon")); // NOI18N
    btnVoidAcDoc.setText(resourceMap.getString("btnVoidAcDoc.text")); // NOI18N
    btnVoidAcDoc.setToolTipText(resourceMap.getString("btnVoidAcDoc.toolTipText")); // NOI18N
    btnVoidAcDoc.setFocusable(false);
    btnVoidAcDoc.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    btnVoidAcDoc.setName("btnVoidAcDoc"); // NOI18N
    btnVoidAcDoc.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    btnVoidAcDoc.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnVoidAcDocActionPerformed(evt);
        }
    });
    jToolBar1.add(btnVoidAcDoc);

    lblAcDocWarning.setFont(resourceMap.getFont("lblAcDocWarning.font")); // NOI18N
    lblAcDocWarning.setForeground(resourceMap.getColor("lblAcDocWarning.foreground")); // NOI18N
    lblAcDocWarning.setText(resourceMap.getString("lblAcDocWarning.text")); // NOI18N
    lblAcDocWarning.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
    lblAcDocWarning.setName("lblAcDocWarning"); // NOI18N
    jToolBar1.add(lblAcDocWarning);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    pnlAcDocs.add(jToolBar1, gridBagConstraints);

    tabsAcDoc.setFont(resourceMap.getFont("tabsAcDoc.font")); // NOI18N
    tabsAcDoc.setName("tabsAcDoc"); // NOI18N

    jPanel11.setName("jPanel11"); // NOI18N
    jPanel11.setLayout(new java.awt.CardLayout(2, 2));

    jScrollPane18.setName("jScrollPane18"); // NOI18N

    tblSalesAcDoc.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {

        },
        new String [] {
            "Type", "IssueDate", "Ref", "Terms", "Amount", "Status"
        }
    ) {
        boolean[] canEdit = new boolean [] {
            false, false, false, false, false, false
        };

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit [columnIndex];
        }
    });
    tblSalesAcDoc.setEditable(false);
    tblSalesAcDoc.setFont(resourceMap.getFont("tblSalesAcDoc.font")); // NOI18N
    tblSalesAcDoc.setName("tblSalesAcDoc"); // NOI18N
    tblSalesAcDoc.setSortable(false);
    tblSalesAcDoc.getTableHeader().setReorderingAllowed(false);
    tblSalesAcDoc.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            tblSalesAcDocMouseClicked(evt);
        }
    });
    jScrollPane18.setViewportView(tblSalesAcDoc);
    tblSalesAcDoc.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblSalesAcDoc.columnModel.title0")); // NOI18N
    tblSalesAcDoc.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblSalesAcDoc.columnModel.title1")); // NOI18N
    tblSalesAcDoc.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblSalesAcDoc.columnModel.title2")); // NOI18N
    tblSalesAcDoc.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblSalesAcDoc.columnModel.title3")); // NOI18N
    tblSalesAcDoc.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("tblSalesAcDoc.columnModel.title5")); // NOI18N
    tblSalesAcDoc.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("tblSalesAcDoc.columnModel.title4")); // NOI18N

    jPanel11.add(jScrollPane18, "card2");

    tabsAcDoc.addTab(resourceMap.getString("jPanel11.TabConstraints.tabTitle"), jPanel11); // NOI18N

    jPanel12.setName("jPanel12"); // NOI18N
    jPanel12.setLayout(new java.awt.CardLayout(2, 2));

    jScrollPane11.setName("jScrollPane11"); // NOI18N

    tblPurchaseAcDoc.setFont(resourceMap.getFont("tblPurchaseAcDoc.font")); // NOI18N
    tblPurchaseAcDoc.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {

        },
        new String [] {
            "Type", "Date", "RecipientRef", "VendorRef", "Terms", "Vendor", "Amount"
        }
    ) {
        boolean[] canEdit = new boolean [] {
            false, false, false, true, true, true, false
        };

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit [columnIndex];
        }
    });
    tblPurchaseAcDoc.setName("tblPurchaseAcDoc"); // NOI18N
    tblPurchaseAcDoc.getTableHeader().setReorderingAllowed(false);
    tblPurchaseAcDoc.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            tblPurchaseAcDocMouseClicked(evt);
        }
    });
    jScrollPane11.setViewportView(tblPurchaseAcDoc);
    tblPurchaseAcDoc.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblPurchaseAcDoc.columnModel.title0")); // NOI18N
    tblPurchaseAcDoc.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblPurchaseAcDoc.columnModel.title1")); // NOI18N
    tblPurchaseAcDoc.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblPurchaseAcDoc.columnModel.title2")); // NOI18N
    tblPurchaseAcDoc.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblPurchaseAcDoc.columnModel.title3")); // NOI18N
    tblPurchaseAcDoc.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("tblPurchaseAcDoc.columnModel.title4")); // NOI18N
    tblPurchaseAcDoc.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("tblPurchaseAcDoc.columnModel.title5")); // NOI18N
    tblPurchaseAcDoc.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("tblPurchaseAcDoc.columnModel.title6")); // NOI18N

    jPanel12.add(jScrollPane11, "card2");

    tabsAcDoc.addTab(resourceMap.getString("jPanel12.TabConstraints.tabTitle"), jPanel12); // NOI18N

    jPanel2.setName("jPanel2"); // NOI18N
    jPanel2.setLayout(new java.awt.GridBagLayout());

    jScrollPane15.setName("jScrollPane15"); // NOI18N

    txtPPane.setBackground(resourceMap.getColor("txtSPane.background")); // NOI18N
    txtPPane.setEditable(false);
    txtPPane.setFont(resourceMap.getFont("txtSPane.font")); // NOI18N
    txtPPane.setName("txtPPane"); // NOI18N
    jScrollPane15.setViewportView(txtPPane);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel2.add(jScrollPane15, gridBagConstraints);

    jScrollPane16.setName("jScrollPane16"); // NOI18N

    txtSPane.setBackground(resourceMap.getColor("txtSPane.background")); // NOI18N
    txtSPane.setEditable(false);
    txtSPane.setFont(resourceMap.getFont("txtSPane.font")); // NOI18N
    txtSPane.setName("txtSPane"); // NOI18N
    jScrollPane16.setViewportView(txtSPane);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel2.add(jScrollPane16, gridBagConstraints);

    jScrollPane17.setName("jScrollPane17"); // NOI18N

    txtBPane.setBackground(resourceMap.getColor("txtSPane.background")); // NOI18N
    txtBPane.setEditable(false);
    txtBPane.setFont(resourceMap.getFont("txtSPane.font")); // NOI18N
    txtBPane.setName("txtBPane"); // NOI18N
    jScrollPane17.setViewportView(txtBPane);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel2.add(jScrollPane17, gridBagConstraints);

    tabsAcDoc.addTab(resourceMap.getString("jPanel2.TabConstraints.tabTitle"), jPanel2); // NOI18N

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    pnlAcDocs.add(tabsAcDoc, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 0.3;
    innerPanel2.add(pnlAcDocs, gridBagConstraints);

    pnlTktDetails.setMinimumSize(new java.awt.Dimension(260, 10));
    pnlTktDetails.setName("pnlTktDetails"); // NOI18N
    pnlTktDetails.setLayout(new java.awt.GridBagLayout());

    jLabel8.setFont(resourceMap.getFont("lblBaseFare.font")); // NOI18N
    jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
    jLabel8.setName("jLabel8"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 0);
    pnlTktDetails.add(jLabel8, gridBagConstraints);

    jLabel11.setFont(resourceMap.getFont("lblBaseFare.font")); // NOI18N
    jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
    jLabel11.setName("jLabel11"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 0);
    pnlTktDetails.add(jLabel11, gridBagConstraints);

    jLabel17.setFont(resourceMap.getFont("lblBaseFare.font")); // NOI18N
    jLabel17.setText(resourceMap.getString("jLabel17.text")); // NOI18N
    jLabel17.setName("jLabel17"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 0);
    pnlTktDetails.add(jLabel17, gridBagConstraints);

    lblBaseFare.setFont(resourceMap.getFont("lblBaseFare.font")); // NOI18N
    lblBaseFare.setText(resourceMap.getString("lblBaseFare.text")); // NOI18N
    lblBaseFare.setName("lblBaseFare"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 8;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 0);
    pnlTktDetails.add(lblBaseFare, gridBagConstraints);

    lblTax.setFont(resourceMap.getFont("lblBaseFare.font")); // NOI18N
    lblTax.setText(resourceMap.getString("lblTax.text")); // NOI18N
    lblTax.setName("lblTax"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 8;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 0);
    pnlTktDetails.add(lblTax, gridBagConstraints);

    lblTotalFare.setFont(resourceMap.getFont("lblBaseFare.font")); // NOI18N
    lblTotalFare.setText(resourceMap.getString("lblTotalFare.text")); // NOI18N
    lblTotalFare.setName("lblTotalFare"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 9;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 0);
    pnlTktDetails.add(lblTotalFare, gridBagConstraints);

    lblBspCom.setFont(resourceMap.getFont("lblBaseFare.font")); // NOI18N
    lblBspCom.setText(resourceMap.getString("lblBspCom.text")); // NOI18N
    lblBspCom.setName("lblBspCom"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 9;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 0);
    pnlTktDetails.add(lblBspCom, gridBagConstraints);

    lblGrossFare.setFont(resourceMap.getFont("lblBaseFare.font")); // NOI18N
    lblGrossFare.setText(resourceMap.getString("lblGrossFare.text")); // NOI18N
    lblGrossFare.setName("lblGrossFare"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 11;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 0);
    pnlTktDetails.add(lblGrossFare, gridBagConstraints);

    lblDisc.setFont(resourceMap.getFont("lblBaseFare.font")); // NOI18N
    lblDisc.setText(resourceMap.getString("lblDisc.text")); // NOI18N
    lblDisc.setName("lblDisc"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 11;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 0);
    pnlTktDetails.add(lblDisc, gridBagConstraints);

    lblAtol.setFont(resourceMap.getFont("lblBaseFare.font")); // NOI18N
    lblAtol.setText(resourceMap.getString("lblAtol.text")); // NOI18N
    lblAtol.setName("lblAtol"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 12;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 0);
    pnlTktDetails.add(lblAtol, gridBagConstraints);

    lblNetToPay.setFont(resourceMap.getFont("lblBaseFare.font")); // NOI18N
    lblNetToPay.setText(resourceMap.getString("lblNetToPay.text")); // NOI18N
    lblNetToPay.setName("lblNetToPay"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 12;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 0);
    pnlTktDetails.add(lblNetToPay, gridBagConstraints);

    jLabel24.setFont(resourceMap.getFont("lblBaseFare.font")); // NOI18N
    jLabel24.setForeground(resourceMap.getColor("jLabel24.foreground")); // NOI18N
    jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    jLabel24.setText(resourceMap.getString("jLabel24.text")); // NOI18N
    jLabel24.setName("jLabel24"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 13;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 0);
    pnlTktDetails.add(jLabel24, gridBagConstraints);

    txtTktNo.setFont(resourceMap.getFont("txtTktNo.font")); // NOI18N
    txtTktNo.setMinimumSize(new java.awt.Dimension(80, 20));
    txtTktNo.setName("txtTktNo"); // NOI18N
    txtTktNo.setPreferredSize(new java.awt.Dimension(60, 20));
    txtTktNo.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusGained(java.awt.event.FocusEvent evt) {
            txtTktNoFocusGained(evt);
        }
    });
    txtTktNo.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyReleased(java.awt.event.KeyEvent evt) {
            txtTktNoKeyReleased(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    pnlTktDetails.add(txtTktNo, gridBagConstraints);

    txtStatus.setEditable(false);
    txtStatus.setFont(resourceMap.getFont("txtStatus.font")); // NOI18N
    txtStatus.setMinimumSize(new java.awt.Dimension(50, 20));
    txtStatus.setName("txtStatus"); // NOI18N
    txtStatus.setPreferredSize(new java.awt.Dimension(65, 20));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
    pnlTktDetails.add(txtStatus, gridBagConstraints);

    txtBaseFare.setBackground(resourceMap.getColor("txtBaseFare.background")); // NOI18N
    txtBaseFare.setFont(resourceMap.getFont("txtBaseFare.font")); // NOI18N
    txtBaseFare.setMinimumSize(new java.awt.Dimension(50, 20));
    txtBaseFare.setName("txtBaseFare"); // NOI18N
    txtBaseFare.setPreferredSize(new java.awt.Dimension(65, 20));
    txtBaseFare.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusGained(java.awt.event.FocusEvent evt) {
            txtBaseFareFocusGained(evt);
        }
        public void focusLost(java.awt.event.FocusEvent evt) {
            txtBaseFareFocusLost(evt);
        }
    });
    txtBaseFare.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyReleased(java.awt.event.KeyEvent evt) {
            txtBaseFareKeyReleased(evt);
        }
        public void keyTyped(java.awt.event.KeyEvent evt) {
            txtBaseFareKeyTyped(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 8;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
    pnlTktDetails.add(txtBaseFare, gridBagConstraints);

    txtTax.setBackground(resourceMap.getColor("txtTax.background")); // NOI18N
    txtTax.setFont(resourceMap.getFont("txtTax.font")); // NOI18N
    txtTax.setMinimumSize(new java.awt.Dimension(50, 20));
    txtTax.setName("txtTax"); // NOI18N
    txtTax.setPreferredSize(new java.awt.Dimension(65, 20));
    txtTax.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusGained(java.awt.event.FocusEvent evt) {
            txtTaxFocusGained(evt);
        }
        public void focusLost(java.awt.event.FocusEvent evt) {
            txtTaxFocusLost(evt);
        }
    });
    txtTax.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyReleased(java.awt.event.KeyEvent evt) {
            txtTaxKeyReleased(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 8;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
    pnlTktDetails.add(txtTax, gridBagConstraints);

    txtTotalFare.setBackground(resourceMap.getColor("txtTotalFare.background")); // NOI18N
    txtTotalFare.setEditable(false);
    txtTotalFare.setFont(resourceMap.getFont("txtTotalFare.font")); // NOI18N
    txtTotalFare.setForeground(resourceMap.getColor("txtTotalFare.foreground")); // NOI18N
    txtTotalFare.setMinimumSize(new java.awt.Dimension(50, 20));
    txtTotalFare.setName("txtTotalFare"); // NOI18N
    txtTotalFare.setPreferredSize(new java.awt.Dimension(65, 20));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 9;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
    pnlTktDetails.add(txtTotalFare, gridBagConstraints);

    txtBspCom.setBackground(resourceMap.getColor("txtBspCom.background")); // NOI18N
    txtBspCom.setFont(resourceMap.getFont("txtBspCom.font")); // NOI18N
    txtBspCom.setMinimumSize(new java.awt.Dimension(50, 20));
    txtBspCom.setName("txtBspCom"); // NOI18N
    txtBspCom.setPreferredSize(new java.awt.Dimension(65, 20));
    txtBspCom.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusGained(java.awt.event.FocusEvent evt) {
            txtBspComFocusGained(evt);
        }
        public void focusLost(java.awt.event.FocusEvent evt) {
            txtBspComFocusLost(evt);
        }
    });
    txtBspCom.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyReleased(java.awt.event.KeyEvent evt) {
            txtBspComKeyReleased(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 9;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
    pnlTktDetails.add(txtBspCom, gridBagConstraints);

    txtGrossFare.setBackground(resourceMap.getColor("txtGrossFare.background")); // NOI18N
    txtGrossFare.setFont(resourceMap.getFont("txtGrossFare.font")); // NOI18N
    txtGrossFare.setToolTipText(resourceMap.getString("txtGrossFare.toolTipText")); // NOI18N
    txtGrossFare.setMinimumSize(new java.awt.Dimension(50, 20));
    txtGrossFare.setName("txtGrossFare"); // NOI18N
    txtGrossFare.setPreferredSize(new java.awt.Dimension(65, 20));
    txtGrossFare.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusGained(java.awt.event.FocusEvent evt) {
            txtGrossFareFocusGained(evt);
        }
        public void focusLost(java.awt.event.FocusEvent evt) {
            txtGrossFareFocusLost(evt);
        }
    });
    txtGrossFare.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyReleased(java.awt.event.KeyEvent evt) {
            txtGrossFareKeyReleased(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 11;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
    pnlTktDetails.add(txtGrossFare, gridBagConstraints);

    txtDisc.setBackground(resourceMap.getColor("txtDisc.background")); // NOI18N
    txtDisc.setFont(resourceMap.getFont("txtDisc.font")); // NOI18N
    txtDisc.setMinimumSize(new java.awt.Dimension(50, 20));
    txtDisc.setName("txtDisc"); // NOI18N
    txtDisc.setPreferredSize(new java.awt.Dimension(65, 20));
    txtDisc.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusGained(java.awt.event.FocusEvent evt) {
            txtDiscFocusGained(evt);
        }
        public void focusLost(java.awt.event.FocusEvent evt) {
            txtDiscFocusLost(evt);
        }
    });
    txtDisc.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyReleased(java.awt.event.KeyEvent evt) {
            txtDiscKeyReleased(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 11;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
    pnlTktDetails.add(txtDisc, gridBagConstraints);

    txtAtol.setBackground(resourceMap.getColor("txtAtol.background")); // NOI18N
    txtAtol.setFont(resourceMap.getFont("txtAtol.font")); // NOI18N
    txtAtol.setMinimumSize(new java.awt.Dimension(50, 20));
    txtAtol.setName("txtAtol"); // NOI18N
    txtAtol.setPreferredSize(new java.awt.Dimension(65, 20));
    txtAtol.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusGained(java.awt.event.FocusEvent evt) {
            txtAtolFocusGained(evt);
        }
        public void focusLost(java.awt.event.FocusEvent evt) {
            txtAtolFocusLost(evt);
        }
    });
    txtAtol.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyReleased(java.awt.event.KeyEvent evt) {
            txtAtolKeyReleased(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 12;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
    pnlTktDetails.add(txtAtol, gridBagConstraints);

    txtNetToPay.setBackground(resourceMap.getColor("txtNetToPay.background")); // NOI18N
    txtNetToPay.setEditable(false);
    txtNetToPay.setFont(resourceMap.getFont("txtNetToPay.font")); // NOI18N
    txtNetToPay.setForeground(resourceMap.getColor("txtNetToPay.foreground")); // NOI18N
    txtNetToPay.setMinimumSize(new java.awt.Dimension(50, 20));
    txtNetToPay.setName("txtNetToPay"); // NOI18N
    txtNetToPay.setPreferredSize(new java.awt.Dimension(65, 20));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 12;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
    pnlTktDetails.add(txtNetToPay, gridBagConstraints);

    lblProfitLoss.setFont(resourceMap.getFont("lblBaseFare.font")); // NOI18N
    lblProfitLoss.setForeground(resourceMap.getColor("jLabel24.foreground")); // NOI18N
    lblProfitLoss.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    lblProfitLoss.setText(resourceMap.getString("lblProfitLoss.text")); // NOI18N
    lblProfitLoss.setName("lblProfitLoss"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 13;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 2);
    pnlTktDetails.add(lblProfitLoss, gridBagConstraints);

    datePicker.setFont(resourceMap.getFont("datePicker.font")); // NOI18N
    datePicker.setMaximumSize(new java.awt.Dimension(200, 80));
    datePicker.setMinimumSize(new java.awt.Dimension(80, 22));
    datePicker.setName("datePicker"); // NOI18N
    datePicker.setPreferredSize(new java.awt.Dimension(60, 20));
    datePicker.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            datePickerActionPerformed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    pnlTktDetails.add(datePicker, gridBagConstraints);

    jSeparator12.setName("jSeparator12"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 10;
    gridBagConstraints.gridwidth = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.ipady = 3;
    gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
    pnlTktDetails.add(jSeparator12, gridBagConstraints);

    lblGFMessage.setFont(resourceMap.getFont("lblGFMessage.font")); // NOI18N
    lblGFMessage.setForeground(resourceMap.getColor("lblGFMessage.foreground")); // NOI18N
    lblGFMessage.setText(resourceMap.getString("lblGFMessage.text")); // NOI18N
    lblGFMessage.setName("lblGFMessage"); // NOI18N
    lblGFMessage.setPreferredSize(new java.awt.Dimension(14, 20));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 14;
    gridBagConstraints.gridwidth = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
    pnlTktDetails.add(lblGFMessage, gridBagConstraints);

    txtNumAirCode.setFont(resourceMap.getFont("txtNumAirCode.font")); // NOI18N
    txtNumAirCode.setMinimumSize(new java.awt.Dimension(50, 20));
    txtNumAirCode.setName("txtNumAirCode"); // NOI18N
    txtNumAirCode.setPreferredSize(new java.awt.Dimension(65, 20));
    txtNumAirCode.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusGained(java.awt.event.FocusEvent evt) {
            txtNumAirCodeFocusGained(evt);
        }
    });
    txtNumAirCode.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyReleased(java.awt.event.KeyEvent evt) {
            txtNumAirCodeKeyReleased(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
    pnlTktDetails.add(txtNumAirCode, gridBagConstraints);

    jScrollPane2.setName("jScrollPane2"); // NOI18N

    txtName.setColumns(12);
    txtName.setEditable(false);
    txtName.setFont(resourceMap.getFont("txtName.font")); // NOI18N
    txtName.setLineWrap(true);
    txtName.setRows(3);
    txtName.setName("txtName"); // NOI18N
    jScrollPane2.setViewportView(txtName);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
    pnlTktDetails.add(jScrollPane2, gridBagConstraints);

    btnPTicket.setFont(resourceMap.getFont("btnPTicket.font")); // NOI18N
    btnPTicket.setForeground(resourceMap.getColor("btnPTicket.foreground")); // NOI18N
    btnPTicket.setText(resourceMap.getString("btnPTicket.text")); // NOI18N
    btnPTicket.setToolTipText(resourceMap.getString("btnPTicket.toolTipText")); // NOI18N
    btnPTicket.setName("btnPTicket"); // NOI18N
    btnPTicket.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnPTicketActionPerformed(evt);
        }
    });
    btnPTicket.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyReleased(java.awt.event.KeyEvent evt) {
            btnPTicketKeyReleased(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 15;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
    pnlTktDetails.add(btnPTicket, gridBagConstraints);

    btnNTicket.setText(resourceMap.getString("btnNTicket.text")); // NOI18N
    btnNTicket.setToolTipText(resourceMap.getString("btnNTicket.toolTipText")); // NOI18N
    btnNTicket.setName("btnNTicket"); // NOI18N
    btnNTicket.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnNTicketActionPerformed(evt);
        }
    });
    btnNTicket.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyReleased(java.awt.event.KeyEvent evt) {
            btnNTicketKeyReleased(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 15;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
    gridBagConstraints.weighty = 1.0;
    pnlTktDetails.add(btnNTicket, gridBagConstraints);

    jSeparator6.setName("jSeparator6"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 7;
    gridBagConstraints.gridwidth = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipady = 3;
    gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
    pnlTktDetails.add(jSeparator6, gridBagConstraints);

    jLabel10.setFont(resourceMap.getFont("jLabel10.font")); // NOI18N
    jLabel10.setForeground(resourceMap.getColor("jLabel10.foreground")); // NOI18N
    jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
    jLabel10.setName("jLabel10"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    pnlTktDetails.add(jLabel10, gridBagConstraints);

    jSeparator13.setName("jSeparator13"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridwidth = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
    pnlTktDetails.add(jSeparator13, gridBagConstraints);

    txtRetrievedPnr.setBackground(resourceMap.getColor("txtRetrievedPnr.background")); // NOI18N
    txtRetrievedPnr.setFont(resourceMap.getFont("txtRetrievedPnr.font")); // NOI18N
    txtRetrievedPnr.setForeground(resourceMap.getColor("txtRetrievedPnr.foreground")); // NOI18N
    txtRetrievedPnr.setText(resourceMap.getString("txtRetrievedPnr.text")); // NOI18N
    txtRetrievedPnr.setName("txtRetrievedPnr"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    pnlTktDetails.add(txtRetrievedPnr, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridheight = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.LAST_LINE_START;
    innerPanel2.add(pnlTktDetails, gridBagConstraints);

    jSplitPane2.setRightComponent(innerPanel2);

    jSplitPane1.setLeftComponent(jSplitPane2);

    innerPanel3.setName("innerPanel3"); // NOI18N
    innerPanel3.setLayout(new java.awt.GridBagLayout());

    jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
    jButton2.setName("jButton2"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    innerPanel3.add(jButton2, gridBagConstraints);

    jSplitPane1.setRightComponent(innerPanel3);

    rightPanel.setName("rightPanel"); // NOI18N
    rightPanel.setLayout(new java.awt.GridBagLayout());

    jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(null, resourceMap.getString("jPanel7.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, resourceMap.getFont("jPanel7.border.titleFont"))); // NOI18N
    jPanel7.setFont(resourceMap.getFont("jPanel7.font")); // NOI18N
    jPanel7.setName("jPanel7"); // NOI18N
    jPanel7.setLayout(new java.awt.GridBagLayout());

    cmbSearchResult.setEnabled(false);
    cmbSearchResult.setName("cmbSearchResult"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 8;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel7.add(cmbSearchResult, gridBagConstraints);

    jScrollPane4.setName("jScrollPane4"); // NOI18N

    txtContactableDetails.setColumns(16);
    txtContactableDetails.setEditable(false);
    txtContactableDetails.setFont(resourceMap.getFont("txtContactableDetails.font")); // NOI18N
    txtContactableDetails.setLineWrap(true);
    txtContactableDetails.setRows(5);
    txtContactableDetails.setName("txtContactableDetails"); // NOI18N
    jScrollPane4.setViewportView(txtContactableDetails);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel7.add(jScrollPane4, gridBagConstraints);

    lblContactibleDetails.setText(resourceMap.getString("lblContactibleDetails.text")); // NOI18N
    lblContactibleDetails.setName("lblContactibleDetails"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel7.add(lblContactibleDetails, gridBagConstraints);

    txtContactableSearch.setColumns(20);
    txtContactableSearch.setToolTipText(resourceMap.getString("txtContactableSearch.toolTipText")); // NOI18N
    txtContactableSearch.setEnabled(false);
    txtContactableSearch.setName("txtContactableSearch"); // NOI18N
    txtContactableSearch.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            txtContactableSearchActionPerformed(evt);
        }
    });
    txtContactableSearch.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusGained(java.awt.event.FocusEvent evt) {
            txtContactableSearchFocusGained(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel7.add(txtContactableSearch, gridBagConstraints);

    btnContactableSearch.setFont(resourceMap.getFont("btnContactableSearch.font")); // NOI18N
    btnContactableSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/etsbackoffice/gui/resources/icons/search24.png"))); // NOI18N
    btnContactableSearch.setText(resourceMap.getString("btnContactableSearch.text")); // NOI18N
    btnContactableSearch.setEnabled(false);
    btnContactableSearch.setName("btnContactableSearch"); // NOI18N
    btnContactableSearch.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnContactableSearchActionPerformed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel7.add(btnContactableSearch, gridBagConstraints);

    buttonGroup1.add(rdoAgent);
    rdoAgent.setText(resourceMap.getString("rdoAgent.text")); // NOI18N
    rdoAgent.setEnabled(false);
    rdoAgent.setName("rdoAgent"); // NOI18N
    rdoAgent.addActionListener(radioAgent);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 0);
    jPanel7.add(rdoAgent, gridBagConstraints);

    buttonGroup1.add(rdoCustomer);
    rdoCustomer.setText(resourceMap.getString("rdoCustomer.text")); // NOI18N
    rdoCustomer.setEnabled(false);
    rdoCustomer.setName("rdoCustomer"); // NOI18N
    rdoCustomer.addActionListener(radioCustomer);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel7.add(rdoCustomer, gridBagConstraints);

    lblSearchClient.setText(resourceMap.getString("lblSearchClient.text")); // NOI18N
    lblSearchClient.setEnabled(false);
    lblSearchClient.setName("lblSearchClient"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel7.add(lblSearchClient, gridBagConstraints);

    busyLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    busyLabel.setFont(resourceMap.getFont("busyLabel.font")); // NOI18N
    busyLabel.setName("busyLabel"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel7.add(busyLabel, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 0.4;
    rightPanel.add(jPanel7, gridBagConstraints);

    jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder(null, resourceMap.getString("jPanel14.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, resourceMap.getFont("jPanel14.border.titleFont"))); // NOI18N
    jPanel14.setName("jPanel14"); // NOI18N
    jPanel14.setLayout(new java.awt.GridBagLayout());

    cmbVendor.setFont(resourceMap.getFont("cmbVendor.font")); // NOI18N
    cmbVendor.setName("cmbVendor"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel14.add(cmbVendor, gridBagConstraints);

    busyLabelVendor.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    busyLabelVendor.setText(resourceMap.getString("busyLabelVendor.text")); // NOI18N
    busyLabelVendor.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
    busyLabelVendor.setName("busyLabelVendor"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.ipadx = 1;
    gridBagConstraints.ipady = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel14.add(busyLabelVendor, gridBagConstraints);

    btnLoadVendors.setFont(resourceMap.getFont("btnLoadVendors.font")); // NOI18N
    btnLoadVendors.setIcon(resourceMap.getIcon("btnLoadVendors.icon")); // NOI18N
    btnLoadVendors.setText(resourceMap.getString("btnLoadVendors.text")); // NOI18N
    btnLoadVendors.setName("btnLoadVendors"); // NOI18N
    btnLoadVendors.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnLoadVendorsActionPerformed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel14.add(btnLoadVendors, gridBagConstraints);

    jScrollPane13.setName("jScrollPane13"); // NOI18N

    txtVendorDetails.setColumns(16);
    txtVendorDetails.setEditable(false);
    txtVendorDetails.setFont(resourceMap.getFont("txtVendorDetails.font")); // NOI18N
    txtVendorDetails.setLineWrap(true);
    txtVendorDetails.setRows(5);
    txtVendorDetails.setName("txtVendorDetails"); // NOI18N
    jScrollPane13.setViewportView(txtVendorDetails);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel14.add(jScrollPane13, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 0.4;
    rightPanel.add(jPanel14, gridBagConstraints);

    jSplitPane1.setRightComponent(rightPanel);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    mainPanel.add(jSplitPane1, gridBagConstraints);

    toolBar.setFloatable(false);
    toolBar.setRollover(true);
    toolBar.setName("toolBar"); // NOI18N

    btnNewCustomer.setFont(resourceMap.getFont("btnRefresh.font")); // NOI18N
    btnNewCustomer.setIcon(resourceMap.getIcon("btnNewCustomer.icon")); // NOI18N
    btnNewCustomer.setText(resourceMap.getString("btnNewCustomer.text")); // NOI18N
    btnNewCustomer.setToolTipText(resourceMap.getString("btnNewCustomer.toolTipText")); // NOI18N
    btnNewCustomer.setFocusable(false);
    btnNewCustomer.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    btnNewCustomer.setName("btnNewCustomer"); // NOI18N
    btnNewCustomer.setPreferredSize(new java.awt.Dimension(0, 40));
    btnNewCustomer.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnNewCustomerActionPerformed(evt);
        }
    });
    toolBar.add(btnNewCustomer);

    btnSave.setAction(actionMap.get("savePnr")); // NOI18N
    btnSave.setFont(resourceMap.getFont("btnSave.font")); // NOI18N
    btnSave.setIcon(resourceMap.getIcon("btnSave.icon")); // NOI18N
    btnSave.setText(resourceMap.getString("btnSave.text")); // NOI18N
    btnSave.setToolTipText(resourceMap.getString("btnSave.toolTipText")); // NOI18N
    btnSave.setFocusable(false);
    btnSave.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    btnSave.setName("btnSave"); // NOI18N
    btnSave.setPreferredSize(new java.awt.Dimension(0, 40));
    btnSave.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnSaveActionPerformed(evt);
        }
    });
    toolBar.add(btnSave);

    btnRefresh.setAction(actionMap.get("refreshMainFrame")); // NOI18N
    btnRefresh.setFont(resourceMap.getFont("btnRefresh.font")); // NOI18N
    btnRefresh.setIcon(resourceMap.getIcon("btnRefresh.icon")); // NOI18N
    btnRefresh.setText(resourceMap.getString("btnRefresh.text")); // NOI18N
    btnRefresh.setToolTipText(resourceMap.getString("btnRefresh.toolTipText")); // NOI18N
    btnRefresh.setFocusable(false);
    btnRefresh.setName("btnRefresh"); // NOI18N
    btnRefresh.setPreferredSize(new java.awt.Dimension(0, 40));
    btnRefresh.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnRefreshActionPerformed(evt);
        }
    });
    toolBar.add(btnRefresh);

    jButton1.setFont(resourceMap.getFont("btnRefresh.font")); // NOI18N
    jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/etsbackoffice/gui/resources/icons/edit.png"))); // NOI18N
    jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
    jButton1.setToolTipText(resourceMap.getString("jButton1.toolTipText")); // NOI18N
    jButton1.setFocusable(false);
    jButton1.setName("jButton1"); // NOI18N
    jButton1.setPreferredSize(new java.awt.Dimension(0, 40));
    toolBar.add(jButton1);

    btnOutstandingInvoice.setAction(actionMap.get("outInvoiceSummery")); // NOI18N
    btnOutstandingInvoice.setFont(resourceMap.getFont("btnRefresh.font")); // NOI18N
    btnOutstandingInvoice.setIcon(resourceMap.getIcon("btnOutstandingInvoice.icon")); // NOI18N
    btnOutstandingInvoice.setText(resourceMap.getString("btnOutstandingInvoice.text")); // NOI18N
    btnOutstandingInvoice.setToolTipText(resourceMap.getString("btnOutstandingInvoice.toolTipText")); // NOI18N
    btnOutstandingInvoice.setFocusable(false);
    btnOutstandingInvoice.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
    btnOutstandingInvoice.setName("btnOutstandingInvoice"); // NOI18N
    btnOutstandingInvoice.setPreferredSize(new java.awt.Dimension(0, 40));
    toolBar.add(btnOutstandingInvoice);

    btnTransaction.setAction(actionMap.get("Transaction")); // NOI18N
    btnTransaction.setFont(resourceMap.getFont("btnRefresh.font")); // NOI18N
    btnTransaction.setIcon(resourceMap.getIcon("btnTransaction.icon")); // NOI18N
    btnTransaction.setText(resourceMap.getString("btnTransaction.text")); // NOI18N
    btnTransaction.setToolTipText(resourceMap.getString("btnTransaction.toolTipText")); // NOI18N
    btnTransaction.setFocusable(false);
    btnTransaction.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
    btnTransaction.setName("btnTransaction"); // NOI18N
    btnTransaction.setPreferredSize(new java.awt.Dimension(0, 40));
    toolBar.add(btnTransaction);

    jSeparator11.setName("jSeparator11"); // NOI18N
    toolBar.add(jSeparator11);

    btnInvoice.setIcon(resourceMap.getIcon("btnInvoice.icon")); // NOI18N
    btnInvoice.setText(resourceMap.getString("btnInvoice.text")); // NOI18N
    btnInvoice.setToolTipText(resourceMap.getString("btnInvoice.toolTipText")); // NOI18N
    btnInvoice.setFocusable(false);
    btnInvoice.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
    btnInvoice.setName("btnInvoice"); // NOI18N
    btnInvoice.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnInvoiceActionPerformed(evt);
        }
    });
    toolBar.add(btnInvoice);

    btnTktRfdCreditNote.setAction(actionMap.get("issueTktCreditNote")); // NOI18N
    btnTktRfdCreditNote.setIcon(resourceMap.getIcon("btnTktRfdCreditNote.icon")); // NOI18N
    btnTktRfdCreditNote.setText(resourceMap.getString("btnTktRfdCreditNote.text")); // NOI18N
    btnTktRfdCreditNote.setToolTipText(resourceMap.getString("btnTktRfdCreditNote.toolTipText")); // NOI18N
    btnTktRfdCreditNote.setFocusable(false);
    btnTktRfdCreditNote.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    btnTktRfdCreditNote.setName("btnTktRfdCreditNote"); // NOI18N
    btnTktRfdCreditNote.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    btnTktRfdCreditNote.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnTktRfdCreditNoteActionPerformed(evt);
        }
    });
    toolBar.add(btnTktRfdCreditNote);

    btnSCreditNote.setAction(actionMap.get("issueCreditNote")); // NOI18N
    btnSCreditNote.setIcon(resourceMap.getIcon("btnSCreditNote.icon")); // NOI18N
    btnSCreditNote.setText(resourceMap.getString("btnSCreditNote.text")); // NOI18N
    btnSCreditNote.setToolTipText(resourceMap.getString("btnSCreditNote.toolTipText")); // NOI18N
    btnSCreditNote.setFocusable(false);
    btnSCreditNote.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    btnSCreditNote.setName("btnSCreditNote"); // NOI18N
    btnSCreditNote.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    btnSCreditNote.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnSCreditNoteActionPerformed(evt);
        }
    });
    toolBar.add(btnSCreditNote);

    btnSDebitNote.setIcon(resourceMap.getIcon("btnSDebitNote.icon")); // NOI18N
    btnSDebitNote.setText(resourceMap.getString("btnSDebitNote.text")); // NOI18N
    btnSDebitNote.setToolTipText(resourceMap.getString("btnSDebitNote.toolTipText")); // NOI18N
    btnSDebitNote.setFocusable(false);
    btnSDebitNote.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    btnSDebitNote.setName("btnSDebitNote"); // NOI18N
    btnSDebitNote.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    btnSDebitNote.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnSDebitNoteActionPerformed(evt);
        }
    });
    toolBar.add(btnSDebitNote);

    btnPCNote.setIcon(resourceMap.getIcon("btnPCNote.icon")); // NOI18N
    btnPCNote.setText(resourceMap.getString("btnPCNote.text")); // NOI18N
    btnPCNote.setToolTipText(resourceMap.getString("btnPCNote.toolTipText")); // NOI18N
    btnPCNote.setFocusable(false);
    btnPCNote.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    btnPCNote.setName("btnPCNote"); // NOI18N
    btnPCNote.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    btnPCNote.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnPCNoteActionPerformed(evt);
        }
    });
    toolBar.add(btnPCNote);

    jButton12.setIcon(resourceMap.getIcon("jButton12.icon")); // NOI18N
    jButton12.setText(resourceMap.getString("jButton12.text")); // NOI18N
    jButton12.setToolTipText(resourceMap.getString("jButton12.toolTipText")); // NOI18N
    jButton12.setFocusable(false);
    jButton12.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jButton12.setName("jButton12"); // NOI18N
    jButton12.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jButton12.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton12ActionPerformed(evt);
        }
    });
    toolBar.add(jButton12);

    btnONewInv.setAction(actionMap.get("frameOAcDoc")); // NOI18N
    btnONewInv.setFont(resourceMap.getFont("btnONewInv.font")); // NOI18N
    btnONewInv.setIcon(resourceMap.getIcon("btnONewInv.icon")); // NOI18N
    btnONewInv.setText(resourceMap.getString("btnONewInv.text")); // NOI18N
    btnONewInv.setToolTipText(resourceMap.getString("btnONewInv.toolTipText")); // NOI18N
    btnONewInv.setFocusable(false);
    btnONewInv.setName("btnONewInv"); // NOI18N
    toolBar.add(btnONewInv);

    jToolBar2.setRollover(true);
    jToolBar2.setName("jToolBar2"); // NOI18N

    btnQuickSearch.setIcon(resourceMap.getIcon("btnQuickSearch.icon")); // NOI18N
    btnQuickSearch.setText(resourceMap.getString("btnQuickSearch.text")); // NOI18N
    btnQuickSearch.setToolTipText(resourceMap.getString("btnQuickSearch.toolTipText")); // NOI18N
    btnQuickSearch.setFocusable(false);
    btnQuickSearch.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    btnQuickSearch.setName("btnQuickSearch"); // NOI18N
    btnQuickSearch.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar2.add(btnQuickSearch);

    txtQuickSearch.setText(resourceMap.getString("txtQuickSearch.text")); // NOI18N
    txtQuickSearch.setToolTipText(resourceMap.getString("txtQuickSearch.toolTipText")); // NOI18N
    txtQuickSearch.setMaximumSize(new java.awt.Dimension(150, 20));
    txtQuickSearch.setName("txtQuickSearch"); // NOI18N
    txtQuickSearch.setPreferredSize(new java.awt.Dimension(40, 20));
    txtQuickSearch.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusGained(java.awt.event.FocusEvent evt) {
            txtQuickSearchFocusGained(evt);
        }
        public void focusLost(java.awt.event.FocusEvent evt) {
            txtQuickSearchFocusLost(evt);
        }
    });
    txtQuickSearch.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyReleased(java.awt.event.KeyEvent evt) {
            txtQuickSearchKeyReleased(evt);
        }
    });
    jToolBar2.add(txtQuickSearch);

    busyLabelSQ.setText(resourceMap.getString("busyLabelSQ.text")); // NOI18N
    busyLabelSQ.setName("busyLabelSQ"); // NOI18N
    jToolBar2.add(busyLabelSQ);

    jButton11.setFont(resourceMap.getFont("btnRefresh.font")); // NOI18N
    jButton11.setIcon(resourceMap.getIcon("jButton11.icon")); // NOI18N
    jButton11.setText(resourceMap.getString("jButton11.text")); // NOI18N
    jButton11.setToolTipText(resourceMap.getString("jButton11.toolTipText")); // NOI18N
    jButton11.setFocusable(false);
    jButton11.setName("jButton11"); // NOI18N
    jButton11.setPreferredSize(new java.awt.Dimension(0, 40));
    jToolBar2.add(jButton11);

    toolBar.add(jToolBar2);

    jScrollPane1.setName("jScrollPane1"); // NOI18N

    jXTable1.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {
            {null, null, null, null},
            {null, null, null, null},
            {null, null, null, null},
            {null, null, null, null}
        },
        new String [] {
            "Title 1", "Title 2", "Title 3", "Title 4"
        }
    ));
    jXTable1.setName("jXTable1"); // NOI18N
    jScrollPane1.setViewportView(jXTable1);

    jTextField1.setText(resourceMap.getString("jTextField1.text")); // NOI18N
    jTextField1.setName("jTextField1"); // NOI18N

    setComponent(mainPanel);
    setMenuBar(menuBar);
    setStatusBar(statusPanel);
    setToolBar(toolBar);
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void txtSByGDSPNRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSByGDSPNRActionPerformed
        tabsPnr.setSelectedIndex(0);
        if (!txtSByGDSPNR.getText().isEmpty()) {
            searchByGDSPnr(txtSByGDSPNR.getText().toString().trim());
        }
}//GEN-LAST:event_txtSByGDSPNRActionPerformed

    private void txtSByGDSPNRKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSByGDSPNRKeyPressed
        txtSByInvRef.setText("");
}//GEN-LAST:event_txtSByGDSPNRKeyPressed

    private void txtSByInvRefActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSByInvRefActionPerformed
        tabsPnr.setSelectedIndex(0);
        if (!txtSByInvRef.getText().isEmpty()) {
            searchByAcDocRef(Integer.parseInt(txtSByInvRef.getText().toString().trim()));
        }
        tabsPnr.setSelectedIndex(0);
}//GEN-LAST:event_txtSByInvRefActionPerformed

    private void txtSByInvRefKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSByInvRefKeyPressed
        txtSByGDSPNR.setText("");
}//GEN-LAST:event_txtSByInvRefKeyPressed

    private void btnRTPNRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRTPNRActionPerformed
        if (!txtSByGDSPNR.getText().isEmpty()) {
            searchByGDSPnr(txtSByGDSPNR.getText().toString().trim());
        } else if (!txtSByInvRef.getText().isEmpty()) {
            searchByAcDocRef(Integer.parseInt(txtSByInvRef.getText().toString().trim()));
        } else if (!txtSByPaxName.getText().isEmpty()) {
            searchByPaxName(txtSByPaxName.getText().toString().trim());
        } else if (!txtSByTktNo.getText().isEmpty()) {
            searchByTktNo(txtSByTktNo.getText());
        }
        tabsPnr.setSelectedIndex(0);
}//GEN-LAST:event_btnRTPNRActionPerformed

    private void tblItineraryMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblItineraryMouseClicked
        if (evt.getClickCount() == 2) {
            int row = tblItinerary.getSelectedRow();
            if (row != -1) {
                dlgSegment(this.segments.get(row));
            }
        }
    }//GEN-LAST:event_tblItineraryMouseClicked

    private void btnContactableSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnContactableSearchActionPerformed
        if (txtContactableSearch.getText().length() > 0) {
            Thread t = new Thread(new threadSearchContactable());
            t.start();
            try {
                t.join();
                if (this.searchedCustomer.isEmpty() && rdoCustomer.isSelected()) {
                    mainFrame = ETSBackofficeApp.getApplication().getMainFrame();
                    frameCustomer = new FrameCustomer(mainFrame);
                    customer = new Customer();
                    String tempCustomer = txtContactableSearch.getText();
                    if (tempCustomer.length() > 0) {
                        String[] data = tempCustomer.split("/");
                        customer.setSurName(data[0]);
                        customer.setForeName(data[1]);
                    }
                    if (frameCustomer.showCustomerDialog(customer)) {
                        Thread searchCustomer = new Thread(new threadSaveNewCustomer());
                        searchCustomer.start();
                        try {
                            searchCustomer.join();
                            new Thread(new threadSearchContactable()).start();
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ETSBackofficeMain.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(ETSBackofficeMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            busyLabel.setText("Warning: No keyword...");
        }
    }//GEN-LAST:event_btnContactableSearchActionPerformed

    private void txtSByInvRefFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSByInvRefFocusGained
        txtSByInvRef.selectAll();
        txtSByPaxName.setText("");
        txtSByGDSPNR.setText("");
        txtSByTktNo.setText("");
    }//GEN-LAST:event_txtSByInvRefFocusGained

    private void txtSByGDSPNRFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSByGDSPNRFocusGained
        txtSByGDSPNR.selectAll();
        txtSByPaxName.setText("");
        txtSByInvRef.setText("");
        txtSByTktNo.setText("");
    }//GEN-LAST:event_txtSByGDSPNRFocusGained

    private void txtSByPaxNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSByPaxNameFocusGained
        txtSByPaxName.selectAll();
        txtSByInvRef.setText("");
        txtSByGDSPNR.setText("");
        txtSByTktNo.setText("");
    }//GEN-LAST:event_txtSByPaxNameFocusGained

    private void txtSByPaxNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSByPaxNameActionPerformed
        tabsPnr.setSelectedIndex(0);
        if (!txtSByPaxName.getText().isEmpty()) {
            searchByPaxName(txtSByPaxName.getText().toString().trim());
        }
    }//GEN-LAST:event_txtSByPaxNameActionPerformed

    private void btnLoadTransactionMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnLoadTransactionMouseClicked
        busyIcon.setVisible(true);
        busyIcon.setBusy(true);
        populateTblTransaction();
        busyIcon.setBusy(false);
        busyIcon.setVisible(false);
    }//GEN-LAST:event_btnLoadTransactionMouseClicked

    private void txtTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTextFieldActionPerformed
}//GEN-LAST:event_txtTextFieldActionPerformed

    private void txtTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTextFieldKeyPressed

        int key = evt.getKeyCode();

        //String currentDateTime = df.format(cal.getTime());
        PNRRemark prmk = new PNRRemark();

        SimpleAttributeSet BLUE = new SimpleAttributeSet();
        SimpleAttributeSet BLACK = new SimpleAttributeSet();
        StyleConstants.setForeground(BLUE, Color.BLUE);
        StyleConstants.setForeground(BLACK, Color.BLACK);
        Document rmkDoc = txtPnrRemark.getDocument();

        if (key == KeyEvent.VK_ENTER) {
            try {
                Timestamp currentTime = df.getCurrentTimeStamp();
                String text = txtTextField.getText();
                prmk.setText(text);
                prmk.setDateTime(currentTime);
                prmk.setUser(AuthenticationBo.getLoggedOnUser());
                prmk.setPnr(this.pnr);

                rmkDoc.insertString(rmkDoc.getLength(), currentTime + " : " + AuthenticationBo.getLoggedOnUser().getSurName() + " : ", BLUE);
                rmkDoc.insertString(rmkDoc.getLength(), prmk.getText() + '\n', BLACK);
                txtTextField.setText("");
                pnrBo.saveRemark(prmk);
            } catch (BadLocationException e) {
                System.out.println("Exception in pnr remark: " + e);
            }
        }
}//GEN-LAST:event_txtTextFieldKeyPressed

    private void btnVewAcDocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVewAcDocActionPerformed
        int row = 0;
        if (tabsAcDoc.getSelectedIndex() == 0) {
            row = tblSalesAcDoc.getSelectedRow();
            if (row != -1) {
                viewAcDoc(this.acDocs.get(row));
            } else {
                JOptionPane.showMessageDialog(null, "Select Sales Invoice", "View Invoice", JOptionPane.WARNING_MESSAGE);
            }
        } else if (tabsAcDoc.getSelectedIndex() == 1) {

            row = tblPurchaseAcDoc.getSelectedRow();
            if (row != -1) {
                if (this.pnr.getTicketingAgt() != null) {
                    acDocBo.findPurchaseAcDocById(this.pAcDocs.get(row).getPurchaseAcDocId());
                    PurchaseAccountingDocument acDoc = acDocBo.getPurchaseAccountingDocument();
                    viewTPAcDocDialogue(acDoc);
                } else {
                    JOptionPane.showMessageDialog(null, "No Vendor !!!", "View Invoice", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Select Purchase Invoice", "View Invoice", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnVewAcDocActionPerformed

    private void btnDeletePnrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeletePnrActionPerformed
        deletePnr();
    }//GEN-LAST:event_btnDeletePnrActionPerformed

    private void btnDeleteTicketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteTicketActionPerformed
        int row = tblTicket.getSelectedRow();
        if (row != -1) {
            int choice = JOptionPane.showConfirmDialog(null, "Delete ticket permanently?", "Delete Ticket", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                Ticket ticket = this.tickets.get(row);
                if (ticket.getAccountingDocumentLine().isEmpty()) {
                    pnrBo.deleteTicket(ticket);
                    new Thread(new threadLoadCompletePnr(this.pnr.getPnrId())).start();
                } else {
                    JOptionPane.showMessageDialog(null, "Invoiced ticket can not be deleted !!!"
                            + "\nIf you still want to delete, remove invoice first"
                            + "\nand then create a fresh invoice", "Delete Ticket", JOptionPane.WARNING_MESSAGE);
                }
            } else if (choice == JOptionPane.NO_OPTION) {
            }
        } else {
            JOptionPane.showMessageDialog(null, "Select Ticket", "Delete Ticket", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnDeleteTicketActionPerformed

    private void btnDeletePnr1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeletePnr1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnDeletePnr1ActionPerformed

    private void btnRefreshPnrTodayMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnRefreshPnrTodayMouseClicked
        new Thread(new threadPnrsToday()).start();
    }//GEN-LAST:event_btnRefreshPnrTodayMouseClicked

    private void btnDeletePnr2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeletePnr2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnDeletePnr2ActionPerformed

    private void btnRefreshSalesSummeryMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnRefreshSalesSummeryMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRefreshSalesSummeryMouseClicked

    private void btnDeletePnr3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeletePnr3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnDeletePnr3ActionPerformed

    private void jButton16MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton16MouseClicked
        new Thread(new threadBookedPnrs()).start();
    }//GEN-LAST:event_jButton16MouseClicked

    private void btnVoidTicketMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnVoidTicketMouseClicked
        int row = tblTicket.getSelectedRow();
        if (row != -1) {
            int choice = JOptionPane.showConfirmDialog(null, "This will permanently VOID Ticket..."
                    + "\nDo you still want to proceed VOID?", "VOID Ticket", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                manualVoidTicket(this.tickets.get(row));
                populateTblTicket();
                populateTblFareLine();
                setSaveNeeded(true);
            } else if (choice == JOptionPane.CANCEL_OPTION) {
            }

        } else {
            JOptionPane.showMessageDialog(null, "Select Ticket", "VOID Ticket", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnVoidTicketMouseClicked

    private void btnRefundTicketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefundTicketActionPerformed

        int row = tblTicket.getSelectedRow();
        if (row != -1) {
            int choice = JOptionPane.showConfirmDialog(null, "This will permanently REFUND Ticket..."
                    + "\nDo you still want to proceed REFUND?", "REFUND Ticket", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                Ticket refundedTkt = manualRefundTicket(this.tickets.get(row));

                //FrameTicketR frameRefundTicket = new FrameTicketR(mainFrame);
                //if (frameRefundTicket.showTicketDialog(refundedTkt)) {
                this.tickets.add(refundedTkt);
                this.manuallyRfdtickets.add(refundedTkt);
                this.ticket = refundedTkt;
                populateTblTicket();
                populateTicketDetails();
                setSaveNeeded(true);
                //} else {
                //    frameRefundTicket = null;
                //}
                populateTblTicket();
                setSaveNeeded(true);
            } else if (choice == JOptionPane.CANCEL_OPTION) {
            }

        } else {
            JOptionPane.showMessageDialog(null, "Select Ticket", "REFUND Ticket", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnRefundTicketActionPerformed

    private void dpPnrCreationDateMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dpPnrCreationDateMouseReleased

    }//GEN-LAST:event_dpPnrCreationDateMouseReleased

    private void btnDeleteSegmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteSegmentActionPerformed
        int row = tblItinerary.getSelectedRow();
        if (row != -1) {
            int choice = JOptionPane.showConfirmDialog(null, "Delete Segment permanently?", "Delete Segment", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                Itinerary segment = this.segments.get(row);
                itineraryBo.removeItinerary(segment);
                itineraryBo.loadItinerary(this.pnr.getPnrId());
                this.segments = itineraryBo.getSegments();
                populateTblItinerary();
            } else if (choice == JOptionPane.NO_OPTION) {
            }
        } else {
            JOptionPane.showMessageDialog(null, "Select Segment", "Delete Segment", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnDeleteSegmentActionPerformed

    private void btnRefreshItineraryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshItineraryActionPerformed
        if (this.pnr != null) {
            new Thread(new threadItinerary(this.pnr.getPnrId())).start();
        }
    }//GEN-LAST:event_btnRefreshItineraryActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        refreshMainFrame();
    }//GEN-LAST:event_jButton9ActionPerformed

    private void txtTicketingAgtOidKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTicketingAgtOidKeyReleased
        this.pnr.setTicketingAgtOID(txtTicketingAgtOid.getText());
        setSaveNeeded(true);
}//GEN-LAST:event_txtTicketingAgtOidKeyReleased

    private void txtBookingAgtOidKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBookingAgtOidKeyReleased
        this.pnr.setBookingAgtOID(txtBookingAgtOid.getText());
        setSaveNeeded(true);
}//GEN-LAST:event_txtBookingAgtOidKeyReleased

    private void btnDelAcDocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelAcDocActionPerformed
        int row = 0;
        if (tabsAcDoc.getSelectedIndex() == 0) {
            row = tblSalesAcDoc.getSelectedRow();
            if (row != -1) {
                int choice = JOptionPane.showConfirmDialog(null, "Delete invoice permanently?", "Delete Invoice", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    acDocBo.deleteAcDoc(this.acDocs.get(row));
                    Thread t = new Thread(new threadLoadCompletePnr(this.pnr.getPnrId()));
                    t.start();
                    try {
                        t.join();
                        Thread t1 = new Thread(new threadLoadAcDocs(pnr.getPnrId()));
                        t1.start();
                        t1.join();
                        populateTblSalesAcDoc();
                        new Thread(new threadUninvoicedPnr()).start();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ETSBackofficeMain.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (choice == JOptionPane.NO_OPTION) {
                }
            } else {
                JOptionPane.showMessageDialog(null, "Select Invoice", "Delete Invoice", JOptionPane.WARNING_MESSAGE);
            }
        } else if (tabsAcDoc.getSelectedIndex() == 1) {
            JOptionPane.showMessageDialog(null, "Puchase Invoice Can not be removed...", "Delete Invoice", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btnDelAcDocActionPerformed

    private void tblPurchaseAcDocMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPurchaseAcDocMouseClicked
        if (evt.getClickCount() == 2) {
            int row = tblPurchaseAcDoc.getSelectedRow();
            if (row != -1) {
                if (this.pnr != null) {
                    if (this.pnr.getTicketingAgt() != null) {
                        //acDocBo.findPurchaseAcDocById(this.pAcDocs.get(row).getPurchaseAcDocId());
                        //PurchaseAccountingDocument acDoc = acDocBo.getPurchaseAccountingDocument();
                        
                        viewTPAcDocDialogue(this.pAcDocs.get(row));
                    } else {
                        JOptionPane.showMessageDialog(null, "No Vendor !!!", "View Invoice", JOptionPane.WARNING_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Select Invoice", "View Invoice", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_tblPurchaseAcDocMouseClicked

    private void btnLoadVendorsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadVendorsActionPerformed
        new Thread(new threadLoadVendors()).start();
    }//GEN-LAST:event_btnLoadVendorsActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        int row = tblTicket.getSelectedRow();
        if (row != -1) {
            //frameTicket(this.tickets.get(row), row);
        }
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        FrameCheckMyTrip c = new FrameCheckMyTrip();
        c.setVisible(true);
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        refreshMainFrame();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void tblReservationMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblReservationMouseClicked
        if (evt.getClickCount() == 2) {
            viewPnrDetails();
        }
    }//GEN-LAST:event_tblReservationMouseClicked

    private void tblPnrTodayMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPnrTodayMouseClicked
        if (evt.getClickCount() == 2) {
            viewPnrDetails();
        }
    }//GEN-LAST:event_tblPnrTodayMouseClicked

    private void btnRefreshSalesSummeryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshSalesSummeryActionPerformed
        new Thread(new threadSales()).start();
    }//GEN-LAST:event_btnRefreshSalesSummeryActionPerformed

    private void txtContactableSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtContactableSearchActionPerformed
        if (txtContactableSearch.getText().length() > 0) {
            new Thread(new threadSearchContactable()).start();
        } else {
            busyLabel.setText("Warning: No keyword...");
        }
    }//GEN-LAST:event_txtContactableSearchActionPerformed

    private void txtContactableSearchFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtContactableSearchFocusGained
        txtContactableSearch.selectAll();
    }//GEN-LAST:event_txtContactableSearchFocusGained

    private void btnNewCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewCustomerActionPerformed
        if (rdoCustomer.isSelected()) {
            newCustomer();
        } else if (rdoAgent.isSelected()) {
            newAgent();
        }
    }//GEN-LAST:event_btnNewCustomerActionPerformed

    private void btnInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInvoiceActionPerformed
        if (this.pnr != null) {
            if (this.pnr.getAgent() != null || this.pnr.getCustomer() != null) {

                Long contId = null;
                BigDecimal cLimit = new BigDecimal("0.00");
                if (this.pnr.getAgent() != null) {
                    contId = this.pnr.getAgent().getContactableId();
                    cLimit = this.pnr.getAgent().getCreditLimit();
                    if (allowFinancialAction(cLimit, this.clientAcBalance)) {
                        issueInvoice();
                    }
                } else if (this.pnr.getCustomer() != null) {
                    //contId = this.pnr.getCustomer().getContactableId();
                    issueInvoice();
                }

            } else {
                JOptionPane.showMessageDialog(null, "Agent/Customer not selected", "Issue Invoice", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "No PNR!!! Retrieve PNR first...", "New Ticketing Invoice", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnInvoiceActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        new Thread(new threadSavePnr()).start();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnTktRfdCreditNoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTktRfdCreditNoteActionPerformed

        if (this.pnr != null) {
            if (this.saveNeeded == true) {
                Thread t = new Thread(new threadSavePnr());
                t.start();
                try {
                    t.join();
                    issueTktCreditNote();
                } catch (InterruptedException ex) {
                    Logger.getLogger(ETSBackofficeMain.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                issueTktCreditNote();
            }
        } else {
            JOptionPane.showMessageDialog(null, "No PNR!!! Retrieve PNR first...", "New Ticketing Invoice", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnTktRfdCreditNoteActionPerformed

    private void btnSCreditNoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSCreditNoteActionPerformed
        if (this.saveNeeded == true) {
            Thread t = new Thread(new threadSavePnr());
            t.start();
            try {
                t.join();
                issueSCreditNote();
            } catch (InterruptedException ex) {
                Logger.getLogger(ETSBackofficeMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            issueSCreditNote();
        }
    }//GEN-LAST:event_btnSCreditNoteActionPerformed

    private void txtQuickSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtQuickSearchKeyReleased
        if (txtQuickSearch.getText().trim().length() == 13) {
            quickSearch();
        }
    }//GEN-LAST:event_txtQuickSearchKeyReleased

    private void txtQuickSearchFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtQuickSearchFocusGained
        txtQuickSearch.setText("");
    }//GEN-LAST:event_txtQuickSearchFocusGained

    private void txtQuickSearchFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtQuickSearchFocusLost
        txtQuickSearch.setForeground(Color.LIGHT_GRAY);
        txtQuickSearch.setText("Full Invoice Refference No");

    }//GEN-LAST:event_txtQuickSearchFocusLost

    private void txtSByInvRefFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSByInvRefFocusLost
    }//GEN-LAST:event_txtSByInvRefFocusLost

    private void txtSByGDSPNRFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSByGDSPNRFocusLost
    }//GEN-LAST:event_txtSByGDSPNRFocusLost

    private void txtSByPaxNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSByPaxNameFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSByPaxNameFocusLost

    private void tblPnrMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPnrMouseClicked
        if (evt.getClickCount() == 2) {
            viewPnrDetails();
        }
    }//GEN-LAST:event_tblPnrMouseClicked

    private void tblTicketMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblTicketMouseClicked
        if (evt.getClickCount() == 2) {
            int row = tblTicket.getSelectedRow();
            if (row != -1 && row != tblTicket.getRowCount()-1) {
                dlgTicket(this.tickets.get(row), row);
            }
        }
    }//GEN-LAST:event_tblTicketMouseClicked

    private void txtTktNoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTktNoKeyReleased
        int key = evt.getKeyCode();

        if (key != KeyEvent.VK_ENTER && key != KeyEvent.VK_DOWN && key != KeyEvent.VK_UP) {
            String text = txtTktNo.getText().trim();
            if (!text.isEmpty()) {
                this.ticket.setTicketNo(txtTktNo.getText());
                populateTblTicket();
                setSaveNeeded(true);
            }
        } else if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_DOWN) {
            txtBaseFare.requestFocus();
        } else if (key == KeyEvent.VK_UP) {
            txtNumAirCode.requestFocus();
        }
}//GEN-LAST:event_txtTktNoKeyReleased

    private void txtBaseFareFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBaseFareFocusGained
        txtBaseFare.selectAll();
}//GEN-LAST:event_txtBaseFareFocusGained

    private void txtBaseFareKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBaseFareKeyReleased
        int key = evt.getKeyCode();

        String text = txtBaseFare.getText().replaceAll("[^.0-9]", "");
        if (key != KeyEvent.VK_ENTER && key != KeyEvent.VK_DOWN && key != KeyEvent.VK_UP
                && key != KeyEvent.VK_RIGHT && key != KeyEvent.VK_LEFT) {

            if (!text.isEmpty() && key != KeyEvent.VK_PERIOD && key != KeyEvent.VK_DECIMAL) {
                if (this.ticket.getTktStatus() == 4) {
                    this.ticket.setBaseFare(new BigDecimal(text).negate());
                } else {
                    this.ticket.setBaseFare(new BigDecimal(text));
                }
                txtTotalFare.setText(this.ticket.getNetFare().toString());
                populateTblTicket();
                setSaveNeeded(true);
            }
        } else if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_DOWN) {
            txtTax.requestFocus();
        } else if (key == KeyEvent.VK_UP) {
            txtTktNo.requestFocus();
        }
}//GEN-LAST:event_txtBaseFareKeyReleased

    private void txtTaxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTaxFocusGained
        txtTax.selectAll();
}//GEN-LAST:event_txtTaxFocusGained

    private void txtTaxKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTaxKeyReleased
        int key = evt.getKeyCode();
        String text = txtTax.getText().replaceAll("[^.0-9]", "");
        if (key != KeyEvent.VK_ENTER && key != KeyEvent.VK_DOWN && key != KeyEvent.VK_UP
                && key != KeyEvent.VK_RIGHT && key != KeyEvent.VK_LEFT) {

            if (!text.isEmpty() && key != KeyEvent.VK_PERIOD) {
                if (this.ticket.getTktStatus() == 4) {
                    this.ticket.setTax(new BigDecimal(text).negate());
                } else {
                    this.ticket.setTax(new BigDecimal(text));
                }
                txtTotalFare.setText(this.ticket.getNetFare().toString());
                populateTblTicket();
                setSaveNeeded(true);
            }
        } else if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_DOWN) {
            txtBspCom.requestFocus();
        } else if (key == KeyEvent.VK_UP) {
            txtBaseFare.requestFocus();
        }
}//GEN-LAST:event_txtTaxKeyReleased

    private void txtBspComFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBspComFocusGained
        txtBspCom.selectAll();
}//GEN-LAST:event_txtBspComFocusGained

    private void txtBspComKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBspComKeyReleased
        int key = evt.getKeyCode();
        String text = txtBspCom.getText().replaceAll("[^.0-9]", "");
        if (key != KeyEvent.VK_ENTER && key != KeyEvent.VK_DOWN && key != KeyEvent.VK_UP
                && key != KeyEvent.VK_RIGHT && key != KeyEvent.VK_LEFT) {

            if (!text.isEmpty() && key != KeyEvent.VK_PERIOD) {
                if (this.ticket.getTktStatus() == 4) {
                    this.ticket.setBspCom(new BigDecimal(text));
                } else {
                    this.ticket.setBspCom(new BigDecimal(text).negate());
                }
                txtTotalFare.setText(this.ticket.getNetFare().toString());
                populateTblTicket();
                setSaveNeeded(true);
            }
        } else if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_DOWN) {
            txtGrossFare.requestFocus();
        } else if (key == KeyEvent.VK_UP) {
            txtTax.requestFocus();
        }
}//GEN-LAST:event_txtBspComKeyReleased

    private void txtGrossFareFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtGrossFareFocusGained
        txtGrossFare.selectAll();
}//GEN-LAST:event_txtGrossFareFocusGained

    private void txtGrossFareKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtGrossFareKeyReleased
        int key = evt.getKeyCode();
        String text = txtGrossFare.getText().replaceAll("[^.0-9]", "");

        if (key != KeyEvent.VK_ENTER && key != KeyEvent.VK_DOWN && key != KeyEvent.VK_UP
                && key != KeyEvent.VK_RIGHT && key != KeyEvent.VK_LEFT) {

            if (!text.isEmpty() && key != KeyEvent.VK_PERIOD && key != KeyEvent.VK_DECIMAL) {
                if (this.ticket.getTktStatus() == 4) {
                    this.ticket.setGrossFare(new BigDecimal(text).negate());
                } else {
                    this.ticket.setGrossFare(new BigDecimal(text));
                }

                if (this.ticket.getTktdRevenue().compareTo(new BigDecimal("0.00")) == -1) {
                    lblGFMessage.setText("Warning: Making loss");
                } else {
                    lblGFMessage.setText("");
                }
                txtNetToPay.setText(this.ticket.getNetPayble().toString());
                lblProfitLoss.setText(this.ticket.getTktdRevenue().toString());
                populateTblTicket();
                setSaveNeeded(true);

            }
        } else if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_DOWN) {
            txtDisc.requestFocus();
        } else if (key == KeyEvent.VK_UP) {
            txtBspCom.requestFocus();
        }
}//GEN-LAST:event_txtGrossFareKeyReleased

    private void txtDiscFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDiscFocusGained
        txtDisc.selectAll();
}//GEN-LAST:event_txtDiscFocusGained

    private void txtDiscKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDiscKeyReleased
        int key = evt.getKeyCode();
        String text = txtDisc.getText().replaceAll("[^.0-9]", "");

        if (key != KeyEvent.VK_ENTER && key != KeyEvent.VK_DOWN && key != KeyEvent.VK_UP
                && key != KeyEvent.VK_RIGHT && key != KeyEvent.VK_LEFT) {

            if (!text.isEmpty() && key != KeyEvent.VK_PERIOD && key != KeyEvent.VK_DECIMAL) {
                if(this.ticket.getTktStatus()!=4){
                this.ticket.setDiscount(new BigDecimal(text).negate());
                }else{
                this.ticket.setDiscount(new BigDecimal(text));
                }
                if (this.ticket.getTktdRevenue().compareTo(new BigDecimal("0.00")) == -1) {
                    lblGFMessage.setText("Excess discount");
                } else {
                    lblGFMessage.setText("");
                }
                lblProfitLoss.setText(this.ticket.getTktdRevenue().toString());
                txtNetToPay.setText(this.ticket.getNetPayble().toString());
                populateTblTicket();
                setSaveNeeded(true);
            }
        } else if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_DOWN) {
            txtAtol.requestFocus();
        } else if (key == KeyEvent.VK_UP) {
            txtGrossFare.requestFocus();
        }
}//GEN-LAST:event_txtDiscKeyReleased

    private void txtAtolFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAtolFocusGained
        txtAtol.selectAll();
}//GEN-LAST:event_txtAtolFocusGained

    private void txtAtolKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAtolKeyReleased
        int key = evt.getKeyCode();
        String text = txtAtol.getText().replaceAll("[^.0-9]", "");

        if (key != KeyEvent.VK_ENTER && key != KeyEvent.VK_DOWN && key != KeyEvent.VK_UP
                && key != KeyEvent.VK_RIGHT && key != KeyEvent.VK_LEFT) {

            if (!text.isEmpty() && key != KeyEvent.VK_PERIOD && key != KeyEvent.VK_DECIMAL) {
                if (this.ticket.getTktStatus() == 4) {
                    this.ticket.setAtolChg(new BigDecimal(text).negate());
                } else {
                    this.ticket.setAtolChg(new BigDecimal(text));
                }

                if (this.ticket.getTktdRevenue().compareTo(new BigDecimal("0.00")) == -1) {
                    lblGFMessage.setText("Warning: Making loss");
                } else {
                    lblGFMessage.setText("");
                }
                lblProfitLoss.setText(this.ticket.getTktdRevenue().toString());
                txtNetToPay.setText(this.ticket.getNetPayble().toString());
                populateTblTicket();
                setSaveNeeded(true);
            }
        } else if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_DOWN) {
            btnNTicket.requestFocus();
        } else if (key == KeyEvent.VK_UP) {
            txtDisc.requestFocus();
        }
}//GEN-LAST:event_txtAtolKeyReleased

    private void txtNumAirCodeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNumAirCodeKeyReleased
        int key = evt.getKeyCode();

        if (key != KeyEvent.VK_ENTER && key != KeyEvent.VK_DOWN && key != KeyEvent.VK_UP) {
            String text = txtNumAirCode.getText().trim();
            if (!text.isEmpty() && key != KeyEvent.VK_ENTER) {
                this.ticket.setNumericAirLineCode(txtNumAirCode.getText());
                populateTblTicket();
                setSaveNeeded(true);
            }
        } else if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_DOWN) {
            txtTktNo.requestFocus();
        } else if (key == KeyEvent.VK_UP) {
            txtAtol.requestFocus();
        }

}//GEN-LAST:event_txtNumAirCodeKeyReleased

    private void txtBaseFareFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBaseFareFocusLost
        txtBaseFare.setText(this.ticket.getBaseFare().toString());
    }//GEN-LAST:event_txtBaseFareFocusLost

    private void txtTaxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTaxFocusLost
        txtTax.setText(this.ticket.getTax().toString());
    }//GEN-LAST:event_txtTaxFocusLost

    private void txtBspComFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBspComFocusLost
        txtBspCom.setText(this.ticket.getBspCom().toString());
    }//GEN-LAST:event_txtBspComFocusLost

    private void txtGrossFareFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtGrossFareFocusLost
        txtGrossFare.setText(this.ticket.getGrossFare().toString());
    }//GEN-LAST:event_txtGrossFareFocusLost

    private void txtDiscFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDiscFocusLost
        txtDisc.setText(this.ticket.getDiscount().toString());
    }//GEN-LAST:event_txtDiscFocusLost

    private void txtAtolFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAtolFocusLost
        txtAtol.setText(this.ticket.getAtolChg().toString());
    }//GEN-LAST:event_txtAtolFocusLost

    private void btnEditAcDocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditAcDocActionPerformed
        int row = 0;
        if (tabsAcDoc.getSelectedIndex() == 0) {
            row = tblSalesAcDoc.getSelectedRow();
            if (row != -1) {
                acDocBo.findAcDocById(this.acDocs.get(row).getAcDocId());

                DlgEditSAcDoc dlgEditSAcDoc = new DlgEditSAcDoc(mainFrame);
                if (dlgEditSAcDoc.showDialog(acDocBo.getAccountingDocument())) {
                    dlgEditSAcDoc.dispose();
                } else {
                    new Thread(new threadLoadCompletePnr(pnr.getPnrId())).start();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Select Sales Invoice", "View Invoice", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnEditAcDocActionPerformed

    private void txtNumAirCodeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNumAirCodeFocusGained
        txtNumAirCode.selectAll();
    }//GEN-LAST:event_txtNumAirCodeFocusGained

    private void txtTktNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTktNoFocusGained
        txtTktNo.selectAll();
    }//GEN-LAST:event_txtTktNoFocusGained

    private void btnNTicketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNTicketActionPerformed
        int index = this.tickets.indexOf(this.ticket);
        if (index < tblTicket.getRowCount() - 1) {
            index++;
            tblTicket.setRowSelectionInterval(index, index);
        }
    }//GEN-LAST:event_btnNTicketActionPerformed

    private void btnPTicketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPTicketActionPerformed
        int index = this.tickets.indexOf(this.ticket);
        if (index > 0) {
            index--;
            tblTicket.setRowSelectionInterval(index, index);
        }
    }//GEN-LAST:event_btnPTicketActionPerformed

    private void btnNTicketKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnNTicketKeyReleased
        int key = evt.getKeyCode();
        if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_RIGHT) {
            int index = this.tickets.indexOf(this.ticket);
            if (index < tblTicket.getRowCount() - 1) {
                index++;
                tblTicket.setRowSelectionInterval(index, index);
            }
        } else if (key == KeyEvent.VK_LEFT) {
            btnPTicket.requestFocus();
        }
    }//GEN-LAST:event_btnNTicketKeyReleased

    private void btnPTicketKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnPTicketKeyReleased
        int key = evt.getKeyCode();
        if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_LEFT) {
            int index = this.tickets.indexOf(this.ticket);
            if (index > 0) {
                index--;
                tblTicket.setRowSelectionInterval(index, index);
            }
        } else if (key == KeyEvent.VK_RIGHT) {
            btnNTicket.requestFocus();
        }
    }//GEN-LAST:event_btnPTicketKeyReleased

    private void btnVoidAcDocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVoidAcDocActionPerformed
        int row = 0;
        if (tabsAcDoc.getSelectedIndex() == 0) {
            row = tblSalesAcDoc.getSelectedRow();
            if (row != -1) {
                int choice = JOptionPane.showConfirmDialog(null, "VOID invoice permanently?", "VOID Invoice", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    //new Thread(new threadLoadAcDocWithRealtedDoc(this.acDocs.get(row).getAcDocId())).start();
                    acDocBo.setAcDocStatusFalse(this.acDocs.get(row).getAcDocId());
                    Thread t = new Thread(new threadLoadCompletePnr(this.pnr.getPnrId()));
                    t.start();
                    try {
                        t.join();
                        Thread t1 = new Thread(new threadLoadAcDocs(pnr.getPnrId()));
                        t1.start();
                        t1.join();
                        populateTblSalesAcDoc();
                        new Thread(new threadUninvoicedPnr()).start();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ETSBackofficeMain.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (choice == JOptionPane.NO_OPTION) {
                }
            } else {
                JOptionPane.showMessageDialog(null, "Select Invoice", "VOID Invoice", JOptionPane.WARNING_MESSAGE);
            }
        } else if (tabsAcDoc.getSelectedIndex() == 1) {
            JOptionPane.showMessageDialog(null, "Puchase Invoice Can not be VOID...", "VOID Invoice", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btnVoidAcDocActionPerformed

    private void tblSalesAcDocMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSalesAcDocMouseClicked
        if (evt.getClickCount() == 2) {
            int row = tblSalesAcDoc.getSelectedRow();
            if (row != -1) {
                viewAcDoc(this.acDocs.get(row));
            } else {
                JOptionPane.showMessageDialog(null, "Select Invoice", "View Invoice", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_tblSalesAcDocMouseClicked

    private void txtSByTktNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSByTktNoFocusGained
       txtSByPaxName.setText("");
        txtSByInvRef.setText("");
        txtSByGDSPNR.setText("");
        txtSByTktNo.selectAll();
    }//GEN-LAST:event_txtSByTktNoFocusGained

    private void btnViewSegmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewSegmentActionPerformed
       int row = tblItinerary.getSelectedRow();
            if (row != -1) {
                dlgSegment(this.segments.get(row));
            }
    }//GEN-LAST:event_btnViewSegmentActionPerformed

    private void txtSByTktNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSByTktNoActionPerformed
       if (!txtSByTktNo.getText().isEmpty()) {
            searchByTktNo(txtSByTktNo.getText());
        }
        tabsPnr.setSelectedIndex(0);
    }//GEN-LAST:event_txtSByTktNoActionPerformed

    private void txtGdsPnrKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtGdsPnrKeyReleased
        this.pnr.setGdsPNR(txtGdsPnr.getText());
        setSaveNeeded(true);
    }//GEN-LAST:event_txtGdsPnrKeyReleased

    private void dpPnrCreationDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dpPnrCreationDateActionPerformed
        this.pnr.setPnrCreationDate(dpPnrCreationDate.getDate());
        setSaveNeeded(true);
    }//GEN-LAST:event_dpPnrCreationDateActionPerformed

    private void menuSendContenttoVendorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSendContenttoVendorActionPerformed
       GDSFileSender fs = new GDSFileSender();       
        try {         
            fs.zipAirAndEmail();
        } catch (Exception ex) {
            Logger.getLogger(ETSBackofficeMain.class.getName()).log(Level.SEVERE, null, ex);
        }         
    }//GEN-LAST:event_menuSendContenttoVendorActionPerformed

    private void datePickerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_datePickerActionPerformed
       this.ticket.setDocIssuedate(datePicker.getDate());
       setSaveNeeded(true);
    }//GEN-LAST:event_datePickerActionPerformed

    private void btnPCNoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPCNoteActionPerformed
        if (this.saveNeeded == true) {
            Thread t = new Thread(new threadSavePnr());
            t.start();
            try {
                t.join();
                issuePCreditNote();
            } catch (InterruptedException ex) {
                Logger.getLogger(ETSBackofficeMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            issuePCreditNote();
        }
    }//GEN-LAST:event_btnPCNoteActionPerformed

    private void btnRemoveLine1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveLine1ActionPerformed
        int row = tblOtherService.getSelectedRow();
        if (row != -1) {
            if (this.otherServicesInPnr.get(row).getAccountingDocumentLine().isEmpty()) {
                if (this.otherServicesInPnr.get(row).getServiceId() == 0) {                    
                    this.otherServicesInPnr.remove(row);
                    populateTblOtherService();
                } else {                    
                    PopulateTblOtherService s = new PopulateTblOtherService();
                    s.execute();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Invoiced line can not be removed, Modify invoice if necessary !!!", "Remove Line", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Select Item to Remove", "New Invoice", JOptionPane.WARNING_MESSAGE);
        }
}//GEN-LAST:event_btnRemoveLine1ActionPerformed

    private void btnReturnItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReturnItemActionPerformed
        int row = tblOtherService.getSelectedRow();
        Services s = this.otherServicesInPnr.get(row);
        if (s.getServiceCost().signum() != -1) {
            RefundOtherService rfdOService = new RefundOtherService(s);
            rfdOService.execute();
        }
}//GEN-LAST:event_btnReturnItemActionPerformed

    private void btnSDebitNoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSDebitNoteActionPerformed
        if (this.saveNeeded == true) {
            Thread t = new Thread(new threadSavePnr());
            t.start();
            try {
                t.join();
                issueSDebitNote();
            } catch (InterruptedException ex) {
                Logger.getLogger(ETSBackofficeMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            issueSDebitNote();
        }
    }//GEN-LAST:event_btnSDebitNoteActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        if (this.saveNeeded == true) {
            Thread t = new Thread(new threadSavePnr());
            t.start();
            try {
                t.join();
                issuePDebitNote();
            } catch (InterruptedException ex) {
                Logger.getLogger(ETSBackofficeMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            issuePDebitNote();
        }
    }//GEN-LAST:event_jButton12ActionPerformed

    private void menuNTktingInvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuNTktingInvActionPerformed
          if (this.pnr != null) {
            if (this.pnr.getAgent() != null || this.pnr.getCustomer() != null) {

                Long contId = null;
                BigDecimal cLimit = new BigDecimal("0.00");
                if (this.pnr.getAgent() != null) {
                    contId = this.pnr.getAgent().getContactableId();
                    cLimit = this.pnr.getAgent().getCreditLimit();
                    if (allowFinancialAction(cLimit, this.clientAcBalance)) {
                        issueInvoice();
                    }
                } else if (this.pnr.getCustomer() != null) {
                    //contId = this.pnr.getCustomer().getContactableId();
                    issueInvoice();
                }

            } else {
                JOptionPane.showMessageDialog(null, "Agent/Customer not selected", "Issue Invoice", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "No PNR!!! Retrieve PNR first...", "New Ticketing Invoice", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_menuNTktingInvActionPerformed

    private void mnuSCreditNoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSCreditNoteActionPerformed
        if (this.saveNeeded == true) {
            Thread t = new Thread(new threadSavePnr());
            t.start();
            try {
                t.join();
                issueSCreditNote();
            } catch (InterruptedException ex) {
                Logger.getLogger(ETSBackofficeMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            issueSCreditNote();
        }
    }//GEN-LAST:event_mnuSCreditNoteActionPerformed

    private void mnuSDebitNoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSDebitNoteActionPerformed
       if (this.saveNeeded == true) {
            Thread t = new Thread(new threadSavePnr());
            t.start();
            try {
                t.join();
                issueSDebitNote();
            } catch (InterruptedException ex) {
                Logger.getLogger(ETSBackofficeMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            issueSDebitNote();
        }
    }//GEN-LAST:event_mnuSDebitNoteActionPerformed

    private void txtBaseFareKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBaseFareKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBaseFareKeyTyped

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnContactableSearch;
    private javax.swing.JButton btnDelAcDoc;
    private javax.swing.JButton btnDeletePnr;
    private javax.swing.JButton btnDeletePnr1;
    private javax.swing.JButton btnDeletePnr2;
    private javax.swing.JButton btnDeletePnr3;
    private javax.swing.JButton btnDeleteSegment;
    private javax.swing.JButton btnDeleteTicket;
    private javax.swing.JButton btnEditAcDoc;
    private javax.swing.JButton btnInvoice;
    private javax.swing.JButton btnLoadTransaction;
    private javax.swing.JButton btnLoadVendors;
    private javax.swing.JButton btnNTicket;
    private javax.swing.JButton btnNewCustomer;
    private javax.swing.JButton btnONewInv;
    private javax.swing.JButton btnOutstandingInvoice;
    private javax.swing.JButton btnPCNote;
    private javax.swing.JButton btnPTicket;
    private javax.swing.JButton btnPnrDetails;
    private javax.swing.JButton btnPrintAcDoc;
    private javax.swing.JButton btnQuickSearch;
    private javax.swing.JButton btnRTPNR;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnRefreshItinerary;
    private javax.swing.JButton btnRefreshPnrToday;
    private javax.swing.JButton btnRefreshSalesSummery;
    private javax.swing.JButton btnRefundTicket;
    private javax.swing.JButton btnRemoveLine1;
    private javax.swing.JButton btnReturnItem;
    private javax.swing.JButton btnSCreditNote;
    private javax.swing.JButton btnSDebitNote;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnTktRfdCreditNote;
    private javax.swing.JButton btnTransaction;
    private javax.swing.JButton btnVewAcDoc;
    private javax.swing.JButton btnViewSegment;
    private javax.swing.JButton btnVoidAcDoc;
    private javax.swing.JButton btnVoidTicket;
    private org.jdesktop.swingx.JXBusyLabel busyIcon;
    private org.jdesktop.swingx.JXBusyLabel busyLabel;
    private org.jdesktop.swingx.JXBusyLabel busyLabelAcDoc;
    private org.jdesktop.swingx.JXBusyLabel busyLabelRemark;
    private org.jdesktop.swingx.JXBusyLabel busyLabelSQ;
    private org.jdesktop.swingx.JXBusyLabel busyLabelVendor;
    private org.jdesktop.swingx.JXBusyLabel busyLblPnrReservation;
    private org.jdesktop.swingx.JXBusyLabel busyLblPnrToDay;
    private org.jdesktop.swingx.JXBusyLabel busyLblSalesToday;
    private org.jdesktop.swingx.JXBusyLabel busyLblTask;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox cmbSearchResult;
    private javax.swing.JComboBox cmbVendor;
    private org.jdesktop.swingx.JXDatePicker datePicker;
    private org.jdesktop.swingx.JXDatePicker dpPnrCreationDate;
    private org.jdesktop.swingx.JXDatePicker dpToday;
    private javax.swing.JPanel inerPanel1;
    private javax.swing.JPanel innerPanel2;
    private javax.swing.JPanel innerPanel3;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane15;
    private javax.swing.JScrollPane jScrollPane16;
    private javax.swing.JScrollPane jScrollPane17;
    private javax.swing.JScrollPane jScrollPane18;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator10;
    private javax.swing.JToolBar.Separator jSeparator11;
    private javax.swing.JSeparator jSeparator12;
    private javax.swing.JSeparator jSeparator13;
    private javax.swing.JPopupMenu.Separator jSeparator14;
    private javax.swing.JPopupMenu.Separator jSeparator15;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JPopupMenu.Separator jSeparator9;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JToolBar jToolBar4;
    private javax.swing.JToolBar jToolBar5;
    private javax.swing.JToolBar jToolBar6;
    private javax.swing.JToolBar jToolBar7;
    private javax.swing.JToolBar jToolBar8;
    private javax.swing.JToolBar jToolBar9;
    private org.jdesktop.swingx.JXTable jXTable1;
    private javax.swing.JLabel lblAcDocWarning;
    private javax.swing.JLabel lblAtol;
    private javax.swing.JLabel lblBalanceSelf;
    private javax.swing.JLabel lblBalanceTP;
    private javax.swing.JLabel lblBaseFare;
    private javax.swing.JLabel lblBookingAgtIATA;
    private javax.swing.JLabel lblBookingAgtName;
    private javax.swing.JLabel lblBspCom;
    private javax.swing.JLabel lblCAcFinalBalance;
    private javax.swing.JLabel lblCareer;
    private javax.swing.JLabel lblContactibleDetails;
    private javax.swing.JLabel lblDisc;
    private javax.swing.JLabel lblGDS;
    private javax.swing.JLabel lblGFMessage;
    private javax.swing.JLabel lblGrossFare;
    private javax.swing.JLabel lblLoggedOnUser;
    private javax.swing.JLabel lblLogonTime;
    private javax.swing.JLabel lblNetToPay;
    private javax.swing.JLabel lblNoOfPaxSelfIssue;
    private javax.swing.JLabel lblNoOfPaxTPIssue;
    private javax.swing.JLabel lblProfitLoss;
    private javax.swing.JLabel lblSearchClient;
    private javax.swing.JLabel lblSegment;
    private javax.swing.JLabel lblTax;
    private javax.swing.JLabel lblTicketingAgtIATA;
    private javax.swing.JLabel lblTicketingAgtName;
    private javax.swing.JLabel lblTotalFare;
    private javax.swing.JLabel lblTotalIssueSelf;
    private javax.swing.JLabel lblTotalIssueTP;
    private javax.swing.JLabel lblTotalRefundSelf;
    private javax.swing.JLabel lblTotalRefundTP;
    private javax.swing.JLabel lblVendorPNR;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuItem menuAccountSummery;
    private javax.swing.JMenu menuAccounts;
    private javax.swing.JMenuItem menuAddChg;
    private javax.swing.JMenuItem menuAgentSummery;
    private javax.swing.JMenuItem menuBPBsp;
    private javax.swing.JMenuItem menuBPThirdParty;
    private javax.swing.JMenuItem menuBTRpt;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu menuBilling;
    private javax.swing.JMenuItem menuBillingRptThirdParty;
    private javax.swing.JMenuItem menuBspCom;
    private javax.swing.JMenuItem menuCAccounts;
    private javax.swing.JMenuItem menuCashBook;
    private javax.swing.JMenu menuClient;
    private javax.swing.JMenuItem menuCustSummery;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenu menuHelp;
    private javax.swing.JMenuItem menuInvHistory;
    private javax.swing.JMenuItem menuInvTAndC;
    private javax.swing.JMenuItem menuItinerary;
    private javax.swing.JMenuItem menuMAgent;
    private javax.swing.JMenuItem menuNTktingInv;
    private javax.swing.JMenuItem menuNewAgent;
    private javax.swing.JMenuItem menuOInvHistory;
    private javax.swing.JMenuItem menuOInvMain;
    private javax.swing.JMenuItem menuOInvOutstanding;
    private javax.swing.JMenuItem menuONewInv;
    private javax.swing.JMenuItem menuOutInv;
    private javax.swing.JMenuItem menuOutRfd;
    private javax.swing.JMenuItem menuOutThrdParty;
    private javax.swing.JMenuItem menuPAcDocHistory;
    private javax.swing.JMenuItem menuPCollection;
    private javax.swing.JMenu menuPInvoice;
    private javax.swing.JMenu menuSInvoice;
    private javax.swing.JMenu menuSOther;
    private javax.swing.JMenuItem menuSale;
    private javax.swing.JMenuItem menuSaleRev;
    private javax.swing.JMenuItem menuSendContenttoVendor;
    private javax.swing.JMenu menuSettings;
    private javax.swing.JMenu menuTicketing;
    private javax.swing.JMenu menuTools;
    private javax.swing.JMenu menuTransaction;
    private javax.swing.JMenuItem menuVAccounts;
    private javax.swing.JMenuItem mnuSCreditNote;
    private javax.swing.JMenuItem mnuSDebitNote;
    private javax.swing.JPanel pnlAcDocs;
    private javax.swing.JScrollPane pnlPnr;
    private javax.swing.JPanel pnlPnrToday;
    private javax.swing.JPanel pnlReservation;
    private javax.swing.JPanel pnlSalesSummery;
    private javax.swing.JPanel pnlTask;
    private javax.swing.JPanel pnlTktDetails;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JRadioButton rdoAgent;
    private javax.swing.JRadioButton rdoCustomer;
    private javax.swing.JPanel rightPanel;
    private javax.swing.JPanel searchPnrPanel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JTabbedPane tabsAcDoc;
    private javax.swing.JTabbedPane tabsPnr;
    private javax.swing.JTabbedPane tabsTicket;
    private org.jdesktop.swingx.JXTable tblFareLine;
    private javax.swing.JTable tblItinerary;
    private org.jdesktop.swingx.JXTable tblOtherService;
    private org.jdesktop.swingx.JXTable tblPnr;
    private org.jdesktop.swingx.JXTable tblPnrToday;
    private javax.swing.JTable tblPurchaseAcDoc;
    private org.jdesktop.swingx.JXTable tblReservation;
    private org.jdesktop.swingx.JXTable tblSalesAcDoc;
    private org.jdesktop.swingx.JXTable tblTicket;
    private org.jdesktop.swingx.JXTable tblTransaction;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JTextField txtAtol;
    private javax.swing.JTextPane txtBPane;
    private javax.swing.JTextField txtBaseFare;
    private javax.swing.JTextField txtBookingAgtOid;
    private javax.swing.JTextField txtBspCom;
    private javax.swing.JTextField txtCareer;
    private javax.swing.JTextArea txtContactableDetails;
    private javax.swing.JTextField txtContactableSearch;
    private javax.swing.JTextField txtDisc;
    private javax.swing.JTextField txtGDS;
    private javax.swing.JTextField txtGdsPnr;
    private javax.swing.JTextField txtGrossFare;
    private javax.swing.JTextArea txtName;
    private javax.swing.JTextField txtNetToPay;
    private javax.swing.JTextField txtNumAirCode;
    private javax.swing.JTextPane txtPPane;
    private javax.swing.JTextPane txtPnrRemark;
    private javax.swing.JTextField txtQuickSearch;
    private javax.swing.JTextArea txtRestrictions;
    private javax.swing.JTextField txtRetrievedPnr;
    private javax.swing.JTextField txtSByGDSPNR;
    private javax.swing.JTextField txtSByInvRef;
    private javax.swing.JTextField txtSByPaxName;
    private javax.swing.JTextField txtSByTktNo;
    private javax.swing.JTextPane txtSPane;
    private javax.swing.JTextField txtSegments;
    private javax.swing.JTextField txtStatus;
    private javax.swing.JTextField txtTax;
    private javax.swing.JTextField txtTextField;
    private javax.swing.JTextField txtTicketingAgtOid;
    private javax.swing.JTextField txtTktNo;
    private javax.swing.JTextField txtTotalFare;
    private javax.swing.JTextArea txtVendorDetails;
    private javax.swing.JTextField txtVendorPnr;
    // End of variables declaration//GEN-END:variables

    @Action
    public void mAgent() {
        mainFrame = ETSBackofficeApp.getApplication().getMainFrame();
        frameMAgent = new FrameMasterAgent(mainFrame);
        if (mAgent == null) {
            mAgent = mAgentBo.getmAgent();
        }
        if (frameMAgent.showMAgentDialog(mAgent)) {
            new Thread(new threadMAgent()).start();
        }
    }

    @Action
    public void newAgent() {
        mainFrame = ETSBackofficeApp.getApplication().getMainFrame();
        frameAgent = new FrameAgent(mainFrame);
        agent = agentBo.getNewAgent();
        if (frameAgent.showAgentDialog(agent)) {
            Thread t = new Thread(new threadSaveNewAgent());
            t.start();
        }
    }

    @Action
    public void newCustomer() {
        mainFrame = ETSBackofficeApp.getApplication().getMainFrame();
        frameCustomer = new FrameCustomer(mainFrame);
        customer = new Customer();

        if (frameCustomer.showCustomerDialog(customer)) {
            Thread t = new Thread(new threadSaveNewCustomer());
            t.start();
        }
    }

    @Action
    public void newUser() {
        mainFrame = ETSBackofficeApp.getApplication().getMainFrame();
        frameUser = new FrameUser(mainFrame);
        if (frameUser.showUserDialog(user)) {
            userDao.store(user);
        }
    }

    @Action
    public void dlgTicket(Ticket ticket, int index) {
        mainFrame = ETSBackofficeApp.getApplication().getMainFrame();
        if (ticket.getTktStatus() != 4) {
            DlgTicket frameTicket = new DlgTicket(mainFrame);
            if (frameTicket.showTicketDialog(ticket)) {
                this.tickets.set(index, ticket);
                populateTblTicket();
                setSaveNeeded(true);
            } else {
                frameTicket = null;
            }
        } else {
            DlgTicketR frameRefundTicket = new DlgTicketR(mainFrame);

            if (frameRefundTicket.showTicketDialog(ticket)) {
                this.tickets.set(index, ticket);
                populateTblTicket();
                setSaveNeeded(true);
            } else {
                frameRefundTicket = null;
            }
        }
    }

    private void dlgSegment(Itinerary segment) {
        mainFrame = ETSBackofficeApp.getApplication().getMainFrame();

        DlgItinerary dlgSegment = new DlgItinerary(mainFrame);
        if (dlgSegment.showSegmentDialog(segment)) {
            new Thread(new threadSaveItinerary(segment)).start();
            //setSaveNeeded(true);
        } else {
        }
    }

    @Action
    public void frameBspCom() {
        mainFrame = ETSBackofficeApp.getApplication().getMainFrame();
        frameBspCom = new FrameBspCom(mainFrame, true);
        frameBspCom.showBspComSetting();
    }

    @Action
    public void dlgInvTAndC() {
        mainFrame = ETSBackofficeApp.getApplication().getMainFrame();
        dlgInvTAndC = new DlgInvTermsAndConditions(mainFrame, true);
        dlgInvTAndC.setVisible(true);
    }

    @Action
    public void console() {
        cf.frameConsole();
    }

    @Action
    public void userSummery() {
        cf.userSummery();
    }

    @Action
    public void agentSummery() {
        cf.agentSummery();
    }

    @Action
    public void frameSAcDocHistory() {
        cf.frameSAcDocHistory();
    }

    @Action
    public void frameSOAcDocHistory() {
        cf.frameSOAcDocHistory();
    }

    @Action
    public void frameSOAcDocOutstanding() {
        cf.frameSOAcDocOutstanding();
    }

    @Action
    public void framePAcDocHistory() {
        cf.framePAcDocHistory();
    }

    @Action
    public void frameCashBook() {
        cf.frameCashBook();
    }

    @Action
    public void outInvoiceSummery() {
        cf.outInvoiceSummery();
    }

    @Action
    public void frameOutPInv() {
        cf.outPInv();
    }

    @Action
    public void frameOutPCNote() {
        cf.outPCNote();
    }

    @Action
    public void outRefundSummery() {
        cf.outRefundSummery();
    }

    @Action
    public void framePCollection() {
        cf.framePCollection();
    }

    @Action
    public void framePCollectionRpt() {
        cf.framePCollectionRpt();
    }

    @Action
    public void frameBPThirdParty() {
        cf.frameBPThirdParty();
    }

    @Action
    public void frameBPThirdPartyRpt() {
        cf.frameBPThirdPartyRpt();
    }

    @Action
    public void frameBPBsp() {
        cf.frameBPBsp();
    }

    @Action
    public void frameSale() {
        cf.frameSale();
    }

    @Action
    public void frameSaleWithRev() {
        cf.frameSaleWithRev();
    }
       
    @Action
    public void frameItinerary() {
        cf.frameItinerary();
    }

    @Action
    public void customerSummery() {
        cf.customerSummery();
    }

    @Action
    public void frameAccounts() {
        cf.frameAccounts();
    }

    @Action
    public void frameOInvoiceMain() {
        cf.frameOInvoiceMain();
    }

    @Action
    public void frameCAccounts() {
        cf.frameCAccounts();
    }

    @Action
    public void frameVAccounts() {
        cf.frameVAccounts();
    }

    @Action
    public void frameOServiceManager() {
        cf.frameOServiceManager();
    }

    @Action
    public void frameAddServiceManager() {
        cf.frameAddServiceManager();
    }

    /*@Action
    public void frameTAcDoc() {
        int row = tblSalesAcDoc.getSelectedRow();
        if (row != -1) {
            if (this.pnr != null) {
                if (this.pnr.getInvoice() != null) {
                    Thread t = new Thread(new threadLoadAcDocWithRealtedDoc(this.acDocs.get(row).getAcDocId()));
                    t.start();
                    try {
                        t.join();
                        cf.frameTAcDoc(this.invoice);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ETSBackofficeMain.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Select Invoice", "Edit Invoice", JOptionPane.WARNING_MESSAGE);
        }
    }*/

    @Action
    public void frameOAcDoc() {
        OAccountingDocument oAcDoc = new OAccountingDocument();
        cf.frameOAcDoc(oAcDoc);
    }

    public void quickSearch() {
        String completeRef = txtQuickSearch.getText();
        String suffix, invRef;

        suffix = completeRef.substring(11);
        invRef = completeRef.replaceAll("[^0-9]", "");
        new Thread(new threadQuickSearch(Integer.valueOf(invRef), suffix)).start();
    }

    private boolean allowFinancialAction(BigDecimal cLimit, BigDecimal acBalance) {
        boolean proceed = false;
        BigDecimal tempAcBal = acBalance;
        
        if (acBalance.signum() == -1) {
            acBalance = acBalance.multiply(new BigDecimal("-1"));
        }

        if (acBalance.compareTo(cLimit) < 0) {
            proceed = true;
        } else {
            int choice = JOptionPane.showConfirmDialog(null, "Credit limit validation failed..."
                    + "\n\nCredit Limit: "+cLimit
                    +"\nAC Balance: "+tempAcBal
                    +"\n\nDo you want to proceed invoicing?", "Credit Limit Validation", JOptionPane.YES_NO_OPTION);
            if (choice == 0) {
                proceed = true;

            } else if (choice == 1) {
                proceed = false;
            }
        }
        return proceed;
    }

    private void issueInvoice() {

        mainFrame = ETSBackofficeApp.getApplication().getMainFrame();
        dlgTInvoice = new DlgTInvoice(mainFrame);
        acDocBo = (AccountingDocBo) ETSBackofficeApp.getApplication().ctx.getBean("acDocBo");
        AccountingDocument newInvoice = acDocBo.newSAcDoc();
        AccountingDocumentLine ticketLine = new AccountingDocumentLine();
        AccountingDocumentLine serviceLine = new AccountingDocumentLine();

        for (Ticket t : pnr.getTickets()) {
            if (t.getAccountingDocumentLine().isEmpty() && t.getTktStatus() != 4) {
                ticketLine.setType(1);
                ticketLine.setAccountingDocument(newInvoice);
                //ticket.addAcDocLine(ticketLine);
                ticketLine.addTicket(t);
                if (!t.getPurchaseAccountingDocumentLine().isEmpty()) {
                    newInvoice.addPAcDoc(t.getPurchaseAccountingDocumentLine().iterator().next().getPurchaseAccountingDocument());
                }
            }
        }

        for (Services s : this.otherServicesInPnr) {
            if (s.getAccountingDocumentLine().isEmpty() && s.getOriginalItemId() ==null) {//Checking not to allow refunded item
                serviceLine.setType(2);
                serviceLine.setAccountingDocument(invoice);
                serviceLine.addService(s);
            }
        }

        if (ticketLine.getTickets().size() > 0) {
            if (validateSellingFare(ticketLine.getTickets())) {
                newInvoice.addLine(ticketLine);
                newInvoice.setPnr(this.pnr);
                newInvoice.setAcDoctype(1);

                if (serviceLine.getServices().size() > 0) {
                    newInvoice.addLine(serviceLine);
                }

                if (dlgTInvoice.showDialog(newInvoice)) {
                } else {

                    long pnrId = this.pnr.getPnrId();
                    Thread t = new Thread(new threadLoadCompletePnr(pnrId));
                    t.start();
                    try {
                        t.join();
                        Thread t1 = new Thread(new threadLoadAcDocs(pnr.getPnrId()));
                        t1.start();
                        t1.join();
                        populateTblSalesAcDoc();
                        new Thread(new threadUninvoicedPnr()).start();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ETSBackofficeMain.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "No Uninvoiced Ticket...", "Issue Invoice", JOptionPane.WARNING_MESSAGE);
        }
    }

    private boolean validateSellingFare(Set<Ticket> ticketsToInvoice) {
        boolean okToInvoice = false;
        String unvalidatedTicketNo = "";
        int i = 0;
        for (Ticket t : ticketsToInvoice) {
            if (t.getNetPayble().compareTo(t.getNetFare()) < 0 && t.getTktStatus()!=1) {
                unvalidatedTicketNo = t.getFullPaxName().concat(" , Selling Fare : " + t.getNetPayble());
                i++;
            }else if(t.getNetPayble().compareTo(new BigDecimal("0.00")) == 0 && t.getTktStatus()==1){
             unvalidatedTicketNo = t.getFullPaxName().concat(" , Selling Fare : " + t.getNetPayble());
                i++;
            }
        }

        if (unvalidatedTicketNo.isEmpty()) {
            okToInvoice = true;
        } else {
            //int choice = JOptionPane.showConfirmDialog(null, "Selling fare validation failed! Check tickets bellow:\n\n"
                  //  + unvalidatedTicketNo + "\n\nDo you wish to continue new invoice? ", "Issue Invoice", JOptionPane.YES_NO_OPTION);
            JOptionPane.showMessageDialog(null, "Selling fare validation failed! Check tickets bellow:\n\n"
                    + unvalidatedTicketNo + "\n\nCould not proceed to invoicing...", "Issue Invoice", JOptionPane.WARNING_MESSAGE);
           okToInvoice = false;
            /* if (choice == 0) {
                okToInvoice = true;
            } else if (choice == 1) {
                okToInvoice = false;
            }*/
        }
        return okToInvoice;
    }
    //@Action

    private void issueTktCreditNote() {

        mainFrame = ETSBackofficeApp.getApplication().getMainFrame();
        dlgTCNote = new DlgTCNote(mainFrame);

        acDocBo = (AccountingDocBo) ETSBackofficeApp.getApplication().ctx.getBean("acDocBo");
        AccountingDocument newTktCreditNote = acDocBo.newSAcDoc();
        //AccountingDocumentLine serviceLine = new AccountingDocumentLine();

        List<Ticket> ticketsToCredit = new ArrayList();
        List<Services> servicesToCredit = new ArrayList();

        List<AccountingDocument> currentInvoices = new ArrayList();

        for (Ticket t : pnr.getTickets()) {
            if (t.getAccountingDocumentLine().isEmpty() && (t.getTktStatus() == 4)) {
                ticketsToCredit.add(t);
            }
        }

        for (Services s : this.otherServicesInPnr) {
            if (s.getAccountingDocumentLine().isEmpty() && s.getOriginalItemId() != null) {
                servicesToCredit.add(s);
            }
        }

        for (AccountingDocument acDoc : this.acDocs) {
            if (acDoc.getAcDoctype() == 1) {
                currentInvoices.add(acDoc);
            }
        }

        //Add any ticket to credit
        loop:
        for (AccountingDocument currentInvoice : currentInvoices) {

            acDocBo.findAcDocById(currentInvoice.getAcDocId());
            currentInvoice = acDocBo.getAccountingDocument();
            Set<Ticket> currentTickets = currentInvoice.getTickets();
            Set<Services> currentServices = new LinkedHashSet(currentInvoice.getAllServices());

            for (Ticket currentticket : currentTickets) {
                for (Ticket ticketToCredit : ticketsToCredit) {
                    if (ticketToCredit.getOrginalTicketNo() != null) {
                        if (ticketToCredit.getOrginalTicketNo().equals(currentticket.getTicketNo())) {
                            AccountingDocumentLine ticketLine = new AccountingDocumentLine();
                            ticketLine.setType(1);
                            ticketLine.addTicket(ticketToCredit);
                            ticketLine.setAccountingDocument(newTktCreditNote);
                            newTktCreditNote.getAccountingDocumentLines().add(ticketLine);
                            if (!ticketToCredit.getPurchaseAccountingDocumentLine().isEmpty()) {
                                newTktCreditNote.addPAcDoc(ticketToCredit.getPurchaseAccountingDocumentLine().iterator().next().getPurchaseAccountingDocument());
                            }
                        }
                    } else {
                        if (ticketToCredit.getTicketNo().equals(currentticket.getTicketNo())) {
                            AccountingDocumentLine ticketLine = new AccountingDocumentLine();
                            ticketLine.setType(1);
                            ticketLine.addTicket(ticketToCredit);
                            ticketLine.setAccountingDocument(newTktCreditNote);
                            newTktCreditNote.getAccountingDocumentLines().add(ticketLine);
                            if (!ticketToCredit.getPurchaseAccountingDocumentLine().isEmpty()) {
                                newTktCreditNote.addPAcDoc(ticketToCredit.getPurchaseAccountingDocumentLine().iterator().next().getPurchaseAccountingDocument());
                            }
                        }
                    }
                }
            }

            //Add any service to credit
            for (Services currentService : currentServices) {
                for (Services serviceToCredit : servicesToCredit) {
                   
                        if (serviceToCredit.getOriginalItemId().equals(currentService.getServiceId())) {
                            AccountingDocumentLine serviceLine = new AccountingDocumentLine();
                            serviceLine.setType(2);
                            serviceLine.addService(serviceToCredit);
                            serviceLine.setAccountingDocument(newTktCreditNote);
                            newTktCreditNote.getAccountingDocumentLines().add(serviceLine);
                        }
                    
                }
            }

            if (newTktCreditNote.getAccountingDocumentLines().size() > 0) {
                newTktCreditNote.setAcDocRef(currentInvoice.getAcDocRef());
                newTktCreditNote.setAccountingDocument(currentInvoice);
                newTktCreditNote.setAcDoctype(2);
                break loop;
            }
        }


        if (newTktCreditNote.getAccountingDocumentLines().size() > 0) {
            if (validateSellingFare(newTktCreditNote.getTickets())) {
                int doCreditNote = 1;
                newTktCreditNote.setPnr(this.pnr);

                if (dlgTCNote.showDialog(newTktCreditNote)) {
                } else {
                    long pnrId = this.pnr.getPnrId();
                    Thread t = new Thread(new threadLoadCompletePnr(pnrId));
                    t.start();
                    try {
                        t.join();
                        Thread t1 = new Thread(new threadLoadAcDocs(pnr.getPnrId()));
                        t1.start();
                        t1.join();
                        populateTblSalesAcDoc();
                        new Thread(new threadUninvoicedPnr()).start();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ETSBackofficeMain.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "No Refunded Ticket...", "Issue Credit Note", JOptionPane.WARNING_MESSAGE);
        }
    } 

    //@Action
    private void issueSCreditNote() {

        int row = tblSalesAcDoc.getSelectedRow();

        mainFrame = ETSBackofficeApp.getApplication().getMainFrame();
        frameCNote = new DlgCNote(mainFrame);
        frameCNote.setTitle("Sales Credit Note");
        acDocBo = (AccountingDocBo) ETSBackofficeApp.getApplication().ctx.getBean("acDocBo");
        AccountingDocument newCreditNote = acDocBo.newSAcDoc();

        if (this.saveNeeded == false) {
            if (row != -1) {
                AccountingDocument acDoc = this.acDocs.get(row);
                if (acDoc.getAcDoctype() == 1) {
                    newCreditNote.setPnr(this.pnr);
                    newCreditNote.setAcDocRef(acDoc.getAcDocRef());
                    newCreditNote.setAccountingDocument(acDoc);
                    newCreditNote.setAcDoctype(3);
                    if (frameCNote.showCreditNoteDialog(newCreditNote)) {
                        Thread t = new Thread(new threadSaveNewCreditNote(newCreditNote));
                        t.start();
                        try {
                            t.join();
                            Thread t1 = new Thread(new threadLoadAcDocs(pnr.getPnrId()));
                            t1.start();
                            t1.join();
                            populateTblSalesAcDoc();
                            //this.pnr.getAccountingDocuments().add(acDocBo.getAccountingDocument());
                            //populateTblSalesAcDoc();
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ETSBackofficeMain.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Only invoice can be credited !!!", "Issue Credit Note", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Select Invoice", "Issue Credit Note", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void issueSDebitNote() {

        int row = tblSalesAcDoc.getSelectedRow();

        mainFrame = ETSBackofficeApp.getApplication().getMainFrame();
        frameDNote = new DlgDNote(mainFrame);
        frameDNote.setTitle("Sales Debit Note");
        
        acDocBo = (AccountingDocBo) ETSBackofficeApp.getApplication().ctx.getBean("acDocBo");
        AccountingDocument newDebitNote = acDocBo.newSAcDoc();

        if (this.saveNeeded == false) {
            if (row != -1) {
                AccountingDocument acDoc = this.acDocs.get(row);
                if (acDoc.getAcDoctype() == 1) {
                    newDebitNote.setPnr(this.pnr);
                    newDebitNote.setAcDocRef(acDoc.getAcDocRef());
                    newDebitNote.setAccountingDocument(acDoc);
                    newDebitNote.setAcDoctype(4);
                    if (frameDNote.showDebitNoteDialog(newDebitNote)) {
                        Thread t = new Thread(new threadSaveNewCreditNote(newDebitNote));
                        t.start();
                        try {
                            t.join();
                            Thread t1 = new Thread(new threadLoadAcDocs(pnr.getPnrId()));
                            t1.start();
                            t1.join();
                            populateTblSalesAcDoc();                            
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ETSBackofficeMain.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Only invoice can be credited !!!", "Issue Credit Note", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Select Invoice", "Issue Credit Note", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
       
      private void issuePCreditNote() {

        int row = tblPurchaseAcDoc.getSelectedRow();

        mainFrame = ETSBackofficeApp.getApplication().getMainFrame();
        frameCNote = new DlgCNote(mainFrame);
        frameCNote.setTitle("Purchase Credit Note");
        acDocBo = (AccountingDocBo) ETSBackofficeApp.getApplication().ctx.getBean("acDocBo");
        PurchaseAccountingDocument newCreditNote = acDocBo.newPAcDoc();

        if (this.saveNeeded == false) {
            if (row != -1) {
                PurchaseAccountingDocument acDoc = this.pAcDocs.get(row);
                if (acDoc.getAcDoctype() == 1) {
                    newCreditNote.setPnr(this.pnr);
                    newCreditNote.setRecipientRef(acDoc.getRecipientRef());
                    newCreditNote.setPurchaseAccountingDocument(acDoc);
                    newCreditNote.setAcDoctype(3);
                    if (frameCNote.showCreditNoteDialog(newCreditNote)) {
                        Thread t = new Thread(new threadSaveNewPCreditNote(newCreditNote));
                        t.start();
                        try {
                            t.join();
                            Thread t1 = new Thread(new threadPurchaseAcDoc(pnr.getPnrId()));
                            t1.start();
                            t1.join();
                            populateTblPurchaseAcDoc();
                            //this.pnr.getAccountingDocuments().add(acDocBo.getAccountingDocument());
                            //populateTblSalesAcDoc();
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ETSBackofficeMain.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Only invoice can be credited !!!", "Issue Credit Note", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Select Invoice", "Issue Credit Note", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    private void issuePDebitNote() {

        int row = tblPurchaseAcDoc.getSelectedRow();

        mainFrame = ETSBackofficeApp.getApplication().getMainFrame();
        frameDNote = new DlgDNote(mainFrame);
        frameDNote.setTitle("Purchase Debit Note");
        
        acDocBo = (AccountingDocBo) ETSBackofficeApp.getApplication().ctx.getBean("acDocBo");
        PurchaseAccountingDocument newDebitNote = acDocBo.newPAcDoc();

        if (this.saveNeeded == false) {
            if (row != -1) {
                PurchaseAccountingDocument acDoc = this.pAcDocs.get(row);
                if (acDoc.getAcDoctype() == 1) {
                    newDebitNote.setPnr(this.pnr);
                    newDebitNote.setRecipientRef(acDoc.getRecipientRef());
                    newDebitNote.setPurchaseAccountingDocument(acDoc);
                    newDebitNote.setAcDoctype(4);
                    if (frameDNote.showDebitNoteDialog(newDebitNote)) {
                        Thread t = new Thread(new threadSaveNewPCreditNote(newDebitNote));
                        t.start();
                        try {
                            t.join();
                            Thread t1 = new Thread(new threadPurchaseAcDoc(pnr.getPnrId()));
                            t1.start();
                            t1.join();
                            populateTblPurchaseAcDoc();
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ETSBackofficeMain.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Only invoice can be credited !!!", "Issue Credit Note", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Select Invoice", "Issue Credit Note", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
      
    @Action
    public void viewAcDoc(AccountingDocument acDoc) {
        mainFrame = ETSBackofficeApp.getApplication().getMainFrame();

        if (acDoc.getAcDoctype() == 1) {
            if (dlgTInvoice == null) {
                dlgTInvoice = new DlgTInvoice(mainFrame);
                if (dlgTInvoice.showDialog(acDoc)) {
                    dlgTInvoice.dispose();
                } else {
                    //new Thread(new threadPurchaseAcDoc(pnr.getPnrId())).start();
                    reloadPNR();
                }
            } else {
                if (dlgTInvoice.showDialog(acDoc)) {
                    dlgTInvoice.dispose();
                } else {
                    //new Thread(new threadPurchaseAcDoc(pnr.getPnrId())).start();
                    reloadPNR();
                }
            }
        } else if (acDoc.getAcDoctype() == 2) {
            if (dlgTCNote == null) {
                dlgTCNote = new DlgTCNote(mainFrame);
                if (dlgTCNote.showDialog(acDoc)) {
                    dlgTCNote.dispose();
                } else {
                    reloadPNR();
                }
            } else {
                if (dlgTCNote.showDialog(acDoc)) {
                    dlgTCNote.dispose();
                } else {
                    reloadPNR();
                }
            }
        } else if (acDoc.getAcDoctype() == 3) {
            if (frameCNote == null) {
                frameCNote = new DlgCNote(mainFrame);
                if (frameCNote.showCreditNoteDialog(acDoc)) {
                    frameCNote.dispose();
                } else {
                    reloadPNR();
                }
            } else {
                if (frameCNote.showCreditNoteDialog(acDoc)) {
                    frameCNote.dispose();
                } else {
                    reloadPNR();
                }
            }
        }
    }

    @Action
    public void viewTPAcDocDialogue(PurchaseAccountingDocument acDoc) {
        mainFrame = ETSBackofficeApp.getApplication().getMainFrame();

        if (acDoc.getAcDoctype() == 1) {
            dlgTPInvoice = new DlgTPInvoice(mainFrame);
            if (dlgTPInvoice.showDialog(acDoc)) {
                dlgTPInvoice.dispose();
            } else {
                //new Thread(new threadPurchaseAcDoc(pnr.getPnrId())).start();
            }
        } else if (acDoc.getAcDoctype() == 2) {
            dlgTPCNote = new DlgTPCNote(mainFrame);
            if (dlgTPCNote.showDialog(acDoc)) {
                dlgTCNote.dispose();
            }
        }else if(acDoc.getAcDoctype()==3 || acDoc.getAcDoctype()==4){
         frameCNote = new DlgCNote(mainFrame);
         if(frameCNote.showCreditNoteDialog(acDoc)){
         
         }
        }
    }

    @Action
    public void Transaction() {
        mainFrame = ETSBackofficeApp.getApplication().getMainFrame();
        if (!this.acDocs.isEmpty()) {
            DlgTransaction frameTransaction = new DlgTransaction(mainFrame);
            if (frameTransaction.showTransactionDialog(this.acDocs)) {
            } else {
                frameTransaction = null;
            }
        }
    }
    //**************************Threads***************************************
    //Thread1 to load complete pnr;
    private long startTime = 0;
    private long stopTime = 0;
    private float elapsedTime = 0;

    /**
     * @param pnrDao the pnrDao to set
     */
    private class threadLoadCompletePnr implements Runnable {

        private long pnrId;

        public threadLoadCompletePnr(long pnrId) {
            this.pnrId = pnrId;
        }

        public void run() {
            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();

            statusMessageLabel.setText("Loading PNR details...");
            pnrBo.loadCompletePNR(pnrId);

            pnr = pnrBo.getPnr();
            tickets = pnrBo.getTickets();
            ticket = tickets.get(0);
            otherServicesInPnr = pnrBo.getOtherServices();
            //acDocs = pnrBo.getAccountingDocuments();
            //pAcDocs = pnrBo.getPAccountingDocuments();
            

            statusMessageLabel.setText("Populating Client Details...");

            
            
            if (pnr.getTicketingAgt() != null) {
                populateTxtVendorDetails(pnr.getTicketingAgt());
            } else {
                txtVendorDetails.setText("");
            }
            statusMessageLabel.setText("Populating PNR Details...");
            populatePnrDetails();

            statusMessageLabel.setText("Populating Tickets...");
            populateTblTicket();
            populateTblFareLine();
            populateTblOtherService();
            //populateTblSalesAcDoc();
            //populateTicketDetails();
            if (pnr.getCustomer() == null && pnr.getAgent() == null) {
                decideAcDocAllocatedTo();
            } else {
                populateClientDetails();
            }
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            progressBar.repaint(); //Refresh graphics
        }
    }

    private class threadSearchContactable implements Runnable {

        public threadSearchContactable() {
        }

        public void run() {
            progressBar = new JProgressBar();
            busyLabel.setBusy(true);
            btnContactableSearch.setEnabled(false);
            progressBar.setIndeterminate(true); //Set value
            progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();

            statusMessageLabel.setText("Searching client...");
            searchContactable();

            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            progressBar.repaint(); //Refresh graphics
            btnContactableSearch.setEnabled(true);
            busyLabel.setBusy(false);
        }
    }

    private class threadMAgent implements Runnable {

        public threadMAgent() {
        }

        public void run() {
            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();

            statusMessageLabel.setText("Saving Master Agent...");
            mAgentBo.saveMAgent(mAgent);

            statusMessageLabel.setText("Refreshing Master Agent...");
            mAgent = mAgentBo.loadMAgent();
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            progressBar.repaint(); //Refresh graphics
        }
    }

    private class threadSaveNewAgent implements Runnable {

        public threadSaveNewAgent() {
        }

        public void run() {
            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();

            statusMessageLabel.setText("Saving Agent...");
            agentBo.save(agent);

            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            progressBar.repaint(); //Refresh graphics
        }
    }

    private class threadSaveNewCustomer implements Runnable {

        public threadSaveNewCustomer() {
        }

        public void run() {
            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();

            statusMessageLabel.setText("Saving Customer...");
            customerBo.saveCustomer(customer);
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            progressBar.repaint(); //Refresh graphics
        }
    }

    private class threadSaveNewCreditNote implements Runnable {

        private AccountingDocument cnote;

        public threadSaveNewCreditNote(AccountingDocument cnote) {
            this.cnote = cnote;
        }

        public void run() {
            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();

            statusMessageLabel.setText("Creating credit note...");
            acDocBo.setAcDoc(cnote);
            acDocBo.saveAcDoc();

            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Credit Note Created. Total time : " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            progressBar.repaint(); //Refresh graphics
        }
    }

      private class threadSaveNewPCreditNote implements Runnable {

        private PurchaseAccountingDocument pAcDoc;

        public threadSaveNewPCreditNote(PurchaseAccountingDocument pAcDoc) {
            this.pAcDoc = pAcDoc;
        }

        public void run() {
            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();

            statusMessageLabel.setText("Creating credit note...");
            acDocBo.setPurchaseAccountingDocument(pAcDoc);
            acDocBo.savePurchaseAcDoc();

            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Credit Note Created. Total time : " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            progressBar.repaint(); //Refresh graphics
        }
    }
      
    private class threadSavePnr implements Runnable {

        public threadSavePnr() {
        }

        public void run() {
            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();

            statusMessageLabel.setText("Saving Pnr...");
            savePnr();

            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            progressBar.repaint(); //Refresh graphics
        }
    }

    private class threadUninvoicedPnr implements Runnable {

        private List<PNR> uninvoicedPNRs = new ArrayList();

        public threadUninvoicedPnr() {
        }

        public void run() {
            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();
            busyLblTask.setBusy(true);
            statusMessageLabel.setText("Loading Pnr...");

            pnrs = pnrBo.uninvoicedPnrs();
            populateTblPnr();

            busyLblTask.setBusy(false);
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            progressBar.repaint(); //Refresh graphics

        }
    }

    private class scanUnInvoicedPNR implements Runnable {

        public void run() {
            while (true) {
                Thread t = new Thread(new threadUninvoicedPnr());
                t.start();
                try {
                    t.join();
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ETSBackofficeMain.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private class threadPnrsToday implements Runnable {

        public threadPnrsToday() {
        }

        public void run() {
            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();

            busyLblPnrToDay.setBusy(true);
            statusMessageLabel.setText("Loading Pnr...");

            pnrsToday = pnrBo.pnrsToday();
            populateTblPnrToday();
            busyLblPnrToDay.setBusy(false);
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            progressBar.repaint(); //Refresh graphics
        }
    }

    private class threadBookedPnrs implements Runnable {

        public threadBookedPnrs() {
        }

        public void run() {
            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();

            busyLblPnrReservation.setBusy(true);
            statusMessageLabel.setText("Loading Pnr...");

            bookedPnrs = pnrBo.bookedPnrs();
            populateTblReservation();
            busyLblPnrReservation.setBusy(false);
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            progressBar.repaint(); //Refresh graphics
        }
    }

    private class threadSales implements Runnable {

        public threadSales() {
        }

        public void run() {
            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();
            statusMessageLabel.setText("Loading...");

            searchSales();
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            progressBar.repaint(); //Refresh graphics
        }
    }

    private class threadDeletePnr implements Runnable {

        private PNR pnr;

        public threadDeletePnr(PNR pnr) {
            this.pnr = pnr;
        }

        public void run() {
            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();

            statusMessageLabel.setText("Deleting Pnr...");

            pnrBo.setPnr(this.pnr);
            pnrBo.deletePNR();

            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("PNR sucessfully deleted. Total time : " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            progressBar.repaint(); //Refresh graphics
        }
    }

    private class threadItinerary implements Runnable {

        private long pnrId;

        public threadItinerary(long pnrId) {
            this.pnrId = pnrId;
        }

        public void run() {
            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();

            statusMessageLabel.setText("Loading Itinerary...");

            itineraryBo.loadItinerary(pnrId);
            segments = itineraryBo.getSegments();

            statusMessageLabel.setText("Populating Itinerary...");
            populateTblItinerary();
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Task completed in : " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            progressBar.repaint(); //Refresh graphics
        }
    }

    private class threadPurchaseAcDoc implements Runnable {

        private long pnrId;

        public threadPurchaseAcDoc(long pnrId) {
            this.pnrId = pnrId;
        }

        public void run() {
            progressBar = new JProgressBar();
            busyLabelAcDoc.setText("Loading...");
            busyLabelAcDoc.setBusy(true);

            pAcDocs = acDocBo.findPurchaseAcDocByPnrId(pnrId);

            busyLabelAcDoc.setText("");
            busyLabelAcDoc.setBusy(false);
        }
    }

    private class threadLoadAcDocs implements Runnable {

        private long pnrId;

        public threadLoadAcDocs(long pnrId) {
            this.pnrId = pnrId;
        }

        public void run() {
            progressBar = new JProgressBar();
            busyLabelAcDoc.setText("Loading...");
            busyLabelAcDoc.setBusy(true);

            //acDocs = acDocBo.findAcDocByPnrId(pnrId);
            acDocs = acDocBo.findAcDocByPnrIdWithPurchaseInfo(pnrId); 
            //populateTblSalesAcDoc();
            comtrollComponents();
            busyLabelAcDoc.setText("");
            busyLabelAcDoc.setBusy(false);
            /*if (pnr.getContactableId() != null) {
                new Thread(new threadCheckAcBalance(pnr.getContactableId())).start();
            }*/
        }
    }

    private class threadLoadVendors implements Runnable {

        public threadLoadVendors() {
        }

        public void run() {
            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();

            busyLabelVendor.setBusy(true);
            statusMessageLabel.setText("Loading Vendors...");
            btnLoadVendors.setEnabled(false);

            vendors = agentBo.ticketingAgtList();

            loadVendors();
            btnLoadVendors.setEnabled(true);
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Vendor List Loaded in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            progressBar.repaint(); //Refresh graphics
            busyLabelVendor.setBusy(false);
        }
    }

    private class threadLoadAcDocWithRealtedDoc implements Runnable {

        private long acDocId;

        public threadLoadAcDocWithRealtedDoc(long acDocId) {
            this.acDocId = acDocId;
        }

        public void run() {

            //progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            //progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();
            statusMessageLabel.setText("Searching...");

            acDocBo.findCompleteAcDocWithRelatedDocsById(acDocId);
            invoice = acDocBo.getAccountingDocument();
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Search Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            //progressBar.repaint(); //Refresh graphics
        }
    }

    private class threadLoadAcDoc implements Runnable {

        private long acDocId;

        public threadLoadAcDoc(long acDocId) {
            this.acDocId = acDocId;
        }

        public void run() {

            //progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            //progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();
            statusMessageLabel.setText("Searching...");

            acDocBo.findAcDocById(acDocId);
            invoice = acDocBo.getAccountingDocument();
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Search Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            //progressBar.repaint(); //Refresh graphics
        }
    }

    private class threadQuickSearch implements Runnable {

        private int refNo;
        private String suffix;

        public threadQuickSearch(int refNo, String suffix) {
            this.refNo = refNo;
            this.suffix = suffix;
        }

        public void run() {

            busyLabelSQ.setBusy(true);

            if (suffix.equals("TIN")) {
                //cf.frameTAcDoc(acDocBo.findCompleteAcDocWithRelatedDocsByRef(refNo));
            } else if (suffix.equals("OIN")) {
                cf.frameOAcDoc(oAcDocBo.findCompleteAcDocByByRef(refNo));
            }

            busyLabelSQ.setBusy(false);
        }
    }

    private class threadSaveItinerary implements Runnable {

        private Itinerary s;

        public threadSaveItinerary(Itinerary s) {
            this.s = s;
        }

        public void run() {

            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            //progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();
            statusMessageLabel.setText("Saving ticket...");
            itineraryBo.save(s);
            populateTblItinerary();
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            //progressBar.repaint(); //Refresh graphics
        }
    }
    //Main Frame variable declatation
    private ControllFrame cf = new ControllFrame();
    private JFrame mainFrame = ETSBackofficeApp.getApplication().getMainFrame();
    AbstractDocument doc;
    // public ContactInfo basicContactInfo;
    private GDS gds;
    private OfficeID oid;
    public FrameMasterAgent frameMAgent;
    private FrameGDS frameGDS;
    private FrameAgent frameAgent;
    private FrameCustomer frameCustomer;
    private DlgTInvoice dlgTInvoice;
    private DlgTCNote dlgTCNote;
    private DlgTPCNote dlgTPCNote;
    private DlgCNote frameCNote;
    private DlgDNote frameDNote;
    private DlgTPInvoice dlgTPInvoice;
    private DlgInvTermsAndConditions dlgInvTAndC;
    private FrameUser frameUser;
    private FrameBspCom frameBspCom;
    private boolean saveNeeded;

    class ColorColumnRenderer extends DefaultTableCellRenderer {

        public ColorColumnRenderer() {
            super();
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            String s = table.getModel().getValueAt(row, 2).toString();

            if (s.equalsIgnoreCase("BOOK")) {
                comp.setForeground(Color.cyan);
            } else if (s.equalsIgnoreCase("ISSUE")) {
                comp.setForeground(Color.green);
            } else if (s.equalsIgnoreCase("REISSUE")) {
                comp.setForeground(Color.blue);
            } else if (s.equalsIgnoreCase("REFUND")) {
                comp.setForeground(Color.red);
            } else if (s.equalsIgnoreCase("VOID")) {
                comp.setForeground(Color.ORANGE);
            } else {
                comp.setForeground(Color.black);
            }

            return comp;
        }
    }

    public void retreivePnrInAmadeus(String pnr) {
        try {
            Robot r = new Robot();
            r.delay(1000);//Wait to get amadeus ready and finish all process

            TextTransfer ts = new TextTransfer();
            //Ignore existing anything on Amadeus
            ts.setClipboardContents("IG");
            System.out.println("Clipboard: " +ts.getClipboardContents());
            //Paste "IG"
            r.keyPress(KeyEvent.VK_CONTROL);
            r.keyPress(KeyEvent.VK_V);
            r.keyRelease(KeyEvent.VK_CONTROL);
            r.keyRelease(KeyEvent.VK_V);
            r.delay(400);
            System.out.println("Clipboard: " +ts.getClipboardContents());
            //Press Enter
            r.keyPress(KeyEvent.VK_ENTER);
            r.keyRelease(KeyEvent.VK_ENTER);
            r.delay(1000);
            //Retrieve pnr
            ts.setClipboardContents("RT"+pnr);
            //Paste
            System.out.println("Clipboard: " +ts.getClipboardContents());
            r.keyPress(KeyEvent.VK_CONTROL);
            r.keyPress(KeyEvent.VK_V);
            r.keyRelease(KeyEvent.VK_CONTROL);
            r.keyRelease(KeyEvent.VK_V);
            r.delay(400);
            r.keyPress(KeyEvent.VK_ENTER);
            r.keyRelease(KeyEvent.VK_ENTER);
        } catch (AWTException e) {
            //e.printStackTrace();
            System.out.println("Exception in robot: " + e);
        }
    }

    private class threadCheckAcBalance implements Runnable {

        private long contId;

        public threadCheckAcBalance(long contId) {
            this.contId = contId;
        }

        public void run() {
            //statusMessageLabel.setText("Caulculating account balance...");
            clientAcBalance = accountsBo.finalClientAcBalance(contId);
            lblCAcFinalBalance.setText("ACBalance: "+clientAcBalance.toString()+" ");
            //statusMessageLabel.setText("Done...");
        }
    }
    
    private class PopulateCmbServiceTitle extends SwingWorker<List<OtherService>, Void> {
        
        public PopulateCmbServiceTitle(){
            oServices.clear();
            additionalServices.clear();
        };
        @Override
        protected List<OtherService> doInBackground() throws Exception {
            List<OtherService> services = oServiceBo.loadAllServices();            
            return services;
        }

        @Override
        protected void done() {
            try {
                services = get();
                for (OtherService s : services) {
                    if (s.getServiceType() == 1) {
                        oServices.add(s);
                    } else if (s.getServiceType() == 2) {
                        additionalServices.add(s);
                    }
                }

                List cmbElement = new ArrayList();
                List cmbAdditionalElement = new ArrayList();

                for (OtherService o : oServices) {
                    cmbElement.add(o.getServiceTitle());
                }
                for (OtherService o : additionalServices) {
                    cmbAdditionalElement.add(o.getServiceTitle());
                }
                cmbOtherSTitleModel = new DefaultComboBoxModel(cmbElement.toArray());
                cmbOtherSTitle.setModel(cmbOtherSTitleModel);
                cmbOtherSTitle.addActionListener(cmbOtherSTitleListener);
                
                cmbOtherSTitle.setRenderer(new cmbOServiceRenderer());
                //cmbAdditionalSTitleModel = new DefaultComboBoxModel(cmbAdditionalElement.toArray());
                //cmbAdditionalSTitle.setModel(cmbAdditionalSTitleModel);
                //cmbAdditionalSTitle.addActionListener(cmbAdditionalSTitleListener);
            } catch (InterruptedException ex) {
                Logger.getLogger(FrameSAcDocHistory.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(FrameSAcDocHistory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }  
    
    private class PopulateTblOtherService extends SwingWorker<List<Services>, Void> {
        
        public PopulateTblOtherService(){
            
        };
        @Override
        protected List<Services> doInBackground() throws Exception {
            List<Services> services = oServiceBo.loadOtherServicesFromPnr(pnr.getPnrId());            
            return services;
        }

        @Override
        protected void done() {
            try {
                otherServicesInPnr = get();                
                populateTblOtherService();
            } catch (InterruptedException ex) {
                Logger.getLogger(FrameSAcDocHistory.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(FrameSAcDocHistory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    } 
    
    private class RefundOtherService extends SwingWorker<Services, Void> {

        private Services s;

        public RefundOtherService(Services s) {
            this.s = s;
        };
        
        @Override
        protected Services doInBackground() throws Exception {
            Services newRefundedService = new Services();
            newRefundedService.setServiceTitle(s.getServiceTitle());
            newRefundedService.setServiceCost(s.getServiceCost().negate());
            newRefundedService.setServiceCharge(s.getServiceCharge().negate());
            newRefundedService.setDiscount(s.getDiscount().multiply(new BigDecimal("-1")));
            newRefundedService.setUnit(s.getUnit());  
            newRefundedService.setServiceType(1);
            newRefundedService.setOriginalItemId(s.getServiceId());
            newRefundedService.setOtherService(s.getOtherService());
            newRefundedService.setPnr(pnr);
            return newRefundedService;
        }
        
        @Override
        protected void done() {
            try {
                Services newRefundedService = get();
                otherServicesInPnr.add(newRefundedService);
                populateTblOtherService();
                setSaveNeeded(true);
            } catch (InterruptedException ex) {
                Logger.getLogger(FrameSAcDocHistory.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(FrameSAcDocHistory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    class cmbOServiceRenderer extends BasicComboBoxRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
                if (-1 < index) {
                    list.setToolTipText(oServices.get(index).getRemark());                    
                }
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            setFont(list.getFont());
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }
}
