package etsbackoffice.domain;

import etsbackoffice.businesslogic.Enums;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * Creation Date: 30 Sep 2010
 * @author Omar
 */
@Entity
@Table(name = "ticket")
public class Ticket implements Serializable {

    private long ticketId;
    private int passengerNo;
    private String paxSurName;
    private String paxForeName;
    private String numericAirLineCode;
    private String ticketNo;
    private String orginalTicketNo;
    private String restrictions;
    private Date docIssuedate;
    private int tktStatus;//1.Booked, 2. Issued,3. ReIssued, 4.Refund,5.VOID
    private BigDecimal baseFareVendor = new BigDecimal("0.00");
    private BigDecimal taxVendor = new BigDecimal("0.00");
    private BigDecimal atolVendor = new BigDecimal("0.00");
    private BigDecimal baseFare = new BigDecimal("0.00");
    private BigDecimal tax = new BigDecimal("0.00");
    private BigDecimal bspCom = new BigDecimal("0.00");
    private BigDecimal grossFare = new BigDecimal("0.00");
    private BigDecimal discount = new BigDecimal("0.00");
    private BigDecimal atolChg = new BigDecimal("0.00");
    private PNR pnr;
    private TicketRefundDetails ticketRefundDetails;
    private Set<AccountingDocumentLine> accountingDocumentLine = new LinkedHashSet<AccountingDocumentLine>();
    private Set<PurchaseAccountingDocumentLine> purchaseAccountingDocumentLine = new LinkedHashSet<PurchaseAccountingDocumentLine>();
    private Set<Itinerary> segments = new LinkedHashSet<Itinerary>();
    private Integer version;

    public Ticket() {
    }

