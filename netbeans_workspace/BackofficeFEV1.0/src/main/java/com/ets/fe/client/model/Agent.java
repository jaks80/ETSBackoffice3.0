package com.ets.fe.client.model;

import java.io.Serializable;
import java.math.BigDecimal;
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
public class Agent extends Contactable implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    @XmlElement
    private String contactPerson;
    @XmlElement
    private String name;
    @XmlElement
    private String web;    
    @XmlElement
    private String atol;
    @XmlElement
    private String iata;
    @XmlElement
    private String abta;
    @XmlElement
    private BigDecimal creditLimit = new BigDecimal("0.00");
    @XmlElement
    private boolean cLimitOverInvoicing;    
    @XmlElement
    private String officeID;
    
    public Agent() {
        super();
        
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getAtol() {
        return atol;
    }

    public void setAtol(String atol) {
        this.atol = atol;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public boolean iscLimitOverInvoicing() {
        return cLimitOverInvoicing;
    }

    public void setcLimitOverInvoicing(boolean cLimitOverInvoicing) {
        this.cLimitOverInvoicing = cLimitOverInvoicing;
    }

    public String getOfficeID() {
        return officeID;
    }

    public void setOfficeID(String officeID) {
        this.officeID = officeID;
    } 

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getIata() {
        return iata;
    }

    public void setIata(String iata) {
        this.iata = iata;
    }

    public String getAbta() {
        return abta;
    }

    public void setAbta(String abta) {
        this.abta = abta;
    }
}
