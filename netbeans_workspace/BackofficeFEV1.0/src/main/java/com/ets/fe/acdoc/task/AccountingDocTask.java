package com.ets.fe.acdoc.task;

import com.ets.fe.acdoc.model.AccountingDocument;
import com.ets.fe.acdoc.ws.TicketingPAcDocWSClient;
import com.ets.fe.acdoc.ws.TicketingSAcDocWSClient;
import com.ets.fe.util.Enums;
import javax.swing.SwingWorker;

/**
 *
 * @author Yusuf
 */
public class AccountingDocTask extends SwingWorker<AccountingDocument, Integer> {

    private Long id;
    private AccountingDocument accountingDocument;
    private Enums.SaleType saleType;
    private String taskType;

    public AccountingDocTask(Long id, Enums.SaleType saleType, String taskType) {
        this.id = id;
        this.saleType = saleType;
        this.taskType = taskType;
    }

    @Override
    protected AccountingDocument doInBackground() throws Exception {

        if (Enums.SaleType.SALES.equals(saleType)) {
            TicketingSAcDocWSClient client = new TicketingSAcDocWSClient();
            switch (taskType) {
                case "DETAILS":
                    accountingDocument = client.getbyId(id);
                    break;
                case "VOID":
                    client._void(id);
                    break;
            }
        } else if (Enums.SaleType.PURCHASE.equals(saleType)) {
            TicketingPAcDocWSClient client = new TicketingPAcDocWSClient();
            switch (taskType) {
                case "DETAILS":
                    accountingDocument = client.getbyId(id);
                    break;
                case "VOID":
                    client._void(id);
                    break;
            }
        }

        return accountingDocument;
    }

    @Override
    protected void done() {
        setProgress(100);
    }
}
