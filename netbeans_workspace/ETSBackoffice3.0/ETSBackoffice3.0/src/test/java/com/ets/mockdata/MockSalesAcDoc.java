package com.ets.mockdata;

import com.ets.accountingdoc.domain.Payment;
import com.ets.accountingdoc.domain.TicketingSalesAcDoc;
import com.ets.util.Enums;
import java.math.BigDecimal;

/**
 *
 * @author Yusuf
 */
public class MockSalesAcDoc {

    private TicketingSalesAcDoc invoice ;
    private Payment payment;
    private TicketingSalesAcDoc creditNote;
    private TicketingSalesAcDoc debitNote;
    private TicketingSalesAcDoc refund;

    public MockSalesAcDoc(){
     this.invoice = createMockInvoice();
     this.payment = createMockPayment();
     this.creditNote = createMockCreditNote();
    }

    private TicketingSalesAcDoc createMockInvoice() {

        TicketingSalesAcDoc invoice = new TicketingSalesAcDoc();
        invoice.setAcDoctype(Enums.AcDocType.INVOICE);
        invoice.setIsArchived(0);
        invoice.setTerms("Net Monthly");                   

        return invoice;
    }
    
    private TicketingSalesAcDoc createMockCreditNote() {

        TicketingSalesAcDoc cnote = new TicketingSalesAcDoc();
        cnote.setAcDoctype(Enums.AcDocType.CREDITMEMO);
        cnote.setIsArchived(0);        

        return cnote;
    }
        
    private Payment createMockPayment() {

        Payment payment = new Payment();
        payment.setRemark("Cash Payment by Abdur Rahman");
        payment.setPaymentType(Enums.PaymentType.CASH);

        TicketingSalesAcDoc paymentDoc = new TicketingSalesAcDoc();
        paymentDoc.setAcDoctype(Enums.AcDocType.PAYMENT);
        paymentDoc.setIsArchived(0);
        paymentDoc.setDocumentedAmount(new BigDecimal("200.00"));       

        return payment;
    }

    public TicketingSalesAcDoc getInvoice() {
        return invoice;
    }

    public Payment getPayment() {
        return payment;
    }

    public TicketingSalesAcDoc getCreditNote() {
        return creditNote;
    }

    public TicketingSalesAcDoc getDebitNote() {
        return debitNote;
    }

    public TicketingSalesAcDoc getRefund() {
        return refund;
    }

}
