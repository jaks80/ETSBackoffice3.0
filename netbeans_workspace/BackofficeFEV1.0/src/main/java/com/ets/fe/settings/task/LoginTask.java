package com.ets.fe.settings.task;

import com.ets.fe.settings.model.User;
import com.ets.fe.client.task.AgentSearchTask;
import com.ets.fe.settings.ws.ApplicationWSClient;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

/**
 *
 * @author Yusuf
 */
public class LoginTask extends SwingWorker<User, Integer> {

    private JProgressBar progressBar;    
    private String loginId;
    private String password;
    private String newPassword;
   

    public LoginTask(String loginId, String password, String newPassword,JProgressBar progressBar) {
        this.loginId = loginId;
        this.password = password;
        this.newPassword = newPassword;
        this.progressBar = progressBar;
    }

    @Override
    protected User doInBackground() throws Exception {

        setProgress(10);
        Progress p = new Progress();
        Thread t = new Thread(p);
        t.start();

        ApplicationWSClient client = new ApplicationWSClient();        
        User user = client.login(loginId, password, newPassword);
        System.out.println("User>>>>"+user);
        p.cancel();
        return user;
    }

    @Override
    protected void done() {
        setProgress(100);
    }

    private class Progress implements Runnable {

        private volatile boolean stop = false;

        @Override
        public void run() {
            int i = 10;
            do {
                if (!stop) {
                    setProgress(i);
                    i++;
                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(AgentSearchTask.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    if (i == 99) {
                        System.out.println("Bar:"+progressBar);
                        progressBar.setIndeterminate(true);
                        break;
                    }
                }
            } while (!stop);
        }

        public void cancel() {
            stop = true;
        }
    }
}
