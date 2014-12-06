package com.ets.accountingdoc.service;

import com.ets.accountingdoc.dao.TSalesAcDocDAO;
import com.ets.accountingdoc.domain.AccountingDocumentLine;
import com.ets.accountingdoc.domain.TicketingSalesAcDoc;
import com.ets.util.Enums;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 *
 * @author Yusuf
 */
@Service("tSalesAcDocService")
public class TSalesAcDocService {

    @Resource(name = "tSalesAcDocDAO")
    private TSalesAcDocDAO dao;

    public List<TicketingSalesAcDoc> findAll() {
        return dao.findAll(TicketingSalesAcDoc.class);
    }

    public TicketingSalesAcDoc getById(long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<TicketingSalesAcDoc> getByReffference(int refNo) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<TicketingSalesAcDoc> getByGDSPnr(String pnr) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<TicketingSalesAcDoc> getOutstandingInvoice(Long contactableId, int type, Date from, Date to) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<TicketingSalesAcDoc> invoiceHistoryByCriteria(Long contactableId,
            int contactableType, Integer docTypeFrom, Integer docTypeTo, Date from, Date to, Long tktingAgtFrom, Long tktingAgtTo) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeLine(AccountingDocumentLine l) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Synchronize this to avoind acdoc ref duplication
     * @param ticketingSalesAcDoc
     * @return 
     */
    public synchronized TicketingSalesAcDoc saveorUpdate(TicketingSalesAcDoc doc) {
        if(doc.getId() == null && 
                doc.getAcDoctype().equals(Enums.AcDocType.INVOICE)){
         doc.setAcDocRef(AcDocUtil.generateAcDocRef(dao.getMaxAcDocRef()));
        }
        
        dao.save(doc);
        return doc;
    }

    public void delete(TicketingSalesAcDoc ticketingSalesAcDoc) {
        dao.delete(ticketingSalesAcDoc);
    }
}
