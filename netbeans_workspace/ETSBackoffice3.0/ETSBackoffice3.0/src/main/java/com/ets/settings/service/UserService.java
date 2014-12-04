package com.ets.settings.service;

import com.ets.AppSettings;
import com.ets.settings.dao.UserDAO;
import com.ets.settings.domain.User;
import java.util.List;
import javax.annotation.Resource;
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

    public User login(String loginId, String password, String newPassword){
    
        return new User();
    }
    
    public User saveorUpdate(User appSettings) {
        dao.save(appSettings);
        return appSettings;
    }

    public void delete(User user) {
        dao.delete(user);
    }
}
