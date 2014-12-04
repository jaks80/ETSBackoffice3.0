package com.ets.settings.service;

import com.ets.AppSettings;
import com.ets.settings.dao.AppSettingsDAO;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 *
 * @author Yusuf
 */
@Service("appSettingsService")
public class AppSettingsService {

    @Resource(name = "appSettingsDAO")
    private AppSettingsDAO dao;

    public List<AppSettings> findAll() {
        return dao.findAll(AppSettings.class);
    }

    public AppSettings saveorUpdate(AppSettings appSettings) {
        dao.save(appSettings);
        return appSettings;
    }

    public void delete(AppSettings appSettings) {
        dao.delete(appSettings);
    }
}
