package com.ets.accountingdoc.service;

import com.ets.accountingdoc.collection.TicketingPurchasePayments;
import com.ets.accountingdoc.dao.TPurchasePaymentDAO;
import com.ets.accountingdoc.domain.TicketingPurchasePayment;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 *
 * @author Yusuf
 */
@Service("tPurchasePaymentService")
public class TPurchasePaymentService {
    @Resource(name = "tPurchasePaymentDAO")
    private TPurchasePaymentDAO dao;
    
        public TicketingPurchasePayment save(TicketingPurchasePayment payment){
         dao.save(payment);
         return payment;
    }
    
    public TicketingPurchasePayments saveBulk(TicketingPurchasePayments payments){
         dao.saveBulk(payments.getList());
         return payments;
    }
}
