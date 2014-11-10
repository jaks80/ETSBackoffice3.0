package com.ets.fe.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Yusuf
 */

@XmlRootElement
public abstract class PersistentObject implements Serializable {

    private Long id;
    private Timestamp createdOn;
    private Timestamp lastModified;
    //private User createdBy;
    //private User lastModifiedBy;

    public PersistentObject() {

    }

    protected void copy(final PersistentObject source) {
        this.id = source.id;
        this.createdOn = source.createdOn;
        this.lastModified = source.lastModified;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof PersistentObject)) {
            return false;
        }
        final PersistentObject other = (PersistentObject) obj;
        if (this.id != null && other.id != null) {
            if (this.id != other.id) {
                return false;
            }
        }
        return true;
    }

    protected void updateObjectProperties() {
        Date date = new Date();
        if (createdOn == null) {
            createdOn = new Timestamp(date.getTime());
        }

        lastModified = new Timestamp(date.getTime());
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    public Timestamp getLastModified() {
        return lastModified;
    }

    public void setLastModified(Timestamp lastModified) {
        this.lastModified = lastModified;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
