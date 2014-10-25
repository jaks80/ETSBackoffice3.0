/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etsbackoffice.datalogic;

import etsbackoffice.domain.Accounts;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 *
 * @author Yusuf
 */
public class HibernateAccountsDao extends HibernateDaoSupport implements AccountsDao {

    public List<Accounts> searchClientAcStatementByCriteria(Long contId, int docType, int transType, Date from, Date to) {
        String hql = "";
        List<Accounts> stmtLines = new ArrayList();

        hql = "select distinct cstmt from Accounts as cstmt "
                + "left join fetch cstmt.accountingDocument as a "                                
                + "left join fetch a.pnr as p "                
                + "left join fetch a.accountingDocumentLines as l "
                + "left join fetch l.tickets as t "
                + "left join fetch l.services as s "
                + "left join fetch cstmt.acTransaction as t "
                + "left join fetch t.pnr as p1 "
                + "left join fetch cstmt.oAccountingDocument as oa "                
                + "left join fetch oa.oAccountingDocumentLines as oal "                
                + "left join fetch t.accountingDocument as a1 "
                + "where cstmt.stmtLineType = 2 and cstmt.contactable.contactableId = :contId and "
                + "(:from is null or cstmt.transDate >= :from) and (:to is null or cstmt.transDate <= :to) "
               + "and (a.active = true or a is null) and (oa.active = true or oa is null ) and (t.active = true or t is null) "
                + "order by cstmt.acLineId ";

          
            stmtLines = getHibernateTemplate().findByNamedParam(hql, new String[]{"from", "to", "contId"},
                    new Object[]{from, to, contId});       
        return stmtLines;
    }

    public List<Accounts> searchAcStatementByInvRef(Integer invRef) {
        String hql = "";
        List<Accounts> stmtLines = new ArrayList();

        hql = "select distinct cstmt from Accounts as cstmt "
                + "left join fetch cstmt.accountingDocument as a "
                + "left join fetch a.pnr as p "
                + "left join fetch p.agent as agt "
                + "left join fetch p.customer as cust "
                + "left join fetch a.accountingDocumentLines as l "
                + "left join fetch l.tickets as t "
                + "left join fetch cstmt.acTransaction as t "
                + "left join fetch t.pnr as p1 "
                + "left join fetch p1.agent as agt1 "
                + "left join fetch p1.customer as cust1 "
                + "left join fetch t.accountingDocument as a1 "
                + "where  cstmt.stmtLineType = 2 and a.acDocRef = ? or a1.acDocRef = ?"
                + "order by cstmt.acLineId ";

        stmtLines = getHibernateTemplate().find(hql, invRef,invRef);
        return stmtLines;
    }

    public List<Accounts> searchAcStatementByGDSPnr(String gdsPnr) {
        String hql = "";
        List<Accounts> stmtLines = new ArrayList();

        hql = "select distinct cstmt from Accounts as cstmt "
                + "left join fetch cstmt.accountingDocument as a "
                + "left join fetch a.pnr as p "
                + "left join p.agent as agt "
                + "left join p.customer as cust "
                + "left join fetch a.accountingDocumentLines as l "
                + "left join fetch l.tickets as t "
                + "left join fetch cstmt.acTransaction as t "
                + "left join fetch t.pnr as p1 "
                + "left join p1.agent as agt1 "
                + "left join p1.customer as cust1 "
                + "left join fetch t.accountingDocument as a1 "
                + "where  cstmt.stmtLineType = 2 and p.gdsPNR = ? or p1.gdsPNR = ? "
                + "order by cstmt.acLineId ";

        stmtLines = getHibernateTemplate().find(hql, gdsPnr,gdsPnr);
        return stmtLines;
    }

    public BigDecimal finalClientAcBalancetoDate(Date to, Long contId) {
        BigDecimal finalBalanceToDate = new BigDecimal("0.00");
        String hql = "";

        hql = "select coalesce(sum(act.transAmount),0) - "
                + "(coalesce(sum(l.amount),0) "
                + "+ coalesce(sum(t.grossFare),0) "
                + "- coalesce(sum(t.discount),0) "
                + "+ coalesce(sum(t.atolChg),0)) "                
                + "- (coalesce(sum( (oal.serviceCharge - oal.discount)*oal.unit + (((oal.serviceCharge - oal.discount)*oal.unit)*oal.vatRate)/100),0)) "
                + "from Accounts as stmt "
                + "left join stmt.accountingDocument as a "
                + "left join a.accountingDocumentLines as l "
                + "left join stmt.oAccountingDocument as oa "  
                + "left join oa.oAccountingDocumentLines as oal "                          
                + "left join l.tickets as t "
                + "left join stmt.acTransaction as act "
                + "where stmt.stmtLineType = 2 and stmt.contactable.contactableId = :contId "
                + "and (a.active = true or a is null) and (oa.active = true or oa is null ) and (act.active = true or act is null) "
                + "and stmt.transDate < :to ";        
        
            finalBalanceToDate = (BigDecimal) DataAccessUtils.uniqueResult(getHibernateTemplate().findByNamedParam(hql, new String[]{"contId", "to"},
                    new Object[]{contId, to}));      
            finalBalanceToDate = finalBalanceToDate.setScale(2);
            System.out.println("Balance to date: "+finalBalanceToDate);
        return finalBalanceToDate;
    }

