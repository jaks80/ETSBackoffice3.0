package com.ets.fe.productivity.task;

import com.ets.fe.acdoc.ws.TicketingSAcDocWSClient;
import com.ets.fe.acdoc_o.ws.OtherSAcDocWSClient;
import com.ets.fe.client.task.AgentSearchTask;
import com.ets.fe.productivity.model.ProductivityReport;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

/**
 *
 * @author Yusuf
 */
public class UserProductivityTask extends SwingWorker<List<ProductivityReport>, Integer> {

    private JProgressBar progressBar;    
    private Date dateFrom = null;
    private Date dateTo = null;

    public UserProductivityTask(Date dateFrom, Date dateTo, JProgressBar progressBar) {
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.progressBar = progressBar;
    }

    @Override
    protected List<ProductivityReport> doInBackground() throws Exception {
        setProgress(10);
        Progress p = new Progress();
        Thread t = new Thread(p);
        t.start();

        OtherSAcDocWSClient oclient = new OtherSAcDocWSClient();
        TicketingSAcDocWSClient tclient = new TicketingSAcDocWSClient();

        ProductivityReport treport = tclient.userProducivityReport(dateFrom, dateTo);
        ProductivityReport oreport = oclient.userProducivityReport(dateFrom, dateTo);

        List<ProductivityReport> list = new ArrayList<>();
        list.add(treport);
        list.add(oreport);
        p.cancel();
        return list;
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
