package com.ets.fe.acdoc.ws;

import com.ets.accountingdoc.collection.OtherSalesAcDocs;
import com.ets.fe.APIConfig;
import com.ets.fe.acdoc.model.OtherSalesAcDoc;
import com.ets.fe.util.RestClientUtil;

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
        String url = APIConfig.get("ws.osacdoc.update");
        OtherSalesAcDoc persistedDoc = RestClientUtil.postEntity(OtherSalesAcDoc.class, url, otherSalesAcDoc);
        return persistedDoc;
    }

    public Integer delete(long id) {
        String url = APIConfig.get("ws.osacdoc.delete" + id);
        Integer status = RestClientUtil.deleteById(url);
        return status;
    }

    public Integer _void(long id) {
        String url = APIConfig.get("ws.osacdoc.void" + id);
        Integer status = RestClientUtil.deleteById(url);
        return status;
    }

    public OtherSalesAcDoc getbyId(long id) {
        String url = APIConfig.get("ws.osacdoc.byid" + id);
        OtherSalesAcDoc doc = RestClientUtil.getEntity(OtherSalesAcDoc.class, url, new OtherSalesAcDoc());
        return doc;
    }

    public OtherSalesAcDocs getByRefNo(Integer refference) {
        String url = APIConfig.get("ws.osacdoc.byref" + refference);
        OtherSalesAcDocs docs = RestClientUtil.getEntity(OtherSalesAcDocs.class, url, new OtherSalesAcDocs());
        return docs;
    }

    public OtherSalesAcDocs getByGDSPnr(String pnr) {
        String url = APIConfig.get("ws.osacdoc.bypnr" + pnr);
        OtherSalesAcDocs docs = RestClientUtil.getEntity(OtherSalesAcDocs.class, url, new OtherSalesAcDocs());
        return docs;
    }
}
