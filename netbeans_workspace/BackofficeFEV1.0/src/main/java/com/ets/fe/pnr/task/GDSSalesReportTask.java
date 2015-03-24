package com.ets.fe.pnr.task;

import com.ets.fe.pnr.model.GDSSaleReport;
import com.ets.fe.pnr.ws.TicketWSClient;
import com.ets.fe.util.Enums;
import java.util.Date;
import javax.swing.SwingWorker;

/**
 *
 * @author Yusuf
 */
public class GDSSalesReportTask extends SwingWorker<GDSSaleReport, Integer> {

    //private List<Ticket> list = new ArrayList<>();
    private GDSSaleReport report;
    private Enums.TicketStatus ticketStatus;
    private Enums.TicketingType ticketingType;
    private String airLineCode;
    private Date issueDateFrom;
    private Date issueDateTo;
    private String ticketingAgtOid;

    public GDSSalesReportTask(Enums.TicketingType ticketingType,Enums.TicketStatus ticketStatus, String airLineCode,
            Date issueDateFrom, Date issueDateTo, String ticketingAgtOid) {
        this.ticketStatus = ticketStatus;
        this.ticketingType = ticketingType;
        this.airLineCode = airLineCode;
        this.issueDateFrom = issueDateFrom;
        this.issueDateTo = issueDateTo;
        this.ticketingAgtOid = ticketingAgtOid;
    }

    @Override
    protected GDSSaleReport doInBackground() {

        TicketWSClient client = new TicketWSClient();
        report = client.gdsSaleReport(ticketingType,ticketStatus, airLineCode, issueDateFrom, issueDateTo, ticketingAgtOid);
        //list = report.getList();
        setProgress(50);
        return report;
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
