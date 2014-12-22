package com.ets.accountingdoc.dao;

import com.ets.GenericDAO;
import com.ets.accountingdoc.domain.TicketingSalesAcDoc;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public interface TSalesAcDocDAO extends GenericDAO<TicketingSalesAcDoc, Long> {

    public Long getNewAcDocRef();

    public TicketingSalesAcDoc getWithChildrenById(Long id);

    public List<TicketingSalesAcDoc> getByPnrId(Long pnrId);

    public List<TicketingSalesAcDoc> getByGDSPnr(String GdsPnr);

    public TicketingSalesAcDoc voidDocument(Long id);
}
