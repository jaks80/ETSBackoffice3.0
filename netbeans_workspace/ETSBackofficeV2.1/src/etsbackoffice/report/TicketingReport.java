/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etsbackoffice.report;

import etsbackoffice.domain.MasterAgent;
import etsbackoffice.domain.Ticket;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public class TicketingReport {

    private MasterAgent mAgent;
    private Date from;
    private Date to;
    private String GDS;
    private String career;
    private String tktingType;
    private String tktStatus;
    private List<Ticket> tickets = new ArrayList();
    private Object[][] sSummery;
    private BigDecimal totalIssuedFare = new BigDecimal("0.00");
    private BigDecimal totalRefundFare = new BigDecimal("0.00");
    private BigDecimal totalTax = new BigDecimal("0.00");
    private BigDecimal totalTaxRefund = new BigDecimal("0.00");
    private BigDecimal totalBSPCom = new BigDecimal("0.00");
    private BigDecimal totalBSPComRefund = new BigDecimal("0.00");
    private BigDecimal saleBalance = new BigDecimal("0.00");
    private BigDecimal refundBalance = new BigDecimal("0.00");

    public TicketingReport() {
    }

    public MasterAgent getmAgent() {
        return mAgent;
    }

    public void setmAgent(MasterAgent mAgent) {
        this.mAgent = mAgent;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public String getGDS() {
        return GDS;
    }

    public void setGDS(String GDS) {
        this.GDS = GDS;
    }

    public String getCareer() {
        return career;
    }

    public void setCareer(String career) {
        this.career = career;
    }

    public String getTktingType() {
        return tktingType;
    }

    public void setTktingType(String tktingType) {
        this.tktingType = tktingType;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    public Object[][] getsSummery() {
        return sSummery;
    }

    public void setsSummery(Object[][] sSummery) {
        this.sSummery = sSummery;
        this.totalIssuedFare = (BigDecimal) sSummery[0][1];
        this.totalRefundFare = (BigDecimal) sSummery[0][2];
        this.totalTax = (BigDecimal) sSummery[1][1];
        this.totalTaxRefund = (BigDecimal) sSummery[1][2];
        this.totalBSPCom = (BigDecimal) sSummery[3][1];
        this.totalBSPComRefund = (BigDecimal) sSummery[3][2];
        this.saleBalance = (BigDecimal) sSummery[4][1];
        this.refundBalance = (BigDecimal) sSummery[4][2];
    }

    public BigDecimal getTotalIssuedFare() {
        return totalIssuedFare;
    }

    public void setTotalIssuedFare(BigDecimal totalIssuedFare) {
        this.totalIssuedFare = totalIssuedFare;
    }

    public BigDecimal getTotalRefundFare() {
        return totalRefundFare;
    }

    public void setTotalRefundFare(BigDecimal totalRefundFare) {
        this.totalRefundFare = totalRefundFare;
    }

    public BigDecimal getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(BigDecimal totalTax) {
        this.totalTax = totalTax;
    }

    public BigDecimal getTotalTaxRefund() {
        return totalTaxRefund;
    }

    public void setTotalTaxRefund(BigDecimal totalTaxRefund) {
        this.totalTaxRefund = totalTaxRefund;
    }

    public BigDecimal getTotalBSPCom() {
        return totalBSPCom;
    }

    public void setTotalBSPCom(BigDecimal totalBSPCom) {
        this.totalBSPCom = totalBSPCom;
    }

    public BigDecimal getTotalBSPComRefund() {
        return totalBSPComRefund;
    }

    public void setTotalBSPComRefund(BigDecimal totalBSPComRefund) {
        this.totalBSPComRefund = totalBSPComRefund;
    }

    public BigDecimal getSaleBalance() {
        return saleBalance;
    }

    public void setSaleBalance(BigDecimal saleBalance) {
        this.saleBalance = saleBalance;
    }

    public BigDecimal getRefundBalance() {
        return refundBalance;
    }

    public void setRefundBalance(BigDecimal refundBalance) {
        this.refundBalance = refundBalance;
    }

    public String getTktStatus() {
        return tktStatus;
    }

    public void setTktStatus(String tktStatus) {
        this.tktStatus = tktStatus;
    }
}
