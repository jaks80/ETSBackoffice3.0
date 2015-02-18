package com.ets.accountingdoc_o.ws;

import com.ets.accountingdoc_o.model.OtherSalesAcDocs;
import com.ets.accountingdoc.domain.OtherSalesAcDoc;
import com.ets.accountingdoc.model.InvoiceModel;
import com.ets.accountingdoc_o.model.InvoiceReportOther;
import com.ets.accountingdoc_o.model.OtherInvoiceModel;
import com.ets.accountingdoc_o.service.OSalesAcDocService;
import com.ets.util.DateUtil;
import com.ets.util.Enums;
import java.util.Date;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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

    @POST
    @Path("/new")
    public OtherSalesAcDoc create(OtherSalesAcDoc doc) {
        return service.newDocument(doc);
    }

    @GET
    @Path("/byid/{id}")
    public OtherSalesAcDoc getbyId(@PathParam("id") long id) {
        OtherSalesAcDoc doc = service.getWithChildrenById(id);
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

    @DELETE
    @Path("/delete/{id}")
    public Response delete(@PathParam("id") long id) {
        boolean success = service.delete(id);
        if (success) {
            return Response.status(200).build();
        } else {
            return Response.status(400).build();
        }
    }

    @DELETE
    @Path("/void/{id}")
    public Response _void(@PathParam("id") long id) {
        boolean success = service._void(id);
        if (success) {
            return Response.status(200).build();
        } else {
            return Response.status(400).build();
        }
    }

    @GET
    @Path("/due_invoices")
    public OtherSalesAcDocs outstandingInvoices(
            @QueryParam("doctype") Enums.AcDocType doctype,
            @QueryParam("clienttype") Enums.ClientType clienttype,
            @QueryParam("clientid") Long clientid,            
            @QueryParam("dateStart") String dateStart,
            @QueryParam("dateEnd") String dateEnd) {

        Date dateFrom = DateUtil.stringToDate(dateStart, "ddMMMyyyy");
        Date dateTo = DateUtil.stringToDate(dateEnd, "ddMMMyyyy");

        List<OtherSalesAcDoc> list = service.dueInvoices(doctype,
                clienttype,clientid, dateFrom, dateTo);

        OtherSalesAcDocs docs = new OtherSalesAcDocs();
        docs.setList(list);
        return docs;
    }
    
    //*****************Reporting Methods are Bellow******************//
    
    @GET
    @Path("/model/byid/{id}")
    public OtherInvoiceModel getModelbyId(@PathParam("id") long id) {
        OtherInvoiceModel model = service.getModelbyId(id);
        return model;
    }
    
    @GET
    @Path("/acdoc_report")
    public InvoiceReportOther outstandingDocumentReport(
            @QueryParam("doctype") Enums.AcDocType doctype,
            @QueryParam("clienttype") Enums.ClientType clienttype,
            @QueryParam("clientid") Long clientid,
            @QueryParam("dateStart") String dateStart,
            @QueryParam("dateEnd") String dateEnd) {

        Date dateFrom = DateUtil.stringToDate(dateStart, "ddMMMyyyy");
        Date dateTo = DateUtil.stringToDate(dateEnd, "ddMMMyyyy");

        InvoiceReportOther report = service.dueInvoiceReport(doctype,
                clienttype, clientid, dateFrom, dateTo);

        return report;
    }

    @GET
    @Path("/acdoc_history")
    public InvoiceReportOther documentHistoryReport(
            @QueryParam("clienttype") Enums.ClientType clienttype,
            @QueryParam("clientid") Long clientid,
            @QueryParam("dateStart") String dateStart,
            @QueryParam("dateEnd") String dateEnd) {

        Date dateFrom = DateUtil.stringToDate(dateStart, "ddMMMyyyy");
        Date dateTo = DateUtil.stringToDate(dateEnd, "ddMMMyyyy");

        InvoiceReportOther report = service.invoiceHistoryReport(clienttype,
                clientid, dateFrom, dateTo);

        return report;
    }
}
