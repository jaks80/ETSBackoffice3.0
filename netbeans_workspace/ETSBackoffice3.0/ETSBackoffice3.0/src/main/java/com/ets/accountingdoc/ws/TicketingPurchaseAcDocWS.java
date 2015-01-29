package com.ets.accountingdoc.ws;

import com.ets.accountingdoc.collection.TicketingPurchaseAcDocs;
import com.ets.accountingdoc.collection.TicketingSalesAcDocs;
import com.ets.accountingdoc.domain.TicketingPurchaseAcDoc;
import com.ets.accountingdoc.domain.TicketingSalesAcDoc;
import com.ets.accountingdoc.service.TPurchaseAcDocService;
import com.ets.report.model.acdoc.InvoiceReport;
import com.ets.util.DateUtil;
import com.ets.util.Enums;
import java.util.Date;
import java.util.List;
import javax.ws.rs.*;
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
    @Path("/byid/{id}")
    public TicketingPurchaseAcDoc getbyId(@PathParam("id") long id) {
        TicketingPurchaseAcDoc doc = service.getWithChildrenById(id);
        return doc;
    }

    @GET
    @Path("/bypnr/{pnr}")
    public TicketingPurchaseAcDocs getByGDSPnr(@PathParam("pnr") String pnr) {
        List<TicketingPurchaseAcDoc> list = service.getByGDSPnr(pnr);
        TicketingPurchaseAcDocs docs = new TicketingPurchaseAcDocs();
        docs.setList(list);
        return docs;
    }

    @GET
    @Path("/bypnrid")
    public TicketingPurchaseAcDocs getByPnrId(@QueryParam("pnrId") Long pnrId) {
        List<TicketingPurchaseAcDoc> list = service.getByPnrId(pnrId);
        TicketingPurchaseAcDocs docs = new TicketingPurchaseAcDocs();
        docs.setList(list);
        return docs;
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

    //*****************Reporting Methods are Bellow******************//
    @GET
    @Path("/acdoc_report")
    public InvoiceReport outstandingDocumentReport(
            @QueryParam("doctype") Enums.AcDocType doctype,
            @QueryParam("agentid") Long agentid,
            @QueryParam("dateStart") String dateStart,
            @QueryParam("dateEnd") String dateEnd) {

        Date dateFrom = DateUtil.stringToDate(dateStart, "ddMMMyyyy");
        Date dateTo = DateUtil.stringToDate(dateEnd, "ddMMMyyyy");

        InvoiceReport report = service.dueInvoiceReport(doctype,
                agentid, dateFrom, dateTo);

        return report;
    }

    @GET
    @Path("/acdoc_history")
    public InvoiceReport documentHistoryReport(
            @QueryParam("agentid") Long agentid,
            @QueryParam("dateStart") String dateStart,
            @QueryParam("dateEnd") String dateEnd) {

        Date dateFrom = DateUtil.stringToDate(dateStart, "ddMMMyyyy");
        Date dateTo = DateUtil.stringToDate(dateEnd, "ddMMMyyyy");

        InvoiceReport report = service.invoiceHistoryReport(agentid, dateFrom, dateTo);

        return report;
    }
}
