package com.ets.dao.pnr;

import com.ets.dao.generic.GenericDAO;
import com.ets.domain.pnr.Itinerary;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public interface ItineraryDAO  extends GenericDAO<Itinerary, Long>{
    
    public List findSegments(Integer tktStatus, String[] airLineID, Date from, Date to,String... officeIds);
}
