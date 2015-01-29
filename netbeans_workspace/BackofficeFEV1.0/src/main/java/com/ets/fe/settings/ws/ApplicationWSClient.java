package com.ets.fe.settings.ws;

import com.ets.fe.APIConfig;
import com.ets.fe.app.model.AppSettings;
import com.ets.fe.client.model.MainAgent;
import com.ets.fe.os.model.AdditionalCharges;
import com.ets.fe.util.RestClientUtil;

/**
 *
 * @author Yusuf
 */
public class ApplicationWSClient {

    public MainAgent getMainAgent(MainAgent agent) {
        String url = APIConfig.get("ws.aps.mainagent");
        MainAgent persistedAgent = RestClientUtil.getEntity(MainAgent.class, url, agent);
        return persistedAgent;
    }

    public MainAgent updateMainAgent(MainAgent agent) {
        String url = APIConfig.get("ws.aps.updatemainagent");
        MainAgent persistedAgent = RestClientUtil.putEntity(MainAgent.class, url, agent);
        return persistedAgent;
    }

    public AppSettings getSettings() {

        String url = APIConfig.get("ws.aps.find");
        AppSettings persistedSettings = RestClientUtil.getEntity(AppSettings.class, url, new AppSettings());
        return persistedSettings;
    }

    public AdditionalCharges getAdditionalCharges() {

        String url = APIConfig.get("ws.aps.charges");
        AdditionalCharges charges = RestClientUtil.getEntity(AdditionalCharges.class, url, new AdditionalCharges());
        return charges;
    }

    public AppSettings update(AppSettings appSettings) {
        String url = APIConfig.get("ws.aps.update");
        AppSettings persistedSettings = RestClientUtil.putEntity(AppSettings.class, url, appSettings);
        return persistedSettings;
    }
}
