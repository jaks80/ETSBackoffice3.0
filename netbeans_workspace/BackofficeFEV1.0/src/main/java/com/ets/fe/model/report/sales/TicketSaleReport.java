package com.ets.fe.model.report.sales;

import com.ets.fe.model.pnr.Ticket;
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
public class TicketSaleReport {

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
    private int totalIssue = 0;
    @XmlElement
    private int totalRefund = 0;

    @XmlElement
    private String summery;
     
    public TicketSaleReport() {

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
    
    
}