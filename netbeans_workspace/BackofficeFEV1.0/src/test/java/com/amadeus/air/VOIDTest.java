package com.amadeus.air;

import com.ets.fe.model.pnr.Ticket;
import java.io.File;
import java.io.IOException;
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
public class VOIDTest {

    private AIR air;

    public VOIDTest() {

    }

    @Before
    public void setUp() throws URISyntaxException, IOException {
        System.out.println("Test VOID Ticket");
        URL url = this.getClass().getResource("/AIR/Void.txt");
        File file = new File(url.toURI());
        FileToAIRConverter converter = new FileToAIRConverter();
        this.air = converter.convert(file);
    }

    @Test
    public void testAirToVOIDTicket() throws ParseException {
        
        AIRToPNRConverter instance = new AIRToPNRConverter(this.air);
        List<Ticket> expResult = new ArrayList<>();
        
        Ticket t1 = new Ticket();
        
        t1.setNumericAirLineCode("229");
        t1.setTicketNo("2797312568");        
        t1.setPassengerNo("01");
        t1.setPaxForeName("RAHENA MRS");
        t1.setPaxSurName("BEGUM CHOWDHURY");        
        t1.setTktStatus(5);                
        expResult.add(t1);        
        
        List<Ticket> result = instance.airToVoidTicket();
        
        assertEquals(expResult.get(0).getNumericAirLineCode(), result.get(0).getNumericAirLineCode());        
        assertEquals(expResult.get(0).getPassengerNo(), result.get(0).getPassengerNo());
        assertEquals(expResult.get(0).getPaxForeName(), result.get(0).getPaxForeName());
        assertEquals(expResult.get(0).getPaxSurName(), result.get(0).getPaxSurName());        
        assertEquals(expResult.get(0).getTicketNo(), result.get(0).getTicketNo());
        assertEquals(expResult.get(0).getTktStatus(), result.get(0).getTktStatus());        
    }
}
