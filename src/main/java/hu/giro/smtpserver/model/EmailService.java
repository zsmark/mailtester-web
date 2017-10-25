package hu.giro.smtpserver.model;

import hu.giro.smtpserver.model.entity.EmailObject;

import java.util.List;
import java.util.Set;

public interface EmailService {
    List<EmailObject> findAll();

    List<EmailObject>findAll(EmailSearchDTO search);

    void save(EmailObject emailObject);


    void truncate();

    Set<String> getDomains();

    byte[] getEmailContent(EmailObject emailObject);

    List<EmailObject> findByContent(String content);

    EmailObject findContentByEmaiLId(Integer id);

     EmailContentDto convertContentDto(EmailObject emailObject);

    List<EmailObject> findAllByRecipient(String recipient);
}
