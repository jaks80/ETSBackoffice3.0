package etsbackoffice.domain;

import etsbackoffice.businesslogic.Enums;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.*;

/**
 *
 * @author Yusuf
 */
@Entity
@Table(name = "billtransaction")
public class BillingTransaction implements Serializable {

    private long transID;
    private Date transDate;
    private BigDecimal transAmount;
    private int transType;//1.Cash,2.Cheque,3.BankDeposit,4.OnlineTransfer,5.ACAdjustment
    private String transRef;
    private PNR pnr;
    private PurchaseAccountingDocument purchaseAccountingDocument;
    private PurchaseAccountingDocument invoice;
    private BatchBillingTransaction batchBillingTransaction;
    private User user;
    private Set<Accounts> accounts = new LinkedHashSet<Accounts>();
    
    private boolean active;
    private Integer version;
    public BillingTransaction() {
    }

    @Id
    @Column(name = "TRANSID")
    @GeneratedValue
    public long getTransID() {
        return transID;
    }

    public void setTransID(long transID) {
        this.transID = transID;
    }

    @Temporal(TemporalType.DATE)
    public Date getTransDate() {
        return transDate;
    }

    public void setTransDate(Date transDate) {
        this.transDate = transDate;
    }

    public BigDecimal getTransAmount() {
        return transAmount;
    }

    public void setTransAmount(BigDecimal transAmount) {
        this.transAmount = transAmount;
    }

    public int getTransType() {
        return transType;
    }

    public void setTransType(int transType) {
        this.transType = transType;
    }

    public String getTransRef() {
        return transRef;
    }

    public void setTransRef(String transRef) {
        this.transRef = transRef;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PNRID_FK")
    public PNR getPnr() {
        return pnr;
    }

    public void setPnr(PNR pnr) {
        this.pnr = pnr;
    }

    @ManyToOne
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PURACDOCID_FK")
    public PurchaseAccountingDocument getPurchaseAccountingDocument() {
        return purchaseAccountingDocument;
    }

    public void setPurchaseAccountingDocument(PurchaseAccountingDocument purchaseAccountingDocument) {
        this.purchaseAccountingDocument = purchaseAccountingDocument;
    }

    @ManyToOne
    public PurchaseAccountingDocument getInvoice() {
        return invoice;
    }

    public void setInvoice(PurchaseAccountingDocument invoice) {
        this.invoice = invoice;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BBID_FK")
    public BatchBillingTransaction getBatchBillingTransaction() {
        return batchBillingTransaction;
    }

    public void setBatchBillingTransaction(BatchBillingTransaction batchBillingTransaction) {
        this.batchBillingTransaction = batchBillingTransaction;
    }

    @OneToMany(mappedBy = "billingTransaction", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
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

    @Transient
    public String getTransTypeString() {

        return Enums.TransType.valueOf(this.getTransType());
    }
    
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Version
    @Column(name = "OPTLOCK")
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
