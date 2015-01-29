package com.ets.accountingdoc.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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
@Table(name = "oth_sales_acdoc")
public class OtherSalesAcDoc extends AccountingDocument implements Serializable {

    @XmlElement
    private Set<AccountingDocumentLine> accountingDocumentLines = new LinkedHashSet<>();
    @XmlElement
    private Set<OtherSalesAcDoc> relatedDocuments = new LinkedHashSet<>();
    @XmlElement
    private OtherSalesAcDoc parent;

    @XmlElement
    private Payment payment;

    @Override
    public BigDecimal calculateDocumentedAmount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public BigDecimal calculateTicketedSubTotal() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public BigDecimal calculateOtherServiceSubTotal() {
        BigDecimal subtotal = new BigDecimal("0.00");
        for (AccountingDocumentLine l : accountingDocumentLines) {
            if (l.getOtherService() != null) {
                subtotal = subtotal.add(l.calculateOServiceLineTotal());
            }
        }
        return subtotal;
    }

    @Override
    public BigDecimal calculateAddChargesSubTotal() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public BigDecimal calculateTotalPayment() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public BigDecimal calculateDueAmount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public Set<OtherSalesAcDoc> getRelatedDocuments() {
        return relatedDocuments;
    }

    public void setRelatedDocuments(Set<OtherSalesAcDoc> relatedDocuments) {
        this.relatedDocuments = relatedDocuments;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_fk")
    public OtherSalesAcDoc getParent() {
        return parent;
    }

    public void setParent(OtherSalesAcDoc parent) {
        this.parent = parent;
    }

    @OneToMany(mappedBy = "otherSalesAcDoc", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public Set<AccountingDocumentLine> getAccountingDocumentLines() {
        return accountingDocumentLines;
    }

    public void setAccountingDocumentLines(Set<AccountingDocumentLine> accountingDocumentLines) {
        this.accountingDocumentLines = accountingDocumentLines;
    }

    @Override
    public BigDecimal calculateTotalDebitMemo() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public BigDecimal calculateTotalCreditMemo() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public BigDecimal calculateTotalRefund() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_fk")
    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }
}
