package com.ets.fe.acdoc.model;

import com.ets.fe.PersistentObject;
import com.ets.fe.pnr.model.Pnr;
import com.ets.fe.pnr.model.Ticket;
import com.ets.fe.util.Enums.AcDocType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private Date docIssueDate;
    
    @XmlElement
    private List<AccountingDocumentLine> accountingDocumentLines = new ArrayList<>();

    public abstract List<Ticket> getTickets();
    public abstract void setTickets(List<Ticket> tickets);
    public abstract Pnr getPnr();
    public abstract void setPnr(Pnr pnr);
    
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
   
    public Long getAcDocRef() {
        return acDocRef;
    }

    public void setAcDocRef(Long acDocRef) {
        this.acDocRef = acDocRef;
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

    public List<AccountingDocumentLine> getAccountingDocumentLines() {
        return accountingDocumentLines;
    }

    public void setAccountingDocumentLines(List<AccountingDocumentLine> accountingDocumentLines) {
        this.accountingDocumentLines = accountingDocumentLines;
    }

    public int getIsArchived() {
        return isArchived;
    }

    public void setIsArchived(int isArchived) {
        this.isArchived = isArchived;
    }

    public Date getDocIssueDate() {
        return docIssueDate;
    }

    public void setDocIssueDate(Date docIssueDate) {
        this.docIssueDate = docIssueDate;
    }
}
