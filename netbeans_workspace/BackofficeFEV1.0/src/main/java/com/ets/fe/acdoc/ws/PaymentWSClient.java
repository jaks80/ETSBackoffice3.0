package com.ets.fe.acdoc.ws;

import com.ets.fe.APIConfig;
import com.ets.fe.acdoc.model.Payment;
import com.ets.fe.acdoc.model.collection.Payments;
import com.ets.fe.util.RestClientUtil;

/**
 *
 * @author Yusuf
 */
public class PaymentWSClient {

    public Payment create(Payment payment) {
        String url = APIConfig.get("ws.pay.new");
        Payment ppayment = RestClientUtil.postEntity(Payment.class, url, payment);
        return ppayment;
    }

    public Payments paymentBySalesInvoice(Long invoiceid) {
        String url = APIConfig.get("ws.pay.bysinv")+invoiceid;
        Payments ppayment = RestClientUtil.getEntity(Payments.class, url, new Payments());
        return ppayment;
    }
}
