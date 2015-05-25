package com.ets.fe.pnr.ws;

import com.ets.fe.pnr.collection.Pnrs;
import com.ets.fe.pnr.model.Pnr;
import com.ets.fe.util.RestClientUtil;
import com.ets.fe.APIConfig;
import com.ets.fe.pnr.model.ATOLCertificate;
import com.ets.fe.pnr.model.Itinerary;
import com.ets.fe.pnr.model.Ticket;
import com.ets.fe.util.DateUtil;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import org.apache.http.HttpResponse;

/**
 *
 * @author Yusuf
 */
public class PnrWSClient {

    public Pnr create(Pnr pnr) {

        return pnr;
    }

    public Pnr update(Pnr pnr) {
        pnr.recordUpdateBy();
        List<Ticket> tickets = pnr.getTickets();
        for (Ticket t : tickets) {
            t.recordUpdateBy();
        }
        
        List<Itinerary> segments = pnr.getSegments();
        for (Itinerary i : segments) {
            i.recordUpdateBy();
        }                
        
        String url = APIConfig.get("ws.pnr.update");
        pnr = RestClientUtil.putEntity(Pnr.class, url, pnr);
        return pnr;
    }

    public Integer delete(long id) {
        
        String date = DateUtil.dateToString(new java.util.Date(), "ddMMMyyyy");
        String url = APIConfig.get("ws.pnr.delete") + id+"?date="+date;
        HttpResponse response = RestClientUtil.deleteByIdGetResponse(url);
              
        RestClientUtil.showMessage(response,"Delete PNR");
        return response.getStatusLine().getStatusCode();
    }

    public Pnr getByDate(Date start, Date end) {
        Pnr pnr = new Pnr();
        return pnr;
    }

    public Pnr getByIdWithChildren(long id) {
        String url = APIConfig.get("ws.pnr.byid") + id;
        Pnr pnr = new Pnr();
        pnr = RestClientUtil.getEntity(Pnr.class, url, pnr);
        return pnr;
    }

    public List<Pnr> searchPnrByName(String surName, String foreName) {
        String url = APIConfig.get("ws.pnr.bypaxname") + "?surName=" + surName;

        if (foreName != null && !foreName.isEmpty()) {
            url = url + "&foreName=" + foreName;
        }

        Pnrs pnrs = new Pnrs();
        pnrs = RestClientUtil.getEntity(Pnrs.class, url, pnrs);
        return pnrs.getList();
    }

    public List<Pnr> searchPnrByGdsPnr(String gdsPnr) {
        String url = APIConfig.get("ws.pnr.bygdsPnr") + "?gdsPnr=" + gdsPnr;

        Pnrs pnrs = new Pnrs();
        pnrs = RestClientUtil.getEntity(Pnrs.class, url, pnrs);
        return pnrs.getList();
    }

    public List<Pnr> getPnrByInvRef(String invref) {
        String url = APIConfig.get("ws.pnr.byginvref") + "?invref=" + invref;

        Pnrs pnrs = new Pnrs();
        pnrs = RestClientUtil.getEntity(Pnrs.class, url, pnrs);
        return pnrs.getList();
    }
        
    public List<Pnr> searchPnrHistory(String bookingAgt, String ticketingAgt, Date from, Date to) {

        String dateFrom = DateUtil.dateToString(from, "ddMMMyyyy");
        String dateTo = DateUtil.dateToString(to, "ddMMMyyyy");

        String url = APIConfig.get("ws.pnr.history") + "?bookingAgtOid=" + bookingAgt + "&ticketingAgtOid=" + ticketingAgt + "&dateStart=" + dateFrom + "&dateEnd=" + dateTo;

        Pnrs pnrs = new Pnrs();
        pnrs = RestClientUtil.getEntity(Pnrs.class, url, pnrs);
        return pnrs.getList();
    }

    public List<Pnr> getPnrsToday() {

        String today = DateUtil.dateToString(new java.util.Date(), "ddMMMyyyy");

        String url = APIConfig.get("ws.pnr.pnrtoday") + "?date=" + today;

        Pnrs pnrs = new Pnrs();
        pnrs = RestClientUtil.getEntity(Pnrs.class, url, pnrs);
        return pnrs.getList();
    }

    public List<Pnr> getUninvoicedPnr() {

        String url = APIConfig.get("ws.pnr.uninvoicedpnr");
        Pnrs pnrs = new Pnrs();
        pnrs = RestClientUtil.getEntity(Pnrs.class, url, pnrs);
        return pnrs.getList();
    }
    
    public ATOLCertificate getAtolCertificate(long pnrid, Date certissuedate) {
        
        String _certissuedate = DateUtil.dateToString(certissuedate, "ddMMMyyyy");
        StringBuilder sb = new StringBuilder(APIConfig.get("ws.pnr.atolcertbyid"));
        sb.append("?pnrid=").append(pnrid);
        sb.append("&certissuedate=").append(_certissuedate);
        
        ATOLCertificate cert = RestClientUtil.getEntity(ATOLCertificate.class, sb.toString(), new ATOLCertificate());
        return cert;
    }
}
