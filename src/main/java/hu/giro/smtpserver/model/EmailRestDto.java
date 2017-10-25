package hu.giro.smtpserver.model;

import java.io.Serializable;
import java.util.Date;

public class EmailRestDto implements Serializable {

    private Integer id;
    private String from;
    private String subject;
    private Date receivedDate;

    public EmailRestDto() {
    }

    public EmailRestDto(Integer id, String from, String subject, Date receivedDate) {
        this.id = id;
        this.from = from;
        this.subject = subject;
        this.receivedDate = receivedDate;
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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }
}
