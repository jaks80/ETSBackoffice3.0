package com.ets.fe.report.model;

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
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class SegmentReport {

    @XmlElement
    private List<Segment> list = new ArrayList<>();

    @XmlElement
    private Integer totalIssuedSegment= 0;
    @XmlElement
    private Integer totalReissuedSegment = 0;
    @XmlElement
    private Integer totalRefundedSegment = 0;
    @XmlElement
    private Integer totalVoidSegment = 0;
    @XmlElement
    private Integer totalOpenSegment = 0;
    @XmlElement
    private Integer segBalance = 0;

    public SegmentReport() {

    }

    public List<Segment> getList() {
        return list;
    }

    public Integer getTotalIssuedSegment() {
        return totalIssuedSegment;
    }

    public Integer getTotalReissuedSegment() {
        return totalReissuedSegment;
    }

    public Integer getTotalRefundedSegment() {
        return totalRefundedSegment;
    }

    public Integer getTotalVoidSegment() {
        return totalVoidSegment;
    }

    public Integer getTotalOpenSegment() {
        return totalOpenSegment;
    }

    public Integer getSegBalance() {
        return segBalance;
    }

    @XmlAccessorType(XmlAccessType.NONE)
    public static class Segment {

        @XmlElement
        private String pnr;
        @XmlElement
        private String bookingOid;
        @XmlElement
        private String ticketingOid;
        @XmlElement
        private String ticketNo;
        @XmlElement
        private String ticketClass;
        @XmlElement
        private String flightNo;
        @XmlElement
        private String airLine;
        @XmlElement
        private String travelDate;
        @XmlElement
        private String inCity;
        @XmlElement
        private String outCity;
        @XmlElement
        private Integer count;
        @XmlElement
        private String tktStatus;

        public Segment() {
            
        }

        public String getPnr() {
            return pnr;
        }

        public void setPnr(String pnr) {
            this.pnr = pnr;
        }

        public String getBookingOid() {
            return bookingOid;
        }

        public void setBookingOid(String bookingOid) {
            this.bookingOid = bookingOid;
        }

        public String getTicketingOid() {
            return ticketingOid;
        }

        public void setTicketingOid(String ticketingOid) {
            this.ticketingOid = ticketingOid;
        }

        public String getTicketNo() {
            return ticketNo;
        }

        public void setTicketNo(String ticketNo) {
            this.ticketNo = ticketNo;
        }

        public String getTicketClass() {
            return ticketClass;
        }

        public void setTicketClass(String ticketClass) {
            this.ticketClass = ticketClass;
        }

        public String getAirLine() {
            return airLine;
        }

        public void setAirLine(String airLine) {
            this.airLine = airLine;
        }

        public String getTravelDate() {
            return travelDate;
        }

        public void setTravelDate(String travelDate) {
            this.travelDate = travelDate;
        }

        public String getInCity() {
            return inCity;
        }

        public void setInCity(String inCity) {
            this.inCity = inCity;
        }

        public String getOutCity() {
            return outCity;
        }

        public void setOutCity(String outCity) {
            this.outCity = outCity;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public String getFlightNo() {
            return flightNo;
        }

        public void setFlightNo(String flightNo) {
            this.flightNo = flightNo;
        }

        public String getTktStatus() {
            return tktStatus;
        }

        public void setTktStatus(String tktStatus) {
            this.tktStatus = tktStatus;
        }
    }
}
