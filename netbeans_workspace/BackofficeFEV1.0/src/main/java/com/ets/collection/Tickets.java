package com.ets.collection;

import com.ets.fe.model.pnr.Ticket;
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
public class Tickets {

    private List<Ticket> list = new ArrayList<>();

    public List<Ticket> getList() {
        return list;
    }

    public void setList(List<Ticket> list) {
        this.list = list;
    }
}
