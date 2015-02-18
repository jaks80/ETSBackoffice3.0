package com.ets.settings.service;

import com.ets.client.domain.MainAgent;
import com.ets.client.service.AgentService;
import com.ets.report.model.Letterhead;
import com.ets.settings.dao.AppSettingsDAO;
import com.ets.settings.domain.AppSettings;
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

    public static Letterhead letterhead;
    
    public MainAgent getMainAgent() {
        return agentService.getMainAgent();
    }

    public MainAgent saveorUpdateMainAgent(MainAgent agent) {
        return agentService.saveorUpdate(agent);
    }

    public AppSettings getSettings() {
        
        AppSettings settings = dao.findByID(AppSettings.class, Long.parseLong("1"));
        
        MainAgent agent = getMainAgent();
        letterhead = new Letterhead();
        letterhead.setCompanyName(agent.getName());
        letterhead.setAddress(agent.getFullAddressCRSeperated());

        StringBuilder sb = new StringBuilder();
        if (agent.getAtol() != null) {
            sb.append("ATOL: ").append(agent.getAtol()).append(" ");
        }

        if (agent.getIata() != null) {
            sb.append("IATA: ").append(agent.getIata()).append(" ");
        }

        if (agent.getAbta() != null) {
            sb.append("ABTA: ").append(agent.getAbta()).append(" ");
        }

        letterhead.setFooter(sb.toString());    
        letterhead.settInvTAndC(settings.gettInvTAndC());
        letterhead.setoInvTAndC(settings.getoInvTAndC());
        return settings;
    }

    public AppSettings saveorUpdate(AppSettings appSettings) {
        dao.save(appSettings);
        return appSettings;
    }

    public void delete(AppSettings appSettings) {
        dao.delete(appSettings);
    }
}
