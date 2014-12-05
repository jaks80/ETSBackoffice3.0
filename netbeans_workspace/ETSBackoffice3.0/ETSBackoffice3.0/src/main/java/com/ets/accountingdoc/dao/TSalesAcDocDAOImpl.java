package com.ets.accountingdoc.dao;

import com.ets.GenericDAOImpl;
import com.ets.accountingdoc.domain.TicketingSalesAcDoc;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ,./
 *
 * @author Yusuf
 */
@Service("tSalesAcDocDAO")
@Transactional
public class TSalesAcDocDAOImpl extends GenericDAOImpl<TicketingSalesAcDoc, Long> implements TSalesAcDocDAO{

}
