package etsbackoffice.datalogic;

import etsbackoffice.domain.Career;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public interface CareerDao {

    public void store(Career career);

    public void storeAll(List<Career> careers);

    public Career findByCode(String code);

    public Career find(String code);

    public List<Career> findAll();
    
}
