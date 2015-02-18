package com.ets.accounts.dao;

import com.ets.GenericDAO;
import com.ets.accounts.model.Payment;
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
    
    public List<Payment> findTicketingPaymentHistory(Enums.ClientType clienttype,Long clientid,Date dateStart,Date dateEnd,Enums.SaleType saleType);
    
    public List<Payment> findOtherPaymentHistory(Enums.ClientType clienttype, Long clientid, Date from, Date to, Enums.SaleType salesType);
}
