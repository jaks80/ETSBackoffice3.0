package com.ets.fe.client.model;

import java.io.Serializable;
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
public class Customer extends Contactable implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    @XmlElement
    private String surName;
    @XmlElement
    private String foreName;
    
    public Customer() {
        super();        
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public String getForeName() {
        return foreName;
    }

    public void setForeName(String foreName) {
        this.foreName = foreName;
    }
                
   
    @Override
    public String calculateFullName() {
      return this.surName + "/" + this.foreName;
    }
}
