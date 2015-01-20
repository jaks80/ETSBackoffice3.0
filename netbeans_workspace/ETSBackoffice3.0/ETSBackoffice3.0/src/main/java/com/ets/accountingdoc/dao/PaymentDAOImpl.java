package com.ets.accountingdoc.dao;

import com.ets.GenericDAOImpl;
import com.ets.accountingdoc.domain.Payment;
import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Yusuf
 */
@Service("paymentDAO")
@Transactional
public class PaymentDAOImpl extends GenericDAOImpl<Payment, Long> implements PaymentDAO {

    @Override
    public List<Payment> findPaymentBySalesInvoice(Long invoice_id) {

        String hql = "select distinct p from Payment as p "
                + "inner join fetch p.tSalesPayments as sp "
                + "left join fetch sp.parent as sinv "
                + "where sinv.id = :invoice_id";

        Query query = getSession().createQuery(hql);
        query.setParameter("invoice_id", invoice_id);
        List<Payment> result = query.list();
        return result;
    }

    @Override
    public Payment findById(Long id) {
        String hql = "select distinct p from Payment as p "
                + "left join fetch p.tSalesPayments as sp "
                + "left join fetch p.tPurchasePayments as pp "
                + "left join fetch p.oSalesPayments as op "
                + "where p.id = :id";

        Query query = getSession().createQuery(hql);
        query.setParameter("id", id);
        Payment payment = (Payment) query.uniqueResult();
        return payment;
    }

}
