package com.ets.dao.air;

import com.ets.dao.generic.GenericDAOImpl;
import com.ets.domain.pnr.Pnr;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Yusuf
 */
@Service("airDAO")
@Transactional
public class AirDAOImpl  extends  GenericDAOImpl<Pnr, Long> implements AirDAO {
    
    public AirDAOImpl(){
    
    }    
}
