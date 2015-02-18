package com.ets.fe.pnr.ws;

import com.ets.fe.APIConfig;
import com.ets.fe.pnr.collection.Remarks;
import com.ets.fe.util.RestClientUtil;
import javax.ws.rs.QueryParam;

/**
 *
 * @author Yusuf
 */
public class RemarkWSClient {

    public Remarks create(Remarks remarks) {
        String url = APIConfig.get("ws.remark.new");
        remarks = RestClientUtil.postEntity(Remarks.class, url, remarks);
        return remarks;
    }

    public Remarks getByPnrId(@QueryParam("pnrId") Long pnrId) {
        String url = APIConfig.get("ws.remark.bypnrid") + "?pnrId=" + pnrId;
        Remarks remarks = RestClientUtil.getEntity(Remarks.class, url, new Remarks());
        return remarks;
    }
}
