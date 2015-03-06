package com.ets.fe.os.ws;

import com.ets.fe.APIConfig;
import com.ets.fe.os.model.Category;
import com.ets.fe.os.model.Categories;
import com.ets.fe.util.RestClientUtil;

/**
 *
 * @author Yusuf
 */
public class CategoryWSClient {
    public Categories find() {

        String url = APIConfig.get("ws.cat.categories");
        return RestClientUtil.getEntity(Categories.class, url, new Categories());

    }

    public Category create(Category category) {
        String url = APIConfig.get("ws.cat.new");
        Category persistedAgent = RestClientUtil.postEntity(Category.class, url, category);
        return persistedAgent;
    }

    public Category update(Category category) {
        String url = APIConfig.get("ws.cat.update");
        Category persistedCategory = RestClientUtil.putEntity(Category.class, url, category);
        return persistedCategory;
    }

    public Integer delete(long id) {
        String url = APIConfig.get("ws.cat.delete");
        Integer status = RestClientUtil.deleteById(url);
        return status;
    }
}
