package com.ets.domain.pnr;

import com.ets.domain.PersistentObject;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Yusuf
 */
@Entity
@XmlRootElement
//@XmlAccessorType(XmlAccessType.NONE)
public class Pnr extends PersistentObject implements Serializable {
    
    private String gdsPnr;
    private Integer noOfPax;
    private String bookingAgtOid;
    private String tktingAgtOid;
    private Date pnrCreationDate;
    private Date airCreationDate;
    private String vendorPNR;

    private Career servicingCareer;
        
    private Set<Ticket> tickets = new LinkedHashSet<>();    
    private Set<Itinerary> segments = new LinkedHashSet<>();    
    private Set<PnrRemark> remarks = new LinkedHashSet<>();

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

    public String getTktingAgtOid() {
        return tktingAgtOid;
    }

    public void setTktingAgtOid(String tktingAgtOid) {
        this.tktingAgtOid = tktingAgtOid;
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

    public Career getServicingCareer() {
        return servicingCareer;
    }

    public void setServicingCareer(Career servicingCareer) {
        this.servicingCareer = servicingCareer;
    }

    @OneToMany(targetEntity = Ticket.class, mappedBy = "pnr",
            cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy(value = "ticketId")
    public Set<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(Set<Ticket> tickets) {
        this.tickets = tickets;
    }

    @OneToMany(targetEntity = Itinerary.class, mappedBy = "pnr",
            cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public Set<Itinerary> getSegments() {
        return segments;
    }

    public void setSegments(Set<Itinerary> segments) {
        this.segments = segments;
    }

    @OneToMany(targetEntity = PnrRemark.class, mappedBy = "pnr",
            cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public Set<PnrRemark> getRemarks() {
        return remarks;
    }

    public void setRemarks(Set<PnrRemark> remarks) {
        this.remarks = remarks;
    }

    public String getBookingAgtOid() {
        return bookingAgtOid;
    }

    public void setBookingAgtOid(String bookingAgtOid) {
        this.bookingAgtOid = bookingAgtOid;
    }
}
