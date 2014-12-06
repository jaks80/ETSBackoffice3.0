package com.ets.accountingdoc.ws;

import com.ets.accountingdoc.collection.OtherSalesAcDocs;
import com.ets.accountingdoc.domain.OtherSalesAcDoc;
import com.ets.accountingdoc.service.OSalesAcDocService;
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
@Path("/osacdoc-management")
@Consumes("application/xml")
@Produces("application/xml")
public class OtherSalesAcDocWS {
    
    @Autowired
    OSalesAcDocService service;

    @GET
    @Path("/otherSalesAcDocs")
    public OtherSalesAcDocs find(@QueryParam("name") String name,
            @QueryParam("postCode") String postCode,
            @QueryParam("officeID") String officeID) {

        List<OtherSalesAcDoc> list = service.findAll();
        OtherSalesAcDocs otherSalesAcDocs = new OtherSalesAcDocs();
        otherSalesAcDocs.setList(list);
        return otherSalesAcDocs;
    }

        @GET
    @Path("/byid/{id}")
    public OtherSalesAcDoc getbyId(@PathParam("id") long id) {
        OtherSalesAcDoc doc = service.getById(id);
        return doc;
    }

    @GET
    @Path("/byref/{refference}")
    public OtherSalesAcDocs getByRefNo(@PathParam("refference") Integer refference) {
        List<OtherSalesAcDoc> list = service.getByReffference(refference);
        OtherSalesAcDocs docs = new OtherSalesAcDocs();
        docs.setList(list);
        return docs;
    }

    @POST
    @Path("/new")
    public OtherSalesAcDoc create(OtherSalesAcDoc otherSalesAcDoc) {
        return service.saveorUpdate(otherSalesAcDoc);
    }

    @PUT
    @Path("/update")
    public OtherSalesAcDoc update(OtherSalesAcDoc otherSalesAcDoc) {
        return service.saveorUpdate(otherSalesAcDoc);
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
