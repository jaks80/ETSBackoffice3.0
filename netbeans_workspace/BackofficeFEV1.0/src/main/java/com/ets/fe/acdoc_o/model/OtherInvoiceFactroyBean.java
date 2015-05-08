package com.ets.fe.acdoc_o.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public class OtherInvoiceFactroyBean {
    
    private static List<OtherInvoiceModel> invoices = new ArrayList<>();

    public static Collection getBeanCollection() {
        return invoices;
    }

    public static void setInvoices(List<OtherInvoiceModel> aInvoices) {
        invoices = aInvoices;
    }
}
