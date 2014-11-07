package com.ets.model;

import java.sql.Timestamp;
import java.util.Date;

/**
 *
 * @author Yusuf
 */

public abstract class ID {

    private Timestamp createdOn;
    private Timestamp lastModified;
    //private User createdBy;
    //private User lastModifiedBy;

    public ID() {

    }

    public void updateObjectProperties() {
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
}
