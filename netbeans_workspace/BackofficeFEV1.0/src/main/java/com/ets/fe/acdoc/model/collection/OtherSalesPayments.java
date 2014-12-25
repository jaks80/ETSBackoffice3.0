package com.ets.fe.acdoc.model.collection;

import com.ets.fe.acdoc.model.OtherSalesPayment;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author Yusuf
 */
public class OtherSalesPayments {
    
    @XmlElement
    private List<OtherSalesPayment> list = new ArrayList<>();

    public List<OtherSalesPayment> getList() {
        return list;
    }

    public void setList(List<OtherSalesPayment> list) {
        this.list = list;
    }        
}
