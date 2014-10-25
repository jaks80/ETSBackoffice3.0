package etsbackoffice.datalogic;

import etsbackoffice.domain.Agent;
import java.util.List;

public interface AgentDao {

    public void store(Agent agent);

    public void delete(int agtID);    

    public Agent findByID(long agtID);

    public List<Agent> searchByAgtNameLike(String name);

    public List<Agent> searchByPCodeLike(String pCode);

    public Agent findByOfficeID(String officeID);

    public List<Agent> findByOfficeIDs(String officeID);
    
    public List<Agent> findAgent(String element,int type);

    public List<Agent> findAllSummery();   

    public Agent findCompleteAgentByID(long agtID);
    
    public List findAgentNameList();

    public List<Agent> findVendors();
    
    public List<Agent> findTicketingAgents();

}
