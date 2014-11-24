package com.ets.pnr.dao;

import com.ets.GenericDAO;
import com.ets.pnr.domain.Pnr;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public interface PnrDAO extends GenericDAO<Pnr, Long>{
   
    public List<Pnr> find(String gdsPnr);
    
    public List<Pnr> bookedPnrs();
    
    public List<Pnr> find(Date issueDateFrom, Date issueDateTo, String[] tktingAgtOID, String[] bookingAgtOID);
}
