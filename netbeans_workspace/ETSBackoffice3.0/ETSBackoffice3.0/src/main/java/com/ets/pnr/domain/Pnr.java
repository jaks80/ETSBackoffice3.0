package com.ets.pnr.domain;

import com.ets.PersistentObject;
import com.ets.accountingdoc.domain.TicketingPurchaseAcDoc;
import com.ets.accountingdoc.domain.TicketingSalesAcDoc;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
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
public class Pnr extends PersistentObject implements Serializable {

    private static final long serialVersionUID = 1L;
    @XmlElement
    private String gdsPnr;
    @XmlElement
    private Integer noOfPax;
    @XmlElement
    private String bookingAgtOid;
    @XmlElement
    private String ticketingAgtOid;
    @XmlElement
    private Date pnrCreationDate;
    @XmlElement
    private Date airCreationDate;
    @XmlElement
    private String PnrCreatorAgentSine;
    @XmlElement
    private String ticketingAgentSine;
    @XmlElement
    private String vendorPNR;
    @XmlElement
    private String airLineCode;

    @XmlElement
    private Set<Ticket> tickets = new LinkedHashSet<>();
    @XmlElement
    private Set<Itinerary> segments = new LinkedHashSet<>();
    @XmlElement
    private Set<Remark> remarks = new LinkedHashSet<>();
    
    public Pnr() {
    }

    public String getGdsPnr() {
        return gdsPnr;
    }

    public void setGdsPnr(String gdsPnr) {
        this.gdsPnr = gdsPnr;
    }

    public Integer getNoOfPax() {
        return noOfPax;
    }

    public void setNoOfPax(Integer noOfPax) {
        this.noOfPax = noOfPax;
    }

    @Temporal(javax.persistence.TemporalType.DATE)
    public Date getPnrCreationDate() {
        return pnrCreationDate;
    }

    public void setPnrCreationDate(Date pnrCreationDate) {
        this.pnrCreationDate = pnrCreationDate;
    }

    @Temporal(javax.persistence.TemporalType.DATE)
    public Date getAirCreationDate() {
        return airCreationDate;
    }

    public void setAirCreationDate(Date airCreationDate) {
        this.airCreationDate = airCreationDate;
    }

    public String getVendorPNR() {
        return vendorPNR;
    }

    public void setVendorPNR(String vendorPNR) {
        this.vendorPNR = vendorPNR;
    }

    @OneToMany(mappedBy = "pnr", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public Set<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(Set<Ticket> tickets) {
        this.tickets = tickets;
    }

    @OneToMany(mappedBy = "pnr", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public Set<Itinerary> getSegments() {
        return segments;
    }

    public void setSegments(Set<Itinerary> segments) {
        this.segments = segments;
    }

    public String getBookingAgtOid() {
        return bookingAgtOid;
    }

    public void setBookingAgtOid(String bookingAgtOid) {
        this.bookingAgtOid = bookingAgtOid;
    }

    public String getTicketingAgtOid() {
        return ticketingAgtOid;
    }

    public void setTicketingAgtOid(String ticketingAgtOid) {
        this.ticketingAgtOid = ticketingAgtOid;
    }

    public String getPnrCreatorAgentSine() {
        return PnrCreatorAgentSine;
    }

    public void setPnrCreatorAgentSine(String PnrCreatorAgentSine) {
        this.PnrCreatorAgentSine = PnrCreatorAgentSine;
    }

    public String getTicketingAgentSine() {
        return ticketingAgentSine;
    }

    public void setTicketingAgentSine(String ticketingAgentSine) {
        this.ticketingAgentSine = ticketingAgentSine;
    }

    public String getAirLineCode() {
        return airLineCode;
    }

    public void setAirLineCode(String airLineCode) {
        this.airLineCode = airLineCode;
    }

    @OneToMany(mappedBy = "pnr", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public Set<Remark> getRemarks() {
        return remarks;
    }

    public void setRemarks(Set<Remark> remarks) {
        this.remarks = remarks;
    }

    public void addTicket(Ticket ticket) {
        this.tickets.add(ticket);
    }
}
