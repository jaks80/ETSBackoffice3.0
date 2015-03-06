package com.ets.fe.os.ws;

import com.ets.fe.APIConfig;
import com.ets.fe.os.model.AdditionalCharge;
import com.ets.fe.os.model.AdditionalCharges;
import com.ets.fe.util.RestClientUtil;

/**
 *
 * @author Yusuf
 */
public class AdditionalChargeWSClient {
    
 public AdditionalCharges find() {

        String url = APIConfig.get("ws.ac.charges");
        return RestClientUtil.getEntity(AdditionalCharges.class, url, new AdditionalCharges());

    }

    public AdditionalCharge create(AdditionalCharge additionalCharge) {
        String url = APIConfig.get("ws.ac.new");
        AdditionalCharge persistedAgent = RestClientUtil.postEntity(AdditionalCharge.class, url, additionalCharge);
        return persistedAgent;
    }

    public AdditionalCharge update(AdditionalCharge additionalCharge) {
        String url = APIConfig.get("ws.ac.update");
        AdditionalCharge persistedAdditionalCharge = RestClientUtil.putEntity(AdditionalCharge.class, url, additionalCharge);
        return persistedAdditionalCharge;
    }

    public Integer delete(long id) {
        String url = APIConfig.get("ws.ac.delete");
        Integer status = RestClientUtil.deleteById(url);
        return status;
    }
}
