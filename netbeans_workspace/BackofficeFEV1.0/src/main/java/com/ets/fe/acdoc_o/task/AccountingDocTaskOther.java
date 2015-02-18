package com.ets.fe.acdoc_o.task;

import com.ets.fe.acdoc.model.AccountingDocument;
import com.ets.fe.acdoc_o.ws.OtherSAcDocWSClient;
import com.ets.fe.util.Enums;
import javax.swing.SwingWorker;

/**
 *
 * @author Yusuf
 */
public class AccountingDocTaskOther extends SwingWorker<AccountingDocument, Integer> {

    private Long id;
    private AccountingDocument accountingDocument;
    private Enums.SaleType saleType;
    private String taskType;

    public AccountingDocTaskOther(Long id, Enums.SaleType saleType, String taskType) {
        this.id = id;
        this.saleType = saleType;
        this.taskType = taskType;
    }

    @Override
    protected AccountingDocument doInBackground() throws Exception {

        if (Enums.SaleType.OTHER.equals(saleType)) {
            OtherSAcDocWSClient client = new OtherSAcDocWSClient();
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
