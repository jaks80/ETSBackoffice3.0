package com.ets.fe.acdoc.model.collection;

import com.ets.fe.acdoc.model.Payment;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author Yusuf
 */
public class Payments {
    
    @XmlElement
    private List<Payment> list = new ArrayList<>();

    public List<Payment> getList() {
        return list;
    }

    public void setList(List<Payment> list) {
        this.list = list;
    }        
}
