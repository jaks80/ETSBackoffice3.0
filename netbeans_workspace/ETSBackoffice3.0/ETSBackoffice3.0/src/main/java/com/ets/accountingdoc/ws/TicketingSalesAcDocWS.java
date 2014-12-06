package com.ets.accountingdoc.ws;

import com.ets.accountingdoc.collection.TicketingSalesAcDocs;
import com.ets.accountingdoc.domain.TicketingSalesAcDoc;
import com.ets.accountingdoc.service.TSalesAcDocService;
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
@Path("/tsacdoc-management")
@Consumes("application/xml")
@Produces("application/xml")
public class TicketingSalesAcDocWS {

    @Autowired
    TSalesAcDocService service;

    @GET
    @Path("/ticketingSalesAcDocs")
    public TicketingSalesAcDocs get(@QueryParam("name") String name,
            @QueryParam("postCode") String postCode,
            @QueryParam("officeID") String officeID) {

        List<TicketingSalesAcDoc> list = service.findAll();
        TicketingSalesAcDocs ticketingSalesAcDocs = new TicketingSalesAcDocs();
        ticketingSalesAcDocs.setList(list);
        return ticketingSalesAcDocs;
    }

    @GET
    @Path("/byid/{id}")
    public TicketingSalesAcDoc getbyId(@PathParam("id") long id) {

        TicketingSalesAcDoc doc = service.getById(id);
        return doc;
    }

    @GET
    @Path("/byref/{refference}")
    public TicketingSalesAcDocs getByRefNo(@PathParam("refference") Integer refference) {
        List<TicketingSalesAcDoc> list = service.getByReffference(refference);
        TicketingSalesAcDocs docs = new TicketingSalesAcDocs();
        docs.setList(list);
        return docs;
    }

    @GET
    @Path("/bypnr/{pnr}")
    public TicketingSalesAcDocs getByGDSPnr(@PathParam("pnr") String pnr) {
        List<TicketingSalesAcDoc> list = service.getByGDSPnr(pnr);
        TicketingSalesAcDocs docs = new TicketingSalesAcDocs();
        docs.setList(list);
        return docs;
    }

//    public List<TicketingSalesAcDoc> findOutstandingInvoice(Long contactableId, int type, Date from, Date to) {
//
//    }
//
//    public List<TicketingSalesAcDoc> invoiceHistoryByCriteria(Long contactableId,
//            int contactableType, Integer docTypeFrom, Integer docTypeTo, Date from, Date to, Long tktingAgtFrom, Long tktingAgtTo) {
//
//    }
//
//    public void removeLine(AccountingDocumentLine l) {
//
//    }

    @POST
    @Path("/new")
    public TicketingSalesAcDoc create(TicketingSalesAcDoc ticketingSalesAcDoc) {
        return service.saveorUpdate(ticketingSalesAcDoc);
    }

    @PUT
    @Path("/update")
    public TicketingSalesAcDoc update(TicketingSalesAcDoc ticketingSalesAcDoc) {
        return service.saveorUpdate(ticketingSalesAcDoc);
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
