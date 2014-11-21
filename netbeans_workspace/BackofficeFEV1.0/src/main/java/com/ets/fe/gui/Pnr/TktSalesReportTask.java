package com.ets.fe.gui.Pnr;

import com.ets.fe.model.report.sales.TicketSaleReport;
import com.ets.fe.ws.TicketWSClient;
import java.util.Date;
import javax.swing.SwingWorker;

/**
 *
 * @author Yusuf
 */
public class TktSalesReportTask extends SwingWorker<Void, Integer> {

    //private List<Ticket> list = new ArrayList<>();
    private TicketSaleReport report;
    private String ticketStatus;
    private String airLineCode;
    private Date issueDateFrom;
    private Date issueDateTo;
    private String ticketingAgtOid;

    public TktSalesReportTask(String ticketStatus, String airLineCode,
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
        report = client.saleReport(ticketStatus, airLineCode, issueDateFrom, issueDateTo, ticketingAgtOid);
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

    public TicketSaleReport getReport() {
        return report;
    }
}
