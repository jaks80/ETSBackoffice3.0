package com.ets.fe.client.task;

import com.ets.fe.client.model.Agent;
import com.ets.fe.client.ws.AgentWSClient;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/**
 *
 * @author Yusuf
 */
public class AgentTask extends SwingWorker<Agent, Integer> {

    private Agent agent = null;
    private List<Agent> agents = null;
    private String taskType = "";
    private int httpStatus = 0;
    private Long agentid;

    public AgentTask(Agent agent, String taskType) {
        this.agent = agent;
        this.taskType = taskType;
    }

    public AgentTask(List<Agent> agents, String taskType) {
        this.agents = agents;
        this.taskType = taskType;
    }
    
    public AgentTask(Long agentid, String taskType) {
        this.agentid = agentid;
        this.taskType = taskType;
    }

    @Override
    protected Agent doInBackground() {

        AgentWSClient client = new AgentWSClient();
        int progress = 0;
        if (taskType.equals("DELETE")) {
            httpStatus = client.delete(agentid);
        } else if (taskType.equals("BULKCREATE")) {
            for (Agent a : agents) {
                client.create(a);
                progress++;
                float percentDone = (progress * 100) / agents.size();
                setProgress((int) Math.round(percentDone));
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
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
