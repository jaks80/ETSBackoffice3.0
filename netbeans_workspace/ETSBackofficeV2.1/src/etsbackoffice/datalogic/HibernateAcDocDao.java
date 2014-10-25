package etsbackoffice.datalogic;

import etsbackoffice.domain.AccountingDocument;
import etsbackoffice.domain.Agent;
import etsbackoffice.domain.Customer;
import etsbackoffice.domain.PNR;
import etsbackoffice.domain.AccountingDocumentLine;
import etsbackoffice.domain.PurchaseAccountingDocument;
import etsbackoffice.domain.Services;
import etsbackoffice.domain.Ticket;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;
/**
 *
 * @author Yusuf
 */
public class HibernateAcDocDao extends HibernateDaoSupport implements AcDocDao {

    @Transactional
    public void saveAcDoc(AccountingDocument acDoc) {
        getHibernateTemplate().saveOrUpdate(acDoc);
    }

     @Transactional
     public void saveAllAcDocs(List<AccountingDocument> acDocs){
      getHibernateTemplate().saveOrUpdateAll(acDocs);
     }
     
    @Transactional
    public void savePurchaseAcDoc(PurchaseAccountingDocument acDoc) {
        getHibernateTemplate().saveOrUpdate(acDoc);
    }

    @Transactional
    public void deleteAcDoc(AccountingDocument acDoc) {
        getHibernateTemplate().delete(acDoc);
    }

    @Transactional
    public void deleteService(Services s) {
        getHibernateTemplate().delete(s);
    }
        
    @Transactional
    public void setAcDocStatusFalse(long acDocId){                   
      List<AccountingDocumentLine> lines = new ArrayList();
      lines = getHibernateTemplate().find("from AccountingDocumentLine l where l.accountingDocument.acDocId = ?",acDocId);
      getHibernateTemplate().deleteAll(lines);
      AccountingDocument acDoc = getHibernateTemplate().load(AccountingDocument.class, acDocId);
      acDoc.setActive(false);
      getHibernateTemplate().saveOrUpdate(acDoc);
    }
    
    @Transactional
    public void deletePAcDoc(PurchaseAccountingDocument acDoc) {
        getHibernateTemplate().delete(acDoc);
    }

    @Transactional
    public void removeLine(AccountingDocumentLine l) {
        getHibernateTemplate().delete(l);
    }

    public AccountingDocument findCNoteByIdWithInvoice(long id) {
        AccountingDocument acDoc = new AccountingDocument();

        String hql = "select distinct a from AccountingDocument as a "
                + "left join fetch a.accountingDocumentLines as adl "
                + "left join fetch adl.services as s1 "
                + "left join fetch adl.tickets as t "
                + "left join fetch t.ticketRefundDetails as tr "
                + "left join fetch a.acTransactions  "
                + "left join fetch t.segments "
                + "left join fetch a.pnr as p "
                + "left join fetch p.servicingCareer "
                + "left join fetch a.pnr.agent "
                + "left join fetch a.pnr.customer "
                + "left join fetch a.accountingDocument as inv "
                + "left join fetch inv.accountingDocumentLines as invl "
                + "left join fetch invl.tickets as t1 "    
                + "left join fetch t1.ticketRefundDetails as tr1 "  
                + "left join fetch invl.services as s2 "
                + "left join fetch inv.acTransactions "
                + "left join fetch inv.relatedDocuments rd "
                + "left join fetch rd.accountingDocumentLines as rdl "
                + "left join fetch rdl.services as s3 "
                + "left join fetch rdl.tickets as t2 "
                + "left join fetch t2.ticketRefundDetails as tr2 "
                + "left join fetch rd.acTransactions "
                + "where a.acDocId = ? ";

        acDoc = (AccountingDocument) DataAccessUtils.uniqueResult(getHibernateTemplate().find(hql, id));
        acDoc.getAccountingDocument().getRelatedDocuments().remove(acDoc);
        return acDoc;
    }

    public AccountingDocument findAcDocById(long id) {
        AccountingDocument acDoc = new AccountingDocument();

        String hql = "select distinct a from AccountingDocument as a "
                + "left join fetch a.accountingDocumentLines as adl "                
                + "left join fetch adl.tickets as t "
                + "left join fetch adl.services as s "
                + "left join fetch a.acTransactions  "
                + "left join fetch t.segments "
                + "left join fetch a.pnr as p "
                + "left join fetch p.servicingCareer "
                + "left join fetch a.pnr.agent "
                + "left join fetch a.pnr.customer " 
                + "left join fetch a.accounts "
                + "where a.acDocId = ? ";

        acDoc = (AccountingDocument) DataAccessUtils.uniqueResult(getHibernateTemplate().find(hql, id));

        return acDoc;
    }
        
    public AccountingDocument findAcDocByTkt(long tktId){
        AccountingDocument acDoc = new AccountingDocument();

        String hql = "select distinct a from AccountingDocument as a "
                + "left join fetch a.accountingDocumentLines as adl "
                + "left join fetch adl.tickets as t "                
                + "where t.ticketId = ? ";

        acDoc = (AccountingDocument) DataAccessUtils.uniqueResult(getHibernateTemplate().find(hql, tktId));
        return acDoc;
    }
    
    public AccountingDocument findCompleteAcDocWithRelatedDocsById(long id) {
        AccountingDocument acDoc = new AccountingDocument();

        String hql = "select distinct a from AccountingDocument as a "
                + "left join fetch a.accountingDocumentLines as adl "
                + "left join fetch adl.services as as1 "
                //+ "left join fetch adl.otherService "//one to one eager
                + "left join fetch adl.tickets as t "
                + "left join fetch t.segments "
                + "left join fetch a.pnr as p "
                + "left join fetch p.servicingCareer "
                + "left join fetch a.pnr.agent "
                + "left join fetch a.pnr.customer "
                + "left join fetch a.acTransactions "
                + "left join fetch a.relatedDocuments as rd "
                + "left join fetch rd.accountingDocumentLines as rdl "
                + "left join fetch rd.acTransactions "
                + "left join fetch rdl.tickets as t2 "
                + "left join fetch rdl.services as2 "
                + "left join fetch t2.segments "
                + "where a.acDocId = ? ";

        acDoc = (AccountingDocument) DataAccessUtils.uniqueResult(getHibernateTemplate().find(hql, id));

        return acDoc;
    }

