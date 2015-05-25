package com.ets.fe.accounts.ws;

import com.ets.fe.APIConfig;
import com.ets.fe.Application;
import com.ets.fe.accounts.model.*;
import com.ets.fe.util.DateUtil;
import com.ets.fe.util.Enums;
import com.ets.fe.util.RestClientUtil;
import java.util.Date;
import javax.swing.JOptionPane;
import org.apache.http.HttpResponse;

/**
 *
 * @author Yusuf
 */
public class PaymentWSClient {

    public Integer newBSPPayment(Long agentid, Date _dateFrom,
            Date _dateTo) {

        String dateFrom = DateUtil.dateToString(_dateFrom, "ddMMMyyyy");
        String dateTo = DateUtil.dateToString(_dateTo, "ddMMMyyyy");
        String _paymentDate = DateUtil.dateToString(new java.util.Date(), "ddMMMyyyy");

        StringBuilder sb = new StringBuilder(APIConfig.get("ws.pay.newbsppay"));
        sb.append("?");
        sb.append("agentid=").append(agentid);
        sb.append("&dateStart=").append(dateFrom);
        sb.append("&dateEnd=").append(dateTo);
        sb.append("&paymentDate=").append(_paymentDate);
        sb.append("&userid=").append(Application.getLoggedOnUser().getId());

        Integer status = RestClientUtil.postEntityReturnStatus(Payment.class, sb.toString(), new Payment());
        return status;
    }

    public Payment create(Payment payment) {
        String url = APIConfig.get("ws.pay.new");
        Payment ppayment = RestClientUtil.postEntity(Payment.class, url, payment);
        return ppayment;
    }

    public Payment voidPayment(Long paymentid) {
        String url = APIConfig.get("ws.pay.void") + "?paymentid=" + paymentid;
        Payment ppayment = RestClientUtil.putEntity(Payment.class, url, new Payment());
        return ppayment;
    }

    public Integer deletePayment(Long paymentid) {
        String url = APIConfig.get("ws.pay.delete") + "?paymentid=" + paymentid;
        
        HttpResponse response = RestClientUtil.deleteByIdGetResponse(url);
        int status = response.getStatusLine().getStatusCode();
        RestClientUtil.showMessage(response,"Delete Payment");
        
        return status;
    }

    public Integer createCreditTransfer(CreditTransfer creditTransfer) {
        StringBuilder sb = new StringBuilder();
        sb.append(APIConfig.get("ws.pay.newctransfer"));

        return RestClientUtil.postEntityReturnStatus(CreditTransfer.class, sb.toString(), creditTransfer);
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

    public TransactionReceipts findReceiptHistory(Enums.ClientType clienttype, Long clientid, Date _dateFrom, Date _dateTo, Enums.SaleType saleType) {
        String dateFrom = DateUtil.dateToString(_dateFrom, "ddMMMyyyy");
        String dateTo = DateUtil.dateToString(_dateTo, "ddMMMyyyy");

        StringBuilder sb = new StringBuilder();

        if (saleType.equals(Enums.SaleType.OTHERSALES)) {
            sb.append(APIConfig.get("ws.pay.oreceiptshistory")).append("?dateStart=").append(dateFrom).append("&dateEnd=").append(dateTo).append("&saleType=").append(saleType);
        } else {
            sb.append(APIConfig.get("ws.pay.treceiptshistory")).append("?dateStart=").append(dateFrom).append("&dateEnd=").append(dateTo).append("&saleType=").append(saleType);
        }

        if (clienttype != null) {
            sb.append("&clienttype=").append(clienttype);
        }
        if (clientid != null) {
            sb.append("&clientid=").append(clientid);
        }

        TransactionReceipts receipts = RestClientUtil.getEntity(TransactionReceipts.class, sb.toString(), new TransactionReceipts());
        return receipts;
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
