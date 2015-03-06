package com.ets.accounts.service;

import com.ets.accounts.model.Payments;
import com.ets.accounts.dao.PaymentDAO;
import com.ets.accountingdoc.domain.OtherSalesAcDoc;
import com.ets.accounts.domain.Payment;
import com.ets.accountingdoc.domain.TicketingPurchaseAcDoc;
import com.ets.accountingdoc.domain.TicketingSalesAcDoc;
import com.ets.accounts.model.CashBookReport;
import com.ets.util.Enums;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 *
 * @author Yusuf
 */
@Service("paymentService")
public class PaymentService {

    @Resource(name = "paymentDAO")
    private PaymentDAO dao;

    public Payment save(Payment payment) {
        Set<TicketingSalesAcDoc> sdocs = payment.gettSalesAcDocuments();
        Set<TicketingPurchaseAcDoc> pdocs = payment.gettPurchaseAcDocuments();
        Set<OtherSalesAcDoc> odocs = payment.getoSalesAcDocuments();

        for (TicketingSalesAcDoc d : sdocs) {
            d.setPayment(payment);
        }

        for (TicketingPurchaseAcDoc d : pdocs) {
            d.setPayment(payment);
        }

        for (OtherSalesAcDoc d : odocs) {
            d.setPayment(payment);
        }

        dao.save(payment);

        sdocs = payment.gettSalesAcDocuments();
        pdocs = payment.gettPurchaseAcDocuments();
        odocs = payment.getoSalesAcDocuments();

        for (TicketingSalesAcDoc d : sdocs) {
            d.setPayment(null);
        }

        for (TicketingPurchaseAcDoc d : pdocs) {
            d.setPayment(null);
        }

        for (OtherSalesAcDoc d : odocs) {
            d.setPayment(null);
        }
        return payment;
    }

    public Payment findById(Long id) {
        Payment payment = dao.findById(id);

        Set<TicketingSalesAcDoc> paydocs = payment.gettSalesAcDocuments();
        for (TicketingSalesAcDoc d : paydocs) {
            d.setTickets(null);
            d.setRelatedDocuments(null);
            d.setAdditionalChargeLines(null);
            //d.getParent().setTickets(null);
            //d.getParent().setRelatedDocuments(null);
            //d.getParent().setAccountingDocumentLines(null);
        }
        payment.settPurchaseAcDocuments(null);
        payment.setoSalesAcDocuments(null);

        return payment;
    }

    public Payments findPaymentBySalesInvoice(Long invoice_id) {
        List<Payment> list = dao.findPaymentBySalesInvoice(invoice_id);
        for (Payment p : list) {
            Set<TicketingSalesAcDoc> paydocs = p.gettSalesAcDocuments();
            for (TicketingSalesAcDoc d : paydocs) {
                d.setTickets(null);
                d.setRelatedDocuments(null);
                d.setAdditionalChargeLines(null);
                d.getParent().setTickets(null);
                d.getParent().setRelatedDocuments(null);
                d.getParent().setAdditionalChargeLines(null);
            }
            p.settPurchaseAcDocuments(null);
            p.setoSalesAcDocuments(null);
        }
        Payments payments = new Payments();
        payments.setList(list);
        return payments;
    }

    public synchronized List<Payment> findTicketingPaymentHistory(Enums.ClientType clienttype, Long clientid, Date from, Date to, Enums.SaleType saleType) {
        List<Payment> payment_list = dao.findTicketingPaymentHistory(clienttype, clientid, from, to, saleType);

        for (Payment pay : payment_list) {

            if (Enums.SaleType.TKTSALES.equals(saleType)) {
                pay.setoSalesAcDocuments(null);
                pay.settPurchaseAcDocuments(null);
                for (TicketingSalesAcDoc doc : pay.gettSalesAcDocuments()) {
                    doc.setAdditionalChargeLines(null);
                    doc.setPayment(null);
                    doc.setTickets(null);
                    doc.setRelatedDocuments(null);
                    doc.getPnr().setTickets(null);
                    doc.getPnr().setRemarks(null);
                    doc.getPnr().setSegments(null);

                    if (doc.getParent() != null) {
                        doc.getParent().setAdditionalChargeLines(null);
                        doc.getParent().setPnr(null);
                        doc.getParent().setTickets(null);
                        doc.getParent().setRelatedDocuments(null);
                        doc.getParent().setPayment(null);
                        doc.getParent().setParent(null);
                    }
                }
            } else if (Enums.SaleType.TKTPURCHASE.equals(saleType)) {
                pay.setoSalesAcDocuments(null);
                pay.settSalesAcDocuments(null);
                for (TicketingPurchaseAcDoc doc : pay.gettPurchaseAcDocuments()) {
                    doc.setAdditionalChargeLines(null);
                    doc.setPayment(null);
                    doc.setTickets(null);
                    doc.setRelatedDocuments(null);
                    doc.getPnr().setTickets(null);
                    doc.getPnr().setRemarks(null);
                    doc.getPnr().setSegments(null);

                    if (doc.getParent() != null) {
                        doc.getParent().setAdditionalChargeLines(null);
                        doc.getParent().setPnr(null);
                        doc.getParent().setTickets(null);
                        doc.getParent().setRelatedDocuments(null);
                        doc.getParent().setPayment(null);
                        doc.getParent().setParent(null);
                    }
                }
            }
        }
        return payment_list;
    }

