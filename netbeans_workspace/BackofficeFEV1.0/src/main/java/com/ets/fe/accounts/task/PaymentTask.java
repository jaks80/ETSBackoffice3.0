package com.ets.fe.accounts.task;

import com.ets.fe.accounts.model.Payment;
import com.ets.fe.accounts.ws.PaymentWSClient;
import com.ets.fe.util.Enums;
import com.ets.fe.util.Enums.TaskType;
import javax.swing.SwingWorker;

/**
 *
 * @author Yusuf
 */
public class PaymentTask extends SwingWorker<Payment, Integer> {

    private Payment payment;
    private TaskType taskType = null;

    public PaymentTask(Payment payment, TaskType taskType) {
        this.payment = payment;
        this.taskType = taskType;
    }

    @Override
    protected Payment doInBackground() throws Exception {

        PaymentWSClient client = new PaymentWSClient();
        Payment persistedPayment = null;
        
        if (taskType.equals(Enums.TaskType.CREATE)) {
            persistedPayment = client.create(payment);
        } else if (taskType.equals(Enums.TaskType.VOID)) {
            persistedPayment = client.voidPayment(payment.getId());
        } else if (taskType.equals(Enums.TaskType.DELETE)) {
            client.deletePayment(payment.getId());
        }

        return persistedPayment;
    }

    @Override
    protected void done() {
        setProgress(100);
    }
}
