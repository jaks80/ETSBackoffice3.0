package com.ets.settings.ws;

import com.ets.client.domain.MainAgent;
import com.ets.settings.domain.AppSettings;
import com.ets.settings.service.AppSettingsService;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 *
 * @author Yusuf
 */
@Controller
@Path("/appsettings-management")
@Consumes("application/xml")
@Produces("application/xml")
public class AppSettingsWS {

    @Autowired
    AppSettingsService service;

    @GET
    @Path("/mainagent")
    public MainAgent getMainAgent(){
     return service.getMainAgent();
    }
    
    @PUT
    @Path("/updatemainagent")
    public MainAgent update(MainAgent agent) {
        return service.saveorUpdateMainAgent(agent);
    }
    
    @GET
    @Path("/appsettings")
    public AppSettings find() {

        List<AppSettings> list = service.findAll();
        return list.get(0);
    }

    @PUT
    @Path("/update")
    public AppSettings update(AppSettings appSettings) {
        return service.saveorUpdate(appSettings);
    }

//    @DELETE
//    @Path("/delete/{id}")
//    public Response delete(@PathParam("id") long id) {
//        return Response.status(200).build();
//    }
}
