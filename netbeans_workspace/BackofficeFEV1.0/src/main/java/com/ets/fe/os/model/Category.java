package com.ets.fe.os.model;


import com.ets.fe.PersistentObject;
import java.io.Serializable;
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
public class Category extends PersistentObject implements Serializable {
 
    private String title;
    
    public Category(){
    }

    @XmlElement
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }        
}