  public PurchaseAccountingDocument findCompletePAcDocWithRelatedDocsById(long id) {
        PurchaseAccountingDocument acDoc = new PurchaseAccountingDocument();

        String hql = "select distinct a from PurchaseAccountingDocument as a "
                + "left join fetch a.purchaseAcDocLines as adl "                
                + "left join fetch adl.tickets as t "
                + "left join fetch t.ticketRefundDetails as trfd "
                //+ "left join fetch t.segments "
                + "left join fetch a.pnr as p "
                + "left join fetch p.servicingCareer "
                + "left join fetch a.pnr.ticketingAgt "                
                + "left join fetch a.billingTransactions as bt "
                + "left join fetch a.relatedDocuments as rd "
                + "left join fetch rd.purchaseAcDocLines as rdl "
                + "left join fetch rd.billingTransactions as bt1 "
                + "left join fetch rdl.tickets as t2 "
                + "left join fetch t2.ticketRefundDetails as trfd2 "
                + "left join fetch t2.segments "
                + "where a.purchaseAcDocId = ? ";

        acDoc = (PurchaseAccountingDocument) DataAccessUtils.uniqueResult(getHibernateTemplate().find(hql, id));

        return acDoc;
    }
    public PurchaseAccountingDocument findPCNoteByIdWithInvoice(long id) {
        PurchaseAccountingDocument acDoc = new PurchaseAccountingDocument();

        String hql = "select distinct a from PurchaseAccountingDocument as a "
                + "left join fetch a.purchaseAcDocLines as adl "
                + "left join fetch adl.otherService "//one to one eager
                + "left join fetch adl.tickets as t "
                + "left join fetch t.ticketRefundDetails "
                + "left join fetch a.billingTransactions as bt "
                //+ "left join fetch t.segments "
                + "left join fetch a.pnr as p "
                + "left join fetch p.servicingCareer "
                + "left join fetch a.pnr.agent "
                + "left join fetch a.pnr.customer "
                + "left join fetch a.purchaseAccountingDocument as inv "
                + "left join fetch inv.purchaseAcDocLines as invl "
                + "left join fetch invl.tickets as t1 "                
                + "left join fetch inv.billingTransactions as bt1 "
                + "left join fetch inv.relatedDocuments rd "
                + "left join fetch rd.purchaseAcDocLines as rdl "
                + "left join fetch rdl.tickets as t2 "
                + "left join fetch rd.billingTransactions "
                + "where a.purchaseAcDocId = ? ";

        acDoc = (PurchaseAccountingDocument) DataAccessUtils.uniqueResult(getHibernateTemplate().find(hql, id));
        acDoc.getPurchaseAccountingDocument().getRelatedDocuments().remove(acDoc);
        return acDoc;
    }
       
    public AccountingDocument findCompleteAcDocWithRelatedDocsByRef(int refNo) {
        AccountingDocument acDoc = new AccountingDocument();

        String hql = "select distinct a from AccountingDocument as a "
                + "left join fetch a.accountingDocumentLines as adl "
                + "left join fetch adl.tickets as t "
                + "left join fetch adl.services as s1 "
                + "left join fetch t.segments "
                + "left join fetch a.pnr as p "
                + "left join fetch p.servicingCareer "
                + "left join fetch a.pnr.agent "
                + "left join fetch a.pnr.customer "
                + "left join fetch a.acTransactions "
                + "left join fetch a.relatedDocuments as rd "
                + "left join fetch rd.accountingDocumentLines as rdl "
                + "left join fetch rdl.services as s2 "
                + "left join fetch rd.acTransactions "
                + "left join fetch rdl.tickets as t2 "
                + "where a.acDocRef = ?  and a.acDoctype = 1 ";

        acDoc = (AccountingDocument) DataAccessUtils.uniqueResult(getHibernateTemplate().find(hql, refNo));

        return acDoc;
    }

    public PurchaseAccountingDocument findPurchaseAcDocById(long id) {
        PurchaseAccountingDocument acDoc = new PurchaseAccountingDocument();

        String hql = "from PurchaseAccountingDocument as a "
                + "left join fetch a.purchaseAcDocLines as adl "
                + "left join fetch adl.tickets as t "
                + "left join fetch t.ticketRefundDetails "
                + "left join fetch a.pnr as p "
                + "left join fetch p.servicingCareer "
                + "left join fetch p.ticketingAgt "
                + "left join fetch a.billingTransactions "
                + "where a.purchaseAcDocId = ?";

        return (PurchaseAccountingDocument) DataAccessUtils.uniqueResult(getHibernateTemplate().find(hql, id));
    }

    public List<AccountingDocument> findAcDocByRefNo(int refNo) {
        String hql = "select distinct a from AccountingDocument a "
                + "left join fetch a.accountingDocumentLines as adl "
                + "left join fetch adl.services as s1 "
                + "left join fetch adl.tickets as t "
                + "left join fetch a.acTransactions "
                + "left join fetch a.pnr "
                + "where a.acDocRef = ? group by a.acDocId";
        List<AccountingDocument> acDocs = getHibernateTemplate().find(hql, refNo);

        return acDocs;
    }

    public List<AccountingDocument> findCompleteAcDocByRefNo(int refNo) {
        String hql = "select distinct a from AccountingDocument a "
                + "left join fetch a.acTransactions "
                + "left join fetch a.accountingDocumentLines as adl "
                + "left join fetch adl.services as s1 "
                + "left join fetch adl.tickets as t "
                + "left join fetch a.pnr as p "
                //+ "left join fetch p.tickets as t "
               + "left join fetch p.agent "
                + "left join fetch p.customer "
                + "where a.acDocRef = ? "
                + " order by a.acDocId";
        List<AccountingDocument> acDocs = getHibernateTemplate().find(hql, refNo);

        return acDocs;
    }

    public List<PurchaseAccountingDocument> findCompletePAcDocByVRefNo(String refNo) {
        String hql = "select distinct acDoc from PurchaseAccountingDocument acDoc "
                + "left join fetch acDoc.billingTransactions "
                + "left join fetch acDoc.pnr as p "
                + "left join fetch p.tickets as t "
                + "left join fetch p.ticketingAgt "
                + "where acDoc.vendorRef = ? "
                + "group by acDoc.purchaseAcDocId";
        List<PurchaseAccountingDocument> acDocs = getHibernateTemplate().find(hql, refNo);

        return acDocs;
    }

    public List<AccountingDocument> findAcDocByGDSPnr(String pnr) {
        String hql = "select distinct a from AccountingDocument a "
                + "left join fetch a.accountingDocumentLines as al "
                + "left join fetch al.otherService "//one to one eager
                + "left join fetch al.tickets as t "
                + "left join fetch a.pnr as p "
                + "left join fetch p.servicingCareer as s "
                + "left join fetch a.acTransactions as act "
                + "left join fetch p.agent as agt "
                + "left join fetch p.customer as cust "
                + "left join fetch p.ticketingAgt as tAgt "
                + "where p.gdsPNR = ? "
                + " group by a.acDocId";
        List<AccountingDocument> acDocs = getHibernateTemplate().find(hql, pnr);
        return acDocs;
    }

    public List<PurchaseAccountingDocument> findPAcDocByGDSPnr(String pnr) {
        String hql = "select distinct acDoc from PurchaseAccountingDocument acDoc "
                + "left join fetch acDoc.billingTransactions "
                + "left join fetch acDoc.pnr as p "
                + "left join fetch p.tickets as t "
                + "left join fetch p.ticketingAgt as agt "
                + "where p.gdsPNR = ? and agt<>1 "
                + "group by acDoc.purchaseAcDocId";
        List<PurchaseAccountingDocument> acDocs = getHibernateTemplate().find(hql, pnr);

        return acDocs;
    }

    public List<AccountingDocument> findAcDocByPnrId(long pnrId) {

        String hql = "select distinct a from AccountingDocument as a "
                + "left join fetch a.accountingDocumentLines as adl "
                + "left join fetch adl.services "
                + "left join fetch adl.tickets as t "
                + "left join fetch t.segments "
                + "left join fetch a.pnr as p "
                + "left join fetch p.servicingCareer "
                + "left join fetch a.pnr.agent "
                + "left join fetch a.pnr.customer "
                + "left join fetch a.relatedDocuments " //One to One eager fatching
                + "left join fetch a.acTransactions "
                + "where a.pnr.pnrId = ? order by a.acDocId ";
        List<AccountingDocument> acDocs = getHibernateTemplate().find(hql, pnrId);
        return acDocs;
    }

