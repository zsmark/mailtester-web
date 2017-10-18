package hu.giro.smtpserver.ui;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import org.apache.commons.io.IOUtils;
import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.codec.DecodeMonitor;
import org.apache.james.mime4j.message.DefaultBodyDescriptorBuilder;
import org.apache.james.mime4j.parser.ContentHandler;
import org.apache.james.mime4j.parser.MimeStreamParser;
import org.apache.james.mime4j.stream.BodyDescriptorBuilder;
import org.apache.james.mime4j.stream.MimeConfig;
import tech.blueglacier.email.Attachment;
import tech.blueglacier.email.Email;
import tech.blueglacier.parser.CustomContentHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class EmailDisplay extends VerticalLayout {
    private List<Attachment> attachments;
    private Attachment calendar;
    private Attachment htmlBody;
    private Attachment plainText;
    String to, cc, from;
    private String body= "Üres";
    private boolean htmlType;

    public EmailDisplay(byte[] emailContent) throws IOException, MimeException {

        parseMail(emailContent);
        init();
    }

    private void init() {

        Label html = new Label(body);
        html.setContentMode(ContentMode.HTML);
        html.setSizeFull();
        addComponent(html);
    }

    private String convertToString(Attachment attachment) throws IOException {
        return IOUtils.toString(attachment.getIs(),attachment.getBd().getCharset());
    }

    private void parseMail(byte[] emailContent) throws MimeException, IOException {
        String x = new String(emailContent,"UTF8");
        System.out.println(x);
        ContentHandler contentHandler = new CustomContentHandler();

        MimeConfig mime4jParserConfig = MimeConfig.DEFAULT;
        BodyDescriptorBuilder bodyDescriptorBuilder = new DefaultBodyDescriptorBuilder();
        MimeStreamParser mime4jParser = new MimeStreamParser(mime4jParserConfig, DecodeMonitor.SILENT, bodyDescriptorBuilder);
        mime4jParser.setContentDecoding(true);
        mime4jParser.setContentHandler(contentHandler);

        InputStream mailIn = new ByteArrayInputStream(emailContent);
        mime4jParser.parse(mailIn);

        Email email = ((CustomContentHandler) contentHandler).getEmail();

        attachments = email.getAttachments();

        calendar = email.getCalendarBody();
        htmlBody = email.getHTMLEmailBody();

        plainText = email.getPlainTextEmailBody();

        if (htmlBody!=null) {
            body = convertToString(htmlBody);
            htmlType=true;
        }
        else if (plainText!=null)
        {   body = convertToString(plainText);
            htmlType=false;
        }

        to = email.getToEmailHeaderValue();
        cc = email.getCCEmailHeaderValue();
        from = email.getFromEmailHeaderValue();

    }


}
