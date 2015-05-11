package com.ets.fe.os.model;

import com.ets.fe.PersistentObject;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Yus nm,; uf
 */

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class OtherService extends PersistentObject implements Serializable {

    @XmlElement
    private String title;
    @XmlElement
    private String description;
    @XmlElement
    private BigDecimal purchaseCost = new BigDecimal("0.00");
    @XmlElement
    private BigDecimal sellingPrice = new BigDecimal("0.00");    
    @XmlElement
    private int vatable;//0-no, 1-yes
    @XmlElement
    private int active;//0-ni, 1-yes
    @XmlElement    
    private Category category;
    
    public OtherService(){
    
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



    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }
}
