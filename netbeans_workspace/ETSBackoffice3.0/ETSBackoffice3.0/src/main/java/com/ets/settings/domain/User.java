package com.ets.settings.domain;

import com.ets.PersistentObject;
import com.ets.util.Enums;
import java.io.Serializable;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Yusuf
 */
@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@Table(name = "bo_user")
@Access(AccessType.PROPERTY)
public class User extends PersistentObject implements Serializable{
    
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
    
    public User(){
    
    }

    @Column(unique = true)
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
   
    public String calculateFullName() {
      return this.surName + "/" + this.foreName;
    }

    public Enums.UserType getUserType() {
        return userType;
    }

    public void setUserType(Enums.UserType userType) {
        this.userType = userType;
    }
}
