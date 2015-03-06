package com.ets.fe.pnr.task;

import com.ets.fe.a_maintask.GlobalSearchTask;
import com.ets.fe.pnr.model.Pnr;
import com.ets.fe.pnr.ws.PnrWSClient;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import org.jdesktop.swingx.JXBusyLabel;

/**
 *
 * @author Yusuf
 */
public class DeletePnrTask extends SwingWorker<Integer, Integer> {

    private final Pnr pnr;
    private JXBusyLabel busyLabel;
    private JTable table;
    
    public DeletePnrTask(Pnr pnr,JXBusyLabel busyLabel, JTable table) {
        this.pnr = pnr;        
        this.busyLabel = busyLabel;
        this.table = table;
    }

    @Override
    protected Integer doInBackground() {        

        PnrWSClient client = new PnrWSClient();
        Integer status = client.delete(pnr.getId());
        
        GlobalSearchTask globalSearchTask = new GlobalSearchTask("UNINVOICED_PNR",busyLabel, table);
        globalSearchTask.execute();
        return status;
    }

    @Override
    protected void done() {
     
        setProgress(100);       
    }
}
