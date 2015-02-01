package com.ets.fe.acdoc.bo;

import com.ets.fe.Application;
import com.ets.fe.acdoc.model.AccountingDocument;
import com.ets.fe.acdoc.model.Payment;
import com.ets.fe.acdoc.model.TicketingSalesAcDoc;
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
    public Payment processSinglePayment(BigDecimal amount, AccountingDocument invoice, String remark, Enums.PaymentType type) {
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

    /**
     * @param amount
     * @param invoices
     * @param remark
     * @param type
     * @return
     */
    public Payment processBulkPayment(BigDecimal amount, List<TicketingSalesAcDoc> invoices, String remark, Enums.PaymentType type) {
        if (amount.compareTo(calculateTotalDueAmount(invoices)) > 0) {
            return null;
        } else {
            Payment payment = new Payment();
            payment.setRemark(remark);
            payment.setPaymentType(type);

            BigDecimal payableAmount;
            BigDecimal remainingAmount = amount;

            for (TicketingSalesAcDoc invoice : invoices) {

                if (remainingAmount.compareTo(invoice.getDocumentedAmount()) <= 0) {
                    payableAmount = remainingAmount;
                    remainingAmount = new BigDecimal("0.00");
                } else {
                    payableAmount = invoice.getDocumentedAmount();
                    remainingAmount = remainingAmount.subtract(payableAmount);
                }

                TicketingSalesAcDoc doc = new TicketingSalesAcDoc();
                doc.setReference(invoice.getReference());
                doc.setType(Enums.AcDocType.PAYMENT);
                doc.setStatus(Enums.AcDocStatus.ACTIVE);
                doc.setDocIssueDate(new java.util.Date());
                doc.setPnr(invoice.getPnr());
                doc.setCreatedBy(Application.getLoggedOnUser());
                doc.setDocumentedAmount(payableAmount.negate());//Payment saves as negative
                doc.setParent(invoice);
                //invoice.addRelatedDocument(doc);
                payment.addTSalesDocument(doc);

                if (remainingAmount.compareTo(new BigDecimal("0.00")) <= 0) {
                    break;
                }
            }
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

}
