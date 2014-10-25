package etsbackoffice.businesslogic;

import etsbackoffice.domain.MasterAgent;

/**
 *
 * @author Yusuf
 */
public class BusinessClassVariables {

    private static MasterAgent MAGENT_PROFILE;

 
    public static MasterAgent getMAGENT_PROFILE() {
        return MAGENT_PROFILE;
    }
  
    public static void setMAGENT_PROFILE(MasterAgent aMAGENT_PROFILE) {
        MAGENT_PROFILE = aMAGENT_PROFILE;
    }        
}
