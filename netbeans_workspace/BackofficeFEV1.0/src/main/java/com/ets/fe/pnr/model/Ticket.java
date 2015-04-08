package com.ets.fe.pnr.model;

import com.ets.fe.PersistentObject;
import com.ets.fe.acdoc.model.TicketingPurchaseAcDoc;
import com.ets.fe.acdoc.model.TicketingSalesAcDoc;
import com.ets.fe.util.DateUtil;
import com.ets.fe.util.Enums.TicketStatus;
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
    private Integer passengerNo;
    @XmlElement
    private String surName;
    @XmlElement
    private String foreName;
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
    private TicketingSalesAcDoc ticketingSalesAcDoc;
    @XmlElement
    private TicketingPurchaseAcDoc ticketingPurchaseAcDoc;

    @XmlElement
    private BigDecimal baseFare = new BigDecimal("0.00");
    @XmlElement
    private BigDecimal tax = new BigDecimal("0.00");
    @XmlElement
    private BigDecimal fee = new BigDecimal("0.00");
    @XmlElement
    private BigDecimal commission = new BigDecimal("0.00");
    
    @XmlElement
    private BigDecimal grossFare = new BigDecimal("0.00");
    @XmlElement
    private BigDecimal discount = new BigDecimal("0.00");
    @XmlElement
    private BigDecimal atolChg = new BigDecimal("0.00");


    @XmlElement
    private TicketStatus tktStatus;

    public Ticket() {

    }

    public boolean isInfant() {
        if (this.getForeName().contains("INF")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isChild() {
        if (this.getForeName().contains("CHD")) {
            return true;
        } else {
            return false;
        }
    }

    public int getPassengerNo() {
        return passengerNo;
    }

    public void setPassengerNo(int passengerNo) {
        this.setPassengerNo((Integer) passengerNo);
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public String getForeName() {
        return foreName;
    }

    public void setForeName(String foreName) {
        this.foreName = foreName;
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

    public void setPassengerNo(Integer passengerNo) {
        this.passengerNo = passengerNo;
    }

    public TicketingSalesAcDoc getTicketingSalesAcDoc() {
        return ticketingSalesAcDoc;
    }

    public void setTicketingSalesAcDoc(TicketingSalesAcDoc ticketingSalesAcDoc) {
        this.ticketingSalesAcDoc = ticketingSalesAcDoc;
    }

    public TicketingPurchaseAcDoc getTicketingPurchaseAcDoc() {
        return ticketingPurchaseAcDoc;
    }

    public void setTicketingPurchaseAcDoc(TicketingPurchaseAcDoc ticketingPurchaseAcDoc) {
        this.ticketingPurchaseAcDoc = ticketingPurchaseAcDoc;
    }

    public BigDecimal getGrossFare() {
        return grossFare;
    }

    public void setGrossFare(BigDecimal grossFare) {
        this.grossFare = grossFare;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getAtolChg() {
        return atolChg;
    }

    public void setAtolChg(BigDecimal atolChg) {
        this.atolChg = atolChg;
    }

    public TicketStatus getTktStatus() {
        return tktStatus;
    }

    public void setTktStatus(TicketStatus tktStatus) {
        this.tktStatus = tktStatus;
    }

    public String getFullTicketNo() {
        if (this.getNumericAirLineCode() != null && this.getTicketNo() != null) {
            return this.getNumericAirLineCode() + "-" + this.getTicketNo();
        } else {
            return "";
        }
    }

    public String getTktDateString() {
        return DateUtil.dateToString(getDocIssuedate());
    }

    public String getFullPaxNameWithPaxNo() {

        String paxFullName = "";
        paxFullName = getPassengerNo() + "." + getSurName() + "/" + getForeName();

        return paxFullName;
    }

    public String getFullPaxName() {

        String paxFullName = "";
        paxFullName = getSurName() + "/" + getForeName();

        return paxFullName;
    }

    public BigDecimal calculateNetSellingFare() {
        return this.grossFare.add(this.atolChg).add(this.discount);
    }

    public BigDecimal calculateNetPurchaseFare() {
        return this.baseFare.add(this.tax).add(this.commission).add(this.fee);
    }

    public BigDecimal calculateGrossPurchaseFare() {
        return this.baseFare.add(this.tax).add(this.fee);
    }
        
    public BigDecimal calculateRevenue() {
        return this.grossFare.add(this.discount).subtract(this.calculateNetPurchaseFare());
    }
}
