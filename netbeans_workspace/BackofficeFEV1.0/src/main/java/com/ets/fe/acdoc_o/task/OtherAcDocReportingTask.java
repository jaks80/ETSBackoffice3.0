package com.ets.fe.acdoc_o.task;

import com.ets.fe.acdoc_o.model.report.InvoiceReportOther;
import com.ets.fe.acdoc_o.ws.OtherSAcDocWSClient;
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
public class OtherAcDocReportingTask extends SwingWorker<InvoiceReportOther, Integer> {

    private JProgressBar progressBar;
    private Enums.AcDocType doctype = null;
    private Enums.ClientType clienttype = null;
    private Long clientid = null;
    private Date dateFrom = null;
    private Date dateTo = null;

    public OtherAcDocReportingTask(Enums.AcDocType doctype, Enums.ClientType clienttype,
            Long clientid, Date dateFrom, Date dateTo,JProgressBar progressBar) {

        this.doctype = doctype;
        this.clienttype = clienttype;
        this.clientid = clientid;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.progressBar = progressBar;
    }

    @Override
    protected InvoiceReportOther doInBackground() throws Exception {
        setProgress(10);
        Progress p = new Progress();
        Thread t = new Thread(p);
        t.start();

        OtherSAcDocWSClient client = new OtherSAcDocWSClient();
        InvoiceReportOther report = null;

        if (doctype == null) {
            report = client.documentHistoryReport(clienttype, clientid, dateFrom, dateTo);
        } else {
            report = client.outstandingDocumentReport(doctype, clienttype, clientid, dateFrom, dateTo);
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
