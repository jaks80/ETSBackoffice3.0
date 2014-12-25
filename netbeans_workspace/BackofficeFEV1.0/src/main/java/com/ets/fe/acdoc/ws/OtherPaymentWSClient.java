package com.ets.fe.acdoc.ws;

import com.ets.fe.APIConfig;
import com.ets.fe.acdoc.model.OtherSalesPayment;
import com.ets.fe.acdoc.model.collection.OtherSalesPayments;
import com.ets.fe.util.RestClientUtil;

/**
 *
 * @author Yusuf
 */
public class OtherPaymentWSClient {

    public OtherSalesPayment create(OtherSalesPayment payment) {
        String url = APIConfig.get("ws.opay.new");
        OtherSalesPayment ppayment = RestClientUtil.postEntity(OtherSalesPayment.class, url, payment);
        return ppayment;
    }

    public OtherSalesPayments update(OtherSalesPayments payments) {
        String url = APIConfig.get("ws.opay.newbulk");
        OtherSalesPayments ppayment = RestClientUtil.postEntity(OtherSalesPayments.class, url, payments);
        return ppayment;
    }
}
