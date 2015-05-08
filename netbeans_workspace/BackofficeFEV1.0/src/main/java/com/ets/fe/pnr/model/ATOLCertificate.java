package com.ets.fe.pnr.model;

import javax.xml.bind.annotation.*;

/**
 *
 * @author Yusuf
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class ATOLCertificate {
    
    @XmlElement
    private String atolHolderName;
    @XmlElement
    private String atolNo;
    @XmlElement
    private String paxNames;
    @XmlElement
    private String segmentSummery;
    @XmlElement
    private String segments = "";
    @XmlElement
    private String atolHolderDetails;
    @XmlElement
    private int paxQty;
    @XmlElement
    private String pnr;
    @XmlElement
    private String totalCost;
    @XmlElement
    private String issuedate;
    
    public String getAtolHolderName() {
        return atolHolderName;
    }
    
    public void setAtolHolderName(String atolHolderName) {
        this.atolHolderName = atolHolderName;
    }
    
    public String getAtolNo() {
        return atolNo;
    }
    
    public void setAtolNo(String atolNo) {
        this.atolNo = atolNo;
    }
    
    public String getAtolHolderDetails() {
        return atolHolderDetails;
    }
    
    public void setAtolHolderDetails(String atolHolderDetails) {
        this.atolHolderDetails = atolHolderDetails;
    }
    
    public int getPaxQty() {
        return paxQty;
    }
    
    public void setPaxQty(int paxQty) {
        this.paxQty = paxQty;
    }
    
    public String getPnr() {
        return pnr;
    }
    
    public void setPnr(String pnr) {
        this.pnr = pnr;
    }
    
    public String getPaxNames() {
        return paxNames;
    }
    
    public void setPaxNames(String paxNames) {
        this.paxNames = paxNames;
    }
   
    
    public String getIssuedate() {
        return issuedate;
    }
    
    public void setIssuedate(String issuedate) {
        this.issuedate = issuedate;
    }
    
    public String getTotalCost() {
        return totalCost;
    }
    
    public void setTotalCost(String totalCost) {
        this.totalCost = totalCost;
    }
    
    public String getSegmentSummery() {
        return segmentSummery;
    }
    
    public void setSegmentSummery(String segmentSummery) {
        this.segmentSummery = segmentSummery;
    }

    public void setSegments(String segments) {
        this.segments = segments;
    }

    public String getSegments() {
        return segments;
    }
    
}
