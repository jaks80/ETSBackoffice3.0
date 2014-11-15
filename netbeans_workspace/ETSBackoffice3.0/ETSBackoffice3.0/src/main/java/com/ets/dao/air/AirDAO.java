package com.ets.dao.air;

import com.ets.dao.generic.GenericDAO;
import com.ets.domain.pnr.Pnr;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public interface AirDAO extends GenericDAO<Pnr, Long>{
    
    public Pnr findPnr(String gdsPnr, Date pnrCreationDate);
    
    public List<Object> findPnr(String tktNo, String surName);
}
