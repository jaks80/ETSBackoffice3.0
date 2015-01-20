package com.ets.accountingdoc.domain;

import com.ets.pnr.domain.Pnr;
import com.ets.pnr.domain.Ticket;
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
@Table(name = "tkt_purch_acdoc")
//@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public class TicketingPurchaseAcDoc extends AccountingDocument implements Serializable {

    @XmlElement
    private String vendorRef;
    @XmlElement
    private Set<Ticket> tickets = new LinkedHashSet<>();
    @XmlElement
    private Pnr pnr;
    @XmlElement
    private Set<AccountingDocumentLine> accountingDocumentLines = new LinkedHashSet<>();
    @XmlElement
    private Set<TicketingPurchaseAcDoc> relatedDocuments = new LinkedHashSet<>();
    @XmlElement
    private TicketingPurchaseAcDoc parent;

    @Override
    public BigDecimal calculateTicketedSubTotal() {
        BigDecimal subtotal = new BigDecimal("0.00");
        for (Ticket t : getTickets()) {
            subtotal = subtotal.add(t.getNetPurchaseFare());
        }
        return subtotal;
    }

        @Override
    public BigDecimal calculateOtherServiceSubTotal() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

    public String getVendorRef() {
        return vendorRef;
    }

    public void setVendorRef(String vendorRef) {
        this.vendorRef = vendorRef;
    }

    @OneToMany(mappedBy = "ticketingPurchaseAcDoc")
    public Set<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(Set<Ticket> tickets) {
        this.tickets = tickets;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pnr_fk")
    public Pnr getPnr() {
        return pnr;
    }

    public void setPnr(Pnr pnr) {
        this.pnr = pnr;
    }

    @Override
    public BigDecimal calculateDocumentedAmount() {
        return new BigDecimal("0.00");
    }

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public Set<TicketingPurchaseAcDoc> getRelatedDocuments() {
        return relatedDocuments;
    }

    public void setRelatedDocuments(Set<TicketingPurchaseAcDoc> relatedDocuments) {
        this.relatedDocuments = relatedDocuments;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_fk")
    public TicketingPurchaseAcDoc getParent() {
        return parent;
    }

    public void setParent(TicketingPurchaseAcDoc parent) {
        this.parent = parent;
    }

    @OneToMany(mappedBy = "ticketingPurchaseAcDoc", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
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
}
