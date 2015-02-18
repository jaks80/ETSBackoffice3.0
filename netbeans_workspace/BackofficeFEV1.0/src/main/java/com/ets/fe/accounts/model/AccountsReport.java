package com.ets.fe.accounts.model;

import java.io.Serializable;
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
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class AccountsReport implements Serializable {    

    private static long serialVersionUID = 1L;

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
    private List<AccountsLine> lines = new ArrayList<>();
    @XmlElement
    private BigDecimal openingBalance = new BigDecimal("0.00");
    @XmlElement
    private BigDecimal closingBalance = new BigDecimal("0.00");
    @XmlElement
    private String totalInvAmount = new String("0.00");
    @XmlElement
    private String totalDMAmount = new String("0.00");
    @XmlElement
    private String totalCMAmount = new String("0.00");
    @XmlElement
    private String totalPayment = new String("0.00");
    @XmlElement
    private String totalRefund = new String("0.00");
    
    public BigDecimal getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(BigDecimal openingBalance) {
        this.openingBalance = openingBalance;
    }

    public BigDecimal getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(BigDecimal closingBalance) {
        this.closingBalance = closingBalance;
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

    public List<AccountsLine> getLines() {
        return lines;
    }

    public void setLines(List<AccountsLine> lines) {
        this.lines = lines;
    }

    public void addLine(AccountsLine line) {
        
        this.getLines().add(line);
    }

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

    @XmlAccessorType(XmlAccessType.NONE)
    @XmlRootElement
    public static class AccountsLine {

        @XmlElement
        private Long id;
        @XmlElement
        private String date;
        @XmlElement
        private String docType;
        @XmlElement
        private String line_desc;
        @XmlElement
        private BigDecimal debit_amount = new BigDecimal("0.00");
        @XmlElement
        private BigDecimal credit_amount = new BigDecimal("0.00");
        @XmlElement
        private BigDecimal line_balance = new BigDecimal("0.00");

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getDocType() {
            return docType;
        }

        public void setDocType(String docType) {
            this.docType = docType;
        }

        public String getLine_desc() {
            return line_desc;
        }

        public void setLine_desc(String line_desc) {
            this.line_desc = line_desc;
        }

        public BigDecimal getDebit_amount() {
            return debit_amount;
        }

        public void setDebit_amount(BigDecimal debit_amount) {
            this.debit_amount = debit_amount;
        }

        public BigDecimal getCredit_amount() {
            return credit_amount;
        }

        public void setCredit_amount(BigDecimal credit_amount) {
            this.credit_amount = credit_amount;
        }

        public BigDecimal getLine_balance() {
            return line_balance;
        }

        public void setLine_balance(BigDecimal line_balance) {
            this.line_balance = line_balance;
        }
    }

}
