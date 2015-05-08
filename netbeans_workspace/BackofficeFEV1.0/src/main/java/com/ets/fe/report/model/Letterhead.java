package com.ets.fe.report.model;

import com.ets.fe.Application;
import java.io.ByteArrayInputStream;
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
    private String tInvFooter;
    @XmlElement
    private String oInvFooter;
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

    public ByteArrayInputStream getCompanyLogo() {
        ByteArrayInputStream bis = new ByteArrayInputStream(Application.getAppSettings().getCompanyLogo());
        return bis;
    }

    public ByteArrayInputStream getAtolLogo() {
        ByteArrayInputStream bis = new ByteArrayInputStream(Application.getAppSettings().getAtolLogo());
        return bis;
    }

    public ByteArrayInputStream getIataLogo() {
        ByteArrayInputStream bis = new ByteArrayInputStream(Application.getAppSettings().getIataLogo());
        return bis;
    }

    public ByteArrayInputStream getOtherLogo() {
        ByteArrayInputStream bis = new ByteArrayInputStream(Application.getAppSettings().getOtherLogo());
        return bis;
    }

    public String gettInvFooter() {
        return tInvFooter;
    }

    public void settInvFooter(String tInvFooter) {
        this.tInvFooter = tInvFooter;
    }

    public String getoInvFooter() {
        return oInvFooter;
    }

    public void setoInvFooter(String oInvFooter) {
        this.oInvFooter = oInvFooter;
    }
}
