/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package etsbackoffice.datalogic;

import etsbackoffice.domain.AppSettings;
import etsbackoffice.domain.User;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Yusuf
 */
public class HibernateAuthenticationDao extends HibernateDaoSupport implements AuthenticationDao {

    public User getUser(String loginID, String pword) {
        String hql = "from User as u "
                + "left join fetch u.userRole "
                + "where u.loginID = ? and u.password = ?";
        User user = (User) DataAccessUtils.uniqueResult(getHibernateTemplate().find(hql,loginID,pword));
        return  user;
    }

    @Transactional
    public void saveAppSettings(AppSettings settings) {
        getHibernateTemplate().saveOrUpdate(settings);
    }

    public AppSettings getAppSettings() {
       long id = 1;
       return getHibernateTemplate().get(AppSettings.class, id);
    }
}
