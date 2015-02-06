package com.ets.accountingdoc.dao;

import com.ets.GenericDAO;
import com.ets.accountingdoc.domain.Payment;
import com.ets.util.Enums;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public interface PaymentDAO extends GenericDAO<Payment, Long>{
    
    public List<Payment> findPaymentBySalesInvoice(Long invoice_id);
    
    public Payment findById(Long id);
    
    public List<Payment> findTSPaymentHistory(Enums.ClientType clienttype,Long clientid,Date dateStart,Date dateEnd,Enums.SaleType saleType);
}
