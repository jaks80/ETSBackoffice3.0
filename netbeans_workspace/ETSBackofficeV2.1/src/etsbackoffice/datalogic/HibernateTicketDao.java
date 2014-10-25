package etsbackoffice.datalogic;

import etsbackoffice.domain.Ticket;
import etsbackoffice.domain.TicketRefundDetails;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Yusuf
 */
public class HibernateTicketDao  extends HibernateDaoSupport implements TicketDao{

    @Transactional
    public void save(Ticket ticket) {
        getHibernateTemplate().saveOrUpdate(ticket);
    }

    
    //FileReading
    public boolean isExistInDb(String tktNo, String surName, int status) {
        String hql = "from Ticket t where t.ticketNo = ? and t.paxSurName = ? and t.tktStatus =? ";

        Ticket t = (Ticket) DataAccessUtils.uniqueResult(getHibernateTemplate().find(hql, tktNo, surName, status));

        if (t == null) {
            System.out.println("Ticket not exist");
            return false;
        } else {
            System.out.println("Ticket exist");
            return true;
        }
    }
    
    @Transactional
    public void deleteTicket(Ticket ticket) {
        getHibernateTemplate().delete(ticket);
    }
    
       @Transactional
    public void deleteAllTicket(List<Ticket> tickets) {
        getHibernateTemplate().deleteAll(tickets);
    }
    
    @Transactional
    public void voidTicket(String pnr, String tktno, String surname) {
        BigDecimal bf = new BigDecimal("0.00");
        BigDecimal tax = new BigDecimal("0.00");        
        BigDecimal bspCom = new BigDecimal("0.00");
        
        //BigDecimal gf = new BigDecimal("0.00");
        //BigDecimal disc = new BigDecimal("0.00");        
        //BigDecimal atol = new BigDecimal("0.00");
        
        String hql = "update Ticket t set t.tktStatus = 5,t.baseFare=?,t.tax=?,t.bspCom =? "
                + "where t.ticketNo = ? and t.paxSurName = ?";

        getHibernateTemplate().bulkUpdate(hql, bf, tax, bspCom,tktno, surname);
    }

    @Transactional
    public void saveAll(Set<Ticket> tickets) {
        getHibernateTemplate().saveOrUpdateAll(tickets);
    }

    public Ticket loadTicketItinerary(long ticketId) {
        String hql = "from Ticket as t "
                + "left join fetch t.pnr "
                + "left join fetch t.segments "
                + "where t.ticketId = ?";
        Ticket ticket = new Ticket();
        ticket = (Ticket) DataAccessUtils.uniqueResult(getHibernateTemplate().
                find(hql, ticketId));
        return ticket;
    }
    
    public TicketRefundDetails loadTicketRfdDetails(long id) {
        String hql = "from TicketRefundDetails as t "
                //+ "left join fetch t.ticketRefundDetails "
                + "where t.ticket.ticketId = ?";
        
        return (TicketRefundDetails) DataAccessUtils.uniqueResult(getHibernateTemplate().
                find(hql, id));
    }
    
     public List<Ticket> saleReport(int ticketingType, Integer gdsId, Long tktingAgtID,Integer tktStatus,String career, Date from, Date to) {
        String hql = "";
        
        if(ticketingType==1){
            hql = "select t "
                + "from Ticket as t "
                + "left join fetch t.accountingDocumentLine "
                + "left join fetch t.purchaseAccountingDocumentLine as pa "
                + "left join fetch t.ticketRefundDetails "    
                + "inner join fetch t.pnr as p "
                + "left join fetch p.servicingCareer as s "
                + "where (t.tktStatus <> 1) and "
                + "(:gdsId is null or p.gds.gdsId = :gdsId) and "
                + "(p.ticketingAgt.contactableId = :tktingAgtID) and "
                + "(:tktStatus is null or t.tktStatus = :tktStatus) and "   
                + "(:career is null or s.code = :career) and "    
                + "(:from is null or t.docIssuedate >= :from) and "
                + "(:to is null or t.docIssuedate <= :to) group by t";
        }else if(ticketingType==2){
            hql = "select t "
                + "from Ticket as t "
                + "left join fetch t.ticketRefundDetails "    
                + "left join fetch t.accountingDocumentLine "
                + "left join fetch t.purchaseAccountingDocumentLine as pa "
                + "inner join fetch t.pnr as p "
                + "left join fetch p.servicingCareer as s "
                + "where (t.tktStatus <> 1) and "
                + "(:gdsId is null or p.gds.gdsId = :gdsId) and "
                + "(:tktingAgtID is null or p.ticketingAgt = :tktingAgtID) and p.ticketingAgt.contactableId<>1 and "
               + "(:tktStatus is null or t.tktStatus = :tktStatus) and "   
                + "(:career is null or s.code = :career) and "         
                + "(:from is null or t.docIssuedate >= :from) and "
                + "(:to is null or t.docIssuedate <= :to) group by t";
        }else if(ticketingType==0){
            hql = "select t "
                + "from Ticket as t "
                + "left join fetch t.ticketRefundDetails "    
                + "left join fetch t.accountingDocumentLine "
                + "left join fetch t.purchaseAccountingDocumentLine as pa "
                + "inner join fetch t.pnr as p "
                + "left join fetch p.servicingCareer as s "
                + "where (t.tktStatus <> 1) and "
                + "(:gdsId is null or p.gds.gdsId = :gdsId) and "
                + "(:tktingAgtID is null or p.ticketingAgt = :tktingAgtID) and "
                + "(:tktStatus is null or t.tktStatus = :tktStatus) and "   
                + "(:career is null or s.code = :career) and "        
                + "(:from is null or t.docIssuedate >= :from) and "
                + "(:to is null or t.docIssuedate <= :to) group by t";
        }          
                
        List<Ticket> tickets = getHibernateTemplate().findByNamedParam(hql, new String[]{"gdsId", "tktingAgtID","tktStatus","career", "from", "to"},
                new Object[]{gdsId, tktingAgtID,tktStatus,career, from, to});
        return tickets;
    }    
     
