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
@Access(AccessType.FIELD)
@Table(name = "acdoc_line")
public class AccountingDocumentLine  extends PersistentObject implements Serializable {
    
    @XmlElement
    private String title;
    @XmlElement
    private String remark;
    @XmlElement
    private BigDecimal amount = new BigDecimal("0.00");
    @XmlElement
    private BigDecimal discount = new BigDecimal("0.00");
    @XmlElement
    private int qty;

    @XmlElement
    @OneToOne
    @JoinColumn(name = "other_service_fk")
    private OtherService otherService;
    @XmlElement      
    @OneToOne
    @JoinColumn(name = "add_charge_fk")
    private AdditionalCharge additionalCharge;
    
    @XmlElement
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acdoc_fk")
    private AccountingDocument accountingDocument;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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
