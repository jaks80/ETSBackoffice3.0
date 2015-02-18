package com.ets.fe.a_maintask;

import com.ets.fe.pnr.collection.Remarks;
import com.ets.fe.pnr.ws.RemarkWSClient;
import javax.swing.SwingWorker;
import org.jdesktop.swingx.JXBusyLabel;

/**
 *
 * @author Yusuf
 */
public class RemarkTask extends SwingWorker< Remarks, Integer> {

    private JXBusyLabel busyLabel;
    private Long pnrId;
    private Remarks remarks;

    public RemarkTask(Remarks remarks, Long pnrId, JXBusyLabel busyLabel) {
        this.remarks = remarks;
        this.pnrId = pnrId;
        this.busyLabel = busyLabel;
    }

    @Override
    protected Remarks doInBackground() throws Exception {
        RemarkWSClient client = new RemarkWSClient();
        busyLabel.setBusy(true);
        if (pnrId != null) {
            remarks = client.getByPnrId(pnrId);
        } else {
            remarks = client.create(remarks);
        }
        return remarks;
    }

    @Override
    protected void done() {
        busyLabel.setBusy(false);
        setProgress(100);
    }
}
