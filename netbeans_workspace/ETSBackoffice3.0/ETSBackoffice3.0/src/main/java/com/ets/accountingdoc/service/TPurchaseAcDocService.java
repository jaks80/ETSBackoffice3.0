package com.ets.accountingdoc.service;

import com.ets.Application;
import com.ets.accountingdoc.dao.TPurchaseAcDocDAO;
import com.ets.accountingdoc.domain.TicketingPurchaseAcDoc;
import com.ets.accountingdoc.domain.TicketingSalesAcDoc;
import com.ets.pnr.domain.Ticket;
import com.ets.report.model.acdoc.InvoiceReport;
import com.ets.report.model.acdoc.TktingInvoiceSummery;
import com.ets.util.DateUtil;
import com.ets.util.Enums;
import com.ets.util.PnrUtil;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;
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

    public synchronized TicketingPurchaseAcDoc newDocument(TicketingPurchaseAcDoc doc) {

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

        AcDocUtil.undefineTPAcDoc(doc, doc.getTickets());
        if (doc.getAdditionalChargeLines() != null && !doc.getAdditionalChargeLines().isEmpty()) {
            AcDocUtil.undefineAddChgLine(doc, doc.getAdditionalChargeLines());
        }
        return doc;
    }

    public TicketingPurchaseAcDoc saveorUpdate(TicketingPurchaseAcDoc ticketingPurchaseAcDoc) {
        dao.save(ticketingPurchaseAcDoc);
        return ticketingPurchaseAcDoc;
    }

    public TicketingPurchaseAcDoc getWithChildrenById(long id) {
        TicketingPurchaseAcDoc doc = dao.getWithChildrenById(id);
        validateDocumentedAmount(doc);
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
        return objectToReport(invoice_history);
    }

    public InvoiceReport dueInvoiceReport(Enums.AcDocType type, Long agentid, Date dateStart, Date dateEnd) {

        List<TicketingPurchaseAcDoc> dueInvoices = dao.findOutstandingDocuments(type, agentid, dateStart, dateEnd);
        return objectToReport(dueInvoices);
    }

    private InvoiceReport objectToReport(List<TicketingPurchaseAcDoc> invoices) {
        BigDecimal totalInvAmount = new BigDecimal("0.00");
        BigDecimal totalDMAmount = new BigDecimal("0.00");
        BigDecimal totalCMAmount = new BigDecimal("0.00");
        BigDecimal totalDue = new BigDecimal("0.00");
        BigDecimal totalPayment = new BigDecimal("0.00");
        BigDecimal totalRefund = new BigDecimal("0.00");

        InvoiceReport report = new InvoiceReport();
        for (TicketingPurchaseAcDoc invoice : invoices) {

            Set<TicketingPurchaseAcDoc> relatedDocs = AcDocUtil.filterPVoidRelatedDocuments(invoice.getRelatedDocuments());
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

}
