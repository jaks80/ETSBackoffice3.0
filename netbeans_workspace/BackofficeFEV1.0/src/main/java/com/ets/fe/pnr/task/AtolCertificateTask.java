package com.ets.fe.pnr.task;

import com.ets.fe.client.task.AgentSearchTask;
import com.ets.fe.pnr.model.ATOLCertificate;
import com.ets.fe.pnr.model.Pnr;
import com.ets.fe.pnr.ws.PnrWSClient;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

/**
 *
 * @author Yusuf
 */
public class AtolCertificateTask extends SwingWorker<ATOLCertificate, Integer> {

    private Long pnrid;
    private Date issuedate;

    public AtolCertificateTask(Long pnrid, Date issuedate) {
        this.pnrid = pnrid;
        this.issuedate = issuedate;
    }

    @Override
    protected ATOLCertificate doInBackground() {

        setProgress(10);
        PnrWSClient client = new PnrWSClient();
        ATOLCertificate certificate = client.getAtolCertificate(pnrid, issuedate);
        return certificate;
    }

    @Override
    protected void done() {
        setProgress(100);
    }
}
