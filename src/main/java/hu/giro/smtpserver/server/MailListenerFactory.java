package hu.giro.smtpserver.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.subethamail.smtp.helper.SimpleMessageListener;

import java.io.IOException;
import java.io.InputStream;

/**
 * Listens to incoming emails and redirects them to the {@code MailSaver} object.
 *
 * @author Nilhcem
 * @since 1.0
 */
@Component
public class MailListenerFactory {
    private static Log log = LogFactory.getLog(MailListenerFactory.class);

    @Autowired
    public MailListenerFactory(MailSaver mailSaver) {
        this.mailSaver = mailSaver;
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
            mailSaver.saveEmailAndNotify(from, recipient, data);
        }
    }

    private final MailSaver mailSaver;

    public MailListener createMailListener(){
        return new MailListener();
    }
}
