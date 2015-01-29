package com.ets.accountingdoc.service;

import com.ets.accountingdoc.collection.Payments;
import com.ets.accountingdoc.dao.PaymentDAO;
import com.ets.accountingdoc.domain.Payment;
import com.ets.accountingdoc.domain.TicketingSalesAcDoc;
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
        Set<TicketingSalesAcDoc> docs = payment.gettSalesAcDocuments();
        for(TicketingSalesAcDoc d: docs){
         d.setPayment(payment);
        }
        
        dao.save(payment);
        
        docs = payment.gettSalesAcDocuments();
        for(TicketingSalesAcDoc d: docs){
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

    public Payments saveBulk(Payments payments) {
        List<Payment> list = payments.getList();
        dao.saveBulk(list);
        return payments;
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
}
