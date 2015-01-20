package com.ets.fe.acdoc.model.report;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Yusuf
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class InvoiceReport implements Serializable{

    private static long serialVersionUID = 1L;
    
    @XmlElement
    private BigDecimal totalInvAmount = new BigDecimal("0.00");
    @XmlElement
    private BigDecimal totalDMAmount = new BigDecimal("0.00");
    @XmlElement
    private BigDecimal totalCMAmount = new BigDecimal("0.00");
    @XmlElement
    private BigDecimal totalDue = new BigDecimal("0.00");
    @XmlElement
    private BigDecimal totalPayment = new BigDecimal("0.00");
    @XmlElement
    private BigDecimal totalRefund = new BigDecimal("0.00");
    @XmlElement
    private List<TktingInvoiceSummery> invoices = new ArrayList<>();

    public BigDecimal getTotalInvAmount() {
        return totalInvAmount;
    }

    public void setTotalInvAmount(BigDecimal totalInvAmount) {
        this.totalInvAmount = totalInvAmount;
    }

    public BigDecimal getTotalDMAmount() {
        return totalDMAmount;
    }

    public void setTotalDMAmount(BigDecimal totalDMAmount) {
        this.totalDMAmount = totalDMAmount;
    }

    public BigDecimal getTotalCMAmount() {
        return totalCMAmount;
    }

    public void setTotalCMAmount(BigDecimal totalCMAmount) {
        this.totalCMAmount = totalCMAmount;
    }

    public BigDecimal getTotalDue() {
        return totalDue;
    }

    public void setTotalDue(BigDecimal totalDue) {
        this.totalDue = totalDue;
    }

    public BigDecimal getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(BigDecimal totalPayment) {
        this.totalPayment = totalPayment;
    }

    public BigDecimal getTotalRefund() {
        return totalRefund;
    }

    public void setTotalRefund(BigDecimal totalRefund) {
        this.totalRefund = totalRefund;
    }

    public List<TktingInvoiceSummery> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<TktingInvoiceSummery> invoices) {
        this.invoices = invoices;
    }    
    
    public void addInvoice(TktingInvoiceSummery invoice){
     this.invoices.add(invoice);
    }
}
