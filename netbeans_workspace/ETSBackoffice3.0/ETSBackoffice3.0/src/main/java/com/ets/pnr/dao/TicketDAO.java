package com.ets.pnr.dao;

import com.ets.GenericDAO;
import com.ets.pnr.domain.Ticket;
import com.ets.util.Enums;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public interface TicketDAO extends GenericDAO<Ticket, Long>{
    
public List<Ticket> saleRevenueReport(Enums.TicketStatus ticketStatus, String[] airLineCode, Date from, Date to,String... ticketingAgtOid);

public List<Ticket> saleReport(Enums.TicketStatus ticketStatus, String[] airLineCode, Date from, Date to,String... ticketingAgtOid);

public List<Ticket> saleReportDetails(int ticketingType, Integer gdsId, Long tktingAgtID,
             Enums.TicketStatus tktStatus,String career, Date from, Date to,String bookingOid);
}
