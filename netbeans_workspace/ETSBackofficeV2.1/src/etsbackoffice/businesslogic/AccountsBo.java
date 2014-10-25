/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etsbackoffice.businesslogic;

import etsbackoffice.datalogic.AccountsDao;
import etsbackoffice.domain.AcTransaction;
import etsbackoffice.domain.AccountingDocument;
import etsbackoffice.domain.Accounts;
import etsbackoffice.domain.BillingTransaction;
import etsbackoffice.domain.OAccountingDocument;
import etsbackoffice.domain.PurchaseAccountingDocument;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public class AccountsBo {

    private AccountsDao accountsDao;
    private Accounts accounts;
    private List<Accounts> clientAcStatements = new ArrayList<Accounts>();
    private BigDecimal openingBalance = new BigDecimal("0.00");    
    
    public AccountsBo() {
    }

    public List<Accounts> searchClientAcStatementByCriteria(Long contId, int docType, int transType, Date from, Date to) {
        return getAccountsDao().searchClientAcStatementByCriteria(contId, docType, transType, from, to);
    }

    public List<Accounts> searchVendorAcStatementByCriteria(Long agtId, int docType, int transType, Date from, Date to) {
        return getAccountsDao().searchVendorAcStatementByCriteria(agtId, docType, transType, from, to);
    }

    public List<Accounts> searchAcStatementByInvRef(Integer invRef) {
        return getAccountsDao().searchAcStatementByInvRef(invRef);
    }

    public List<Accounts> searchAcStatementByGDSPnr(String gdsPnr) {
        return getAccountsDao().searchAcStatementByGDSPnr(gdsPnr);
    }

    public Accounts newAccountsTransactionFromSAcDoc(AccountingDocument acDoc) {
        Accounts newTrans = new Accounts();
        
        if (acDoc.getPnr().getCustomer() != null) {
            newTrans.setContactable(acDoc.getPnr().getCustomer());
        } else if (acDoc.getPnr().getAgent() != null) {
            newTrans.setContactable(acDoc.getPnr().getAgent());
        }
        
        newTrans.setTransDate(acDoc.getIssueDate());
        newTrans.setAccountingDocument(acDoc);
        newTrans.setStmtLineType(2);
        return newTrans;
    }
    
    public Accounts newAccountsTransactionFromSOAcDoc(OAccountingDocument acDoc) {
        Accounts newTrans = new Accounts();
        
        if (acDoc.getCustomer() != null) {
            newTrans.setContactable(acDoc.getCustomer());
        } else if (acDoc.getAgent() != null) {
            newTrans.setContactable(acDoc.getAgent());
        }
        
        newTrans.setTransDate(acDoc.getIssueDate());
        newTrans.setoAccountingDocument(acDoc);
        newTrans.setStmtLineType(2);
        return newTrans;
    }

    public Accounts newAccountsTransactionFromSAcTransaction(AcTransaction acTransaction) {
        Accounts newTrans = new Accounts();

        if (acTransaction.getPnr().getCustomer() != null) {
            newTrans.setContactable(acTransaction.getPnr().getCustomer());
        } else if (acTransaction.getPnr().getAgent() != null) {
            newTrans.setContactable(acTransaction.getPnr().getAgent());
        }
        newTrans.setTransDate(acTransaction.getTransDate());
        newTrans.setAcTransaction(acTransaction);
        newTrans.setStmtLineType(2);
        //if(acTransaction.getTransType()!=11){
        return newTrans;
        //}else 
        //    return null;
    }

        public Accounts newAccountsTransactionFromSOAcTransaction(AcTransaction acTransaction) {
        Accounts newTrans = new Accounts();

        if (acTransaction.getoAccountingDocument().getCustomer() != null) {
            newTrans.setContactable(acTransaction.getoAccountingDocument().getCustomer());
        } else if (acTransaction.getoAccountingDocument().getAgent() != null) {
            newTrans.setContactable(acTransaction.getoAccountingDocument().getAgent());
        }
        newTrans.setTransDate(acTransaction.getTransDate());
        newTrans.setAcTransaction(acTransaction);
        newTrans.setStmtLineType(2);
        //if(acTransaction.getTransType()!=11){
        return newTrans;
        //}else 
        //    return null;
    }
        
    public Accounts newAccountsTransactionFromPAcDoc(PurchaseAccountingDocument pAcDoc) {
        Accounts newTrans = new Accounts();
        if (pAcDoc.getPnr().getTicketingAgt() != null) {
            newTrans.setContactable(pAcDoc.getPnr().getTicketingAgt());
        }
        newTrans.setTransDate(pAcDoc.getIssueDate());
        newTrans.setPurchaseAccountingDocument(pAcDoc);
        newTrans.setStmtLineType(1);
        return newTrans;
    }

    public Accounts newAccountsTransactionFromPAcTransaction(BillingTransaction bTransaction) {
        Accounts newTrans = new Accounts();
        if (bTransaction.getPnr().getTicketingAgt() != null) {
            newTrans.setContactable(bTransaction.getPnr().getTicketingAgt());
        }
        newTrans.setTransDate(bTransaction.getTransDate());
        newTrans.setBillingTransaction(bTransaction);
        newTrans.setStmtLineType(1);
        return newTrans;
    }

    public BigDecimal finalClientAcBalancetoDate(Date to, Long contId) {
        return getAccountsDao().finalClientAcBalancetoDate(to, contId);
    }

        public BigDecimal finalClientAcBalance(Long contId) {
        return getAccountsDao().finalClientAcBalance(contId);
    }
        
    public BigDecimal finalVendorAcBalancetoDate(Date to, Long agtId) {
        return getAccountsDao().finalVendorAcBalancetoDate(to, agtId);
    }
    //*****************************************************************

    public AccountsDao getAccountsDao() {
        return accountsDao;
    }

    public void setAccountsDao(AccountsDao accountsDao) {
        this.accountsDao = accountsDao;
    }

    public Accounts getAccounts() {
        return accounts;
    }

    public void setAccounts(Accounts accounts) {
        this.accounts = accounts;
    }

    public List<Accounts> getClientAcStatements() {
        return clientAcStatements;
    }

    public void setClientAcStatements(List<Accounts> clientAcStatements) {
        this.clientAcStatements = clientAcStatements;
    }
    //***********************************************************************    
}
