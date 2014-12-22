package com.ets.settings.service;

import com.ets.client.domain.MainAgent;
import com.ets.client.service.AgentService;
import com.ets.settings.dao.AppSettingsDAO;
import com.ets.settings.domain.AppSettings;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Yusuf
 */
@Service("appSettingsService")
public class AppSettingsService {

    @Resource(name = "appSettingsDAO")
    private AppSettingsDAO dao;
    
    @Autowired
    AgentService agentService;

    public MainAgent getMainAgent(){
        return agentService.getMainAgent();
    }
    
    public MainAgent saveorUpdateMainAgent(MainAgent agent){
        return agentService.saveorUpdate(agent);
    }
            
    public AppSettings getSettings() {
        return dao.findByID(AppSettings.class, Long.parseLong("1"));
    }

    public AppSettings saveorUpdate(AppSettings appSettings) {
        dao.save(appSettings);
        return appSettings;
    }

    public void delete(AppSettings appSettings) {
        dao.delete(appSettings);
    }
}
