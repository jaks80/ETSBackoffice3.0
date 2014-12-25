package com.ets.accountingdoc.collection;

import com.ets.accountingdoc.domain.OtherSalesPayment;
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
