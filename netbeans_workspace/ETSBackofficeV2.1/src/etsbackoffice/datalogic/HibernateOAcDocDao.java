/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etsbackoffice.datalogic;

import etsbackoffice.domain.Agent;
import etsbackoffice.domain.Customer;
import etsbackoffice.domain.OAccountingDocument;
import etsbackoffice.domain.OAccountingDocumentLine;
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
public class HibernateOAcDocDao extends HibernateDaoSupport implements OAcDocDao {

    @Transactional
    public void saveOrUpdateAcDoc(OAccountingDocument oAcDoc) {
        getHibernateTemplate().saveOrUpdate(oAcDoc);
    }
    
    @Transactional
    public void deleteOAcDoc(OAccountingDocument oAcDoc) {
        getHibernateTemplate().delete(oAcDoc);
    }

    @Transactional
     public void deleteOAcDocLine(OAccountingDocumentLine l) {
        getHibernateTemplate().delete(l);
    }
    public int getMaxAcDocRef() {
        String hql = "select max(acDoc.acDocRef) from OAccountingDocument acDoc";
        Object result = DataAccessUtils.uniqueResult(getHibernateTemplate().find(hql));
        return (result != null) ? Integer.valueOf(result.toString()) : 0;
    }

    public List<OAccountingDocument> findAcDocByRef(int acDocRef) {
        String hql = "select distinct acDoc from OAccountingDocument acDoc "
                + "left join fetch acDoc.agent "
                + "left join fetch acDoc.customer "
                + "left join fetch acDoc.oAccountingDocumentLines "
                + "left join fetch acDoc.acTransactions "
                + "where acDoc.acDocRef = ? ";
        List<OAccountingDocument> acDocs = getHibernateTemplate().find(hql, acDocRef);

        return acDocs;
    }

    public List<OAccountingDocument> findAcDocByCustomerName(String surName, String foreName) {
        surName = surName.concat("%");
        foreName = foreName.concat("%");

        String hql = "select acDoc from OAccountingDocument acDoc "
                + "inner join fetch acDoc.customer  as c "
                + "left join fetch acDoc.oAccountingDocumentLines "
                + "where c.surName like ? and c.foreName like ? ";

        List<OAccountingDocument> acDocs = getHibernateTemplate().find(hql, surName, foreName);

        return acDocs;
    }

    public OAccountingDocument findCompleteAcDocById(long acDocId) {

        String hql = "select a from OAccountingDocument as a "
                + "left join fetch a.acTransactions "
                + "left join fetch a.agent "
                + "left join fetch a.customer "
                + "left join fetch a.oAccountingDocumentLines as line "
                + "left join fetch line.otherService "
                + "left join fetch a.relatedDocuments as rds "
                + "left join fetch rds.oAccountingDocumentLines as oline "
                + "left join fetch rds.acTransactions "
                + "where a.oAcDocId =? ";

        OAccountingDocument acDoc = (OAccountingDocument) DataAccessUtils.uniqueResult(getHibernateTemplate().find(hql, acDocId));
        return acDoc;
    }

    public OAccountingDocument findCompleteAcDocByRef(int refNo) {

        String hql = "select a from OAccountingDocument as a "
                + "left join fetch a.acTransactions "
                + "left join fetch a.agent "
                + "left join fetch a.customer "
                + "left join fetch a.oAccountingDocumentLines as line "
                + "left join fetch line.otherService "
                + "left join fetch a.relatedDocuments as rds "
                + "left join fetch rds.oAccountingDocumentLines as oline "
                + "left join fetch rds.acTransactions "
                + "where a.acDocRef =? and a.acDoctype =1 ";

        OAccountingDocument acDoc = (OAccountingDocument) DataAccessUtils.uniqueResult(getHibernateTemplate().find(hql, refNo));
        return acDoc;
    }

    public List<OAccountingDocument> invHistoryByCriteria(int contType, Long contId,
            Integer docType, Date from, Date to) {
        List<OAccountingDocument> acDocs = new ArrayList();
        String hql = "", hql1 = "";

        if (contType == 0) {
            hql = "select distinct a "
                    + "from OAccountingDocument as a "
                    + "left join fetch a.agent as agt "
                    + "left join fetch a.customer as cust "
                    + "left join fetch a.oAccountingDocumentLines as l "
                    + "left join fetch l.otherService "
                    + "left join fetch a.acTransactions as act "
                    + "where (:docType is null or a.acDoctype = :docType) "
                    + "and (:contId is null or agt.contactableId = :contId) "
                    + "and (:contId is null or cust.contactableId = :contId) "
                    + "and (:from is null or a.issueDate >= :from) and (:to is null or a.issueDate <= :to) "
                    + " order by a.oAcDocId ";
        } else if (contType == 1) {
            hql = "select distinct a "
                    + "from OAccountingDocument as a "
                    + "inner join fetch a.agent as agt "
                    + "left join fetch a.oAccountingDocumentLines as l "
                    + "left join fetch l.otherService "
                    + "left join fetch a.acTransactions as act "
                    + "where (:docType is null or a.acDoctype = :docType) "
                    + "and (:contId is null or agt.contactableId = :contId) "
                    + "and (:from is null or a.issueDate >= :from) and (:to is null or a.issueDate <= :to) "
                    + " order by a.oAcDocId ";
        } else if (contType == 2) {
            hql = "select distinct a "
                    + "from OAccountingDocument as a "
                    + "inner join fetch a.customer as cust "
                    + "left join fetch a.oAccountingDocumentLines as l "
                    + "left join fetch l.otherService "
                    + "left join fetch a.acTransactions as act "
                    + "where (:docType is null or a.acDoctype = :docType) "
                    + "and (:contId is null or cust.contactableId = :contId) "
                    + "and (:from is null or a.issueDate >= :from) and (:to is null or a.issueDate <= :to) "
                    + " order by a.oAcDocId ";
        }

        acDocs = getHibernateTemplate().findByNamedParam(hql, new String[]{"contId", "docType", "from", "to"},
                new Object[]{contId, docType, from, to});

        return acDocs;
    }

