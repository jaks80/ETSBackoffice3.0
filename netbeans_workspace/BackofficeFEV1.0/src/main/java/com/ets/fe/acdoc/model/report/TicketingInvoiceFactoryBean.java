package com.ets.fe.acdoc.model.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public class TicketingInvoiceFactoryBean {
    
    private static List<InvoiceModel> invoices = new ArrayList<>();

    public static Collection getBeanCollection() {
        return invoices;
    }

    public static void setInvoices(List<InvoiceModel> aInvoices) {
        invoices = aInvoices;
    }

}
