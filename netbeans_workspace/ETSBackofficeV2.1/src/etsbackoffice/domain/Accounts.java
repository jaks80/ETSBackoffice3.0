/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etsbackoffice.domain;

import etsbackoffice.businesslogic.Enums;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

/**
 *
 * @author Yusuf
 */
@Entity
@Table(name = "accounts")
public class Accounts implements Serializable {

    private long acLineId;
    private Date transDate;
    private AccountingDocument accountingDocument;
    private OAccountingDocument oAccountingDocument;
    private PurchaseAccountingDocument purchaseAccountingDocument;
    private AcTransaction acTransaction;
    private BillingTransaction billingTransaction;
    private int stmtLineType;//1.Purchase, 2.Sales
    private Contactable contactable;
    
    //For reporting
    private BigDecimal balance = new BigDecimal("0.00");
    private String lineDdesc;
    private BigDecimal cTransAmount = null;
    public Accounts() {
    }

    @Id
    @Column(name = "aclineid")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "aclinetid")
    @TableGenerator(name = "aclinetid", table = "aclinetidpktb",
    pkColumnName = "aclinetidkey", pkColumnValue = "aclinetidvalue", allocationSize = 1)
    public long getAcLineId() {
        return acLineId;
    }

    public void setAcLineId(long acLineId) {
        this.acLineId = acLineId;
    }

    @Temporal(javax.persistence.TemporalType.DATE)
    public Date getTransDate() {
        return transDate;
    }

    public void setTransDate(Date transDate) {
        this.transDate = transDate;
    }

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "ACDOC_ID", unique = true, nullable = true)
    public AccountingDocument getAccountingDocument() {
        return accountingDocument;
    }

    public void setAccountingDocument(AccountingDocument accountingDocument) {
        this.accountingDocument = accountingDocument;
    }

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "OACDOC_ID", unique = true, nullable = true)
    public OAccountingDocument getoAccountingDocument() {
        return oAccountingDocument;
    }

    public void setoAccountingDocument(OAccountingDocument oAccountingDocument) {
        this.oAccountingDocument = oAccountingDocument;
    }

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "PACDOC_ID", unique = true, nullable = true)
    public PurchaseAccountingDocument getPurchaseAccountingDocument() {
        return purchaseAccountingDocument;
    }

    public void setPurchaseAccountingDocument(PurchaseAccountingDocument purchaseAccountingDocument) {
        this.purchaseAccountingDocument = purchaseAccountingDocument;
    }

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "TRANS_ID", unique = true, nullable = true)
    public AcTransaction getAcTransaction() {
        return acTransaction;
    }

    public void setAcTransaction(AcTransaction acTransaction) {
        this.acTransaction = acTransaction;
    }

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "BTRANS_ID", unique = true, nullable = true)
    public BillingTransaction getBillingTransaction() {
        return billingTransaction;
    }

    public void setBillingTransaction(BillingTransaction billingTransaction) {
        this.billingTransaction = billingTransaction;
    }

    public int getStmtLineType() {
        return stmtLineType;
    }

    public void setStmtLineType(int stmtLineType) {
        this.stmtLineType = stmtLineType;
    }

    @ManyToOne
    public Contactable getContactable() {
        return contactable;
    }

    public void setContactable(Contactable contactable) {
        this.contactable = contactable;
    }

    @Transient
    public BigDecimal getcInvAmount() {
        BigDecimal cInvAmount = null;

        if (getAccountingDocument() != null) {
            cInvAmount = getAccountingDocument().getTotalDocumentedAmount();
        } else if (getoAccountingDocument() != null) {
            cInvAmount = getoAccountingDocument().getTotalDocumentedAmount();
        }
        return cInvAmount;
    }

    @Transient
    public BigDecimal getcTransAmount() {       

        if (getAcTransaction() != null) {
            cTransAmount = getAcTransaction().getTransAmount();
        }
        return cTransAmount;
    }

    @Transient
    public String getTransType() {
        String transType = "";
        if (getAccountingDocument() != null) {
            if (getAccountingDocument().getAcDoctype() == 1) {
                transType = "Invoice";
            } else {
                if (getAccountingDocument().getAcDoctype() == 2 || getAccountingDocument().getAcDoctype() == 3) {
                    transType = "Credit Note";
                }
            }

        } else if (getAcTransaction() != null) {
            if (getAcTransaction().getTransAmount().signum() == -1) {
                transType = "Piad";
            } else {
                if (getAcTransaction().getTransAmount().signum() == 1) {
                    transType = "Received";
                }
            }
        } else if (getoAccountingDocument() != null) {
            if (getoAccountingDocument().getAcDoctype() == 1) {
                transType = "Invoice";
            } else {
                if (getoAccountingDocument().getAcDoctype() == 2) {
                    transType = "Credit Note";
                }
            }
        }

        return transType;
    }

    @Transient
    public String getLineDdesc() {
        
        if (getAccountingDocument() != null) {

            setLineDdesc("Ticketing Invoice, Ref: " + getAccountingDocument().getAcDocRefString()
                     + " Pnr: " + getAccountingDocument().getPnr().getGdsPNR());
        } else if (getAcTransaction() != null) {
            if (getAcTransaction().getAccountingDocument() != null) {
                setLineDdesc(getAcTransaction().getAccountingDocument().getAcDocRefString() + ", " + getAcTransaction().getTransRef()
                         + Enums.TransType.valueOf(getAcTransaction().getTransType()) + " "
                         + " Pnr: " + getAcTransaction().getAccountingDocument().getPnr().getGdsPNR());
            } else if (getAcTransaction().getoAccountingDocument() != null) {
                setLineDdesc(getAcTransaction().getoAccountingDocument().getAcDocRefString() + ", "
                         + Enums.TransType.valueOf(getAcTransaction().getTransType()) + " " + getAcTransaction().getTransRef());
            }
        } else if (getoAccountingDocument() != null) {
            setLineDdesc("Other Invoice, Ref: " + getoAccountingDocument().getAcDocRefString());
        }
        return lineDdesc;
    }

    public void setLineDdesc(String lineDdesc) {
        this.lineDdesc = lineDdesc;
    }
        
    @Transient
    public BigDecimal getBalance() {
        return this.balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setcTransAmount(BigDecimal cTransAmount) {
        this.cTransAmount = cTransAmount;
    }
}
