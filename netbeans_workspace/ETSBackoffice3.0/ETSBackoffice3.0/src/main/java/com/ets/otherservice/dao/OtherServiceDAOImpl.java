package com.ets.otherservice.dao;

import com.ets.GenericDAOImpl;
import com.ets.otherservice.domain.OtherService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Yusuf
 */
@Service("otherServiceDAO")
@Transactional
public class OtherServiceDAOImpl extends GenericDAOImpl<OtherService, Long> implements OtherServiceDAO{
    
}
