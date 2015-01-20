package com.ets.fe.acdoc.task;

import com.ets.fe.acdoc.model.Payment;
import com.ets.fe.acdoc.model.collection.Payments;
import com.ets.fe.acdoc.ws.PaymentWSClient;
import javax.swing.SwingWorker;

/**
 *
 * @author Yusuf
 */
public class PaymentTask extends SwingWorker<Payments, Integer> {

    private Payment payment;
    private Long invoiceId;

    public PaymentTask(Payment payment, Long invoiceId) {
        this.payment = payment;
        this.invoiceId = invoiceId;
    }

    @Override
    protected Payments doInBackground() throws Exception {

        PaymentWSClient client = new PaymentWSClient();
        Payments payments = new Payments();

        if (invoiceId == null) {
            Payment persistedPayment = client.create(payment);
            payments.add(persistedPayment);
        } else {
            payments = client.paymentBySalesInvoice(invoiceId);
        }
        return payments;
    }

    @Override
    protected void done() {
        setProgress(100);
    }
}
