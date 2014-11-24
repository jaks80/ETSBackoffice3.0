package com.ets.fe.pnr.collection;

import com.ets.fe.pnr.model.PnrRemark;
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
public class PnrRemarks {

    private List<PnrRemark> list = new ArrayList<>();

    public List<PnrRemark> getList() {
        return list;
    }

    public void setList(List<PnrRemark> list) {
        this.list = list;
    }
}
