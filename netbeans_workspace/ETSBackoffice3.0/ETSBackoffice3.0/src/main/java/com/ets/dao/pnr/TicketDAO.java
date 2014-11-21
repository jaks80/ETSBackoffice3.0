package com.ets.dao.pnr;

import com.ets.dao.generic.GenericDAO;
import com.ets.domain.pnr.Ticket;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public interface TicketDAO extends GenericDAO<Ticket, Long>{
    
public List<Ticket> saleReport(Integer tktStatus, String[] airLineCode, Date from, Date to,String... ticketingOID);

public List<Ticket> saleReportDetails(int ticketingType, Integer gdsId, Long tktingAgtID,
             Integer tktStatus,String career, Date from, Date to,String bookingOid);
}
