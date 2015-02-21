package com.ets.fe.app.model;


import com.ets.fe.client.model.Contactable;
import com.ets.fe.util.Enums;
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
public class User extends Contactable implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    @XmlElement
    private String loginID;
    @XmlElement
    private String password;
    @XmlElement
    private String surName;
    @XmlElement
    private String foreName;
    @XmlElement
    private Enums.UserType userType;
    @XmlElement
    private boolean isActive;
    @XmlElement
    private String sessionId;
    
    public User(){
    
    }

    public String getLoginID() {
        return loginID;
    }

    public void setLoginID(String loginID) {
        this.loginID = loginID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
    
    @Override
    public String getFullName() {
      return this.getSurName() + "/" + this.getForeName();
    }

    public Enums.UserType getUserType() {
        return userType;
    }

    public void setUserType(Enums.UserType userType) {
        this.userType = userType;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
