package com.ets.accountingdoc.domain;

import com.ets.PersistentObject;
import com.ets.otherservice.domain.AdditionalCharge;
import com.ets.otherservice.domain.OtherService;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
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
@Table(name = "acdoc_line")
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

    @OneToOne
    @JoinColumn(name = "other_service_fk")
    public OtherService getOtherService() {
        return otherService;
    }

    public void setOtherService(OtherService otherService) {
        this.otherService = otherService;
    }

    @OneToOne
    @JoinColumn(name = "add_charge_fk")
    public AdditionalCharge getAdditionalCharge() {
        return additionalCharge;
    }

    public void setAdditionalCharge(AdditionalCharge additionalCharge) {
        this.additionalCharge = additionalCharge;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acdoc_fk")
    public AccountingDocument getAccountingDocument() {
        return accountingDocument;
    }

    public void setAccountingDocument(AccountingDocument accountingDocument) {
        this.accountingDocument = accountingDocument;
    }
}
