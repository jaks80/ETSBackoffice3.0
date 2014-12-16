package com.ets;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Yusuf
 */
public class Application {
    private static Properties prop;
    
    public Application(){
     try {
            InputStream sdkis = Application.class.getResourceAsStream("/settings.properties");
            prop = new Properties();
            prop.load(sdkis);
            sdkis.close();
        } catch (IOException ex) {
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
        }
    }       
    
    public static String get(String key){    
        return prop.getProperty(key);
    }
}
