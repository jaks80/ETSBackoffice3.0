package com.ets.accountingdoc.dao;

import com.ets.GenericDAOImpl;
import com.ets.accountingdoc.domain.Payment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Yusuf
 */
@Service("paymentDAO")
@Transactional
public class PaymentDAOImpl extends GenericDAOImpl<Payment, Long> implements PaymentDAO{
    
}
