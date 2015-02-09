package com.ets.accountingdoc.service;

import com.ets.accountingdoc.dao.OtherSalesAcDocDAO;
import com.ets.accountingdoc.domain.OtherSalesAcDoc;
import com.ets.accountingdoc.domain.TicketingSalesAcDoc;
import com.ets.accountingdoc.model.InvoiceReport;
import com.ets.accountingdoc.model.InvoiceReportOther;
import com.ets.util.Enums;
import com.ets.util.PnrUtil;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 *
 * @author Yusuf
 */
@Service("oSalesAcDocService")
public class OSalesAcDocService {

    @Resource(name = "otherSalesAcDocDAO")
    private OtherSalesAcDocDAO dao;

    public synchronized OtherSalesAcDoc newDocument(OtherSalesAcDoc doc) {

        if (doc.getAccountingDocumentLines() == null || doc.getAccountingDocumentLines().isEmpty()) {
            return null;
        }

        if (doc.getReference() == null && doc.getType().equals(Enums.AcDocType.INVOICE)) {
            if (doc.getReference() == null) {
                doc.setReference(AcDocUtil.generateAcDocRef(dao.getNewAcDocRef()));
            }
        }

        if (doc.getAdditionalChargeLines() != null && !doc.getAdditionalChargeLines().isEmpty()) {
            AcDocUtil.initAddChgLine(doc, doc.getAdditionalChargeLines());
        }

        AcDocUtil.initAcDocInLine(doc, doc.getAccountingDocumentLines());
        doc.setStatus(Enums.AcDocStatus.ACTIVE);
        doc.setDocumentedAmount(doc.calculateDocumentedAmount());
        dao.save(doc);

        if (doc.getAdditionalChargeLines() != null && !doc.getAdditionalChargeLines().isEmpty()) {
            AcDocUtil.undefineAddChgLine(doc, doc.getAdditionalChargeLines());
        }

        if (doc.getAccountingDocumentLines() != null && !doc.getAccountingDocumentLines().isEmpty()) {
            AcDocUtil.undefineAcDocInLine(doc, doc.getAccountingDocumentLines());
        }
        return doc;
    }

    public OtherSalesAcDoc getWithChildrenById(long id) {
        OtherSalesAcDoc doc = dao.getWithChildrenById(id);
        //validateDocumentedAmount(doc);
        if (doc.getParent() != null) {
            doc.getParent().setAccountingDocumentLines(null);
            doc.getParent().setAdditionalChargeLines(null);
            doc.getParent().setPayment(null);
            doc.getParent().setRelatedDocuments(null);
        }

        return undefineChildren(doc);
    }

    public List<OtherSalesAcDoc> getByReffference(int refNo) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void delete(OtherSalesAcDoc otherSalesAcDoc) {
        dao.delete(otherSalesAcDoc);
    }

    public InvoiceReportOther invoiceHistoryReport(Enums.ClientType clienttype, Long clientid, Date dateStart, Date dateEnd) {
        List<OtherSalesAcDoc> invoice_history = dao.findInvoiceHistory(clienttype, clientid, dateStart, dateEnd);

        return InvoiceReportOther.serializeToSalesSummery(invoice_history);
    }

    public List<OtherSalesAcDoc> dueInvoices(Enums.AcDocType type, Enums.ClientType clienttype, Long clientid, Date dateStart, Date dateEnd) {
        List<OtherSalesAcDoc> dueInvoices = dao.findOutstandingDocuments(type, clienttype, clientid, dateStart, dateEnd);

        for (OtherSalesAcDoc inv : dueInvoices) {
            for (OtherSalesAcDoc related : inv.getRelatedDocuments()) {
                related.setAdditionalChargeLines(null);
                related.setPayment(null);
                related.setRelatedDocuments(null);
                related.setParent(null);
            }
            inv.setAdditionalChargeLines(null);
        }

        return dueInvoices;
    }

    public InvoiceReportOther dueInvoiceReport(Enums.AcDocType type, Enums.ClientType clienttype, Long clientid, Date dateStart, Date dateEnd) {

        List<OtherSalesAcDoc> dueInvoices = dueInvoices(type, clienttype, clientid, dateStart, dateEnd);
        return InvoiceReportOther.serializeToSalesSummery(dueInvoices);
    }

        private OtherSalesAcDoc undefineChildren(OtherSalesAcDoc doc) {

        Set<OtherSalesAcDoc> relatedDocs = AcDocUtil.filterVoidRelatedDocumentsOther(doc.getRelatedDocuments());
        
        for (OtherSalesAcDoc r : relatedDocs) {
            if (r.getType().equals(Enums.AcDocType.PAYMENT) || r.getType().equals(Enums.AcDocType.REFUND)) {
                AcDocUtil.undefineOAcDocumentInPayment(r);
            }
            AcDocUtil.undefineAddChgLine(r, r.getAdditionalChargeLines());
        }
        doc.setRelatedDocuments(relatedDocs);
        
        if (doc.getAccountingDocumentLines() != null && !doc.getAccountingDocumentLines().isEmpty()) {
            AcDocUtil.undefineAcDocInLine(doc, doc.getAccountingDocumentLines());
        }
                
        if (doc.getAdditionalChargeLines() != null && !doc.getAdditionalChargeLines().isEmpty()) {
            AcDocUtil.undefineAddChgLine(doc, doc.getAdditionalChargeLines());
        }

        return doc;
    }

}