        public List<AccountingDocument> findAcDocByPnrIdWithPurchaseInfo(long pnrId) {

        String hql = "select distinct a from AccountingDocument as a "
                + "left join fetch a.accountingDocumentLines as adl "
                + "left join fetch adl.services "
                + "left join fetch adl.tickets as t "
                + "left join fetch t.ticketRefundDetails "
                + "left join fetch t.segments "
                + "left join fetch a.pnr as p "
                + "left join fetch p.servicingCareer "
                + "left join fetch a.pnr.agent "
                + "left join fetch a.pnr.customer "
                + "left join fetch a.relatedDocuments " //One to One eager fatching
                + "left join fetch a.acTransactions "
                + "left join fetch a.purchaseAccountingDocuments pacdoc "
                + "left join fetch pacdoc.purchaseAcDocLines pl "
                + "left join fetch pacdoc.billingTransactions "
                + "where a.pnr.pnrId = ? order by a.acDocId ";
        List<AccountingDocument> acDocs = getHibernateTemplate().find(hql, pnrId);
        return acDocs;
    }
        
    public List<PurchaseAccountingDocument> findPurchaseAcDocByPnrId(long pnrId) {
                        
        String hql = "select distinct a from PurchaseAccountingDocument as a "
                + "left join fetch a.purchaseAcDocLines as adl "
                + "left join fetch adl.tickets as t "
                + "left join fetch t.ticketRefundDetails "
                + "left join fetch a.pnr as p "
                + "left join fetch p.servicingCareer "
                + "left join fetch p.ticketingAgt "
                + "left join fetch a.billingTransactions "
                + "left join fetch a.relatedDocuments as rd "
                + "left join fetch rd.purchaseAcDocLines as rdl "
                + "left join fetch rd.billingTransactions as bt1 "
                + "left join fetch rdl.tickets as t2 "
                + "where a.pnr.pnrId = ? order by a.purchaseAcDocId ";
        List<PurchaseAccountingDocument> acDocs = getHibernateTemplate().find(hql, pnrId);
        return acDocs;
    }

    public AccountingDocument findInvoice(long pnrId) {
        String hql = "from AccountingDocument acDoc where acDoc.pnr = ? and acDoc.acDoctype = 1";

        AccountingDocument acDoc = (AccountingDocument) DataAccessUtils.uniqueResult(getHibernateTemplate().find(hql, pnrId));

        return acDoc;
    }

    public int getMaxAcDocRef() {
        String hql = "select max(acDoc.acDocRef) from AccountingDocument acDoc";
        Object result = DataAccessUtils.uniqueResult(getHibernateTemplate().find(hql));
        return (result != null) ? Integer.valueOf(result.toString()) : 0;
    }

    public List<AccountingDocumentLine> loadPnrProduct(long acDocId) {
        return getHibernateTemplate().find("from PnrProduct pnrProduct where pnrProduct.accountingDocument.acDocId = ?", acDocId);
    }

    //This method has been used mainframe pnr searching
    public List<PNR> findAcDocByRefNoReturnPnr(int acDocRef) {
        String hql = "from AccountingDocument as a "
                + "left join fetch a.pnr as p "
                + "left join fetch p.servicingCareer "
                + "left join p.tickets as t "
                + "where a.acDocRef = ? "
                + "and t.passengerNo = (select min(t.passengerNo) from Ticket t where t.pnr.pnrId = p.pnrId) group by t.passengerNo";

        List results = getHibernateTemplate().find(hql, acDocRef);
        List<PNR> pnrs = new ArrayList();

        for (int i = 0; i < results.size(); i++) {
            Object[] objects = (Object[]) results.get(i);
            AccountingDocument acDoc = (AccountingDocument) objects[0];
            PNR pnr = acDoc.getPnr();
            Ticket ticket = (Ticket) objects[1];
            pnr.setLeadPax(ticket);
            pnrs.add(pnr);
        }
        return pnrs;
    }

    public List<AccountingDocument> findOutstandingInvoiceByCriteria(Long contactableId, int contactableType, Date from, Date to) {

        List<AccountingDocument> rptAcDocs = new ArrayList();
        String hql = "";

        if (contactableType == 1) {
            hql = "select distinct a "
                    + "from AccountingDocument as a "
                    + "left join fetch a.acDocIssuedBy "
                    + "left join fetch a.accountingDocumentLines as adl "                    
                    + "left join fetch adl.tickets as t "
                    + "left join fetch adl.services as s "
                    + "left join fetch a.pnr as p "
                    + "left join fetch p.servicingCareer as s "                   
                    + "inner join fetch p.agent as agt "
                    + "left join fetch a.acTransactions as act "
                    + "left join fetch a.relatedDocuments as cNotes "
                    + "left join fetch cNotes.accountingDocumentLines as rdl "
                    + "left join fetch rdl.tickets as t1 "
                     + "left join fetch rdl.services as s1 "
                    + "left join fetch cNotes.acTransactions "
                    + "where a.acDoctype = 1 and "
                    + "(select coalesce(sum(l.amount),0) + coalesce(sum(t.grossFare),0) + coalesce(sum(t.discount),0) + coalesce(sum(t.atolChg),0) "
                    + "+ coalesce(sum(s.serviceCharge),0) + coalesce(sum(s.discount),0) "
                    + "from AccountingDocument as b "
                    + "left join b.accountingDocumentLines as l "
                    + "left join l.tickets as t "
                    + "left join l.services as s "
                    + "where b.acDocRef = a.acDocRef) > "
                    + "(select coalesce(sum(act.transAmount),0) from AcTransaction act "
                    + "where act.invoice.acDocId = a.acDocId and act.active = true) "
                    + " and "
                    + "(:from is null or a.issueDate >= :from) and (:to is null or a.issueDate <= :to) "
                    + "and (:contactableId is null or agt.contactableId = :contactableId) "
                    + " order by a.acDocId ";

        } else if (contactableType == 2) {
            hql = "select distinct a "
                    + "from AccountingDocument as a "
                    + "left join fetch a.acDocIssuedBy "
                    + "left join fetch a.accountingDocumentLines as adl "
                    + "left join fetch a.acTransactions as act "                   
                    + "left join fetch adl.tickets as t "
                     + "left join fetch adl.services as s "
                    + "left join fetch a.relatedDocuments as cNotes "
                    + "left join fetch cNotes.accountingDocumentLines as rdl "
                    + "left join fetch rdl.tickets as t1 "
                     + "left join fetch rdl.services as s1 "
                    + "left join fetch cNotes.acTransactions "
                    + "left join fetch a.pnr as p "
                    + "left join fetch p.servicingCareer as s "                    
                    + "inner join fetch p.customer as cust "                                        
                    + "where a.acDoctype = 1 and "
                    + "(select coalesce(sum(l.amount),0) + coalesce(sum(t.grossFare),0) + coalesce(sum(t.discount),0) + coalesce(sum(t.atolChg),0) "
                    + "+ coalesce(sum(s.serviceCharge),0) + coalesce(sum(s.discount),0) "
                    + "from AccountingDocument as b "
                    + "left join b.accountingDocumentLines as l "
                    + "left join l.tickets as t "
                    + "left join l.services as s "
                    + "where b.acDocRef = a.acDocRef) > "
                    + "(select coalesce(sum(act.transAmount),0) from AcTransaction act "
                    + "where act.invoice.acDocId = a.acDocId and act.active = true) and "                   
                    + "(:from is null or a.issueDate >= :from) and (:to is null or a.issueDate <= :to) "
                    + "and (:contactableId is null or cust.contactableId = :contactableId) "
                    + " order by a.acDocId ";

        } else if (contactableType == 0) {
            hql = "select distinct a "
                    + "from AccountingDocument as a "
                    + "left join fetch a.acDocIssuedBy "
                    + "left join fetch a.accountingDocumentLines as adl "                    
                    + "left join fetch adl.tickets as t "
                    + "left join fetch adl.services as s "
                    + "left join fetch a.acTransactions as act "
                    + "left join fetch a.relatedDocuments as cNotes "
                    + "left join fetch cNotes.accountingDocumentLines as rdl "
                    + "left join fetch rdl.tickets as t1 "
                    + "left join fetch rdl.services as s1 "
                    + "left join fetch cNotes.acTransactions "
                    + "left join fetch a.pnr as p "
                    + "left join fetch p.agent as agt "
                    + "left join fetch p.customer as cust "
                    + "left join fetch p.servicingCareer as s "                                  
                    
                    + "where a.acDoctype = 1  and "
                    + "(select coalesce(sum(l.amount),0) + coalesce(sum(t.grossFare),0) + coalesce(sum(t.discount),0) + coalesce(sum(t.atolChg),0) "
                    + "+ coalesce(sum(s.serviceCharge),0) + coalesce(sum(s.discount),0) "
                    + "from AccountingDocument as b "
                    + "left join b.accountingDocumentLines as l "
                    + "left join l.tickets as t "
                    + "left join l.services as s "
                    + "where b.acDocRef = a.acDocRef) > "
                    + "(select coalesce(sum(act.transAmount),0) from AcTransaction act "
                    + "where act.invoice.acDocId = a.acDocId and act.active = true) and "
                    + "(:from is null or a.issueDate >= :from) and (:to is null or a.issueDate <= :to) "
                    + " order by a.acDocId ";
        }

        if (contactableType != 0) {
            rptAcDocs = getHibernateTemplate().findByNamedParam(hql, new String[]{"contactableId", "from", "to"},
                    new Object[]{contactableId, from, to});
        } else {
            rptAcDocs = getHibernateTemplate().findByNamedParam(hql, new String[]{"from", "to"},
                    new Object[]{from, to});
        }

        return rptAcDocs;
    }

