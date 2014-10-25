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
@Table(name = "transactionbatch")
public class BatchTransaction implements Serializable {

    private long batchId;
    private String collectionRef;
    private Date collectionDate;
    private Set<AcTransaction> acTransactions = new LinkedHashSet<AcTransaction>();
    private User collectionBy;
    private Agent consignee;

    public BatchTransaction() {
    }

    @Id
    @Column(name = "PCOL_ID")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "pcolid")
    @TableGenerator(name = "pcolid", table = "pcolidpktb",
    pkColumnName = "pcolidkey", pkColumnValue = "pcolidvalue", allocationSize = 1)
    public long getBatchId() {
        return batchId;
    }

    public void setBatchId(long batchId) {
        this.batchId = batchId;
    }

    public String getCollectionRef() {
        return collectionRef;
    }

    public void setCollectionRef(String collectionRef) {
        this.collectionRef = collectionRef;
    }

    @Temporal(TemporalType.DATE)
    public Date getCollectionDate() {
        return collectionDate;
    }

    public void setCollectionDate(Date collectionDate) {
        this.collectionDate = collectionDate;
    }

    @OneToMany(targetEntity = AcTransaction.class, mappedBy = "batchTransaction",
    cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy(value = "transID asc")
    public Set<AcTransaction> getAcTransactions() {
        return acTransactions;
    }

    public void setAcTransactions(Set<AcTransaction> acTransactions) {
        this.acTransactions = acTransactions;
    }

    @ManyToOne
    public User getCollectionBy() {
        return collectionBy;
    }

    public void setCollectionBy(User collectionBy) {
        this.collectionBy = collectionBy;
    }

    @ManyToOne
    public Agent getConsignee() {
        return consignee;
    }

    public void setConsignee(Agent consignee) {
        this.consignee = consignee;
    }

    @Transient
    public BigDecimal getBatchAmount() {
        BigDecimal amount = new BigDecimal("0.00");
        for (AcTransaction act : getAcTransactions()) {
            amount = amount.add(act.getTransAmount());
        }
        return amount;
    }

    @Transient
    public int getNoOfTransactions() {
        return this.acTransactions.size();
    }
    
        @Transient
    public long getNumberOfTransaction() {
        return getAcTransactions().size();
    }

    @Transient
    public BigDecimal getTotalCollection() {
        BigDecimal totalAmount = new BigDecimal("0.00");
        for (AcTransaction act : this.acTransactions) {
            totalAmount = totalAmount.add(act.getTransAmount());
        }
        return totalAmount;
    }
}
