package etsbackoffice.datalogic;

import etsbackoffice.domain.Itinerary;
import java.util.Date;
import java.util.List;
import java.util.Set;


/**
 *
 * @author Yusuf
 */
public interface ItineraryDao {
    
    public void saveSegment(Itinerary s);
    
    public void removeItinerary1(final long pnrID);

    public void removeSegment(Itinerary segment);

    public List loadItinerary(long pnrId);

    public void removeItinerary(Set<Itinerary> segments);

    public void updateSegments(Set<Itinerary> segments);
    
    public List<Itinerary> findSegments(Integer status,Integer gdsId,String oid,String airLineID,Date from, Date to);
}
