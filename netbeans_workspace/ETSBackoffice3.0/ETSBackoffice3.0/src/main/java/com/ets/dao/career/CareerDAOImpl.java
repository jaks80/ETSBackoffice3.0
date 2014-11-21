package com.ets.dao.career;

import com.ets.dao.generic.GenericDAOImpl;
import com.ets.domain.pnr.Airline;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Yusuf
 */
@Service("careerDAO")
@Transactional
public class CareerDAOImpl extends GenericDAOImpl<Airline, Long> implements CareerDAO{
    
}
