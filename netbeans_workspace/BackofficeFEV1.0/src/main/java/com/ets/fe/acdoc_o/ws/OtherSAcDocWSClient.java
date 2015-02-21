package com.ets.fe.acdoc_o.ws;

import com.ets.accountingdoc.collection.OtherSalesAcDocs;
import com.ets.fe.APIConfig;
import com.ets.fe.acdoc_o.model.OtherSalesAcDoc;
import com.ets.fe.accounts.model.AccountsReport;
import com.ets.fe.acdoc_o.model.InvoiceReportOther;
import com.ets.fe.util.DateUtil;
import com.ets.fe.util.Enums;
import com.ets.fe.util.RestClientUtil;
import java.util.Date;

/**
 *
 * @author Yusuf
 */
public class OtherSAcDocWSClient {
    
        public OtherSalesAcDoc create(OtherSalesAcDoc otherSalesAcDoc) {
        String url = APIConfig.get("ws.osacdoc.new");
        OtherSalesAcDoc persistedDoc = RestClientUtil.postEntity(OtherSalesAcDoc.class, url, otherSalesAcDoc);
        return persistedDoc;
    }

    public OtherSalesAcDoc update(OtherSalesAcDoc otherSalesAcDoc) {
        otherSalesAcDoc.recordUpdateBy();
        String url = APIConfig.get("ws.osacdoc.update");
        OtherSalesAcDoc persistedDoc = RestClientUtil.postEntity(OtherSalesAcDoc.class, url, otherSalesAcDoc);
        return persistedDoc;
    }

    public Integer delete(long id) {
        String url = APIConfig.get("ws.osacdoc.delete")+ id;
        Integer status = RestClientUtil.deleteById(url);
        return status;
    }

    public OtherSalesAcDoc _void(OtherSalesAcDoc otherSalesAcDoc) {
        String url = APIConfig.get("ws.osacdoc.void");
        otherSalesAcDoc = RestClientUtil.putEntity(OtherSalesAcDoc.class, url, otherSalesAcDoc);
        return otherSalesAcDoc;
    }

    public OtherSalesAcDoc getbyId(long id) {
        String url = APIConfig.get("ws.osacdoc.byid") + id;
        OtherSalesAcDoc doc = RestClientUtil.getEntity(OtherSalesAcDoc.class, url, new OtherSalesAcDoc());
        return doc;
    }

    public OtherSalesAcDocs getByRefNo(Integer refference) {
        String url = APIConfig.get("ws.osacdoc.byref")+ refference;
        OtherSalesAcDocs docs = RestClientUtil.getEntity(OtherSalesAcDocs.class, url, new OtherSalesAcDocs());
        return docs;
    }
        
        public InvoiceReportOther outstandingDocumentReport(
            Enums.AcDocType doctype, Enums.ClientType clienttype,
            Long clientid, Date _dateFrom, Date _dateTo) {

        String dateFrom = DateUtil.dateToString(_dateFrom, "ddMMMyyyy");
        String dateTo = DateUtil.dateToString(_dateTo, "ddMMMyyyy");

        String url = APIConfig.get("ws.osacdoc.report") + "?dateStart=" + dateFrom + "&dateEnd=" + dateTo + "&doctype=" + doctype;

        if (clienttype != null) {
            url = url + "&clienttype=" + clienttype;
        }
        if (clientid != null) {
            url = url + "&clientid=" + clientid;
        }

        InvoiceReportOther report = RestClientUtil.getEntity(InvoiceReportOther.class, url, new InvoiceReportOther());
        return report;
    }

    public OtherSalesAcDocs outstandingInvoices(
            Enums.AcDocType doctype, Enums.ClientType clienttype,
            Long clientid, Date _dateFrom, Date _dateTo) {

        String dateFrom = DateUtil.dateToString(_dateFrom, "ddMMMyyyy");
        String dateTo = DateUtil.dateToString(_dateTo, "ddMMMyyyy");

        String url = APIConfig.get("ws.osacdoc.dueinvoices") + "?dateStart=" + dateFrom + "&dateEnd=" + dateTo + "&doctype=" + doctype;

        if (clienttype != null) {
            url = url + "&clienttype=" + clienttype;
        }
        if (clientid != null) {
            url = url + "&clientid=" + clientid;
        }

        OtherSalesAcDocs invoices = RestClientUtil.getEntity(OtherSalesAcDocs.class, url, new OtherSalesAcDocs());
        return invoices;
    }

    public InvoiceReportOther documentHistoryReport(
            Enums.ClientType clienttype, Long clientid, Date _dateFrom, Date _dateTo) {

        String dateFrom = DateUtil.dateToString(_dateFrom, "ddMMMyyyy");
        String dateTo = DateUtil.dateToString(_dateTo, "ddMMMyyyy");

        String url = APIConfig.get("ws.osacdoc.history") + "?dateStart=" + dateFrom + "&dateEnd=" + dateTo;
        if (clienttype != null) {
            url = url + "&clienttype=" + clienttype;
        }
        if (clientid != null) {
            url = url + "&clientid=" + clientid;
        }

        InvoiceReportOther report = RestClientUtil.getEntity(InvoiceReportOther.class, url, new InvoiceReportOther());
        return report;
    }
    
     public AccountsReport salesAccountsReport(
            Enums.ClientType clienttype, Long clientid, Date _dateFrom, Date _dateTo) {

        String dateFrom = DateUtil.dateToString(_dateFrom, "ddMMMyyyy");
        String dateTo = DateUtil.dateToString(_dateTo, "ddMMMyyyy");

        String url = APIConfig.get("ws.osacdoc.accounts") + "?dateStart=" + dateFrom + "&dateEnd=" + dateTo;
        if (clienttype != null) {
            url = url + "&clienttype=" + clienttype;
        }
        if (clientid != null) {
            url = url + "&clientid=" + clientid;
        }

        AccountsReport report = RestClientUtil.getEntity(AccountsReport.class, url, new AccountsReport());
        return report;
    }

}
