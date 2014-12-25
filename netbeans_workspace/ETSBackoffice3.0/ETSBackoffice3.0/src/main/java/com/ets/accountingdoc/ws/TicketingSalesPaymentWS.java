package com.ets.accountingdoc.ws;

import com.ets.accountingdoc.collection.TicketingSalesPayments;
import com.ets.accountingdoc.domain.TicketingSalesPayment;
import com.ets.accountingdoc.service.TSalesPaymentService;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 *
 * @author Yusuf
 */
@Controller
@Path("/tspayment-management")
@Consumes("application/xml")
@Produces("application/xml")
public class TicketingSalesPaymentWS {
    
    @Autowired
    TSalesPaymentService service;
    
    @POST
    @Path("/new")
    public TicketingSalesPayment create(TicketingSalesPayment payment) {
        return service.save(payment);
    }

    @POST
    @Path("/newbulk")
    public TicketingSalesPayments update(TicketingSalesPayments payments) {
        return service.saveBulk(payments);
    }
}
