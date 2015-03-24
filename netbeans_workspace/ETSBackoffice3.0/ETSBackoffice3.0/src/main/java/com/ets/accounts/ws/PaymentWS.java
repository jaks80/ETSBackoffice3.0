package com.ets.accounts.ws;

import com.ets.accounts.model.*;
import com.ets.accounts.domain.Payment;
import com.ets.accounts.service.PaymentService;
import com.ets.util.DateUtil;
import com.ets.util.Enums;
import java.util.Date;
import java.util.List;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
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
    @RolesAllowed("GS")
    public Payment newPayment(Payment payment) {
        return service.save(payment);
    }

    @POST
    @Path("/newbsppay")
    @RolesAllowed("SM")
    public Payment newBSPPayment(@QueryParam("userid") Long userid,
            @QueryParam("agentid") Long agentid,
            @QueryParam("dateStart") String dateStart,
            @QueryParam("dateEnd") String dateEnd,@QueryParam("dateEnd") String paymentDate) {
        
        
        Date dateFrom = DateUtil.stringToDate(dateStart, "ddMMMyyyy");
        Date dateTo = DateUtil.stringToDate(dateEnd, "ddMMMyyyy");
        Date _paymentDate = DateUtil.stringToDate(paymentDate, "ddMMMyyyy");

        return service.newBSPPayment(agentid, dateFrom, dateTo,userid,_paymentDate);
    }
    
    @POST
    @Path("/newctransfer")
    @RolesAllowed("GS")
    public Response createCreditTransfer(CreditTransfer creditTransfer) {
        service.createCreditTransfer(creditTransfer);
        return Response.status(200).build();
    }
    
    @GET
    @Path("/byid/{id}")
    @RolesAllowed("GS")
    public Payment getById(@PathParam("id") Long id) {
        return service.findById(id);
    }

    @GET
    @Path("/paymentbysinv")
    @RolesAllowed("GS")
    public Payments getPaymentBySalesInvoice(@QueryParam("invoiceid") Long invoiceid) {
        return service.findPaymentBySalesInvoice(invoiceid);
    }

    @GET
    @Path("/tpayment_history")
    @RolesAllowed("SM")
    public Payments ticketingPaymentHistory(
            @QueryParam("clienttype") Enums.ClientType clienttype,
            @QueryParam("clientid") Long clientid,
            @QueryParam("dateStart") String dateStart,
            @QueryParam("dateEnd") String dateEnd,
            @QueryParam("saleType") Enums.SaleType saleType) {

        Date dateFrom = DateUtil.stringToDate(dateStart, "ddMMMyyyy");
        Date dateTo = DateUtil.stringToDate(dateEnd, "ddMMMyyyy");

        List<Payment> payment_history = service.findTicketingPaymentHistory(clienttype, clientid, dateFrom, dateTo, saleType);
        Payments payments = new Payments();
        payments.setList(payment_history);
        return payments;
    }
    
    @GET
    @Path("/opayment_history")
    @RolesAllowed("SM")
    public Payments otherPaymentHistory(
            @QueryParam("clienttype") Enums.ClientType clienttype,
            @QueryParam("clientid") Long clientid,
            @QueryParam("dateStart") String dateStart,
            @QueryParam("dateEnd") String dateEnd,
            @QueryParam("saleType") Enums.SaleType saleType) {

        Date dateFrom = DateUtil.stringToDate(dateStart, "ddMMMyyyy");
        Date dateTo = DateUtil.stringToDate(dateEnd, "ddMMMyyyy");

        List<Payment> payment_history = service.findOtherPaymentHistory(clienttype, clientid, dateFrom, dateTo, saleType);
        Payments payments = new Payments();
        payments.setList(payment_history);
        return payments;
    }
    
    @GET
    @Path("/cashbook")
    @PermitAll
    public CashBookReport cashBook(
            @QueryParam("userid") Long userid,
            @QueryParam("clienttype") Enums.ClientType clienttype,
            @QueryParam("clientid") Long clientid,
            @QueryParam("dateStart") String dateStart,
            @QueryParam("dateEnd") String dateEnd,
            @QueryParam("saleType") Enums.SaleType saleType, 
            @QueryParam("paymentType") Enums.PaymentType paymentType) {

        Date dateFrom = DateUtil.stringToDate(dateStart, "ddMMMyyyy");
        Date dateTo = DateUtil.stringToDate(dateEnd, "ddMMMyyyy");

        CashBookReport report = service.findCashBookReport(dateFrom, dateTo, userid, clienttype, clientid, saleType,paymentType);
        return report;
    }
}
