package etsbackoffice.datalogic;

import etsbackoffice.domain.User;
import java.util.ArrayList;
import java.util.List;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Yusuf
 */
public class HibernateUserDao extends HibernateDaoSupport implements UserDao{

    @Transactional
    public void store(User user) {
         getHibernateTemplate().saveOrUpdate(user);
    }

    public void delete(long userID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public User searchByID(long userID) {
        return getHibernateTemplate().get(User.class, userID);
    }

    public List<User> searchByName(String name) {
        return (List<User>)getHibernateTemplate().find("from User where foreName =?",name);
        
    }

    public User searchByPostCode(String postCode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public User searchByOID(String oid) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public List<User> findAll() {
        return (List<User>)getHibernateTemplate().find("select u from User as u order by u.surName ");
    }

    public List findUserNameList() {

        List UserNameList = new ArrayList();

        String hql = "select u.surName,u.foreName,u.userId from User as u";
        List results = getHibernateTemplate().find(hql);
        for (int i = 0; i < results.size(); i++) {
            Object[] objects = (Object[]) results.get(i);
            String surName = (String) objects[0];
            String foreName = (String) objects[1];
            String userId = objects[2].toString();
            UserNameList.add(surName + "-" + foreName + "-" + userId);
        }
        return UserNameList;
    }

    public List<User> findAllSummery() {
        throw new UnsupportedOperationException("Not supported yet.");
    }    
}
