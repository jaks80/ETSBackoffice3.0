package etsbackoffice.datalogic;

import etsbackoffice.domain.AppSettings;
import etsbackoffice.domain.User;

/**
 *
 * @author Yusuf
 */
public interface AuthenticationDao {

    public User getUser(String loginID,String pword);
    
    public void saveAppSettings(AppSettings settings);        
    
    public AppSettings getAppSettings();
}
