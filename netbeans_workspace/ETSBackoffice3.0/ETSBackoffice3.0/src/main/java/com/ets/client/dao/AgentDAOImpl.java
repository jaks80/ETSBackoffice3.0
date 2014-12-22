package com.ets.client.dao;

import com.ets.GenericDAOImpl;
import com.ets.client.domain.Agent;
import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Yusuf
 */
@Service("agentDAO")
@Transactional
public class AgentDAOImpl extends GenericDAOImpl<Agent, Long> implements AgentDAO{    
    
    @Override
    public Agent findByOfficeID(String officeID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Agent findCompleteAgentByID(long agtID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Agent> findByLike(String name, String postCode, String officeID) {
        name = nullToEmptyValue(name).concat("%");        
        postCode = nullToEmptyValue(postCode).concat("%");
        officeID = nullToEmptyValue(officeID).concat("%");
        
        if(officeID.length()>2){
         officeID = "%".concat(officeID);
        }
        
        String hql = "from Agent a "
                + "where "
                + "a.name like :name and "               
                + "a.postCode like :postCode and "
                + "(a.officeID is null or a.officeID like :officeID) ";
        Query query = getSession().createQuery(hql);
        query.setParameter("name", name);       
        query.setParameter("postCode", postCode);
        query.setParameter("officeID", officeID);
        
        return query.list();
    }

    @Override
    public List findAgentNameList() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Agent> findTicketingAgents() {
        String hql = "select agt from Agent as agt, Pnr as p "               
                + "where p.ticketingAgtOid = agt.officeID "                
                + "group by agt order by agt.name ";
        
        Query query = getSession().createQuery(hql);
        return query.list();
    }        
}
