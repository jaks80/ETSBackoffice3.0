package etsbackoffice.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.*;

/**
 * Creation Date: 30 Sep 2010
 * @author Yusuf
 */
@Entity
@Table(name = "pnr")
public class PNR implements Serializable {

    private long pnrId;
    private String gdsPNR;
    private int noOfPax;
    private String bookingAgtOID;
    private String IATAbAGT;
    private String ticketingAgtOID;
    private String iataTicketingAGT;
    private Date pnrCreationDate;
    private String vendorPNR;
    private Career servicingCareer;
    private GDS gds;// 1- Amadeus, 2- Galileo, 3- Worldspan, 4- Sabre
    private Set<Ticket> tickets = new LinkedHashSet<Ticket>();
    private Set<Services> services = new LinkedHashSet<Services>();
    private Set<Itinerary> segments = new LinkedHashSet<Itinerary>();
    private Set<AccountingDocument> accountingDocuments = new LinkedHashSet<AccountingDocument>();
    private Set<PurchaseAccountingDocument> purchaseAccountingDocuments = new LinkedHashSet<PurchaseAccountingDocument>();
    private Set<AcTransaction> acTransactions = new LinkedHashSet<AcTransaction>();
    private Set<BillingTransaction> billingTransactions = new LinkedHashSet<BillingTransaction>();
    private Set<PNRRemark> remarks = new LinkedHashSet<PNRRemark>();
    //private Set<PNRLog> pnrLogs = new LinkedHashSet<PNRLog>();
    private Ticket leadPax;
    private Agent agent;
    private Customer customer;
    private Agent ticketingAgt;

    public PNR() {
    }

