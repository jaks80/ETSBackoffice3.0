package etsbackoffice.businesslogic;

import etsbackoffice.datalogic.UserDao;
import etsbackoffice.domain.User;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public class UserBo {

    private User user;
    private UserDao userDao;

    public UserBo(){}

    public List loadAll(){
        return userDao.findAll();
    }

    public List userNameList(){
        return userDao.findUserNameList();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void saveUser(){
    userDao.store(this.user);
    }
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
}
