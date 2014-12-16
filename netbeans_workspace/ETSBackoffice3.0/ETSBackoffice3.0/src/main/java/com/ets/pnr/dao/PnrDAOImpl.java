package com.ets.pnr.dao;

import com.ets.GenericDAOImpl;
import com.ets.pnr.domain.Pnr;
import com.ets.pnr.domain.Ticket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.hibernate.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Yusuf
 */
@Service("pnrDAO")
@Transactional
public class PnrDAOImpl extends GenericDAOImpl<Pnr, Long> implements PnrDAO {

    @Override
    public List<Pnr> find(String gdsPnr) {
        String hql = "from Pnr as p "
                + "left join p.tickets as t "
                + "where p.gdsPNR = ? "
                + "and t.passengerNo = (select min(t.passengerNo) from Ticket t where t.pnr.id = p.id) group by p.id";

        Query query = getSession().createQuery(hql);
        query.setParameter("gdsPnr", gdsPnr);
        return query.list();
    }

    @Override
    public Pnr getByIdWithChildren(Long id) {

        String hql = "select distinct p from Pnr as p "
                + "left join fetch p.tickets as t "
                + "left join fetch p.segments "
                + "left join fetch p.remarks "
                + "left join fetch t.ticketingSalesAcDoc "
                + "left join fetch t.ticketingPurchaseAcDoc "
                + "where p.id = :id";

        Query query = getSession().createQuery(hql);
        query.setParameter("id", id);
        Pnr pnr = (Pnr) query.uniqueResult();
        return pnr;
    }

    @Override
    public List<Pnr> bookedPnrs() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Pnr> find(Date from, Date to, String[] ticketingAgtOid, String[] bookingAgtOid) {

        String bookingAgtOidQuery = "";
        String ticketingAgtOidQuery = "";

        if (bookingAgtOid != null) {
            bookingAgtOidQuery = "p.bookingAgtOid in (:bookingAgtOid) and ";
        }
        if (ticketingAgtOid != null) {
            ticketingAgtOidQuery = "p.ticketingAgtOid in (:ticketingAgtOid) and ";
        }

        String hql = "select distinct p from Pnr p "
                + "left join fetch p.tickets as t "
                + "left join fetch p.segments as s "
                + "left join fetch p.remarks as r "
                + "where "
                + bookingAgtOidQuery + ticketingAgtOidQuery
                + "(p.airCreationDate >= :from) and (p.airCreationDate <= :to)";

        Query query = getSession().createQuery(hql);
        query.setParameter("from", from);
        query.setParameter("to", to);

        if (bookingAgtOid != null) {
            query.setParameterList("bookingAgtOid", bookingAgtOid);
        }

        if (ticketingAgtOid != null) {
            query.setParameterList("ticketingAgtOid", ticketingAgtOid);
        }

        return query.list();
    }

    @Override
    public List<Pnr> searchUninvoicedPnr() {
        List<Pnr> list = new ArrayList<>();

        String hql = "select t,p from Ticket t "
                + "inner join t.pnr as p "
                + "where t.ticketingSalesAcDoc is null group by p.id";

        Query query = getSession().createQuery(hql);
        List results = query.list();

        Iterator it = results.iterator();

        while (it.hasNext()) {
            Object[] objects = (Object[]) it.next();
            Ticket leadPaxTicket = (Ticket) objects[0];
            Pnr pnr = (Pnr) objects[1];
            pnr.setTickets(null);//Avoid lazy loading here. Create a new hashset and set
            pnr.setSegments(null);
            pnr.setRemarks(null);
            Set<Ticket> tickets = new LinkedHashSet<>();
            tickets.add(leadPaxTicket);
            pnr.setTickets(tickets);
            list.add(pnr);
        }

        return list;
    }

    @Override
    public List<Pnr> searchPnrsToday(Date date) {

        List<Pnr> list = new ArrayList<>();

        String hql = "select t,p from Ticket t "
                + "inner join t.pnr as p "
                + "where t.docIssuedate = :date group by p.id";

        Query query = getSession().createQuery(hql);
        query.setDate("date", date);
        List results = query.list();

        Iterator it = results.iterator();

        while (it.hasNext()) {
            Object[] objects = (Object[]) it.next();
            Ticket leadPaxTicket = (Ticket) objects[0];
            Pnr pnr = (Pnr) objects[1];
            pnr.setTickets(null);//Avoid lazy loading here. Create a new hashset and set
            pnr.setSegments(null);
            pnr.setRemarks(null);
            Set<Ticket> tickets = new LinkedHashSet<>();
            tickets.add(leadPaxTicket);
            pnr.setTickets(tickets);
            list.add(pnr);
        }

        return list;
    }

    @Override
    public List<Pnr> searchByTktNo(String tktNo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Pnr> searchByPaxName(String surName, String foreName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
