package hu.giro.smtpserver.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.subethamail.smtp.helper.SimpleMessageListenerAdapter;
import org.subethamail.smtp.server.SMTPServer;

import java.net.InetAddress;

/**
 * Starts and stops the SMTP server.
 *
 * @author Nilhcem
 * @since 1.0
 */
@Component
public class SMTPServerHandler {

    private static final Log LOGGER = LogFactory.getLog(SMTPServerHandler.class);
    private final MailSaver mailSaver = new MailSaver();
    private final MailListener myListener = new MailListener(mailSaver);
    private final SMTPServer smtpServer = new SMTPServer(new SimpleMessageListenerAdapter(myListener), new SMTPAuthHandlerFactory());

    public SMTPServerHandler() {
    }

    public void startServer(int port, InetAddress bindAddress) throws Exception {
        LOGGER.debug("Starting server on port "+port);
        smtpServer.setBindAddress(bindAddress);
        smtpServer.setPort(port);
        smtpServer.start();
    }

    //@Bean
    public SMTPServerHandler smtpServerHandler()
    {return new SMTPServerHandler();
    }

    /**
     * Stops the server.
     * <p>
     * If the server is not started, does nothing special.
     * </p>
     */
    private void stopServer() {
        if (smtpServer.isRunning()) {
            LOGGER.debug("Stopping SMTP server");
            smtpServer.stop();
        }
    }

    /**
     * Returns the {@code MailSaver} object.
     *
     * @return the {@code MailSaver} object.
     */
    public MailSaver getMailSaver() {
        return mailSaver;
    }

    /**
     * Returns the {@code SMTPServer} object.
     *
     * @return the {@code SMTPServer} object.
     */
    public SMTPServer getSmtpServer() {
        return smtpServer;
    }


    @EventListener(ContextRefreshedEvent.class)
    public void start() {

        try {
            startServer(2525, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @EventListener(ContextClosedEvent.class)
    public void stop() {
        stopServer();
    }

    public boolean isRunning() {
        return smtpServer.isRunning();
    }


}
