package com.ets.fe.pnr.ws;

import com.ets.fe.pnr.collection.Airlines;
import com.ets.fe.pnr.model.Airline;
import com.ets.fe.util.RestClientUtil;
import com.ets.fe.APIConfig;

/**
 *
 * @author Yusuf
 */
public class AirlineWSClient {

    public Airline find(String code) {

        String url = APIConfig.get("ws.airline.find") + code;
        return RestClientUtil.getEntity(Airline.class, url, new Airline());
    }

    public void save(Airline airline) {
        String url = APIConfig.get("ws.airline.save");
        RestClientUtil.postEntity(Airline.class, url, airline);
    }

    public void saveBulk(Airlines airlines) {
        String url = APIConfig.get("ws.airline.savebulk");
        RestClientUtil.postEntity(Airlines.class, url, new Airlines());
    }
}
