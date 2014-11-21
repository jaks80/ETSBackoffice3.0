package com.ets.dao.pnr;

import com.ets.dao.generic.GenericDAOImpl;
import com.ets.domain.pnr.Itinerary;
import java.util.Date;
import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Yusuf
 */
@Service("itineraryDAO")
@Transactional
public class ItineraryDAOImpl  extends GenericDAOImpl<Itinerary, Long> implements ItineraryDAO {
    
     public List findSegments(Integer tktStatus,
             String[] airLineID, Date from, Date to,String... ticketingAgtOid) {
     
         String hql = "from Itinerary as i, Ticket as t "
                 + "left join fetch t.pnr as p "
                 + "where i.pnr.id = t.pnr.id and "
                 + "(:from is null or t.docIssuedate >= :from) and "
                 + "(:to is null or t.docIssuedate <= :to) and"
                 + "(:tktStatus is null or t.tktStatus = :tktStatus) and "
                 + "(:ticketingAgtOid is null or p.ticketingAgtOid = :ticketingAgtOid) and "
                 + "(:airLineID is null or i.airLineID = :airLineID)";
         
         Query query = getSession().createQuery(hql);        
         query.setParameter("tktStatus", tktStatus);
        if (airLineID == null) {
            query.setParameter("airLineID", airLineID);
        } else {
            query.setParameterList("airLineID", airLineID);
        }

        query.setParameter("from", from);
        query.setParameter("to", to);
        if (ticketingAgtOid == null) {
            query.setParameter("ticketingAgtOid", ticketingAgtOid);
        } else {
            query.setParameterList("ticketingAgtOid", ticketingAgtOid);
        }
         List results = query.list();                 
         
         return results;
     }
}
