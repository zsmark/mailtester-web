package hu.giro.smtpserver.server;

import java.util.Date;

/**
 * Created by morti on 10/9/17.
 */
public class EmailObject {
    private String from;
    private String to;
    private String subject;
    private String emailStr;
    private Date receivedDate;
    private String filePath;

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setEmailStr(String emailStr) {
        this.emailStr = emailStr;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getSubject() {
        return subject;
    }

    public String getEmailStr() {
        return emailStr;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public String getFilePath() {
        return filePath;
    }
}
