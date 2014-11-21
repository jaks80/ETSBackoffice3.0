package com.ets.service.pnr;

import com.ets.collection.Tickets;
import com.ets.dao.pnr.TicketDAO;
import com.ets.domain.pnr.Ticket;
import com.ets.model.report.TicketSaleReport;
import com.ets.util.DateUtil;
import com.ets.util.Enums;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 *
 * @author Yusuf
 */
@Service("ticketService")
public class TicketService {

    @Resource(name = "ticketDAO")
    private TicketDAO dao;
    
    public TicketSaleReport saleReport(String tktStatus, String airLineCode, 
            String issueDateFrom, String issueDateTo,String ticketingAgtOid) {
        
        Integer status = null;
        String[] tktedOIDs = null;
        String[] airLineCodes = null;
        
        if(!"null".equals(tktStatus)){
          status = Enums.TicketStatus.valueOf(tktStatus).getId();        
        }
        
        if("null".equals(airLineCode)){
         airLineCode = null;
        }else{
         airLineCodes = airLineCode.split(",");
        }
        
        if("null".equals(ticketingAgtOid)){
         ticketingAgtOid = null;
        }else{
         tktedOIDs = ticketingAgtOid.split(",");
        }                
         
        Date dateFrom = DateUtil.stringToDate(issueDateFrom, "ddMMMyyyy");
        Date dateTo = DateUtil.stringToDate(issueDateTo, "ddMMMyyyy");        
        
        List<Ticket> tickets = dao.saleReport(status, airLineCodes, dateFrom, dateTo, tktedOIDs);
        TicketSaleReport report = new TicketSaleReport(tickets);
        
        return report;
    }
}
