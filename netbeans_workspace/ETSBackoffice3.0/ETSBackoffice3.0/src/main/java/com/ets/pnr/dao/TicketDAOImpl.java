package com.ets.pnr.dao;

import com.ets.GenericDAOImpl;
import com.ets.accountingdoc.dao.TPurchaseAcDocDAO;
import com.ets.accountingdoc.domain.TicketingPurchaseAcDoc;
import com.ets.pnr.domain.Ticket;
import com.ets.util.Enums;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.hibernate.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Yusuf
 */
@Service("ticketDAO")
@Transactional
public class TicketDAOImpl extends GenericDAOImpl<Ticket, Long> implements TicketDAO {

    @Resource(name = "tPurchaseAcDocDAO")
    private TPurchaseAcDocDAO tPurchaseAcDocDAO;
    
    @Override
    public Ticket findTicket(String pnr, String ticketNo, String surName) {

        String hql = "select distinct t from Ticket as t "
                + "left join fetch t.ticketingPurchaseAcDoc acdoc "
                + "left join fetch t.pnr as p "
                + "where t.ticketNo =:ticketNo and t.surName =:surName and p.gdsPnr=:pnr ";

        Query query = getSession().createQuery(hql);
        query.setParameter("surName", surName);
        query.setParameter("ticketNo", ticketNo);
        query.setParameter("pnr", pnr);

        List<Ticket> tickets = query.list();
        return tickets.get(0);
    }

    @Override
    public int voidTicket(String pnr, String airlineCode, String ticketNo, String surName) {
        Ticket ticket = findTicket(pnr, ticketNo, surName);        
        ticket.setBaseFare(new BigDecimal("0.00"));
        ticket.setTax(new BigDecimal("0.00"));
        ticket.setFee(new BigDecimal("0.00"));
        ticket.setCommission(new BigDecimal("0.00"));        
        ticket.setGrossFare(new BigDecimal("0.00"));
        ticket.setAtolChg(new BigDecimal("0.00"));
        ticket.setDiscount(new BigDecimal("0.00"));
        ticket.setTktStatus(Enums.TicketStatus.VOID);
        save(ticket);
        
        if(ticket.getTicketingPurchaseAcDoc()!=null){
         TicketingPurchaseAcDoc doc = tPurchaseAcDocDAO.getWithChildrenById(ticket.getTicketingPurchaseAcDoc().getId());
         doc.setDocumentedAmount(doc.calculateDocumentedAmount());
         tPurchaseAcDocDAO.save(doc);
        }
        return 1;
    }

    @Override
    public List<Ticket> saleReport(Enums.TicketingType ticketingType, Enums.TicketStatus ticketStatus, String[] airLineCode, Date from, Date to, String... ticketingAgtOid) {

        String airLineCodeQuery = "";
        String ticketingAgtOidQuery = "";

        if (airLineCode != null) {
            airLineCodeQuery = "p.airLineCode in (:airLineCode) and ";
        }
        if (ticketingAgtOid != null) {
            ticketingAgtOidQuery = "p.ticketingAgtOid in (:ticketingAgtOid) and ";
        }
        String hql = "select t from Ticket as t "
                + "inner join fetch t.pnr as p "
                + "where (t.tktStatus <> 0) and "
                + airLineCodeQuery + ticketingAgtOidQuery
                + "t.docIssuedate >= :from and t.docIssuedate <= :to and "
                + "(:ticketStatus is null or t.tktStatus = :ticketStatus) "
                + "group by t";

        Query query = getSession().createQuery(hql);
        if (ticketStatus != null) {
            query.setParameter("ticketStatus", ticketStatus.getId());
        } else {
            query.setParameter("ticketStatus", null);
        }
        if (airLineCode != null) {
            query.setParameterList("airLineCode", airLineCode);
        }

        query.setParameter("from", from);
        query.setParameter("to", to);

        if (ticketingAgtOid != null) {
            query.setParameterList("ticketingAgtOid", ticketingAgtOid);
        }

        List result = query.list();
        return result;
    }

    @Override
    public List<Ticket> saleReportDetails(int ticketingType, Integer gdsId, Long tktingAgtID, Enums.TicketStatus tktStatus, String career, Date from, Date to, String bookingOid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Ticket> saleRevenueReport(Enums.TicketingType ticketingType, Enums.TicketStatus ticketStatus, String[] airLineCode, Date from, Date to, String... ticketingAgtOid) {
        String airLineCodeQuery = "";
        String ticketingAgtOidQuery = "";

        if (airLineCode != null) {
            airLineCodeQuery = "p.airLineCode in (:airLineCode) and ";
        }
        if (ticketingAgtOid != null) {
            ticketingAgtOidQuery = "p.ticketingAgtOid in (:ticketingAgtOid) and ";
        }
        String hql = "select distinct t from Ticket as t "
                + "left join fetch t.pnr as p "
                + "left join fetch p.agent as agt "
                + "left join fetch p.customer as cust "
                + "left join fetch p.ticketing_agent as vendor "
                + "left join fetch t.ticketingSalesAcDoc "
                + "left join fetch t.ticketingPurchaseAcDoc "
                + "where (t.tktStatus <> 0) and "
                + airLineCodeQuery + ticketingAgtOidQuery
                + "t.docIssuedate >= :from and t.docIssuedate <= :to and "
                + "(:ticketStatus is null or t.tktStatus = :ticketStatus) "
                + "order by t.id";

        Query query = getSession().createQuery(hql);
        if (ticketStatus != null) {
            query.setParameter("ticketStatus", ticketStatus.getId());
        } else {
            query.setParameter("ticketStatus", null);
        }

        if (airLineCode != null) {
            query.setParameterList("airLineCode", airLineCode);
        }

        query.setParameter("from", from);
        query.setParameter("to", to);

        if (ticketingAgtOid != null) {
            query.setParameterList("ticketingAgtOid", ticketingAgtOid);
        }

        List result = query.list();
        return result;
    }

}
