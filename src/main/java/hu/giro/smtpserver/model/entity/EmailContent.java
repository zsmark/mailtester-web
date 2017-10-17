package hu.giro.smtpserver.model.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "EMAIL_CONTENT")
public class EmailContent implements Serializable{

    @Id
    @GenericGenerator(name = "SEQ_MAIL", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@org.hibernate.annotations.Parameter(name = "sequence_name", value = "SEQ_MAIL"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")})
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_MAIL")
    private Integer id;

    @Column(name = "EMAIL_STRING")
    private String emailStr;

    @Version
    private Integer version;


    public EmailContent(String emailStr) {
        this.emailStr = emailStr;
    }

    public EmailContent() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmailStr() {
        return emailStr;
    }

    public void setEmailStr(String emailStr) {
        this.emailStr = emailStr;
    }

    public Integer getVersion() {
        return version;
    }
}
