package com.ets.fe.settings.task;

import com.ets.fe.settings.model.Users;
import com.ets.fe.settings.ws.UserWSClient;
import javax.swing.SwingWorker;

/**
 *
 * @author Yusuf
 */
public class UserTask extends SwingWorker<Users, Integer> {

    public UserTask() {

    }

    @Override
    protected Users doInBackground() throws Exception {

        UserWSClient client = new UserWSClient();
        Users users = client.find();

        return users;
    }

    @Override
    protected void done() {
        setProgress(100);
    }
}
