package com.ets.fe.pnr.task;

import com.ets.fe.a_maintask.PnrSearchTask;
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

    public DeletePnrTask(Pnr pnr, JXBusyLabel busyLabel) {
        this.pnr = pnr;
        this.busyLabel = busyLabel;
    }

    @Override
    protected Integer doInBackground() {

        PnrWSClient client = new PnrWSClient();
        Integer status = client.delete(pnr.getId());

        PnrSearchTask globalSearchTask = new PnrSearchTask("UNINVOICED_PNR", busyLabel);
        globalSearchTask.execute();
        return status;
    }

    @Override
    protected void done() {
        setProgress(100);
    }

    public Pnr getPnr() {
        return pnr;
    }
}
