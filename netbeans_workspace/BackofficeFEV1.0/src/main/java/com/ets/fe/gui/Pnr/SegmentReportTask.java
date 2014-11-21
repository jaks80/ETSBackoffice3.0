package com.ets.fe.gui.Pnr;

import com.ets.fe.model.report.sales.SegmentReport;
import com.ets.fe.ws.ItineraryWSClient;
import java.util.Date;
import javax.swing.SwingWorker;

/**
 *
 * @author Yusuf
 */
public class SegmentReportTask extends SwingWorker<Void, Integer> {

    private SegmentReport report;
    private String ticketStatus;
    private String careerCode;
    private Date issueDateFrom;
    private Date issueDateTo;
    private String ticketingAgtOid;

    public SegmentReportTask(String ticketStatus, String careerCode,
            Date issueDateFrom, Date issueDateTo, String ticketingAgtOid) {
        this.ticketStatus = ticketStatus;
        this.careerCode = careerCode;
        this.issueDateFrom = issueDateFrom;
        this.issueDateTo = issueDateTo;
        this.ticketingAgtOid = ticketingAgtOid;
    }

    @Override
    protected Void doInBackground() {

        ItineraryWSClient client = new ItineraryWSClient();
        report = client.segmentReport(ticketStatus, careerCode, issueDateFrom, issueDateTo, ticketingAgtOid);

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
