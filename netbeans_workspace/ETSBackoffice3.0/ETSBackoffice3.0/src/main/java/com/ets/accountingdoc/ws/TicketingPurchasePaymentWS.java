package com.ets.accountingdoc.ws;

import com.ets.accountingdoc.collection.TicketingPurchasePayments;
import com.ets.accountingdoc.collection.TicketingSalesPayments;
import com.ets.accountingdoc.domain.TicketingPurchasePayment;
import com.ets.accountingdoc.domain.TicketingSalesPayment;
import com.ets.accountingdoc.service.TPurchasePaymentService;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 *
 * @author Yusuf
 */
@Controller
@Path("/tppayment-management")
@Consumes("application/xml")
@Produces("application/xml")
public class TicketingPurchasePaymentWS {
    
    @Autowired
    TPurchasePaymentService service;
    
        @POST
    @Path("/new")
    public TicketingPurchasePayment create(TicketingPurchasePayment payment) {

        return service.save(payment);
    }

    @POST
    @Path("/newbulk")
    public TicketingPurchasePayments update(TicketingPurchasePayments payments) {
        return service.saveBulk(payments);
    }
}
