package com.ets.fe.acdoc.model;

import com.ets.fe.pnr.model.Pnr;
import com.ets.fe.pnr.model.Ticket;
import java.io.Serializable;
import java.util.LinkedHashSet;
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
public abstract class TicketingAcDoc extends AccountingDocument implements Serializable {

    @XmlElement
    private Pnr pnr;
    @XmlElement    
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
