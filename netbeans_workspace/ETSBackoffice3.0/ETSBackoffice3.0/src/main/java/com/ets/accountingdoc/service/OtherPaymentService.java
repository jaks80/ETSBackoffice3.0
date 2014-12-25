package com.ets.accountingdoc.service;

import com.ets.accountingdoc.collection.OtherSalesPayments;
import com.ets.accountingdoc.dao.OtherSalesPaymentDAO;
import com.ets.accountingdoc.domain.OtherSalesPayment;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 *
 * @author Yusuf
 */
@Service("otherPaymentService")
public class OtherPaymentService {
    
    @Resource(name = "otherSalesPaymentDAO")
    private OtherSalesPaymentDAO dao;
    
      public OtherSalesPayment save(OtherSalesPayment payment){
         dao.save(payment);
         return payment;
    }
    
    public OtherSalesPayments saveBulk(OtherSalesPayments payments){
         dao.saveBulk(payments.getList());
         return payments;
    }
}
