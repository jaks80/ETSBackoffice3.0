package etsbackoffice.domain;

import etsbackoffice.businesslogic.AuthenticationBo;
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
 * This class is mainly to issue invoice to client. Invoice will follow
 * AcDocAdjustment which can issue further debit note or credit note.
 */
@Entity
@Table(name = "acdoc")
public class AccountingDocument implements Serializable {

    private long acDocId;
    private int acDoctype;//1: Invoice, 2:TktRefundCreditNote,3: CreditNote ,4: Debit note
    private Date issueDate;
    private Date lastModifiedDate;
    private Integer acDocRef;
    private String terms;
    private User acDocIssuedBy;
    private User acDocModifiedBy;
    private PNR pnr;
    private Set<AcTransaction> acTransactions = new LinkedHashSet<AcTransaction>();
    private Set<AccountingDocumentLine> accountingDocumentLines = new LinkedHashSet<AccountingDocumentLine>();
    private Set<AccountingDocument> relatedDocuments = new LinkedHashSet<AccountingDocument>();
    private AccountingDocument accountingDocument;
    private Set<PurchaseAccountingDocument> purchaseAccountingDocuments = new LinkedHashSet<PurchaseAccountingDocument>();
    private boolean active;
    private Set<Accounts> accounts = new LinkedHashSet<Accounts>();
    private Integer version;
    
    private String invoiceTerms = "";
    private String invoiceFooter = "";
    
    public AccountingDocument() {
    }

    @Id
    @Column(name = "ACDOCID")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acdocid")
    @TableGenerator(name = "acdocid", table = "acdocidpktb",
    pkColumnName = "acdocidkey", pkColumnValue = "acdocidvalue", allocationSize = 1)
    public long getAcDocId() {
        return acDocId;
    }

    public void setAcDocId(long acDocId) {
        this.acDocId = acDocId;
    }

    @Temporal(TemporalType.DATE)
    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PNRID_FK")
    public PNR getPnr() {
        return pnr;
    }

    public void setPnr(PNR pnr) {
        this.pnr = pnr;
    }

    public Integer getAcDocRef() {
        return acDocRef;
    }

    public void setAcDocRef(Integer acDocRef) {
        this.acDocRef = acDocRef;
    }

    public int getAcDoctype() {
        return acDoctype;
    }

