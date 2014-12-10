package com.ets.air;

import com.ets.pnr.domain.Itinerary;
import com.ets.pnr.domain.Pnr;
import com.ets.pnr.domain.Remark;
import com.ets.pnr.domain.Ticket;
import com.ets.air.service.AirService;
import java.util.LinkedHashSet;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 *
 * @author Yusuf
 */
@Service("aIRReader")
public class AIRReader {

    @Resource(name = "airService")
    private AirService service;

    private AIR air;

    public AIRReader() {

    }

    public void startReading() {

        AIRToPNRConverter converter = new AIRToPNRConverter(this.air);

        if ("TTP".equals(air.getType())) {
            Pnr pnr = converter.airToPNR();
            List<Ticket> tickets = converter.airToTicket();
            List<Itinerary> segments = converter.airToItinerary();
            List<Remark> remarks = converter.airToPNRRemarks();

            for (AIR a : air.getMorePages()) {
                converter = new AIRToPNRConverter(a);
                tickets.addAll(converter.airToTicket());
            }

            pnr.setTickets(new LinkedHashSet(tickets));
            pnr.setSegments(new LinkedHashSet(segments));
            pnr.setRemarks(new LinkedHashSet(remarks));
            service.savePnr(pnr);
            //ws.issue(pnr);

        } else if ("TRFP".equals(air.getType())) {

            List<Ticket> tickets = converter.airToRefundedTicket();

            for (AIR a : air.getMorePages()) {
                converter = new AIRToPNRConverter(a);
                tickets.addAll(converter.airToTicket());
            }
            service.refundTicket(tickets);
            //ws.refundTicket(tickets);
        } else if ("VOID".equals(air.getType())) {
            List<Ticket> tickets = converter.airToVoidTicket();
            for (AIR a : air.getMorePages()) {
                converter = new AIRToPNRConverter(a);
                tickets.addAll(converter.airToTicket());
            }
            service.voidTicket(tickets);
            //ws.voidTicket(tickets);
        } else if ("INV".equals(air.getType()) || "BT".equals(air.getType()) || "ET".equals(air.getType())) {
            Pnr pnr = converter.airToPNR();
            List<Ticket> tickets = converter.airToTicket();
            List<Itinerary> segments = converter.airToItinerary();
            List<Remark> remarks = converter.airToPNRRemarks();

            for (AIR a : air.getMorePages()) {
                converter = new AIRToPNRConverter(a);
                tickets.addAll(converter.airToTicket());
            }

            pnr.setTickets(new LinkedHashSet(tickets));
            pnr.setSegments(new LinkedHashSet(segments));
            pnr.setRemarks(new LinkedHashSet(remarks));

            boolean issued = false;
            for (Ticket t : tickets) {
                if ("ISSUE".equals(t.getTktStatusString())) {
                    issued = true;
                    break;
                }
            }

            if (issued) {
                service.savePnr(pnr);               
            } else {
                pnr.setTicketingAgtOid(null);
                pnr.setTicketingAgentSine(null);
                service.savePnr(pnr);              
            }
        }
    }

    public void setAir(AIR air) {
        this.air = air;
    }

}
