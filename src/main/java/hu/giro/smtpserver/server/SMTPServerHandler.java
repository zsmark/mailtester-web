package hu.giro.smtpserver.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.subethamail.smtp.auth.LoginAuthenticationHandlerFactory;
import org.subethamail.smtp.auth.LoginFailedException;
import org.subethamail.smtp.auth.UsernamePasswordValidator;
import org.subethamail.smtp.server.SMTPServer;

import javax.annotation.PostConstruct;
import java.net.InetAddress;

/**
 * Starts and stops the SMTP server.
 *
 * @author Nilhcem
 * @since 1.0
 */
@Configuration
public class SMTPServerHandler {

    private static final Log LOGGER = LogFactory.getLog(SMTPServerHandler.class);

    private MailHandlerFactory handlerFactory;

    private SMTPServer smtpServer;

    @Value("${smtpserver.port}")
    private int port;

    @PostConstruct
    public void init(){

        smtpServer  = new SMTPServer(handlerFactory,new LoginAuthenticationHandlerFactory(new UsernamePasswordValidator() {
            @Override
            public void login(String username, String password) throws LoginFailedException {
            //Mindent elfogadó authenticator
            }
        }));
    }


    @Autowired
    public SMTPServerHandler(MailHandlerFactory handlerFactory) {
        this.handlerFactory = handlerFactory;
    }

    public void startServer(int tcpport, InetAddress bindAddress) throws Exception {
        LOGGER.debug("Starting server on port "+tcpport);
        smtpServer.setBindAddress(bindAddress);
        smtpServer.setPort(tcpport);
        smtpServer.start();
    }

    private void stopServer() {
        if (smtpServer.isRunning()) {
            LOGGER.debug("Stopping SMTP server");
            smtpServer.stop();
        }
    }

    public SMTPServer getSmtpServer() {
        return smtpServer;
    }


    @EventListener(ContextRefreshedEvent.class)
    @Order(1)
    public void start() {
        try {
            startServer(port, null);

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
