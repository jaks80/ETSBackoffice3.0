package com.ets.fe.acdoc.ws;

import com.ets.fe.APIConfig;
import com.ets.fe.acdoc.model.TicketingPurchasePayment;
import com.ets.fe.acdoc.model.collection.TicketingPurchasePayments;
import com.ets.fe.util.RestClientUtil;

/**
 *
 * @author Yusuf
 */
public class TicketingPurchasePaymentWS {
    public TicketingPurchasePayment create(TicketingPurchasePayment payment) {

        String url = APIConfig.get("ws.tppay.new");
        TicketingPurchasePayment ppayment = RestClientUtil.postEntity(TicketingPurchasePayment.class, url, payment);
        return ppayment;
    }

    public TicketingPurchasePayments update(TicketingPurchasePayments payments) {
        String url = APIConfig.get("ws.tppay.newbulk");
        TicketingPurchasePayments ppayment = RestClientUtil.postEntity(TicketingPurchasePayments.class, url, payments);
        return ppayment;
    }
}
