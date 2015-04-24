package com.ets.fe;

import com.ets.fe.settings.model.User;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Yusuf
 */

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public abstract class PersistentObject implements Serializable {

    @XmlElement
    private Long id;
    @XmlElement
    private Date createdOn = new java.util.Date();
    @XmlElement
    private Date lastModified;
    @XmlElement
    private User createdBy = Application.getLoggedOnUser();
    @XmlElement
    private User lastModifiedBy;

    public PersistentObject() {

    }

    public String getCreatedByName() {
        String initial = "";
        if (this.createdBy != null) {
            initial = this.createdBy.calculateFullName();
        }
        return initial;
    }
    
    protected void copy(final PersistentObject source) {
        this.id = source.id;
        this.createdOn = source.createdOn;
        this.setLastModified(source.getLastModified());
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.id);
        return hash;
    }

    public void recordUpdateBy() {
        setLastModified(new java.util.Date());
        setLastModifiedBy(Application.getLoggedOnUser());
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public User getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(User lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Date getLastModified() {
        return lastModified;
    }
}
