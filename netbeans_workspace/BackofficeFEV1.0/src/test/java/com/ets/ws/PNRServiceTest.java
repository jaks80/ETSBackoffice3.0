package com.ets.ws;

import com.ets.fe.model.pnr.Pnr;
import com.ets.fe.model.pnr.Ticket;
import com.ets.util.DateUtil;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.junit.Before;

/**
 *
 * @author Yusuf
 */
public class PNRServiceTest {

    String url = "http://localhost:8084/ETSBackoffice3.0/air";

    public PNRServiceTest() {
    }

    @Before
    public void setUp() {
    }

    //@Test
    public void testGet() {
        System.out.println("web service getAll");

        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target(this.url+"/pnrs");
        Response response = target.request().get();

        Pnr value = response.readEntity(Pnr.class);
        response.close();

        System.out.println("EEE");
    }
    
    //@Test
    public void testPost() throws ParseException {
    
        Pnr pnr = new Pnr();
        //pnr.setId("1212");
        pnr.setAirCreationDate(DateUtil.yyMMddToDate("090316"));
        pnr.setBookingAgtOid("BONU123IK");
        pnr.setTicketingAgtOid("LONU123IT");
        pnr.setGdsPnr("5HH342");
        pnr.setNoOfPax(2);
        pnr.setPnrCreationDate(DateUtil.yyMMddToDate("090313"));
        pnr.setVendorPNR("AI HJ3HV");

        List<Ticket> tickets = new ArrayList<>();

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
        
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(url);
        Response response = target.request().post(Entity.entity(pnr, "application/xml"));
        //Read output in string format
        System.out.println(response.getStatus());
        response.close(); 
    }
}
