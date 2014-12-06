package com.ets.pnr.ws;

import com.ets.pnr.model.collection.Pnrs;
import com.ets.pnr.domain.Pnr;
import com.ets.pnr.service.PnrService;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 *
 * @author Yusuf
 */
@Controller
@Path("/pnr-management")
public class PnrWS {

    @Autowired
    PnrService service;

    @POST
    @Path("/new")
    @Consumes("application/xml")
    @Produces("application/xml")
    public Pnr create(Pnr pnr){
        service.save(pnr);
        return pnr;
    }

    @PUT
    @Path("/update/{id}")
    @Consumes("application/xml")
    @Produces("application/xml")
    public Pnr update(@PathParam("id") long id, Pnr pnr){
        System.out.println("PNR" + pnr.getGdsPnr());
        return pnr;
    }

    @DELETE
    @Path("/delete/{id}")
    public Response delete(@PathParam("id") long id){
        return Response.status(200).build();
    }

    @GET
    @Produces("application/xml")
    @Path("/history")
    public Pnrs getHistory(@QueryParam("bookingAgtOid") String bookingAgtOid,
                           @QueryParam("ticketingAgtOid") String ticketingAgtOid, 
                           @QueryParam("dateStart") String dateStart, 
                           @QueryParam("dateEnd") String dateEnd){
        Pnrs pnrs = service.pnrHistory(dateStart, dateEnd, ticketingAgtOid, bookingAgtOid);
        return pnrs;
    }

    @GET
    @Produces("application/xml")
    @Path("/byid/{id}")
    public Pnr getById(@PathParam("id") long id){
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @GET
    @Produces("application/xml")
    @Path("/withchildren/{id}")
    public Pnr getByIdWithChildren(@PathParam("id") long id){
         throw new UnsupportedOperationException("Not supported yet.");
    }

    @GET
    @Produces("application/xml")
    @Path("/bytkt/{tktNo}/{surName}")
    public List<Pnr> getPnrByTktNo(@QueryParam("tktNo") String tktNo, @QueryParam("surName") String surName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @GET
    @Produces("application/xml")
    @Path("/bypaxname/{tktNo}/{surName}")
    public List<Pnr> getPnrByName(@QueryParam("surName") String surName, @QueryParam("foreName") String foreName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
