package com.ets.fe.acdoc_o.task;

import com.ets.fe.acdoc_o.model.OtherSalesAcDoc;
import com.ets.fe.acdoc_o.ws.OtherSAcDocWSClient;
import com.ets.fe.client.task.AgentSearchTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

/**
 *
 * @author Yusuf
 */
public class NewOSalesDocumentTask extends SwingWorker<OtherSalesAcDoc, Integer> {
   
    private JProgressBar progressBar;
    private OtherSalesAcDoc otherSalesAcDoc;

    public NewOSalesDocumentTask(Long pnrId, JProgressBar progressBar) {       
        this.progressBar = progressBar;
        this.otherSalesAcDoc = null;
    }

    public NewOSalesDocumentTask(OtherSalesAcDoc otherSalesAcDoc, JProgressBar progressBar) {
        this.otherSalesAcDoc = otherSalesAcDoc;
        this.progressBar = progressBar;       
    }

    @Override
    protected OtherSalesAcDoc doInBackground() throws Exception {

        setProgress(10);
        Progress p = new Progress();
        Thread t = new Thread(p);
        t.start();

        OtherSAcDocWSClient client = new OtherSAcDocWSClient();        
        otherSalesAcDoc = client.create(otherSalesAcDoc);
        
        p.cancel();
        return otherSalesAcDoc;
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
