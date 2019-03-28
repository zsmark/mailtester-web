package hu.giro.smtpserver.model;

import hu.giro.smtpserver.model.entity.EmailObject;
import org.apache.james.mime4j.MimeException;
import tech.blueglacier.email.Email;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface EmailService {
    List<EmailObject> findAll();
    List<EmailRestDto> findAllRestDTO();

    List<EmailObject>findAll(EmailSearchDTO search);

    void save(EmailObject emailObject);


    void truncate();

    Set<String> getDomains();

    byte[] getEmailContent(EmailObject emailObject);

    Email getEmail(EmailObject emailObject) throws IOException, MimeException;

    List<EmailRestDto> findByContent(String content);

    EmailObject findContentByEmaiLId(Integer id);

     EmailContentDto convertContentDto(EmailObject emailObject);

    List<EmailRestDto> findAllByRecipient(String recipient);

    void deleteAll(String domain);
}
