package com.amadeus.pnr;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 * @author Yusuf
 */
public class PNR implements Serializable {

    private String gdsPNR;
    private Integer noOfPax;
    private String bookingAgtOid;    
    private String tktingAgtOid;
    private Date pnrCreationDate;    
    private Date airCreationDate;
    private String vendorPNR;
    
    private Career servicingCareer;
    private Set<Ticket> tickets = new LinkedHashSet<>();
    private Set<Itinerary> segments = new LinkedHashSet<>();
    private Set<String> remarks = new LinkedHashSet<>();
    
    
    public PNR() {
    }

    public String getGdsPNR() {
        return gdsPNR;
    }

    public void setGdsPNR(String gdsPNR) {
        this.gdsPNR = gdsPNR;
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

    public Date getPnrCreationDate() {
        return pnrCreationDate;
    }

    public void setPnrCreationDate(Date pnrCreationDate) {
        this.pnrCreationDate = pnrCreationDate;
    }
    
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

    public Set<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(Set<Ticket> tickets) {
        this.tickets = tickets;
    }

    public Set<Itinerary> getSegments() {
        return segments;
    }

    public void setSegments(Set<Itinerary> segments) {
        this.segments = segments;
    }

    public Set<String> getRemarks() {
        return remarks;
    }

    public void setRemarks(Set<String> remarks) {
        this.remarks = remarks;
    }

    public String getBookingAgtOid() {
        return bookingAgtOid;
    }

    public void setBookingAgtOid(String bookingAgtOid) {
        this.bookingAgtOid = bookingAgtOid;
    }

}