    public List<AccountingDocument> findOutstandingCNoteByCriteria(Long contactableId, int contactableType, Date from, Date to) {

        List<AccountingDocument> rptAcDocs = new ArrayList();
        String hql = "";

        if (contactableType == 1) {
            hql = "select distinct a "
                    + "from AccountingDocument as a "
                    + "left join fetch a.acDocIssuedBy "
                    + "left join fetch a.accountingDocumentLines as adl "
                    + "left join fetch adl.services "
                    + "left join fetch adl.tickets as t "
                    + "left join fetch a.pnr as p "
                    + "left join fetch p.servicingCareer as s "
                    + "inner join fetch p.agent as agt "
                    + "left join fetch a.acTransactions as act "
                    + "left join fetch a.relatedDocuments as cNotes "
                    + "left join fetch cNotes.accountingDocumentLines as cLines "
                    + "left join fetch cLines.tickets as t1 "
                    + "left join fetch cNotes.acTransactions "
                    + "where "
                     + "(select coalesce(sum(l.amount),0) + coalesce(sum(t.grossFare),0) + coalesce(sum(t.discount),0) + coalesce(sum(t.atolChg),0) "
                    + "+ coalesce(sum(s.serviceCharge),0) + coalesce(sum(s.discount),0) "
                    + "from AccountingDocument as b "
                    + "left join b.accountingDocumentLines as l "
                    + "left join l.tickets as t "
                    + "left join l.services as s "
                    + "where b.acDocRef = a.acDocRef) < "
                    + "(select coalesce(sum(act.transAmount),0) from AcTransaction act where act.invoice.acDocId = a.acDocId) "
                    + " and "
                    + "(:from is null or a.issueDate >= :from) and (:to is null or a.issueDate <= :to) "
                    + "and (:contactableId is null or agt.contactableId = :contactableId) "
                    + " order by a.acDocId ";
        } else if (contactableType == 2) {
            hql = "select distinct a "
                    + "from AccountingDocument as a "
                    + "left join fetch a.accountingDocumentLines as adl "
                     + "left join fetch a.acDocIssuedBy "
                    + "left join fetch adl.services "
                    + "left join fetch adl.tickets as t "
                    + "left join fetch a.pnr as p "
                    + "left join fetch p.servicingCareer as s "
                    + "inner join fetch p.customer as cust "
                    + "left join fetch a.acTransactions as act "
                    + "left join fetch a.relatedDocuments as cNotes "
                    + "left join fetch cNotes.accountingDocumentLines as cLines "
                    + "left join fetch cLines.tickets as t1 "
                    + "left join fetch cNotes.acTransactions "
                    + "where "
                     + "(select coalesce(sum(l.amount),0) + coalesce(sum(t.grossFare),0) + coalesce(sum(t.discount),0) + coalesce(sum(t.atolChg),0) "
                    + "+ coalesce(sum(s.serviceCharge),0) + coalesce(sum(s.discount),0) "
                    + "from AccountingDocument as b "
                    + "left join b.accountingDocumentLines as l "
                    + "left join l.tickets as t "
                    + "left join l.services as s "
                    + "where b.acDocRef = a.acDocRef) < "
                    + "(select coalesce(sum(act.transAmount),0) from AcTransaction act where act.invoice.acDocId = a.acDocId) "
                    + " and "
                    + "(:from is null or a.issueDate >= :from) and (:to is null or a.issueDate <= :to) "
                    + "and (:contactableId is null or cust.contactableId = :contactableId) "
                    + " order by a.acDocId ";

        } else if (contactableType == 0) {
            System.out.println("Here>>>>>");
            hql = "select distinct a "
                    + "from AccountingDocument as a "
                    + "left join fetch a.accountingDocumentLines as adl "
                    + "left join fetch a.acDocIssuedBy "
                    + "left join fetch adl.services "
                    + "left join fetch adl.tickets as t "
                    + "left join fetch a.pnr as p "
                    + "left join fetch p.agent as agt "
                    + "left join fetch p.customer as cust "
                    + "left join fetch p.servicingCareer as s "
                    + "left join fetch a.acTransactions as act "
                    + "left join fetch a.relatedDocuments as cNotes "
                    + "left join fetch cNotes.accountingDocumentLines as cLines "
                    + "left join fetch cLines.tickets as t1 "
                    + "left join fetch cNotes.acTransactions "
                    + "where "
                     + "(select coalesce(sum(l.amount),0) + coalesce(sum(t.grossFare),0) + coalesce(sum(t.discount),0) + coalesce(sum(t.atolChg),0) "
                    + "+ coalesce(sum(s.serviceCharge),0) + coalesce(sum(s.discount),0) "
                    + "from AccountingDocument as b "
                    + "left join b.accountingDocumentLines as l "
                    + "left join l.tickets as t "
                    + "left join l.services as s "
                    + "where b.acDocRef = a.acDocRef) < "
                    + "(select coalesce(sum(act.transAmount),0) from AcTransaction act where act.invoice.acDocId = a.acDocId) "
                    + "and (:from is null or a.issueDate >= :from) and (:to is null or a.issueDate <= :to) "
                    + " order by a.acDocId ";
        }

        if (contactableType != 0) {
            rptAcDocs = getHibernateTemplate().findByNamedParam(hql, new String[]{"contactableId", "from", "to"},
                    new Object[]{contactableId, from, to});
        } else {
            rptAcDocs = getHibernateTemplate().findByNamedParam(hql, new String[]{"from", "to"},
                    new Object[]{from, to});
        }

        return rptAcDocs;
    }

