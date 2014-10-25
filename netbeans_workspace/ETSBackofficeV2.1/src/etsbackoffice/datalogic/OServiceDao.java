/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package etsbackoffice.datalogic;

import etsbackoffice.domain.OtherService;
import etsbackoffice.domain.Services;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public interface OServiceDao {

    public void saveOrUpdateOService(OtherService oService);

    public List<OtherService> loadOtherService();

    public List<OtherService> loadAllService();

    public List<OtherService> loadAdditionalService();
    
    public List<Services> loadOtherServicesFromPnr(long pnrId);
    
    public void removeService(Services s);
}
