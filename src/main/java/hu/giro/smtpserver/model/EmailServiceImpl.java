package hu.giro.smtpserver.model;

import static hu.giro.smtpserver.model.TrueFalseAllEnum.ALL;
import static hu.giro.smtpserver.model.TrueFalseAllEnum.TRUE;

import hu.giro.smtpserver.model.entity.EmailObject;
import hu.giro.smtpserver.model.repository.EmailObjectRepository;
import hu.giro.smtpserver.server.EmailParserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.james.mime4j.MimeException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tech.blueglacier.email.Email;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

@Service
public class EmailServiceImpl implements EmailService {

  static private Log log = LogFactory.getLog(EmailServiceImpl.class);
  private final ModelMapper mapper;
  private EmailObjectRepository repository;
  private Set<String> domains = new TreeSet<String>();

  @Autowired
  EmailParserService emailParser;

  @Autowired
  public EmailServiceImpl(EmailObjectRepository repository, ModelMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public List<EmailObject> findAll() {
    return repository.findAll();
  }

  @Override
  public List<EmailRestDto> findAllRestDTO() {
    return mapEmailObejtToEmailRestDto(findAll());
  }

  @Override
  public List<EmailObject> findAll(EmailSearchDTO search) {
    return repository.findAll(new EmailSearchSpecification(search)
    );
  }

  @Override
  public void save(EmailObject emailObject) {
    synchronized (domains) {
      if (!domains.contains(emailObject.getSource())) {
        domains.add(emailObject.getSource());
      }
    }
    repository.save(emailObject);
  }

  @Override
  public void truncate() {
    repository.deleteAll();

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

  @Override
  public Email getEmail(EmailObject emailObject) throws IOException, MimeException {

    return emailParser.parse(getEmailContent(emailObject));
  }

  @Override
  public List<EmailRestDto> findByContent(String content) {
    return mapEmailObejtToEmailRestDto(repository.findAll((root, criteriaQuery, criteriaBuilder) -> {
      Predicate predicate = criteriaBuilder.isTrue(criteriaBuilder.literal(true));
      if (content != null && content.length() > 0) {
        Path<String> to = root.get("recipient");
        Path<String> subject = root.get("subject");
        Path<String> cc = root.get("cc");

        String likeTxt = ("%" + content + "%").toLowerCase();
        predicate = criteriaBuilder.and(predicate,
            criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(subject), likeTxt),
                criteriaBuilder.like(criteriaBuilder.lower(to), likeTxt),
                criteriaBuilder.like(criteriaBuilder.lower(cc), likeTxt)
            )
        );
      }
      return predicate;
    }, new Sort(Sort.Direction.ASC, "id")));
  }

  private List<EmailRestDto> mapEmailObejtToEmailRestDto(List<EmailObject> list) {
    if (list == null) {
      return new ArrayList<>();
    }

    return list.stream().map(emailObject -> mapper.map(emailObject, EmailRestDto.class)).collect(Collectors.toList());
  }

  @Override
  public EmailObject findContentByEmaiLId(Integer id) {
    return repository.findOne(id);
  }

  @Transactional
  @Override
  public EmailContentDto convertContentDto(EmailObject emailObject) {
    emailObject = repository.findOne(emailObject.getId());
    EmailContentDto dto = mapper.map(emailObject.getEmailContent(), EmailContentDto.class);
    return dto;
  }

  @Override
  public List<EmailRestDto> findAllByRecipient(String recipient) {
    return mapEmailObejtToEmailRestDto(repository.findAll((root, criteriaQuery, criteriaBuilder) -> {
      Predicate predicate = criteriaBuilder.isTrue(criteriaBuilder.literal(true));
      if (recipient != null && recipient.length() > 0) {
        Path<String> recipientPath = root.get("recipient");

        String likeTxt = ("%" + recipient + "%").toLowerCase();
        predicate = criteriaBuilder.and(predicate,
            criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(recipientPath), likeTxt)
            )
        );
      }
      return predicate;
    }, new Sort(Sort.Direction.ASC, "id")));
  }

  @Override
  @Transactional
  public void deleteAll(String domain) {
    repository.deleteAllBySource(domain);
  }

  public class EmailSearchSpecification implements Specification<EmailObject> {
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

}
