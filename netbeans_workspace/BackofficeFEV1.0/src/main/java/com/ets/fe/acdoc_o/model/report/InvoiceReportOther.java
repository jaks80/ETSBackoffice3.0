package com.ets.fe.acdoc_o.model.report;

import com.ets.fe.Application;
import com.ets.fe.report.model.Letterhead;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.*;

/**
 *
 * @author Yusuf
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class InvoiceReportOther implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement
    private Letterhead letterhead = Application.getLetterhead();
    @XmlElement
    private String reportDate;
    @XmlElement
    private String title;
    @XmlElement
    private String totalInvoice;
    @XmlElement
    private String totalInvAmount = "0.00";
    @XmlElement
    private String totalDMAmount = "0.00";
    @XmlElement
    private String totalCMAmount = "0.00";
    @XmlElement
    private String totalDue = "0.00";
    @XmlElement
    private String totalPayment = "0.00";
    @XmlElement
    private String totalRefund = "0.00";
    @XmlElement
    private List<OtherInvoiceSummery> invoices = new ArrayList<>();

    @XmlElement
    private String clientName;
    @XmlElement
    private String addressCRSeperated;
    @XmlElement
    private String telNo;
    @XmlElement
    private String mobile;
    @XmlElement
    private String email;
    @XmlElement
    private String fax;
    
    @XmlElement
    private String dateFrom;
    @XmlElement
    private String dateTo;

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

    public List<OtherInvoiceSummery> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<OtherInvoiceSummery> invoices) {
        this.invoices = invoices;
    }

    public void addInvoice(OtherInvoiceSummery invoice) {
        this.getInvoices().add(invoice);
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getTelNo() {
        return telNo;
    }

    public void setTelNo(String telNo) {
        this.telNo = telNo;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getAddressCRSeperated() {
        return addressCRSeperated;
    }

    public void setAddressCRSeperated(String addressCRSeperated) {
        this.addressCRSeperated = addressCRSeperated;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    public String getDateTo() {
        return dateTo;
    }

    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTotalInvoice() {
        return totalInvoice;
    }

    public void setTotalInvoice(String totalInvoice) {
        this.totalInvoice = totalInvoice;
    }

    public Letterhead getLetterhead() {
        return letterhead;
    }

    public void setLetterhead(Letterhead letterhead) {
        this.letterhead = letterhead;
    }

    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }
}
