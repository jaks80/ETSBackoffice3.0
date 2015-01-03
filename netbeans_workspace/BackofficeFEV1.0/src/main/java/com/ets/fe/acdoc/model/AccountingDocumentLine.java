package com.ets.fe.acdoc.model;

import com.ets.fe.PersistentObject;
import com.ets.fe.os.model.AdditionalCharge;
import com.ets.fe.os.model.OtherService;
import java.io.Serializable;
import java.math.BigDecimal;
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
public class AccountingDocumentLine extends PersistentObject implements Serializable {

    @XmlElement
    private String remark;
    @XmlElement
    private BigDecimal discount = new BigDecimal("0.00");
    @XmlElement
    private int qty = 1;
    @XmlElement
    private OtherService otherService;
    @XmlElement
    private AdditionalCharge additionalCharge;
    @XmlElement
    private TicketingSalesAcDoc ticketingSalesAcDoc;
    @XmlElement
    private TicketingPurchaseAcDoc ticketingPurchaseAcDoc;
    @XmlElement
    private OtherSalesAcDoc otherSalesAcDoc;

    public BigDecimal calculateOsNetSellingTotal() {
        return this.getOtherService().getSellingPrice().add(this.getDiscount()).multiply(new BigDecimal(getQty()));
    }

    public BigDecimal calculateAcNetSellingTotal() {
        return this.getAdditionalCharge().getCharge().add(this.getDiscount());
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public OtherService getOtherService() {
        return otherService;
    }

    public void setOtherService(OtherService otherService) {
        this.otherService = otherService;
    }

    public AdditionalCharge getAdditionalCharge() {
        return additionalCharge;
    }

    public void setAdditionalCharge(AdditionalCharge additionalCharge) {
        this.additionalCharge = additionalCharge;
    }

    public TicketingSalesAcDoc getTicketingSalesAcDoc() {
        return ticketingSalesAcDoc;
    }

    public void setTicketingSalesAcDoc(TicketingSalesAcDoc ticketingSalesAcDoc) {
        this.ticketingSalesAcDoc = ticketingSalesAcDoc;
    }

    public TicketingPurchaseAcDoc getTicketingPurchaseAcDoc() {
        return ticketingPurchaseAcDoc;
    }

    public void setTicketingPurchaseAcDoc(TicketingPurchaseAcDoc ticketingPurchaseAcDoc) {
        this.ticketingPurchaseAcDoc = ticketingPurchaseAcDoc;
    }

    public OtherSalesAcDoc getOtherSalesAcDoc() {
        return otherSalesAcDoc;
    }

    public void setOtherSalesAcDoc(OtherSalesAcDoc otherSalesAcDoc) {
        this.otherSalesAcDoc = otherSalesAcDoc;
    }
}
