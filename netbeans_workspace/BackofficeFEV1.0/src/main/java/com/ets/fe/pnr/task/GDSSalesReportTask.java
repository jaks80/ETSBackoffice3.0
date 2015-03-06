package com.ets.fe.pnr.task;

import com.ets.fe.pnr.model.GDSSaleReport;
import com.ets.fe.pnr.ws.TicketWSClient;
import java.util.Date;
import javax.swing.SwingWorker;

/**
 *
 * @author Yusuf
 */
public class GDSSalesReportTask extends SwingWorker<Void, Integer> {

    //private List<Ticket> list = new ArrayList<>();
    private GDSSaleReport report;
    private String ticketStatus;
    private String airLineCode;
    private Date issueDateFrom;
    private Date issueDateTo;
    private String ticketingAgtOid;

    public GDSSalesReportTask(String ticketStatus, String airLineCode,
            Date issueDateFrom, Date issueDateTo, String ticketingAgtOid) {
        this.ticketStatus = ticketStatus;
        this.airLineCode = airLineCode;
        this.issueDateFrom = issueDateFrom;
        this.issueDateTo = issueDateTo;
        this.ticketingAgtOid = ticketingAgtOid;
    }

    @Override
    protected Void doInBackground() {

        TicketWSClient client = new TicketWSClient();
        report = client.gdsSaleReport(ticketStatus, airLineCode, issueDateFrom, issueDateTo, ticketingAgtOid);
        //list = report.getList();
        setProgress(50);
        return null;
    }

    @Override
    protected void done() {
        setProgress(100);
        if (!isCancelled()) {

        }
    }

    public GDSSaleReport getReport() {
        return report;
    }
}
