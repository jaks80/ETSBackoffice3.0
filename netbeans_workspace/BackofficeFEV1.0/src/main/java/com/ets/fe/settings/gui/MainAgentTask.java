package com.ets.fe.settings.gui;

import com.ets.fe.Application;
import com.ets.fe.client.model.MainAgent;
import com.ets.fe.settings.ws.ApplicationWSClient;
import javax.swing.SwingWorker;

/**
 *
 * @author Yusuf
 */
public class MainAgentTask extends SwingWorker<MainAgent, Integer> {

    private final MainAgent agent;

    public MainAgentTask(MainAgent agent) {
        this.agent = agent;
    }

    @Override
    protected MainAgent doInBackground() {

        ApplicationWSClient client = new ApplicationWSClient();
        client.updateMainAgent(agent);

        MainAgent persistedAgent = client.getMainAgent(agent);   
        Application.setMainAgent(persistedAgent);
        return persistedAgent;
    }

    @Override
    protected void done() {
        setProgress(100);
        if (!isCancelled()) {

        }
    }
}
