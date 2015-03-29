package com.ets.fe.pnr.task;

import com.ets.fe.client.task.AgentSearchTask;
import com.ets.fe.pnr.model.Airline;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import com.ets.fe.pnr.ws.AirlineWSClient;

/**
 *
 * @author Yusuf
 */
public class AirLineTask extends SwingWorker<Airline, Integer> {

    private JProgressBar progressBar;
    private Airline airline;
    private String airLineCode;

    public AirLineTask(Airline airline, JProgressBar progressBar) {
        this.airline = airline;
        this.progressBar = progressBar;
    }

    public AirLineTask(String airLineCode, JProgressBar progressBar) {
        this.airLineCode = airLineCode;
        this.progressBar = progressBar;
    }

    @Override
    protected Airline doInBackground() throws Exception {

        setProgress(10);
        Progress p = new Progress();
        Thread t = new Thread(p);
        t.start();

        AirlineWSClient client = new AirlineWSClient();
        if (airLineCode != null) {
            airline = client.find(airLineCode);
        } else {
            client.save(airline);
        }

        p.cancel();
        return airline;
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
