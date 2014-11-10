package com.ets.service.air;

import com.ets.collection.Tickets;
import com.ets.dao.air.AirDAO;
import com.ets.domain.pnr.Pnr;
import com.ets.util.PnrUtil;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 *
 * @author Yusuf
 */
@Service("airService")
public class AirService {

    @Resource(name = "airDAO")
    private AirDAO dao;

    public AirService(){

    }

    public Pnr createBooking(Pnr pnr) {
    
        return pnr;
    }
    
    public Pnr createIssue(Pnr pnr) {
        PnrUtil.initPnrInRemark(pnr, pnr.getRemarks());
        PnrUtil.initPnrInTickets(pnr, pnr.getTickets());
        PnrUtil.initPnrInSegments(pnr, pnr.getSegments());
        PnrUtil.initItineraryInTickets(pnr.getSegments(), pnr.getTickets());

        dao.save(pnr);

        //Undefine cyclic dependencies to avoid cyclic xml exception
        PnrUtil.undefinePnrInRemark(pnr, pnr.getRemarks());
        PnrUtil.undefinePnrInTickets(pnr, pnr.getTickets());
        PnrUtil.undefinePnrInSegments(pnr, pnr.getSegments());
        PnrUtil.undefineItineraryInTickets(pnr.getSegments(), pnr.getTickets());

        return pnr;
    }
    
    public Pnr createReIssue(Pnr pnr){
    
        return pnr;
    }

    public Tickets refundTicket(Tickets tickets) {

        
        return tickets;
    }
    
    public Tickets voidTicket(Tickets tickets) {

        return tickets;
    }
}
