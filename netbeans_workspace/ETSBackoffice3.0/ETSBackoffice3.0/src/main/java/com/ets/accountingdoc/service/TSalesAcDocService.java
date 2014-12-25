package com.ets.accountingdoc.service;

import com.ets.accountingdoc.dao.TSalesAcDocDAO;
import com.ets.accountingdoc.domain.AccountingDocumentLine;
import com.ets.accountingdoc.domain.TicketingSalesAcDoc;
import com.ets.accountingdoc.logic.TicketingAcDocBL;
import com.ets.pnr.domain.Pnr;
import com.ets.pnr.domain.Ticket;
import com.ets.pnr.service.PnrService;
import com.ets.util.Enums;
import com.ets.util.PnrUtil;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Yusuf
 */
@Service("tSalesAcDocService")
public class TSalesAcDocService {
    
    @Resource(name = "tSalesAcDocDAO")
    private TSalesAcDocDAO dao;
    
    @Autowired
    private PnrService pnrService;
    
    public synchronized TicketingSalesAcDoc createNewDraftInvoice(Long id) {
        
        Pnr pnr = pnrService.getByIdWithChildren(id);
        TicketingAcDocBL logic = new TicketingAcDocBL(pnr);
        TicketingSalesAcDoc invoice = logic.createNewTicketingInvoice();
        return invoice;
    }

    /**
     * Synchronize this to avoind acdoc ref duplication
     *
     * @param doc     
     * @return
     */
    public synchronized TicketingSalesAcDoc newDocument(TicketingSalesAcDoc doc) {
        if (doc.getId() == null
                && doc.getType().equals(Enums.AcDocType.INVOICE)) {
            doc.setReference(AcDocUtil.generateAcDocRef(dao.getNewAcDocRef()));
        }
        
        AcDocUtil.initTSAcDocInTickets(doc, doc.getTickets());
        PnrUtil.initPnrInTickets(doc.getPnr(), doc.getTickets());
        doc.setDocumentedAmount(doc.calculateDocumentedAmount());
        dao.save(doc);
        AcDocUtil.undefineTSAcDoc(doc, doc.getTickets());
        return doc;
    }
    
    public List<TicketingSalesAcDoc> findAll() {
        return dao.findAll(TicketingSalesAcDoc.class);
    }
    
    public TicketingSalesAcDoc getWithChildrenById(long id) {
        TicketingSalesAcDoc doc = dao.getWithChildrenById(id);
        for (Ticket t : doc.getTickets()) {
            t.setTicketingSalesAcDoc(null);
        }
        doc.getPnr().setTickets(null);
        doc.getPnr().setRemarks(null);
        return doc;
    }
    
    public List<TicketingSalesAcDoc> getByReffference(int refNo) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public List<TicketingSalesAcDoc> getByGDSPnr(String pnr) {
        List<TicketingSalesAcDoc> list = dao.getByGDSPnr(pnr);
        
        return list;
    }
    
    public List<TicketingSalesAcDoc> getByPnrId(Long pnrId) {
        List<TicketingSalesAcDoc> list = dao.getByPnrId(pnrId);
        for (TicketingSalesAcDoc a : list) {
            a.setRelatedDocuments(null);
            for (Ticket t : a.getTickets()) {
                t.setTicketingSalesAcDoc(null);
            }
        }
        return list;
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
    
    public void delete(TicketingSalesAcDoc ticketingSalesAcDoc) {
        dao.delete(ticketingSalesAcDoc);
    }
}
