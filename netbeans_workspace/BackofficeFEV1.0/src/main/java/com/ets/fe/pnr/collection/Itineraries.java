package com.ets.fe.pnr.collection;

import com.ets.fe.pnr.model.Itinerary;
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
public class Itineraries {

    private List<Itinerary> list = new ArrayList<>();

    public List<Itinerary> getList() {
        return list;
    }

    public void setList(List<Itinerary> list) {
        this.list = list;
    }
}
