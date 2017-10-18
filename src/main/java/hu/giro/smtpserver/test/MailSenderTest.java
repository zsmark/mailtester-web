package hu.giro.smtpserver.test;

import hu.giro.smtpserver.server.SMTPServerHandler;
import org.apache.commons.lang3.CharEncoding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import javax.mail.internet.MimeMessage;
import java.io.File;
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
    @Order(2)
    public void send() {
        try {
            springMailTest();
            springHtmlMailTest();
            ikatasMail();
            ikatasMail();
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
        File file = ResourceUtils.getFile(this.getClass().getResource("/text.txt"));

        sendEmail(to,cc,bcc,  "Html levél txt csatolmannyal", htmlBody, true, true, file);
    }

    private void ikatasMail() throws Exception {
        String htmlBody = "Kedves Iktatás!<br>" +
                "<br>" +
                "1.)\tFeladat leírása (ELŐZMÉNY): <br>" +
                "  Instant utalás elkészítése   <br>" +
                "<br>" +
                "2.) Érintett szolgáltatás (TÁRGY): <br>" +
                "  Nincs   <br>" +
                "<br>" +
                "3.) Külsős fejlesztő (PARTNER):<br>" +
                "  Nincs   <br>" +
                "<br>" +
                "4.) Feladat felelőse (FELELŐS): <br>" +
                "   Thor <br>" +
                "<br>" +
                "5.) Érintettek (ÉRDEKELTEK): <br>" +
                "   Merlin, Dijkstra  <br>" +
                "<br>" +
                "Köszönettel: <br>" +
                "50Cent";
        File file = ResourceUtils.getFile(this.getClass().getResource("/sample.pdf"));
        sendEmail(to,cc,bcc,  "Iktatás", htmlBody, true, true, file);
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
