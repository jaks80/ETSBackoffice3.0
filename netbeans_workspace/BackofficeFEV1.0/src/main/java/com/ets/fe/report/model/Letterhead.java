package com.ets.fe.report.model;

import javax.xml.bind.annotation.*;

/**
 *
 * @author Yusuf
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Letterhead {
    
    @XmlElement
    private String companyName;
    @XmlElement
    private String address;
    @XmlElement
    private String footer;
    @XmlElement
    private String tInvTAndC;
    @XmlElement
    private String oInvTAndC;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }        

    public String gettInvTAndC() {
        return tInvTAndC;
    }

    public void settInvTAndC(String tInvTAndC) {
        this.tInvTAndC = tInvTAndC;
    }

    public String getoInvTAndC() {
        return oInvTAndC;
    }

    public void setoInvTAndC(String oInvTAndC) {
        this.oInvTAndC = oInvTAndC;
    }
}
