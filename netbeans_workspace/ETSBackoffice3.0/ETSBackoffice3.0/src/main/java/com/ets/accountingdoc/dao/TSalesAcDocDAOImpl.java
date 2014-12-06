package com.ets.accountingdoc.dao;

import com.ets.GenericDAOImpl;
import com.ets.accountingdoc.domain.TicketingSalesAcDoc;
import org.hibernate.Query;
import org.springframework.dao.support.DataAccessUtils;
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

    @Override
    public Integer getMaxAcDocRef() {
        String hql = "select max(acDoc.acDocRef) from TicketingSalesAcDoc acDoc";
        Query query = getSession().createQuery(hql);
        
        Object result = DataAccessUtils.uniqueResult(query.list());
        return (result != null) ? Integer.valueOf(result.toString()) : 0;
    }
}
