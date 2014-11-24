package com.ets.pnr.service;

import com.ets.pnr.model.collection.Pnrs;
import com.ets.pnr.dao.PnrDAO;
import com.ets.pnr.domain.Pnr;
import com.ets.util.DateUtil;
import com.ets.util.PnrUtil;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 *
 * @author Yusuf
 */
@Service("pnrService")
public class PnrService{

    @Resource(name = "pnrDAO")
    private PnrDAO dao;

    public void save(Pnr pnr) {

    }

    public Pnr getByGDSPnr(String gdsPnr) {
        Pnr pnr = new Pnr();
        

        return pnr;
    }

    public Pnrs pnrHistory(String issueDateFrom, String issueDateTo, String ticketingAgtOid, String bookingAgtOid){
    
        if ("null".equals(ticketingAgtOid)) {
            ticketingAgtOid = null;
        }

        if ("null".equals(bookingAgtOid)) {
            bookingAgtOid = null;
        }
        
        Date dateFrom = DateUtil.stringToDate(issueDateFrom, "ddMMMyyyy");
        Date dateTo = DateUtil.stringToDate(issueDateTo, "ddMMMyyyy");
        
        String[] tktedOIDs = null;
        String[] bokingOIDs = null;
        
        if("null".equals(ticketingAgtOid)){
         ticketingAgtOid = null;
        }else if(ticketingAgtOid != null){
         tktedOIDs = ticketingAgtOid.split(",");
        } 
        
        if("null".equals(bookingAgtOid)){
         bookingAgtOid = null;
        }else if( bookingAgtOid != null){
         bokingOIDs = bookingAgtOid.split(",");
        }
        
        List<Pnr> pnrList = dao.find(dateFrom, dateTo, tktedOIDs, bokingOIDs);
        for(Pnr p: pnrList){
         PnrUtil.undefinePnrChildren(p);
        }
        
        Pnrs pnrs =  new Pnrs();
        pnrs.setList(pnrList);
        
        return pnrs;
    }
}
