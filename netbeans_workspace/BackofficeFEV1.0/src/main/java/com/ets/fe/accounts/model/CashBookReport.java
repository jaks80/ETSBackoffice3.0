package com.ets.fe.accounts.model;

import com.ets.fe.util.DateUtil;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
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
public class CashBookReport implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement
    private String title = "Cash Book";    

    @XmlElement
    private int totalItems;
    @XmlElement
    private String dateFrom;
    @XmlElement
    private String dateTo;
    @XmlElement
    private BigDecimal totalCash = new BigDecimal("0.00");
    @XmlElement
    private BigDecimal totalCheque = new BigDecimal("0.00");
    @XmlElement
    private BigDecimal totalCCard = new BigDecimal("0.00");
    @XmlElement
    private BigDecimal totalDCard = new BigDecimal("0.00");
    @XmlElement
    private BigDecimal totalBankTransfer = new BigDecimal("0.00");
    @XmlElement
    private BigDecimal totalOther = new BigDecimal("0.00");
    @XmlElement
    private BigDecimal totalCreditTransfer = new BigDecimal("0.00");
    @XmlElement

    private List<CashBookLine> cashbook_items = new ArrayList<>();
   
    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
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

    public List<CashBookLine> getCashbook_items() {
        return cashbook_items;
    }

    public void setCashbook_items(List<CashBookLine> cashbook_items) {
        this.cashbook_items = cashbook_items;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getTotalCash() {
        return totalCash;
    }

    public void setTotalCash(BigDecimal totalCash) {
        this.totalCash = totalCash;
    }

    public BigDecimal getTotalCheque() {
        return totalCheque;
    }

    public void setTotalCheque(BigDecimal totalCheque) {
        this.totalCheque = totalCheque;
    }

    public BigDecimal getTotalCCard() {
        return totalCCard;
    }

    public void setTotalCCard(BigDecimal totalCCard) {
        this.totalCCard = totalCCard;
    }

    public BigDecimal getTotalDCard() {
        return totalDCard;
    }

    public void setTotalDCard(BigDecimal totalDCard) {
        this.totalDCard = totalDCard;
    }

    public BigDecimal getTotalBankTransfer() {
        return totalBankTransfer;
    }

    public void setTotalBankTransfer(BigDecimal totalBankTransfer) {
        this.totalBankTransfer = totalBankTransfer;
    }

    public BigDecimal getTotalOther() {
        return totalOther;
    }

    public void setTotalOther(BigDecimal totalOther) {
        this.totalOther = totalOther;
    }

    public BigDecimal getTotalCreditTransfer() {
        return totalCreditTransfer;
    }

    public void setTotalCreditTransfer(BigDecimal totalCreditTransfer) {
        this.totalCreditTransfer = totalCreditTransfer;
    }
}
