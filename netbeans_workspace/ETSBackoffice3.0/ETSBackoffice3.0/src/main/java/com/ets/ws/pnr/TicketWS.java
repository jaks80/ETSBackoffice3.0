package com.ets.ws.pnr;

import com.ets.collection.Tickets;
import com.ets.domain.pnr.Ticket;
import com.ets.service.pnr.TicketService;
import com.ets.util.PnrUtil;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 *
 * @author Yusuf
 */
@Controller
@Path("/ticket-management")
public class TicketWS {
    
    @Autowired
    TicketService service;
    
    @GET
    @Produces("application/xml")
    @Path("/gds-salereport")
    public Tickets saleReport(@QueryParam("ticketStatus") Integer ticketStatus, 
                              @QueryParam("careerCode") String careerCode, 
                              @QueryParam("dateStart") String dateStart, 
                              @QueryParam("dateEnd") String dateEnd,
                              @QueryParam("ticketingAgtOid") String ticketingAgtOid){
    
        Tickets tickets = new Tickets();
        tickets.setList(service.saleReport(ticketStatus, careerCode, dateStart, dateEnd, ticketingAgtOid));
        
        for(Ticket t: tickets.getList()){
            PnrUtil.undefineChildrenInPnr(t.getPnr());
        }
        
        
        return tickets;
    }
}
