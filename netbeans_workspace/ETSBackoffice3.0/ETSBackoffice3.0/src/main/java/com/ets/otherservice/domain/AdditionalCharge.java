package com.ets.otherservice.domain;

import com.ets.PersistentObject;
import com.ets.accountingdoc.domain.AccountingDocument;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
public class AdditionalCharge extends PersistentObject implements Serializable{

    @XmlElement
    private String title;
    @XmlElement
    private BigDecimal charge = new BigDecimal("0.00");
    @XmlElement
    private int calculationType = 0; //1-Manual 2-Fixed Rate 3-Percentage
    @XmlElement
    private boolean isArchived;
    @XmlElement
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acdoc_fk")
    private AccountingDocument accountingDocument;

    public AdditionalCharge(){

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getCharge() {
        return charge;
    }

    public void setCharge(BigDecimal charge) {
        this.charge = charge;
    }

    public int getCalculationType() {
        return calculationType;
    }

    public void setCalculationType(int calculationType) {
        this.calculationType = calculationType;
    }

    public boolean isIsArchived() {
        return isArchived;
    }

    public void setIsArchived(boolean isArchived) {
        this.isArchived = isArchived;
    }

    public AccountingDocument getAccountingDocument() {
        return accountingDocument;
    }

    public void setAccountingDocument(AccountingDocument accountingDocument) {
        this.accountingDocument = accountingDocument;
    }
}
