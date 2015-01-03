package com.ets.accountingdoc.logic;

import com.ets.accountingdoc.domain.TicketingSalesAcDoc;
import com.ets.pnr.domain.Pnr;
import com.ets.pnr.domain.Ticket;
import com.ets.util.Enums;
import com.ets.util.PnrUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
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
        invoice.setType(Enums.AcDocType.INVOICE);
        //invoice.setPnr(pnr);
        //invoice.setTickets(tickets);
        invoice.setDocIssueDate(getEarliestDate(tickets));
        invoice.setStatus(Enums.AcDocStatus.ACTIVE);
        Set<Ticket> ticketsToInv = new LinkedHashSet<>();
        for (Ticket t : this.tickets) {
            if (t.getTicketingSalesAcDoc() == null && invoice.getDocIssueDate().equals(t.getDocIssuedate())) {
                ticketsToInv.add(t);
            }
        }

        if (ticketsToInv.isEmpty()) {
            return null;
        }
        tickets.removeAll(ticketsToInv);
        invoice.setTickets(ticketsToInv);
        invoice.setPnr(pnr);
        return invoice;
    }

    public TicketingSalesAcDoc createNewTicketingDebitMemo(TicketingSalesAcDoc invoice) {

        Set<Ticket> unInvoicedTickets = PnrUtil.getUnRefundedTickets(pnr);
        TicketingSalesAcDoc debitMemo = new TicketingSalesAcDoc();
        debitMemo.setType(Enums.AcDocType.DEBITMEMO);
        debitMemo.setPnr(pnr);
        debitMemo.setDocIssueDate(getEarliestDate(unInvoicedTickets));
        debitMemo.setStatus(Enums.AcDocStatus.ACTIVE);

        for (Ticket t : unInvoicedTickets) {
            if (t.getTicketingSalesAcDoc() == null && debitMemo.getDocIssueDate().equals(t.getDocIssuedate())) {
                debitMemo.addTicket(t);
            }
        }
        if (debitMemo.getTickets().isEmpty()) {
            return null;
        }

        return debitMemo;
    }

    public TicketingSalesAcDoc createNewTicketingCMemo(TicketingSalesAcDoc invoice) {

        Set<Ticket> unRefundedTickets = PnrUtil.getUnRefundedTickets(pnr);
        Set<Ticket> invoicedTickets = PnrUtil.getIssuedInvoicedTickets(pnr);

        List<Ticket> ticketsToCredit = new ArrayList<>();

        for (Ticket current_tkt : invoicedTickets) {
            for (Ticket unRefundedTicket : unRefundedTickets) {
                if (unRefundedTicket.getOrginalTicketNo() != null) {
                    if (unRefundedTicket.getOrginalTicketNo().equals(current_tkt.getTicketNo())) {
                        unRefundedTicket.setTicketingSalesAcDoc(current_tkt.getTicketingSalesAcDoc());
                        ticketsToCredit.add(unRefundedTicket);
                    }
                } else {
                    if (unRefundedTicket.getTicketNo().equals(current_tkt.getTicketNo())) {
                        unRefundedTicket.setTicketingSalesAcDoc(current_tkt.getTicketingSalesAcDoc());
                        ticketsToCredit.add(unRefundedTicket);
                    }
                }
            }
        }

        if (ticketsToCredit.isEmpty()) {
            return null;
        }

        TicketingSalesAcDoc creditMemo = new TicketingSalesAcDoc();
        creditMemo.setAccountingDocument(invoice);
        creditMemo.setReference(invoice.getReference());
        creditMemo.setPnr(pnr);
        creditMemo.setStatus(Enums.AcDocStatus.ACTIVE);
        creditMemo.setType(Enums.AcDocType.CREDITMEMO);
        creditMemo.setDocIssueDate(getEarliestDate(new LinkedHashSet(ticketsToCredit)));

        for (Ticket t : ticketsToCredit) {
            if (Objects.equals(t.getTicketingSalesAcDoc().getId(), invoice.getId())) {
                creditMemo.addTicket(t);
            }
        }

        return creditMemo;
    }

    public Date getEarliestDate(Set<Ticket> tickets) {

        Date date = new java.util.Date();

        for (Ticket t : tickets) {
            if (t.getDocIssuedate().before(date) && t.getTicketingSalesAcDoc() == null) {
                date = t.getDocIssuedate();
            }
        }
        return date;
    }
}
