package com.amadeus.air;

import com.amadeus.pnr.Ticket;
import com.amadeus.util.DateConverter;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Yusuf
 */
public class TTP_ReIssueTest {

    private AIR air;

    public TTP_ReIssueTest() {

    }

    @Before
    public void setUp() throws URISyntaxException, IOException {
        URL url = this.getClass().getResource("/TTP_ReIssue.txt");
        File file = new File(url.toURI());
        FileToAIRConverter converter = new FileToAIRConverter();
        this.air = converter.convert(file);
    }

    @Test
    public void testAirToTicket() throws ParseException {
        System.out.println("Test TTP ReIssue");
        AIRToPNRConverter instance = new AIRToPNRConverter(this.air);
        List<Ticket> expResult = new ArrayList<>();
        
        Ticket t1 = new Ticket();
        t1.setBaseFare(new BigDecimal("0.00"));
        t1.setTax(new BigDecimal("0.00"));
        t1.setCurrencyCode("GBP");
        t1.setDocIssuedate(DateConverter.yyMMddToDate("090530"));
        t1.setFee(new BigDecimal("0.00"));        
        t1.setNumericAirLineCode("098");
        t1.setTicketNo("3943767500");
        t1.setOrginalTicketNo("3535898495");
        t1.setPassengerNo("01");
        t1.setPaxForeName("NABIL MSTR(CHD)(ID05MAR03)");
        t1.setPaxSurName("MIAH");
        t1.setRestrictions("VALID ON AI ONLY PNR HJ3HV, NON-END/RER/CONV INTO MCO INBOUND 1ST CHANGE FREE/DC/");        
        t1.setTktStatus(3);
                
        expResult.add(t1);        
        
        List<Ticket> result = instance.airToTicket();
        assertEquals(expResult.size(), result.size());
        assertEquals(expResult.get(0).getBaseFare(), result.get(0).getBaseFare());
        assertEquals(expResult.get(0).getTax(), result.get(0).getTax());
        assertEquals(expResult.get(0).getCurrencyCode(), result.get(0).getCurrencyCode());
        assertEquals(expResult.get(0).getDocIssuedate(), result.get(0).getDocIssuedate());
        assertEquals(expResult.get(0).getFee(), result.get(0).getFee());        
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
