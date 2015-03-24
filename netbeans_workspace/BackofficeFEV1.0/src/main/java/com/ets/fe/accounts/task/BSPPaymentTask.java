package com.ets.fe.accounts.task;

import com.ets.fe.accounts.ws.PaymentWSClient;
import java.util.Date;
import javax.swing.SwingWorker;

/**
 *
 * @author Yusuf
 */
public class BSPPaymentTask extends SwingWorker<String, Integer> {
    
    private Long agentid;
    private Date dateFrom;
    private Date dateTo;

    public BSPPaymentTask(Long agentid, Date dateFrom,Date dateTo) {
        this.agentid = agentid;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
    }

    @Override
    protected String doInBackground() throws Exception {

        PaymentWSClient client = new PaymentWSClient();
        Integer status = client.newBSPPayment(agentid, dateFrom, dateTo);
        
        return "Payment Success...";
    }

    @Override
    protected void done() {
        setProgress(100);
    }
}
