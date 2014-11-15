package com.ets.util;

import com.ets.fe.model.pnr.Itinerary;
import com.ets.fe.model.pnr.Pnr;
import com.ets.fe.model.pnr.PnrRemark;
import com.ets.fe.model.pnr.Ticket;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public class PnrUtil {

//    public static void intiItineraryInTickets(List<Itinerary> segments, List<Ticket> tickets) {
//
//        for (Ticket ticket : tickets) {
//            ticket.setSegments(segments);
//        }
//    }

    public static List<PnrRemark> initPnrInRemark(Pnr pnr, List<PnrRemark> remarks) {

        for (PnrRemark rm : remarks) {
            rm.setPnr(pnr);
        }
        return remarks;
    }

    public static List<Ticket> initPnrInTickets(Pnr pnr, List<Ticket> tickets) {
        List<Ticket> tempTickets = new ArrayList<>();
        for (Ticket ticket : tickets) {
            ticket.setPnr(pnr);
            tempTickets.add(ticket);
        }
        return tempTickets;
    }

    public static List<Itinerary> initPnrInSegments(Pnr pnr, List<Itinerary> segments) {
        for (Itinerary segment : segments) {
            segment.setPnr(pnr);
        }
        return segments;
    }
}
