package com.ets.otherservice.domain;

import com.ets.PersistentObject;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Yus nm,; uf
 */
@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@Access(AccessType.FIELD)
public class OtherService extends PersistentObject implements Serializable {

    @XmlElement
    @Column(nullable = false, length = 60)
    private String title;
    @XmlElement
    private String description;
    @XmlElement
    private BigDecimal purchaseCost = new BigDecimal("0.00");
    @XmlElement
    @Column(nullable = false)
    private BigDecimal sellingPrice = new BigDecimal("0.00");
    @XmlElement
    @Column(nullable = false)
    private int vatable;//0-no, 1-yes
    @XmlElement
    @Column(nullable = false)
    private int isActive;//0-yes, 1-no
    @XmlElement
    @ManyToOne
    private Category category;

    public OtherService() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPurchaseCost() {
        return purchaseCost;
    }

    public void setPurchaseCost(BigDecimal purchaseCost) {
        this.purchaseCost = purchaseCost;
    }

    public BigDecimal getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(BigDecimal sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public int isVatable() {
        return getVatable();
    }

    public void setVatable(int vatable) {
        this.vatable = vatable;
    }

    public int getVatable() {
        return vatable;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
