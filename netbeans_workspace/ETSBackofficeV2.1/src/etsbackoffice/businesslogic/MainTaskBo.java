package etsbackoffice.businesslogic;

import etsbackoffice.datalogic.AgentDao;
import etsbackoffice.datalogic.CustomerDao;
import etsbackoffice.domain.Agent;
import etsbackoffice.domain.Customer;
import etsbackoffice.domain.PNR;
import etsbackoffice.ETSBackofficeApp;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public class MainTaskBo {

    AgentDao agentDao = (AgentDao) ETSBackofficeApp.getApplication().ctx.getBean("agentDao");
    CustomerDao customerDao = (CustomerDao) ETSBackofficeApp.getApplication().ctx.getBean("customerDao");
    
    private static List<Agent> agents;
    private static List<Customer> customers;
    private List<PNR> pnrs;
    
    public MainTaskBo(){
        
    }
    
  public void loadAgents(){
      MainTaskBo.agents = agentDao.findAllSummery();      
  }
  
  public void loadCustomers(){
      MainTaskBo.customers = customerDao.findAll();      
  }
  
  
}
