package com.ets.fe.app.ws;

import com.ets.fe.APIConfig;
import com.ets.fe.app.model.User;
import com.ets.fe.app.model.Users;
import com.ets.fe.util.RestClientUtil;

/**
 *
 * @author Yusuf
 */
public class UserWSClient {

    public Users find() {

        String url = APIConfig.get("ws.user.users");
        return RestClientUtil.getEntity(Users.class, url, new Users());

    }

    public User create(User user) {
        String url = APIConfig.get("ws.user.new");
        User persistedAgent = RestClientUtil.postEntity(User.class, url, user);
        return persistedAgent;
    }

    public User update(User user) {
        user.recordUpdateBy();
        String url = APIConfig.get("ws.user.update");
        User persistedUser = RestClientUtil.putEntity(User.class, url, user);
        return persistedUser;
    }

    public Integer delete(long id) {
        String url = APIConfig.get("ws.user.delete");
        Integer status = RestClientUtil.deleteById(url);
        return status;
    }
}
