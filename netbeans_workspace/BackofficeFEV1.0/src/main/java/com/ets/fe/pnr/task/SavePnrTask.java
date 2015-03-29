package com.ets.fe.pnr.task;

import com.ets.fe.client.task.AgentSearchTask;
import com.ets.fe.pnr.model.Pnr;
import com.ets.fe.pnr.ws.PnrWSClient;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

/**
 *
 * @author Yusuf
 */
public class SavePnrTask extends SwingWorker<Pnr, Integer> {

    private final Pnr pnr;
    private JProgressBar progressBar;

    public SavePnrTask(Pnr pnr, JProgressBar progressBar) {
        this.pnr = pnr;
        this.progressBar = progressBar;
    }

    @Override
    protected Pnr doInBackground() {

        setProgress(10);    
        Progress p = new Progress();
        Thread t = new Thread(p);
        t.start();

        PnrWSClient client = new PnrWSClient();
        Pnr persistedpnr = client.update(pnr);
        p.cancel();
        return persistedpnr;
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
