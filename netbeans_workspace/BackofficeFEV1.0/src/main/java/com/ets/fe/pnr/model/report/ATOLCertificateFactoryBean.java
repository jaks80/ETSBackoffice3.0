package com.ets.fe.pnr.model.report;

import com.ets.fe.pnr.model.ATOLCertificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public class ATOLCertificateFactoryBean {

    private static List<ATOLCertificate> certificates = new ArrayList<>();

    public static Collection getBeanCollection() {
        return certificates;
    }

    public static void setCertificates(List<ATOLCertificate> aCertificates) {
        certificates = aCertificates;
    }
}
