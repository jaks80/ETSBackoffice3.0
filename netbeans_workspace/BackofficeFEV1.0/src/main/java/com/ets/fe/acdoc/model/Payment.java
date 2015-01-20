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
    private List<TicketingSalesAcDoc> tSalesAcDocuments = new ArrayList<>();
    @XmlElement
    private List<OtherSalesAcDoc> oSalesAcDocuments = new ArrayList<>();
    @XmlElement
    private List<TicketingPurchaseAcDoc> tPurchaseAcDocuments = new ArrayList<>();
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
    
    public void addTSalesDocument(TicketingSalesAcDoc  ticketingSalesAcDoc){
     this.tSalesAcDocuments.add(ticketingSalesAcDoc);
    }
}
