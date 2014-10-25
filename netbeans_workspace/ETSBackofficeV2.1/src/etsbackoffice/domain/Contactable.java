package etsbackoffice.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

/**
 * Creation Date: 30 Sep 2010
 * @author Yusuf
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "contactable")
public class Contactable implements Serializable {

    private long contactableId;
    private String addLine1;
    private String addLine2;
    private String city;
    private String province;
    private String postCode;
    private String country;
    private String telNo;
    private String telNo1;
    private String mobile;
    private String email;
    private String fax;
    private String remark;
    private String contactPerson;
    private String ref;
    private Date createdOn;
    private User createdBy;
    private Date lastModified;
    private User lastModifiedBy;
    private boolean status;    
    private List<Accounts> accounts;    

    public Contactable() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "contactableid")
    @TableGenerator(name = "contactableid", table = "contactablepktb",
    pkColumnName = "contactablekey", pkColumnValue = "contactablevalue", allocationSize = 1)
    @Column(name = "CONTACTABLEID")
    public long getContactableId() {
        return contactableId;
    }

    public void setContactableId(long contactableId) {
        this.contactableId = contactableId;
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
    @Column(name = "TELNO1")
    public String getTelNo1() {
        return telNo1;
    }

    public void setTelNo1(String telNo1) {
        this.telNo1 = telNo1;
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
    @Column(name = "FAX", length = 450)
    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
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
    @Column(name = "REMARK", length = 450)
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Basic
    @Column(name = "CONTACTPERSON", length = 450)
    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    @Column(name = "REFFERENCE")
    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    @Basic
    @Column(name = "CREATEDON")
    @Temporal(TemporalType.DATE)
    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CREATEDBY")
    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "LASTMODIFIED")
    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MODIFIEDBY")
    public User getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(User lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Basic
    @Column(name = "ISACTIVE")
    public boolean getStatus() {
        return status;
    }    

    @OneToMany(mappedBy = "contactable")
    public List<Accounts> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Accounts> accountss) {
        this.accounts = accountss;
    }  

    /*@Transient
    public String getFullAddress() {
        String fullAddress = "";

        if (!this.getAddLine1().equals("") && this.getAddLine1() != null) {
            fullAddress = fullAddress.concat(this.getAddLine1());
        }
        if (!this.getAddLine2().equals("") && this.getAddLine2() != null) {
            fullAddress = fullAddress.concat("," + this.getAddLine2());
        }
        if (!this.getCity().equals("") && this.getCity() != null) {
            fullAddress = fullAddress.concat("," + this.getCity());
        }
        if (!this.getPostCode().equals("") && this.getPostCode() != null) {
            fullAddress = fullAddress.concat("," + this.getPostCode());
        }
        if (!this.getTelNo().equals("") && this.getTelNo() != null) {
            fullAddress = fullAddress.concat("," + this.getTelNo());
        }
        if (!this.getFax().equals("") && this.getFax() != null) {
            fullAddress = fullAddress.concat("," + this.getFax());
        }
        if (!this.getEmail().equals("") && this.getEmail() != null) {
            fullAddress = fullAddress.concat("," + this.getEmail());
        }

        return fullAddress;
    }
    
    @Transient
    public String getFullAddressCRSeperated() {
        String fullAddress = "";

        if (!this.getAddLine1().equals("") && this.getAddLine1() != null) {
            fullAddress = fullAddress.concat(this.getAddLine1());
        }
        if (!this.getAddLine2().equals("") && this.getAddLine2() != null) {
            fullAddress = fullAddress.concat("\n" + this.getAddLine2());
        }
        if (!this.getCity().equals("") && this.getCity() != null) {
            fullAddress = fullAddress.concat("\n" + this.getCity());
        }
        if (!this.getPostCode().equals("") && this.getPostCode() != null) {
            fullAddress = fullAddress.concat("\n" + this.getPostCode());
        }
        if (!this.getTelNo().equals("") && this.getTelNo() != null) {
            fullAddress = fullAddress.concat("\n" + this.getTelNo());
        }
        if (!this.getFax().equals("") && this.getFax() != null) {
            fullAddress = fullAddress.concat("\n" + this.getFax());
        }
        if (!this.getEmail().equals("") && this.getEmail() != null) {
            fullAddress = fullAddress.concat("\n" + this.getEmail());
        }

        return fullAddress;
    }*/

    /**
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param country the country to set
     */
    public void setCountry(String country) {
        this.country = country;
    }
}
