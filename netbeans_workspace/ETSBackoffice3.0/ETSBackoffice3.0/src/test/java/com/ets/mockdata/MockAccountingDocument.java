package com.ets.mockdata;

import com.ets.accountingdoc.domain.TicketingSalesAcDoc;
import com.ets.pnr.domain.Itinerary;
import com.ets.pnr.domain.Pnr;
import com.ets.pnr.domain.Ticket;
import com.ets.util.Enums;
import java.util.Set;

/**
 *
 * @author Yusuf
 */
public class MockAccountingDocument {

    private TicketingSalesAcDoc invoice ;
    private TicketingSalesAcDoc payment;
    private TicketingSalesAcDoc creditNote;
    private TicketingSalesAcDoc debitNote;
    private TicketingSalesAcDoc refund;

    public MockAccountingDocument(){
     this.invoice = createMockInvoice();
    }

    private TicketingSalesAcDoc createMockInvoice() {

        TicketingSalesAcDoc invoice = new TicketingSalesAcDoc();
        invoice.setAcDoctype(Enums.AcDocType.INVOICE);        
        invoice.setIsArchived(0);
        invoice.setTerms("Net Monthly");
        
        MockData mockData = new MockData();
        
        Pnr issuedPnr = mockData.getMockPnr();
        Set<Ticket> issuedTickets = mockData.getMockTTPIssuedTickets();
        
        for(Ticket t: issuedTickets){
         t.setTicketingSalesAcDoc(invoice);
        }
        invoice.setTickets(issuedTickets);
        invoice.setPnr(issuedPnr);
        
        return invoice;
    }
}
