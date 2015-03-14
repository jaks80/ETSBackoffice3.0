package com.ets.fe.accounts.model;

import com.ets.fe.settings.model.User;
import com.ets.fe.util.Enums;
import java.util.LinkedHashMap;
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
public class CreditTransfer {
    
    @XmlElement
    private Long invoiceId;
    @XmlElement
    private Enums.SaleType saleType;    
    @XmlElement
    private LinkedHashMap<Long, String> refundMap = new LinkedHashMap<>();
    @XmlElement
    private User user;
    
    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public LinkedHashMap<Long, String> getRefundMap() {
        return refundMap;
    }

    public void setRefundMap(LinkedHashMap<Long, String> refundMap) {
        this.refundMap = refundMap;
    }        

    public Enums.SaleType getSaleType() {
        return saleType;
    }

    public void setSaleType(Enums.SaleType saleType) {
        this.saleType = saleType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
