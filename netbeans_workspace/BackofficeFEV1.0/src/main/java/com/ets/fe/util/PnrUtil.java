package com.ets.fe.util;

import com.ets.fe.pnr.model.Itinerary;
import com.ets.fe.pnr.model.Pnr;
import com.ets.fe.pnr.model.PnrRemark;
import com.ets.fe.pnr.model.Ticket;
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