    public List<AccountingDocument> findOutstandingInvForCollection(long contactableId, int contactableType, Date from, Date to) {
        List<AccountingDocument> acDocs = new ArrayList();
        String hql = "";

        if (contactableType == 1) {
            hql = "select distinct a "
                    + "from AccountingDocument as a "
                    + "left join fetch a.accountingDocumentLines as al "
                    + "left join fetch al.tickets as t "
                    + "left join fetch al.services as s "
                    + "left join fetch a.pnr as p "
                    + "left join fetch p.servicingCareer as s "
                    //+ "inner join fetch p.tickets as t "
                    + "inner join fetch p.agent as agt "
                    + "left join fetch a.acTransactions as act "
                    + "left join fetch a.relatedDocuments as rd "
                    + "left join fetch rd.accountingDocumentLines as rdl "
                    + "left join fetch rdl.tickets as t1 "
                    + "left join fetch rdl.services as s1 "
                    + "left join fetch rd.acTransactions as act1 "
                    + "where a.acDoctype = 1 and "
                    + "(select coalesce(sum(l.amount),0) + coalesce(sum(t.grossFare),0) + coalesce(sum(t.discount),0) + coalesce(sum(t.atolChg),0) "
                    + "+ coalesce(sum(s.serviceCharge),0) + coalesce(sum(s.discount),0) "
                    + "from AccountingDocument as b "
                    + "left join b.accountingDocumentLines as l "
                    + "left join l.tickets as t "
                    + "left join l.services as s "
                    + "where b.acDocRef = a.acDocRef) > "
                    + "(select coalesce(sum(act.transAmount),0) from AcTransaction act where act.invoice.acDocId = a.acDocId) "
                    + " and agt.contactableId = :contactableId and "
                    + "(:from is null or a.issueDate >= :from) and (:to is null or a.issueDate <= :to) "
                    + "order by a.acDocId ";
        } else if (contactableType == 2) {
            hql = "select distinct a "
                    + "from AccountingDocument as a "
                    + "left join fetch a.accountingDocumentLines as al "
                    + "left join fetch al.tickets as t "
                    + "left join fetch al.services as s "
                    + "left join fetch a.pnr as p "
                    + "left join fetch p.servicingCareer as s "
                    //+ "inner join fetch p.tickets as t "
                    + "inner join fetch p.customer as cust "//with :contactableId is null or cust.contactableId = :contactableId "
                    + "left join fetch a.acTransactions as act "
                    + "left join fetch a.relatedDocuments as rd "
                    + "left join fetch rd.accountingDocumentLines as rdl "
                    + "left join fetch rdl.tickets as t1 "
                    + "left join fetch rdl.services as s1 "
                    + "left join fetch rd.acTransactions as act1 "
                    + "(select coalesce(sum(l.amount),0) + coalesce(sum(t.grossFare),0) + coalesce(sum(t.discount),0) + coalesce(sum(t.atolChg),0) "
                    + "+ coalesce(sum(s.serviceCharge),0) + coalesce(sum(s.discount),0) "
                    + "from AccountingDocument as b "
                    + "left join b.accountingDocumentLines as l "
                    + "left join l.tickets as t "
                    + "left join l.services as s "
                    + "where b.acDocRef = a.acDocRef) > "
                    + "(select coalesce(sum(act.transAmount),0) from AcTransaction act where act.invoice.acDocId = a.acDocId) "
                    + " and cust.contactableId = :contactableId "
                    + "(:from is null or a.issueDate >= :from) and (:to is null or a.issueDate <= :to) "
                    + " order by a.acDocId ";
        }

        acDocs = getHibernateTemplate().findByNamedParam(hql, new String[]{"contactableId", "from", "to"},
                new Object[]{contactableId, from, to});
        /*for (AccountingDocument invoice : acDocs) {
            List<Ticket> tickets = new ArrayList(invoice.getPnr().getTickets());
            invoice.getPnr().setLeadPax(tickets.get(0));
        }*/
        return acDocs;
    }

      public List<AccountingDocument> findOutstandingUnBalancedInvForCollection(long contactableId, int contactableType, Date from, Date to) {
        List<AccountingDocument> acDocs = new ArrayList();
        String hql = "";

        if (contactableType == 1) {
            hql = "select distinct a "
                    + "from AccountingDocument as a "
                    + "left join fetch a.accountingDocumentLines as al "
                    + "left join fetch al.tickets as t "
                    + "left join fetch a.pnr as p "
                    + "left join fetch p.servicingCareer as s "                    
                    + "inner join p.agent as agt "//with :contactableId is null or agt.contactableId = :contactableId "
                    + "left join fetch a.acTransactions as act "
                    + "left join fetch a.relatedDocuments as rd "
                    + "left join fetch rd.accountingDocumentLines as rdl "
                    + "left join fetch rdl.tickets as t1 "
                    + "left join fetch rd.acTransactions as act1 "
                    + "where a.acDoctype = 1 and agt.contactableId = :contactableId and "
                    + "(select coalesce(sum(l.amount),0) + coalesce(sum(t.grossFare),0) - coalesce(sum(t.discount),0) + coalesce(sum(t.atolChg),0) "
                    + "from AccountingDocument as b left join b.accountingDocumentLines as l left join l.tickets as t where b.acDocRef = a.acDocRef) <> "
                    + "(select coalesce(sum(act.transAmount),0) from AcTransaction act where act.invoice.acDocId = a.acDocId) "
                    + " and "
                    + "(:from is null or a.issueDate >= :from) and (:to is null or a.issueDate <= :to) "
                    + "order by a.acDocId ";
        } else if (contactableType == 2) {
            hql = "select distinct a "
                    + "from AccountingDocument as a "
                    + "left join fetch a.accountingDocumentLines as al "
                    + "left join fetch al.tickets as t "
                    + "left join fetch a.pnr as p "
                    + "left join fetch p.servicingCareer as s "                    
                    + "inner join p.customer as cust "//with :contactableId is null or cust.contactableId = :contactableId "
                    + "left join fetch a.acTransactions as act "
                    + "left join fetch a.relatedDocuments as rd "
                    + "left join fetch rd.accountingDocumentLines as rdl "
                    + "left join fetch rdl.tickets as t1 "
                    + "left join fetch rd.acTransactions as act1 "
                    + "where a.acDoctype = 1 and cust.contactableId = :contactableId and "
                    + "(select coalesce(sum(l.amount),0) + coalesce(sum(t.grossFare),0) - coalesce(sum(t.discount),0) + coalesce(sum(t.atolChg),0) "
                    + "from AccountingDocument as b left join b.accountingDocumentLines as l left join l.tickets as t where b.acDocRef = a.acDocRef) <> "
                    + "(select coalesce(sum(act.transAmount),0) from AcTransaction act where act.invoice.acDocId = a.acDocId) "
                    + " and "
                    + "(:from is null or a.issueDate >= :from) and (:to is null or a.issueDate <= :to) "
                    + " order by a.acDocId ";
        }

        acDocs = getHibernateTemplate().findByNamedParam(hql, new String[]{"contactableId", "from", "to"},
                new Object[]{contactableId, from, to});
        for (AccountingDocument invoice : acDocs) {
            List<Ticket> tickets = new ArrayList(invoice.getPnr().getTickets());
            invoice.getPnr().setLeadPax(tickets.get(0));
        }
        return acDocs;
    }
      
