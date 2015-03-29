package com.ets.fe.acdoc.task;

import com.ets.fe.acdoc.model.TicketingPurchaseAcDoc;
import com.ets.fe.acdoc.ws.TicketingPAcDocWSClient;
import com.ets.fe.client.task.AgentSearchTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

/**
 *
 * @author Yusuf
 */
public class TktingPurchaseDocTask extends SwingWorker<TicketingPurchaseAcDoc, Integer> {

    private final Long pnrId;
    private JProgressBar progressBar;
    private TicketingPurchaseAcDoc document;

    public TktingPurchaseDocTask(Long pnrId, JProgressBar progressBar) {
        this.pnrId = pnrId;
        this.progressBar = progressBar;
        this.document = null;
    }

    public TktingPurchaseDocTask(TicketingPurchaseAcDoc draftDocument, JProgressBar progressBar) {
        this.document = draftDocument;
        this.progressBar = progressBar;
        this.pnrId = null;
    }

    @Override
    protected TicketingPurchaseAcDoc doInBackground() throws Exception {

        setProgress(10);
        Progress p = new Progress();
        Thread t = new Thread(p);
        t.start();

        TicketingPAcDocWSClient client = new TicketingPAcDocWSClient();

        if (document.getId() == null) {
            document = client.create(document);
        } else {
            document = client.update(document);
        }

        p.cancel();
        return document;
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
