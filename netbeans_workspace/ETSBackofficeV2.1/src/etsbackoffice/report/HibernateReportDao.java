package etsbackoffice.report;

import etsbackoffice.domain.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 *
 * @author Yusuf
 */
public class HibernateReportDao extends HibernateDaoSupport implements ReportDao {   

    public List<Ticket> saleReportGds() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<OfficeID> findthirdPartyVendors() {
        List<OfficeID> vendors = new ArrayList();
        String hql = "select oid from PNR as p,OfficeID as oid "
                + " inner join fetch oid.agent as agt "
                + " where p.ticketingAgtOID = oid.officeID and agt.contactableId != 1 "
                + " group by agt ";

        vendors = getHibernateTemplate().find(hql);

        return vendors;
    }

    public List<Object> saleReportValueOnly() {//Uncompleted
        String hql = "";

        hql = "select t "
                + "from Ticket as t "
                + "left join fetch t.accountingDocumentLine "
                + "left join fetch t.purchaseAccountingDocumentLine as pa "
                + "inner join fetch t.pnr as p "
                + "left join fetch p.servicingCareer as s "
                + "where (t.tktStatus <> 1) and "
                + "p.ticketingAgt is null and "
                + "(:gdsId is null or p.gds.gdsId = :gdsId) and "
                + "(:oid is null or p.ticketingAgtOID = :oid) and "
                + "(:from is null or t.docIssuedate >= :from) and "
                + "(:to is null or t.docIssuedate <= :to) group by t";

        List<Object> results = getHibernateTemplate().find(hql);
        return results;
    }

    public List<AccountingDocument> revenueSummeryReport(Date from, Date to,String careerId,
            int contactableType, Long contactableId) {

        List<AccountingDocument> acDocs = new ArrayList();

        String hql = "";
        String hql1 = "";

        if (contactableType == 1) {
            hql = "select distinct a "
                    + "from AccountingDocument as a "
                    + "left join fetch a.acTransactions as act "
                    + "left join fetch a.relatedDocuments "
                    + "left join fetch a.accountingDocumentLines as aLines "
                    + "left join fetch aLines.services as s1 "
                    + "left join fetch aLines.tickets as t1 "
                    + "left join fetch a.pnr as p "                    
                    + "left join fetch p.servicingCareer as s "
                    + "inner join fetch p.agent as agt "
                    + "left join fetch p.ticketingAgt as tPV "
                    + "left join fetch a.purchaseAccountingDocuments as pAcDoc "
                    + "left join fetch pAcDoc.relatedDocuments as pRDoc "
                    + "left join fetch pAcDoc.billingTransactions as bt "
                    + "left join fetch pAcDoc.purchaseAcDocLines as pLines "  
                    + "left join fetch pRDoc.billingTransactions as bt1 "
                    + "left join fetch pRDoc.purchaseAcDocLines as pRLines " 
                    + "left join fetch pLines.tickets as t2 "
                    + "left join fetch t1.ticketRefundDetails "
                    + "left join fetch t2.ticketRefundDetails "
                    
                    + "where a.active = true and "
                    + "(:from is null or a.issueDate >= :from) and (:to is null or a.issueDate <= :to) "
                    + "and (:careerId is null or p.servicingCareer.code = :careerId) "
                    + "and (:contactableId is null or agt.contactableId = :contactableId) "
                    + "and (:careerId is null or p.servicingCareer.code = :careerId) "
                    + "order by p.pnrId ";
        } else if (contactableType == 2) {
            hql = "select distinct a "
                    + "from AccountingDocument as a "
                    + "left join fetch a.acTransactions as act "
                    + "left join fetch a.relatedDocuments "
                    + "left join fetch a.accountingDocumentLines as aLines "
                    + "left join fetch aLines.services as s1 "
                    + "left join fetch aLines.tickets as t1 "
                    + "left join fetch a.pnr as p "                    
                    + "left join fetch p.servicingCareer as s "
                    + "inner join fetch p.customer as cust "
                    + "left join fetch p.ticketingAgt as tPV "
                    + "left join fetch a.purchaseAccountingDocuments as pAcDoc "
                    + "left join fetch pAcDoc.relatedDocuments as pRDoc "
                    + "left join fetch pAcDoc.billingTransactions as bt "
                    + "left join fetch pAcDoc.purchaseAcDocLines as pLines "  
                    + "left join fetch pRDoc.billingTransactions as bt1 "
                    + "left join fetch pRDoc.purchaseAcDocLines as pRLines " 
                    + "left join fetch pLines.tickets as t2 "
                    + "left join fetch t1.ticketRefundDetails "
                    + "left join fetch t2.ticketRefundDetails "
                    + "where a.active = true and "
                    + "(:from is null or a.issueDate >= :from) and (:to is null or a.issueDate <= :to) "
                    + "and (:careerId is null or p.servicingCareer.code = :careerId) "
                    + "and (:contactableId is null or cust.contactableId = :contactableId) "
                    + "and (:careerId is null or p.servicingCareer.code = :careerId) "
                    + "order by p.pnrId ";
        } else if (contactableType == 0) {
            hql1 = "select distinct a "
                    + "from AccountingDocument as a "
                    + "left join fetch a.acTransactions as act "
                    + "left join fetch a.relatedDocuments "
                    + "left join fetch a.accountingDocumentLines as aLines "
                    + "left join fetch aLines.services as s1 "
                    + "left join fetch aLines.tickets as t1 "
                    + "left join fetch a.pnr as p "                    
                    + "left join fetch p.servicingCareer as s "
                    + "left join fetch p.customer as cust "
                    + "left join fetch p.agent as agt "
                    + "left join fetch p.ticketingAgt as tPV "
                    + "left join fetch a.purchaseAccountingDocuments as pAcDoc "
                    + "left join fetch pAcDoc.relatedDocuments as pRDoc "
                    + "left join fetch pAcDoc.billingTransactions as bt "
                    + "left join fetch pAcDoc.purchaseAcDocLines as pLines "  
                    + "left join fetch pRDoc.billingTransactions as bt1 "
                    + "left join fetch pRDoc.purchaseAcDocLines as pRLines " 
                    + "left join fetch pLines.tickets as t2 "
                    + "left join fetch t1.ticketRefundDetails "
                    + "left join fetch t2.ticketRefundDetails "
                    + "where  a.active = true and "
                    + "(:from is null or a.issueDate >= :from) and (:to is null or a.issueDate <= :to) "                                        
                    + "and (:careerId is null or p.servicingCareer.code = :careerId) "
                    + "order by a.acDocId ";
        }
           
        if (!hql.isEmpty()) {
            acDocs = getHibernateTemplate().findByNamedParam(hql, new String[]{"from","to","careerId","contactableId"},
                    new Object[]{from,to,careerId,contactableId});
        } else {
            acDocs = getHibernateTemplate().findByNamedParam(hql1, new String[]{"from","to","careerId"},
                    new Object[]{from,to,careerId});
        }
        return acDocs;
    }

    public List<AccountingDocument> netTRevenueReport(Date from, Date to, String careerId, int contactableType, Long contactableId) {
        List<AccountingDocument> acDocs = new ArrayList();
       
        return acDocs;
        
    }
}
