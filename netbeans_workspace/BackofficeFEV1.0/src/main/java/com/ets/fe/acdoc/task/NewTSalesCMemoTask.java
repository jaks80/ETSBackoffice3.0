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
public class NewTSalesCMemoTask extends SwingWorker<TicketingSalesAcDoc, Integer> {

    private final Long pnrId;
    private JProgressBar progressBar;
    private TicketingSalesAcDoc creditMemo;

    public NewTSalesCMemoTask(Long pnrId, JProgressBar progressBar) {
        this.pnrId = pnrId;
        this.progressBar = progressBar;
        this.creditMemo = null;
    }

    public NewTSalesCMemoTask(TicketingSalesAcDoc creditMemo, JProgressBar progressBar) {
        this.creditMemo = creditMemo;
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

        if (creditMemo == null) {
            creditMemo = client.newDraftTCreditMemo(pnrId);
        } else {
            creditMemo = client.newSalesDocument(creditMemo);
        }
        p.cancel();

        return creditMemo;
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