    public synchronized List<Payment> findOtherPaymentHistory(Enums.ClientType clienttype, Long clientid, Date from, Date to, Enums.SaleType saleType) {
        List<Payment> payment_list = dao.findOtherPaymentHistory(clienttype, clientid, from, to, saleType);

        for (Payment pay : payment_list) {
            pay.settSalesAcDocuments(null);
            pay.settPurchaseAcDocuments(null);
            for (OtherSalesAcDoc doc : pay.getoSalesAcDocuments()) {
                doc.setAdditionalChargeLines(null);
                doc.setAccountingDocumentLines(null);
                doc.setPayment(null);
                doc.setRelatedDocuments(null);

                doc.getParent().setAccountingDocumentLines(null);
                doc.getParent().setAdditionalChargeLines(null);
                doc.getParent().setRelatedDocuments(null);
                doc.getParent().setPayment(null);
                doc.getParent().setParent(null);
            }
        }
        return payment_list;
    }

    public List<Payment> findTicketingSalesCashBook(Date from, Date to, Long userId,
            Enums.ClientType clienttype, Long clientid, Enums.PaymentType paymentType) {
        List<Payment> payment_list = dao.findTicketingSalesCashBook(from, to, userId, clienttype, clientid, paymentType);

        for (Payment pay : payment_list) {
            pay.settPurchaseAcDocuments(null);
            pay.setoSalesAcDocuments(null);
        }

        return payment_list;
    }

    public List<Payment> findTicketingPurchaseCashBook(Date from, Date to, Long userId,
            Enums.ClientType clienttype, Long clientid, Enums.PaymentType paymentType) {
        List<Payment> payment_list = dao.findTicketingPurchaseCashBook(from, to, userId, clienttype, clientid, paymentType);

        for (Payment pay : payment_list) {
            pay.settSalesAcDocuments(null);
            pay.setoSalesAcDocuments(null);
        }

        return payment_list;
    }

    public List<Payment> findOtherSalesCashBook(Date from, Date to, Long userId,
            Enums.ClientType clienttype, Long clientid, Enums.PaymentType paymentType) {
        List<Payment> payment_list = dao.findOtherSalesCashBook(from, to, userId, clienttype, clientid, paymentType);

        for (Payment pay : payment_list) {
            pay.settPurchaseAcDocuments(null);
            pay.settSalesAcDocuments(null);
        }

        return payment_list;
    }

    public CashBookReport findCashBookReport(Date from, Date to, Long userid, Enums.ClientType clienttype,
            Long clientid, Enums.SaleType saleType, Enums.PaymentType paymentType) {
        List<Payment> payment_list = new ArrayList<>();

        if (saleType.equals(Enums.SaleType.TKTSALES)) {
            payment_list = findTicketingSalesCashBook(from, to, userid, clienttype, clientid, paymentType);
        } else if (saleType.equals(Enums.SaleType.OTHERSALES)) {
            payment_list = findOtherSalesCashBook(from, to, userid, clienttype, clientid, paymentType);
        } else if (saleType.equals(Enums.SaleType.TKTPURCHASE)) {
            payment_list = findTicketingPurchaseCashBook(from, to, userid, clienttype, clientid, paymentType);
        }

        CashBookReport report = CashBookReport.serializeToReport(payment_list, from, to);
        report.setTitle("Cash Book");
        return report;
    }

}
