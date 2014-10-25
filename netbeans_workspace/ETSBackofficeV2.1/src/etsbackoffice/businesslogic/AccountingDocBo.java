package etsbackoffice.businesslogic;

import etsbackoffice.datalogic.AcDocDao;
import etsbackoffice.domain.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Yusuf
 */
public class AccountingDocBo {
    
    private AcDocDao acDocDao;
    private AccountingDocument accountingDocument;
    private PurchaseAccountingDocument purchaseAccountingDocument;
    private AccountsBo accountsBo;
    private PNR pnr;
    //Reporting fields
    private Date from;
    private Date to;
    private List<AccountingDocument> acDocsRpt;
    private MasterAgent mAgent;
    private Agent agent;
    private Customer customer;
    private String acDocType;
    private boolean officeCopy;
    private int totalInvoice = 0;
    private int totalCNote = 0;    
    private BigDecimal totalInvAmount = new BigDecimal("0.00");
    private BigDecimal totalReceived = new BigDecimal("0.00");
    private BigDecimal totalCredit = new BigDecimal("0.00");
    private BigDecimal totalPaid = new BigDecimal("0.00");
    private BigDecimal totalOutstanding = new BigDecimal("0.00");
    private BigDecimal invBalance = new BigDecimal("0.00");
    private BigDecimal transBalance = new BigDecimal("0.00");
    private BigDecimal finalBalance = new BigDecimal("0.00");
    private int noOfInvoice = 0;
    
    
    public AccountingDocBo() {
        
    }

    public void saveAcDoc() {
        getAcDocDao().saveAcDoc(getAccountingDocument());
    }

    public void saveAllAcDoc(List<AccountingDocument> acDocs) {
        getAcDocDao().saveAllAcDocs(acDocs);
    }
        
    public void savePurchaseAcDoc() {
        getAcDocDao().savePurchaseAcDoc(getPurchaseAccountingDocument());
    }

    public void deleteAcDoc(AccountingDocument acDoc) {
        getAcDocDao().deleteAcDoc(acDoc);
    }
    
    public void deleteService(Services s) {
        getAcDocDao().deleteService(s);
    }
    
    public void setAcDocStatusFalse(final long acDocId) {
        getAcDocDao().setAcDocStatusFalse(acDocId);
    }

    public void findAcDocById(long id) {
        this.setAccountingDocument(getAcDocDao().findAcDocById(id));
    }

    public void findCNoteByIdWithInvoice(long id){
     this.setAccountingDocument(getAcDocDao().findCNoteByIdWithInvoice(id));
    }
    
    public void findPCNoteByIdWithInvoice(long id){
     this.setPurchaseAccountingDocument(getAcDocDao().findPCNoteByIdWithInvoice(id));
    }
        
    public AccountingDocument findAcDocByTkt(long tktId) {
        return getAcDocDao().findAcDocByTkt(tktId);
    }

    public void findCompleteAcDocWithRelatedDocsById(long id) {
        this.setAcDoc(getAcDocDao().findCompleteAcDocWithRelatedDocsById(id));
    }

    public void findCompletePAcDocWithRelatedDocsById(long id) {
        this.setPurchaseAccountingDocument(getAcDocDao().findCompletePAcDocWithRelatedDocsById(id));
    }
    
    public AccountingDocument findCompleteAcDocWithRelatedDocsByRef(int refNo) {
        return getAcDocDao().findCompleteAcDocWithRelatedDocsByRef(refNo);
    }

    public void findPurchaseAcDocById(long id) {
        this.setPurchaseAccountingDocument(getAcDocDao().findPurchaseAcDocById(id));
    }

    public AccountingDocument loadInvoice(long pnrID) {
        return getAcDocDao().findInvoice(pnrID);
    }

    public List<PNR> findPnrByAcDocRef(int acDocRef) {
        return getAcDocDao().findAcDocByRefNoReturnPnr(acDocRef);
    }

