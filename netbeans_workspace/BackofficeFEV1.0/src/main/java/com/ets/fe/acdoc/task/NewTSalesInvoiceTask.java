package com.ets.fe.acdoc.task;

import com.ets.fe.acdoc.model.TicketingSalesAcDoc;
import com.ets.fe.acdoc.ws.TicketingSAcDocWSClient;
import com.ets.fe.client.gui.AgentSearchTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

/**
 *
 * @author Yusuf
 */
public class NewTSalesInvoiceTask extends SwingWorker<TicketingSalesAcDoc, Integer> {

    private final Long pnrId;
    private JProgressBar progressBar;
    private TicketingSalesAcDoc invoice;

    public NewTSalesInvoiceTask(Long pnrId, JProgressBar progressBar) {
        this.pnrId = pnrId;
        this.progressBar = progressBar;
        this.invoice = null;
    }

    public NewTSalesInvoiceTask(TicketingSalesAcDoc draftInvoice, JProgressBar progressBar) {
        this.invoice = draftInvoice;
        this.progressBar = progressBar;
        this.pnrId = null;
    }

    @Override
    protected TicketingSalesAcDoc doInBackground() throws Exception {

        setProgress(10);
        Progress p = new Progress();
        Thread t = new Thread(p);
        t.start();

        TicketingSAcDocWSClient client = new TicketingSAcDocWSClient();

        if (invoice == null) {
            invoice = client.createNewDraftInvoice(pnrId);
        } else {
            if (invoice instanceof TicketingSalesAcDoc) {
                invoice = client.createNewInvoice(invoice);
            }
        }

        p.cancel();

        return invoice;
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
