package com.ets.fe.ws;

import com.ets.fe.model.report.sales.TicketSaleReport;
import com.ets.util.APIConfig;
import com.ets.util.DateUtil;
import java.util.Date;

/**
 *
 * @author Yusuf
 */
public class TicketWSClient {

    public TicketSaleReport saleReport(String ticketStatus, String airLineCode,
            Date issueDateFrom, Date issueDateTo, String ticketingAgtOid) {

        String dateFrom = DateUtil.dateToString(issueDateFrom, "ddMMMyyyy");
        String dateTo = DateUtil.dateToString(issueDateTo, "ddMMMyyyy");

        String url = APIConfig.get("ws.ticket.gds-salereport")
                + "?ticketStatus=" + ticketStatus + "&airLineCode=" + airLineCode
                + "&dateStart=" + dateFrom + "&dateEnd=" + dateTo + "&ticketingAgtOid=" + ticketingAgtOid;

        TicketSaleReport report = new TicketSaleReport();
        report = RestClientUtil.getEntity(TicketSaleReport.class, url, report);
        return report;
    }
}
