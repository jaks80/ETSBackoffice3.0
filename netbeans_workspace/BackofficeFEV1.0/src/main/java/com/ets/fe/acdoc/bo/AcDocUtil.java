package com.ets.fe.acdoc.bo;

import com.ets.fe.acdoc.model.TicketingSalesAcDoc;
import com.ets.fe.acdoc_o.model.AccountingDocumentLine;
import com.ets.fe.pnr.model.Pnr;
import com.ets.fe.pnr.model.Ticket;
import com.ets.fe.util.Enums;
import com.ets.fe.util.PnrUtil;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Yusuf
 */
public class AcDocUtil {

    public static List<TicketingSalesAcDoc> getPayments(TicketingSalesAcDoc invoice) {

        List<TicketingSalesAcDoc> payments = new ArrayList<>();
        for (TicketingSalesAcDoc doc : payments) {
            if (doc.getType().equals(Enums.AcDocType.PAYMENT) || doc.getType().equals(Enums.AcDocType.REFUND)) {
                payments.add(doc);
            }
        }
        return payments;
    }

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

    public static boolean validatePurchaseFare(List<Ticket> ticketsToInvoice) {
        boolean okToInvoice = false;
        StringBuilder unvalidatedTicketNo = new StringBuilder();
        for (Ticket t : ticketsToInvoice) {
            if (t.calculateNetPurchaseFare().compareTo(new BigDecimal("0.00")) == 0) {
                unvalidatedTicketNo.append(t.getFullPaxName().concat(" , Nett Purchase Fare : " + t.calculateNetPurchaseFare() + "\n"));
            }
        }

        if (unvalidatedTicketNo.toString().isEmpty()) {
            okToInvoice = true;
        } else {
            JOptionPane.showMessageDialog(null, "Purchase fare validation failed! Check tickets bellow:\n\n"
                    + unvalidatedTicketNo + "\n\nCould not proceed to invoicing...", "Issue Invoice", JOptionPane.WARNING_MESSAGE);
            okToInvoice = false;
        }
        return okToInvoice;
    }

    public static boolean validateOtherSellingPrice(AccountingDocumentLine line) {
        boolean okToInvoice = false;

        if (line.calculateRevenue().signum() == -1 || line.calculateRevenue().signum() == 0) {
            okToInvoice = false;
            JOptionPane.showMessageDialog(null, "Negetive revenue error...", "Issue Invoice", JOptionPane.WARNING_MESSAGE);
        } else {
            okToInvoice = true;
        }
        return okToInvoice;
    }

    public static boolean validateContactable(Pnr pnr) {

        boolean valid = true;
        if (pnr.getTicketing_agent() == null) {
            valid = !PnrUtil.getUnInvoicedBookedTicket(pnr, Enums.SaleType.TKTSALES).isEmpty();
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
