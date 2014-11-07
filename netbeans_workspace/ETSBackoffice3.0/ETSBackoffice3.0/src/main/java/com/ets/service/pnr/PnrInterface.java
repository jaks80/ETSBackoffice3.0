package com.ets.service.pnr;

import com.ets.dao.generic.GenericDAO;
import com.ets.domain.pnr.Pnr;
import com.ets.domain.pnr.PnrRemark;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public interface PnrInterface extends GenericDAO<Pnr, Serializable>{        
    
    public Pnr loadPnr(String gdsPnr, Date pnrCreationDate);

    public List<Object> loadPnr(String tktNo, String surName);

    public Pnr findCompletePnr(long pnrID);

    public List<Pnr> findUninvoicedPnr();

    public List<Pnr> findByDate(Date start, Date end);

    public List<Pnr> bookedPnrs();

    public List<Pnr> searchByGdsPnr(String gdsPnr);    
    
    public List<Pnr> searchByTktNo(String tktNo); 

    public List<Pnr> searchByPaxName(String surName, String foreName);
}
