package com.ets.air;

import com.ets.pnr.domain.Itinerary;
import com.ets.pnr.domain.Pnr;
import com.ets.pnr.domain.Remark;
import com.ets.pnr.domain.Ticket;
import com.ets.air.service.AirService;
import com.ets.util.Enums;
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

        if (null != air.getType()) {
            switch (air.getType()) {
                case "TTP": {
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
                    break;
                }
                case "TRFP": {
                    List<Ticket> tickets = converter.airToRefundedTicket();
                    for (AIR a : air.getMorePages()) {
                        converter = new AIRToPNRConverter(a);
                        tickets.addAll(converter.airToTicket());
                    }
                    service.refundTicket(tickets);
                    break;
                }
                case "VOID": {
                    List<Ticket> tickets = converter.airToVoidTicket();
                    for (AIR a : air.getMorePages()) {
                        converter = new AIRToPNRConverter(a);
                        tickets.addAll(converter.airToTicket());
                    }
                    service.voidTicket(tickets);
                    break;
                }
                case "INV":
                case "BT":
                case "ET": {
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
                    break;
                }
            }
        }
    }

    public void setAir(AIR air) {
        this.air = air;
    }

}
