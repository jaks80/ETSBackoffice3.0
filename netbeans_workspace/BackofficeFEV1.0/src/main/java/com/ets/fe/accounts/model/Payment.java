package com.ets.fe.accounts.model;

import com.ets.fe.acdoc_o.model.OtherSalesAcDoc;
import com.ets.fe.PersistentObject;
import com.ets.fe.acdoc.model.TicketingPurchaseAcDoc;
import com.ets.fe.acdoc.model.TicketingSalesAcDoc;
import com.ets.fe.util.Enums;
import com.ets.fe.util.Enums.PaymentType;
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
public class Payment extends PersistentObject implements Serializable {

    @XmlElement
    private String remark;
    @XmlElement
    private List<TicketingSalesAcDoc> tSalesAcDocuments = new ArrayList<>();
    @XmlElement
    private List<OtherSalesAcDoc> oSalesAcDocuments = new ArrayList<>();
    @XmlElement
    private List<TicketingPurchaseAcDoc> tPurchaseAcDocuments = new ArrayList<>();
    @XmlElement
    private PaymentType paymentType;

    public Payment() {

    }

    public BigDecimal calculateTotalSalesPayment() {
        BigDecimal total = new BigDecimal("0.00");

        for (TicketingSalesAcDoc doc : tSalesAcDocuments) {
            if (!doc.getStatus().equals(Enums.AcDocStatus.VOID)) {
                total = total.add(doc.getDocumentedAmount());
            }
        }
        return total;
    }

    public BigDecimal calculateTotalPurchasePayment() {
        BigDecimal total = new BigDecimal("0.00");

        for (TicketingPurchaseAcDoc doc : tPurchaseAcDocuments) {
            if (!doc.getStatus().equals(Enums.AcDocStatus.VOID)) {
                total = total.add(doc.getDocumentedAmount());
            }
        }
        return total;
    }

    public BigDecimal calculateTotalOtherPayment() {
        BigDecimal total = new BigDecimal("0.00");

        for (OtherSalesAcDoc doc : oSalesAcDocuments) {
            if (!doc.getStatus().equals(Enums.AcDocStatus.VOID)) {
                total = total.add(doc.getDocumentedAmount());
            }
        }
        return total;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public List<TicketingSalesAcDoc> gettSalesAcDocuments() {
        return tSalesAcDocuments;
    }

    public void settSalesAcDocuments(List<TicketingSalesAcDoc> tSalesAcDocuments) {
        this.tSalesAcDocuments = tSalesAcDocuments;
    }

    public List<OtherSalesAcDoc> getoSalesAcDocuments() {
        return oSalesAcDocuments;
    }

    public void setoSalesAcDocuments(List<OtherSalesAcDoc> oSalesAcDocuments) {
        this.oSalesAcDocuments = oSalesAcDocuments;
    }

    public List<TicketingPurchaseAcDoc> gettPurchaseAcDocuments() {
        return tPurchaseAcDocuments;
    }

    public void settPurchaseAcDocuments(List<TicketingPurchaseAcDoc> tPurchaseAcDocuments) {
        this.tPurchaseAcDocuments = tPurchaseAcDocuments;
    }

    public void addTSalesDocument(TicketingSalesAcDoc ticketingSalesAcDoc) {
        this.tSalesAcDocuments.add(ticketingSalesAcDoc);
    }

    public void addTPurchaseDocument(TicketingPurchaseAcDoc ticketingPurchaseAcDoc) {
        this.tPurchaseAcDocuments.add(ticketingPurchaseAcDoc);
    }

    public void addOtherDocument(OtherSalesAcDoc otherSalesAcDoc) {
        this.oSalesAcDocuments.add(otherSalesAcDoc);
    }
}
