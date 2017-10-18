package hu.giro.smtpserver.test;

import org.apache.commons.lang3.CharEncoding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class MailSenderTest {
    private List<String> to = Arrays.asList("to1@mail.giro.hu", "to2@mail.giro.hu", "to3@mail.giro.hu");
    private List<String> cc = Arrays.asList("tocc1@mail.giro.hu", "tocc2@mail.giro.hu", "tocc3@mail.giro.hu");
    private List<String> bcc = Arrays.asList("tobcc1@mail.giro.hu", "tobcc2@mail.giro.hu", "tobcc3@mail.giro.hu");


    @Autowired
    private JavaMailSender javaMailSender;

    @EventListener(ContextRefreshedEvent.class)
    public void send() {
        try {
            springMailTest();
            springHtmlMailTest();
            springHtmlWithAttachmentMailTest();
            springHtmlWithAttachmentMailTest();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void springMailTest() throws Exception {

        sendEmail(to,cc,bcc, "Szöveges mail", "Szöveges tartalom!", false, false, null);
    }

    private void springHtmlMailTest() throws Exception {
        String htmlBody = "<!DOCTYPE html>" +
                "<html>" +
                "<body>" +
                "" +
                "<h1>Email!</h1>" +
                "<p>Hello </p>" +
                "" +
                "</body>" +
                "</html>";

        sendEmail(to,cc,bcc,  "Html levél", htmlBody, false, true, null);
    }

    private void springHtmlWithAttachmentMailTest() throws Exception {
        String htmlBody = "<!DOCTYPE html>" +
                "<html>" +
                "<body>" +
                "" +
                "<h1>Email!</h1>" +
                "<p>Hello </p>" +
                "" +
                "</body>" +
                "</html>";
        File file = ResourceUtils.getFile(this.getClass().getResource("/sample.pdf"));
        sendEmail(to,cc,bcc,  "HTML levél csatolmánnyal", htmlBody, true, true, file);
    }

    private void sendEmail(List<String> to,List<String> cc,List<String> bcc, String subject, String content, boolean isMultipart, boolean isHtml, File attachment) throws Exception {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, CharEncoding.UTF_8);
        message.setTo(to.toArray(new String[to.size()]));
        message.setCc(cc.toArray(new String[cc.size()]));
        message.setBcc(bcc.toArray(new String[bcc.size()]));
        message.setFrom("sender@test.hu");
        message.setSubject(subject);
        message.setText(content, isHtml);
        if (null != attachment) {
            message.addAttachment(attachment.getName(), attachment);
        }


        javaMailSender.send(mimeMessage);
    }
}
