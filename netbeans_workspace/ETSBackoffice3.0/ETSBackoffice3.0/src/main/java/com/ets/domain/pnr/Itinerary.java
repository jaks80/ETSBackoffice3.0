package com.ets.domain.pnr;

import com.ets.domain.PersistentObject;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

/**
 *
 * @author Yusuf
 */
@Entity
public class Itinerary extends PersistentObject implements Serializable{

    private String segmentNo;
    private String stopOver;
    private String deptFrom;
    private String deptTo;
    private String deptDate;
    private String deptTime;
    private String arvDate;
    private String arvTime;
    private String airLineID;
    private String flightNo;
    private String ticketClass;
    private String tktStatus;
    private String baggage;
    private String mealCode;
    private String checkInTerminal;
    private String checkInTime;
    private String flightDuration;
    private String mileage;

    private Pnr pnr;
    private Set<Ticket> tickets = new LinkedHashSet<Ticket>();

    public Itinerary() {

    }

    public String getSegmentNo() {
        return segmentNo;
    }

    public void setSegmentNo(String segmentNo) {
        this.segmentNo = segmentNo;
    }

    public String isStopOver() {
        return stopOver;
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

    @ManyToMany
    @JoinTable(name = "join_ticket_itinerary",
            joinColumns = {
                @JoinColumn(name = "itineraryId")},
            inverseJoinColumns = {
                @JoinColumn(name = "ticketId")})
    public Set<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(Set<Ticket> tickets) {
        this.tickets = tickets;
    }

    @ManyToOne
    @JoinColumn(name = "PNRID_FK")
    public Pnr getPnr() {
        return pnr;
    }

    public void setPnr(Pnr pnr) {
        this.pnr = pnr;
    }
}