    @Id
    @Column(name = "TKTID")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "tktid")
    @TableGenerator(name = "tktid", table = "tktidpktb",
    pkColumnName = "tktidkey", pkColumnValue = "tktidvalue", allocationSize = 1)
    public long getTicketId() {
        return ticketId;
    }

    public void setTicketId(long ticketId) {
        this.ticketId = ticketId;
    }

    @Basic
    @Column(name = "PAXNO")
    public int getPassengerNo() {
        return passengerNo;
    }

    public void setPassengerNo(int passengerNo) {
        this.passengerNo = passengerNo;
    }

    @Basic
    @Column(name = "TKTNO")
    public String getTicketNo() {
        return ticketNo;
    }

    public void setTicketNo(String ticketNo) {
        this.ticketNo = ticketNo;
    }

    @Temporal(TemporalType.DATE)
    public Date getDocIssuedate() {
        return docIssuedate;
    }

    public void setDocIssuedate(Date docIssuedate) {
        this.docIssuedate = docIssuedate;
    }

    @Basic
    @Column(name = "STATUS")
    public int getTktStatus() {
        return tktStatus;
    }

    public void setTktStatus(int tktStatus) {
        this.tktStatus = tktStatus;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PNRID_FK")
    public PNR getPnr() {
        return pnr;
    }

    public void setPnr(PNR pnr) {
        this.pnr = pnr;
    }

    @Basic
    @Column(name = "P_SNAME")
    public String getPaxSurName() {
        return paxSurName;
    }

    public void setPaxSurName(String paxSurName) {
        this.paxSurName = paxSurName;
    }

    @Basic
    @Column(name = "P_FNAME")
    public String getPaxForeName() {
        return paxForeName;
    }

    public void setPaxForeName(String paxForeName) {
        this.paxForeName = paxForeName;
    }

    @Column(name = "BASEFARE")
    public BigDecimal getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(BigDecimal baseFare) {
        this.baseFare = baseFare;
    }

    @Column(name = "TAX")
    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    @Column(name = "GROSSFARE")
    public BigDecimal getGrossFare() {
        return grossFare;
    }

    public void setGrossFare(BigDecimal grossFare) {
        this.grossFare = grossFare;
    }

    @Column(name = "DISCOUNT")
    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    @Column(name = "ATOL")
    public BigDecimal getAtolChg() {
        return atolChg;
    }

    public void setAtolChg(BigDecimal atolChg) {
        this.atolChg = atolChg;
    }

    public String getNumericAirLineCode() {
        return numericAirLineCode;
    }

    public void setNumericAirLineCode(String numericAirLineCode) {
        this.numericAirLineCode = numericAirLineCode;
    }

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "TKTRFDID_FK")
    public TicketRefundDetails getTicketRefundDetails() {
        return ticketRefundDetails;
    }

    public void setTicketRefundDetails(TicketRefundDetails ticketRefundDetails) {
        this.ticketRefundDetails = ticketRefundDetails;
    }

    //@Transient
    public String getOrginalTicketNo() {
        return orginalTicketNo;
    }

    public void setOrginalTicketNo(String orginalTicketNo) {
        this.orginalTicketNo = orginalTicketNo;
    }

    public BigDecimal getBspCom() {
        return bspCom;
    }

    public void setBspCom(BigDecimal bspCom) {
        this.bspCom = bspCom;
    }

    @ManyToMany
    @JoinTable(name = "join_ticket_itinerary",
    joinColumns = {
        @JoinColumn(name = "ticketId")},
    inverseJoinColumns = {
        @JoinColumn(name = "itineraryId")})
    @OrderBy(value = "itineraryId")
    public Set<Itinerary> getSegments() {
        return segments;
    }

    public void setSegments(Set<Itinerary> segments) {
        this.segments = segments;
    }

    public BigDecimal getBaseFareVendor() {
        return baseFareVendor;
    }

    public void setBaseFareVendor(BigDecimal baseFareVendor) {
        this.baseFareVendor = baseFareVendor;
    }

    public BigDecimal getTaxVendor() {
        return taxVendor;
    }

    public void setTaxVendor(BigDecimal taxVendor) {
        this.taxVendor = taxVendor;
    }

    @ManyToMany//(mappedBy = "tickets", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "join_ticket_acdocline",
    joinColumns = {
        @JoinColumn(name = "TKT_ID")},
    inverseJoinColumns = {
        @JoinColumn(name = "ACDOCLINE_ID")})
    public Set<AccountingDocumentLine> getAccountingDocumentLine() {
        return accountingDocumentLine;
    }

    public void setAccountingDocumentLine(Set<AccountingDocumentLine> accountingDocumentLine) {
        this.accountingDocumentLine = accountingDocumentLine;
    }

    @Version
    @Column(name = "OPTLOCK")
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @ManyToMany//(mappedBy = "tickets", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "join_ticket_pacdocline",
    joinColumns = {
        @JoinColumn(name = "TKT_ID")},
    inverseJoinColumns = {
        @JoinColumn(name = "PACDOCLINE_ID")})
    //@ManyToMany(mappedBy = "tickets")
    public Set<PurchaseAccountingDocumentLine> getPurchaseAccountingDocumentLine() {
        return purchaseAccountingDocumentLine;
    }

    public void setPurchaseAccountingDocumentLine(Set<PurchaseAccountingDocumentLine> purchaseAccountingDocumentLine) {
        this.purchaseAccountingDocumentLine = purchaseAccountingDocumentLine;
    }

    public void addPAcDocLine(PurchaseAccountingDocumentLine pacDocline) {
        this.purchaseAccountingDocumentLine.add(pacDocline);
    }

    public void addAcDocLine(AccountingDocumentLine acDocline) {
        this.accountingDocumentLine.add(acDocline);
    }

    public String getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(String restrictions) {
        this.restrictions = restrictions;
    }

    public void addSegment(Itinerary segment) {
        this.segments.add(segment);
    }

    public BigDecimal getAtolVendor() {
        return atolVendor;
    }

    public void setAtolVendor(BigDecimal atolVendor) {
        this.atolVendor = atolVendor;
    }

    @Transient
    public BigDecimal getNetPayble() {
        return this.grossFare.add(this.atolChg).add(this.discount);
    }

    @Transient
    public BigDecimal getNetBillable() {
        return getNetFare().add(getAtolVendor());
    }

    @Transient
    public BigDecimal getTktdRevenue() {
        return getNetPayble().subtract(getNetFare());
        //return getNetPayble().subtract(this.getNetFare());
    }

    @Transient
    public String getFullPaxNameWithPaxNo() {

        String paxFullName = "";
        paxFullName = getPassengerNo() + ". " + getPaxSurName() + " / " + getPaxForeName();

        return paxFullName;
    }

    @Transient
    public String getFullPaxName() {

        String paxFullName = "";
        paxFullName = getPaxSurName() + " / " + getPaxForeName();

        return paxFullName;
    }

    @Transient
    public String getForeNameNoSuffix() {        
        String foreName = getPaxForeName().replaceAll("\\(.+\\)", "").trim();        
        return foreName;
    }
        
    @Transient
    public String getFullTicketNo() {
        String fullTktNo = "";
        if (getTicketNo() != null) {
            fullTktNo = getNumericAirLineCode() + "-"
                    + getTicketNo();
        }
        return fullTktNo;
    }

    @Transient
    public BigDecimal getTotalFare() {
        return getBaseFare().add(getTax());
    }

    @Transient
    public BigDecimal getNetFare() {
        if (this.getTktStatus() == 4) {
            return getTotalFare().add(getBspCom()).add(this.getTicketRefundDetails().getCancellationFee()).
                    add(this.getTicketRefundDetails().getCMiscFee()).add(this.getTicketRefundDetails().getCfCom());
        } else {
            return getTotalFare().add(getBspCom());
        }
    }

    @Transient
    public String getTktStatusString() {
        return Enums.TicketStatus.valueOf(this.getTktStatus());
    }
    
    @Transient
    public BigDecimal getBFRefundCalculation() {
        setBaseFare(this.getTicketRefundDetails().getFarePaid().negate().add(this.getTicketRefundDetails().getFareUsed()));
        return getBaseFare();
    }            
}
