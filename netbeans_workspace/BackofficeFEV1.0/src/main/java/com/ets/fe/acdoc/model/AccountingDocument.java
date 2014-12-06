package com.ets.fe.acdoc.model;

import com.ets.fe.PersistentObject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

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
public abstract class AccountingDocument extends PersistentObject implements Serializable {

    @XmlElement
    private int acDoctype;//1: Invoice, 2:TktRefundCreditNote,3: CreditNote ,4: Debit note
    @XmlElement
    private String terms;
    @XmlElement
    private Integer acDocRef;
    @XmlElement
    private Integer version;
    @XmlElement
    private int isArchived;
    @XmlElement
    private BigDecimal documentedAmount;

    @XmlElement      
    private Set<AccountingDocumentLine> accountingDocumentLines = new LinkedHashSet<>();    

    public int getAcDoctype() {
        return acDoctype;
    }

    public void setAcDoctype(int acDoctype) {
        this.acDoctype = acDoctype;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public Integer getAcDocRef() {
        return acDocRef;
    }

    public void setAcDocRef(Integer acDocRef) {
        this.acDocRef = acDocRef;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public int isIsArchived() {
        return isArchived;
    }

    public void setIsArchived(int isArchived) {
        this.isArchived = isArchived;
    }

    public BigDecimal getDocumentedAmount() {
        return documentedAmount;
    }

    public void setDocumentedAmount(BigDecimal documentedAmount) {
        this.documentedAmount = documentedAmount;
    }

    public Set<AccountingDocumentLine> getAccountingDocumentLines() {
        return accountingDocumentLines;
    }

    public void setAccountingDocumentLines(Set<AccountingDocumentLine> accountingDocumentLines) {
        this.accountingDocumentLines = accountingDocumentLines;
    }
}
