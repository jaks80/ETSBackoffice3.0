package com.add.model.page;

import org.springframework.stereotype.Service;

/**
 *
 * @author Yusuf
 */
@Service("keywordSearch")
public class KeywordSearch {

    private String keyword;

    public KeywordSearch() {

    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
