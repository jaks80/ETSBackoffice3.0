package com.ets.fe;

import com.ets.fe.util.DirectoryHandler;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author Yusuf
 */
public class APIConfig {

    private static Properties apiprop;
    private static Properties confprop;

    public APIConfig() {
        FileInputStream is = null;
        InputStream sdkis = null;
        try {
            sdkis = APIConfig.class.getResourceAsStream("/api.properties");
            apiprop = new Properties();
            apiprop.load(sdkis);            
            
            File conf_file = new File(DirectoryHandler.getAPP_CONF_DIR().getAbsolutePath() + "/conf.properties");            
            if (conf_file.exists()) {
                is = new FileInputStream(conf_file);
                confprop = new Properties();
                confprop.load(is);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (sdkis != null) {
                    sdkis.close();
                }

                if (is != null) {
                    is.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void writeConfProps(String encryptedagent_id) {
        FileOutputStream fileOut = null;
        try {
            Properties properties = new Properties();
            properties.setProperty("ws.id", encryptedagent_id);
                        
            File file = new File(DirectoryHandler.getAPP_CONF_DIR().getAbsolutePath() + "/conf.properties");
            fileOut = new FileOutputStream(file);
            properties.store(fileOut, "Conf");

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                fileOut.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static String get(String key) {        
        return apiprop.getProperty(key);
    }

    public static String getConfProp(String key) {        
        return confprop.getProperty(key);
    }
}
