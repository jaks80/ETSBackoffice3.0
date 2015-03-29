package com.ets.fe.client.task;

import com.ets.fe.client.model.Customer;
import com.ets.fe.client.ws.CustomerWSClient;
import javax.swing.SwingWorker;

/**
 *
 * @author Yusuf
 */
public class CustomerTask extends SwingWorker<Void, Integer> {

    private Customer customer;

    public CustomerTask(Customer customer) {
        this.customer = customer;
    }

    @Override
    protected Void doInBackground() {

        CustomerWSClient client = new CustomerWSClient();
        if (customer.getId() == null) {
            client.create(customer);
        } else {
            client.update(customer);
        }

        return null;
    }

    @Override
    protected void done() {
        setProgress(100);
        if (!isCancelled()) {

        }
    }
}