    public List<AccountingDocument> findAcDocByGdsPnr(String gdsPnr) {
        return getAcDocDao().findAcDocByGDSPnr(gdsPnr);
    }

    public List<PurchaseAccountingDocument> findPAcDocByGdsPnr(String gdsPnr) {
        return getAcDocDao().findPAcDocByGDSPnr(gdsPnr);
    }

    public List<AccountingDocument> findAcDocByRefNo(int refNo) {
        return getAcDocDao().findAcDocByRefNo(refNo);
    }

    public List<AccountingDocument> findCompleteAcDocByRefNo(int refNo) {
        return getAcDocDao().findCompleteAcDocByRefNo(refNo);
    }

    public List<PurchaseAccountingDocument> findCompletePAcDocVByRefNo(String refNo) {
        return getAcDocDao().findCompletePAcDocByVRefNo(refNo);
    }

    public List<AccountingDocument> findAcDocByPnrId(long pnrId) {
        return getAcDocDao().findAcDocByPnrId(pnrId);
    }
    
    public List<AccountingDocument> findAcDocByPnrIdWithPurchaseInfo(long pnrId) {
        return getAcDocDao().findAcDocByPnrIdWithPurchaseInfo(pnrId);
    }
    

    public List<PurchaseAccountingDocument> findPurchaseAcDocByPnrId(long pnrId) {
        return getAcDocDao().findPurchaseAcDocByPnrId(pnrId);
    }

    public AccountingDocument newSAcDoc() {
        this.setAccountingDocument(new AccountingDocument());
        this.getAccountingDocument().setPnr(new PNR());
        this.getAccountingDocument().setAcDocIssuedBy(new User());
        //this.accountingDocument.setAcDocModifiedBy(new User());
        this.getAccountingDocument().setAccountingDocumentLines(new LinkedHashSet());
        this.getAccountingDocument().setAcTransactions(new LinkedHashSet());
        this.getAccountingDocument().getAccountingDocumentLines().clear();
        this.getAccountingDocument().getAcTransactions().clear();
        return this.getAccountingDocument();
    }

    public PurchaseAccountingDocument newPAcDoc() {
        this.setPurchaseAccountingDocument(new PurchaseAccountingDocument());
        this.getPurchaseAccountingDocument().setPnr(new PNR());
        this.getPurchaseAccountingDocument().setAcDocIssuedBy(AuthenticationBo.getLoggedOnUser());
        //this.accountingDocument.setAcDocModifiedBy(new User());
        //this.getPurchaseAccountingDocument().setPurchaseAcDocLines(new LinkedHashSet());
        //this.getPurchaseAccountingDocument().setAcTransactions(new LinkedHashSet());
        //this.getPurchaseAccountingDocument().getAccountingDocumentLines().clear();
        //this.getPurchaseAccountingDocument().getAcTransactions().clear();
        return this.getPurchaseAccountingDocument();
    }
        
    public List<AccountingDocumentLine> loadPnrProduct(long acDocId) {
        return getAcDocDao().loadPnrProduct(acDocId);
    }

    public List<PurchaseAccountingDocument> outstandingPurchaseInvoice() {
        List<PurchaseAccountingDocument> acDocs = new ArrayList();
        acDocs = getAcDocDao().findOutstandingPInv();
        return acDocs;
    }

    public List<AccountingDocument> outstandingCNote(Long contactableId, int contactableType, Date from, Date to) {
        List<AccountingDocument> acDocs = new ArrayList();
        acDocs = getAcDocDao().findOutstandingCNoteByCriteria(contactableId, contactableType, from, to);
        return acDocs;
    }

    public List<PurchaseAccountingDocument> outstandingPCNote(Long contactableId, Date from, Date to) {
        List<PurchaseAccountingDocument> acDocs = new ArrayList();
        acDocs = getAcDocDao().findOutstandingPCNoteByCriteria(contactableId, from, to);
        return acDocs;
    }
  
