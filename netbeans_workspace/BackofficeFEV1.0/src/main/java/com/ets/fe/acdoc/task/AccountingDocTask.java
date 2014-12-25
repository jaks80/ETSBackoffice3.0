package com.ets.fe.acdoc.task;

import com.ets.fe.acdoc.model.AccountingDocument;
import com.ets.fe.acdoc.model.TicketingSalesAcDoc;
import com.ets.fe.acdoc.ws.TicketingSAcDocWSClient;
import javax.swing.SwingWorker;

/**
 *
 * @author Yusuf
 */
public class AccountingDocTask extends SwingWorker<AccountingDocument, Integer> {

    private Long id;
    private AccountingDocument accountingDocument;
    private String docClass;
    private String taskType;

    public AccountingDocTask(Long id, String docClass, String taskType) {
        this.id = id;
        this.docClass = docClass;
        this.taskType = taskType;
    }

    @Override
    protected AccountingDocument doInBackground() throws Exception {

        if ("SALES".equals(docClass)) {
            TicketingSAcDocWSClient client = new TicketingSAcDocWSClient();
            switch (taskType) {
                case "DETAILS":
                    accountingDocument = client.getbyId(id);
                    break;
                case "VOID":
                    client._void(id);
                    break;
                case "PAYMENT":
                    client.createNewPayment((TicketingSalesAcDoc)accountingDocument);
                    break;    
            }
        } else if ("PURCHASE".equals(docClass)) {

        }

        return accountingDocument;
    }

    @Override
    protected void done() {
        setProgress(100);
    }
}
