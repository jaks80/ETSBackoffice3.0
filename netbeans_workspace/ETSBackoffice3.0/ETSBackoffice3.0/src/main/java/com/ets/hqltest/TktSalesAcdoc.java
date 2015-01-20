/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ets.hqltest;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Yusuf
 */
@Entity
@Table(name = "tkt_sales_acdoc")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TktSalesAcdoc.findAll", query = "SELECT t FROM TktSalesAcdoc t"),
    @NamedQuery(name = "TktSalesAcdoc.findById", query = "SELECT t FROM TktSalesAcdoc t WHERE t.id = :id"),
    @NamedQuery(name = "TktSalesAcdoc.findByCreatedOn", query = "SELECT t FROM TktSalesAcdoc t WHERE t.createdOn = :createdOn"),
    @NamedQuery(name = "TktSalesAcdoc.findByLastModified", query = "SELECT t FROM TktSalesAcdoc t WHERE t.lastModified = :lastModified"),
    @NamedQuery(name = "TktSalesAcdoc.findByDocIssueDate", query = "SELECT t FROM TktSalesAcdoc t WHERE t.docIssueDate = :docIssueDate"),
    @NamedQuery(name = "TktSalesAcdoc.findByDocumentedAmount", query = "SELECT t FROM TktSalesAcdoc t WHERE t.documentedAmount = :documentedAmount"),
    @NamedQuery(name = "TktSalesAcdoc.findByReference", query = "SELECT t FROM TktSalesAcdoc t WHERE t.reference = :reference"),
    @NamedQuery(name = "TktSalesAcdoc.findByStatus", query = "SELECT t FROM TktSalesAcdoc t WHERE t.status = :status"),
    @NamedQuery(name = "TktSalesAcdoc.findByTerms", query = "SELECT t FROM TktSalesAcdoc t WHERE t.terms = :terms"),
    @NamedQuery(name = "TktSalesAcdoc.findByType", query = "SELECT t FROM TktSalesAcdoc t WHERE t.type = :type"),
    @NamedQuery(name = "TktSalesAcdoc.findByVersion", query = "SELECT t FROM TktSalesAcdoc t WHERE t.version = :version")})
public class TktSalesAcdoc implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Long id;
    @Column(name = "createdOn")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;
    @Column(name = "lastModified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModified;
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
    private Collection<TktSalesAcdoc> tktSalesAcdocCollection;
    @JoinColumn(name = "parent_fk", referencedColumnName = "id")
    @ManyToOne
    private TktSalesAcdoc parentFk;

    public TktSalesAcdoc() {
    }

    public TktSalesAcdoc(Long id) {
        this.id = id;
    }

    public TktSalesAcdoc(Long id, BigDecimal documentedAmount, long reference, int status, int type) {
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

    @XmlTransient
    public Collection<TktSalesAcdoc> getTktSalesAcdocCollection() {
        return tktSalesAcdocCollection;
    }

    public void setTktSalesAcdocCollection(Collection<TktSalesAcdoc> tktSalesAcdocCollection) {
        this.tktSalesAcdocCollection = tktSalesAcdocCollection;
    }

    public TktSalesAcdoc getParentFk() {
        return parentFk;
    }

    public void setParentFk(TktSalesAcdoc parentFk) {
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
        if (!(object instanceof TktSalesAcdoc)) {
            return false;
        }
        TktSalesAcdoc other = (TktSalesAcdoc) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ets.hqltest.TktSalesAcdoc[ id=" + id + " ]";
    }
    
}
