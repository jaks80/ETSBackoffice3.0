package com.ets.fe.client.task;

import com.ets.fe.client.model.Agent;
import com.ets.fe.client.ws.AgentWSClient;
import java.util.List;
import javax.swing.SwingWorker;

/**
 *
 * @author Yusuf
 */
public class AgentTask extends SwingWorker<Agent, Integer> {

    private Agent agent = null;
    private List<Agent> agents = null;

    public AgentTask(Agent agent) {
        this.agent = agent;
    }

    public AgentTask(List<Agent> agents) {
        this.agents = agents;
    }

    @Override
    protected Agent doInBackground() {

        AgentWSClient client = new AgentWSClient();
        int progress = 0;
        if (agents != null) {
            for (Agent a : agents) {
                client.create(a);
                progress++;
                float percentDone = (progress*100)/agents.size();
                setProgress((int)Math.round(percentDone));             
            }
        } else {
            if (agent.getId() == null) {
                client.create(agent);
            } else {
                client.update(agent);
            }
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
