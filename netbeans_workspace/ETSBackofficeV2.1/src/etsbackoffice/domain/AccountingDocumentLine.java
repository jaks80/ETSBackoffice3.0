package etsbackoffice.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.*;

/**
 *
 * @author Yusuf
 */
@Entity
@Table(name = "acdocline")
public class AccountingDocumentLine implements Serializable {

    private long acDocLineId;
    private int type;//1. Ticket, 2. Additional Service 3. Other Service(Hotel etc) 7. Unspecified Type
                    //1. Ticket 2. Services
    private BigDecimal cost = new BigDecimal("0.00");
    private BigDecimal amount = new BigDecimal("0.00");
    private BigDecimal discount = new BigDecimal("0.00");
    private String remark;
    //private int unit;
    private AccountingDocument accountingDocument;
    private Set<Ticket> tickets = new LinkedHashSet<Ticket>();
    private Set<Services> services = new LinkedHashSet<Services>();
    //private OtherService otherService;

    public AccountingDocumentLine() {
    }

    @Id
    @Column(name = "LINEID")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "lineId")
    @TableGenerator(name = "lineId", table = "lineidpktb",
    pkColumnName = "lineidkey", pkColumnValue = "lineidvalue", allocationSize = 1)
    public long getAcDocLineId() {
        return acDocLineId;
    }

    public void setAcDocLineId(long acDocLineId) {
        this.acDocLineId = acDocLineId;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACDOCID_FK")
    public AccountingDocument getAccountingDocument() {
        return accountingDocument;
    }

    public void setAccountingDocument(AccountingDocument accountingDocument) {
        this.accountingDocument = accountingDocument;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    @ManyToMany//(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinTable(name = "join_ticket_acdocline",
    joinColumns = {
        @JoinColumn(name = "ACDOCLINE_ID")},
    inverseJoinColumns = {
        @JoinColumn(name = "TKT_ID")})
    @OrderBy(value = "ticketId")
    public Set<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(Set<Ticket> tickets) {
        this.tickets = tickets;
    }

    public void addTicket(Ticket t) {
        this.tickets.add(t);
    }

    @ManyToMany//(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinTable(name = "join_service_acdocline",
    joinColumns = {
        @JoinColumn(name = "ACDOCLINE_ID")},
    inverseJoinColumns = {
        @JoinColumn(name = "SERV_ID")})
    @OrderBy(value = "serviceId")
    public Set<Services> getServices() {
        return services;
    }

    public void setServices(Set<Services> services) {
        this.services = services;
    }

    public void addService(Services s){
     this.services.add(s);
    }
    
    public void removeService(Services s){
    this.services.remove(s);
    }
    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    } 

    @Transient
    public BigDecimal getTktNetPayable() {
        BigDecimal net = new BigDecimal("0.00");
        for (Ticket t : this.tickets) {
            net = net.add(t.getNetPayble());
        }
        return net;
    }

    @Transient
    public BigDecimal getOtherNetPayable() {
        BigDecimal netPayable = new BigDecimal("0.00");
        for (Services s : this.services) {
            if (s.getServiceType() == 1) {
                netPayable = netPayable.add(s.getNetPayable());
            }
        }
        return netPayable;
    }

    @Transient
    public BigDecimal getAdditionalNetPayable() {
        BigDecimal netPayable = new BigDecimal("0.00");
        for (Services s : this.services) {
            if (s.getServiceType() == 2) {
                netPayable = netPayable.add(s.getNetPayable());               
            }
        }
        return netPayable;
    }

    @Transient
    public BigDecimal getRevenueFromOLine() {
        BigDecimal p = new BigDecimal("0.00");
        if (!this.services.isEmpty()) {
            for (Services s : this.services) {
                if (s.getServiceType() == 1) {
                    p = p.add(s.getRevenue());
                }
            }
        }
        return p;
    }

    @Transient
    public BigDecimal getRevenueFromALine() {
        BigDecimal p = new BigDecimal("0.00");
        if (!this.services.isEmpty()) {
            for (Services s : this.services) {
                if (s.getServiceType() == 2) {
                    p = p.add(s.getRevenue());
                }
            }
        }
        return p;
    }   
    
    @Transient
    public List<Services> getOtherServices() {
        List<Services> oServices = new ArrayList();
        if (!this.services.isEmpty()) {
            for (Services s : services) {
                if (s.getServiceType() == 1) {
                    oServices.add(s);
                }
            }
        }
        return oServices;
    }  
    
    @Transient
    public List<Services> getAdditionalServices() {
        List<Services> aServices = new ArrayList();
        if (!this.services.isEmpty()) {
            for (Services s : services) {
                if (s.getServiceType() == 2) {
                    aServices.add(s);
                }
            }
        }
        return aServices;
    } 
    
    @Transient
    public List<Services> getAllServices() {
        List<Services> aServices = new ArrayList();
        if (!this.services.isEmpty()) {
            for (Services s : services) {                
                    aServices.add(s);                
            }
        }
        return aServices;
    } 
    
    @Transient
    public int getNoofPax(){
        int noOfPax = 0;
     if(!this.tickets.isEmpty()){
     noOfPax = this.tickets.size();
     }
     return noOfPax;
    }
}
