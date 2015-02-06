package com.ets.fe.app.model;


import com.ets.fe.client.model.Contactable;
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
    private int UserType;//1. Manager 3. Supervisor 4. Sales
    @XmlElement
    private boolean isActive;
    
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

    public int getUserType() {
        return UserType;
    }

    public void setUserType(int UserType) {
        this.UserType = UserType;
    }
    
    @Override
    public String getFullName() {
      return this.surName + "/" + this.foreName;
    }
}
