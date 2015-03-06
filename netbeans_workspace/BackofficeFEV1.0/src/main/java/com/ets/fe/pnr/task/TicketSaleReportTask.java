package com.ets.fe.pnr.task;

import com.ets.fe.pnr.model.TicketSaleReport;
import com.ets.fe.pnr.ws.TicketWSClient;
import java.util.Date;
import javax.swing.SwingWorker;

/**
 *
 * @author Yusuf
 */
public class TicketSaleReportTask extends SwingWorker<TicketSaleReport, Integer> {

    private TicketSaleReport report;
    private String ticketStatus;
    private String airLineCode;
    private Date issueDateFrom;
    private Date issueDateTo;
    private String ticketingAgtOid;

    public TicketSaleReportTask(String ticketStatus, String airLineCode,
            Date issueDateFrom, Date issueDateTo, String ticketingAgtOid) {
        this.ticketStatus = ticketStatus;
        this.airLineCode = airLineCode;
        this.issueDateFrom = issueDateFrom;
        this.issueDateTo = issueDateTo;
        this.ticketingAgtOid = ticketingAgtOid;
    }

    @Override
    protected TicketSaleReport doInBackground() {

        TicketWSClient client = new TicketWSClient();
        report = client.saleReport(ticketStatus, airLineCode, issueDateFrom, issueDateTo, ticketingAgtOid);
        return report;
    }

    @Override
    protected void done() {
        setProgress(100);
        if (!isCancelled()) {

        }
    }
}
