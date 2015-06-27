package com.ets.fe.util;

import com.ets.fe.pnr.model.Itinerary;
import com.ets.fe.pnr.model.Pnr;
import com.ets.fe.pnr.model.Remark;
import com.ets.fe.pnr.model.Ticket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Yusuf
 */
public class PnrUtil {

    public static String[] splitName(String name) {
        String[] nameElement = null;
        String surName = null;
        String foreName = null;

        if (name != null && name.contains("/")) {
            nameElement = name.split("/");
            surName = nameElement[0].trim();
            foreName = nameElement[1].trim();
        } else if (name != null) {
            surName = name.trim();
        }
        String[] names = {surName, foreName};
        return names;
    }

    public static List<Remark> initPnrInRemark(Pnr pnr, List<Remark> remarks) {

        for (Remark rm : remarks) {
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

    public static boolean validateOfficeIdFormat(String officeIdString) {
        return false;
    }

    public static List<String> splitOfficeId(String officeIdString) {
        List<String> list = new ArrayList<>();
        if (officeIdString.contains(",")) {
            list = Arrays.asList(officeIdString.split(","));
        } else {
            list.add(officeIdString);
        }

        return list;
    }

    public static String calculateLeadPaxName(List<Ticket> ticket_list) {
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
            Ticket setElement = null;
            while (iterator.hasNext()) {
                setElement = iterator.next();
                break;
            }
            if(setElement!=null){
             return setElement.getFullPaxName();
            }else{
             return "";
            }
        }
    }

    public static String calculatePartialName(String name) {
        if (name.length() < 10) {
            return name;
        } else {
            return name.substring(0, 10);
        }
    }

    public static String calculatePartialName(String name,int length) {
        if (name.length() < length) {
            return name;
        } else {
            return name.substring(0, length);
        }
    }
        
    public static Enums.TicketStatus tjqStatusConverter(String TRNC) {

        switch (TRNC) {
            case "TKTT":
                return Enums.TicketStatus.ISSUE;
            case "CANX":
                return Enums.TicketStatus.REFUND;
                case "RFND":
                return Enums.TicketStatus.REFUND;
            default:
                return null;
        }
    }
    
    public static Set<Ticket> getUnInvoicedBookedTicket(Pnr pnr, Enums.SaleType saleType) {
        Set<Ticket> tickets = new LinkedHashSet<>();

        for (Ticket t : pnr.getTickets()) {
            if (t.getTktStatus().equals(Enums.TicketStatus.BOOK)) {

                if (saleType.equals(Enums.SaleType.TKTSALES) && t.getTicketingSalesAcDoc() == null) {
                    tickets.add(t);
                } else if (saleType.equals(Enums.SaleType.TKTPURCHASE) && t.getTicketingPurchaseAcDoc() == null) {
                    tickets.add(t);
                }
            }
        }
        return tickets;
    }
    
    public static Set<Ticket> getUnInvoicedIssuedTicket(Pnr pnr, Enums.SaleType saleType) {
        Set<Ticket> tickets = new LinkedHashSet<>();

        for (Ticket t : pnr.getTickets()) {
            if (t.getTktStatus().equals(Enums.TicketStatus.ISSUE)) {

                if (saleType.equals(Enums.SaleType.TKTSALES) && t.getTicketingSalesAcDoc() == null) {
                    tickets.add(t);
                } else if (saleType.equals(Enums.SaleType.TKTPURCHASE) && t.getTicketingPurchaseAcDoc() == null) {
                    tickets.add(t);
                }
            }
        }
        return tickets;
    }
    
    public static Set<Ticket> getUnInvoicedReIssuedTicket(Pnr pnr, Enums.SaleType saleType) {
        Set<Ticket> tickets = new LinkedHashSet<>();

        for (Ticket t : pnr.getTickets()) {
            if (t.getTktStatus().equals(Enums.TicketStatus.REISSUE)) {

                if (saleType.equals(Enums.SaleType.TKTSALES) && t.getTicketingSalesAcDoc() == null) {
                    tickets.add(t);
                } else if (saleType.equals(Enums.SaleType.TKTPURCHASE) && t.getTicketingPurchaseAcDoc() == null) {
                    tickets.add(t);
                }
            }
        }
        return tickets;
    }
}