    public List<OAccountingDocument> invOutstandingByCriteria(int contType, Long contId,
            Date from, Date to) {
        List<OAccountingDocument> acDocs = new ArrayList();
        String hql = "", hql1 = "";

        if (contType == 0) {
            hql = "select distinct a "
                    + "from OAccountingDocument as a "
                    + "left join fetch a.agent as agt "
                    + "left join fetch a.customer as cust "
                    + "left join fetch a.oAccountingDocumentLines as l "
                    + "left join fetch l.otherService "
                    + "left join fetch a.acTransactions as act "
                    + "where a.acDoctype = 1 and "
                    + "(select coalesce(sum( (l.serviceCharge - l.discount)*l.unit + (((l.serviceCharge - l.discount)*l.unit)*l.vatRate)/100),0) "
                    + "from OAccountingDocument as b  left join b.oAccountingDocumentLines as l where b.acDocRef = a.acDocRef) > "
                    + "(select coalesce(sum(act.transAmount),0) from AcTransaction act where act.oInvoice.oAcDocId = a.oAcDocId) "
                    + "and (:contId is null or agt.contactableId = :contId) "
                    + "and (:contId is null or cust.contactableId = :contId) "
                    + "and (:from is null or a.issueDate >= :from) and (:to is null or a.issueDate <= :to) "
                    + " order by a.oAcDocId ";
        } else if (contType == 1) {
            hql = "select distinct a "
                    + "from OAccountingDocument as a "
                    + "inner join fetch a.agent as agt "
                    + "left join fetch a.oAccountingDocumentLines as l "
                    + "left join fetch l.otherService "
                    + "left join fetch a.acTransactions as act "
                    + "where a.acDoctype = 1 and "
                    + "(select coalesce(sum( (l.serviceCharge - l.discount)*l.unit + (((l.serviceCharge - l.discount)*l.unit)*l.vatRate)/100),0) "
                    + "from OAccountingDocument as b  left join b.oAccountingDocumentLines as l where b.acDocRef = a.acDocRef) > "
                    + "(select coalesce(sum(act.transAmount),0) from AcTransaction act where act.oInvoice.oAcDocId = a.oAcDocId) "
                    + "and (:contId is null or agt.contactableId = :contId) "
                    + "and (:from is null or a.issueDate >= :from) and (:to is null or a.issueDate <= :to) "
                    + " order by a.oAcDocId ";
        } else if (contType == 2) {
            hql = "select distinct a "
                    + "from OAccountingDocument as a "
                    + "inner join fetch a.customer as cust "
                    + "left join fetch a.oAccountingDocumentLines as l "
                    + "left join fetch l.otherService "
                    + "left join fetch a.acTransactions as act "
                    + "where a.acDoctype = 1 and "
                    + "(select coalesce(sum( (l.serviceCharge - l.discount)*l.unit + (((l.serviceCharge - l.discount)*l.unit)*l.vatRate)/100),0) "
                    + "from OAccountingDocument as b  left join b.oAccountingDocumentLines as l where b.acDocRef = a.acDocRef) > "
                    + "(select coalesce(sum(act.transAmount),0) from AcTransaction act where act.oInvoice.oAcDocId = a.oAcDocId) "
                    + "and (:contId is null or cust.contactableId = :contId) "
                    + "and (:from is null or a.issueDate >= :from) and (:to is null or a.issueDate <= :to) "
                    + " order by a.oAcDocId ";
        }

        acDocs = getHibernateTemplate().findByNamedParam(hql, new String[]{"contId", "from", "to"},
                new Object[]{contId, from, to});

        return acDocs;
    }

    public List<Agent> findOutstandingAgents() {

        String hql = "select agt "
                + "from OAccountingDocument as a "
                + "inner join a.agent as agt "
                + "where a.acDoctype = 1 and "
                + "(select coalesce(sum( (l.serviceCharge - l.discount)*l.unit + (((l.serviceCharge - l.discount)*l.unit)*l.vatRate)/100),0) "
                + "from OAccountingDocument as b  left join b.oAccountingDocumentLines as l where b.acDocRef = a.acDocRef) > "
                + "(select coalesce(sum(act.transAmount),0) from AcTransaction act where act.oInvoice.oAcDocId = a.oAcDocId) "
                + "group by agt  order by agt.name ";

        List results = getHibernateTemplate().find(hql);

        return results;
    }

    public List<Customer> findOutstandingCustomers() {

        String hql = "select cust "
                + "from OAccountingDocument as a "
                + "inner join a.customer as cust "
                + "where a.acDoctype = 1 and "
                + "(select coalesce(sum( (l.serviceCharge - l.discount)*l.unit + (((l.serviceCharge - l.discount)*l.unit)*l.vatRate)/100),0) "
                + "from OAccountingDocument as b  left join b.oAccountingDocumentLines as l where b.acDocRef = a.acDocRef) > "
                + "(select coalesce(sum(act.transAmount),0) from AcTransaction act where act.oInvoice.oAcDocId = a.oAcDocId) "
                + "group by cust order by cust.surName ";

        List results = getHibernateTemplate().find(hql);

        return results;
    }   
}
