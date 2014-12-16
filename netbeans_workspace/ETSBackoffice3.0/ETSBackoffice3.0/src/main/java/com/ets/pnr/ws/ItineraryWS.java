package com.ets.pnr.ws;

import com.ets.report.model.SegmentReport;
import com.ets.pnr.service.ItineraryService;
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
@Path("/itinerary-management")
public class ItineraryWS {
 
    @Autowired
    ItineraryService service;
    
    @GET
    @Produces("application/xml")
    @Path("/segment-history")
    public SegmentReport saleReport(@QueryParam("ticketStatus") String ticketStatus, 
                              @QueryParam("airLineCode") String airLineCode, 
                              @QueryParam("dateStart") String dateStart, 
                              @QueryParam("dateEnd") String dateEnd,
                              @QueryParam("officeId") String officeId){
    
        
      
        SegmentReport report = service.segmentReport(ticketStatus, airLineCode, dateStart, dateEnd, officeId);
        return report;
    }
}