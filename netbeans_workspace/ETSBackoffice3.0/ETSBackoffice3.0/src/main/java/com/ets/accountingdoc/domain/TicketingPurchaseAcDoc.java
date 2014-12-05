package com.ets.accountingdoc.domain;

import java.io.Serializable;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
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
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
@Table(name = "tkt_purch_acdoc")
public class TicketingPurchaseAcDoc extends TicketingAcDoc implements Serializable{
    
    @XmlElement
    private String vendorRef;    

    public String getVendorRef() {
        return vendorRef;
    }

    public void setVendorRef(String vendorRef) {
        this.vendorRef = vendorRef;
    }
}
