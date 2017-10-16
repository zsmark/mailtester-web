package hu.giro.smtpserver.server;

import hu.giro.smtpserver.model.repository.EmailObjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Component
public class EmailCleaner {

    private final EmailObjectRepository emailObjectRepository;

    @Autowired
    public EmailCleaner(EmailObjectRepository emailObjectRepository) {
        this.emailObjectRepository = emailObjectRepository;
    }

    @Scheduled(fixedDelay = 3600000)
//    @Scheduled(fixedDelay = 10000) teszthez
    public void deleteOldEmails(){
        LocalDate date = LocalDate.now();
        date = date.minusDays(3);
        emailObjectRepository.deleteByReceivedDateBefore( Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

}
