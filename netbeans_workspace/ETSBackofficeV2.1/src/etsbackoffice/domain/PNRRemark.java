package etsbackoffice.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * Creation Date: 30 Sep 2010
 * @author Yusuf
 */
@Entity
@Table(name = "pnrremark")
public class PNRRemark implements Serializable {

    private int remarkId;    
    private Timestamp dateTime;
    private String text;
    private PNR pnr;
    private User user;

    public PNRRemark() {
        
    }

    @Id
    @Column(name = "PNRRMKID")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "pnrrmkid")
    @TableGenerator(name = "pnrrmkid", table = "pnrrmkidpktb",
    pkColumnName = "pnrrmkidkey", pkColumnValue = "pnrrmkidvalue", allocationSize = 1)
    public int getRemarkId() {
        return remarkId;
    }

    public void setRemarkId(int remarkId) {
        this.remarkId = remarkId;
    }     
    
    //@Temporal(javax.persistence.TemporalType.DATE)
    public Timestamp getDateTime() {
        return dateTime;
    }

    public void setDateTime(Timestamp dateTime) {
        this.dateTime = dateTime;
    }    

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }  

   
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PNRID_FK")
    public PNR getPnr() {
        return pnr;
    }

    public void setPnr(PNR pnr) {
        this.pnr = pnr;
    }
    
    @ManyToOne
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
