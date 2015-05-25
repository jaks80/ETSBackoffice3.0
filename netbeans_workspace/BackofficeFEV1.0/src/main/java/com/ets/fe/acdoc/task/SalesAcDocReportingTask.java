package com.ets.fe.acdoc.task;

import com.ets.fe.acdoc.model.report.InvoiceReport;
import com.ets.fe.acdoc.ws.TicketingSAcDocWSClient;
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
public class SalesAcDocReportingTask extends SwingWorker<InvoiceReport, Integer> {

    private JProgressBar progressBar;
    private Enums.AcDocType doctype = null;
    private Enums.ClientType clienttype = null;
    private Long clientid = null;
    private Date dateFrom = null;
    private Date dateTo = null;
    private String reportType = "";

    public SalesAcDocReportingTask(String reportType, Enums.AcDocType doctype, Enums.ClientType clienttype,
            Long clientid, Date dateFrom, Date dateTo, JProgressBar progressBar) {

        this.reportType = reportType;
        this.doctype = doctype;
        this.clienttype = clienttype;
        this.clientid = clientid;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.progressBar = progressBar;
    }

    public SalesAcDocReportingTask(String reportType, Enums.ClientType clienttype, Long clientid, Date dateFrom,
            Date dateTo, JProgressBar progressBar) {

        this.reportType = reportType;
        this.clienttype = clienttype;
        this.clientid = clientid;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.progressBar = progressBar;
    }

    @Override
    protected InvoiceReport doInBackground() throws Exception {
        setProgress(10);
        Progress p=null;
        if (progressBar != null) {
            p = new Progress();
            Thread t = new Thread(p);
            t.start();
        }

        TicketingSAcDocWSClient client = new TicketingSAcDocWSClient();
        InvoiceReport report = null;

        if (reportType.equals("HISTORY")) {
            report = client.documentHistoryReport(clienttype, clientid, dateFrom, dateTo);
        } else if (reportType.equals("OUTSTANDING")) {
            report = client.outstandingDocumentReport(doctype, clienttype, clientid, dateFrom, dateTo);
        } else if (reportType.equals("UNPAID_FLIGHT")) {
            report = client.outstandingFlightReport(clienttype, clientid, dateFrom, dateTo);
        }

        if(p!=null){
         p.cancel();
        }
        return report;
    }

    @Override
    protected void done() {
        if (progressBar != null) {
            progressBar.setIndeterminate(false);
        }
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
