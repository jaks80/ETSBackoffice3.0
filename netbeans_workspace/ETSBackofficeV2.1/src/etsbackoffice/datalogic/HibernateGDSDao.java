package etsbackoffice.datalogic;

import java.util.List;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Yusuf
 */
public class HibernateGDSDao extends HibernateDaoSupport implements GDSDao{

   /* @Transactional
    public void store(List<GDS> gds) {
         getHibernateTemplate().saveOrUpdateAll(gds);
    }

    public List<GDS> findAll() {
        return (List<GDS>) getHibernateTemplate().find("from GDS");
    }
*/
}