    public BigDecimal finalClientAcBalance(Long contId) {
        BigDecimal finalBalanceToDate = new BigDecimal("0.00");
        String hql = "";

        hql = "select coalesce(sum(act.transAmount),0) - "
                + "(coalesce(sum(l.amount),0) "
                + "+ coalesce(sum(t.grossFare),0) "
                + "- coalesce(sum(t.discount),0) "
                + "+ coalesce(sum(t.atolChg),0)) "
                + "- (coalesce(sum( (oal.serviceCharge - oal.discount)*oal.unit + (((oal.serviceCharge - oal.discount)*oal.unit)*oal.vatRate)/100),0)) "
                + "from Accounts as stmt "
                + "left join stmt.accountingDocument as a "                
                + "left join a.accountingDocumentLines as l "
                + "left join stmt.oAccountingDocument as oa "  
                + "left join oa.oAccountingDocumentLines as oal "  
                + "left join l.tickets as t "
                + "left join stmt.acTransaction as act "                
                + "where stmt.stmtLineType = 2 and stmt.contactable.contactableId = :contId "
                + "and (a.active = true or a is null) and (oa.active = true or oa is null ) and (act.active = true or act is null) ";

        finalBalanceToDate = (BigDecimal) DataAccessUtils.uniqueResult(getHibernateTemplate().findByNamedParam(hql, new String[]{"contId"},
                new Object[]{contId}));
        finalBalanceToDate = finalBalanceToDate.setScale(2);
        return finalBalanceToDate;
    }
        
    public List<Accounts> searchVendorAcStatementByCriteria(Long agtId,int docType, int transType, Date from, Date to) {
        String hql = "";
        List<Accounts> stmtLines = new ArrayList();

        hql = "select distinct cstmt from Accounts as cstmt "
                + "left join fetch cstmt.purchaseAccountingDocument as pa "
                + "left join fetch pa.pnr as p "
                + "left join fetch p.ticketingAgt as agt "
                + "left join fetch pa.purchaseAcDocLines as pl "
                + "left join fetch pl.tickets as t "
                + "left join fetch cstmt.billingTransaction as bt "
                + "left join fetch bt.pnr as p1 "
                + "left join fetch p1.ticketingAgt as agt1 "
                + "left join fetch bt.purchaseAccountingDocument as pa1 "
                + "where (cstmt.stmtLineType = 1 and agt.contactableId = :agtId or agt1.contactableId = :agtId) and "
                + "(:from is null or cstmt.transDate >= :from) and (:to is null or cstmt.transDate <= :to) "
                + "order by cstmt.acLineId ";        

            stmtLines = getHibernateTemplate().findByNamedParam(hql, new String[]{"from", "to", "agtId"},
                    new Object[]{from, to, agtId});
        
        return stmtLines;
    }

    public BigDecimal finalVendorAcBalancetoDate(Date to, Long agtId) {
        BigDecimal finalBalanceToDate = new BigDecimal("0.00");
        String hql = "";

        hql = "select coalesce(sum(pl.amount),0) "
                + "+ coalesce(sum(t.baseFare),0) "
                + "+ coalesce(sum(t.tax),0) "
                + "- coalesce(sum(t.bspCom),0) "
                + "+ coalesce(sum(t.atolVendor),0) "
                + "- coalesce(sum(bt.transAmount),0) "
                + "from Accounts as stmt "
                + "left join stmt.purchaseAccountingDocument as pa "
                + "left join pa.pnr as p "
                + "left join p.ticketingAgt as agt "
                + "left join pa.purchaseAcDocLines as pl "
                + "left join pl.tickets as t "
                + "left join stmt.billingTransaction as bt "
                + "left join bt.pnr as p1 "
                + "left join p1.ticketingAgt as agt1 "
                + "where (agt.contactableId = :agtId or agt1.contactableId = :agtId) and "
                + "stmt.transDate < :to ";
               
            finalBalanceToDate = (BigDecimal) DataAccessUtils.uniqueResult(getHibernateTemplate().findByNamedParam(hql, new String[]{"agtId", "to"},
                    new Object[]{agtId, to}));
       
        return finalBalanceToDate;
    }
}
