package com.ets.accountingdoc.dao;

import com.ets.GenericDAOImpl;
import com.ets.accountingdoc.domain.TicketingSalesPayment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Yusuf
 */
@Service("tSalesPaymentDAO")
@Transactional
public class TSalesPaymentDAOImpl extends GenericDAOImpl<TicketingSalesPayment, Long> implements TSalesPaymentDAO{
    
}