     public List<Ticket> saleReportDetails(int ticketingType, Integer gdsId, Long tktingAgtID,
             Integer tktStatus,String career, Date from, Date to,String bookingOid) {
        String hql = "";
        
        if(ticketingType==1){
            hql = "select t "
                + "from Ticket as t "
                + "left join fetch t.accountingDocumentLine as al "
                + "left join fetch al.accountingDocument "    
                + "left join fetch t.purchaseAccountingDocumentLine as pa "
                + "left join fetch t.ticketRefundDetails "    
                + "inner join fetch t.pnr as p "
                + "left join fetch p.agent as a "
                + "left join fetch p.customer as c "    
                + "left join fetch p.servicingCareer as s "
                + "where (t.tktStatus <> 1) and "
                + "(:gdsId is null or p.gds.gdsId = :gdsId) and "
                + "(p.ticketingAgt.contactableId = :tktingAgtID) and "
                + "(:tktStatus is null or t.tktStatus = :tktStatus) and "   
                + "(:career is null or s.code = :career) and "    
                + "(:from is null or t.docIssuedate >= :from) and "
                + "(:to is null or t.docIssuedate <= :to ) and "
                + "(:bookingOid is null or p.bookingAgtOID = :bookingOid) "    
                    + "group by t";
        }else if(ticketingType==2){
            hql = "select t "
                + "from Ticket as t "
                + "left join fetch t.accountingDocumentLine as al "
                + "left join fetch al.accountingDocument "    
                + "left join fetch t.purchaseAccountingDocumentLine as pa "
                + "left join fetch t.ticketRefundDetails "    
                + "inner join fetch t.pnr as p "
                + "left join fetch p.agent as a "
                + "left join fetch p.customer as c "    
                + "left join fetch p.servicingCareer as s "
                + "where "
                + "(:gdsId is null or p.gds.gdsId = :gdsId) and "
                + "(:tktingAgtID is null or p.ticketingAgt = :tktingAgtID)  and "
               + "(:tktStatus is null or t.tktStatus = :tktStatus) and "   
                + "(:career is null or s.code = :career) and "         
                + "(:from is null or t.docIssuedate >= :from) and "
                + "(:to is null or t.docIssuedate <= :to) and "
                    + "(:bookingOid is null or p.bookingAgtOID = :bookingOid) "
                    + "group by t";
        }else if(ticketingType==0){
            hql = "select t "
                                + "from Ticket as t "
                + "left join fetch t.accountingDocumentLine as al "
                + "left join fetch al.accountingDocument "    
                + "left join fetch t.purchaseAccountingDocumentLine as pa "
                + "left join fetch t.ticketRefundDetails "    
                + "inner join fetch t.pnr as p "
                + "left join fetch p.agent as a "
                + "left join fetch p.customer as c "    
                + "left join fetch p.servicingCareer as s "
                + "where (t.tktStatus <> 1) and "
                + "(:gdsId is null or p.gds.gdsId = :gdsId) and "
                + "(:tktingAgtID is null or p.ticketingAgt = :tktingAgtID) and "
                + "(:tktStatus is null or t.tktStatus = :tktStatus) and "   
                + "(:career is null or s.code = :career) and "        
                + "(:from is null or t.docIssuedate >= :from) and "
                + "(:to is null or t.docIssuedate <= :to) and "
                + "(:bookingOid is null or p.bookingAgtOID = :bookingOid) "
                    + "group by t";
        }          
                
        List<Ticket> tickets = getHibernateTemplate().findByNamedParam(hql, new String[]{"gdsId", "tktingAgtID","tktStatus","career", "from", "to","bookingOid"},
                new Object[]{gdsId, tktingAgtID,tktStatus,career, from, to,bookingOid});
        return tickets;
    }
}
