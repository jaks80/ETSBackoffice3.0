package com.ets.fe.acdoc.task;

import com.ets.fe.accounts.task.AccountsHistoryTask;
import com.ets.fe.acdoc.ws.TicketingPAcDocWSClient;
import com.ets.fe.acdoc.ws.TicketingSAcDocWSClient;
import com.ets.fe.acdoc_o.ws.OtherSAcDocWSClient;
import com.ets.fe.client.task.AgentSearchTask;
import com.ets.fe.productivity.model.ProductivityReport;
import com.ets.fe.util.Enums;
import com.ets.fe.util.Enums.SaleType;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

/**
 *
 * @author Yusuf
 */
public class AllAgentsDueSummeryTask extends SwingWorker<ProductivityReport, Integer> {

    private JProgressBar progressBar;
    private SaleType saleType;
    private Date dateFrom;
    private Date dateTo;

    public AllAgentsDueSummeryTask(SaleType saleType, Date dateFrom, Date dateTo, JProgressBar progressBar) {
        this.progressBar = progressBar;
        this.saleType = saleType;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
    }

    @Override
    protected ProductivityReport doInBackground() throws Exception {

        setProgress(10);
        Progress p = new Progress();
        Thread t = new Thread(p);
        t.start();

        ProductivityReport report = null;

        if (saleType.equals(Enums.SaleType.TKTSALES)) {
            TicketingSAcDocWSClient client = new TicketingSAcDocWSClient();
            report = client.allAgentDueReport(dateFrom, dateTo);
        } else if (saleType.equals(Enums.SaleType.TKTPURCHASE)) {
            TicketingPAcDocWSClient client = new TicketingPAcDocWSClient();
            report = client.allAgentDueReport(dateFrom, dateTo);
        } else if (saleType.equals(Enums.SaleType.OTHERSALES)) {
            OtherSAcDocWSClient client = new OtherSAcDocWSClient();
            report = client.allAgentDueReport(dateFrom, dateTo);
        }
        p.cancel();
        return report;
    }

    @Override
    protected void done() {
        progressBar.setIndeterminate(false);
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
