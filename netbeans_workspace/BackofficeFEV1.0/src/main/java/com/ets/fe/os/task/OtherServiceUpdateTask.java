package com.ets.fe.os.task;

import com.ets.fe.os.model.OtherService;
import com.ets.fe.os.model.OtherServices;
import com.ets.fe.os.ws.OtherServiceWSClient;
import javax.swing.SwingWorker;

/**
 *
 * @author Yusuf
 */
public class OtherServiceUpdateTask extends SwingWorker<OtherService, Integer> {

    private OtherService service;
    
    public OtherServiceUpdateTask(OtherService service){
     this.service = service;
    }
    
    @Override
    protected OtherService doInBackground() {

        setProgress(30);
        OtherServiceWSClient client = new OtherServiceWSClient();
        if(service.getId()==null){
        service = client.create(service);
        }else{
        service = client.update(service);
        }
        return service;
    }

    @Override
    protected void done() {
        setProgress(100);
        if (!isCancelled()) {

        }
    }
}
