package com.ets.fe.pnr.logic;

import com.ets.fe.pnr.model.ATOLCertificate;
import com.ets.fe.pnr.model.Ticket;
import com.ets.fe.pnr.task.AtolCertificateTask;
import com.ets.fe.report.BeanJasperReport;
import com.ets.fe.util.Enums;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author Yusuf
 */
public class AtolLogic implements PropertyChangeListener {

    private AtolCertificateTask atolCertificateTask;

    public static boolean validForATOL(List<Ticket> tickets) {
        boolean valid = false;
        boolean issued = false;

        BigDecimal totalAtol = new BigDecimal("0.00");

        for (Ticket t : tickets) {

            totalAtol = totalAtol.add(t.getAtolChg());

            if (t.getTktStatus().equals(Enums.TicketStatus.ISSUE)
                    || t.getTktStatus().equals(Enums.TicketStatus.REISSUE)) {
                issued = true;
            }
        }
        if (totalAtol.compareTo(new BigDecimal("0.00")) > 0 && issued) {
            valid = true;
        }
        return valid;
    }

    public void printCertificate(Long pnrid) {
        atolCertificateTask = new AtolCertificateTask(pnrid, new java.util.Date());
        atolCertificateTask.addPropertyChangeListener(this);
        atolCertificateTask.execute();
    }

    private void report(ATOLCertificate cert) {
        BeanJasperReport jasperreport = new BeanJasperReport();
        List<ATOLCertificate> list = new ArrayList<>();
        list.add(cert);
        jasperreport.atolFront(list, "PRINT");
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress".equals(evt.getPropertyName())) {
            int progress = (Integer) evt.getNewValue();
            if (progress == 100) {
                try {
                    ATOLCertificate cert = atolCertificateTask.get();
                    report(cert);

                } catch (InterruptedException | ExecutionException ex) {

                } finally {

                }
            }
        }
    }

}
