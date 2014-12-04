package com.ets.client.service;

import com.ets.client.dao.AgentDAO;
import com.ets.client.domain.Agent;
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
    
    public List<Agent> findAll(){    
        return dao.findAll(Agent.class);
    }
    
    public List<Agent> findAll(String name, String pCode, String officeID){    
        return dao.findByLike(name, pCode, officeID);
    }
    
    public Agent saveorUpdate(Agent agent){
     dao.save(agent);
     return agent;
    }
    
    public void delete(Agent agent){
     dao.delete(agent);
    }
    
}
