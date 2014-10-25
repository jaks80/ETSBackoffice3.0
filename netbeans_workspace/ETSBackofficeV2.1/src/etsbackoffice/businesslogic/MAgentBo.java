package etsbackoffice.businesslogic;

import etsbackoffice.datalogic.MAgentDao;
import etsbackoffice.domain.MasterAgent;
import etsbackoffice.domain.OfficeID;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public class MAgentBo {

    private MasterAgent mAgent;
    private MAgentDao mAgentDao;
    private List<OfficeID> officeIDs;

    public MAgentBo() {
        
    }

    public void saveMAgent(MasterAgent mAgent) {
        mAgentDao.save(mAgent);
    }

    public MasterAgent loadMAgent() {        
        return mAgentDao.load();
    }

    public void setmAgent(MasterAgent mAgent) {
        this.mAgent = mAgent;
    }

    public void setmAgentDao(MAgentDao mAgentDao) {
        this.mAgentDao = mAgentDao;
    }

    public MasterAgent getmAgent() {
        return mAgent;
    }
}
