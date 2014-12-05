package com.ets.accountingdoc.service;

import com.ets.accountingdoc.dao.TSalesAcDocDAO;
import com.ets.accountingdoc.domain.TicketingSalesAcDoc;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 *
 * @author Yusuf
 */
@Service("tSalesAcDocService")
public class TSalesAcDocService {

    @Resource(name = "tSalesAcDocDAO")
    private TSalesAcDocDAO dao;

    public List<TicketingSalesAcDoc> findAll() {
        return dao.findAll(TicketingSalesAcDoc.class);
    }

    public TicketingSalesAcDoc saveorUpdate(TicketingSalesAcDoc ticketingSalesAcDoc) {
        dao.save(ticketingSalesAcDoc);
        return ticketingSalesAcDoc;
    }

    public void delete(TicketingSalesAcDoc ticketingSalesAcDoc) {
        dao.delete(ticketingSalesAcDoc);
    }
}
