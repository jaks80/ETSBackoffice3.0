package com.ets.fe.acdoc.ws;

import com.ets.accountingdoc.collection.TicketingSalesAcDocs;
import com.ets.fe.APIConfig;
import com.ets.fe.acdoc.model.TicketingSalesAcDoc;
import com.ets.fe.acdoc.model.report.InvoiceReport;
import com.ets.fe.util.DateUtil;
import com.ets.fe.util.Enums;
import com.ets.fe.util.RestClientUtil;
import java.util.Date;

/**
 *
 * @author Yusuf
 */
public class TicketingSAcDocWSClient {

    public TicketingSalesAcDoc newDraftDocument(Long pnrid) {
        String url = APIConfig.get("ws.tsacdoc.draftdoc") + pnrid;
        TicketingSalesAcDoc draftDocument = RestClientUtil.getEntity(TicketingSalesAcDoc.class, url, new TicketingSalesAcDoc());
        return draftDocument;
    }

    public TicketingSalesAcDoc newSalesDocument(TicketingSalesAcDoc ticketingSalesAcDoc) {
        String url = APIConfig.get("ws.tsacdoc.newdoc");
        TicketingSalesAcDoc persistedDoc = RestClientUtil.postEntity(TicketingSalesAcDoc.class, url, ticketingSalesAcDoc);
        return persistedDoc;
    }

    public TicketingSalesAcDoc createNewPayment(TicketingSalesAcDoc payment) {
        String url = APIConfig.get("ws.tsacdoc.newpayment");
        TicketingSalesAcDoc persistedPayment = RestClientUtil.postEntity(TicketingSalesAcDoc.class, url, payment);
        return persistedPayment;
    }

    public Integer delete(long id) {
        String url = APIConfig.get("ws.tsacdoc.delete") + id;
        Integer status = RestClientUtil.deleteById(url);
        return status;
    }

    public Integer _void(long id) {
        String url = APIConfig.get("ws.tsacdoc.void") + id;
        Integer status = RestClientUtil.deleteById(url);
        return status;
    }

    public TicketingSalesAcDoc getbyId(long id) {
        String url = APIConfig.get("ws.tsacdoc.byid") + id;
        TicketingSalesAcDoc doc = RestClientUtil.getEntity(TicketingSalesAcDoc.class, url, new TicketingSalesAcDoc());
        return doc;
    }

    public TicketingSalesAcDocs getByPnrId(Long pnrId) {
        String url = APIConfig.get("ws.tsacdoc.bypnrid") + "?pnrId=" + pnrId;
        TicketingSalesAcDocs docs = RestClientUtil.getEntity(TicketingSalesAcDocs.class, url, new TicketingSalesAcDocs());
        return docs;
    }

    public TicketingSalesAcDocs getByRefNo(Integer refference) {
        String url = APIConfig.get("ws.tsacdoc.byref" + refference);
        TicketingSalesAcDocs docs = RestClientUtil.getEntity(TicketingSalesAcDocs.class, url, new TicketingSalesAcDocs());
        return docs;
    }

    public TicketingSalesAcDocs getByGDSPnr(String pnr) {
        String url = APIConfig.get("ws.tsacdoc.bypnr") + pnr;
        TicketingSalesAcDocs docs = RestClientUtil.getEntity(TicketingSalesAcDocs.class, url, new TicketingSalesAcDocs());
        return docs;
    }

    public InvoiceReport outstandingDocumentReport(
            Enums.AcDocType doctype, Enums.ClientType clienttype,
            Long clientid, Date _dateFrom, Date _dateTo) {

        String dateFrom = DateUtil.dateToString(_dateFrom, "ddMMMyyyy");
        String dateTo = DateUtil.dateToString(_dateTo, "ddMMMyyyy");

        String url = APIConfig.get("ws.tsacdoc.report") + "?dateStart=" + dateFrom + "&dateEnd=" + dateTo + "&doctype=" + doctype;

        if (clienttype != null) {
            url = url + "&clienttype=" + clienttype;
        }
        if (clientid != null) {
            url = url + "&clientid=" + clientid;
        }

        InvoiceReport report = RestClientUtil.getEntity(InvoiceReport.class, url, new InvoiceReport());
        return report;
    }

    public InvoiceReport documentHistoryReport(
            Enums.ClientType clienttype, Long clientid, Date _dateFrom, Date _dateTo) {

        String dateFrom = DateUtil.dateToString(_dateFrom, "ddMMMyyyy");
        String dateTo = DateUtil.dateToString(_dateTo, "ddMMMyyyy");

        String url = APIConfig.get("ws.tsacdoc.history") + "?dateStart=" + dateFrom + "&dateEnd=" + dateTo;
        if (clienttype != null) {
            url = url + "&clienttype=" + clienttype;
        }
        if (clientid != null) {
            url = url + "&clientid=" + clientid;
        }

        InvoiceReport report = RestClientUtil.getEntity(InvoiceReport.class, url, new InvoiceReport());
        return report;
    }
}
