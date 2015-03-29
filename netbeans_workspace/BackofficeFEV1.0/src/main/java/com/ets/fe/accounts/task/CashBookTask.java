package com.ets.fe.accounts.task;

import com.ets.fe.accounts.model.CashBookReport;
import com.ets.fe.accounts.ws.PaymentWSClient;
import com.ets.fe.client.task.AgentSearchTask;
import com.ets.fe.util.Enums;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

/**
 *
 * @author Yusuf
 */
public class CashBookTask extends SwingWorker<CashBookReport, Integer> {

    private JProgressBar progressBar;
    private Long userid;
    private Enums.SaleType saleType=null;   
    private Enums.ClientType clienttype = null;
    private Enums.PaymentType paymentType = null;
    private Long clientid = null;
    private Date dateFrom = null;
    private Date dateTo = null;    

    public CashBookTask(Long userid,Enums.ClientType clienttype,
            Long clientid, Date dateFrom, Date dateTo, JProgressBar progressBar, Enums.SaleType saleType,Enums.PaymentType paymentType) {
        
        this.userid = userid;
        this.clienttype = clienttype;
        this.clientid = clientid;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.progressBar = progressBar;
        this.saleType = saleType;
        this.paymentType = paymentType;
    }   

    @Override
    protected CashBookReport doInBackground() throws Exception {

        setProgress(10);
        Progress p = new Progress();
        Thread t = new Thread(p);
        t.start();

        PaymentWSClient client = new PaymentWSClient();        
        CashBookReport report = client.cashBook(userid, clienttype, clientid, dateFrom, dateTo, saleType,paymentType);

        p.cancel();
        return report;
    }

    @Override
    protected void done() {
        setProgress(100);
    }

    private class Progress implements Runnable {

        private volatile boolean stop = false;

        @Override
        public void run() {
            int i = 10;
            do {
                if (!stop) {
                    setProgress(i);
                    i++;
                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(AgentSearchTask.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    if (i == 99) {
                        progressBar.setIndeterminate(true);
                        break;
                    }
                }
            } while (!stop);
        }

        public void cancel() {
            stop = true;
        }
    }
}