    public void setAcDoctype(int acDoctype) {
        this.acDoctype = acDoctype;
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

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    @OneToMany(targetEntity = AcTransaction.class, mappedBy = "accountingDocument",
    cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy(value = "transDate asc")
    public Set<AcTransaction> getAcTransactions() {
        return acTransactions;
    }

    public void setAcTransactions(Set<AcTransaction> acTransactions) {
        this.acTransactions = acTransactions;
    }

    @OneToMany(targetEntity = AccountingDocumentLine.class, mappedBy = "accountingDocument",
    cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy(value = "acDocLineId asc")
    public Set<AccountingDocumentLine> getAccountingDocumentLines() {
        return accountingDocumentLines;
    }

    public void setAccountingDocumentLines(Set<AccountingDocumentLine> accountingDocumentLines) {
        this.accountingDocumentLines = accountingDocumentLines;
    }

    @OneToMany(mappedBy = "accountingDocument")
    public Set<AccountingDocument> getRelatedDocuments() {
        return relatedDocuments;
    }

    public void setRelatedDocuments(Set<AccountingDocument> relatedDocuments) {
        this.relatedDocuments = relatedDocuments;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @ManyToOne
    public AccountingDocument getAccountingDocument() {
        return accountingDocument;
    }

    public void setAccountingDocument(AccountingDocument accountingDocument) {
        this.accountingDocument = accountingDocument;
    }

    @ManyToMany
    @JoinTable(name = "join_pacDoc_sAcDoc",
    joinColumns = {
        @JoinColumn(name = "sAcDocId")},
    inverseJoinColumns = {
        @JoinColumn(name = "pAcDocId")})
    public Set<PurchaseAccountingDocument> getPurchaseAccountingDocuments() {
        return purchaseAccountingDocuments;
    }

    public void setPurchaseAccountingDocuments(Set<PurchaseAccountingDocument> purchaseAccountingDocuments) {
        this.purchaseAccountingDocuments = purchaseAccountingDocuments;
    }

    @OneToMany(mappedBy = "accountingDocument", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public Set<Accounts> getAccounts() {
        return accounts;
    }

    public void setAccounts(Set<Accounts> accounts) {
        this.accounts = accounts;
    }

    public void addAcStatement(Accounts stmt) {
        Set<Accounts> stmts = new LinkedHashSet<Accounts>();
        stmts.add(stmt);
        setAccounts(stmts);
    }

    @Version
    @Column(name = "OPTLOCK")
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Transient
    public String getInvoiceTerms() {
        return invoiceTerms;
    }

    public void setInvoiceTerms(String invoiceTerms) {
        this.invoiceTerms = invoiceTerms;
    }
    
    @Transient
    public String getAcDocTypeString() {
        String acDocType = "";
        if (getAcDoctype() == 1) {
            acDocType = "INV";
        } else if (getAcDoctype() == 2) {
            acDocType = "CRN";
        } else if (getAcDoctype() == 3) {
            acDocType = "CRN";
        }
        return acDocType;
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
        for (AccountingDocumentLine acDocLine : this.accountingDocumentLines) {
            if (acDocLine.getType() == 1) {
                tickets.addAll(acDocLine.getTickets());
            }
        }
        return tickets;
    }

    @Transient
    public List<Services> getOtherservices() {
        List<Services> oServices = new ArrayList();
        for (AccountingDocumentLine acDocLine : this.accountingDocumentLines) {
            if (acDocLine.getType() == 2) {
                for (Services s : acDocLine.getServices()) {
                    if (s.getServiceType() == 1) {
                        oServices.add(s);
                    }
                }
            }
        }
        return oServices;
    }
        
    @Transient
    public Set<Services> getAdditionalServices() {
        Set<Services> aServices = new LinkedHashSet();
        for (AccountingDocumentLine acDocLine : this.accountingDocumentLines) {
            if (acDocLine.getType() == 2) {
                for (Services s : acDocLine.getServices()) {
                    if (s.getServiceType() == 2) {
                        aServices.add(s);
                    }
                }
            }
        }
        return aServices;
    }
        
    @Transient
    public Set<Services> getAllServices() {
        Set<Services> aServices = new LinkedHashSet();
        for (AccountingDocumentLine acDocLine : this.accountingDocumentLines) {
            
                for (Services s : acDocLine.getServices()) {
                    aServices.add(s);
                }
            
        }
        return aServices;
    }
        
    @Transient
    public String getLeadPax() {
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
    public BigDecimal getSubTotal() {
        BigDecimal subtotal = new BigDecimal("0.00");
        for (Ticket t : getTickets()) {
            subtotal = subtotal.add(t.getNetPayble());
        }
        return subtotal;
    }

    @Transient
    public BigDecimal getOutstandingAmount() {
        return getTotalDocumentedAmount().subtract(getTotalTransactionAmount());
    }

    @Transient
    public BigDecimal getTotalTransactionAmount() {
        BigDecimal totalTransAmount = new BigDecimal("0.00");
        for (AcTransaction t : this.acTransactions) {
            if (t.isActive()) {
                totalTransAmount = totalTransAmount.add(t.getTransAmount());
            }
        }
        return totalTransAmount;
    }

    @Transient
    public BigDecimal getTotalTransactionAmountWithRelatedDoc() {
        BigDecimal totalTransAmount = new BigDecimal("0.00");
        for (AcTransaction t : this.acTransactions) {
            if (t.isActive()) {
                totalTransAmount = totalTransAmount.add(t.getTransAmount());
            }
        }
        for (AccountingDocument rd : this.getRelatedDocuments()) {
            for (AcTransaction t : rd.acTransactions) {
                if (t.isActive()) {
                    totalTransAmount = totalTransAmount.add(t.getTransAmount());
                }
            }
        }
        return totalTransAmount;
    }

    @Transient
    public BigDecimal getTotalPaidFromCNote() {
        BigDecimal totalPaid = new BigDecimal("0.00");
        for (AccountingDocument rd : this.getRelatedDocuments()) {
            for (AcTransaction t : rd.acTransactions) {
                totalPaid = totalPaid.add(t.getTransAmount());
            }
        }
        return totalPaid;
    }
   
    @Transient
    public BigDecimal getTotalDocumentedAmount() {        
            return getTktdSubTotal().add(getOtherServiceSubTotal()).add(getAdditionalServiceSubTotal()).add(getUnspecifiedSubTotal());                      
    }

    @Transient
    public String getAcDocRefString() {
        String prefix = AuthenticationBo.getmAgent().getmAgentCode();
        String suffix = "";
        if (this.acDoctype == 1) {
            suffix = "TIN";
        } else if (this.acDoctype == 2) {
            suffix = "TCN";
        }
        return String.valueOf(this.acDocRef).concat(suffix);
    }

    @Transient
    public BigDecimal getTotalBspComInThisAcDoc() {
        BigDecimal bspCom = new BigDecimal("0.00");

        for (Ticket t : getTickets()) {
            bspCom = bspCom.add(t.getBspCom());
        }
        return bspCom;
    }

    @Transient
    public BigDecimal getTotalPurchaseFare() {
        BigDecimal subtotal = new BigDecimal("0.00");
        for (Ticket t : getTickets()) {
            subtotal = subtotal.add(t.getNetFare());
        }
        return subtotal;
    }

    @Transient
    public BigDecimal getTotalAdm() {// This method is instead of totalCredit()
        BigDecimal totalAdm = new BigDecimal("0.00");

        for (AccountingDocument creditNote : getRelatedDocuments()) {
            totalAdm = totalAdm.add(creditNote.getTotalDocumentedAmount());
        }
        return totalAdm;
    }

    public void addPAcDoc(PurchaseAccountingDocument pAcDoc) {
        this.purchaseAccountingDocuments.add(pAcDoc);
    }

    public void addLine(AccountingDocumentLine line) {
        this.accountingDocumentLines.add(line);
    }

    @Transient
    public int getNoOfPaxInCurrentAcDoc() {
        return getTickets().size();
    }

    @Transient//This method is no longer in use
    public Set<AccountingDocumentLine> getAdditionalServiceLine() {

        Set<AccountingDocumentLine> additionalServices = new LinkedHashSet();
        for (AccountingDocumentLine l : this.accountingDocumentLines) {
            if (l.getType() == 2) {                
                    additionalServices.add(l);                
            }
        }
        return additionalServices;
    }

    @Transient
    public Set<AccountingDocumentLine> getOtherServiceLine() {
        Set<AccountingDocumentLine> otherServices = new LinkedHashSet();
        for (AccountingDocumentLine l : this.accountingDocumentLines) {
            if (l.getType() == 2) {               
                    otherServices.add(l);               
            }
        }
        return otherServices;
    }

    @Transient
    public Set<AccountingDocumentLine> getTicketLine() {

        Set<AccountingDocumentLine> tktLine = new LinkedHashSet();
        for (AccountingDocumentLine l : this.accountingDocumentLines) {
            if (l.getType() == 1) {
                tktLine.add(l);
            }
        }
        return tktLine;
    }


    @Transient
    public BigDecimal getAdditionalServiceSubTotal() {
        BigDecimal subTotal = new BigDecimal("0.00");

        for (AccountingDocumentLine l : this.accountingDocumentLines) {  
            if(l.getType()==2){
           subTotal = subTotal.add(l.getAdditionalNetPayable());                           
            }
        }
        return subTotal;
    }

    @Transient
    public BigDecimal getTktdSubTotal() {
        BigDecimal subTotal = new BigDecimal("0.00");

        for (AccountingDocumentLine l : this.accountingDocumentLines) {
            if (l.getType() == 1) {
                subTotal = subTotal.add(l.getTktNetPayable());
            }
        }
        return subTotal;
    }

    @Transient
    public BigDecimal getOtherServiceSubTotal() {
        BigDecimal subTotal = new BigDecimal("0.00");

        for (AccountingDocumentLine l : this.accountingDocumentLines) {
                if(l.getType()==2){
                subTotal = subTotal.add(l.getOtherNetPayable());                            
                }
        }
        return subTotal;
    }
    
    @Transient
    public BigDecimal getUnspecifiedSubTotal() {
        BigDecimal subTotal = new BigDecimal("0.00");

        for (AccountingDocumentLine l : this.accountingDocumentLines) {
            if (l.getType() != 1 || l.getType() != 2 || l.getType() != 3) {
                subTotal = subTotal.add(l.getAmount());
            }
        }
        return subTotal;
    }

    @Transient
    public String getAcDocIssuedByName() {
        return this.acDocIssuedBy.getSurName() + " " + this.acDocIssuedBy.getForeName();
    }

    @Temporal(javax.persistence.TemporalType.DATE)
    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @Transient
    public String getLeadPaxFromTickets() {

        Ticket leadPaxTicket = null;

        List<Integer> paxNos = new ArrayList();
        if (!getTickets().isEmpty()) {
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
        }
        if (leadPaxTicket != null) {
            return leadPaxTicket.getFullPaxName();
        } else {
            return null;
        }
    }

    @Transient
    public BigDecimal getDueAmountWithRelatedDoc() {
        return getTotalDocumentedAmount().subtract(getTotalTransactionAmountWithRelatedDoc()).add(this.getTotalAdm());
    }

    public void applyTransToThisAcDoc(AcTransaction newTrans) {
        this.acTransactions.add(newTrans);
    }

    @Transient
    public String getAcDocFor() {
        if (this.pnr.getAgent() != null) {
            return this.pnr.getAgent().getName();
        } else {
            return this.pnr.getCustomer().getFullCustomerName();
        }
    }

    public void removeTicketLine(Ticket t) {
        for (AccountingDocumentLine l : this.getTicketLine()) {
            l.getTickets().remove(t);
        }
    }

    @Transient
    public BigDecimal getRevenueFromOService() {
        BigDecimal tProfit = new BigDecimal("0.00");

        for (AccountingDocumentLine l : this.accountingDocumentLines) {
            if (l.getType() == 2) {
                tProfit = tProfit.add(l.getRevenueFromOLine());
            }
        }
        return tProfit;
    }

    @Transient
    public BigDecimal getRevenueFormAService() {
        BigDecimal tProfit = new BigDecimal("0.00");
        BigDecimal tPurchaseOCost = new BigDecimal("0.00");
        
        for (AccountingDocumentLine l : this.accountingDocumentLines) {
            if (l.getType() == 2) {
                tProfit = tProfit.add(l.getRevenueFromALine());
            }
        }
        
        for (PurchaseAccountingDocument p : this.getPurchaseAccountingDocuments()) {
            tPurchaseOCost = p.getOtherCharge();
        }
        
        return tProfit.subtract(tPurchaseOCost);
    }
    
    @Transient
    public BigDecimal getTktdRevenue() {
        BigDecimal tRevenue = new BigDecimal("0.00");
        

        for (Ticket t : getTickets()) {
            tRevenue = tRevenue.add(t.getTktdRevenue());
        }        
        return tRevenue;
    }    
    
    @Transient
    public BigDecimal getRevenueAdm() {
        BigDecimal revAdm = new BigDecimal("0.00");

        for (AccountingDocument a : this.getRelatedDocuments()) {
            if (a.getAcDoctype() == 3 || a.getAcDoctype() == 4) {
                revAdm = revAdm.add(a.getTotalDocumentedAmount());
            }
        }
        return revAdm;
    } 
    
    @Transient
    public BigDecimal getPRevenueAdm() {
        BigDecimal revAdm = new BigDecimal("0.00");
        if (!this.purchaseAccountingDocuments.isEmpty()) {
            for (PurchaseAccountingDocument a : this.getPurchaseAccountingDocuments()) {
                revAdm = revAdm.add(a.getRevenueAdm());
            }
        }
        return revAdm;
    }
        
    @Transient
    public BigDecimal getExpectedRevFromAcDoc() {
        return getTktdRevenue().add(getRevenueFormAService()).add(getRevenueFromOService());
    }  
    
    public void updateAccounts() {
        for (Accounts a : this.accounts) {
            if (getPnr().getAgent() != null) {
                a.setContactable(this.getPnr().getAgent());
            } else {
                a.setContactable(this.getPnr().getCustomer());
            }
        }
    }

    @Transient
    public int getTotalPaxInAcDoc() {
        int noOfPax = 0;
        for (AccountingDocumentLine l : this.accountingDocumentLines) {
            if (l.getType() == 1) {
                noOfPax = l.getTickets().size();
            }
        }
        return noOfPax;
    }

    @Transient
    public String getInvoiceFooter() {
        return invoiceFooter;
    }

    public void setInvoiceFooter(String invoiceFooter) {
        this.invoiceFooter = invoiceFooter;
    }
}
