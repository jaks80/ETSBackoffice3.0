package com.ets.fe.acdoc.bo;

import com.ets.fe.Application;
import com.ets.fe.acdoc.model.AccountingDocument;
import com.ets.fe.acdoc.model.OtherSalesAcDoc;
import com.ets.fe.acdoc.model.Payment;
import com.ets.fe.acdoc.model.TicketingPurchaseAcDoc;
import com.ets.fe.acdoc.model.TicketingSalesAcDoc;
import com.ets.fe.acdoc.model.report.TktingInvoiceSummery;
import com.ets.fe.pnr.model.Pnr;
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
    
    /**
     * @param amount
     * @param invoices
     * @param remark
     * @param type
     * @return
     */
    public Payment processBulkPayment(BigDecimal amount, List<TktingInvoiceSummery> invoices, String remark, Enums.PaymentType type) {
        if (amount.compareTo(calcDueAmountFromSummery(invoices)) > 0) {
            return null;
        } else {
            Payment payment = new Payment();
            payment.setRemark(remark);
            payment.setPaymentType(type);

            BigDecimal payableAmount;
            BigDecimal remainingAmount = amount;

            for (TktingInvoiceSummery invoice : invoices) {

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
                Pnr pnr = new Pnr();
                pnr.setId(invoice.getPnr_id());
                pnr.setGdsPnr(invoice.getGdsPnr());
                doc.setPnr(pnr);
                doc.setCreatedBy(Application.getLoggedOnUser());
                doc.setDocumentedAmount(payableAmount.negate());//Payment saves as negative
                TicketingSalesAcDoc parent = new TicketingSalesAcDoc();
                parent.setId(invoice.getId());
                doc.setParent(parent);
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
