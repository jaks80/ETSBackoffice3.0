package com.ets.accountingdoc.dao;

import com.ets.GenericDAO;
import com.ets.accountingdoc.domain.TicketingPurchaseAcDoc;
import com.ets.util.Enums;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public interface TPurchaseAcDocDAO extends GenericDAO<TicketingPurchaseAcDoc, Long>{
    
    public TicketingPurchaseAcDoc getWithChildrenById(Long id);

    public List<TicketingPurchaseAcDoc> getByPnrId(Long pnrId);

    public List<TicketingPurchaseAcDoc> getByGDSPnr(String GdsPnr);
    
    public List<TicketingPurchaseAcDoc> findOutstandingDocuments(Enums.AcDocType type,Enums.ClientType clienttype,Long clientid,Date dateStart,Date dateEnd);      
    
    public List<TicketingPurchaseAcDoc> findInvoiceHistory(Enums.ClientType clienttype,Long clientid,Date dateStart,Date dateEnd);
}
