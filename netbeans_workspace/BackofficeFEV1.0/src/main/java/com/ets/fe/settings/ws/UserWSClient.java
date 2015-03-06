package com.ets.fe.settings.ws;

import com.ets.fe.APIConfig;
import com.ets.fe.settings.model.User;
import com.ets.fe.settings.model.Users;
import com.ets.fe.util.RestClientUtil;

/**
 *
 * @author Yusuf
 */
public class UserWSClient {

    public Users find() {
        String url = APIConfig.get("ws.user.users");
        
        Users users = RestClientUtil.getEntity(Users.class, url, new Users());
        return users;
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
}
