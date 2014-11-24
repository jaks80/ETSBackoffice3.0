package com.ets.pnr.ws;

import com.ets.pnr.model.collection.Careers;
import com.ets.pnr.domain.Airline;
import com.ets.pnr.service.AirlineService;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 *
 * @author Yusuf
 */
@Controller
@Path("/career-management")
public class AirlineWS {

    @Autowired
    AirlineService service;

    @GET
    @Path("/careers/{code}")
    @Produces("application/xml")
    public Airline find(@PathParam("code") String code) {

        return service.find(code);
    }

    
    @GET
    @Path("/careers/match/{name}")
    @Produces("application/xml")
    public Careers match(@QueryParam("name") String name) {

        return service.match(name);
    }
    
    @POST
    @Path("/careers/save")
    @Produces("application/xml")
    public void save(Airline career) {
        
        service.save(career);
    }

    @POST
    @Path("/careers/save-all")
    @Produces("application/xml")
    public void saveBulk(Careers careers) {
        
        service.saveBulk(careers);
    }
}
