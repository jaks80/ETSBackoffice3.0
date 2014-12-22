package com.ets.accountingdoc.domain;

import com.ets.PersistentObject;
import com.ets.util.Enums.AcDocType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.Version;
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
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AccountingDocument extends PersistentObject implements Serializable {

    @XmlElement
    private Date docIssueDate;
    @XmlElement
    private AcDocType acDoctype;
    @XmlElement
    private String terms;
    @XmlElement
    private Long acDocRef;
    @XmlElement
    private Integer version;
    @XmlElement    
    private int isArchived=0;//0-No 1-Yes
    @XmlElement
    private BigDecimal documentedAmount;

    @XmlElement
    private Set<AccountingDocumentLine> accountingDocumentLines = new LinkedHashSet<>();
    @XmlElement
    private Set<AccountingDocument> relatedDocuments = new LinkedHashSet<>();
    @XmlElement
    private AccountingDocument accountingDocument;
    
    public abstract BigDecimal calculateDocumentedAmount();
    
    public BigDecimal calculateOtherServiceSubTotal() {
        BigDecimal subtotal = new BigDecimal("0.00");
        for (AccountingDocumentLine l : accountingDocumentLines) {
            if (l.getOtherService() != null) {
                subtotal = subtotal.add(l.calculateOsNetSellingTotal());
            }
        }
        return subtotal;
    }

    public BigDecimal calculateAddChargesSubTotal() {
        BigDecimal subtotal = new BigDecimal("0.00");
        for (AccountingDocumentLine l : accountingDocumentLines) {
            if (l.getAdditionalCharge() != null) {
                subtotal = subtotal.add(l.calculateAcNetSellingTotal());
            }
        }
        return subtotal;
    }

    public void addLine(AccountingDocumentLine line) {
        this.accountingDocumentLines.add(line);
    }

    @Column(nullable = false)
    public AcDocType getAcDoctype() {
        return acDoctype;
    }

    public void setAcDoctype(AcDocType acDoctype) {
        this.acDoctype = acDoctype;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    @Column(nullable = false)
    public Long getAcDocRef() {
        return acDocRef;
    }

    public void setAcDocRef(Long acDocRef) {
        this.acDocRef = acDocRef;
    }

    @Version
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Column(nullable = false)
    public BigDecimal getDocumentedAmount() {
        return documentedAmount;
    }

    public void setDocumentedAmount(BigDecimal documentedAmount) {
        this.documentedAmount = documentedAmount;
    }

    @OneToMany(mappedBy = "accountingDocument", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public Set<AccountingDocumentLine> getAccountingDocumentLines() {
        return accountingDocumentLines;
    }

    public void setAccountingDocumentLines(Set<AccountingDocumentLine> accountingDocumentLines) {
        this.accountingDocumentLines = accountingDocumentLines;
    }

    @Column(nullable = false)
    public int getIsArchived() {
        return isArchived;
    }

    public void setIsArchived(int isArchived) {
        this.isArchived = isArchived;
    }

    @Temporal(javax.persistence.TemporalType.DATE)
    public Date getDocIssueDate() {
        return docIssueDate;
    }

    public void setDocIssueDate(Date docIssueDate) {
        this.docIssueDate = docIssueDate;
    }

    @OneToMany(mappedBy = "accountingDocument")
    public Set<AccountingDocument> getRelatedDocuments() {
        return relatedDocuments;
    }

    public void setRelatedDocuments(Set<AccountingDocument> relatedDocuments) {
        this.relatedDocuments = relatedDocuments;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_fk")
    public AccountingDocument getAccountingDocument() {
        return accountingDocument;
    }

    public void setAccountingDocument(AccountingDocument accountingDocument) {
        this.accountingDocument = accountingDocument;
    }
}
