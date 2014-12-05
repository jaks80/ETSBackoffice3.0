package com.ets.accountingdoc.dao;

import com.ets.GenericDAOImpl;
import com.ets.accountingdoc.domain.OtherSalesAcDoc;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Yusuf
 */
@Service("otherSalesAcDocDAO")
@Transactional
public class OtherSalesAcDocDAOImpl  extends GenericDAOImpl<OtherSalesAcDoc, Long> implements OtherSalesAcDocDAO{
    
}
