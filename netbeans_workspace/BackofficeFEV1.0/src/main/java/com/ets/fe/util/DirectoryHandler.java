package com.ets.fe.util;

import java.io.File;

/**
 *
 * @author Yusuf
 */
public class DirectoryHandler {

    private static final File ERROR_AIR_DIR = new File("C://ERROR_AIR");
    private static final File BACKUP_AIR_DIR = new File("C://BKUP_AIR");
    private static final File APP_WORKING_DIR = new File("C://ETSBackoffice");
    private static final File APP_CONF_DIR = new File("C://ETSBackoffice//conf");

    public static void setAppDirectories() {

        if (!BACKUP_AIR_DIR.exists()) {
            BACKUP_AIR_DIR.mkdir();
        }

        if (!ERROR_AIR_DIR.exists()) {
            ERROR_AIR_DIR.mkdir();
        }

        if (!APP_WORKING_DIR.exists()) {
            APP_WORKING_DIR.mkdir();
        }
        
        if (!APP_CONF_DIR.exists()) {
            APP_CONF_DIR.mkdir();
        }
    }

    public static File getError_air_dir() {
        return ERROR_AIR_DIR;
    }

    public static File getBackup_air_dir() {
        return BACKUP_AIR_DIR;
    }

    public static File getApp_working_dir() {
        return APP_WORKING_DIR;
    }

    public static File getAPP_CONF_DIR() {
        return APP_CONF_DIR;
    }
}
