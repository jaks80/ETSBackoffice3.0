package com.ets.client.dao;

import com.ets.GenericDAO;
import com.ets.client.domain.Agent;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public interface AgentDAO extends GenericDAO<Agent, Long>{
    
    public Agent findByOfficeID(String officeID);
    
    public Agent findCompleteAgentByID(long agtID);
    
    public List<Agent> findByLike(String name, String pCode, String officeID);               
    
    public List findAgentNameList();

    public List<Agent> findVendors();
    
    public List<Agent> findTicketingAgents();
}
