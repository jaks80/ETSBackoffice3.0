package com.ets.pnr.dao;

import com.ets.GenericDAOImpl;
import com.ets.pnr.domain.Ticket;
import com.ets.util.Enums;
import java.util.Date;
import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Yusuf
 */
@Service("ticketDAO")
@Transactional
public class TicketDAOImpl extends GenericDAOImpl<Ticket, Long> implements TicketDAO{

    @Override
    public List<Ticket> saleReport(Integer ticketStatus, String[] airLineCode, Date from, Date to, String... ticketingAgtOid) {

        String airLineCodeQuery="";
        String ticketingAgtOidQuery = "";

        if (airLineCode != null) {
         airLineCodeQuery="p.airLineCode in (:airLineCode) and ";
        }
        if (ticketingAgtOid != null) {
            ticketingAgtOidQuery = "p.ticketingAgtOid in (:ticketingAgtOid) and ";
        }
        String hql = "select t from Ticket as t "
                + "inner join fetch t.pnr as p "
                + "where (t.tktStatus <> 0) and "                
                +airLineCodeQuery + ticketingAgtOidQuery
                + "t.docIssuedate >= :from and t.docIssuedate <= :to and "
                + "(:ticketStatus is null or t.tktStatus = :ticketStatus) "
                + "group by t";

        Query query = getSession().createQuery(hql);
        query.setParameter("ticketStatus", ticketStatus);

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

}
