package com.ets.fe;

import com.ets.fe.settings.model.AppSettings;
import com.ets.fe.settings.model.User;
import com.ets.fe.client.model.MainAgent;
import com.ets.fe.os.model.AdditionalCharge;
import com.ets.fe.report.model.Letterhead;
import com.ets.fe.settings.ws.ApplicationWSClient;
import com.itextpdf.xmp.impl.Base64;
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

    private static Letterhead letterhead;
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
            getLetterhead().settInvTAndC(appSettings.gettInvTAndC());
            getLetterhead().setoInvTAndC(appSettings.getoInvTAndC());
        }
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

    public static String getUserPassowrdEncoded() {
        String userpassword = "";
        if (loggedOnUser != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(loggedOnUser.getLoginID());
            sb.append(":");
            sb.append(loggedOnUser.getPassword());
            userpassword = sb.toString();
        }
        String encodedString = Base64.encode(userpassword);
        return encodedString;
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

    public static Letterhead getLetterhead() {
        return letterhead;
    }
}
