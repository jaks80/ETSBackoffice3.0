package com.ets.fe.client.task;

import com.ets.fe.client.collection.Customers;
import com.ets.fe.client.ws.CustomerWSClient;

/**
 *
 * @author Yusuf
 */
public class CustomerSearchTask extends ContactableSearchTask {

    private String name = "";
    private String postCode = "";
    private String telNo = "";

    public CustomerSearchTask(String name, String postCode, String telNo) {
        this.name = name;
        this.telNo = telNo;
        this.postCode = postCode;
    }

    @Override
    protected Customers doInBackground() {
        String[] nameElement = null;
        String surName = null;
        String foreName = null;

        if (name != null && name.contains("/")) {
            nameElement = name.split("/");
            surName = nameElement[0].trim();
            if(nameElement.length>1){
             foreName = nameElement[1].trim();
            }
        } else if(name !=null){
            surName = name.trim();
        }
        setProgress(30);
        CustomerWSClient client = new CustomerWSClient();
        Customers customers = client.find(surName, foreName, postCode, telNo);
        return customers;
    }

    @Override
    protected void done() {
        setProgress(100);
        if (!isCancelled()) {

        }
    }
}
