package com.ets.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Yusuf
 */
public class APIConfig {

    private static Properties prop;
    
    public APIConfig(){
     try {
            InputStream sdkis = APIConfig.class.getResourceAsStream("/api.properties");
            prop = new Properties();
            prop.load(sdkis);
            sdkis.close();
        } catch (IOException ex) {
            Logger.getLogger(APIConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
    }       
    
    public static String get(String key){    
        return prop.getProperty(key);
    }
}
