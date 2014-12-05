package com.ets.accountingdoc.service;

import com.ets.accountingdoc.dao.OtherSalesAcDocDAO;
import com.ets.accountingdoc.domain.OtherSalesAcDoc;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 *
 * @author Yusuf
 */
@Service("oSalesAcDocService")
public class OSalesAcDocService {
    
    @Resource(name = "otherSalesAcDocDAO")
    private OtherSalesAcDocDAO dao;
    
    public List<OtherSalesAcDoc> findAll(){    
        return dao.findAll(OtherSalesAcDoc.class);
    }                                                 
    
    public OtherSalesAcDoc saveorUpdate(OtherSalesAcDoc otherSalesAcDoc){
     dao.save(otherSalesAcDoc);
     return otherSalesAcDoc;
    }
    
    public void delete(OtherSalesAcDoc otherSalesAcDoc){
     dao.delete(otherSalesAcDoc);
    }
}
