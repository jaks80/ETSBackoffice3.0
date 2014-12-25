package com.ets.accountingdoc.collection;

import com.ets.accountingdoc.domain.TicketingPurchasePayment;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author Yusuf
 */
public class TicketingPurchasePayments {
    
    @XmlElement
    private List<TicketingPurchasePayment> list = new ArrayList<>();

    public List<TicketingPurchasePayment> getList() {
        return list;
    }

    public void setList(List<TicketingPurchasePayment> list) {
        this.list = list;
    }        
}
