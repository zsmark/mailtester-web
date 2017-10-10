package hu.giro.smtpserver.web;

import hu.giro.smtpserver.server.SMTPServerHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by morti on 10/9/17.
 */
@RestController
public class Index {
    @Autowired
    SMTPServerHandler server;

    @RequestMapping("/")
    String home() {
        return "Hello World! "+server.isRunning();
    }
}
