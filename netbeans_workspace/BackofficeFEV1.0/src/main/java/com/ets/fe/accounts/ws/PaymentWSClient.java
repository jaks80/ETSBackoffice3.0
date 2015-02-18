package com.ets.fe.accounts.ws;

import com.ets.fe.APIConfig;
import com.ets.fe.accounts.model.Payment;
import com.ets.fe.accounts.model.Payments;
import com.ets.fe.util.DateUtil;
import com.ets.fe.util.Enums;
import com.ets.fe.util.RestClientUtil;
import java.util.Date;
import java.util.List;

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
        String url = APIConfig.get("ws.pay.bysinv") + invoiceid;
        Payments ppayment = RestClientUtil.getEntity(Payments.class, url, new Payments());
        return ppayment;
    }

    public Payments findTSPaymentHistory(Enums.ClientType clienttype, Long clientid, Date _dateFrom, Date _dateTo, Enums.SaleType saleType) {
        String dateFrom = DateUtil.dateToString(_dateFrom, "ddMMMyyyy");
        String dateTo = DateUtil.dateToString(_dateTo, "ddMMMyyyy");

        String url = "";
        
        if(!saleType.equals(Enums.SaleType.OTHER)){
         url = APIConfig.get("ws.pay.thistory") + "?dateStart=" + dateFrom + "&dateEnd=" + dateTo + "&saleType=" + saleType;
        }else{
         url = APIConfig.get("ws.pay.ohistory") + "?dateStart=" + dateFrom + "&dateEnd=" + dateTo + "&saleType=" + saleType;
        }
        
        if (clienttype != null) {
            url = url + "&clienttype=" + clienttype;
        }
        if (clientid != null) {
            url = url + "&clientid=" + clientid;
        }

        Payments ppayment = RestClientUtil.getEntity(Payments.class, url, new Payments());
        return ppayment;
    }
}
