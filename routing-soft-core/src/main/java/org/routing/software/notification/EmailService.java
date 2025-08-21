package org.routing.software.notification;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

@ApplicationScoped
public class EmailService {
    //TODO put env files into config file
    private final String smtpHost = "smtp.gmail.com";
    private final String smtpPort = "587";
    private final String username = "moraitis.al94@gmail.com";
    private final String password = "noug iuae bcpz dmth"; // 16-char Gmail App Password

    public void sendConfirmationEmail(String toEmail, String userName, String confirmLink) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort);

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject("Confirm Your Registration");
        message.setText("Hi " + userName + ",\n\nPlease confirm your registration by clicking the link below:\n" + confirmLink);

        Transport.send(message);
    }


}
