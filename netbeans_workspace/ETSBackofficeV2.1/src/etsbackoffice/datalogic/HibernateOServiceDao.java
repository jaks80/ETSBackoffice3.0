/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etsbackoffice.datalogic;

import etsbackoffice.domain.OtherService;
import etsbackoffice.domain.Services;
import java.util.List;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Yusuf
 */
public class HibernateOServiceDao extends HibernateDaoSupport implements OServiceDao {

    @Transactional
    public void saveOrUpdateOService(OtherService oService) {
        getHibernateTemplate().saveOrUpdate(oService);
    }    

    public List<OtherService> loadOtherService() {
        return getHibernateTemplate().find("from OtherService as oService "
                + "left join fetch oService.vendor where serviceType = 1 order by serviceTitle ");
    }

    public List<OtherService> loadAdditionalService() {
        return getHibernateTemplate().find("from OtherService as oService where oService.serviceType = 2 order by serviceTitle");
    }

    public List<OtherService> loadAllService(){
     return getHibernateTemplate().find("from OtherService as oService order by serviceTitle ");
    }

    public List<Services> loadOtherServicesFromPnr(long pnrId) {    
        String hql = "from Services as oService "
                + "left join fetch oService.accountingDocumentLine "
                + "where  oService.pnr.pnrId = ? and oService.serviceType = 1";
        return getHibernateTemplate().find(hql,pnrId);
    }

    @Transactional
    public void removeService(Services s) {
        getHibernateTemplate().delete(s);
    }
}
