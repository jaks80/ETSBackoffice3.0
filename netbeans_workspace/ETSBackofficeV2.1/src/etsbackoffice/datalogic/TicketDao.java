/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package etsbackoffice.datalogic;

import etsbackoffice.domain.Ticket;
import etsbackoffice.domain.TicketRefundDetails;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Yusuf
 */
public interface TicketDao {

public void save(Ticket ticket);

public void saveAll(Set<Ticket> tickets);

public void deleteTicket(Ticket ticket);

public boolean isExistInDb(String tktNo, String surName, int status);
        
public void deleteAllTicket(List<Ticket> ticket);

public TicketRefundDetails loadTicketRfdDetails(long id);

public void voidTicket(String pnr, String tktno, String surname);

public Ticket loadTicketItinerary(long ticketId);

public List<Ticket> saleReport(int ticketingType, Integer gdsId, Long tktingAgtID,Integer tktStatus,String career, Date from, Date to);

public List<Ticket> saleReportDetails(int ticketingType, Integer gdsId, Long tktingAgtID,
             Integer tktStatus,String career, Date from, Date to,String bookingOid);
}
