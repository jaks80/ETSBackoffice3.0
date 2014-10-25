package etsbackoffice.domain;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;

/**
 *
 * @author Yusuf
 */
//@Entity
//@Table(name = "contactinfo")
public class ContactInfo implements Serializable {

    private long contactId;
    private String addLine1;
    private String addLine2;
    private String city;
    private String province;
    private String postCode;
    private String telNo;
    private String telNo1;
    private String mobile;
    private String email;
    private String fax;
    private String remark;
    private String contactPerson;
    private List<Contactable> contactables;

    public ContactInfo() {
    }

    //@Id
   // @Column(name = "CONTACTID")
    //@TableGenerator(name = "contactid", table = "contactpktb",
    //pkColumnName = "contactkey", pkColumnValue = "contactvalue", allocationSize = 1)
    //@GeneratedValue//(strategy = GenerationType.TABLE, generator = "contactid")
    public long getContactId() {
        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }



    

    //@OneToMany(mappedBy = "contactable")
    public List<Contactable> getContactables() {
        return contactables;
    }

    public void setContactables(List<Contactable> contactables) {
        this.contactables = contactables;
    }
}
