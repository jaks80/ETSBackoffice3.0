package com.ets.fe.accounts.model;

import com.ets.fe.Application;
import com.ets.fe.acdoc.model.report.TktingInvoiceSummery;
import com.ets.fe.acdoc_o.model.report.OtherInvoiceSummery;
import com.ets.fe.report.model.Letterhead;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.*;

/**
 *
 * @author Yusuf
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class TransactionReceipt {

    @XmlElement
    private Letterhead letterhead = Application.getLetterhead();
    @XmlElement
    private Long id;
    @XmlElement
    private String reportTitle;
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
    private String remark;
    @XmlElement
    private String paymentType;
    @XmlElement
    private String totalAmount;
    @XmlElement
    private int totalItems;
    @XmlElement
    private String paymentDate;
    @XmlElement
    private String reportDate;
    @XmlElement
    private String cashier;
    @XmlElement
    private List<TktingInvoiceSummery> lines = new ArrayList<>();
    @XmlElement
    private List<OtherInvoiceSummery> olines = new ArrayList<>();

    public TransactionReceipt() {
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public String getReportTitle() {
        return reportTitle;
    }

    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getAddressCRSeperated() {
        return addressCRSeperated;
    }

    public void setAddressCRSeperated(String addressCRSeperated) {
        this.addressCRSeperated = addressCRSeperated;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }

    public String getCashier() {
        return cashier;
    }

    public void setCashier(String cashier) {
        this.cashier = cashier;
    }

    public List<TktingInvoiceSummery> getLines() {
        return lines;
    }

    public void setLines(List<TktingInvoiceSummery> lines) {
        this.lines = lines;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Letterhead getLetterhead() {
        return letterhead;
    }

    public void setLetterhead(Letterhead letterhead) {
        this.letterhead = letterhead;
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

    public List<OtherInvoiceSummery> getOlines() {
        return olines;
    }

    public void setOlines(List<OtherInvoiceSummery> olines) {
        this.olines = olines;
    }
}
