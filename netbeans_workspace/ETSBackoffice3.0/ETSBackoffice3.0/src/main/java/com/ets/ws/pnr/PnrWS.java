package com.ets.ws.pnr;

import com.ets.domain.pnr.Pnr;
import com.ets.service.pnr.PnrService;
import java.util.Date;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
    @Path("/pnrs")
    @Consumes("application/xml")
    @Produces("application/xml")
    public Pnr create(Pnr pnr){
        service.save(pnr);
        return pnr;
    }

    @PUT
    @Path("/pnrs/{id}")
    @Consumes("application/xml")
    @Produces("application/xml")
    public Pnr update(@PathParam("id") long id, Pnr pnr){
        System.out.println("PNR" + pnr.getGdsPnr());
        return pnr;
    }

    @DELETE
    @Path("/pnrs/{id}")
    public Response delete(@PathParam("id") long id){
        return Response.status(200).build();
    }

    @GET
    @Produces("application/xml")
    @Path("/pnrs/getbydate/{start}/{end}")
    public Pnr getByDate(@QueryParam("start") Date start, @QueryParam("end") Date end){
        Pnr pnr = new Pnr();
        return pnr;
    }

    @GET
    @Produces("application/xml")
    @Path("/pnrs/getbyid/{id}")
    public Pnr getById(@PathParam("id") long id){
        Pnr pnr = new Pnr();
        return pnr;
    }

    @GET
    @Produces("application/xml")
    @Path("/pnrs/withchildren/{id}")
    public Pnr getByIdWithChildren(@PathParam("id") long id){
         Pnr pnr = new Pnr();
        return pnr;
    }

    @GET
    @Produces("application/xml")
    @Path("/pnrs/getbytkt/{tktNo}/{surName}")
    public List<Pnr> getPnrByTktNo(@QueryParam("tktNo") String tktNo, @QueryParam("surName") String surName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @GET
    @Produces("application/xml")
    @Path("/pnrs/getbyname/{tktNo}/{surName}")
    public List<Pnr> getPnrByName(@QueryParam("surName") String surName, @QueryParam("foreName") String foreName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
