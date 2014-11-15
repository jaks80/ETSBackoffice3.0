package com.ets.collection;

import com.ets.domain.pnr.Career;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Yusuf
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class Careers {
    
    private List<Career> list = new ArrayList<>();

    public List<Career> getList() {
        return list;
    }

    public void setList(List<Career> list) {
        this.list = list;
    }        
}
