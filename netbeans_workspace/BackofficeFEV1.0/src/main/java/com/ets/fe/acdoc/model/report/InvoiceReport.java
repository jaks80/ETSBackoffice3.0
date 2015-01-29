package com.ets.fe.acdoc.model.report;

import java.io.Serializable;
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
    private String totalInvAmount = new String("0.00");
    @XmlElement
    private String totalDMAmount = new String("0.00");
    @XmlElement
    private String totalCMAmount = new String("0.00");
    @XmlElement
    private String totalDue = new String("0.00");
    @XmlElement
    private String totalPayment = new String("0.00");
    @XmlElement
    private String totalRefund = new String("0.00");
    @XmlElement
    private List<TktingInvoiceSummery> invoices = new ArrayList<>();

    public String getTotalInvAmount() {
        return totalInvAmount;
    }

    public void setTotalInvAmount(String totalInvAmount) {
        this.totalInvAmount = totalInvAmount;
    }

    public String getTotalDMAmount() {
        return totalDMAmount;
    }

    public void setTotalDMAmount(String totalDMAmount) {
        this.totalDMAmount = totalDMAmount;
    }

    public String getTotalCMAmount() {
        return totalCMAmount;
    }

    public void setTotalCMAmount(String totalCMAmount) {
        this.totalCMAmount = totalCMAmount;
    }

    public String getTotalDue() {
        return totalDue;
    }

    public void setTotalDue(String totalDue) {
        this.totalDue = totalDue;
    }

    public String getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(String totalPayment) {
        this.totalPayment = totalPayment;
    }

    public String getTotalRefund() {
        return totalRefund;
    }

    public void setTotalRefund(String totalRefund) {
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
