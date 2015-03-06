package com.ets.fe.pnr.task;

import com.ets.fe.report.model.SegmentReport;
import com.ets.fe.pnr.ws.ItineraryWSClient;
import java.util.Date;
import javax.swing.SwingWorker;

/**
 *
 * @author Yusuf
 */
public class SegmentReportTask extends SwingWorker<Void, Integer> {

    private SegmentReport report;
    private String ticketStatus;
    private String airLineCode;
    private Date issueDateFrom;
    private Date issueDateTo;
    private String ticketingAgtOid;

    public SegmentReportTask(String ticketStatus, String airLineCode,
            Date issueDateFrom, Date issueDateTo, String ticketingAgtOid) {
        this.ticketStatus = ticketStatus;
        this.airLineCode = airLineCode;
        this.issueDateFrom = issueDateFrom;
        this.issueDateTo = issueDateTo;
        this.ticketingAgtOid = ticketingAgtOid;
    }

    @Override
    protected Void doInBackground() {

        ItineraryWSClient client = new ItineraryWSClient();
        report = client.segmentReport(ticketStatus, airLineCode, issueDateFrom, issueDateTo, ticketingAgtOid);

        setProgress(50);
        return null;
    }

    @Override
    protected void done() {
        setProgress(100);
        if (!isCancelled()) {

        }
    }

    public SegmentReport getReport() {
        return report;
    }
}
