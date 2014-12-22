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
    private AccountingDocument accountingDocument;

    public BigDecimal calculateOsNetSellingTotal() {
        return this.otherService.getSellingPrice().add(this.discount).multiply(new BigDecimal(qty));
    }

    public BigDecimal calculateAcNetSellingTotal() {
        return this.additionalCharge.getCharge().add(this.discount);
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

    public AccountingDocument getAccountingDocument() {
        return accountingDocument;
    }

    public void setAccountingDocument(AccountingDocument accountingDocument) {
        this.accountingDocument = accountingDocument;
    }
}
