package com.ets.fe.acdoc.ws;

import com.ets.accountingdoc.collection.TicketingSalesAcDocs;
import com.ets.fe.APIConfig;
import com.ets.fe.acdoc.model.TicketingSalesAcDoc;
import com.ets.fe.util.RestClientUtil;

/**
 *
 * @author Yusuf
 */
public class TicketingSAcDocWSClient {

    public TicketingSalesAcDoc newDraftInvoice(Long pnrid) {
        String url = APIConfig.get("ws.tsacdoc.newinv") + pnrid;
        TicketingSalesAcDoc persistedDoc = RestClientUtil.postEntity(TicketingSalesAcDoc.class, url, new TicketingSalesAcDoc());
        return persistedDoc;
    }

    public TicketingSalesAcDoc newInvoice(TicketingSalesAcDoc ticketingSalesAcDoc) {
        String url = APIConfig.get("ws.tsacdoc.newinv");
        TicketingSalesAcDoc persistedDoc = RestClientUtil.postEntity(TicketingSalesAcDoc.class, url, ticketingSalesAcDoc);
        return persistedDoc;
    }

     public TicketingSalesAcDoc newDraftDebitMemo(Long pnrid) {
        String url = APIConfig.get("ws.tsacdoc.newtdraftdm")+ pnrid;
        TicketingSalesAcDoc draftDebitMemo = RestClientUtil.getEntity(TicketingSalesAcDoc.class, url, new TicketingSalesAcDoc());
        return draftDebitMemo;
    }
        
    public TicketingSalesAcDoc newDraftTCreditMemo(Long pnrid) {
        String url = APIConfig.get("ws.tsacdoc.newtdraftcrm")+ pnrid;
        TicketingSalesAcDoc persistedDoc = RestClientUtil.getEntity(TicketingSalesAcDoc.class, url, new TicketingSalesAcDoc());
        return persistedDoc;
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
        String url = APIConfig.get("ws.tsacdoc.delete" + id);
        Integer status = RestClientUtil.deleteById(url);
        return status;
    }

    public Integer _void(long id) {
        String url = APIConfig.get("ws.tsacdoc.void" + id);
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
        String url = APIConfig.get("ws.tsacdoc.bypnr" + pnr);
        TicketingSalesAcDocs docs = RestClientUtil.getEntity(TicketingSalesAcDocs.class, url, new TicketingSalesAcDocs());
        return docs;
    }
}
