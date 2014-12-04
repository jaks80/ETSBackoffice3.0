//package com.ets.otherservice.acdoc;
//
//import com.ets.PersistentObject;
//import com.ets.otherservice.domain.AdditionalCharge;
//import com.ets.otherservice.domain.OtherService;
//import java.io.Serializable;
//import java.math.BigDecimal;
//import javax.persistence.Entity;
//import javax.persistence.FetchType;
//import javax.persistence.JoinColumn;
//import javax.persistence.ManyToOne;
//import javax.xml.bind.annotation.XmlAccessType;
//import javax.xml.bind.annotation.XmlAccessorType;
//import javax.xml.bind.annotation.XmlElement;
//import javax.xml.bind.annotation.XmlRootElement;
//
///**
// *
// * @author Yus nm,; uf
// */
//@Entity
//@XmlRootElement
//@XmlAccessorType(XmlAccessType.NONE)
//public class AccountingDocumentLine extends PersistentObject implements Serializable {
//
//    @XmlElement
//    private String title;   
//    @XmlElement
//    private String remark;
//    @XmlElement
//    private BigDecimal amount = new BigDecimal("0.00");
//    @XmlElement
//    private BigDecimal discount = new BigDecimal("0.00");     
//    @XmlElement
//    private int qty;
//    @XmlElement
//    private OtherService otherService;    
//    @XmlElement
//    private OtherSalesAcDocument otherSalesAcDocument;
//    
//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public String getRemark() {
//        return remark;
//    }
//
//    public void setRemark(String remark) {
//        this.remark = remark;
//    }
//
//    public BigDecimal getAmount() {
//        return amount;
//    }
//
//    public void setAmount(BigDecimal amount) {
//        this.amount = amount;
//    }
//
//    public BigDecimal getDiscount() {
//        return discount;
//    }
//
//    public void setDiscount(BigDecimal discount) {
//        this.discount = discount;
//    }
//
//    public int getQty() {
//        return qty;
//    }
//
//    public void setQty(int qty) {
//        this.qty = qty;
//    }
//
//    public OtherService getOtherService() {
//        return otherService;
//    }
//
//    public void setOtherService(OtherService otherService) {
//        this.otherService = otherService;
//    } 
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    public OtherSalesAcDocument getOtherSalesAcDocument() {
//        return otherSalesAcDocument;
//    }
//
//    public void setOtherSalesAcDocument(OtherSalesAcDocument otherSalesAcDocument) {
//        this.otherSalesAcDocument = otherSalesAcDocument;
//    }
//}
