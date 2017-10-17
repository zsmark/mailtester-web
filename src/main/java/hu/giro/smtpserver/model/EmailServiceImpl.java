package hu.giro.smtpserver.model;

import hu.giro.smtpserver.model.entity.EmailObject;
import hu.giro.smtpserver.model.repository.EmailObjectRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static hu.giro.smtpserver.model.TrueFalseAllEnum.ALL;
import static hu.giro.smtpserver.model.TrueFalseAllEnum.TRUE;

@Service
public class EmailServiceImpl implements EmailService {

    static private Log log = LogFactory.getLog(EmailServiceImpl.class);

    private EmailObjectRepository repository;

    private Set<String> domains = new TreeSet<String>();


    @Autowired
    public EmailServiceImpl(EmailObjectRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<EmailObject> findAll() {
        return repository.findAll();
    }

    @Override
    public List<EmailObject> findAll(EmailSearchDTO search) {
        return repository.findAll(new EmailSearchSpecification(search)
        );
    }

    @Override
    public void save(EmailObject emailObject) {
        synchronized (domains) {
            if (!domains.contains(emailObject.getSource()))
                domains.add(emailObject.getSource());
        }
        repository.save(emailObject);
    }

    @Override
    public void truncate() {
        repository.deleteAll();

    }

    public class EmailSearchSpecification implements Specification {
        private final EmailSearchDTO search;

        public EmailSearchSpecification(EmailSearchDTO search) {
            this.search = search;
            log.debug("Filtering with " + search);
        }

        @Override
        public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder cb) {
            Predicate predicate = cb.isTrue(cb.literal(true));
            if (search.getReadFilter() != ALL) {
                predicate = cb.and(predicate, cb.equal(root.get("read"), search.getReadFilter() == TRUE));
            }
            if (search.getTextFilter() != null && search.getTextFilter().length() > 0) {
                Path to = root.get("recipient");
                Path subject = root.get("subject");
                Path cc = root.get("cc");

                String likeTxt = ("%" + search.getTextFilter() + "%").toLowerCase();
                predicate = cb.and(predicate,
                        cb.or(
                                cb.like(cb.lower(subject), likeTxt),
                                cb.like(cb.lower(to), likeTxt),
                                cb.like(cb.lower(cc), likeTxt)
                        )
                );
            }
            if (search.getDomainFilter() != null && search.getDomainFilter().length() > 0) {
               // String likeTxt = ("%@" + search.getDomainFilter()).toLowerCase();
                predicate = cb.and(predicate, cb.equal(root.get("source"), search.getDomainFilter()));
            }
            return predicate;
        }
    }

    @Override
    public Set<String> getDomains() {
        return domains;
    }

    @Transactional
    @Override
    public byte[] getEmailContent(EmailObject emailObject) {
        emailObject = repository.findOne(emailObject.getId());
        return emailObject.getEmailContent().getContent();
    }

}
