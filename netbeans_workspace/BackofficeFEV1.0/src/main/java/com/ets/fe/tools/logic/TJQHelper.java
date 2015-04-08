package com.ets.fe.tools.logic;

import com.ets.fe.pnr.model.Pnr;
import com.ets.fe.pnr.model.Ticket;
import com.ets.fe.util.Enums;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Yusuf
 */
public class TJQHelper {

    private final String[] tjqLines;
    private int issuedQty = 0;
    private int cancelQty = 0;

    public TJQHelper(String[] tjqLines) {
        this.tjqLines = tjqLines;
    }

    public List<Ticket> getIssuedTickets() {
        List<Ticket> tickets = new ArrayList<>();

        for (String line : tjqLines) {
            if (line.contains("TKTT") || line.contains("CANX")|| line.contains("RFND")) {
                tickets.add(getTicketFromLine(line.trim()));
            }
        }

        return tickets;
    }

    private Ticket getTicketFromLine(String line) {

        if (line.contains("-")) {
            line = line.replaceAll("-", " ");
        }

        line = line.replaceAll("\\s+", " ").trim();
        if (line.contains("*")) {
            line = line.replace('*', ' ');
        }

        String[] vals = line.split(" ");
        Pnr pnr = new Pnr();
        pnr.setGdsPnr(vals[vals.length - 2]);

        Ticket ticket = new Ticket();
        ticket.setTicketNo(vals[2]);
        BigDecimal totalGrossFare = new BigDecimal(vals[3]);

        ticket.setTax(new BigDecimal(vals[4]));
        ticket.setFee(new BigDecimal(vals[5]));
        ticket.setCommission(new BigDecimal(vals[6]));
        ticket.setBaseFare(totalGrossFare.subtract(ticket.getTax()));
        if (line.contains("TKTT")) {
            ticket.setTktStatus(Enums.TicketStatus.ISSUE);
            issuedQty++;
        } else if (line.contains("CANX")) {
            ticket.setTktStatus(Enums.TicketStatus.VOID);
            cancelQty++;
        } else if (line.contains("RFND")) {
            ticket.setTktStatus(Enums.TicketStatus.REFUND);
            ticket.setTax(new BigDecimal(vals[4]).negate());
            ticket.setFee(new BigDecimal(vals[5]));
            ticket.setCommission(new BigDecimal(vals[6]).negate());
            ticket.setBaseFare(totalGrossFare.subtract(new BigDecimal(vals[4])).negate());
        }

        ticket.setPnr(pnr);
        return ticket;
    }

    public static boolean totalMatch(Ticket tjqTicket, Ticket dbTicket) {
        boolean exist = false;

        String dbTktNo= dbTicket.getTicketNo();
        
        if(dbTktNo.contains("-")){
         dbTktNo = StringUtils.substringBefore(dbTktNo, "-");
        }
        
        if (tjqTicket.getTicketNo().equals(dbTktNo)
                && tjqTicket.getPnr().getGdsPnr().equals(dbTicket.getPnr().getGdsPnr())
                && tjqTicket.getBaseFare().compareTo(dbTicket.getBaseFare()) == 0
                && tjqTicket.getTax().compareTo(dbTicket.getTax()) == 0
                && tjqTicket.getFee().compareTo(dbTicket.getFee()) == 0) {//Every field is matching

            //tjqTicket.getCommission().compareTo(dbTicket.getCommission()) == 0
            if (dbTicket.getTktStatus().equals(Enums.TicketStatus.ISSUE)) {
                if (tjqTicket.getCommission().compareTo(dbTicket.getCommission().abs()) == 0) {
                    exist = true;
                }
            }

            if ((dbTicket.getTktStatus().equals(Enums.TicketStatus.REISSUE)
                    || dbTicket.getTktStatus().equals(Enums.TicketStatus.ISSUE))
                    && tjqTicket.getTktStatus().equals(Enums.TicketStatus.ISSUE)) {
                exist = true;
            }

        }
        return exist;
    }
    
    public int getIssuedQty() {
        return issuedQty;
    }

    public int getCancelQty() {
        return cancelQty;
    }
}
