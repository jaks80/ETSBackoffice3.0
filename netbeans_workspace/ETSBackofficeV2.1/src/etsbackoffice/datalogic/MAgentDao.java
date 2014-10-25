package etsbackoffice.datalogic;

import etsbackoffice.domain.MasterAgent;

/**
 *
 * @author Yusuf
 */
public interface MAgentDao {

    public void save(MasterAgent mAgent);

    public MasterAgent load();
}
