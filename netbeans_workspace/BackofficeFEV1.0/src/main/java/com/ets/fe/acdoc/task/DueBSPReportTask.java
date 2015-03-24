package com.ets.fe.acdoc.task;

import com.ets.accountingdoc.collection.TicketingPurchaseAcDocs;
import com.ets.accountingdoc.collection.TicketingSalesAcDocs;
import com.ets.fe.acdoc.model.AccountingDocument;
import com.ets.fe.acdoc.model.BSPReport;
import com.ets.fe.acdoc.model.TicketingPurchaseAcDoc;
import com.ets.fe.acdoc.model.TicketingSalesAcDoc;
import com.ets.fe.acdoc.ws.TicketingPAcDocWSClient;
import com.ets.fe.acdoc.ws.TicketingSAcDocWSClient;
import com.ets.fe.util.Enums;
import com.ets.fe.util.Enums.AcDocType;
import com.ets.fe.util.Enums.ClientType;
import com.ets.fe.util.Enums.TicketingType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

/**
 *
 * @author Yusuf
 */
public class DueBSPReportTask extends SwingWorker<BSPReport, Integer> {

    private JProgressBar progressBar;

    private Long agentid = null;
    private Date dateFrom = null;
    private Date dateTo = null;

    public DueBSPReportTask(Long agentid, Date dateFrom, Date dateTo, JProgressBar progressBar) {
        this.agentid = agentid;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.progressBar = progressBar;
    }

    @Override
    protected BSPReport doInBackground() throws Exception {

        TicketingPAcDocWSClient client = new TicketingPAcDocWSClient();
        BSPReport report = client.outstandingBSPReport(agentid, dateFrom, dateTo);
        return report;
    }

    @Override
    protected void done() {
        setProgress(100);
    }
}
