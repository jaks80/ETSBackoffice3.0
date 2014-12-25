package com.ets.accountingdoc.dao;

import com.ets.GenericDAOImpl;
import com.ets.accountingdoc.domain.OtherSalesPayment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Yusuf
 */
@Service("otherSalesPaymentDAO")
@Transactional
public class OtherSalesPaymentDAOImpl extends GenericDAOImpl<OtherSalesPayment, Long> implements OtherSalesPaymentDAO{
    
}
