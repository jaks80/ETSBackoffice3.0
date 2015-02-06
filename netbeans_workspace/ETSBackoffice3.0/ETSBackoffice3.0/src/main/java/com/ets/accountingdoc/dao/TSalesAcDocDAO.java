package com.ets.accountingdoc.dao;

import com.ets.GenericDAO;
import com.ets.accountingdoc.domain.TicketingSalesAcDoc;
import com.ets.util.Enums;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public interface TSalesAcDocDAO extends GenericDAO<TicketingSalesAcDoc, Long> {

    public Long getNewAcDocRef();        

    public TicketingSalesAcDoc getWithChildrenById(Long id);

    public List<TicketingSalesAcDoc> getByPnrId(Long pnrId);

    public List<TicketingSalesAcDoc> getByGDSPnr(String GdsPnr);
    
    public boolean voidDocument(TicketingSalesAcDoc doc);
    
    public List<TicketingSalesAcDoc> findOutstandingDocuments(Enums.AcDocType type,Enums.ClientType clienttype,Long clientid,Date dateStart,Date dateEnd);      
    
    public List<TicketingSalesAcDoc> findInvoiceHistory(Enums.ClientType clienttype,Long clientid,Date dateStart,Date dateEnd);
    
    public List<TicketingSalesAcDoc> findAllDocuments(Enums.ClientType clienttype,Long clientid,Date dateStart,Date dateEnd);
        
    public BigDecimal getAccountBallanceToDate(Enums.ClientType clienttype,Long clientid,Date dateEnd);
    
    
}
