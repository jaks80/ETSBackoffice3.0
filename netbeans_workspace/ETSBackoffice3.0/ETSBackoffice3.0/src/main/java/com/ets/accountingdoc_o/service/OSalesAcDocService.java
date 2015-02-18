package com.ets.accountingdoc_o.service;

import com.ets.accountingdoc.domain.AccountingDocumentLine;
import com.ets.accountingdoc_o.dao.OtherSalesAcDocDAO;
import com.ets.accountingdoc.domain.OtherSalesAcDoc;
import com.ets.accountingdoc.model.InvoiceReport;
import com.ets.accountingdoc_o.model.InvoiceReportOther;
import com.ets.accountingdoc.service.AcDocUtil;
import com.ets.accountingdoc_o.model.OtherInvoiceModel;
import com.ets.util.Enums;
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
        
        if(doc.getParent()!=null){
         OtherSalesAcDoc parent = getWithChildrenById(doc.getParent().getId());
         doc.setParent(parent);
        }
        
        if (!doc.getAccountingDocumentLines().isEmpty() || !doc.getAdditionalChargeLines().isEmpty()) {
            doc.setDocumentedAmount(doc.calculateDocumentedAmount());
        }
        
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

    public boolean delete(Long id) {
        OtherSalesAcDoc doc = dao.getWithChildrenById(id);
        dao.delete(doc);
        return true;
    }

     public boolean _void(Long id) {
        OtherSalesAcDoc doc = dao.getWithChildrenById(id);
        Set<OtherSalesAcDoc> relatedDocs = doc.getRelatedDocuments();
        if (doc.getType().equals(Enums.AcDocType.INVOICE) && !relatedDocs.isEmpty()) {
            return false;
        } else {

            dao.voidDocument(undefineChildren(doc));
            return true;
        }
    }
        
    public InvoiceReportOther invoiceHistoryReport(Enums.ClientType clienttype, Long clientid, Date dateStart, Date dateEnd) {
        List<OtherSalesAcDoc> invoice_history = dao.findInvoiceHistory(clienttype, clientid, dateStart, dateEnd);
        
        InvoiceReportOther report = InvoiceReportOther.serializeToSalesSummery(clientid,invoice_history,dateStart,dateEnd);
        report.setTitle("Invoice History Report");
        return report;                
    }

    public List<OtherSalesAcDoc> dueInvoices(Enums.AcDocType type, Enums.ClientType clienttype, Long clientid, Date dateStart, Date dateEnd) {
        List<OtherSalesAcDoc> dueInvoices = dao.findOutstandingDocuments(type, clienttype, clientid, dateStart, dateEnd);

        for (OtherSalesAcDoc inv : dueInvoices) {
            for(AccountingDocumentLine l: inv.getAccountingDocumentLines()){
             l.setOtherSalesAcDoc(null);             
            }
            
            for (OtherSalesAcDoc related : inv.getRelatedDocuments()) {
                related.setAccountingDocumentLines(null);
                related.setAdditionalChargeLines(null);
                //related.setPayment(null);
                related.setRelatedDocuments(null);
                related.setParent(null);
            }
            inv.setAdditionalChargeLines(null);
        }

        return dueInvoices;
    }

    public InvoiceReportOther dueInvoiceReport(Enums.AcDocType type, Enums.ClientType clienttype, Long clientid, Date dateStart, Date dateEnd) {

        List<OtherSalesAcDoc> dueInvoices = dueInvoices(type, clienttype, clientid, dateStart, dateEnd);
        InvoiceReportOther report = InvoiceReportOther.serializeToSalesSummery(clientid,dueInvoices,dateStart,dateEnd);
        report.setTitle("Outstanding Hostory Report");
        return report;   
    }

    private OtherSalesAcDoc undefineChildren(OtherSalesAcDoc doc) {

        Set<OtherSalesAcDoc> relatedDocs = AcDocUtil.filterVoidRelatedDocumentsOther(doc.getRelatedDocuments());
        
        for (OtherSalesAcDoc r : relatedDocs) {
            if (r.getType().equals(Enums.AcDocType.PAYMENT) || r.getType().equals(Enums.AcDocType.REFUND)) {
                AcDocUtil.undefineOAcDocumentInPayment(r);
            }
            if(r.getAdditionalChargeLines()!=null){
             AcDocUtil.undefineAddChgLine(r, r.getAdditionalChargeLines());
            }
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

    public OtherInvoiceModel getModelbyId(long id) {
        OtherSalesAcDoc doc = getWithChildrenById(id);                      
        return OtherInvoiceModel.createModel(undefineChildren(doc));
    }
}
