package com.amadeus.air;

import com.amadeus.pnr.Itinerary;
import com.amadeus.pnr.PNR;
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
public class TTP_IssueTest {

    private AIR air;

    public TTP_IssueTest() {

    }

    @Before
    public void setUp() throws URISyntaxException, IOException {
        URL url = this.getClass().getResource("/TTP_Issue.txt");
        File file = new File(url.toURI());
        FileToAIRConverter converter = new FileToAIRConverter();
        this.air = converter.convert(file);
    }

    @Test
    public void testAirToPNR() throws Exception {
        System.out.println("Test TTP Issue");
        AIRToPNRConverter instance = new AIRToPNRConverter(this.air);
        PNR expResult = new PNR();
        expResult.setAirCreationDate(DateConverter.yyMMddToDate("090316"));
        expResult.setBookingAgtOid("BONU123IK");
        expResult.setTktingAgtOid("LONU123IT");
        expResult.setGdsPNR("5HH342");
        expResult.setNoOfPax(2);
        expResult.setPnrCreationDate(DateConverter.yyMMddToDate("090313"));
        expResult.setVendorPNR("AI HJ3HV");

        PNR result = instance.airToPNR();
        assertEquals(expResult.getAirCreationDate(), result.getAirCreationDate());
        assertEquals(expResult.getBookingAgtOid(), result.getBookingAgtOid());
        assertEquals(expResult.getGdsPNR(), result.getGdsPNR());
        assertEquals(expResult.getNoOfPax(), result.getNoOfPax());
        assertEquals(expResult.getPnrCreationDate(), result.getPnrCreationDate());
        assertEquals(expResult.getTktingAgtOid(), result.getTktingAgtOid());
        assertEquals(expResult.getVendorPNR(), result.getVendorPNR());
    }

    //@Test
    public void testAirToItinerary() {
        System.out.println("airToItinerary");
        AIRToPNRConverter instance = new AIRToPNRConverter(this.air);
        String segmentNo = "001";
        String stopOver = "O";
        String deptFrom = "";
        String deptTo = "";
        String deptDate = "";
        String deptTime = "";
        String arvDate = "";
        String arvTime = "";
        String airLineID = "";
        String flightNo = "";
        String ticketClass = "";
        String tktStatus = "";
        String baggage = "";
        String mealCode = "";
        String checkInTerminal = "";
        String checkInTime = "";
        String flightDuration = "";
        String mileage = "";
        List<Itinerary> expResult = null;
        List<Itinerary> result = instance.airToItinerary();
        assertEquals(expResult, result);

    }

    @Test
    public void testAirToTicket() throws ParseException {
        System.out.println("airToTicket");
        AIRToPNRConverter instance = new AIRToPNRConverter(this.air);
        List<Ticket> expResult = new ArrayList<>();

        Ticket t1 = new Ticket();
        t1.setBaseFare(new BigDecimal("90.00"));
        t1.setTax(new BigDecimal("296.10"));
        t1.setTotalFare(new BigDecimal("386.10"));
        t1.setCurrencyCode("GBP");
        t1.setDocIssuedate(DateConverter.yyMMddToDate("090316"));
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
        t2.setDocIssuedate(DateConverter.yyMMddToDate("090316"));
        t2.setFee(new BigDecimal("0.00"));
        t2.setNumericAirLineCode("098");
        t2.setOrginalTicketNo(null);
        t2.setPassengerNo("02");
        t2.setPaxForeName("KABIL MSTR");
        t2.setPaxSurName("MIAH");
        t2.setRestrictions("VALID ON AI ONLY PNR HJ3HV, NON-END/RER/CONV INTO MCO INBOUND 1ST CHANGE FREE/DC/");
        t2.setTicketNo("3535898496");
        t2.setTktStatus(2);

        expResult.add(t1);
        expResult.add(t2);
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

        assertEquals(expResult.get(1).getBaseFare(), result.get(1).getBaseFare());
        assertEquals(expResult.get(1).getTax(), result.get(1).getTax());
        assertEquals(expResult.get(1).getCurrencyCode(), result.get(1).getCurrencyCode());
        assertEquals(expResult.get(1).getDocIssuedate(), result.get(1).getDocIssuedate());
        assertEquals(expResult.get(1).getFee(), result.get(1).getFee());
        assertEquals(expResult.get(1).getTotalFare(), result.get(1).getTotalFare());
        assertEquals(expResult.get(1).getNumericAirLineCode(), result.get(1).getNumericAirLineCode());
        assertEquals(expResult.get(1).getOrginalTicketNo(), result.get(1).getOrginalTicketNo());
        assertEquals(expResult.get(1).getPassengerNo(), result.get(1).getPassengerNo());
        assertEquals(expResult.get(1).getPaxForeName(), result.get(1).getPaxForeName());
        assertEquals(expResult.get(1).getPaxSurName(), result.get(1).getPaxSurName());
        assertEquals(expResult.get(1).getRestrictions(), result.get(1).getRestrictions());
        assertEquals(expResult.get(1).getTicketNo(), result.get(1).getTicketNo());
        assertEquals(expResult.get(1).getTktStatus(), result.get(1).getTktStatus());
    }
}
