package com.ets.fe.util;

import com.ets.fe.Application;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.swing.JOptionPane;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

/**
 *
 * @author Yusuf
 */
public class EmailService {

    private JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    private Properties properties = System.getProperties();
    
    private static final String SMTP_AUTH_USER = Application.getAppSettings().getEmail();
    private static final String SMTP_AUTH_PWD = Application.getAppSettings().getEmailPassword();
    private static final String SMTP_HOST = Application.getAppSettings().getEmailHost();
    private static final int SMTP_PORT = Application.getAppSettings().getPort();
    public static final String SMTP_AUTH = Application.getAppSettings().getSmtp_auth();
    public static final String SMTP_TLS_ENABLE = Application.getAppSettings().getStarttls_enable();

    public void SendMail(final String recipientAddress, final String subject,
            final String body, final byte[] attachment, final String attachmentName) {

        properties.put("mail.smtp.auth", SMTP_AUTH);
        properties.put("mail.smtp.starttls.enable", SMTP_TLS_ENABLE);
        mailSender.setHost(SMTP_HOST);
        mailSender.setPort(SMTP_PORT);
        mailSender.setUsername(SMTP_AUTH_USER);
        mailSender.setPassword(SMTP_AUTH_PWD);
        mailSender.setJavaMailProperties(properties);

        mailSender.send(new MimeMessagePreparator() {

            @Override
            public void prepare(MimeMessage mimeMessage) {
                MimeMessageHelper helper;
                try {
                    helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                    helper.setFrom(new InternetAddress(SMTP_AUTH_USER));
                    helper.setTo(new InternetAddress(recipientAddress));
                    helper.setSubject(subject);
                    helper.setText(body);
                    if(attachment!=null){
                     helper.addAttachment(attachmentName + ".pdf", new ByteArrayResource(attachment));
                    }
                } catch (MessagingException ex) {
                    System.out.println(ex);
                    JOptionPane.showMessageDialog(null, "Error sending email...!!!", "Email", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        JOptionPane.showMessageDialog(null, "Email Delivered...", "Email", JOptionPane.WARNING_MESSAGE);
    }
}
