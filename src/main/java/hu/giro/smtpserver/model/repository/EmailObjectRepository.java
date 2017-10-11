package hu.giro.smtpserver.model.repository;

import hu.giro.smtpserver.model.entity.EmailObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailObjectRepository extends JpaRepository<EmailObject,Integer> {
}
