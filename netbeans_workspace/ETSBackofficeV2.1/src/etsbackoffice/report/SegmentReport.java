/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etsbackoffice.report;

import etsbackoffice.businesslogic.Enums;
import etsbackoffice.domain.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Yusuf Creation Date: 25/06/2011
 */
public class SegmentReport {

    private MasterAgent mAgent;
    private Integer status = null;
    private Integer gdsId = null;
    private String oid = null;
    private String airLineID = null;
    private Date from = null;
    private Date to = null;
    private List<Itinerary> segments = new ArrayList<Itinerary>();
    private Date docIssuedate;
    private PNR pnr;
    private Ticket ticket;
    private Itinerary segment;
    private int totalIssuedSegment = 0;
    private int totalReissuedSegment = 0;
    private int totalRefundedSegment = 0;
    private int totalVoidSegment = 0;
    private int totalOpenSegment = 0;
    private int segmentBalance = 0;

    public SegmentReport(List<Itinerary> segments) {
        this.segments = segments;
    }

    public List<Itinerary> getSegments() {
        return segments;
    }

    public void setSegments(List<Itinerary> segments) {
        this.segments = segments;
    }

    public Date getDocIssuedate() {
        return docIssuedate;
    }

    public void setDocIssuedate(Date docIssuedate) {
        this.docIssuedate = docIssuedate;
    }

    public PNR getPnr() {
        return pnr;
    }

    public void setPnr(PNR pnr) {
        this.pnr = pnr;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public Itinerary getSegment() {
        return segment;
    }

    public void setSegment(Itinerary segment) {
        this.segment = segment;
    }

    public int getTotalIssuedSegment() {
        return totalIssuedSegment;
    }

    public void setTotalIssuedSegment(int totalIssuedSegment) {
        this.totalIssuedSegment = totalIssuedSegment;
    }

    public int getTotalReissuedSegment() {
        return totalReissuedSegment;
    }

    public void setTotalReissuedSegment(int totalReissuedSegment) {
        this.totalReissuedSegment = totalReissuedSegment;
   }

    public int getTotalRefundedSegment() {
        return totalRefundedSegment;
    }

    public void setTotalRefundedSegment(int totalRefundedSegment) {
        this.totalRefundedSegment = totalRefundedSegment;
    }

    public int getTotalVoidSegment() {
        return totalVoidSegment;
    }

    public void setTotalVoidSegment(int totalVoidSegment) {
        this.totalVoidSegment = totalVoidSegment;
    }

    public int getTotalOpenSegment() {
        return totalOpenSegment;
    }

    public void setTotalOpenSegment(int totalOpenSegment) {
        this.totalOpenSegment = totalOpenSegment;
    }

    public int getSegmentBalance() {
        return segmentBalance;
    }

    public void setSegmentBalance(int segmentBalance) {
        this.segmentBalance = segmentBalance;
    }

    

    public MasterAgent getmAgent() {
        return mAgent;
    }

    public void setmAgent(MasterAgent mAgent) {
        this.mAgent = mAgent;
    }

    public String getStatus() {
        if (this.status != null) {
            return Enums.TicketStatus.valueOf(this.status);
        } else {
            return "All Status";
        }
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getGds() {
        if (this.gdsId != null) {
            return Enums.GDS.valueOf(this.gdsId);
        } else {
            return "All GDS";
        }
    }

    public void setGdsId(Integer gdsId) {
        this.gdsId = gdsId;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getAirLineID() {
        if (this.airLineID != null) {
            return airLineID;
        } else {
            return "All Airline";
        }
    }

    public void setAirLineID(String airLineID) {
        this.airLineID = airLineID;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }    
}
