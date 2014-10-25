package etsbackoffice.datalogic;

import etsbackoffice.domain.Agent;
import etsbackoffice.domain.OfficeID;
import java.util.ArrayList;
import java.util.List;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

public class HibernateAgentDao extends HibernateDaoSupport implements AgentDao {

    @Transactional
    public void store(Agent agent) {
        getHibernateTemplate().saveOrUpdate(agent);
    }

    @Transactional
    public void delete(int agtID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Transactional(readOnly = true)
    public Agent findByID(long agtID) {
        Agent agt = new Agent();
        agt = getHibernateTemplate().get(Agent.class, agtID);
        getHibernateTemplate().initialize(agt.getOfficeIDs());
        return agt;
    }

    public List<Agent> searchByAgtNameLike(String name) {
        List<Agent> agents = new ArrayList();
        name = name.concat("%");
        
        String hql = "select agent  from Agent agent "
                + "where agent.name like ?";
        agents = getHibernateTemplate().find(hql,name);
        return agents;
    }

    public List<Agent> searchByPCodeLike(String pCode) {
        List<Agent> agents = new ArrayList();
        pCode = pCode.concat("%");

        String hql = "select agent  from Agent agent "
                + "where agent.postCode like ?";
        agents = getHibernateTemplate().find(hql, pCode);
        return agents;
    }

    public Agent findCompleteAgentByID(long agtID) {

        String hql = "select agent  from Agent agent "
                + "left join fetch agent.createdBy "
                + "left join fetch agent.lastModifiedBy "
                + "where agent.contactableId=?";
        Agent agent = (Agent) DataAccessUtils.uniqueResult(getHibernateTemplate().find(hql, agtID));
        getHibernateTemplate().initialize(agent.getOfficeIDs());
        return agent;
    }

    @Transactional(readOnly = true)
    public List<Agent> findAgent(String element, int type) {
        if (type == 1) {
            return getHibernateTemplate().find("from Agent agent where agent.name=?", element);
        } else if (type == 2) {
            return getHibernateTemplate().find("from Agent agent where agent.postCode=?", element);
        } else if (type == 3) {
            String hql = "select o.agent from OfficeID o where o.officeID = ?";
            return getHibernateTemplate().find(hql, element);
        } else {
            return null;
        }
    }

    @Transactional(readOnly = true)
    public List<Agent> findAllSummery() {
        return getHibernateTemplate().find("from Agent as agt order By agt.name");
    }
    
    public Agent findByOfficeID(String officeID) {
        
       OfficeID oid = (OfficeID) DataAccessUtils.uniqueResult(getHibernateTemplate().find(
               "from OfficeID oid "
                + "left join fetch oid.agent as agt "
                + "where oid.officeID=? group by oid ", officeID));
       if(oid!=null){
         return oid.getAgent();
        }else{
        return null;
        }
    }

    public List<Agent> findByOfficeIDs(String officeID) {

        List<OfficeID> oids = getHibernateTemplate().find("from OfficeID oid "
                + "left join fetch oid.agent "
                + "where oid.officeID=?", officeID);
        List<Agent> agents = new ArrayList<Agent>();

        for (OfficeID oid : oids) {
            agents.add(oid.getAgent());
        }
        return agents;
    }

    public List findAgentNameList() {

        List agentNameList = new ArrayList();
        String hql = "select agent.name,agent.postCode,agent.contactableId from Agent as agent";
        List results = getHibernateTemplate().find(hql);
        for (int i = 0; i < results.size(); i++) {
            Object[] objects = (Object[]) results.get(i);
            String name = (String) objects[0];
            String postCode = (String) objects[1];
            String id = objects[2].toString();
            agentNameList.add(name + "-" + postCode + "-" + id);
        }
        return agentNameList;
    }

    public List<Agent> findVendors() {
        String hql = "select agt from Agent as agt, PNR as p "
                +"inner join fetch agt.officeIDs as o "
                + "where p.ticketingAgtOID = o.officeID and agt.contactableId <>1 "                
                + "group by agt  order by agt.name ";
        List<Agent> vendors = getHibernateTemplate().find(hql);
        return vendors;
    }

    public List<Agent> findTicketingAgents() {
        String hql =  "select agt from Agent as agt, PNR as p "
                     +"inner join fetch agt.officeIDs as o "
                     +"where p.ticketingAgtOID = o.officeID group by o ";

        List<Agent> vendors = getHibernateTemplate().find(hql);
        return vendors;
    }
}
