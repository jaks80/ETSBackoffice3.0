package com.ets.ws.air;

import com.ets.domain.pnr.Pnr;
import com.ets.service.air.AirService;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Date;
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
@Path("/air")
public class AirWS {

    @Autowired
    AirService service;

    @POST
    @Path("")
    @Consumes("application/xml")
    @Produces("application/xml")
    public Pnr create(Pnr pnr) throws ParseException {
        service.create();
        return pnr;
    }

    @PUT
    @Path("/pnrs/{id}")
    @Consumes("application/xml")
    @Produces("application/xml")
    public Pnr update(@PathParam("id") long id, Pnr pnr) throws URISyntaxException {
        System.out.println("PNR" + pnr.getGdsPNR());
        return pnr;
    }

    @DELETE
    @Path("/pnrs/{id}")
    public Response delete(@PathParam("id") long id)
            throws URISyntaxException {
        return Response.status(200).build();
    }

    @GET
    @Produces("application/xml")
    @Path("/pnrs/searchbypnr{gdsPnr}/{pnrCreationDate}")
    public Pnr getGdsPnr(@QueryParam("gdsPnr") String gdsPnr, @QueryParam("pnrCreationDate") Date pnrCreationDate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
