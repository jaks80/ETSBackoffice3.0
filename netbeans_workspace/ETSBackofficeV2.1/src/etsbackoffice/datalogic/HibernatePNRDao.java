package etsbackoffice.datalogic;

import etsbackoffice.domain.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Yusuf
 */
public class HibernatePNRDao extends HibernateDaoSupport implements PNRDao {

    @Transactional
    public void save(PNR pnr) {
        getHibernateTemplate().saveOrUpdate(pnr);
    }

    @Transactional
    public void deletePnr(PNR pnr) {
        getHibernateTemplate().delete(pnr);
    }
    
    @Transactional
    public void deleteService(Services s) {
        getHibernateTemplate().delete(s);
    }
    
    //For file reading
    public PNR loadPNR(String gdsPnr, Date pnrCreationDate) {

        String hql = "select distinct pnr from PNR pnr "
                + "left join fetch pnr.tickets as t "
                + "left join fetch t.purchaseAccountingDocumentLine "
                +" left join fetch t.accountingDocumentLine l "
                +" left join fetch l.accountingDocument "
                + "left join fetch pnr.segments "
                + "left join fetch pnr.servicingCareer "
                + "left join fetch pnr.ticketingAgt "
                + "left join fetch pnr.purchaseAccountingDocuments "
                + "where pnr.gdsPNR = ? and pnr.pnrCreationDate=?";
             //List<Object> result =    getHibernateTemplate().find(hql, gdsPnr, pnrCreationDate);
        return (PNR) DataAccessUtils.uniqueResult(getHibernateTemplate().find(hql, gdsPnr, pnrCreationDate));
    }

    //Filereading
    public List<Object> loadPNR(String tktNo, String surName) {           /*This method returns object because */
                                                                                  /*left join fetch initialize segments under ticket*/
        String hql = "from Ticket as t "                                             /*updating refund removes assosiation with old ticket*/
                + "left join t.pnr as p "                                         /*This way we get independant segments which does not effect*/
                + "left join fetch p.servicingCareer "
                + "left join t.segments as s "                                    /*on oldticket and segment relation*/
                + "left join fetch p.ticketingAgt "
                + "left join fetch t.purchaseAccountingDocumentLine as pacdocline "
                + "left join pacdocline.purchaseAccountingDocument as pacdoc "
                + "where t.ticketNo = ? and t.paxSurName = ? ";
            List results = getHibernateTemplate().find(hql, tktNo, surName);
        return results;
    }

    //Complete pnr details with accounting document
    public PNR findCompletePNR(long pnrID) {
        PNR pnr = new PNR();

        String hql = "select distinct p from PNR as p "
                + "left join fetch p.servicingCareer "
                + "left join fetch p.agent "
                + "left join fetch p.customer "
                + "left join fetch p.ticketingAgt "
                + "left join fetch p.gds "
                //+ "left join fetch p.accountingDocuments "
                //+ "left join fetch p.purchaseAccountingDocuments "
                + "left join fetch p.tickets as t "
                + "left join fetch t.ticketRefundDetails as tr "
                + "left join fetch t.accountingDocumentLine "
                + "left join fetch p.services as s "
                + "left join fetch s.accountingDocumentLine "
                + "left join fetch t.purchaseAccountingDocumentLine as pa "
                + "where p.pnrId = ?";

        pnr = (PNR) DataAccessUtils.uniqueResult(getHibernateTemplate().find(hql, pnrID));

        return pnr;
    }

    public List<PNR> findUninvoicedPnr() {
        List<PNR> pnrs = new ArrayList();
       
        String hql = "from Ticket t "
                + "left join fetch t.accountingDocumentLine as a "
                //+ "left join fetch t.purchaseAccountingDocumentLine as pa "
                + "inner join  t.pnr "
                + "left join fetch t.pnr.servicingCareer "
                + "where a is null group by t.pnr.pnrId";

        List results = getHibernateTemplate().find(hql);
        Iterator it = results.iterator();

        while (it.hasNext()) {
            Object[] objects = (Object[]) it.next();
            Ticket leadPaxTicket = (Ticket) objects[0];
            PNR pnr = (PNR) objects[1];
            pnr.setLeadPax(leadPaxTicket);
            pnrs.add(pnr);
        }        
        return pnrs;
    }

