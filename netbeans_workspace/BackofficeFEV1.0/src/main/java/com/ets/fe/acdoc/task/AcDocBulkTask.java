package com.ets.fe.acdoc.task;

import com.ets.accountingdoc.collection.TicketingPurchaseAcDocs;
import com.ets.accountingdoc.collection.TicketingSalesAcDocs;
import com.ets.fe.acdoc.model.AccountingDocument;
import com.ets.fe.acdoc.model.TicketingPurchaseAcDoc;
import com.ets.fe.acdoc.model.TicketingSalesAcDoc;
import com.ets.fe.acdoc.ws.TicketingPAcDocWSClient;
import com.ets.fe.acdoc.ws.TicketingSAcDocWSClient;
import com.ets.fe.util.Enums;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

/**
 *
 * @author Yusuf
 */
public class AcDocBulkTask extends SwingWorker<List<AccountingDocument>, Integer> {

    private JProgressBar progressBar;
    private Enums.AcDocType doctype = null;
    private Enums.ClientType clienttype = null;
    private Long clientid = null;
    private Date dateFrom = null;
    private Date dateTo = null;
    private String docClass;

    public AcDocBulkTask(Enums.AcDocType doctype, Enums.ClientType clienttype,
            Long clientid, Date dateFrom, Date dateTo, JProgressBar progressBar,String docClass) {

        this.docClass = docClass;
        this.doctype = doctype;
        this.clienttype = clienttype;
        this.clientid = clientid;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.progressBar = progressBar;
    }

    @Override
    protected List<AccountingDocument> doInBackground() throws Exception {

        List<AccountingDocument> list = new ArrayList<>();

        if ("SALES".equals(docClass)) {
            TicketingSAcDocWSClient client = new TicketingSAcDocWSClient();
            TicketingSalesAcDocs docs = client.outstandingInvoices(doctype, clienttype, clientid, dateFrom, dateTo);

            for (TicketingSalesAcDoc doc : docs.getList()) {
                list.add(doc);
            }
        } else if ("PURCHASE".equals(docClass)) {
            TicketingPAcDocWSClient client = new TicketingPAcDocWSClient();
            TicketingPurchaseAcDocs docs = client.outstandingInvoices(doctype, clientid, dateFrom, dateTo);

            for (TicketingPurchaseAcDoc doc : docs.getList()) {
                list.add(doc);
            }
        }
        return list;
    }

    @Override
    protected void done() {
        setProgress(100);
    }
}
