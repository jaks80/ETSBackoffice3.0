package com.ets.pnr.service;

import com.ets.pnr.dao.TicketDAO;
import com.ets.pnr.domain.Ticket;
import com.ets.pnr.model.GDSSaleReport;
import com.ets.pnr.model.TicketSaleReport;
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
    
    public GDSSaleReport saleReport(Enums.TicketStatus ticketStatus, String airLineCode, 
            Date issueDateFrom, Date issueDateTo,String ticketingAgtOid) {
                
        String[] tktedOIDs = null;
        String[] airLineCodes = null;
        
          if(airLineCode !=null){
         airLineCodes = airLineCode.split(",");
        }
        
        if(ticketingAgtOid !=null){
         tktedOIDs = ticketingAgtOid.split(",");
        }                                
        
        List<Ticket> tickets = dao.saleReport(ticketStatus, airLineCodes, issueDateFrom, issueDateTo, tktedOIDs);
        GDSSaleReport report = new GDSSaleReport(tickets);
        
        return report;
    }
    
    public TicketSaleReport saleRevenueReport(Enums.TicketStatus ticketStatus, String airLineCode, 
            Date issueDateFrom, Date issueDateTo,String ticketingAgtOid) {
               
        String[] tktedOIDs = null;
        String[] airLineCodes = null;
        
        if(airLineCode !=null){
         airLineCodes = airLineCode.split(",");
        }
        
        if(ticketingAgtOid !=null){
         tktedOIDs = ticketingAgtOid.split(",");
        }                            
        
        List<Ticket> tickets = dao.saleRevenueReport(ticketStatus, airLineCodes, issueDateFrom, issueDateTo, tktedOIDs);
        TicketSaleReport report = TicketSaleReport.serializeToSalesSummery(tickets,issueDateFrom, issueDateTo);
        report.setReportTitle("Sale Report: AIR Ticket");
        return report;
    }
}
