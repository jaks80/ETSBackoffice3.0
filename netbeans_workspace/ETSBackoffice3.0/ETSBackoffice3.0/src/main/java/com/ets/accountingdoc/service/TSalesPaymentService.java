package com.ets.accountingdoc.service;

import com.ets.accountingdoc.collection.TicketingSalesPayments;
import com.ets.accountingdoc.dao.TSalesPaymentDAO;
import com.ets.accountingdoc.domain.TicketingSalesPayment;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 *
 * @author Yusuf
 */
@Service("tSalesPaymentService")
public class TSalesPaymentService {

    @Resource(name = "tSalesPaymentDAO")
    private TSalesPaymentDAO dao;

    public TicketingSalesPayment save(TicketingSalesPayment payment) {
        dao.save(payment);
        return payment;
    }

    public TicketingSalesPayments saveBulk(TicketingSalesPayments payments) {
        List<TicketingSalesPayment> list = payments.getList();
        dao.saveBulk(list);
        return payments;
    }
}
