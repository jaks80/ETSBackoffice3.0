package com.ets.security;

import com.ets.settings.domain.User;
import com.ets.util.Enums;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * For new login User will be added, there after every time login will be just
 * validated and last communication time will be updated.
 *
 * @author Yusuf
 */
public class LoginManager {

    private static Map<String, Login> loginList = new HashMap<>();
    private static final int LOGIN_EXPIRE_TIME = 30;//Minutes

    public synchronized static void addLogin(User user) {
        Login login = new Login();
        login.setUser(user);
        Date currentTime = new java.util.Date();

        login.setLoginTime(currentTime);
        login.setLastCommunication(currentTime);
        getLoginList().put(user.getLoginID(), login);
    }

    public synchronized static void removeLogin(String loginId) {
        loginList.remove(loginId);
    }

    public synchronized static User validateLogin(String loginId, String password) {
        Login login = getLoginList().get(loginId);

        if (login != null) {
            return login.getUser();
        } else {
            return null;
        }
    }

    public synchronized static boolean loginExpired(String loginId, String password) {
        Login login = getLoginList().get(loginId);
        Date currentTime = new java.util.Date();
        long currentTimeMills = currentTime.getTime();
        long lastCommunicationMills = login.getLastCommunication().getTime();

        long lastCommunicationGap = TimeUnit.MILLISECONDS.toMinutes(currentTimeMills - lastCommunicationMills);

        if (lastCommunicationGap > LOGIN_EXPIRE_TIME) {
            return true;
        } else {
            return false;
        }
    }

    public synchronized static boolean valideRole(String userRole, String allowedRole) {
        boolean isAllowed = false;
        if (Enums.UserType.valueOf(allowedRole).getId() <= Enums.UserType.valueOf(userRole).getId()) {
            isAllowed = true;
        }

        return isAllowed;
    }

    public static Map<String, Login> getLoginList() {
        return loginList;
    }

    public static void resetCache() {
        loginList = new HashMap<>();
    }
}
