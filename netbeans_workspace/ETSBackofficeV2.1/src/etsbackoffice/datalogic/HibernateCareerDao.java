/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etsbackoffice.datalogic;

import etsbackoffice.domain.Career;
import java.util.List;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Yusuf
 */
public class HibernateCareerDao extends HibernateDaoSupport implements CareerDao {

    @Transactional
    public void store(Career career) {
        getHibernateTemplate().saveOrUpdate(career);
    }

    @Transactional
    public void storeAll(List<Career> careers) {
        getHibernateTemplate().saveOrUpdateAll(careers);
    }

    public Career findByCode(String code) {
        return (Career) DataAccessUtils.uniqueResult(getHibernateTemplate().find("from Career career where career.code = ?", code));
    }

    public Career find(String code) {
        List<Career> career;
        career = getHibernateTemplate().find("from Career career where career.code = ?", code);
        if (career.size() > 0) {
            return career.get(0);
        } else {
            return null;
        }
    }

    public List<Career> findAll() {
        return getHibernateTemplate().find("from Career");
    }
}
