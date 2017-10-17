package hu.giro.smtpserver.model.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

import static javax.persistence.FetchType.LAZY;

/**
 * Created by morti on 10/9/17.
 */
@Entity
@Table(name = "SENDED_MAIL")
public class EmailObject implements Serializable{

    @Id
    @GenericGenerator(name = "SEQ_MAIL", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@org.hibernate.annotations.Parameter(name = "sequence_name", value = "SEQ_MAIL"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")})
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_MAIL")
    private Integer id;

    @Column(name = "SOURCE")
    private String source;

    @Column(name = "FROM_USER")
    private String from;

    @Column(name = "TO_USERS")
    private String to;

    @Column(name = "CARBON_COPY")
    private String cc;

    @Column(name = "BLIND_CARBON_COPY")
    private String bcc;

    @Column(name = "EMAIL_SUBJECT")
    private String subject;

    @JoinColumn(name = "EMAIL_CONTENT")
    @ManyToOne(fetch = LAZY,cascade = CascadeType.ALL)
    private EmailContent emailContent;

    @Type(type = "org.hibernate.type.NumericBooleanType")
    @Column(name = "READ")
    private boolean read=false;

    @Column(name = "RECEIVED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date receivedDate;

    @Lob
    @Column(name = "CONTENT")
    private byte[] content;

    @Version
    private Integer version;

    @Column
    private String recipient;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public EmailContent getEmailContent() {
        return emailContent;
    }

    public void setEmailContent(EmailContent emailContent) {
        this.emailContent = emailContent;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getDomain()
    {  String []parts = recipient.split("@");
       if (parts.length<2) return "Ismeretlen";
       return parts[1];
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getRecipient() {
        return recipient;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmailObject)) return false;

        EmailObject that = (EmailObject) o;

        return getId() != null ? getId().equals(that.getId()) : that.getId() == null;
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}
