package com.ets.accountingdoc.dao;

import com.ets.GenericDAO;
import com.ets.accountingdoc.domain.TicketingPurchaseAcDoc;
import com.ets.util.Enums;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public interface TPurchaseAcDocDAO extends GenericDAO<TicketingPurchaseAcDoc, Long>{
    
    public TicketingPurchaseAcDoc getWithChildrenById(Long id);

    public List<TicketingPurchaseAcDoc> getByPnrId(Long pnrId);

    public boolean voidDocument(TicketingPurchaseAcDoc doc);

    public TicketingPurchaseAcDoc getByTicketId(Long ticketId);

    public List<TicketingPurchaseAcDoc> getByGDSPnr(String GdsPnr);

    public List<TicketingPurchaseAcDoc> findOutstandingInvoice(Enums.AcDocType type,Long agentid,Date dateStart,Date dateEnd);
    
    public List<TicketingPurchaseAcDoc> findOutstandingBSPInvoice(Long agentid,Date dateStart,Date dateEnd);
    
    public List<TicketingPurchaseAcDoc> findBSP_ADM_ACM(Long agentid, Date from, Date to);

    public List<TicketingPurchaseAcDoc> findInvoiceHistory(Long agentid,Date dateStart,Date dateEnd);

    public List<TicketingPurchaseAcDoc> findAllDocuments(Long agentid,Date dateStart,Date dateEnd);

    public BigDecimal getAccountBallanceToDate(Long agentid,Date dateEnd);
}
