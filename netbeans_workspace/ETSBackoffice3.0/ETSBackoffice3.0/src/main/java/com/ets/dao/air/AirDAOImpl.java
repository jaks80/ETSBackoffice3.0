package com.ets.dao.air;

import com.ets.dao.generic.GenericDAOImpl;
import com.ets.domain.pnr.Pnr;
import java.util.Date;
import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Yusuf
 */
@Service("airDAO")
@Transactional
public class AirDAOImpl extends GenericDAOImpl<Pnr, Long> implements AirDAO {

    public AirDAOImpl() {

    }

    public Pnr findPnr(String gdsPnr, Date pnrCreationDate) {

        String hql = "select distinct p from Pnr p "
                + "left join fetch p.tickets as t "
                + "left join fetch p.segments "
                //+ "left join fetch p.servicingCareer "
                + "where p.gdsPnr = :gdsPnr and p.pnrCreationDate = :pnrCreationDate";

        Query query = getSession().createQuery(hql);
        query.setParameter("gdsPnr", gdsPnr);
        query.setParameter("pnrCreationDate", pnrCreationDate);

        Pnr pnr = null;
        List l = query.list();
        if(l.size() > 0){
         pnr = (Pnr) l.get(0);
        }

        return pnr;
    }

    public List<Object> findPnr(String tktNo, String surName) {           /*This method returns object because */
        /*left join fetch initialize segments under ticket*/

        String hql = "from Ticket as t " /*updating refund removes assosiation with old ticket*/
                + "left join t.pnr as p " /*This way we get independant segments which does not effect*/
                //+ "left join fetch p.servicingCareer "
                + "le..ft join t.segments as s " /*on oldticket and segment relation*/
                + "left join fetch p.ticketingAgt "
                + "left join fetch t.purchaseAccountingDocumentLine as pacdocline "
                + "left join pacdocline.purchaseAccountingDocument as pacdoc "
                + "where t.ticketNo = ? and t.paxSurName = ? ";
        List results = null;
        return results;
    }
}
