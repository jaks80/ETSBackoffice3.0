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
    private AgentService agentService;

    private static Letterhead letterhead;
    public static MainAgent mainAgent;

    public MainAgent getMainAgent() {
        return getAgentService().getMainAgent();
    }

    public MainAgent saveorUpdateMainAgent(MainAgent agent) {
        return getAgentService().saveorUpdate(agent);
    }

    public AppSettings getSettings() {

        AppSettings settings = dao.findByID(AppSettings.class, Long.parseLong("1"));
        if (settings == null) {
            return null;
        }

        mainAgent = getMainAgent();
        if (mainAgent != null) {
            letterhead = new Letterhead();
            getLetterhead().setCompanyName(mainAgent.getName());
            getLetterhead().setAddress(mainAgent.getFullAddressCRSeperated());

            StringBuilder sb = new StringBuilder();
            if (mainAgent.getAtol() != null && !mainAgent.getAtol().isEmpty()) {
                sb.append("ATOL: ").append(mainAgent.getAtol()).append(" ");
            }

            if (mainAgent.getIata() != null && !mainAgent.getIata().isEmpty()) {
                sb.append("IATA: ").append(mainAgent.getIata()).append(" ");
            }

            if (mainAgent.getAbta() != null && !mainAgent.getAbta().isEmpty()) {
                sb.append("ABTA: ").append(mainAgent.getAbta()).append(" ");
            }
            getLetterhead().setFooter(sb.toString());
            getLetterhead().settInvTAndC(settings.gettInvTAndC());
            getLetterhead().setoInvTAndC(settings.getoInvTAndC());
        }

        return settings;
    }

    public AppSettings saveorUpdate(AppSettings appSettings) {
        dao.save(appSettings);
        return appSettings;
    }

    public void delete(AppSettings appSettings) {
        dao.delete(appSettings);
    }

    public AgentService getAgentService() {
        return agentService;
    }

    public static Letterhead getLetterhead() {
        return letterhead;
    }
}
