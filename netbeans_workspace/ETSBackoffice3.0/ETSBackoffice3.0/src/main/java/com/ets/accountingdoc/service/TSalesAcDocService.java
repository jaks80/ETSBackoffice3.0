package com.ets.accountingdoc.service;

import com.ets.Application;
import com.ets.accountingdoc.dao.AdditionalChgLineDAO;
import com.ets.accountingdoc.dao.TSalesAcDocDAO;
import com.ets.accountingdoc.domain.AdditionalChargeLine;
import com.ets.accountingdoc.domain.TicketingPurchaseAcDoc;
import com.ets.accountingdoc.domain.TicketingSalesAcDoc;
import com.ets.accountingdoc.logic.TicketingAcDocBL;
import com.ets.pnr.dao.TicketDAO;
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
import java.util.ArrayList;
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
    private TicketDAO ticketDAO;
    @Autowired
    TPurchaseAcDocService purchase_service;
    @Autowired
    private AdditionalChgLineDAO additionalChgLineDAO;

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
            //invoice.setReference(null);
            //invoice.setStatus(Enums.AcDocStatus.ACTIVE);
            Set<Ticket> uninvoicedTicket = PnrUtil.getUnInvoicedTicket(pnr);
            draftDocument = logic.newTicketingDraftInvoice(invoice, uninvoicedTicket);
        } else {

            Set<Ticket> reIssuedTickets = PnrUtil.getUnInvoicedReIssuedTicket(pnr);
            Set<Ticket> refundedTickets = PnrUtil.getUnRefundedTickets(pnr);

            invoice.setTickets(null);
            invoice.setRelatedDocuments(null);

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
        doc.setStatus(Enums.AcDocStatus.ACTIVE);               
        dao.save(doc);

        if(!doc.getTickets().isEmpty()){
         autoCreatePurchaseDocuments(doc);
        }
                
        AcDocUtil.undefineTSAcDoc(doc, doc.getTickets());
        if (doc.getAdditionalChargeLines() != null && !doc.getAdditionalChargeLines().isEmpty()) {
            AcDocUtil.undefineAddChgLine(doc, doc.getAdditionalChargeLines());
        }
        return doc;
    }

    /**
     * Every time a sales document is generated, a corresponding purchase document
     * should generate automatically. Logic is document will be generated automatically mirroring sales document
     * but should be editable by user except ticket fields
     * @param doc 
     */
    private void autoCreatePurchaseDocuments(TicketingSalesAcDoc doc) {

        TicketingPurchaseAcDoc p_doc = new TicketingPurchaseAcDoc();

        TicketingAcDocBL logic = new TicketingAcDocBL(doc.getPnr());

        if (doc.getType().equals(Enums.AcDocType.INVOICE)) {
            p_doc = logic.newTicketingPurchaseInvoice(doc);
            purchase_service.newDocument(p_doc);

        } else if (doc.getType().equals(Enums.AcDocType.DEBITMEMO)) {
            TicketingPurchaseAcDoc invoice = purchase_service.findInvoiceByPnrId(doc.getPnr().getId());
            p_doc = logic.newTicketingPurchaseDMemo(doc, invoice);
            purchase_service.newDocument(p_doc);

        } else if (doc.getType().equals(Enums.AcDocType.CREDITMEMO)) {
            TicketingPurchaseAcDoc invoice = purchase_service.findInvoiceByPnrId(doc.getPnr().getId());
            p_doc = logic.newTicketingPurchaseCMemo(doc, invoice);
            purchase_service.newDocument(p_doc);
        }
    }

    public TicketingSalesAcDoc getWithChildrenById(long id) {
        TicketingSalesAcDoc doc = dao.getWithChildrenById(id);
        validateDocumentedAmount(doc);
        return undefineChildren(doc);
    }

    private TicketingSalesAcDoc undefineChildren(TicketingSalesAcDoc doc) {

        for (Ticket t : doc.getTickets()) {
            t.setTicketingSalesAcDoc(null);
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
            Set<Ticket> tickets = doc.getTickets();
            for (Ticket t : tickets) {
                t.setTicketingSalesAcDoc(null);
            }

            if (!tickets.isEmpty()) {
                ticketDAO.saveBulk(new ArrayList(tickets));
            }

            Set<AdditionalChargeLine> additionalChargeLines = doc.getAdditionalChargeLines();

            if (!additionalChargeLines.isEmpty()) {
                additionalChgLineDAO.deleteBulk(additionalChargeLines);
                doc.setAdditionalChargeLines(null);
            }
            doc.setStatus(Enums.AcDocStatus.VOID);
            dao.save(doc);
            return true;
        }
    }

    public InvoiceReport invoiceHistoryReport(Enums.ClientType clienttype, Long clientid, Date dateStart, Date dateEnd) {
        List<TicketingSalesAcDoc> invoice_history = dao.findInvoiceHistory(clienttype, clientid, dateStart, dateEnd);
        return objectToReport(invoice_history);
    }

    public InvoiceReport dueInvoiceReport(Enums.AcDocType type, Enums.ClientType clienttype, Long clientid, Date dateStart, Date dateEnd) {

        List<TicketingSalesAcDoc> dueInvoices = dao.findOutstandingDocuments(type, clienttype, clientid, dateStart, dateEnd);
        return objectToReport(dueInvoices);
    }

    private InvoiceReport objectToReport(List<TicketingSalesAcDoc> invoices) {
        BigDecimal totalInvAmount = new BigDecimal("0.00");
        BigDecimal totalDMAmount = new BigDecimal("0.00");
        BigDecimal totalCMAmount = new BigDecimal("0.00");
        BigDecimal totalDue = new BigDecimal("0.00");
        BigDecimal totalPayment = new BigDecimal("0.00");
        BigDecimal totalRefund = new BigDecimal("0.00");

        InvoiceReport report = new InvoiceReport();
        for (TicketingSalesAcDoc invoice : invoices) {

            Set<TicketingSalesAcDoc> relatedDocs = AcDocUtil.filterVoidRelatedDocuments(invoice.getRelatedDocuments());
            invoice.setRelatedDocuments(relatedDocs);

            TktingInvoiceSummery invSummery = new TktingInvoiceSummery();

            invSummery.setId(invoice.getId());
            invSummery.setDocIssueDate(DateUtil.dateToString(invoice.getDocIssueDate()));
            invSummery.setGdsPnr(invoice.getPnr().getGdsPnr());
            invSummery.setNoOfPax(invoice.getPnr().getNoOfPax());
            invSummery.setReference(invoice.getReference());
            invSummery.setStatus(invoice.getStatus());
            invSummery.setType(invoice.getType());
            invSummery.setOutBoundDetails(PnrUtil.getOutBoundFlightSummery(invoice.getPnr().getSegments()));
            invSummery.setDocumentedAmount(invoice.getDocumentedAmount());
            invSummery.setOtherAmount(invoice.calculateTotalDebitMemo().add(invoice.calculateTotalCreditMemo()));
            invSummery.setPayment(invoice.calculateTotalPayment().add(invoice.calculateTotalRefund()));
            invSummery.setDue(invoice.calculateDueAmount());

            totalInvAmount = totalInvAmount.add(invoice.getDocumentedAmount());
            totalDMAmount = totalDMAmount.add(invoice.calculateTotalDebitMemo());
            totalCMAmount = totalCMAmount.add(invoice.calculateTotalCreditMemo());
            totalPayment = totalPayment.add(invoice.calculateTotalPayment());
            totalRefund = totalRefund.add(invoice.calculateTotalRefund());
            totalDue = totalDue.add(invoice.calculateDueAmount());

            report.addInvoice(invSummery);
        }
        String currency = Application.currency();
        report.setTotalInvAmount(currency + totalInvAmount.toString());
        report.setTotalCMAmount(currency + totalCMAmount.toString());
        report.setTotalDMAmount(currency + totalDMAmount.toString());
        report.setTotalDue(currency + totalDue.toString());
        report.setTotalPayment(currency + totalPayment.toString());
        report.setTotalRefund(currency + totalRefund.toString());

        return report;
    }

    private void validateDocumentedAmount(TicketingSalesAcDoc doc) {
        if (doc.calculateDocumentedAmount().compareTo(doc.getDocumentedAmount()) != 0) {
            doc.setDocumentedAmount(doc.calculateDocumentedAmount());
            dao.save(doc);
        }
    }
}
