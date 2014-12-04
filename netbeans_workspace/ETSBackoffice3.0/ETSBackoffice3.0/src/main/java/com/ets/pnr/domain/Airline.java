package com.ets.pnr.domain;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Yusuf
 */
@Entity
@XmlRootElement
public class Airline implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @XmlElement
        @Id  
    private String code;
    @XmlElement
    private String name;
    
    public Airline(){
    
    }
  
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }        
}
