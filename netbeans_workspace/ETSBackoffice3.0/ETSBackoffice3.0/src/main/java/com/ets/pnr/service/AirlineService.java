package com.ets.pnr.service;

import com.ets.pnr.model.collection.Careers;
import com.ets.pnr.dao.AirlineDAO;
import com.ets.pnr.domain.Airline;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 *
 * @author Yusuf
 */
@Service("airlineService")
public class AirlineService {
    
    @Resource(name = "airlineDAO")
    private AirlineDAO dao;
    
    public Airline find(String code){
    
        return new Airline();
    }
    
    public Careers match(String name){
    
        return new Careers();        
    }
    
    public void save(Airline career){
    
    }
    
    public void saveBulk(Careers careers){
    
    }
}