    public List<AccountingDocument> outstandingInvForCollection(long contactableId, int contactableType, Date from, Date to) {
        List<AccountingDocument> acDocs = new ArrayList();
        acDocs = getAcDocDao().findOutstandingInvForCollection(contactableId, contactableType, from, to);
        return acDocs;
    }
    
    public List<AccountingDocument> outstandingUnBalancedInvForCollection(long contactableId, int contactableType, Date from, Date to) {
        List<AccountingDocument> acDocs = new ArrayList();
        acDocs = getAcDocDao().findOutstandingUnBalancedInvForCollection(contactableId, contactableType, from, to);
        return acDocs;
    }

    public List<AccountingDocument> sAcDocHistoryByCriteria(Long contactableId,
            int contactableType, Integer docTypeFrom, Integer docTypeTo, Date from, Date to,Long tktingAgtFrom,Long tktingAgtTo) {
        List<AccountingDocument> acDocs = new ArrayList();
        acDocs = getAcDocDao().invHistoryByCriteria(contactableId, contactableType, docTypeFrom,docTypeTo, from, to,tktingAgtFrom,tktingAgtTo);
        return acDocs;
    }

    public List<PurchaseAccountingDocument> pAcDocHistoryByCriteria(Long contactableId, Integer docType, Date from, Date to) {
        List<PurchaseAccountingDocument> acDocs = new ArrayList();
        acDocs = getAcDocDao().pAcDocHistoryByCriteria(contactableId, docType, from, to);
        return acDocs;
    }

    public List<AccountingDocument> outstandingInvByCriteria(Long contactableId, int type, Date from, Date to) {
        List<AccountingDocument> acDocs = new ArrayList();
        acDocs = getAcDocDao().findOutstandingInvoiceByCriteria(contactableId, type, from, to);
        return acDocs;
    }

    public List<PurchaseAccountingDocument> outstandingPInvByCriteria(Long contactableId, Date from, Date to) {
        List<PurchaseAccountingDocument> acDocs = new ArrayList();
        acDocs = getAcDocDao().findOutstandingPInvByCriteria(contactableId, from, to);
        return acDocs;
    }

    public List<PurchaseAccountingDocument> outstandingPCNoteByCriteria(Long contactableId, Date from, Date to) {
        List<PurchaseAccountingDocument> acDocs = new ArrayList();
        acDocs = getAcDocDao().findOutstandingPCNoteByCriteria(contactableId, from, to);
        return acDocs;
    }

    public List<PurchaseAccountingDocument> outstandingPInvForBspBilling(Date from, Date to) {
        List<PurchaseAccountingDocument> acDocs = new ArrayList();
        acDocs = getAcDocDao().findOutstandingPInvForBspBilling(from, to);
        return acDocs;
    }

    public List outstandingAgents() {
        List outstandingAgents = new ArrayList();
        outstandingAgents = getAcDocDao().findOutstandingAgents();
        return outstandingAgents;
    }

    public List outstandingCustomers() {
        List outstandingCustomers = new ArrayList();
        outstandingCustomers = getAcDocDao().findOutstandingCustomers();
        return outstandingCustomers;
    }

    public List outstandingVendors() {
        List outstandingVendors = new ArrayList();
        outstandingVendors = getAcDocDao().findOutstandingThirdPartyVendors();
        return outstandingVendors;
    }

    public List outstandingCNoteVendors() {
        List outstandingVendors = new ArrayList();
        outstandingVendors = getAcDocDao().findOutstandingCNoteThirdPartyVendors();
        return outstandingVendors;
    }

    public List outstandingRefundAgents() {
        List outstandingAgents = new ArrayList();
        outstandingAgents = getAcDocDao().findOutstandingRefundAgents();
        return outstandingAgents;
    }

