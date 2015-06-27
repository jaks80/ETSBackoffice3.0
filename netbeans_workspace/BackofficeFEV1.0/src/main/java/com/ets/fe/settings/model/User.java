package com.ets.fe.settings.model;

import com.ets.fe.client.model.Contactable;
import com.ets.fe.util.Enums;
import java.io.Serializable;
import javax.xml.bind.annotation.*;

/**
 *
 * @author Yusuf
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class User extends Contactable implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement
    private String loginID;
    @XmlElement
    private String password;
    @XmlElement
    private String newPassword;
    @XmlElement
    private String surName;
    @XmlElement
    private String foreName;
    @XmlElement
    private Enums.UserType userType;
    @XmlElement
    private boolean active;
    @XmlElement
    private String sessionId;

    public User() {

    }

    public boolean isAdmin() {
        if (userType.equals(Enums.UserType.AD)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isMan() {
        if (userType.equals(Enums.UserType.SM)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isGs() {
        if (userType.equals(Enums.UserType.GS)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isSu() {
        if (userType.equals(Enums.UserType.SU)) {
            return true;
        } else {
            return false;
        }
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


    @Override
    public String calculateFullName() {
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
