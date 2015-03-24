package com.ets.fe.acdoc.model;

import com.ets.fe.acdoc_o.model.AdditionalChargeLine;
import com.ets.fe.accounts.model.Payment;
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
public class TicketingPurchaseAcDoc extends AccountingDocument implements Serializable {

    @XmlElement
    private String vendorRef;
    @XmlElement
    private Pnr pnr;
    @XmlElement
    private List<Ticket> tickets = new ArrayList<>();
    @XmlElement
    private List<AdditionalChargeLine> additionalChargeLines = new ArrayList<>();
    @XmlElement
    private Set<TicketingPurchaseAcDoc> relatedDocuments = new LinkedHashSet<>();
    @XmlElement
    private TicketingPurchaseAcDoc parent;
    @XmlElement
    private Payment payment;

    @Override
    public BigDecimal calculateTicketedSubTotal() {
        BigDecimal subtotal = new BigDecimal("0.00");
        for (Ticket t : getTickets()) {
            subtotal = subtotal.add(t.calculateNetPurchaseFare());
        }
        return subtotal;
    }

    @Override
    public BigDecimal calculateDocumentedAmount() {
        return calculateTicketedSubTotal().add(calculateAddChargesSubTotal());
    }

    @Override
    public BigDecimal calculateAddChargesSubTotal() {
        BigDecimal subtotal = new BigDecimal("0.00");
        for (AdditionalChargeLine l : this.getAdditionalChargeLines()) {
            subtotal = subtotal.add(l.getAmount());
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

    @Override
    public BigDecimal calculateRelatedDocBalance() {
        BigDecimal relAmount = new BigDecimal("0.00");
        for (AccountingDocument doc : this.relatedDocuments) {
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
                dueAmount = dueAmount.add(doc.getDocumentedAmount());
            }
        }
        return invoiceAmount.add(dueAmount);
    }

    public String getVendorRef() {
        return vendorRef;
    }

    public void setVendorRef(String vendorRef) {
        this.vendorRef = vendorRef;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    @Override
    public Pnr getPnr() {
        return pnr;
    }

    public void setPnr(Pnr pnr) {
        this.pnr = pnr;
    }

    public List<AdditionalChargeLine> getAdditionalChargeLines() {
        return additionalChargeLines;
    }

    public void setAdditionalChargeLines(List<AdditionalChargeLine> additionalChargeLines) {
        this.additionalChargeLines = additionalChargeLines;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Set<TicketingPurchaseAcDoc> getRelatedDocuments() {
        return relatedDocuments;
    }

    public void setRelatedDocuments(Set<TicketingPurchaseAcDoc> relatedDocuments) {
        this.relatedDocuments = relatedDocuments;
    }

    public TicketingPurchaseAcDoc getParent() {
        return parent;
    }

    @Override
    public void setParent(AccountingDocument parent) {
        this.parent = (TicketingPurchaseAcDoc) parent;
    }

    public String getDocTypeString() {
        if (this.getType().equals(Enums.AcDocType.DEBITMEMO)) {
            return "ADM";
        } else if (this.getType().equals(Enums.AcDocType.CREDITMEMO)) {
            return "ACM";
        }
        return "";
    }
}
