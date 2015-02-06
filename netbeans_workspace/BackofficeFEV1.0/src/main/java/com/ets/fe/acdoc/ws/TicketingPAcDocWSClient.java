package com.ets.fe.acdoc.ws;

import com.ets.accountingdoc.collection.TicketingPurchaseAcDocs;
import com.ets.fe.APIConfig;
import com.ets.fe.acdoc.model.TicketingPurchaseAcDoc;
import com.ets.fe.acdoc.model.report.AccountsReport;
import com.ets.fe.acdoc.model.report.InvoiceReport;
import com.ets.fe.util.DateUtil;
import com.ets.fe.util.Enums;
import com.ets.fe.util.RestClientUtil;
import java.util.Date;

/**
 *
 * @author Yusuf
 */
public class TicketingPAcDocWSClient {

    public TicketingPurchaseAcDoc create(TicketingPurchaseAcDoc ticketingPurchaseAcDoc) {
        String url = APIConfig.get("ws.tpacdoc.new");
        TicketingPurchaseAcDoc persistedDoc = RestClientUtil.postEntity(TicketingPurchaseAcDoc.class, url, ticketingPurchaseAcDoc);
        return persistedDoc;
    }

    public TicketingPurchaseAcDoc createNewPayment(TicketingPurchaseAcDoc payment) {
        String url = APIConfig.get("ws.tsacdoc.newpayment");
        TicketingPurchaseAcDoc persistedPayment = RestClientUtil.postEntity(TicketingPurchaseAcDoc.class, url, payment);
        return persistedPayment;
    }

    public TicketingPurchaseAcDoc update(TicketingPurchaseAcDoc ticketingPurchaseAcDoc) {
        String url = APIConfig.get("ws.tpacdoc.update");
        TicketingPurchaseAcDoc persistedDoc = RestClientUtil.putEntity(TicketingPurchaseAcDoc.class, url, ticketingPurchaseAcDoc);
        return persistedDoc;
    }

    public Integer delete(long id) {
        String url = APIConfig.get("ws.tpacdoc.delete") + id;
        Integer status = RestClientUtil.deleteById(url);
        return status;
    }

    public Integer _void(long id) {
        String url = APIConfig.get("ws.tpacdoc.void") + id;
        Integer status = RestClientUtil.deleteById(url);
        return status;
    }

    public TicketingPurchaseAcDoc getbyId(long id) {
        String url = APIConfig.get("ws.tpacdoc.byid")+ id;
        TicketingPurchaseAcDoc doc = RestClientUtil.getEntity(TicketingPurchaseAcDoc.class, url, new TicketingPurchaseAcDoc());
        return doc;
    }

    public TicketingPurchaseAcDocs getByRefNo(Integer refference) {
        String url = APIConfig.get("ws.tpacdoc.byref" + refference);
        TicketingPurchaseAcDocs docs = RestClientUtil.getEntity(TicketingPurchaseAcDocs.class, url, new TicketingPurchaseAcDocs());
        return docs;
    }

    public TicketingPurchaseAcDocs getByGDSPnr(String pnr) {
        String url = APIConfig.get("ws.tpacdoc.bypnr" + pnr);
        TicketingPurchaseAcDocs docs = RestClientUtil.getEntity(TicketingPurchaseAcDocs.class, url, new TicketingPurchaseAcDocs());
        return docs;
    }

    public TicketingPurchaseAcDocs getByPnrId(Long pnrId) {
        String url = APIConfig.get("ws.tpacdoc.bypnrid") + "?pnrId=" + pnrId;
        TicketingPurchaseAcDocs docs = RestClientUtil.getEntity(TicketingPurchaseAcDocs.class, url, new TicketingPurchaseAcDocs());
        return docs;
    }

     public TicketingPurchaseAcDocs outstandingInvoices(Enums.AcDocType doctype,Long agentid, Date _dateFrom, Date _dateTo) {

        String dateFrom = DateUtil.dateToString(_dateFrom, "ddMMMyyyy");
        String dateTo = DateUtil.dateToString(_dateTo, "ddMMMyyyy");

        String url = APIConfig.get("ws.tpacdoc.dueinvoices") + "?dateStart=" + dateFrom + "&dateEnd=" + dateTo + "&doctype=" + doctype;

        if (agentid != null) {
            url = url + "&agentid=" + agentid;
        }

        TicketingPurchaseAcDocs report = RestClientUtil.getEntity(TicketingPurchaseAcDocs.class, url, new TicketingPurchaseAcDocs());
        return report;
    }
        
    public InvoiceReport outstandingDocumentReport(Enums.AcDocType doctype,Long agentid, Date _dateFrom, Date _dateTo) {

        String dateFrom = DateUtil.dateToString(_dateFrom, "ddMMMyyyy");
        String dateTo = DateUtil.dateToString(_dateTo, "ddMMMyyyy");

        String url = APIConfig.get("ws.tpacdoc.report") + "?dateStart=" + dateFrom + "&dateEnd=" + dateTo + "&doctype=" + doctype;

        if (agentid != null) {
            url = url + "&agentid=" + agentid;
        }

        InvoiceReport report = RestClientUtil.getEntity(InvoiceReport.class, url, new InvoiceReport());
        return report;
    }

    public InvoiceReport documentHistoryReport(Long agentid, Date _dateFrom, Date _dateTo) {

        String dateFrom = DateUtil.dateToString(_dateFrom, "ddMMMyyyy");
        String dateTo = DateUtil.dateToString(_dateTo, "ddMMMyyyy");

        String url = APIConfig.get("ws.tpacdoc.history") + "?dateStart=" + dateFrom + "&dateEnd=" + dateTo;
        if (agentid != null) {
            url = url + "&agentid=" + agentid;
        }

        InvoiceReport report = RestClientUtil.getEntity(InvoiceReport.class, url, new InvoiceReport());
        return report;
    }
    
    public AccountsReport purchaseAccountsReport(
            Long clientid, Date _dateFrom, Date _dateTo) {

        String dateFrom = DateUtil.dateToString(_dateFrom, "ddMMMyyyy");
        String dateTo = DateUtil.dateToString(_dateTo, "ddMMMyyyy");

        String url = APIConfig.get("ws.tpacdoc.accounts") + "?dateStart=" + dateFrom + "&dateEnd=" + dateTo;
        
        if (clientid != null) {
            url = url + "&clientid=" + clientid;
        }

        AccountsReport report = RestClientUtil.getEntity(AccountsReport.class, url, new AccountsReport());
        return report;
    }
}
