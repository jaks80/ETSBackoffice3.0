package com.ets.fe.acdoc.ws;

import com.ets.accountingdoc.collection.TicketingPurchaseAcDocs;
import com.ets.fe.APIConfig;
import com.ets.fe.acdoc.model.TicketingPurchaseAcDoc;
import com.ets.fe.util.RestClientUtil;

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
        String url = APIConfig.get("ws.tpacdoc.update");
        TicketingPurchaseAcDoc persistedDoc = RestClientUtil.postEntity(TicketingPurchaseAcDoc.class, url, ticketingPurchaseAcDoc);
        return persistedDoc;
    }

    public Integer delete(long id) {
        String url = APIConfig.get("ws.tpacdoc.delete" + id);
        Integer status = RestClientUtil.deleteById(url);
        return status;
    }

    public Integer _void(long id) {
        String url = APIConfig.get("ws.tpacdoc.void" + id);
        Integer status = RestClientUtil.deleteById(url);
        return status;
    }

    public TicketingPurchaseAcDoc getbyId(long id) {
        String url = APIConfig.get("ws.tpacdoc.byid" + id);
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
}
