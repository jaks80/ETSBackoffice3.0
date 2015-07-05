package com.ets.fe.settings.ws;

import com.ets.fe.APIConfig;
import com.ets.fe.settings.model.AppSettings;
import com.ets.fe.settings.model.User;
import com.ets.fe.client.model.MainAgent;
import com.ets.fe.os.model.AdditionalCharges;
import com.ets.fe.util.RestClientUtil;

/**
 *
 * @author Yusuf
 */
public class ApplicationWSClient {

    public User login(String loginId, String password, String newPassword) {
        String url = APIConfig.get("ws.user.login");
        
        User user = new User();
        user.setLoginID(loginId);
        user.setPassword(password);
        user.setNewPassword(newPassword);
        System.out.println("Property 1>>>:"+url);               
        user = RestClientUtil.postEntity(User.class, url, user);
        return user;
    }

    public void logout(User user) {
        String url = APIConfig.get("ws.user.logout");    
        RestClientUtil.postEntity(User.class, url, user);        
    }
    
    public MainAgent getMainAgent(MainAgent agent) {
        String url = APIConfig.get("ws.aps.mainagent");
        MainAgent persistedAgent = RestClientUtil.getEntity(MainAgent.class, url, agent);
        return persistedAgent;
    }

    public MainAgent updateMainAgent(MainAgent agent) {
        String url = APIConfig.get("ws.aps.updatemainagent");
        agent.recordUpdateBy();        
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
        appSettings.recordUpdateBy();
        AppSettings persistedSettings = RestClientUtil.putEntity(AppSettings.class, url, appSettings);
        return persistedSettings;
    }
}
