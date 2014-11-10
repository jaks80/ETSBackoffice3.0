package com.ets.fe.model.pnr;

import com.ets.fe.model.PersistentObject;
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
@XmlRootElement(name = "pnr")
public class Pnr extends PersistentObject implements Serializable {

    private static final long serialVersionUID = 1L;
        
    @XmlElement
    private String gdsPNR;
    @XmlElement
    private Integer noOfPax;
    @XmlElement
    private String bookingAgtOid;    
    @XmlElement
    private String tktingAgtOid;
    @XmlElement
    private Date pnrCreationDate;    
    @XmlElement
    private Date airCreationDate;
    @XmlElement
    private String vendorPNR;
    
    private Career servicingCareer;
    @XmlElement
    private List<Ticket> tickets = new ArrayList<>();
    @XmlElement
    private List<Itinerary> segments = new ArrayList<>();
    @XmlElement
    private List<String> remarks = new ArrayList<>();
    
    
    public Pnr() {
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

   
    public List<String> getRemarks() {
        return remarks;
    }

    public void setRemarks(List<String> remarks) {
        this.remarks = remarks;
    }

    public String getBookingAgtOid() {
        return bookingAgtOid;
    }

    public void setBookingAgtOid(String bookingAgtOid) {
        this.bookingAgtOid = bookingAgtOid;
    }
}
