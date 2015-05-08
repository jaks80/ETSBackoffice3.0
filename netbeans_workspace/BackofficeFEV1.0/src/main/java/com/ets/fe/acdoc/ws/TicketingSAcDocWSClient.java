package com.ets.fe.acdoc.ws;

import com.ets.accountingdoc.collection.TicketingSalesAcDocs;
import com.ets.fe.APIConfig;
import com.ets.fe.acdoc.model.TicketingSalesAcDoc;
import com.ets.fe.accounts.model.AccountsReport;
import com.ets.fe.acdoc.model.report.InvoiceReport;
import com.ets.fe.productivity.model.ProductivityReport;
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

    public Integer delete(long id) {
        String url = APIConfig.get("ws.tsacdoc.delete") + id;
        Integer status = RestClientUtil.deleteById(url);
        return status;
    }

    public TicketingSalesAcDoc _void(TicketingSalesAcDoc ticketingSalesAcDoc) {
        String url = APIConfig.get("ws.tsacdoc.void");
        ticketingSalesAcDoc = RestClientUtil.putEntity(TicketingSalesAcDoc.class, url, ticketingSalesAcDoc);
        return ticketingSalesAcDoc;
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

    /**
     * This method returns summery of invoices, not actual invoice object
     * collection.
     *
     * @param doctype
     * @param clienttype
     * @param clientid
     * @param _dateFrom
     * @param _dateTo
     * @return
     */
    public InvoiceReport outstandingDocumentReport(
            Enums.AcDocType doctype, Enums.ClientType clienttype,
            Long clientid, Date _dateFrom, Date _dateTo) {

        StringBuilder sb = new StringBuilder();
        sb.append(APIConfig.get("ws.tsacdoc.report")).append("?");

        if (_dateFrom != null && _dateTo != null) {
            String dateFrom = DateUtil.dateToString(_dateFrom, "ddMMMyyyy");
            String dateTo = DateUtil.dateToString(_dateTo, "ddMMMyyyy");
            sb.append("dateStart=").append(dateFrom).append("&dateEnd=").append(dateTo);
        }

        if (doctype != null) {
            sb.append("&doctype=").append(doctype);
        }
        if (clienttype != null) {
            sb.append("&clienttype=").append(clienttype);
        }
        if (clientid != null) {
            sb.append("&clientid=").append(clientid);
        }

        InvoiceReport report = RestClientUtil.getEntity(InvoiceReport.class, sb.toString(), new InvoiceReport());
        return report;
    }

    /**
     * This method is for getting collection of invoice ojbect. It is used for
     * batch payment.
     *
     * @param doctype
     * @param clienttype
     * @param clientid
     * @param _dateFrom
     * @param _dateTo
     * @return
     */
    public TicketingSalesAcDocs outstandingInvoices(
            Enums.AcDocType doctype, Enums.ClientType clienttype,
            Long clientid, Date _dateFrom, Date _dateTo) {

        String dateFrom = DateUtil.dateToString(_dateFrom, "ddMMMyyyy");
        String dateTo = DateUtil.dateToString(_dateTo, "ddMMMyyyy");

        String url = APIConfig.get("ws.tsacdoc.dueinvoices") + "?dateStart=" + dateFrom + "&dateEnd=" + dateTo + "&doctype=" + doctype;

        if (clienttype != null) {
            url = url + "&clienttype=" + clienttype;
        }
        if (clientid != null) {
            url = url + "&clientid=" + clientid;
        }

        TicketingSalesAcDocs invoices = RestClientUtil.getEntity(TicketingSalesAcDocs.class, url, new TicketingSalesAcDocs());
        return invoices;
    }

    public InvoiceReport outstandingFlightReport(Enums.ClientType clienttype, Long clientid, Date _dateFrom, Date _dateTo) {
        String dateFrom = DateUtil.dateToString(_dateFrom, "ddMMMyyyy");
        String dateTo = DateUtil.dateToString(_dateTo, "ddMMMyyyy");

        StringBuilder sb = new StringBuilder();
        sb.append(APIConfig.get("ws.tsacdoc.paymentdueflight")).append("?");
        sb.append("dateStart=").append(dateFrom).append("&dateEnd=").append(dateTo);

        if (clienttype != null) {
            sb.append("&clienttype=").append(clienttype);
        }
        if (clientid != null) {
            sb.append("&clientid=").append(clientid);
        }

        InvoiceReport invoices = RestClientUtil.getEntity(InvoiceReport.class, sb.toString(), new InvoiceReport());
        return invoices;
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

    public String documentHistoryReportXML(
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

        String report = RestClientUtil.getXML(url);
        return report;
    }

    public AccountsReport salesAccountsReport(
            Enums.ClientType clienttype, Long clientid, Date _dateFrom, Date _dateTo) {

        String dateFrom = DateUtil.dateToString(_dateFrom, "ddMMMyyyy");
        String dateTo = DateUtil.dateToString(_dateTo, "ddMMMyyyy");

        String url = APIConfig.get("ws.tsacdoc.accounts") + "?dateStart=" + dateFrom + "&dateEnd=" + dateTo;
        if (clienttype != null) {
            url = url + "&clienttype=" + clienttype;
        }
        if (clientid != null) {
            url = url + "&clientid=" + clientid;
        }

        AccountsReport report = RestClientUtil.getEntity(AccountsReport.class, url, new AccountsReport());
        return report;
    }

    public ProductivityReport userProducivityReport(Date _dateFrom, Date _dateTo) {

        String dateFrom = DateUtil.dateToString(_dateFrom, "ddMMMyyyy");
        String dateTo = DateUtil.dateToString(_dateTo, "ddMMMyyyy");

        StringBuilder sb = new StringBuilder();
        sb.append(APIConfig.get("ws.tsacdoc.userproductivity"));
        sb.append("?dateStart=").append(dateFrom).append("&dateEnd=").append(dateTo);

        ProductivityReport report = RestClientUtil.getEntity(ProductivityReport.class, sb.toString(), new ProductivityReport());
        return report;
    }

    public ProductivityReport allAgentDueReport(Date _dateFrom, Date _dateTo) {

        String dateFrom = DateUtil.dateToString(_dateFrom, "ddMMMyyyy");
        String dateTo = DateUtil.dateToString(_dateTo, "ddMMMyyyy");
        
        StringBuilder sb = new StringBuilder(APIConfig.get("ws.tsacdoc.allagentduereport"));
        sb.append("?dateStart=").append(dateFrom).append("&dateEnd=").append(dateTo);

        ProductivityReport report = RestClientUtil.getEntity(ProductivityReport.class, sb.toString(), new ProductivityReport());
        return report;
    }
}
