package etsbackoffice.businesslogic;

import etsbackoffice.datalogic.ItineraryDao;
import etsbackoffice.domain.Itinerary;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public class ItineraryBo {

    private ItineraryDao itineraryDao;
    private Itinerary itinerary;
    private List<Itinerary> segments;

    public ItineraryBo() {
    }

    public void save(Itinerary s) {
        itineraryDao.saveSegment(s);
    }

    public void removeItinerary(Itinerary segment) {
        itineraryDao.removeSegment(segment);
    }

    public void setItineraryDao(ItineraryDao itineraryDao) {
        this.itineraryDao = itineraryDao;
    }

    public void setItinerary(Itinerary itinerary) {
        this.itinerary = itinerary;
    }

    public void loadItinerary(long pnrId) {
        setSegments(itineraryDao.loadItinerary(pnrId));
    }

    public List<Itinerary> getSegments() {
        return segments;
    }

    public void setSegments(List<Itinerary> segments) {
        this.segments = segments;
    }

    public List<Itinerary> findSegments(Integer status, Integer gdsId, String oid, String airLineID, Date from, Date to) {
        return itineraryDao.findSegments(status, gdsId, oid, airLineID, from, to);
    }
}
