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
 *
 * @author Yusuf
 */
@Entity
@Table(name = "purchaseacdoc")
public class PurchaseAccountingDocument implements Serializable {

    private long purchaseAcDocId;
    private int acDoctype;//1: Invoice, 2:TktRefundCreditNote,3: CreditNote, 4: Debit Note
    private Date issueDate;
    private String vendorRef;
    private String recipientRef;
    private String terms;
    private int vendor; //1.IATA
    private User acDocIssuedBy;    
    private User acDocModifiedBy;
    private PNR pnr;
    private Agent acDocFrom;//Transient
    private Agent acDocTo;//Transient
    private Set<BillingTransaction> billingTransactions = new LinkedHashSet<BillingTransaction>();
    private Set<PurchaseAccountingDocumentLine> purchaseAcDocLines = new LinkedHashSet<PurchaseAccountingDocumentLine>();
    private Set<PurchaseAccountingDocument> relatedDocuments = new LinkedHashSet<PurchaseAccountingDocument>();
    private PurchaseAccountingDocument purchaseAccountingDocument;
    private Set<AccountingDocument> accountingDocuments = new LinkedHashSet();
    private Set<Accounts> accounts = new LinkedHashSet<Accounts>();
    private boolean active;

    public PurchaseAccountingDocument() {
    }

