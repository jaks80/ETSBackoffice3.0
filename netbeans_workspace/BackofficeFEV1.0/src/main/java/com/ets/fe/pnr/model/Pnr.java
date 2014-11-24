package com.ets.fe.pnr.model;

import com.ets.fe.PersistentObject;
import java.io.Serializable;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Yusuf
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class Pnr extends PersistentObject implements Serializable {

    private static long serialVersionUID = 1L;
        
    @XmlElement
    private String gdsPnr;
    @XmlElement
    private Integer noOfPax;
    @XmlElement
    private String bookingAgtOid;    
    @XmlElement
    private String ticketingAgtOid;
    @XmlElement
    private String PnrCreatorAgentSine;
    @XmlElement
    private String ticketingAgentSine;    
    @XmlElement
    private Date pnrCreationDate;    
    @XmlElement
    private Date airCreationDate;
    @XmlElement
    private String vendorPNR;
    @XmlElement
    private String airLineCode;
    @XmlElement
    private List<Ticket> tickets = new ArrayList<>();
    @XmlElement
    private List<Itinerary> segments = new ArrayList<>();
    @XmlElement
    private List<PnrRemark> remarks = new ArrayList<>();
    
    
    public Pnr() {
    }

    public Integer getNoOfPax() {
        return noOfPax;
    }

    public void setNoOfPax(Integer noOfPax) {
        this.noOfPax = noOfPax;
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
    
    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

   
    public List<Itinerary> getSegments() {
        return segments;
    }

    public void setSegments(List<Itinerary> segments) {
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

    public String getGdsPnr() {
        return gdsPnr;
    }

    public void setGdsPnr(String gdsPnr) {
        this.gdsPnr = gdsPnr;
    }

    public List<PnrRemark> getRemarks() {
        return remarks;
    }

    public void setRemarks(List<PnrRemark> remarks) {
        this.remarks = remarks;
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
}
