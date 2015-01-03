package com.ets.accountingdoc.dao;

import com.ets.GenericDAOImpl;
import com.ets.accountingdoc.domain.TicketingSalesAcDoc;
import com.ets.pnr.domain.Ticket;
import com.ets.util.Enums;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import org.hibernate.Query;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ,./
 *
 * @author Yusuf
 */
@Service("tSalesAcDocDAO")
@Transactional
public class TSalesAcDocDAOImpl extends GenericDAOImpl<TicketingSalesAcDoc, Long> implements TSalesAcDocDAO {

    @Resource(name = "accountingDocumentLineDAO")
    private AccountingDocumentLineDAO lineDAO;

    @Override
    public Long getNewAcDocRef() {
        String hql = "select max(acDoc.reference) from TicketingSalesAcDoc acDoc";
        Query query = getSession().createQuery(hql);

        Object result = DataAccessUtils.uniqueResult(query.list());
        return (result != null) ? Long.valueOf(result.toString()) : null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketingSalesAcDoc> getByPnrId(Long pnrId) {
        String hql = "select distinct a from TicketingSalesAcDoc as a "
                + "left join fetch a.accountingDocumentLines as adl "
                + "left join fetch adl.otherService "
                + "left join fetch adl.additionalCharge "
                + "left join fetch a.tickets as t "
                + "left join fetch a.relatedDocuments as a1 "
                + "where a.pnr.id = :pnrId group by a.id";

        Query query = getSession().createQuery(hql);
        query.setParameter("pnrId", pnrId);
        List<TicketingSalesAcDoc> result = query.list();
        return result;
    }

    @Override
    public List<TicketingSalesAcDoc> getByGDSPnr(String GdsPnr) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    @Transactional(readOnly = true)
    public TicketingSalesAcDoc getWithChildrenById(Long id) {

        String hql = "select distinct a from TicketingSalesAcDoc as a "
                + "left join fetch a.accountingDocumentLines as adl "
                + "left join fetch adl.otherService "
                + "left join fetch adl.additionalCharge "
                + "left join fetch a.tickets as t "
                + "left join fetch a.pnr as p "
                + "left join fetch p.segments "
                + "left join fetch p.agent "
                + "left join fetch p.customer "
                + "left join fetch a.relatedDocuments as a1 "
                + "left join fetch a1.tickets as t1 "
                + "left join fetch a1.accountingDocumentLines as adl1 "
                + "left join fetch adl1.otherService "
                + "left join fetch adl1.additionalCharge "
                + "where a.id = :id";

        Query query = getSession().createQuery(hql);
        query.setParameter("id", id);
        TicketingSalesAcDoc doc = (TicketingSalesAcDoc) query.uniqueResult();
        
        for (TicketingSalesAcDoc rd : doc.getRelatedDocuments()) {
            rd.setRelatedDocuments(null);
            rd.setAccountingDocument(null);
            Set<Ticket> tickets = rd.getTickets();
            for(Ticket t: tickets){
             t.setTicketingSalesAcDoc(null);
            }
        }
        return doc;
    }

    @Override
    public TicketingSalesAcDoc voidDocument(Long id) {
        String hql = "select distinct a from TicketingSalesAcDoc as a "
                + "left join fetch a.accountingDocumentLines as adl "
                + "left join fetch adl.otherService "
                + "left join fetch adl.additionalCharge "
                + "left join fetch a.tickets as t "
                + "where a.id = :id";

        Query query = getSession().createQuery(hql);
        query.setParameter("id", id);
        TicketingSalesAcDoc doc = (TicketingSalesAcDoc) query.uniqueResult();
        doc.setTickets(null);
        doc.setPnr(null);
        doc.setStatus(Enums.AcDocStatus.VOID);
        lineDAO.deleteBulk(doc.getAccountingDocumentLines());
        save(doc);
        TicketingSalesAcDoc void_doc = (TicketingSalesAcDoc) query.uniqueResult();
        return void_doc;
    }
}
