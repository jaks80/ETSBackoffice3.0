package com.ets.productivity.model;

import com.ets.util.Enums;
import java.util.LinkedHashMap;
import java.util.Map;
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
public class UserProductivityReport {
   
    @XmlElement
    private String title;
    @XmlElement
    private Enums.SaleType saleType;
    @XmlElement
    private String dateFrom;
    @XmlElement
    private String dateTo;
    @XmlElement
    private Map<String, String> productivityLine = new LinkedHashMap<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<String, String> getProductivityLine() {
        return productivityLine;
    }

    public void setProductivityLine(Map<String, String> productivityLine) {
        this.productivityLine = productivityLine;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    public String getDateTo() {
        return dateTo;
    }

    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }

    public Enums.SaleType getSaleType() {
        return saleType;
    }

    public void setSaleType(Enums.SaleType saleType) {
        this.saleType = saleType;
    }

}
