package com.ets.accountingdoc.domain;

import com.ets.pnr.domain.Pnr;
import com.ets.pnr.domain.Ticket;
import com.ets.util.Enums;
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
import javax.persistence.OrderBy;
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
@Table(name = "tkt_sales_acdoc")
public class TicketingSalesAcDoc extends AccountingDocument implements Serializable {

    @XmlElement
    private Pnr pnr;
    @XmlElement
    private Set<Ticket> tickets = new LinkedHashSet<>();

    @XmlElement
    private Set<AccountingDocumentLine> accountingDocumentLines = new LinkedHashSet<>();

    @XmlElement
    private Set<TicketingSalesAcDoc> relatedDocuments = new LinkedHashSet<>();
    @XmlElement
    private TicketingSalesAcDoc parent;

    @XmlElement
    private Payment payment;

    @Override
    public BigDecimal calculateDocumentedAmount() {
        return calculateTicketedSubTotal().add(calculateOtherServiceSubTotal().add(calculateAddChargesSubTotal()));
    }

    @Override
    public BigDecimal calculateTicketedSubTotal() {
        BigDecimal subtotal = new BigDecimal("0.00");
        for (Ticket t : getTickets()) {
            subtotal = subtotal.add(t.calculateNetSellingFare());
        }
        return subtotal;
    }

    @Override
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
        BigDecimal subtotal = new BigDecimal("0.00");
        for (AccountingDocumentLine l : accountingDocumentLines) {
            subtotal = subtotal.add(l.calculateAChargeLineTotal());
        }
        return subtotal;
    }
    
    @Override
    public BigDecimal calculateTotalDebitMemo() {
         BigDecimal total = new BigDecimal("0.00");
        for (AccountingDocument doc : this.relatedDocuments) {
            if (doc.getType().equals(Enums.AcDocType.DEBITMEMO)) {
                total = total.add(doc.getDocumentedAmount());
            }
        }
        return total;
    }

    @Override
    public BigDecimal calculateTotalCreditMemo() {
         BigDecimal total = new BigDecimal("0.00");
        for (AccountingDocument doc : this.relatedDocuments) {
            if (doc.getType().equals(Enums.AcDocType.CREDITMEMO)) {
                total = total.add(doc.getDocumentedAmount());
            }
        }
        return total;
    }

    @Override
    public BigDecimal calculateTotalPayment() {
        BigDecimal total = new BigDecimal("0.00");
        for (AccountingDocument doc : this.relatedDocuments) {
            if (doc.getType().equals(Enums.AcDocType.PAYMENT)) {
                total = total.add(doc.getDocumentedAmount());
            }
        }
        return total;
    }

    @Override
    public BigDecimal calculateTotalRefund() {
        BigDecimal total = new BigDecimal("0.00");
        for (AccountingDocument doc : this.relatedDocuments) {
            if (doc.getType().equals(Enums.AcDocType.REFUND)) {
                total = total.add(doc.getDocumentedAmount());
            }
        }
        return total;
    }
    
    @Override
    public BigDecimal calculateDueAmount() {       
        BigDecimal invoiceAmount = this.getDocumentedAmount();

        if (getType().equals(Enums.AcDocType.INVOICE)) {
            for (AccountingDocument doc : this.relatedDocuments) {
                invoiceAmount = invoiceAmount.add(doc.getDocumentedAmount());
            }
        }
        return invoiceAmount;
    }

    public void addLine(AccountingDocumentLine line) {
        this.accountingDocumentLines.add(line);
    }

    @OneToMany(mappedBy = "ticketingSalesAcDoc", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
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

    @OneToMany(mappedBy = "parent")
    @OrderBy(value = "id")
    public Set<TicketingSalesAcDoc> getRelatedDocuments() {
        return relatedDocuments;
    }

    public void setRelatedDocuments(Set<TicketingSalesAcDoc> relatedDocuments) {
        this.relatedDocuments = relatedDocuments;
    }

    @ManyToOne
    @JoinColumn(name = "parent_fk")
    public TicketingSalesAcDoc getParent() {
        return parent;
    }

    public void setParent(TicketingSalesAcDoc parent) {
        this.parent = parent;
    }

    @OneToMany(mappedBy = "ticketingSalesAcDoc", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public Set<AccountingDocumentLine> getAccountingDocumentLines() {
        return accountingDocumentLines;
    }

    public void setAccountingDocumentLines(Set<AccountingDocumentLine> accountingDocumentLines) {
        this.accountingDocumentLines = accountingDocumentLines;
    }

    public void addTicket(Ticket ticket) {
        this.tickets.add(ticket);
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
