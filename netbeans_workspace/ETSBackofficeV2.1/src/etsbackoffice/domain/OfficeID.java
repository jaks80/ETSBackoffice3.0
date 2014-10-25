package etsbackoffice.domain;

import java.io.Serializable;
import javax.persistence.*;

/**
 *
 * @author Yusuf
 */
@Entity
@Table(name = "officeid")
public class OfficeID implements Serializable {

    private long officeId_Id;    
    private String officeID;
    private int type;//1.Main Office ID,2.Sub Agent officeID.
    private Agent agent;    
    private GDS gds;    

    public OfficeID() {
        
    }

    @Id
    @Column(name = "OID_ID")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "officeid_id")
    @TableGenerator(name = "officeid_id", table = "officeidpktb",
    pkColumnName = "officeidkey", pkColumnValue = "officeidvalue",
    allocationSize = 1)
       public long getOfficeId_Id() {
        return officeId_Id;
    }


    public void setOfficeId_Id(long officeId_Id) {
        this.officeId_Id = officeId_Id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GDSID_FK")
    public GDS getGds() {
        return gds;
    }

    public void setGds(GDS gds) {
        this.gds = gds;
    }

    @Basic
    @Column(name = "OFFICEID")
    public String getOfficeID() {
        return officeID;
    }

    public void setOfficeID(String officeID) {
        this.officeID = officeID;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AGENTID_FK")
    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }
    @Basic
    @Column(name = "TYPE", nullable=true)
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
