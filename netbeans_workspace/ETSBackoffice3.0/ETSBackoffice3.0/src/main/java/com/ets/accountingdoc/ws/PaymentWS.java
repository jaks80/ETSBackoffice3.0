package com.ets.accountingdoc.ws;

import com.ets.accountingdoc.collection.Payments;
import com.ets.accountingdoc.domain.Payment;
import com.ets.accountingdoc.service.PaymentService;
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
@Path("/payment-management")
@Consumes("application/xml")
@Produces("application/xml")
public class PaymentWS {
    
    @Autowired
    PaymentService service;
    
    @POST
    @Path("/new")
    public Payment create(Payment payment) {
        return service.save(payment);
    }

    @POST
    @Path("/newbulk")
    public Payments createBulk(Payments payments) {
        return service.saveBulk(payments);
    }
}