    @Id
    @Column(name = "PURCHASEACDOCID")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "puracdocid")
    @TableGenerator(name = "puracdocid", table = "puracdocidpktb",
    pkColumnName = "puracdocidkey", pkColumnValue = "puracdocidvalue", allocationSize = 1)
    public long getPurchaseAcDocId() {
        return purchaseAcDocId;
    }

    public void setPurchaseAcDocId(long purchaseAcDocId) {
        this.purchaseAcDocId = purchaseAcDocId;
    }

    public int getAcDoctype() {
        return acDoctype;
    }

    public void setAcDoctype(int acDoctype) {
        this.acDoctype = acDoctype;
    }

    @Temporal(TemporalType.DATE)
    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    @ManyToOne
    public User getAcDocIssuedBy() {
        return acDocIssuedBy;
    }

    public void setAcDocIssuedBy(User acDocIssuedBy) {
        this.acDocIssuedBy = acDocIssuedBy;
    }

    @ManyToOne
    public User getAcDocModifiedBy() {
        return acDocModifiedBy;
    }

    public void setAcDocModifiedBy(User acDocModifiedBy) {
        this.acDocModifiedBy = acDocModifiedBy;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PNRID_FK")
    public PNR getPnr() {
        return pnr;
    }

    public void setPnr(PNR pnr) {
        this.pnr = pnr;
    }

    @OneToMany(mappedBy = "purchaseAccountingDocument")
    public Set<PurchaseAccountingDocument> getRelatedDocuments() {
        return relatedDocuments;
    }

    public void setRelatedDocuments(Set<PurchaseAccountingDocument> relatedDocuments) {
        this.relatedDocuments = relatedDocuments;
    }

    @ManyToOne
    public PurchaseAccountingDocument getPurchaseAccountingDocument() {
        return purchaseAccountingDocument;
    }

    public void setPurchaseAccountingDocument(PurchaseAccountingDocument purchaseAccountingDocument) {
        this.purchaseAccountingDocument = purchaseAccountingDocument;
    }

    @OneToMany(targetEntity = BillingTransaction.class, mappedBy = "purchaseAccountingDocument",
    cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public Set<BillingTransaction> getBillingTransactions() {
        return billingTransactions;
    }

    public void setBillingTransactions(Set<BillingTransaction> billingTransactions) {
        this.billingTransactions = billingTransactions;
    }

    @OneToMany(targetEntity = PurchaseAccountingDocumentLine.class, mappedBy = "purchaseAccountingDocument",
    cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy(value = "purchaseAcDocLineId")
    public Set<PurchaseAccountingDocumentLine> getPurchaseAcDocLines() {
        return purchaseAcDocLines;
    }

    public void setPurchaseAcDocLines(Set<PurchaseAccountingDocumentLine> purchaseAcDocLines) {
        this.purchaseAcDocLines = purchaseAcDocLines;
    }

    @OneToMany(mappedBy = "purchaseAccountingDocument", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public Set<Accounts> getAccounts() {
        return accounts;
    }

    public void setAccounts(Set<Accounts> accounts) {
        this.accounts = accounts;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    
    public void addAcStatement(Accounts stmt) {
        Set<Accounts> stmts = new LinkedHashSet<Accounts>();
        stmts.add(stmt);
        setAccounts(stmts);
    }

    @Transient
    public String getAcDocTypeString() {
        String acDocType = "";
        if (getAcDoctype() == 1) {
            acDocType = "INVOICE";
        } else if (getAcDoctype() == 2) {
            acDocType = "CNOTE";
        } else if (getAcDoctype() == 3) {
            acDocType = "CNOTE";
        }
        return acDocType;
    }

    @Transient
    public int getNoOfPaxInCurrentAcDoc() {
        return getTickets().size();
    }

    @Transient
    public BigDecimal getSubTotal() {
        BigDecimal subtotal = new BigDecimal("0.00");
        for (Ticket t : getTickets()) {
            subtotal = subtotal.add(t.getNetBillable());
        }
        return subtotal;
    }

    @Transient
    public BigDecimal getPostage() {
        BigDecimal postage = new BigDecimal("0.00");
        for (PurchaseAccountingDocumentLine line : getPurchaseAcDocLines()) {
            if (line.getType() == 4) {
                postage = line.getAmount();
            }
        }
        return postage;
    }

    @Transient
    public BigDecimal getAdmiinCharge() {
        BigDecimal adminCharge = new BigDecimal("0.00");
        for (PurchaseAccountingDocumentLine line : getPurchaseAcDocLines()) {
            if (line.getType() == 2) {
                adminCharge = line.getAmount();
            }
        }
        return adminCharge;
    }

    @Transient
    public Set<Itinerary> getSegments() {
        Set<Itinerary> segments = new LinkedHashSet<Itinerary>();
        for (Ticket t : getTickets()) {
            segments.addAll(t.getSegments());
        }
        return segments;
    }

    @Transient
    public Set<Ticket> getTickets() {
        Set<Ticket> tickets = new LinkedHashSet();
        for (PurchaseAccountingDocumentLine acDocLine : this.purchaseAcDocLines) {
            if (!acDocLine.getTickets().isEmpty()) {
                tickets = acDocLine.getTickets();
            }
        }
        return tickets;
    }

    @Transient
    public BigDecimal getOtherCharge() {
        BigDecimal otherCharge = new BigDecimal("0.00");
        for (PurchaseAccountingDocumentLine line : getPurchaseAcDocLines()) {
            if (line.getType() == 2) {
                otherCharge = line.getAmount();
            }
        }
        return otherCharge;
    }

    @Transient
    public BigDecimal getBalanceToThisDocument() {
        return getTotalPaid().subtract(getTotalDocumentedAmount());
    }

    @Transient
    public BigDecimal getOutstandingToThisDocument() {
        return getTotalDocumentedAmount().subtract(getTotalPaid());
    }

    @Transient
    public BigDecimal getOutstandingAmountWithRelatedDocuments() {
        BigDecimal outstandingRd = new BigDecimal("0.00");
        for (PurchaseAccountingDocument rd : this.relatedDocuments) {
            outstandingRd = outstandingRd.add(rd.getOutstandingToThisDocument());
        }
        return getTotalDocumentedAmount().subtract(getTotalPaid()).add(outstandingRd);
    }

    @Transient
    public BigDecimal getTotalPaid() {
        BigDecimal totalReceived = new BigDecimal("0.00");
        for (BillingTransaction t : this.billingTransactions) {
            totalReceived = totalReceived.add(t.getTransAmount());
        }
        return totalReceived;
    }

    @Transient
    public BigDecimal getTotalTransactionAmountWithRelatedDoc() {
        BigDecimal totalTransAmount = new BigDecimal("0.00");
        for (BillingTransaction t : this.billingTransactions) {
            totalTransAmount = totalTransAmount.add(t.getTransAmount());
        }
        for (PurchaseAccountingDocument rd : this.getRelatedDocuments()) {
            for (BillingTransaction t : rd.billingTransactions) {
                totalTransAmount = totalTransAmount.add(t.getTransAmount());
            }
        }
        return totalTransAmount;
    }
    
    @Transient
    public List<BillingTransaction> getAlltransaction() {
        List<BillingTransaction> bts = new ArrayList();
        bts.addAll(this.billingTransactions);
        
        for (PurchaseAccountingDocument rd : this.getRelatedDocuments()) {
            bts.addAll(rd.getBillingTransactions());
        }
        return bts;
    }
        
    @Transient
    public BigDecimal getTotalAdm() {
        BigDecimal totalAdm = new BigDecimal("0.00");

        for (PurchaseAccountingDocument creditNote : getRelatedDocuments()) {
            totalAdm = totalAdm.add(creditNote.getTotalDocumentedAmount());
        }
        return totalAdm;
    }

    @Transient
    public BigDecimal getTotalReceivedFromCNote() {
        BigDecimal totalPaid = new BigDecimal("0.00");
        for (PurchaseAccountingDocument rd : this.getRelatedDocuments()) {
            for (BillingTransaction t : rd.billingTransactions) {
                totalPaid = totalPaid.add(t.getTransAmount());
            }
        }
        return totalPaid;
    }
        
    @Transient
    public BigDecimal getTotalDocumentedAmount() {
        BigDecimal tktNetBillable = new BigDecimal("0.00");
        BigDecimal otherTotal = new BigDecimal("0.00");

        for (PurchaseAccountingDocumentLine acDocLine : this.purchaseAcDocLines) {
            if (acDocLine.getType() == 1) {
                for (Ticket t : acDocLine.getTickets()) {

                    tktNetBillable = tktNetBillable.add(t.getNetBillable());
                }
            } else if (acDocLine.getType() == 2) {
                otherTotal = otherTotal.add(acDocLine.getAmount());
            }else{
                otherTotal = otherTotal.add(acDocLine.getAmount());            
            }
        }
        return tktNetBillable.add(otherTotal);
    }

    @Transient
    public BigDecimal getOutstandingAmount() {
        return getTotalDocumentedAmount().subtract(getTotalPaid());
    }
    
    @Transient
    public BigDecimal getDueAmountWithRelatedDoc(){
     return getTotalDocumentedAmount().subtract(getTotalTransactionAmountWithRelatedDoc()).add(this.getTotalAdm());
    }
    
    @Transient
    public Agent getPurchaseAcDocFrom() {
        return acDocFrom;
    }

    public void setPurchaseAcDocFrom(Agent acDocFrom) {
        this.acDocFrom = acDocFrom;
    }

    @Transient
    public Agent getAcDocTo() {
        return acDocTo;
    }

    public void setAcDocTo(Agent acDocTo) {
        this.acDocTo = acDocTo;
    }

    public String getVendorRef() {
        return vendorRef;
    }

    public void setVendorRef(String vendorRef) {
        this.vendorRef = vendorRef;
    }

    public String getRecipientRef() {
        return recipientRef;
    }

    public void setRecipientRef(String recipientRef) {
        this.recipientRef = recipientRef;
    }

    public void addLine(PurchaseAccountingDocumentLine line) {
        this.purchaseAcDocLines.add(line);
    }

    @ManyToMany
    @JoinTable(name = "join_pacDoc_sAcDoc",
    joinColumns = {
        @JoinColumn(name = "pAcDocId")},
    inverseJoinColumns = {
        @JoinColumn(name = "sAcDocId")})
    public Set<AccountingDocument> getAccountingDocuments() {
        return accountingDocuments;
    }

    public void setAccountingDocuments(Set<AccountingDocument> accountingDocuments) {
        this.accountingDocuments = accountingDocuments;
    }

    public void addaccountingDocument(AccountingDocument acDoc) {
        this.accountingDocuments.add(acDoc);
    }

    @Transient
    public Set<PurchaseAccountingDocumentLine> getAdditionalServiceLine() {

        Set<PurchaseAccountingDocumentLine> additionalServices = new LinkedHashSet();
        for (PurchaseAccountingDocumentLine l : this.purchaseAcDocLines) {
            if (l.getType() == 2) {
                if (l.getOtherService().getServiceType() == 2) {
                    additionalServices.add(l);
                }
            }
        }
        return additionalServices;
    }

    @Transient
    public Set<PurchaseAccountingDocumentLine> getTicketLine() {

        Set<PurchaseAccountingDocumentLine> tktLine = new LinkedHashSet();
        for (PurchaseAccountingDocumentLine l : this.purchaseAcDocLines) {
            if (l.getType() == 1) {
                tktLine.add(l);
            }
        }
        return tktLine;
    }

    @Transient
    public BigDecimal getAdditionalServiceSubTotal() {
        BigDecimal subTotal = new BigDecimal("0.00");

        for (PurchaseAccountingDocumentLine l : this.purchaseAcDocLines) {
            if (l.getType() == 2) {//Type 2 is additional service
                if (l.getOtherService().getServiceType() == 2) {
                    subTotal = subTotal.add(l.getAmount());
                }
            }
        }
        return subTotal;
    }

    @Transient
    public BigDecimal getTktdSubTotal() {
        BigDecimal subTotal = new BigDecimal("0.00");

        for (PurchaseAccountingDocumentLine l : this.purchaseAcDocLines) {
            if (l.getType() == 1) {
                subTotal = subTotal.add(l.getTktNetBillable());
            }
        }
        return subTotal;
    }

    @Transient
    public BigDecimal getTNetFare() {
        BigDecimal tNetFare = new BigDecimal("0.00");

        for (Ticket t : this.getTickets()) {
            tNetFare = tNetFare.add(t.getNetFare());
        }
        return tNetFare;
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
    public void applyTransToThisAcDoc(BillingTransaction newTrans) {
        this.billingTransactions.add(newTrans);
    }
    @Transient
    public String getAcDocFrom() {
        if (this.pnr.getTicketingAgt() != null) {
            return this.pnr.getTicketingAgt().getName();
        } else {
            return null;
        }
    }
    
    @Transient
    public BigDecimal getRevenueAdm() {
        BigDecimal revAdm = new BigDecimal("0.00");

        for (PurchaseAccountingDocument a : this.getRelatedDocuments()) {
            if (a.getAcDoctype() == 3 || a.getAcDoctype() == 4) {
                revAdm = revAdm.subtract(a.getTotalDocumentedAmount());
            }
        }
        return revAdm;
    }
}
