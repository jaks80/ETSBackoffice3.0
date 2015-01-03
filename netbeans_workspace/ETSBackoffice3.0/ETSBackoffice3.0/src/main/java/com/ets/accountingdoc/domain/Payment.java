package com.ets.accountingdoc.domain;

import com.ets.PersistentObject;
import com.ets.util.Enums.PaymentType;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
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
public class Payment extends PersistentObject implements Serializable {

    @XmlElement
    private String remark;
    @XmlElement
    private Set<TicketingSalesAcDoc> tSalesPayments = new LinkedHashSet<>();
    @XmlElement
    private Set<OtherSalesAcDoc> oSalesPayments = new LinkedHashSet<>();
    @XmlElement
    private Set<TicketingPurchaseAcDoc> tPurchasePayments = new LinkedHashSet<>();
    @XmlElement
    private PaymentType paymentType;

    public Payment() {

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

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "payment_tsacdoc",
            joinColumns = @JoinColumn(name = "tsacdoc_id"),
            inverseJoinColumns = @JoinColumn(name = "payment_id")
    )
    public Set<TicketingSalesAcDoc> gettSalesPayments() {
        return tSalesPayments;
    }

    public void settSalesPayments(Set<TicketingSalesAcDoc> tSalesPayments) {
        this.tSalesPayments = tSalesPayments;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "payment_oacdoc",
            joinColumns = @JoinColumn(name = "oacdoc_id"),
            inverseJoinColumns = @JoinColumn(name = "payment_id")
    )
    public Set<OtherSalesAcDoc> getoSalesPayments() {
        return oSalesPayments;
    }

    public void setoSalesPayments(Set<OtherSalesAcDoc> oSalesPayments) {
        this.oSalesPayments = oSalesPayments;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "payment_tpacdoc",
            joinColumns = @JoinColumn(name = "tpacdoc_id"),
            inverseJoinColumns = @JoinColumn(name = "payment_id")
    )
    public Set<TicketingPurchaseAcDoc> gettPurchasePayments() {
        return tPurchasePayments;
    }

    public void settPurchasePayments(Set<TicketingPurchaseAcDoc> tPurchasePayments) {
        this.tPurchasePayments = tPurchasePayments;
    }
}
