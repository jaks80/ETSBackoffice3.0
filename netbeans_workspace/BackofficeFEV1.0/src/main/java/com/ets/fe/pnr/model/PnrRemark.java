package com.ets.fe.pnr.model;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * Creation Date: 30 Sep 2010
 * @author Yusuf
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class PnrRemark implements Serializable {

     private static final long serialVersionUID = 1L;
     
    @XmlElement
    private int remarkId;    
    @XmlElement
    private Timestamp dateTime;
    @XmlElement
    private String text;
    
    @XmlElement
    private Pnr pnr;
    

    public PnrRemark() {
        
    }
    
    public int getRemarkId() {
        return remarkId;
    }

    public void setRemarkId(int remarkId) {
        this.remarkId = remarkId;
    }     
    
    //@Temporal(javax.persistence.TemporalType.DATE)
    public Timestamp getDateTime() {
        return dateTime;
    }

    public void setDateTime(Timestamp dateTime) {
        this.dateTime = dateTime;
    }    

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }  

    public Pnr getPnr() {
        return pnr;
    }

    public void setPnr(Pnr pnr) {
        this.pnr = pnr;
    }
    }
