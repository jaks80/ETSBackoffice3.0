package com.ets.fe.ws.pnr;

import com.ets.collection.Tickets;
import com.ets.fe.model.pnr.Ticket;
import com.ets.fe.ws.RestClientUtil;
import com.ets.util.APIConfig;
import com.ets.util.DateUtil;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public class TicketWSClient {

    public List<Ticket> searchPnrHistory(Integer ticketStatus, String careerCode,
            Date issueDateFrom, Date issueDateTo, String ticketingAgtOid) {

        String dateFrom = DateUtil.dateToString(issueDateFrom, "dd-MM-yyyy");
        String dateTo = DateUtil.dateToString(issueDateTo, "dd-MM-yyyy");

        String url = APIConfig.get("ws.ticket.gds-salereport")
                + "?ticketStatus=" + ticketStatus + "&careerCode=" + careerCode
                + "&dateStart=" + dateFrom + "&dateEnd=" + dateTo + "&ticketingAgtOid=" + ticketingAgtOid;

        Tickets tickets = new Tickets();
        tickets = RestClientUtil.getEntity(Tickets.class, url, tickets);
        return tickets.getList();
    }
}
