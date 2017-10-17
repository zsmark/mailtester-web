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

    String getEmailContent(EmailObject emailObject);
}
