package com.ets.accountingdoc.service;


import com.ets.accountingdoc.dao.TPurchaseAcDocDAO;
import com.ets.accountingdoc.domain.TicketingPurchaseAcDoc;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 *
 * @author Yusuf
 */
@Service("tPurchaseAcDocService")
public class TPurchaseAcDocService {
    @Resource(name = "tPurchaseAcDocDAO")
    private TPurchaseAcDocDAO dao;
    
    public List<TicketingPurchaseAcDoc> findAll(){    
        return dao.findAll(TicketingPurchaseAcDoc.class);
    }                                                 
    
    public TicketingPurchaseAcDoc getById(long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<TicketingPurchaseAcDoc> getByReffference(int refNo) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public TicketingPurchaseAcDoc saveorUpdate(TicketingPurchaseAcDoc ticketingPurchaseAcDoc){
     dao.save(ticketingPurchaseAcDoc);
     return ticketingPurchaseAcDoc;
    }
    
    public void delete(TicketingPurchaseAcDoc ticketingPurchaseAcDoc){
     dao.delete(ticketingPurchaseAcDoc);
    }
}
