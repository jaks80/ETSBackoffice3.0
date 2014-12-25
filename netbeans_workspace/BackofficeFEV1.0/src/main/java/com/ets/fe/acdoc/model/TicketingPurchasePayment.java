package com.ets.fe.acdoc.model;

import com.ets.fe.PersistentObject;
import com.ets.fe.util.Enums.PaymentType;
import java.io.Serializable;
import java.util.LinkedHashSet;
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
