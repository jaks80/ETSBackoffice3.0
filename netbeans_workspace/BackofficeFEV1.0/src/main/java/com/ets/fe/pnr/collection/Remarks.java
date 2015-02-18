package com.ets.fe.pnr.collection;

import com.ets.fe.pnr.model.Remark;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Yusuf
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class Remarks {

    @XmlElement
    private List<Remark> list = new ArrayList<>();

    public List<Remark> getList() {
        return list;
    }

    public void setList(List<Remark> list) {
        this.list = list;
    }
}
