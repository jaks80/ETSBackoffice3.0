/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etsbackoffice.datalogic;

import etsbackoffice.domain.Accounts;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public interface AccountsDao {
    public List<Accounts> searchClientAcStatementByCriteria(Long contId,int docType, int transType,Date from, Date to);
    
    public List<Accounts> searchVendorAcStatementByCriteria(Long agtId,int docType, int transType,Date from, Date to);
    
    public List<Accounts> searchAcStatementByInvRef(Integer invRef);
    
    public List<Accounts> searchAcStatementByGDSPnr(String gdsPnr);
    
    public BigDecimal finalClientAcBalancetoDate(Date to,Long contId);
    
    public BigDecimal finalClientAcBalance(Long contId);
    
    public BigDecimal finalVendorAcBalancetoDate(Date to,Long agtId);
}
