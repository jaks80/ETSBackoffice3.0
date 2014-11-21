package com.ets.ws;

import com.ets.model.report.TicketSaleReport;
import com.ets.service.pnr.TicketService;
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
    public TicketSaleReport saleReport(@QueryParam("ticketStatus") String ticketStatus, 
                              @QueryParam("airLineCode") String airLineCode, 
                              @QueryParam("dateStart") String dateStart, 
                              @QueryParam("dateEnd") String dateEnd,
                              @QueryParam("ticketingAgtOid") String ticketingAgtOid){
    
        TicketSaleReport report = service.saleReport(ticketStatus, airLineCode, dateStart, dateEnd, ticketingAgtOid);
        return report;
    }
}
