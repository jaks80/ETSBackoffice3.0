package com.ets.fe.acdoc.task;

import com.ets.accountingdoc.collection.TicketingPurchaseAcDocs;
import com.ets.accountingdoc.collection.TicketingSalesAcDocs;
import com.ets.fe.acdoc.model.AccountingDocument;
import com.ets.fe.acdoc.model.TicketingPurchaseAcDoc;
import com.ets.fe.acdoc.model.TicketingSalesAcDoc;
import com.ets.fe.acdoc.ws.TicketingPAcDocWSClient;
import com.ets.fe.acdoc.ws.TicketingSAcDocWSClient;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingWorker;

/**
 *
 * @author Yusuf
 */
public class AcDocSummeryTask extends SwingWorker<List<AccountingDocument>, Integer> {

    private Long pnrId;
    private String docClass;

    public AcDocSummeryTask(Long pnrId, String docClass) {
        this.pnrId = pnrId;
        this.docClass = docClass;
    }

    @Override
    protected List<AccountingDocument> doInBackground() throws Exception {

        List<AccountingDocument> list = new ArrayList<>();

        if ("SALES".equals(docClass)) {
            TicketingSAcDocWSClient client = new TicketingSAcDocWSClient();
            TicketingSalesAcDocs docs = client.getByPnrId(pnrId);

            for (TicketingSalesAcDoc doc : docs.getList()) {
                list.add(doc);
            }
        } else if ("PURCHASE".equals(docClass)) {
            TicketingPAcDocWSClient client = new TicketingPAcDocWSClient();
            TicketingPurchaseAcDocs docs = client.getByPnrId(pnrId);

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
