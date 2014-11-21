package com.ets.dao.air;

import com.ets.dao.generic.GenericDAO;
import com.ets.domain.pnr.Pnr;
import java.util.Date;

/**
 *
 * @author Yusuf
 */
public interface AirDAO extends GenericDAO<Pnr, Long>{
    
    public Pnr findPnr(String gdsPnr, Date pnrCreationDate);
    
    public Pnr findPnr(String ticketNo, String surName);
}
