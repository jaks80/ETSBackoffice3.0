package com.ets.pnr.service;

import com.ets.accountingdoc.service.TPurchaseAcDocService;
import com.ets.pnr.dao.TicketDAO;
import com.ets.pnr.domain.Ticket;
import com.ets.pnr.model.GDSSaleReport;
import com.ets.pnr.model.TicketSaleReport;
import com.ets.util.Enums;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Yusuf
 */
@Service("ticketService")
public class TicketService {

    @Resource(name = "ticketDAO")
    private TicketDAO dao;
    @Autowired
    TPurchaseAcDocService tPurchaseAcDocService;

    public Ticket update(Ticket ticket) {
        dao.save(ticket);
        return ticket;
    }

    public int _void(String pnr, String airlineCode, String tktno, String surname) {
        int status = dao.voidTicket(pnr, airlineCode, tktno, surname);
        return status;
    }

    public boolean delete(long id) {
        Ticket t = dao.findByID(Ticket.class, id);
        dao.delete(t);
        return true;
    }

    public void saveBulk(List<Ticket> tickets) {
        dao.saveBulk(tickets);
    }

    public GDSSaleReport saleReport(Enums.TicketingType ticketingType, Enums.TicketStatus ticketStatus, String airLineCode,
            Date issueDateFrom, Date issueDateTo, String ticketingAgtOid) {

        String[] tktedOIDs = null;
        String[] airLineCodes = null;

        if (airLineCode != null) {
            airLineCodes = airLineCode.split(",");
        }

        if (ticketingAgtOid != null) {
            tktedOIDs = ticketingAgtOid.split(",");
        }

        List<Ticket> tickets = dao.saleReport(ticketingType, ticketStatus, airLineCodes, issueDateFrom, issueDateTo, tktedOIDs);
        GDSSaleReport report = new GDSSaleReport(tickets);

        return report;
    }

    public TicketSaleReport saleRevenueReport(Enums.TicketingType ticketingType, Enums.TicketStatus ticketStatus, String airLineCode,
            Date issueDateFrom, Date issueDateTo, String ticketingAgtOid) {

        String[] tktedOIDs = null;
        String[] airLineCodes = null;

        if (airLineCode != null) {
            airLineCodes = airLineCode.split(",");
        }

        if (ticketingAgtOid != null) {
            tktedOIDs = ticketingAgtOid.split(",");
        }

        List<Ticket> tickets = dao.saleRevenueReport(ticketingType, ticketStatus, airLineCodes, issueDateFrom, issueDateTo, tktedOIDs);
        TicketSaleReport report = TicketSaleReport.serializeToSalesSummery(tickets, issueDateFrom, issueDateTo);
        report.setReportTitle("Sale Report: AIR Ticket");
        return report;
    }
}
