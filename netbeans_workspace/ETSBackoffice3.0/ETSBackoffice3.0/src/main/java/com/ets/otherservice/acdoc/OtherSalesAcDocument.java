//package com.ets.otherservice.acdoc;
//
//import com.ets.accountingdoc.domain.AccountingDocument;
//import java.io.Serializable;
//import java.util.LinkedHashSet;
//import java.util.Set;
//import javax.persistence.CascadeType;
//import javax.persistence.Entity;
//import javax.persistence.FetchType;
//import javax.persistence.OneToMany;
//import javax.persistence.OrderBy;
//import javax.xml.bind.annotation.XmlAccessType;
//import javax.xml.bind.annotation.XmlAccessorType;
//import javax.xml.bind.annotation.XmlElement;
//import javax.xml.bind.annotation.XmlRootElement;
//
///**
// *
// * @author Yusuf
// */
//@Entity
//@XmlRootElement
//@XmlAccessorType(XmlAccessType.NONE)
//public class OtherSalesAcDocument extends AccountingDocument implements Serializable{
//
//    @XmlElement
//    private Set<AccountingDocumentLine> accountingDocumentLines = new LinkedHashSet<>();
//
//    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @OrderBy(value = "id asc")
//    public Set<AccountingDocumentLine> getAccountingDocumentLines() {
//        return accountingDocumentLines;
//    }
//
//    public void setAccountingDocumentLines(Set<AccountingDocumentLine> accountingDocumentLines) {
//        this.accountingDocumentLines = accountingDocumentLines;
//    }
//
//
//}
