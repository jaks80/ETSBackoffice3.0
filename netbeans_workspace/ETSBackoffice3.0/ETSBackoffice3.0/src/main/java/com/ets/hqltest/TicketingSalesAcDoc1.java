/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ets.hqltest;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Yusuf
 */
@Entity
@Table(name = "tkt_sales_acdoc")
public class TicketingSalesAcDoc1 implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Long id;
    @Lob
    @Column(name = "createdBy")
    private byte[] createdBy;
    @Column(name = "createdOn")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;
    @Column(name = "lastModified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModified;
    @Lob
    @Column(name = "lastModifiedBy")
    private byte[] lastModifiedBy;
    @Column(name = "docIssueDate")
    @Temporal(TemporalType.DATE)
    private Date docIssueDate;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "documentedAmount")
    private BigDecimal documentedAmount;
    @Basic(optional = false)
    @NotNull
    @Column(name = "reference")
    private long reference;
    @Basic(optional = false)
    @NotNull
    @Column(name = "status")
    private int status;
    @Size(max = 255)
    @Column(name = "terms")
    private String terms;
    @Basic(optional = false)
    @NotNull
    @Column(name = "type")
    private int type;
    @Column(name = "version")
    private Integer version;
    @OneToMany(mappedBy = "parentFk")
    private Set<TicketingSalesAcDoc1> ticketingSalesAcDocSet;
    @JoinColumn(name = "parent_fk", referencedColumnName = "id")
    @ManyToOne
    private TicketingSalesAcDoc1 parentFk;


    public TicketingSalesAcDoc1() {
    }

    public TicketingSalesAcDoc1(Long id) {
        this.id = id;
    }

    public TicketingSalesAcDoc1(Long id, BigDecimal documentedAmount, long reference, int status, int type) {
        this.id = id;
        this.documentedAmount = documentedAmount;
        this.reference = reference;
        this.status = status;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(byte[] createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public byte[] getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(byte[] lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Date getDocIssueDate() {
        return docIssueDate;
    }

    public void setDocIssueDate(Date docIssueDate) {
        this.docIssueDate = docIssueDate;
    }

    public BigDecimal getDocumentedAmount() {
        return documentedAmount;
    }

    public void setDocumentedAmount(BigDecimal documentedAmount) {
        this.documentedAmount = documentedAmount;
    }

    public long getReference() {
        return reference;
    }

    public void setReference(long reference) {
        this.reference = reference;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Set<TicketingSalesAcDoc1> getTicketingSalesAcDocSet() {
        return ticketingSalesAcDocSet;
    }

    public void setTicketingSalesAcDocSet(Set<TicketingSalesAcDoc1> ticketingSalesAcDocSet) {
        this.ticketingSalesAcDocSet = ticketingSalesAcDocSet;
    }

    public TicketingSalesAcDoc1 getParentFk() {
        return parentFk;
    }

    public void setParentFk(TicketingSalesAcDoc1 parentFk) {
        this.parentFk = parentFk;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TicketingSalesAcDoc1)) {
            return false;
        }
        TicketingSalesAcDoc1 other = (TicketingSalesAcDoc1) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ets.hqltest.TicketingSalesAcDoc[ id=" + id + " ]";
    }
    
}
