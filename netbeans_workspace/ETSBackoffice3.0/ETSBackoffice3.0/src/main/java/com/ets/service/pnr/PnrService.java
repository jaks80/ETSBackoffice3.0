package com.ets.service.pnr;

import com.ets.collection.Pnrs;
import com.ets.dao.pnr.PnrDAO;
import com.ets.domain.pnr.Pnr;
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
        
        Date dateFrom = DateUtil.stringToDate(issueDateFrom, "dd-MM-yyyy");
        Date dateTo = DateUtil.stringToDate(issueDateTo, "dd-MM-yyyy");
        
        List<Pnr> pnrList = dao.find(dateFrom, dateTo, ticketingAgtOid, bookingAgtOid);
        for(Pnr p: pnrList){
         PnrUtil.undefinePnrChildren(p);
        }
        
        Pnrs pnrs =  new Pnrs();
        pnrs.setList(pnrList);
        
        return pnrs;
    }
}
