package com.ets.util;

import com.ets.pnr.domain.Itinerary;
import com.ets.pnr.domain.Pnr;
import com.ets.pnr.domain.Remark;
import com.ets.pnr.domain.Ticket;
import com.ets.util.Enums.TicketStatus;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Yusuf
 */
public class PnrUtil {

    public static void initPnrChildren(Pnr pnr) {

        PnrUtil.initPnrInRemark(pnr, pnr.getRemarks());
        PnrUtil.initPnrInTickets(pnr, pnr.getTickets());
        PnrUtil.initPnrInSegments(pnr, pnr.getSegments());

    }

    public static void undefineChildrenInPnr(Pnr pnr) {
        pnr.setTickets(null);
        pnr.setSegments(null);
        pnr.setRemarks(null);
    }

    public static void undefinePnrChildren(Pnr pnr) {
        if(pnr.getRemarks()!=null){
         PnrUtil.undefinePnrInRemark(pnr, pnr.getRemarks());
        }
        PnrUtil.undefinePnrInTickets(pnr, pnr.getTickets());
        PnrUtil.undefinePnrInSegments(pnr, pnr.getSegments());
    }

    public static Set<Remark> initPnrInRemark(Pnr pnr, Set<Remark> remarks) {

        for (Remark rm : remarks) {
            rm.setPnr(pnr);
        }
        return remarks;
    }

    public static Set<Remark> undefinePnrInRemark(Pnr pnr, Set<Remark> remarks) {
        for (Remark rm : remarks) {
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
        oldPnr.setTicketingAgentSine(newPnr.getTicketingAgentSine());
        oldPnr.setPnrCreatorAgentSine(newPnr.getPnrCreatorAgentSine());
        oldPnr.setPnrCreationDate(newPnr.getPnrCreationDate());
        oldPnr.setVendorPNR(newPnr.getVendorPNR());
        oldPnr.setAirLineCode(newPnr.getAirLineCode());
        oldPnr.setRemarks(initPnrInRemark(oldPnr, newPnr.getRemarks()));
        return oldPnr;
    }

    public static Set<Ticket> updateTickets(Collection oldCollection, Collection newCollection) {

        Set<Ticket> oldTickets = new LinkedHashSet<>();
        Set<Ticket> newTickets = new LinkedHashSet<>();

        if (oldCollection instanceof ArrayList) {
            oldTickets = new LinkedHashSet<>(oldCollection);
        } else {
            oldTickets = (Set<Ticket>) oldCollection;
        }

        if (newCollection instanceof ArrayList) {
            newTickets = new LinkedHashSet<>(oldCollection);
        } else {
            newTickets = (Set<Ticket>) newCollection;
        }

        for (Ticket newTkt : newTickets) {
            boolean exist = false;

            for (Ticket t : oldTickets) {
                if ((newTkt.getTicketNo() == null ? t.getTicketNo() == null : newTkt.getTicketNo().equals(t.getTicketNo()))
                        && newTkt.getTktStatus() == t.getTktStatus()
                        && t.getSurName().equals(newTkt.getSurName())) {
                    exist = true;
                    break;
                } else if ((t.getTktStatus() == TicketStatus.BOOK || t.getTktStatus() == TicketStatus.VOID)
                        && t.getSurName().equals(newTkt.getSurName())) {

                    t.setForeName(newTkt.getForeName());
                    t.setTktStatus(newTkt.getTktStatus());
                    t.setNumericAirLineCode(newTkt.getNumericAirLineCode());
                    t.setTicketNo(newTkt.getTicketNo());
                    t.setBaseFare(newTkt.getBaseFare());
                    t.setTax(newTkt.getTax());
                    t.setDocIssuedate(newTkt.getDocIssuedate());
                    exist = true;
                    break;
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

    public static Set<Ticket> getUnRefundedTickets(Pnr pnr) {
        Set<Ticket> refundTickets = new LinkedHashSet<>();

        for (Ticket t : pnr.getTickets()) {
            if (t.getTktStatus().equals(Enums.TicketStatus.REFUND) && t.getTicketingSalesAcDoc() == null && t.getGrossFare().compareTo(new BigDecimal("0.00")) != 0) {
                refundTickets.add(t);
            }
        }
        return refundTickets;
    }

    public static Set<Ticket> getUnInvoicedTicket(Pnr pnr) {
        Set<Ticket> tickets = new LinkedHashSet<>();

        for (Ticket t : pnr.getTickets()) {
            if (!t.getTktStatus().equals(Enums.TicketStatus.REFUND)
                    && !t.getTktStatus().equals(Enums.TicketStatus.VOID)
                    && t.getTicketingSalesAcDoc() == null) {
                tickets.add(t);
            }
        }
        return tickets;
    }

    public static Set<Ticket> getUnInvoicedReIssuedTicket(Pnr pnr) {
        Set<Ticket> tickets = new LinkedHashSet<>();

        for (Ticket t : pnr.getTickets()) {
            if (t.getTktStatus().equals(Enums.TicketStatus.REISSUE)
                    && t.getTicketingSalesAcDoc() == null) {
                tickets.add(t);
            }
        }
        return tickets;
    }

    public static Set<Ticket> getIssuedInvoicedTickets(Pnr pnr) {
        Set<Ticket> invoicedTickets = new LinkedHashSet<>();

        for (Ticket t : pnr.getTickets()) {
            if ((t.getTktStatus().equals(Enums.TicketStatus.ISSUE)
                    || t.getTktStatus().equals(Enums.TicketStatus.REISSUE))
                    && t.getTicketingSalesAcDoc() != null) {
                invoicedTickets.add(t);
            }
        }
        return invoicedTickets;
    }

    public static String getOutBoundFlightSummery(Set<Itinerary> segments) {
        StringBuilder sb = new StringBuilder();
        Itinerary fs = getFirstSegment(segments);
        
        sb.append(fs.getDeptDate()+"/");
        sb.append(fs.getDeptFrom()+"-"+fs.getDeptTo()+"/");
        sb.append(fs.getAirLineID());        
        
        return sb.toString();
    }

    public static Itinerary getFirstSegment(Set<Itinerary> segments) {
        Itinerary firstSegment = null;
        Long index = null;

        for (Itinerary i : segments) {
            if (index == null) {
                index = i.getId();
                firstSegment = i;
            } else {
                if (i.getId() < index) {
                    index = i.getId();
                    firstSegment = i;
                }
            }

        }
        return firstSegment;
    }
    
    public static String calculateLeadPaxName(Set<Ticket> ticket_list) {
        Ticket leadPax = null;
        int paxNo = 99;

        for (Ticket t : ticket_list) {
            if (t.getPassengerNo() <= paxNo && (!t.isChild() && !t.isInfant())) {
                leadPax = t;
                paxNo = t.getPassengerNo();
            }
        }
        if (leadPax != null) {
            return leadPax.getFullPaxName();
        } else {
            Iterator<Ticket> iterator = ticket_list.iterator();
            Ticket setElement = new Ticket();
            while (iterator.hasNext()) {
                setElement = iterator.next();
                break;
            }
            return setElement.getFullPaxName();
        }
    }
}
