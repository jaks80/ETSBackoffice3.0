package com.ets.client.service;

import com.ets.client.dao.AgentDAO;
import com.ets.client.dao.MainAgentDAO;
import com.ets.client.domain.Agent;
import com.ets.client.domain.MainAgent;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 *
 * @author Yusuf
 */
@Service("agentService")
public class AgentService {

    @Resource(name = "agentDAO")
    private AgentDAO dao;
    @Resource(name = "mainAgentDAO")
    private MainAgentDAO mainAgentDAO;

    public MainAgent getMainAgent() {
        return mainAgentDAO.findByID(MainAgent.class, Long.parseLong("1"));
    }

    public Agent getAgent(Long id) {
        return dao.findByID(Agent.class, id);
    }

    public List<Agent> findAll() {
        return dao.findAll(Agent.class);
    }

    public List<Agent> findTicketingAgents() {
        return dao.findTicketingAgents();
    }

    public List<Agent> findAll(String name, String pCode, String officeID) {
        return dao.findByLike(name, pCode, officeID);
    }

    public MainAgent saveorUpdate(MainAgent agent) {
        //agent.setId(Long.parseLong("1"));
        mainAgentDAO.save(agent);
        return agent;
    }

    public Agent saveorUpdate(Agent agent) {
        dao.save(agent);
        return agent;
    }

    public void delete(Agent agent) {
        dao.delete(agent);
    }

}
