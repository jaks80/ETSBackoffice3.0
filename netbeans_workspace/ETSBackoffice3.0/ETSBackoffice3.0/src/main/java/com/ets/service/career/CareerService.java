package com.ets.service.career;

import com.ets.collection.Careers;
import com.ets.dao.career.CareerDAO;
import com.ets.domain.pnr.Airline;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 *
 * @author Yusuf
 */
@Service("careerService")
public class CareerService {
    
    @Resource(name = "careerDAO")
    private CareerDAO dao;
    
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
