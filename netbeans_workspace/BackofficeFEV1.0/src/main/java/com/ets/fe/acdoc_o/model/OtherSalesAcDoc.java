package com.ets.fe.acdoc_o.model;

import com.ets.fe.acdoc.model.AccountingDocument;
import com.ets.fe.accounts.model.Payment;
import com.ets.fe.client.model.Agent;
import com.ets.fe.client.model.Customer;
import com.ets.fe.pnr.model.Pnr;
import com.ets.fe.util.Enums;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
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
public class OtherSalesAcDoc extends AccountingDocument implements Serializable {

    @XmlElement
    private Agent agent;
    @XmlElement
    private Customer customer;
    @XmlElement
    private List<AccountingDocumentLine> accountingDocumentLines = new ArrayList<>();
    @XmlElement
    private List<AdditionalChargeLine> additionalChargeLines = new ArrayList<>();
    @XmlElement
    private List<OtherSalesAcDoc> relatedDocuments = new ArrayList<>();
    @XmlElement
    private OtherSalesAcDoc parent;

    @XmlElement
    private Payment payment;

    @Override
    public BigDecimal calculateDocumentedAmount() {
        return calculateOtherServiceSubTotal().add(calculateAddChargesSubTotal());
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
    public BigDecimal calculateDueAmount() {
        BigDecimal dueAmount = new BigDecimal("0.00");
        BigDecimal invoiceAmount = getDocumentedAmount();

        if (invoiceAmount == null) {
            invoiceAmount = calculateDocumentedAmount();
        }

        if (getType()!=null && getType().equals(Enums.AcDocType.INVOICE)) {
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

    public String calculateClientName() {
        String name = "";
        if (this.getAgent() != null) {
            name = this.getAgent().getName();
        } else {
            name = this.getCustomer().calculateFullName();
        }
        return name;
    }
        
    public List<OtherSalesAcDoc> getRelatedDocuments() {
        return relatedDocuments;
    }

    public void setRelatedDocuments(List<OtherSalesAcDoc> relatedDocuments) {
        this.relatedDocuments = relatedDocuments;
    }

    public OtherSalesAcDoc getParent() {
        return parent;
    }

    public void setParent(OtherSalesAcDoc parent) {
        this.parent = parent;
    }

    public List<AccountingDocumentLine> getAccountingDocumentLines() {
        return accountingDocumentLines;
    }

    public void setAccountingDocumentLines(List<AccountingDocumentLine> accountingDocumentLines) {
        this.accountingDocumentLines = accountingDocumentLines;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<AdditionalChargeLine> getAdditionalChargeLines() {
        return additionalChargeLines;
    }

    public void setAdditionalChargeLines(List<AdditionalChargeLine> additionalChargeLines) {
        this.additionalChargeLines = additionalChargeLines;
    }
    
    @Override
    public BigDecimal calculateTicketedSubTotal() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Pnr getPnr() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setParent(AccountingDocument doc) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
