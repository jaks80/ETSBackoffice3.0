package com.ets.fe.acdoc.model.report;

import com.ets.fe.Application;
import com.ets.fe.acdoc.model.RelatedDocSummery;
import com.ets.fe.acdoc.model.TicketingSalesAcDoc;
import com.ets.fe.pnr.model.Itinerary;
import com.ets.fe.pnr.model.Pnr;
import com.ets.fe.pnr.model.Ticket;
import com.ets.fe.pnr.model.report.SegmentModel;
import com.ets.fe.pnr.model.report.TicketModel;
import com.ets.fe.report.model.Letterhead;
import com.ets.fe.util.DateUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.*;

/**
 * com.ets.fe.acdoc.model.report.InvoiceModel
 *
 * @author Yusuf
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class InvoiceModel implements Serializable{

    @XmlElement
    private Letterhead letterhead =  Application.getLetterhead();
    @XmlElement
    private String title;
    @XmlElement
    private String clientName;
    @XmlElement
    private String clientAddress;
    @XmlElement
    private String refference;
    @XmlElement
    private String vendorRefference;
    @XmlElement
    private String date;
    @XmlElement
    private String terms;
    @XmlElement
    private String documentBy;

    @XmlElement
    private String pnr;
    @XmlElement
    private String vendorPnr;
    @XmlElement
    private String airLine;
    @XmlElement
    private String bookingDate;

    @XmlElement
    private String subTotal;
    @XmlElement
    private String additionalChg;
    @XmlElement
    private String otherAdjustment;
    @XmlElement
    private String invoiceAmount;
    @XmlElement
    private String payment;
    @XmlElement
    private String balance;
    @XmlElement
    private String termsAndConditions;
    @XmlElement
    private List<SegmentModel> segments= new ArrayList<>();
    @XmlElement
    private List<TicketModel> tickets = new ArrayList<>();
    @XmlElement
    private List<RelatedDocSummery> relateddocs= new ArrayList<>();

    public InvoiceModel() {

    }

    public static InvoiceModel createModel(TicketingSalesAcDoc invoice) {
        InvoiceModel model = new InvoiceModel();
        model.setTitle("Invoice");

        Pnr pnr = invoice.getPnr();
        if (pnr.getAgent() != null) {
            model.setClientName(pnr.getAgent().getName());
            model.setClientAddress(pnr.getAgent().getFullAddressCRSeperated());
        } else {
            model.setClientName(pnr.getCustomer().calculateFullName());
            model.setClientAddress(pnr.getCustomer().getFullAddressCRSeperated());
        }
        model.setRefference(invoice.getReference().toString());
        model.setDate(DateUtil.dateToString(invoice.getDocIssueDate()));
        model.setTerms(invoice.getTerms());
        model.setDocumentBy(invoice.getCreatedBy().calculateFullName());
        model.setPnr(pnr.getGdsPnr());
        model.setVendorPnr(pnr.getVendorPNR());
        model.setAirLine(pnr.getAirLineCode());
        model.setBookingDate(DateUtil.dateToString(pnr.getPnrCreationDate()));
        model.setSubTotal(invoice.calculateTicketedSubTotal().toString());
        model.setAdditionalChg(invoice.calculateAddChargesSubTotal().toString());
        model.setOtherAdjustment(invoice.calculateTotalCreditMemo().add(invoice.calculateTotalDebitMemo()).toString());
        model.setPayment(invoice.calculateTotalPayment().abs().toString());
        model.setInvoiceAmount(invoice.getDocumentedAmount().toString());
        model.setBalance(invoice.calculateDueAmount().toString());

        List<TicketingSalesAcDoc> relatedDocs = invoice.getRelatedDocuments();
        
        List<Itinerary> segments = pnr.getSegments();
        for(Itinerary it: segments){
         model.addSegmentModel(SegmentModel.segmentToModel(it));
        }

        List<Ticket> ticket_list = invoice.getTickets();
        
        for (Ticket t : ticket_list) {            
            model.addTicketModel(TicketModel.ticketToModel(t));
        }      
        
        List<RelatedDocSummery> relateddocs= new ArrayList<>();
        
        for(TicketingSalesAcDoc d: relatedDocs){
         relateddocs.add(RelatedDocSummery.documentToSummery(d));
        }
        model.setRelateddocs(relateddocs);      
        return model;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public String getRefference() {
        return refference;
    }

    public void setRefference(String refference) {
        this.refference = refference;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public String getDocumentBy() {
        return documentBy;
    }

    public void setDocumentBy(String documentBy) {
        this.documentBy = documentBy;
    }

    public String getPnr() {
        return pnr;
    }

    public void setPnr(String pnr) {
        this.pnr = pnr;
    }

    public String getVendorPnr() {
        return vendorPnr;
    }

    public void setVendorPnr(String vendorPnr) {
        this.vendorPnr = vendorPnr;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(String subTotal) {
        this.subTotal = subTotal;
    }

    public String getAdditionalChg() {
        return additionalChg;
    }

    public void setAdditionalChg(String additionalChg) {
        this.additionalChg = additionalChg;
    }

    public String getOtherAdjustment() {
        return otherAdjustment;
    }

    public void setOtherAdjustment(String otherAdjustment) {
        this.otherAdjustment = otherAdjustment;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getTermsAndConditions() {
        return termsAndConditions;
    }

    public void setTermsAndConditions(String termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
    }

    public List<SegmentModel> getSegments() {
        return segments;
    }

    public void setSegments(List<SegmentModel> segments) {
        this.segments = segments;
    }

    public List<RelatedDocSummery> getRelateddocs() {
        return relateddocs;
    }

    public void setRelateddocs(List<RelatedDocSummery> relateddocs) {
        this.relateddocs = relateddocs;
    }

    public String getAirLine() {
        return airLine;
    }

    public void setAirLine(String airLine) {
        this.airLine = airLine;
    }

    public String getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(String invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public List<TicketModel> getTickets() {
        return tickets;
    }

    public void setTickets(List<TicketModel> tickets) {
        this.tickets = tickets;
    }

    public void addTicketModel(TicketModel model) {
        this.tickets.add(model);
    }

    public void addSegmentModel(SegmentModel model){
     this.segments.add(model);
    }
    
    public String getVendorRefference() {
        return vendorRefference;
    }

    public void setVendorRefference(String vendorRefference) {
        this.vendorRefference = vendorRefference;
    }

    public Letterhead getLetterhead() {
        return letterhead;
    }

    public void setLetterhead(Letterhead letterhead) {
        this.letterhead = letterhead;
    }
}
