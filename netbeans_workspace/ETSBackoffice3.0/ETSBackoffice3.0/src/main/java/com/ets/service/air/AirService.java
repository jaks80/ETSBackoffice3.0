package com.ets.service.air;

import com.ets.dao.air.AirDAO;
import com.ets.domain.pnr.Pnr;
import com.ets.domain.pnr.Ticket;
import com.ets.util.DateUtil;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
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
    
    public Pnr create(Pnr pnr) {
        dao.save(pnr);        
        return pnr;
    }
    
    public Pnr create() throws ParseException {
        Pnr pnr = new Pnr();
        //pnr.setId("1212");
        pnr.setAirCreationDate(DateUtil.yyMMddToDate("090316"));
        pnr.setBookingAgtOid("BONU123IK");
        pnr.setTktingAgtOid("LONU123IT");
        pnr.setGdsPNR("5HH342");
        pnr.setNoOfPax(2);
        pnr.setPnrCreationDate(DateUtil.yyMMddToDate("090313"));
        pnr.setVendorPNR("AI HJ3HV");

        Set<Ticket> tickets = new LinkedHashSet<>();

        Ticket t1 = new Ticket();
        t1.setBaseFare(new BigDecimal("90.00"));
        t1.setTax(new BigDecimal("296.10"));
        t1.setTotalFare(new BigDecimal("386.10"));
        t1.setCurrencyCode("GBP");
        t1.setDocIssuedate(DateUtil.yyMMddToDate("090316"));
        t1.setFee(new BigDecimal("0.00"));
        t1.setNumericAirLineCode("098");
        t1.setOrginalTicketNo(null);
        t1.setPassengerNo("01");
        t1.setPaxForeName("NABIL MSTR(CHD)(ID05MAR03)");
        t1.setPaxSurName("MIAH");
        t1.setRestrictions("VALID ON AI ONLY PNR HJ3HV, NON-END/RER/CONV INTO MCO INBOUND 1ST CHANGE FREE/DC/");
        t1.setTicketNo("3535898495");
        t1.setTktStatus(2);

        Ticket t2 = new Ticket();
        t2.setBaseFare(new BigDecimal("90.00"));
        t2.setTax(new BigDecimal("296.10"));
        t2.setTotalFare(new BigDecimal("386.10"));
        t2.setCurrencyCode("GBP");
        t2.setDocIssuedate(DateUtil.yyMMddToDate("090316"));
        t2.setFee(new BigDecimal("0.00"));
        t2.setNumericAirLineCode("098");
        t2.setOrginalTicketNo(null);
        t2.setPassengerNo("02");
        t2.setPaxForeName("KABIL MSTR");
        t2.setPaxSurName("MIAH");
        t2.setRestrictions("VALID ON AI ONLY PNR HJ3HV, NON-END/RER/CONV INTO MCO INBOUND 1ST CHANGE FREE/DC/");
        t2.setTicketNo("3535898496");
        t2.setTktStatus(2);

        tickets.add(t1);
        tickets.add(t2);

        pnr.setTickets(tickets);  
        dao.save(pnr);
        return pnr;
    }
}
