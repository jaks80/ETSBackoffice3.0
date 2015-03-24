package com.ets.fe.accounts.gui.logic;

import com.ets.fe.Application;
import com.ets.fe.acdoc_o.model.OtherSalesAcDoc;
import com.ets.fe.accounts.model.Payment;
import com.ets.fe.acdoc.model.TicketingPurchaseAcDoc;
import com.ets.fe.acdoc.model.TicketingSalesAcDoc;
import com.ets.fe.acdoc.model.report.TktingInvoiceSummery;
import com.ets.fe.util.Enums;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public class PaymentLogic {

    /**
     * If invoice amount is greater then 0 then It will be payment. If invoice
     * amount is less then 0 the It will be a refund. If invoice amount is 0
     * then payment will inactive
     *
     * @param amount
     * @param invoice
     * @param remark
     * @param type
     * @return
     */
    public Payment processSingleTSalesPayment(BigDecimal amount, TicketingSalesAcDoc invoice, String remark, Enums.PaymentType type) {
        if (amount.compareTo(invoice.calculateDueAmount().abs()) > 0) {
            return null;
        } else {
            Payment payment = new Payment();
            payment.setRemark(remark);
            payment.setPaymentType(type);

            TicketingSalesAcDoc doc = new TicketingSalesAcDoc();
            doc.setReference(invoice.getReference());
            doc.setStatus(Enums.AcDocStatus.ACTIVE);
            doc.setDocIssueDate(new java.util.Date());
            doc.setPnr(invoice.getPnr());
            doc.setCreatedBy(Application.getLoggedOnUser());
            doc.setParent(invoice);

            if (invoice.calculateDueAmount().compareTo(new BigDecimal("0.00")) == 1) {
                //Make payment   
                doc.setType(Enums.AcDocType.PAYMENT);
                doc.setDocumentedAmount(amount.negate());//Payment saves as negative
            } else if (invoice.calculateDueAmount().compareTo(new BigDecimal("0.00")) == -1) {
                //Make refund
                doc.setType(Enums.AcDocType.REFUND);
                doc.setDocumentedAmount(amount);
            } else {
                //Do nothing  
                return null;
            }
            //invoice.addRelatedDocument(doc);
            payment.addTSalesDocument(doc);

            return payment;
        }
    }

    public Payment processSingleTPurchasePayment(BigDecimal amount, TicketingPurchaseAcDoc invoice, String remark, Enums.PaymentType type) {
        if (amount.compareTo(invoice.calculateDueAmount().abs()) > 0) {
            return null;
        } else {
            Payment payment = new Payment();
            payment.setRemark(remark);
            payment.setPaymentType(type);

            TicketingPurchaseAcDoc doc = new TicketingPurchaseAcDoc();
            doc.setReference(invoice.getReference());
            doc.setStatus(Enums.AcDocStatus.ACTIVE);
            doc.setDocIssueDate(new java.util.Date());
            doc.setPnr(invoice.getPnr());
            doc.setCreatedBy(Application.getLoggedOnUser());
            doc.setParent(invoice);

            if (invoice.calculateDueAmount().compareTo(new BigDecimal("0.00")) == 1) {
                //Make payment   
                doc.setType(Enums.AcDocType.PAYMENT);
                doc.setDocumentedAmount(amount.negate());//Payment saves as negative
            } else if (invoice.calculateDueAmount().compareTo(new BigDecimal("0.00")) == -1) {
                //Make refund
                doc.setType(Enums.AcDocType.REFUND);
                doc.setDocumentedAmount(amount);
            } else {
                //Do nothing  
                return null;
            }
            
            payment.addTPurchaseDocument(doc);            
            return payment;
        }
    }
        
    public BigDecimal calculateTotalDueAmount(List<TicketingSalesAcDoc> invoices) {

        BigDecimal total = new BigDecimal("0.00");
        for (TicketingSalesAcDoc inv : invoices) {
            total = total.add(inv.calculateDueAmount());
        }

        return total;
    }
    
    public BigDecimal calculateTotalDueAmountOther(List<OtherSalesAcDoc> invoices) {

        BigDecimal total = new BigDecimal("0.00");
        for (OtherSalesAcDoc inv : invoices) {
            total = total.add(inv.calculateDueAmount());
        }

        return total;
    }
  
    public BigDecimal calculateTotalPDueAmount(List<TicketingPurchaseAcDoc> invoices) {

        BigDecimal total = new BigDecimal("0.00");
        for (TicketingPurchaseAcDoc inv : invoices) {
            total = total.add(inv.calculateDueAmount());
        }

        return total;
    }
        
    public BigDecimal calcDueAmountFromSummery(List<TktingInvoiceSummery> invoices) {

        BigDecimal total = new BigDecimal("0.00");
        for (TktingInvoiceSummery inv : invoices) {
            total = total.add(inv.getDue());
        }

        return total;
    }
}
