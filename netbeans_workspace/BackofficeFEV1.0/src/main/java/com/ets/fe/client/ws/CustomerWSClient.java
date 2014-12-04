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
        public Customers find(String surName,String foreName, String postCode,String telNo) {
        String url = APIConfig.get("ws.customer.customers") +"?surName="+surName
                                                            +"&foreName="+foreName
                                                            +"&postCode="+postCode
                                                            +"&telNo="+telNo;
        return RestClientUtil.getEntity(Customers.class, url, new Customers());
        
    }

    public Customer create(Customer customer) {
       String url = APIConfig.get("ws.customer.new");
       Customer persistedCustomer = RestClientUtil.postEntity(Customer.class, url, customer);
       return persistedCustomer;
    }

    public Customer update(Customer customer) {
        String url = APIConfig.get("ws.customer.update");
        Customer persistedCustomer = RestClientUtil.putEntity(Customer.class, url, customer);
       return persistedCustomer;
    }

    public Integer delete(long id) {
        String url = APIConfig.get("ws.customer.delete"+id);
        Integer status = RestClientUtil.deleteById(url);
        return status;
    }
}
