package com.ets.fe.a_maintask;

import com.ets.fe.acdoc.model.report.InvoiceReport;
import com.ets.fe.acdoc.ws.TicketingSAcDocWSClient;
import com.ets.fe.acdoc_o.model.InvoiceReportOther;
import com.ets.fe.acdoc_o.ws.OtherSAcDocWSClient;
import javax.swing.SwingWorker;
import org.jdesktop.swingx.JXBusyLabel;

/**
 *
 * @author Yusuf
 */
public class TodayTInvoiceSearchTask extends SwingWorker< Object, Integer> {

    private JXBusyLabel busyLabel;
    private String searchType = "";

    public TodayTInvoiceSearchTask(String searchType, JXBusyLabel busyLabel) {
        this.searchType = searchType;
        this.busyLabel = busyLabel;
    }

    @Override
    protected Object doInBackground() throws Exception {
        busyLabel.setBusy(true);

        InvoiceReport report = null;
        InvoiceReportOther oreport = null;
        switch (searchType) {
            case "TSINVOICE_TODAY":
                TicketingSAcDocWSClient client = new TicketingSAcDocWSClient();
                report = client.documentHistoryReport(null, null, new java.util.Date(), new java.util.Date());
                break;
            case "OSINVOICE_TODAY":
                OtherSAcDocWSClient client1 = new OtherSAcDocWSClient();
                oreport = client1.documentHistoryReport(null, null, new java.util.Date(), new java.util.Date());
                break;
            default:
        }

        if (report != null) {
            return report;
        } else {
            return oreport;
        }
    }

    @Override
    protected void done() {
        busyLabel.setBusy(false);
        setProgress(100);
    }
}
