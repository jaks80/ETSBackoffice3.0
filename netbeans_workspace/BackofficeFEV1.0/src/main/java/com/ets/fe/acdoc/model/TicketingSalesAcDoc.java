package com.ets.fe.acdoc.model;

import com.ets.fe.acdoc_o.model.AdditionalChargeLine;
import com.ets.fe.accounts.model.Payment;
import com.ets.fe.pnr.model.Pnr;
import com.ets.fe.pnr.model.Ticket;
import com.ets.fe.util.Enums;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.*;

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
    private List<AdditionalChargeLine> additionalChargeLines = new ArrayList<>();
    @XmlElement
    private List<TicketingSalesAcDoc> relatedDocuments = new ArrayList<>();
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

    @Override
    public BigDecimal calculateDocumentedAmount() {
        return calculateTicketedSubTotal().add(calculateAddChargesSubTotal());
    }

    public String calculateClientName() {
        String name = "";
        if (this.pnr.getAgent() != null) {
            name = this.pnr.getAgent().getName();
        } else {
            name = this.pnr.getCustomer().calculateFullName();
        }
        return name;
    }

    public List<TicketingSalesAcDoc> calculateActiveRelatedDocuments() {
        List<TicketingSalesAcDoc> activeDocs = new ArrayList<>();
        for (TicketingSalesAcDoc d : this.relatedDocuments) {
            if (d.getStatus().equals(Enums.AcDocStatus.ACTIVE)) {
                activeDocs.add(d);
            }
        }
        return activeDocs;
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

    public List<TicketingSalesAcDoc> getRelatedDocuments() {
        return relatedDocuments;
    }

    public void setRelatedDocuments(List<TicketingSalesAcDoc> relatedDocuments) {
        this.relatedDocuments = relatedDocuments;
    }

    public TicketingSalesAcDoc getParent() {
        return parent;
    }

    @Override
    public void setParent(AccountingDocument parent) {
        this.parent = (TicketingSalesAcDoc) parent;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public void addLine(AdditionalChargeLine line) {
        this.getAdditionalChargeLines().add(line);
    }

    public List<AdditionalChargeLine> getAdditionalChargeLines() {
        return additionalChargeLines;
    }

    public void setAdditionalChargeLines(List<AdditionalChargeLine> additionalChargeLines) {
        this.additionalChargeLines = additionalChargeLines;
    }
}
