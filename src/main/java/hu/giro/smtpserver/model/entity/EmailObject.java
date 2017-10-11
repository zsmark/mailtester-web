package hu.giro.smtpserver.model.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by morti on 10/9/17.
 */
@Entity
@Table(name = "SENDED_MAIL")
public class EmailObject {

    @Id
    @GenericGenerator(name = "SEQ_MAIL", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@org.hibernate.annotations.Parameter(name = "sequence_name", value = "SEQ_MAIL"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")})
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_MAIL")
    private Integer id;

    @Column(name = "FROM")
    private String from;

    @Column(name = "TO")
    private String to;

    @Column(name = "CARBON_COPY")
    private String cc;

    @Column(name = "BLIND_CARBON_COPY")
    private String bcc;

    @Column(name = "SUBJECT")
    private String subject;

    @Column(name = "EMAIL_STRING")
    private String emailStr;

    @Column(name = "FILE_PATH")
    private String filePath;

    @Column(name = "RECEIVED_DATE")
    @Temporal(TemporalType.DATE)
    private Date receivedDate;

    @Lob
    @Column(name = "CONTENT")
    private byte[] content;

    @Version
    private Integer version;

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

    public String getEmailStr() {
        return emailStr;
    }

    public void setEmailStr(String emailStr) {
        this.emailStr = emailStr;
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
