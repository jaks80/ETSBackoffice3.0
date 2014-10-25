/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etsbackoffice.domain;

import etsbackoffice.businesslogic.AuthenticationBo;
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
@Table(name = "oacdoc")
public class OAccountingDocument implements Serializable {

    private long oAcDocId;
    private int acDoctype;//1: Invoice, 2: CreditNote
    private Date issueDate;
    private Integer acDocRef;
    private String terms;    
    private String remark;
    private User acDocIssuedBy;
    private User acDocModifiedBy;
    private boolean active;
    private Agent agent;
    private Customer customer;
    private Set<AcTransaction> acTransactions = new LinkedHashSet<AcTransaction>();
    private Set<OAccountingDocumentLine> oAccountingDocumentLines = new LinkedHashSet<OAccountingDocumentLine>();
    private Set<OAccountingDocument> relatedDocuments = new LinkedHashSet<OAccountingDocument>();
    private OAccountingDocument oAccountingDocument;    

    private Set<Accounts> accounts = new LinkedHashSet<Accounts>();
    public OAccountingDocument() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "oacdocid")
    @TableGenerator(name = "oacdocid", table = "oacdocidpktb",
    pkColumnName = "oacdocidkey", pkColumnValue = "oacdocidvalue", allocationSize = 1)
    public long getoAcDocId() {
        return oAcDocId;
    }

    public void setoAcDocId(long oAcDocId) {
        this.oAcDocId = oAcDocId;
    }

    public int getAcDoctype() {
        return acDoctype;
    }

    public void setAcDoctype(int acDoctype) {
        this.acDoctype = acDoctype;
    }

    @Temporal(TemporalType.DATE)
    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    @Column(length = 100)
    public Integer getAcDocRef() {
        return acDocRef;
    }

    public void setAcDocRef(Integer acDocRef) {
        this.acDocRef = acDocRef;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    @ManyToOne
    public User getAcDocIssuedBy() {
        return acDocIssuedBy;
    }

    public void setAcDocIssuedBy(User acDocIssuedBy) {
        this.acDocIssuedBy = acDocIssuedBy;
    }

    @ManyToOne
    public User getAcDocModifiedBy() {
        return acDocModifiedBy;
    }

    public void setAcDocModifiedBy(User acDocModifiedBy) {
        this.acDocModifiedBy = acDocModifiedBy;
    }

    @OneToMany(targetEntity = AcTransaction.class, mappedBy = "oAccountingDocument",
    cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy(value = "transDate asc")
    public Set<AcTransaction> getAcTransactions() {
        return acTransactions;
    }

    public void setAcTransactions(Set<AcTransaction> acTransactions) {
        this.acTransactions = acTransactions;
    }

    @OneToMany(targetEntity = OAccountingDocumentLine.class, mappedBy = "oAccountingDocument",
    cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy(value = "oAcDocLineId asc")
    public Set<OAccountingDocumentLine> getoAccountingDocumentLines() {
        return oAccountingDocumentLines;
    }

    public void setoAccountingDocumentLines(Set<OAccountingDocumentLine> oAccountingDocumentLines) {
        this.oAccountingDocumentLines = oAccountingDocumentLines;
    }

    @OneToMany(mappedBy = "oAccountingDocument")
    public Set<OAccountingDocument> getRelatedDocuments() {
        return relatedDocuments;
    }

    public void setRelatedDocuments(Set<OAccountingDocument> relatedDocuments) {
        this.relatedDocuments = relatedDocuments;
    }

    @ManyToOne
    public OAccountingDocument getoAccountingDocument() {
        return oAccountingDocument;
    }

    public void setoAccountingDocument(OAccountingDocument oAccountingDocument) {
        this.oAccountingDocument = oAccountingDocument;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "AGTID_FK")
    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "CUSTID_FK")
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Transient
    public String getAcDocRefString() {
        String prefix = AuthenticationBo.getmAgent().getmAgentCode();
        String suffix  = "" ;
        if (this.acDoctype == 1) {
            suffix = "OIN";
        } else if (this.acDoctype == 2) {
            suffix = "OCN";
        }
        return String.valueOf(this.acDocRef).concat(suffix);
    }

    
    @Transient
    public String getAcDocRefFull() {
        String prefix = AuthenticationBo.getmAgent().getmAgentCode();
        String suffix  = "" ;
        if (this.acDoctype == 1) {
            suffix = "OIN";
        } else if (this.acDoctype == 2) {
            suffix = "OCN";
        }
        return String.valueOf(this.acDocRef).concat(suffix);
    }
        
    @Transient
    public String getAcDocTypeString() {
        if (this.acDoctype == 1) {
            return "INVOICE";
        } else if (this.acDoctype == 2) {
            return "CNOTE";
        } else {
            return "";
        }
    }

    @Transient
    public BigDecimal getTotalDocumentedAmount() {
        BigDecimal netPayableFromService = new BigDecimal("0.00");
        BigDecimal otherTotal = new BigDecimal("0.00");
        for (OAccountingDocumentLine acDocLine : this.oAccountingDocumentLines) {
            netPayableFromService = netPayableFromService.add(acDocLine.getNetPayable());
        }
        return netPayableFromService.add(otherTotal).add(getVatTotal());
    }

 /*   @Transient
    public BigDecimal getSubTotal() {
        BigDecimal netPayableFromService = new BigDecimal("0.00");
        for (OAccountingDocumentLine acDocLine : this.oAccountingDocumentLines) {
            netPayableFromService = netPayableFromService.add(acDocLine.getNetPayable());
        }
        return netPayableFromService;
    }*/

    @Transient
    public BigDecimal getTotalTransactionAmount() {
        BigDecimal totalTransaction = new BigDecimal("0.00");
        for (AcTransaction t : this.getAcTransactions()) {
            totalTransaction = totalTransaction.add(t.getTransAmount());
        }
        return totalTransaction;
    }

    @Transient
    public BigDecimal getOutstandingAmount() {
        return getTotalDocumentedAmount().subtract(getTotalTransactionAmount());
    }

    @Transient
    public Set<OAccountingDocumentLine> getNormalService() {

        Set<OAccountingDocumentLine> normalServices = new LinkedHashSet();
        for (OAccountingDocumentLine l : this.oAccountingDocumentLines) {
            if (l.getOtherService().getServiceType() == 1) {
                normalServices.add(l);
            }
        }
        return normalServices;
    }

    @Transient
    public Set<OAccountingDocumentLine> getAdditionalService() {

        Set<OAccountingDocumentLine> additionalServices = new LinkedHashSet();
        for (OAccountingDocumentLine l : this.oAccountingDocumentLines) {
            if (l.getOtherService().getServiceType() == 2) {
                additionalServices.add(l);
            }
        }
        return additionalServices;
    }

    @Transient
    public BigDecimal getNormalServiceSubTotal() {
        BigDecimal subTotal = new BigDecimal("0.00");
        
        for (OAccountingDocumentLine l : this.oAccountingDocumentLines) {
            if (l.getOtherService().getServiceType() == 1) {
                subTotal = subTotal.add(l.getNetPayable());
            }
        }
        return subTotal;
    }

    @Transient
    public BigDecimal getAdditionalServiceSubTotal() {
        BigDecimal subTotal = new BigDecimal("0.00");

        for (OAccountingDocumentLine l : this.oAccountingDocumentLines) {
            if (l.getOtherService().getServiceType() == 2) {
                subTotal = subTotal.add(l.getNetPayable());
            }
        }
        return subTotal;
    }

    @Transient
    public BigDecimal getVatTotal() {
        BigDecimal vatTotal = new BigDecimal("0.00");

        for (OAccountingDocumentLine l : this.oAccountingDocumentLines) {
            vatTotal = vatTotal.add(l.getVat());
        }
        return vatTotal;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @OneToMany(mappedBy = "oAccountingDocument", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public Set<Accounts> getAccounts() {
        return accounts;
    }

    public void setAccounts(Set<Accounts> accounts) {
        this.accounts = accounts;
    }

    public void addAcStatement(Accounts stmt) {
        Set<Accounts> stmts = new LinkedHashSet<Accounts>();
        stmts.add(stmt);
        setAccounts(stmts);
    }

    @Transient
    public String getAcDocFor() {
        if (this.getAgent() != null) {
            return this.getAgent().getName();
        } else if(this.customer != null){
            return this.getCustomer().getFullCustomerName();
        }else{
        return "";
        }
    }
    
    public void addLine(OAccountingDocumentLine l) {
        this.oAccountingDocumentLines.add(l);
    }
    
    @Transient
    public BigDecimal getSRevenue() {
        BigDecimal rev = new BigDecimal("0.00");

        for (OAccountingDocumentLine l : this.oAccountingDocumentLines) {
            if (l.getOtherService().getServiceType() == 1) {
                rev = rev.add(l.getRevFromLine());
            }
        }
        return rev;
    }  
    
    @Transient
    public BigDecimal getOChgRevenue() {
        BigDecimal rev = new BigDecimal("0.00");

        for (OAccountingDocumentLine l : this.oAccountingDocumentLines) {
            if (l.getOtherService().getServiceType() == 2) {
                rev = rev.add(l.getRevFromLine());
            }
        }
        return rev;
    }
}
