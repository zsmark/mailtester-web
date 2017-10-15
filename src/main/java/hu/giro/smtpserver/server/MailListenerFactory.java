package hu.giro.smtpserver.server;

import hu.giro.smtpserver.model.entity.EmailObject;
import hu.giro.smtpserver.model.repository.EmailObjectRepository;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.subethamail.smtp.helper.SimpleMessageListener;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Listens to incoming emails and redirects them to the {@code MailSaver} object.
 *
 * @author Nilhcem
 * @since 1.0
 */
@Component
public class MailListenerFactory {
    private static Log log = LogFactory.getLog(MailListener.class);
    private final EmailObjectRepository repository;
    private Set<String> domains = new TreeSet<String>();

    @Autowired
    public MailListenerFactory(EmailObjectRepository repository) {
        this.repository = repository;
    }

    final class MailListener implements SimpleMessageListener {
        /**
         * Accepts all kind of email <i>(always return true)</i>.
         * <p>
         * Called once for every RCPT TO during a SMTP exchange.<br>
         * Each accepted recipient will result in a separate deliver() call later.
         * </p>
         *
         * @param from      the user who send the email.
         * @param recipient the recipient of the email.
         * @return always return {@code true}
         */
        public boolean accept(String from, String recipient) {
            return true;
        }

        /**
         * Receives emails and forwards them to the {@link MailSaver} object.
         */
        @Override
        public void deliver(String from, String recipient, InputStream data) throws IOException {
            log.info(String.format("Deliver from %s to %s", from, recipient));
            //mailSaver.saveEmailAndNotify(from, recipient, data);

            EmailObject model = new EmailObject();
            model.setFrom(from);
            model.setTo(recipient);
            String mailContent = convertStreamToString(data);
            model.setSubject(getSubjectFromStr(mailContent));
            model.setEmailStr(mailContent);
            model.setReceivedDate(new Date());
            synchronized (domains)
            {
                if (!domains.contains(model.getDomain()))
                    domains.add(model.getDomain());
            }
            /*synchronized (getLock()) {
                String filePath = saveEmailToFile(mailContent);


                model.setFilePath(filePath);

                setChanged();
                notifyObservers(model);
            }*/
            repository.save(model);
        }
    }


    public Set<String> getDomains() {
        return domains;
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

    public MailListener createMailListener() {
        return new MailListener();
    }
}
