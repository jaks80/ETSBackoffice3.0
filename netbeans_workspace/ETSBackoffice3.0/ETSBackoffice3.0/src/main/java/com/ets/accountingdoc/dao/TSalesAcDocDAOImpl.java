package com.ets.accountingdoc.dao;

import com.ets.GenericDAOImpl;
import com.ets.accountingdoc.domain.*;
import com.ets.pnr.dao.TicketDAO;
import com.ets.pnr.domain.Ticket;
import com.ets.settings.domain.User;
import com.ets.util.Enums;
import java.math.BigDecimal;
import java.util.*;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private TicketDAO ticketDAO;
    @Autowired
    private AdditionalChgLineDAO additionalChgLineDAO;
    @Autowired
    private TPurchaseAcDocDAO tPurchaseAcDocDAO;

    @Override
    public Long getNewAcDocRef() {
        String hql = "select max(acDoc.reference) from TicketingSalesAcDoc acDoc";
        Query query = getSession().createQuery(hql);

        Object result = DataAccessUtils.uniqueResult(query.list());
        return (result != null) ? Long.valueOf(result.toString()) : null;
    }

    @Override
    public List<TicketingSalesAcDoc> findAllById(Long... id) {
        String hql = "select distinct a from TicketingSalesAcDoc as a "
                + "left join fetch a.additionalChargeLines as acl "
                + "left join fetch a.pnr "
                + "left join fetch a.relatedDocuments as a1 "
                + "left join fetch a1.additionalChargeLines as acl1 "
                + "where a.type = 0 and a.id in (:id)";

        Query query = getSession().createQuery(hql);
        query.setParameterList("id", id);
        List<TicketingSalesAcDoc> result = query.list();
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketingSalesAcDoc> getByPnrId(Long pnrId) {
        String hql = "select distinct a from TicketingSalesAcDoc as a "
                + "left join fetch a.additionalChargeLines as acl "
                + "left join fetch acl.additionalCharge "
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
                + "left join fetch a.additionalChargeLines as acl "
                + "left join fetch acl.additionalCharge "
                + "left join fetch a.tickets as t "
                + "left join fetch a.pnr as p "
                + "left join fetch p.segments "
                + "left join fetch p.agent "
                + "left join fetch p.customer "
                + "left join fetch a.relatedDocuments as a1 "
                + "left join fetch a1.payment as payment "
                + "left join fetch a1.tickets as t1 "
                + "left join fetch a1.additionalChargeLines as acl1 "
                + "left join fetch acl1.additionalCharge "
                + "where a.id = :id";

        Query query = getSession().createQuery(hql);
        query.setParameter("id", id);
        TicketingSalesAcDoc doc = (TicketingSalesAcDoc) query.uniqueResult();
        Set<TicketingSalesAcDoc> related_docs = doc.getRelatedDocuments();

        for (TicketingSalesAcDoc rd : related_docs) {
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
    public List<TicketingSalesAcDoc> findOutstandingDocuments(Enums.AcDocType type, Enums.ClientType clienttype,
            Long clientid, Date from, Date to) {

        String concatClient = "";
        String dateCondition = "";
        String clientcondition = "and (:clientid is null or client.id = :clientid) ";
        char operator = '>';

        if (from != null && to != null) {
            dateCondition = "and a.docIssueDate >= :from and a.docIssueDate <= :to ";
        }

        if (type.equals(Enums.AcDocType.REFUND)) {
            operator = '<';//To get outstanding refund
        } else {
            operator = '>';//To get outstanding invoice
        }

        if (clienttype != null && clienttype.equals(Enums.ClientType.AGENT)) {
            concatClient = "inner join fetch p.agent as client ";
        } else if (clienttype != null && clienttype.equals(Enums.ClientType.CUSTOMER)) {
            concatClient = "inner join fetch p.customer as client ";
        } else {
            concatClient = "left join fetch p.agent left join fetch p.customer ";
            clientcondition = "";
        }

        String hql = "select distinct a from TicketingSalesAcDoc as a "
                + "left join fetch a.relatedDocuments as r "
                + "left join fetch a.tickets as t "
                + "left join fetch a.pnr as p "
                + "left join fetch p.segments "
                + concatClient
                + "where a.status = 0 and a.type = 0 and "
                + "(select sum(b.documentedAmount) as total "
                + "from TicketingSalesAcDoc b "
                + "where a.reference=b.reference and b.status = 0 group by b.reference)" + operator + "0 "
                + dateCondition
                + clientcondition
                + " order by a.id";

        Query query = getSession().createQuery(hql);
        if (!clientcondition.isEmpty()) {
            query.setParameter("clientid", clientid);
        }

        if (from != null && to != null) {
            query.setParameter("from", from);
            query.setParameter("to", to);
        }

        List<TicketingSalesAcDoc> dueInvoices = query.list();
        return dueInvoices;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketingSalesAcDoc> outstandingFlightReport(Enums.ClientType clienttype,
            Long clientid, Date dateEnd) {
        String concatClient = "";
        String clientcondition = "and (:clientid is null or client.id = :clientid) ";

        if (clienttype != null && clienttype.equals(Enums.ClientType.AGENT)) {
            concatClient = "inner join fetch p.agent as client ";
        } else if (clienttype != null && clienttype.equals(Enums.ClientType.CUSTOMER)) {
            concatClient = "inner join fetch p.customer as client ";
        } else {
            concatClient = "left join fetch p.agent left join fetch p.customer ";
            clientcondition = "";
        }

        String hql = "select a from TicketingSalesAcDoc as a "
                + "left join fetch a.relatedDocuments as r "
                + "left join fetch a.tickets as t "
                + "left join fetch a.pnr as p "
                + "inner join fetch p.segments as seg "
                + concatClient
                + "where a.status = 0 and a.type = 0 and "
                + "(select sum(b.documentedAmount) as total "
                + "from TicketingSalesAcDoc b "
                + "where a.reference=b.reference and b.status = 0 group by b.reference) > 0 "
                + "and seg.deptDate <= :dateEnd "
                + clientcondition
                + "group by p.id order by seg.deptDate asc";

        Query query = getSession().createQuery(hql);
        if (!clientcondition.isEmpty()) {
            query.setParameter("clientid", clientid);
        }
        query.setParameter("dateEnd", dateEnd);

        List<TicketingSalesAcDoc> dueInvoices = query.list();
        return dueInvoices;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketingSalesAcDoc> findInvoiceHistory(Enums.ClientType clienttype, Long clientid, Date from, Date to) {
        String concatClient = "";
        String clientcondition = "and (:clientid is null or client.id = :clientid) ";

        if (clienttype != null && clienttype.equals(Enums.ClientType.AGENT)) {
            concatClient = "inner join fetch p.agent as client ";
        } else if (clienttype != null && clienttype.equals(Enums.ClientType.CUSTOMER)) {
            concatClient = "inner join fetch p.customer as client ";
        } else {
            concatClient = "left join fetch p.agent left join fetch p.customer ";
            clientcondition = "";
        }

        String hql = "select distinct a from TicketingSalesAcDoc as a "
                + "left join fetch a.tickets as t "
                + "left join fetch a.relatedDocuments as r "
                + "left join fetch a.pnr as p "
                + "left join fetch p.segments "
                + concatClient
                + "where a.status = 0 and a.type = 0 "
                + "and a.docIssueDate >= :from and a.docIssueDate <= :to "
                + clientcondition
                + " order by a.id";

        Query query = getSession().createQuery(hql);
        if (!clientcondition.isEmpty()) {
            query.setParameter("clientid", clientid);
        }
        query.setParameter("from", from);
        query.setParameter("to", to);

        List<TicketingSalesAcDoc> invoice_history = query.list();
        return invoice_history;
    }

    @Override
    public TicketingSalesAcDoc voidSimpleDocument(TicketingSalesAcDoc doc) {
        doc.setStatus(Enums.AcDocStatus.VOID);
        save(doc);  
        return doc;
    }

    @Override
    public TicketingSalesAcDoc voidTicketedDocument(TicketingSalesAcDoc doc) {
        Set<Ticket> tickets = doc.getTickets();

        TicketingPurchaseAcDoc purchaseDoc = null;
        if (!tickets.isEmpty()) {
            purchaseDoc = tPurchaseAcDocDAO.getByTicketId(tickets.iterator().next().getId());
            purchaseDoc.setTickets(null);
        }

        for (Ticket t : tickets) {
            t.setTicketingSalesAcDoc(null);
            t.setTicketingPurchaseAcDoc(null);
        }

        if (!tickets.isEmpty()) {
            ticketDAO.saveBulk(new ArrayList(tickets));
        }

        Set<AdditionalChargeLine> additionalChargeLines = doc.getAdditionalChargeLines();

        if (!additionalChargeLines.isEmpty()) {
            additionalChgLineDAO.deleteBulk(additionalChargeLines);
            doc.setAdditionalChargeLines(null);
        }
        doc.setStatus(Enums.AcDocStatus.VOID);
        save(doc);
        if (purchaseDoc != null) {
            tPurchaseAcDocDAO.delete(purchaseDoc);
        }
        return doc;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketingSalesAcDoc> findAllDocuments(Enums.ClientType clienttype, Long clientid, Date from, Date to) {
        String concatClient = "";
        String clientcondition = "and (:clientid is null or client.id = :clientid) ";

        if (clienttype != null && clienttype.equals(Enums.ClientType.AGENT)) {
            concatClient = "left join fetch p.agent as client ";
        } else if (clienttype != null && clienttype.equals(Enums.ClientType.CUSTOMER)) {
            concatClient = "left join fetch p.customer as client ";
        } else {
            concatClient = "left join fetch p.agent left join fetch p.customer ";
            clientcondition = "";
        }

        String hql = "select distinct a from TicketingSalesAcDoc as a "
                + "left join fetch a.payment as payment "
                + "left join fetch a.pnr as p "
                + "left join fetch p.segments "
                + concatClient
                + "where a.status <> 2 "
                + "and a.docIssueDate >= :from and a.docIssueDate <= :to "
                + clientcondition
                + " order by a.docIssueDate asc";

        Query query = getSession().createQuery(hql);
        if (!clientcondition.isEmpty()) {
            query.setParameter("clientid", clientid);
        }
        query.setParameter("from", from);
        query.setParameter("to", to);

        List<TicketingSalesAcDoc> history = query.list();
        return history;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getAccountBallanceToDate(Enums.ClientType clienttype, Long clientid, Date to) {

        String concatClient = "";
        String clientcondition = "and (:clientid is null or client.id = :clientid) ";

        if (clienttype != null && clienttype.equals(Enums.ClientType.AGENT)) {
            concatClient = "inner join p.agent as client ";
        } else if (clienttype != null && clienttype.equals(Enums.ClientType.CUSTOMER)) {
            concatClient = "inner join p.customer as client ";
        } else {
            concatClient = "left join p.agent left join p.customer ";
            clientcondition = "";
        }

        String hql = "select coalesce(sum(a.documentedAmount),0) as balance "
                + "from TicketingSalesAcDoc a "
                + "left join a.pnr as p "
                + concatClient
                + "where a.status <> 2 and "
                + "(:clientid is null or client.id = :clientid) "
                + "and a.docIssueDate <= :to "
                + clientcondition;

        Query query = getSession().createQuery(hql);
        if (!clientcondition.isEmpty()) {
            query.setParameter("clientid", clientid);
        }

        query.setParameter("to", to);

        Object balance = query.uniqueResult();
        return new BigDecimal(balance.toString());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<User, BigDecimal> userProductivityReport(Date from, Date to) {
        String hql = "select user.surName, user.foreName, user.loginID, "
                + "coalesce(sum(a.documentedAmount),0) as balance "
                + "from TicketingSalesAcDoc a "
                + "left join a.createdBy as user "
                + "where a.status <> 2 and a.type <> 1 and a.type <> 4 and user.isActive = true "
                + "and a.docIssueDate >= :from and a.docIssueDate <= :to "
                + "group by user.id order by balance desc ";

        Query query = getSession().createQuery(hql);
        query.setParameter("from", from);
        query.setParameter("to", to);

        List results = query.list();
        Map<User, BigDecimal> map = new LinkedHashMap<>();

        Iterator it = results.iterator();
        while (it.hasNext()) {
            Object[] objects = (Object[]) it.next();
            User user = new User();
            user.setSurName((String) objects[0]);
            user.setForeName((String) objects[1]);
            user.setLoginID((String) objects[2]);
            map.put(user, (BigDecimal) objects[3]);
        }
        return map;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> agentOutstandingReport(Date from, Date to) {
        String hql = "select agent.name, coalesce(sum(a.documentedAmount),0) as balance "
                + "from TicketingSalesAcDoc a "
                + "left join a.pnr as p "
                + "inner join p.agent as agent "
                + "where a.status = 0 "
                + "and a.docIssueDate >= :from and a.docIssueDate <= :to "
                + "group by agent.id order by balance desc ";

        Query query = getSession().createQuery(hql);
        query.setParameter("from", from);
        query.setParameter("to", to);

        List results = query.list();
        Map<String, BigDecimal> map = new LinkedHashMap<>();

        Iterator it = results.iterator();
        while (it.hasNext()) {
            Object[] objects = (Object[]) it.next();
            map.put((String) objects[0], (BigDecimal) objects[1]);
        }
        return map;
    }
}
