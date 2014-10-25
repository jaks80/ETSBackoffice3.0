package etsbackoffice.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.*;

/**
 * Creation Date: 30 Sep 2010
 * @author Yusuf
 */
@Entity
//@Inheritance(strategy = InheritanceType.JOINED)
@PrimaryKeyJoinColumn(name = "AGTID")
@Table(name = "agent")
public class Agent extends Contactable implements Serializable {

    private String name;
    private String web;
    private String iata;
    private String atol;
    private BigDecimal creditLimit = new BigDecimal("0.00");
    private boolean cLimitOverInvoicing;
    //private List<GDS> gdss;
    private List<OfficeID> officeIDs;
    private Contactable contactable;

    public Agent() {
        super();        
    }

    @Basic
    @Column(name = "NAME", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "WEB")
    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    @Basic
    @Column(name = "IATA")
    public String getIata() {
        return iata;
    }

    public void setIata(String iata) {
        this.iata = iata;
    }

    @Basic
    @Column(name = "ATOL")
    public String getAtol() {
        return atol;
    }

    public void setAtol(String atol) {
        this.atol = atol;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public boolean iscLimitOverInvoicing() {
        return cLimitOverInvoicing;
    }

    public void setcLimitOverInvoicing(boolean cLimitOverInvoicing) {
        this.cLimitOverInvoicing = cLimitOverInvoicing;
    }

    @OneToMany(targetEntity = OfficeID.class, mappedBy = "agent",
    cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<OfficeID> getOfficeIDs() {
        return officeIDs;
    }

    public void setOfficeIDs(List<OfficeID> officeIDs) {
        this.officeIDs = officeIDs;
    }

    public boolean addOfficeID(OfficeID officeID) {
        boolean isExist = false;
        loop:
        for (int i = 0; i < officeIDs.size(); i++) {
            if (officeIDs.get(i).getOfficeID().contains(officeID.getOfficeID())) {
                isExist = true;
                break loop;
            }
        }
        if (isExist == false) {
            this.officeIDs.add(officeID);
            System.out.println("OID:" + this.officeIDs.size());
            return true;
        } else {
            return false;
        }
    }

    public void clean() {
        name = null;
        web = null;
        iata = null;
        atol = null;

        officeIDs = null;
    }

    @Transient
    public String getFullAddress() {
        String fullAddress = "";

        if (!this.getName().equals("") && this.getName() != null) {
            fullAddress = fullAddress.concat(this.getName());
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

        if (!this.getName().equals("") && this.getName() != null) {
            fullAddress = fullAddress.concat(this.getName());
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

        if (!this.getName().equals("") && this.getName() != null) {
            fullAddress = fullAddress.concat(this.getName());
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
    
    @Transient
    public String getCompanyDetailsCommaSeperated() {
        String details = "";

        if (!this.getName().equals("") && this.getName() != null) {
            details = details.concat(this.getName());
        }

        if (!this.getAtol().equals("") && this.getAtol() != null) {
            details = details.concat(", " + this.getAtol());
        }
        if (!this.getIata().equals("") && this.getIata() != null) {
            details = details.concat(", " + this.getAddLine2());
        }
        return details;
    }
}
