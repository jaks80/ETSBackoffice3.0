package com.ets.accountingdoc.logic;

import com.ets.accountingdoc.domain.TicketingSalesAcDoc;
import com.ets.pnr.domain.Pnr;
import com.ets.pnr.domain.Ticket;
import com.ets.util.Enums;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Ticketing accounting document business logic. Invoice date should be
 * ticketing date. For multiple Ticket Issue date there should be multiple
 * Invoice. Ticket batch is sorted according to date.
 *
 * @author Yusuf
 */
public class TicketingAcDocBL {

    private Pnr pnr;
    private Set<Ticket> tickets = new LinkedHashSet<>();

    public TicketingAcDocBL(Pnr pnr, Set<Ticket> tickets) {
        this.pnr = pnr;
        this.tickets = tickets;
    }

    public TicketingAcDocBL(Pnr pnr) {
        this.pnr = pnr;
        this.tickets = pnr.getTickets();
    }

    public TicketingSalesAcDoc createNewTicketingInvoice() {

        TicketingSalesAcDoc invoice = new TicketingSalesAcDoc();
        invoice.setAcDoctype(Enums.AcDocType.INVOICE);
        invoice.setPnr(pnr);
        invoice.setTickets(tickets);
        invoice.setDocIssueDate(getEarliestDate());

        Set<Ticket> ticketsToInv = new LinkedHashSet<>();
        for (Ticket t : this.tickets) {
            if (t.getTicketingSalesAcDoc() == null && invoice.getDocIssueDate().equals(t.getDocIssuedate())) {
                ticketsToInv.add(t);
            }
        }

        if(ticketsToInv.isEmpty()){
         return null;
        }
        tickets.removeAll(ticketsToInv);
        invoice.setTickets(ticketsToInv);
        invoice.setPnr(pnr);
        return invoice;
    }

    public Date getEarliestDate() {

        Date date = new java.util.Date();

        for (Ticket t : tickets) {
            if (t.getDocIssuedate().before(date)) {
                date = t.getDocIssuedate();
            }
        }
        return date;
    }
}
