package com.ets.fe.client.gui;

import com.ets.fe.client.collection.Agents;
import com.ets.fe.client.ws.AgentWSClient;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JProgressBar;

/**
 *
 * @author Yusuf
 */
public class AgentSearchTask extends ContactableSearchTask {

    private String name = null;
    private String officeID = null;
    private String postCode = null;
    private JProgressBar progressBar;
    private String keyword = null;

    public AgentSearchTask(String name, String postCode, String officeID, JProgressBar progressBar) {
        this.name = name;
        this.officeID = officeID;
        this.postCode = postCode;
        this.progressBar = progressBar;
    }

    public AgentSearchTask(String keyword) {
        this.keyword = keyword;
        this.progressBar = progressBar;
    }

    public AgentSearchTask() {
 
    }
        
    @Override
    protected Agents doInBackground() {

        setProgress(10);
        AgentWSClient client = new AgentWSClient();

        Agents agents = null;
        if (keyword != null) {
            agents = client.find(keyword);
        } else {
            Progress p = new Progress();
            Thread t = new Thread(p);
            t.start();
            agents = client.find(name, postCode, officeID);
            p.cancel();
        }

        return agents;
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
