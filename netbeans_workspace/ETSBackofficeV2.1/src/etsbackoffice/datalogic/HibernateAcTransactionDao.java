package etsbackoffice.datalogic;

import etsbackoffice.domain.AcTransaction;
import etsbackoffice.domain.BatchBillingTransaction;
import etsbackoffice.domain.BillingTransaction;
import etsbackoffice.domain.BatchTransaction;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Yusuf
 */
public class HibernateAcTransactionDao extends HibernateDaoSupport implements AcTransactionDao {

    @Transactional
    public void saveTransaction(AcTransaction t) {
        getHibernateTemplate().saveOrUpdate(t);
    }

    @Transactional
    public void saveBulkTransaction(List<AcTransaction> acts) {        
        getHibernateTemplate().saveOrUpdateAll(acts);
    }

    @Transactional
    public void saveBTransaction(BatchTransaction consignment) {
        getHibernateTemplate().saveOrUpdate(consignment);
    }

    @Transactional
    public void saveBBillingTransaction(BatchBillingTransaction consignment) {
        getHibernateTemplate().saveOrUpdate(consignment);
    }

    public List<AcTransaction> loadTransactions(long pnrId) {
        return getHibernateTemplate().find("from AcTransaction as act where act.pnr.pnrId=?", pnrId);
    }

    @Transactional
    public void saveBTransaction(BillingTransaction t) {
        getHibernateTemplate().saveOrUpdate(t);
    }

    @Transactional
    public void saveBulkBTransaction(List<BillingTransaction> acts) {
        getHibernateTemplate().saveOrUpdateAll(acts);
    }

    public List<AcTransaction> loadTransactionsByCriteria(Date from, Date to, Integer tType,
            Long userId, Long contId, Integer contType,Integer acDocId,Integer oAcDocId) {
        String hql = "";
        String hql1 = "";
        //acDocId = -1;
        //oAcDocId = null;
                
        List<AcTransaction> acts = new ArrayList<AcTransaction>();

        if (contType == 1) {
            hql = "select distinct act from AcTransaction as act "
                    + "left join fetch act.user "
                    + "left join fetch act.accountingDocument as acDoc "
                    + "left join fetch act.oAccountingDocument as oAcDoc "
                    + "left join fetch acDoc.pnr as p "
                    + "left join fetch p.agent as agt "
                    + "left join fetch oAcDoc.agent as agt1 "
                    + "where p.customer is null and oAcDoc.customer is null and "
                    + "(:from is null or act.transDate >= :from) and (:to is null or act.transDate <= :to) "
                    + "and (:tType is null or act.transType = :tType) "
                    + "and (:userId is null or act.user.userId = :userId) "
                    + "and (:contId is null or agt.contactableId = :contId or agt1.contactableId = :contId) "  
                    //+ "and (:contId is null or agt1.contactableId = :contId) " 
                    + "and (:acDocId is null or acDoc.acDocId >= :acDocId) "
                    + "and (:oAcDocId is null or oAcDoc.oAcDocId >= :oAcDocId) "
                    + "order by act.transID ";
        } else if (contType == 2) {
            hql = "select distinct act from AcTransaction as act "
                    + "left join fetch act.user "
                    + "left join fetch act.accountingDocument as acDoc "
                    + "left join fetch act.oAccountingDocument as oAcDoc "
                    + "left join fetch acDoc.pnr as p "
                    + "left join fetch p.customer as cust "
                    + "left join fetch oAcDoc.customer as cust1 "
                    + "where p.agent is null and oAcDoc.agent is null and "
                    + "(:from is null or act.transDate >= :from) and (:to is null or act.transDate <= :to) "
                    + "and (:tType is null or act.transType = :tType) "
                    + "and (:userId is null or act.user.userId = :userId) "
                    + "and (:contId is null or cust.contactableId = :contId or cust1.contactableId = :contId) "
                    + "and (:acDocId is null or act.accountingDocument.acDocId >= :acDocId) "
                    + "and (:oAcDocId is null or act.oAccountingDocument.oAcDocId >= :oAcDocId) "
                    + "order by act.transID ";
        } else if (contType == 0) {
            hql1 = "select distinct act from AcTransaction as act "
                    + "left join fetch act.user "
                    + "left join fetch act.accountingDocument as acDoc "
                    + "left join fetch act.oAccountingDocument as oAcDoc "
                    + "left join fetch acDoc.pnr as p "
                    + "left join fetch p.customer as cust "
                    + "left join fetch p.agent as agt "
                    + "left join fetch oAcDoc.customer as cust "
                    + "left join fetch oAcDoc.agent as agt "
                    + "where "
                    + "(:from is null or act.transDate >= :from) and (:to is null or act.transDate <= :to) "
                    + "and (:tType is null or act.transType = :tType) "
                    + "and (:userId is null or act.user.userId = :userId) "
                    + "and (:acDocId is null or act.accountingDocument.acDocId != :acDocId) "
                    + "and (:oAcDocId is null or act.oAccountingDocument.oAcDocId != :oAcDocId) "
                    + "order by act.transID ";
        }

        if (!hql.isEmpty()) {
            acts = getHibernateTemplate().findByNamedParam(hql, new String[]{"from", "to", "tType", "userId", "contId","acDocId","oAcDocId"},
                    new Object[]{from, to, tType, userId, contId,acDocId,oAcDocId});
        } else {
            acts = getHibernateTemplate().findByNamedParam(hql1, new String[]{"from", "to", "tType", "userId","acDocId","oAcDocId"},
                    new Object[]{from, to, tType, userId,acDocId,oAcDocId});
        }

        return acts;
    }

