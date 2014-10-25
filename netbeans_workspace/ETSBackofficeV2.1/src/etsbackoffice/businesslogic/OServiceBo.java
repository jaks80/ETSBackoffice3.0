/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etsbackoffice.businesslogic;

import etsbackoffice.datalogic.OServiceDao;
import etsbackoffice.domain.OtherService;
import etsbackoffice.domain.Services;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public class OServiceBo {

    private OServiceDao oServiceDao;

    public OServiceBo() {
    }

    public void saveOrUpdateOService(OtherService oService) {
        oServiceDao.saveOrUpdateOService(oService);
    }

    public void removeService(Services s) {
        oServiceDao.removeService(s);
    }

    public List<OtherService> loadAllServices() {
        return oServiceDao.loadAllService();
    }

    public List<OtherService> loadAdditionalServices() {
        return oServiceDao.loadAdditionalService();
    }

    public List<OtherService> loadOtherServices() {
        return oServiceDao.loadOtherService();
    }

    public List<Services> loadOtherServicesFromPnr(long pnrId) {
        return oServiceDao.loadOtherServicesFromPnr(pnrId);
    }

    public void setoServiceDao(OServiceDao oServiceDao) {
        this.oServiceDao = oServiceDao;
    }
}
