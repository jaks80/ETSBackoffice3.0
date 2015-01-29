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
    private Set<TicketingSalesAcDoc> tSalesAcDocuments = new LinkedHashSet<>();
    @XmlElement
    private Set<OtherSalesAcDoc> oSalesAcDocuments = new LinkedHashSet<>();
    @XmlElement
    private Set<TicketingPurchaseAcDoc> tPurchaseAcDocuments = new LinkedHashSet<>();
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

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)   
    public Set<TicketingSalesAcDoc> gettSalesAcDocuments() {
        return tSalesAcDocuments;
    }

    public void settSalesAcDocuments(Set<TicketingSalesAcDoc> tSalesAcDocuments) {
        this.tSalesAcDocuments = tSalesAcDocuments;
    }

   @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public Set<OtherSalesAcDoc> getoSalesAcDocuments() {
        return oSalesAcDocuments;
    }

    public void setoSalesAcDocuments(Set<OtherSalesAcDoc> oSalesAcDocuments) {
        this.oSalesAcDocuments = oSalesAcDocuments;
    }

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public Set<TicketingPurchaseAcDoc> gettPurchaseAcDocuments() {
        return tPurchaseAcDocuments;
    }

    public void settPurchaseAcDocuments(Set<TicketingPurchaseAcDoc> tPurchaseAcDocuments) {
        this.tPurchaseAcDocuments = tPurchaseAcDocuments;
    }
}