    @Id
    @Column(name = "PNRID")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "pnrid")
    @TableGenerator(name = "pnrid", table = "pnridpktb",
    pkColumnName = "pnridkey", pkColumnValue = "pnridvalue", allocationSize = 1)
    public long getPnrId() {        
        return pnrId;
    }

    public void setPnrId(long pnrId) {
        this.pnrId = pnrId;
    }

    @Basic
    @Column(name = "GDSPNR")
    public String getGdsPNR() {
        return gdsPNR;
    }

    public void setGdsPNR(String gdsPNR) {
        this.gdsPNR = gdsPNR;
    }

    @Basic
    @Column(name = "NOOFPAX")
    public int getNoOfPassenger() {
        return noOfPax;
    }

    public void setNoOfPassenger(int noOfPassenger) {
        this.noOfPax = noOfPassenger;
    }

    @Basic
    @Column(name = "B_AGT_OID")
    public String getBookingAgtOID() {
        return bookingAgtOID;
    }

    public void setBookingAgtOID(String bookingAgtOID) {
        this.bookingAgtOID = bookingAgtOID;
    }

    @Basic
    @Column(name = "B_AGT_IATA")
    public String getBookingAGTIATANo() {
        return IATAbAGT;
    }

    public void setBookingAGTIATANo(String bookingAGTIATANo) {
        this.IATAbAGT = bookingAGTIATANo;
    }

    @Basic
    @Column(name = "T_AGT_OID")
    public String getTicketingAgtOID() {
        return ticketingAgtOID;
    }

    public void setTicketingAgtOID(String ticketingAgtOID) {
        this.ticketingAgtOID = ticketingAgtOID;
    }

    @Basic
    @Column(name = "T_AGT_IATA")
    public String getTicketingAGTIATANo() {
        return iataTicketingAGT;
    }

    public void setTicketingAGTIATANo(String ticketingAGTIATANo) {
        this.iataTicketingAGT = ticketingAGTIATANo;
    }

    @Temporal(TemporalType.DATE)
    public Date getPnrCreationDate() {
        return pnrCreationDate;
    }

    public void setPnrCreationDate(Date pnrCreationDate) {
        this.pnrCreationDate = pnrCreationDate;
    }

    @Basic
    @Column(name = "VENDORPNR")
    public String getVendorPNR() {
        return vendorPNR;
    }

    public void setVendorPNR(String vendorPNR) {
        this.vendorPNR = vendorPNR;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    //@JoinColumn(name = "CARCODE",referencedColumnName ="CODE")
    @JoinColumn(name = "CARCODE")
    public Career getServicingCareer() {
        return servicingCareer;
    }

    public void setServicingCareer(Career servicingCareer) {
        this.servicingCareer = servicingCareer;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GDSID_FK", referencedColumnName = "gdsId")
    public GDS getGds() {
        return gds;
    }

    public void setGds(GDS gds) {
        this.gds = gds;
    }

    @OneToMany(targetEntity = Ticket.class, mappedBy = "pnr",
    cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy(value = "ticketId")
    public Set<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(Set<Ticket> tickets) {
        this.tickets = tickets;
    }

    @OneToMany(targetEntity = Services.class, mappedBy = "pnr",
    cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy(value = "serviceId")
    public Set<Services> getServices() {
        return services;
    }

    public void setServices(Set<Services> services) {
        this.services = services;
    }

    @OneToMany(targetEntity = Itinerary.class, mappedBy = "pnr",
    cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public Set<Itinerary> getSegments() {
        return segments;
    }

    public void setSegments(Set<Itinerary> segments) {
        this.segments = segments;
    }

    @OneToMany(targetEntity = AccountingDocument.class, mappedBy = "pnr",
    cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy(value = "acDocId asc")
    public Set<AccountingDocument> getAccountingDocuments() {
        return accountingDocuments;
    }

    public void setAccountingDocuments(Set<AccountingDocument> accountingDocuments) {
        this.accountingDocuments = accountingDocuments;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "AGTID_FK")
    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "CUSTID_FK")
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @OneToMany(targetEntity = AcTransaction.class, mappedBy = "pnr",
    cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy(value = "transDate desc")
    public Set<AcTransaction> getAcTransactions() {
        return acTransactions;
    }

    public void setAcTransactions(Set<AcTransaction> acTransactions) {
        this.acTransactions = acTransactions;
    }

    @OneToMany(targetEntity = PNRRemark.class, mappedBy = "pnr",
    cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public Set<PNRRemark> getRemarks() {
        return remarks;
    }

    public void setRemarks(Set<PNRRemark> remarks) {
        this.remarks = remarks;
    }

    @OneToMany(targetEntity = BillingTransaction.class, mappedBy = "pnr",
    cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public Set<BillingTransaction> getBillingTransactions() {
        return billingTransactions;
    }

    public void setBillingTransactions(Set<BillingTransaction> billingTransactions) {
        this.billingTransactions = billingTransactions;
    }

    @OneToMany(targetEntity = PurchaseAccountingDocument.class, mappedBy = "pnr",
    cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy(value = "purchaseAcDocId")
    public Set<PurchaseAccountingDocument> getPurchaseAccountingDocuments() {
        return purchaseAccountingDocuments;
    }

    public void setPurchaseAccountingDocuments(Set<PurchaseAccountingDocument> purchaseAccountingDocuments) {
        this.purchaseAccountingDocuments = purchaseAccountingDocuments;
    }

    @Transient
    public Ticket getLeadPax() {
        return leadPax;
    }

    public void setLeadPax(Ticket mainPassenger) {
        this.leadPax = mainPassenger;
    }

    @Transient
    public String getLeadPaxFromTickets() {

        Ticket leadPaxTicket = null;

        List<Integer> paxNos = new ArrayList();

        for (Ticket t : getTickets()) {
            paxNos.add(t.getPassengerNo());
        }
        Collections.sort(paxNos);
        loop:
        for (Ticket t : getTickets()) {
            if (t.getPassengerNo() == paxNos.get(0)) {
                leadPaxTicket = t;
                break loop;
            }
        }
        return leadPaxTicket.getFullPaxNameWithPaxNo();
    }

    @Transient
    public String getLeadPaxSurNameFromTickets() {

        Ticket leadPaxTicket = null;

        List<Integer> paxNos = new ArrayList();

        for (Ticket t : getTickets()) {
            paxNos.add(t.getPassengerNo());
        }
        Collections.sort(paxNos);
        loop:
        for (Ticket t : getTickets()) {
            if (t.getPassengerNo() == paxNos.get(0)) {
                leadPaxTicket = t;
                break loop;
            }
        }
        return leadPaxTicket.getPaxSurName();
    }

    @Transient
    public BigDecimal getTotalPurchaseCost() {
        BigDecimal purchaseCost = new BigDecimal("0.00");

        for (Ticket t : getTickets()) {
            if (t.getTktStatus() == 2) {
                purchaseCost = purchaseCost.add(t.getNetFare());
            }
        }
        return purchaseCost;
    }

    public void addPurchaseAcDoc(PurchaseAccountingDocument pAcDoc) {
        this.purchaseAccountingDocuments.add(pAcDoc);
    }

    public void addAllPurchaseAcDoc(List<PurchaseAccountingDocument> pAcDocs) {
        for (PurchaseAccountingDocument p : pAcDocs) {
            this.purchaseAccountingDocuments.add(p);
        }
    }

    public void addAccountingDocument(AccountingDocument acDoc) {
        this.accountingDocuments.add(acDoc);
    }

    public void addAllTickets(List<Ticket> tickets) {
        for (Ticket t : tickets) {
            this.tickets.add(t);
        }
    }

    public void addTicket(Ticket t) {
        this.tickets.add(t);
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "VENDORID_FK")
    public Agent getTicketingAgt() {
        return ticketingAgt;
    }

    public void setTicketingAgt(Agent ticketingAgt) {
        this.ticketingAgt = ticketingAgt;
    }

    /* @OneToMany(mappedBy = "pnr")
    public Set<PNRLog> getPnrLogs() {
    return pnrLogs;
    }
    
    public void setPnrLogs(Set<PNRLog> pnrLogs) {
    this.pnrLogs = pnrLogs;
    }
    
    public void addLog(PNRLog log){
    this.pnrLogs.add(log);
    }*/
    @Transient
    public AccountingDocument getInvoice() {
        AccountingDocument doc = null;
        for (AccountingDocument a : this.accountingDocuments) {
            if (a.getAcDoctype() == 1) {
                doc = a;
            }
        }
        return doc;
    }

    public void addAllRemark(Set<PNRRemark> remarks) {
        this.remarks.addAll(remarks);
    }

    @Transient
    public Long getContactableId() {
        if (this.agent != null) {
            return this.agent.getContactableId();
        } else if (this.customer != null) {
            return this.customer.getContactableId();
        } else {
            return null;
        }
    }

    @Transient
    public long getContactableIdTktingAgt() {
        if (this.ticketingAgt != null) {
            return this.ticketingAgt.getContactableId();
        } else {
            return 0;
        }
    }

    @Transient
    public int getContactableType() {
        if (this.agent != null) {
            return 1;
        } else {
            return 2;
        }
    }
        
    public boolean hasUninvoicedTicket() {
        boolean hasUninvoicedTkt = false;

        for (Ticket t : this.tickets) {
            if (t.getAccountingDocumentLine().isEmpty()) {
                hasUninvoicedTkt = true;
                break;
            }
        }
        return hasUninvoicedTkt;
    }
}
