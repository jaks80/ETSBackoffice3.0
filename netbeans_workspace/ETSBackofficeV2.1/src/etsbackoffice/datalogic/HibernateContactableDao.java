/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package etsbackoffice.datalogic;

import etsbackoffice.domain.Contactable;
import etsbackoffice.domain.Vendor;
import java.util.List;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Yusuf
 */
public class HibernateContactableDao extends HibernateDaoSupport implements ContactableDao{

    @Transactional
    public void store(Contactable contactable) {
        getHibernateTemplate().saveOrUpdate(contactable);
    }

    public void delete(int ClientID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Contactable searchByID(int ClientID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Contactable> searchByName(String name) {
       return (List<Contactable>)getHibernateTemplate().find("from Client where foreName =:name",name);
    }
    
    public List<Contactable> searchByEmail(String email) {
       return (List<Contactable>)getHibernateTemplate().find("from Client c where c.contactInfo.email =?",email);
    }

    public List<Vendor> loadVendors(){
     return getHibernateTemplate().find("from Vendor as v order by v.name ");
    }
    
    public Contactable searchByPostCode(String postCode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Contactable searchByOID(String oid) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Contactable> findAll() {
        return (List<Contactable>)getHibernateTemplate().find("from Client");
    }

    public List<Contactable> findAllSummery() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
}
