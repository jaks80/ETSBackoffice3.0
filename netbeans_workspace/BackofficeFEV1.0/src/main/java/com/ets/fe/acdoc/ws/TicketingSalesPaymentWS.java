package com.ets.fe.acdoc.ws;

import com.ets.fe.APIConfig;
import com.ets.fe.acdoc.model.TicketingSalesPayment;
import com.ets.fe.acdoc.model.collection.TicketingSalesPayments;
import com.ets.fe.util.RestClientUtil;

/**
 *
 * @author Yusuf
 */

public class TicketingSalesPaymentWS {
    public TicketingSalesPayment create(TicketingSalesPayment payment) {
        String url = APIConfig.get("ws.tspay.new");
        TicketingSalesPayment ppayment = RestClientUtil.postEntity(TicketingSalesPayment.class, url, payment);
        return ppayment;
    }

    public TicketingSalesPayments update(TicketingSalesPayments payments) {
        String url = APIConfig.get("ws.tspay.newbulk");
        TicketingSalesPayments ppayment = RestClientUtil.postEntity(TicketingSalesPayments.class, url, payments);
        return ppayment;
    }
}
