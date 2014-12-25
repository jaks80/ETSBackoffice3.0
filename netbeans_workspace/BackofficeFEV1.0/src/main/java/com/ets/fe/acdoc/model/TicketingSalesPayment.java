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
public class TicketingSalesPayment  extends PersistentObject implements Serializable {
    
    @XmlElement
    private String remark;
    @XmlElement
    private List<TicketingSalesAcDoc> payments = new ArrayList<>();
    @XmlElement    
    private PaymentType paymentType;
    
    public TicketingSalesPayment(){
    
    }

    public void addPayment(TicketingSalesAcDoc doc){
     this.payments.add(doc);
    }
    
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<TicketingSalesAcDoc> getPayments() {
        return payments;
    }

    public void setPayments(List<TicketingSalesAcDoc> payments) {
        this.payments = payments;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }      
    
    public void addAccountingDocument(TicketingSalesAcDoc payment){
     this.payments.add(payment);
    }
}
