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
@Table(name = "actransaction")
public class AcTransaction implements Serializable {

    private long transID;
    private Date transDate;
    private BigDecimal transAmount;
    private int transType;//1.Cash,2.Cheque,3.DebitCard,4.Credit,5.BankDeposit,6.OnlineTransfer,
    //7.ACAdjustment,8.Debit,9.Credit,10. Automated Debit,11.AutomatedCredit
    // ACAdjustment is for credit amount added to account, it is not a valid transaction
    //It just balance invoice
    private String transRef;
    private PNR pnr;
    private AccountingDocument accountingDocument;
    private AccountingDocument invoice;
    private OAccountingDocument oAccountingDocument;
    private OAccountingDocument oInvoice;
    private BatchTransaction batchTransaction;
    private Set<Accounts> accounts = new LinkedHashSet<Accounts>();    
    private User user;
    private boolean active;
    private Integer version;

    public AcTransaction() {
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

    @Basic
    @Column(name = "AMOUNT")
    public BigDecimal getTransAmount() {
        return transAmount;
    }

    public void setTransAmount(BigDecimal transAmount) {
        this.transAmount = transAmount;
    }

    @Basic
    @Column(name = "TYPE")
    public int getTransType() {
        return transType;
    }

    public void setTransType(int transType) {
        this.transType = transType;
    }

    @Basic
    @Column(name = "TREF")
    public String getTransRef() {
        return transRef;
    }

    public void setTransRef(String transRef) {
        this.transRef = transRef;
    }

    @Temporal(javax.persistence.TemporalType.DATE)
    public Date getTransDate() {
        return transDate;
    }

    public void setTransDate(Date transDate) {
        this.transDate = transDate;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACDOCID_FK")
    public AccountingDocument getAccountingDocument() {
        return accountingDocument;
    }

    public void setAccountingDocument(AccountingDocument accountingDocument) {
        this.accountingDocument = accountingDocument;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OACDOCID_FK")
    public OAccountingDocument getoAccountingDocument() {
        return oAccountingDocument;
    }

    public void setoAccountingDocument(OAccountingDocument oAccountingDocument) {
        this.oAccountingDocument = oAccountingDocument;
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

    @ManyToOne
    public AccountingDocument getInvoice() {
        return invoice;
    }

    public void setInvoice(AccountingDocument invoice) {
        this.invoice = invoice;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PCOLID_FK")
    public BatchTransaction getBatchTransaction() {
        return batchTransaction;
    }

    public void setBatchTransaction(BatchTransaction batchTransaction) {
        this.batchTransaction = batchTransaction;
    }

    @Version
    @Column(name = "OPTLOCK")
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @ManyToOne
    public OAccountingDocument getoInvoice() {
        return oInvoice;
    }

    public void setoInvoice(OAccountingDocument oInvoice) {
        this.oInvoice = oInvoice;
    }

    @OneToMany(mappedBy = "acTransaction", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
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
    public String getTransTypeString() {
        return Enums.TransType.valueOf(this.getTransType());
    }
}
