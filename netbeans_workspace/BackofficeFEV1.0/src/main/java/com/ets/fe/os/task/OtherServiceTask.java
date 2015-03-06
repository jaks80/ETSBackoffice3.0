package com.ets.fe.os.task;

import com.ets.fe.os.model.OtherServices;
import com.ets.fe.os.ws.OtherServiceWSClient;
import javax.swing.SwingWorker;

/**
 *
 * @author Yusuf
 */
public class OtherServiceTask extends SwingWorker<OtherServices, Integer> {

    private Long categoryId = null;

    public OtherServiceTask(Long categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    protected OtherServices doInBackground() {

        setProgress(30);
        OtherServiceWSClient client = new OtherServiceWSClient();
        OtherServices services;

        if (categoryId == null) {
            services = client.find();
        } else {
            services = client.findByCategory(categoryId);
        }
        return services;
    }

    @Override
    protected void done() {
        setProgress(100);
    }
}
