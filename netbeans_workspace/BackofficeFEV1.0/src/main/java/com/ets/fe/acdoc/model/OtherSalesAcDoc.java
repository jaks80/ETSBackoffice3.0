package com.ets.fe.acdoc.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Yusuf
 */

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class OtherSalesAcDoc extends AccountingDocument implements Serializable{

    @XmlElement
    private Set<AccountingDocumentLine> accountingDocumentLines = new LinkedHashSet<>();
    @XmlElement
    private Set<OtherSalesAcDoc> relatedDocuments = new LinkedHashSet<>();
    @XmlElement
    private OtherSalesAcDoc parent;
    
    @Override
    public BigDecimal calculateDocumentedAmount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public BigDecimal calculateTicketedSubTotal() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

       
    public BigDecimal calculateOtherServiceSubTotal() {
        BigDecimal subtotal = new BigDecimal("0.00");
        for (AccountingDocumentLine l : getAccountingDocumentLines()) {
            if (l.getOtherService() != null) {
                subtotal = subtotal.add(l.calculateOsNetSellingTotal());
            }
        }
        return subtotal;
    }

    @Override
    public BigDecimal calculateAddChargesSubTotal() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public BigDecimal calculateTotalPayment() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public BigDecimal calculateDueAmount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Set<AccountingDocumentLine> getAccountingDocumentLines() {
        return accountingDocumentLines;
    }

    public void setAccountingDocumentLines(Set<AccountingDocumentLine> accountingDocumentLines) {
        this.accountingDocumentLines = accountingDocumentLines;
    }

    public Set<OtherSalesAcDoc> getRelatedDocuments() {
        return relatedDocuments;
    }

    public void setRelatedDocuments(Set<OtherSalesAcDoc> relatedDocuments) {
        this.relatedDocuments = relatedDocuments;
    }

    public OtherSalesAcDoc getParent() {
        return parent;
    }

    public void setParent(OtherSalesAcDoc parent) {
        this.parent = parent;
    }

}
