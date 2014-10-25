package etsbackoffice.datalogic;

import etsbackoffice.domain.MasterAgent;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Yusuf
 */
public class HibernateMAgentDao extends HibernateDaoSupport implements MAgentDao {

    @Transactional
    public void save(MasterAgent masterAgent) {
        getHibernateTemplate().saveOrUpdate(masterAgent);
    }

    public MasterAgent load() {
        String hql = "select mAgent from MasterAgent mAgent "
                + "left join fetch mAgent.createdBy "
                + "left join fetch mAgent.lastModifiedBy "
                + "left join fetch mAgent.officeIDs ";
        MasterAgent mAgent = (MasterAgent) DataAccessUtils.uniqueResult(getHibernateTemplate().find(hql));
        /*if (mAgent != null) {
            getHibernateTemplate().initialize(mAgent.getOfficeIDs());
        }*/
        return mAgent;
    }
}
