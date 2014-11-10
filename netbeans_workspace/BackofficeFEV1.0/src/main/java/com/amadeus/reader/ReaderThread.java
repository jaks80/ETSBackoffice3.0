package com.amadeus.reader;

import com.amadeus.air.AIR;
import com.amadeus.air.AIRToPNRConverter;
import com.amadeus.air.AirUtil;
import com.ets.fe.model.pnr.Itinerary;
import com.ets.fe.model.pnr.Pnr;
import com.ets.fe.model.pnr.Ticket;
import com.ets.collection.Tickets;
import com.ets.fe.ws.air.AirWSClient;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public class ReaderThread implements Runnable {

    private final AIR air;

    public ReaderThread(AIR air) {
        this.air = air;
    }

    public void run() {

        AIRToPNRConverter converter = new AIRToPNRConverter(this.air);
        AirWSClient ws = new AirWSClient();

        if ("INV".equals(air.getType()) || "TTP".equals(air.getType())) {
            Pnr pnr = converter.airToPNR();
            List<Ticket> tickets = converter.airToTicket();
            List<Itinerary> segments = converter.airToItinerary();
            pnr.setTickets(tickets);
            pnr.setSegments(segments);
            ws.issue(pnr);

        } else if ("TRFP".equals(air.getType())) {

            List<Ticket> list = converter.airToRefundedTicket();
            Tickets tickets = new Tickets();
            tickets.setList(list);
            ws.refundTicket(tickets);
        } else if ("VOID".equals(air.getType())) {
            List<Ticket> list = converter.airToVoidTicket();
            Tickets tickets = new Tickets();
            tickets.setList(list);
            ws.voidTicket(tickets);
        }

        AirUtil.backupAir(air.getFile());
    }
}
