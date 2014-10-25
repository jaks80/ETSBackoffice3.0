package etsbackoffice.domain;

import java.io.Serializable;
import javax.persistence.*;

/**
 * Creation Date: 30 Sep 2010
 * @author Yusuf
 */
@Entity
@PrimaryKeyJoinColumn(name = "CUSTID")
@Table(name = "customer")
public class Customer extends Contactable implements Serializable {

    private String surName;
    private String foreName;

    public Customer() {
        super();
        
    }

    @Basic
    @Column(name = "SURNAME")
    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    @Basic
    @Column(name = "FORENAME")
    public String getForeName() {
        return foreName;
    }

    public void setForeName(String foreName) {
        this.foreName = foreName;
    }

    @Transient
    public String getFullCustomerName() {
        return this.surName + "/" + this.foreName;
    }

    @Transient
    public String getFullAddress() {
        String fullAddress = "";

        if (!this.getFullCustomerName().equals("") && this.getFullCustomerName() != null) {
            fullAddress = fullAddress.concat(this.getFullCustomerName());
        }

        if (!this.getAddLine1().equals("") && this.getAddLine1() != null) {
            fullAddress = fullAddress.concat("," + this.getAddLine1());
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
            fullAddress = fullAddress.concat("," + "Tel: " + this.getTelNo());
        }
        if (!this.getFax().equals("") && this.getFax() != null) {
            fullAddress = fullAddress.concat("," + "Fax: " + this.getFax());
        }
        if (!this.getEmail().equals("") && this.getEmail() != null) {
            fullAddress = fullAddress.concat("," + "EMail: " + this.getEmail());
        }

        return fullAddress;
    }

    @Transient
    public String getFullAddressCRSeperated() {
        String fullAddress = "";

        if (!this.getFullCustomerName().equals("") && this.getFullCustomerName() != null) {
            fullAddress = fullAddress.concat(this.getFullCustomerName());
        }

        if (!this.getAddLine1().equals("") && this.getAddLine1() != null) {
            fullAddress = fullAddress.concat("\n" + this.getAddLine1());
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
            fullAddress = fullAddress.concat("\n" + "Tel: " + this.getTelNo());
        }
        if (!this.getFax().equals("") && this.getFax() != null) {
            fullAddress = fullAddress.concat("\n" + "Fax: " + this.getFax());
        }
        if (!this.getEmail().equals("") && this.getEmail() != null) {
            fullAddress = fullAddress.concat("\n" + "EMail: " + this.getEmail());
        }

        return fullAddress;
    }
    
    @Transient
    public String getAddressCRSeperated() {
        String fullAddress = "";

        if (!this.getFullCustomerName().equals("") && this.getFullCustomerName() != null) {
            fullAddress = fullAddress.concat(this.getFullCustomerName());
        }

        if (!this.getAddLine1().equals("") && this.getAddLine1() != null) {
            fullAddress = fullAddress.concat("\n" + this.getAddLine1());
        }
        if (!this.getAddLine2().equals("") && this.getAddLine2() != null) {
            fullAddress = fullAddress.concat("\n" + this.getAddLine2());
        }
        if (!this.getCity().equals("") && this.getCity() != null) {
            fullAddress = fullAddress.concat("\n" + this.getCity());
        }
        if (!this.getPostCode().equals("") && this.getPostCode() != null) {
            fullAddress = fullAddress.concat(", " + this.getPostCode());
        }
        

        return fullAddress;
    }
}
