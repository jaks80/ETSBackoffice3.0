package etsbackoffice.datalogic;

import etsbackoffice.domain.Itinerary;
import etsbackoffice.domain.Ticket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Yusuf
 */
public class HibernateItineraryDao extends HibernateDaoSupport implements ItineraryDao {

    public void removeItinerary1(final long pnrID) {
        getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session session) throws HibernateException {
                String hqlDelete = "delete from Itinerary it where it.pnr.pnrId = ?";
                session.createQuery(hqlDelete).setLong(0, pnrID).executeUpdate();
                return null;
            }
        });
    }
    @Transactional
    public void removeItinerary(Set<Itinerary> segments){
    getHibernateTemplate().deleteAll(segments);
    }

    @Transactional
    public void removeSegment(Itinerary segment){
    getHibernateTemplate().delete(segment);
    }

    public void updateSegments(Set<Itinerary> segments) {
       getHibernateTemplate().saveOrUpdateAll(segments);
    }

    public void collectSegments(Set<Itinerary> segments) {
       getHibernateTemplate().find(null);
    }

     public List loadItinerary(long pnrId){
      Set<Itinerary> segments = new LinkedHashSet<Itinerary>();
      return getHibernateTemplate().find("from Itinerary as it where it.pnr.pnrId = ?",pnrId);
     }

    public void saveSegment(Itinerary s) {
        getHibernateTemplate().saveOrUpdate(s);
    }

    public List<Itinerary> findSegments(Integer tktStatus, Integer gdsId, String oid, String airLineID, Date from, Date to) {
        List<Itinerary> segments = new ArrayList();
        
        String hql = "select distinct i,t from Itinerary i "
                + "inner join i.tickets as t "
                + "left join fetch i.pnr as p "
                + "where (t.tktStatus <> 1) and "
                + "(:gdsId is null or p.gds.gdsId = :gdsId) and "
                + "(:tktStatus is null or t.tktStatus = :tktStatus) and "
                + "(:oid is null or p.ticketingAgtOID = :oid) and "
                + "(:airLineID is null or i.airLineID = :airLineID) and "
                + "(:from is null or t.docIssuedate >= :from) and "
                + "(:to is null or t.docIssuedate <= :to) order by t.ticketId";        
        
        List results = getHibernateTemplate().findByNamedParam(hql, new String[]{"tktStatus", "gdsId", "oid", "airLineID", "from", "to"},
                new Object[]{tktStatus, gdsId, oid, airLineID, from, to});
        Iterator it = results.iterator();
        
        for (int i = 0; i < results.size(); i++) {
            Object[] o = (Object[]) results.get(i);
            
            Ticket t = (Ticket) o[1];
            Itinerary oldSeg = (Itinerary) o[0];
            Itinerary s = new Itinerary();
            s = oldSeg.copyValue(s);
            
            Set<Ticket> ts = new HashSet();
            ts.add(t);
            s.setTickets(ts);
            segments.add(s);
        }
        return segments;
    }
}
