package com.ets.otherservice.ws;

import com.ets.otherservice.domain.AdditionalCharge;
import com.ets.otherservice.domain.AdditionalCharges;
import com.ets.otherservice.service.AdditionalChargeService;
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
@Path("/additionalcharge-management")
@Consumes("application/xml")
@Produces("application/xml")
public class AdditionalChargeWS {
    
    @Autowired
    AdditionalChargeService service;

    @GET
    @Path("/additionalcharges")
    public AdditionalCharges findAll() {

        List<AdditionalCharge> list = service.findAll();
        AdditionalCharges charges = new AdditionalCharges();
        charges.setList(list);
        return charges;
    }

    @POST
    @Path("/new")
    public AdditionalCharge create(AdditionalCharge additionalCharge) {
        return service.saveorUpdate(additionalCharge);
    }

    @PUT
    @Path("/update")
    public AdditionalCharge update(AdditionalCharge additionalCharge) {
        return service.saveorUpdate(additionalCharge);
    }

    @DELETE
    @Path("/delete/{id}")
    public Response delete(@PathParam("id") long id) {
        return Response.status(200).build();
    }
}

