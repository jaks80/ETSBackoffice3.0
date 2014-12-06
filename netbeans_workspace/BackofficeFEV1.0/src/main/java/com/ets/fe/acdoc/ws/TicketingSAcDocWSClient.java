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

    public TicketingSalesAcDoc create(TicketingSalesAcDoc ticketingSalesAcDoc) {
        String url = APIConfig.get("ws.tsacdoc.new");
        TicketingSalesAcDoc persistedDoc = RestClientUtil.postEntity(TicketingSalesAcDoc.class, url, ticketingSalesAcDoc);
        return persistedDoc;
    }

    public TicketingSalesAcDoc update(TicketingSalesAcDoc ticketingSalesAcDoc) {
        String url = APIConfig.get("ws.tsacdoc.update");
        TicketingSalesAcDoc persistedDoc = RestClientUtil.postEntity(TicketingSalesAcDoc.class, url, ticketingSalesAcDoc);
        return persistedDoc;
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
        String url = APIConfig.get("ws.tsacdoc.byid" + id);
        TicketingSalesAcDoc doc = RestClientUtil.getEntity(TicketingSalesAcDoc.class, url, new TicketingSalesAcDoc());
        return doc;
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
