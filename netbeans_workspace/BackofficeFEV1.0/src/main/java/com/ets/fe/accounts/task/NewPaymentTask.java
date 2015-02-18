package com.ets.fe.accounts.task;

import com.ets.fe.accounts.model.Payment;
import com.ets.fe.accounts.ws.PaymentWSClient;
import javax.swing.SwingWorker;

/**
 *
 * @author Yusuf
 */
public class NewPaymentTask extends SwingWorker<Payment, Integer> {

    private Payment payment;
   
    public NewPaymentTask(Payment payment) {
        this.payment = payment;
    }

    @Override
    protected Payment doInBackground() throws Exception {

        PaymentWSClient client = new PaymentWSClient();       
        Payment persistedPayment = client.create(payment);
 
        return payment;
    }

    @Override
    protected void done() {
        setProgress(100);
    }
}
