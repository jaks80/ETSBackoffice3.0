package etsbackoffice.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.*;

/**
 *
 * @author Yusuf
 */
@Entity
@Table(name = "gds")
public class GDS implements Serializable {

    private int gdsId;
    private String name;
    private BigDecimal ratePerSegment = new BigDecimal("0.00");
    private List<OfficeID> officeIDs;
    private List<PNR> pnrs;

    public GDS() {
    }

    @Id
    @Column(name = "GDSID")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "gdsid")
    @TableGenerator(name = "gdsid", table = "gdsidpktb",
    pkColumnName = "gdsidkey", pkColumnValue = "gdsidvalue",
    allocationSize = 1)
    public int getGdsId() {
        return gdsId;
    }

    public void setGdsId(int gdsId) {
        this.gdsId = gdsId;
    }

    @Basic
    @Column(name = "GDSNAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(targetEntity = OfficeID.class, mappedBy = "gds",
    cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<OfficeID> getOfficeIDs() {
        return officeIDs;
    }

    public void setOfficeIDs(List<OfficeID> officeIDs) {
        this.officeIDs = officeIDs;
    }

    @OneToMany(targetEntity = PNR.class, mappedBy = "gds",
    cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<PNR> getPnrs() {
        return pnrs;
    }

    public void setPnrs(List<PNR> pnrs) {
        this.pnrs = pnrs;
    }

    public BigDecimal getRatePerSegment() {
        return ratePerSegment;
    }

    public void setRatePerSegment(BigDecimal ratePerSegment) {
        this.ratePerSegment = ratePerSegment;
    }
}
