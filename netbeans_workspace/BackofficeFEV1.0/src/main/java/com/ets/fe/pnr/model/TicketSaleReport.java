package com.ets.fe.pnr.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.*;

/**
 *
 * @author Yusuf
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class TicketSaleReport implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement
    private String reportTitle;
    @XmlElement
    private String dateFrom;
    @XmlElement
    private String dateTo;
    @XmlElement
    private int tktQty;

    @XmlElement
    private String totalBaseFare;
    @XmlElement
    private String totalTax;
    @XmlElement
    private String totalFee;
    @XmlElement
    private String totalCommission;
    @XmlElement
    private String totalNetPurchaseFare;
    
    @XmlElement
    private String totalAtolChg;
    @XmlElement
    private String totalNetSellingFare;
    @XmlElement
    private String totalRevenue;
    @XmlElement
    private List<SaleReportLine> saleReportLines = new ArrayList<>();
   
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

    public int getTktQty() {
        return tktQty;
    }

    public void setTktQty(int tktQty) {
        this.tktQty = tktQty;
    }

    public List<SaleReportLine> getSaleReportLines() {
        return saleReportLines;
    }

    public void setSaleReportLines(List<SaleReportLine> saleReportLines) {
        this.saleReportLines = saleReportLines;
    }

    public String getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(String totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    @XmlAccessorType(XmlAccessType.NONE)
    @XmlRootElement
    public static class SaleReportLine {

        @XmlElement
        private Long salesInvoiceId;
        @XmlElement
        private Long purchaseInvoiceId;
        @XmlElement
        private String airLineCode;
        @XmlElement
        private String gdsPnr;

        @XmlElement
        private String client;
        @XmlElement
        private String ticketingAgent;

        @XmlElement
        private String ticketNo;
        @XmlElement
        private String docIssuedate;
        @XmlElement
        private String sellingRefference;
        @XmlElement
        private String vendorRefference;

        @XmlElement
        private String baseFare;
        @XmlElement
        private String tax;
        @XmlElement
        private String fee;
        @XmlElement
        private String commission;
        @XmlElement
        private String netPurchaseFare;
        @XmlElement
        private String grossFare;
        @XmlElement
        private String discount;
        @XmlElement
        private String atolChg;
        @XmlElement
        private String netSellingFare;
        @XmlElement
        private String tktStatus;
        @XmlElement
        private String revenue;

        public String getTicketNo() {
            return ticketNo;
        }

        public void setTicketNo(String ticketNo) {
            this.ticketNo = ticketNo;
        }

        public String getDocIssuedate() {
            return docIssuedate;
        }

        public void setDocIssuedate(String docIssuedate) {
            this.docIssuedate = docIssuedate;
        }

        public String getAirLineCode() {
            return airLineCode;
        }

        public void setAirLineCode(String airLineCode) {
            this.airLineCode = airLineCode;
        }

        public String getGdsPnr() {
            return gdsPnr;
        }

        public void setGdsPnr(String gdsPnr) {
            this.gdsPnr = gdsPnr;
        }

        public String getClient() {
            return client;
        }

        public void setClient(String client) {
            this.client = client;
        }

        public String getTicketingAgent() {
            return ticketingAgent;
        }

        public void setTicketingAgent(String ticketingAgent) {
            this.ticketingAgent = ticketingAgent;
        }

        public String getSellingRefference() {
            return sellingRefference;
        }

        public void setSellingRefference(String sellingRefference) {
            this.sellingRefference = sellingRefference;
        }

        public String getVendorRefference() {
            return vendorRefference;
        }

        public void setVendorRefference(String vendorRefference) {
            this.vendorRefference = vendorRefference;
        }

        public String getBaseFare() {
            return baseFare;
        }

        public void setBaseFare(String baseFare) {
            this.baseFare = baseFare;
        }

        public String getTax() {
            return tax;
        }

        public void setTax(String tax) {
            this.tax = tax;
        }

        public String getFee() {
            return fee;
        }

        public void setFee(String fee) {
            this.fee = fee;
        }

        public String getCommission() {
            return commission;
        }

        public void setCommission(String commission) {
            this.commission = commission;
        }

        public String getNetPurchaseFare() {
            return netPurchaseFare;
        }

        public void setNetPurchaseFare(String netPurchaseFare) {
            this.netPurchaseFare = netPurchaseFare;
        }

        public String getGrossFare() {
            return grossFare;
        }

        public void setGrossFare(String grossFare) {
            this.grossFare = grossFare;
        }

        public String getDiscount() {
            return discount;
        }

        public void setDiscount(String discount) {
            this.discount = discount;
        }

        public String getAtolChg() {
            return atolChg;
        }

        public void setAtolChg(String atolChg) {
            this.atolChg = atolChg;
        }

        public String getNetSellingFare() {
            return netSellingFare;
        }

        public void setNetSellingFare(String netSellingFare) {
            this.netSellingFare = netSellingFare;
        }

        public String getTktStatus() {
            return tktStatus;
        }

        public void setTktStatus(String tktStatus) {
            this.tktStatus = tktStatus;
        }

        public String getRevenue() {
            return revenue;
        }

        public void setRevenue(String revenue) {
            this.revenue = revenue;
        }

        public Long getSalesInvoiceId() {
            return salesInvoiceId;
        }

        public void setSalesInvoiceId(Long salesInvoiceId) {
            this.salesInvoiceId = salesInvoiceId;
        }

        public Long getPurchaseInvoiceId() {
            return purchaseInvoiceId;
        }

        public void setPurchaseInvoiceId(Long purchaseInvoiceId) {
            this.purchaseInvoiceId = purchaseInvoiceId;
        }
    }

    public String getReportTitle() {
        return reportTitle;
    }

    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }

    public String getTotalBaseFare() {
        return totalBaseFare;
    }

    public void setTotalBaseFare(String totalBaseFare) {
        this.totalBaseFare = totalBaseFare;
    }

    public String getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(String totalTax) {
        this.totalTax = totalTax;
    }

    public String getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(String totalFee) {
        this.totalFee = totalFee;
    }

    public String getTotalCommission() {
        return totalCommission;
    }

    public void setTotalCommission(String totalCommission) {
        this.totalCommission = totalCommission;
    }

    public String getTotalNetPurchaseFare() {
        return totalNetPurchaseFare;
    }

    public void setTotalNetPurchaseFare(String totalNetPurchaseFare) {
        this.totalNetPurchaseFare = totalNetPurchaseFare;
    }

    public String getTotalAtolChg() {
        return totalAtolChg;
    }

    public void setTotalAtolChg(String totalAtolChg) {
        this.totalAtolChg = totalAtolChg;
    }

    public String getTotalNetSellingFare() {
        return totalNetSellingFare;
    }

    public void setTotalNetSellingFare(String totalNetSellingFare) {
        this.totalNetSellingFare = totalNetSellingFare;
    }
    
    public void addLine(SaleReportLine line){
     this.saleReportLines.add(line);
    }
}
