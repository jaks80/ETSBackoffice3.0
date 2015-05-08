package com.ets.fe.settings.model;

import com.ets.fe.PersistentObject;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.xml.bind.annotation.*;

/**
 *
 * @author Yusuf
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class AppSettings extends PersistentObject implements Serializable {

    @XmlElement
    private String email;
    @XmlElement
    private String emailPassword;
    @XmlElement
    private String emailHost;
    @XmlElement
    private int port;
    @XmlElement
    private String smtp_auth;
    @XmlElement
    private String starttls_enable;

    @XmlElement
    private String tInvTAndC;
    @XmlElement
    private String oInvTAndC;
    @XmlElement
    private String tInvFooter;
    @XmlElement
    private String oInvFooter;
    @XmlElement
    private String mainAgentID;
    @XmlElement
    private BigDecimal vatRate = new BigDecimal("0.00");
    @XmlElement
    private Integer version;
    @XmlElement
    private byte[] companyLogo;
    @XmlElement
    private byte[] atolLogo;
    @XmlElement
    private byte[] iataLogo;
    @XmlElement
    private byte[] otherLogo;

    public AppSettings() {

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailPassword() {
        return emailPassword;
    }

    public void setEmailPassword(String emailPassword) {
        this.emailPassword = emailPassword;
    }

    public String getEmailHost() {
        return emailHost;
    }

    public void setEmailHost(String emailHost) {
        this.emailHost = emailHost;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public BigDecimal getVatRate() {
        return vatRate;
    }

    public void setVatRate(BigDecimal vatRate) {
        this.vatRate = vatRate;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getSmtp_auth() {
        return smtp_auth;
    }

    public void setSmtp_auth(String smtp_auth) {
        this.smtp_auth = smtp_auth;
    }

    public String getStarttls_enable() {
        return starttls_enable;
    }

    public void setStarttls_enable(String starttls_enable) {
        this.starttls_enable = starttls_enable;
    }

    public String getMainAgentID() {
        return mainAgentID;
    }

    public void setMainAgentID(String mainAgentID) {
        this.mainAgentID = mainAgentID;
    }

    public byte[] getCompanyLogo() {
        return companyLogo;
    }

    public void setCompanyLogo(byte[] companyLogo) {
        this.companyLogo = companyLogo;
    }

    public byte[] getAtolLogo() {
        return atolLogo;
    }

    public void setAtolLogo(byte[] atolLogo) {
        this.atolLogo = atolLogo;
    }

    public byte[] getIataLogo() {
        return iataLogo;
    }

    public void setIataLogo(byte[] iataLogo) {
        this.iataLogo = iataLogo;
    }

    public byte[] getOtherLogo() {
        return otherLogo;
    }

    public void setOtherLogo(byte[] otherLogo) {
        this.otherLogo = otherLogo;
    }
}
