package com.ets.fe.acdoc.task;

import com.ets.accountingdoc.collection.TicketingSalesAcDocs;
import com.ets.fe.acdoc.model.AccountingDocument;
import com.ets.fe.acdoc.model.TicketingSalesAcDoc;
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
    private List<AccountingDocument> acDocList = new ArrayList<>();

    public AcDocSummeryTask(Long pnrId) {
        this.pnrId = pnrId;
    }

    @Override
    protected List<AccountingDocument> doInBackground() throws Exception {

        acDocList = new ArrayList<>();
        TicketingSAcDocWSClient client = new TicketingSAcDocWSClient();
        TicketingSalesAcDocs docs = client.getByPnrId(pnrId);
        List<TicketingSalesAcDoc> list = docs.getList();

        for (TicketingSalesAcDoc doc : list) {
            this.getAcDocList().add(doc);
        }
        return getAcDocList();
    }

    @Override
    protected void done() {
        setProgress(100);
    }

    public List<AccountingDocument> getAcDocList() {
        return acDocList;
    }
}
