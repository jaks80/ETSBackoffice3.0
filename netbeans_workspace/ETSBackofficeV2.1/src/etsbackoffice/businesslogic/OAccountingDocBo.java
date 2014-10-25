package etsbackoffice.businesslogic;

import etsbackoffice.datalogic.OAcDocDao;
import etsbackoffice.domain.Agent;
import etsbackoffice.domain.Customer;
import etsbackoffice.domain.MasterAgent;
import etsbackoffice.domain.OAccountingDocument;
import etsbackoffice.domain.OAccountingDocumentLine;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Yusuf
 */

public class OAccountingDocBo {

    private OAcDocDao oAcDocDao;
    private OAccountingDocument oAcDoc;
    private Date from;
    private Date to;
    private String client;
    //for reporting
    private Agent agent;
    private Customer customer;
    private boolean officeCopy;
    private MasterAgent mAgent;
    private String acDocType;
    private List<OAccountingDocument> acDocsRpt;    
    
    private int noOfInvoice = 0;    
    private BigDecimal totalInvAmount = new BigDecimal("0.00");
    private BigDecimal totalCredit = new BigDecimal("0.00");
    private BigDecimal totalReceived = new BigDecimal("0.00");
    private BigDecimal totalOutstanding = new BigDecimal("0.00");
    public OAccountingDocBo(){
    }

    public void saveOrUpdateOAcDoc() {
        getoAcDocDao().saveOrUpdateAcDoc(this.getoAcDoc());
    }

    public void deleteOAcDoc(OAccountingDocument oAcDoc){
     getoAcDocDao().deleteOAcDoc(oAcDoc);
    }
    
    public void deleteOAcDocLine(OAccountingDocumentLine l) {
        getoAcDocDao().deleteOAcDocLine(l);
    }
    
    public List<OAccountingDocument> findAcDocByRef(int acDocRef) {
        return getoAcDocDao().findAcDocByRef(acDocRef);
    }

    public List<OAccountingDocument> findAcDocByCustomerName(String surName, String foreName) {
        return getoAcDocDao().findAcDocByCustomerName(surName, foreName);
    }

    public OAccountingDocument findCompleteAcDocById(long acDocId) {
        return getoAcDocDao().findCompleteAcDocById(acDocId);
    }

   public OAccountingDocument findCompleteAcDocByByRef(int refNo) {
        return getoAcDocDao().findCompleteAcDocByRef(refNo);
    }

   public List<OAccountingDocument> sOAcDocHistoryByCriteria(int contType, Long contId,Integer docType, Date from, Date to) {
        List<OAccountingDocument> acDocs = new ArrayList();
        acDocs = oAcDocDao.invHistoryByCriteria(contType, contId,docType, from, to);
        return acDocs;
    }

   public List<OAccountingDocument> sOOutstandingInvByCriteria(int contType, Long contId,Date from, Date to) {
        List<OAccountingDocument> acDocs = new ArrayList();
        acDocs = oAcDocDao.invOutstandingByCriteria(contType, contId,from, to);
        return acDocs;
    }

    public List outstandingAgents() {
        List outstandingAgents = new ArrayList();
        outstandingAgents = oAcDocDao.findOutstandingAgents();
        return outstandingAgents;
    }

    public List outstandingCustomers() {
        List outstandingCustomers = new ArrayList();
        outstandingCustomers = oAcDocDao.findOutstandingCustomers();
        return outstandingCustomers;
    }



    public Integer generateAcDocRef() {

        int lastInvRef = 0, lastInvPrefix = 0, currentYear = 0;
        String finalAcDocRef = "";

        SimpleDateFormat dfYear = new SimpleDateFormat("yy");
        Calendar cal = Calendar.getInstance();
        String year = dfYear.format(cal.getTime());
        currentYear = Integer.valueOf(year);

        lastInvRef = getoAcDocDao().getMaxAcDocRef();
        lastInvPrefix = lastInvPrefix = lastInvRef / 1000000;

        if (lastInvRef == 0 || lastInvPrefix != currentYear) {

            finalAcDocRef = year + "000001";//Starting invref from YY000001

        } else {

            finalAcDocRef = String.valueOf(++lastInvRef);
        }
        return Integer.valueOf(finalAcDocRef);
    }    

    public OAcDocDao getoAcDocDao() {
        return oAcDocDao;
    }

    public void setoAcDocDao(OAcDocDao oAcDocDao) {
        this.oAcDocDao = oAcDocDao;
    }

    public OAccountingDocument getoAcDoc() {
        return oAcDoc;
    }

    public void setoAcDoc(OAccountingDocument oAcDoc) {
        this.oAcDoc = oAcDoc;
    }

    public boolean isOfficeCopy() {
        return officeCopy;
    }

    public void setOfficeCopy(boolean officeCopy) {
        this.officeCopy = officeCopy;
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
    //*************************************************

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
  
    public int getNoOfInvoice() {
        return noOfInvoice;
    }

    public void setNoOfInvoice(int noOfInvoice) {
        this.noOfInvoice = noOfInvoice;
    }

    public BigDecimal getTotalInvAmount() {
        return totalInvAmount;
    }

    public void setTotalInvAmount(BigDecimal totalInvAmount) {
        this.totalInvAmount = totalInvAmount;
    }

    public BigDecimal getTotalCredit() {
        return totalCredit;
    }

    public void setTotalCredit(BigDecimal totalCredit) {
        this.totalCredit = totalCredit;
    }

    public BigDecimal getTotalOutstanding() {
        return totalOutstanding;
    }

    public void setTotalOutstanding(BigDecimal totalOutstanding) {
        this.totalOutstanding = totalOutstanding;
    }

    public BigDecimal getTotalReceived() {
        return totalReceived;
    }

    public void setTotalReceived(BigDecimal totalReceived) {
        this.totalReceived = totalReceived;
    }

    public List<OAccountingDocument> getAcDocsRpt() {
        return acDocsRpt;
    }

    public void setAcDocsRpt(List<OAccountingDocument> acDocsRpt) {
        this.acDocsRpt = acDocsRpt;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
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
