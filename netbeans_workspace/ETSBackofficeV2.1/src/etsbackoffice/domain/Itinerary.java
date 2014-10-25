package etsbackoffice.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.*;

@Entity
@Table(name = "itinary")
public class Itinerary implements Serializable {

    private long itineraryId;
    //private int segmentStatus;//1.Booked,2.Issued,3.Reissued,4.Refund
    private String segmentNo;
    private boolean stopOver;
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
    private int mileage;
    private PNR pnr;
    private Set<Ticket> tickets = new LinkedHashSet<Ticket>();
   
    public Itinerary() {
    }

    @Id
    @Column(name = "ITIID")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "itiid")
    @TableGenerator(name = "itiid", table = "itiidpktb",
    pkColumnName = "itiidkey", pkColumnValue = "itiidvalue", allocationSize = 1)
    public long getItineraryId() {
        return itineraryId;
    }

    public void setItineraryId(long itineraryId) {
        this.itineraryId = itineraryId;
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

    //@Temporal(TemporalType.TIMESTAMP)
    public String getDeptDate() {
        return deptDate;
    }

    public void setDeptDate(String deptDate) {
        this.deptDate = deptDate;
    }

    //@Temporal(TemporalType.TIMESTAMP)
    public String getArvDate() {
        return arvDate;
    }

    public void setArvDate(String arvDate) {
        this.arvDate = arvDate;
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

    public int getMileage() {
        return mileage;
    }

    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    @ManyToOne
    @JoinColumn(name = "PNRID_FK")
    public PNR getPnr() {
        return pnr;
    }

    public void setPnr(PNR pnr) {
        this.pnr = pnr;
    }
    

    public String getDeptTime() {
        return deptTime;
    }

    public void setDeptTime(String deptTime) {
        this.deptTime = deptTime;
    }

    public String getArvTime() {
        return arvTime;
    }

    public void setArvTime(String arvTime) {
        this.arvTime = arvTime;
    }

 /*
    public int getSegmentStatus() {
        return segmentStatus;
    }
 
    public void setSegmentStatus(int segmentStatus) {
        this.segmentStatus = segmentStatus;
    }*/

    @ManyToMany
    @JoinTable(name = "join_ticket_itinerary",
    joinColumns={@JoinColumn(name = "itineraryId")},
    inverseJoinColumns={@JoinColumn(name ="ticketId")})
    public Set<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(Set<Ticket> tickets) {
        this.tickets = tickets;
    }

    public String getSegmentNo() {
        return segmentNo;
    }

    public void setSegmentNo(String segmentNo) {
        this.segmentNo = segmentNo;
    }

    /**
     * @return the stopOver
     */
    public boolean isStopOver() {
        return stopOver;
    }

    /**
     * @param stopOver the stopOver to set
     */
    public void setStopOver(boolean stopOver) {
        this.stopOver = stopOver;
    }

    //@JoinColumn(name = "ACDOCID_FK")
   
    /*@ManyToMany
    @JoinTable(name = "join_acdoc_itinerary",
    joinColumns={@JoinColumn(name = "itineraryId")},
    inverseJoinColumns={@JoinColumn(name ="acDocId")})
    public Set<AccountingDocument> getAccountingDocument() {
        return accountingDocument;
    }

    public void setAccountingDocument(Set<AccountingDocument> accountingDocument) {
        this.accountingDocument = accountingDocument;
    }*/    
    public Itinerary copyValue(Itinerary newSeg) {       

        newSeg.setItineraryId(itineraryId);
        newSeg.setSegmentNo(segmentNo);
        newSeg.setStopOver(stopOver);
        newSeg.setDeptFrom(deptFrom);
        newSeg.setDeptTo(deptTo);
        newSeg.setDeptDate(deptDate);
        newSeg.setDeptTime(deptTime);
        newSeg.setArvDate(arvDate);
        newSeg.setArvTime(arvTime);
        newSeg.setAirLineID(airLineID);
        newSeg.setFlightNo(flightNo);
        newSeg.setTicketClass(ticketClass);
        newSeg.setTktStatus(tktStatus);
        newSeg.setBaggage(baggage);
        newSeg.setMealCode(mealCode);
        newSeg.setCheckInTerminal(checkInTerminal);
        newSeg.setCheckInTime(checkInTime);
        newSeg.setFlightDuration(flightDuration);
        newSeg.setMileage(mileage);
        newSeg.setPnr(pnr);
        newSeg.setTickets(tickets);
        return newSeg;
    }
    
    //For reporting
    @Transient
    public Ticket getFirstTicket() {
        return this.getTickets().iterator().next();
    }
    
    @Transient
    public int getSegmentCount() {
        int seg = 0;
        if (getFirstTicket().getTktStatus() == 2) {
            seg = 1;            
        } else if (getFirstTicket().getTktStatus() == 3) {
            seg = 0;            
        } else if (getFirstTicket().getTktStatus() == 4) {
            seg = -1;            
        } else if (getFirstTicket().getTktStatus() == 5 || getAirLineID().equals("VOID")) {
            seg = 0;         
        }
        return seg;
    }
}
