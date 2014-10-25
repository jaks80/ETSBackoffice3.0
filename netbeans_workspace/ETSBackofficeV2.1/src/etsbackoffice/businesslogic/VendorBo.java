/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etsbackoffice.businesslogic;

import etsbackoffice.datalogic.ContactableDao;
import etsbackoffice.domain.Vendor;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public class VendorBo {

    private Vendor vendor;
    private ContactableDao contactableDao;

    public VendorBo() {
    }

    public void save(Vendor v) {
        getContactableDao().store(v);
    }

    public Vendor getVendor() {
        return this.vendor;
    }

    public List<Vendor> getVendors(){
     return getContactableDao().loadVendors();
    }
    
    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

    public ContactableDao getContactableDao() {
        return contactableDao;
    }

    public void setContactableDao(ContactableDao contactableDao) {
        this.contactableDao = contactableDao;
    }
}
