/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package etsbackoffice.datalogic;

import etsbackoffice.domain.OfficeID;
import java.util.List;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 *
 * @author Yusuf
 */
public class HibernateOfficeIDDao extends HibernateDaoSupport implements OfficeIDDao{

    public void store(List<OfficeID> officeIDs) {
        getHibernateTemplate().saveOrUpdateAll(officeIDs);
    }

    public List<OfficeID> findAgtOfficeIDs(long agtID) {
      return getHibernateTemplate().find("from OfficeID oid where oid.agent.contactableId=?",agtID);
    }
}
