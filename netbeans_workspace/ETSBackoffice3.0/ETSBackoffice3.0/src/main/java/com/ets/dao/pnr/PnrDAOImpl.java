package com.ets.dao.pnr;

import com.ets.dao.generic.GenericDAOImpl;
import com.ets.domain.pnr.Pnr;
import java.util.Date;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.type.DateType;
import org.hibernate.type.StringType;
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
    public List<Pnr> find(Date from, Date to, String ticketingAgtOid, String bookingAgtOid) {
        String hql = "select distinct p from Pnr p "          
                + "left join fetch p.tickets as t "
                + "left join fetch p.segments as s "
                + "left join fetch p.remarks as r "
                + "where "                
                + "(:tAgtOid is null or p.ticketingAgtOid = :tAgtOid) and "
                + "(:bAgtOid is null or p.bookingAgtOid = :bAgtOid) and "
                + "(p.airCreationDate >= :from) and (p.airCreationDate <= :to)";
        
        Query query = getSession().createQuery(hql);        
        query.setParameter("from", from);
        query.setParameter("to", to);
        query.setParameter("tAgtOid", ticketingAgtOid);
        query.setParameter("bAgtOid", bookingAgtOid);
                
        return query.list();                
    }

}
