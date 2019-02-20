package hu.giro.smtpserver.model.repository;

import hu.giro.smtpserver.model.entity.EmailObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Repository
public interface EmailObjectRepository extends JpaRepository<EmailObject,Integer>, JpaSpecificationExecutor<EmailObject> {
    @Modifying
    @Transactional
    void deleteByReceivedDateBefore(Date date);

    List<EmailObject> findAllByRecipientLike(String recipient);

    @Modifying
    void deleteAllBySource(String source);
}
