package com.ets.fe.client.ws;

import com.ets.fe.APIConfig;
import com.ets.fe.client.collection.Agents;
import com.ets.fe.client.model.Agent;
import com.ets.fe.util.RestClientUtil;

/**
 *
 * @author Yusuf
 */
public class AgentWSClient {

    public Agents find(String name,String postCode,String officeID) {

        StringBuilder sb = new StringBuilder(APIConfig.get("ws.agent.agents"));
        
        if(name !=null && !name.isEmpty()){
            sb.append("?name=").append(name);         
        }
        
        if(postCode !=null && !postCode.isEmpty()){
            sb.append("?postCode=").append(postCode);
        }
        
        if(officeID !=null && !officeID.isEmpty()){
            sb.append("?officeID=").append(officeID);
        }
        
        return RestClientUtil.getEntity(Agents.class, sb.toString(), new Agents());

    }

    public Agents find(String keyword) {
        String url = APIConfig.get("ws.agent.keyword") +"?keyword="+keyword;
        return RestClientUtil.getEntity(Agents.class, url, new Agents());

    }
    
    public Agents findTicketingsAgent() {
        String url = APIConfig.get("ws.agent.ticketingagents");
        return RestClientUtil.getEntity(Agents.class, url, new Agents());

    }
        
    public Agent create(Agent agent) {
       String url = APIConfig.get("ws.agent.new");
       Agent persistedAgent = RestClientUtil.postEntity(Agent.class, url, agent);
       return persistedAgent;
    }
    
    public Agent update(Agent agent) {
        agent.recordUpdateBy();
        String url = APIConfig.get("ws.agent.update");
        Agent persistedAgent = RestClientUtil.putEntity(Agent.class, url, agent);
       return persistedAgent;
    }

    public Integer delete(long id) {
        String url = APIConfig.get("ws.agent.delete");
        Integer status = RestClientUtil.deleteById(url+id);
        return status;
    }
}
