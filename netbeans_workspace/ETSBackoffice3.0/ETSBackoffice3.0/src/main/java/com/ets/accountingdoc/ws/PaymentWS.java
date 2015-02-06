package com.ets.accountingdoc.ws;

import com.ets.accountingdoc.collection.Payments;
import com.ets.accountingdoc.domain.Payment;
import com.ets.accountingdoc.service.PaymentService;
import com.ets.accountingdoc.model.InvoiceReport;
import com.ets.util.DateUtil;
import com.ets.util.Enums;
import java.util.Date;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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

    @GET
    @Path("/byid/{id}")
    public Payment getById(@PathParam("id") Long id) {
        return service.findById(id);
    }

    @GET
    @Path("/paymentbysinv")
    public Payments getPaymentBySalesInvoice(@QueryParam("invoiceid") Long invoiceid) {
        return service.findPaymentBySalesInvoice(invoiceid);
    }

    @GET
    @Path("/payment_history")
    public Payments salesPaymentHistory(
            @QueryParam("clienttype") Enums.ClientType clienttype,
            @QueryParam("clientid") Long clientid,
            @QueryParam("dateStart") String dateStart,
            @QueryParam("dateEnd") String dateEnd,
            @QueryParam("saleType") Enums.SaleType saleType) {

        Date dateFrom = DateUtil.stringToDate(dateStart, "ddMMMyyyy");
        Date dateTo = DateUtil.stringToDate(dateEnd, "ddMMMyyyy");

        List<Payment> payment_history = service.findPaymentHistory(clienttype, clientid, dateFrom, dateTo, saleType);
        Payments payments = new Payments();
        payments.setList(payment_history);
        return payments;
    }

//    @GET
//    @Path("/purchasepayment_history")
//    public Payments purchasePaymentHistory(
//            @QueryParam("clienttype") Enums.ClientType clienttype,
//            @QueryParam("clientid") Long clientid,
//            @QueryParam("dateStart") String dateStart,
//            @QueryParam("dateEnd") String dateEnd) {
//
//        Date dateFrom = DateUtil.stringToDate(dateStart, "ddMMMyyyy");
//        Date dateTo = DateUtil.stringToDate(dateEnd, "ddMMMyyyy");
//
//        List<Payment> payment_history = service.findTSPaymentHistory(clienttype, clientid, dateFrom, dateTo, Enums.SaleType.PURCHASE);
//        Payments payments = new Payments();
//        payments.setList(payment_history);
//        return payments;
//    }
//
//    @GET
//    @Path("/otherpayment_history")
//    public Payments otherPaymentHistory(
//            @QueryParam("clienttype") Enums.ClientType clienttype,
//            @QueryParam("clientid") Long clientid,
//            @QueryParam("dateStart") String dateStart,
//            @QueryParam("dateEnd") String dateEnd) {
//
//        Date dateFrom = DateUtil.stringToDate(dateStart, "ddMMMyyyy");
//        Date dateTo = DateUtil.stringToDate(dateEnd, "ddMMMyyyy");
//
//        List<Payment> payment_history = service.findTSPaymentHistory(clienttype, clientid, dateFrom, dateTo, Enums.SaleType.OTHER);
//        Payments payments = new Payments();
//        payments.setList(payment_history);
//        return payments;
//    }
}
