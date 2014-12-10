package com.ets.pnr.domain;

import com.ets.PersistentObject_1;
import java.io.Serializable;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 *
 * @author Yusuf
 */
@Entity
@Access(AccessType.PROPERTY)
public class Children extends PersistentObject_1 implements Serializable {

    private String title;
    
    private Parent parent;

    public Children() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @ManyToOne
    public Parent getParent() {
        return parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }
}