    public List<Agent> findOutstandingAgents() {

        String hql = "select agt "
                + "from AccountingDocument as a "
                + "inner join a.pnr as p "
                + "inner join p.agent as agt "
                + "where a.acDoctype = 1 and "
                + "(select coalesce(sum(l.amount),0) + coalesce(sum(t.grossFare),0) + coalesce(sum(t.discount),0) + coalesce(sum(t.atolChg),0) "
                + "+ coalesce(sum(s.serviceCharge),0) + coalesce(sum(s.discount),0) "
                + "from AccountingDocument as b "
                + "left join b.accountingDocumentLines as l "
                + "left join l.tickets as t "
                + "left join l.services as s "
                + "where b.acDocRef = a.acDocRef) > "
                + "(select coalesce(sum(act.transAmount),0) from AcTransaction act "
                + "where act.invoice.acDocId = a.acDocId) "
                + "group by agt  order by agt.name ";

        List results = getHibernateTemplate().find(hql);

        return results;
    }

    public List<Customer> findOutstandingCustomers() {

        String hql = "select cust "
                + "from AccountingDocument as a "
                + "inner join a.pnr as p "
                + "inner join p.customer as cust "
                + "where a.acDoctype = 1 and "
                + "(select coalesce(sum(l.amount),0) + coalesce(sum(t.grossFare),0) + coalesce(sum(t.discount),0) + coalesce(sum(t.atolChg),0) "
                + "+ coalesce(sum(s.serviceCharge),0) + coalesce(sum(s.discount),0) "
                + "from AccountingDocument as b "
                + "left join b.accountingDocumentLines as l "
                + "left join l.tickets as t "
                + "left join l.services as s "
                + "where b.acDocRef = a.acDocRef) > "
                + "(select coalesce(sum(act.transAmount),0) from AcTransaction act where act.invoice.acDocId = a.acDocId) "
                + "group by cust order by cust.surName ";

        List results = getHibernateTemplate().find(hql);

        return results;
    }

    public List<AccountingDocument> findOutstandingRefund() {

        String hql = "select distinct a "
                + "from AccountingDocument as a "
                + "left join fetch a.pnr as p "
                + "left join fetch p.agent as agt "
                + "left join fetch p.customer as cust "
                + "left join fetch p.servicingCareer as s "
                + "inner join fetch p.tickets as t "
                + "left join fetch a.acTransactions as act "
                + "left join fetch a.relatedDocuments "
                + "where "
                + "(select coalesce(sum(l.amount),0) + coalesce(sum(t.grossFare),0) - coalesce(sum(t.discount),0) + coalesce(sum(t.atolChg),0) "
                + "from AccountingDocument as b left join b.accountingDocumentLines as l left join l.tickets as t where b.acDocRef = a.acDocRef) < "
                + "(select coalesce(sum(act.transAmount),0) from AcTransaction act where act.invoice.acDocId = a.acDocId) "
                + " order by a.acDocId ";

        List<AccountingDocument> acDocs = new ArrayList();
        acDocs = getHibernateTemplate().find(hql);

        return acDocs;
    }

    public List<Agent> findOutstandingRefundAgents() {

        String hql = "select agt "
                + "from AccountingDocument as a "
                + "inner join a.pnr as p "
                + "inner join p.agent as agt "
                + "where "
                + "(select coalesce(sum(l.amount),0) + coalesce(sum(t.grossFare),0) - coalesce(sum(t.discount),0) + coalesce(sum(t.atolChg),0) "
                + "from AccountingDocument as b left join b.accountingDocumentLines as l left join l.tickets as t where b.acDocRef = a.acDocRef) < "
                + "(select coalesce(sum(act.transAmount),0) from AcTransaction act where act.invoice.acDocId = a.acDocId) "
                + "group by agt  order by agt.name ";

        List results = getHibernateTemplate().find(hql);

        return results;
    }

    public List<Customer> findOutstandingRefundCustomers() {

        String hql = "select cust "
                + "from AccountingDocument as a "
                + "inner join a.pnr as p "
                + "inner join p.customer as cust "
                + "where "
                + "(select coalesce(sum(l.amount),0) + coalesce(sum(t.grossFare),0) - coalesce(sum(t.discount),0) + coalesce(sum(t.atolChg),0) "
                + "from AccountingDocument as b left join b.accountingDocumentLines as l left join l.tickets as t where b.acDocRef = a.acDocRef) < "
                + "(select coalesce(sum(act.transAmount),0) from AcTransaction act where act.invoice.acDocId = a.acDocId) "
                + "group by cust  order by cust.surName ";

        List results = getHibernateTemplate().find(hql);

        return results;
    }

    public List<Agent> findOutstandingticketingAgts() {
        String hql = "select agt "
                + "from PurchaseAccountingDocument as a "
                + "inner join a.pnr as p "
                + "inner join p.ticketingAgt as agt "
                + "where "
                + "(select coalesce(sum(l.amount),0) + coalesce(sum(t.grossFare),0) - coalesce(sum(t.discount),0) + coalesce(sum(t.atolChg),0) "
                + "from PurchaseAccountingDocument as b left join b.purchaseAcDocLines as l left join l.tickets as t where b.recipientRef = a.recipientRef) > "
                + "(select coalesce(sum(bt.transAmount),0) from BillingTransaction bt where bt.invoice.purchaseAcDocId = a.purchaseAcDocId) "
                + "group by agt  order by agt.name ";

        List results = getHibernateTemplate().find(hql);

        return results;
    }

    public List<PurchaseAccountingDocument> findOutstandingPInv() {

        String hql = "select distinct a "
                + "from PurchaseAccountingDocument as a "
                + "left join fetch a.pnr as p "
                + "left join fetch p.ticketingAgt as agt "
                + "left join fetch p.servicingCareer as s "
                + "inner join fetch p.tickets as t "
                + "left join fetch a.billingTransactions as bt "
                + "left join fetch a.relatedDocuments "
                + "where a.acDoctype = 1  and "
                + "(select coalesce(sum(l.amount),0) + coalesce(sum(t.grossFare),0) - coalesce(sum(t.discount),0) + coalesce(sum(t.atolChg),0) "
                + "from PurchaseAccountingDocument as b left join b.purchaseAcDocLines as l left join l.tickets as t where b.recipientRef = a.recipientRef) > "
                + "(select coalesce(sum(bt.transAmount),0) from BillingTransaction bt where bt.invoice.purchaseAcDocId = a.purchaseAcDocId) "
                + " group by a.purchaseAcDocId ";

        List<PurchaseAccountingDocument> rptAcDocs = new ArrayList();
        rptAcDocs = getHibernateTemplate().find(hql);

        return rptAcDocs;
    }

