package com.ets.fe.accounts.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public class PaymentFactoryBean {

    private static List<Payment> payments = new ArrayList<>();

    public static Collection getBeanCollection() {
        return payments;
    }

    public static void setPayments(List<Payment> aPayments) {
        payments = aPayments;
    }
}
