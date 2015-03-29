package com.ets.fe.client.task;

import com.ets.fe.client.model.Agent;
import com.ets.fe.client.ws.AgentWSClient;
import javax.swing.SwingWorker;

/**
 *
 * @author Yusuf
 */
public class AgentTask extends SwingWorker<Void, Integer> {

    private Agent agent;

    public AgentTask(Agent agent) {
        this.agent = agent;
    }

    @Override
    protected Void doInBackground() {

        AgentWSClient client = new AgentWSClient();
        if (agent.getId() == null) {
            client.create(agent);
        } else {
            client.update(agent);
        }

        return null;
    }

    @Override
    protected void done() {
        setProgress(100);
        if (!isCancelled()) {

        }
    }
}
