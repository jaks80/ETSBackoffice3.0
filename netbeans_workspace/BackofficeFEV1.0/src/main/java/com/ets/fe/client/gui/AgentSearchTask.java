package com.ets.fe.client.gui;

import com.ets.fe.client.collection.Agents;
import com.ets.fe.client.ws.AgentWSClient;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

/**
 *
 * @author Yusuf
 */
public class AgentSearchTask extends SwingWorker<Agents, Integer> {

    private String name = "";
    private String officeID = "";
    private String postCode = "";
    private JProgressBar progressBar;

    public AgentSearchTask(String name,String postCode,String officeID,JProgressBar progressBar) {
        this.name = name;
        this.officeID = officeID;
        this.postCode = postCode;
        this.progressBar = progressBar;
    }

    @Override
    protected Agents doInBackground() {

        setProgress(10);
        AgentWSClient client = new AgentWSClient();

        Progress p = new Progress();
        Thread t = new Thread(p);
        t.start();

        Agents agents = client.find(name, postCode, officeID);
        p.cancel();
        return agents;
    }

    @Override
    protected void done() {
        progressBar.setIndeterminate(false);
        setProgress(100);
        if (!isCancelled()) {

        }
    }

    private class Progress implements Runnable{

        private volatile boolean stop = false;

        @Override
        public void run() {
            int i =10;
            do{
                if (!stop) {
                    setProgress(i);
                    i++;
                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(AgentSearchTask.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    if(i==99){
                    progressBar.setIndeterminate(true);
                    break;
                    }
                }
            }while(!stop);
        }

        public void cancel(){
         stop = true;
        }
    }
}
