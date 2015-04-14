package com.ets.fe.pnr.model;

import com.ets.fe.pnr.model.Ticket;
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
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class GDSSaleReport {

    @XmlElement
    private List<Ticket> list = new ArrayList<>();
    @XmlElement
    private BigDecimal totalIssuedFare;
    @XmlElement
    private BigDecimal totalRefundFare;
    @XmlElement
    private BigDecimal totalTax;
    @XmlElement
    private BigDecimal totalTaxRefund;
    @XmlElement
    private BigDecimal totalBSPCom;
    @XmlElement
    private BigDecimal totalBSPComRefund;
    @XmlElement
    private BigDecimal saleBalance;
    @XmlElement
    private BigDecimal refundBalance;
    @XmlElement
    private BigDecimal comBalance;
        @XmlElement
    private int totalBooking = 0;
    @XmlElement
    private int totalIssue = 0;
    @XmlElement
    private int totalReIssue = 0;
    @XmlElement
    private int totalVoid = 0;
    @XmlElement
    private int totalRefund = 0;

    @XmlElement
    private String summery;
     
    public GDSSaleReport() {

    }

    public List<Ticket> getList() {
        return list;
    }

    public void setList(List<Ticket> list) {
        this.list = list;
    }

    public BigDecimal getTotalIssuedFare() {
        return totalIssuedFare;
    }

    public BigDecimal getTotalRefundFare() {
        return totalRefundFare;
    }

    public BigDecimal getTotalTax() {
        return totalTax;
    }

    public BigDecimal getTotalTaxRefund() {
        return totalTaxRefund;
    }

    public BigDecimal getTotalBSPCom() {
        return totalBSPCom;
    }

    public BigDecimal getTotalBSPComRefund() {
        return totalBSPComRefund;
    }

    public BigDecimal getSaleBalance() {
        return saleBalance;
    }

    public BigDecimal getRefundBalance() {
        return refundBalance;
    }

    public BigDecimal getComBalance() {
        return comBalance;
    }

    public int getTotalIssue() {
        return totalIssue;
    }

    public int getTotalRefund() {
        return totalRefund;
    }

    public String getSummery() {
        return summery;
    }

    public void setTotalIssuedFare(BigDecimal totalIssuedFare) {
        this.totalIssuedFare = totalIssuedFare;
    }

    public void setTotalRefundFare(BigDecimal totalRefundFare) {
        this.totalRefundFare = totalRefundFare;
    }

    public void setTotalTax(BigDecimal totalTax) {
        this.totalTax = totalTax;
    }

    public void setTotalTaxRefund(BigDecimal totalTaxRefund) {
        this.totalTaxRefund = totalTaxRefund;
    }

    public void setTotalBSPCom(BigDecimal totalBSPCom) {
        this.totalBSPCom = totalBSPCom;
    }

    public void setTotalBSPComRefund(BigDecimal totalBSPComRefund) {
        this.totalBSPComRefund = totalBSPComRefund;
    }

    public void setSaleBalance(BigDecimal saleBalance) {
        this.saleBalance = saleBalance;
    }

    public void setRefundBalance(BigDecimal refundBalance) {
        this.refundBalance = refundBalance;
    }

    public void setComBalance(BigDecimal comBalance) {
        this.comBalance = comBalance;
    }

    public int getTotalBooking() {
        return totalBooking;
    }

    public void setTotalBooking(int totalBooking) {
        this.totalBooking = totalBooking;
    }

    public void setTotalIssue(int totalIssue) {
        this.totalIssue = totalIssue;
    }

    public int getTotalReIssue() {
        return totalReIssue;
    }

    public void setTotalReIssue(int totalReIssue) {
        this.totalReIssue = totalReIssue;
    }

    public int getTotalVoid() {
        return totalVoid;
    }

    public void setTotalVoid(int totalVoid) {
        this.totalVoid = totalVoid;
    }

    public void setTotalRefund(int totalRefund) {
        this.totalRefund = totalRefund;
    }

    public void setSummery(String summery) {
        this.summery = summery;
    }
    
    
}
