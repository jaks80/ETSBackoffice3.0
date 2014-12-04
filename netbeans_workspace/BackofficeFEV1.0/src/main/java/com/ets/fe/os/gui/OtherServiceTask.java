package com.ets.fe.os.gui;

import com.ets.fe.os.model.OtherServices;
import com.ets.fe.os.model.ws.OtherServiceWSClient;
import javax.swing.SwingWorker;

/**
 *
 * @author Yusuf
 */
public class OtherServiceTask extends SwingWorker<OtherServices, Integer> {

    @Override
    protected OtherServices doInBackground() {

        setProgress(30);
        OtherServiceWSClient client = new OtherServiceWSClient();
        OtherServices services = client.find();
        return services;
    }

    @Override
    protected void done() {
        setProgress(100);
        if (!isCancelled()) {

        }
    }
}
