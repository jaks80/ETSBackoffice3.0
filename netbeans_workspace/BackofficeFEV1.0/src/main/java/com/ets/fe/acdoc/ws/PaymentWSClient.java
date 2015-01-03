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

    public Payments update(Payments payments) {
        String url = APIConfig.get("ws.pay.newbulk");
        Payments ppayment = RestClientUtil.postEntity(Payments.class, url, payments);
        return ppayment;
    }
}
