package com.ets.accountingdoc.service;

import com.ets.accountingdoc.domain.TicketingSalesAcDoc;
import com.ets.pnr.domain.Ticket;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 * @author Yusuf
 */
public class AcDocUtil {

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

    public static Set<Ticket> undefineTSAcDoc(TicketingSalesAcDoc doc, Set<Ticket> tickets) {
        Set<Ticket> tempTickets = new LinkedHashSet<>();
        for (Ticket ticket : tickets) {
            ticket.setTicketingSalesAcDoc(null);
            tempTickets.add(ticket);
        }
        return tempTickets;
    }
}
