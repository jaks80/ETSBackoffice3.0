package etsbackoffice.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.*;

/**
 *
 * @author Yusuf
 */
@Entity
@Table(name = "puracdocline")
public class PurchaseAccountingDocumentLine implements Serializable {

    private long purchaseAcDocLineId;
    private int type;//1. Ticket, 2. Admin Chg,3.OtherCharge,4.Postage,5. Void Ticket,6.Compensation Credit, 7.Other Credit
                     //1. Ticket, 2. Additional Service 3. Other Service(Hotel etc) 7. Unspecified Type   
    private BigDecimal amount = new BigDecimal("0.00");
    private BigDecimal discount = new BigDecimal("0.00");
    private String remark;
    private PurchaseAccountingDocument purchaseAccountingDocument;
    private Set<Ticket> tickets = new LinkedHashSet<Ticket>();
    private OtherService otherService;

    public PurchaseAccountingDocumentLine(){

    }

    @Id
    @Column(name = "LINEID")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "lineId")
    @TableGenerator(name = "lineId", table = "lineidpktb",
    pkColumnName = "lineidkey", pkColumnValue = "lineidvalue", allocationSize = 1)
    public long getPurchaseAcDocLineId() {
        return purchaseAcDocLineId;
    }

    public void setPurchaseAcDocLineId(long purchaseAcDocLineId) {
        this.purchaseAcDocLineId = purchaseAcDocLineId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PURACDOCID_FK")
    public PurchaseAccountingDocument getPurchaseAccountingDocument() {
        return purchaseAccountingDocument;
    }

    public void setPurchaseAccountingDocument(PurchaseAccountingDocument purchaseAccountingDocument) {
        this.purchaseAccountingDocument = purchaseAccountingDocument;
    }

    @ManyToMany(fetch = FetchType.LAZY)    
    @JoinTable(name = "join_ticket_pacdocline",
    joinColumns={@JoinColumn(name = "PACDOCLINE_ID")},
    inverseJoinColumns={@JoinColumn(name ="TKT_ID")})
    //@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)    
    public Set<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(Set<Ticket> tickets) {
        this.tickets = tickets;
    }

    public void addTicket(Ticket t){
    this.tickets.add(t);
    }

    @OneToOne
    public OtherService getOtherService() {
        return otherService;
    }

    public void setOtherService(OtherService otherService) {
        this.otherService = otherService;
    }

    @Transient
    public BigDecimal getTktNetBillable() {
        BigDecimal net = new BigDecimal("0.00");
        for (Ticket t : this.tickets) {
            net = net.add(t.getNetBillable());
        }

        return net;
    }
}
