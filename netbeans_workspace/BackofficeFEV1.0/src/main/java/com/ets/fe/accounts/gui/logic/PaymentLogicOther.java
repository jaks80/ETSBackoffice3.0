package com.ets.fe.accounts.gui.logic;

import com.ets.fe.Application;
import com.ets.fe.acdoc_o.model.OtherSalesAcDoc;
import com.ets.fe.accounts.model.Payment;
import com.ets.fe.util.Enums;
import java.math.BigDecimal;

/**
 *
 * @author Yusuf
 */
public class PaymentLogicOther {

    public Payment processSingleOSalesPayment(BigDecimal amount, OtherSalesAcDoc invoice, String remark, Enums.PaymentType type) {
        if (amount.compareTo(invoice.calculateDueAmount().abs()) > 0) {
            return null;
        } else {
            Payment payment = new Payment();
            payment.setRemark(remark);
            payment.setPaymentType(type);

            OtherSalesAcDoc doc = new OtherSalesAcDoc();
            doc.setReference(invoice.getReference());
            doc.setStatus(Enums.AcDocStatus.ACTIVE);
            doc.setDocIssueDate(new java.util.Date());
            doc.setCreatedBy(Application.getLoggedOnUser());
            doc.setParent(invoice);
            doc.setAgent(invoice.getAgent());
            doc.setCustomer(invoice.getCustomer());

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

            payment.addOtherDocument(doc);
            return payment;
        }
    }

}
