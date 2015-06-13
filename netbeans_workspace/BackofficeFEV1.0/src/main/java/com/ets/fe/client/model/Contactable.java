package com.ets.fe.client.model;

import com.ets.fe.PersistentObject;
import java.io.Serializable;
import javax.xml.bind.annotation.*;

/**
 *
 * @author Yusuf
 */


@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public abstract class Contactable extends PersistentObject implements Serializable{
    
    private static final long serialVersionUID = 1L;

        @XmlElement
    private String contactPerson;
    @XmlElement
    private String addLine1;
    @XmlElement
    private String addLine2;
    @XmlElement
    private String city;
    @XmlElement
    private String province;
    @XmlElement
    private String postCode;
    @XmlElement
    private String country;
    @XmlElement
    private String telNo;
    @XmlElement
    private String mobile;
    @XmlElement
    private String email;
    @XmlElement
    private String fax;
    @XmlElement
    private String remark;    

    public Contactable(){
    
    }
    
    public abstract String calculateFullName();

    
    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }
    
    public String getAddLine1() {
        return addLine1;
    }

    public void setAddLine1(String addLine1) {
        this.addLine1 = addLine1;
    }

    public String getAddLine2() {
        return addLine2;
    }

    public void setAddLine2(String addLine2) {
        this.addLine2 = addLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTelNo() {
        return telNo;
    }

    public void setTelNo(String telNo) {
        this.telNo = telNo;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getFullAddress() {
        String fullAddress = "";

        if (this.getAddLine1() != null && !this.getAddLine1().isEmpty()) {
            fullAddress = fullAddress.concat("," + this.getAddLine1());
        }
        if (this.getAddLine2() != null && !this.getAddLine2().isEmpty()) {
            fullAddress = fullAddress.concat("," + this.getAddLine2());
        }
        if (this.getCity() != null && !this.getCity().isEmpty()) {
            fullAddress = fullAddress.concat("," + this.getCity()+" " + this.getPostCode());
        }

        if (this.getTelNo() != null && !this.getTelNo().isEmpty()) {
            fullAddress = fullAddress.concat("," + "Tel: " + this.getTelNo());
        }
        if (this.getFax() != null && !this.getFax().isEmpty()) {
            fullAddress = fullAddress.concat("," + "Fax: " + this.getFax());
        }
        if (this.getEmail() != null && !this.getEmail().isEmpty()) {
            fullAddress = fullAddress.concat("," + "EMail: " + this.getEmail());
        }

        return fullAddress;
    }

    public String getFullAddressCRSeperated() {
        String fullAddress = "";

        if (this.getAddLine1() != null && !this.getAddLine1().isEmpty()) {
            fullAddress = fullAddress.concat(this.getAddLine1());
        }
        if (this.getAddLine2() != null && !this.getAddLine2().isEmpty()) {
            fullAddress = fullAddress.concat("\n" + this.getAddLine2());
        }
        if (this.getCity() != null && !this.getCity().isEmpty()) {
            fullAddress = fullAddress.concat("\n" + this.getCity()+" " + this.getPostCode());
        }

        if (this.getTelNo() != null && !this.getTelNo().isEmpty()) {
            fullAddress = fullAddress.concat("\n" + "Tel: " + this.getTelNo());
        }
        if (this.getFax() != null && !this.getFax().isEmpty()) {
            fullAddress = fullAddress.concat("\n" + "Fax: " + this.getFax());
        }
        if (this.getEmail() != null&&!this.getEmail().isEmpty()) {
            fullAddress = fullAddress.concat("\n" + "EMail: " + this.getEmail());
        }

        return fullAddress;
    }
    
    public String getAddressCRSeperated() {
        String fullAddress = "";

        if (this.getAddLine1() != null && !this.getAddLine1().isEmpty()) {
            fullAddress = fullAddress.concat(this.getAddLine1());
        }
        if (this.getAddLine2() != null && !this.getAddLine2().isEmpty()) {
            fullAddress = fullAddress.concat("\n" + this.getAddLine2());
        }
        if (this.getCity() != null && !this.getCity().isEmpty()) {
            fullAddress = fullAddress.concat("\n" + this.getCity()+" " + this.getPostCode());
        }
        
        return fullAddress;
    }
}
