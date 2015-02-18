package com.ets.fe.acdoc_o.model;

import com.ets.fe.acdoc_o.model.OtherSalesAcDoc;
import com.ets.fe.PersistentObject;
import com.ets.fe.acdoc.model.TicketingPurchaseAcDoc;
import com.ets.fe.acdoc.model.TicketingSalesAcDoc;
import com.ets.fe.os.model.AdditionalCharge;
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
public class AdditionalChargeLine extends PersistentObject implements Serializable {

    @XmlElement
    private BigDecimal amount = new BigDecimal("0.00");    
    @XmlElement
    private AdditionalCharge additionalCharge;

    @XmlElement
    private TicketingSalesAcDoc ticketingSalesAcDoc;
    @XmlElement
    private TicketingPurchaseAcDoc ticketingPurchaseAcDoc;
    @XmlElement
    private OtherSalesAcDoc otherSalesAcDoc;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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
