package com.ets.pnr.dao;

import com.ets.GenericDAOImpl;
import com.ets.pnr.domain.Pnr;
import java.util.Date;
import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Yusuf
 */
@Service("pnrDAO")
@Transactional
public class PnrDAOImpl extends GenericDAOImpl<Pnr, Long> implements PnrDAO {

    @Override
    public List<Pnr> find(String gdsPnr) {
        String hql = "from Pnr as p "
                + "left join fetch p.servicingCareer "
                + "left join p.tickets as t "
                + "where p.gdsPNR = ? "
                + "and t.passengerNo = (select min(t.passengerNo) from Ticket t where t.pnr.id = p.id) group by p.id";

        Query query = getSession().createQuery(hql);
        query.setParameter("gdsPnr", gdsPnr);
        return query.list();
    }

    @Override
    public List<Pnr> bookedPnrs() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Pnr> find(Date from, Date to, String[] ticketingAgtOid, String[] bookingAgtOid) {
        
        String bookingAgtOidQuery = "";
        String ticketingAgtOidQuery = "";

        if (bookingAgtOid != null) {
            bookingAgtOidQuery = "p.bookingAgtOid in (:bookingAgtOid) and ";
        }
        if (ticketingAgtOid != null) {
            ticketingAgtOidQuery = "p.ticketingAgtOid in (:ticketingAgtOid) and ";
        }
        
        String hql = "select distinct p from Pnr p "          
                + "left join fetch p.tickets as t "
                + "left join fetch p.segments as s "
                + "left join fetch p.remarks as r "
                + "where "                
                +bookingAgtOidQuery + ticketingAgtOidQuery
                + "(p.airCreationDate >= :from) and (p.airCreationDate <= :to)";
        
        Query query = getSession().createQuery(hql);        
        query.setParameter("from", from);
        query.setParameter("to", to);
        
        if (bookingAgtOid != null) {
            query.setParameterList("bookingAgtOid", bookingAgtOid);
        }

        if (ticketingAgtOid != null) {
            query.setParameterList("ticketingAgtOid", ticketingAgtOid);
        }
                
        return query.list();                
    }
}
