package com.ets.pnr.domain;

import com.ets.PersistentObject_1;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

/**
 *
 * @author Yusuf
 */
@Entity
@Access(AccessType.PROPERTY)
public class Parent extends PersistentObject_1 implements Serializable {

    private String title;    
    private Set<Children> children = new LinkedHashSet<>();

    public Parent() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @OneToMany(mappedBy = "parent")
    public Set<Children> getChildren() {
        return children;
    }

    public void setChildren(Set<Children> children) {
        this.children = children;
    }
}
