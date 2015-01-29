package com.ets.fe.acdoc.model.report;

import com.ets.fe.util.Enums;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
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
public class TktingInvoiceSummery implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement
    private Long reference;
    @XmlElement
    private Enums.AcDocType type;
    @XmlElement
    private Enums.AcDocStatus status;
    @XmlElement
    private BigDecimal documentedAmount;
    @XmlElement
    private BigDecimal payment;
    @XmlElement
    private BigDecimal otherAmount;
    @XmlElement
    private BigDecimal due;
    @XmlElement
    private String docIssueDate;
    @XmlElement
    private String gdsPnr;
    @XmlElement
    private Integer noOfPax;
    @XmlElement
    private String outBoundDetails;
    @XmlElement
    private String leadPsgr;

    public Long getReference() {
        return reference;
    }

    public void setReference(Long reference) {
        this.reference = reference;
    }

    public Enums.AcDocType getType() {
        return type;
    }

    public void setType(Enums.AcDocType type) {
        this.type = type;
    }

    public Enums.AcDocStatus getStatus() {
        return status;
    }

    public void setStatus(Enums.AcDocStatus status) {
        this.status = status;
    }

    public BigDecimal getDocumentedAmount() {
        return documentedAmount;
    }

    public void setDocumentedAmount(BigDecimal documentedAmount) {
        this.documentedAmount = documentedAmount;
    }

    public BigDecimal getPayment() {
        return payment;
    }

    public void setPayment(BigDecimal payment) {
        this.payment = payment;
    }

    public BigDecimal getOtherAmount() {
        return otherAmount;
    }

    public void setOtherAmount(BigDecimal otherAmount) {
        this.otherAmount = otherAmount;
    }

    public BigDecimal getDue() {
        return due;
    }

    public void setDue(BigDecimal due) {
        this.due = due;
    }

    public String getDocIssueDate() {
        return docIssueDate;
    }

    public void setDocIssueDate(String docIssueDate) {
        this.docIssueDate = docIssueDate;
    }

    public String getGdsPnr() {
        return gdsPnr;
    }

    public void setGdsPnr(String gdsPnr) {
        this.gdsPnr = gdsPnr;
    }

    public Integer getNoOfPax() {
        return noOfPax;
    }

    public void setNoOfPax(Integer noOfPax) {
        this.noOfPax = noOfPax;
    }

    public String getOutBoundDetails() {
        return outBoundDetails;
    }

    public void setOutBoundDetails(String outBoundDetails) {
        this.outBoundDetails = outBoundDetails;
    }

    public String getLeadPsgr() {
        return leadPsgr;
    }

    public void setLeadPsgr(String leadPsgr) {
        this.leadPsgr = leadPsgr;
    }

}
