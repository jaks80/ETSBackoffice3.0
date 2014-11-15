package com.ets.service.air;

import com.ets.collection.Tickets;
import com.ets.dao.air.AirDAO;
import com.ets.dao.itinerary.ItineraryDAO;
import com.ets.domain.pnr.Pnr;
import com.ets.domain.pnr.Ticket;
import com.ets.util.PnrUtil;
import java.util.Date;
import java.util.Set;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 *
 * @author Yusuf
 */
@Service("airService")
public class AirService {

    @Resource(name = "airDAO")
    private AirDAO dao;

    @Resource(name = "itineraryDAO")
    private ItineraryDAO itineraryDAO;
    
    public AirService(){

    }

    /**
     * This method is to save booked pnr.     
     * @param pnr
     * @return 
     */
    public Pnr createBooking(Pnr pnr) {

        Pnr persistedPnr = createIssue(pnr);

        return persistedPnr;
    }
    
    /**
     * This method is for Booking to Issue or Direct Issue
     * If Pnr exist update it otherwise insert. Its children needs to be
     * updated too.
     * @param newPnr    
     * @return
     */
    public Pnr createIssue(Pnr newPnr) {
        
        Pnr persistedPnr = findPnr(newPnr.getGdsPnr(), newPnr.getPnrCreationDate());
        
        if (persistedPnr == null) {          
            persistedPnr = newPnr;
        } else {
            // This else is for booking to issue. Booked pnr was in database.
            persistedPnr = PnrUtil.updatePnr(persistedPnr, newPnr);
            Set<Ticket> dbTickets = persistedPnr.getTickets();
            persistedPnr.setTickets(PnrUtil.updateTickets(dbTickets, newPnr.getTickets()));

            itineraryDAO.deleteBulk(persistedPnr.getSegments());//Saving final segments. Issued segments are final segments.
            persistedPnr.setSegments(newPnr.getSegments());           
        }
        
        PnrUtil.initPnrChildren(newPnr);
        save(persistedPnr);
        PnrUtil.undefinePnrChildren(persistedPnr); //Undefine cyclic dependencies to avoid cyclic xml exception

        return persistedPnr;
    }
    
    public Pnr createReIssue(Pnr pnr){
    
        return pnr;
    }

    public Tickets refundTicket(Tickets tickets) {

        
        return tickets;
    }
    
    public Tickets voidTicket(Tickets tickets) {

        return tickets;
    }
    
    public void save(Pnr pnr){
        dao.save(pnr);
    }
    
    public Pnr findPnr(String gdsPnr, Date pnrCreationdate) {
        return dao.findPnr(gdsPnr, pnrCreationdate);
    }
}