    public void deleteAcTransaction(AcTransaction t) {
        getHibernateTemplate().delete(t);
    }

    public List<BatchTransaction> findBatchCollectionsByCriteria(Long contactableId, Date from, Date to) {
        List<BatchTransaction> batchs = new ArrayList();

        String hql = "";

        hql = "select distinct bt from BatchTransaction as bt "
                + "left join fetch bt.consignee as agt "
                + "left join fetch bt.collectionBy as user "
                + "left join fetch bt.acTransactions as act "
                + "left join fetch act.accountingDocument as acDoc "
                + "left join fetch acDoc.accountingDocumentLines as l "
                + "left join fetch l.tickets as t "
                + "left join fetch acDoc.pnr as p "                          
                + "where "
                + "(:from is null or bt.collectionDate >= :from) and (:to is null or bt.collectionDate <= :to) "
                + "and (:contactableId is null or agt.contactableId = :contactableId) "
                + " order by bt.batchId ";

        batchs = getHibernateTemplate().findByNamedParam(hql, new String[]{"contactableId", "from", "to"},
                new Object[]{contactableId, from, to});

        return batchs;
    }

    public List<BatchBillingTransaction> findBPBatchThirdPartyByCriteria(Long contactableId, Date from, Date to) {
        List<BatchBillingTransaction> batchs = new ArrayList();

        String hql = "";

        hql = "select distinct bbt from BatchBillingTransaction as bbt "
                + "left join fetch bbt.vendor as agt "
                + "left join fetch bbt.billingBy as user "
                + "left join fetch bbt.billingTransactions as bt "
                + "left join fetch bt.purchaseAccountingDocument as pAcDoc "
                + "left join fetch pAcDoc.pnr as p "
                + "left join fetch p.tickets as t "
                //+ "left join fetch t.purchaseAccountingDocumentLine "
                //+" left join fetch t.accountingDocumentLine "
                + "where "
                + "(:from is null or bbt.batchBillingDate >= :from) and (:to is null or bbt.batchBillingDate <= :to) "
                + "and (:contactableId is null or agt.contactableId = :contactableId) "
                + "order by bbt.batchBillingId ";

        batchs = getHibernateTemplate().findByNamedParam(hql, new String[]{"contactableId", "from", "to"},
                new Object[]{contactableId, from, to});

        return batchs;
    }

    public void voidAcTransaction(AcTransaction t) {
        getHibernateTemplate().saveOrUpdate(t);
    }

    public void voidBillingTransaction(BillingTransaction bt) {
        getHibernateTemplate().saveOrUpdate(bt);
    }
}
