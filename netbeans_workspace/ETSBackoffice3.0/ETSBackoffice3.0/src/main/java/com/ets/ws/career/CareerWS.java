package com.ets.ws.career;

import com.ets.collection.Careers;
import com.ets.domain.pnr.Career;
import com.ets.service.career.CareerService;
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
public class CareerWS {

    @Autowired
    CareerService service;

    @GET
    @Path("/careers/{code}")
    @Produces("application/xml")
    public Career find(@PathParam("code") String code) {

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
    public void save(Career career) {
        
        service.save(career);
    }

    @POST
    @Path("/careers/save-all")
    @Produces("application/xml")
    public void saveBulk(Careers careers) {
        
        service.saveBulk(careers);
    }
}
