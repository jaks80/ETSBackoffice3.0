package com.ets.accountingdoc.service;

import com.ets.accountingdoc.dao.TPurchaseAcDocDAO;
import com.ets.accountingdoc.domain.TicketingPurchaseAcDoc;
import com.ets.pnr.domain.Ticket;
import com.ets.accountingdoc.model.InvoiceReport;
import com.ets.util.Enums;
import com.ets.util.PnrUtil;
import java.util.Date;
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

    public synchronized TicketingPurchaseAcDoc createNewDocument(TicketingPurchaseAcDoc doc) {

        if (doc.getTickets() != null && !doc.getTickets().isEmpty()) {
            AcDocUtil.initTPAcDocInTickets(doc, doc.getTickets());
            PnrUtil.initPnrInTickets(doc.getPnr(), doc.getTickets());
        }

        if (doc.getAdditionalChargeLines() != null && !doc.getAdditionalChargeLines().isEmpty()) {
            AcDocUtil.initAddChgLine(doc, doc.getAdditionalChargeLines());
        }

        if (!doc.getTickets().isEmpty() || !doc.getAdditionalChargeLines().isEmpty()) {
            doc.setDocumentedAmount(doc.calculateDocumentedAmount());
        }

        doc.setStatus(Enums.AcDocStatus.ACTIVE);
        dao.save(doc);
        return doc;
    }

    public TicketingPurchaseAcDoc saveorUpdate(TicketingPurchaseAcDoc doc) {
        if (doc.getAdditionalChargeLines() != null && !doc.getAdditionalChargeLines().isEmpty()) {
            AcDocUtil.initAddChgLine(doc, doc.getAdditionalChargeLines());
        }

        if (!doc.getTickets().isEmpty() || !doc.getAdditionalChargeLines().isEmpty()) {
            doc.setDocumentedAmount(doc.calculateDocumentedAmount());
        }
        dao.save(doc);
        return undefineChildren(doc);
    }

    public TicketingPurchaseAcDoc getWithChildrenById(long id) {
        TicketingPurchaseAcDoc doc = dao.getWithChildrenById(id);
        validateDocumentedAmount(doc);

        if (doc.getParent() != null) {
            doc.getParent().setAdditionalChargeLines(null);
            doc.getParent().setPayment(null);
            doc.getParent().setPnr(null);
            doc.getParent().setTickets(null);
            doc.getParent().setRelatedDocuments(null);
        }
        return undefineChildren(doc);
    }

    public List<TicketingPurchaseAcDoc> getByReffference(int refNo) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void validateDocumentedAmount(TicketingPurchaseAcDoc doc) {
        if (doc.calculateDocumentedAmount().compareTo(doc.getDocumentedAmount()) != 0) {
            doc.setDocumentedAmount(doc.calculateDocumentedAmount());
            dao.save(doc);
        }
    }

    private TicketingPurchaseAcDoc undefineChildren(TicketingPurchaseAcDoc doc) {

        for (Ticket t : doc.getTickets()) {
            t.setTicketingPurchaseAcDoc(null);
        }

        for (TicketingPurchaseAcDoc r : doc.getRelatedDocuments()) {
            if (r.getType().equals(Enums.AcDocType.PAYMENT) || r.getType().equals(Enums.AcDocType.REFUND)) {
                AcDocUtil.undefineTPAcDocumentInPayment(r);
            }
            AcDocUtil.undefineAddChgLine(r, r.getAdditionalChargeLines());
        }
        //doc.setRelatedDocuments(relatedDocs);
        if (doc.getAdditionalChargeLines() != null && !doc.getAdditionalChargeLines().isEmpty()) {
            AcDocUtil.undefineAddChgLine(doc, doc.getAdditionalChargeLines());
        }

        PnrUtil.undefinePnrInSegments(doc.getPnr(), doc.getPnr().getSegments());
        doc.getPnr().setTickets(null);
        doc.getPnr().setRemarks(null);

        return doc;
    }

    public List<TicketingPurchaseAcDoc> getByGDSPnr(String pnr) {
        List<TicketingPurchaseAcDoc> list = dao.getByGDSPnr(pnr);
        return list;
    }

    public TicketingPurchaseAcDoc findInvoiceByPnrId(Long pnrId) {

        List<TicketingPurchaseAcDoc> docs = getByPnrId(pnrId);
        TicketingPurchaseAcDoc invoice = null;

        for (TicketingPurchaseAcDoc doc : docs) {
            if (doc.getType().equals(Enums.AcDocType.INVOICE)) {
                invoice = doc;
                break;
            }
        }
        return invoice;
    }

    public List<TicketingPurchaseAcDoc> getByPnrId(Long pnrId) {
        List<TicketingPurchaseAcDoc> list = dao.getByPnrId(pnrId);
        for (TicketingPurchaseAcDoc a : list) {
            a.setRelatedDocuments(null);
            if (a.getType().equals(Enums.AcDocType.PAYMENT) || a.getType().equals(Enums.AcDocType.REFUND)) {
                AcDocUtil.undefineTPAcDocumentInPayment(a);
            }
            for (Ticket t : a.getTickets()) {
                t.setTicketingPurchaseAcDoc(null);
            }
            AcDocUtil.undefineAddChgLine(a, a.getAdditionalChargeLines());
        }
        return list;
    }

    public void delete(TicketingPurchaseAcDoc document) {
        dao.delete(document);
    }

    public InvoiceReport invoiceHistoryReport(Long agentid, Date dateStart, Date dateEnd) {
        List<TicketingPurchaseAcDoc> invoice_history = dao.findInvoiceHistory(agentid, dateStart, dateEnd);

        return InvoiceReport.serializeToPurchaseSummery(agentid,invoice_history,dateStart,dateEnd);
    }

    public List<TicketingPurchaseAcDoc> dueInvoices(Enums.AcDocType type, Long agentid, Date dateStart, Date dateEnd) {

        List<TicketingPurchaseAcDoc> dueInvoices = dao.findOutstandingDocuments(type, agentid, dateStart, dateEnd);
        for (TicketingPurchaseAcDoc inv : dueInvoices) {
            for (TicketingPurchaseAcDoc related : inv.getRelatedDocuments()) {
                related.setAdditionalChargeLines(null);
                related.setPayment(null);
                related.setPnr(null);
                related.setTickets(null);
                related.setRelatedDocuments(null);
                related.setParent(null);
            }
            inv.setAdditionalChargeLines(null);
            inv.setTickets(null);
            //inv.setRelatedDocuments(null);
            inv.getPnr().setTickets(null);
            inv.getPnr().setRemarks(null);
            PnrUtil.undefinePnrInSegments(inv.getPnr(), inv.getPnr().getSegments());
            //inv.getPnr().setSegments(null);
        }
        return dueInvoices;
    }
        
    public InvoiceReport dueInvoiceReport(Enums.AcDocType type, Long agentid, Date dateStart, Date dateEnd) {

        List<TicketingPurchaseAcDoc> dueInvoices = dueInvoices(type, agentid, dateStart, dateEnd);
        return InvoiceReport.serializeToPurchaseSummery(agentid,dueInvoices,dateStart,dateEnd);
    }
}
