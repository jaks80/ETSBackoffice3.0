package etsbackoffice.businesslogic;


import etsbackoffice.datalogic.AcTransactionDao;
import etsbackoffice.domain.AcTransaction;
import etsbackoffice.domain.BatchBillingTransaction;
import etsbackoffice.domain.BillingTransaction;
import etsbackoffice.domain.BatchTransaction;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;




/**
 *
 * @author Yusuf
 */
public class AcTransactionBo {

    private AcTransactionDao acTransDao;
    private AcTransaction acTrans;
    private BillingTransaction BTrans;

    public AcTransactionBo(){
    }

    //ACTransaction --------------------------------------
    public void saveACTransaction(){
        acTransDao.saveTransaction(this.acTrans);
    }

    public void saveBulkACTransaction(List<AcTransaction> acts){
        acTransDao.saveBulkTransaction(acts);
    }

    public void saveBTransaction(BatchTransaction consignment) {
        acTransDao.saveBTransaction(consignment);
    }

    public void saveBBillingTransaction(BatchBillingTransaction consignment) {
        acTransDao.saveBBillingTransaction(consignment);
    }

    public void deleteAcTransaction(AcTransaction t){
    acTransDao.deleteAcTransaction(t);
    }

    public void voidAcTransaction(AcTransaction t){
        t.setActive(false);
        acTransDao.voidAcTransaction(t);
    }

    public void voidBillingTransaction(BillingTransaction t) {
        t.setActive(false);
        acTransDao.voidBillingTransaction(t);
    }
        
    public List<AcTransaction> loadTransactions(long pnrId){
    return acTransDao.loadTransactions(pnrId);
    }

    public String generateBatchTransRef(String agtName) {
        agtName = agtName.replace(" ", "");
        String prefix = agtName.substring(0, 3);
        DateFormat df = new DateFormat();
        String currentDate = df.getCurrentDateTime();
        return prefix.concat(currentDate).concat("BC");
    }

    public String generateBatchBillingRef(String agtName) {
        agtName = agtName.replace(" ", "");
        String prefix = agtName.substring(0, 3);
        DateFormat df = new DateFormat();
        String currentDate = df.getCurrentDateTime();
        return prefix.concat(currentDate).concat("BP");
    }

    public String generateBatchBillingRefBsp() {
        String prefix = "BSP";
        DateFormat df = new DateFormat();
        String currentDate = df.getCurrentDateTime();
        return prefix.concat(currentDate).concat("BP");
    }

    public AcTransaction getNewTransaction(){
        try {
            this.acTrans.getClass().newInstance();
        } catch (InstantiationException ex) {
            Logger.getLogger(AcTransactionBo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(AcTransactionBo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return this.acTrans;
    }

    public List<AcTransaction> loadTransactionsByCriteria(Date from, Date to, Integer tType,
            Long userId, Long contactableId, Integer contType,Integer acDocId,Integer oAcDocId) {
        return acTransDao.loadTransactionsByCriteria(from, to, tType, userId, contactableId, contType,acDocId,oAcDocId);
    }
//End of ACTransaction

    public Integer getTransTypeInteger(String type) {

        return Enums.TransType.valueOf(type).getId();
    }

    public List<BatchTransaction> findBatchCollections(Long contactableId, Date from, Date to){
        return acTransDao.findBatchCollectionsByCriteria(contactableId, from, to);
    }
    
    public List<BatchBillingTransaction> findBatchBPThirdParty(Long contactableId, Date from, Date to){
        return acTransDao.findBPBatchThirdPartyByCriteria(contactableId, from, to);
    }
//BillingTransaction---------------------------------------
    public void saveBTransaction() {
        acTransDao.saveBTransaction(this.BTrans);
    }

    public void saveBulkBTransaction(List<BillingTransaction> acts) {
        acTransDao.saveBulkBTransaction(acts);
    }
//End of BillingTransaction---------------------------------------
    public AcTransactionDao getAcTransDao() {
        return acTransDao;
    }

    public void setAcTransDao(AcTransactionDao acTransDao) {
        this.acTransDao = acTransDao;
    }

    public AcTransaction getAcTrans() {
        return acTrans;
    }

    public void setAcTrans(AcTransaction acTrans) {
        this.acTrans = acTrans;
    }

    public void setBTrans(BillingTransaction BTrans) {
        this.BTrans = BTrans;
    }
}
