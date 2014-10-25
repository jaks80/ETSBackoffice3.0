package etsbackoffice.businesslogic;

import etsbackoffice.datalogic.AuthenticationDao;
import etsbackoffice.domain.AppSettings;
import etsbackoffice.domain.MasterAgent;
import etsbackoffice.domain.User;

/**
 *
 * @author Yusuf
 */
public class AuthenticationBo {

    private static User loggedOnUser;
    private static MasterAgent mAgent;
    private static AppSettings appSettings;
    
    private AuthenticationDao authenticationDao;

    public AuthenticationBo() {
    }

    public static AppSettings getAppSettings() {
        return appSettings;
    }

    public static void setAppSettings(AppSettings aAppSettings) {
        appSettings = aAppSettings;
    }

    public void saveAppSettings() {
        getAuthenticationDao().saveAppSettings(getAppSettings());
    }

    public AppSettings loadAppSettings() {
        return getAuthenticationDao().getAppSettings();
    }

    public User checkLoginDetails(String loginID, String password) {
        AuthenticationBo.setLoggedOnUser(getAuthenticationDao().getUser(loginID, password));
        return AuthenticationBo.getLoggedOnUser();
    }

    public AuthenticationDao getAuthenticationDao() {
        return authenticationDao;
    }

    public void setAuthenticationDao(AuthenticationDao authenticationDao) {
        this.authenticationDao = authenticationDao;
    }

    public static User getLoggedOnUser() {
        return loggedOnUser;
    }

    public static void setLoggedOnUser(User aLoggedOnUser) {
        loggedOnUser = aLoggedOnUser;
    }

    public static MasterAgent getmAgent() {
        return mAgent;
    }

    public static void setmAgent(MasterAgent amAgent) {
        mAgent = amAgent;
    }   
}
