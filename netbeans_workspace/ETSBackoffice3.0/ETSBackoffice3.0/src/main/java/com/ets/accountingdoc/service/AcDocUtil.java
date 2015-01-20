package com.ets.accountingdoc.service;

import com.ets.accountingdoc.domain.AccountingDocument;
import com.ets.accountingdoc.domain.AccountingDocumentLine;
import com.ets.accountingdoc.domain.OtherSalesAcDoc;
import com.ets.accountingdoc.domain.Payment;
import com.ets.accountingdoc.domain.TicketingPurchaseAcDoc;
import com.ets.accountingdoc.domain.TicketingSalesAcDoc;
import com.ets.pnr.domain.Ticket;
import com.ets.util.Enums;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 * @author Yusuf
 */
public class AcDocUtil {

    public static Set<TicketingSalesAcDoc> filterVoidRelatedDocuments(Set<TicketingSalesAcDoc> relatedDocuments) {
        Set<TicketingSalesAcDoc> filteredDocs = new LinkedHashSet<>();

        for (TicketingSalesAcDoc d : relatedDocuments) {
            if (!d.getStatus().equals(Enums.AcDocStatus.VOID)) {
                filteredDocs.add(d);
            }
        }
        return filteredDocs;
    }

    public static void undefineTSAcDocumentInPayment(TicketingSalesAcDoc a) {
        if (a.getPayment()!= null)
        {
            a.getPayment().settSalesAcDocuments(null);
            a.getPayment().settPurchaseAcDocuments(null);
            a.getPayment().setoSalesAcDocuments(null);
        }
    }

    public static Long generateAcDocRef(Long lastInvRef) {
        if (lastInvRef != null) {
            return ++lastInvRef;
        } else {
            return Long.valueOf("1001");
        }
    }

    public static Set<Ticket> initTSAcDocInTickets(TicketingSalesAcDoc doc, Set<Ticket> tickets) {
        Set<Ticket> tempTickets = new LinkedHashSet<>();
        for (Ticket t : tickets) {
            t.setTicketingSalesAcDoc(doc);
        }
        return tempTickets;
    }

    public static Set<AccountingDocumentLine> initAcDocInLine(AccountingDocument doc, Set<AccountingDocumentLine> lines) {
        Set<AccountingDocumentLine> tempLines = new LinkedHashSet<>();
        for (AccountingDocumentLine line : lines) {
            if (doc instanceof TicketingSalesAcDoc) {
                line.setTicketingSalesAcDoc((TicketingSalesAcDoc) doc);
            } else if (doc instanceof TicketingPurchaseAcDoc) {
                line.setTicketingPurchaseAcDoc((TicketingPurchaseAcDoc) doc);
            } else if (doc instanceof OtherSalesAcDoc) {
                line.setOtherSalesAcDoc((OtherSalesAcDoc) doc);
            }
        }
        return tempLines;
    }

    public static Set<AccountingDocumentLine> UndefineAcDocInLine(AccountingDocument doc, Set<AccountingDocumentLine> lines) {
        Set<AccountingDocumentLine> tempLines = new LinkedHashSet<>();
        for (AccountingDocumentLine line : lines) {
            if (doc instanceof TicketingSalesAcDoc) {
                line.setTicketingSalesAcDoc(null);
            } else if (doc instanceof TicketingPurchaseAcDoc) {
                line.setTicketingPurchaseAcDoc(null);
            } else if (doc instanceof OtherSalesAcDoc) {
                line.setOtherSalesAcDoc(null);
            }
        }
        return tempLines;
    }

    public static Set<Ticket> undefineTSAcDoc(TicketingSalesAcDoc doc, Set<Ticket> tickets) {
        Set<Ticket> tempTickets = new LinkedHashSet<>();
        for (Ticket ticket : tickets) {
            ticket.setTicketingSalesAcDoc(null);
            tempTickets.add(ticket);
        }
        return tempTickets;
    }
}
