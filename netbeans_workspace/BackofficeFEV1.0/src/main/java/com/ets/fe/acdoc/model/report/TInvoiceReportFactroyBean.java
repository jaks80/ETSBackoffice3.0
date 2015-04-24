package com.ets.fe.acdoc.model.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public class TInvoiceReportFactroyBean {

    private static List<InvoiceReport> invoices = new ArrayList<>();

    public static Collection getBeanCollection() {
        return invoices;
    }

    public static void setInvoices(List<InvoiceReport> aInvoices) {
        invoices = aInvoices;
    }

}