    public List<PurchaseAccountingDocument> findOutstandingPInvByCriteria(Long contactableId, Date from, Date to) {
        List<PurchaseAccountingDocument> rptAcDocs = new ArrayList();
        String hql = "";

        hql = "select distinct a "
                + "from PurchaseAccountingDocument as a "
                + "left join fetch a.purchaseAcDocLines as al "
                + "left join fetch a.billingTransactions as bt " 
                
                + "left join fetch al.tickets as t "
                + "left join fetch a.relatedDocuments as rd "
                + "left join fetch rd.purchaseAcDocLines as rdl "
                + "left join fetch rd.billingTransactions as bt1 "       
                + "left join fetch rdl.tickets as t1 "
                + "left join fetch a.pnr as p "
                + "left join fetch p.servicingCareer as s "
                //+ "inner join fetch p.tickets as t "
                + "inner join fetch p.ticketingAgt as agt "                                      
                + "where a.acDoctype = 1 and agt.contactableId <> 1 and "
                + "(select coalesce(sum(l.amount),0) + coalesce(sum(t.baseFare),0) + coalesce(sum(t.tax),0) + coalesce(sum(t.atolVendor),0) + coalesce(sum(t.bspCom),0) "
                + "from PurchaseAccountingDocument as b left join b.purchaseAcDocLines as l left join l.tickets as t where b.recipientRef = a.recipientRef) > "
                + "(select coalesce(sum(bt.transAmount),0) from BillingTransaction bt where bt.invoice.purchaseAcDocId = a.purchaseAcDocId) "
                + " and "
                + "(:from is null or a.issueDate >= :from) and (:to is null or a.issueDate <= :to) "
                + "and (:contactableId is null or agt.contactableId = :contactableId) "
                + " order by a.purchaseAcDocId ";


        rptAcDocs = getHibernateTemplate().findByNamedParam(hql, new String[]{"contactableId", "from", "to"},
                new Object[]{contactableId, from, to});

        return rptAcDocs;
    }

        public List<PurchaseAccountingDocument> findOutstandingPCNoteByCriteria(Long contactableId, Date from, Date to) {
        List<PurchaseAccountingDocument> rptAcDocs = new ArrayList();
        String hql = "";

        hql = "select distinct a "
                + "from PurchaseAccountingDocument as a "
                + "left join fetch a.purchaseAcDocLines as al "
                + "inner join fetch al.tickets as t "
                + "left join fetch a.relatedDocuments as rd "
                + "left join fetch rd.purchaseAcDocLines as rdl "
                + "left join fetch rdl.tickets as t1 "
                + "left join fetch a.pnr as p "
                + "left join fetch p.servicingCareer as s "
                //+ "inner join fetch p.tickets as t "
                + "inner join fetch p.ticketingAgt as agt "
                + "left join fetch a.billingTransactions as bt " 
                + "left join fetch rd.billingTransactions as bt1 "   
                + "where a.acDoctype = 1 and agt.contactableId <> 1 and "
                + "(select coalesce(sum(l.amount),0) + coalesce(sum(t.baseFare),0) + coalesce(sum(t.tax),0) + coalesce(sum(t.atolVendor),0) + coalesce(sum(t.bspCom),0) "
                + "from PurchaseAccountingDocument as b left join b.purchaseAcDocLines as l left join l.tickets as t where b.recipientRef = a.recipientRef) < "
                + "(select coalesce(sum(bt.transAmount),0) from BillingTransaction bt where bt.invoice.purchaseAcDocId = a.purchaseAcDocId) "
                + " and "
                + "(:from is null or a.issueDate >= :from) and (:to is null or a.issueDate <= :to) "
                + "and (:contactableId is null or agt.contactableId = :contactableId) "
                + " order by a.purchaseAcDocId ";


        rptAcDocs = getHibernateTemplate().findByNamedParam(hql, new String[]{"contactableId", "from", "to"},
                new Object[]{contactableId, from, to});

        return rptAcDocs;
    }
        
    public List<PurchaseAccountingDocument> findOutstandingPInvForBspBilling(Date from, Date to) {
        List<PurchaseAccountingDocument> pacDocs = new ArrayList();
        String hql = "";

        hql = "select distinct a "
                + "from PurchaseAccountingDocument as a "
                + "left join fetch a.purchaseAcDocLines as line "
                + "left join fetch line.purchaseAccountingDocument "
                + "left join fetch line.tickets as t "
                + "left join fetch t.ticketRefundDetails "
                //+ "left join fetch t.purchaseAccountingDocumentLine "
                + "left join fetch t.pnr as p "
                + "left join fetch p.ticketingAgt as agt "
                + "left join fetch p.servicingCareer as s "
                + "left join fetch a.billingTransactions as bt1 "                
                + "where "
                + "((select coalesce(sum(l.amount),0) + coalesce(sum(t1.baseFare),0) + coalesce(sum(t1.tax),0) + coalesce(sum(t1.atolVendor),0) + coalesce(sum(t1.bspCom),0) "
                + "from PurchaseAccountingDocument as b left join b.purchaseAcDocLines as l left join l.tickets as t1 where b.purchaseAcDocId = a.purchaseAcDocId) <> "
                + "(select coalesce(sum(bt.transAmount),0) from BillingTransaction bt where bt.purchaseAccountingDocument.purchaseAcDocId = a.purchaseAcDocId)) "
                + " and "
                + "(:from is null or a.issueDate >= :from) and (:to is null or a.issueDate <= :to) "
                + "and agt.contactableId = 1 "
                + "order by a.purchaseAcDocId ";

        pacDocs = getHibernateTemplate().findByNamedParam(hql, new String[]{"from", "to"},
                new Object[]{from, to});

        return pacDocs;
    }

