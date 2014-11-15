package com.ets.dao.itinerary;

import com.ets.dao.generic.GenericDAOImpl;
import com.ets.domain.pnr.Itinerary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Yusuf
 */
@Service("itineraryDAO")
@Transactional
public class ItineraryDAOImpl  extends GenericDAOImpl<Itinerary, Long> implements ItineraryDAO {
    
}
