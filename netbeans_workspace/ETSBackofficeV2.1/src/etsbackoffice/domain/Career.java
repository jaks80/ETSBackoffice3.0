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
@Table(name = "career")
public class Career implements Serializable {

    //private long careerId;
    private String name;
    private String code;
    private BigDecimal bspComFixed = new BigDecimal("0.00");
    private BigDecimal bspComPercentage = new BigDecimal("0.00");
    private List<PNR> pnrs;
    public Career() {
    }    

    @Basic
    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Id
    @Column(name = "CODE")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    @OneToMany(targetEntity = PNR.class, mappedBy = "servicingCareer",
    cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<PNR> getPnrs() {
        return pnrs;
    }

    public void setPnrs(List<PNR> pnrs) {
        this.pnrs = pnrs;
    }

    public BigDecimal getBspComFixed() {
        return bspComFixed;
    }

    public void setBspComFixed(BigDecimal bspComFixed) {
        this.bspComFixed = bspComFixed;
    }

    public BigDecimal getBspComPercentage() {
        return bspComPercentage;
    }

    public void setBspComPercentage(BigDecimal bspComPercentage) {
        this.bspComPercentage = bspComPercentage;
    }
}
