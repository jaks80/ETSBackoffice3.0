package com.ets.fe.pnr.ws;

import com.ets.fe.pnr.collection.Pnrs;
import com.ets.fe.pnr.model.Pnr;
import com.ets.fe.util.RestClientUtil;
import com.ets.fe.APIConfig;
import com.ets.fe.util.DateUtil;
import java.util.Date;
import java.util.List;
import javax.ws.rs.core.Response;

/**
 *
 * @author Yusuf
 */
public class PnrWSClient {

    public Pnr create(Pnr pnr) {

        return pnr;
    }

    public Pnr update(Pnr pnr) {

        return pnr;
    }

    public Response delete(long id) {
        return Response.status(200).build();
    }

    public Pnr getByDate(Date start, Date end) {
        Pnr pnr = new Pnr();
        return pnr;
    }

    public Pnr getById(long id) {
        Pnr pnr = new Pnr();
        return pnr;
    }

    public Pnr getByIdWithChildren(long id) {
        Pnr pnr = new Pnr();
        return pnr;
    }

    public List<Pnr> searchPnrByTktNo(String tktNo, String surName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Pnr> searchPnrByName(String surName, String foreName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Pnr> searchPnrHistory(String bookingAgt, String ticketingAgt, Date from, Date to) {
        
        String dateFrom = DateUtil.dateToString(from, "ddMMMyyyy");
        String dateTo = DateUtil.dateToString(to, "ddMMMyyyy");
        
        String url = APIConfig.get("ws.pnr.history")+"?bookingAgtOid="+bookingAgt+"&ticketingAgtOid="+ticketingAgt+"&dateStart="+dateFrom+"&dateEnd="+dateTo;
        
        Pnrs pnrs = new Pnrs();
        pnrs = RestClientUtil.getEntity(Pnrs.class, url, pnrs);
        return pnrs.getList();
    }
}
