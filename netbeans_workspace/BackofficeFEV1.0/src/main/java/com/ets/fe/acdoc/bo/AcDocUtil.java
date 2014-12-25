package com.ets.fe.acdoc.bo;

import com.ets.fe.pnr.model.Pnr;
import com.ets.fe.pnr.model.Ticket;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Yusuf
 */
public class AcDocUtil {

    public static boolean validateSellingFare(List<Ticket> ticketsToInvoice) {
        boolean okToInvoice = false;
        StringBuilder unvalidatedTicketNo = new StringBuilder();
        for (Ticket t : ticketsToInvoice) {
            if (t.calculateRevenue().compareTo(new BigDecimal("0.00")) < 0) {
                unvalidatedTicketNo.append(t.getFullPaxName().concat(" , Selling Fare : " + t.calculateNetSellingFare() + "\n"));
            }
        }

        if (unvalidatedTicketNo.toString().isEmpty()) {
            okToInvoice = true;
        } else {
            JOptionPane.showMessageDialog(null, "Selling fare validation failed! Check tickets bellow:\n\n"
                    + unvalidatedTicketNo + "\n\nCould not proceed to invoicing...", "Issue Invoice", JOptionPane.WARNING_MESSAGE);
            okToInvoice = false;
        }
        return okToInvoice;
    }

    public static boolean validateContactable(Pnr pnr) {

        boolean valid = true;
        if (pnr.getTicketing_agent() == null) {
            valid = false;
        }

        if (pnr.getAgent() == null && pnr.getCustomer() == null) {
            valid = false;
        }

        if (!valid) {
            JOptionPane.showMessageDialog(null, "Client / Ticketing Agent not selected", "Issue Invoice", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return valid;
    }
}
