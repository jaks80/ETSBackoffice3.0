package com.ets.accountingdoc.service;

import com.ets.accountingdoc.collection.Payments;
import com.ets.accountingdoc.dao.PaymentDAO;
import com.ets.accountingdoc.domain.Payment;
import java.util.List;
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
        dao.save(payment);
        return payment;
    }

    public Payments saveBulk(Payments payments) {
        List<Payment> list = payments.getList();
        dao.saveBulk(list);
        return payments;
    }
}
