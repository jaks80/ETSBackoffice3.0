package com.ets.fe.acdoc.task;

import com.ets.accountingdoc.collection.TicketingPurchaseAcDocs;
import com.ets.accountingdoc.collection.TicketingSalesAcDocs;
import com.ets.fe.acdoc.model.AccountingDocument;
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
public class DueInvoiceTask extends SwingWorker<List<AccountingDocument>, Integer> {

    private JProgressBar progressBar;
    private AcDocType doctype = null;
    private ClientType clienttype = null;
    private TicketingType ticketingType = null;
    private Long clientid = null;
    private Date dateFrom = null;
    private Date dateTo = null;
    private String docClass;

    public DueInvoiceTask(TicketingType ticketingType,AcDocType doctype, ClientType clienttype,
            Long clientid, Date dateFrom, Date dateTo, JProgressBar progressBar,String docClass) {

        this.docClass = docClass;
        this.doctype = doctype;
        this.clienttype = clienttype;
        this.clientid = clientid;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.progressBar = progressBar;
        this.ticketingType = ticketingType;
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
            TicketingPurchaseAcDocs docs = client.outstandingInvoices(ticketingType,doctype, clientid, 
                    dateFrom, dateTo);

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
