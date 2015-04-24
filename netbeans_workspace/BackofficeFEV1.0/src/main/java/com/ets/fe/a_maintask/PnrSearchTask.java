package com.ets.fe.a_maintask;

import com.ets.fe.pnr.model.Pnr;
import com.ets.fe.pnr.ws.PnrWSClient;
import com.ets.fe.util.PnrUtil;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingWorker;
import org.jdesktop.swingx.JXBusyLabel;

/**
 *
 * @author Yusuf
 */
public class PnrSearchTask extends SwingWorker< List<Pnr>, Integer> {

    private JXBusyLabel busyLabel;
    private String gdsPnr = null;
    private String invRef = null;
    private String name = null;
    private String taskType = null;

    public PnrSearchTask(String searchType, JXBusyLabel busyLabel) {
        this.taskType = searchType;
        this.busyLabel = busyLabel;
    }

    public PnrSearchTask(String searchType, JXBusyLabel busyLabel, String gdsPnr, String invRef, String name) {
        this.taskType = searchType;
        this.busyLabel = busyLabel;
        this.gdsPnr = gdsPnr;
        this.invRef = invRef;
        this.name = name;
    }

    @Override
    protected List<Pnr> doInBackground() {
        busyLabel.setBusy(true);
        PnrWSClient client = new PnrWSClient();
        List<Pnr> pnrs = new ArrayList<>();
        setProgress(10);
        switch (taskType) {
            case "UNINVOICED_PNR":
                pnrs = new ArrayList<>();
                pnrs = client.getUninvoicedPnr();
                break;
            case "PNRTODAY":
                pnrs = new ArrayList<>();
                pnrs = client.getPnrsToday();
                break;
            case "QUERY_SEARCH":
                pnrs = new ArrayList<>();
                if (gdsPnr != null && !gdsPnr.isEmpty()) {
                    pnrs = client.searchPnrByGdsPnr(gdsPnr);
                } else if (name != null && !name.isEmpty()) {
                    String[] names = PnrUtil.splitName(name);
                    pnrs = client.searchPnrByName(names[0], names[1]);
                } else if (invRef != null && !invRef.isEmpty()) {
                    pnrs = client.getPnrByInvRef(invRef);
                }
                break;
            default:
        }
        setProgress(50);
        return pnrs;
    }

    @Override
    protected void done() {
        busyLabel.setBusy(false);
        setProgress(100);
    }
}
