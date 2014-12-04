package com.ets.settings.ws;

import com.ets.AppSettings;
import com.ets.settings.service.AppSettingsService;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
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
    @Path("/appsettings")
    public AppSettings find() {

        List<AppSettings> list = service.findAll();
        return list.get(0);
    }

    @POST
    @Path("/new")
    public AppSettings create(AppSettings appSettings) {
        return service.saveorUpdate(appSettings);
    }

    @PUT
    @Path("/update")
    public AppSettings update(AppSettings appSettings) {
        return service.saveorUpdate(appSettings);
    }

    @DELETE
    @Path("/delete/{id}")
    public Response delete(@PathParam("id") long id) {
        return Response.status(200).build();
    }
}
