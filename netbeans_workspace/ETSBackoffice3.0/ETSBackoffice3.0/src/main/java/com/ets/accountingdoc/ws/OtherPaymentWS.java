package com.ets.accountingdoc.ws;

import com.ets.accountingdoc.collection.OtherSalesPayments;
import com.ets.accountingdoc.domain.OtherSalesPayment;
import com.ets.accountingdoc.service.OtherPaymentService;
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
@Path("/opayment-management")
@Consumes("application/xml")
@Produces("application/xml")
public class OtherPaymentWS {
 
    @Autowired
    OtherPaymentService service;
    
    @POST
    @Path("/new")
    public OtherSalesPayment create(OtherSalesPayment payment) {

        return service.save(payment);
    }

    @POST
    @Path("/newbulk")
    public OtherSalesPayments update(OtherSalesPayments payments) {
        return service.saveBulk(payments);
    }
}
