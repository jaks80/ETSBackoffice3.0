package etsbackoffice.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.*;

/**
 *
 * @author Yusuf
 */
@Entity
@Table(name = "batchBillPayment")
public class BatchBillingTransaction implements Serializable {

    private long batchBillingId;
    private String batchBillingRef;
    private Date batchBillingDate;
    private int billingType; //1.IATA 2. Thirdparty
    private Set<BillingTransaction> billingTransactions = new LinkedHashSet<BillingTransaction>();
    private User billingBy;
    private Agent vendor;

    public BatchBillingTransaction() {
    }

    @Id
    @Column(name = "BBILLING_ID")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "bbid")
    @TableGenerator(name = "bbid", table = "bbidpktb",
    pkColumnName = "bbidkey", pkColumnValue = "bbidvalue", allocationSize = 1)
    public long getBatchBillingId() {
        return batchBillingId;
    }

    public void setBatchBillingId(long batchBillingId) {
        this.batchBillingId = batchBillingId;
    }

    public String getBatchBillingRef() {
        return batchBillingRef;
    }

    public void setBatchBillingRef(String batchBillingRef) {
        this.batchBillingRef = batchBillingRef;
    }

    @Temporal(TemporalType.DATE)
    public Date getBatchBillingDate() {
        return batchBillingDate;
    }

    public void setBatchBillingDate(Date batchBillingDate) {
        this.batchBillingDate = batchBillingDate;
    }

    @OneToMany(targetEntity = BillingTransaction.class, mappedBy = "batchBillingTransaction",
    cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy(value = "transID asc")
    public Set<BillingTransaction> getBillingTransactions() {
        return billingTransactions;
    }

    public void setBillingTransactions(Set<BillingTransaction> billingTransactions) {
        this.billingTransactions = billingTransactions;
    }

    public int getBillingType() {
        return billingType;
    }

    public void setBillingType(int billingType) {
        this.billingType = billingType;
    }

    @ManyToOne
    public User getBillingBy() {
        return billingBy;
    }

    public void setBillingBy(User billingBy) {
        this.billingBy = billingBy;
    }

    @ManyToOne
    public Agent getVendor() {
        return vendor;
    }

    public void setVendor(Agent vendor) {
        this.vendor = vendor;
    }

    @Transient
    public BigDecimal getBatchAmount() {
        BigDecimal amount = new BigDecimal("0.00");
        for (BillingTransaction bt : this.billingTransactions) {
            amount = amount.add(bt.getTransAmount());
        }
        return amount;
    }

    @Transient
    public int getNoOfTransactions() {
        return this.billingTransactions.size();
    }

    @Transient
    public BigDecimal getTotalCollection() {
        BigDecimal totalAmount = new BigDecimal("0.00");
        for (BillingTransaction bt : this.billingTransactions) {
            totalAmount = totalAmount.add(bt.getTransAmount());
        }
        return totalAmount;
    }
}
