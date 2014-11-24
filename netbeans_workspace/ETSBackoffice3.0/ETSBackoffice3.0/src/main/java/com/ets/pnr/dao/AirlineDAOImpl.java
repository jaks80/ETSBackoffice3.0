package com.ets.pnr.dao;

import com.ets.GenericDAOImpl;
import com.ets.pnr.domain.Airline;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Yusuf
 */
@Service("airlineDAO")
@Transactional
public class AirlineDAOImpl extends GenericDAOImpl<Airline, Long> implements AirlineDAO{
    
}
