package com.ets.fe.ws;

import com.ets.collection.Careers;
import com.ets.fe.model.pnr.Airline;
import com.ets.fe.ws.RestClientUtil;
import com.ets.util.APIConfig;

/**
 *
 * @author Yusuf
 */
public class CareerWSClient {

    public Airline find(String code) {

        String url = APIConfig.get("ws.career.find") + code;
        return RestClientUtil.getEntity(Airline.class, url, new Airline());
    }

    public Careers match(String name) {

        String url = APIConfig.get("ws.career.match") + name;
        return RestClientUtil.getEntity(Careers.class, url, new Careers());
    }

    public void save(Airline career) {
        String url = APIConfig.get("ws.career.save");
        RestClientUtil.putEntity(Airline.class, url, new Airline());
    }

    public void saveBulk(Careers careers) {
        String url = APIConfig.get("ws.career.savebulk");
        RestClientUtil.putEntity(Careers.class, url, new Careers());
    }
}
