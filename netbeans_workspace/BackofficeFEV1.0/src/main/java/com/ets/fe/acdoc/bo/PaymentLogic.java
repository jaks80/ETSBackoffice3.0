package com.ets.fe.acdoc.bo;

import com.ets.fe.Application;
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

    public Payment processPayment(BigDecimal amount, List<TicketingSalesAcDoc> invoices, String remark, Enums.PaymentType type) {
        if (amount.compareTo(calculateTotalInvoiceAmount(invoices)) > 0) {
            return null;
        } else {
            Payment payment = new Payment();
            payment.setRemark(remark);
            payment.setPaymentType(type);

            BigDecimal payableAmount;
            BigDecimal remainingAmount= amount;
            
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
                doc.setAccountingDocument(invoice);
                //invoice.addRelatedDocument(doc);
                payment.addTSalesPayment(doc);

                if (remainingAmount.compareTo(new BigDecimal("0.00")) <= 0) {
                    break;
                }
            }
            return payment;
        }
    }

    public BigDecimal calculateTotalInvoiceAmount(List<TicketingSalesAcDoc> invoices) {

        BigDecimal total = new BigDecimal("0.00");
        for (TicketingSalesAcDoc inv : invoices) {
            total = total.add(inv.getDocumentedAmount());
        }

        return total;
    }

}
