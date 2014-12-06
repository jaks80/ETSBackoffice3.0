package com.ets.accountingdoc.ws;

import com.ets.accountingdoc.collection.TicketingPurchaseAcDocs;
import com.ets.accountingdoc.domain.TicketingPurchaseAcDoc;
import com.ets.accountingdoc.service.TPurchaseAcDocService;
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
@Path("/tpacdoc-management")
@Consumes("application/xml")
@Produces("application/xml")
public class TicketingPurchaseAcDocWS {

    @Autowired
    TPurchaseAcDocService service;

    @GET
    @Path("/ticketingPurchaseAcDocs")
    public TicketingPurchaseAcDocs find(@QueryParam("name") String name,
            @QueryParam("postCode") String postCode,
            @QueryParam("officeID") String officeID) {

        List<TicketingPurchaseAcDoc> list = service.findAll();
        TicketingPurchaseAcDocs ticketingPurchaseAcDocs = new TicketingPurchaseAcDocs();
        ticketingPurchaseAcDocs.setList(list);
        return ticketingPurchaseAcDocs;
    }
    
    @GET
    @Path("/byid/{id}")
    public TicketingPurchaseAcDoc getbyId(@PathParam("id") long id) {
        TicketingPurchaseAcDoc doc = service.getById(id);
        return doc;
    }

    @GET
    @Path("/byref/{refference}")
    public TicketingPurchaseAcDocs getByRefNo(@PathParam("refference") Integer refference) {
        List<TicketingPurchaseAcDoc> list = service.getByReffference(refference);
        TicketingPurchaseAcDocs docs = new TicketingPurchaseAcDocs();
        docs.setList(list);
        return docs;
    }

    @POST
    @Path("/new")
    public TicketingPurchaseAcDoc create(TicketingPurchaseAcDoc ticketingPurchaseAcDoc) {
        return service.saveorUpdate(ticketingPurchaseAcDoc);
    }

    @PUT
    @Path("/update")
    public TicketingPurchaseAcDoc update(TicketingPurchaseAcDoc ticketingPurchaseAcDoc) {
        return service.saveorUpdate(ticketingPurchaseAcDoc);
    }

    @DELETE
    @Path("/delete/{id}")
    public Response delete(@PathParam("id") long id) {
        return Response.status(200).build();
    }
    
    @DELETE
    @Path("/void/{id}")
    public Response _void(@PathParam("id") long id) {
        return Response.status(200).build();
    }
}
