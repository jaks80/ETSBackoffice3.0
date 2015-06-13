package com.ets.fe.client.task;

import com.ets.fe.client.model.Customer;
import com.ets.fe.client.ws.CustomerWSClient;
import java.util.List;
import javax.swing.SwingWorker;

/**
 *
 * @author Yusuf
 */
public class CustomerTask extends SwingWorker<Void, Integer> {

    private Customer customer;
    private List<Customer> customers;
    private String taskType = "";

    public CustomerTask(Customer customer) {
        this.customer = customer;
    }

    public CustomerTask(List<Customer> customers, String taskType) {
        this.customers = customers;
        this.taskType = taskType;
    }

    @Override
    protected Void doInBackground() {

        CustomerWSClient client = new CustomerWSClient();
        if (taskType.equals("BULKCREATE")) {
            int progress = 0;
            for (Customer a : customers) {
                client.create(a);
                progress++;
                float percentDone = (progress * 100) / customers.size();
                setProgress((int) Math.round(percentDone));
            }
            return null;
        }

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
