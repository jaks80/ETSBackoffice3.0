package com.ets.accountingdoc.dao;

import com.ets.GenericDAOImpl;
import com.ets.accountingdoc.domain.TicketingPurchaseAcDoc;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Yusuf
 */
@Service("tPurchaseAcDocDAO")
@Transactional
public class TPurchaseAcDocDAOImpl extends GenericDAOImpl<TicketingPurchaseAcDoc, Long> implements TPurchaseAcDocDAO{
    
}