    public List<AccountingDocument> invHistoryByCriteria(Long contactableId,
            int contactableType, Integer docTypeFrom, Integer docTypeTo, Date from, Date to,Long tktingAgtFrom,Long tktingAgtTo) {
        List<AccountingDocument> acDocs = new ArrayList();

        String hql = "";
        String hql1 = "";

        if (contactableType == 1) {
            hql = "select distinct a "
                    + "from AccountingDocument as a "
                    + "left join fetch a.accountingDocumentLines as al "                                        
                    + "left join fetch al.tickets as t "
                    + "left join fetch al.services as s "
                    + "left join fetch a.pnr as p "
                    + "left join fetch p.servicingCareer as s "
                    + "left join fetch a.acTransactions as act "
                    + "inner join fetch p.agent as agt "
                    + "left join fetch p.ticketingAgt as tAgt "
                    + "where "
                    + "(:from is null or a.issueDate >= :from) and (:to is null or a.issueDate <= :to) "
                    + "and (:contactableId is null or agt.contactableId = :contactableId) "
                    + "and (:docTypeFrom is null or a.acDoctype >= :docTypeFrom)  and (:docTypeTo is null or a.acDoctype <= :docTypeTo) "
                    + "and (:tktingAgtFrom is null or tAgt.contactableId >= :tktingAgtFrom) and(:tktingAgtTo is null or tAgt.contactableId <= :tktingAgtTo) "
                    + " order by a.acDocId ";
        } else if (contactableType == 2) {
            hql = "select distinct a "
                    + "from AccountingDocument as a "
                    + "left join fetch a.accountingDocumentLines as al "                    
                    + "left join fetch al.tickets as t "
                    + "left join fetch al.services as s "
                    + "left join fetch a.pnr as p "
                    + "left join fetch p.servicingCareer as s "
                    + "left join fetch a.acTransactions as act "
                    + "inner join fetch p.customer as cust "
                     + "left join fetch p.ticketingAgt as tAgt "
                    + "where "
                    + "(:from is null or a.issueDate >= :from) and (:to is null or a.issueDate <= :to) "
                    + "and (:contactableId is null or cust.contactableId = :contactableId) "
                    + "and (:docTypeFrom is null or a.acDoctype >= :docTypeFrom and (:docTypeTo is null or a.acDoctype <= :docTypeTo) ) "
                    + "and (:tktingAgtFrom is null or tAgt.contactableId >= :tktingAgtFrom) and(:tktingAgtTo is null or tAgt.contactableId <= :tktingAgtTo) "
                    + " order by a.acDocId  ";
        } else if (contactableType == 0) {
            hql1 = "select distinct a "
                    + "from AccountingDocument as a "
                    + "left join fetch a.accountingDocumentLines as al "                    
                    + "left join fetch al.tickets as t "
                    + "left join fetch al.services as s "
                    + "left join fetch a.pnr as p "
                    + "left join fetch p.servicingCareer as s "
                    + "left join fetch a.acTransactions as act "
                    + "left join fetch p.customer as cust "
                    + "left join fetch p.agent as agt "
                    + "left join fetch p.ticketingAgt as tAgt "
                    + "where (:docTypeFrom is null or a.acDoctype >= :docTypeFrom) and (:docTypeTo is null or a.acDoctype <= :docTypeTo) "
                    + "and (:tktingAgtFrom is null or tAgt.contactableId >= :tktingAgtFrom) and(:tktingAgtTo is null or tAgt.contactableId <= :tktingAgtTo) "
                    + "and (:from is null or a.issueDate >= :from) and (:to is null or a.issueDate <= :to) "
                    + " order by a.acDocId ";
        }

        if (!hql.isEmpty()) {
            acDocs = getHibernateTemplate().findByNamedParam(hql, new String[]{"contactableId", "docTypeFrom","docTypeTo", "from", "to","tktingAgtFrom","tktingAgtTo"},
                    new Object[]{contactableId, docTypeFrom,docTypeTo, from, to,tktingAgtFrom,tktingAgtTo});
        } else {
            acDocs = getHibernateTemplate().findByNamedParam(hql1, new String[]{"docTypeFrom","docTypeTo","from", "to","tktingAgtFrom","tktingAgtTo"},
                    new Object[]{docTypeFrom,docTypeTo,from, to,tktingAgtFrom,tktingAgtTo});
        }

        return acDocs;
    }

    public List<PurchaseAccountingDocument> pAcDocHistoryByCriteria(Long contactableId,
            Integer docType, Date from, Date to) {
        List<PurchaseAccountingDocument> acDocs = new ArrayList();

        String hql = "";
        hql = "select distinct a "
                + "from PurchaseAccountingDocument as a "
                + "left join fetch a.purchaseAcDocLines as al "
                + "left join fetch al.tickets as t "
                + "left join fetch a.pnr as p "
                + "left join fetch p.servicingCareer as s "
                + "left join fetch a.billingTransactions as bt "
                + "inner join fetch p.ticketingAgt as agt "
                + "where "
                + "(:from is null or a.issueDate >= :from) and (:to is null or a.issueDate <= :to) "
                + "and (:contactableId is null or agt.contactableId = :contactableId) and agt<>1 "
                + "and (:docType is null or a.acDoctype = :docType) "
                + " order by a.purchaseAcDocId ";

        acDocs = getHibernateTemplate().findByNamedParam(hql, new String[]{"contactableId", "docType", "from", "to"},
                new Object[]{contactableId, docType, from, to});


        return acDocs;
    }

    public List<Agent> findOutstandingThirdPartyVendors() {
        String hql = "select distinct agt "
                + "from PurchaseAccountingDocument as a "
                + "inner join a.pnr as p "
                + "inner join p.ticketingAgt as agt "
                + "where a.acDoctype = 1 and agt.contactableId <> 1 and "
                + "(select coalesce(sum(l.amount),0) + coalesce(sum(t.baseFare),0) + coalesce(sum(t.tax),0) + coalesce(sum(t.atolVendor),0) + coalesce(sum(t.bspCom),0) "
                + "from PurchaseAccountingDocument as b left join b.purchaseAcDocLines as l left join l.tickets as t where b.recipientRef = a.recipientRef) > "
                + "(select coalesce(sum(bt.transAmount),0) from BillingTransaction bt where bt.invoice.purchaseAcDocId = a.purchaseAcDocId) "
                + "order by agt.name ";

        List results = getHibernateTemplate().find(hql);

        return results;
    }
    
        public List<Agent> findOutstandingCNoteThirdPartyVendors() {
        String hql = "select distinct agt "
                + "from PurchaseAccountingDocument as a "
                + "inner join a.pnr as p "
                + "inner join p.ticketingAgt as agt "
                + "where a.acDoctype = 1 and agt.contactableId <> 1 and "
                + "(select coalesce(sum(l.amount),0) + coalesce(sum(t.baseFare),0) + coalesce(sum(t.tax),0) + coalesce(sum(t.atolVendor),0) + coalesce(sum(t.bspCom),0) "
                + "from PurchaseAccountingDocument as b left join b.purchaseAcDocLines as l left join l.tickets as t where b.recipientRef = a.recipientRef) < "
                + "(select coalesce(sum(bt.transAmount),0) from BillingTransaction bt where bt.invoice.purchaseAcDocId = a.purchaseAcDocId) "
                + "order by agt.name ";

        List results = getHibernateTemplate().find(hql);

        return results;
    }

    public BigDecimal finalAcBalancetoDate(Date to, Long agtId, Long custId) {
        BigDecimal finalBalanceToDate = new BigDecimal("0.00");
        String hql = "select coalesce(sum(l.amount),0) "
                + "+ coalesce(sum(t.grossFare),0) "
                + "- coalesce(sum(t.discount),0) "
                + "+ coalesce(sum(t.atolChg),0) "
                + "- coalesce(sum(act.transAmount),0) "
                + "from ClientAcStatement as stmt "
                + "left join stmt.accountingDocument as a "
                + "left join a.pnr as p with p.agent.contactableId = :agtId "
                + "left join  a.accountingDocumentLines as l "
                + "left join  l.tickets as t "
                + "left join stmt.acTransaction as act "
                + "left join act.pnr as p1 with p1.agent.contactableId = :agtId "
                + "where "
                + "+ (:to is null or stmt.transDate >= :to) ";
        Object val = getHibernateTemplate().findByNamedParam(hql, new String[]{"agtId","to"},
                new Object[]{agtId,to});
        finalBalanceToDate = new BigDecimal(String.valueOf(val));
        return finalBalanceToDate;
    }
}
