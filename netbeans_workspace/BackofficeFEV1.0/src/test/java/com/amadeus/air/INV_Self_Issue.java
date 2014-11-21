package com.amadeus.air;

import com.ets.fe.model.pnr.Pnr;
import com.ets.fe.model.pnr.Ticket;
import com.ets.util.DateUtil;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Yusuf
 */
public class INV_Self_Issue {
    
    private AIR air;

    public INV_Self_Issue() {

    }

    @Before
    public void setUp() throws URISyntaxException, IOException {
        System.out.println("Test INV self issue...");
        URL url = this.getClass().getResource("/AIR/INV_SelfIssue.txt");
        File file = new File(url.toURI());
        FileToAIRConverter converter = new FileToAIRConverter();
        this.air = converter.convert(file);
    }
    
    @Test
    public void testAirToPNR() throws Exception {        
        AIRToPNRConverter instance = new AIRToPNRConverter(this.air);
        Pnr expResult = new Pnr();
        expResult.setAirCreationDate(DateUtil.yyMMddToDate("101016"));
        expResult.setBookingAgtOid("LONU123IT");
        expResult.setTicketingAgtOid("LONU123IT");
        expResult.setGdsPnr("3SNTG9");
        expResult.setNoOfPax(1);
        expResult.setPnrCreationDate(DateUtil.yyMMddToDate("101008"));
        expResult.setVendorPNR("SV 3SNTG9");

        Pnr result = instance.airToPNR();
        assertEquals(expResult.getAirCreationDate(), result.getAirCreationDate());
        assertEquals(expResult.getBookingAgtOid(), result.getBookingAgtOid());
        assertEquals(expResult.getGdsPnr(), result.getGdsPnr());
        assertEquals(expResult.getNoOfPax(), result.getNoOfPax());
        assertEquals(expResult.getPnrCreationDate(), result.getPnrCreationDate());
        assertEquals(expResult.getTicketingAgtOid(), result.getTicketingAgtOid());
        assertEquals(expResult.getVendorPNR(), result.getVendorPNR());
    }

    @Test
    public void testAirToTicket() throws ParseException {
        System.out.println("airToTicket");
        AIRToPNRConverter instance = new AIRToPNRConverter(this.air);
        List<Ticket> expResult = new ArrayList<>();

        Ticket t1 = new Ticket();
        t1.setBaseFare(new BigDecimal("439.00"));
        t1.setTax(new BigDecimal("131.90"));
        t1.setFee(new BigDecimal("0.00"));
        t1.setTotalFare(new BigDecimal("570.90"));
        t1.setCurrencyCode("GBP");
        t1.setDocIssuedate(DateUtil.yyMMddToDate("101016"));        
        t1.setNumericAirLineCode("065");
        t1.setOrginalTicketNo(null);
        t1.setPassengerNo("01");
        t1.setPaxForeName("MD KAMRUL MR");
        t1.setPaxSurName("BASHIR");
        t1.setRestrictions(null);
        t1.setTicketNo("1632104224");
        t1.setTktStatus(2);
        
        expResult.add(t1);
        
        List<Ticket> result = instance.airToTicket();
        assertEquals(expResult.size(), result.size());
        assertEquals(expResult.get(0).getBaseFare(), result.get(0).getBaseFare());
        assertEquals(expResult.get(0).getTax(), result.get(0).getTax());
        assertEquals(expResult.get(0).getCurrencyCode(), result.get(0).getCurrencyCode());
        assertEquals(expResult.get(0).getDocIssuedate(), result.get(0).getDocIssuedate());
        assertEquals(expResult.get(0).getFee(), result.get(0).getFee());
        assertEquals(expResult.get(0).getTotalFare(), result.get(0).getTotalFare());
        assertEquals(expResult.get(0).getNumericAirLineCode(), result.get(0).getNumericAirLineCode());
        assertEquals(expResult.get(0).getOrginalTicketNo(), result.get(0).getOrginalTicketNo());
        assertEquals(expResult.get(0).getPassengerNo(), result.get(0).getPassengerNo());
        assertEquals(expResult.get(0).getPaxForeName(), result.get(0).getPaxForeName());
        assertEquals(expResult.get(0).getPaxSurName(), result.get(0).getPaxSurName());
        assertEquals(expResult.get(0).getRestrictions(), result.get(0).getRestrictions());
        assertEquals(expResult.get(0).getTicketNo(), result.get(0).getTicketNo());
        assertEquals(expResult.get(0).getTktStatus(), result.get(0).getTktStatus());
    }
}