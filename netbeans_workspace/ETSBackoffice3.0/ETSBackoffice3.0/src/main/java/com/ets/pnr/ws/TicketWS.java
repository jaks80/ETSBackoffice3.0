package com.ets.pnr.ws;

import com.ets.pnr.model.GDSSaleReport;
import com.ets.pnr.model.TicketSaleReport;
import com.ets.pnr.service.TicketService;
import com.ets.util.DateUtil;
import com.ets.util.Enums;
import java.util.Date;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
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
    @RolesAllowed("SM")
    public GDSSaleReport gdsSaleReport(@QueryParam("ticketingType") Enums.TicketingType ticketingType,
            @QueryParam("ticketStatus") Enums.TicketStatus ticketStatus,
            @QueryParam("airLineCode") String airLineCode,
            @QueryParam("dateStart") String dateStart,
            @QueryParam("dateEnd") String dateEnd,
            @QueryParam("ticketingAgtOid") String ticketingAgtOid) {

        Date dateFrom = DateUtil.stringToDate(dateStart, "ddMMMyyyy");
        Date dateTo = DateUtil.stringToDate(dateEnd, "ddMMMyyyy");

        GDSSaleReport report = service.saleReport(ticketingType,ticketStatus, airLineCode, dateFrom, dateTo, ticketingAgtOid);
        return report;
    }

    @GET
    @Produces("application/xml")
    @Path("/salereport")
    @RolesAllowed("SM")    
    public TicketSaleReport saleReport(@QueryParam("ticketingType") Enums.TicketingType ticketingType,
            @QueryParam("ticketStatus") Enums.TicketStatus ticketStatus,
            @QueryParam("airLineCode") String airLineCode,
            @QueryParam("dateStart") String dateStart,
            @QueryParam("dateEnd") String dateEnd,
            @QueryParam("ticketingAgtOid") String ticketingAgtOid) {

        Date dateFrom = DateUtil.stringToDate(dateStart, "ddMMMyyyy");
        Date dateTo = DateUtil.stringToDate(dateEnd, "ddMMMyyyy");

        TicketSaleReport report = service.saleRevenueReport(ticketingType,ticketStatus, airLineCode, dateFrom, dateTo, ticketingAgtOid);
        return report;
    }
}
