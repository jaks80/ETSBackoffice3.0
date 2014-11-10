package com.ets.fe.model.pnr;

import com.ets.fe.model.PersistentObject;
import java.io.Serializable;
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
public class Itinerary  extends PersistentObject implements Serializable{
    
     private static final long serialVersionUID = 1L;

    @XmlElement
    private String segmentNo;
    @XmlElement
    private String stopOver;
    @XmlElement
    private String deptFrom;
    @XmlElement
    private String deptTo;
    @XmlElement
    private String deptDate;
    @XmlElement
    private String deptTime;
    @XmlElement
    private String arvDate;
    @XmlElement
    private String arvTime;
    @XmlElement
    private String airLineID;
    @XmlElement
    private String flightNo;
    @XmlElement
    private String ticketClass;
    @XmlElement
    private String tktStatus;
    @XmlElement
    private String baggage;
    @XmlElement
    private String mealCode;
    @XmlElement
    private String checkInTerminal;
    @XmlElement
    private String checkInTime;
    @XmlElement
    private String flightDuration;
    @XmlElement
    private String mileage;
    
    @XmlElement
    private Pnr pnr;
    
    @XmlElement
    private List<Ticket> tickets = new ArrayList<>();
    
    public Itinerary(){
    
    }

    public String getSegmentNo() {
        return segmentNo;
    }

    public void setSegmentNo(String segmentNo) {
        this.segmentNo = segmentNo;
    }

    public String isStopOver() {
        return getStopOver();
    }

    public void setStopOver(String stopOver) {
        this.stopOver = stopOver;
    }

    public String getDeptFrom() {
        return deptFrom;
    }

    public void setDeptFrom(String deptFrom) {
        this.deptFrom = deptFrom;
    }

    public String getDeptTo() {
        return deptTo;
    }

    public void setDeptTo(String deptTo) {
        this.deptTo = deptTo;
    }

    public String getDeptDate() {
        return deptDate;
    }

    public void setDeptDate(String deptDate) {
        this.deptDate = deptDate;
    }

    public String getDeptTime() {
        return deptTime;
    }

    public void setDeptTime(String deptTime) {
        this.deptTime = deptTime;
    }

    public String getArvDate() {
        return arvDate;
    }

    public void setArvDate(String arvDate) {
        this.arvDate = arvDate;
    }

    public String getArvTime() {
        return arvTime;
    }

    public void setArvTime(String arvTime) {
        this.arvTime = arvTime;
    }

    public String getAirLineID() {
        return airLineID;
    }

    public void setAirLineID(String airLineID) {
        this.airLineID = airLineID;
    }

    public String getFlightNo() {
        return flightNo;
    }

    public void setFlightNo(String flightNo) {
        this.flightNo = flightNo;
    }

    public String getTicketClass() {
        return ticketClass;
    }

    public void setTicketClass(String ticketClass) {
        this.ticketClass = ticketClass;
    }

    public String getTktStatus() {
        return tktStatus;
    }

    public void setTktStatus(String tktStatus) {
        this.tktStatus = tktStatus;
    }

    public String getBaggage() {
        return baggage;
    }

    public void setBaggage(String baggage) {
        this.baggage = baggage;
    }

    public String getMealCode() {
        return mealCode;
    }

    public void setMealCode(String mealCode) {
        this.mealCode = mealCode;
    }

    public String getCheckInTerminal() {
        return checkInTerminal;
    }

    public void setCheckInTerminal(String checkInTerminal) {
        this.checkInTerminal = checkInTerminal;
    }

    public String getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(String checkInTime) {
        this.checkInTime = checkInTime;
    }

    public String getFlightDuration() {
        return flightDuration;
    }

    public void setFlightDuration(String flightDuration) {
        this.flightDuration = flightDuration;
    }

    public String getMileage() {
        return mileage;
    }

    public void setMileage(String mileage) {
        this.mileage = mileage;
    }

    public String getStopOver() {
        return stopOver;
    }

    public Pnr getPnr() {
        return pnr;
    }

    public void setPnr(Pnr pnr) {
        this.pnr = pnr;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }
}
