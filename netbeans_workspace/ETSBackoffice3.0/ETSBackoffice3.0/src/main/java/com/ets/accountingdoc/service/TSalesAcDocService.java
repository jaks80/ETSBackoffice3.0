package com.ets.accountingdoc.service;

import com.ets.Application;
import com.ets.accountingdoc.dao.TSalesAcDocDAO;
import com.ets.accountingdoc.domain.TicketingPurchaseAcDoc;
import com.ets.accountingdoc.domain.TicketingSalesAcDoc;
import com.ets.accountingdoc.logic.TicketingAcDocBL;
import com.ets.pnr.domain.Pnr;
import com.ets.pnr.domain.Ticket;
import com.ets.pnr.service.PnrService;
import com.ets.report.model.acdoc.InvoiceReport;
import com.ets.report.model.acdoc.TktingInvoiceSummery;
import com.ets.util.DateUtil;
import com.ets.util.Enums;
import com.ets.util.Enums.AcDocType;
import com.ets.util.PnrUtil;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;
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
    @Autowired
    TPurchaseAcDocService purchase_service;

    /**
     * If there is no accounting document in Pnr it creates an invoiced.
     * Afterwards according to ticket issue sequence it creates debit memo or
     * credit memo. For re-Issued tickets it create debit memo and for refund it
     * creates credit memo.
     *
     * @param pnrid
     * @return
     */
    public synchronized TicketingSalesAcDoc newDraftDocument(Long pnrid) {

        Pnr pnr = pnrService.getByIdWithChildren(pnrid);
        List<TicketingSalesAcDoc> acdocList = dao.getByPnrId(pnrid);

        TicketingAcDocBL logic = new TicketingAcDocBL(pnr);

        TicketingSalesAcDoc invoice = null;

        for (TicketingSalesAcDoc doc : acdocList) {
            if (doc.getType().equals(AcDocType.INVOICE)) {
                invoice = doc;
                break;
            }
        }

        TicketingSalesAcDoc draftDocument = new TicketingSalesAcDoc();
        if (invoice == null) {
            invoice = new TicketingSalesAcDoc();
            Set<Ticket> uninvoicedTicket = PnrUtil.getUnInvoicedTicket(pnr);
            draftDocument = logic.newTicketingDraftInvoice(invoice, uninvoicedTicket);

        } else if (invoice.getStatus().equals(Enums.AcDocStatus.VOID)) {
            Set<Ticket> uninvoicedTicket = PnrUtil.getUnInvoicedTicket(pnr);
            draftDocument = logic.newTicketingDraftInvoice(invoice, uninvoicedTicket);
        } else {

            Set<Ticket> reIssuedTickets = PnrUtil.getUnInvoicedReIssuedTicket(pnr);
            Set<Ticket> refundedTickets = PnrUtil.getUnRefundedTickets(pnr);

            //We need only Invoice here not children
            invoice.setTickets(null);
            invoice.setRelatedDocuments(null);
            invoice.setAdditionalChargeLines(null);

            if (!reIssuedTickets.isEmpty()) {
                draftDocument = logic.newTicketingDraftDMemo(invoice, reIssuedTickets);
            } else if (!refundedTickets.isEmpty()) {
                draftDocument = logic.newTicketingDraftCMemo(invoice, refundedTickets);
            }
        }

        return draftDocument;
    }

    /**
     * Synchronize this to avoid acdoc ref duplication
     *
     * @param doc
     * @return
     */
    public synchronized TicketingSalesAcDoc newDocument(TicketingSalesAcDoc doc) {
        if (doc.getReference() == null && doc.getType().equals(Enums.AcDocType.INVOICE)) {
            if (doc.getReference() == null) {
                //There will be refference from void invoice.
                doc.setReference(AcDocUtil.generateAcDocRef(dao.getNewAcDocRef()));
            }
        }

        if (doc.getTickets() != null && !doc.getTickets().isEmpty()) {
            AcDocUtil.initTSAcDocInTickets(doc, doc.getTickets());
            PnrUtil.initPnrInTickets(doc.getPnr(), doc.getTickets());
        }

        if (doc.getAdditionalChargeLines() != null && !doc.getAdditionalChargeLines().isEmpty()) {
            AcDocUtil.initAddChgLine(doc, doc.getAdditionalChargeLines());
        }

        if (!doc.getTickets().isEmpty() || !doc.getAdditionalChargeLines().isEmpty()) {
            doc.setDocumentedAmount(doc.calculateDocumentedAmount());
        }

        TicketingPurchaseAcDoc p_doc = null;
        if (!doc.getTickets().isEmpty()) {
            p_doc = autoCreatePurchaseDocuments(doc);
        }

        doc.setStatus(Enums.AcDocStatus.ACTIVE);
        dao.save(doc);

        AcDocUtil.undefineTSAcDoc(doc, doc.getTickets());
        AcDocUtil.undefineTPAcDoc(p_doc, doc.getTickets());
        if (doc.getAdditionalChargeLines() != null && !doc.getAdditionalChargeLines().isEmpty()) {
            AcDocUtil.undefineAddChgLine(doc, doc.getAdditionalChargeLines());
        }
        return doc;
    }

    /**
     * Every time a sales document is generated, a corresponding purchase
     * document should generate automatically. Logic is document will be
     * generated automatically mirroring sales document but should be editable
     * by user except ticket fields
     *
     * @param doc
     */
    private TicketingPurchaseAcDoc autoCreatePurchaseDocuments(TicketingSalesAcDoc doc) {

        TicketingPurchaseAcDoc p_doc = new TicketingPurchaseAcDoc();

        TicketingAcDocBL logic = new TicketingAcDocBL(doc.getPnr());

        if (doc.getType().equals(Enums.AcDocType.INVOICE)) {
            p_doc = logic.newTicketingPurchaseInvoice(doc);
            purchase_service.createNewDocument(p_doc);

        } else if (doc.getType().equals(Enums.AcDocType.DEBITMEMO)) {
            TicketingPurchaseAcDoc invoice = purchase_service.findInvoiceByPnrId(doc.getPnr().getId());
            p_doc = logic.newTicketingPurchaseDMemo(doc, invoice);
            purchase_service.createNewDocument(p_doc);

        } else if (doc.getType().equals(Enums.AcDocType.CREDITMEMO)) {
            TicketingPurchaseAcDoc invoice = purchase_service.findInvoiceByPnrId(doc.getPnr().getId());
            p_doc = logic.newTicketingPurchaseCMemo(doc, invoice);
            purchase_service.createNewDocument(p_doc);
        }
        return p_doc;
    }

    public TicketingSalesAcDoc getWithChildrenById(long id) {
        TicketingSalesAcDoc doc = dao.getWithChildrenById(id);
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

    private TicketingSalesAcDoc undefineChildren(TicketingSalesAcDoc doc) {

        for (Ticket t : doc.getTickets()) {
            t.setTicketingSalesAcDoc(null);
            t.setTicketingPurchaseAcDoc(null);
        }

        Set<TicketingSalesAcDoc> relatedDocs = AcDocUtil.filterVoidRelatedDocuments(doc.getRelatedDocuments());
        for (TicketingSalesAcDoc r : relatedDocs) {
            if (r.getType().equals(Enums.AcDocType.PAYMENT) || r.getType().equals(Enums.AcDocType.REFUND)) {
                AcDocUtil.undefineTSAcDocumentInPayment(r);
            }
            AcDocUtil.undefineAddChgLine(r, r.getAdditionalChargeLines());
        }
        doc.setRelatedDocuments(relatedDocs);
        if (doc.getAdditionalChargeLines() != null && !doc.getAdditionalChargeLines().isEmpty()) {
            AcDocUtil.undefineAddChgLine(doc, doc.getAdditionalChargeLines());
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
            if (a.getType().equals(Enums.AcDocType.PAYMENT) || a.getType().equals(Enums.AcDocType.REFUND)) {
                AcDocUtil.undefineTSAcDocumentInPayment(a);
            }
            for (Ticket t : a.getTickets()) {
                t.setTicketingSalesAcDoc(null);
            }
            AcDocUtil.undefineAddChgLine(a, a.getAdditionalChargeLines());
        }
        return list;
    }

    public void delete(TicketingSalesAcDoc document) {
        dao.delete(document);
    }

    /**
     * If invoice has related documents it is not possible to void invoice while
     * it has children. Children needs to be VOID first.
     *
     * @param id
     * @return
     */
    public boolean _void(Long id) {
        TicketingSalesAcDoc doc = dao.getWithChildrenById(id);
        Set<TicketingSalesAcDoc> relatedDocs = AcDocUtil.filterVoidRelatedDocuments(doc.getRelatedDocuments());

        if (doc.getType().equals(Enums.AcDocType.INVOICE) && !relatedDocs.isEmpty()) {
            return false;
        } else {

            dao.voidDocument(undefineChildren(doc));
            return true;
        }
    }

    public InvoiceReport invoiceHistoryReport(Enums.ClientType clienttype, Long clientid, Date dateStart, Date dateEnd) {
        List<TicketingSalesAcDoc> invoice_history = dao.findInvoiceHistory(clienttype, clientid, dateStart, dateEnd);
        return TktingInvoiceSummery.serializeToSalesSummery(invoice_history);
    }

    public List<TicketingSalesAcDoc> dueInvoices(Enums.AcDocType type, Enums.ClientType clienttype, Long clientid, Date dateStart, Date dateEnd) {
        List<TicketingSalesAcDoc> dueInvoices = dao.findOutstandingDocuments(type, clienttype, clientid, dateStart, dateEnd);

        for (TicketingSalesAcDoc inv : dueInvoices) {
            for (TicketingSalesAcDoc related : inv.getRelatedDocuments()) {
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

    public InvoiceReport dueInvoiceReport(Enums.AcDocType type, Enums.ClientType clienttype, Long clientid, Date dateStart, Date dateEnd) {

        List<TicketingSalesAcDoc> dueInvoices = dueInvoices(type, clienttype, clientid, dateStart, dateEnd);
        return TktingInvoiceSummery.serializeToSalesSummery(dueInvoices);
    }

    private void validateDocumentedAmount(TicketingSalesAcDoc doc) {
        if (doc.calculateDocumentedAmount().compareTo(doc.getDocumentedAmount()) != 0) {
            doc.setDocumentedAmount(doc.calculateDocumentedAmount());
            dao.save(doc);
        }
    }
}
