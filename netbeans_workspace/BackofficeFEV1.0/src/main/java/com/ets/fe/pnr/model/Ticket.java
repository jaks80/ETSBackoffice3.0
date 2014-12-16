package com.ets.fe.pnr.model;

import com.ets.fe.PersistentObject;
import com.ets.fe.util.DateUtil;
import com.ets.fe.util.Enums;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
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
public class Ticket extends PersistentObject implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement
    private String passengerNo;
    @XmlElement
    private String paxSurName;
    @XmlElement
    private String paxForeName;
    @XmlElement
    private String numericAirLineCode;
    @XmlElement
    private String ticketNo;
    @XmlElement
    private String orginalTicketNo;
    @XmlElement
    private String currencyCode;
    @XmlElement
    private String restrictions;
    @XmlElement
    private Date docIssuedate;

    @XmlElement
    private Pnr pnr;

    @XmlElement
    private BigDecimal baseFare = new BigDecimal("0.00");
    @XmlElement
    private BigDecimal tax = new BigDecimal("0.00");
    @XmlElement
    private BigDecimal fee = new BigDecimal("0.00");
    @XmlElement
    private BigDecimal commission = new BigDecimal("0.00");
    @XmlElement
    private BigDecimal totalFare = new BigDecimal("0.00");

    @XmlElement
    private String tktStatus;

    public Ticket() {

    }

    public String getPassengerNo() {
        return passengerNo;
    }

    public void setPassengerNo(String passengerNo) {
        this.passengerNo = passengerNo;
    }

    public String getPaxSurName() {
        return paxSurName;
    }

    public void setPaxSurName(String paxSurName) {
        this.paxSurName = paxSurName;
    }

    public String getPaxForeName() {
        return paxForeName;
    }

    public void setPaxForeName(String paxForeName) {
        this.paxForeName = paxForeName;
    }

    public String getNumericAirLineCode() {
        return numericAirLineCode;
    }

    public void setNumericAirLineCode(String numericAirLineCode) {
        this.numericAirLineCode = numericAirLineCode;
    }

    public String getTicketNo() {
        return ticketNo;
    }

    public void setTicketNo(String ticketNo) {
        this.ticketNo = ticketNo;
    }

    public String getOrginalTicketNo() {
        return orginalTicketNo;
    }

    public void setOrginalTicketNo(String orginalTicketNo) {
        this.orginalTicketNo = orginalTicketNo;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public BigDecimal getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(BigDecimal baseFare) {
        this.baseFare = baseFare;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    public String getTktStatus() {
        return tktStatus;
    }

    public void setTktStatus(String tktStatus) {
        this.tktStatus = tktStatus;
    }

    public String getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(String restrictions) {
        this.restrictions = restrictions;
    }

    public Date getDocIssuedate() {
        return docIssuedate;
    }

    public void setDocIssuedate(Date docIssuedate) {
        this.docIssuedate = docIssuedate;
    }

    public BigDecimal getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(BigDecimal totalFare) {
        this.totalFare = totalFare;
    }

    public Pnr getPnr() {
        return pnr;
    }

    public void setPnr(Pnr pnr) {
        this.pnr = pnr;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    public String getFullTicketNo() {
        if (this.numericAirLineCode != null && this.ticketNo != null) {
            return this.numericAirLineCode + "-" + this.ticketNo;
        } else {
            return "";
        }
    }
    
    public String getTktDateString(){
        return DateUtil.dateToString(docIssuedate);
    }

    public String getFullPaxNameWithPaxNo() {

        String paxFullName = "";
        paxFullName = getPassengerNo() + ". " + getPaxSurName() + " / " + getPaxForeName();

        return paxFullName;
    }
}