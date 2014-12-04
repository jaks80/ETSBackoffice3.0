package com.ets.settings.dao;

import com.ets.AppSettings;
import com.ets.GenericDAOImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Yusuf
 */
@Service("appSettingsDAO")
@Transactional
public class AppSettingsDAOImpl  extends GenericDAOImpl<AppSettings, Long> implements AppSettingsDAO{
    
}
