/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etsbackoffice.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.*;

/**
 *
 * @author Yusuf
 */
@Entity
@Table(name = "oacdocline")
public class OAccountingDocumentLine implements Serializable {

    private long oAcDocLineId;    
    private String serviceTitle;
    private BigDecimal serviceCost = new BigDecimal("0.00");
    private BigDecimal serviceCharge = new BigDecimal("0.00");
    private BigDecimal discount = new BigDecimal("0.00");
    private BigDecimal vatRate = new BigDecimal("0.00");        
    private int unit;
    private String remark;
    private OAccountingDocument oAccountingDocument;
    private OtherService otherService;

    public OAccountingDocumentLine() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "olineId")
    @TableGenerator(name = "olineId", table = "olineidpktb",
    pkColumnName = "olineidkey", pkColumnValue = "olineidvalue", allocationSize = 1)
    public long getoAcDocLineId() {
        return oAcDocLineId;
    }

    public void setoAcDocLineId(long oAcDocLineId) {
        this.oAcDocLineId = oAcDocLineId;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public OAccountingDocument getoAccountingDocument() {
        return oAccountingDocument;
    }

    public void setoAccountingDocument(OAccountingDocument oAccountingDocument) {
        this.oAccountingDocument = oAccountingDocument;
    }

    public BigDecimal getVatRate() {
        return vatRate;
    }

    public void setVatRate(BigDecimal vatRate) {
        this.vatRate = vatRate;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    
    @OneToOne
    public OtherService getOtherService() {
        return otherService;
    }

    public void setOtherService(OtherService otherService) {
        this.otherService = otherService;
    }

    public String getServiceTitle() {
        return serviceTitle;
    }

    public void setServiceTitle(String serviceTitle) {
        this.serviceTitle = serviceTitle;
    }

    public BigDecimal getServiceCost() {
        return serviceCost;
    }

    public void setServiceCost(BigDecimal serviceCost) {
        this.serviceCost = serviceCost;
    }

    public BigDecimal getServiceCharge() {
        return serviceCharge;
    }

    public void setServiceCharge(BigDecimal serviceCharge) {
        this.serviceCharge = serviceCharge;
    }

    @Transient
    public BigDecimal getSChargeAfterDisc() {
        BigDecimal finalSCharge = new BigDecimal("0.00");
        finalSCharge = (getServiceCharge().subtract(getDiscount())).
                multiply(new BigDecimal(String.valueOf(getUnit())));
                        
        return finalSCharge;
    }
    
    @Transient
    public BigDecimal getVat(){
        BigDecimal vat = new BigDecimal("0.00");
        vat = getSChargeAfterDisc().multiply(getVatRate()).divide(new BigDecimal("100"));        
        return vat.setScale(2);
    }

    @Transient
    public BigDecimal getNetPayable(){
     return getSChargeAfterDisc();
    }
    
    @Transient
    public BigDecimal getRevFromLine() {
        return getNetPayable().subtract(this.serviceCost);
    }     
}
