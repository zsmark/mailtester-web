package hu.giro.smtpserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Date;

public class EmailContentDto implements Serializable {

    private Integer id;
    private String contentString;
    @JsonIgnore
    private byte[] content;

    public EmailContentDto() {
    }

    public EmailContentDto(Integer id, String contentString, byte[] content) {
        this.id = id;
        this.contentString = contentString;
        this.content = content;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContentString() {
        return new String(content);
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
