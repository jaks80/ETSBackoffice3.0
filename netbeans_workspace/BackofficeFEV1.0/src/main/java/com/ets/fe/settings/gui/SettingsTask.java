package com.ets.fe.settings.gui;

import com.ets.fe.Application;
import com.ets.fe.settings.model.AppSettings;
import com.ets.fe.settings.ws.ApplicationWSClient;
import javax.swing.SwingWorker;

/**
 *
 * @author Yusuf
 */
public class SettingsTask extends SwingWorker<AppSettings, Integer>{
    
 private final AppSettings settings;

    public SettingsTask(AppSettings settings) {
        this.settings = settings;
    }

    @Override
    protected AppSettings doInBackground() {

        ApplicationWSClient client = new ApplicationWSClient();
        client.update(settings);

        AppSettings persistedSettings = client.getSettings();
        Application.setAppSettings(persistedSettings);
        return persistedSettings;
    }

    @Override
    protected void done() {
        setProgress(100);
        if (!isCancelled()) {

        }
    }
}
