package hu.giro.smtpserver.server;

import hu.giro.smtpserver.model.EmailService;
import hu.giro.smtpserver.model.entity.EmailContent;
import hu.giro.smtpserver.model.entity.EmailObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.james.mime4j.MimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.subethamail.smtp.MessageContext;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.MessageHandlerFactory;
import org.subethamail.smtp.RejectException;
import tech.blueglacier.email.Email;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@Component
public class MailHandlerFactory implements MessageHandlerFactory {
  private static Log log = LogFactory.getLog(MailHandler.class);

  private final EmailService emailService;

  @Autowired
  private EmailParserService parserService;

  @Autowired
  public MailHandlerFactory(EmailService emailService) {
    this.emailService = emailService;
  }

  public MessageHandler create(MessageContext ctx) {
    return new MailHandler(ctx);
  }

  class MailHandler implements MessageHandler {
    private final MessageContext ctx;
    private String source;
    private List<EmailObject> emails = new ArrayList<>();
    private String from;


    public MailHandler(MessageContext ctx) {
      this.ctx = ctx;

      this.source = ctx.getAuthenticationHandler() == null ? null : (String) ctx.getAuthenticationHandler().getIdentity();
      if (source == null) {
        this.source = ctx.getRemoteAddress().toString();
      }
    }

    public void from(String from) throws RejectException {
      this.from = from; //ez fix mindegyik esetén
    }

    public void recipient(String recipient) throws RejectException {
      EmailObject email = new EmailObject();
      email.setRecipient(recipient);
      emails.add(email);
    }

    public void data(InputStream data) throws IOException {


      //mailSaver.saveEmailAndNotify(from, recipient, data);

      String mailContent = convertStreamToString(data);
      for (EmailObject email : emails) {
        email.setFrom(from);
        email.setSource(source);
        email.setSubject(getSubjectFromStr(mailContent));
        byte[] content = mailContent.getBytes();
        email.setEmailContent(new EmailContent(content));
        email.setMessageId(parseMessageId(content));
        email.setReceivedDate(new Date());
        emailService.save(email);
        log.info(String.format("Deliver from %s to %s", email.getFrom(), email.getRecipient()));
      }
    }

    private String parseMessageId(byte[] content) {
      try {
        Email mail = parserService.parse(content);
        if (mail.getHeader().getField("message-id")!=null) {
          return mail.getHeader().getField("message-id").getBody().
              replaceAll("@.*$","").substring(1);

        }
      } catch (Exception e) {
        // elvileg lehetetlen
      }
      return null;
    }

    public void done() {

    }

    private String getSubjectFromStr(String data) {


      try {
        Session s = Session.getDefaultInstance(new Properties());
        InputStream is = new ByteArrayInputStream(data.getBytes());
        MimeMessage message = new MimeMessage(s, is);
        return message.getSubject();
      } catch (MessagingException e) {
        log.error("", e);

      }
      return "";
    }

    private String convertStreamToString(InputStream is) {
      try {
        return IOUtils.toString(is, Charset.forName("UTF8"));
      } catch (IOException e) {
        e.printStackTrace();
      }
      return null;
    }
  }
}
