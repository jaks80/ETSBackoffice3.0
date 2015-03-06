package com.ets.fe.client.ws;

import com.ets.fe.APIConfig;
import com.ets.fe.client.collection.Customers;
import com.ets.fe.client.model.Customer;
import com.ets.fe.util.RestClientUtil;

/**
 *
 * @author Yusuf
 */
public class CustomerWSClient {

    public Customers find(String surName, String foreName, String postCode, String telNo) {

        StringBuilder sb = new StringBuilder();
        sb.append(APIConfig.get("ws.customer.customers"));
        if (surName != null) {
            sb.append("?surName=").append(surName);
        } else if (foreName != null) {
            sb.append("&foreName=").append(foreName);
        } else if (postCode != null) {
            sb.append("&postCode=").append(postCode);
        } else if (telNo != null) {
            sb.append("&telNo=").append(telNo);
        }

        return RestClientUtil.getEntity(Customers.class, sb.toString(), new Customers());

    }

    public Customer create(Customer customer) {
        String url = APIConfig.get("ws.customer.new");
        Customer persistedCustomer = RestClientUtil.postEntity(Customer.class, url, customer);
        return persistedCustomer;
    }

    public Customer update(Customer customer) {
        customer.recordUpdateBy();
        String url = APIConfig.get("ws.customer.update");
        Customer persistedCustomer = RestClientUtil.putEntity(Customer.class, url, customer);
        return persistedCustomer;
    }

    public Integer delete(long id) {
        String url = APIConfig.get("ws.customer.delete" + id);
        Integer status = RestClientUtil.deleteById(url);
        return status;
    }
}
