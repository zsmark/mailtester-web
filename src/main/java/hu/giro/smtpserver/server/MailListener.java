package hu.giro.smtpserver.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.smtp.helper.SimpleMessageListener;

import java.io.IOException;
import java.io.InputStream;

/**
 * Listens to incoming emails and redirects them to the {@code MailSaver} object.
 *
 * @author Nilhcem
 * @since 1.0
 */
public final class MailListener implements SimpleMessageListener {
	private static Log log = LogFactory.getLog(MailListener.class);
	private final MailSaver saver;

	/**
	 * Creates the listener.
	 *
	 * @param saver a {@code MailServer} object used to save emails and notify components.
	 */
	public MailListener(MailSaver saver) {
		this.saver = saver;
	}

	/**
	 * Accepts all kind of email <i>(always return true)</i>.
	 * <p>
	 * Called once for every RCPT TO during a SMTP exchange.<br>
     * Each accepted recipient will result in a separate deliver() call later.
     * </p>
     *
	 * @param from the user who send the email.
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
		log.info(String.format("Deliver from %s to %s",from,recipient));
		saver.saveEmailAndNotify(from, recipient, data);
	}
}
