package hu.giro.smtpserver.rest;

import hu.giro.smtpserver.model.EmailContentDto;
import hu.giro.smtpserver.model.EmailRestDto;
import hu.giro.smtpserver.model.EmailService;
import hu.giro.smtpserver.model.entity.EmailObject;
import hu.giro.smtpserver.model.repository.EmailObjectRepository;
import hu.giro.smtpserver.server.SMTPServerHandler;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by morti on 10/9/17.
 */
@RequestMapping("/rest")
@RestController
public class Index {
    private final SMTPServerHandler server;

    private final EmailObjectRepository repository;

    private final EmailService service;

    private final ModelMapper modelMapper;

    @Autowired
    public Index(EmailService service, EmailObjectRepository repository, SMTPServerHandler server, ModelMapper modelMapper) {
        this.service = service;
        this.repository = repository;
        this.server = server;
        this.modelMapper = modelMapper;
    }

    @GetMapping(value = "status")
    public String getSatus() {
        return "Running: " + server.isRunning();
    }

    @GetMapping(value = "findAll")
    public List<EmailObject> getAllEmail() {
        return repository.findAll();
    }

    @GetMapping(value = "findByContent")
    public List<EmailRestDto> findByContent(@RequestParam("content") String content) {
        List<EmailObject> result = service.findByContent(content);
        return result.stream().map(emailObject -> modelMapper.map(emailObject,EmailRestDto.class)).collect(Collectors.toList());
    }

    @GetMapping(value = "getContentById")
    public EmailContentDto getContentById(@RequestParam("id") Integer id){
        EmailObject result = service.findContentByEmaiLId(id);
        return service.convertContentDto(result);
    }
}
