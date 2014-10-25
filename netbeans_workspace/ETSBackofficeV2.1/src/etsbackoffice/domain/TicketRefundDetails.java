package etsbackoffice.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.*;

/**
 *
 * @author Yusuf
 */
@Entity
@Table(name = "tktrfddetails")
public class TicketRefundDetails implements Serializable{
    private long refundID;
    private BigDecimal farePaid = new BigDecimal("0.00");
    private BigDecimal fareUsed = new BigDecimal("0.00");
    private BigDecimal cancellationFee = new BigDecimal("0.00");
    private BigDecimal cfCom = new BigDecimal("0.00");
    private BigDecimal cMiscFee = new BigDecimal("0.00");
    //private Ticket ticket;

    public TicketRefundDetails(){
    }

    @Id
    @Column(name = "TKTRFDID")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "tktrfdid")
    @TableGenerator(name = "tktrfdid", table = "tktrfdidpktb",
    pkColumnName = "tktrfdidkey", pkColumnValue = "tktrfdidvalue", allocationSize = 1)
    public long getRefundID() {
        return refundID;
    }

    public void setRefundID(long refundID) {
        this.refundID = refundID;
    }

    public BigDecimal getFarePaid() {
        return farePaid;
    }

    public void setFarePaid(BigDecimal farePaid) {
        this.farePaid = farePaid;
    }

    public BigDecimal getFareUsed() {
        return fareUsed;
    }

    public void setFareUsed(BigDecimal fareUsed) {
        this.fareUsed = fareUsed;
    }    

    public BigDecimal getCancellationFee() {
        return cancellationFee;
    }

    public void setCancellationFee(BigDecimal cancellationFee) {
        this.cancellationFee = cancellationFee;
    }

    public BigDecimal getCfCom() {
        return cfCom;
    }

    public void setCfCom(BigDecimal cfCom) {
        this.cfCom = cfCom;
    }

    public BigDecimal getCMiscFee() {
        return cMiscFee;
    }

    public void setCMiscFee(BigDecimal cMiscFee) {
        this.cMiscFee = cMiscFee;
    }

   /*@OneToOne(mappedBy = "ticketRefundDetails")
    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }
    
    @Transient
    public BigDecimal getBFRefundCalculation(){
        this.ticket.setBaseFare(this.farePaid.subtract(this.getFareUsed()).
            subtract(this.cancellationFee).subtract(this.cMiscFee).add(this.cfCom).negate());
    return this.ticket.getBaseFare();
    } */       
}
