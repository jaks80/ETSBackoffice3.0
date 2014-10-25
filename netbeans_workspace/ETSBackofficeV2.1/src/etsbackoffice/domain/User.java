package etsbackoffice.domain;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;



/**
 * Creation Date: 30 Sep 2010
 * @author Yusuf
 */
@Entity
//@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Table(name = "userinfo")
public class User implements Serializable {

    private long userId;
    private String loginID;
    private String password;
    private String surName;
    private String foreName;
    private String addLine1;
    private String addLine2;
    private String city;
    private String province;
    private String postCode;
    private String telNo;
    private String mobile;
    private String email;
    private Date createdOn;
    private User createdBy;
    private Date lastModified;
    private User lastModifiedBy;
    private boolean isActive;
    private UserRole userRole;
    //*******************User permission booleans******************

    public User() {
    }

    @Id
    @TableGenerator(name = "userid", table = "userpktb",
    pkColumnName = "userkey", pkColumnValue = "empvalue", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "userid")
    @Column(name = "USERID")
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
    /*@Id
    @TableGenerator(name = "userid", table = "userpktb",
    pkColumnName = "userkey", pkColumnValue = "empvalue", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "userid")
    @Column(name = "ID")
    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }*/

    @Basic
    @Column(name = "LOGINID", nullable = false, length = 20, unique = true)
    public String getLoginID() {
        return loginID;
    }

    public void setLoginID(String loginID) {
        this.loginID = loginID;
    }

    @Basic
    @Column(name = "PWORD", nullable = false, length = 20)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Basic
    @Column(name = "SURNAME", nullable = false, length = 50)
    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    @Basic
    @Column(name = "FORENAME", nullable = false, length = 50)
    public String getForeName() {
        return foreName;
    }

    public void setForeName(String foreName) {
        this.foreName = foreName;
    }    

        @Basic
    @Column(name = "ADDLINE1", length = 50)
    public String getAddLine1() {
        return addLine1;
    }

    public void setAddLine1(String addLine1) {
        this.addLine1 = addLine1;
    }

    @Basic
    @Column(name = "ADDLINE2", length = 50)
    public String getAddLine2() {
        return addLine2;
    }

    public void setAddLine2(String addLine2) {
        this.addLine2 = addLine2;
    }

    @Basic
    @Column(name = "CITY", length = 50)
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Basic
    @Column(name = "PROVINCE", length = 50)
    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    @Basic
    @Column(name = "POSTCODE", length = 50)
    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    @Basic
    @Column(name = "TELNO", length = 50)
    public String getTelNo() {
        return telNo;
    }

    public void setTelNo(String telNo) {
        this.telNo = telNo;
    }

    @Basic
    @Column(name = "MOBILE", length = 50)
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Basic
    @Column(name = "EMAIL", length = 50)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    @Basic
    @Temporal(TemporalType.DATE)
    @Column(name = "CREATEDON")
    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "LASTMODIFIED")
    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    @Basic
    @Column(name = "ISACTIVE")
    public boolean getStatus() {
        return isActive;
    }

    public void setStatus(boolean isActive) {
        this.isActive = isActive;
    }

    @ManyToOne
    @JoinColumn(name = "CREATEDBY")
    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    @ManyToOne
    @JoinColumn(name = "MODIFIEDBY")
    public User getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(User lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    @OneToOne
    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }
    
    @Transient
    public String getFullName(){
    return this.getSurName()+" / "+this.getForeName();
    }        
}
