package com.ets.fe.app.model;

import com.ets.fe.PersistentObject;
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
public class AppSettings  extends PersistentObject implements Serializable{

    @XmlElement
    private String email;
    @XmlElement
    private String emailPassword;
    @XmlElement
    private String emailHost;
    @XmlElement
    private int port;
    
    @XmlElement
    private String tInvTAndC;
    @XmlElement
    private String oInvTAndC;
    @XmlElement
    private String tInvFooter;
    @XmlElement
    private String oInvFooter;
    @XmlElement
    private String mainAgentCode;
    @XmlElement
    private BigDecimal vatRate = new BigDecimal("0.00");
    @XmlElement
    private Integer version;
    
    public AppSettings(){

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

    public String getMainAgentCode() {
        return mainAgentCode;
    }

    public void setMainAgentCode(String mainAgentCode) {
        this.mainAgentCode = mainAgentCode;
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


}
