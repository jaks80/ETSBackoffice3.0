package com.ets.fe.acdoc.model.collection;

import com.ets.fe.acdoc.model.TicketingSalesPayment;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author Yusuf
 */
public class TicketingSalesPayments {
    
    @XmlElement
    private List<TicketingSalesPayment> list = new ArrayList<>();

    public List<TicketingSalesPayment> getList() {
        return list;
    }

    public void setList(List<TicketingSalesPayment> list) {
        this.list = list;
    }        
}
