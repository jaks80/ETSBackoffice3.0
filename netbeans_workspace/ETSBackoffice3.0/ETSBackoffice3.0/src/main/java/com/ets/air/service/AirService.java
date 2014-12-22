package com.ets.air.service;

import com.ets.air.dao.AirDAO;
import com.ets.pnr.dao.ItineraryDAO;
import com.ets.pnr.dao.TicketDAO;
import com.ets.pnr.domain.Pnr;
import com.ets.pnr.domain.Ticket;
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
     * This method is for Booking to Issue or Direct Issue or Re Issue
     * If Pnr exist update it otherwise insert. Its children needs to be
     * updated too.
     * @param newPnr    
     * @return
     */
    public Pnr savePnr(Pnr newPnr) {
        
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

    public List<Ticket> refundTicket(List<Ticket> tickets) {
        
        Pnr persistedPnr = dao.findPnr(tickets.get(0).getTicketNo(), tickets.get(0).getSurName());
        List<Ticket> tobePersisted = new ArrayList<>();

        //Check if ticket already exist in db. If exist then ignore
        for (Ticket tn : tickets) {
            boolean exist = false;
            for (Ticket to : persistedPnr.getTickets()) {
                if (tn.getTicketNo().equals(to.getTicketNo())
                        && tn.getSurName().equals(tn.getSurName())
                        && tn.getTktStatusString().equals(to.getTktStatusString())) {
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
    
    public List<Ticket> voidTicket(List<Ticket> tickets) {

        return tickets;
    }
    
    public void save(Pnr pnr){
        dao.save(pnr);
    }
    
    public Pnr findPnr(String gdsPnr, Date pnrCreationdate) {
        return dao.findPnr(gdsPnr, pnrCreationdate);
    }
}
