/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etsbackoffice.report;

import etsbackoffice.domain.AccountingDocument;
import etsbackoffice.domain.AccountingDocumentLine;
import etsbackoffice.domain.PurchaseAccountingDocument;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author Yusuf
 */
public class RevenueReport {

    private AccountingDocument sAcDoc;

    public RevenueReport(AccountingDocument sAcDoc) {
        this.sAcDoc = sAcDoc;
    }

    public AccountingDocument getsAcDoc() {
        return sAcDoc;
    }

    public void setsAcDoc(AccountingDocument sAcDoc) {
        this.sAcDoc = sAcDoc;
    }

    public BigDecimal tSFare() {
        return this.sAcDoc.getTktdSubTotal();
    }
    
    public BigDecimal tSOtherService() {
        return this.sAcDoc.getOtherServiceSubTotal();
    }
        
    public BigDecimal tSOCharge() {
        return this.sAcDoc.getAdditionalServiceSubTotal();
    }

    public BigDecimal tOtherSelfCost() {
      BigDecimal oSelfCost = new BigDecimal("0.00");
        for (AccountingDocumentLine l : this.sAcDoc.getAdditionalServiceLine()) {
            oSelfCost = oSelfCost.add(l.getCost());
        }
        return oSelfCost;
    }
        
    public BigDecimal sInvAmount() {
        return this.sAcDoc.getTotalDocumentedAmount();
    }

    public BigDecimal paymentReceived() {
        return this.sAcDoc.getTotalTransactionAmount();
    }

    public BigDecimal outstandingSBalance() {
        return this.sInvAmount().subtract(this.paymentReceived());
    }

    public BigDecimal tPFare() {
        BigDecimal pFare = new BigDecimal("0.00");

        for (PurchaseAccountingDocument pAcDoc : this.sAcDoc.getPurchaseAccountingDocuments()) {
            pFare = pFare.add(pAcDoc.getSubTotal());
        }
        return pFare;
    }

    public BigDecimal tPOCost() {
        BigDecimal oCost = new BigDecimal("0.00");

        for (PurchaseAccountingDocument pAcDoc : this.sAcDoc.getPurchaseAccountingDocuments()) {
            oCost = oCost.add(pAcDoc.getTotalDocumentedAmount().subtract(pAcDoc.getSubTotal()));
        }
        return oCost;
    }

    public BigDecimal pInvAmount() {
        BigDecimal pCost = new BigDecimal("0.00");

        for (PurchaseAccountingDocument pAcDoc : this.sAcDoc.getPurchaseAccountingDocuments()) {
            pCost = pCost.add(pAcDoc.getTotalDocumentedAmount());
        }
        return pCost;
    }

    public BigDecimal billPaid() {
        BigDecimal billPaid = new BigDecimal("0.00");
        for (PurchaseAccountingDocument pAcDoc : this.sAcDoc.getPurchaseAccountingDocuments()) {
            billPaid = billPaid.add(pAcDoc.getTotalPaid());
        }

        return billPaid;
    }

    public BigDecimal outstandingPBalance() {
        return this.pInvAmount().subtract(this.billPaid());
    }

    public BigDecimal oChgRevenue() {        
        return this.sAcDoc.getRevenueFormAService();
    }
    
    public BigDecimal tktdRevenue() {
        return this.sAcDoc.getTktdRevenue();
    }
    
    public BigDecimal oSRevenue(){
     return this.sAcDoc.getRevenueFromOService();
    }
    
    public BigDecimal revenueAdm(){
     return this.sAcDoc.getRevenueAdm().add(this.sAcDoc.getPRevenueAdm());
    }
    
    public BigDecimal etstimatedTotalRevenue() {      
        //if(this.sAcDoc.getAcDoctype()==1 || this.sAcDoc.getAcDoctype()==2){         
         return tktdRevenue().add(oChgRevenue()).add(oSRevenue()).add(revenueAdm());
        //}else{
        //return this.sAcDoc.getTotalDocumentedAmount();
        //}
    }

    public BigDecimal etstimatedRevenueEffectedByTrans() {        
        return (etstimatedTotalRevenue().subtract(outstandingSBalance())).add(outstandingPBalance());               
    }

    public String pnr() {
        return this.sAcDoc.getPnr().getGdsPNR();
    }

    public String career() {
        return this.sAcDoc.getPnr().getServicingCareer().getCode();
    }

    public int noOfTicket() {
        return this.sAcDoc.getTicketLine().size();
    }

    public String sAcDocRef() {
        return this.sAcDoc.getAcDocRefString();
    }

    public Date sAcDocDate() {
        return this.sAcDoc.getIssueDate();
    }
}
