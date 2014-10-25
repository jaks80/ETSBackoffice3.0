package etsbackoffice.datalogic;

import etsbackoffice.domain.AccountingDocument;
import etsbackoffice.domain.PNR;
import etsbackoffice.domain.AccountingDocumentLine;
import etsbackoffice.domain.Agent;
import etsbackoffice.domain.Customer;
import etsbackoffice.domain.PurchaseAccountingDocument;
import etsbackoffice.domain.Services;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public interface AcDocDao {
    
    public void saveAcDoc(AccountingDocument acDoc);
    
    public void saveAllAcDocs(List<AccountingDocument> acDocs);

    public void savePurchaseAcDoc(PurchaseAccountingDocument acDoc);

    public void deleteAcDoc(AccountingDocument acDoc);
    
    public void deleteService(Services s);
    
    public void setAcDocStatusFalse(long acDocId);

    public void deletePAcDoc(PurchaseAccountingDocument acDoc);
    
    public AccountingDocument findCNoteByIdWithInvoice(long id);
    
    public List<AccountingDocument> findAcDocByPnrIdWithPurchaseInfo(long pnrId);
    
    public PurchaseAccountingDocument findPCNoteByIdWithInvoice(long id);
            
    public AccountingDocument findAcDocById(long id);
     
    public AccountingDocument findAcDocByTkt(long tktId);

    public AccountingDocument findCompleteAcDocWithRelatedDocsById(long id);
    
    public PurchaseAccountingDocument findCompletePAcDocWithRelatedDocsById(long id);

    public AccountingDocument findCompleteAcDocWithRelatedDocsByRef(int refNo);

    public PurchaseAccountingDocument findPurchaseAcDocById(long id);
    
    public List<AccountingDocument> findAcDocByRefNo(int refNo);

    public List<AccountingDocument> findCompleteAcDocByRefNo(int refNo);

    public List<PurchaseAccountingDocument> findCompletePAcDocByVRefNo(String refNo);

    public List<AccountingDocument> findAcDocByGDSPnr(String pnr);

    public List<PurchaseAccountingDocument> findPAcDocByGDSPnr(String pnr);

    public List<PNR> findAcDocByRefNoReturnPnr(int acDocRef);
    
    public List<AccountingDocument> findAcDocByPnrId(long pnrId);

    public List<PurchaseAccountingDocument> findPurchaseAcDocByPnrId(long pnrId);
    
    public AccountingDocument findInvoice(long pnrId);
    
    public int getMaxAcDocRef();
    
    public List<AccountingDocumentLine> loadPnrProduct(long acDocId);

    //public List<AccountingDocument> findOutstandingInvoice();

    public List<PurchaseAccountingDocument> findOutstandingPInv();

    public List<AccountingDocument> findOutstandingInvForCollection(long contactableId, int contactableType, Date from, Date to);
    
    public List<AccountingDocument> findOutstandingUnBalancedInvForCollection(long contactableId, int contactableType, Date from, Date to);

    public List<AccountingDocument> findOutstandingInvoiceByCriteria(Long contactableId,int type, Date from, Date to);

    public List<AccountingDocument> findOutstandingCNoteByCriteria(Long contactableId, int contactableType, Date from, Date to);

    public List<AccountingDocument> invHistoryByCriteria(Long contactableId,
            int contactableType, Integer docTypeFrom, Integer docTypeTo, Date from, Date to,Long tktingAgtFrom,Long tktingAgtTo);

    public List<PurchaseAccountingDocument> pAcDocHistoryByCriteria(Long contactableId,Integer docType, Date from, Date to);

    public List<PurchaseAccountingDocument> findOutstandingPInvByCriteria(Long contactableId, Date from, Date to);
    
    public List<PurchaseAccountingDocument> findOutstandingPCNoteByCriteria(Long contactableId, Date from, Date to);

    public List<PurchaseAccountingDocument> findOutstandingPInvForBspBilling(Date from, Date to);

    public List<Agent> findOutstandingAgents();
    
    public List<Customer> findOutstandingCustomers();

    public List<Agent> findOutstandingRefundAgents();

    public List<Customer> findOutstandingRefundCustomers();

    public List<AccountingDocument> findOutstandingRefund();

    public List<Agent> findOutstandingThirdPartyVendors();
    
    public List<Agent> findOutstandingCNoteThirdPartyVendors();

    public void removeLine(AccountingDocumentLine l);
    
    public BigDecimal finalAcBalancetoDate(Date to,Long agtId,Long custId);
}
