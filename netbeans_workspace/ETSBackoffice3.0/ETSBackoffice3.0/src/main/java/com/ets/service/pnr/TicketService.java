package com.ets.service.pnr;

import com.ets.collection.Tickets;
import com.ets.dao.pnr.TicketDAO;
import com.ets.domain.pnr.Ticket;
import com.ets.util.DateUtil;
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
    
    public List<Ticket> saleReport(Integer tktStatus, String careerCode, 
            String issueDateFrom, String issueDateTo,String ticketingAgtOid) {
                
        if("null".equals(careerCode)){
         careerCode = null;
        }
        
        if("null".equals(ticketingAgtOid)){
         ticketingAgtOid = null;
        }
        
        String[] tktedOIDs = ticketingAgtOid.split(",");
         
        Date dateFrom = DateUtil.stringToDate(issueDateFrom, "dd-MM-yyyy");
        Date dateTo = DateUtil.stringToDate(issueDateTo, "dd-MM-yyyy");
        
        List<Ticket> tickets = dao.saleReport(tktStatus, careerCode, dateFrom, dateTo, tktedOIDs);
        
        return tickets;
    }
}
