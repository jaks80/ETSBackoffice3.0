package com.amadeus.reader;

import com.amadeus.air.AIR;
import com.amadeus.air.AIRToPNRConverter;
import com.amadeus.air.AirUtil;
import com.ets.fe.model.pnr.Itinerary;
import com.ets.fe.model.pnr.Pnr;
import com.ets.fe.model.pnr.Ticket;
import com.ets.collection.Tickets;
import com.ets.fe.model.pnr.PnrRemark;
import com.ets.fe.ws.AirWSClient;
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

        if ("TTP".equals(air.getType())) {
            Pnr pnr = converter.airToPNR();
            List<Ticket> tickets = converter.airToTicket();
            List<Itinerary> segments = converter.airToItinerary();
            List<PnrRemark> remarks = converter.airToPNRRemarks();
            pnr.setTickets(tickets);
            pnr.setSegments(segments);
            pnr.setRemarks(remarks);
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
        }else if ("INV".equals(air.getType()) || "BT".equals(air.getType())) {
            Pnr pnr = converter.airToPNR();
            List<Ticket> tickets = converter.airToTicket();
            List<Itinerary> segments = converter.airToItinerary();
            List<PnrRemark> remarks = converter.airToPNRRemarks();
            pnr.setTickets(tickets);
            pnr.setSegments(segments);
            pnr.setRemarks(remarks);
            
            boolean issued= false;
            for (Ticket t : tickets) {
                if ("ISSUED".equals(t.getTktStatusString())) {
                    issued = true;
                    break;
                }
            }
            
            if(issued){
             ws.issue(pnr);
            }else{
             pnr.setTicketingAgtOid(null);
             ws.book(pnr);
            }
            
        }

        AirUtil.backupAir(air.getFile());
    }
}
