package com.ets.pnr.domain;

import com.ets.PersistentObject;
import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Creation Date: 30 Sep 2010
 *
 * @author Yusuf
 */
@Entity
@XmlRootElement
public class PnrRemark extends PersistentObject implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @XmlElement
    private Timestamp dateTime;
    @XmlElement
    private String text;
    @XmlElement
    private Pnr pnr;

    public PnrRemark() {

    }

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
    @JoinColumn(name = "pnrid_fk")
    public Pnr getPnr() {
        return pnr;
    }

    public void setPnr(Pnr pnr) {
        this.pnr = pnr;
    }
}
