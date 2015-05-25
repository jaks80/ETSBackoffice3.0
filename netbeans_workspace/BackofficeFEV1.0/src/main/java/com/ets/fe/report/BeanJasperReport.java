package com.ets.fe.report;

import com.ets.fe.report.gui.ReportViewerFrame;
import com.ets.fe.util.*;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.swing.JRViewer;

/**
 *
 * @author Yusuf
 */
public class BeanJasperReport {

    //public static final String domain = APIConfig.get("ws.domain");
    private String recepeintAddress;
    private String subject;
    private String body;
    private String refference;

    public BeanJasperReport() {

    }

    /**
     * This constructor is for email
     *
     * @param recepeintAddress
     * @param subject
     * @param body
     * @param refference
     */
    public BeanJasperReport(String recepeintAddress, String subject, String body, String refference) {
        this.recepeintAddress = recepeintAddress;
        this.subject = subject;
        this.body = body;
        this.refference = refference;
    }

    public void invoice(Collection<?> beanCollection, Enums.SaleType sale_type, String actionType) {
        InputStream template = null;

        if (sale_type.equals(Enums.SaleType.TKTSALES)) {
            template = BeanJasperReport.class.getResourceAsStream("/Report/tinvoice/TicketingInvoice.jasper");
        } else if (sale_type.equals(Enums.SaleType.TKTPURCHASE)) {

        } else if (sale_type.equals(Enums.SaleType.OTHERSALES)) {
            template = BeanJasperReport.class.getResourceAsStream("/Report/oinvoice/OtherInvoice.jasper");
        }

        JasperPrint jasperPrint = prepareReport(template, beanCollection);
        takeAction(actionType, jasperPrint);
    }

    public void atolFront(Collection<?> beanCollection, String actionType) {

        InputStream template = BeanJasperReport.class.getResourceAsStream("/Report/pnr/ATOLCertFront.jasper");
        JasperPrint jasperPrint = prepareReport(template, beanCollection);
        takeAction(actionType, jasperPrint);
        //return jasperPrint;
    }

    public JasperPrint productivityReport(Collection<?> beanCollection, String actionType) {

        InputStream template = BeanJasperReport.class.getResourceAsStream("/Report/productivity/Productivity.jasper");

        JasperPrint jasperPrint = prepareReport(template, beanCollection);
        return jasperPrint;
    }

    public void accountStatement(Collection<?> beanCollection, String actionType) {

        InputStream template = BeanJasperReport.class.getResourceAsStream("/Report/statement/AccountsStatement.jasper");

        JasperPrint jasperPrint = prepareReport(template, beanCollection);
        takeAction(actionType, jasperPrint);
    }

    public JRViewer invoiceReport(Collection<?> beanCollection, Enums.SaleType saletype, String actionType) {

        InputStream template = null;
        if (saletype.equals(Enums.SaleType.OTHERSALES)) {
            template = BeanJasperReport.class.getResourceAsStream("/Report/oinvoice/OAcDocumentReport.jasper");
        } else {
            template = BeanJasperReport.class.getResourceAsStream("/Report/tinvoice/TAcDocumentReport.jasper");
        }

        JasperPrint jasperPrint = prepareReport(template, beanCollection);
        if ("VIEW".equals(actionType)) {
            return viewReport(jasperPrint);
        } else {
            takeAction(actionType, jasperPrint);
        }

        return null;
    }

    public JRViewer transactionReceipt(Collection<?> beanCollection, Enums.SaleType sale_type, String actionType) {

        InputStream template = null;

        if (sale_type.equals(Enums.SaleType.OTHERSALES)) {
            template = BeanJasperReport.class.getResourceAsStream("/Report/payment/OtherTransReceipt.jasper");
        } else {
            template = BeanJasperReport.class.getResourceAsStream("/Report/payment/TicketingTransReceipt.jasper");
        }

        JasperPrint jasperPrint = prepareReport(template, beanCollection);
        if ("VIEW".equals(actionType)) {
            return viewReport(jasperPrint);
        } else {
            takeAction(actionType, jasperPrint);
        }

        return null;
    }

    private void takeAction(String actionType, JasperPrint jasperPrint) {
        if ("VIEW".equals(actionType)) {
            viewReportFrame(jasperPrint);
        } else if ("EMAIL".equals(actionType)) {
            emailReport(this.recepeintAddress, this.subject, this.body, this.refference, jasperPrint);
        } else if ("PRINT".equals(actionType)) {
            printReport(jasperPrint);
        }
    }

    public JRViewer viewReport(JasperPrint jasperPrint) {
        JRViewer viewer = new JRViewer(jasperPrint);
        return viewer;
    }

    private void viewReportFrame(JasperPrint jasperPrint) {
        ReportViewerFrame rptViewer = new ReportViewerFrame();
        rptViewer.viewReport(jasperPrint, "Report Viewer");
    }

    private void printReport(JasperPrint jasperPrint) {
        try {
            JasperPrintManager.printReport(jasperPrint, true);
        } catch (JRException ex) {
            ex.printStackTrace();
        }
    }

    private void emailReport(String recipientAddress, String subject, String body,
            String attachmentName, JasperPrint jasperPrint) {

        try {
            byte[] attachment = JasperExportManager.exportReportToPdf(jasperPrint);
            EmailService emailService = new EmailService();
            emailService.SendMail(recipientAddress, subject, body, attachment, attachmentName);
        } catch (JRException ex) {
            ex.printStackTrace();
        }
    }

    private JasperPrint prepareReport(InputStream template, Collection<?> beanCollection) {

        JasperPrint jasperPrint = null;
        try {
            Map parameters = new HashMap();
            parameters.put("ReportTitle", "Address Report");
            parameters.put("DataFile", "PaymentFactoryBean.java - Bean Collection");
            jasperPrint = JasperFillManager.fillReport(template, parameters,
                    new JRBeanCollectionDataSource(beanCollection));

        } catch (JRException ex) {
            ex.printStackTrace();
        }

        return jasperPrint;
    }
}
