package com.ets.fe.pnr.ws;

import com.ets.fe.pnr.model.GDSSaleReport;
import com.ets.fe.util.RestClientUtil;
import com.ets.fe.APIConfig;
import com.ets.fe.pnr.model.TicketSaleReport;
import com.ets.fe.util.DateUtil;
import com.ets.fe.util.Enums.*;
import java.util.Date;

/**
 *
 * @author Yusuf
 */
public class TicketWSClient {

    public GDSSaleReport gdsSaleReport(TicketingType ticketingType,TicketStatus ticketStatus, String airLineCode,
            Date issueDateFrom, Date issueDateTo, String ticketingAgtOid) {

        String dateFrom = DateUtil.dateToString(issueDateFrom, "ddMMMyyyy");
        String dateTo = DateUtil.dateToString(issueDateTo, "ddMMMyyyy");        

        StringBuilder sb = new StringBuilder();
        sb.append(APIConfig.get("ws.ticket.gds-salereport"))
                .append("?dateStart=").append(dateFrom)
                .append("&dateEnd=").append(dateTo);
        
        if(ticketingType!=null){
         sb.append("&ticketingType=").append(ticketingType);
        }
        
        if(ticketStatus!=null){
         sb.append("&ticketStatus=").append(ticketStatus);
        }
        if(airLineCode!=null){
         sb.append("&airLineCode=").append(airLineCode);
        }
        if(ticketingAgtOid!=null){
         sb.append("&ticketingAgtOid=").append(ticketingAgtOid);
        }
                
        GDSSaleReport report = RestClientUtil.getEntity(GDSSaleReport.class, sb.toString(), new GDSSaleReport());
        return report;
    }
    
    public TicketSaleReport saleReport(TicketingType ticketingType,TicketStatus ticketStatus, String airLineCode,
            Date issueDateFrom, Date issueDateTo, String ticketingAgtOid) {

        String dateFrom = DateUtil.dateToString(issueDateFrom, "ddMMMyyyy");
        String dateTo = DateUtil.dateToString(issueDateTo, "ddMMMyyyy");

        StringBuilder sb = new StringBuilder();
        sb.append(APIConfig.get("ws.ticket.salereport"))
                .append("?dateStart=").append(dateFrom)
                .append("&dateEnd=").append(dateTo);
        
        if(ticketingType!=null){
         sb.append("&ticketingType=").append(ticketingType);
        }
        
        if(ticketStatus!=null){
         sb.append("&ticketStatus=").append(ticketStatus);
        }
        if(airLineCode!=null){
         sb.append("&airLineCode=").append(airLineCode);
        }
        if(ticketingAgtOid!=null){
         sb.append("&ticketingAgtOid=").append(ticketingAgtOid);
        }
        
        TicketSaleReport report = RestClientUtil.getEntity(TicketSaleReport.class, sb.toString(), new TicketSaleReport());
        return report;
    }
}
