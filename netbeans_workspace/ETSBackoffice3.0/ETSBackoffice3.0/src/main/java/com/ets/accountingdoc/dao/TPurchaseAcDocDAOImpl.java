package com.ets.accountingdoc.dao;

import com.ets.GenericDAOImpl;
import com.ets.accountingdoc.domain.TicketingPurchaseAcDoc;
import com.ets.pnr.domain.Ticket;
import com.ets.util.Enums;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.hibernate.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Yusuf
 */
@Service("tPurchaseAcDocDAO")
@Transactional
public class TPurchaseAcDocDAOImpl extends GenericDAOImpl<TicketingPurchaseAcDoc, Long> implements TPurchaseAcDocDAO {

    @Override
    @Transactional(readOnly = true)
    public TicketingPurchaseAcDoc getWithChildrenById(Long id) {
        String hql = "select distinct a from TicketingPurchaseAcDoc as a "
                + "left join fetch a.additionalChargeLines as adl "
                + "left join fetch adl.additionalCharge "
                + "left join fetch a.tickets as t "
                + "left join fetch a.pnr as p "
                + "left join fetch p.segments "
                + "left join fetch p.agent "
                + "left join fetch p.customer "
                + "inner join fetch p.ticketing_agent as tktingagent "
                + "left join fetch a.relatedDocuments as a1 "
                + "left join fetch a1.payment as payment "
                + "left join fetch a1.tickets as t1 "
                + "left join fetch a1.additionalChargeLines as adl1 "
                + "left join fetch adl1.additionalCharge "
                + "where a.id = :id";

        Query query = getSession().createQuery(hql);
        query.setParameter("id", id);
        TicketingPurchaseAcDoc doc = (TicketingPurchaseAcDoc) query.uniqueResult();

        for (TicketingPurchaseAcDoc rd : doc.getRelatedDocuments()) {
            rd.setRelatedDocuments(null);
            rd.setParent(null);
            Set<Ticket> tickets = rd.getTickets();
            for (Ticket t : tickets) {
                t.setTicketingPurchaseAcDoc(null);
            }
        }
        return doc;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketingPurchaseAcDoc> getByPnrId(Long pnrId) {
        String hql = "select distinct a from TicketingPurchaseAcDoc as a "
                + "left join fetch a.additionalChargeLines as adl "
                + "left join fetch adl.additionalCharge "
                + "left join fetch a.tickets as t "
                + "left join fetch a.relatedDocuments as a1 "
                + "left join fetch a1.payment as p "
                + "where a.pnr.id = :pnrId order by a.id asc";

        Query query = getSession().createQuery(hql);
        query.setParameter("pnrId", pnrId);
        List<TicketingPurchaseAcDoc> result = query.list();
        return result;
    }

    @Override
    public List<TicketingPurchaseAcDoc> getByGDSPnr(String GdsPnr) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketingPurchaseAcDoc> findOutstandingDocuments(Enums.AcDocType type, Long agentid, Date from, Date to) {

        char operator = '>';

        if (type.equals(Enums.AcDocType.REFUND)) {
            operator = '<';//To get outstanding refund
        } else {
            operator = '>';//To get outstanding invoice
        }

        String hql = "select distinct a from TicketingPurchaseAcDoc as a "
                + "left join fetch a.relatedDocuments as r "
                + "left join fetch a.pnr as p "
                + "left join fetch p.segments "
                + "inner join fetch p.ticketing_agent as tktingagent "
                + "where a.status = 0 and a.type = 0 and "
                + "(select sum(b.documentedAmount) as total "
                + "from TicketingPurchaseAcDoc b "
                + "where a.reference=b.reference and b.status = 0 group by b.reference)" + operator + "0 "
                + "and a.docIssueDate >= :from and a.docIssueDate <= :to "
                + "and (:agentid is null or tktingagent.id = :agentid) order by a.id";

        Query query = getSession().createQuery(hql);

        query.setParameter("from", from);
        query.setParameter("to", to);
        query.setParameter("agentid", agentid);

        List<TicketingPurchaseAcDoc> dueInvoices = query.list();
        return dueInvoices;
    }

    @Override
    public List<TicketingPurchaseAcDoc> findInvoiceHistory(Long agentid, Date from, Date to) {

        String hql = "select distinct a from TicketingPurchaseAcDoc as a "
                + "left join fetch a.relatedDocuments as r "
                + "left join fetch a.pnr as p "
                + "left join fetch p.segments "
                + "inner join fetch p.ticketing_agent as tktingagent "
                + "where a.status = 0 and a.type = 0 and "       
                + "a.docIssueDate >= :from and a.docIssueDate <= :to "
                + "and (:agentid is null or tktingagent.id = :agentid) order by a.id";

        Query query = getSession().createQuery(hql);

        query.setParameter("from", from);
        query.setParameter("to", to);
        query.setParameter("agentid", agentid);
        List<TicketingPurchaseAcDoc> dueInvoices = query.list();
        return dueInvoices;
    }

    @Override
    public TicketingPurchaseAcDoc getByTicketId(Long ticketId) {
        String hql = "select distinct a from TicketingPurchaseAcDoc as a "
                + "left join fetch a.additionalChargeLines as adl "
                + "left join fetch adl.additionalCharge "
                + "left join a.tickets as t "
                + "left join fetch a.relatedDocuments as a1 "
                + "left join fetch a1.payment as p "
                + "where t.id = :ticketId";

        Query query = getSession().createQuery(hql);
        query.setParameter("ticketId", ticketId);
        TicketingPurchaseAcDoc result = (TicketingPurchaseAcDoc) query.uniqueResult();
        //getSession().evict(result);
        return result;
    }
}
