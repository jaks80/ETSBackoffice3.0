package com.ets.accountingdoc.dao;

import com.ets.GenericDAOImpl;
import com.ets.accountingdoc.domain.TicketingSalesAcDoc;
import com.ets.pnr.domain.Itinerary;
import com.ets.pnr.domain.Pnr;
import com.ets.pnr.domain.Ticket;
import com.ets.util.Enums;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.hibernate.Query;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ,./
 *
 * @author Yusuf
 */
@Service("tSalesAcDocDAO")
@Transactional
public class TSalesAcDocDAOImpl extends GenericDAOImpl<TicketingSalesAcDoc, Long> implements TSalesAcDocDAO {

    @Override
    public Long getNewAcDocRef() {
        String hql = "select max(acDoc.reference) from TicketingSalesAcDoc acDoc";
        Query query = getSession().createQuery(hql);

        Object result = DataAccessUtils.uniqueResult(query.list());
        return (result != null) ? Long.valueOf(result.toString()) : null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketingSalesAcDoc> getByPnrId(Long pnrId) {
        String hql = "select distinct a from TicketingSalesAcDoc as a "
                + "left join fetch a.accountingDocumentLines as adl "
                + "left join fetch adl.otherService "
                + "left join fetch adl.additionalCharge "
                + "left join fetch a.tickets as t "
                + "left join fetch a.relatedDocuments as a1 "
                + "left join fetch a1.payment as p "
                + "where a.pnr.id = :pnrId order by a.id asc";

        Query query = getSession().createQuery(hql);
        query.setParameter("pnrId", pnrId);
        List<TicketingSalesAcDoc> result = query.list();
        return result;
    }

    @Override
    public List<TicketingSalesAcDoc> getByGDSPnr(String GdsPnr) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    @Transactional(readOnly = true)
    public TicketingSalesAcDoc getWithChildrenById(Long id) {

        String hql = "select distinct a from TicketingSalesAcDoc as a "
                + "left join fetch a.accountingDocumentLines as adl "
                + "left join fetch adl.otherService "
                + "left join fetch adl.additionalCharge "
                + "left join fetch a.tickets as t "
                + "left join fetch a.pnr as p "
                + "left join fetch p.segments "
                + "left join fetch p.agent "
                + "left join fetch p.customer "
                + "left join fetch a.relatedDocuments as a1 "
                + "left join fetch a1.payment as payment "
                + "left join fetch a1.tickets as t1 "
                + "left join fetch a1.accountingDocumentLines as adl1 "
                + "left join fetch adl1.otherService "
                + "left join fetch adl1.additionalCharge "
                + "where a.id = :id";

        Query query = getSession().createQuery(hql);
        query.setParameter("id", id);
        TicketingSalesAcDoc doc = (TicketingSalesAcDoc) query.uniqueResult();

        for (TicketingSalesAcDoc rd : doc.getRelatedDocuments()) {
            rd.setRelatedDocuments(null);
            rd.setParent(null);
            Set<Ticket> tickets = rd.getTickets();
            for (Ticket t : tickets) {
                t.setTicketingSalesAcDoc(null);
            }
        }
        return doc;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketingSalesAcDoc> findOutstandingDocuments(Enums.AcDocType type, Enums.ClientType clienttype, Long clientid, Date from, Date to) {

        String concatClient = "";
        String clientcondition = "and (:clientid is null or client.id = :clientid) ";
        char operator = '>';

        if (type.equals(Enums.AcDocType.REFUND)) {
            operator = '<';//To get outstanding refund
        } else {
            operator = '>';//To get outstanding invoice
        }

        if (clienttype != null && clienttype.equals("Agent")) {
            concatClient = "inner join fetch p.agent as client ";
        } else if (clienttype != null && clienttype.equals("Customer")) {
            concatClient = "inner join fetch p.customer as client ";
        } else {
            concatClient = "left join fetch p.agent left join fetch p.customer ";
            clientcondition = "";
        }

        String hql = "select distinct a from TicketingSalesAcDoc as a "
                + "left join fetch a.relatedDocuments as r "
                + "left join fetch a.pnr as p "
                + "left join fetch p.segments "
                + concatClient
                + "where a.status = 0 and a.type = 0 and "
                + "(select sum(b.documentedAmount) as total "
                + "from TicketingSalesAcDoc b "
                + "where a.reference=b.reference and b.status = 0 group by b.reference)" + operator + "0 "
                + "and a.docIssueDate >= :from and a.docIssueDate <= :to "
                + clientcondition;

        Query query = getSession().createQuery(hql);
        if (!clientcondition.isEmpty()) {
            query.setParameter("clientid", clientid);
        }
        query.setParameter("from", from);
        query.setParameter("to", to);

        List<TicketingSalesAcDoc> dueInvoices = query.list();
        return dueInvoices;
    }

    @Override
    public List<TicketingSalesAcDoc> findInvoiceHistory(Enums.ClientType clienttype, Long clientid, Date from, Date to) {
        String concatClient = "";
        String clientcondition = "and (:clientid is null or client.id = :clientid) ";

        if (clienttype != null && clienttype.equals("Agent")) {
            concatClient = "inner join fetch p.agent as client ";
        } else if (clienttype != null && clienttype.equals("Customer")) {
            concatClient = "inner join fetch p.customer as client ";
        } else {
            concatClient = "left join fetch p.agent left join fetch p.customer ";
            clientcondition = "";
        }

        String hql = "select distinct a from TicketingSalesAcDoc as a "
                + "left join fetch a.relatedDocuments as r "
                + "left join fetch a.pnr as p "
                + "left join fetch p.segments "
                + concatClient
                + "where a.status = 0 and a.type = 0 "
                + "and a.docIssueDate >= :from and a.docIssueDate <= :to "
                + clientcondition;

        Query query = getSession().createQuery(hql);
        if (!clientcondition.isEmpty()) {
            query.setParameter("clientid", clientid);
        }
        query.setParameter("from", from);
        query.setParameter("to", to);

        List<TicketingSalesAcDoc> invoice_history = query.list();
        return invoice_history;
    }
}
