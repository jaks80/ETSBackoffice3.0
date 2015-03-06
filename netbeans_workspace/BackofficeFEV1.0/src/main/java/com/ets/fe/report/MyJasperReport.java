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
public class MyJasperReport {

    //public static final String domain = APIConfig.get("ws.domain");

    private String recepeintAddress;
    private String subject;
    private String body;
    private String refference;

    public MyJasperReport() {
    }

    /**
     * This constructor is for email
     *
     * @param recepeintAddress
     * @param subject
     * @param body
     * @param refference
     */
    public MyJasperReport(String recepeintAddress, String subject, String body, String refference) {
        this.recepeintAddress = recepeintAddress;
        this.subject = subject;
        this.body = body;
        this.refference = refference;
    }

    /**
     *
     * @param saletype
     * @param doctype
     * @param clienttype
     * @param clientid
     * @param _dateFrom
     * @param _dateTo
     * @param actionType VIEW,EMAIL,PRINT
     */
    public void ticketingInvoiceReport(Enums.SaleType saletype, Enums.AcDocType doctype, Enums.ClientType clienttype,
            Long clientid, Date _dateFrom, Date _dateTo, String actionType) {

        InputStream template = template = MyJasperReport.class.getResourceAsStream("/reports/MyReports/TAcDocReport.jasper");;
        String url = "";
        String dateFrom = DateUtil.dateToString(_dateFrom, "ddMMMyyyy");
        String dateTo = DateUtil.dateToString(_dateTo, "ddMMMyyyy");

        if (saletype.equals(Enums.SaleType.TKTSALES)) {
            if (doctype != null) {
                url = url + APIConfig.get("ws.tsacdoc.report") + "?dateStart=" + dateFrom + "&dateEnd=" + dateTo + "&doctype=" + doctype;
            } else {
                url = url + APIConfig.get("ws.tsacdoc.history") + "?dateStart=" + dateFrom + "&dateEnd=" + dateTo;
            }
        } else if (saletype.equals(Enums.SaleType.TKTSALES)) {
            url = url + APIConfig.get("ws.tpacdoc.report") + "?dateStart=" + dateFrom + "&dateEnd=" + dateTo + "&doctype=" + doctype;
        }

        if (clienttype != null) {
            url = url + "&clienttype=" + clienttype;
        }
        if (clientid != null) {
            url = url + "&clientid=" + clientid;
        }
        
        JasperPrint jasperPrint = prepareReport(template, url);
        takeAction(actionType, jasperPrint);
    }

    public void otherInvoiceReport(Enums.AcDocType doctype, Enums.ClientType clienttype,
            Long clientid, Date _dateFrom, Date _dateTo, String actionType) {

        InputStream template = template = MyJasperReport.class.getResourceAsStream("/reports/MyReports/os/OtherAcDocReport.jasper");;
        String url = "";
        String dateFrom = DateUtil.dateToString(_dateFrom, "ddMMMyyyy");
        String dateTo = DateUtil.dateToString(_dateTo, "ddMMMyyyy");

        if (doctype != null) {
            url = url + APIConfig.get("ws.osacdoc.dueinvoices") + "?dateStart=" + dateFrom + "&dateEnd=" + dateTo + "&doctype=" + doctype;
        } else {
            url = url + APIConfig.get("ws.osacdoc.history") + "?dateStart=" + dateFrom + "&dateEnd=" + dateTo;
        }

        if (clienttype != null) {
            url = url + "&clienttype=" + clienttype;
        }
        if (clientid != null) {
            url = url + "&clientid=" + clientid;
        }

        System.out.println("URL: " + url);
        JasperPrint jasperPrint = prepareReport(template, url);
        takeAction(actionType, jasperPrint);
    }

    public void reportInvoice(Long id, Enums.SaleType sale_type, String actionType) {

        String url = "";
        InputStream template = null;

        if (sale_type.equals(Enums.SaleType.TKTSALES)) {
            url = url + APIConfig.get("ws.tsacdoc.model") + id;
            template = MyJasperReport.class.getResourceAsStream("/reports/MyReports/Invoice.jasper");
        } else if (sale_type.equals(Enums.SaleType.TKTPURCHASE)) {
            url = url + APIConfig.get("ws.tpacdoc.model") + id;
            template = MyJasperReport.class.getResourceAsStream("/reports/MyReports/Invoice.jasper");
        } else if (sale_type.equals(Enums.SaleType.OTHERSALES)) {
            url = url + APIConfig.get("ws.osacdoc.model") + id;
            template = MyJasperReport.class.getResourceAsStream("/reports/MyReports/os/OtherInvoice.jasper");
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
