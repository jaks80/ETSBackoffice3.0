package com.ets.pnr.model.collection;

import com.ets.pnr.domain.PnrRemark;
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
