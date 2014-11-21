package com.ets.service.air;

import com.ets.collection.Tickets;
import com.ets.dao.air.AirDAO;
import com.ets.dao.pnr.ItineraryDAO;
import com.ets.dao.pnr.TicketDAO;
import com.ets.domain.pnr.Pnr;
import com.ets.domain.pnr.Ticket;
import com.ets.util.PnrUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    
    @Resource(name = "ticketDAO")
    private TicketDAO ticketDAO;
    
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
        
        PnrUtil.initPnrChildren(persistedPnr);
        save(persistedPnr);
        PnrUtil.undefinePnrChildren(persistedPnr); //Undefine cyclic dependencies to avoid cyclic xml exception

        return persistedPnr;
    }
    
    public Pnr createReIssue(Pnr pnr){
    
        return pnr;
    }

    public Tickets refundTicket(Tickets tickets) {

        List<Ticket> newlist = tickets.getList();
        Pnr persistedPnr = dao.findPnr(newlist.get(0).getTicketNo(), newlist.get(0).getPaxSurName());

        List<Ticket> tobePersisted = new ArrayList<>();

        //Check if ticket already exist in db. If exist then ignore
        for (Ticket tn : newlist) {
            boolean exist = false;
            for (Ticket to : persistedPnr.getTickets()) {
                if (tn.getTicketNo().equals(to.getTicketNo())
                        && tn.getPaxSurName().equals(tn.getPaxSurName())
                        && tn.getTktStatus() == to.getTktStatus()) {
                    exist = true;
                }
            }

            if (!exist) {
                tobePersisted.add(tn);
            }
        }

        if (!tobePersisted.isEmpty()) {
            PnrUtil.initPnrInTickets(persistedPnr, tobePersisted);
            ticketDAO.saveBulk(tobePersisted);
            PnrUtil.undefinePnrInTickets(persistedPnr, tobePersisted);
        }
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
