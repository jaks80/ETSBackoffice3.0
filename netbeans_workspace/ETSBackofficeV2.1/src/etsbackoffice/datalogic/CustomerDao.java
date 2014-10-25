/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package etsbackoffice.datalogic;

import etsbackoffice.domain.Customer;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public interface CustomerDao {

    public void store(Customer customer);

    public void delete(int custID);

    public Customer findByID(long custID);

    public List<Customer> searchByNameLike(String surName, String foreName);

    public List<Customer> searchByForeNameLike(String surName, String foreName);

    public List<Customer> searchByPostCodeLike(String postCode);

    public List<Customer> searchByTelNo(String telNo);

    public List<Customer> findAll();

    public List<Customer> findAllSummery();
    
    public List findCustomerNameList();
}
