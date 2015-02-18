package com.ets.accountingdoc.collection;

import com.ets.fe.acdoc_o.model.OtherSalesAcDoc;
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
public class OtherSalesAcDocs {
  
@XmlElement
    private List<OtherSalesAcDoc> list = new ArrayList<>();
    
    public OtherSalesAcDocs(){
    
    }
    
    public OtherSalesAcDocs(List<OtherSalesAcDoc> list){
     this.list = list;               
    }

    public List<OtherSalesAcDoc> getList() {
        return list;
    }

    public void setList(List<OtherSalesAcDoc> list) {
        this.list = list;
    }
}