    public List<PNR> pnrsToday(){
     List<PNR> pnrs = new ArrayList();

//        String hql = "from Ticket t "
//                + "left join fetch t.accountingDocumentLine as a "
//                + "left join fetch t.purchaseAccountingDocumentLine as pa "
//                + "inner join  t.pnr as p "
//                + "left join fetch t.pnr.servicingCareer "
//                + "left join fetch p.tickets as t1 "
//                + "left join fetch t1.accountingDocumentLine "
//                + "where t.docIssuedate = ? group by p.pnrId";

             String hql = "from Ticket t "
                + "left join fetch t.accountingDocumentLine as a "
                + "left join fetch t.purchaseAccountingDocumentLine as pa "

                + "inner join t.pnr as p "
                + "left join fetch t.pnr.servicingCareer "
                + "left join fetch p.tickets as t1 "     
                + "where t.docIssuedate = ? group by t.pnr.pnrId";
             
        List results = getHibernateTemplate().find(hql, new java.util.Date());
        
        Iterator it = results.iterator();

        while (it.hasNext()) {
            Object[] objects = (Object[]) it.next();
            Ticket leadPaxTicket = (Ticket) objects[0];
            PNR pnr = (PNR) objects[1];
            pnr.setLeadPax(leadPaxTicket);
            pnrs.add(pnr);
        }
        Set<PNR> temps = new LinkedHashSet<PNR>(pnrs);
        
        return new ArrayList<PNR>(temps);
    }

    public List<PNR> bookedPnrs(){
     List<PNR> pnrs = new ArrayList();

        String hql = "from Ticket t "
                + "inner join  t.pnr "
                + "left join fetch t.pnr.servicingCareer "
                + "where t.tktStatus = 1 group by t.pnr.pnrId";

        List results = getHibernateTemplate().find(hql);
        Iterator it = results.iterator();

        while (it.hasNext()) {
            Object[] objects = (Object[]) it.next();
            Ticket leadPaxTicket = (Ticket) objects[0];
            PNR pnr = (PNR) objects[1];
            pnr.setLeadPax(leadPaxTicket);
            pnrs.add(pnr);
        }
        return pnrs;
    }

    public List<PNR> searchByGdsPnr(String gdsPnr) {
        
        String hql = "from PNR as p "
                + "left join fetch p.servicingCareer "
                + "left join p.tickets as t "                
                + "where p.gdsPNR = ? "
                + "and t.passengerNo = (select min(t.passengerNo) from Ticket t where t.pnr.pnrId = p.pnrId) group by p.pnrId";
        List results = getHibernateTemplate().find(hql, gdsPnr);
        List<PNR> pnrs = new ArrayList();

        for(int i =0;i<results.size();i++){
            Object[] objects = (Object[]) results.get(i);
            PNR pnr = (PNR) objects[0];
            Ticket ticket = (Ticket) objects[1];
            pnr.setLeadPax(ticket);
            pnrs.add(pnr);
        }
        return pnrs;
    }    

    public List<PNR> searchByPaxName(String surName, String foreName) {
        surName = surName.concat("%");
        foreName = foreName.concat("%");

        String hql = "from Ticket as t "
                + "left join fetch t.pnr as p "
                + "left join fetch p.servicingCareer "
                + "where t.paxSurName like ? and t.paxForeName like ? "
                + "group by p.pnrId ";

        List<Ticket> tickets = getHibernateTemplate().find(hql, surName, foreName);
        List<PNR> pnrs = new ArrayList();

        for (int i = 0; i < tickets.size(); i++) {
            PNR pnr = tickets.get(i).getPnr();
            pnr.setLeadPax(tickets.get(i));
            pnrs.add(pnr);
        }
        return pnrs;
    }

    public List<PNR> searchByTktNo(String tktNo) {
        String hql = "from Ticket as t "
                + "left join fetch t.pnr as p "
                + "left join fetch p.servicingCareer "
                + "where t.ticketNo like ?"
                + "group by t";

        List<Ticket> tickets = getHibernateTemplate().find(hql, tktNo);
        List<PNR> pnrs = new ArrayList();

        for (int i = 0; i < tickets.size(); i++) {
            PNR pnr = tickets.get(i).getPnr();
            pnr.setLeadPax(tickets.get(i));
            pnrs.add(pnr);
        }
        return pnrs;
    }
        
    public List<PNRRemark> loadRemarks(PNR pnr) {
        return getHibernateTemplate().find("from PNRRemark as prmk where prmk.pnr = ?",pnr);
    }

    @Transactional
    public void saveRemark(PNRRemark remark) {
        getHibernateTemplate().save(remark);
    }

    public void saveBulkRemark(List<PNRRemark> remark) {
        getHibernateTemplate().saveOrUpdateAll(remark);
    }    
}
