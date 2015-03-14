package com.ets.fe.accounts.task;

import com.ets.fe.accounts.model.CreditTransfer;
import com.ets.fe.accounts.ws.PaymentWSClient;
import javax.swing.JLabel;
import javax.swing.SwingWorker;

/**
 *
 * @author Yusuf
 */
public class NewCreditTransferTask extends SwingWorker<CreditTransfer, Integer> {

    private CreditTransfer creditTransfer;
    private JLabel message;
    public NewCreditTransferTask(CreditTransfer creditTransfer,JLabel message) {
       this.creditTransfer = creditTransfer;
       this.message = message;
    }

    @Override
    protected CreditTransfer doInBackground() throws Exception {                
        PaymentWSClient client = new PaymentWSClient();
        Integer status = client.createCreditTransfer(creditTransfer);
        if(status == 200){
         message.setText("Credit Successfully Transferred");
        }
        return null;
    }

    @Override
    protected void done() {
        setProgress(100);
    }
}
