package com.add.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;

@Service("emailService")
public class EmailService {

    private String smtpServer = "localhost";
    private String from = "systems@thefusionlabs.com";
    private List<String> recepients = new ArrayList<String>();

    public void addRecepients() {
        recepients.add("yusuf.shetu1@googlemail.com");
    }

    private Properties prepareProperties() {
        Properties props = new Properties();
        props.setProperty("mail.smtp.host", smtpServer);
        return props;
    }

    private MimeMessage prepareMessage(Session mailSession, String charset,
            String from, String subject,
            String HtmlMessage, String[] recipient) {

       // addRecepients();
        if (recipient == null) {
            recipient = this.recepients.toArray(new String[recepients.size()]);
        }

        MimeMessage message = null;
        try {
            message = new MimeMessage(mailSession);
            message.setFrom(new InternetAddress(from));
            message.setSubject(subject);
            for (int i = 0; i < recipient.length; i++) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient[i]));
            }
            message.setContent(HtmlMessage, "text/html; charset=\"" + charset + "\"");
        } catch (Exception ex) {
        }
        return message;
    }

    public boolean sendEmail(String subject, String HtmlMessage, String[] to) {
        Transport transport = null;
        boolean result = false;
        try {
            Properties props = prepareProperties();
            Session mailSession = Session.getDefaultInstance(props);
            transport = mailSession.getTransport("smtp");
            MimeMessage message = prepareMessage(mailSession, "UTF-8", from, subject, HtmlMessage, to);
            transport.connect();
            Transport.send(message);
            System.out.println("Email Sent...");
            result = true;
        } catch (Exception ex) {
            System.out.println("Exception sending email: " + ex);
        } finally {
            try {
                transport.close();
            } catch (MessagingException ex) {
                System.out.println("MessagingException: " + ex);
            }
        }
        return result;
    }
}