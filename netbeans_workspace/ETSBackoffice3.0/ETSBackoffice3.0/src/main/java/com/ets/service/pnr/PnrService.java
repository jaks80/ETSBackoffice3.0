package com.ets.service.pnr;

import com.ets.dao.generic.GenericDAO;
import com.ets.domain.pnr.Pnr;
import com.ets.domain.pnr.Ticket;
import com.ets.util.DateUtil;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.hibernate.Query;
import org.springframework.stereotype.Service;

/**
 *
 * @author Yusuf
 */
@Service
public class PnrService implements GenericDAO<Pnr, Long>{

    public void save(Pnr pnr) {

    }

    public Pnr getByGDSPnr(String gdsPnr) throws ParseException {
        Pnr pnr = new Pnr();
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

        return pnr;
    }

    @Override
    public void merge(Pnr entity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(Pnr entity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List findAll(Class clazz) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Pnr findByID(Class clazz, Long id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Pnr> findMany(Query query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Pnr findOne(Query query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

   
}
