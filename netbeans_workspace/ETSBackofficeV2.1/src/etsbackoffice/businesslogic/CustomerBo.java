package etsbackoffice.businesslogic;

import etsbackoffice.datalogic.CustomerDao;
import etsbackoffice.domain.Customer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public class CustomerBo {

    private Customer customer;
    private List<Customer> customers;
    private CustomerDao customerDao;
    private List customerNameList;

    public CustomerBo() {
    }

    public void saveCustomer(Customer customer) {
        customerDao.store(customer);
    }

    public Customer findByID(long custID) {

        return customerDao.findByID(custID);
    }

    public List<Customer> loadAll() {
        return customerDao.findAll();
    }
    
    public List customerNameList() {
        this.customerNameList = customerDao.findCustomerNameList();
        Collections.sort(this.customerNameList);
        return this.customerNameList;
    }

    
    public List<Customer> searchCustomer(String name) {
        String[] data = null;
        String surName = null;
        String foreName = null;

        if (name.contains("/")) {
            data = name.split("/");
            surName = data[0];
            foreName = data[1];
        } else {
            surName = name;
        }

        List customersFound = customerDao.searchByNameLike(surName, foreName);
        return customersFound;
    }

        public List<Customer> searchCustomerForeNameLike(String name) {
        String[] data = null;
        String surName = null;
        String foreName = null;

        if (name.contains("/")) {
            data = name.split("/");
            surName = data[0].trim();
            foreName = data[1].trim();
        } else {
            surName = name;
        }

        List customersFound = customerDao.searchByForeNameLike(surName, foreName);
        return customersFound;
    }

    public List<Customer> findCustomer(String element, int type) {
        String[] data = null;
        String surName = null;
        String foreName = null;

        if (type == 1) {
            if (element.contains("/")) {
                data = element.split("/");
                surName = data[0];
                foreName = data[1];
            } else {
                surName = element;
            }
        }
        List<Customer> custList = new ArrayList();
        if (type == 1) {
            custList = customerDao.searchByNameLike(surName, foreName);
        } else if (type == 2) {
            custList = customerDao.searchByPostCodeLike(element);
        } else if (type == 3) {
            custList = customerDao.searchByTelNo(element);
        }
        return custList;
    }

    public Customer getCustomer() {
        System.out.println("Name: " +this.customer.getSurName());
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setCustomerDao(CustomerDao customertDao) {
        this.customerDao = customertDao;
    }
}
