package com.ets.dao.pnr;

import com.ets.dao.generic.GenericDAOImpl;
import com.ets.domain.pnr.Ticket;
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
    public List<Ticket> saleReport(Integer tktStatus, String[] airLineCode, Date from, Date to, String... ticketingAgtOid) {

        String hql = "select t from Ticket as t "
                + "inner join fetch t.pnr as p "
                + "where (t.tktStatus <> 1) and "
                + "(:tktStatus is null or t.tktStatus = :tktStatus) and "
                + "(:airLineCode is null or p.airLineCode = :airLineCode) and "
                + "(:ticketingAgtOid is null or p.ticketingAgtOid = :ticketingAgtOid) and "
                + "(:from is null or t.docIssuedate >= :from) and "
                + "(:to is null or t.docIssuedate <= :to) group by t";

        Query query = getSession().createQuery(hql);
        query.setParameter("tktStatus", tktStatus);
        if (airLineCode == null) {
            query.setParameter("airLineCode", airLineCode);
        } else {
            query.setParameterList("airLineCode", airLineCode);
        }

        query.setParameter("from", from);
        query.setParameter("to", to);
        if (ticketingAgtOid == null) {
            query.setParameter("ticketingAgtOid", ticketingAgtOid);
        } else {
            query.setParameterList("ticketingAgtOid", ticketingAgtOid);
        }
        List result = query.list();
        return result;
    }

    @Override
    public List<Ticket> saleReportDetails(int ticketingType, Integer gdsId, Long tktingAgtID, Integer tktStatus, String career, Date from, Date to, String bookingOid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
