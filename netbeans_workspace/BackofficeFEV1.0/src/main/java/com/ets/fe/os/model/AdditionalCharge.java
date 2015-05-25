package com.ets.fe.os.model;

import com.ets.fe.PersistentObject;
import com.ets.fe.util.Enums.CalculationType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
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
public class AdditionalCharge extends PersistentObject implements Serializable {
    
    @XmlElement
    private String title;
    @XmlElement
    private BigDecimal charge = new BigDecimal("0.00");    
    @XmlElement
    private CalculationType calculationType;
    
    public AdditionalCharge() {
        
    }
    
    public BigDecimal calculateAmount(BigDecimal documentedAmount) {
        
        if (calculationType.equals(CalculationType.FIXED)) {
            return charge;
        } else if (calculationType.equals(CalculationType.VARIABLE)) {
            return new BigDecimal("0.00");
        } else {
            return (charge.divide(new BigDecimal("100.00"))).multiply(documentedAmount);
        }
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
    
    public CalculationType getCalculationType() {
        return calculationType;
    }
    
    public void setCalculationType(CalculationType calculationType) {
        this.calculationType = calculationType;
    }    
}
