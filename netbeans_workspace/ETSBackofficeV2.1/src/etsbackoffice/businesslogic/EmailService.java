package etsbackoffice.businesslogic;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.swing.JOptionPane;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
/**
 *
 * @author Yusuf
 */
public class EmailService {

    private JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    private Properties props = System.getProperties();
    private final String mainAgtEmail;
    private String mainAgtEmailPWord;
    private String host;
    private int port;

    public EmailService() {
       this.props.put("mail.smtp.auth", "true");
       this.props.put("mail.smtp.starttls.enable", "true"); //Only for gmail
       this.mainAgtEmail = AuthenticationBo.getmAgent().getEmail();
       this.mainAgtEmailPWord = AuthenticationBo.getmAgent().getEmailPassword();
       this.host = AuthenticationBo.getmAgent().getEmailHost();
       this.port = AuthenticationBo.getmAgent().getPort();
       mailSender.setHost(this.host);
            mailSender.setPort(this.port);
            mailSender.setUsername(mainAgtEmail);
            mailSender.setPassword(mainAgtEmailPWord);
            mailSender.setJavaMailProperties(props);
    }

    public void SendMail(final String recipientAddress, final String subject,
            final String body, final byte[] attachment, final String attachmentName) {
        
        mailSender.send(new MimeMessagePreparator() {

            public void prepare(MimeMessage mimeMessage) {
                MimeMessageHelper helper;
                try {
                    helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                    helper.setFrom(new InternetAddress(mainAgtEmail));
                    helper.setTo(new InternetAddress(recipientAddress));
                    helper.setSubject(subject);
                    helper.setText(body);
                    helper.addAttachment(attachmentName + ".pdf", new ByteArrayResource(attachment));
                } catch (MessagingException ex) {
                    System.out.println(ex);
                    JOptionPane.showMessageDialog(null, "There was an error sending email...!!!", "Email", JOptionPane.WARNING_MESSAGE);
                    Logger.getLogger(EmailService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        JOptionPane.showMessageDialog(null, "Email Delivered...", "Email", JOptionPane.WARNING_MESSAGE);
    }
    
    public void SendMailWithBAR(final String recipientAddress, final String subject,
            final String body, final ByteArrayResource bout) {
        
        mailSender.send(new MimeMessagePreparator() {

            public void prepare(MimeMessage mimeMessage) {
                MimeMessageHelper helper;
                try {
                    helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                    helper.setFrom(new InternetAddress(mainAgtEmail));
                    helper.setTo(new InternetAddress(recipientAddress));
                    helper.setSubject(subject);
                    helper.setText(body);
                    helper.addAttachment("bkup_air.zip", bout);
                } catch (MessagingException ex) {
                    System.out.println(ex);
                    JOptionPane.showMessageDialog(null, "There was an error sending email...!!!", "Email", JOptionPane.WARNING_MESSAGE);
                    Logger.getLogger(EmailService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        JOptionPane.showMessageDialog(null, "Email Delivered...", "Email", JOptionPane.WARNING_MESSAGE);
    }
}
