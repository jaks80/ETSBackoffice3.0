package etsbackoffice.businesslogic;

import etsbackoffice.datalogic.TicketDao;
import etsbackoffice.domain.MasterAgent;
import etsbackoffice.domain.Ticket;
import etsbackoffice.domain.TicketRefundDetails;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public class TicketBo {

    private Ticket ticket;
    private List<Ticket> tickets = new ArrayList();
    private TicketDao ticketDao;
            
    public TicketBo() {
    }

    public void saveTicket() {
        ticketDao.save(ticket);
    }
    
    public void saveAllTickets(List<Ticket> ts) {
        ticketDao.saveAll(new LinkedHashSet(ts));
    }
    
    public void deleteAll(List<Ticket> ts){
    ticketDao.deleteAllTicket(ts);
    }
    public Ticket loadTicketItinerary(long ticketId) {
        return ticketDao.loadTicketItinerary(ticketId);
    }

    public TicketRefundDetails loadTicketRfdDetails(long id){
     return ticketDao.loadTicketRfdDetails(id);
    }
    
    public List<Ticket> findSoldTickets(int ticketingType, Integer gdsId, Long tktingAgtID,Integer tktStatus,String career, Date from, Date to) {
        return ticketDao.saleReport(ticketingType, gdsId, tktingAgtID,tktStatus,career, from, to);       
    }
     
    public List<Ticket> saleReportDetails(int ticketingType, Integer gdsId, Long tktingAgtID,
            Integer tktStatus, String career, Date from, Date to, String bookingOid) {
        return ticketDao.saleReportDetails(ticketingType, gdsId, tktingAgtID, tktStatus, career, from, to, bookingOid);
    }
    
    public void setTicketDao(TicketDao ticketDao) {
        this.ticketDao = ticketDao;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }
    
    public Ticket reissueTicket(Ticket originalTicket){
        Ticket reIssuedTicket = new Ticket();
        reIssuedTicket.setPassengerNo(originalTicket.getPassengerNo());
        reIssuedTicket.setPaxSurName(originalTicket.getPaxSurName());
        reIssuedTicket.setPaxForeName(originalTicket.getPaxForeName());
        reIssuedTicket.setNumericAirLineCode(originalTicket.getNumericAirLineCode());
        reIssuedTicket.setTktStatus(3);
        //reIssuedTicket.setPnr(originalTicket.getp);        
        return reIssuedTicket;
    }
}
