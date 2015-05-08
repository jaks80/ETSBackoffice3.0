package com.ets.fe.acdoc_o.model.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public class OInvoiceReportFactroyBean {

    private static List<InvoiceReportOther> invoices = new ArrayList<>();

    public static Collection getBeanCollection() {
        return invoices;
    }

    public static void setInvoices(List<InvoiceReportOther> aInvoices) {
        invoices = aInvoices;
    }

}
