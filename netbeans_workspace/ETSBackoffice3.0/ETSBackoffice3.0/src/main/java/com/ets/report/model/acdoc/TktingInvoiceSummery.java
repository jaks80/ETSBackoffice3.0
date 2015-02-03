package com.ets.report.model.acdoc;

import com.ets.Application;
import com.ets.accountingdoc.domain.AccountingDocument;
import com.ets.accountingdoc.domain.TicketingPurchaseAcDoc;
import com.ets.accountingdoc.domain.TicketingSalesAcDoc;
import com.ets.accountingdoc.service.AcDocUtil;
import com.ets.util.DateUtil;
import com.ets.util.Enums;
import com.ets.util.PnrUtil;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;
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
    private Long id;
    @XmlElement
    private Long pnr_id;
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

    public static InvoiceReport serializeToSalesSummery(List<TicketingSalesAcDoc> invoices) {
        BigDecimal totalInvAmount = new BigDecimal("0.00");
        BigDecimal totalDMAmount = new BigDecimal("0.00");
        BigDecimal totalCMAmount = new BigDecimal("0.00");
        BigDecimal totalDue = new BigDecimal("0.00");
        BigDecimal totalPayment = new BigDecimal("0.00");
        BigDecimal totalRefund = new BigDecimal("0.00");

        InvoiceReport report = new InvoiceReport();
        for (TicketingSalesAcDoc invoice : invoices) {

            if (invoice.getRelatedDocuments() != null) {
                Set<TicketingSalesAcDoc> relatedDocs = AcDocUtil.filterVoidRelatedDocuments(invoice.getRelatedDocuments());
                invoice.setRelatedDocuments(relatedDocs);
            }

            TktingInvoiceSummery invSummery = new TktingInvoiceSummery();

            invSummery.setId(invoice.getId());
            invSummery.setDocIssueDate(DateUtil.dateToString(invoice.getDocIssueDate()));
            invSummery.setGdsPnr(invoice.getPnr().getGdsPnr());
            invSummery.setNoOfPax(invoice.getPnr().getNoOfPax());
            invSummery.setPnr_id(invoice.getPnr().getId());
            invSummery.setReference(invoice.getReference());
            invSummery.setStatus(invoice.getStatus());
            invSummery.setType(invoice.getType());
            invSummery.setOutBoundDetails(PnrUtil.getOutBoundFlightSummery(invoice.getPnr().getSegments()));
            invSummery.setDocumentedAmount(invoice.getDocumentedAmount());
            invSummery.setOtherAmount(invoice.calculateTotalDebitMemo().add(invoice.calculateTotalCreditMemo()));
            invSummery.setPayment(invoice.calculateTotalPayment().add(invoice.calculateTotalRefund()));
            invSummery.setDue(invoice.calculateDueAmount());

            totalInvAmount = totalInvAmount.add(invoice.getDocumentedAmount());
            totalDMAmount = totalDMAmount.add(invoice.calculateTotalDebitMemo());
            totalCMAmount = totalCMAmount.add(invoice.calculateTotalCreditMemo());
            totalPayment = totalPayment.add(invoice.calculateTotalPayment());
            totalRefund = totalRefund.add(invoice.calculateTotalRefund());
            totalDue = totalDue.add(invoice.calculateDueAmount());

            report.addInvoice(invSummery);
        }
        String currency = Application.currency();
        report.setTotalInvAmount(currency + totalInvAmount.toString());
        report.setTotalCMAmount(currency + totalCMAmount.toString());
        report.setTotalDMAmount(currency + totalDMAmount.toString());
        report.setTotalDue(currency + totalDue.toString());
        report.setTotalPayment(currency + totalPayment.toString());
        report.setTotalRefund(currency + totalRefund.toString());

        return report;
    }

    public static InvoiceReport serializeToPurchaseSummery(List<TicketingPurchaseAcDoc> invoices) {
        BigDecimal totalInvAmount = new BigDecimal("0.00");
        BigDecimal totalDMAmount = new BigDecimal("0.00");
        BigDecimal totalCMAmount = new BigDecimal("0.00");
        BigDecimal totalDue = new BigDecimal("0.00");
        BigDecimal totalPayment = new BigDecimal("0.00");
        BigDecimal totalRefund = new BigDecimal("0.00");

        InvoiceReport report = new InvoiceReport();
        for (TicketingPurchaseAcDoc invoice : invoices) {

            if (invoice.getRelatedDocuments() != null) {
                Set<TicketingPurchaseAcDoc> relatedDocs = AcDocUtil.filterPVoidRelatedDocuments(invoice.getRelatedDocuments());
                invoice.setRelatedDocuments(relatedDocs);
            }

            TktingInvoiceSummery invSummery = new TktingInvoiceSummery();

            invSummery.setId(invoice.getId());
            invSummery.setDocIssueDate(DateUtil.dateToString(invoice.getDocIssueDate()));
            invSummery.setGdsPnr(invoice.getPnr().getGdsPnr());
            invSummery.setNoOfPax(invoice.getPnr().getNoOfPax());
            invSummery.setPnr_id(invoice.getPnr().getId());
            invSummery.setReference(invoice.getReference());
            invSummery.setStatus(invoice.getStatus());
            invSummery.setType(invoice.getType());
            invSummery.setOutBoundDetails(PnrUtil.getOutBoundFlightSummery(invoice.getPnr().getSegments()));
            invSummery.setDocumentedAmount(invoice.getDocumentedAmount());
            invSummery.setOtherAmount(invoice.calculateTotalDebitMemo().add(invoice.calculateTotalCreditMemo()));
            invSummery.setPayment(invoice.calculateTotalPayment().add(invoice.calculateTotalRefund()));
            invSummery.setDue(invoice.calculateDueAmount());

            totalInvAmount = totalInvAmount.add(invoice.getDocumentedAmount());
            totalDMAmount = totalDMAmount.add(invoice.calculateTotalDebitMemo());
            totalCMAmount = totalCMAmount.add(invoice.calculateTotalCreditMemo());
            totalPayment = totalPayment.add(invoice.calculateTotalPayment());
            totalRefund = totalRefund.add(invoice.calculateTotalRefund());
            totalDue = totalDue.add(invoice.calculateDueAmount());

            report.addInvoice(invSummery);
        }
        String currency = Application.currency();
        report.setTotalInvAmount(currency + totalInvAmount.toString());
        report.setTotalCMAmount(currency + totalCMAmount.toString());
        report.setTotalDMAmount(currency + totalDMAmount.toString());
        report.setTotalDue(currency + totalDue.toString());
        report.setTotalPayment(currency + totalPayment.toString());
        report.setTotalRefund(currency + totalRefund.toString());

        return report;
    }

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPnr_id() {
        return pnr_id;
    }

    public void setPnr_id(Long pnr_id) {
        this.pnr_id = pnr_id;
    }

}