    public List outstandingRefundCustomers() {
        List outstandingCustomers = new ArrayList();
        outstandingCustomers = getAcDocDao().findOutstandingRefundCustomers();
        return outstandingCustomers;
    }

    public Integer generateAcDocRef() {

        int lastInvRef = 0, lastInvPrefix = 0, currentYear = 0;
        String finalAcDocRef = "";

        SimpleDateFormat dfYear = new SimpleDateFormat("yy");
        Calendar cal = Calendar.getInstance();
        String year = dfYear.format(cal.getTime());
        currentYear = Integer.valueOf(year);

        lastInvRef = getAcDocDao().getMaxAcDocRef();
        lastInvPrefix = lastInvPrefix = lastInvRef / 1000000;

        if (lastInvRef == 0 || lastInvPrefix != currentYear) {

            finalAcDocRef = year + "000001";//Starting invref from YY000001    

        } else {

            finalAcDocRef = String.valueOf(++lastInvRef);
        }
        return Integer.valueOf(finalAcDocRef);
    }

    public AccountingDocument invoice(PNR pnr) {
        try {
            this.getAccountingDocument().getClass().newInstance();
            //  this.accountingDocument.getTickets().clear();
            this.getAccountingDocument().getAccountingDocumentLines().clear();
        } catch (InstantiationException ex) {
            Logger.getLogger(AccountingDocBo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(AccountingDocBo.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (pnr.getAccountingDocuments().size() > 0) {
            for (AccountingDocument acDoc : pnr.getAccountingDocuments()) {
                if (acDoc.getAcDoctype() == 1) {
                    this.setAccountingDocument(acDoc);


                    this.getAccountingDocument().setPnr(pnr);
                    //this.accountingDocument.setPnrProducts(this.acDocDao.loadPnrProduct(acDoc.getAcDocId()));
                }
            }
            List<Ticket> invoicedTickets = new ArrayList();
            for (Ticket ticket : pnr.getTickets()) {
                /*    if (ticket.getAccountingDocument().getAcDoctype() == 1) {
                invoicedTickets.add(ticket);
                }*/
            }
            //this.accountingDocument.setTickets(invoicedTickets);
        }
        return this.getAccountingDocument();
    }

    public PurchaseAccountingDocument newPurchaseAcDoc(PNR pnr) {

        PurchaseAccountingDocument newPAcDoc = new PurchaseAccountingDocument();
        PurchaseAccountingDocument pInvoice = new PurchaseAccountingDocument();
        PurchaseAccountingDocumentLine purchaseAcDocLine = new PurchaseAccountingDocumentLine();
        BigDecimal totalPurchaseCost = new BigDecimal("0.00");

        int noInvoice = 0;
        if (pnr.getPurchaseAccountingDocuments().size() > 0) {
            for (PurchaseAccountingDocument pAcDoc : pnr.getPurchaseAccountingDocuments()) {
                if (pAcDoc.getAcDoctype() == 1) {
                    noInvoice++;
                }
            }
        }

        String acDocRef = pnr.getGdsPNR().concat(String.valueOf(pnr.getPnrId()).concat(String.valueOf(++noInvoice)));
        //Create new document
        newPAcDoc.setAcDoctype(1);
        newPAcDoc.setRecipientRef(acDocRef);
        newPAcDoc.setIssueDate(new java.util.Date());
        newPAcDoc.setPnr(pnr);

        for (Ticket t : pnr.getTickets()) {
            if (t.getTktStatus() != 1 && t.getPurchaseAccountingDocumentLine().isEmpty()) {
                totalPurchaseCost = totalPurchaseCost.add(t.getNetBillable());
                newPAcDoc.setIssueDate(t.getDocIssuedate());
                if (t.getTktStatus() == 4) {
                    pInvoice = pnr.getPurchaseAccountingDocuments().iterator().next();
                    newPAcDoc.setAcDoctype(2);
                    newPAcDoc.setRecipientRef(pnr.getPurchaseAccountingDocuments().iterator().next().getRecipientRef());
                    newPAcDoc.setPurchaseAccountingDocument(pInvoice);//Linking cnote with invocie
                }
                if (!t.getAccountingDocumentLine().isEmpty()) {
                    newPAcDoc.addaccountingDocument(t.getAccountingDocumentLine().iterator().next().getAccountingDocument());
                }
                t.addPAcDocLine(purchaseAcDocLine);
                purchaseAcDocLine.addTicket(t);
            }
        }
        purchaseAcDocLine.setType(1);
        purchaseAcDocLine.setPurchaseAccountingDocument(newPAcDoc);
        newPAcDoc.getPurchaseAcDocLines().add(purchaseAcDocLine);
         newPAcDoc.addAcStatement(getAccountsBo().newAccountsTransactionFromPAcDoc(newPAcDoc));
        if (newPAcDoc.getTickets().size() > 0) {
            return newPAcDoc;
        } else {
            return null;
        }
    }

    public void removeLine(AccountingDocumentLine l) {
        this.getAcDocDao().removeLine(l);
    }

    public void reportSummery() {//For outstanding documents
        totalInvAmount = new BigDecimal("0.00");
        totalCredit = new BigDecimal("0.00");
        totalReceived = new BigDecimal("0.00");        
        totalPaid = new BigDecimal("0.00");
        //totalOutstanding = new BigDecimal("0.00");
        invBalance = new BigDecimal("0.00");
        setTransBalance(new BigDecimal("0.00"));
        finalBalance = new BigDecimal("0.00");
        
        for (AccountingDocument a : this.getAcDocsRpt()) {
            this.setTotalInvAmount(this.getTotalInvAmount().add(a.getTotalDocumentedAmount()));
            this.setTotalReceived(this.getTotalReceived().add(a.getTotalTransactionAmount()));
            for (AccountingDocument creditNote : a.getRelatedDocuments()) {
                this.setTotalCredit(this.getTotalCredit().add(creditNote.getTotalDocumentedAmount()));
                this.setTotalReceived(this.getTotalReceived().add(creditNote.getTotalTransactionAmount()));
            }
        }
        this.setTotalOutstanding(this.getTotalInvAmount().add(this.getTotalCredit()).subtract(this.getTotalReceived()));
        this.setNoOfInvoice(this.getAcDocsRpt().size());

    }

    public void reportSummeryAcDocHistory() {// for history
        totalInvAmount = new BigDecimal("0.00");
        totalCredit = new BigDecimal("0.00");
        totalReceived = new BigDecimal("0.00");        
        totalPaid = new BigDecimal("0.00");
        //totalOutstanding = new BigDecimal("0.00");
        invBalance = new BigDecimal("0.00");
        setTransBalance(new BigDecimal("0.00"));
        finalBalance = new BigDecimal("0.00");

        for (AccountingDocument a : this.getAcDocsRpt()) {

            if (a.getAcDoctype() == 1) {
                this.setTotalInvoice(this.getTotalInvoice() + 1);
                this.setTotalInvAmount(this.getTotalInvAmount().add(a.getTotalDocumentedAmount()));
                this.setTotalReceived(this.getTotalReceived().add(a.getTotalTransactionAmount()));
            } else if (a.getAcDoctype() == 2 || a.getAcDoctype() == 3) {
                this.setTotalCNote(this.getTotalCNote() + 1);
                this.setTotalCredit(this.getTotalCredit().add(a.getTotalDocumentedAmount()));
                this.setTotalPaid(this.getTotalPaid().add(a.getTotalTransactionAmount()));
            }
        }

        this.setInvBalance(this.getTotalInvAmount().add(this.getTotalCredit()));
        this.setTransBalance(this.getTotalReceived().add(this.getTotalPaid()));
        this.setFinalBalance(this.getInvBalance().subtract(this.getTransBalance()));
    }
    //*****************Reporting************************    

    //*************************************************
    //**********Getters and Setters*******************
    public void setAcDocDao(AcDocDao acDocDao) {
        this.acDocDao = acDocDao;
    }

    public AccountingDocument getAccountingDocument() {
        return accountingDocument;
    }

    public void setAccountingDocument(AccountingDocument acDoc) {
        this.accountingDocument = acDoc;
    }

    public void setPnr(PNR pnr) {
        this.pnr = pnr;
    }

    public void setAcDoc(AccountingDocument accountingDocument) {
        this.setAccountingDocument(accountingDocument);
    }

    public PurchaseAccountingDocument getPurchaseAccountingDocument() {
        return purchaseAccountingDocument;
    }

    public void setPurchaseAccountingDocument(PurchaseAccountingDocument purchaseAccountingDocument) {
        this.purchaseAccountingDocument = purchaseAccountingDocument;
    }

    public List<AccountingDocument> getAcDocsRpt() {
        return acDocsRpt;
    }

    public void setAcDocsRpt(List<AccountingDocument> acDocsRpt) {
        this.acDocsRpt = acDocsRpt;
    }

    public MasterAgent getmAgent() {
        return mAgent;
    }

    public void setmAgent(MasterAgent mAgent) {
        this.mAgent = mAgent;
    }

    public String getAcDocType() {
        return acDocType;
    }

    public void setAcDocType(String acDocType) {
        this.acDocType = acDocType;
    }

    public BigDecimal getTotalInvAmount() {
        return totalInvAmount;
    }

    public BigDecimal getTotalCredit() {
        return totalCredit;
    }

    public BigDecimal getTotalReceived() {
        return totalReceived;
    }

    public BigDecimal getTotalOutstanding() {
        return totalOutstanding;
    }

    public int getNoOfInvoice() {
        return noOfInvoice;
    }

    public boolean isOfficeCopy() {
        return officeCopy;
    }

    public void setOfficeCopy(boolean officeCopy) {
        this.officeCopy = officeCopy;
    }

    public AcDocDao getAcDocDao() {
        return acDocDao;
    }

    public PNR getPnr() {
        return pnr;
    }

    public int getTotalInvoice() {
        return totalInvoice;
    }

    public void setTotalInvoice(int totalInvoice) {
        this.totalInvoice = totalInvoice;
    }

    public int getTotalCNote() {
        return totalCNote;
    }

    public void setTotalCNote(int totalCNote) {
        this.totalCNote = totalCNote;
    }

    public void setTotalInvAmount(BigDecimal totalInvAmount) {
        this.totalInvAmount = totalInvAmount;
    }

    public void setTotalReceived(BigDecimal totalReceived) {
        this.totalReceived = totalReceived;
    }

    public void setTotalCredit(BigDecimal totalCredit) {
        this.totalCredit = totalCredit;
    }

    public BigDecimal getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(BigDecimal totalPaid) {
        this.totalPaid = totalPaid;
    }

    public void setTotalOutstanding(BigDecimal totalOutstanding) {
        this.totalOutstanding = totalOutstanding;
    }

    public BigDecimal getInvBalance() {
        return invBalance;
    }

    public void setInvBalance(BigDecimal invBalance) {
        this.invBalance = invBalance;
    }    

    public BigDecimal getFinalBalance() {
        return finalBalance;
    }

    public void setFinalBalance(BigDecimal finalBalance) {
        this.finalBalance = finalBalance;
    }

    public void setNoOfInvoice(int noOfInvoice) {
        this.noOfInvoice = noOfInvoice;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public BigDecimal getTransBalance() {
        return transBalance;
    }

    public void setTransBalance(BigDecimal transBalance) {
        this.transBalance = transBalance;
    }

    public AccountsBo getAccountsBo() {
        return accountsBo;
    }

    public void setAccountsBo(AccountsBo accountsBo) {
        this.accountsBo = accountsBo;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

}
