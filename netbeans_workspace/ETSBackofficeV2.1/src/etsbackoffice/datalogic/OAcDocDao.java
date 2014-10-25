/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package etsbackoffice.datalogic;

import etsbackoffice.domain.Agent;
import etsbackoffice.domain.Customer;
import etsbackoffice.domain.OAccountingDocument;
import etsbackoffice.domain.OAccountingDocumentLine;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public interface OAcDocDao {

    public void saveOrUpdateAcDoc(OAccountingDocument oAcDoc);
    
    public void deleteOAcDoc(OAccountingDocument oAcDoc);

    public void deleteOAcDocLine(OAccountingDocumentLine l);
    
    public int getMaxAcDocRef();

    public List<OAccountingDocument> findAcDocByRef(int acDocRef);

    public List<OAccountingDocument> findAcDocByCustomerName(String surName,String foreName);

    public OAccountingDocument findCompleteAcDocById(long acDocId);

    public OAccountingDocument findCompleteAcDocByRef(int refNo);

    public List<OAccountingDocument> invHistoryByCriteria(int contType,Long contId,
            Integer docType, Date from, Date to);

    public List<OAccountingDocument> invOutstandingByCriteria(int contType,Long contId,
            Date from, Date to);

    public List<Customer> findOutstandingCustomers();

    public List<Agent> findOutstandingAgents();
}
