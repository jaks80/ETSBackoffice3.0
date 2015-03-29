package com.ets.fe.acdoc.task;

import com.ets.fe.acdoc.model.report.InvoiceReport;
import com.ets.fe.acdoc.ws.TicketingPAcDocWSClient;
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
public class PurchaseAcDocReportingTask extends SwingWorker<InvoiceReport, Integer> {

    private JProgressBar progressBar;
    private Enums.AcDocType doctype = null;    
    private Long agentid = null;
    private Date dateFrom = null;
    private Date dateTo = null;

    public PurchaseAcDocReportingTask(Enums.AcDocType doctype, Long agentid, Date dateFrom, Date dateTo,JProgressBar progressBar) {

        this.doctype = doctype;        
        this.agentid = agentid;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.progressBar = progressBar;
    }

    @Override
    protected InvoiceReport doInBackground() throws Exception {
        setProgress(10);
        Progress p = new Progress();
        Thread t = new Thread(p);
        t.start();

        TicketingPAcDocWSClient client = new TicketingPAcDocWSClient();
        InvoiceReport report = null;

        if (doctype == null) {
            report = client.documentHistoryReport(agentid, dateFrom, dateTo);
        } else {
            report = client.outstandingDocumentReport(doctype, agentid, dateFrom, dateTo);
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
