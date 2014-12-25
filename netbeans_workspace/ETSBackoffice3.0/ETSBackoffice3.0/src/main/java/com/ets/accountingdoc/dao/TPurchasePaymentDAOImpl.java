package com.ets.accountingdoc.dao;

import com.ets.GenericDAOImpl;
import com.ets.accountingdoc.domain.TicketingPurchasePayment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Yusuf
 */
@Service("tPurchasePaymentDAO")
@Transactional
public class TPurchasePaymentDAOImpl extends GenericDAOImpl<TicketingPurchasePayment, Long> implements TPurchasePaymentDAO{
    
}
