package etsbackoffice.businesslogic;

import etsbackoffice.datalogic.AgentDao;
import etsbackoffice.domain.Agent;
import etsbackoffice.domain.OfficeID;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public class AgentBo {

    private Agent agent;
    private List<Agent> agents;
    private AgentDao agentDao;
    private List agentNameList;

    public AgentBo() {
    }

    public void save(Agent agent) {
        agentDao.store(agent);
    }

    public void delete(int agtID) {
    }

    public Agent findByID(long agtID) {

        return agentDao.findByID(agtID);
    }

    public Agent searchAgent(String officeID) {

        return agentDao.findByOfficeID(officeID);
    }

    public List<Agent> searchAgents(String officeID) {

        return agentDao.findByOfficeIDs(officeID);
    }

    public List<Agent> searchByAgtNameLike(String name) {
        return agentDao.searchByAgtNameLike(name);
    }

    public List<Agent> findAgent(String element, int type) {
        List<Agent> agtList = new ArrayList();
        if (type == 1) {
            agtList = agentDao.searchByAgtNameLike(element);
        } else if (type == 2) {
            agtList = agentDao.searchByPCodeLike(element);
        } else if (type == 3) {
            agtList = agentDao.findByOfficeIDs(element);
        }
        return agtList;
    }

    public Agent findCompleteAgentByID(long id) {
        return agentDao.findCompleteAgentByID(id);
    }

    public List<Agent> loadAll() {
        this.agents = agentDao.findAllSummery();
        return this.agents;
    }

    public List agentNameList() {
        this.agentNameList = agentDao.findAgentNameList();
        Collections.sort(this.agentNameList);
        return this.agentNameList;
    }

    public List<Agent> vendorList(){
     return agentDao.findVendors();
    }
    
    public List<Agent> ticketingAgtList(){
     return agentDao.findTicketingAgents();
    }
        
    public void setAgentDao(AgentDao agentDao) {
        this.agentDao = agentDao;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public Agent getAgent() {
        return agent;
    }

    public Agent getNewAgent() {
        Agent newAgent = new Agent();
        List<OfficeID> oids = new ArrayList();
        newAgent.setOfficeIDs(oids);
        return newAgent;
    }
}
