package com.ets.pnr.domain;

import com.ets.PersistentObject;
import com.ets.accountingdoc.domain.TicketingPurchaseAcDoc;
import com.ets.accountingdoc.domain.TicketingSalesAcDoc;
import com.ets.util.DateUtil;
import com.ets.util.Enums.TicketStatus;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Yusuf
 */
@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@Access(AccessType.PROPERTY)
public class Ticket extends PersistentObject implements Serializable {

    private static long serialVersionUID = 1L;
    @XmlElement
    private Integer passengerNo;
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
    private BigDecimal netPurchaseFare = new BigDecimal("0.00");
    @XmlElement
    private BigDecimal grossFare = new BigDecimal("0.00");
    @XmlElement
    private BigDecimal discount = new BigDecimal("0.00");
    @XmlElement
    private BigDecimal atolChg = new BigDecimal("0.00");
    @XmlElement
    private BigDecimal netSellingFare = new BigDecimal("0.00");

    @XmlElement
    private TicketStatus tktStatus;//1.Booked, 2. Issued,3. ReIssued, 4.Refund,5.VOID    

    public Ticket() {

    }

    @Transient
    public String getFullTicketNo() {
        if (this.getNumericAirLineCode() != null && this.getTicketNo() != null) {
            return this.getNumericAirLineCode() + "-" + this.getTicketNo();
        } else {
            return null;
        }
    }

    @Transient
    public String getTktDateString() {
        return DateUtil.dateToString(getDocIssuedate());
    }

    @Transient
    public String getTktStatusString() {
        return this.tktStatus.name();
    }

    @Transient
    public String getFullPaxNameWithPaxNo() {

        String paxFullName = "";
        paxFullName = getPassengerNo() + ". " + getPaxSurName() + " / " + getPaxForeName();

        return paxFullName;
    }

    public BigDecimal calculateNetSellingFare() {
        return this.grossFare.add(this.atolChg).add(this.discount);
    }

    public BigDecimal calculateNetPurchaseFare() {
        return this.baseFare.add(this.tax).add(this.commission).add(this.fee);
    }

    public Integer getPassengerNo() {
        return passengerNo;
    }

    public void setPassengerNo(Integer passengerNo) {
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

    @Enumerated(EnumType.ORDINAL)
    public TicketStatus getTktStatus() {
        return tktStatus;
    }

    public void setTktStatus(TicketStatus tktStatus) {
        this.tktStatus = tktStatus;
    }

    public String getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(String restrictions) {
        this.restrictions = restrictions;
    }

    @Temporal(TemporalType.DATE)
    public Date getDocIssuedate() {
        return docIssuedate;
    }

    public void setDocIssuedate(Date docIssuedate) {
        this.docIssuedate = docIssuedate;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pnr_fk")
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tsacdoc_fk")
    public TicketingSalesAcDoc getTicketingSalesAcDoc() {
        return ticketingSalesAcDoc;
    }

    public void setTicketingSalesAcDoc(TicketingSalesAcDoc ticketingSalesAcDoc) {
        this.ticketingSalesAcDoc = ticketingSalesAcDoc;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tpacdoc_fk")
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

    public void setNetSellingFare(BigDecimal netSellingFare) {
        this.netSellingFare = netSellingFare;
    }

    public BigDecimal getNetSellingFare() {
        return netSellingFare;
    }

    public BigDecimal getNetPurchaseFare() {
        return netPurchaseFare;
    }

    public void setNetPurchaseFare(BigDecimal netPurchaseFare) {
        this.netPurchaseFare = netPurchaseFare;
    }
}
