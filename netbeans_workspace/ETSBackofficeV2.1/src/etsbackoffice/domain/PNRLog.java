/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etsbackoffice.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.*;

/**
 *
 * @author Yusuf
 */
@Entity
@Table(name = "pnrlog")
public class PNRLog implements Serializable {

    private long logId;
    private String logDetails;
    private Timestamp instance;
    private PNR pnr;
    private User user;

    public PNRLog() {
    }

    @Id
    @TableGenerator(name = "logid", table = "logpktb",
    pkColumnName = "logkey", pkColumnValue = "logvalue", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "logid")
    public long getLogId() {
        return logId;
    }

    /**
     * @param logId the logId to set
     */
    public void setLogId(long logId) {
        this.logId = logId;
    }

    /**
     * @return the logDetails
     */
    public String getLogDetails() {
        return logDetails;
    }

    /**
     * @param logDetails the logDetails to set
     */
    public void setLogDetails(String logDetails) {
        this.logDetails = logDetails;
    }

    /**
     * @return the instance
     */
    public Timestamp getInstance() {
        return instance;
    }

    /**
     * @param instance the instance to set
     */
    public void setInstance(Timestamp instance) {
        this.instance = instance;
    }

    /**
     * @return the pnr
     */
    @ManyToOne
    public PNR getPnr() {
        return pnr;
    }

    /**
     * @param pnr the pnr to set
     */
    public void setPnr(PNR pnr) {
        this.pnr = pnr;
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }
}
