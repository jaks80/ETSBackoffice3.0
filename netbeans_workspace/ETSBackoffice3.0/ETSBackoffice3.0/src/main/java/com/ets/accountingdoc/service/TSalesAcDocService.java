package com.ets.accountingdoc.service;

import com.ets.accountingdoc.dao.TSalesAcDocDAO;
import com.ets.accountingdoc.domain.AccountingDocumentLine;
import com.ets.accountingdoc.domain.TicketingSalesAcDoc;
import com.ets.accountingdoc.logic.TicketingAcDocBL;
import com.ets.pnr.domain.Pnr;
import com.ets.pnr.domain.Ticket;
import com.ets.pnr.service.PnrService;
import com.ets.util.Enums;
import com.ets.util.Enums.AcDocType;
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

    public synchronized TicketingSalesAcDoc newDraftInvoice(Long pnrid) {

        Pnr pnr = pnrService.getByIdWithChildren(pnrid);
        TicketingAcDocBL logic = new TicketingAcDocBL(pnr);
        TicketingSalesAcDoc invoice = logic.createNewTicketingInvoice();
        return invoice;
    }

    public synchronized TicketingSalesAcDoc newDraftDebitMemo(Long pnrid) {

        Pnr pnr = pnrService.getByIdWithChildren(pnrid);
        List<TicketingSalesAcDoc> acdocList = dao.getByPnrId(pnrid);
        TicketingSalesAcDoc invoice=null;

        for (TicketingSalesAcDoc doc : acdocList) {
            if (doc.getType().equals(AcDocType.INVOICE)) {
                invoice = doc;
            }
        }

        TicketingAcDocBL logic = new TicketingAcDocBL(pnr);
        TicketingSalesAcDoc draftDMemo = logic.createNewTicketingDebitMemo(invoice);

        if (draftDMemo == null) {
            draftDMemo = new TicketingSalesAcDoc();
        }
        return draftDMemo;
    }

    public synchronized TicketingSalesAcDoc newDraftCreditMemo(Long pnrid) {

        Pnr pnr = pnrService.getByIdWithChildren(pnrid);
        List<TicketingSalesAcDoc> acdocList = dao.getByPnrId(pnrid);
        TicketingSalesAcDoc invoice=null;

        for (TicketingSalesAcDoc doc : acdocList) {
            if (doc.getType().equals(AcDocType.INVOICE)) {
                invoice = doc;
            }
        }

        TicketingAcDocBL logic = new TicketingAcDocBL(pnr);
        TicketingSalesAcDoc draftCMemo = logic.createNewTicketingCMemo(invoice);

        if (draftCMemo == null) {
            draftCMemo = new TicketingSalesAcDoc();
        }
        return draftCMemo;
    }

    /**
     * Synchronize this to avoid acdoc ref duplication
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
        PnrUtil.undefinePnrInSegments(doc.getPnr(), doc.getPnr().getSegments());
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
