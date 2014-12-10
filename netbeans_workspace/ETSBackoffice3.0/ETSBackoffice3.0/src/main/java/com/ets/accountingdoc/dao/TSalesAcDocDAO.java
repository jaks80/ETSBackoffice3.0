package com.ets.accountingdoc.dao;

import com.ets.GenericDAO;
import com.ets.accountingdoc.domain.TicketingSalesAcDoc;

/**
 *
 * @author Yusuf
 */
public interface TSalesAcDocDAO extends GenericDAO<TicketingSalesAcDoc, Long>{
    
    public Long getNewAcDocRef();
}
