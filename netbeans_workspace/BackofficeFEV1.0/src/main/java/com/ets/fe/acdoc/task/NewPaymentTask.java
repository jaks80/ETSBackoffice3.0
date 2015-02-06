package com.ets.fe.acdoc.task;

import com.ets.fe.acdoc.model.Payment;
import com.ets.fe.acdoc.ws.PaymentWSClient;
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
        //Payments payments = new Payments();
        Payment persistedPayment = client.create(payment);
 
        return payment;
    }

    @Override
    protected void done() {
        setProgress(100);
    }
}