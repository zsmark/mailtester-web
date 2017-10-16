package hu.giro.smtpserver.rest;

import hu.giro.smtpserver.model.entity.EmailObject;
import hu.giro.smtpserver.model.repository.EmailObjectRepository;
import hu.giro.smtpserver.server.SMTPServerHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by morti on 10/9/17.
 */
@RequestMapping("/rest")
@RestController
public class Index {
    @Autowired
    SMTPServerHandler server;

    @Autowired
    private EmailObjectRepository repository;

    @GetMapping(value = "status")
    public String getSatus(){
        return "Running: " + server.isRunning();
    }

    @GetMapping(value = "findAll")
    public List<EmailObject> getAllEmail(){
        return repository.findAll();
    }

}
