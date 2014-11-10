package com.ets.ws.air;

import com.ets.collection.Tickets;
import com.ets.domain.pnr.Pnr;
import com.ets.domain.pnr.Ticket;
import com.ets.service.air.AirService;
import java.util.Set;
import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.springframework.stereotype.Controller;

/**
 *
 * @author Yusuf
 */
@Controller
@Path("/air")
@Consumes("application/xml")
@Produces("application/xml")
public class AirWS {

    @Resource(name = "airService")
    AirService service;

    @POST
    @Path("/book")
    public Pnr createBooking(Pnr pnr){
        service.createBooking(pnr);
        return pnr;
    }

    @POST
    @Path("/issue")
    public Pnr createIssue(Pnr pnr){
        service.createIssue(pnr);
        return pnr;
    }

    @POST
    @Path("/re-issue")
    public Pnr createReIssue(Pnr pnr){
        service.createReIssue(pnr);
        return pnr;
    }

    @POST
    @Path("/refund")
    public Response refundTicket(Tickets tickets) {
        service.refundTicket(tickets);
        return Response.status(200).build();
    }

    @PUT
    @Path("/void")
    public Response voidTicket(Tickets tickets) {
        service.voidTicket(tickets);
        return Response.status(200).build();
    }
}
