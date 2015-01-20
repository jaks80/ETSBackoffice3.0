package com.ets.fe.acdoc.model;

import com.ets.fe.pnr.model.Pnr;
import com.ets.fe.pnr.model.Ticket;
import com.ets.fe.util.Enums;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
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
public class TicketingSalesAcDoc extends AccountingDocument implements Serializable {

    @XmlElement
    private Pnr pnr;
    @XmlElement
    private List<Ticket> tickets = new ArrayList<>();
    @XmlElement
    private Set<AccountingDocumentLine> accountingDocumentLines = new LinkedHashSet<>();
    @XmlElement
    private Set<TicketingSalesAcDoc> relatedDocuments = new LinkedHashSet<>();
    @XmlElement
    private TicketingSalesAcDoc parent;
    @XmlElement
    private Payment payment;
    
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
                subtotal = subtotal.add(l.calculateOsNetSellingTotal());
            }
        }
        return subtotal;
    }

    @Override
    public BigDecimal calculateAddChargesSubTotal() {
        BigDecimal subtotal = new BigDecimal("0.00");
        for (AccountingDocumentLine l : accountingDocumentLines) {
            if (l.getAdditionalCharge() != null) {
                subtotal = subtotal.add(l.calculateAcNetSellingTotal());
            }
        }
        return subtotal;
    }

    @Override
    public BigDecimal calculateTotalPayment() {
        BigDecimal totalPayment = new BigDecimal("0.00");
        for (AccountingDocument doc : this.relatedDocuments) {
            if (doc.getType().equals(Enums.AcDocType.PAYMENT)) {
                totalPayment = totalPayment.add(doc.getDocumentedAmount());//Can not use calculate
            }
        }
        return totalPayment;
    }

    public BigDecimal calculateRelatedDocBalance() {
        BigDecimal relAmount = new BigDecimal("0.00");
        for (TicketingSalesAcDoc doc : this.relatedDocuments) {
            if (!doc.getType().equals(Enums.AcDocType.PAYMENT)) {
                if (this.getId() != null) {
                    relAmount = relAmount.add(doc.getDocumentedAmount());
                } else {
                }

            }
        }
        return relAmount;
    }

    @Override
    public BigDecimal calculateDueAmount() {
        BigDecimal dueAmount = new BigDecimal("0.00");
        BigDecimal invoiceAmount = getDocumentedAmount();

        if (invoiceAmount == null) {
            invoiceAmount = calculateDocumentedAmount();
        }

        if (getType().equals(Enums.AcDocType.INVOICE)) {
            for (AccountingDocument doc : this.relatedDocuments) {

                if (doc.getDocumentedAmount() == null) {
                    doc.setDocumentedAmount(doc.calculateDocumentedAmount());
                }
                System.out.println("Doc amout: "+doc.getDocumentedAmount());
                dueAmount = dueAmount.add(doc.getDocumentedAmount());
            }
        }
        return invoiceAmount.add(dueAmount);
    }

    @Override
    public BigDecimal calculateDocumentedAmount() {
        return calculateTicketedSubTotal().add(calculateOtherServiceSubTotal().add(calculateAddChargesSubTotal()));
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    public Pnr getPnr() {
        return pnr;
    }

    public void setPnr(Pnr pnr) {
        this.pnr = pnr;
    }

    public Set<AccountingDocumentLine> getAccountingDocumentLines() {
        return accountingDocumentLines;
    }

    public void setAccountingDocumentLines(Set<AccountingDocumentLine> accountingDocumentLines) {
        this.accountingDocumentLines = accountingDocumentLines;
    }

    public Set<TicketingSalesAcDoc> getRelatedDocuments() {
        return relatedDocuments;
    }

    public void setRelatedDocuments(Set<TicketingSalesAcDoc> relatedDocuments) {
        this.relatedDocuments = relatedDocuments;
    }

    public TicketingSalesAcDoc getParent() {
        return parent;
    }

    public void setParent(TicketingSalesAcDoc parent) {
        this.parent = parent;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }
    
    public void addLine(AccountingDocumentLine line){
     this.accountingDocumentLines.add(line);
    }
}
