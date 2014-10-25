package etsbackoffice.datalogic;

import etsbackoffice.domain.Customer;
import java.util.ArrayList;
import java.util.List;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Yusuf
 */
public class HibernateCustomerDao extends HibernateDaoSupport implements CustomerDao {

    @Transactional
    public void store(Customer customer) {
        getHibernateTemplate().saveOrUpdate(customer);
    }

    public void delete(int custID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Customer> searchByNameLike(String surName, String foreName) {
        surName = surName.concat("%");
        foreName = foreName.concat("%");

        String hql = "from Customer c where c.surName like ? and c.foreName like ?";
        return getHibernateTemplate().find(hql, surName, foreName);
    }

    public List<Customer> searchByForeNameLike(String surName, String foreName) {
        if(foreName != null){
        foreName = foreName.concat("%");
        }
        String hql = "from Customer c where c.surName = ? and c.foreName like ?";
        return getHibernateTemplate().find(hql, surName, foreName);
    }

    public List<Customer> searchByPostCodeLike(String postCode) {
        postCode = postCode.concat("%");

        String hql = "from Customer c where c.postCode like ? ";
        return getHibernateTemplate().find(hql, postCode);
    }

    public List<Customer> searchByTelNo(String telNo) {
        String hql = "from Customer c where c.telNo = ? ";
        return getHibernateTemplate().find(hql, telNo);
    }

    public List<Customer> findAll() {
        String hql = "select customer  from Customer customer "
                + "left join fetch customer.createdBy "
                + "left join fetch customer.lastModifiedBy";
        return getHibernateTemplate().find(hql);
    }

    public List<Customer> findAllSummery() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List findCustomerNameList() {

        List customerNameList = new ArrayList();
        String hql = "select customer.surName,customer.foreName,customer.postCode,customer.contactableId from Customer as customer";
        List results = getHibernateTemplate().find(hql);

        for (int i = 0; i < results.size(); i++) {

            Object[] objects = (Object[]) results.get(i);
            String name = objects[0].toString() + "/" + objects[1].toString();
            String postCode = (String) objects[2];
            String id = objects[3].toString();

            customerNameList.add(name + "-" + postCode + "-" + id);
        }
        return customerNameList;
    }

    public Customer findByID(long custID) {
        return getHibernateTemplate().get(Customer.class, custID);
    }
}
