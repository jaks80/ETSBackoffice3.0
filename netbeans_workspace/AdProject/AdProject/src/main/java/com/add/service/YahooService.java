package com.add.service;

import com.add.apidao.yahoo.YBossDao;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Yusuf
 */
public class YahooService {

    private List<String> loadRelatedKeywords(String keyword) {
        YBossDao dao = new YBossDao();
        dao.setSearchString(keyword);
        String result = null;
        try {
            result = dao.returnHttpData();
        } catch (Exception ex) {

        }
        List<String> keywords = dao.getSuggestedKeywords(result);
        return keywords;
    }
}
