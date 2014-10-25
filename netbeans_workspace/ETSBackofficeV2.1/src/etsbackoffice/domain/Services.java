/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etsbackoffice.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.*;

/**
 *
 * @author Yusuf
 */
@Entity
@Table(name = "services")
public class Services  implements Serializable {
    private long serviceId;
    private String serviceTitle;
    
    private BigDecimal serviceCost = new BigDecimal("0.00");
    private BigDecimal serviceCharge = new BigDecimal("0.00");
    private BigDecimal discount = new BigDecimal("0.00");
    private BigDecimal vatRate = new BigDecimal("0.00");        
    private int unit;    
    private Long originalItemId;//Refund purpose only;
    private int serviceType;//1.Normal service 2. Additional Service Exp. Postage, AdminChg, Card Handling fee ets.
    private Set<AccountingDocumentLine> accountingDocumentLine = new LinkedHashSet<AccountingDocumentLine>();
    private PNR pnr;
    private OtherService otherService;
    private Integer version;
        
    public Services(){
    
    }

    @Id
    @Column(name = "SERVICEID")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "servid")
    @TableGenerator(name = "servid", table = "servidpktb",
    pkColumnName = "servidkey", pkColumnValue = "servidvalue", allocationSize = 1)
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
        return serviceCost.multiply(new BigDecimal(unit));
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

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
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

    @ManyToMany//(mappedBy = "services", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "join_service_acdocline",
    joinColumns = {
        @JoinColumn(name = "SERV_ID", unique = true)},
    inverseJoinColumns = {
        @JoinColumn(name = "ACDOCLINE_ID")})
    public Set<AccountingDocumentLine> getAccountingDocumentLine() {
        return accountingDocumentLine;
    }

    public void setAccountingDocumentLine(Set<AccountingDocumentLine> accountingDocumentLine) {
        this.accountingDocumentLine = accountingDocumentLine;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PNRID_FK")
    public PNR getPnr() {
        return pnr;
    }

    public void setPnr(PNR pnr) {
        this.pnr = pnr;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public OtherService getOtherService() {
        return otherService;
    }

    public void setOtherService(OtherService otherService) {
        this.otherService = otherService;
    }

    @Version
    @Column(name = "OPTLOCK")
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
    
    @Transient
    public BigDecimal getNetPayable(){
     return (this.getServiceCharge().add(getDiscount())).multiply(new BigDecimal(String.valueOf(getUnit())));
    }
    
    @Transient
    public BigDecimal getRevenue() {
        return this.getNetPayable().subtract(this.getServiceCost());
    }

    public int getServiceType() {
        return serviceType;
    }

    public void setServiceType(int serviceType) {
        this.serviceType = serviceType;
    }

    public Long getOriginalItemId() {
        return originalItemId;
    }

    public void setOriginalItemId(Long originalItemId) {
        this.originalItemId = originalItemId;
    }
}
