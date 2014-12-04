package com.ets.fe.os.model.ws;

import com.ets.fe.APIConfig;
import com.ets.fe.os.model.OtherService;
import com.ets.fe.os.model.OtherServices;
import com.ets.fe.util.RestClientUtil;

/**
 *
 * @author Yusuf
 */
public class OtherServiceWSClient {
    
 public OtherServices find() {

        String url = APIConfig.get("ws.os.otherservices");
        return RestClientUtil.getEntity(OtherServices.class, url, new OtherServices());

    }

    public OtherService create(OtherService otherService) {
        String url = APIConfig.get("ws.os.new");
        OtherService persistedAgent = RestClientUtil.postEntity(OtherService.class, url, otherService);
        return persistedAgent;
    }

    public OtherService update(OtherService otherService) {
        String url = APIConfig.get("ws.os.update");
        OtherService persistedOtherService = RestClientUtil.putEntity(OtherService.class, url, otherService);
        return persistedOtherService;
    }

    public Integer delete(long id) {
        String url = APIConfig.get("ws.os.delete");
        Integer status = RestClientUtil.deleteById(url);
        return status;
    }
}
