package com.ets.settings.service;

import com.ets.security.LoginManager;
import com.ets.settings.dao.UserDAO;
import com.ets.settings.domain.User;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

/**
 *
 * @author Yusuf
 */
@Service("userService")
public class UserService {

    @Resource(name = "userDAO")
    private UserDAO dao;

    public List<User> findAll() {
        return dao.findAll(User.class);
    }

    public User login(String loginId, String password, String newPassword) {

        List<User> dbUsers = findAll();
        User authenticatedUser = null;

        for (User user : dbUsers) {
            if (user.getLoginID().equals(loginId) && user.getPassword().equals(password)) {
                authenticatedUser = user;                
                break;
            }
        }

        LoginManager.addLogin(authenticatedUser);
        
        if (authenticatedUser != null && newPassword != null) {
            authenticatedUser.setPassword(newPassword);
            dao.save(authenticatedUser);
        }
        
        return authenticatedUser;
    }

    public void logout(User user) {
        LoginManager.removeLogin(user.getLoginID());
    }

    public User saveorUpdate(User appSettings) {
        dao.save(appSettings);
        return appSettings;
    }

    public void delete(User user) {
        dao.delete(user);
    }
}
