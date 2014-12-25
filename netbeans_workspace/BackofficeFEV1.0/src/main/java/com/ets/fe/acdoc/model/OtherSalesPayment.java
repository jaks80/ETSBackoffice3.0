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
public class OtherSalesPayment extends PersistentObject implements Serializable{
    
    @XmlElement
    private String remark;
    @XmlElement
    private Set<OtherSalesAcDoc> payments = new LinkedHashSet<>();
    @XmlElement    
    private PaymentType paymentType;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Set<OtherSalesAcDoc> getPayments() {
        return payments;
    }

    public void setPayments(Set<OtherSalesAcDoc> payments) {
        this.payments = payments;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }        
}
