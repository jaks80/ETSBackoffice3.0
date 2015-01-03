package com.ets.fe.acdoc.model;

import com.ets.fe.PersistentObject;
import com.ets.fe.util.Enums.PaymentType;
import java.io.Serializable;
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
public class Payment extends PersistentObject implements Serializable {

    @XmlElement
    private String remark;
    @XmlElement
    private List<TicketingSalesAcDoc> tSalesPayments = new ArrayList<>();
    @XmlElement
    private List<OtherSalesAcDoc> oSalesPayments = new ArrayList<>();
    @XmlElement
    private List<TicketingPurchaseAcDoc> tPurchasePayments = new ArrayList<>();
    @XmlElement
    private PaymentType paymentType;

    public Payment() {

    }

    public void addTSalesPayment(TicketingSalesAcDoc doc) {
        this.tSalesPayments.add(doc);
    }

    public void addTPurchasePayment(TicketingPurchaseAcDoc doc) {
        this.tPurchasePayments.add(doc);
    }

    public void addOSalesPayment(OtherSalesAcDoc doc) {
        this.getoSalesPayments().add(doc);
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

    public List<TicketingSalesAcDoc> gettSalesPayments() {
        return tSalesPayments;
    }

    public void settSalesPayments(List<TicketingSalesAcDoc> tSalesPayments) {
        this.tSalesPayments = tSalesPayments;
    }

    public List<TicketingPurchaseAcDoc> gettPurchasePayments() {
        return tPurchasePayments;
    }

    public void settPurchasePayments(List<TicketingPurchaseAcDoc> tPurchasePayments) {
        this.tPurchasePayments = tPurchasePayments;
    }

    public List<OtherSalesAcDoc> getoSalesPayments() {
        return oSalesPayments;
    }

    public void setoSalesPayments(List<OtherSalesAcDoc> oSalesPayments) {
        this.oSalesPayments = oSalesPayments;
    }
}
