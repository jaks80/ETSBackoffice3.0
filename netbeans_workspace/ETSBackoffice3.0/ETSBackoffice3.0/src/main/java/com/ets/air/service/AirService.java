package com.ets.air.service;

import com.ets.accountingdoc.service.TPurchaseAcDocService;
import com.ets.air.dao.AirDAO;
import com.ets.pnr.dao.*;
import com.ets.pnr.domain.*;
import com.ets.pnr.service.AirlineService;
import com.ets.pnr.service.TicketService;
import com.ets.util.PnrUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    TicketService ticketService;

    @Autowired
    AirlineService airlineService;
    @Autowired
    TPurchaseAcDocService purchase_service;

    public AirService() {

    }

    /**
     * This method is for Booking to Issue or Direct Issue or Re Issue If Pnr
     * exist update it otherwise insert. Its children needs to be updated too.
     *
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

        Airline airline = airlineService.find(persistedPnr.getAirLineCode());
        if (airline != null) {
            setBspCommission(persistedPnr.getTickets(), airline);
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

            Airline airline = airlineService.find(persistedPnr.getAirLineCode());
            if (airline != null) {
                setBspCommission(persistedPnr.getTickets(), airline);
            }

            PnrUtil.initPnrInTickets(persistedPnr, tobePersisted);
            ticketDAO.saveBulk(tobePersisted);
            PnrUtil.undefinePnrInTickets(persistedPnr, tobePersisted);
        }
        return tickets;
    }

    public List<Ticket> voidTicket(List<Ticket> tickets, Pnr pnr) {
        for (Ticket t : tickets) {
            ticketService._void(pnr.getGdsPnr(),t.getNumericAirLineCode(), t.getTicketNo(), t.getSurName());
        }
        return tickets;
    }

    private void setBspCommission(Set<Ticket> tickets, Airline airline) {
        for (Ticket t : tickets) {
            t.setCommission(airline.calculateBspCom(t));
        }
    }

    public Pnr findPnr(String gdsPnr, Date pnrCreationdate) {
        return dao.findPnr(gdsPnr, pnrCreationdate);
    }

    public void save(Pnr pnr) {
        dao.save(pnr);
    }
}
