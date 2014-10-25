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
@Table(name = "otherservice")
public class OtherService implements Serializable {

    private long serviceId;
    private int serviceType;//1.Normal service 2. Additional Service Exp. Postage, AdminChg, Card Handling fee ets.
    private String serviceTitle;
    private BigDecimal serviceCost = new BigDecimal("0.00");
    private BigDecimal serviceCharge = new BigDecimal("0.00");
    private BigDecimal vatRate;    
    private String remark;
    private int calculationtype; //1.Fixed, 2.Percentage
    private OAccountingDocument oAccountingDocument;
    private Vendor vendor;
    
    public OtherService(){
    
    }

    @Id    
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "serid")
    @TableGenerator(name = "serid", table = "seridpktb",
    pkColumnName = "seridkey", pkColumnValue = "seridvalue", allocationSize = 1)
    public long getServiceId() {
        return serviceId;
    }

    public void setServiceId(long serviceId) {
        this.serviceId = serviceId;
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
    public BigDecimal getVatRate() {
        return vatRate;
    }

    public void setVatRate(BigDecimal vatRate) {
        this.vatRate = vatRate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @ManyToOne
    public OAccountingDocument getOAccountingDocument() {
        return oAccountingDocument;
    }

    public void setOAccountingDocument(OAccountingDocument oAccountingDocument) {
        this.oAccountingDocument = oAccountingDocument;
    }

    public int getServiceType() {
        return serviceType;
    }

    public void setServiceType(int serviceType) {
        this.serviceType = serviceType;
    }

    public int getCalculationtype() {
        return calculationtype;
    }

    public void setCalculationtype(int calculationtype) {
        this.calculationtype = calculationtype;
    }

    @ManyToOne    
    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }
        
    public BigDecimal getFinalServiceCharge(BigDecimal amount) {
        BigDecimal finalCharge = new BigDecimal("0.00");
        
        if (this.calculationtype == 1) {
            finalCharge = getServiceCharge();
        } else if(this.calculationtype == 2) {
            finalCharge = amount.multiply(this.getServiceCharge()).divide(new BigDecimal("100"));
            finalCharge = finalCharge.setScale(2, BigDecimal.ROUND_DOWN);
        }
        
        return finalCharge;
    }
    
    
    public BigDecimal getFinalServiceCost(BigDecimal amount) {
        BigDecimal finalCost = new BigDecimal("0.00");
        
        if (this.calculationtype == 1) {
            finalCost = getServiceCost();
        } else if(this.calculationtype == 2) {
            finalCost = amount.multiply(this.getServiceCost()).divide(new BigDecimal("100"));
            finalCost = finalCost.setScale(2, BigDecimal.ROUND_DOWN);
        }
        
        return finalCost;
    }
}
