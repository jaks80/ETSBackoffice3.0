package etsbackoffice.report;

import etsbackoffice.businesslogic.AccountingDocBo;
import etsbackoffice.businesslogic.AuthenticationBo;
import etsbackoffice.businesslogic.EmailService;
import etsbackoffice.domain.AccountingDocument;
import etsbackoffice.businesslogic.OAccountingDocBo;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;

/**
 *
 * @author Yusuf
 */
public class BackofficeReporting implements Serializable {

    public static JasperDesign jasperDesign;
    public static JasperPrint jasperPrint;
    public static JasperReport jasperReport;
    public static String reportTemplateUrl = "etsbackoffice.report.report1.jrxml";
    OutputStream outputStream;
    ByteArrayOutputStream baos;
    EmailService emailReport;

    public BackofficeReporting() {
    }

    public void viewTicketingReport(List<Object> ticketingObjects) {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("tktId", 1);

        try {
            InputStream invMain = getClass().getResourceAsStream("/etsbackoffice/report/rptTicketingReport.jasper");

            jasperPrint =
                    JasperFillManager.fillReport(invMain, parameters, new JRBeanCollectionDataSource(ticketingObjects));

            FrameReportViewer rptViewer = new FrameReportViewer();
            rptViewer.viewReport(jasperPrint, "Ticketing Report");

        } catch (JRException e) {
            JOptionPane.showMessageDialog(null, e, "Exception in Reporting", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void viewSegmentReport(List<Object> ticketingObjects) {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("segId", 1);

        try {
            InputStream invMain = getClass().getResourceAsStream("/etsbackoffice/report/rptSegmentReport.jasper");

            jasperPrint =
                    JasperFillManager.fillReport(invMain, parameters, new JRBeanCollectionDataSource(ticketingObjects));

            FrameReportViewer rptViewer = new FrameReportViewer();
            rptViewer.viewReport(jasperPrint, "Segment Report");

        } catch (JRException e) {
            JOptionPane.showMessageDialog(null, e, "Exception in Reporting", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void printAcDoc(List<Object> aAcDocObjects) {
        HashMap parameters = new HashMap();
        parameters.put("acDocId", 1);

        InputStream invMain = getClass().getResourceAsStream("/etsbackoffice/report/acDoc.jasper");
        try {
            jasperPrint =
                    JasperFillManager.fillReport(invMain, parameters, new JRBeanCollectionDataSource(aAcDocObjects));
            JasperPrintManager.printReport(jasperPrint, true);
        } catch (JRException e) {
            //System.out.println("Exception in reporting: " + e);
            JOptionPane.showMessageDialog(null, e, "Exception in Reporting", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void fileAcDoc(List<Object> acDocObjects) {
        baos = new ByteArrayOutputStream();
        HashMap parameters = new HashMap();
        parameters.put("acDocId", 1);
        AccountingDocument acdoc = (AccountingDocument) acDocObjects.get(0);
        String invRef = acdoc.getAcDocRefString();

        try {
            InputStream invMain = getClass().getResourceAsStream("/etsbackoffice/report/acDoc.jasper");
            jasperPrint =
                    JasperFillManager.fillReport(invMain, parameters, new JRBeanCollectionDataSource(acDocObjects));
            JasperExportManager.exportReportToPdfFile(jasperPrint, "c:/TicketingInvoice/" + invRef + ".pdf");

        } catch (JRException e) {
            //System.out.println("Exception in reporting: " + e);
            JOptionPane.showMessageDialog(null, e, "Exception in Reporting", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void viewAcDoc(List<Object> acDocObjects) {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("acDocId", 1);

        try {
            InputStream invMain = getClass().getResourceAsStream("/etsbackoffice/report/acDoc.jasper");

            jasperPrint =
                    JasperFillManager.fillReport(invMain, parameters, new JRBeanCollectionDataSource(acDocObjects));

            FrameReportViewer rptViewer = new FrameReportViewer();
            rptViewer.viewReport(jasperPrint, "Accounting Document");

        } catch (JRException e) {
            //System.out.println("Exception in reporting: " + e);
            JOptionPane.showMessageDialog(null, e, "Exception in Reporting", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void emailAcDoc(String recipientAddress, List<Object> acDocObjects) {

        AccountingDocBo acDocBo = (AccountingDocBo) acDocObjects.get(0);
        emailReport = new EmailService();
        String subject = "";
        String body = "";
        String invRef = acDocBo.getAccountingDocument().getAcDocRefString();
        HashMap parameters = new HashMap();
        parameters.put("acDocId", 1);
        try {

            InputStream invMain = getClass().getResourceAsStream("/etsbackoffice/report/acDoc.jasper");
            jasperPrint = JasperFillManager.fillReport(invMain, parameters, new JRBeanCollectionDataSource(acDocObjects));
            byte[] report = JasperExportManager.exportReportToPdf(jasperPrint);

            subject = "Invoice from: " + AuthenticationBo.getmAgent().getName();
            body = "You have received an invoice from " + AuthenticationBo.getmAgent().getName();

            emailReport.SendMail(recipientAddress, subject, body, report, invRef);
        } catch (JRException ex) {
            //Logger.getLogger(RptAccountingDocument.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, ex, "Exception in Reporting", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void viewAcDocReport(List<Object> acDocObjects) {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("acDocId", 1);

        try {
            InputStream invMain = getClass().getResourceAsStream("/etsbackoffice/report/rptOutstandingAcDoc.jasper");

            jasperPrint =
                    JasperFillManager.fillReport(invMain, parameters, new JRBeanCollectionDataSource(acDocObjects));

            FrameReportViewer rptViewer = new FrameReportViewer();
            rptViewer.viewReport(jasperPrint, "Accounting Document");

        } catch (JRException e) {
            //System.out.println("Exception in reporting: " + e);
            JOptionPane.showMessageDialog(null, e, "Exception in Reporting", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void viewAcDocHistoryReport(List<Object> acDocObjects) {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("acDocId", 1);

        try {
            InputStream invMain = getClass().getResourceAsStream("/etsbackoffice/report/rptAcDocHistory.jasper");

            jasperPrint =
                    JasperFillManager.fillReport(invMain, parameters, new JRBeanCollectionDataSource(acDocObjects));

            FrameReportViewer rptViewer = new FrameReportViewer();
            rptViewer.viewReport(jasperPrint, "Accounting Document");

        } catch (JRException e) {
            //System.out.println("Exception in reporting: " + e);
            JOptionPane.showMessageDialog(null, e, "Exception in Reporting", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void printAcDocHistoryReport(List<Object> acDocObjects) {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("acDocId", 1);

        try {
            InputStream invMain = getClass().getResourceAsStream("/etsbackoffice/report/rptAcDocHistory.jasper");

            jasperPrint =
                    JasperFillManager.fillReport(invMain, parameters, new JRBeanCollectionDataSource(acDocObjects));

            JasperPrintManager.printReport(jasperPrint, true);

        } catch (JRException e) {
            //System.out.println("Exception in reporting: " + e);
            JOptionPane.showMessageDialog(null, e, "Exception in Reporting", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void emailAcDocHistoryReport(String recipientAddress, String subject, String body, List<Object> acDocObjects) {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("acDocId", 1);

        try {
            InputStream invMain = getClass().getResourceAsStream("/etsbackoffice/report/rptAcDocHistory.jasper");

            jasperPrint =
                    JasperFillManager.fillReport(invMain, parameters, new JRBeanCollectionDataSource(acDocObjects));

            byte[] report = JasperExportManager.exportReportToPdf(jasperPrint);
            emailReport = new EmailService();
            emailReport.SendMail(recipientAddress, subject, body, report, "Invoices");

        } catch (JRException e) {
            //System.out.println("Exception in reporting: " + e);
            JOptionPane.showMessageDialog(null, e, "Exception in Reporting", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void printAcDocReport(List<Object> acDocObjects) {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("acDocId", 1);

        try {
            InputStream invMain = getClass().getResourceAsStream("/etsbackoffice/report/rptOutstandingAcDoc.jasper");

            jasperPrint =
                    JasperFillManager.fillReport(invMain, parameters, new JRBeanCollectionDataSource(acDocObjects));

            JasperPrintManager.printReport(jasperPrint, true);

        } catch (JRException e) {
            //System.out.println("Exception in reporting: " + e);
            JOptionPane.showMessageDialog(null, e, "Exception in Reporting", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void emailAcDocReport(String recipientAddress, String subject, String body, List<Object> acDocObjects) {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("acDocId", 1);

        try {
            InputStream invMain = getClass().getResourceAsStream("/etsbackoffice/report/rptOutstandingAcDoc.jasper");

            jasperPrint =
                    JasperFillManager.fillReport(invMain, parameters, new JRBeanCollectionDataSource(acDocObjects));

            byte[] report = JasperExportManager.exportReportToPdf(jasperPrint);
            emailReport = new EmailService();
            emailReport.SendMail(recipientAddress, subject, body, report, "Invoices");
        } catch (JRException e) {
            //System.out.println("Exception in reporting: " + e);
            JOptionPane.showMessageDialog(null, e, "Exception in Reporting", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void viewOAcDoc(List<Object> acDocObjects) {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("acDocId", 1);

        try {
            InputStream invMain = getClass().getResourceAsStream("/etsbackoffice/report/oAcDoc.jasper");

            jasperPrint =
                    JasperFillManager.fillReport(invMain, parameters, new JRBeanCollectionDataSource(acDocObjects));

            FrameReportViewer rptViewer = new FrameReportViewer();
            rptViewer.viewReport(jasperPrint, "Accounting Document");

        } catch (JRException e) {
            //System.out.println("Exception in reporting: " + e);
            JOptionPane.showMessageDialog(null, e, "Exception in Reporting", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void printOAcDoc(List<Object> aAcDocObjects) {
        HashMap parameters = new HashMap();
        parameters.put("acDocId", 1);

        InputStream invMain = getClass().getResourceAsStream("/etsbackoffice/report/oAcDoc.jasper");
        try {
            jasperPrint =
                    JasperFillManager.fillReport(invMain, parameters, new JRBeanCollectionDataSource(aAcDocObjects));
            JasperPrintManager.printReport(jasperPrint, true);
        } catch (JRException e) {
            //System.out.println("Exception in reporting: " + e);
            JOptionPane.showMessageDialog(null, e, "Exception in Reporting", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void emailOAcDoc(String recipientAddress, List<Object> acDocObjects) {
        //OAccountingDocument acdoc = (OAccountingDocument) acDocObjects.get(0);
        OAccountingDocBo acDocBo = (OAccountingDocBo) acDocObjects.get(0);
        emailReport = new EmailService();
        String subject = "";
        String body = "";
        String invRef = acDocBo.getoAcDoc().getAcDocRefString();
        HashMap parameters = new HashMap();
        parameters.put("acDocId", 1);
        try {

            InputStream invMain = getClass().getResourceAsStream("/etsbackoffice/report/oAcDoc.jasper");
            jasperPrint = JasperFillManager.fillReport(invMain, parameters, new JRBeanCollectionDataSource(acDocObjects));
            byte[] report = JasperExportManager.exportReportToPdf(jasperPrint);

            subject = "Invoice from: " + AuthenticationBo.getmAgent().getName();
            body = "You have received an invoice from " + AuthenticationBo.getmAgent().getName();

            emailReport.SendMail(recipientAddress, subject, body, report, invRef);
        } catch (JRException ex) {
            //Logger.getLogger(RptAccountingDocument.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, ex, "Exception in Reporting", JOptionPane.WARNING_MESSAGE);
        }
    }

        public void viewOAcDocHistoryReport(List<Object> acDocObjects) {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("acDocId", 1);

        try {
            InputStream invMain = getClass().getResourceAsStream("/etsbackoffice/report/rptOtherOutstandingAcDoc.jasper");

            jasperPrint =
                    JasperFillManager.fillReport(invMain, parameters, new JRBeanCollectionDataSource(acDocObjects));

            FrameReportViewer rptViewer = new FrameReportViewer();
            rptViewer.viewReport(jasperPrint, "Accounting Document");

        } catch (JRException e) {
            //System.out.println("Exception in reporting: " + e);
            JOptionPane.showMessageDialog(null, e, "Exception in Reporting", JOptionPane.WARNING_MESSAGE);
        }
    }
        
    public void printOAcDocHistoryReport(List<Object> acDocObjects) {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("acDocId", 1);

        try {
            InputStream invMain = getClass().getResourceAsStream("/etsbackoffice/report/rptOtherOutstandingAcDoc.jasper");

            jasperPrint =
                    JasperFillManager.fillReport(invMain, parameters, new JRBeanCollectionDataSource(acDocObjects));

            JasperPrintManager.printReport(jasperPrint, true);

        } catch (JRException e) {            
            JOptionPane.showMessageDialog(null, e, "Exception in Reporting", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void emailOAcDocHistoryReport(String recipientAddress, String subject, String body, List<Object> acDocObjects) {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("acDocId", 1);

        try {
            InputStream invMain = getClass().getResourceAsStream("/etsbackoffice/report/rptOtherOutstandingAcDoc.jasper");

            jasperPrint =
                    JasperFillManager.fillReport(invMain, parameters, new JRBeanCollectionDataSource(acDocObjects));

            byte[] report = JasperExportManager.exportReportToPdf(jasperPrint);
            emailReport = new EmailService();
            emailReport.SendMail(recipientAddress, subject, body, report, "Invoices");

        } catch (JRException e) {            
            JOptionPane.showMessageDialog(null, e, "Exception in Reporting", JOptionPane.WARNING_MESSAGE);
        }
    }    
        
    public void printBatchTransaction(List<Object> batchObjects) {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("batchId", 1);

        try {
            InputStream invMain = getClass().getResourceAsStream("/etsbackoffice/report/rptBatchTReport.jasper");

            jasperPrint =
                    JasperFillManager.fillReport(invMain, parameters, new JRBeanCollectionDataSource(batchObjects));

            JasperPrintManager.printReport(jasperPrint, true);

        } catch (JRException e) {
            //System.out.println("Exception in reporting: " + e);
            JOptionPane.showMessageDialog(null, e, "Exception in Reporting", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void viewBatchTransaction(List<Object> batchObjects) {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("batchId", 1);

        try {
            InputStream invMain = getClass().getResourceAsStream("/etsbackoffice/report/rptBatchTReport.jasper");

            jasperPrint =
                    JasperFillManager.fillReport(invMain, parameters, new JRBeanCollectionDataSource(batchObjects));

            FrameReportViewer rptViewer = new FrameReportViewer();
            rptViewer.viewReport(jasperPrint, "Batch Transaction");

        } catch (JRException e) {
            System.out.println("Exception in reporting: " + e);
            JOptionPane.showMessageDialog(null, e, "Exception in Reporting", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void emailBatchTransaction(String recipientAddress, String subject, String body, List<Object> batchObjects) {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("batchId", 1);

        try {
            InputStream invMain = getClass().getResourceAsStream("/etsbackoffice/report/rptBatchTReport.jasper");

            jasperPrint =
                    JasperFillManager.fillReport(invMain, parameters, new JRBeanCollectionDataSource(batchObjects));

            byte[] report = JasperExportManager.exportReportToPdf(jasperPrint);
            emailReport = new EmailService();
            emailReport.SendMail(recipientAddress, subject, body, report, "Batch Transaction Receipt");

        } catch (JRException e) {
            System.out.println("Exception in reporting: " + e);
            JOptionPane.showMessageDialog(null, e, "Exception in Reporting", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void printBillingReport(List<Object> batchObjects) {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("batchId", 1);

        try {
            InputStream invMain = getClass().getResourceAsStream("/etsbackoffice/report/rptBillingReport.jasper");

            jasperPrint =
                    JasperFillManager.fillReport(invMain, parameters, new JRBeanCollectionDataSource(batchObjects));

            JasperPrintManager.printReport(jasperPrint, true);

        } catch (JRException e) {
            //System.out.println("Exception in reporting: " + e);
            JOptionPane.showMessageDialog(null, e, "Exception in Reporting", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void viewBillingReport(List<Object> batchObjects) {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("batchId", 1);

        try {
            InputStream invMain = getClass().getResourceAsStream("/etsbackoffice/report/rptBillingReport.jasper");

            jasperPrint =
                    JasperFillManager.fillReport(invMain, parameters, new JRBeanCollectionDataSource(batchObjects));

            FrameReportViewer rptViewer = new FrameReportViewer();
            rptViewer.viewReport(jasperPrint, "Billing Report");

        } catch (JRException e) {
            System.out.println("Exception in reporting: " + e);
            JOptionPane.showMessageDialog(null, e, "Exception in Reporting", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void emailBillingReport(String recipientAddress, String subject, String body, List<Object> batchObjects) {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("batchId", 1);

        try {
            InputStream invMain = getClass().getResourceAsStream("/etsbackoffice/report/rptBillingReport.jasper");

            jasperPrint =
                    JasperFillManager.fillReport(invMain, parameters, new JRBeanCollectionDataSource(batchObjects));

            byte[] report = JasperExportManager.exportReportToPdf(jasperPrint);
            emailReport = new EmailService();
            emailReport.SendMail(recipientAddress, subject, body, report, "Billing Receipt");

        } catch (JRException e) {
            System.out.println("Exception in reporting: " + e);
            JOptionPane.showMessageDialog(null, e, "Exception in Reporting", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void viewClientAcStatement(List<Object> statementObjects) {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("lineId", 1);

        try {
            InputStream invMain = getClass().getResourceAsStream("/etsbackoffice/report/rptClientAcStatement.jasper");

            jasperPrint =
                    JasperFillManager.fillReport(invMain, parameters, new JRBeanCollectionDataSource(statementObjects));

            FrameReportViewer rptViewer = new FrameReportViewer();
            rptViewer.viewReport(jasperPrint, "Client Accounts Statement");

        } catch (JRException e) {
            System.out.println("Exception in reporting: " + e);
            JOptionPane.showMessageDialog(null, e, "Exception in Reporting", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void emailClientAcStatement(String recipientAddress, String subject, String body, List<Object> batchObjects) {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("lineId", 1);

        try {
            InputStream invMain = getClass().getResourceAsStream("/etsbackoffice/report/rptClientAcStatement.jasper");

            jasperPrint =
                    JasperFillManager.fillReport(invMain, parameters, new JRBeanCollectionDataSource(batchObjects));

            byte[] report = JasperExportManager.exportReportToPdf(jasperPrint);
            emailReport = new EmailService();
            emailReport.SendMail(recipientAddress, subject, body, report, "Client Accounts Statement");

        } catch (JRException e) {
            System.out.println("Exception in reporting: " + e);
            JOptionPane.showMessageDialog(null, e, "Exception in Reporting", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void printClientAcStatement(List<Object> batchObjects) {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("lineId", 1);

        try {
            InputStream invMain = getClass().getResourceAsStream("/etsbackoffice/report/rptClientAcStatement.jasper");

            jasperPrint =
                    JasperFillManager.fillReport(invMain, parameters, new JRBeanCollectionDataSource(batchObjects));

            JasperPrintManager.printReport(jasperPrint, true);

        } catch (JRException e) {
            //System.out.println("Exception in reporting: " + e);
            JOptionPane.showMessageDialog(null, e, "Exception in Reporting", JOptionPane.WARNING_MESSAGE);
        }
    }
}
