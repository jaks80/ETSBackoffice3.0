package com.ets.accountingdoc.domain;

import com.ets.pnr.domain.Pnr;
import com.ets.pnr.domain.Ticket;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
@Access(AccessType.FIELD)
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public abstract class TicketingAcDoc extends AccountingDocument implements Serializable {

    @XmlElement
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pnr_fk")
    private Pnr pnr;
    @XmlElement
    @OneToMany
    private Set<Ticket> tickets = new LinkedHashSet<>();

    public TicketingAcDoc() {

    }

    public Pnr getPnr() {
        return pnr;
    }

    public void setPnr(Pnr pnr) {
        this.pnr = pnr;
    }

    public Set<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(Set<Ticket> tickets) {
        this.tickets = tickets;
    }
}
