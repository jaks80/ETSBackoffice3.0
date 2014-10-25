/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package etsbackoffice.datalogic;

import etsbackoffice.domain.Contactable;
import etsbackoffice.domain.Vendor;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public interface ContactableDao {

    public void store(Contactable contactable);       

    public void delete(int contactableID);

    public Contactable searchByID(int contactableID);

    public List<Contactable> searchByName(String name);
    
    public List<Vendor> loadVendors();
    
    public Contactable searchByPostCode(String postCode);

    public Contactable searchByOID(String oid);

    public List<Contactable> findAll();

    public List<Contactable> findAllSummery();

    public List<Contactable> searchByEmail(String email);
}
