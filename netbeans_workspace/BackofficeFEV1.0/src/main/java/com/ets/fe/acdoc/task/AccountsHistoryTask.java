package com.ets.fe.acdoc.task;

import com.ets.fe.acdoc.model.AccountingDocument;
import com.ets.fe.acdoc.model.TicketingPurchaseAcDoc;
import com.ets.fe.acdoc.model.TicketingSalesAcDoc;
import com.ets.fe.acdoc.model.report.AccountsReport;
import com.ets.fe.acdoc.model.report.InvoiceReport;
import com.ets.fe.acdoc.ws.TicketingPAcDocWSClient;
import com.ets.fe.acdoc.ws.TicketingSAcDocWSClient;
import com.ets.fe.client.gui.AgentSearchTask;
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
public class AccountsHistoryTask extends SwingWorker<AccountsReport, Integer> {

    private JProgressBar progressBar;
    private Enums.SaleType saleType;   
    private Enums.ClientType clienttype = null;
    private Long clientid = null;
    private Date dateFrom = null;
    private Date dateTo = null;

    public AccountsHistoryTask(Enums.ClientType clienttype,
            Long clientid, Date dateFrom, Date dateTo, JProgressBar progressBar, Enums.SaleType saleType) {
        
        this.clienttype = clienttype;
        this.clientid = clientid;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.progressBar = progressBar;
        this.saleType = saleType;
    }

    @Override
    protected AccountsReport doInBackground() throws Exception {

        setProgress(10);
        Progress p = new Progress();
        Thread t = new Thread(p);
        t.start();

        AccountsReport report = null;

        if (Enums.SaleType.SALES.equals(saleType)) {
            TicketingSAcDocWSClient client = new TicketingSAcDocWSClient();
            report = client.salesAccountsReport(clienttype, clientid, dateFrom, dateTo);

        } else if (Enums.SaleType.PURCHASE.equals(saleType)) {
            TicketingPAcDocWSClient client = new TicketingPAcDocWSClient();
            report = client.purchaseAccountsReport(clientid, dateFrom, dateTo);
            
        } else if (Enums.SaleType.OTHER.equals(saleType)) {
            

        }

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
