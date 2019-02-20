package hu.giro.smtpserver.rest;

import hu.giro.smtpserver.model.EmailContentDto;
import hu.giro.smtpserver.model.EmailRestDto;
import hu.giro.smtpserver.model.EmailService;
import hu.giro.smtpserver.model.entity.EmailObject;
import hu.giro.smtpserver.model.repository.EmailObjectRepository;
import hu.giro.smtpserver.server.SMTPServerHandler;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by morti on 10/9/17.
 */
@RequestMapping("/rest/")
@RestController
public class Index {
    private final SMTPServerHandler server;

    private final EmailObjectRepository repository;

    private final EmailService service;

    @Autowired
    public Index(EmailService service, EmailObjectRepository repository, SMTPServerHandler server) {
        this.service = service;
        this.repository = repository;
        this.server = server;
    }

    @GetMapping(value = "status")
    public String getSatus() {
        return "Running: " + server.isRunning();
    }

    @GetMapping(value = "findAll")
    public ResponseEntity<List<EmailRestDto>> getAllEmail() {
        return ResponseEntity.ok(service.findAllRestDTO());
    }

    @GetMapping(value = "findByContent")
    public ResponseEntity<List<EmailRestDto>> findByContent(@RequestParam("content") String content) {
        List<EmailRestDto> result = service.findByContent(content);
        return ResponseEntity.ok(result);
    }
    @GetMapping(value = "findByRecipient")
    public ResponseEntity<List<EmailRestDto>> findByRecipient(@RequestParam("recipient") String recipient) {
        List<EmailRestDto> result = service.findAllByRecipient(recipient);
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "getContentById")
    public ResponseEntity<EmailContentDto> getContentById(@RequestParam("id") Integer id){
        EmailObject result = service.findContentByEmaiLId(id);
        return ResponseEntity.ok(service.convertContentDto(result));
    }

    @DeleteMapping(value ="deleteAll")
    public boolean deleteAll(@RequestParam("domain") String domain){
        service.deleteAll(domain);
        return true;
    }
}
