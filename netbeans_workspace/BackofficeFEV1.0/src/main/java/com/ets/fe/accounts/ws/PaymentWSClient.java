package com.ets.fe.accounts.ws;

import com.ets.fe.APIConfig;
import com.ets.fe.accounts.model.CashBookReport;
import com.ets.fe.accounts.model.Payment;
import com.ets.fe.accounts.model.Payments;
import com.ets.fe.util.DateUtil;
import com.ets.fe.util.Enums;
import com.ets.fe.util.RestClientUtil;
import java.util.Date;

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

        StringBuilder sb = new StringBuilder();

        if (!saleType.equals(Enums.SaleType.OTHERSALES)) {
            sb.append(APIConfig.get("ws.pay.thistory")).append("?dateStart=").append(dateFrom).append("&dateEnd=").append(dateTo).append("&saleType=").append(saleType);
        } else {
            sb.append(APIConfig.get("ws.pay.ohistory")).append("?dateStart=").append(dateFrom).append("&dateEnd=").append(dateTo).append("&saleType=").append(saleType);
        }

        if (clienttype != null) {
            sb.append("&clienttype=").append(clienttype);
        }
        if (clientid != null) {
            sb.append("&clientid=").append(clientid);
        }

        Payments ppayment = RestClientUtil.getEntity(Payments.class, sb.toString(), new Payments());
        return ppayment;
    }

    public CashBookReport cashBook(Long userid, Enums.ClientType clienttype, Long clientid,
            Date _dateFrom, Date _dateTo, Enums.SaleType saleType, Enums.PaymentType paymentType) {

        String dateFrom = DateUtil.dateToString(_dateFrom, "ddMMMyyyy");
        String dateTo = DateUtil.dateToString(_dateTo, "ddMMMyyyy");

        StringBuilder sb = new StringBuilder();

        sb.append(APIConfig.get("ws.pay.cashbook"))
                .append("?dateStart=").append(dateFrom)
                .append("&dateEnd=").append(dateTo);

        if (userid != null) {
            sb.append("&userid=").append(userid);
        }
        if (saleType != null) {
            sb.append("&saleType=").append(saleType);
        }
        if (paymentType != null) {
            sb.append("&paymentType=").append(paymentType);
        }
        if (clienttype != null) {
            sb.append("&clienttype=").append(clienttype);
        }
        if (clientid != null) {
            sb.append("&clientid=").append(clientid);
        }

        CashBookReport report = RestClientUtil.getEntity(CashBookReport.class, sb.toString(), new CashBookReport());
        return report;
    }
}
