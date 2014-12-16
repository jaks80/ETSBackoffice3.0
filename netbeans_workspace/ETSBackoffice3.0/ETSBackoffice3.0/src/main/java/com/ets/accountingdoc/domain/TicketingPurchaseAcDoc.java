package com.ets.accountingdoc.domain;

import com.ets.pnr.domain.Pnr;
import com.ets.pnr.domain.Ticket;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Yusuf
 */
@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@Access(AccessType.PROPERTY)
@Table(name = "tkt_purch_acdoc")
//@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public class TicketingPurchaseAcDoc extends AccountingDocument implements Serializable{

    @XmlElement
    private String vendorRef;
    @XmlElement
    private Set<Ticket> tickets = new LinkedHashSet<>();
    @XmlElement
    private Pnr pnr;
    
   
    public BigDecimal calculateTicketedSubTotal() {
       BigDecimal subtotal = new BigDecimal("0.00");
        for (Ticket t : getTickets()) {
            subtotal = subtotal.add(t.getNetPurchaseFare());
        }
        return subtotal;
    }

    public String getVendorRef() {
        return vendorRef;
    }

    public void setVendorRef(String vendorRef) {
        this.vendorRef = vendorRef;
    }

    @OneToMany(mappedBy = "ticketingPurchaseAcDoc")
    public Set<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(Set<Ticket> tickets) {
        this.tickets = tickets;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pnr_fk")
    public Pnr getPnr() {
        return pnr;
    }

    public void setPnr(Pnr pnr) {
        this.pnr = pnr;
    }
}
