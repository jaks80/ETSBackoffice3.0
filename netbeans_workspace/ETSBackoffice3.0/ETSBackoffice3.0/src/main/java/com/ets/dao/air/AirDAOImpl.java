package com.ets.dao.air;

import com.ets.dao.generic.GenericDAOImpl;
import com.ets.domain.pnr.Pnr;
import java.util.Date;
import java.util.List;
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

        String hql = "select distinct pnr from Pnr pnr "
                + "left join fetch pnr.tickets as t "
                + "left join fetch pnr.segments "
                + "left join fetch pnr.servicingCareer "                
                + "where pnr.gdsPnr = ? and pnr.pnrCreationDate=?";

        Pnr pnr = new Pnr();

        return pnr;
    }

    public List<Object> findPnr(String tktNo, String surName) {           /*This method returns object because */
        /*left join fetch initialize segments under ticket*/

        String hql = "from Ticket as t " /*updating refund removes assosiation with old ticket*/
                + "left join t.pnr as p " /*This way we get independant segments which does not effect*/
                + "left join fetch p.servicingCareer "
                + "le..ft join t.segments as s " /*on oldticket and segment relation*/
                + "left join fetch p.ticketingAgt "
                + "left join fetch t.purchaseAccountingDocumentLine as pacdocline "
                + "left join pacdocline.purchaseAccountingDocument as pacdoc "
                + "where t.ticketNo = ? and t.paxSurName = ? ";
        List results = null;
        return results;
    }
}
