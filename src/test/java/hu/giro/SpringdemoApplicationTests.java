package hu.giro;

import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.mail.*;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.junit4.SpringRunner;
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
import javax.mail.internet.InternetAddress;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringdemoApplicationTests {
    @Autowired
    private JavaMailSender javaMailSender;

    private static Session session;
    private String mailContentType = "html";
    private String mailEncoding = "UTF-8";


    @BeforeClass
    public static void init(){
        initMail();
    }
    @Test
    public void javaxMailTest() throws MessagingException {

        List<InternetAddress> toList = new ArrayList<InternetAddress>();

        sendMail("sender@test.hu",
                getAddressList("to1@mail.giro.hu", "to2@mail.giro.hu"),
                getAddressList("cc1@mail.giro.hu", "cc2@mail.giro.hu"),
                getAddressList("bcc1@mail.giro.hu", "bcc2@mail.giro.hu"),
                "Subject", "Levél");
    }

    private static void initMail() {
        final String username = "username@gmail.com";
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
    public void springMailTest() throws Exception {
        sendEmail("to1@mail.giro.hu","Tárgya","Szöveges tartalom!",false,false,null);
    }

    @Test
    public void springHtmlMailTest() throws Exception {
        String htmlBody ="<!DOCTYPE html>" +
                "<html>" +
                "<body>" +
                "" +
                "<h1>Email!</h1>" +
                "<p>Hello </p>" +
                "" +
                "</body>" +
                "</html>";

        sendEmail("to1@mail.giro.hu","Tárgya",htmlBody,false,true,null);
    }

    @Test
    public void springHtmlWithAttachmentMailTest() throws Exception {
        String htmlBody ="<!DOCTYPE html>" +
                "<html>" +
                "<body>" +
                "" +
                "<h1>Email!</h1>" +
                "<p>Hello </p>" +
                "" +
                "</body>" +
                "</html>";
        File file = ResourceUtils.getFile(this.getClass().getResource("/sample.pdf"));
        sendEmail("to1@mail.giro.hu","Tárgya",htmlBody,true,true,file);
    }

    public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml,File attachment) throws Exception {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, CharEncoding.UTF_8);
        message.setTo(to);
        message.setFrom("sender@test.hu");
        message.setSubject(subject);
        message.setText(content, isHtml);
        if(null != attachment){
            message.addAttachment(attachment.getName(),attachment);
        }


        javaMailSender.send(mimeMessage);
    }

    @Test
    public void apacheMailTest() {
        try {
            Email email = new MultiPartEmail();
            //email.setDebug(true);
            email.setHostName("localhost");
            email.setSmtpPort(2525);
            email.setAuthentication("akarki", "pass");
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

    @Test
    public void sendWithAttachement() throws EmailException, FileNotFoundException {
        MultiPartEmail email = new MultiPartEmail();
        //email.setDebug(true);
        email.setHostName("localhost");
        email.setSmtpPort(2525);
        email.setAuthentication("akarki", "pass");
        email.setFrom("sender@test.hu");
        email.setSubject("Test email");
        email.setCharset("UTF8");
        email.addTo("to1@mail.giro.hu", "to2@mail.giro.hu");
        email.addCc("cc1@mail.giro.hu", "cc2@mail.giro.hu");
        email.addBcc("bcc1@mail.giro.hu", "bcc2@mail.giro.hu");

        //add attachement
        EmailAttachment attachment = new EmailAttachment();
        File file = ResourceUtils.getFile(this.getClass().getResource("/text.txt"));
        attachment.setPath(file.getAbsolutePath());
        attachment.setDisposition(EmailAttachment.ATTACHMENT);
        attachment.setDescription("Szöveges csatolmány");
        attachment.setName("szöveges");
        email.attach(attachment);

        email.send();

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
