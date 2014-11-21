package com.ets.fe.ws;

import com.ets.fe.model.pnr.Pnr;
import com.ets.collection.Tickets;
import com.ets.util.APIConfig;

/**
 *
 * @author Yusuf
 */
public class AirWSClient {


    public Pnr book(Pnr pnr){
        Pnr persistentPnr = RestClientUtil.postEntity(Pnr.class, APIConfig.get("ws.air.book"),pnr);
        return persistentPnr;
    }

    public Pnr issue(Pnr pnr){
        Pnr persistentPnr = RestClientUtil.postEntity(Pnr.class, APIConfig.get("ws.air.issue"),pnr);
        return persistentPnr;
    }

    public Pnr reIssue(Pnr pnr) {
        return RestClientUtil.postEntity(Pnr.class, APIConfig.get("ws.air.reissue"),pnr);
    }

    public Tickets refundTicket(Tickets tickets) {
        Tickets persistentTkts = RestClientUtil.postEntity(Tickets.class, APIConfig.get("ws.air.refund"),tickets);
        return persistentTkts;
    }

    public void voidTicket(Tickets tickets) {
        RestClientUtil.putEntity(Tickets.class, APIConfig.get("ws.air.void"),tickets);
    }
}
