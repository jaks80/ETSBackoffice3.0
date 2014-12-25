package com.ets.accountingdoc.domain;

import com.ets.PersistentObject;
import com.ets.util.Enums.PaymentType;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
public class TicketingPurchasePayment  extends PersistentObject implements Serializable {
    
    @XmlElement
    private String remark;
    @XmlElement
    private Set<TicketingPurchaseAcDoc> payments = new LinkedHashSet<>();
    @XmlElement    
    private PaymentType paymentType;
    
    public TicketingPurchasePayment(){
    
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Column(nullable=false)
    public Set<TicketingPurchaseAcDoc> getPayments() {
        return payments;
    }

    public void setPayments(Set<TicketingPurchaseAcDoc> payments) {
        this.payments = payments;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }      
    
    public void addAccountingDocument(TicketingPurchaseAcDoc payment){
     this.payments.add(payment);
    }
}
