package com.ets.fe.acdoc.ws;

import com.ets.accountingdoc.collection.TicketingPurchaseAcDocs;
import com.ets.fe.APIConfig;
import com.ets.fe.acdoc.model.TicketingPurchaseAcDoc;
import com.ets.fe.accounts.model.AccountsReport;
import com.ets.fe.acdoc.model.BSPReport;
import com.ets.fe.acdoc.model.report.InvoiceReport;
import com.ets.fe.productivity.model.ProductivityReport;
import com.ets.fe.util.DateUtil;
import com.ets.fe.util.Enums;
import com.ets.fe.util.RestClientUtil;
import java.util.Date;
import javax.swing.JOptionPane;
import org.apache.http.HttpResponse;

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

    public TicketingPurchaseAcDoc update(TicketingPurchaseAcDoc ticketingPurchaseAcDoc) {
        ticketingPurchaseAcDoc.recordUpdateBy();
        String url = APIConfig.get("ws.tpacdoc.update");
        TicketingPurchaseAcDoc persistedDoc = RestClientUtil.putEntity(TicketingPurchaseAcDoc.class, url, ticketingPurchaseAcDoc);
        return persistedDoc;
    }

    public Integer delete(long id) {
        String url = APIConfig.get("ws.tpacdoc.delete") + id;
        HttpResponse response = RestClientUtil.deleteByIdGetResponse(url); 
        RestClientUtil.showMessage(response,"Delete Document");        
        return response.getStatusLine().getStatusCode();
    }

    public TicketingPurchaseAcDoc _void(TicketingPurchaseAcDoc ticketingPurchaseAcDoc) {
        String url = APIConfig.get("ws.tpacdoc.void");
        ticketingPurchaseAcDoc = RestClientUtil.putEntity(TicketingPurchaseAcDoc.class, url, ticketingPurchaseAcDoc);
        return ticketingPurchaseAcDoc;
    }

    public TicketingPurchaseAcDoc getbyId(long id) {
        String url = APIConfig.get("ws.tpacdoc.byid") + id;
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

    public BSPReport outstandingBSPReport(Long agentid, Date _dateFrom, Date _dateTo) {

        String dateFrom = DateUtil.dateToString(_dateFrom, "ddMMMyyyy");
        String dateTo = DateUtil.dateToString(_dateTo, "ddMMMyyyy");

        StringBuilder sb = new StringBuilder(APIConfig.get("ws.tpacdoc.duebspreport"));
        sb.append("?agentid=").append(agentid);
        sb.append("&dateStart=").append(dateFrom);
        sb.append("&dateEnd=").append(dateTo);

        BSPReport report = RestClientUtil.getEntity(BSPReport.class, sb.toString(), new BSPReport());
        return report;
    }

    public TicketingPurchaseAcDocs outstandingInvoices(Enums.TicketingType ticketingType, Enums.AcDocType doctype, Long agentid, Date _dateFrom, Date _dateTo) {

        String dateFrom = DateUtil.dateToString(_dateFrom, "ddMMMyyyy");
        String dateTo = DateUtil.dateToString(_dateTo, "ddMMMyyyy");

        String url = APIConfig.get("ws.tpacdoc.dueinvoices") + "?dateStart=" + dateFrom
                + "&dateEnd=" + dateTo;
        url = url + "&ticketingtype=" + ticketingType;

        if (doctype != null) {
            url = url + "&doctype=" + doctype;
        }

        if (agentid != null) {
            url = url + "&agentid=" + agentid;
        }

        TicketingPurchaseAcDocs report = RestClientUtil.getEntity(TicketingPurchaseAcDocs.class, url, new TicketingPurchaseAcDocs());
        return report;
    }

    public InvoiceReport outstandingDocumentReport(Enums.AcDocType doctype, Long agentid, Date _dateFrom, Date _dateTo) {

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

    public ProductivityReport allAgentDueReport(Date _dateFrom, Date _dateTo) {

        String dateFrom = DateUtil.dateToString(_dateFrom, "ddMMMyyyy");
        String dateTo = DateUtil.dateToString(_dateTo, "ddMMMyyyy");
        
        StringBuilder sb = new StringBuilder(APIConfig.get("ws.tpacdoc.allagentduereport"));        
        sb.append("?dateStart=").append(dateFrom).append("&dateEnd=").append(dateTo);
        ProductivityReport report = RestClientUtil.getEntity(ProductivityReport.class, sb.toString(), new ProductivityReport());
        return report;
    }
}
