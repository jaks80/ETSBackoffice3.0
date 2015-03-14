package com.ets.fe.pnr.model;

import com.ets.fe.util.Enums;
import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Yusuf
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Airline {

    private static final long serialVersionUID = 1L;

    @XmlElement
    private String code;
    @XmlElement
    private String numaricCode;
    @XmlElement
    private String name;
    @XmlElement
    private Enums.CalculationType calculationType;
    @XmlElement
    private BigDecimal bspCom;

    public Airline() {

    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Enums.CalculationType getCalculationType() {
        return calculationType;
    }

    public void setCalculationType(Enums.CalculationType calculationType) {
        this.calculationType = calculationType;
    }

    public BigDecimal getBspCom() {
        return bspCom;
    }

    public void setBspCom(BigDecimal bspCom) {
        this.bspCom = bspCom;
    }

    public String getNumaricCode() {
        return numaricCode;
    }

    public void setNumaricCode(String numaricCode) {
        this.numaricCode = numaricCode;
    }
}
