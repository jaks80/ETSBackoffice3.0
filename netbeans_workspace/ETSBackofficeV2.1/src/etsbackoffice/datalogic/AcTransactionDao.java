package etsbackoffice.datalogic;

import etsbackoffice.domain.*;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public interface AcTransactionDao {

    public void saveTransaction(AcTransaction t);

    public void saveBTransaction(BatchTransaction consignment);

    public void saveBBillingTransaction(BatchBillingTransaction consignment);

    public void saveBulkTransaction(List<AcTransaction> acts);

    public void saveBTransaction(BillingTransaction t);

    public void deleteAcTransaction(AcTransaction t);
    
    public void voidAcTransaction(AcTransaction t);
    
    public void voidBillingTransaction(BillingTransaction bt);

    public void saveBulkBTransaction(List<BillingTransaction> acts);

    public List<AcTransaction> loadTransactions(long pnrId);

    public List<AcTransaction> loadTransactionsByCriteria(Date from, Date to, Integer tType,
            Long userId, Long contactableId, Integer contType,Integer acDocId,Integer oAcDocId);

    public List<BatchTransaction> findBatchCollectionsByCriteria(Long contactableId, Date from, Date to);
    
    public List<BatchBillingTransaction> findBPBatchThirdPartyByCriteria(Long contactableId, Date from, Date to);
}
