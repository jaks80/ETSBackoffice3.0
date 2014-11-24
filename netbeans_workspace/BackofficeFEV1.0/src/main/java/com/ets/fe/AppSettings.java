package com.ets.fe;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Yusuf
 */
public class AppSettings {
    private static Properties prop;
    
    public AppSettings(){
     try {
            InputStream sdkis = AppSettings.class.getResourceAsStream("/settings.properties");
            prop = new Properties();
            prop.load(sdkis);
            sdkis.close();
        } catch (IOException ex) {
            Logger.getLogger(AppSettings.class.getName()).log(Level.SEVERE, null, ex);
        }
    }       
    
    public static String get(String key){    
        return prop.getProperty(key);
    }
}
