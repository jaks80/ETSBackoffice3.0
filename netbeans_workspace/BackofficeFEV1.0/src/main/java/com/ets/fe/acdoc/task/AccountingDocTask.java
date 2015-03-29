package com.ets.fe.acdoc.task;

import com.ets.fe.acdoc.model.AccountingDocument;
import com.ets.fe.acdoc.model.TicketingPurchaseAcDoc;
import com.ets.fe.acdoc.model.TicketingSalesAcDoc;
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

    /**
     * Use it to void Document
     *
     * @param accountingDocument
     * @param saleType
     * @param taskType
     */
    public AccountingDocTask(AccountingDocument accountingDocument, Enums.SaleType saleType, String taskType) {
        this.accountingDocument = accountingDocument;
        this.saleType = saleType;
        this.taskType = taskType;
    }

    @Override
    protected AccountingDocument doInBackground() throws Exception {

        if (Enums.SaleType.TKTSALES.equals(saleType)) {
            TicketingSAcDocWSClient client = new TicketingSAcDocWSClient();
            switch (taskType) {
                case "DETAILS":
                    accountingDocument = client.getbyId(id);
                    break;
                case "VOID":
                    TicketingSalesAcDoc doc = (TicketingSalesAcDoc) accountingDocument;
                    client._void(doc);
                    break;
                case "DELETE":
                    TicketingSalesAcDoc void_doc = (TicketingSalesAcDoc) accountingDocument;
                    client.delete(void_doc.getId());
                    break;
            }
        } else if (Enums.SaleType.TKTPURCHASE.equals(saleType)) {
            TicketingPAcDocWSClient client = new TicketingPAcDocWSClient();
            switch (taskType) {
                case "DETAILS":
                    accountingDocument = client.getbyId(id);
                    break;
                case "VOID":
                    TicketingPurchaseAcDoc doc = (TicketingPurchaseAcDoc) accountingDocument;
                    client._void(doc);
                    break;
                case "DELETE":
                    TicketingPurchaseAcDoc void_doc = (TicketingPurchaseAcDoc) accountingDocument;
                    client.delete(void_doc.getId());
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
