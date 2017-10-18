package hu.giro;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.util.ResourceUtils;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class SimpleMailTests {

    private Session session;
    private String mailContentType = "html";
    private String mailEncoding = "UTF-8";

    @Test
    public void javaxMailTest() throws MessagingException {
        initMail();

        List<InternetAddress> toList = new ArrayList<InternetAddress>();

        sendMail("sender@test.hu",
                getAddressList("to1@mail.giro.hu", "to2@gmail.com"),
                getAddressList("cc1@mail.giro.hu", "cc2@otp.hu"),
                getAddressList("bcc1@mail.giro.hu", "bcc2@mail.giro.hu"),
                "Subject", "Levél");
    }

    private void initMail() {
        final String username = "Bankvaltas";
        final String password = "password";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", "localhost");
        props.put("mail.smtp.port", "2525");

        session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
    }


    public void sendMail(String from, InternetAddress[] to, InternetAddress[] tocc, InternetAddress[] toBcc, String subject, String text) throws MessagingException {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));

        message.addRecipients(Message.RecipientType.TO, to);
        message.setHeader("Content-Type", this.mailContentType + "; charset=\"" + this.mailEncoding + "\"");
        message.setSubject(subject);
        message.setText(text, "UTF-8", "html");
        if (null != tocc) {
            message.addRecipients(Message.RecipientType.CC, tocc);
        }
        if (null != toBcc) {
            message.addRecipients(Message.RecipientType.BCC, toBcc);
        }

        Transport.send(message);


    }

    @Test
    public void apacheMailTest() {
        try {
            Email email = new MultiPartEmail();
            //email.setDebug(true);
            email.setHostName("localhost");
            email.setSmtpPort(2525);
            email.setAuthentication("change", "pass");
            email.setFrom("sender@test.hu");
            email.setSubject("Test email");
            email.setCharset("UTF8");
            email.addTo("to1@mail.giro.hu", "to2@mail.giro.hu");
            email.addCc("cc1@mail.giro.hu", "cc2@mail.giro.hu");
            email.addBcc("bcc1@mail.giro.hu", "bcc2@mail.giro.hu");

            email.send();
        } catch (EmailException e) {
            e.printStackTrace();
        }
    }

    private InternetAddress[] getAddressList(String... addresses) {
        List<InternetAddress> list = new ArrayList<InternetAddress>();
        for (String mailAddress : addresses) {
            try {
                list.add(new InternetAddress(mailAddress));
            } catch (AddressException e) {
                e.printStackTrace();
            }
        }
        InternetAddress[] result = new InternetAddress[list.size()];
        list.toArray(result);
        return result;
    }
}
