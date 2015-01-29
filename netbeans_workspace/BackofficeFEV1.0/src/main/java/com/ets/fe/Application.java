package com.ets.fe;

import com.ets.fe.app.model.AppSettings;
import com.ets.fe.app.model.User;
import com.ets.fe.client.model.MainAgent;
import com.ets.fe.os.model.AdditionalCharge;
import com.ets.fe.os.model.AdditionalCharges;
import com.ets.fe.settings.ws.ApplicationWSClient;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Yusuf
 */
public class Application {

    private static User loggedOnUser;
    private static MainAgent mainAgent;
    private static AppSettings appSettings;
    private static List<AdditionalCharge> additionalCharges = new ArrayList<>();
    private static Properties prop;

    public static void loadSettings() {
        ApplicationWSClient client = new ApplicationWSClient();
        setMainAgent(client.getMainAgent(mainAgent));
        setAppSettings(client.getSettings());
        setAdditionalCharges(client.getAdditionalCharges().getList());
        loadProperties();
    }

    public static void loadProperties() {
        try {
            InputStream sdkis = com.ets.fe.Application.class.getResourceAsStream("/settings.properties");
            setProp(new Properties());
            getProp().load(sdkis);
            sdkis.close();
        } catch (IOException ex) {
            Logger.getLogger(com.ets.fe.Application.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String get(String key) {
        return getProp().getProperty(key);
    }

    public static MainAgent getMainAgent() {
        return mainAgent;
    }

    public static User getLoggedOnUser() {
        return loggedOnUser;
    }

    public static void setLoggedOnUser(User aLoggedOnUser) {
        loggedOnUser = aLoggedOnUser;
    }

    public static AppSettings getAppSettings() {
        return appSettings;
    }

    public static void setAppSettings(AppSettings aAppSettings) {
        appSettings = aAppSettings;
    }

    public static Properties getProp() {
        return prop;
    }

    public static void setProp(Properties aProp) {
        prop = aProp;
    }

    public static void setMainAgent(MainAgent aMainAgent) {
        mainAgent = aMainAgent;
    }

    public static AdditionalCharge getCardHandlingFee() {
        AdditionalCharge charge = null;
        for (AdditionalCharge c : additionalCharges) {
            if (c.getTitle().equals("Card Handling")) {
                charge = c;
                break;
            }
        }
        return charge;
    }

    public static AdditionalCharge getPostage() {
        AdditionalCharge charge = null;
        for (AdditionalCharge c : additionalCharges) {
            if (c.getTitle().equals("Postage")) {
                charge = c;
                break;
            }
        }
        return charge;
    }
    
    public static AdditionalCharge getOther() {
        AdditionalCharge charge = null;
        for (AdditionalCharge c : additionalCharges) {
            if (c.getTitle().equals("Other")) {
                charge = c;
                break;
            }
        }
        return charge;
    }
        
    public static List<AdditionalCharge> getAdditionalCharges() {
        return additionalCharges;
    }

    public static void setAdditionalCharges(List<AdditionalCharge> aAdditionalCharges) {
        additionalCharges = aAdditionalCharges;
    }
}
