package com.ets.fe.pnr.logic;

import com.ets.fe.pnr.model.Ticket;
import com.ets.fe.util.Enums;
import java.math.BigDecimal;

/**
 *
 * @author Yusuf
 */
public class TicketLogic {

    public static Ticket createNewRefund(Ticket ticketToRfd) {
        if (ticketToRfd.getTktStatus().equals(Enums.TicketStatus.ISSUE)
                || ticketToRfd.getTktStatus().equals(Enums.TicketStatus.REISSUE)) {
            Ticket newRefund = new Ticket();
            newRefund.setPassengerNo(ticketToRfd.getPassengerNo());
            newRefund.setSurName(ticketToRfd.getSurName());
            newRefund.setForeName(ticketToRfd.getForeName());
            newRefund.setNumericAirLineCode(ticketToRfd.getNumericAirLineCode());
            newRefund.setTicketNo(ticketToRfd.getTicketNo());
            newRefund.setCurrencyCode(ticketToRfd.getCurrencyCode());
            newRefund.setDocIssuedate(new java.util.Date());
            newRefund.setTktStatus(Enums.TicketStatus.REFUND);
            newRefund.setBaseFare(ticketToRfd.getBaseFare().negate());
            newRefund.setTax(ticketToRfd.getTax().negate());

            return newRefund;
        } else {
            return null;
        }
    }

    public static Ticket createNewReIssue(Ticket ticketToReissue) {
        if (ticketToReissue.getTktStatus().equals(Enums.TicketStatus.ISSUE)) {
            Ticket newReissue = new Ticket();
            newReissue.setPassengerNo(ticketToReissue.getPassengerNo());
            newReissue.setSurName(ticketToReissue.getSurName());
            newReissue.setForeName(ticketToReissue.getForeName());
            newReissue.setNumericAirLineCode(ticketToReissue.getNumericAirLineCode());
            newReissue.setTicketNo("");
            newReissue.setOrginalTicketNo(ticketToReissue.getTicketNo());
            newReissue.setCurrencyCode(ticketToReissue.getCurrencyCode());
            newReissue.setDocIssuedate(new java.util.Date());
            newReissue.setTktStatus(Enums.TicketStatus.REISSUE);
            return newReissue;
        } else {
            return null;
        }
    }

    public static Ticket voidTicket(Ticket ticketToVoid) {
        if (ticketToVoid.getTktStatus().equals(Enums.TicketStatus.ISSUE)) {
            ticketToVoid.setTktStatus(Enums.TicketStatus.VOID);
            ticketToVoid.setBaseFare(new BigDecimal("0.00"));
            ticketToVoid.setTax(new BigDecimal("0.00"));
            ticketToVoid.setCommission(new BigDecimal("0.00"));
            ticketToVoid.setFee(new BigDecimal("0.00"));
            return ticketToVoid;
        } else {
            return null;
        }
    }
}
