package com.ets.dao.career;

import com.ets.dao.generic.GenericDAOImpl;
import com.ets.domain.pnr.Career;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Yusuf
 */
@Service("careerDAO")
@Transactional
public class CareerDAOImpl extends GenericDAOImpl<Career, Long> implements CareerDAO{
    
}
