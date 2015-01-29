package com.ets.fe.acdoc.model;

import com.ets.fe.pnr.model.Pnr;
import com.ets.fe.pnr.model.Ticket;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Yusuf
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class TicketingPurchaseAcDoc extends AccountingDocument implements Serializable {

    @XmlElement
    private String vendorRef;
    @XmlElement
    private List<Ticket> tickets = new ArrayList<>();
    @XmlElement
    private Pnr pnr;

    @Override
    public BigDecimal calculateTicketedSubTotal() {
        BigDecimal subtotal = new BigDecimal("0.00");
        for (Ticket t : getTickets()) {
            subtotal = subtotal.add(t.calculateNetPurchaseFare());
        }
        return subtotal;
    }

    @Override
    public BigDecimal calculateDocumentedAmount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public BigDecimal calculateAddChargesSubTotal() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public BigDecimal calculateTotalPayment() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public BigDecimal calculateDueAmount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String getVendorRef() {
        return vendorRef;
    }

    public void setVendorRef(String vendorRef) {
        this.vendorRef = vendorRef;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    public Pnr getPnr() {
        return pnr;
    }

    public void setPnr(Pnr pnr) {
        this.pnr = pnr;
    }
}
