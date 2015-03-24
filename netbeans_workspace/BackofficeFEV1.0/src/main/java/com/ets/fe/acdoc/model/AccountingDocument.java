package com.ets.fe.acdoc.model;

import com.ets.fe.PersistentObject;
import com.ets.fe.pnr.model.Pnr;
import com.ets.fe.util.Enums.AcDocStatus;
import com.ets.fe.util.Enums.AcDocType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
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
    private AcDocType type;
    @XmlElement
    private String terms;
    @XmlElement
    private Long reference;
    @XmlElement
    private Integer version;
    @XmlElement
    private AcDocStatus status;
    @XmlElement
    private BigDecimal documentedAmount;
    @XmlElement
    private Date docIssueDate;
    @XmlElement
    private String remark;

    public abstract BigDecimal calculateTicketedSubTotal();    

    public abstract BigDecimal calculateAddChargesSubTotal();
    
    public abstract BigDecimal calculateRelatedDocBalance();
    
    public abstract BigDecimal calculateDocumentedAmount();

    public abstract BigDecimal calculateTotalPayment();
    
    public abstract BigDecimal calculateTotalRefund();
    
    public abstract BigDecimal calculateTotalDebitMemo();
    
    public abstract BigDecimal calculateTotalCreditMemo(); 

    public abstract BigDecimal calculateDueAmount();
    
    public abstract Pnr getPnr();
    
    public abstract void setParent(AccountingDocument doc);
    
    public AcDocType getType() {
        return type;
    }

    public void setType(AcDocType type) {
        this.type = type;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public Long getReference() {
        return reference;
    }

    public void setReference(Long reference) {
        this.reference = reference;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public BigDecimal getDocumentedAmount() {
        return documentedAmount;
    }

    public void setDocumentedAmount(BigDecimal documentedAmount) {
        this.documentedAmount = documentedAmount;
    }

    public Date getDocIssueDate() {
        return docIssueDate;
    }

    public void setDocIssueDate(Date docIssueDate) {
        this.docIssueDate = docIssueDate;
    }

    public AcDocStatus getStatus() {
        return status;
    }

    public void setStatus(AcDocStatus status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
