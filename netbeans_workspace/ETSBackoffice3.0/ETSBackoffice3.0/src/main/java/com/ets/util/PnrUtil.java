package com.ets.util;

import com.ets.domain.pnr.Itinerary;
import com.ets.domain.pnr.Pnr;
import com.ets.domain.pnr.PnrRemark;
import com.ets.domain.pnr.Ticket;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 * @author Yusuf
 */
public class PnrUtil {

    public static void initItineraryInTickets(Set<Itinerary> segments, Set<Ticket> tickets) {

        tickets.stream().forEach((ticket) -> {
            ticket.setSegments(segments);
        });
    }

    public static void undefineItineraryInTickets(Set<Itinerary> segments, Set<Ticket> tickets) {

        tickets.stream().forEach((ticket) -> {
            ticket.setSegments(null);
        });
    }
    
    public static Set<PnrRemark> initPnrInRemark(Pnr pnr, Set<PnrRemark> remarks) {
        remarks.stream().forEach((rm) -> {
            rm.setPnr(pnr);
        });
        return remarks;
    }

    public static Set<PnrRemark> undefinePnrInRemark(Pnr pnr, Set<PnrRemark> remarks) {
        remarks.stream().forEach((rm) -> {
            rm.setPnr(null);
        });
        return remarks;
    }
    
    public static Set<Ticket> initPnrInTickets(Pnr pnr, Set<Ticket> tickets) {
        Set<Ticket> tempTickets = new LinkedHashSet<>();
        tickets.stream().map((ticket) -> {
            ticket.setPnr(pnr);
            return ticket;
        }).forEach((ticket) -> {
            tempTickets.add(ticket);
        });
        return tempTickets;
    }
    
    public static Set<Ticket> undefinePnrInTickets(Pnr pnr, Set<Ticket> tickets) {
        Set<Ticket> tempTickets = new LinkedHashSet<>();
        tickets.stream().map((ticket) -> {
            ticket.setPnr(null);
            return ticket;
        }).forEach((ticket) -> {
            tempTickets.add(ticket);
        });
        return tempTickets;
    }

    public static Set<Itinerary> initPnrInSegments(Pnr pnr, Set<Itinerary> segments) {
        segments.stream().forEach((segment) -> {
            segment.setPnr(pnr);
        });
        return segments;
    }
    
    public static Set<Itinerary> undefinePnrInSegments(Pnr pnr, Set<Itinerary> segments) {
        segments.stream().forEach((segment) -> {
            segment.setPnr(null);
        });
        return segments;
    }
}
