package com.ets.fe.report;

import com.ets.fe.APIConfig;
import com.ets.fe.report.gui.ReportViewerFrame;
import com.ets.fe.util.*;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.query.JRXPathQueryExecuterFactory;
import org.w3c.dom.Document;

/**
 *
 * @author Yusuf
 */
public class XMLJasperReport {

    //public static final String domain = APIConfig.get("ws.domain");

    private String recepeintAddress;
    private String subject;
    private String body;
    private String refference;

    public XMLJasperReport() {
    }

    /**
     * This constructor is for email
     *
     * @param recepeintAddress
     * @param subject
     * @param body
     * @param refference
     */
    public XMLJasperReport(String recepeintAddress, String subject, String body, String refference) {
        this.recepeintAddress = recepeintAddress;
        this.subject = subject;
        this.body = body;
        this.refference = refference;
    }
    public void reportInvoice(Long id, Enums.SaleType sale_type, String actionType) {

        String url = "";
        InputStream template = null;

        if (sale_type.equals(Enums.SaleType.TKTSALES)) {
            url = url + APIConfig.get("ws.tsacdoc.model") + id;
            template = XMLJasperReport.class.getResourceAsStream("/reports/MyReports/Invoice.jasper");
        } else if (sale_type.equals(Enums.SaleType.TKTPURCHASE)) {
            url = url + APIConfig.get("ws.tpacdoc.model") + id;
            template = XMLJasperReport.class.getResourceAsStream("/reports/MyReports/Invoice.jasper");
        } else if (sale_type.equals(Enums.SaleType.OTHERSALES)) {
            url = url + APIConfig.get("ws.osacdoc.model") + id;
            template = XMLJasperReport.class.getResourceAsStream("/reports/MyReports/os/OtherInvoice.jasper");
        }
        System.out.println("URL: " + url);

        JasperPrint jasperPrint = prepareReport(template, url);
        takeAction(actionType, jasperPrint);
    }

    public void takeAction(String actionType, JasperPrint jasperPrint) {
        if ("VIEW".equals(actionType)) {
            viewReport(jasperPrint);
        } else if ("EMAIL".equals(actionType)) {
            emailReport(this.recepeintAddress, this.subject, this.body, this.refference, jasperPrint);
        } else if ("PRINT".equals(actionType)) {
            printReport(jasperPrint);
        }
    }

    private void viewReport(JasperPrint jasperPrint) {
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

    private JasperPrint prepareReport(InputStream template, String url) {
        JasperPrint jasperPrint = null;
        try {
            Map<String, Object> params = new HashMap<>();
            params.put(JRXPathQueryExecuterFactory.PARAMETER_XML_DATA_DOCUMENT, remoteXMLToDocument(url));
            jasperPrint = JasperFillManager.fillReport(template, params);
            return jasperPrint;
        } catch (JRException ex) {
            ex.printStackTrace();
        }

        return jasperPrint;
    }
//
//    private Document remoteXMLToDocument(String path) {
//
//        URL url = null;
//        try {
//            url = new URL(path);
//        } catch (MalformedURLException ex) {
//            Logger.getLogger(MyJasperReport.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        Document document = null;
//        try {
//            document = JRXmlUtils.parse(url, false);
//        } catch (JRException ex) {
//            ex.printStackTrace();
//        }
//        System.out.println(document);
//        return document;
//    }

    public Document remoteXMLToDocument(String path) {
        Document document = null;
        try {
            StringReader reader = new StringReader(RestClientUtil.getXML(path));
            Source source = new StreamSource(reader);
            DOMResult result = new DOMResult();
            TransformerFactory.newInstance().newTransformer().transform(source, result);
            document = (Document) result.getNode();
        } catch (TransformerConfigurationException ex) {
            ex.printStackTrace();
        } catch (TransformerException ex) {
            ex.printStackTrace();
        }
        return document;
    }
}
