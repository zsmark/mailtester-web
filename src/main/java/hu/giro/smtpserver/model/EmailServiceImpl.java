package hu.giro.smtpserver.model;

import hu.giro.smtpserver.model.entity.EmailObject;
import hu.giro.smtpserver.model.repository.EmailObjectRepository;
import hu.giro.smtpserver.ui.EmailsViewFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

import static hu.giro.smtpserver.model.TrueFalseAllEnum.ALL;
import static hu.giro.smtpserver.model.TrueFalseAllEnum.TRUE;

@Service
public class EmailServiceImpl implements EmailService {

    static private Log log = LogFactory.getLog(EmailServiceImpl.class);

    EmailObjectRepository repository;

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
            log.info("Filtering with " + search);
        }

        @Override
        public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder cb) {
            Predicate predicate = cb.isTrue(cb.literal(true));
            if (search.getReadFilter() != ALL) {
                predicate = cb.and(predicate, cb.equal(root.get("read"), search.getReadFilter() == TRUE));
            }
            if (search.getTextFilter() != null && search.getTextFilter().length() > 0) {
                Path to = root.get("to");
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
            if (search.getDomainFilter()!=null && search.getDomainFilter().length()>0)
            {   String likeTxt = ("%@" + search.getDomainFilter()).toLowerCase();
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("to")),likeTxt));
            }
            return predicate;
        }
    }
}
