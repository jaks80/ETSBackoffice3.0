package com.ets.accountingdoc.dao;

import com.ets.GenericDAOImpl;
import com.ets.accountingdoc.domain.AccountingDocumentLine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Yusuf
 */
@Service("accountingDocumentLineDAO")
@Transactional
public class AccountingDocumentLineDAOImpl extends GenericDAOImpl<AccountingDocumentLine, Long> implements AccountingDocumentLineDAO{
    
}
