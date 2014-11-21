package com.ets.util;

import com.ets.domain.pnr.Itinerary;
import com.ets.domain.pnr.Pnr;
import com.ets.domain.pnr.PnrRemark;
import com.ets.domain.pnr.Ticket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Yusuf
 */
public class PnrUtil {

    public static void initPnrChildren(Pnr pnr){
    
        PnrUtil.initPnrInRemark(pnr, pnr.getRemarks());
        PnrUtil.initPnrInTickets(pnr, pnr.getTickets());
        PnrUtil.initPnrInSegments(pnr, pnr.getSegments());
        //PnrUtil.initItineraryInTickets(pnr.getSegments(), pnr.getTickets());
    }
    
    public static void undefineChildrenInPnr(Pnr pnr){
        pnr.setTickets(null);
        pnr.setSegments(null);
        pnr.setRemarks(null);
    }
    
    public static void undefinePnrChildren(Pnr pnr){
        PnrUtil.undefinePnrInRemark(pnr, pnr.getRemarks());
        PnrUtil.undefinePnrInTickets(pnr, pnr.getTickets());
        PnrUtil.undefinePnrInSegments(pnr, pnr.getSegments());
        //PnrUtil.undefineItineraryInTickets(pnr.getSegments(), pnr.getTickets());
    }
    
//    public static void initItineraryInTickets(Set<Itinerary> segments, Set<Ticket> tickets) {
//
//        for (Ticket ticket : tickets) {
//            ticket.setSegments(segments);
//        }
//    }
//
//    public static void undefineItineraryInTickets(Set<Itinerary> segments, Set<Ticket> tickets) {
//
//        for (Ticket ticket : tickets) {
//            ticket.setSegments(null);
//        }
//    }

    public static Set<PnrRemark> initPnrInRemark(Pnr pnr, Set<PnrRemark> remarks) {
        for (PnrRemark rm : remarks) {
            rm.setPnr(pnr);
        }
        return remarks;
    }

    public static Set<PnrRemark> undefinePnrInRemark(Pnr pnr, Set<PnrRemark> remarks) {
        for (PnrRemark rm : remarks) {
            rm.setPnr(null);
        }
        return remarks;
    }

    public static Collection initPnrInTickets(Pnr pnr, Collection tickets) {
        Set<Ticket> tempTickets = new LinkedHashSet<>();
        for (Object object : tickets) {
            Ticket ticket = (Ticket) object;
            ticket.setPnr(pnr);
            tempTickets.add(ticket);
        }
        return tempTickets;
    }
    
//    public static Set<Ticket> initPnrInTickets(Pnr pnr, Set<Ticket> tickets) {
//        Set<Ticket> tempTickets = new LinkedHashSet<>();
//        for (Ticket ticket : tickets) {
//            ticket.setPnr(pnr);
//            tempTickets.add(ticket);
//        }
//        return tempTickets;
//    }
//
//    public static List<Ticket> initPnrInTickets(Pnr pnr, List<Ticket> tickets) {
//        List<Ticket> tempTickets = new ArrayList<>();
//        for (Ticket ticket : tickets) {
//            ticket.setPnr(pnr);
//            tempTickets.add(ticket);
//        }
//        return tempTickets;
//    }
        
    public static Set<Ticket> undefinePnrInTickets(Pnr pnr, Set<Ticket> tickets) {
        Set<Ticket> tempTickets = new LinkedHashSet<>();
        for (Ticket ticket : tickets) {
            ticket.setPnr(null);
            tempTickets.add(ticket);
        }
        return tempTickets;
    }

        public static List<Ticket> undefinePnrInTickets(Pnr pnr, List<Ticket> tickets) {
        List<Ticket> tempTickets = new ArrayList<>();
        for (Ticket ticket : tickets) {
            ticket.setPnr(null);
            tempTickets.add(ticket);
        }
        return tempTickets;
    }
        
    public static Set<Itinerary> initPnrInSegments(Pnr pnr, Set<Itinerary> segments) {
        for (Itinerary segment : segments) {
            segment.setPnr(pnr);
        }
        return segments;
    }

    public static Set<Itinerary> undefinePnrInSegments(Pnr pnr, Set<Itinerary> segments) {
        for (Itinerary segment : segments) {
            segment.setPnr(null);
        }
        return segments;
    }

    public static Pnr updatePnr(Pnr oldPnr, Pnr newPnr) {
        oldPnr.setNoOfPax(newPnr.getNoOfPax());
        oldPnr.setBookingAgtOid(newPnr.getBookingAgtOid());
        oldPnr.setTicketingAgtOid(newPnr.getTicketingAgtOid());
        oldPnr.setPnrCreationDate(newPnr.getPnrCreationDate());
        oldPnr.setVendorPNR(newPnr.getVendorPNR());
        oldPnr.setAirLineCode(newPnr.getAirLineCode());
        oldPnr.setRemarks(initPnrInRemark(oldPnr, newPnr.getRemarks()));
        return oldPnr;
    }

    public static Set<Ticket> updateTickets(Collection oldCollection, Collection newCollection) {

        Set<Ticket> oldTickets = new LinkedHashSet<>();
        Set<Ticket> newTickets = new LinkedHashSet<>();
        
        if (oldCollection instanceof ArrayList){
         oldTickets = new LinkedHashSet<>(oldCollection);
        }else{
         oldTickets = (Set<Ticket>) oldCollection;
        }
        
        if (newCollection instanceof ArrayList){
         newTickets = new LinkedHashSet<>(oldCollection);
        }else{
         newTickets = (Set<Ticket>) newCollection;
        }
        
        for (Ticket newTkt : newTickets) {
            boolean exist = false;

            for (Ticket t : oldTickets) {
                if ((newTkt.getTicketNo() == null ? t.getTicketNo() == null : newTkt.getTicketNo().equals(t.getTicketNo()))
                        && newTkt.getTktStatus() == t.getTktStatus()
                        && t.getPaxSurName().equals(newTkt.getPaxSurName())) {
                    exist = true;

                } else if ((t.getTktStatus() == 1 || t.getTktStatus() == 5)
                        && t.getPaxSurName().equals(newTkt.getPaxSurName())) {

                    t.setPaxForeName(newTkt.getPaxForeName());
                    t.setTktStatus(newTkt.getTktStatus());
                    t.setNumericAirLineCode(newTkt.getNumericAirLineCode());
                    t.setTicketNo(newTkt.getTicketNo());
                    t.setBaseFare(newTkt.getBaseFare());
                    t.setTax(newTkt.getTax());
                    t.setDocIssuedate(newTkt.getDocIssuedate());
                    exist = true;
                }
            }
            if (!exist) {
                oldTickets.add(newTkt);
            }
        }
        return oldTickets;
    }

    public static Set<Itinerary> updateSegments(Set<Itinerary> oldSegs, Set<Itinerary> newSegs) {
        Set<Itinerary> finalSegments = new LinkedHashSet();
        Itinerary tempOSeg = new Itinerary();

        for (Itinerary newSeg : newSegs) {
            boolean exist = false;
            loop:
            for (Itinerary oldSeg : oldSegs) {
                tempOSeg = new Itinerary();
                tempOSeg = oldSeg;
                if (oldSeg.getSegmentNo().equals(newSeg.getSegmentNo())) {
                    exist = true;
                    break loop;
                }
            }
            if (exist) {
                finalSegments.add(tempOSeg);
            } else {
                finalSegments.add(newSeg);
            }
        }
        return finalSegments;
    }

}
