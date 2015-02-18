package com.ets.fe.acdoc.bo;

import com.ets.fe.accounts.model.Payment;
import com.ets.fe.acdoc.model.TicketingSalesAcDoc;
import com.ets.fe.util.Enums;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Yusuf
 */
public class PaymentLogicTest {

    public PaymentLogicTest() {
    }

    @Before
    public void setUp() {
    }
    
    //@Test
    public void testProcessPayment() {
//        System.out.println("Batch Payment Processing Test");
//        BigDecimal amount = new BigDecimal("230.00");
//
//        Payment payment = new Payment();
//        Enums.PaymentType type = Enums.PaymentType.CASH;
//        String remark = "Batch payment 230.00";
//        List<TicketingSalesAcDoc> invoices = getMockInvoices();
//        
//        PaymentLogic instance = new PaymentLogic();        
//        assertEquals(new BigDecimal("240.00"),instance.calculateTotalDueAmount(invoices));
//        Payment result = instance.processBulkPayment(amount, invoices, remark, type);
//        
//        List<TicketingSalesAcDoc> paymentDocs = result.gettSalesAcDocuments();
//        assertEquals(4, paymentDocs.size());
//        
//        assertEquals(invoices.get(0).getReference(),paymentDocs.get(0).getReference());
//        assertEquals(invoices.get(0).getDocumentedAmount(),paymentDocs.get(0).getDocumentedAmount().abs());
//        assertEquals(new BigDecimal("0.00"),invoices.get(0).calculateDueAmount());
//        
//        assertEquals(invoices.get(1).getReference(),paymentDocs.get(1).getReference());
//        assertEquals(invoices.get(1).getDocumentedAmount(),paymentDocs.get(1).getDocumentedAmount().abs());
//        assertEquals(new BigDecimal("0.00"),invoices.get(1).calculateDueAmount());
//        
//        assertEquals(invoices.get(2).getReference(),paymentDocs.get(2).getReference());
//        assertEquals(invoices.get(2).getDocumentedAmount(),paymentDocs.get(2).getDocumentedAmount().abs());
//        assertEquals(new BigDecimal("0.00"),invoices.get(2).calculateDueAmount());
//        
//        assertEquals(invoices.get(3).getReference(),paymentDocs.get(3).getReference());
//        assertEquals(new BigDecimal("20.00"),paymentDocs.get(3).getDocumentedAmount());
//        assertEquals(new BigDecimal("10.00"),invoices.get(3).calculateDueAmount());
//        
//    }
//
//    //@Test
//    public void testProcessSinglePayment() {
//        System.out.println("Single Payment Processing Test");
//       
//        Payment payment = new Payment();
//        Enums.PaymentType type = Enums.PaymentType.CASH;
//        String remark = "Cash payment 20.00";
//        List<TicketingSalesAcDoc> invoices = new ArrayList<>();
//        TicketingSalesAcDoc doc1 = new TicketingSalesAcDoc();
//        doc1.setId(new Long(1));
//        doc1.setReference(new Long(1));
//        doc1.setType(Enums.AcDocType.INVOICE);
//        doc1.setDocumentedAmount(new BigDecimal("50.00"));
//        invoices.add(doc1);                
//        
//        PaymentLogic instance = new PaymentLogic();                
//        Payment result = instance.processBulkPayment(new BigDecimal("20.00"), invoices, remark, type);
//        
//        List<TicketingSalesAcDoc> paymentDocs = result.gettSalesAcDocuments();
//        assertEquals(1, paymentDocs.size());
//        
//        assertEquals(invoices.get(0).getReference(),paymentDocs.get(0).getReference());        
//        assertEquals(new BigDecimal("30.00"),invoices.get(0).calculateDueAmount());
//        
//        result = instance.processBulkPayment(new BigDecimal("15.00"), invoices, remark, type);
//        paymentDocs = result.gettSalesAcDocuments();
//        assertEquals(1, paymentDocs.size());
//        
//        assertEquals(invoices.get(0).getReference(),paymentDocs.get(0).getReference());        
//        //assertEquals(new BigDecimal("15.00"),invoices.get(0).calculateDueAmount());//fail test
//    }
//    
//    private List<TicketingSalesAcDoc> getMockInvoices() {
//
//        List<TicketingSalesAcDoc> invoices = new ArrayList<>();
//
//        TicketingSalesAcDoc doc1 = new TicketingSalesAcDoc();
//        doc1.setId(new Long(1));
//        doc1.setReference(new Long(1));
//        doc1.setType(Enums.AcDocType.INVOICE);
//        doc1.setDocumentedAmount(new BigDecimal("50.00"));
//        invoices.add(doc1);
//
//        TicketingSalesAcDoc doc2 = new TicketingSalesAcDoc();
//        doc2.setId(new Long(2));
//        doc2.setReference(new Long(2));
//        doc2.setType(Enums.AcDocType.INVOICE);
//        doc2.setDocumentedAmount(new BigDecimal("60.00"));
//        invoices.add(doc2);
//
//        TicketingSalesAcDoc doc3 = new TicketingSalesAcDoc();
//        doc3.setId(new Long(3));
//        doc3.setReference(new Long(3));
//        doc3.setType(Enums.AcDocType.INVOICE);
//        doc3.setDocumentedAmount(new BigDecimal("100.00"));
//        invoices.add(doc3);
//
//        TicketingSalesAcDoc doc4 = new TicketingSalesAcDoc();
//        doc4.setId(new Long(4));
//        doc4.setReference(new Long(4));
//        doc4.setType(Enums.AcDocType.INVOICE);
//        doc4.setDocumentedAmount(new BigDecimal("30.00"));
//        invoices.add(doc4);
//
//        return invoices;
//    }
}
}
